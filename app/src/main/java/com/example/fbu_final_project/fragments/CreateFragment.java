package com.example.fbu_final_project.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.fbu_final_project.R;

import android.text.format.DateFormat;
import java.util.Calendar;

public class CreateFragment extends Fragment {

    TextView tvStartDate;
    TextView tvEndDate;
    TextView tvStartTime;
    TextView tvEndTime;

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
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        tvStartTime = view.findViewById(R.id.tvStartTime);
        tvEndTime = view.findViewById(R.id.tvEndTime);
        setOnClickListeners();

    }

    private void setOnClickListeners() {
        tvStartTime.setOnClickListener(new View.OnClickListener() {

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
                                tvStartTime.setText(DateFormat.format("hh:mm aa", calendar));
                            }
                        }, 12, 0, false);

                timePickerDialog.updateTime(hour, min);
                timePickerDialog.show();
            }
        });

        tvEndTime.setOnClickListener(new View.OnClickListener() {

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
                                tvEndTime.setText(DateFormat.format("hh:mm aa", calendar));
                            }
                        }, 12, 0, false);

                timePickerDialog.updateTime(hour, min);
                timePickerDialog.show();
            }
        });

        tvStartDate.setOnClickListener(new View.OnClickListener() {

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
                                tvStartDate.setText(DateFormat.format("MM/dd/yyyy", calendar));
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                //datePickerDialog.updateDate(year, month, day);
                datePickerDialog.show();
            }
        });

        tvEndDate.setOnClickListener(new View.OnClickListener() {

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
                                tvEndDate.setText(DateFormat.format("MM/dd/yyyy", calendar));
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                //datePickerDialog.updateDate(year, month, day);
                datePickerDialog.show();
            }
        });
    }
}