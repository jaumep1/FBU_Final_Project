package com.example.fbu_final_project.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_final_project.databinding.ItemImageBinding;
import com.example.fbu_final_project.models.DriveFile;

import org.parceler.Parcels;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.ViewHolder> {

    Context context;
    List<DriveFile> files;

    public ImagePickerAdapter(Context context, List<DriveFile> files) {
        this.context = context;
        this.files = files;
    }

    @NonNull
    @Override
    public ImagePickerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImagePickerAdapter.ViewHolder(ItemImageBinding.inflate(LayoutInflater.from(context),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImagePickerAdapter.ViewHolder holder, int position) {
        DriveFile file = files.get(position);
        holder.bind(file);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemImageBinding binding;

        public ViewHolder(ItemImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DriveFile file) {
            Glide.with(context)
                    .load(file.getThumbnail())
                    .into(binding.ivImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.putExtra(DriveFile.class.getSimpleName(), Parcels.wrap(file));
                    ((Activity) context).setResult(RESULT_OK, i);
                    ((Activity) context).finish();
                }
            });
        }
    }
}
