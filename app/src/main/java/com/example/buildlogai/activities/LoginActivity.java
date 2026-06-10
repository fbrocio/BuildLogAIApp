package com.example.buildlogai.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buildlogai.ApiClient;
import com.example.buildlogai.ApiService;
import com.example.buildlogai.R;
import com.example.buildlogai.model.AuthResponse;
import com.example.buildlogai.model.UserRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUser, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        etUser = findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> loginUser());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });
    }

    private void loginUser() {

        String email = etUser.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Introduce email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        UserRequest request = new UserRequest(null, email, password);

        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    String token = response.body().getToken();
                    String username = response.body().getName();
                    String email = response.body().getEmail();



                    // Guardar token
                    SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
                    prefs.edit().putString("token", token).apply();
                    prefs.edit().putString("username", username).apply();
                    prefs.edit().putString("email", email).apply();

                    Toast.makeText(LoginActivity.this, "Login correcto", Toast.LENGTH_SHORT).show();

                    // Ir a siguiente pantalla (ej: ProjectListActivity)
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                } else if (response.code() == 403) {

                    Intent intent = new Intent(
                            LoginActivity.this,
                            VerifyEmailActivity.class
                    );

                    intent.putExtra("email", email);

                    startActivity(intent);

                } else {

                    Toast.makeText(
                            LoginActivity.this,
                            "Credenciales incorrectas",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(
                        LoginActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();

                t.printStackTrace();
            }
        });
    }
}