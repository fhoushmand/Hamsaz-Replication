package main.java.protocol.protocols.tutorialDA.coordinationProtocols;

import main.java.protocol.protocols.tutorialDA.utils.Call;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.SendableEvent;

public class NCoordCall extends SendableEvent
{

    public Call call;
    public NCoordCall() {
        super();
    }

    public NCoordCall(Channel c, int dir, Session s)
            throws AppiaEventException {
        super(c, dir, s);
    }
}
