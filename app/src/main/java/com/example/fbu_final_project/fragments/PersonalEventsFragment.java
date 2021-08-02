package com.example.fbu_final_project.fragments;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.fbu_final_project.activities.MainActivity;
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

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PersonalEventsFragment extends EventsFeedFragment {

    private static final String TAG = "PersonalEventsFragment";

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

    @Override
    protected void queryEvents() {
        {
            ArrayList<String> eventIds = new ArrayList<>();

            for (Event event: ((User) ParseUser.getCurrentUser()).getSubscriptions()) {
                eventIds.add(event.getObjectId());
            }

            ParseQuery<Event> query = ParseQuery.getQuery(Event.class);

            query.include(Event.KEY_EVENT_NAME);
            query.include(Event.KEY_EVENT_DESCRIPTION);
            query.include(Event.KEY_AUTHOR);
            query.include(Event.KEY_START_TIME);
            query.include(Event.KEY_END_TIME);
            query.include(Event.KEY_IMAGE);

            query.whereContainedIn(Event.KEY_OBJECT_ID, eventIds);

            query.setLimit(20);
            query.addDescendingOrder("createdAt");
            query.findInBackground(new FindCallback<Event>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void done(List<Event> feed, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Issue with getting events", e);
                        return;
                    }
                    events.clear();
                    events.addAll(feed);
                    activeEvents.clear();
                    activeEvents.addAll(feed);
                    feedAdapter.notifyDataSetChanged();
                    try {
                        MainActivity.cacheEvents(events);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    MainActivity.loaded = true;
                }
            });
        }
    }
}