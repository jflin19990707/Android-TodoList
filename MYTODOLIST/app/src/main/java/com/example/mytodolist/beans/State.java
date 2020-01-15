package com.example.mytodolist.beans;


public enum State {
    TODO(0),
    DONE(1),
    NONE(2);

    public final int intValue;

    State(int intValue) {
        this.intValue = intValue;
    }

    public static State from(int intValue) {
        for (State state : State.values()) {
            if (state.intValue == intValue) {
                return state;
            }
        }
        return TODO; // default
    }

    public static String intToString(int intValue) {
        switch (intValue) {
            case 0:
                return "TODO";
            case 1:
                return "DONE";
            case 2:
                return "NONE";

        }
        return "NONE";
    }
}
