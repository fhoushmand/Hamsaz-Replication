package main.java.protocol.protocols.tutorialDA.coordinationProtocols;

import main.java.protocol.protocols.tutorialDA.utils.Call;
import net.sf.appia.core.*;

public class ReturnIndication extends Event {

    public Object returnValue;
    public Call methodCall;

    public ReturnIndication(Channel channel, int dir, Session session) throws AppiaEventException
    {
        super(channel, dir, session);
    }
}
