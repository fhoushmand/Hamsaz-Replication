package main.java.utils.collection;


public class Set {
    public static <T>  boolean contains(java.util.Set<T> set, T t) {
        for (T element : set) {
            if (element.equals(t))
                return true;
        }
        return false;
    }
}
