package com.example.fbu_final_project.activities;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fbu_final_project.databinding.ActivityEventDetailsBinding;
import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;

public class EventDetailsActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 20;
    private static final String TAG = "EventDetailsActivity";
    Event event;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityEventDetailsBinding binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        event = Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));

        binding.tvAuthor.setText(String.format("Created by: %s", event.getAuthor()));
        binding.tvDescription.setText(event.getDescription());
        binding.tvName.setText(event.getName());
        binding.tvTime.setText(String.format("%s - %s", event.getStartTime(), event.getEndTime()));

        if (isSubscribed()) {
            binding.btnSubscribe.setText("Unsubscribe");
        } else {
            binding.btnSubscribe.setText("Subscribe");
        }

        binding.btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = (User) ParseUser.getCurrentUser();

                if (isSubscribed()) {

                    event.unsubscribe(user);
                    user.unsubscribe(event);
                    event.saveInBackground();
                    user.saveInBackground();

                    Toast.makeText(EventDetailsActivity.this, "Unsubscribed!",
                            Toast.LENGTH_SHORT).show();
                    binding.btnSubscribe.setText("Subscribe");
                } else {
                    event.subscribe(user);
                    user.subscribe(event);
                    event.saveInBackground();
                    user.saveInBackground();

                    Toast.makeText(EventDetailsActivity.this, "Subscribed!",
                            Toast.LENGTH_SHORT).show();
                    binding.btnSubscribe.setText("Unsubscribe");
                }
            }
        });

        binding.fabAddToCal.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                       .requestEmail()
                       .build();

               mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

               signIn();
           }
       });
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.i(TAG, account.toString());

            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    private boolean isSubscribed() {

        User user = ((User) ParseUser.getCurrentUser());

        ArrayList<Event> subs = user.getSubscriptions();
        for (Event sub : subs) {
            if (sub.getObjectId().equals(event.getObjectId())) {
                return true;
            }
        }
        return false;
    }
}