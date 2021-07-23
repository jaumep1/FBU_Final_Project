package com.example.fbu_final_project.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.example.fbu_final_project.adapters.ImagePickerAdapter;
import com.example.fbu_final_project.databinding.ActivityImagePickerBinding;
import com.google.api.services.drive.model.FileList;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ImagePickerActivity extends AppCompatActivity {

    ActivityImagePickerBinding binding;
    ImagePickerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityImagePickerBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        ArrayList<String> files = Parcels.unwrap(getIntent().getParcelableExtra("files"));

        LinearLayoutManager manager = new GridLayoutManager(this, 3);
        binding.rvImages.setLayoutManager(manager);

        adapter = new ImagePickerAdapter(this, files);
        binding.rvImages.setAdapter(adapter);
    }
}