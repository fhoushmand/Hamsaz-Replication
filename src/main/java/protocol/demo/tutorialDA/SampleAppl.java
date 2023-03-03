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

package main.java.protocol.demo.tutorialDA;

import main.java.analyser.Analyzer;
import main.java.analyser.ObjectAST;
import main.java.protocol.protocols.tutorialDA.allAckURB.AllAckURBLayer;
import main.java.protocol.protocols.tutorialDA.basicBroadcast.BasicBroadcastLayer;
import main.java.protocol.protocols.tutorialDA.basicBroadcast.BasicBroadcastSession;
import main.java.protocol.protocols.tutorialDA.consensusMembership.ConsensusMembershipLayer;
import main.java.protocol.protocols.tutorialDA.consensusNBAC.ConsensusNBACLayer;
import main.java.protocol.protocols.tutorialDA.consensusTRB.ConsensusTRBLayer;
import main.java.protocol.protocols.tutorialDA.consensusUTO.*;
import main.java.protocol.protocols.tutorialDA.coordinationProtocols.BlockingProtocolLayer;
import main.java.protocol.protocols.tutorialDA.coordinationProtocols.BlockingProtocolSession;
import main.java.protocol.protocols.tutorialDA.coordinationProtocols.NonBlockingProtocolLayer;
import main.java.protocol.protocols.tutorialDA.coordinationProtocols.NonBlockingProtocolSession;
import main.java.protocol.protocols.tutorialDA.delay.DelayLayer;
import main.java.protocol.protocols.tutorialDA.eagerPB.EagerPBLayer;
import main.java.protocol.protocols.tutorialDA.floodingConsensus.FloodingConsensusLayer;
import main.java.protocol.protocols.tutorialDA.gcPastCO.GCPastCOLayer;
import main.java.protocol.protocols.tutorialDA.hierarchicalConsensus.HierarchicalConsensusLayer;
import main.java.protocol.protocols.tutorialDA.lazyRB.LazyRBLayer;
import main.java.protocol.protocols.tutorialDA.lazyRB.LazyRBSession;
import main.java.protocol.protocols.tutorialDA.majorityAckURB.MajorityAckURBLayer;
import main.java.protocol.protocols.tutorialDA.noWaitingCO.NoWaitingCOLayer;
import main.java.protocol.protocols.tutorialDA.readImposeWriteAll1NAR.ReadImposeWriteAll1NARLayer;
import main.java.protocol.protocols.tutorialDA.readImposeWriteAll1NAR.ReadImposeWriteAll1NARSession;
import main.java.protocol.protocols.tutorialDA.readImposeWriteConsultNNAR.ReadImposeWriteConsultNNARLayer;
import main.java.protocol.protocols.tutorialDA.readImposeWriteConsultNNAR.ReadImposeWriteConsultNNARSession;
import main.java.protocol.protocols.tutorialDA.readOneWriteAll1NRR.ReadOneWriteAll1NRRLayer;
import main.java.protocol.protocols.tutorialDA.readOneWriteAll1NRR.ReadOneWriteAll1NRRSession;
import main.java.protocol.protocols.tutorialDA.sampleAppl.SampleApplLayer;
import main.java.protocol.protocols.tutorialDA.sampleAppl.SampleApplSession;
import main.java.protocol.protocols.tutorialDA.tcpBasedPFD.TcpBasedPFDLayer;
import main.java.protocol.protocols.tutorialDA.tcpBasedPFD.TcpBasedPFDSession;
import main.java.protocol.protocols.tutorialDA.trbViewSync.TRBViewSyncLayer;
import main.java.protocol.protocols.tutorialDA.uniformFloodingConsensus.UniformFloodingConsensusLayer;
import main.java.protocol.protocols.tutorialDA.uniformHierarchicalConsensus.UniformHierarchicalConsensusLayer;
import main.java.protocol.protocols.tutorialDA.uniformHierarchicalConsensus.UniformHierarchicalConsensusSession;
import main.java.protocol.protocols.tutorialDA.utils.ProcessSet;
import main.java.protocol.protocols.tutorialDA.utils.SampleProcess;
import main.java.protocol.protocols.tutorialDA.waitingCO.WaitingCOLayer;
import net.sf.appia.core.*;
import net.sf.appia.protocols.tcpcomplete.TcpCompleteLayer;
import net.sf.appia.protocols.tcpcomplete.TcpCompleteSession;
import net.sf.appia.protocols.udpsimple.UdpSimpleLayer;
import robject.Clique;
import main.java.robject.ReplicatedObject;
import main.java.robject.usecase.BankAccountObj;
import main.java.robject.usecase.CoursewareObj;
import main.java.robject.usecase.TwoPhaseSetObj;

import java.io.*;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * This class is the MAIN class to run the Reliable Broadcast protocols.
 * 
 * @author nuno
 */
public class SampleAppl {

  /**
   * Builds the Process set, using the information in the specified file.
   * 
   * @param filename
   *          the location of the file
   * @param selfProc
   *          the number of the self process
   * @return a new ProcessSet
   */
  private static ProcessSet buildProcessSet(String filename, int selfProc) {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(
          filename)));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(0);
    }
    String line;
    StringTokenizer st;
    boolean hasMoreLines = true;
    ProcessSet set = new ProcessSet();
    // reads lines of type: <process number> <IP address> <port>
    while(hasMoreLines) {
      try {
        line = reader.readLine();
        if (line == null)
          break;
        st = new StringTokenizer(line);
        if (st.countTokens() != 3) {
          System.err.println("Wrong line in file: "+st.countTokens());
          continue;
        }
        int procNumber = Integer.parseInt(st.nextToken());
        InetAddress addr = InetAddress.getByName(st.nextToken());
        int portNumber = Integer.parseInt(st.nextToken());
        boolean self = (procNumber == selfProc);
        SampleProcess process = new SampleProcess(new InetSocketAddress(addr,
            portNumber), procNumber, self);
        set.addProcess(process, procNumber);
      } catch (IOException e) {
        hasMoreLines = false;
      } catch (NumberFormatException e) {
        System.err.println(e.getMessage());
      }
    } // end of while
    return set;
  }

  /**
   * Builds an Appia channel with the specified QoS
   * 
   * @param set
   *          the ProcessSet
   * @param qos
   *          the specified QoS
   * @return a new uninitialized channel
   */
  private static Channel getChannel(ProcessSet set, String qos) {
    if (qos.equals("beb"))
      return getBebChannel(set);
    else if (qos.equals("rb"))
      return getRbChannel(set);
    else if (qos.equals("urb"))
      return getURbChannel(set);
    else if (qos.equals("iurb"))
      return getIURbChannel(set);
    else if (qos.equals("fc"))
      return getFCChannel(set);
    else if (qos.equals("hc"))
      return getHCChannel(set);
    else if (qos.equals("ufc"))
      return getUFCChannel(set);
    else if (qos.equals("uhc"))
      return getUHCChannel(set);
    else if (qos.equals("conow"))
      return getCOnoWChannel(set);
    else if (qos.equals("conowgc"))
      return getCOnoWGCChannel(set);
    else if (qos.equals("cow"))
      return getCOWChannel(set);
    else if (qos.equals("uto"))
      return getUnTOChannel(set);
    else if (qos.equals("nbac"))
      return getNBACChannel(set);
    else if (qos.equals("cmem"))
      return getCMemChannel(set);
    else if (qos.equals("trbvs"))
      return getTrbVSChannel(set);
    else if (qos.equals("r1nr"))
      return getR1NRChannel(set);
    else if (qos.equals("a1nr"))
      return getA1NRChannel(set);
    else if (qos.equals("annr"))
      return getANNRChannel(set);
    else if (qos.equals("non-block"))
      return getNonBlockingChannel(set);
    else if (qos.equals("block"))
      return getBlockingChannel(set);
    else if (qos.equals("rsm"))
      return getRSMChannel(set);
    else if (qos.equals("eventual"))
      return getEventualChannel(set);
    else {
      StringTokenizer st = new StringTokenizer(qos);
      if (st.countTokens() != 3)
        invalidArgs("Unexpected number of tokens when creating the channel...");
      if (!st.nextToken().equals("pb"))
        invalidArgs("");
      int fanout = 0;
      int rounds = 0;
      try {
        fanout = Integer.parseInt(st.nextToken());
        rounds = Integer.parseInt(st.nextToken());
      } catch (NumberFormatException e) {
        invalidArgs(e.getMessage());
      }
      return getPBChannel(set, fanout, rounds);
    }
  }

 private static Channel getRSMChannel(ProcessSet processes) {

    /* Create layers and put them on a array */
   SampleApplLayer applLayer = new SampleApplLayer();
   applLayer.nodeNumber = numberOfNodes;
   applLayer.opsPerNod = numberOfOps;
   applLayer.callsPath = callsFilePath;
   applLayer.obj = obj;
   applLayer.delay = delay;

    Layer[] qos = {
            new TcpCompleteLayer(), new BasicBroadcastLayer(),
            new TcpBasedPFDLayer(), new AllAckURBLayer(),
            new UniformFloodingConsensusLayer(),
            new DelayLayer(),
            new OldUTOLayer(),new ReplicatedStateMachineLayer(obj), applLayer};

            //new TcpCompleteLayer(), new BasicBroadcastLayer(), new TcpBasedPFDLayer(), new AllAckURBLayer(),
            //new UniformFloodingConsensusLayer(), new DelayLayer(), new BasicBroadcastLayer(),
            //new ReplicatedStateMachineLayer(new BankAccountObj()), new SampleApplLayer()};

    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("RSM QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      System.err.println("Invalid QoS");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS.createUnboundChannel("RSM Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) applLayer.createSession2();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }


  private static Channel getEventualChannel(ProcessSet processes) {

    /* Create layers and put them on a array */
    SampleApplLayer applLayer = new SampleApplLayer();
    applLayer.nodeNumber = numberOfNodes;
    applLayer.opsPerNod = numberOfOps;
    applLayer.callsPath = callsFilePath;
    applLayer.obj = obj;
    applLayer.delay = delay;

    Layer[] qos = {
            new TcpCompleteLayer(), new BasicBroadcastLayer(),
            new EventualLayer(obj), applLayer};
    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("Eventual QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      System.err.println("Invalid QoS");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS.createUnboundChannel("Eventual Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) applLayer.createSession2();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }



  /**
   * Builds a new Channel with Probabilistic Broadcast.
   * 
   * @param processes
   *          set of processes
   * @param fanout
   *          fanout to use in the protocol
   * @param rounds
   *          number of rounds to use in the protocol
   * @return a new uninitialized Channel
   */
  private static Channel getPBChannel(ProcessSet processes, int fanout,
      int rounds) {
    /* Creates a new PBLayer and initializes it */
    EagerPBLayer pbLayer = new EagerPBLayer();
    pbLayer.initValues(fanout, rounds);
    /* Create layers and put them on a array */
    Layer[] qos = {new UdpSimpleLayer(), pbLayer, new SampleApplLayer()};

    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("Probabilistic Broadcast QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      System.err.println("Invalid QoS");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS
        .createUnboundChannel("Probabilistic Broadcast Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) qos[qos.length - 1]
        .createSession();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }

  /**
   * Builds a new Appia Channel with Best Effort Broadcast
   * 
   * @param processes
   *          set of processes
   * @return a new uninitialized Channel
   */
  private static Channel getBebChannel(ProcessSet processes) {
    /* Create layers and put them on a array */
    Layer[] qos = {new TcpCompleteLayer(), new BasicBroadcastLayer(),
        new SampleApplLayer()};

    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("Best Effort Broadcast QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      System.err.println("Invalid QoS");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS
        .createUnboundChannel("Best effort Broadcast Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) qos[qos.length - 1]
        .createSession();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }

  /**
   * Builds a new Appia Channel with Reliable Broadcast
   * 
   * @param processes
   *          set of processes
   * @return a new uninitialized Channel
   */
  public static Channel getRbChannel(ProcessSet processes) {
    /* Create layers and put them on a array */
    Layer[] qos = {new TcpCompleteLayer(), new BasicBroadcastLayer(),
        new TcpBasedPFDLayer(), new LazyRBLayer(), new SampleApplLayer()};

    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("Reliable Broadcast QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      System.err.println("Invalid QoS");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS.createUnboundChannel("Reliable Broadcast Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) qos[qos.length - 1]
        .createSession();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }

  /**
   * Builds a new Appia Channel with Uniform Reliable Broadcast
   * 
   * @param processes
   *          set of processes
   * @return a new uninitialized Channel
   */
  private static Channel getURbChannel(ProcessSet processes) {
    /* Create layers and put them on a array */
    Layer[] qos = {new TcpCompleteLayer(), new BasicBroadcastLayer(),
        new TcpBasedPFDLayer(), new AllAckURBLayer(), new SampleApplLayer()};

    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("Uniform Reliable Broadcast QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      System.err.println("Invalid QoS");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS
        .createUnboundChannel("Uniform Reliable Broadcast Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) qos[qos.length - 1]
        .createSession();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }

  /**
   * Builds a new Appia Channel with Indulgent Uniform Reliable Broadcast
   * 
   * @param processes
   *          set of processes
   * @return a new uninitialized Channel
   */
  private static Channel getIURbChannel(ProcessSet processes) {
    /* Create layers and put them on a array */
    Layer[] qos = {new TcpCompleteLayer(), new BasicBroadcastLayer(),
        new MajorityAckURBLayer(), new SampleApplLayer()};

    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("Indulgent Uniform Reliable Broadcast QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      System.err.println("Invalid QoS");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS
        .createUnboundChannel("Indulgent Uniform Reliable Broadcast Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) qos[qos.length - 1]
        .createSession();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }

  /**
   * Builds a new Appia Channel with Flooding Consensus
   * 
   * @param processes
   *          set of processes
   * @return a new uninitialized Channel
   */
  private static Channel getFCChannel(ProcessSet processes) {
    /* Create layers and put them on a array */
    Layer[] qos = {new TcpCompleteLayer(), new BasicBroadcastLayer(),
        new TcpBasedPFDLayer(), new FloodingConsensusLayer(),
        new SampleApplLayer()};

    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("Flooding Consensus QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      System.err.println("Invalid QoS");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS.createUnboundChannel("Flooding Consensus Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) qos[qos.length - 1]
        .createSession();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }

  /**
   * Builds a new Appia Channel with Hierarchical Consensus
   * 
   * @param processes
   *          set of processes
   * @return a new uninitialized Channel
   */
  private static Channel getHCChannel(ProcessSet processes) {
    /* Create layers and put them on a array */
    Layer[] qos = {new TcpCompleteLayer(), new BasicBroadcastLayer(),
        new TcpBasedPFDLayer(), new HierarchicalConsensusLayer(),
        new SampleApplLayer()};

    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("Hierarchical Consensus QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      System.err.println("Invalid QoS");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS
        .createUnboundChannel("Hierarchical Consensus Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) qos[qos.length - 1]
        .createSession();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }

  /**
   * Builds a new Appia Channel with Uniform Flooding Consensus
   * 
   * @param processes
   *          set of processes
   * @return a new uninitialized Channel
   */
  private static Channel getUFCChannel(ProcessSet processes) {
    /* Create layers and put them on a array */
    Layer[] qos = {new TcpCompleteLayer(), new BasicBroadcastLayer(),
        new TcpBasedPFDLayer(), new UniformFloodingConsensusLayer(),
        new SampleApplLayer()};

    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("Uniform Flooding Consensus QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      System.err.println("Invalid QoS");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS
        .createUnboundChannel("Uniform Flooding Consensus Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) qos[qos.length - 1]
        .createSession();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }

  /**
   * Builds two Appia channels for Uniform Hierarchical Consensus - A BeB
   * channel - A ReliableBroadcast channel that is started from the
   * UniformHierarchicalConsensusSession
   * 
   * @param processes
   *          set of processes
   * @return a new uninitialized Channel
   */
  private static Channel getUHCChannel(ProcessSet processes) {
    TcpCompleteLayer tcplayer = new TcpCompleteLayer();
    BasicBroadcastLayer beblayer = new BasicBroadcastLayer();
    TcpBasedPFDLayer pfdlayer = new TcpBasedPFDLayer();
    LazyRBLayer rblayer = new LazyRBLayer();
    UniformHierarchicalConsensusLayer uhclayer = new UniformHierarchicalConsensusLayer();
    SampleApplLayer salayer = new SampleApplLayer();

    /* Create layers and put them on a array */
    Layer[] bebqos = {tcplayer, beblayer, pfdlayer, uhclayer, salayer};
    /* Create a QoS */
    QoS bebQoS = null;
    try {
      bebQoS = new QoS("UHC-BeB QoS", bebqos);
    } catch (AppiaInvalidQoSException ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel bebchannel = bebQoS.createUnboundChannel("UHC-BeB Channel");

    /* Create layers and put them on a array */
    Layer[] rbqos = {tcplayer, beblayer, pfdlayer, rblayer, uhclayer};
    /* Create a QoS */
    QoS rbQoS = null;
    try {
      rbQoS = new QoS("UHC-RB QoS", rbqos);
    } catch (AppiaInvalidQoSException ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel rbchannel = rbQoS.createUnboundChannel("UHC-RB Channel");

    // All sessions are created explicitly so they can be shared
    TcpCompleteSession tcpsession = (TcpCompleteSession) tcplayer
        .createSession();
    BasicBroadcastSession bebsession = (BasicBroadcastSession) beblayer
        .createSession();
    TcpBasedPFDSession pfdsession = (TcpBasedPFDSession) pfdlayer
        .createSession();
    LazyRBSession rbsession = (LazyRBSession) rblayer.createSession();
    UniformHierarchicalConsensusSession uhcsession = (UniformHierarchicalConsensusSession) uhclayer
        .createSession();
    SampleApplSession sasession = (SampleApplSession) salayer.createSession();

    // Sessions that require initial configuration
    sasession.init(processes);
    uhcsession.rbchannel(rbchannel);

    // Setting sessions
    ChannelCursor bebcc = bebchannel.getCursor();
    ChannelCursor rbcc = rbchannel.getCursor();
    try {
      bebcc.bottom();
      bebcc.setSession(tcpsession);
      bebcc.up();
      bebcc.setSession(bebsession);
      bebcc.up();
      bebcc.setSession(pfdsession);
      bebcc.up();
      bebcc.setSession(uhcsession);
      bebcc.up();
      bebcc.setSession(sasession);

      rbcc.bottom();
      rbcc.setSession(tcpsession);
      rbcc.up();
      rbcc.setSession(bebsession);
      rbcc.up();
      rbcc.setSession(pfdsession);
      rbcc.up();
      rbcc.setSession(rbsession);
      rbcc.up();
      rbcc.setSession(uhcsession);
    } catch (AppiaCursorException ex) {
      ex.printStackTrace();
      System.exit(1);
    }

    return bebchannel;
  }

  /**
   * Creates a Causal Order No waiting Reliable Broadcast Channel
   * 
   * @param processes,
   *          set of processes belonging to the group
   * @return the created channel
   */
  private static Channel getCOnoWChannel(ProcessSet processes) {
    /* Create layers and put them on a array */
    Layer[] qos = {new TcpCompleteLayer(), new BasicBroadcastLayer(),
        new TcpBasedPFDLayer(),
        // new DelayLayer(),
        new LazyRBLayer(), new NoWaitingCOLayer(), new SampleApplLayer()};

    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("Casual Order no Waiting QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      System.err.println("Invalid QoS");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS
        .createUnboundChannel("Casual Order no Waiting Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) qos[qos.length - 1]
        .createSession();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }

  /**
   * Creates a Causal Order No waiting with GC Reliable Broadcast Channel
   * 
   * @param processes,
   *          set of processes belonging to the group
   * @return the new channel
   */
  private static Channel getCOnoWGCChannel(ProcessSet processes) {
    /* Create layers and put them on a array */
    Layer[] qos = {new TcpCompleteLayer(), new BasicBroadcastLayer(),
        new TcpBasedPFDLayer(), new LazyRBLayer(),
        // new DelayLayer(),
        new GCPastCOLayer(), new SampleApplLayer()};

    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("Casual Order no Waiting with GC QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      System.err.println("Invalid QoS");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS
        .createUnboundChannel("Casual Order no Waiting with GC Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) qos[qos.length - 1]
        .createSession();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }

  /**
   * Creates a Causal Order Waiting Reliable Broadcast Channel
   * 
   * @param processes,
   *          set of processes belonging to the group
   * @return the new channel
   */
  private static Channel getCOWChannel(ProcessSet processes) {
    /* Create layers and put them on a array */
    Layer[] qos = {new TcpCompleteLayer(), new BasicBroadcastLayer(),
        new TcpBasedPFDLayer(), new LazyRBLayer(),
        // new DelayLayer(),
        new WaitingCOLayer(), new SampleApplLayer()};

    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("Casual Order Waiting QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      System.err.println("Invalid QoS");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS
        .createUnboundChannel("Casual Order Waiting Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) qos[qos.length - 1]
        .createSession();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }

  /**
   * Creates a Uniform Total Order channel
   * 
   * @param processes the process set
   * @return the new channel
   */
  private static Channel getUnTOChannel(ProcessSet processes) {
    /* Create layers and put them on a array */
    Layer[] qos = {new TcpCompleteLayer(), new BasicBroadcastLayer(),
        new TcpBasedPFDLayer(), new AllAckURBLayer(),
        new UniformFloodingConsensusLayer(),
//         new DelayLayer(),
        new OldUTOLayer(), new SampleApplLayer()};

    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("Uniform Total Order QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      System.err.println("Invalid QoS");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS.createUnboundChannel("Uniform Total Order Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) qos[qos.length - 1]
        .createSession();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }


  /**
   * Builds a new Appia Channel with Consensus-based Non-Blocking Atomic Commit
   * 
   * @param processes
   *          set of processes
   * @return a new uninitialized Channel
   */
  private static Channel getNBACChannel(ProcessSet processes) {
    /* Create layers and put them on a array */
    Layer[] qos = {new TcpCompleteLayer(), new BasicBroadcastLayer(),
        new TcpBasedPFDLayer(), new UniformFloodingConsensusLayer(),
        new ConsensusNBACLayer(), new SampleApplLayer()};

    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("Consensus-based NBAC QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS
        .createUnboundChannel("Consensus-based NBAC Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) qos[qos.length - 1]
        .createSession();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }

  /**
   * Builds a new Appia Channel with Uniform Flooding Consensus
   * 
   * @param processes
   *          set of processes
   * @return a new uninitialized Channel
   */
  private static Channel getCMemChannel(ProcessSet processes) {
    /* Create layers and put them on a array */
    Layer[] qos = {new TcpCompleteLayer(), new BasicBroadcastLayer(),
        new TcpBasedPFDLayer(), new UniformFloodingConsensusLayer(),
        new ConsensusMembershipLayer(), new SampleApplLayer()};

    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("Consensus-based Membership QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS
        .createUnboundChannel("Consensus-based Membership Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) qos[qos.length - 1]
        .createSession();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }

  /**
   * Builds a new Appia Channel with Uniform Flooding Consensus
   * 
   * @param processes
   *          set of processes
   * @return a new uninitialized Channel
   */
  private static Channel getTrbVSChannel(ProcessSet processes) {
    /* Create layers and put them on a array */
    Layer[] qos = {new TcpCompleteLayer(), new BasicBroadcastLayer(),
        new TcpBasedPFDLayer(), new UniformFloodingConsensusLayer(),
        new ConsensusTRBLayer(), new LazyRBLayer(),
        // new DelayLayer(),
        new GCPastCOLayer(), new ConsensusMembershipLayer(),
        new TRBViewSyncLayer(), new SampleApplLayer()};

    /* Create a QoS */
    QoS myQoS = null;
    try {
      myQoS = new QoS("TRB-based View Synchrony QoS", qos);
    } catch (AppiaInvalidQoSException ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel channel = myQoS
        .createUnboundChannel("TRB-based View Synchrony Channel");
    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */
    SampleApplSession sas = (SampleApplSession) qos[qos.length - 1]
        .createSession();
    sas.init(processes);
    ChannelCursor cc = channel.getCursor();
    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      cc.top();
      cc.setSession(sas);
    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return channel;
  }

  /**
   * Builds two Appia channels for Regular (1,N) Register - A BeB channel - A
   * PerfectPointoToPointLinks channel that is started from the
   * AbortableConsensusSession
   * 
   * @param processes
   *          set of processes
   * @return a new uninitialized Channel
   */
  private static Channel getR1NRChannel(ProcessSet processes) {
    /* Create layers and put them on a array */
    Layer[] bebqos = {new TcpCompleteLayer(), new BasicBroadcastLayer(),
        new TcpBasedPFDLayer(), new ReadOneWriteAll1NRRLayer(),
        new SampleApplLayer()};

    /* Create a QoS */
    QoS bebQoS = null;
    try {
      bebQoS = new QoS("R1NR-BeB QoS", bebqos);
    } catch (AppiaInvalidQoSException ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel bebchannel = bebQoS.createUnboundChannel("R1NR-BeB Channel");

    /* Create layers and put them on a array */
    Layer[] pp2pqos = {bebqos[0], bebqos[bebqos.length - 2]};
    /* Create a QoS */
    QoS pp2pQoS = null;
    try {
      pp2pQoS = new QoS("R1NR-PP2P QoS", pp2pqos);
    } catch (AppiaInvalidQoSException ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    /* Create a channel. */
    Channel pp2pchannel = pp2pQoS.createUnboundChannel("R1NR-PP2P Channel",
        bebchannel.getEventScheduler());

    // Shared sessions and those that require initial configuration are created
    // explicitly.
    TcpCompleteSession tcpsession = (TcpCompleteSession) bebqos[0]
        .createSession();
    ReadOneWriteAll1NRRSession r1nrsession = (ReadOneWriteAll1NRRSession) bebqos[bebqos.length - 2]
        .createSession();
    SampleApplSession sasession = (SampleApplSession) bebqos[bebqos.length - 1]
        .createSession();

    sasession.init(processes);
    r1nrsession.pp2pchannel(pp2pchannel);

    // Setting sessions
    ChannelCursor bebcc = bebchannel.getCursor();
    ChannelCursor pp2pcc = pp2pchannel.getCursor();
    try {
      bebcc.top();
      bebcc.setSession(sasession);
      bebcc.down();
      bebcc.setSession(r1nrsession);
      bebcc.bottom();
      bebcc.setSession(tcpsession);

      pp2pcc.top();
      pp2pcc.setSession(r1nrsession);
      pp2pcc.down();
      pp2pcc.setSession(tcpsession);
    } catch (AppiaCursorException ex) {
      ex.printStackTrace();
      System.exit(1);
    }

    return bebchannel;
  }


  private static Channel getNonBlockingChannel(ProcessSet processes) {

    HashMap<Clique, Channel> mtobChannels = new HashMap<>();
    HashMap<Integer, Channel> invAtomicChannels = new HashMap<>();


    TcpCompleteLayer tcpCompleteLayer = new TcpCompleteLayer();
    BasicBroadcastLayer basicBroadcastLayer = new BasicBroadcastLayer();
    TcpBasedPFDLayer tcpBasedPFDLayer = new TcpBasedPFDLayer();
    LazyRBLayer lazyRBLayer = new LazyRBLayer();
    AllAckURBLayer allAckURBLayer = new AllAckURBLayer();
    UniformFloodingConsensusLayer uniformFloodingConsensusLayer = new UniformFloodingConsensusLayer();
    ConsensusUTOLayer consensusUTOLayer = new ConsensusUTOLayer();
    NonBlockingProtocolLayer nonBlockingProtocolLayer = new NonBlockingProtocolLayer(obj, analyzer);
    ConsensusNBACLayer consensusNBACLayer = new ConsensusNBACLayer();
    SampleApplLayer sampleApplLayer = new SampleApplLayer();
    sampleApplLayer.nodeNumber = numberOfNodes;
    sampleApplLayer.opsPerNod = numberOfOps;
    sampleApplLayer.callsPath = callsFilePath;
    sampleApplLayer.obj = obj;
    sampleApplLayer.delay = delay;


    Layer[] nbRBQos = {tcpCompleteLayer, basicBroadcastLayer, tcpBasedPFDLayer, lazyRBLayer, new DelayLayer() ,nonBlockingProtocolLayer,sampleApplLayer};

    /* Create a QoS */
    QoS rbQos = null;
    try {
      rbQos = new QoS("NonBlocking RB Protocol QoS", nbRBQos);
    } catch (AppiaInvalidQoSException ex) {
      System.err.println("Invalid QoS");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */

    Channel mainChannel = rbQos.createUnboundChannel("RB");

    Layer[] atomicLayers = {tcpCompleteLayer, basicBroadcastLayer, tcpBasedPFDLayer, uniformFloodingConsensusLayer, consensusNBACLayer, nonBlockingProtocolLayer};

    for (int i = 0; i < processes.getSize(); i++)
    {
      QoS atomicQos = null;
      try {
        atomicQos = new QoS("Atomic commit Protocol QoS", atomicLayers);
      } catch (AppiaInvalidQoSException ex) {
        System.err.println("Invalid QoS");
        System.err.println(ex.getMessage());
        System.exit(1);
      }
      /* Create a channel. Uses default event scheduler. */
      Channel atomicChannel = atomicQos.createUnboundChannel("Atomic"+i, mainChannel.getEventScheduler());
      invAtomicChannels.put(i, atomicChannel);
    }

    Layer[] nbMTOBQos = {tcpCompleteLayer, basicBroadcastLayer, tcpBasedPFDLayer, allAckURBLayer, uniformFloodingConsensusLayer, consensusUTOLayer,  nonBlockingProtocolLayer};

    int i = 0;
    for (Clique cl : analyzer.getAllCliques()) {
      /* Create a QoS */
      QoS mtobQos = null;
      try {
        mtobQos = new QoS(String.format("NonBlocking MTOB Protocol QoS%s", i), nbMTOBQos);
      } catch (AppiaInvalidQoSException ex) {
        ex.printStackTrace();
        System.err.println("Invalid QoS");
        System.err.println(ex.getMessage());
        System.exit(1);
      }
      /* Create a channel. Uses default event scheduler. */
      Channel mtobChannel = mtobQos.createUnboundChannel(cl.name, mainChannel.getEventScheduler());
      mtobChannels.put(cl, mtobChannel);
    }




    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */

    TcpCompleteSession tcpCompleteSession = (TcpCompleteSession) tcpCompleteLayer.createSession();
//    BasicBroadcastSession basicBroadcastSession = (BasicBroadcastSession) basicBroadcastLayer.createSession();
    TcpBasedPFDSession tcpBasedPFDSession = (TcpBasedPFDSession) tcpBasedPFDLayer.createSession();

    NonBlockingProtocolSession nonBlockingProtocolSession = (NonBlockingProtocolSession) nonBlockingProtocolLayer.createSession();
    SampleApplSession sas = (SampleApplSession) sampleApplLayer.createSession2();


    sas.init(processes);
    ChannelCursor mainChannelCursor = mainChannel.getCursor();
//    ChannelCursor atomicChannelCursor = atomicChannel.getCursor();


    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      mainChannelCursor.top(); //application
      mainChannelCursor.setSession(sas);
      mainChannelCursor.down(); //non blocking
//      mainChannelCursor.down(); //non blocking
      mainChannelCursor.setSession(nonBlockingProtocolSession);
      mainChannelCursor.down(); //delay
      mainChannelCursor.down(); //lazy
      mainChannelCursor.down(); //pfd
      mainChannelCursor.setSession(tcpBasedPFDSession);
      mainChannelCursor.down(); //basic
//      mainChannelCursor.setSession(basicBroadcastSession);
      mainChannelCursor.down(); //tcp complete
      mainChannelCursor.setSession(tcpCompleteSession);
      ////////////////

      for(Channel invAtomicChannel : invAtomicChannels.values())
      {
        ChannelCursor atomicChannelCursor = invAtomicChannel.getCursor();
        atomicChannelCursor.top();
        atomicChannelCursor.setSession(nonBlockingProtocolSession);
        atomicChannelCursor.bottom();
        atomicChannelCursor.setSession(tcpCompleteSession);
      }
//      nonBlockingProtocolSession.invAtomicChannelInit = invAtomicChannels;


      ///////////////////
      for (Channel channel : mtobChannels.values())
      {
        ConsensusUTOSession consensusUTOSession = (ConsensusUTOSession) consensusUTOLayer.createSession();
        HashMap<String, Integer> map = new HashMap<>();
        for (Clique cl : analyzer.getAllCliques())
        {
          map.put(cl.name, 0);
        }
        consensusUTOSession.rankMap = map;
        ChannelCursor mtobChannelCursor = channel.getCursor();
        mtobChannelCursor.top();
        mtobChannelCursor.setSession(nonBlockingProtocolSession);
//        mtobChannelCursor.down();
        mtobChannelCursor.down();
        mtobChannelCursor.setSession(consensusUTOSession);
        mtobChannelCursor.down();
        mtobChannelCursor.down();
        mtobChannelCursor.down();
        mtobChannelCursor.setSession(tcpBasedPFDSession);
        mtobChannelCursor.bottom();
        mtobChannelCursor.setSession(tcpCompleteSession);

      }
      nonBlockingProtocolSession.setMtobChannels(mtobChannels);


    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return mainChannel;
  }





  private static Channel getBlockingChannel(ProcessSet processes) {
    HashMap<String, Channel> tobChannels = new HashMap<>();
    DelayLayer delayLayer = new DelayLayer();
    TcpCompleteLayer tcpCompleteLayer = new TcpCompleteLayer();
    TcpBasedPFDLayer tcpBasedPFDLayer = new TcpBasedPFDLayer();
    OldUTOLayer oldUTOLayer = new OldUTOLayer();
    BlockingProtocolLayer blockingProtocolLayer = new BlockingProtocolLayer(obj, analyzer);
    SampleApplLayer sampleApplLayer = new SampleApplLayer();
    sampleApplLayer.nodeNumber = numberOfNodes;
    sampleApplLayer.opsPerNod = numberOfOps;
    sampleApplLayer.callsPath = callsFilePath;
    sampleApplLayer.obj = obj;
    sampleApplLayer.delay = delay;


    Layer[] nbRBQos = {tcpCompleteLayer, new BasicBroadcastLayer(), tcpBasedPFDLayer, new LazyRBLayer(), delayLayer , blockingProtocolLayer, sampleApplLayer};

    /* Create a QoS */
    QoS rbQos = null;
    try {
      rbQos = new QoS("NonBlocking RB Protocol QoS", nbRBQos);
    } catch (AppiaInvalidQoSException ex) {
      System.err.println("Invalid QoS");
      System.err.println(ex.getMessage());
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */

    Channel mainChannel = rbQos.createUnboundChannel("RB");



    for(Method method : obj.getAllMethodsOfObject())
    {
        Layer[] tobQosLayers = {tcpCompleteLayer, new BasicBroadcastLayer(),
                tcpBasedPFDLayer, new AllAckURBLayer(),
                new UniformFloodingConsensusLayer(),
//                delayLayer,
                new OldUTOLayer(), blockingProtocolLayer};
        QoS tobQos = null;
        try {
           tobQos = new QoS("TOB Protocol QoS " + method.getName(), tobQosLayers);
        } catch (AppiaInvalidQoSException e) {
          e.printStackTrace();
        }
        Channel tobChannel = tobQos.createUnboundChannel("TOB_"+method.getName(), mainChannel.getEventScheduler());
        tobChannels.put(method.getName(), tobChannel);
    }


    /*
     * Application Session requires special arguments: filename and . A session
     * is created and binded to the stack. Remaining ones are created by default
     */

    TcpCompleteSession tcpCompleteSession = (TcpCompleteSession) tcpCompleteLayer.createSession();
//    BasicBroadcastSession basicBroadcastSession = (BasicBroadcastSession) basicBroadcastLayer.createSession();
    TcpBasedPFDSession tcpBasedPFDSession = (TcpBasedPFDSession) tcpBasedPFDLayer.createSession();

    BlockingProtocolSession blockingProtocolSession = (BlockingProtocolSession) blockingProtocolLayer.createSession();
    SampleApplSession sas = (SampleApplSession) sampleApplLayer.createSession2();


    sas.init(processes);
    ChannelCursor mainChannelCursor = mainChannel.getCursor();
//    ChannelCursor atomicChannelCursor = atomicChannel.getCursor();


    /*
     * Application is the last session of the array. Positioning in it is simple
     */
    try {
      mainChannelCursor.top(); //application
      mainChannelCursor.setSession(sas);
      mainChannelCursor.down(); //non blocking
//      mainChannelCursor.down(); //non blocking
      mainChannelCursor.setSession(blockingProtocolSession);
      mainChannelCursor.down(); //delay
      mainChannelCursor.down(); //lazy
      mainChannelCursor.down(); //pfd
      mainChannelCursor.setSession(tcpBasedPFDSession);
      mainChannelCursor.down(); //basic
//      mainChannelCursor.setSession(basicBroadcastSession);
      mainChannelCursor.down(); //tcp complete
      mainChannelCursor.setSession(tcpCompleteSession);
      ///////////////


      ///////////////////
      for (Channel channel : tobChannels.values())
      {
        OldUTOSession consensusUTOSession = (OldUTOSession) oldUTOLayer.createSession();

        ChannelCursor tobChannelCursor = channel.getCursor();
        tobChannelCursor.top();
        tobChannelCursor.setSession(blockingProtocolSession);
//        mtobChannelCursor.down();
        tobChannelCursor.down();
        tobChannelCursor.setSession(consensusUTOSession);
//        tobChannelCursor.down();
        tobChannelCursor.down();
        tobChannelCursor.down();
        tobChannelCursor.down();
        tobChannelCursor.setSession(tcpBasedPFDSession);
        tobChannelCursor.bottom();
        tobChannelCursor.setSession(tcpCompleteSession);

      }
      blockingProtocolSession.tobChannelInits = tobChannels;


    } catch (AppiaCursorException ex) {
      System.err.println("Unexpected exception in main. Type code:" + ex.type);
      System.exit(1);
    }
    return mainChannel;
  }


  /**
   * Builds two Appia channels for Atomic (1,N) Register - A BeB channel - A
   * PerfectPointoToPointLinks channel that is started from the
   * AbortableConsensusSession
   * 
   * @param processes
   *          set of processes
   * @return a new uninitialized Channel
   */
  private static Channel getA1NRChannel(ProcessSet processes) {
    /* Create layers and put them on a array */
    Layer[] bebqos = {new TcpCompleteLayer(), new BasicBroadcastLayer(),
        new TcpBasedPFDLayer(), new ReadImposeWriteAll1NARLayer(),
        new SampleApplLayer()};

    /* Create a QoS */
    QoS bebQoS = null;
    try {
      bebQoS = new QoS("A1NR-BeB QoS", bebqos);
    } catch (AppiaInvalidQoSException ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel bebchannel = bebQoS.createUnboundChannel("A1NR-BeB Channel");

    /* Create layers and put them on a array */
    Layer[] pp2pqos = {bebqos[0], bebqos[bebqos.length - 2]};
    /* Create a QoS */
    QoS pp2pQoS = null;
    try {
      pp2pQoS = new QoS("A1NR-PP2P QoS", pp2pqos);
    } catch (AppiaInvalidQoSException ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel pp2pchannel = pp2pQoS.createUnboundChannel("A1NR-PP2P Channel",
        bebchannel.getEventScheduler());

    // Shared sessions and those that require initial configuration are created
    // explicitly.
    TcpCompleteSession tcpsession = (TcpCompleteSession) bebqos[0]
        .createSession();
    ReadImposeWriteAll1NARSession a1nrsession = (ReadImposeWriteAll1NARSession) bebqos[bebqos.length - 2]
        .createSession();
    SampleApplSession sasession = (SampleApplSession) bebqos[bebqos.length - 1]
        .createSession();

    sasession.init(processes);
    a1nrsession.pp2pchannel(pp2pchannel);

    // Setting sessions
    ChannelCursor bebcc = bebchannel.getCursor();
    ChannelCursor pp2pcc = pp2pchannel.getCursor();
    try {
      bebcc.top();
      bebcc.setSession(sasession);
      bebcc.down();
      bebcc.setSession(a1nrsession);
      bebcc.bottom();
      bebcc.setSession(tcpsession);

      pp2pcc.top();
      pp2pcc.setSession(a1nrsession);
      pp2pcc.down();
      pp2pcc.setSession(tcpsession);
    } catch (AppiaCursorException ex) {
      ex.printStackTrace();
      System.exit(1);
    }

    return bebchannel;
  }

  /**
   * Builds two Appia channels for Atomic (N,N) Register - A BeB channel - A
   * PerfectPointoToPointLinks channel that is started from the
   * AbortableConsensusSession
   * 
   * @param processes
   *          set of processes
   * @return a new uninitialized Channel
   */
  private static Channel getANNRChannel(ProcessSet processes) {
    TcpCompleteLayer tcplayer = new TcpCompleteLayer();
    BasicBroadcastLayer beblayer = new BasicBroadcastLayer();
    TcpBasedPFDLayer pfdlayer = new TcpBasedPFDLayer();
    ReadImposeWriteConsultNNARLayer annrlayer = new ReadImposeWriteConsultNNARLayer();
    SampleApplLayer salayer = new SampleApplLayer();

    /* Create layers and put them on a array */
    Layer[] bebqos = {tcplayer, beblayer, pfdlayer, annrlayer, salayer};
    /* Create a QoS */
    QoS bebQoS = null;
    try {
      bebQoS = new QoS("ANNR-BeB QoS", bebqos);
    } catch (AppiaInvalidQoSException ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel bebchannel = bebQoS.createUnboundChannel("ANNR-BeB Channel");

    /* Create layers and put them on a array */
    Layer[] pp2pqos = {tcplayer, annrlayer};
    /* Create a QoS */
    QoS pp2pQoS = null;
    try {
      pp2pQoS = new QoS("ANNR-PP2P QoS", pp2pqos);
    } catch (AppiaInvalidQoSException ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    /* Create a channel. Uses default event scheduler. */
    Channel pp2pchannel = pp2pQoS.createUnboundChannel("ANNR-PP2P Channel");

    // Shared sessions and those that require initial configuration are created
    // explicitly.
    TcpCompleteSession tcpsession = (TcpCompleteSession) tcplayer
        .createSession();
    ReadImposeWriteConsultNNARSession annrsession = (ReadImposeWriteConsultNNARSession) annrlayer
        .createSession();
    SampleApplSession sasession = (SampleApplSession) salayer.createSession();

    sasession.init(processes);
    annrsession.pp2pchannel(pp2pchannel);

    // Setting sessions
    ChannelCursor bebcc = bebchannel.getCursor();
    ChannelCursor pp2pcc = pp2pchannel.getCursor();
    try {
      bebcc.bottom();
      bebcc.setSession(tcpsession);
      bebcc.up();
      bebcc.up();
      bebcc.up();
      bebcc.setSession(annrsession);
      bebcc.up();
      bebcc.setSession(sasession);

      pp2pcc.bottom();
      pp2pcc.setSession(tcpsession);
      pp2pcc.up();
      pp2pcc.setSession(annrsession);
    } catch (AppiaCursorException ex) {
      ex.printStackTrace();
      System.exit(1);
    }

    return bebchannel;
  }

  private static final int NUM_ARGS = 8;

  private static int numberOfOps;
  private static int numberOfNodes;
  private static String callsFilePath;
  private static long delay;
private static ReplicatedObject obj;
private static ObjectAST objTranslation;
  private static Analyzer analyzer;

  public static void main(String[] args) {
    if (args.length < (NUM_ARGS - 2)) {
      invalidArgs("Wrong number of arguments: "+args.length);
    }

    /* Parse arguments */
    int arg = 0, self = -1;
    String filename = null, qos = null;
    try {
      while (arg < args.length) {
        if (args[arg].equals("-f")) {
          arg++;
          filename = args[arg];
          System.out.println("Reading from file: " + filename);
        } else if (args[arg].equals("-n")) {
          arg++;
          try {
            self = Integer.parseInt(args[arg]);
            System.out.println("Process number: " + self);
          } catch (NumberFormatException e) {
            invalidArgs(e.getMessage());
          }
        } else if (args[arg].equals("-qos")) {
          arg++;
          qos = args[arg];
          if (qos.equals("pb")) {
            qos = qos + " " + args[++arg] + " " + args[++arg];
          }
          System.out.println("Starting with QoS: " + qos);
        } else if (args[arg].equals("-ops")) {
          arg++;
          numberOfOps = Integer.parseInt(args[arg]);
        }
        else if (args[arg].equals("-nodes")) {
          arg++;
          numberOfNodes = Integer.parseInt(args[arg]);
        }
        else if (args[arg].equals("-bench")) {
          arg++;
          callsFilePath = args[arg];
        }
        else if (args[arg].equals("-obj")) {
          arg++;
          switch (args[arg]){
            case "2pset" :
              obj = new TwoPhaseSetObj();
              objTranslation = obj.getASTFormat();
              break;
            case "bank" :
              obj = new BankAccountObj();
              objTranslation = obj.getASTFormat();

//              Class aClass = CompilerUtils.CACHED_COMPILER.loadFromJava(className, javaCode);
//              Runnable runner = (Runnable) aClass.newInstance();

              break;
            case "courseware" :
              obj = new CoursewareObj();
              objTranslation = obj.getASTFormat();
              break;
            default:
              invalidArgs("wrong object name " + args[arg]);
          }
        }
        else if (args[arg].equals("-delay")) {
          arg++;
          delay = Long.valueOf(args[arg]);
        }
        arg++;
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      e.printStackTrace();
      invalidArgs(e.getMessage());
    }

    System.out.println("analyzing the object...");
    analyzer = new Analyzer(objTranslation);
    System.out.println("#########Analysis Results##############");
    System.out.println("Conflict graph:");
    System.out.println(analyzer.getConflictMap());
    System.out.println("Maximal Cliques:");
    System.out.println(analyzer.getAllCliques());
    System.out.println("Minimum Cover:");
    System.out.println(analyzer.getCover());
    System.out.println("Dependency Graph");
    System.out.println(analyzer.getDependencyMap());

    /*
     * gets a new uninitialized Channel with the specified QoS and the Appl
     * session created. Remaining sessions are created by default. Just tell the
     * channel to start.
     */
    Channel channel = getChannel(buildProcessSet(filename, self), qos);
    try {
      channel.start();
    } catch (AppiaDuplicatedSessionsException ex) {
      System.err.println("Sessions binding strangely resulted in "
          + "one single sessions occurring more than " + "once in a channel");
      System.exit(1);
    }

    /* All set. Appia main class will handle the rest */
    System.out.println("Starting Appia...");



//    while (replicasJoined.get() != numberOfNodes - 1) {
//      try {
//        Thread.sleep(10);
//      } catch (InterruptedException e) {
//        throw new RuntimeException(e);
//      }
//    }
//    System.out.println("start sending calls");

    Appia.run();
  }

  /**
   * Prints a error message and exit.
   * @param reason the reason of the failure
   */
  private static void invalidArgs(String reason) {
    System.out
        .println("Invalid args: "+reason+"\nUsage SampleAppl -f filemane -n proc_number -qos QoS_type -obj object_name -delay msg_interval_ms"
            + "\n QoS can be one of the following:"
            + "\n\t block - Blocking Well-coordination"
            + "\n\t non-block - Non-Blocking Well-coordination"
            + "\n object_name can be one of the followings:"
            + "\n\t 2pset - Two-Phase Set"
            + "\n\t bank - Bank Account"
            + "\n\t courseware - Courseware");
    System.exit(1);
  }
}
