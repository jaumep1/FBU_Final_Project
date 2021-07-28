package com.example.fbu_final_project.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.desarrollodroide.libraryfragmenttransactionextended.FragmentTransactionExtended;
import com.example.fbu_final_project.databinding.ActivityEventDetailsBinding;
import com.example.fbu_final_project.fragments.EventDetailsFragment;
import com.example.fbu_final_project.models.Event;
import com.r0adkll.slidr.Slidr;

import org.parceler.Parcels;

public class EventDetailsActivity extends AppCompatActivity {

    public ActivityEventDetailsBinding binding;

    final FragmentManager fragmentManager = getFragmentManager();
    android.app.Fragment activeFragment;

    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Slidr.attach(this);
        event = Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));
        binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        EventDetailsFragment fragment = new EventDetailsFragment();
        fragment.setEvent(event);

        activeFragment = fragment;
        fragmentManager.beginTransaction().replace(binding.flContainer.getId(), fragment).commit();

    }

    public void loadProfile(android.app.Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentTransactionExtended fragmentTransactionExtended =
                new FragmentTransactionExtended(this, fragmentTransaction, activeFragment,
                        fragment, binding.flContainer.getId());
        fragmentTransactionExtended.addTransition(FragmentTransactionExtended.FADE);
        fragmentTransactionExtended.commit();
    }


}