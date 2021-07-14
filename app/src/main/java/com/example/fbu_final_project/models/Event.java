package com.example.fbu_final_project.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Event")
public class Event extends ParseObject {
    private static final String KEY_CREATED_BY = "createdBy";
    private static final String KEY_EVENT_NAME = "eventName";
    private static final String KEY_EVENT_DESCRIPTION = "eventDescription";
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";

    public String getAuthor() {
        return getString(KEY_CREATED_BY);
    }

    public void setAuthor(String author) {
        put(KEY_CREATED_BY, author);
    }

    public String getName() {
        return getString(KEY_EVENT_NAME);
    }

    public void setName(String name) {
        put(KEY_EVENT_NAME, name);
    }

    public String getDescription() {
        return getString(KEY_EVENT_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_EVENT_DESCRIPTION, description);
    }

    public Date getStartTime() {
        return getDate(KEY_START_TIME);
    }

    public void setStartTime(Date start) {
        put(KEY_START_TIME, start);
    }

    public Date getEndTime() {
        return getDate(KEY_END_TIME);
    }

    public void setEndTime(Date end) {
        put(KEY_END_TIME, end);
    }


}