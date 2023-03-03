package main.java.protocol.protocols.tutorialDA.consensusUTO;

import main.java.protocol.protocols.tutorialDA.coordinationProtocols.AbortIndication;
import main.java.protocol.protocols.tutorialDA.coordinationProtocols.ReturnIndication;
import main.java.protocol.protocols.tutorialDA.events.ProcessInitEvent;
import main.java.protocol.protocols.tutorialDA.utils.Call;
import main.java.protocol.protocols.tutorialDA.utils.Debug;
import main.java.robject.ReplicatedObject;
import robject.ReplicatedObjectState;
import net.sf.appia.core.*;
import net.sf.appia.core.events.SendableEvent;
import net.sf.appia.core.events.channel.ChannelInit;

import java.lang.reflect.Method;
import java.net.SocketAddress;


public class ReplicatedStateMachineSession extends Session {

    private SocketAddress iwp;
    private Channel channel;

    private ReplicatedObject object;


    public ReplicatedStateMachineSession(Layer l) {
        super(l);
    }

    public ReplicatedStateMachineSession(Layer layer, ReplicatedObject obj) {
        super(layer);
        this.object = obj;
    }

    /**
     * The event handler function. Dispatches the new event to the appropriate
     * function.
     *
     * @param e
     *          the event
     */
    public void handle(Event e) {
        if (e instanceof ChannelInit)
            handleChannelInit((ChannelInit) e);
        else if (e instanceof ProcessInitEvent)
            handleProcessInitEvent((ProcessInitEvent) e);
        else if (e instanceof SendableEvent) {
            if (e.getDir() == Direction.DOWN)
                handleSendableEventDOWN((SendableEvent) e);
            else
                handleSendableEventUP((SendableEvent) e);
        }
        else {
            try {
                e.go();
            } catch (AppiaEventException ex) {
                System.out.println("[ReplicatedStateMachineSession:handle]" + ex.getMessage());
            }
        }
    }

    /**
     * Handles channelInit event. Initializes the two lists.
     *
     * @param e
     *          the channelinit event just arrived.
     */
    public void handleChannelInit(ChannelInit e) {
        Debug.print("TO: handle: " + e.getClass().getName());

        try {
            e.go();
        } catch (AppiaEventException ex) {
            System.out.println("[ReplicatedStateMachineSession:handleCI]:1:" + ex.getMessage());
        }

        //object.setState();

        this.channel = e.getChannel();

    }

    /**
     * Handles process init event. Now, it's the right time to initialize the
     * consensus protocol.
     *
     * @param e
     *          the sendable event.
     */
    public void handleProcessInitEvent(ProcessInitEvent e) {

        iwp = e.getProcessSet().getSelfProcess().getSocketAddress();

        try {
            e.go();
        } catch (AppiaEventException ex) {
            System.out.println("[ReplicatedStateMachineSession:handlePI]:1:" + ex.getMessage());
        }
    }

    private void handleSendableEventDOWN(SendableEvent e) {
        Debug.print("TO: handle: " + e.getClass().getName() + " DOWN");

        Call c = (Call)e.getMessage().peekObject();


        try
        {
            e.go();
        }
        catch (AppiaEventException ex)
        {
            System.out.println("[ReplicatedStateMachineSession:handleDOWN]" + ex.getMessage());
        }

    }

    private void handleSendableEventUP(SendableEvent e) {

        Call c = (Call) e.getMessage().popObject();

        executeCall(c, e);//this.executeCall(c);

//        state = execute();
//        System.out.println("New State Value" +state);
        try {
            e.go();
        } catch (AppiaEventException ex) {
            System.out.println("[ReplicatedStateMachineSession:handleDecide]"
                    + ex.getMessage());
        }
    }


    private void executeCall(Call c, SendableEvent e)
    {
        Method method = object.getMethod(c.methodName);
        Object[] guardParams = new Object[method.getParameterCount()];
        Method guard = object.getGuard(c.methodName);
        Method invar = object.getInvariant();
        for (int i = 0; i < method.getParameterCount(); i++)
        {
            guardParams[i] = Integer.parseInt(c.getArgsArray()[i]);
        }

        try {

            Boolean guardRet = (Boolean) guard.invoke(object, guardParams);
//            Debug.printExec("Guard: "+guardRet);
            ReplicatedObjectState state = (ReplicatedObjectState) method.invoke(object, guardParams);
            Boolean invarRet = (Boolean) invar.invoke(object, state);
//            Debug.printExec("Invar: "+invarRet);
//            Debug.printExec("before: "+object.getState().balance);
            if(guardRet && invarRet)
            {
                //done
                object.setState(state);
                ReturnIndication ret = new ReturnIndication(channel, Direction.UP, this);
                ret.returnValue = state;
                ret.methodCall = c;
                //ret.setExecutionTime(e.getMessage().popLong() - System.nanoTime());
                //ret.setExecutionTime(System.nanoTime());
                ret.go();
            }
            else {
                AbortIndication ret = new AbortIndication(channel, Direction.UP, this);
                ret.methodCall = c;

                //ret.setExecutionTime(e.getMessage().popLong() - System.nanoTime());
                //ret.setExecutionTime(System.nanoTime());
                ret.go();
            }
//            Debug.printExec("after: "+object.getState().balance);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


    }


}
