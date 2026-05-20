package com.example.buildlogai.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if(token != null && !token.isEmpty()){
            // Usuario ya autenticado
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Usuario no autenticado
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish(); // importante: evita volver atrás
    }
}