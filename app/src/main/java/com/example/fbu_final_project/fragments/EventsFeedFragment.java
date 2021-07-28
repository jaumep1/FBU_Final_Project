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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fbu_final_project.R;
import com.example.fbu_final_project.adapters.EventsFeedAdapter;
import com.example.fbu_final_project.adapters.TagsAdapter;
import com.example.fbu_final_project.databinding.FragmentEventsFeedBinding;
import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.Tag;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

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

    public EventsFeedFragment() {
        // Required empty public constructor
    }

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

    @RequiresApi(api = Build.VERSION_CODES.M)
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
        queryEvents();

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
        handler.postDelayed(runnable, 1000); // run in 1 second

        // Setup refresh listener which triggers new data loading
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                feedAdapter.clear();
                queryEvents();
                for (int i = 0; i < binding.rvTagsFilter.getChildCount(); i++) {
                    TagsAdapter.ViewHolder holder =
                            (TagsAdapter.ViewHolder) binding.rvTagsFilter.findViewHolderForAdapterPosition(i);

                    holder.binding.cbTag.setChecked(false);

                }
                binding.swipeContainer.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
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

    protected void queryEvents(){
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
            }
        });
    }
}