package com.example.fbu_final_project.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fbu_final_project.R;
import com.example.fbu_final_project.activities.MainActivity;
import com.example.fbu_final_project.adapters.EventsFeedAdapter;
import com.example.fbu_final_project.adapters.TagsAdapter;
import com.example.fbu_final_project.databinding.FragmentEventsFeedBinding;
import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.Tag;
import com.example.fbu_final_project.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
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

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class EventsFeedFragment extends Fragment {

    private static final String TAG = "EventsFeedFragment";
    FragmentEventsFeedBinding binding;
    List<Event> events = new ArrayList<>();
    List<Event> activeEvents = new ArrayList<>();
    List<Tag> tags = new ArrayList<>();
    EventsFeedAdapter feedAdapter;
    TagsAdapter tagsAdapter;
    LinearLayoutManager feedManager;
    LinearLayoutManager tagsManager;
    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;

    public EventsFeedFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventsFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {

            }
        });

        setHasOptionsMenu(true);

        feedManager = new LinearLayoutManager(getContext());
        binding.rvEvents.setLayoutManager(feedManager);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(binding.rvEvents.getContext(),
                        feedManager.getOrientation());
        binding.rvEvents.addItemDecoration(dividerItemDecoration);

        tagsManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        tagsManager.canScrollHorizontally();
        binding.rvTagsFilter.setLayoutManager(tagsManager);

        feedAdapter = new EventsFeedAdapter(getContext(), activeEvents);
        binding.rvEvents.setAdapter(feedAdapter);

        tagsAdapter = new TagsAdapter(getContext(), tags);
        binding.rvTagsFilter.setAdapter(tagsAdapter);

        queryTags();
        try {
            fetchEvents();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < binding.rvTagsFilter.getChildCount(); i++) {
                    TagsAdapter.ViewHolder holder =
                            (TagsAdapter.ViewHolder) binding.rvTagsFilter.findViewHolderForAdapterPosition(i);

                   holder.binding.cbTag.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           filterEventsByTags();
                       }
                   });

                }
            }
        };

        //Wait to set listeners until recylcler view is bound
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, 200); // run in 1 second

        mWaveSwipeRefreshLayout = binding.swipeRefresh;
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override public void onRefresh() {
                MainActivity.loaded = false;
                try {
                    fetchEvents();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                queryTags();
                mWaveSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        try {
            fetchEvents();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.miSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterEvents(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                activeEvents.clear();
                activeEvents.addAll(events);
                feedAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void queryTags() {
        ParseQuery<Tag> query = ParseQuery.getQuery(Tag.class);

        query.include(Tag.KEY_TAG);
        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Tag>() {
            @Override
            public void done(List<Tag> tagList, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting tags", e);
                    return;
                }
                tags.addAll(tagList);
                tagsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void filterEvents(String search) {

        activeEvents.clear();
        for (Event event : events) {
            if (event.getName().contains(search)) {
                activeEvents.add(event);
            } else if (event.getDescription().contains(search)) {
                activeEvents.add(event);
            } else if (event.getAuthor().contains(search)) {
                activeEvents.add(event);
            }
        }
        feedAdapter.notifyDataSetChanged();
    }


    public void filterEventsByTags() {
        List<Tag> activeTags = new ArrayList<>();

        for (int i = 0; i < binding.rvTagsFilter.getChildCount(); i++) {
            TagsAdapter.ViewHolder holder =
                    (TagsAdapter.ViewHolder) binding.rvTagsFilter.findViewHolderForAdapterPosition(i);

            if (holder.binding.cbTag.isChecked()) {
                activeTags.add(tags.get(i));
            }

        }

        if (activeTags.isEmpty()) {
            activeEvents.clear();
            activeEvents.addAll(events);
            feedAdapter.notifyDataSetChanged();
            return;
        }

        activeEvents.clear();
        for (Event event : events) {
            List<Tag> eventTags = event.getTags();
            if (eventTags != null) {
                for (Tag eventTag : eventTags) {
                    if (activeEvents.contains(event)) {
                        break;
                    }
                    for (Tag activeTag : activeTags) {
                        if (eventTag.getObjectId().equals(activeTag.getObjectId())) {
                            activeEvents.add(event);
                            break;
                        }
                    }
                }
            }
        }
        feedAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void fetchEvents() throws JSONException {
        events.clear();
        activeEvents.clear();

        if (MainActivity.loaded) {
            JSONArray eventsJsonArray = MainActivity.getEvents();
            loadEventsFromCacheData(eventsJsonArray);

        } else {
            queryEvents();
        }
    }

    protected void queryEvents() {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);

        query.include(Event.KEY_EVENT_NAME);
        query.include(Event.KEY_EVENT_DESCRIPTION);
        query.include(Event.KEY_AUTHOR);
        query.include(Event.KEY_START_TIME);
        query.include(Event.KEY_END_TIME);
        query.include(Event.KEY_IMAGE);

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void loadEventsFromCacheData(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);

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
            for (int j = 0; j< attendeesJsonArray.length(); j++) {
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