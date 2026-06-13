package com.example.buildlogai.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buildlogai.ApiClient;
import com.example.buildlogai.ApiService;
import com.example.buildlogai.R;
import com.example.buildlogai.adapter.EditImageAdapter;
import com.example.buildlogai.adapter.RecordImageAdapter;
import com.example.buildlogai.model.RecordDTO;
import com.example.buildlogai.model.RecordImageDTO;
import com.example.buildlogai.model.StructuredData;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditRecordActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etTitle, etContent;
    private MaterialButton btnCancel, btnSave;
    private RecyclerView rvEditImages;
    
    private RecordDTO record;
    private ApiService apiService;
    private List<RecordImageDTO> recordImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);

        apiService = ApiClient.getClient(this).create(ApiService.class);
        
        initViews();
        
        record = getIntent().getParcelableExtra("record");

        if (record != null) {
            populateFields();
            loadImages();
        } else {
            Toast.makeText(this, "Error: No se pudo cargar el registro", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        
        rvEditImages = findViewById(R.id.rvEditImages);
        rvEditImages.setLayoutManager(new GridLayoutManager(this, 3));
        
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
    }

    private void populateFields() {
        etTitle.setText(record.getTitle());
        etContent.setText(record.getDescription());

    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void loadImages() {
        apiService.getImages(record.getId()).enqueue(new Callback<List<RecordImageDTO>>() {
            @Override
            public void onResponse(Call<List<RecordImageDTO>> call, Response<List<RecordImageDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recordImages = response.body();
                    updateImagesUI();
                }
            }

            @Override
            public void onFailure(Call<List<RecordImageDTO>> call, Throwable t) {
                Log.e("API", "Error cargando imágenes", t);
            }
        });
    }

    private void updateImagesUI() {

        EditImageAdapter adapter = new EditImageAdapter(
                this,
                recordImages,
                this::showDeleteImageDialog
        );

        rvEditImages.setAdapter(adapter);
    }

    private void saveChanges() {
        String newTitle = etTitle.getText().toString().trim();
        String newContent = etContent.getText().toString().trim();

        if (newTitle.isEmpty() || newContent.isEmpty()) {
            Toast.makeText(this, "Título y contenido son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar Record
        record.setTitle(newTitle);
        record.setDescription(newContent);


        apiService.saveRecord(record).enqueue(new Callback<RecordDTO>() {
            @Override
            public void onResponse(Call<RecordDTO> call, Response<RecordDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditRecordActivity.this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditRecordActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecordDTO> call, Throwable t) {
                Toast.makeText(EditRecordActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteImageDialog(RecordImageDTO image) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar imagen")
                .setMessage("¿Estás seguro de que quieres eliminar esta imagen?")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteImage(image))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteImage(RecordImageDTO image) {
        apiService.deleteImage(image.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditRecordActivity.this, "Imagen eliminada", Toast.LENGTH_SHORT).show();
                    loadImages(); // Recargar
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditRecordActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
