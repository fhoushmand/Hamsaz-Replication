package main.java.protocol.protocols.tutorialDA.coordinationProtocols;

import main.java.protocol.protocols.tutorialDA.utils.Call;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.SendableEvent;

import java.util.LinkedList;

public class CommitVote extends SendableEvent
{

    public Call call;
    public LinkedList<Call> deps;
    public CommitVote() {
        super();
    }

    public CommitVote(Channel c, int dir, Session s)
            throws AppiaEventException {
        super(c, dir, s);
    }
}
