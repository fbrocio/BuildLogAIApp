package com.example.buildlogai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buildlogai.ApiClient;
import com.example.buildlogai.ApiService;
import com.example.buildlogai.R;
import com.example.buildlogai.model.UserRequest;
import com.example.buildlogai.model.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText etNewUser, etEmail, etNewPassword;
    private Button btnSignUp;
    private TextView tvGoToLogin;
    private ImageView btnBack;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        etNewUser = findViewById(R.id.etNewUser);
        etEmail = findViewById(R.id.etEmail);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnSignUp.setOnClickListener(v -> registerUser());

        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {

        String user = etNewUser.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etNewPassword.getText().toString().trim();

        if (user.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        UserRequest request = new UserRequest(user, email, password);

        apiService.register(request).enqueue(new Callback<UserResponse>() {
            @Override
            /*public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class)); // volver a login
                } else {
                    Toast.makeText(SignupActivity.this, "Error en registro", Toast.LENGTH_SHORT).show();
                }
            }*/
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    Toast.makeText(SignupActivity.this,
                            "Usuario creado: " + response.body().getEmail(),
                            Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    finish();

                } else {

                    String errorMsg = "Error desconocido";

                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string(); // texto del backend
                        }
                    } catch (Exception e) {
                        errorMsg = "Error al leer respuesta";
                    }

                    Toast.makeText(SignupActivity.this,
                            "Error (" + response.code() + "): " + errorMsg,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            /*public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });*/
            public void onFailure(Call<UserResponse> call, Throwable t) {

                String msg;

                if (t instanceof java.net.SocketTimeoutException) {
                    msg = "Timeout de conexión";
                } else if (t instanceof java.net.UnknownHostException) {
                    msg = "Sin conexión al servidor";
                } else {
                    msg = t.getMessage();
                }

                Toast.makeText(SignupActivity.this,
                        "Error de red: " + msg,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}