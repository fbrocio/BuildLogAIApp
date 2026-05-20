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

    public RecordImageAdapter(List<RecordImageDTO> images) {
        this.images = images;
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

        // 1. USAR SIEMPRE LA CONSTANTE DE ApiClient
        String baseUrl = ApiClient.BASE_URL; 
        String imagePath = image.getImageUrl(); // Debería ser "/upload/records/uuid.jpg"

        // Limpiar URL para evitar dobles barras //
        if (baseUrl.endsWith("/") && imagePath.startsWith("/")) {
            imagePath = imagePath.substring(1);
        } else if (!baseUrl.endsWith("/") && !imagePath.startsWith("/")) {
            imagePath = "/" + imagePath;
        }

        String imageUrl = baseUrl + imagePath;
        Log.d("IMAGE_URL", "Cargando en Glide: " + imageUrl);

        holder.imageView.setOnClickListener(v -> {

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

            Intent intent = new Intent(
                    context,
                    ImageGalleryActivity.class
            );

            intent.putStringArrayListExtra(
                    "IMAGES",
                    urls
            );

            intent.putExtra(
                    "POSITION",
                    position
            );

            context.startActivity(intent);
        });

        // 2. Obtener el Token
        SharedPreferences prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token != null) {
            // 3. Cargar con GlideUrl para enviar el token de seguridad
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
            // Carga normal si no hay token (fallará si el backend lo requiere)
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
