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

import main.java.protocol.protocols.tutorialDA.events.SampleSendableEvent;
import main.java.protocol.protocols.tutorialDA.utils.Call;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Direction;
import net.sf.appia.core.message.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Class that reads from the keyboard and generates events to the appia Channel.
 *
 * @author nuno
 */
public class SampleApplFileReader extends Thread {

  AtomicInteger n;
  private SampleApplSession parentSession;
  private Scanner keyb;
  private String local = null;
  private long delay;

  Channel joinChannel;

  public SampleApplFileReader(SampleApplSession parentSession, String fileLoc, long delay, Channel channel, AtomicInteger nn) {
    super();
    this.delay = delay;
    this.parentSession = parentSession;
    this.joinChannel = channel;
    this.n = nn;

    try {
      keyb = new Scanner(new File(fileLoc));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void run() {
//    try {
//      Thread.sleep(5000);
//      SampleSendableEvent join = new SampleSendableEvent();
//      for (int i = 0; i < parentSession.processes.getSize(); i++) {
//        join.source = parentSession.processes.getSelfProcess().getSocketAddress();
//        join.dest = parentSession.processes.getProcess(i).getSocketAddress();
//        join.setSourceSession(parentSession);
//        join.setCommand("join");
//      }
////    join.setCommand("join");
//      join.asyncGo(joinChannel, Direction.DOWN);
//    } catch (AppiaEventException e) {
//      throw new RuntimeException(e);
//    } catch (InterruptedException e) {
//      throw new RuntimeException(e);
//    }
//    try {
//      while (parentSession.processes.getSize() != n.get()) {
//        Thread.sleep(10);
//      }
////          System.gc();
//    } catch (InterruptedException e) {
//    }
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    while (keyb.hasNext()) {
      try {
        local = keyb.nextLine();
        if (local.equals("") || local.startsWith("#"))
          continue;
        if(local.equals("exit-sim"))
        {
          SampleSendableEvent asyn = new SampleSendableEvent();
          asyn.setCommand("exit-sim");
          asyn.asyncGo(parentSession.channel, Direction.DOWN);
          continue;
        }
        StringTokenizer st = new StringTokenizer(local);
        /*
         * creates the event, push the message and sends this to the appia
         * channel.
         */
        SampleSendableEvent asyn = new SampleSendableEvent();
        Message message = asyn.getMessage();
        String command = st.nextToken().split(":")[1];
        asyn.setCommand(command);
        String msg = "";
        while (st.hasMoreTokens())
          msg += (st.nextToken() + " ");
        if(asyn.getCommand().equals("call"))
        {
          Call c = new Call(msg.substring(0, msg.indexOf('(')), msg.substring(msg.indexOf('(') + 1, msg.indexOf(')')), System.nanoTime());
          asyn.setCallToMethod(c);
        }
        message.pushString(msg);
        asyn.asyncGo(parentSession.channel, Direction.DOWN);
        try {
          Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
      } catch (AppiaEventException e) {
        e.printStackTrace();
      }
    }
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
//    for (int x = 0; x < 10; x ++)
//    {
//
//      StringTokenizer st = null;
////      st = new StringTokenizer(new String("7:call deleteCourse(10)"));
////        if(x % 3 == 0)
////             st = new StringTokenizer(new String("7:call deleteCourse(10)"));
//          st = new StringTokenizer("7:call withdraw(0)");
////        else if(x % 4 == 1)
////             st = new StringTokenizer(new String("7:call query()"));
////        else if(x % 3 == 1)
////             st = new StringTokenizer(new String("7:call enrol(0,0)"));
////        else if(x % 3 == 2)
////            st = new StringTokenizer(new String("7:call addCourse(1000)"));
//
////        StringTokenizer st = new StringTokenizer(new String("7:call query()"));
////      StringTokenizer st = new StringTokenizer(new String("7:call withdraw(0)"));
//      SampleSendableEvent asyn = new SampleSendableEvent();
//      Message message = asyn.getMessage();
//      String command = st.nextToken().split(":")[1];
//      asyn.setCommand(command);
//      System.out.println("end of file and flushing...");
//
//      String msg = "";
//      while (st.hasMoreTokens())
//        msg += (st.nextToken() + " ");
//      if(asyn.getCommand().equals("call"))
//      {
//        Call c = new Call(msg.substring(0, msg.indexOf('(')), msg.substring(msg.indexOf('(') + 1, msg.indexOf(')')), System.nanoTime());
//        asyn.setCallToMethod(c);
//      }
//      message.pushString(msg);
//      try {
//        asyn.asyncGo(parentSession.channel, Direction.DOWN);
//      } catch (AppiaEventException e) {
//        e.printStackTrace();
//      }
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//        }
//    }

  }
}
