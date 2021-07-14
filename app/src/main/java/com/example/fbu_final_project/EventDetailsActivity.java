package com.example.fbu_final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.fbu_final_project.databinding.ActivityEventDetailsBinding;
import com.example.fbu_final_project.models.Event;

import org.parceler.Parcels;

public class EventDetailsActivity extends AppCompatActivity {

    Event event;

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
    }
}