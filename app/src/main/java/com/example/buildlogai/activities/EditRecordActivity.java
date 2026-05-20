package com.example.buildlogai.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buildlogai.ApiClient;
import com.example.buildlogai.ApiService;
import com.example.buildlogai.R;
import com.example.buildlogai.model.RecordDTO;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditRecordActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etTitle, etContent;
    private MaterialButton btnCancel, btnSave;
    private RecordDTO record;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);

        // 1. Inicializar vistas (etTitle estaba en el XML pero no en el Java)
        btnBack = findViewById(R.id.btnBack);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        // 2. Obtener el record del Intent
        record = getIntent().getParcelableExtra("record");

        if (record != null) {
            // Rellenar campos con la información actual
            etTitle.setText(record.getTitle());
            etContent.setText(record.getDescription());
        } else {
            Toast.makeText(this, "Error: No se pudo cargar el registro", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 3. Inicializar API
        apiService = ApiClient.getClient(this).create(ApiService.class);

        // 4. Configurar Listeners
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        String newTitle = etTitle.getText().toString().trim();
        String newContent = etContent.getText().toString().trim();

        if (newTitle.isEmpty() || newContent.isEmpty()) {
            Toast.makeText(this, "Título y contenido son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar el objeto local
        record.setTitle(newTitle);
        record.setDescription(newContent);

        // Llamada a la API para persistir los cambios
        // Nota: Usamos saveRecord asumiendo que el backend gestiona la actualización si el objeto tiene ID
        apiService.saveRecord(record).enqueue(new Callback<RecordDTO>() {
            @Override
            public void onResponse(Call<RecordDTO> call, Response<RecordDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditRecordActivity.this, "Cambios guardados con éxito", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("API", "Error al actualizar: " + response.code());
                    Toast.makeText(EditRecordActivity.this, "Error al guardar cambios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecordDTO> call, Throwable t) {
                Log.e("API", "Fallo de red", t);
                Toast.makeText(EditRecordActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}