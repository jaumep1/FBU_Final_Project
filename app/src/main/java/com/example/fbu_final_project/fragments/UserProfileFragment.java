package com.example.fbu_final_project.fragments;

import com.example.fbu_final_project.models.User;

public class UserProfileFragment extends PersonalProfileFragment {
    User profile;

    public UserProfileFragment(){}

    public void setUserDetails(User user, boolean attending) {
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
