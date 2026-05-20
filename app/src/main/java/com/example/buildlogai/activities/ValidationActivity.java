package com.example.buildlogai.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buildlogai.ApiClient;
import com.example.buildlogai.ApiService;
import com.example.buildlogai.R;
import com.example.buildlogai.adapter.RecordValidationAdapter;
import com.example.buildlogai.model.RecordDTO;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ValidationActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvDate, tvContent;
    private MaterialButton btnEdit, btnDelete;
    private RecordDTO record;
    private ArrayList<RecordDTO> records;
    private RecordValidationAdapter adapter;
    private ApiService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);

        btnBack = findViewById(R.id.btnBack);

        records = getIntent().
                getParcelableArrayListExtra("RECORDS");

        Long projectId = getIntent().getLongExtra("PROJECT_ID", -1L);

        if (records == null || records.isEmpty()) {
            // Manejar error de forma explícita
            finish();
            return;
        }
        apiService = ApiClient
                .getClient(this)
                .create(ApiService.class);

        //Referenciar RecyclerView
        RecyclerView rv = findViewById(R.id.rvRecords);

        //LayoutManager
        rv.setLayoutManager(new LinearLayoutManager(this));

        //Adapter
        adapter =
                new RecordValidationAdapter(records, new RecordValidationAdapter.OnRecordActionListener() {

                    @Override
                    public void onAccept(RecordDTO record, int position) {
                        // Asegurar projectId
                        record.setProjectId(projectId);

                        // DEBUG
                        Log.d("DEBUG", "Enviando record:");
                        Log.d("DEBUG", "Title: " + record.getTitle());
                        Log.d("DEBUG", "ProjectId: " + record.getProjectId());
                        Log.d("DEBUG", "Type: " + record.getType());
                        Log.d("DEBUG", "Status: " + record.getStatus());
                        apiService.saveRecord(record).enqueue(new Callback<RecordDTO>() {
                            @Override
                            public void onResponse(Call<RecordDTO> call, Response<RecordDTO> response) {
                                if (response.isSuccessful() && response.body() != null) {

                                    RecordDTO savedRecord = response.body();

                                    Long recordId = savedRecord.getId();

                                    Intent intent =
                                            new Intent(ValidationActivity.this,
                                                    RecordDetailActivity.class);

                                    intent.putExtra("RECORD_ID", recordId);

                                    startActivity(intent);

                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<RecordDTO> call, Throwable t) {
                                Log.e("API", "Error", t);
                            }
                        });
                        // 2. Actualizar UI
                        records.remove(position);
                        adapter.notifyItemRemoved(position);

                        if (records.isEmpty()) {
                            Intent intent = new Intent(ValidationActivity.this, ProjectDetailActivity.class);
                            intent.putExtra("PROJECT_ID", projectId);

                            // Limpia la pila de actividades
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);
                            finish();
                        }

                        // 3. Feedback
                        Toast.makeText(ValidationActivity.this, "Registro aceptado", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReject(RecordDTO record, int position) {
                        android.util.Log.d("VALIDATION", "Reject: " + record.getTitle());

                        records.remove(position);
                        adapter.notifyItemRemoved(position);

                        Toast.makeText(ValidationActivity.this, "Registro rechazado", Toast.LENGTH_SHORT).show();

                        if (records.isEmpty()) {

                            Intent intent = new Intent(ValidationActivity.this, ProjectDetailActivity.class);
                            intent.putExtra("PROJECT_ID", projectId);

                            // Limpia la pila de actividades
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onAddImage(RecordDTO record, int position) {

                    }
                });

        //Set adapter
        rv.setAdapter(adapter);




        btnBack.setOnClickListener(v -> finish());


        record = (RecordDTO) getIntent().getSerializableExtra("record");

        if (record != null) {
            tvContent.setText(record.getDescription());
        }

        record = (RecordDTO) getIntent().getSerializableExtra("record");

        if (record != null) {
            tvContent.setText(record.getDescription());
        }
    }
}
