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

package main.java.protocol.protocols.tutorialDA.floodingConsensus;

import main.java.protocol.protocols.tutorialDA.events.ConsensusDecide;
import main.java.protocol.protocols.tutorialDA.events.ConsensusPropose;
import main.java.protocol.protocols.tutorialDA.events.Crash;
import main.java.protocol.protocols.tutorialDA.events.ProcessInitEvent;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;

/**
 * The Regular Flooding Consensus algorithm.
 * 
 * @author alexp
 */
public class FloodingConsensusLayer extends Layer {

  public FloodingConsensusLayer() {

    evProvide = new Class[3];
    evProvide[0] = ConsensusDecide.class;
    evProvide[1] = MySetEvent.class;
    evProvide[2] = DecidedEvent.class;

    evRequire = new Class[3];
    evRequire[0] = ProcessInitEvent.class;
    evRequire[1] = Crash.class;
    evRequire[2] = ConsensusPropose.class;

    evAccept = new Class[5];
    evAccept[0] = ProcessInitEvent.class;
    evAccept[1] = Crash.class;
    evAccept[2] = ConsensusPropose.class;
    evAccept[3] = MySetEvent.class;
    evAccept[4] = DecidedEvent.class;
  }

  /**
   * @see appia.Layer#createSession()
   */
  public Session createSession() {
    return new FloodingConsensusSession(this);
  }
}
