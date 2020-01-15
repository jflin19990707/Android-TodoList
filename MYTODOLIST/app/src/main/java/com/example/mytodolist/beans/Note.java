package com.example.mytodolist.beans;

public class Note {
    public final long id;
    private String headline;
    private State state;
    private Priority priority;
    private String schedule;
    private String deadline;
    private String show;
    private String repeat_show;
    private int repeat_show_num;
    private String content;
    private String tag;
    private  int countstar = 1;
    private String files;

    public Note(long id) { this.id = id; }

    public void setheadline(String headline) {
        this.headline = headline;
    }

    public String getheadline() {
        return headline;
    }

    public void setfiles(String files) {
        this.files = files;
    }

    public String getfiles() {
        return files;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getschedule() {
        return schedule;
    }
    public void setTag(String tag){this.tag = tag;}

    public String getTag(){return tag;}

    public void setschedule(String schedule) {
        this.schedule = schedule;
    }

    public String getdeadline() {
        return deadline;
    }

    public void setdeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getshow() {
        return show;
    }

    public void setshow(String show) {
        this.show = show;
    }

    public String getrepeat_show() {
        return repeat_show;
    }

    public void setrepeat_show(String repeat_show) {
        this.repeat_show = repeat_show;
    }

    public int getrepeat_show_num() {
        return repeat_show_num;
    }

    public void setrepeat_show_num(int repeat_show_num) { this.repeat_show_num = repeat_show_num; }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCountstar(){
        return countstar;
    }

    public int setCountstar(int countstar) {
        return this.countstar=countstar;
    }
}
