package com.example.fbu_final_project.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.fbu_final_project.adapters.EventsFeedAdapter;
import com.example.fbu_final_project.databinding.FragmentProfileBinding;
import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;


    FragmentProfileBinding binding;
    EventsFeedAdapter adapter;
    List<Event> events;
    User user;

    private File photoFile;
    public String photoFileName = "photo.jpg";


    public ProfileFragment() {
        // Required empty public constructor
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUser();

        binding.tvName.setText(String.format("%s %s", user.getFirstname(), user.getLastname()));
        Glide.with(getContext()).load(user.getProfilePic().getUrl())
                .circleCrop()
                .into(binding.ivProfilePic);

        events = new ArrayList<>();
        adapter = new EventsFeedAdapter(getContext(), events);
        binding.rvEvents.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.rvEvents.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(binding.rvEvents.getContext(),
                        manager.getOrientation());
        binding.rvEvents.addItemDecoration(dividerItemDecoration);
        queryEvents();

        binding.ivProfilePic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                launchCamera();
                return true;
            }
        });
    }

    protected void setUser() {
        user = (User) ParseUser.getCurrentUser();
    }

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
    public void handleResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                user.setProfilePic(new ParseFile(photoFile));
                user.saveInBackground(new SaveCallback() {
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

    private void queryEvents() {
        events.clear();

        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);

        query.include(Event.KEY_EVENT_NAME);
        query.include(Event.KEY_EVENT_DESCRIPTION);
        query.include(Event.KEY_AUTHOR);
        query.include(Event.KEY_START_TIME);
        query.include(Event.KEY_END_TIME);
        query.include(Event.KEY_IMAGE);

        query.whereEqualTo(Event.KEY_AUTHOR, String.format("%s %s", user.getFirstname(), user.getLastname()));

        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> feed, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting events", e);
                    return;
                }
                events.addAll(feed);
                adapter.notifyDataSetChanged();
            }
        });
    }
}