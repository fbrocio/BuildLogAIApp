package com.example.buildlogai.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buildlogai.ApiClient;
import com.example.buildlogai.ApiService;
import com.example.buildlogai.R;
import com.example.buildlogai.model.Project;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProjectActivity extends AppCompatActivity {

    private TextInputEditText editTextName;
    private TextInputEditText editTextDescription;
    private Button buttonSave;
    private ImageView btnBack;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        editTextName = findViewById(R.id.editTextProjectName);
        editTextDescription = findViewById(R.id.editTextProjectDescription);
        buttonSave = findViewById(R.id.buttonSaveProject);
        btnBack = findViewById(R.id.btnBack);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        buttonSave.setOnClickListener(v -> saveProject());

        // BOTÓN VOLVER
        btnBack.setOnClickListener(v -> finish());
    }

    private void saveProject() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("El nombre es obligatorio");
            return;
        }

        Project project = new Project();
        project.name = name;
        project.description = description;

        apiService.createProject(project).enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddProjectActivity.this, "Proyecto guardado", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(AddProjectActivity.this, "Error: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(AddProjectActivity.this, "Error desconocido", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            /*public void onResponse(Call<Project> call, Response<Project> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddProjectActivity.this, "Proyecto guardado", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddProjectActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }*/

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                Toast.makeText(AddProjectActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
