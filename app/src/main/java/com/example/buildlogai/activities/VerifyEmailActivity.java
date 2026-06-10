package com.example.buildlogai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buildlogai.ApiClient;
import com.example.buildlogai.ApiService;
import com.example.buildlogai.R;
import com.example.buildlogai.model.VerifyRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyEmailActivity extends AppCompatActivity {

    private TextInputEditText etCode;
    private MaterialButton btnVerify;

    private ApiService apiService;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        email = getIntent().getStringExtra("email");

        etCode = findViewById(R.id.etCode);
        btnVerify = findViewById(R.id.btnVerify);

        TextView tvEmail = findViewById(R.id.tvEmail);

        String email = getIntent().getStringExtra("email");
        tvEmail.setText(email);

        btnVerify.setOnClickListener(v -> verify());

        TextView tvResendCode = findViewById(R.id.tvResendCode);

        tvResendCode.setOnClickListener(v -> {

            Toast.makeText(
                    VerifyEmailActivity.this,
                    "Email: " + email,
                    Toast.LENGTH_LONG
            ).show();

            apiService.resendVerification(email)
                    .enqueue(new Callback<String>() {

                        @Override
                        public void onResponse(
                                Call<String> call,
                                Response<String> response) {

                            Toast.makeText(
                                    VerifyEmailActivity.this,
                                    "HTTP " + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                        @Override
                        public void onFailure(
                                Call<String> call,
                                Throwable t) {

                            Toast.makeText(
                                    VerifyEmailActivity.this,
                                    "ERROR: " + t.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });

        });
    }

    private void verify() {

        String code = etCode.getText().toString().trim();

        VerifyRequest request =
                new VerifyRequest(email, code);

        apiService.verifyEmail(request)
                .enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(
                            Call<String> call,
                            Response<String> response) {

                        if(response.isSuccessful()) {

                            Toast.makeText(
                                    VerifyEmailActivity.this,
                                    "Cuenta verificada",
                                    Toast.LENGTH_LONG
                            ).show();

                            Intent intent =
                                    new Intent(
                                            VerifyEmailActivity.this,
                                            LoginActivity.class
                                    );

                            startActivity(intent);
                            finish();

                        } else {

                            Toast.makeText(
                                    VerifyEmailActivity.this,
                                    "Código incorrecto",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<String> call,
                            Throwable t) {

                        Toast.makeText(
                                VerifyEmailActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}