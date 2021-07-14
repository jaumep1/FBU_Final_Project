package com.example.fbu_final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.fbu_final_project.databinding.ActivityEventDetailsBinding;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityEventDetailsBinding binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


    }
}