package main.java.protocol.protocols.tutorialDA.coordinationProtocols;

import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.SendableEvent;

public class AbortVote extends SendableEvent
{
    public AbortVote() {
        super();
    }

    public AbortVote(Channel c, int dir, Session s)
            throws AppiaEventException {
        super(c, dir, s);
    }
}
