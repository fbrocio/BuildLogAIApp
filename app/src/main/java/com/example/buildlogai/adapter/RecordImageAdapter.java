package com.example.buildlogai.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.buildlogai.ApiClient;
import com.example.buildlogai.R;
import com.example.buildlogai.activities.ImageGalleryActivity;
import com.example.buildlogai.model.RecordImageDTO;

import java.util.ArrayList;
import java.util.List;

public class RecordImageAdapter
        extends RecyclerView.Adapter<RecordImageAdapter.ImageViewHolder> {

    private final List<RecordImageDTO> images;
    private OnImageClickListener listener;

    public interface OnImageClickListener {
        void onImageClick(RecordImageDTO image, int position);
        void onImageLongClick(RecordImageDTO image, int position);
    }

    public RecordImageAdapter(List<RecordImageDTO> images) {
        this.images = images;
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_record_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        RecordImageDTO image = images.get(position);
        Context context = holder.itemView.getContext();

        String baseUrl = ApiClient.BASE_URL; 
        String imagePath = image.getImageUrl(); 

        if (baseUrl.endsWith("/") && imagePath.startsWith("/")) {
            imagePath = imagePath.substring(1);
        } else if (!baseUrl.endsWith("/") && !imagePath.startsWith("/")) {
            imagePath = "/" + imagePath;
        }

        String imageUrl = baseUrl + imagePath;

        holder.imageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageClick(image, position);
            } else {
                // Comportamiento por defecto: abrir galería
                ArrayList<String> urls = new ArrayList<>();
                for (RecordImageDTO img : images) {
                    String path = img.getImageUrl();
                    if (baseUrl.endsWith("/") && path.startsWith("/")) {
                        path = path.substring(1);
                    } else if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
                        path = "/" + path;
                    }
                    urls.add(baseUrl + path);
                }

                Intent intent = new Intent(context, ImageGalleryActivity.class);
                intent.putStringArrayListExtra("IMAGES", urls);
                intent.putExtra("POSITION", position);
                context.startActivity(intent);
            }
        });

        holder.imageView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onImageLongClick(image, position);
                return true;
            }
            return false;
        });

        SharedPreferences prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token != null) {
            GlideUrl glideUrl = new GlideUrl(imageUrl,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build());

            Glide.with(context)
                    .load(glideUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.stat_notify_error)
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.stat_notify_error)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgRecord);
        }
    }
}
