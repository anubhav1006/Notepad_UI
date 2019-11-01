package com.aj.notepad;

public class Notepad {
    String title;
    String text;
    Long notepad_id;

    public Long getId() {
        return notepad_id;
    }

    public void setId(Long notepad_id) {
        this.notepad_id = notepad_id;
    }

    public Notepad() {
    }
    String user;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Notepad(String title, String user, String text) {
        this.title = title;
        this.user = user;
        this.text = text;
    }
}
