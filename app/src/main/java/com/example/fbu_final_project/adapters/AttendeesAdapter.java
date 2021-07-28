package com.example.fbu_final_project.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_final_project.activities.EventDetailsActivity;
import com.example.fbu_final_project.databinding.ItemAttendeeBinding;
import com.example.fbu_final_project.fragments.UserProfileFragment;
import com.example.fbu_final_project.models.User;

import java.util.List;

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
                ((EventDetailsActivity) activity)
                        .loadProfile(new UserProfileFragment(attendees.get(position), true));
            }
        }
    }
}
