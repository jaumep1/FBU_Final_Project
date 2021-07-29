package com.example.fbu_final_project.fragments;

import android.util.Log;

import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class PersonalEventsFragment extends EventsFeedFragment {
    private static final String TAG = "PersonalEventsFragment";

    @Override
    protected void queryEvents() {
        events.clear();
        activeEvents.clear();

        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);

        ArrayList<String> eventIds = new ArrayList<>();

        for (Event sub : ((User) ParseUser.getCurrentUser()).getSubscriptions()) {
            eventIds.add(sub.getObjectId());
        }

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
            @Override
            public void done(List<Event> feed, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting events", e);
                    return;
                }
                events.addAll(feed);
                activeEvents.addAll(feed);
                feedAdapter.notifyDataSetChanged();
            }
        });
    }
}