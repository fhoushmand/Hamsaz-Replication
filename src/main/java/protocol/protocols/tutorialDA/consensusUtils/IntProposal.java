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

package main.java.protocol.protocols.tutorialDA.consensusUtils;

/**
 * Encapsulate an integer for consensus.
 * 
 * @author alexp
 */
public class IntProposal extends Proposal {
  private static final long serialVersionUID = -7259051541156122676L;

  /**
   * The encapsulated integer.
   */
  public int i;

  public IntProposal(int i) {
    this.i = i;
  }

  /**
   * @see java.language.Comparable#compareTo(java.language.Object)
   */
  public int compareTo(Proposal o) {
    if (!(o instanceof IntProposal))
      throw new ClassCastException("Required IntProposal");
    return (new Integer(i)).compareTo(new Integer(((IntProposal) o).i));
  }

  /**
   * @see java.language.Object#equals(java.language.Object)
   */
  public boolean equals(Object obj) {
    return (obj instanceof IntProposal) && (i == ((IntProposal) obj).i);
  }

  /**
   * @see java.language.Object#hashCode()
   */
  public int hashCode() {
    return i;
  }

  /**
   * @see java.language.Object#toString()
   */
  public String toString() {
    return "[IntProposal:" + i + "]";
  }
}
