package main.java.utils.collection;

import java.util.HashSet;

/**
 * User: lesani, Date: Nov 3, 2009, Time: 11:56:53 AM
 */
public class Pair<T1, T2> {
    public T1 element1;
    public T2 element2;

    public Pair(T1 element1, T2 element2) {
        this.element1 = element1;
        this.element2 = element2;
    }

    public T1 _1() {
        return element1;
    }

    public T1 getElement1() {
        return element1;
    }

    public void set_1(T1 element1) {
        this.element1 = element1;
    }

    public void setElement1(T1 element1) {
        this.element1 = element1;
    }

    public T2 _2() {
        return element2;
    }

    public T2 getElement2() {
        return element2;
    }

    public void set_2(T2 element2) {
        this.element2 = element2;
    }

    public void setElement2(T2 element2) {
        this.element2 = element2;
    }

    public T1 ge1() {
        return element1;
    }

    public void set1(T1 element1) {
        this.element1 = element1;
    }

    public T2 get2() {
        return element2;
    }

    public void set2(T2 element2) {
        this.element2 = element2;
    }

    @Override
    public int hashCode() {
        int result = element1 != null ? element1.hashCode() : 0;
        result = 31 * result + (element2 != null ? element2.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (this.getClass() != other.getClass()) return false;
        Pair<?, ?> otherPair = (Pair<?, ?>) other;
        return element1.equals(otherPair.element1) && element2.equals(otherPair.element2);
    }

    @Override
    public String toString() {
        return "<" +
               element1 +
               ", " +
               element2 +
               ">";
    }

    public static void main(String[] args) {
        System.out.println(
                new Pair<String, String>("AA", "BB")
                .equals(
                    new Pair<String, String>("AA", "BB")
                )
        );

        HashSet<Pair<String, String>> hashSet = new HashSet<Pair<String, String>>();

        hashSet.add(new Pair<String, String>("AA", "BB"));

        Pair<String, String> inputPair = new Pair<String, String>("AA", "BB");

        System.out.println(hashSet.contains(inputPair));

        for (Pair<String, String> stringStringPair : hashSet) {
            if (stringStringPair.equals(inputPair))
                System.out.println("true");
        }

        System.out.println(Set.contains(hashSet, inputPair));
    }

}
