package com.example.buildlogai.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buildlogai.ApiClient;
import com.example.buildlogai.ApiService;
import com.example.buildlogai.R;
import com.example.buildlogai.adapter.RecordImageAdapter;
import com.example.buildlogai.model.RecordDTO;
import com.example.buildlogai.model.RecordImageDTO;
import com.example.buildlogai.model.StructuredData;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordDetailActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ImageButton btnEdit;

    private Chip chipStatus;

    private TextView tvType;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvStructuredData;
    private TextView tvCreatedBy;
    private TextView tvDate;
    private FloatingActionButton btnAddImage;

    private RecyclerView rvImages;

    private ApiService apiService;

    private RecordDTO record;

    private Long recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record_detail);

        // INIT VIEWS

        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit);

        chipStatus = findViewById(R.id.chipStatus);

        tvType = findViewById(R.id.tvType);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvStructuredData = findViewById(R.id.tvStructuredData);
        tvCreatedBy = findViewById(R.id.tvCreatedBy);
        tvDate = findViewById(R.id.tvDate);


        rvImages = findViewById(R.id.rvImages);
        btnAddImage = findViewById(R.id.btnAddImage);

        // API

        apiService = ApiClient
                .getClient(this)
                .create(ApiService.class);

        // INTENT

        recordId = getIntent()
                .getLongExtra(
                        "RECORD_ID",
                        -1L
                );

        // BACK BUTTON

        btnBack.setOnClickListener(v -> finish());

        //AÑADIR IMAGEN

        btnAddImage.setOnClickListener(v -> openGallery());

        // STATUS CLICK

        chipStatus.setOnClickListener(v -> {

            toggleStatus();
        });

        // LOAD DATA

        loadRecord();

        btnEdit.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RecordDetailActivity.this,
                    EditRecordActivity.class
            );

            intent.putExtra(
                    "record",
                    record
            );

            startActivity(intent);
        });
    }

    private void openGallery() {

        Intent intent = new Intent(
                Intent.ACTION_PICK
        );

        intent.setType("image/*");

        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == 100
                && resultCode == RESULT_OK
                && data != null) {

            Uri imageUri = data.getData();

            if (imageUri != null) {

                uploadImage(imageUri);
            }
        }
    }

    private void uploadImage(Uri uri) {

        try {

            InputStream inputStream =
                    getContentResolver().openInputStream(uri);

            if (inputStream == null) {

                Toast.makeText(
                        this,
                        "No se pudo abrir imagen",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            File tempFile = new File(
                    getCacheDir(),
                    "upload_image.jpg"
            );

            OutputStream outputStream =
                    new FileOutputStream(tempFile);

            byte[] buffer = new byte[4096];

            int bytesRead;

            while ((bytesRead =
                    inputStream.read(buffer)) != -1) {

                outputStream.write(
                        buffer,
                        0,
                        bytesRead
                );
            }

            outputStream.close();
            inputStream.close();

            RequestBody requestFile =
                    RequestBody.create(
                            tempFile,
                            MediaType.parse("image/*")
                    );

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData(
                            "image",
                            tempFile.getName(),
                            requestFile
                    );

            apiService.uploadImage(
                    recordId,
                    body
            ).enqueue(new Callback<RecordImageDTO>() {

                @Override
                public void onResponse(
                        Call<RecordImageDTO> call,
                        Response<RecordImageDTO> response
                ) {

                    if (response.isSuccessful()) {

                        Toast.makeText(
                                RecordDetailActivity.this,
                                "Imagen subida",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                RecordDetailActivity.this,
                                "Error backend: "
                                        + response.code(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(
                        Call<RecordImageDTO> call,
                        Throwable t
                ) {

                    t.printStackTrace();

                    Toast.makeText(
                            RecordDetailActivity.this,
                            t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });

        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Error procesando imagen",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }



    private void loadRecord() {

        apiService.getRecordById(recordId)
                .enqueue(new Callback<RecordDTO>() {

                    @Override
                    public void onResponse(
                            Call<RecordDTO> call,
                            Response<RecordDTO> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            record = response.body();

                            populateUI();

                            loadImages();

                            updateStatusUI();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<RecordDTO> call,
                            Throwable t
                    ) {

                        t.printStackTrace();
                    }
                });
    }

    private void populateUI() {
        Log.d("DEBUG_RECORD", "createdBy raw = " + record.getCreatedBy());

        if (record.getCreatedBy() != null) {
            Log.d("DEBUG_RECORD", "createdBy name = "
                    + record.getCreatedBy().getName());
        }
        Log.d("DEBUG_RECORD", new Gson().toJson(record));
        Log.d("DEBUG_RECORD", new Gson().toJson(record));
        if (record.getCreatedBy() != null) {
            Log.d("DEBUG_RECORD", "name = " + record.getCreatedBy().getName());
        }
        tvType.setText(
                record.getType()
        );

        tvTitle.setText(
                record.getTitle()
        );

        tvDescription.setText(
                record.getDescription()
        );

        tvStructuredData.setText(
                buildStructuredDataText()
        );

        String author =
                record.getCreatedBy() != null
                        ? record.getCreatedBy().getName()
                        : "Usuario";
        tvCreatedBy.setText("👤 " + author);


        tvDate.setText("🕒 " + formatDetailDate(record.getCreatedAt()));
    }

    private String formatDetailDate(String createdAt) {
        if (createdAt == null) return "Sin fecha";

        try {
            // Parser para el formato ISO que viene del backend (ej: 2026-05-15T10:00:00)
            java.text.SimpleDateFormat parser = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US);
            java.util.Date date = parser.parse(createdAt);

            if (date == null) return createdAt;

            // Formateador para el estilo deseado: 15 MAYO 2026
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd MMMM yyyy", new java.util.Locale("es", "ES"));
            return formatter.format(date).toUpperCase();

        } catch (Exception e) {
            // Si falla el parseo, devolvemos el original para no dejar el campo vacío
            return createdAt;
        }
    }

    private void updateStatusUI() {

        boolean closed = "CERRADA".equalsIgnoreCase(
                record.getStatus()
        );

        if (closed) {

            chipStatus.setText("CERRADA");

            chipStatus.setTextColor(
                    Color.parseColor("#2E7D32")
            );

            chipStatus.setChipBackgroundColor(
                    ColorStateList.valueOf(
                            Color.parseColor("#E8F5E9")
                    )
            );

        } else {

            chipStatus.setText("ABIERTA");

            chipStatus.setTextColor(
                    Color.parseColor("#C62828")
            );

            chipStatus.setChipBackgroundColor(
                    ColorStateList.valueOf(
                            Color.parseColor("#FDECEC")
                    )
            );
        }
    }

    private void loadImages() {

        apiService.getImages(recordId)
                .enqueue(new Callback<List<RecordImageDTO>>() {

                    @Override
                    public void onResponse(
                            Call<List<RecordImageDTO>> call,
                            Response<List<RecordImageDTO>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<RecordImageDTO> images =
                                    response.body();

                            rvImages.setLayoutManager(
                                    new LinearLayoutManager(
                                            RecordDetailActivity.this,
                                            LinearLayoutManager.HORIZONTAL,
                                            false
                                    )
                            );

                            rvImages.setAdapter(
                                    new RecordImageAdapter(images)
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<RecordImageDTO>> call,
                            Throwable t
                    ) {

                        t.printStackTrace();
                    }
                });
    }

    private void toggleStatus() {

        if (record == null) {
            return;
        }

        String previousStatus = record.getStatus();

        boolean closed = "CERRADA".equalsIgnoreCase(
                previousStatus
        );

        if (closed) {

            record.setStatus("ABIERTA");

        } else {

            record.setStatus("CERRADA");
        }

        // UI instantánea

        updateStatusUI();

        Map<String, String> body = new HashMap<>();

        body.put(
                "status",
                record.getStatus()
        );

        apiService.updateRecordStatus(
                record.getId(),
                body
        ).enqueue(new Callback<RecordDTO>() {

            @Override
            public void onResponse(
                    Call<RecordDTO> call,
                    Response<RecordDTO> response
            ) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    record = response.body();

                } else {

                    // revertir cambios si backend falla

                    record.setStatus(previousStatus);

                    updateStatusUI();
                }
            }

            @Override
            public void onFailure(
                    Call<RecordDTO> call,
                    Throwable t
            ) {

                t.printStackTrace();

                // revertir cambios si falla red

                record.setStatus(previousStatus);

                updateStatusUI();
            }
        });
    }

    private String buildStructuredDataText() {

        StructuredData data = record.getStructuredData();

        if (data == null || data.isEmpty()) {

            return "Sin datos estructurados";
        }

        StringBuilder builder = new StringBuilder();

        if (data.getCompany() != null) {

            builder.append("Empresa: ")
                    .append(data.getCompany())
                    .append("\n");
        }

        if (data.getSubject() != null) {

            builder.append("Asunto: ")
                    .append(data.getSubject())
                    .append("\n");
        }

        if (data.getQuantity() != null) {

            builder.append("Cantidad: ")
                    .append(data.getQuantity());

            if (data.getUnit() != null) {

                builder.append(" ")
                        .append(data.getUnit());
            }

            builder.append("\n");
        }

        if (data.getDueDate() != null) {

            builder.append("Fecha límite: ")
                    .append(data.getDueDate())
                    .append("\n");
        }

        if (data.getPercentage() != null) {

            builder.append("Porcentaje: ")
                    .append(data.getPercentage())
                    .append("%\n");
        }

        if (data.getPrice() != null) {

            builder.append("Precio: ")
                    .append(data.getPrice())
                    .append(" €\n");
        }

        return builder.toString().trim();
    }
}