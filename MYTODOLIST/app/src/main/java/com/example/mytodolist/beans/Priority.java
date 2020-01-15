package com.example.mytodolist.beans;

public enum Priority {
    A(4),
    B(3),
    C(2),
    D(1),
    NONE(0);

    public final int intValue;

    Priority(int intValue) {
        this.intValue = intValue;
    }

    public static Priority from(int intValue) {
        for (Priority priority : Priority.values()) {
            if (priority.intValue == intValue) {
                return priority;
            }
        }
        return Priority.NONE; // default
    }
    public static String intToString(int intValue) {
        switch (intValue){
            case 0:
                return "NONE";
            case 1:
                return "#D";
            case 2:
                return "#C";
            case 3:
                return "#B";
            case 4:
                return "#A";
        }
        return "NONE";
    }
}
