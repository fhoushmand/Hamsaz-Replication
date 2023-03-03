package robject;

import java.util.ArrayList;
import java.util.Set;

public class Clique {

    public String name;
    private ArrayList<String> methods;

    @Override
    public String toString() {
        return "Clique{" +
                "name='" + name + '\'' +
                ", methods=" + methods +
                '}';
    }

    public Clique(String n)
    {
        this.name = n;
        methods = new ArrayList<>();
    }

    public void addMethods(Set<String> ms) {
        methods.addAll(ms);
    }

    public ArrayList<String> getMethods() {
        return methods;
    }
}
