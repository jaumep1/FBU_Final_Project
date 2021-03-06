package com.example.fbu_final_project.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.fbu_final_project.R;
import com.example.fbu_final_project.activities.DetailsActivity;
import com.example.fbu_final_project.activities.LoginActivity;
import com.example.fbu_final_project.activities.MainActivity;
import com.example.fbu_final_project.adapters.AttendeesAdapter;
import com.example.fbu_final_project.applications.GoogleApplication;
import com.example.fbu_final_project.databinding.FragmentEventDetailsBinding;
import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.royrodriguez.transitionbutton.TransitionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventDetailsFragment extends Fragment {

    private static final String TAG = "EventDetailsFragment";

    Event event;
    List<User> attendees;

    AttendeesAdapter adapter;
    GoogleApplication client;

    FragmentEventDetailsBinding binding;

    public EventDetailsFragment(){}

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        client = new GoogleApplication();

        attendees = new ArrayList<>();
        adapter = new AttendeesAdapter(getContext(), attendees, (DetailsActivity) getActivity());
        LinearLayoutManager manager = new GridLayoutManager(getContext(), 2);
        binding.rvAttendees.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(binding.rvAttendees.getContext(),
                        manager.getOrientation());
        binding.rvAttendees.addItemDecoration(dividerItemDecoration);
        binding.rvAttendees.setAdapter(adapter);
        loadAttendees();

        binding.tvAuthor.setText(String.format("Created by: %s", event.getAuthor()));
        binding.tvDescription.setText(event.getDescription());
        binding.tvName.setText(event.getName());
        binding.tvTime.setText(String.format("%s - %s", event.getStartTime(), event.getEndTime()));

        if (isSubscribed()) {
            binding.btnSubscribe.setText("Unsubscribe");
        } else {
            binding.btnSubscribe.setText("Subscribe");
        }

        String img = event.getPoster();

        if (img != null) {
            Glide.with(this).load(img).into(binding.ivPoster);
        }

        if (event.getCreator().equals(ParseUser.getCurrentUser().getObjectId())){
            binding.btnDelete.setVisibility(View.VISIBLE);

        } else {
            binding.btnDelete.setVisibility(View.INVISIBLE);

        }
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        binding.btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                User user = (User) ParseUser.getCurrentUser();

                if (isSubscribed()) {

                    event.unsubscribe(user);
                    user.unsubscribe(event);
                    event.saveInBackground();
                    user.saveInBackground();

                    Toast.makeText(getContext(), "Unsubscribed!",
                            Toast.LENGTH_SHORT).show();
                    binding.btnSubscribe.setText(R.string.subscribe);
                } else {
                    event.subscribe(user);
                    user.subscribe(event);
                    event.saveInBackground();
                    user.saveInBackground();

                    Toast.makeText(getContext(), "Subscribed!",
                            Toast.LENGTH_SHORT).show();
                    binding.btnSubscribe.setText(R.string.unsubscribe);
                }
                MainActivity.loaded = false;
            }
        });

        binding.fabAddToCal.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    client.createCalendarEvent(getActivity(), event);
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Error adding event to calendar",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        binding.tvAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<User> query = ParseQuery.getQuery(User.class);

                query.include(User.KEY_PICTURE);
                query.include(User.KEY_FIRSTNAME);
                query.include(User.KEY_LASTNAME);
                query.include(User.KEY_OBJECT_ID);

                query.whereEqualTo(User.KEY_OBJECT_ID, event.getCreator());

                query.setLimit(20);
                query.addDescendingOrder("createdAt");
                query.findInBackground(new FindCallback<User>() {
                    @Override
                    public void done(List<User> attendeeList, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with getting tags", e);
                            return;
                        }
                        UserProfileFragment fragment = new UserProfileFragment();
                        fragment.setUserDetails(attendeeList.get(0), false);
                        ((DetailsActivity) getActivity()).loadProfile(fragment);
                    }
                });
            }
        });

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the loading animation when the user tap the button
                binding.btnDelete.startAnimation();

                // Do your networking task or background work here.
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean isSuccessful = deleteEvent();

                        // Choose a stop animation if your call was succesful or not
                        if (isSuccessful) {
                            binding.btnDelete.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                @Override
                                public void onAnimationStopEnd() {
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            binding.btnDelete.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        }
                    }
                }, 2000);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.miLogout) {
            //Compose icon has been clicked
            Log.d(TAG, "Logout clicked");
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Issue on login", e);
                        return;
                    }
                    goLoginActivity();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void goLoginActivity() {
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    private void loadAttendees() {
        ArrayList<String> attendeeIds = new ArrayList<>();

        for (User attendee : event.getAttendees()) {
            if (!attendeeIds.contains(attendee.getObjectId())) {
                attendeeIds.add(attendee.getObjectId());
            }
        }

        ParseQuery<User> query = ParseQuery.getQuery(User.class);

        query.include(User.KEY_PICTURE);
        query.include(User.KEY_FIRSTNAME);
        query.include(User.KEY_LASTNAME);
        query.include(User.KEY_OBJECT_ID);

        query.whereContainedIn(User.KEY_OBJECT_ID, attendeeIds);

        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> attendeeList, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting tags", e);
                    return;
                }
                attendees.addAll(attendeeList);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private boolean isSubscribed() {
        User user = ((User) ParseUser.getCurrentUser());

        ArrayList<Event> subs = user.getSubscriptions();
        for (Event sub : subs) {
            if (sub.getObjectId().equals(event.getObjectId())) {
                return true;
            }
        }
        return false;
    }

    private boolean deleteEvent() {
        final boolean[] succeeded = {true};

        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.whereEqualTo(Event.KEY_OBJECT_ID, event.getObjectId());
        query.getFirstInBackground(new GetCallback<Event>() {
            @Override
            public void done(Event object, ParseException e) {
                try {
                    object.delete();
                    object.saveInBackground();
                    JSONArray events = MainActivity.getEvents();
                    for (int i = 0; i < events.length(); i++) {
                        if (((JSONObject) events.get(i)).getString("objectID")
                                .equals(event.getObjectId())) {
                            events.remove(i);
                            break;
                        }
                    }

                    MainActivity.cacheEvents(events);
                } catch (ParseException | JSONException | IOException exception) {
                    exception.printStackTrace();
                    succeeded[0] = false;
                }
            }
        });
        return succeeded[0];
    }
}
