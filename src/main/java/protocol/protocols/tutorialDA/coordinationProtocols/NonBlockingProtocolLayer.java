package main.java.protocol.protocols.tutorialDA.coordinationProtocols;

import main.java.analyser.Analyzer;
import main.java.protocol.protocols.tutorialDA.events.Crash;
import main.java.protocol.protocols.tutorialDA.events.NBACDecide;
import main.java.protocol.protocols.tutorialDA.events.NBACPropose;
import main.java.protocol.protocols.tutorialDA.events.ProcessInitEvent;
import main.java.protocol.protocols.tutorialDA.tcpBasedPFD.PFDStartEvent;
import main.java.robject.ReplicatedObject;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.SendableEvent;
import net.sf.appia.core.events.channel.ChannelClose;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.protocols.common.RegisterSocketEvent;

public class NonBlockingProtocolLayer extends Layer
{
    private ReplicatedObject object;
    private Analyzer analyzer;
    public NonBlockingProtocolLayer(ReplicatedObject o, Analyzer a)
    {
        evProvide = new Class[7];
        evProvide[0] = ReturnIndication.class;
        evProvide[1] = RegisterSocketEvent.class;
        evProvide[2] = ProcessInitEvent.class;
        evProvide[3] = PFDStartEvent.class;
        evProvide[4] = AbortIndication.class;
        evProvide[5] = ProcessInitEvent.class;
        evProvide[6] = NBACPropose.class;


        /*
         * events that the protocol require to work. This is a subset of the
         * accepted events
         */
        evRequire = new Class[5];
        evRequire[0] = SendableEvent.class;
        evRequire[1] = ChannelInit.class;
        evRequire[2] = ProcessInitEvent.class;
        evRequire[3] = Crash.class;
        evRequire[4] = NBACPropose.class;



        /* events that the protocol will accept */
        evAccept = new Class[7];
        evAccept[0] = SendableEvent.class;
        evAccept[1] = ChannelInit.class;
        evAccept[2] = ChannelClose.class;
        evAccept[3] = ProcessInitEvent.class;
        evAccept[4] = Crash.class;
        evAccept[5] = ProcessInitEvent.class;
        evAccept[6] = NBACDecide.class;

        this.object = o;
        this.analyzer = a;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public ReplicatedObject getObject() {
        return object;
    }

    @Override
    public Session createSession() {
        return new NonBlockingProtocolSession(this);
    }
}
