package com.example.fbu_final_project.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import java.util.ArrayList;

@ParseClassName("_User")
public class User extends ParseUser {

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_SUBSCRIPTIONS = "subscriptions";

    public void setUsername(String username) {
        put(KEY_USERNAME, username);
    }
    public String getUsername() {
        return getString("username");
    }
    public void setPassword(String password) {
        put(KEY_PASSWORD, password);
    }
    public void setFirstname(String firstname) {
        put(KEY_FIRSTNAME, firstname);
    }
    public String getFirstname() {
        return getString("firstname");
    }
    public void setLastname(String lastname) {
        put(KEY_LASTNAME, lastname);
    }
    public String getLastname() {
        return getString("lastname");
    }
    public void subscribe(Event event) {
        add(KEY_SUBSCRIPTIONS, event);
    }
    public void unsubscribe(Event event) {
        ArrayList<Event> events = (ArrayList<Event>) get(KEY_SUBSCRIPTIONS);
        ArrayList<Event> newEventList = new ArrayList<>();
        for (Event e : events) {
            if (!e.getObjectId().equals(event.getObjectId())) {
                newEventList.add(e);
            }
        }
        put(KEY_SUBSCRIPTIONS, newEventList);
    }
    public ArrayList<Event> getSubscriptions() {
        return ((ArrayList<Event>) get(KEY_SUBSCRIPTIONS));
    }
    public void createSubs() {
        put(KEY_SUBSCRIPTIONS, new ArrayList<Event>());
    }

}