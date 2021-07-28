package com.example.fbu_final_project.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.fbu_final_project.R;
import com.example.fbu_final_project.adapters.EventsFeedAdapter;
import com.example.fbu_final_project.databinding.ActivityMainBinding;
import com.example.fbu_final_project.fragments.CreateFragment;
import com.example.fbu_final_project.fragments.EventsFeedFragment;
import com.example.fbu_final_project.fragments.PersonalEventsFragment;
import com.example.fbu_final_project.fragments.PersonalProfileFragment;
import com.example.fbu_final_project.fragments.UserProfileFragment;
import com.example.fbu_final_project.models.DriveFile;
import com.example.fbu_final_project.models.User;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.r0adkll.slidr.Slidr;

import org.parceler.Parcels;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int SELECT_IMAGE_CODE = 20;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

    private Fragment activeFragment;

    final FragmentManager fragmentManager = getSupportFragmentManager();

    public static ActivityMainBinding binding;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Slidr.attach(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        context = this;

        setContentView(binding.getRoot());

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

                fragmentManager.beginTransaction().replace(binding.flContainer.getId(), fragment).commit();
                activeFragment = fragment;
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
        } else if (item.getItemId() == R.id.profile) {
            Log.d(TAG, "Profile button clicked");
            Fragment fragment = new PersonalProfileFragment();
            activeFragment = fragment;
            fragmentManager.beginTransaction().replace(binding.flContainer.getId(), fragment).commit();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_IMAGE_CODE) {
            DriveFile file = Parcels.unwrap(data.getParcelableExtra(DriveFile.class.getSimpleName()));
            if (activeFragment instanceof CreateFragment) {
                ((CreateFragment) activeFragment).selectedImage = file;
            }

            Toast.makeText(getApplicationContext(), "Image selected!",
                    Toast.LENGTH_SHORT).show();
        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            ((PersonalProfileFragment) activeFragment).handleResult(requestCode, resultCode, data);
        }
    }
}