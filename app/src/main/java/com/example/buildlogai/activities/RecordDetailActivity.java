package com.example.buildlogai.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buildlogai.ApiClient;
import com.example.buildlogai.ApiService;
import com.example.buildlogai.R;
import com.example.buildlogai.adapter.RecordImageAdapter;
import com.example.buildlogai.model.ImageResponse;
import com.example.buildlogai.model.RecordDTO;
import com.example.buildlogai.model.RecordImageDTO;
import com.example.buildlogai.model.StructuredData;
import com.google.android.material.chip.Chip;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private TextView tvType, tvTitle, tvDescription, tvCreatedBy, tvDate;
    private FloatingActionButton btnAddImage;
    private RecyclerView rvImages;

    private ApiService apiService;
    private RecordDTO record;
    private Long recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        initViews();
        apiService = ApiClient.getClient(this).create(ApiService.class);

        recordId = getIntent().getLongExtra("RECORD_ID", -1L);

        btnBack.setOnClickListener(v -> finish());
        btnAddImage.setOnClickListener(v -> openGallery());
        chipStatus.setOnClickListener(v -> toggleStatus());
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditRecordActivity.class);
            intent.putExtra("record", record);
            startActivity(intent);
        });

        loadRecord();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit);
        chipStatus = findViewById(R.id.chipStatus);
        tvType = findViewById(R.id.tvType);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvCreatedBy = findViewById(R.id.tvCreatedBy);
        tvDate = findViewById(R.id.tvDate);
        rvImages = findViewById(R.id.rvImages);
        btnAddImage = findViewById(R.id.btnAddImage);


    }

    private void populateUI() {
        tvType.setText(record.getType());
        tvTitle.setText(record.getTitle());
        tvDescription.setText(record.getDescription());
        
        String author = record.getCreatedBy()!= null ? record.getCreatedBy().getName() :"Usuario";
        tvCreatedBy.setText("👤 Por " + author);
        tvDate.setText("🕒 " + formatDetailDate(record.getCreatedAt()));
    }


    /**
     * Configura visualmente una fila de la tabla.
     * Si el valor existe: Colores normales (negro/gris oscuro).
     * Si el valor es nulo/vacío: Colores claros (gris suave) y muestra "-".
     */
    private void setupTableRow(View row, TextView valueTextView, String value) {

        row.setVisibility(View.VISIBLE);

        TextView labelTextView = null;

        if (row instanceof TableRow && ((TableRow) row).getChildCount() > 0) {
            labelTextView = (TextView) ((TableRow) row).getChildAt(0);
        }

        // Colores dinámicos del tema Material 3
        int activeColor = MaterialColors.getColor(
                row,
                com.google.android.material.R.attr.colorOnSurface
        );

        int secondaryColor = MaterialColors.getColor(
                row,
                com.google.android.material.R.attr.colorOnSurfaceVariant
        );

        valueTextView.setAlpha(1f);

        // Estado ACTIVO: hay información
        if (value != null && !value.trim().isEmpty()) {

            // Etiqueta más suave
            if (labelTextView != null) {
                labelTextView.setTextColor(secondaryColor);
            }

            // Valor principal más visible
            valueTextView.setTextColor(activeColor);

            valueTextView.setText(value);

        } else {

            // Estado INACTIVO: sin información
            if (labelTextView != null) {
                labelTextView.setTextColor(secondaryColor);
            }

            valueTextView.setTextColor(secondaryColor);

            valueTextView.setAlpha(0.45f);

            valueTextView.setText("-");
        }
    }

    private void loadRecord() {
        apiService.getRecordById(recordId).enqueue(new Callback<RecordDTO>() {
            @Override
            public void onResponse(Call<RecordDTO> call, Response<RecordDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    record = response.body();
                    populateUI();
                    loadImages();
                    updateStatusUI();
                }
            }
            @Override
            public void onFailure(Call<RecordDTO> call, Throwable t) {
                Log.e("API", "Error al cargar record", t);
            }
        });
    }

    private String formatDetailDate(String createdAt) {
        if (createdAt == null) return "Sin fecha";
        try {
            java.text.SimpleDateFormat parser = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US);
            java.util.Date date = parser.parse(createdAt);
            if (date == null) return createdAt;
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd MMMM yyyy", new java.util.Locale("es", "ES"));
            return formatter.format(date).toUpperCase();
        } catch (Exception e) {
            return createdAt;
        }
    }

    private void updateStatusUI() {
        boolean closed = "CERRADA".equalsIgnoreCase(record.getStatus());
        chipStatus.setText(closed ? "CERRADA" : "ABIERTA");
        chipStatus.setTextColor(Color.parseColor(closed ? "#2E7D32" : "#C62828"));
        chipStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor(closed ? "#E8F5E9" : "#FDECEC")));
    }

    private void loadImages() {
        apiService.getImages(recordId).enqueue(new Callback<List<RecordImageDTO>>() {
            @Override
            public void onResponse(Call<List<RecordImageDTO>> call, Response<List<RecordImageDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rvImages.setLayoutManager(new LinearLayoutManager(RecordDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    rvImages.setAdapter(new RecordImageAdapter(response.body()));
                }
            }
            @Override
            public void onFailure(Call<List<RecordImageDTO>> call, Throwable t) {}
        });
    }

    private void toggleStatus() {
        if (record == null) return;
        String newStatus = "CERRADA".equalsIgnoreCase(record.getStatus()) ? "ABIERTA" : "CERRADA";
        Map<String, String> body = new HashMap<>();
        body.put("status", newStatus);
        apiService.updateRecordStatus(recordId, body).enqueue(new Callback<RecordDTO>() {
            @Override
            public void onResponse(Call<RecordDTO> call, Response<RecordDTO> response) {
                if (response.isSuccessful()) {
                    record.setStatus(newStatus);
                    updateStatusUI();
                }
            }
            @Override
            public void onFailure(Call<RecordDTO> call, Throwable t) {}
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) uploadImage(imageUri);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (recordId != null && recordId > 0) {
            loadRecord();
        }
    }

    private void uploadImage(Uri uri) {

        try {

            InputStream inputStream =
                    getContentResolver().openInputStream(uri);

            File tempFile = File.createTempFile(
                    "upload_",
                    ".jpg",
                    getCacheDir()
            );

            OutputStream outputStream =
                    new FileOutputStream(tempFile);

            byte[] buffer = new byte[4096];

            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {

                outputStream.write(buffer, 0, bytesRead);
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

            apiService.uploadImage(recordId, body)
                    .enqueue(new Callback<ImageResponse>() {

                        @Override
                        public void onResponse(
                                Call<ImageResponse> call,
                                Response<ImageResponse> response
                        ) {

                            if (response.isSuccessful()) {

                                Toast.makeText(
                                        RecordDetailActivity.this,
                                        "Imagen subida",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadImages();
                            }
                        }

                        @Override
                        public void onFailure(
                                Call<ImageResponse> call,
                                Throwable t
                        ) {

                            t.printStackTrace();

                            Toast.makeText(
                                    RecordDetailActivity.this,
                                    "Error subiendo imagen",
                                    Toast.LENGTH_SHORT
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
}
