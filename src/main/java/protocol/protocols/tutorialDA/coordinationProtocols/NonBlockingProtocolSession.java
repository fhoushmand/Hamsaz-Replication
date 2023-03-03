package main.java.protocol.protocols.tutorialDA.coordinationProtocols;


import main.java.analyser.Analyzer;
import main.java.protocol.protocols.tutorialDA.events.*;
import main.java.protocol.protocols.tutorialDA.utils.*;
import robject.Clique;
import main.java.robject.ReplicatedObject;
import robject.ReplicatedObjectState;
import net.sf.appia.core.*;
import net.sf.appia.core.events.SendableEvent;
import net.sf.appia.core.events.channel.ChannelInit;


import java.lang.reflect.Method;
import java.util.*;

public class NonBlockingProtocolSession extends Session {

    private ProcessSet processes;

    // array of lists
    private LinkedList<SendableEvent>[] from;
    // List of MessageID objects
    private LinkedList<MessageID> delivered;

    //clique map for mtob sessions
    private HashMap<Clique, Channel> mtobChannels;
    private HashMap<Clique, Channel> mtobInits;
    private Channel mainChannel;

    private ReplicatedObject object;
    private Analyzer analyzer;

    private HashMap<Clique, LinkedList<Call>> q = new HashMap<>();

    private HashMap<String, ArrayList<Call>> xed = new HashMap<>();

    private LinkedList<Call> blockedCalls = new LinkedList<>();

    private HashSet<String> commitMessages = new HashSet<>();



    /**
     * @param layer
     */
    public NonBlockingProtocolSession(NonBlockingProtocolLayer layer) {
        super(layer);
        object = layer.getObject();
        analyzer = layer.getAnalyzer();
    }

    public NonBlockingProtocolSession(Layer layer, ReplicatedObject obj, Analyzer a) {
        super(layer);
        this.object = obj;
        this.analyzer = a;
    }

    public void setMtobChannels(HashMap<Clique, Channel> mtobChannels) {
        this.mtobInits = mtobChannels;
    }

    public HashMap<Clique, Channel> getMtobChannels() {
        return mtobChannels;
    }

    /**
     * Main event handler
     */
    public void handle(Event event) {
        // Init events. Channel Init is from Appia and ProcessInitEvent is to know
        // the elements of the group
        if (event instanceof ChannelInit)
            handleChannelInit((ChannelInit) event);
        else if (event instanceof ProcessInitEvent)
            handleProcessInitEvent((ProcessInitEvent) event);
        else if(event.getClass().getName().equals(SampleSendableEvent.class.getName()) && event.getDir() == Direction.DOWN)
            callRequest((SampleSendableEvent) event);
        else if(event instanceof SendableEvent && event.getDir() == Direction.UP)
        {
            if(event.getChannel().getChannelID().equals("RB") && event.getClass().getName().equals(SampleSendableEvent.class.getName()))
                rbDeliver((SendableEvent) event);
            else if(event.getChannel().getChannelID().equals("RB") && event.getClass().getName().equals(CommitVote.class.getName()))
                atomicDecide((CommitVote) event);
            else if(event.getChannel().getChannelID().equals("RB") && event.getClass().getName().equals(AbortVote.class.getName()))
                abortVoteDeliver((AbortVote) event);
            else
                mtobDelivery((SampleSendableEvent) event);

        }
        else if (event instanceof Crash)
            handleCrash((Crash) event);

        checkForBlockedCalls();

    }



    /**
     * @param init
     */
    private void handleChannelInit(ChannelInit init) {
        try {
            if(mainChannel == null) {
                mtobChannels = new HashMap<>();
                mainChannel = init.getChannel();
                for(Channel c : mtobInits.values()) {
                    c.start();
                }
            }
            else {
                for(Map.Entry entry : mtobInits.entrySet())
                {
                    if(init.getChannel().getChannelID().equals(((Clique)entry.getKey()).name)) {
                        mtobChannels.put((Clique) entry.getKey(), init.getChannel());

                        ProcessInitEvent pInit = new ProcessInitEvent(init.getChannel(), Direction.DOWN, this);
                        pInit.setProcessSet(processes);
                        //TODO this line is really strange!!!
//                        handleProcessInitEvent(pInit);
                        pInit.go();
                    }

                }
            }
            init.go();


        } catch (AppiaDuplicatedSessionsException e) {
            e.printStackTrace();
        }
        catch (AppiaEventException e1)
        {
            e1.printStackTrace();
        }
        delivered = new LinkedList<MessageID>();
    }

    /**
     * @param event
     */
    @SuppressWarnings("unchecked")
    private void handleProcessInitEvent(ProcessInitEvent event) {
        processes = event.getProcessSet();
        for (Method method : object.getAllMethodsOfObject())
        {
            xed.put(method.getName(), new ArrayList<>());
        }
        q = new HashMap<>();
        for(Clique cl : analyzer.getAllCliques()) {
            q.put(cl, new LinkedList<>());
        }
        try {
            event.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }
        from = new LinkedList[processes.getSize()];
        for (int i = 0; i < from.length; i++)
            from[i] = new LinkedList<>();
    }



    /**
     * Called when the above protocol sends a message.
     *
     * @param event
     */
    private void callRequest(SampleSendableEvent event) {

        Call c = (Call)event.getMessage().popObject();
        event.getMessage().pushObject(c);

        ArrayList<Clique> cliques = analyzer.getCliquesForMethod(c.methodName);
        if(cliques == null || cliques.size() == 0) {
            try {
                SendableEvent rbEvent = (SendableEvent) event.cloneEvent();
                rbEvent.setChannel(mainChannel);
                rbEvent.setDir(Direction.DOWN);
                rbEvent.setSourceSession(this);
                rbEvent.init();
                rbEvent.go();
            }
            catch (AppiaEventException e)
            {
                System.err.println("cannot broadcast message to rb in call request");
                e.printStackTrace();
            }
            catch (CloneNotSupportedException e)
            {
                e.printStackTrace();
            }

        }
        else
        {
            try {
                Clique cl = cliques.get(0);
                SampleSendableEvent mtobEvent = (SampleSendableEvent) event.cloneEvent();
                mtobEvent.setChannel(mtobChannels.get(cl));
                mtobEvent.setDir(Direction.DOWN);
                mtobEvent.setSourceSession(this);
                MTOBPayload payload = new MTOBPayload();
                payload.m = c.globalId;
//                payload.c = cl.name;
                payload.c = null;
                payload.callType = c.methodName;
                mtobEvent.setMtobPayload(payload);
                mtobEvent.init();
                mtobEvent.go();
            }

            catch (AppiaEventException e)
            {
                System.err.println("cannot broadcast message to rb in call request");
                e.printStackTrace();
            }
            catch (CloneNotSupportedException e)
            {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private boolean check(Call c) {
        ArrayList<Clique> cls = analyzer.getCliquesForMethod(c.methodName);
        boolean check = true;
        for(Clique cl : cls)
        {
            try {
                if(!q.get(cl).peekFirst().equals(c))
                    return false;
            }
            catch (NullPointerException e)
            {
                return false;
            }

        }
        for(Clique cl : cls)
            q.get(cl).pollFirst();

        if(isOriginatingNode(c))
        {
            if(isPermissible(c)) {
                    executeCommandAndReturnIndication(c);
                    ArrayList<Call> cs = getAllCallTypesInExecByName(analyzer.getDependenciesForMethod(c.methodName));
                    c.deps = cs;
                    sendCommitMessage(c);
                }
                else {
                    try {
                        AbortVote abortVote = new AbortVote(mainChannel, Direction.DOWN, this);
                        abortVote.getMessage().pushObject(c);
                        abortVote.go();
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
        else {
            ArrayList<Call> cs = getAllCallTypesInExecByName(analyzer.getDependenciesForMethod(c.methodName));
            c.deps = cs;
            waitForDeps(c);
        }
//        executeCall(c);
        checkQs();
        return true;
    }

    @SuppressWarnings("unchecked")
    private void checkQs() {
        for(Clique cl : analyzer.getAllCliques()) {
            Call c = q.get(cl).peekFirst();
            if(c != null) {
                check(c);
                return;
            }
        }
    }
    /**
     * Called when the lower protocol delivers a message.
     *
     * @param event
     */
    private void rbDeliver(SendableEvent event) {
        Call c = (Call) event.getMessage().peekObject();
        Debug.print("Executing " + c.toString());
        executeRBCalls((Call) event.getMessage().popObject());
    }


    private void mtobDelivery(SampleSendableEvent event) {
        Call c = (Call) event.getMessage().peekObject();
        Clique cl = analyzer.getCliqueByName(event.getChannel().getChannelID());
        ArrayList<Clique> cls = analyzer.getCliquesForMethod(c.methodName);
        Debug.print("MTOB: Received message from mtob "+ cl.name);
        q.get(cl).add(c);
        if(cls.get(cls.size()-1) == cl)
        {
            check(c);
        }
        else
        {
            try {
                Clique nextCl = null;
                for(int i = 0; i < cls.size(); i++)
                {
                    if(cls.get(i) == cl)
                    {
                        nextCl = cls.get(i+1);
                        break;
                    }
                }
                SampleSendableEvent rbEvent = (SampleSendableEvent) event.cloneEvent();
                rbEvent.setChannel(mtobChannels.get(nextCl));
                rbEvent.setDir(Direction.DOWN);
                rbEvent.setSourceSession(this);
                MTOBPayload payload = new MTOBPayload();
                payload.m = c.globalId;
                payload.c = cl.name;
                payload.callType = c.methodName;
                rbEvent.setMtobPayload(payload);
                rbEvent.init();
                rbEvent.go();
            }
            catch (CloneNotSupportedException e){
                Debug.print("indication from mtob, cannot clone to request to mtob");
                e.printStackTrace();
            }
            catch (AppiaEventException e)
            {
                Debug.print("indication from mtob, cannot send event");
                e.printStackTrace();
            }
        }
    }

    /**
     * Called by this protocol to send a message to the lower protocol.
     *
     * @param event
     */
    private void bebBroadcast(SendableEvent event) {
        Debug.print("RB: sending message to beb.");
        try {
            event.setDir(Direction.DOWN);
            event.setSourceSession(this);
            event.init();
            event.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when some process crashed.
     *
     * @param crash
     */
    private void handleCrash(Crash crash) {
        int pi = crash.getCrashedProcess();
        try {
            crash.go();
        } catch (AppiaEventException ex) {
            ex.printStackTrace();
        }
        processes.getProcess(pi).setCorrect(false);
        for(SendableEvent event : from[pi])
            bebBroadcast(event);
        from[pi].clear();
    }

    private void executeRBCalls(Call c)
    {
        if(isPermissible(c))
            executeCommandAndReturnIndication(c);
        else
            rejectCommandAndAbortIndication(c);
    }

    public void atomicDecide(CommitVote event)
    {
        Call c = (Call)event.getMessage().popObject();
        commitMessages.add(c.globalId);
        if(isPermissible(c))
        {
            Debug.printExec("executing commit message for " + c.globalId);
            executeCommandAndReturnIndication(c);
            blockedCalls.remove(c);
            return;
        }
        Debug.printExec("received commit message for " + c.globalId);
    }
    public void abortVoteDeliver(AbortVote event)
    {
        rejectCommandAndAbortIndication((Call)event.getMessage().popObject());
    }

    private ArrayList<Call> getAllCallTypesInExecByName(ArrayList<String> methodNames)
    {
        ArrayList<Call> intersection = new ArrayList<>();
        if(methodNames == null)
            return intersection;
        for (String method : methodNames)
        {
            intersection.addAll(xed.get(method));
        }
        return intersection;
    }

    public void checkForBlockedCalls()
    {
        LinkedList<Call> tobeRemoved = new LinkedList<>();
        for (Call c : blockedCalls)
        {
            ArrayList<String> dependencies =  analyzer.getDependenciesForMethod(c.methodName);
            ArrayList<Call> executedDeps = getAllCallTypesInExecByName(dependencies);
            if(commitMessages.contains(c.globalId)&& executedDeps.containsAll(c.deps))
            {
                Debug.printExec("executing " + c.globalId + " from before ...");
                executeCommandAndReturnIndication(c);
//                blockedCalls.remove(c);
                tobeRemoved.add(c);
                commitMessages.remove(c.globalId);
            }
        }
        blockedCalls.removeAll(tobeRemoved);
    }

    public boolean isPermissible(Call c)
    {
        Method method = object.getMethod(c.methodName);
        Object[] guardParams = new Object[method.getParameterCount()];
        Method guard = object.getGuard(c.methodName);
        Method invar = object.getInvariant();
        for (int i = 0; i < method.getParameterCount(); i++)
            guardParams[i] = Integer.parseInt(c.getArgsArray()[i]);

        try {
            Boolean guardRet = (Boolean) guard.invoke(object, guardParams);
            ReplicatedObjectState state = (ReplicatedObjectState) method.invoke(object, guardParams);
            Boolean invarRet = (Boolean) invar.invoke(object, state);
            return guardRet && invarRet;
        }
        catch (Exception e)
        {
            System.err.println(e);
            System.err.println("exception in checking permissibility");
        }
        return false;
    }

    private void sendCommitMessage(Call c)
    {
        try {
            Debug.printExec("sending commit message for " + c.globalId);
            CommitVote cv = new CommitVote(mainChannel, Direction.DOWN, this);
            cv.getMessage().pushObject(c);
            cv.go();
        }
        catch (Exception e)
        {

        }
    }

    private boolean executeCommandAndReturnIndication(Call c)
    {
        if(!xed.get(c.methodName).contains(c)) {
            Method method = object.getMethod(c.methodName);
            Object[] guardParams = new Object[method.getParameterCount()];
            for (int i = 0; i < method.getParameterCount(); i++)
                guardParams[i] = Integer.parseInt(c.getArgsArray()[i]);
            try {
//                System.out.println("executing " + c);
                ReplicatedObjectState state = (ReplicatedObjectState) method.invoke(object, guardParams);
                object.setState(state);
//                System.out.println("after(object.state): " + object.getState());
                xed.get(c.methodName).add(c);
                ReturnIndication returnIndication = new ReturnIndication(mainChannel, Direction.UP, this);
                returnIndication.methodCall = c;
                returnIndication.returnValue = state;
                returnIndication.go();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private void rejectCommandAndAbortIndication(Call c)
    {
        try {
            AbortIndication abortIndication = new AbortIndication(mainChannel, Direction.UP, this);
            abortIndication.methodCall = c;
            xed.get(c.methodName).add(c);
            abortIndication.go();
        }
        catch (Exception e)
        {

        }
    }

    private boolean isOriginatingNode(Call c)
    {
        return processes.getSelfRank() == Integer.valueOf(c.globalId.substring(1, c.globalId.indexOf(',')));
    }

    private void waitForDeps(Call c)
    {
        Debug.printExec("adding " + c.globalId + " to waitlist for dependencies");
        blockedCalls.add(c);
    }
}
