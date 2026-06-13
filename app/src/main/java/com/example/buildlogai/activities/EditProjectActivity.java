package com.example.buildlogai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.buildlogai.ApiClient;
import com.example.buildlogai.ApiService;
import com.example.buildlogai.R;
import com.example.buildlogai.model.ProjectRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProjectActivity extends AppCompatActivity {

    private ApiService apiService;

    private ImageView btnBack;

    private TextInputEditText etProjectName;
    private TextInputEditText etProjectDescription;

    private MaterialButton btnSaveProject;
    private MaterialButton btnDeleteProject;

    private Long projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);

        projectId = getIntent()
                .getLongExtra("PROJECT_ID", -1L);

        String projectName =
                getIntent().getStringExtra("PROJECT_NAME");
        String projectDescription=
                getIntent().getStringExtra("PROJECT_DESCRIPTION");

        apiService =
                ApiClient.getClient(this)
                        .create(ApiService.class);

        btnBack = findViewById(R.id.btnBack);

        etProjectName =
                findViewById(R.id.etProjectName);

        etProjectDescription =
                findViewById(R.id.etProjectDescription);

        btnSaveProject =
                findViewById(R.id.btnSaveProject);

        btnDeleteProject =
                findViewById(R.id.btnDeleteProject);

        // PRECARGAR DATOS

        etProjectName.setText(projectName);
        etProjectDescription.setText(projectDescription);

        // BACK

        btnBack.setOnClickListener(v -> finish());

        // SAVE

        btnSaveProject.setOnClickListener(v -> {
            updateProject();
        });

        // DELETE

        btnDeleteProject.setOnClickListener(v -> {
            showDeleteDialog();
        });
    }

    private void updateProject() {

        String name = etProjectName
                .getText()
                .toString()
                .trim();

        String description = etProjectDescription
                .getText()
                .toString()
                .trim();

        if (name.isEmpty()) {

            etProjectName.setError(
                    "Introduce un nombre"
            );

            return;
        }

        ProjectRequest request =
                new ProjectRequest();

        request.setName(name);

        request.setDescription(description);

        apiService.updateProject(
                projectId,
                request
        ).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(
                    Call<Void> call,
                    Response<Void> response
            ) {

                if (response.isSuccessful()) {

                    Toast.makeText(
                            EditProjectActivity.this,
                            "Proyecto actualizado",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                } else {

                    Toast.makeText(
                            EditProjectActivity.this,
                            "Error actualizando proyecto",
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
            }
        });
    }
    private void showDeleteDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Eliminar proyecto")
                .setMessage(
                        "Esta acción no se puede deshacer."
                )
                .setNegativeButton(
                        "Cancelar",
                        null
                )
                .setPositiveButton(
                        "Eliminar",
                        (dialog, which) -> {
                            deleteProject();
                        }
                )
                .show();
    }

    private void deleteProject() {

        apiService.deleteProject(projectId)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(
                            Call<Void> call,
                            Response<Void> response
                    ) {

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    EditProjectActivity.this,
                                    "Proyecto eliminado",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent intent = new Intent(
                                    EditProjectActivity.this,
                                    MainActivity.class
                            );

                            intent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            );

                            startActivity(intent);
                            finish();

                        } else {

                            Toast.makeText(
                                    EditProjectActivity.this,
                                    "Error eliminando proyecto",
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
                                EditProjectActivity.this,
                                "Error de conexión",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
