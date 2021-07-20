package com.example.fbu_final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.fbu_final_project.databinding.ActivityEventDetailsBinding;
import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;

public class EventDetailsActivity extends AppCompatActivity {

    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityEventDetailsBinding binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        event = Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));

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
                    binding.btnSubscribe.setText("Subscribe");
                } else {
                    event.subscribe(user);
                    user.subscribe(event);
                    event.saveInBackground();
                    user.saveInBackground();

                    Toast.makeText(EventDetailsActivity.this, "Subscribed!",
                            Toast.LENGTH_SHORT).show();
                    binding.btnSubscribe.setText("Unsubscribe");
                }
            }
        });

        binding.fabAddToCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: INTENT GOES HERE
            }
        });
        binding.fabAddToCal.setVisibility(View.INVISIBLE);
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