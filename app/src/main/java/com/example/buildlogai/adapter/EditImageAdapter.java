package com.example.buildlogai.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buildlogai.R;
import com.example.buildlogai.model.RecordImageDTO;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class EditImageAdapter
        extends RecyclerView.Adapter<EditImageAdapter.ViewHolder> {

    private final Context context;
    private final List<RecordImageDTO> images;
    private final OnDeleteClickListener deleteListener;

    public EditImageAdapter(
            Context context,
            List<RecordImageDTO> images,
            OnDeleteClickListener deleteListener
    ) {

        this.context = context;
        this.images = images;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_edit_record_image,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    /*@Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Glide.with(context)
                .load(images.get(position))
                .into(holder.photoView);
    }*/
    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        RecordImageDTO image = images.get(position);

        Log.d("GALLERY_URL", image.getImageUrl());

        Glide.with(context)
                .load(image.getImageUrl())
                .into(holder.imgRecord);

        holder.btnDeleteImage.setOnClickListener(v ->
                deleteListener.onDeleteClick(image));
    }

    @Override
    public int getItemCount() {

        return images.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgRecord;
        ImageButton btnDeleteImage;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            imgRecord =
                    itemView.findViewById(R.id.imgRecord);

            btnDeleteImage =
                    itemView.findViewById(R.id.btnDeleteImage);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(RecordImageDTO image);
    }


}