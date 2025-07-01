package com.example.notes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Note {
    private long id;
    private String title;
    private String content;
    private String timestamp;
    private boolean pinned;
    private boolean locked;
    private int color;
    private String pin;

    // Constructors
    public Note() {
        this.timestamp = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(new Date());
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.timestamp = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(new Date());
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public boolean isPinned() { return pinned; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }
    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

    public String getFormattedTimestamp() {
        return timestamp;
    }
}