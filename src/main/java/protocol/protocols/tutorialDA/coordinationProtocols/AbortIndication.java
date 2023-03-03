package main.java.protocol.protocols.tutorialDA.coordinationProtocols;

import main.java.protocol.protocols.tutorialDA.utils.Call;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Event;
import net.sf.appia.core.Session;

public class AbortIndication extends Event {

    public Call methodCall;
    public Object returnValue;

    public AbortIndication(Channel channel, int dir, Session session) throws AppiaEventException
    {
        super(channel, dir, session);
    }
}
