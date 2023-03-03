package main.java.robject;

import main.java.analyser.ObjectAST;
import robject.ReplicatedObjectState;

//import java.language.reflect.Method;
import java.lang.reflect.Method;
import java.util.*;

public abstract class ReplicatedObject {

    private ReplicatedObjectState state;
    public ReplicatedObject() {}
    public ReplicatedObjectState getState() {
        return state;
    }
    public abstract ObjectAST getASTFormat();
    public void setState(ReplicatedObjectState state) {
        this.state = state;
    }

    public Method getMethod(String method)
    {
        for (Method m : this.getClass().getDeclaredMethods()) {
            if(m.getName().equals(method)) {
                return m;
            }
        }
        return null;
    }

    public Method getGuard(String method)
    {
        for (Method m : this.getClass().getDeclaredMethods()) {
            if(m.getName().equals(method)) {
                String guardFuncName = m.getAnnotation(Guard.class).value();

                for (Method g : this.getClass().getDeclaredMethods()) {
                    if(g.getName().equals(guardFuncName)) {
                        return g;
                    }
                }
            }
        }
        return null;
    }

    public Method getInvariant()
    {
        for (Method m : this.getClass().getDeclaredMethods()) {
            if(m.getName().equals("invariant")) {
                return m;
            }
        }
        return null;
    }

    public LinkedList<Method> getAllMethodsOfObject()
    {
        LinkedList<Method> methods = new LinkedList<>();
        for (Method method : this.getClass().getMethods()) {
            if(!method.getName().startsWith("g_") && method.isAnnotationPresent(Guard.class)) {
                methods.add(method);
            }
        }
        return methods;
    }
}
