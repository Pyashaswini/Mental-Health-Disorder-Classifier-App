package com.example.mentalhealthapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView disclaimerText = findViewById(R.id.disclaimerText);
        Button startQuizButton = findViewById(R.id.startQuizButton);

        disclaimerText.setText("Please note: This quiz is not a substitute for professional diagnosis. For serious concerns, consult a licensed mental health professional.");

        startQuizButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, QuizActivity.class)));
    }
}