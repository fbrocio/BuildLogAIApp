package com.example.buildlogai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buildlogai.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class GalleryImageAdapter
        extends RecyclerView.Adapter<GalleryImageAdapter.ViewHolder> {

    private final Context context;
    private final List<String> images;

    public GalleryImageAdapter(
            Context context,
            List<String> images
    ) {

        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_gallery_image,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Glide.with(context)
                .load(images.get(position))
                .into(holder.photoView);
    }

    @Override
    public int getItemCount() {

        return images.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        PhotoView photoView;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            photoView =
                    itemView.findViewById(R.id.photoView);
        }
    }
}