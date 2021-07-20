package com.example.fbu_final_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fbu_final_project.R;
import com.example.fbu_final_project.activities.LoginActivity;
import com.example.fbu_final_project.databinding.ActivityMainBinding;
import com.example.fbu_final_project.fragments.CreateFragment;
import com.example.fbu_final_project.fragments.EventsFeedFragment;
import com.example.fbu_final_project.fragments.PersonalEventsFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    public static ActivityMainBinding binding;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        context = this;

        //setup toolbar
        setSupportActionBar(binding.toolbar);

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.miEventsFeed:
                    default:
                        fragment = new EventsFeedFragment();
                        break;
                    case R.id.miPersonalEvents:
                        fragment = new PersonalEventsFragment();
                        break;
                    case R.id.miCreate:
                        fragment = new CreateFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        binding.bottomNavigation.setSelectedItemId(R.id.miEventsFeed);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            //Compose icon has been clicked
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

    public static void finishCreatingEvent() {
        binding.bottomNavigation.setSelectedItemId(R.id.miEventsFeed);
        Toast.makeText(context, "Event created!", Toast.LENGTH_SHORT).show();
    }
}