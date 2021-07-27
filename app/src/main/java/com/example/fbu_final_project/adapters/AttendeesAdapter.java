package com.example.fbu_final_project.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_final_project.activities.EventDetailsActivity;
import com.example.fbu_final_project.activities.LoginActivity;
import com.example.fbu_final_project.activities.MainActivity;
import com.example.fbu_final_project.databinding.ItemAttendeeBinding;
import com.example.fbu_final_project.models.Event;
import com.example.fbu_final_project.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AttendeesAdapter extends RecyclerView.Adapter<AttendeesAdapter.ViewHolder> {

    Context context;
    List<User> attendees;
    Activity activity;

    public AttendeesAdapter(Context context, List<User> attendees, EventDetailsActivity activity) {
        this.context = context;
        this.attendees = attendees;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AttendeesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemAttendeeBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeesAdapter.ViewHolder holder, int position) {
        User attendee = attendees.get(position);
        holder.bind(attendee);
    }

    @Override
    public int getItemCount() {
        return attendees.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ItemAttendeeBinding binding;

        public ViewHolder(ItemAttendeeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        public void bind(User attendee) {
            Glide.with(context)
                    .load(attendee.getProfilePic().getUrl())
                    .circleCrop()
                    .into(binding.ivProfilePic);
            binding.tvName.setText(String.format("%s %s",
                    attendee.getFirstname(), attendee.getLastname()));
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra(User.class.getSimpleName(), Parcels.wrap(attendees.get(getAdapterPosition())));
                i.putExtra("attendeeStatus", true);
                activity.setResult(RESULT_OK, i);
                activity.finish();
            }
        }
    }
}
