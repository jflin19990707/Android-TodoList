package com.example.mytodolist.beans;

public class Tag {

    public final long id;
    private String tagName;


    public Tag(long id) { this.id = id; }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }
}
