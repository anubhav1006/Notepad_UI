package com.aj.notepad;

public class Notepads {

    Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Notepad getNotepad() {
        return notepad;
    }

    public void setNotepad(Notepad notepad) {
        this.notepad = notepad;
    }
    Notepad notepad;

    public Notepads(Notepad notepad) {
        this.notepad = notepad;
    }

    public Notepads() {
    }
}
