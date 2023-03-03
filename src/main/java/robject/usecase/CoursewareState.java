package main.java.robject.usecase;

import org.apache.commons.lang3.tuple.ImmutablePair;
import robject.ReplicatedObjectState;

import java.util.HashSet;

public class CoursewareState implements ReplicatedObjectState {
    public HashSet<Integer> students = new HashSet();
    public HashSet<Integer> courses = new HashSet();
    public HashSet<ImmutablePair<Integer, Integer>> enrolments = new HashSet();


    @Override
    public String toString() {
        return "\n"+students.toString() +"\n" + courses.toString() + "\n" + enrolments.toString() + "\n";
    }
}
