package com.example.fbu_final_project.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_final_project.activities.EventDetailsActivity;
import com.example.fbu_final_project.databinding.ItemEventBinding;
import com.example.fbu_final_project.models.Event;

import org.parceler.Parcels;

import java.util.List;

public class EventsFeedAdapter extends RecyclerView.Adapter<EventsFeedAdapter.ViewHolder> {
    Context context;
    List<Event> events;

    public EventsFeedAdapter (Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public EventsFeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemEventBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventsFeedAdapter.ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemEventBinding binding;

        public ViewHolder(ItemEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                Intent i = new Intent(context, EventDetailsActivity.class);
                i.putExtra(Event.class.getSimpleName(), Parcels.wrap(events.get(position)));
                context.startActivity(i);
            }
        }

        public void bind(Event event) {
            binding.tvName.setText(event.getName());
            binding.tvAuthor.setText(String.format("Created by: %s", event.getAuthor()));
            binding.tvDescription.setText(event.getDescription());
            binding.tvTime.setText(String.format("%s - %s", event.getStartTime(), event.getEndTime()));
            Glide.with(context).load(event.getPoster()).into(binding.ivPoster);
        }
    }
}
