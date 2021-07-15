package com.example.fbu_final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.fbu_final_project.databinding.ActivityEventDetailsBinding;
import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.User;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class EventDetailsActivity extends AppCompatActivity {

    Event event;
    private String TAG = "EventDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityEventDetailsBinding binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        event = Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));

        binding.tvAuthor.setText(event.getAuthor());
        binding.tvDescription.setText(event.getDescription());
        binding.tvName.setText(event.getName());
        binding.tvTime.setText(String.format("%s - %s", event.getStartTime(), event.getEndTime()));

        binding.btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = (User) ParseUser.getCurrentUser();

                event.subscribe(user);
                user.subscribe(event);
                event.saveInBackground();
                user.saveInBackground();

                Toast.makeText(EventDetailsActivity.this, "Subscribed!",
                        Toast.LENGTH_SHORT).show();


            }
        });
    }
}