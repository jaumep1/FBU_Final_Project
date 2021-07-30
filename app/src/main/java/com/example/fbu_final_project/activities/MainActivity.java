package com.example.fbu_final_project.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fbu_final_project.R;
import com.example.fbu_final_project.databinding.ActivityMainBinding;
import com.example.fbu_final_project.fragments.CreateFragment;
import com.example.fbu_final_project.fragments.EventsFeedFragment;
import com.example.fbu_final_project.fragments.PersonalEventsFragment;
import com.example.fbu_final_project.fragments.PersonalProfileFragment;
import com.example.fbu_final_project.models.DriveFile;
import com.example.fbu_final_project.models.Event;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.r0adkll.slidr.Slidr;

import org.json.JSONArray;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int SELECT_IMAGE_CODE = 20;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

    private android.app.Fragment activeFragment;

    final FragmentManager fragmentManager = getFragmentManager();

    public static ActivityMainBinding binding;
    public static Context context;

    public static boolean loaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaded = false;
        Slidr.attach(this);
        Log.i("waka", "created");
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
        } else if (item.getItemId() == R.id.miProfile) {
            Log.d(TAG, "Profile button clicked");
            Intent i = new Intent(context, DetailsActivity.class);
            context.startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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

    public static boolean cacheEvents(List<Event> events) throws IOException {
        String filename = "events";
        File file = new File(context.getCacheDir(), filename);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputWriter);
            boolean success = false;

            try {
                JSONArray jsonArray = new JSONArray();
                for (Event event : events) {
                    jsonArray.put(event.toJSON());
                }
                bufferedWriter.write(jsonArray.toString());
                success = true;
            } catch(Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    bufferedWriter.close();
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }

            return success;
        } catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static JSONArray getEvents() {
        String filename = "events";
        File file = new File(context.getCacheDir(), filename);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(inputreader);

            String line;
            StringBuilder text = new StringBuilder();

            try {
                while (( line = buffreader.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
            } catch (IOException e) {
                return null;
            }

            return new JSONArray(text.toString());
        } catch(Exception ex) {
            return null;
        }

    }
}