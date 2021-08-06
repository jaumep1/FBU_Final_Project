package com.example.fbu_final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fbu_final_project.activities.LoginActivity;
import com.example.fbu_final_project.databinding.ActivityPasswordRecoveryBinding;
import com.example.fbu_final_project.models.User;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.r0adkll.slidr.Slidr;
import com.royrodriguez.transitionbutton.TransitionButton;

public class PasswordRecoveryActivity extends LoginActivity {

    ActivityPasswordRecoveryBinding binding;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Slidr.attach(this);

        binding = ActivityPasswordRecoveryBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the loading animation when the user tap the button
                binding.btnLogin.startAnimation();
                String username = binding.etUsername.getText().toString();
                String answer = binding.etSecurity.getText().toString();

                final Handler handler = new Handler();
                loginUser(username, password);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Choose a stop animation if your call was succesful or not
                        boolean isSuccessful = (ParseUser.getCurrentUser() != null);
                        if (isSuccessful) {
                            binding.btnLogin.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                @Override
                                public void onAnimationStopEnd() {
                                    goMainActivity();
                                }
                            });
                        } else {
                            binding.btnLogin.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        }
                    }
                }, 2000);
            }
        });
    }
}