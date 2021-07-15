package com.example.fbu_final_project.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.example.fbu_final_project.R;
import com.example.fbu_final_project.databinding.FragmentPersonalEventsBinding;
import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PersonalEventsFragment extends EventsFeedFragment {
    private static final String TAG = "PersonalEventsFragment";

    @Override
    protected void queryPosts() {
        ArrayList<Event> subs = (ArrayList<Event>) (ParseUser.getCurrentUser()).get("subscriptions");
        for (Event sub : subs) {
            ParseQuery<Event> query = ParseQuery.getQuery(Event.class);

            query.include(Event.KEY_EVENT_NAME);
            query.include(Event.KEY_EVENT_DESCRIPTION);
            query.include(Event.KEY_AUTHOR);
            query.include(Event.KEY_START_TIME);
            query.include(Event.KEY_END_TIME);

            query.whereEqualTo("objectId", sub.getObjectId());

            query.addDescendingOrder("createdAt");
            query.findInBackground(new FindCallback<Event>() {
                @Override
                public void done(List<Event> feed, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Issue with getting posts", e);
                        return;
                    }
                    events.addAll(feed);
                    adapter.notifyDataSetChanged();
                }
            });

        }
    }
}