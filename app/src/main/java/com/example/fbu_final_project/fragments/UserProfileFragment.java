package com.example.fbu_final_project.fragments;

import android.util.Log;

import com.example.fbu_final_project.models.User;

public class UserProfileFragment extends PersonalProfileFragment {
    User profile;

    public UserProfileFragment(User user) {
        super();
        this.profile = user;
    }

    @Override
    public void setUser() {
        user = profile;
        binding.tvHeader.setText(String.format("Events %s Created:", profile.getFirstname()));
    }
}
