package com.example.buildlogai.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buildlogai.ApiClient;
import com.example.buildlogai.ApiService;
import com.example.buildlogai.R;
import com.example.buildlogai.model.Project;
import com.example.buildlogai.adapter.ProjectAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private ApiService apiService;
    private RecyclerView recyclerView;
    private ImageView btnLogout;
    private TextView tvGreeting;
    private MaterialSwitch switchDarkMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);

        boolean darkMode = prefs.getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                darkMode
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        recyclerView = findViewById(R.id.rvProjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnLogout = findViewById(R.id.btnLogout);
        tvGreeting = findViewById(R.id.tvGreeting);
        btnLogout.setOnClickListener(v -> logout());
        switchDarkMode = findViewById(R.id.switchDarkMode);


        String username = prefs.getString("username", "username");

        tvGreeting.setText(
                getString(R.string.projects_greeting, username)
        );

        FloatingActionButton fab = findViewById(R.id.fabAddProject);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProjectActivity.class);
            startActivity(intent);
        });

        apiService = ApiClient.getClient(this).create(ApiService.class);


        switchDarkMode.setChecked(darkMode);

        AppCompatDelegate.setDefaultNightMode(
                darkMode
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );

        switchDarkMode.setChecked(
                AppCompatDelegate.getDefaultNightMode()
                        == AppCompatDelegate.MODE_NIGHT_YES);

        switchDarkMode.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    prefs.edit()
                            .putBoolean("dark_mode", isChecked)
                            .apply();

                    AppCompatDelegate.setDefaultNightMode(
                            isChecked
                                    ? AppCompatDelegate.MODE_NIGHT_YES
                                    : AppCompatDelegate.MODE_NIGHT_NO
                    );
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarProjects();
    }

    private void cargarProjects() {
        apiService.getProjects().enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Project> lista = response.body();
                    ProjectAdapter adapter = new ProjectAdapter(lista);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void logout(){
        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        prefs.edit().remove("token").apply();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
