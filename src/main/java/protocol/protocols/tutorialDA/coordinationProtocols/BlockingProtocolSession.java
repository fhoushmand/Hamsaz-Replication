package main.java.protocol.protocols.tutorialDA.coordinationProtocols;

import main.java.analyser.Analyzer;
import main.java.protocol.protocols.tutorialDA.events.ProcessInitEvent;
import main.java.protocol.protocols.tutorialDA.events.SampleSendableEvent;
import main.java.protocol.protocols.tutorialDA.utils.Call;
import main.java.protocol.protocols.tutorialDA.utils.Debug;
import main.java.protocol.protocols.tutorialDA.utils.ProcessSet;
import main.java.robject.ReplicatedObject;
import robject.ReplicatedObjectState;
import net.sf.appia.core.*;
import net.sf.appia.core.events.SendableEvent;
import net.sf.appia.core.events.channel.ChannelInit;

import java.lang.reflect.Method;
import java.util.*;

public class BlockingProtocolSession extends Session
{

    ReplicatedObject object;
    Analyzer analyzer;

    private ProcessSet processes;

    // method -> Set<Call>
    private HashMap<String, ArrayList<Call>> xed;

    // method -> int
    private HashMap<String, Integer> b;

    // method -> boolean
    private HashMap<String, Boolean> act;

    // call -> int
    private HashMap<String, Integer> cnt;

    private ArrayList<String> commitMessages;

    public Channel rbChannel;

    public HashMap<String, Channel> tobChannelInits;

    private HashMap<String, Channel> tobChannels;

    private LinkedList<Call> nCoordRBWaitingList;

    private LinkedList<Call> coordTOBWaitingList;

    private LinkedList<Call> blockedCallsWaitingForDeps;


    public BlockingProtocolSession(Layer layer, ReplicatedObject obj, Analyzer a)
    {
        super(layer);
        this.object = obj;
        this.analyzer = a;
    }


    @Override
    public void handle(Event event) {
        if (event instanceof ChannelInit)
            handleChannelInit((ChannelInit) event);
        else if (event instanceof ProcessInitEvent)
            handleProcessInit((ProcessInitEvent) event);
        if(event.getDir() == Direction.UP && event instanceof SendableEvent)
        {
            // receive from tob channel
            if(event.getChannel().getChannelID().startsWith("TOB"))
            {
                coordTOBDeliver((CoordCall)event);
            }
            else if(event.getChannel().getChannelID().startsWith("RB"))
            {
                //commit message for dep
                if(event.getClass().getName().equals(CommitVote.class.getName()))
                {
                    commitVoteDeliver((CommitVote)event);
                }

                //abort message for dep
                if(event.getClass().getName().equals(AbortVote.class.getName()))
                {
                    abortVoteDeliver((AbortVote)event);
                }
                //call comes from rb
                else if(event.getClass().getName().equals(CoordCall.class.getName()))
                {
                    coordRBDeliver((CoordCall)event);
                }
                //call comes from rb
                else if(event.getClass().getName().equals(NCoordCall.class.getName()))
                {
                    nCoordRBDeliver((NCoordCall)event);
                }
                //call comes from rb
                else if(event.getClass().getName().equals(Update.class.getName()))
                {
                    updateRBDeliver((Update)event);
                }


            }
        }
        else if(event.getClass().getName().equals(SampleSendableEvent.class.getName())) {
            request((SampleSendableEvent) event);
        }
        if(!(event instanceof ChannelInit) && !(event instanceof ProcessInitEvent))
            periodicCall();

    }

    private void handleChannelInit(ChannelInit init)
    {
        try {
            if(rbChannel == null)
            {
                tobChannels = new HashMap<>();
                rbChannel = init.getChannel();
                for(Channel c : tobChannelInits.values())
                    c.start();
            }
            else if(tobChannels.size() < 3)
            {
                for(Map.Entry entry : tobChannelInits.entrySet())
                {
                    if(init.getChannel().getChannelID().split("_")[0].startsWith("TOB"))
                    {
                        tobChannels.put(init.getChannel().getChannelID().split("_")[1], init.getChannel());
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
    }

    private void handleProcessInit(ProcessInitEvent event)
    {
        processes = event.getProcessSet();
        xed = new HashMap<String, ArrayList<Call>>();
        b = new HashMap<>();
        act = new HashMap<>();
        cnt = new HashMap<>();
        commitMessages = new ArrayList<>();
        nCoordRBWaitingList = new LinkedList<>();
        coordTOBWaitingList = new LinkedList<>();
        blockedCallsWaitingForDeps = new LinkedList<>();
        for (Method method : object.getAllMethodsOfObject()) {
            xed.put(method.getName(), new ArrayList<Call>());
            b.put(method.getName(), 0);
            act.put(method.getName(), false);
        }

        try {
            event.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }

    }

    private void request(SampleSendableEvent event){
        Call call = (Call)event.getMessage().popObject();
//        MessageID id = new MessageID(processes.getSelfRank(), globalSeq);
//        globalSeq++;
//        call.globalId = id.toString();
        event.getMessage().pushObject(call);

        // no need to coordinate
        if(!analyzer.getCover().contains(call.methodName))
        {
            NCoordCall nCoordCall = null;
            try {
                nCoordCall = new NCoordCall(rbChannel, Direction.DOWN, this);
                nCoordCall.getMessage().pushObject(call);
                nCoordCall.init();
                nCoordCall.go();
            } catch (AppiaEventException e) {
                e.printStackTrace();
            }

        }
        //needs coordination
        else
        {
            //tob

            if(analyzer.getConflictMap().get(call.methodName).contains(call.methodName))
            {
                CoordCall coordCall = null;
                try {
                    coordCall = new CoordCall(tobChannels.get(call.methodName), Direction.DOWN, this);
                    coordCall.getMessage().pushObject(call);
                    coordCall.init();
                    coordCall.go();
                } catch (AppiaEventException e) {
                    e.printStackTrace();
                }
            }
            //rb
            else
            {
                CoordCall coordCall = null;
                try {
                    coordCall = new CoordCall(rbChannel, Direction.DOWN, this);
                    coordCall.getMessage().pushObject(call);
                    coordCall.init();
                    coordCall.go();
                } catch (AppiaEventException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void nCoordRBDeliver(NCoordCall event)
    {
        Call call = (Call)event.getMessage().popObject();
        if(b.get(call.methodName) == 0)
        {
            executeRBCalls(call);
        }
    }

    private void coordTOBDeliver(CoordCall event)
    {
        Call call = (Call)event.getMessage().popObject();
        if(!act.get(call.methodName))
        {
            act.put(call.methodName, true);
            blockAndUpdate(call);
        }
        else
            coordTOBWaitingList.add(call);
    }

    private void coordRBDeliver(CoordCall event)
    {
        Call call = (Call)event.getMessage().popObject();
        blockAndUpdate(call);
    }

    private void updateRBDeliver(Update event)
    {
        ArrayList<Call> deps = (ArrayList<Call>)event.getMessage().popObject() ;
        Call call = (Call)event.getMessage().popObject();
        for (Call c : deps)
        {
            executeRBCalls(c);
        }

        if(cnt.get(call.globalId) == null)
            cnt.put(call.globalId, 0);
        int x = cnt.get(call.globalId);
        cnt.put(call.globalId, ++x);
        if(x == processes.getSize())
        {
//            executeRBCalls(call);
            //doing dep tracking
            if(isOriginatingNode(call))
            {
                if(isPermissible(call)) {
                    executeCommandAndReturnIndication(call);
                    ArrayList<Call> cs = getAllCallTypesInExecByName(analyzer.getDependenciesForMethod(call.methodName));
                    call.deps = cs;
                    sendCommitMessage(call);
                }
                else {
                    sendAbortMessage(call);
                }
            }
            else {
                ArrayList<Call> cs = getAllCallTypesInExecByName(analyzer.getDependenciesForMethod(call.methodName));
                call.deps = cs;
                waitForDeps(call);
            }

            for (String m : analyzer.getConflictMap().get(call.methodName))
            {
                int t = b.get(m);
                b.put(m, --t);
            }
            act.put(call.methodName, false);
        }


    }

    private void blockAndUpdate(Call call)
    {
        for (String m : analyzer.getConflictMap().get(call.methodName))
        {
            int x = b.get(m);
            b.put(m, ++x);
        }
        ArrayList<Call> deps = getAllCallTypesInExecByName(analyzer.getConflictMap().get(call.methodName));

        Update update = null;
        try {
            update = new Update(rbChannel, Direction.DOWN, this);
            update.getMessage().pushObject(call);
            update.getMessage().pushObject(deps);
            update.init();
            update.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }

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


    private void executeRBCalls(Call c)
    {
        if(isPermissible(c))
            executeCommandAndReturnIndication(c);
        else
            rejectCommandAndAbortIndication(c);
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
            System.err.println("exception in checking permissibility");
            e.printStackTrace();
        }
        return false;
    }

    private boolean executeCommandAndReturnIndication(Call c)
    {
        if(!xed.get(c.methodName).contains(c)) {
            Method method = object.getMethod(c.methodName);
            Object[] guardParams = new Object[method.getParameterCount()];
            for (int i = 0; i < method.getParameterCount(); i++)
                guardParams[i] = Integer.parseInt(c.getArgsArray()[i]);
            try {
                ReplicatedObjectState state = (ReplicatedObjectState) method.invoke(object, guardParams);
                object.setState(state);
                xed.get(c.methodName).add(c);
                ReturnIndication returnIndication = new ReturnIndication(rbChannel, Direction.UP, this);
                returnIndication.methodCall = c;
                returnIndication.returnValue = state;
                returnIndication.go();
                return true;
            } catch (Exception e) {
                System.out.println("exception in execution");
                return false;
            }
        }
        else
            return false;

    }

    private void rejectCommandAndAbortIndication(Call c)
    {
        try {
            AbortIndication abortIndication = new AbortIndication(rbChannel, Direction.UP, this);
            abortIndication.methodCall = c;
            abortIndication.go();
        }
        catch (Exception e)
        {

        }
    }

    private void nCoordRBWaitingCalls()
    {
        LinkedList<Call> toBeRemoved = new LinkedList<>();
        for (Call c : nCoordRBWaitingList)
        {
            if(b.get(c.methodName) == 0) {
                executeRBCalls(c);
                toBeRemoved.add(c);
            }
        }
        nCoordRBWaitingList.removeAll(toBeRemoved);
    }

    private void coordTOBWaitingCalls()
    {
        LinkedList<Call> toBeRemoved = new LinkedList<>();
        for (Call c : coordTOBWaitingList)
        {
            if(!act.get(c.methodName)) {
                executeRBCalls(c);
                toBeRemoved.add(c);
            }
        }
        coordTOBWaitingList.removeAll(toBeRemoved);
    }

    private boolean isOriginatingNode(Call c)
    {
        return processes.getSelfRank() == Integer.valueOf(c.globalId.substring(1, c.globalId.indexOf(',')));
    }

    private void waitForDeps(Call c)
    {
        Debug.printExec("adding " + c.globalId + " to waitlist for dependencies");
        blockedCallsWaitingForDeps.add(c);
    }





    public void commitVoteDeliver(CommitVote event)
    {
        Call c = (Call)event.getMessage().popObject();
        commitMessages.add(c.globalId);
        if(isPermissible(c))
        {
            Debug.printExec("executing commit message for " + c.globalId);
            executeCommandAndReturnIndication(c);
            blockedCallsWaitingForDeps.remove(c);
            return;
        }
        Debug.printExec("received commit message for " + c.globalId);
//        checkForBlockedCalls();
    }

    public void abortVoteDeliver(AbortVote event)
    {
        rejectCommandAndAbortIndication((Call)event.getMessage().popObject());
    }


    public void checkForBlockedCalls()
    {
        LinkedList<Call> tobeRemoved = new LinkedList<>();
        for (Call c : blockedCallsWaitingForDeps)
        {
            ArrayList<String> dependencies =  analyzer.getDependenciesForMethod(c.methodName);
            ArrayList<Call> executedDeps = getAllCallTypesInExecByName(dependencies);
            if(commitMessages.contains(c.globalId)&& executedDeps.containsAll(c.deps))
            {
                Debug.printExec("executing " + c.globalId + " from before ...");
                executeCommandAndReturnIndication(c);
                tobeRemoved.add(c);
                commitMessages.remove(c.globalId);
            }
        }
        blockedCallsWaitingForDeps.removeAll(tobeRemoved);
    }

    private void sendCommitMessage(Call c)
    {
        try {
            Debug.printExec("sending commit message for " + c.globalId);
            CommitVote cv = new CommitVote(rbChannel, Direction.DOWN, this);
            cv.getMessage().pushObject(c);
            cv.go();
        }
        catch (Exception e)
        {

        }
    }

    private void sendAbortMessage(Call c)
    {
        try {
            Debug.printExec("sending abort message for " + c.globalId);
            AbortVote cv = new AbortVote(rbChannel, Direction.DOWN, this);
            cv.getMessage().pushObject(c);
            cv.go();
        }
        catch (Exception e)
        {

        }
    }

    private void periodicCall()
    {
        nCoordRBWaitingCalls();
        coordTOBWaitingCalls();
        checkForBlockedCalls();
    }



}


