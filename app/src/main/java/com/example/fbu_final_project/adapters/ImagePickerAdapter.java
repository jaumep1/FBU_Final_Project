package com.example.fbu_final_project.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_final_project.activities.ImagePickerActivity;
import com.example.fbu_final_project.databinding.ItemEventBinding;
import com.example.fbu_final_project.databinding.ItemImageBinding;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.ViewHolder> {

    Context context;
    List<String> files;

    public ImagePickerAdapter(Context context, List<String> files) {
        this.context = context;
        this.files = files;
        Log.i("wakaka", files.toString());
    }

    @NonNull
    @Override
    public ImagePickerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImagePickerAdapter.ViewHolder(ItemImageBinding.inflate(LayoutInflater.from(context),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImagePickerAdapter.ViewHolder holder, int position) {
        String file = files.get(position);
        holder.bind(file);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemImageBinding binding;

        public ViewHolder(ItemImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        public void bind(String file) {
            Glide.with(context)
                    .load(String.format(file))
                    .into(binding.ivImage);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
