package main.java.protocol.protocols.tutorialDA.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class Call implements Serializable {
    private static final long serialVersionUID = -5633377599L;


    public long startTime;
    public String methodName;
    private String args;
    public String globalId;
    public ArrayList<String> dependencies;
    public ArrayList<Call> deps;


    public Call(String name, String a, long start)
    {
        methodName = name;
        args = a;
        startTime = start;
    }

    public String[] getArgsArray()
    {
        return args.split(",");
    }

    @Override
    public String toString() {
        return " " +methodName + "(" + args +") ";
    }

    @Override
    public boolean equals(Object obj) {
        return this.globalId.equals(((Call)obj).globalId);
    }
}
