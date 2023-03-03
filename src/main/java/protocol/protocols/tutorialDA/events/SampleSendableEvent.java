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

package main.java.protocol.protocols.tutorialDA.events;

import main.java.protocol.protocols.tutorialDA.coordinationProtocols.MTOBPayload;
import main.java.protocol.protocols.tutorialDA.utils.Call;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.SendableEvent;

import java.io.Serializable;

/**
 * Sendable Event used by the application.
 * 
 * @author nuno
 */
public class SampleSendableEvent extends SendableEvent implements Serializable{

  private String command;

  private Call callToMethod;

  private String seqNumber;

  private MTOBPayload mtobPayload;


  /**
   * Default constructor.
   */
  public SampleSendableEvent() {
    super();
  }
  
  public SampleSendableEvent(Channel c, int dir, Session s)
  throws AppiaEventException{
	    super(c, dir, s);
  }
  

  /**
   * @return Returns the command.
   */
  public String getCommand() {
    return command;
  }

  /**
   * @param command
   *          The command to set.
   */
  public void setCommand(String command) {
    this.command = command;
  }

  public Call getCallToMethod() {
    return callToMethod;
  }

  public void setCallToMethod(Call callToMethod) {
    this.callToMethod = callToMethod;
  }

  public MTOBPayload getMtobPayload() {
    return mtobPayload;
  }

  public void setMtobPayload(MTOBPayload mtobPayload) {
    this.mtobPayload = mtobPayload;
  }

  public String getSeqNumber() {
    return seqNumber;
  }

  public void setSeqNumber(String seqNumber) {
    this.seqNumber = seqNumber;
  }
}
