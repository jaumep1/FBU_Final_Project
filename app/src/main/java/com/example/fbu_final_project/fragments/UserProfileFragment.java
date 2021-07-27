package com.example.fbu_final_project.fragments;

import android.util.Log;

import com.example.fbu_final_project.models.User;

public class UserProfileFragment extends PersonalProfileFragment {
    User profile;

    public UserProfileFragment(User user, boolean attending) {
        super();
        this.profile = user;
        isAttendee = attending;
    }

    @Override
    public void setUser() {
        user = profile;

        if (isAttendee) {
            binding.tvHeader.setText(String.format("Events %s is Going To:", profile.getFirstname()));
        } else {
            binding.tvHeader.setText(String.format("Events %s Created:", profile.getFirstname()));
        }
    }
}
