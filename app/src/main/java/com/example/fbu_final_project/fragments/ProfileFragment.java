package com.example.fbu_final_project.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.fbu_final_project.R;
import com.example.fbu_final_project.adapters.EventsFeedAdapter;
import com.example.fbu_final_project.databinding.FragmentEventsFeedBinding;
import com.example.fbu_final_project.databinding.FragmentProfileBinding;
import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    FragmentProfileBinding binding;
    EventsFeedAdapter adapter;
    List<Event> events;
    User user;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = (User) ParseUser.getCurrentUser();

        binding.tvName.setText(String.format("%s %s", user.getFirstname(), user.getLastname()));
        Glide.with(getContext()).load(user.getProfilePic().getUrl()).into(binding.ivProfilePic);

        events = new ArrayList<>();
        adapter = new EventsFeedAdapter(getContext(), events);
        binding.rvEvents.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.rvEvents.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(binding.rvEvents.getContext(),
                        manager.getOrientation());
        binding.rvEvents.addItemDecoration(dividerItemDecoration);
        queryEvents();
    }

    private void queryEvents() {
        events.clear();

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


        query.whereEqualTo(Event.KEY_AUTHOR, String.format("%s %s", user.getFirstname(), user.getLastname()));

        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> feed, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting events", e);
                    return;
                }
                Log.i("waka", String.valueOf(feed.size()));
                events.addAll(feed);
                adapter.notifyDataSetChanged();
            }
        });
    }
}