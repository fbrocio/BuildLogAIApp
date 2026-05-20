package com.example.buildlogai.activities;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.buildlogai.R;
import com.example.buildlogai.adapter.GalleryImageAdapter;

import java.util.ArrayList;

public class ImageGalleryActivity extends AppCompatActivity {

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_gallery);

        viewPager = findViewById(R.id.viewPager);
        ImageButton btnClose = findViewById(R.id.btnClose);

        ArrayList<String> imageUrls =
                getIntent().getStringArrayListExtra("IMAGES");

        int startPosition =
                getIntent().getIntExtra("POSITION", 0);

        GalleryImageAdapter adapter =
                new GalleryImageAdapter(
                        this,
                        imageUrls
                );

        viewPager.setAdapter(adapter);

        btnClose.setOnClickListener(v -> finish());

        viewPager.setCurrentItem(startPosition, false);
    }
}