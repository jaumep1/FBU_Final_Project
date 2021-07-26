package com.example.fbu_final_project.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.fbu_final_project.R;
import com.example.fbu_final_project.adapters.AttendeesAdapter;
import com.example.fbu_final_project.applications.GoogleApplication;
import com.example.fbu_final_project.databinding.ActivityEventDetailsBinding;
import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.Tag;
import com.example.fbu_final_project.models.User;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";


    Event event;
    List<User> attendees;

    AttendeesAdapter adapter;
    GoogleApplication client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityEventDetailsBinding binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        event = Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));

        client = new GoogleApplication();

        attendees = new ArrayList<>();
        adapter = new AttendeesAdapter(getApplicationContext(), attendees);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
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

        binding.btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = (User) ParseUser.getCurrentUser();

                if (isSubscribed()) {

                    event.unsubscribe(user);
                    user.unsubscribe(event);
                    event.saveInBackground();
                    user.saveInBackground();

                    Toast.makeText(EventDetailsActivity.this, "Unsubscribed!",
                            Toast.LENGTH_SHORT).show();
                    binding.btnSubscribe.setText(R.string.subscribe);
                } else {
                    event.subscribe(user);
                    user.subscribe(event);
                    event.saveInBackground();
                    user.saveInBackground();

                    Toast.makeText(EventDetailsActivity.this, "Subscribed!",
                            Toast.LENGTH_SHORT).show();
                    binding.btnSubscribe.setText(R.string.unsubscribe);
                }
            }
        });



        binding.fabAddToCal.setOnClickListener(new View.OnClickListener() {
           @RequiresApi(api = Build.VERSION_CODES.O)
           @Override
           public void onClick(View v) {
               try {
                   client.createCalendarEvent(EventDetailsActivity.this, event);
               } catch (IOException e) {
                   Toast.makeText(getApplicationContext(), "Error adding event to calendar",
                           Toast.LENGTH_SHORT).show();
                   e.printStackTrace();
               }
           }
       });

        String img = event.getPoster();

        if (img != null) {
            Glide.with(this).load(img).into(binding.ivPoster);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
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
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
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
}