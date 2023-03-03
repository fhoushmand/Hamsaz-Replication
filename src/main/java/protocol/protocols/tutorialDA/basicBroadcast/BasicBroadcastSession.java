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

package main.java.protocol.protocols.tutorialDA.basicBroadcast;

import main.java.protocol.protocols.tutorialDA.coordinationProtocols.MTOBPayload;
import main.java.protocol.protocols.tutorialDA.events.ProcessInitEvent;
import main.java.protocol.protocols.tutorialDA.events.SampleSendableEvent;
import main.java.protocol.protocols.tutorialDA.uniformFloodingConsensus.MySetEvent;
import main.java.protocol.protocols.tutorialDA.utils.Debug;
import main.java.protocol.protocols.tutorialDA.utils.ProcessSet;
import main.java.protocol.protocols.tutorialDA.utils.SampleProcess;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.SendableEvent;
import net.sf.appia.core.events.channel.ChannelInit;


/**
 * Session implementing the Basic Broadcast protocol.
 * 
 * @author nuno
 * 
 */
public class BasicBroadcastSession extends Session {

  /*
   * State of the protocol: the set of processes in the group
   */
  private ProcessSet processes;

  /**
   * Builds a new BEBSession.
   * 
   * @param layer
   */
  public BasicBroadcastSession(Layer layer) {
    super(layer);
  }

//  public BasicBroadcastSession(Layer layer, ProcessSet p) {
//    super(layer);
//    processes = p;
//  }

  /**
   * Handles incoming events.
   *
   */
  public void handle(Event event) {
    // Init events. Channel Init is from Appia and ProcessInitEvent is to know
    // the elements of the group
    if (event instanceof ChannelInit)
      handleChannelInit((ChannelInit) event);
    else if (event instanceof ProcessInitEvent)
      handleProcessInitEvent((ProcessInitEvent) event);
    else if (event instanceof SendableEvent) {
      if (event.getDir() == Direction.DOWN)
        // UPON event from the above protocol (or application)
        bebBroadcast((SendableEvent) event);
      else
        // UPON event from the bottom protocol (or perfect point2point links)
        pp2pDeliver((SendableEvent) event);
    }
  }

  /**
   * Gets the process set and forwards the event to other layers.
   * 
   * @param event
   */
  private void handleProcessInitEvent(ProcessInitEvent event) {
    processes = event.getProcessSet();
    try {
      event.go();
    } catch (AppiaEventException e) {
      e.printStackTrace();
    }
  }

  /**
   * Handles the first event that arrives to the protocol session. In this case,
   * just forwards it.
   * 
   * @param init
   */
  private void handleChannelInit(ChannelInit init) {
    try {
      init.go();
    } catch (AppiaEventException e) {
      e.printStackTrace();
    }
  }

  /**
   * Broadcasts a message.
   * 
   * @param event
   */
  private void bebBroadcast(SendableEvent event) {
    Debug.print("BEB: broadcasting message.");
    // get an array of processes
    SampleProcess[] processArray = this.processes.getAllProcesses();
    SendableEvent sendingEvent = null;
    // for each process...
    for (int i = 0; i < processArray.length; i++) {
      try {

        // if it is the last process, don't clone the event
        if (i == (processArray.length - 1))
          sendingEvent = (SendableEvent) event;
        else
          sendingEvent = (SendableEvent) event.cloneEvent();
        // set source and destination of event message
        sendingEvent.source = processes.getSelfProcess().getSocketAddress();
        sendingEvent.dest = processArray[i].getSocketAddress();
        // sets the session that created the event.
        // this is important when this session is sending a cloned event
        sendingEvent.setSourceSession(this);
        // if it is the "self" process, send the event upwards
        if (i == processes.getSelfRank())
          sendingEvent.setDir(Direction.UP);
        // initializes and sends the message event
        try {
          if (i != processes.getSelfRank() && !event.getClass().getName().equals(MySetEvent.class.getName())) {
            sendingEvent.getMessage().pushObject(((SampleSendableEvent) event).getMtobPayload());
          }
        } catch (ClassCastException e) {
//          System.out.println("exception in broadcast beb");
        }

        sendingEvent.init();
        sendingEvent.go();
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
        return;
      } catch (AppiaEventException e) {
        e.printStackTrace();
        return;
      }
    }
  }

  /**
   * Delivers an incoming message.
   * 
   * @param event
   */
  private void pp2pDeliver(SendableEvent event) {
    // just sends the message event up
    Debug.print("BEB: Delivering message.");
    //TODO ugly hack : just need second condition to check and send the event up
    if(event.getClass().getName().equals(MySetEvent.class.getName()) || !event.getClass().getName().equals(SampleSendableEvent.class.getName()))
    {
      try {
        event.go();
      }
      catch (Exception e1)
      {

      }
    }
    else if(event.getClass().getName().equals(SampleSendableEvent.class.getName()))
    {
      ((SampleSendableEvent) event).setMtobPayload((MTOBPayload) event.getMessage().popObject());
      try {
        event.go();
      }
      catch (Exception e1)
      {
        System.out.println("exception in deliver beb");
      }
    }

  }

}
