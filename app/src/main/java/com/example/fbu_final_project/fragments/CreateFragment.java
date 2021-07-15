package com.example.fbu_final_project.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.fbu_final_project.R;
import com.example.fbu_final_project.databinding.FragmentCreateBinding;
import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.text.format.DateFormat;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateFragment extends Fragment {

    private static final String TAG = "CreateFragment";

    FragmentCreateBinding binding;

    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnClickListeners();

    }

    private void setOnClickListeners() {

        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (fieldsInputted()) {
                    createEvent();
                } else {
                    Toast.makeText(getContext(), "Please input all text fields!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.tvStartTime.setOnClickListener(new View.OnClickListener() {

            int hour;
            int min;

            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar calendar = Calendar.getInstance();
                                hour = hourOfDay;
                                min = minute;
                                calendar.set(0,0,0, hourOfDay, minute);
                                binding.tvStartTime.setText(DateFormat.format("hh:mm aa", calendar));
                            }
                        }, 12, 0, false);

                timePickerDialog.updateTime(hour, min);
                timePickerDialog.show();
            }
        });

        binding.tvEndTime.setOnClickListener(new View.OnClickListener() {

            int hour;
            int min;

            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar calendar = Calendar.getInstance();
                                hour = hourOfDay;
                                min = minute;
                                calendar.set(0,0,0, hourOfDay, minute);
                                binding.tvEndTime.setText(DateFormat.format("hh:mm aa", calendar));
                            }
                        }, 12, 0, false);

                timePickerDialog.updateTime(hour, min);
                timePickerDialog.show();
            }
        });

        binding.tvStartDate.setOnClickListener(new View.OnClickListener() {

            int year;
            int month;
            int day;

            Calendar c = Calendar.getInstance();

            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int yearNumber, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                year = yearNumber;
                                month = monthOfYear;
                                day = dayOfMonth;
                                calendar.set(year, month, day);
                                binding.tvStartDate.setText(DateFormat.format("MM/dd/yyyy", calendar));
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                //datePickerDialog.updateDate(year, month, day);
                datePickerDialog.show();
            }
        });

        binding.tvEndDate.setOnClickListener(new View.OnClickListener() {

            int year;
            int month;
            int day;

            Calendar c = Calendar.getInstance();

            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int yearNumber, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                year = yearNumber;
                                month = monthOfYear;
                                day = dayOfMonth;
                                calendar.set(year, month, day);
                                binding.tvEndDate.setText(DateFormat.format("MM/dd/yyyy", calendar));
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                //datePickerDialog.updateDate(year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private boolean fieldsInputted() {
        if (binding.etEventName.getText().toString().equals("")) {
            return false;
        }
        if (binding.etDescription.getText().toString().equals("")) {
            return false;
        }
        if (binding.tvStartDate.getText().toString().equals("START DATE")) {
            return false;
        }
        if (binding.tvStartTime.getText().toString().equals("START TIME")) {
            return false;
        }
        if (binding.tvEndDate.getText().toString().equals("END DATE")) {
            return false;
        }
        if (binding.tvEndTime.getText().toString().equals("END TIME")) {
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createEvent() {
        Event event = new Event();
        String fullname = String.format("%s %s", ((User) ParseUser.getCurrentUser()).getFirstname(),
                ((User) ParseUser.getCurrentUser()).getLastname());
        event.setAuthor(fullname);
        event.setCreator(ParseUser.getCurrentUser().getObjectId());
        event.setName(binding.etEventName.getText().toString());
        event.setDescription(binding.etDescription.getText().toString());

        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("hh:mm a");

        LocalDate startDate = LocalDate.parse(binding.tvStartDate.getText().toString(), df);
        LocalTime startTime = LocalTime.parse(binding.tvStartTime.getText().toString(), tf);
        ZonedDateTime start = LocalDateTime.of(startDate, startTime)
                .atZone(ZoneId.systemDefault());
        event.setStartTime(Date.from(start.toInstant()));

        LocalDate endDate = LocalDate.parse(binding.tvEndDate.getText().toString(), df);
        LocalTime endTime = LocalTime.parse(binding.tvEndTime.getText().toString(), tf);
        ZonedDateTime end = LocalDateTime.of(endDate, endTime)
                .atZone(ZoneId.systemDefault());
        event.setEndTime(Date.from(end.toInstant()));

        event.createAttendees();

        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG ,"Error while saving event", e);
                    Toast.makeText(getContext(), "Error while saving!",
                            Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Event save was successful!");
                Toast.makeText(getContext(), "Event created!",
                        Toast.LENGTH_SHORT).show();
                binding.etEventName.setText("");
                binding.etDescription.setText("");
                binding.tvStartTime.setText("START TIME");
                binding.tvStartDate.setText("START DATE");
                binding.tvEndTime.setText("END TIME");
                binding.tvEndDate.setText("END DATE");
            }
        });

    }
}