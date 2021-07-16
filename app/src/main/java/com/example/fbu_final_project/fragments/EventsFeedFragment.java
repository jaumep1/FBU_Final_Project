package com.example.fbu_final_project.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbu_final_project.R;
import com.example.fbu_final_project.adapters.EventsFeedAdapter;
import com.example.fbu_final_project.databinding.FragmentEventsFeedBinding;
import com.example.fbu_final_project.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class EventsFeedFragment extends Fragment {

    private static final String TAG = "EventsFeedFragment";
    FragmentEventsFeedBinding binding;
    List<Event> events;
    EventsFeedAdapter adapter;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());

        binding.rvEvents.setLayoutManager(manager);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(binding.rvEvents.getContext(),
                        manager.getOrientation());
        binding.rvEvents.addItemDecoration(dividerItemDecoration);

        events = new ArrayList<>();

        adapter = new EventsFeedAdapter(getContext(), events);
        binding.rvEvents.setAdapter(adapter);
        queryPosts();

        // Setup refresh listener which triggers new data loading
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                queryPosts();
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
                queryPosts();
                return true;
            }
        });
    }

    private void filterEvents(String search) {
        ParseQuery<Event> queryName = ParseQuery.getQuery(Event.class);
        ParseQuery<Event> queryDescription = ParseQuery.getQuery(Event.class);
        ParseQuery<Event> queryAuthor = ParseQuery.getQuery(Event.class);

        queryName.whereContains(Event.KEY_EVENT_NAME, search);
        queryDescription.whereContains(Event.KEY_EVENT_DESCRIPTION, search);
        queryAuthor.whereContains(Event.KEY_AUTHOR, search);

        List<ParseQuery<Event>> queries = new ArrayList<>();
        queries.add(queryName);
        queries.add(queryDescription);
        queries.add(queryAuthor);


        ParseQuery<Event> mainQuery = ParseQuery.or(queries);
        mainQuery.include(Event.KEY_EVENT_NAME);
        mainQuery.include(Event.KEY_EVENT_DESCRIPTION);
        mainQuery.include(Event.KEY_AUTHOR);
        mainQuery.include(Event.KEY_START_TIME);
        mainQuery.include(Event.KEY_END_TIME);

        mainQuery.setLimit(20);
        mainQuery.addDescendingOrder("createdAt");
        mainQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> feed, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                events.clear();
                events.addAll(feed);
                adapter.notifyDataSetChanged();
                Log.i("waka", events.toString());
            }
        });
    }

    protected void queryPosts(){
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);

        query.include(Event.KEY_EVENT_NAME);
        query.include(Event.KEY_EVENT_DESCRIPTION);
        query.include(Event.KEY_AUTHOR);
        query.include(Event.KEY_START_TIME);
        query.include(Event.KEY_END_TIME);

        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> feed, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                events.clear();
                events.addAll(feed);
                adapter.notifyDataSetChanged();
            }
        });
    }
}