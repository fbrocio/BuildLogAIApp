package com.example.buildlogai.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buildlogai.ApiClient;
import com.example.buildlogai.ApiService;
import com.example.buildlogai.adapter.RecordAdapter;
import com.example.buildlogai.R;
import com.example.buildlogai.model.RecordDTO;
import com.example.buildlogai.model.UserResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectDetailActivity extends AppCompatActivity {

    private ApiService apiService;
    private RecyclerView recyclerView;
    private RecordAdapter adapter;
    private ImageView btnBack;
    private TextView tvProjectName;
    private FloatingActionButton fabRecord;
    private ChipGroup chipGroupFilters;
    private Long projectId;
    private List<RecordDTO> allRecords = new ArrayList<>();
    private LinearLayout layoutUsers;
    private MaterialButton btnGenerateReport;
    private MaterialCardView btnAddUser;
    private ImageButton btnEditProject;

    private EditText etSearch;
    private MaterialSwitch switchDarkMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs =
                getSharedPreferences("app", MODE_PRIVATE);

        boolean darkMode =
                prefs.getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                darkMode
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        //Recuperamos valores de intent
        this.projectId = getIntent().getLongExtra("PROJECT_ID", -1L);
        String projectName = getIntent().getStringExtra("PROJECT_NAME");
        String projectDescription = getIntent().getStringExtra("PROJECT_DESCRIPTION");

        recyclerView = findViewById(R.id.rvRecords);
        fabRecord = findViewById(R.id.fabRecord);
        btnBack = findViewById(R.id.btnBack);
        tvProjectName = findViewById(R.id.tvProjectName);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        btnGenerateReport = findViewById(R.id.btnGenerateReport);
        btnAddUser = findViewById(R.id.btnAddUser);
        layoutUsers = findViewById(R.id.layoutUsers);
        btnEditProject = findViewById(R.id.btnEditProject);
        etSearch = findViewById(R.id.etSearch);
        switchDarkMode = findViewById(R.id.switchDarkMode);

        switchDarkMode.setChecked(darkMode);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiService = ApiClient.getClient(this).create(ApiService.class);

        //Asignar nombre a tvProjectName
        tvProjectName.setText(projectName);

        // Configurar el listener de filtrado para los chips
        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            updateChipVisuals();
            applyFilters();
        });

        // Navegar a grabación
        fabRecord.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecordingActivity.class);
            intent.putExtra("PROJECT_ID", projectId);
            startActivity(intent);
        });

        // BOTÓN GENERATE REPORT
        btnGenerateReport.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProjectDetailActivity.this,
                    GenerateReportActivity.class
            );

            startActivity(intent);
        });

        btnEditProject.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProjectDetailActivity.this,
                    EditProjectActivity.class
            );

            intent.putExtra("PROJECT_ID", projectId);
            intent.putExtra(
                    "PROJECT_NAME",
                    tvProjectName.getText().toString()
            );
            intent.putExtra("PROJECT_DESCRIPTION", projectDescription);

            startActivity(intent);
        });

        //LISTENER BARRA DE BÚSQUEDA
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters(); // Filtramos cada vez que el texto cambia
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        //BOTÓN AÑADIR USUARIO
        btnAddUser.setOnClickListener(v -> {

            showAddUserDialog();
        });

        // BOTÓN VOLVER
        btnBack.setOnClickListener(v -> finish());

        // Inicializar visuales de los chips
        updateChipVisuals();

        switchDarkMode.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    prefs.edit()
                            .putBoolean(
                                    "dark_mode",
                                    isChecked
                            )
                            .apply();

                    AppCompatDelegate.setDefaultNightMode(
                            isChecked
                                    ? AppCompatDelegate.MODE_NIGHT_YES
                                    : AppCompatDelegate.MODE_NIGHT_NO
                    );
                });
    }

    /**
     * Actualiza la apariencia de los chips:
     * - Seleccionado: Fondo de color, texto blanco, sin borde.
     * - No seleccionado: Fondo blanco, borde y texto de color.
     */
    private void updateChipVisuals() {

        for (int i = 0; i < chipGroupFilters.getChildCount(); i++) {

            View view = chipGroupFilters.getChildAt(i);

            if (view instanceof Chip) {

                Chip chip = (Chip) view;

                int colorRes;

                if (chip.getId() == R.id.chipIncidencia) {

                    colorRes = R.color.chip_incidencia;

                } else if (chip.getId() == R.id.chipPendiente) {

                    colorRes = R.color.chip_pendiente;

                } else if (chip.getId() == R.id.chipAvance) {

                    colorRes = R.color.chip_avance;

                } else {
                    continue;
                }

                int chipColor = ContextCompat.getColor(this, colorRes);

                int surfaceColor = MaterialColors.getColor(
                        chip,
                        com.google.android.material.R.attr.colorSurface
                );

                int onPrimary = MaterialColors.getColor(
                        chip,
                        com.google.android.material.R.attr.colorOnPrimary
                );

                if (chip.isChecked()) {

                    // Seleccionado
                    chip.setChipBackgroundColor(
                            ColorStateList.valueOf(chipColor)
                    );

                    chip.setTextColor(onPrimary);

                    chip.setChipStrokeWidth(0f);

                } else {

                    // No seleccionado
                    chip.setChipBackgroundColor(
                            ColorStateList.valueOf(surfaceColor)
                    );

                    chip.setTextColor(chipColor);

                    chip.setChipStrokeColor(
                            ColorStateList.valueOf(chipColor)
                    );

                    float strokeWidth = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            2,
                            getResources().getDisplayMetrics()
                    );

                    chip.setChipStrokeWidth(strokeWidth);
                }
            }
        }
    }

    private void getRecordsByProject() {

        apiService.getRecordsByProject(projectId)
                .enqueue(new Callback<List<RecordDTO>>() {

                    @Override
                    public void onResponse(
                            Call<List<RecordDTO>> call,
                            Response<List<RecordDTO>> response
                    ) {

                        Log.d(
                                "RETROFIT_RESPONSE",
                                "success: " + response.isSuccessful()
                        );

                        Log.d(
                                "RETROFIT_CODE",
                                String.valueOf(response.code())
                        );

                        // ERROR BODY
                        if (response.errorBody() != null) {

                            try {

                                Log.e(
                                        "RETROFIT_ERROR_BODY",
                                        response.errorBody().string()
                                );

                            } catch (Exception e) {

                                e.printStackTrace();
                            }
                        }

                        // SUCCESS
                        if (response.isSuccessful()
                                && response.body() != null) {

                            allRecords = response.body();

                            Log.d(
                                    "RETROFIT_SIZE",
                                    String.valueOf(allRecords.size())
                            );

                            // Inicializar adapter
                            if (adapter == null) {

                                adapter = new RecordAdapter(
                                        new ArrayList<>(allRecords),
                                        record -> {

                                            Intent intent = new Intent(
                                                    ProjectDetailActivity.this,
                                                    RecordDetailActivity.class
                                            );

                                            intent.putExtra(
                                                    "RECORD_ID",
                                                    record.getId()
                                            );

                                            startActivity(intent);
                                        }
                                );

                                recyclerView.setAdapter(adapter);

                            } else {

                                adapter.updateList(
                                        new ArrayList<>(allRecords)
                                );
                            }

                            applyFilters();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<RecordDTO>> call,
                            Throwable t
                    ) {

                        Log.e(
                                "RETROFIT_ERROR",
                                "Error cargando records",
                                t
                        );

                        t.printStackTrace();
                    }
                });
    }

    private void applyFilters() {
        if (allRecords == null || adapter == null) return;

        List<Integer> checkedIds = chipGroupFilters.getCheckedChipIds();
        String query = etSearch.getText().toString().toLowerCase().trim();

        List<RecordDTO> filteredList = new ArrayList<>();

        for (RecordDTO record : allRecords) {
            // Filtro por TIPO (Chips)
            boolean matchesType = true;
            if (!checkedIds.isEmpty()) {
                matchesType = false;
                String type = record.getType();
                if (type != null) {
                    if (checkedIds.contains(R.id.chipIncidencia) && type.equalsIgnoreCase("incidencia")) matchesType = true;
                    if (checkedIds.contains(R.id.chipPendiente) && type.equalsIgnoreCase("pendiente")) matchesType = true;
                    if (checkedIds.contains(R.id.chipAvance) && type.equalsIgnoreCase("avance")) matchesType = true;
                }
            }

            // Filtro por TEXTO (Buscador)
            boolean matchesSearch = true;
            if (!query.isEmpty()) {
                boolean titleMatch = record.getTitle() != null && record.getTitle().toLowerCase().contains(query);
                boolean descMatch = record.getDescription() != null && record.getDescription().toLowerCase().contains(query);
                matchesSearch = titleMatch || descMatch;
            }

            // Si cumple ambos criterios, se añade a la lista
            if (matchesType && matchesSearch) {
                filteredList.add(record);
            }
        }

        adapter.updateList(filteredList);

        // Mostrar/ocultar estado vacío si no hay resultados
        View layoutEmpty = findViewById(R.id.layoutEmpty);
        if (filteredList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRecordsByProject();
        loadProjectUsers();
    }

    private void showAddUserDialog() {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setTitle("Añadir usuario");

        // CONTENEDOR

        LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (
                20 * getResources()
                        .getDisplayMetrics()
                        .density
        );

        layout.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        // INPUT EMAIL

        EditText etEmail = new EditText(this);

        etEmail.setHint("Email del usuario");

        etEmail.setInputType(
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );

        layout.addView(etEmail);

        builder.setView(layout);

        // BOTÓN CANCELAR

        builder.setNegativeButton(
                "Cancelar",
                (dialog, which) -> dialog.dismiss()
        );

        // BOTÓN AÑADIR

        builder.setPositiveButton(
                "Añadir",
                null
        );

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {

            Button btnPositive =
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            btnPositive.setOnClickListener(v -> {

                String email = etEmail
                        .getText()
                        .toString()
                        .trim();

                if (email.isEmpty()) {

                    etEmail.setError(
                            "Introduce un email"
                    );

                    return;
                }

                addUserToProject(email);

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void addUserToProject(String email) {

        apiService.getUserByEmail(email)
                .enqueue(new Callback<UserResponse>() {

                    @Override
                    public void onResponse(
                            Call<UserResponse> call,
                            Response<UserResponse> response
                    ) {

                        Log.d(
                                "USER_SEARCH",
                                "CODE: " + response.code()
                        );

                        Log.d(
                                "USER_SEARCH",
                                "SUCCESS: " + response.isSuccessful()
                        );

                        try {

                            if (response.errorBody() != null) {

                                Log.e(
                                        "USER_SEARCH",
                                        response.errorBody().string()
                                );
                            }

                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                        if (response.isSuccessful()
                                && response.body() != null) {

                            UserResponse user = response.body();

                            Log.d(
                                    "USER_SEARCH",
                                    "USER ID: " + user.getId()
                            );

                            apiService.addUserToProject(
                                    projectId,
                                    user.getId()
                            ).enqueue(new Callback<Void>() {

                                @Override
                                public void onResponse(
                                        Call<Void> call,
                                        Response<Void> response
                                ) {

                                    Toast.makeText(
                                            ProjectDetailActivity.this,
                                            "Usuario añadido correctamente",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }

                                @Override
                                public void onFailure(
                                        Call<Void> call,
                                        Throwable t
                                ) {

                                    t.printStackTrace();
                                }
                            });

                        } else {

                            Toast.makeText(
                                    ProjectDetailActivity.this,
                                    "Usuario no encontrado",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<UserResponse> call,
                            Throwable t
                    ) {

                        t.printStackTrace();

                        Toast.makeText(
                                ProjectDetailActivity.this,
                                "Error de red",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void loadProjectUsers() {

        apiService.getProjectUsers(projectId)
                .enqueue(new Callback<List<UserResponse>>() {

                    @Override
                    public void onResponse(
                            Call<List<UserResponse>> call,
                            Response<List<UserResponse>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            renderUsers(response.body());
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<List<UserResponse>> call,
                            Throwable t
                    ) {

                        t.printStackTrace();
                    }
                });
    }

    private void renderUsers(List<UserResponse> users) {

        layoutUsers.removeAllViews();

        String[] colors = {
                "#3949AB",
                "#00897B",
                "#E53935",
                "#8E24AA",
                "#FB8C00"
        };

        int maxVisible = 3;

        int visibleUsers = Math.min(
                users.size(),
                maxVisible
        );

        for (int i = 0; i < visibleUsers; i++) {

            UserResponse user = users.get(i);

            MaterialCardView card =
                    new MaterialCardView(this);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            72,
                            72
                    );

            if (i > 0) {
                params.setMargins(-16, 0, 0, 0);
            }

            card.setLayoutParams(params);

            card.setRadius(36);

            card.setCardElevation(0);

            card.setCardBackgroundColor(
                    Color.parseColor(
                            colors[i % colors.length]
                    )
            );

            TextView tv = new TextView(this);

            tv.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    )
            );

            tv.setGravity(Gravity.CENTER);

            tv.setTextColor(Color.WHITE);

            tv.setTextSize(12);

            tv.setTypeface(null, Typeface.BOLD);

            tv.setText(
                    getInitials(user.getName())
            );

            card.addView(tv);

            card.setTooltipText(
                    user.getName()
            );

            card.setOnClickListener(v -> {

                Toast.makeText(
                        this,
                        user.getName(),
                        Toast.LENGTH_SHORT
                ).show();
            });

            layoutUsers.addView(card);
        }

        // +N

        if (users.size() > maxVisible) {

            int remaining =
                    users.size() - maxVisible;

            MaterialCardView moreCard =
                    new MaterialCardView(this);

            LinearLayout.LayoutParams moreParams =
                    new LinearLayout.LayoutParams(
                            72,
                            72
                    );

            moreParams.setMargins(-16, 0, 0, 0);

            moreCard.setLayoutParams(moreParams);

            moreCard.setRadius(36);

            moreCard.setCardElevation(0);

            moreCard.setCardBackgroundColor(
                    Color.parseColor("#757575")
            );

            TextView tv = new TextView(this);

            tv.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    )
            );

            tv.setGravity(Gravity.CENTER);

            tv.setTextColor(Color.WHITE);

            tv.setTypeface(null, Typeface.BOLD);

            tv.setText("+" + remaining);

            moreCard.addView(tv);

            layoutUsers.addView(moreCard);
        }

        // BOTÓN +

        MaterialCardView addButton =
                new MaterialCardView(this);

        LinearLayout.LayoutParams addParams =
                new LinearLayout.LayoutParams(
                        72,
                        72
                );

        addParams.setMargins(16, 0, 0, 0);

        addButton.setLayoutParams(addParams);

        addButton.setRadius(36);

        addButton.setCardElevation(0);

        addButton.setStrokeWidth(2);

        int outlineColor = MaterialColors.getColor(
                addButton,
                com.google.android.material.R.attr.colorOutline
        );

        addButton.setStrokeColor(outlineColor);

        addButton.setCardBackgroundColor(Color.WHITE);

        TextView plus = new TextView(this);

        plus.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                )
        );

        plus.setGravity(Gravity.CENTER);

        plus.setText("+");

        plus.setTextSize(20);

        plus.setTypeface(null, Typeface.BOLD);

        int textColor = MaterialColors.getColor(
                plus,
                com.google.android.material.R.attr.colorOnSurface
        );

        plus.setTextColor(textColor);

        addButton.addView(plus);

        addButton.setOnClickListener(v -> {
            showAddUserDialog();
        });

        layoutUsers.addView(addButton);
    }
    private String getInitials(String name) {

        if (name == null || name.isEmpty()) {
            return "?";
        }

        String[] parts = name.trim().split(" ");

        if (parts.length == 1) {

            return parts[0]
                    .substring(0, 1)
                    .toUpperCase();
        }

        return (
                parts[0].substring(0,1)
                        + parts[1].substring(0,1)
        ).toUpperCase();
    }
}
