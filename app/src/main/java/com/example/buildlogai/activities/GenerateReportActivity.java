package com.example.buildlogai.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buildlogai.ApiService;
import com.example.buildlogai.R;
import com.example.buildlogai.ApiClient;
import com.example.buildlogai.model.ReportRequestDTO;
import com.example.buildlogai.model.ReportResponseDTO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenerateReportActivity extends AppCompatActivity {

    private ImageView btnBack;

    private TextInputEditText etTopic;

    private MaterialButton btnSelectDate;
    private MaterialButton btnGenerate;

    private TextView tvSelectedDate;

    private String selectedDate = null;
    private ApiService apiService;
    private ProgressBar progressBar;
    private MaterialButtonToggleGroup toggleMode;
    private View layoutTopicSection;
    private View layoutDateSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_report);

        initViews();

        setupListeners();

        apiService = ApiClient
                .getClient(this).create(ApiService.class);
    }

    private void initViews() {

        btnBack = findViewById(R.id.btnBack);

        etTopic = findViewById(R.id.etTopic);

        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnGenerate = findViewById(R.id.btnGenerate);

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        progressBar = findViewById(R.id.progressBar);
        toggleMode = findViewById(R.id.toggleMode);
        layoutTopicSection = findViewById(R.id.layoutTopicSection);
        layoutDateSection = findViewById(R.id.layoutDateSection);
    }

    private void setupListeners() {

        btnBack.setOnClickListener(v -> finish());

        btnSelectDate.setOnClickListener(v -> openDatePicker());

        btnGenerate.setOnClickListener(v -> generateReport());
        toggleMode.check(R.id.btnModeTopic);

        layoutTopicSection.setVisibility(View.VISIBLE);
        layoutDateSection.setVisibility(View.GONE);

        toggleMode.addOnButtonCheckedListener(
                (group, checkedId, isChecked) -> {

                    if (!isChecked) {
                        return;
                    }

                    if (checkedId == R.id.btnModeTopic) {

                        layoutTopicSection.setVisibility(View.VISIBLE);
                        layoutDateSection.setVisibility(View.GONE);

                    } else if (checkedId == R.id.btnModeDate) {

                        layoutTopicSection.setVisibility(View.GONE);
                        layoutDateSection.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void openDatePicker() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH);

        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {

                    String formattedDate = String.format(
                            "%02d-%02d-%04d",
                            selectedDay,
                            selectedMonth + 1,
                            selectedYear
                    );

                    selectedDate = formattedDate;

                    tvSelectedDate.setText(selectedDate);
                },
                year,
                month,
                day
        );

        dialog.show();
    }

    private void generateReport() {

        String topic = "";

        if (etTopic.getText() != null) {
            topic = etTopic.getText().toString().trim();
        }

        int selectedMode = toggleMode.getCheckedButtonId();

        boolean isTopicMode =
                selectedMode == R.id.btnModeTopic;

        if (isTopicMode && topic.isEmpty()) {

            Toast.makeText(
                    this,
                    "Introduce un tema",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!isTopicMode && selectedDate == null) {

            Toast.makeText(
                    this,
                    "Selecciona una fecha",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String query = isTopicMode
                ? topic
                : selectedDate;

        progressBar.setVisibility(View.VISIBLE);

        btnGenerate.setEnabled(false);

        btnGenerate.setText("Generando...");

        ReportRequestDTO request =
                new ReportRequestDTO(query);

        apiService.generateReport(request)
                .enqueue(new Callback<ReportResponseDTO>() {

                    @Override
                    public void onResponse(
                            Call<ReportResponseDTO> call,
                            Response<ReportResponseDTO> response
                    ) {

                        btnGenerate.setEnabled(true);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            String report =
                                    response.body().getSummary();

                            progressBar.setVisibility(View.GONE);
                            btnGenerate.setText("Generar informe");

                            openReportScreen(report, query);

                            btnGenerate.setText("Generar informe");

                        } else {

                            Toast.makeText(
                                    GenerateReportActivity.this,
                                    "Error: " + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();
                            progressBar.setVisibility(View.GONE);
                            btnGenerate.setText("Generar informe");

                            try {

                                if (response.errorBody() != null) {

                                    System.out.println(
                                            response.errorBody().string()
                                    );
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ReportResponseDTO> call,
                            Throwable t
                    ) {

                        btnGenerate.setEnabled(true);

                        String errorMessage = t.getMessage();

                        if (errorMessage == null) {
                            errorMessage = "Error de conexión";
                        }
                        progressBar.setVisibility(View.GONE);
                        btnGenerate.setText("Generar informe");

                        Toast.makeText(
                                GenerateReportActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
    private void openReportScreen(String report, String query) {

        Intent intent = new Intent(
                this,
                ReportResultActivity.class
        );

        intent.putExtra("report", report);
        intent.putExtra("topic", query);

        startActivity(intent);
    }
}