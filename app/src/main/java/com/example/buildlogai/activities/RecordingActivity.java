package com.example.buildlogai.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.buildlogai.ApiClient;
import com.example.buildlogai.ApiService;
import com.example.buildlogai.R;
import com.example.buildlogai.model.AIRequest;
import com.example.buildlogai.model.AIResponse;

import com.example.buildlogai.model.RecordDTO;
import com.google.android.material.button.MaterialButtonToggleGroup;


import java.util.ArrayList;
import java.util.List;

public class RecordingActivity extends AppCompatActivity {

    private enum InputMode { VOICE, TEXT }
    private InputMode currentMode = InputMode.VOICE;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private ApiService apiService;
    private TextView tvRecording;
    private EditText etNote;
    private View layoutVoice;
    private View layoutText;
    private Long projectId;
    private TextView tvTimer;
    private int secondsElapsed = 0;
    private android.os.Handler timerHandler = new android.os.Handler();

    private Runnable timerRunnable = new Runnable(){
        @Override
        public void run(){
            int minutes = secondsElapsed / 60;
            int seconds = secondsElapsed % 60;

            String time = String.format("%02d:%02d", minutes, seconds);
            tvTimer.setText(time);
            secondsElapsed++;
            timerHandler.postDelayed(this, 1000);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        //Recuperamos valores de intent
        this.projectId = getIntent().getLongExtra("PROJECT_ID", -1L);

        // UI
        Button btnFinish = findViewById(R.id.btnFinish);
        Button btnRecord = findViewById(R.id.btnRecord);
        ImageView btnBack = findViewById(R.id.btnBack);
        tvRecording = findViewById(R.id.tvRecording);
        tvTimer = findViewById(R.id.tvTimer);
        etNote = findViewById(R.id.etNote);

        layoutVoice = findViewById(R.id.layoutVoice);
        layoutText = findViewById(R.id.layoutText);


        MaterialButtonToggleGroup toggleMode = findViewById(R.id.toggleMode);

        // Modo por defecto
        toggleMode.check(R.id.btnModeVoice);

        toggleMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;

            if (checkedId == R.id.btnModeVoice) {
                currentMode = InputMode.VOICE;
                layoutVoice.setVisibility(View.VISIBLE);
                layoutText.setVisibility(View.GONE);
                tvTimer.setVisibility(View.VISIBLE);
                tvRecording.setText("Listo para grabar");
            } else {
                currentMode = InputMode.TEXT;
                layoutVoice.setVisibility(View.GONE);
                layoutText.setVisibility(View.VISIBLE);
                tvTimer.setVisibility(View.GONE);
                tvRecording.setText("Escribiendo nota");
            }
        });

        // Permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        }

        // SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");

        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onResults(Bundle results) {

                stopTimer();

                ArrayList<String> matches =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null && !matches.isEmpty()) {
                    String text = matches.get(0);
                    tvRecording.setText(text);
                    sendText(text); //Se envía el texto transcrito al backend
                }
            }

            @Override public void onError(int error) {
                stopTimer();
                tvRecording.setText("Error de reconocimiento");
            }

            @Override public void onReadyForSpeech(Bundle params) {
                tvRecording.setText("Escuchando...");
            }

            @Override public void onEndOfSpeech() {}

            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });

        apiService = ApiClient.getClient(this).create(ApiService.class);

        // BOTÓN GRABAR (solo VOZ)
        btnRecord.setOnClickListener(v -> {
            if (currentMode != InputMode.VOICE) return;

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                startTimer();
                speechRecognizer.startListening(speechIntent);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        1);
            }
        });

        // BOTÓN FINISH (modo dual)
        btnFinish.setOnClickListener(v -> {

            if (currentMode == InputMode.VOICE) {
                stopTimer();
                speechRecognizer.stopListening();
            } else {
                String text = etNote.getText().toString().trim();

                if (!text.isEmpty()) {
                    sendText(text);
                } else {
                    tvRecording.setText("Texto vacío");
                }
            }
        });

        // BOTÓN VOLVER
        btnBack.setOnClickListener(v -> finish());
    }

    private void sendText(String text) {
        tvRecording.setText("Procesando...");

        AIRequest request = new AIRequest(text);

        apiService.parseAI(request).enqueue(
                new retrofit2.Callback<AIResponse>() {

                    @Override
                    public void onResponse(retrofit2.Call<AIResponse> call,
                                           retrofit2.Response<AIResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            List<RecordDTO> records = response.body().getRecords();

                            if (records != null && !records.isEmpty()) {

                                goToValidation(records);
                                /*for (RecordDTO r : records) {
                                    android.util.Log.d("API", "Title: " + r.getTitle());
                                    android.util.Log.d("API", "Desc: " + r.getDescription());
                                }

                                tvRecording.setText("Records generados: " + records.size());*/
                            } else {
                                tvRecording.setText("Sin records");
                            }
                        } else {
                            tvRecording.setText("Error backend: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<AIResponse> call, Throwable t) {
                        tvRecording.setText("Error de red: " + t.getMessage());
                        //android.util.Log.e("API", "Error de red: " + t.getMessage());
                    }


                });
        }

    private void startTimer() {
        secondsElapsed = 0;
        timerHandler.post(timerRunnable);
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void goToValidation(List<RecordDTO> records) {
        Intent intent = new Intent(RecordingActivity.this, ValidationActivity.class);

        intent.putParcelableArrayListExtra(
                "RECORDS",
                new ArrayList<>(records)
        );

        intent.putExtra("PROJECT_ID", projectId);

        startActivity(intent);

        // Opcional: evitar volver atrás a esta pantalla
        // finish();
    }
}