package com.example.fbu_final_project.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fbu_final_project.databinding.ActivityEventDetailsBinding;
import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.Tag;
import com.example.fbu_final_project.models.User;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventDateTime;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class EventDetailsActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 20;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String TAG = "EventDetailsActivity";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

//    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
//    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";




//    GoogleAccountCredential credential;
//    com.google.api.services.calendar.Calendar client;

    Event event;



//    GoogleSignInOptions gso;
//    GoogleSignInClient mGoogleSignInClient;

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
           @RequiresApi(api = Build.VERSION_CODES.O)
           @Override
           public void onClick(View v) {

               if (cannotWrite()) {
                   ActivityCompat.requestPermissions(EventDetailsActivity.this,
                           new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                           1);
               }

               while (cannotWrite()) {
                   try {
                       Thread.sleep(100);
                   } catch (InterruptedException e) {
                       Log.e(TAG, "error waiting", e);
                   }

               }

               final NetHttpTransport HTTP_TRANSPORT;
               try {
                   HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
                   com.google.api.services.calendar.Calendar service =
                           new com.google.api.services.calendar.Calendar.Builder(
                                   HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                           .setApplicationName(APPLICATION_NAME)
                           .build();

                   com.google.api.services.calendar.model.Event calItem = createCalItem(event);
                   String calendarId = "primary";
                   calItem = service.events().insert(calendarId, calItem).execute();
                   System.out.printf("Event created: %s\n", calItem.getHtmlLink());
//            Log.i("waka", calItem.toPrettyString());
               } catch (IOException e) {
                   e.printStackTrace();
               }


//
//            account.requestExtraScopes();
//
//



//               gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                       .requestScopes(new Scope(Scopes.DRIVE_APPS))
//                       .requestEmail()
//                       .build();
//
//               mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
//
//               signIn();
           }
       });
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = EventDetailsActivity.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        File fsd = Environment.getExternalStorageDirectory();
        String filePath = fsd.getAbsolutePath()  +
                File.separator + TOKENS_DIRECTORY_PATH;

        File dir = new File(filePath);
        if(dir.isDirectory()==false || !dir.exists()){
            dir.mkdirs();
        }
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(dir))
                .setAccessType("offline")
                .build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        AuthorizationCodeInstalledApp ab = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()){
            protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
                String url = (authorizationUrl.build());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        };
        return ab.authorize("user");
    }

    private boolean cannotWrite() {
        return (ContextCompat.checkSelfPermission(EventDetailsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(EventDetailsActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED);
    }
//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            Log.i(TAG, account.getGrantedScopes().toString());
//
//            com.google.api.services.calendar.model.Event calItem = createCalItem(event);
//
//            account.requestExtraScopes();
//
//
//            String calendarId = "primary";
//            //calItem = service.events().insert(calendarId, calItem).execute();
//            System.out.printf("Event created: %s\n", calItem.getHtmlLink());
//            Log.i("waka", calItem.toPrettyString());
//
//
//        } catch (ApiException | IOException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.e(TAG, "signInResult:failed code", e);
//        }
//    }

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