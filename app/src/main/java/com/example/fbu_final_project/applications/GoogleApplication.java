package com.example.fbu_final_project.applications;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fbu_final_project.activities.EventDetailsActivity;
import com.example.fbu_final_project.models.Event;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import static com.parse.Parse.getApplicationContext;

public class GoogleApplication {

    private static final String TAG = "GoogleApplication";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String APPLICATION_NAME = "FBU_Final_Project";

    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();


    public GoogleApplication() {}

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, Activity activity) throws IOException {
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
        if(!dir.isDirectory() || !dir.exists()){
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
            protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) {
                String url = (authorizationUrl.build());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                activity.startActivity(browserIntent);
            }
        };
        return ab.authorize("user");
    }

    public void createCalendarEvent(Activity activity, Event event) {

        requestWritePermissions(activity);

        final NetHttpTransport HTTP_TRANSPORT;
        try {
            HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
            com.google.api.services.calendar.Calendar service =
                    new com.google.api.services.calendar.Calendar.Builder(
                            HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT,
                            activity))
                            .setApplicationName(APPLICATION_NAME)
                            .build();

            com.google.api.services.calendar.model.Event calItem = createCalItem(event);
            Log.i("waka", calItem.toPrettyString());
            String calendarId = "primary";
            calItem = service.events().insert(calendarId, calItem).execute();
            System.out.printf("Event created: %s\n", calItem.getHtmlLink());
            Toast.makeText(getApplicationContext(), "Event added to calendar!",
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error in adding event to calendar",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void requestWritePermissions(Activity activity) {
        if (cannotWrite(activity)) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

        while (cannotWrite(activity)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.e(TAG, "error waiting", e);
            }

        }
    }

    private boolean cannotWrite(Activity activity) {
        return (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED);
    }

    private com.google.api.services.calendar.model.Event createCalItem(Event event) {
        com.google.api.services.calendar.model.Event calItem =
                new com.google.api.services.calendar.model.Event()
                        .setSummary(event.getName())
                        .setDescription(event.getDescription());

        DateTime startDateTime = new DateTime(event.getStartTime());
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime);
        calItem.setStart(start);

        DateTime endDateTime = new DateTime(event.getEndTime());
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime);
        calItem.setEnd(end);

        return calItem;
    }
}
