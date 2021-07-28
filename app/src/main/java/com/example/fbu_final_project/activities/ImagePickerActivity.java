package com.example.fbu_final_project.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fbu_final_project.adapters.ImagePickerAdapter;
import com.example.fbu_final_project.databinding.ActivityImagePickerBinding;
import com.example.fbu_final_project.models.DriveFile;
import com.r0adkll.slidr.Slidr;

import org.parceler.Parcels;

import java.util.ArrayList;

public class ImagePickerActivity extends AppCompatActivity {

    ActivityImagePickerBinding binding;
    ImagePickerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Slidr.attach(this);
        binding = ActivityImagePickerBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        ArrayList<DriveFile> files = Parcels.unwrap(getIntent().getParcelableExtra("files"));

        LinearLayoutManager manager = new GridLayoutManager(this, 3);
        binding.rvImages.setLayoutManager(manager);

        adapter = new ImagePickerAdapter(this, files);
        binding.rvImages.setAdapter(adapter);
    }
}