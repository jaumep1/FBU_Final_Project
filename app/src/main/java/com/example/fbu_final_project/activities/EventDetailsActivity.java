package com.example.fbu_final_project.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fbu_final_project.R;
import com.example.fbu_final_project.databinding.ActivityEventDetailsBinding;
import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.i(TAG, account.getGrantedScopes().toString());

            com.google.api.services.calendar.model.Event calItem = createCalItem(event);
            Log.i("waka", calItem.toPrettyString());


        } catch (ApiException | IOException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code", e);
        }
    }

    private com.google.api.services.calendar.model.Event createCalItem(Event e) {
        com.google.api.services.calendar.model.Event calItem =
                new com.google.api.services.calendar.model.Event()
                .setSummary(event.getName())
                .setDescription(event.getDescription());

        DateTime startDateTime = new DateTime(event.getStartTime());
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone(Calendar.getInstance().getTimeZone().toString());
        calItem.setStart(start);

        DateTime endDateTime = new DateTime(event.getEndTime());
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(Calendar.getInstance().getTimeZone().toString());
        calItem.setEnd(end);

        return calItem;

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