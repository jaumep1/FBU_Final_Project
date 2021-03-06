package com.example.fbu_final_project.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel(analyze = User.class)
@ParseClassName("_User")
public class User extends ParseUser {

    public static final String KEY_ID = "objectId";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FIRSTNAME = "firstname";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_SUBSCRIPTIONS = "subscriptions";
    public static final String KEY_PICTURE = "profileImage";
    public static final String KEY_SECURITY_QUESTION = "securityQuestion";

    public void setUsername(String username) {
        put(KEY_USERNAME, username);
    }
    public String getUsername() {
        return getString(KEY_USERNAME);
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

    public ParseFile getProfilePic() {
        return getParseFile(KEY_PICTURE);
    }

    public void setProfilePic(ParseFile file) {
         remove(KEY_PICTURE);
         put(KEY_PICTURE, file);
    }

    public void setSecurityQuestion(String answer) {
        put(KEY_SECURITY_QUESTION, answer);
    }

    public String getSecurityQuestion() {
        return getString(KEY_SECURITY_QUESTION);
    }
}