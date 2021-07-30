package com.example.fbu_final_project.fragments;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.Tag;
import com.example.fbu_final_project.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PersonalEventsFragment extends EventsFeedFragment {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void loadEventsFromCacheData(JSONArray jsonArray) throws JSONException {

        ArrayList<String> eventIds = new ArrayList<>();

        for (Event sub : ((User) ParseUser.getCurrentUser()).getSubscriptions()) {
            eventIds.add(sub.getObjectId());
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);

            if (eventIds.contains(jsonObject.getString("objectID"))) {

                Event newEvent = new Event();
                newEvent.setObjectId(jsonObject.getString("objectID"));
                newEvent.setCreator(jsonObject.getString("created_by"));
                newEvent.setAuthor(jsonObject.getString("author"));
                newEvent.setName(jsonObject.getString("name"));
                newEvent.setDescription(jsonObject.getString("description"));
                newEvent.setPoster(jsonObject.getString("poster"));

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("eee MMM dd HH:mm:ss zzz yyyy");

                ZonedDateTime start = ZonedDateTime.parse(jsonObject.getString("startTime"), dtf);
                newEvent.setStartTime(Date.from(start.toInstant()));
                ZonedDateTime end = ZonedDateTime.parse(jsonObject.getString("endTime"), dtf);
                newEvent.setEndTime(Date.from(end.toInstant()));

                JSONArray tagsJsonArray = new JSONArray(jsonObject.getString("tags"));
                ArrayList<Tag> eventTagsFromJson = new ArrayList<>();
                for (int j = 0; j < tagsJsonArray.length(); j++) {
                    Tag newTag = new Tag();
                    newTag.setObjectId(tagsJsonArray.getString(j));
                    eventTagsFromJson.add(newTag);
                }
                newEvent.setTags(eventTagsFromJson);

                JSONArray attendeesJsonArray = new JSONArray(jsonObject.getString("attendees"));
                ArrayList<User> eventAttendeesFromJson = new ArrayList<>();
                for (int j = 0; j < attendeesJsonArray.length(); j++) {
                    User newUser = new User();
                    newUser.setObjectId(attendeesJsonArray.getString(j));
                    eventAttendeesFromJson.add(newUser);
                }
                newEvent.setAttendees(eventAttendeesFromJson);

                events.add(newEvent);
                activeEvents.add(newEvent);
            }
        }
    }
}