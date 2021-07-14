package com.example.fbu_final_project.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbu_final_project.R;
import com.example.fbu_final_project.databinding.FragmentPersonalEventsBinding;

public class PersonalEventsFragment extends Fragment {

    FragmentPersonalEventsBinding binding;

    public PersonalEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPersonalEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}