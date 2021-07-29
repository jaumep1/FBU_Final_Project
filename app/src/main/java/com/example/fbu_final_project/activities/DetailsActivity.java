package com.example.fbu_final_project.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.desarrollodroide.libraryfragmenttransactionextended.FragmentTransactionExtended;
import com.example.fbu_final_project.R;
import com.example.fbu_final_project.databinding.ActivityDetailsBinding;
import com.example.fbu_final_project.fragments.EventDetailsFragment;
import com.example.fbu_final_project.fragments.PersonalProfileFragment;
import com.example.fbu_final_project.models.Event;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.r0adkll.slidr.Slidr;

import org.parceler.Parcels;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";
    public ActivityDetailsBinding binding;

    final FragmentManager fragmentManager = getFragmentManager();
    android.app.Fragment activeFragment;

    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Slidr.attach(this);
        event = Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        if (event == null) {
            android.app.Fragment fragment = new PersonalProfileFragment();
            activeFragment = fragment;
            fragmentManager.beginTransaction().replace(binding.flContainer.getId(), fragment).commit();
        } else {
            EventDetailsFragment fragment = new EventDetailsFragment();
            fragment.setEvent(event);
            activeFragment = fragment;
            fragmentManager.beginTransaction().replace(binding.flContainer.getId(), fragment).commit();
        }
    }

    public void loadProfile(android.app.Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentTransactionExtended fragmentTransactionExtended =
                new FragmentTransactionExtended(this, fragmentTransaction, activeFragment,
                        fragment, binding.flContainer.getId());
        fragmentTransactionExtended.addTransition(FragmentTransactionExtended.FADE);
        fragmentTransactionExtended.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.miSearch).setVisible(false);
        menu.findItem(R.id.miProfile).setVisible(false);
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.miLogout) {
            Log.d(TAG, "Logout clicked");
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Issue on login", e);
                        return;
                    }
                    goLoginActivity();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}