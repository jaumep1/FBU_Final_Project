package com.example.fbu_final_project.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.fbu_final_project.R;
import com.example.fbu_final_project.activities.MainActivity;
import com.example.fbu_final_project.adapters.EventsFeedAdapter;
import com.example.fbu_final_project.databinding.FragmentProfileBinding;
import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.Tag;
import com.example.fbu_final_project.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PersonalProfileFragment extends Fragment {

    private static final String TAG = "PersonalProfileFragment";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;


    FragmentProfileBinding binding;
    EventsFeedAdapter adapter;
    List<Event> events;
    List<Event> activeEvents;
    User user;
    boolean isAttendee;

    private File photoFile;
    public String photoFileName = "photo.jpg";


    public PersonalProfileFragment() {
        isAttendee = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        setUser();

        binding.tvName.setText(String.format("%s %s", user.getFirstname(), user.getLastname()));
        Glide.with(getContext()).load(user.getProfilePic().getUrl())
                .circleCrop()
                .into(binding.ivProfilePic);

        events = new ArrayList<>();
        activeEvents = new ArrayList<>();
        adapter = new EventsFeedAdapter(getContext(), activeEvents);
        binding.rvEvents.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.rvEvents.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(binding.rvEvents.getContext(),
                        manager.getOrientation());
        binding.rvEvents.addItemDecoration(dividerItemDecoration);
        try {
            loadEventsFromCacheData(MainActivity.getEvents());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        binding.switchProfileType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAttendee = !isAttendee;
                setUser();
                try {
                    loadEventsFromCacheData(MainActivity.getEvents());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.miSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterEvents(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                activeEvents.clear();
                activeEvents.addAll(events);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void filterEvents(String search) {

        activeEvents.clear();
        for (Event event : events) {
            if (event.getName().contains(search)) {
                activeEvents.add(event);
            } else if (event.getDescription().contains(search)) {
                activeEvents.add(event);
            } else if (event.getAuthor().contains(search)) {
                activeEvents.add(event);
            }
        }
        adapter.notifyDataSetChanged();
    }

    protected void setUser() {
        user = (User) ParseUser.getCurrentUser();
        binding.ivProfilePic.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onLongClick(View v) {
                launchCamera();
                return true;
            }
        });

        if (isAttendee) {
            binding.tvHeader.setText("Events You Are Going To:");
        } else {
            binding.tvHeader.setText("Events You Created:");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            getActivity().startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    @RequiresApi(api = Build.VERSION_CODES.M)
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void handleResult(int requestCode, int resultCode, Intent data) {
        Log.i("reached", "reached");
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                user.setProfilePic(new ParseFile(photoFile));
                user.saveInBackground(new SaveCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void done(ParseException e) {
                        Glide.with(getContext()).load(photoFile)
                                .circleCrop()
                                .into(binding.ivProfilePic);
                    }
                });
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void loadEventsFromCacheData(JSONArray jsonArray) throws JSONException {
        events.clear();
        activeEvents.clear();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);

            ArrayList<String> eventIds = new ArrayList<>();

            for (Event sub : user.getSubscriptions()) {
                eventIds.add(sub.getObjectId());
            }

            if (!isAttendee && jsonObject.getString("created_by").equals(user.getObjectId()) ||
                    (isAttendee && eventIds.contains(jsonObject.getString("objectID")))) {

                Event newEvent = new Event();
                newEvent.setObjectId(jsonObject.getString("objectID"));
                newEvent.setCreator(jsonObject.getString("created_by"));
                newEvent.setAuthor(jsonObject.getString("author"));
                newEvent.setName(jsonObject.getString("name"));
                newEvent.setDescription(jsonObject.getString("description"));
                newEvent.setPoster(jsonObject.getString("poster"));

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("eee MMM dd HH:mm:ss zzz yyyy");

                ZonedDateTime start = ZonedDateTime.parse(jsonObject.getString("startTime"), dtf);
                newEvent.setStartTime(Date.from(start.toInstant()));
                ZonedDateTime end = ZonedDateTime.parse(jsonObject.getString("endTime"), dtf);
                newEvent.setEndTime(Date.from(end.toInstant()));

                JSONArray tagsJsonArray = new JSONArray(jsonObject.getString("tags"));
                ArrayList<Tag> eventTagsFromJson = new ArrayList<>();
                for (int j = 0; j < tagsJsonArray.length(); j++) {
                    Tag newTag = new Tag();
                    newTag.setObjectId(tagsJsonArray.getString(j));
                    eventTagsFromJson.add(newTag);
                }
                newEvent.setTags(eventTagsFromJson);

                JSONArray attendeesJsonArray = new JSONArray(jsonObject.getString("attendees"));
                ArrayList<User> eventAttendeesFromJson = new ArrayList<>();
                for (int j = 0; j < attendeesJsonArray.length(); j++) {
                    User newUser = new User();
                    newUser.setObjectId(attendeesJsonArray.getString(j));
                    eventAttendeesFromJson.add(newUser);
                }
                newEvent.setAttendees(eventAttendeesFromJson);

                events.add(newEvent);
                activeEvents.add(newEvent);
                adapter.notifyDataSetChanged();
            }
        }
    }
}