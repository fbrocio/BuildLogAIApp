package com.example.buildlogai.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.noties.markwon.Markwon;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buildlogai.ApiService;
import com.example.buildlogai.R;
import com.example.buildlogai.ApiClient;
import com.example.buildlogai.model.ReportRequestDTO;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ReportResultActivity extends AppCompatActivity {

    private ImageView btnBack;

    private TextView tvReport;

    private MaterialButton btnExportPdf;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report_result);

        initViews();

        setupListeners();

        loadReport();
        apiService = ApiClient.getClient(this).create(ApiService.class);
    }

    private void initViews() {

        btnBack = findViewById(R.id.btnBack);

        tvReport = findViewById(R.id.tvReport);

        btnExportPdf = findViewById(R.id.btnExportPdf);
    }

    private void setupListeners() {

        btnBack.setOnClickListener(v -> finish());

        btnExportPdf.setOnClickListener(v -> {

            String topic =
                    getIntent().getStringExtra("topic");

            if (topic != null) {
                downloadPdf(topic);
            } else {
                Toast.makeText(
                        this,
                        "Error: No hay tema para exportar",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void loadReport() {

        String report =
                getIntent().getStringExtra("report");

        if (report == null) {
            report = "No se pudo cargar el informe.";
        }

        Markwon markwon = Markwon.create(this);

        markwon.setMarkdown(tvReport, report);
    }

    private void downloadPdf(String topic) {

        ReportRequestDTO request =
                new ReportRequestDTO(topic);

        apiService.generatePdf(request)
                .enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(
                            Call<ResponseBody> call,
                            Response<ResponseBody> response
                    ) {

                        System.out.println("PDF RESPONSE CODE: " + response.code());

                        if (response.isSuccessful()
                                && response.body() != null) {

                            System.out.println("PDF OK");

                            savePdf(response.body());

                        } else {

                            System.out.println("PDF ERROR");

                            try {

                                if (response.errorBody() != null) {

                                    System.out.println(
                                            response.errorBody().string()
                                    );
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(
                                    ReportResultActivity.this,
                                    "Error servidor PDF: " + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ResponseBody> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                ReportResultActivity.this,
                                "Error descargando PDF",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void savePdf(ResponseBody body) {

        try {

            File file = new File(
                    getExternalFilesDir(null),
                    "informe_" + System.currentTimeMillis() + ".pdf"
            );

            InputStream inputStream = body.byteStream();

            FileOutputStream outputStream =
                    new FileOutputStream(file);

            byte[] buffer = new byte[4096];

            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            Toast.makeText(
                    this,
                    "PDF guardado:\n" + file.getAbsolutePath(),
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Error guardando PDF",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}