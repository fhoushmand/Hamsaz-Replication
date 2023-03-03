/*
 *
 * Hands-On code of the book Introduction to Reliable Distributed Programming
 * by Christian Cachin, Rachid Guerraoui and Luis Rodrigues
 * Copyright (C) 2005-2011 Luis Rodrigues
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 *
 * Contact
 * 	Address:
 *		Rua Alves Redol 9, Office 605
 *		1000-029 Lisboa
 *		PORTUGAL
 * 	Email:
 * 		ler@ist.utl.pt
 * 	Web:
 *		http://homepages.gsd.inesc-id.pt/~ler/
 *
 */

package main.java.protocol.protocols.tutorialDA.sampleAppl;

import main.java.protocol.protocols.tutorialDA.consensusUtils.StringProposal;
import main.java.protocol.protocols.tutorialDA.coordinationProtocols.AbortIndication;
import main.java.protocol.protocols.tutorialDA.coordinationProtocols.ReturnIndication;
import main.java.protocol.protocols.tutorialDA.delay.DelayEvent;
import main.java.protocol.protocols.tutorialDA.events.*;
import main.java.protocol.protocols.tutorialDA.tcpBasedPFD.PFDStartEvent;
import main.java.protocol.protocols.tutorialDA.utils.Call;
import main.java.protocol.protocols.tutorialDA.utils.MessageID;
import main.java.protocol.protocols.tutorialDA.utils.ProcessSet;
import main.java.robject.ReplicatedObject;
import net.sf.appia.core.*;
import net.sf.appia.core.events.channel.ChannelClose;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.protocols.common.RegisterSocketEvent;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * Session implementing the sample application.
 *
 * @author nuno
 */
public class SampleApplSession extends Session implements Runnable {
    AtomicInteger replicasJoined = new AtomicInteger(0);
    public boolean printed = false;
    public ProcessSet processes;
    Channel channel;
    private SampleApplReader reader;
    private SampleApplFileReader fileReader;
    private int globalSeq = 0;
    private boolean blocked = false;
    private final HashMap<String, Integer> numberOfOps = new HashMap<>();
    private final HashMap<String, Long> totalResTime = new HashMap<>();
    //  private HashMap<String,HashMap<String, Long>> startTime = new HashMap<>();
    private final HashMap<String, HashMap<String, Long>> endTime = new HashMap<>();
    private String callsLoc;
    private ReplicatedObject replicatedObject;
    private long delay;

    private int ops;
    private int numberOfNodes;
    private int receivedOps = 0;
    private int executedOps = 0;
    private long startSim;
    private long endSim;
    private int exitReceived = 0;

    public SampleApplSession(Layer layer) {
        super(layer);
    }

    public SampleApplSession(Layer layer, int nodn, int oppn, String fileLoc, ReplicatedObject obj, long delay) {
        super(layer);
        this.replicatedObject = obj;
        for (Method method : obj.getAllMethodsOfObject()) {
//      startTime.put(method.getName(), new HashMap<>()) ;
            endTime.put(method.getName(), new HashMap<>());
            numberOfOps.put(method.getName(), 0);
            totalResTime.put(method.getName(), 0l);
        }
        ops = oppn;
        numberOfNodes = nodn;
        callsLoc = fileLoc;
        this.delay = delay;
    }

    @Override
    public void run() {

//    try {
//      Thread.sleep(10000);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
//    while (true) {
//      inactiveTime = new Date().getTime()-lastMessagTime;
//      if (inactiveTime > MAX_INACTIVE_TIMEOUT && i < 20) {
//        endSim = new Date().getTime();
//        calculateAverageResponseTime();
//        calculateThroughput();
//        i++;
//        if(i == 20)
//          System.exit(0);
////        break;
//
//      }
//    }
    }

    public void init(ProcessSet processes) {
        this.processes = processes;
    }

    public void handle(Event event) {
        if (event instanceof SampleSendableEvent)
            handleSampleSendableEvent((SampleSendableEvent) event);
        else if (event instanceof ChannelInit)
            handleChannelInit((ChannelInit) event);
        else if (event instanceof ChannelClose)
            handleChannelClose((ChannelClose) event);
        else if (event instanceof RegisterSocketEvent)
            handleRegisterSocket((RegisterSocketEvent) event);
        else if (event instanceof ConsensusDecide)
            handleConsensusDecide((ConsensusDecide) event);
        else if (event instanceof NBACDecide)
            handleNBACDecide((NBACDecide) event);
        else if (event instanceof ViewEvent)
            handleMembView((ViewEvent) event);
        else if (event instanceof BlockEvent)
            handleBlock((BlockEvent) event);
        else if (event instanceof ReleaseEvent)
            handleRelease((ReleaseEvent) event);
        else if (event instanceof SharedReadReturn)
            handleSharedReadReturn((SharedReadReturn) event);
        else if (event instanceof SharedWriteReturn)
            handleSharedWriteReturn((SharedWriteReturn) event);
        else if (event instanceof ReturnIndication)
            handleReturnIndication((ReturnIndication) event);
        else if (event instanceof AbortIndication)
            handleAbortIndication((AbortIndication) event);

    }

    /**
     * @param event
     */
    private void handleRegisterSocket(RegisterSocketEvent event) {
        if (event.error) {
            System.out.println("Address already in use!");
            System.exit(2);
        }
    }

    /**
     * @param init
     */
    private void handleChannelInit(ChannelInit init) {
        try {
            init.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }
        channel = init.getChannel();

        try {
            // sends this event to open a socket in the layer that is used has perfect
            // point to point
            // channels or unreliable point to point channels.
            RegisterSocketEvent rse = new RegisterSocketEvent(channel,
                    Direction.DOWN, this);
            rse.port = ((InetSocketAddress) processes.getSelfProcess().getSocketAddress()).getPort();
            rse.localHost = ((InetSocketAddress) processes.getSelfProcess().getSocketAddress()).getAddress();
            rse.go();
            ProcessInitEvent processInit = new ProcessInitEvent(channel,
                    Direction.DOWN, this);
            processInit.setProcessSet(processes);
            processInit.go();
        } catch (AppiaEventException e1) {
            e1.printStackTrace();
        }
        System.out.println("Channel is open.");
//    if(processes.getSelfRank() == 0) {
        // starts the thread that reads from the keyboard.
//      reader = new SampleApplReader(this);
//      reader.start();
//    try {
//      Thread.sleep(10000);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
        fileReader = new SampleApplFileReader(this, callsLoc, delay, channel, replicasJoined);
        fileReader.start();
//    }
    }

    /**
     * @param close
     */
    private void handleChannelClose(ChannelClose close) {
        channel = null;
        System.out.println("Channel is closed.");
    }

    /**
     * @param event
     */
    private void handleSampleSendableEvent(SampleSendableEvent event) {
        if (event.getDir() == Direction.DOWN)
            handleOutgoingEvent(event);
        else
            handleIncomingEvent(event);
    }

    /**
     * @param decide
     */
    private void handleConsensusDecide(ConsensusDecide decide) {
        System.out.println("Receive Consensus decision: "
                + ((StringProposal) decide.decision).msg);
    }

    /**
     * @param decide
     */
    private void handleNBACDecide(NBACDecide decide) {
        System.out.println("Commit " + decide.decision);
    }

    /**
     * @param event
     */
    private void handleMembView(ViewEvent event) {
        System.out.println("View: " + event.view);
    }

    /**
     * @param event
     */
    private void handleRelease(ReleaseEvent event) {
        System.out.println("The group is no longer blocked, messages can be sent.");
        blocked = false;
    }

    /**
     * @param event
     */
    private void handleBlock(BlockEvent event) {
        System.out
                .println("The group is blocked for view change, therefore messages can not be sent.");
        blocked = true;

        try {
            BlockOkEvent ev = new BlockOkEvent(event.getChannel(), Direction.DOWN,
                    this);
            ev.go();
        } catch (AppiaEventException ex) {
            ex.printStackTrace();
        }
    }

    private void handleSharedReadReturn(SharedReadReturn event) {
        System.out.println("Register " + event.reg + " reads " + event.value);
    }

    private void handleSharedWriteReturn(SharedWriteReturn event) {
        System.out.println("Register " + event.reg + " written");
    }

    /**
     * @param event
     */
    private void handleIncomingEvent(SampleSendableEvent event) {
        String message = event.getMessage().popString();
//    System.out.print("Received event with message: " + message + "\n>");
    }

    /**
     * @param event
     */
    private void handleReturnIndication(ReturnIndication event) {
        receivedOps++;
        if (processes.getSelfRank() == Integer.parseInt(event.methodCall.globalId.substring(1, event.methodCall.globalId.indexOf(',')))) {
            long now = System.nanoTime();
            long responseTime = TimeUnit.NANOSECONDS.toMillis(now - event.methodCall.startTime);
            numberOfOps.put(event.methodCall.methodName, numberOfOps.get(event.methodCall.methodName) + 1);
            totalResTime.put(event.methodCall.methodName, totalResTime.get(event.methodCall.methodName) + responseTime);
        }
        if (receivedOps >= ops * numberOfNodes && !printed) {
            printed = true;
            endSim = new Date().getTime();
            System.out.println("start: " + startSim);
            System.out.println("end: " + endSim);
            calculateAverageResponseTime();
            calculateThroughput();
        }
    }

    private void calculateAverageResponseTime() {
        double tot = 0;
        for (String methods : totalResTime.keySet()) {
            tot += (((double) totalResTime.get(methods) / (double) numberOfOps.get(methods)));
            System.out.println("average " + methods + " response time: " + (((double) totalResTime.get(methods) / (double) numberOfOps.get(methods))) + " for " + numberOfOps.get(methods) + " calls");
        }
        System.out.println("total average response time: " + tot / totalResTime.keySet().size());
    }

    private void calculateThroughput() {
        System.out.println("throughput: " + (double) (ops * numberOfNodes) / ((endSim - startSim)));
    }

    /**
     * @param event
     */
    private void handleAbortIndication(AbortIndication event) {
        receivedOps++;
        if (processes.getSelfRank() == Integer.parseInt(event.methodCall.globalId.substring(1, event.methodCall.globalId.indexOf(',')))) {
            long now = System.nanoTime();
            System.out.print("Aborted call to : " + event.methodCall + " -> " + event.methodCall.globalId + "\n");
            long responseTime = TimeUnit.NANOSECONDS.toMillis(now - event.methodCall.startTime);
            numberOfOps.put(event.methodCall.methodName, numberOfOps.get(event.methodCall.methodName) + 1);
            totalResTime.put(event.methodCall.methodName, totalResTime.get(event.methodCall.methodName) + responseTime);
        }
        if (receivedOps >= ops * numberOfNodes && !printed) {
            printed = true;
            endSim = new Date().getTime();
            System.out.println("start: " + startSim);
            System.out.println("end: " + endSim);
            calculateAverageResponseTime();
            calculateThroughput();
            System.out.println((replicatedObject.getState()));
        }
    }

    /**
     * @param event
     */
    private void handleOutgoingEvent(SampleSendableEvent event) {
        String command = event.getCommand();
        if ("bcast".equals(command))
            handleBCast(event);
        else if ("join".equals(command))
            handleJoin(event);
        else if ("startpfd".equals(command))
            handleStartPFD(event);
        else if ("consensus".equals(command))
            handleConsensus(event);
        else if ("atomic".equals(command))
            handleAtomic(event);
        else if ("read".equals(command))
            handleRead(event);
        else if ("write".equals(command))
            handleWrite(event);
        else if ("delay".equals(command))
            handleDelay(event);
        else if ("call".equals(command))
            handleCallToMethod(event);
        else if ("help".equals(command))
            printHelp();
        else if ("exit-sim".equals(command))
            handleExitSim(event);
        else {
            System.out.println("Invalid command: " + command);
            printHelp();
        }
    }

    /**
     * @param event
     */
    private void handleBCast(SampleSendableEvent event) {
        if (blocked) {
            System.out
                    .println("The group is blocked, therefore message can not be sent.");
            return;
        }

        try {
            event.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }
    }

    private void handleJoin(SampleSendableEvent event) {
        replicasJoined.incrementAndGet();
        System.out.println("a replica joined");
    }

    private void handleCallToMethod(SampleSendableEvent event) {
        if (blocked) {
            System.out
                    .println("The group is blocked, therefore message can not be sent.");
            return;
        }

//    if(processes.getSelfRank() == 0) {
        try {
            if (executedOps == 0) {
                startSim = new Date().getTime();
                Thread per = new Thread(this);
                per.start();
            }
            Call c = event.getCallToMethod();
            MessageID id = new MessageID(processes.getSelfRank(), globalSeq);
            globalSeq++;
            String identifier = id.toString();
            c.globalId = identifier;
            executedOps++;
            event.getMessage().pushObject(c);
            event.go();
        } catch (Exception e) {
            e.printStackTrace();
        }
//    }
    }


    private void handleExitSim(SampleSendableEvent event) {
        if (blocked) {
            System.out
                    .println("The group is blocked, therefore message can not be sent.");
            return;
        }
        exitReceived++;
        if (exitReceived == numberOfNodes) {
            endSim = new Date().getTime();
            calculateAverageResponseTime();
            calculateThroughput();
        }

    }

    /**
     * @param event
     */
    private void handleStartPFD(SampleSendableEvent event) {
        try {
            PFDStartEvent pfdStart = new PFDStartEvent(channel, Direction.DOWN, this);
            pfdStart.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }
    }

    private void handleConsensus(SampleSendableEvent event) {
        String s = event.getMessage().popString();
        try {
            ConsensusPropose ev = new ConsensusPropose(event.getChannel(),
                    Direction.DOWN, this);
            ev.value = new StringProposal(s);
            ev.go();
        } catch (AppiaEventException ex) {
            ex.printStackTrace();
        }
    }

    private void handleAtomic(SampleSendableEvent event) {
        String s = event.getMessage().popString();
        try {
            NBACPropose ev = new NBACPropose(event.getChannel(), Direction.DOWN, this);
            ev.value = (Integer.parseInt(s.trim()) == 0 ? 0 : 1);
            ev.go();
        } catch (AppiaEventException ex) {
            ex.printStackTrace();
        }
    }

    private void handleRead(SampleSendableEvent event) {
        String s = event.getMessage().popString();
        try {
            SharedRead ev = new SharedRead(event.getChannel(), Direction.DOWN, this);
            ev.reg = Integer.parseInt(s.trim());
            ev.go();
        } catch (AppiaEventException ex) {
            ex.printStackTrace();
        }
    }

    private void handleWrite(SampleSendableEvent event) {
        String s = event.getMessage().popString();
        StringTokenizer st = new StringTokenizer(s);
        String sreg = st.nextToken();
        s = "";
        while (st.hasMoreTokens())
            s += st.nextToken();

        try {
            SharedWrite ev = new SharedWrite(event.getChannel(), Direction.DOWN, this);
            ev.reg = Integer.parseInt(sreg.trim());
            ev.value = s;
            ev.go();
        } catch (AppiaEventException ex) {
            ex.printStackTrace();
        }
    }

    private void handleDelay(SampleSendableEvent event) {
        String s = event.getMessage().popString();
        StringTokenizer st = new StringTokenizer(s);
        String sprocess = st.nextToken();
        String stics = st.nextToken();

        try {
            DelayEvent ev = new DelayEvent(event.getChannel(), Direction.DOWN, this);
            ev.processDelayed = Integer.parseInt(sprocess);
            ev.ticsDelayed = Integer.parseInt(stics);
            ev.go();
        } catch (AppiaEventException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     */
    private void printHelp() {
        System.out
                .println("Available commands:\n"
                        + "startpfd - starts the Perfect Failure detector (when it applies)\n"
                        + "bcast <msg> - Broadcast the message \"msg\"\n"
                        + "consensus <string> - Initiates a consensus decision with the given value\n"
                        + "atomic <value> - Initiates an atomic commit with the given value (0 or 1)\n"
                        + "read <register> - Reads the shared memory register with the given id (integer)\n"
                        + "write <register> <value> - Writes the value in the shared memory register with the given id (integer)\n"
                        + "delay <process_number> <tics> - Delays all messages received from the given process number by a number of tics, usually seconds\n"
                        + "help - Print this help information.");
    }

}
