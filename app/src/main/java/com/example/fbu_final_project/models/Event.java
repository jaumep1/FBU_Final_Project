package com.example.fbu_final_project.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Date;

@Parcel(analyze = Event.class)
@ParseClassName("Event")
public class Event extends ParseObject {
    public static final String KEY_CREATED_BY = "createdBy";
    public static final String KEY_AUTHOR = "authorName";
    public static final String KEY_EVENT_NAME = "eventName";
    public static final String KEY_EVENT_DESCRIPTION = "eventDescription";
    public static final String KEY_START_TIME = "startTime";
    public static final String KEY_END_TIME = "endTime";
    public static final String KEY_ATTENDEES = "attendees";
    public static final String KEY_TAGS = "tags";

    public String getCreator() {
        return getString(KEY_CREATED_BY);
    }

    public void setCreator(String creatorID) { put(KEY_CREATED_BY, creatorID);
    }

    public String getAuthor() {
        return getString(KEY_AUTHOR);
    }

    public void setAuthor(String author) {
        put(KEY_AUTHOR, author);
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

    public void createAttendees () {
        put(KEY_ATTENDEES, new ArrayList<User>());
    }

    public void subscribe (User user) {
        add(KEY_ATTENDEES, user);
    }

    public void unsubscribe(User user) {
        ArrayList<User> users = (ArrayList<User>) get(KEY_ATTENDEES);
        ArrayList<User> newUserList = new ArrayList<>();
        for (User u : users) {
            if (!u.getObjectId().equals(user.getObjectId())) {
                newUserList.add(u);
            }
        }
        put(KEY_ATTENDEES, newUserList);
    }

    public ArrayList<Event> getAttendees() {
        return ((ArrayList<Event>) get(KEY_ATTENDEES));
    }

    public void createTags () {
        put(KEY_TAGS, new ArrayList<Tag>());
    }

    public void addTag (Tag tag) {
        add(KEY_TAGS, tag);
    }


}