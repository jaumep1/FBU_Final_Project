package com.example.fbu_final_project.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fbu_final_project.databinding.ActivitySignupBinding;
import com.example.fbu_final_project.models.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.r0adkll.slidr.Slidr;
import com.royrodriguez.transitionbutton.TransitionButton;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Slidr.attach(this);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the loading animation when the user tap the button
                binding.btnSignup.startAnimation();

                // Do your networking task or background work here.
                Log.i(TAG, "onClick signup button");
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                String firstname = binding.etFirstname.getText().toString();
                String lastname = binding.etLastname.getText().toString();
                String securityQuestion = binding.etSecurity.getText().toString();

                if (fieldsInputted()) {
                    signUp(username, password, firstname, lastname, securityQuestion);
                }

                final Handler handler = new Handler();
                loginUser(username, password);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Choose a stop animation if your call was succesful or not
                        boolean isSuccessful = (ParseUser.getCurrentUser() != null);
                        if (isSuccessful) {
                            binding.btnSignup.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                @Override
                                public void onAnimationStopEnd() {
                                    goMainActivity();
                                }
                            });
                        } else {
                            binding.btnSignup.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                            if (fieldsInputted()) {
                                Toast.makeText(getApplicationContext(), "Error on sign up!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Please input all fields!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, 3000);
            }
        });
    }

    private void signUp(String username, String password, String firstname, String lastname, String securityQuestion) {
        // Create the ParseUser
        User user = new User();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setSecurityQuestion(securityQuestion);
        user.createSubs();

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    Toast.makeText(SignupActivity.this, "Success!",
                            Toast.LENGTH_SHORT).show();
                    loginUser(username, password);
                } else {
                    Log.e(TAG, "Sign up failed", e);
                }
            }
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user: " + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue on login", e);
                    return;
                }
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private boolean fieldsInputted() {
        if (binding.etUsername.getText().toString().equals("")) {
            return false;
        }
        if (binding.etPassword.getText().toString().equals("")) {
            return false;
        }
        if (binding.etFirstname.getText().toString().equals("")) {
            return false;
        }
        if (binding.etLastname.getText().toString().equals("")) {
            return false;
        }
        if (binding.etSecurity.getText().toString().equals("")) {
            return false;
        }
        return true;
    }
}