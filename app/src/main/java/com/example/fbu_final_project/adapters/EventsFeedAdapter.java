package com.example.fbu_final_project.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_final_project.R;
import com.example.fbu_final_project.databinding.ItemEventBinding;
import com.example.fbu_final_project.models.Event;

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
        Log.i("waka", "reached");
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        Log.i("waka", String.valueOf(events.size()));
        return events.size();
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
            Log.i("waka", "Postion clicked: " + getAdapterPosition());

        }

        public void bind(Event event) {
            binding.tvName.setText(event.getName());
            binding.tvAuthor.setText(String.format("Created by: %s", event.getAuthor()));
            binding.tvDescription.setText(event.getDescription());
            binding.tvTime.setText(String.format("%s - %s", event.getStartTime(), event.getEndTime()));
        }
    }
}
