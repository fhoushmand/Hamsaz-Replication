package main.java.protocol.protocols.tutorialDA.consensusUTO;

import main.java.protocol.protocols.tutorialDA.coordinationProtocols.AbortIndication;
import main.java.protocol.protocols.tutorialDA.coordinationProtocols.ReturnIndication;
import main.java.protocol.protocols.tutorialDA.events.ProcessInitEvent;
import main.java.robject.ReplicatedObject;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.SendableEvent;
import net.sf.appia.core.events.channel.ChannelInit;

public class EventualLayer extends Layer {

    ReplicatedObject ro;
    /**
     * Standard constructor
     */
    public EventualLayer(ReplicatedObject ro) {

        evProvide = new Class[5];
        evProvide[0] = ChannelInit.class;
        evProvide[1] = ProcessInitEvent.class;
        evProvide[2] = SendableEvent.class;
        evProvide[3] = ReturnIndication.class;
        evProvide[4] = AbortIndication.class;

        evRequire = new Class[1];
        evRequire[0] = ChannelInit.class;

        evAccept = new Class[3];
        evAccept[0] = ChannelInit.class;
        evAccept[1] = ProcessInitEvent.class;
        evAccept[2] = SendableEvent.class;

        this.ro = ro;
    }

    public Session createSession() {
        return new EventualSession(this, ro);
    }

}