package main.java.robject.usecase;

import robject.ReplicatedObjectState;

public class BankAccountState implements ReplicatedObjectState {
    public Integer balance;
    public BankAccountState(Integer b) {
        balance = b;
    }

    @Override
    public String toString() {
        return "state = <balance: " + balance + ">";
    }
}
