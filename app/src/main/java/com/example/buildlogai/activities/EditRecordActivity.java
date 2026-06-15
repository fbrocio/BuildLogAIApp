package com.example.buildlogai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

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
    private MaterialButton btnDeleteRecord;
    private MaterialAutoCompleteTextView actvType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);

        apiService = ApiClient.getClient(this).create(ApiService.class);
        
        initViews();
        
        record = getIntent().getParcelableExtra("record");

        btnDeleteRecord =
                findViewById(R.id.btnDeleteRecord);

        btnDeleteRecord.setOnClickListener(v -> {
            showDeleteDialog();
        });

        if (record != null) {
            populateFields();
            loadImages();
        } else {
            Toast.makeText(this, "Error: No se pudo cargar el registro", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String[] types = {
                "Avance",
                "Incidencia",
                "Pendiente"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        types
                );

        actvType.setAdapter(adapter);

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
        actvType = findViewById(R.id.actvType);
    }

    private void populateFields() {
        etTitle.setText(record.getTitle());
        etContent.setText(record.getDescription());
        actvType.setText(
                record.getType(),
                false
        );

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
        String newType =
                actvType.getText()
                        .toString()
                        .trim()
                        .toUpperCase();

        record.setType(newType);


        apiService.updateRecord(record.getId(), record).
                enqueue(new Callback<RecordDTO>()
                {
            @Override
            public void onResponse(Call<RecordDTO> call, Response<RecordDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditRecordActivity.this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response.code() == 403) {

                    Toast.makeText(
                            EditRecordActivity.this,
                            "No tienes permiso para editar este registro",
                            Toast.LENGTH_SHORT
                    ).show();

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

    private void showDeleteDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Eliminar registro")
                .setMessage(
                        "¿Deseas eliminar este registro? " +
                                "También se eliminarán las imágenes asociadas."
                )
                .setNegativeButton(
                        "Cancelar",
                        null
                )
                .setPositiveButton(
                        "Eliminar",
                        (dialog, which) -> {
                            deleteRecord();
                        }
                )
                .show();
    }
    private void deleteRecord() {

        apiService.deleteRecord(record.getId())
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(
                            Call<Void> call,
                            Response<Void> response
                    ) {
                        Log.e(
                                "DELETE_RECORD",
                                "Código: " + response.code()
                        );

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    EditRecordActivity.this,
                                    "Registro eliminado",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent resultIntent = new Intent();

                            resultIntent.putExtra(
                                    "RECORD_DELETED",
                                    true
                            );

                            setResult(
                                    RESULT_OK,
                                    resultIntent
                            );

                            finish();



                        } else if (response.code() == 403) {

                            Toast.makeText(
                                    EditRecordActivity.this,
                                    "No tienes permiso para modificar este registro",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {

                            Toast.makeText(
                                    EditRecordActivity.this,
                                    "Error eliminando registro",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Void> call,
                            Throwable t
                    ) {

                        t.printStackTrace();

                        Toast.makeText(
                                EditRecordActivity.this,
                                "Error de conexión",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
