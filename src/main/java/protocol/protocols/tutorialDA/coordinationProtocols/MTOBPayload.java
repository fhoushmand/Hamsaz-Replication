package main.java.protocol.protocols.tutorialDA.coordinationProtocols;

import java.io.Serializable;

public class MTOBPayload implements Serializable {

    public String m; //this represents call global id in the non-blocking protocol
    public String c; //this represent clique id
    public int rank; //this is the rank of call in this clique
    public String callType; //this field is aux field for sorting decision

    @Override
    public String toString() {
        return "("+callType+ "->" + m +", "+ c+", "+ rank+")";
    }
}