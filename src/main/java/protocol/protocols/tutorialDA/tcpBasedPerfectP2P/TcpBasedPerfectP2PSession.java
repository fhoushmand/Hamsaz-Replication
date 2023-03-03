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

package main.java.protocol.protocols.tutorialDA.tcpBasedPerfectP2P;

import net.sf.appia.core.Event;
import net.sf.appia.core.Layer;
import net.sf.appia.protocols.tcpcomplete.TcpCompleteSession;

/**
 * Session implementing the
 * TCP-Based Perfect Point-to-Point Links protocol.
 * 
 * @author Nuno Carvalho
 *
 * @deprecated
 */
public class TcpBasedPerfectP2PSession extends TcpCompleteSession {


  /**
   * Constructor for NewTcpSession.
   * 
   * @param layer
   */
  public TcpBasedPerfectP2PSession(Layer layer) {
    super(layer);
  }

  public void handle(Event e) {
	  super.handle(e);
  }

}