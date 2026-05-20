package com.example.buildlogai.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buildlogai.R;
import com.google.android.material.button.MaterialButton;

public class SummaryActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTitle, tvSummary;
    private MaterialButton btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvSummary = findViewById(R.id.tvSummary);
        btnDownload = findViewById(R.id.btnDownload);

        btnBack.setOnClickListener(v -> finish());

        btnDownload.setOnClickListener(v -> {
            Toast.makeText(this, "Descargando resumen...", Toast.LENGTH_SHORT).show();
        });

        // Cargar datos reales aquí
    }
}
