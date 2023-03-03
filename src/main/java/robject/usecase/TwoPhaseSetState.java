package main.java.robject.usecase;

import robject.ReplicatedObjectState;

import java.util.HashSet;

public class TwoPhaseSetState implements ReplicatedObjectState {
    public HashSet<Integer> avail = new HashSet();
    public HashSet<Integer> tomb = new HashSet();

    @Override
    public String toString() {
//        return "\n"+students.toString() +"\n" + courses.toString() + "\n" + enrolments.toString() + "\n";
        return "\n";
    }
}
