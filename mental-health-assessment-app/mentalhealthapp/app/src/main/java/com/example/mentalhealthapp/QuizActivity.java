package com.example.mentalhealthapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class QuizActivity extends AppCompatActivity {
    String[] questions = {
            "What is your age?",
            "Do you often feel nervous?",
            "Do you experience panic attacks or intense fear?",
            "Do you find yourself breathing rapidly in stressful situations?",
            "Do you frequently sweat due to anxiety or stress?",
            "Do you have trouble concentrating?",
            "Do you have difficulty sleeping?",
            "Are you having trouble managing your work or responsibilities?",
            "Do you often feel hopeless?",
            "Do you often feel angry or irritable?",
            "Do you tend to overreact to situations?",
            "Have you noticed changes in your eating habits?",
            "Do you have suicidal thoughts?",
            "Do you often feel tired or drained of energy?",
            "Do you have a close friend you can talk to?",
            "Are you addicted to social media?",
            "Have you experienced weight gain recently?",
            "Would you describe yourself as an introvert?",
            "Do stressful memories frequently pop up in your mind?",
            "Do you experience nightmares often?",
            "Do you avoid people or activities you once enjoyed?",
            "Do you often have negative thoughts or feelings?",
            "Do you have trouble concentrating on daily tasks?",
            "Do you frequently blame yourself for things?",
            "Do you experience hallucinations (seeing or hearing things that aren't there)?",
            "Do you have repetitive behaviors or habits?",
            "Do you notice changes in your mental state based on the season?",
            "Have you experienced a sudden increase in energy levels?"
    };

    String[] fieldNames = {

            "age", "feeling_nervous", "panic", "breathing_rapidly", "sweating",
            "trouble_in_concentration", "having_trouble_in_sleeping", "having_trouble_with_work",
            "hopelessness", "anger", "over_react", "change_in_eating", "suicidal_thought", "feeling_tired",
            "close_friend", "social_media_addiction", "weight_gain", "introvert", "popping_up_stressful_memory",
            "having_nightmares", "avoids_people_or_activities", "feeling_negative", "trouble_concentrating",
            "blamming_yourself", "hallucinations", "repetitive_behaviour", "seasonally", "increased_energy"

    };

    HashMap<String, String> answersMap = new HashMap<>();
    int currentQuestion = 0;
    TextView questionText;
    Button nextButton;
    RadioGroup radioGroup;
    RadioButton radioYes, radioNo;
    EditText ageInput;
    Button goToLocationButton;
    private RequestQueue requestQueue;
    private String flaskServerUrl = "YOUR_FLASK_SERVER_URL/predict"; // Replace with your Flask server URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.quiz), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        questionText = findViewById(R.id.question_text);
        nextButton = findViewById(R.id.next_button);
        radioGroup = findViewById(R.id.radio_group);
        radioYes = findViewById(R.id.radio_yes);
        radioNo = findViewById(R.id.radio_no);
        ageInput = findViewById(R.id.age_input);
        goToLocationButton = findViewById(R.id.goToLocationButton);
        requestQueue = Volley.newRequestQueue(this);
        showQuestion();


        goToLocationButton.setOnClickListener(v -> {
            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            startActivity(intent);
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save current response
                if (currentQuestion == 0) {
                    String age = ageInput.getText().toString().trim();
                    if (age.isEmpty()) {
                        Toast.makeText(QuizActivity.this, "Please enter your age", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    answersMap.put(fieldNames[currentQuestion], age);
                } else {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    if (selectedId == -1) {
                        Toast.makeText(QuizActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String answer = (selectedId == R.id.radio_yes) ? "1" : "0";
                    answersMap.put(fieldNames[currentQuestion], answer);
                }


                currentQuestion++;
                if (currentQuestion < questions.length) {
                    showQuestion();
                }
                    else {
                        radioGroup.setVisibility(View.GONE);  // <- Immediately hide
                        ageInput.setVisibility(View.GONE);
                        nextButton.setVisibility(View.GONE);  // Optionally hide submit button
                        sendAnswersToFlask();
                    }

                }
            
        });
    }
    private void showQuestion() {
        questionText.setText(questions[currentQuestion]);

        if (currentQuestion == 0) {
            radioGroup.setVisibility(View.GONE);
            ageInput.setVisibility(View.VISIBLE);
            goToLocationButton.setVisibility(View.GONE);
            nextButton.setText("Next");
        }
        else if (currentQuestion == questions.length - 1) {
            radioGroup.setVisibility(View.VISIBLE);
            ageInput.setVisibility(View.GONE);
            goToLocationButton.setVisibility(View.GONE);
            nextButton.setText("Submit");
        }
        else {

            radioGroup.clearCheck();
            radioGroup.setVisibility(View.VISIBLE);
            ageInput.setVisibility(View.GONE);
            goToLocationButton.setVisibility(View.GONE);
            nextButton.setText("Next");
        }

    }

    private void sendAnswersToFlask() {
        JSONObject jsonAnswers = new JSONObject(answersMap);
        Log.d("QuizAnswers", jsonAnswers.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, flaskServerUrl, jsonAnswers,
                response -> {
                    try {
                        String prediction = response.getString("prediction");
                        questionText.setText("Prediction: " + prediction);
                        radioGroup.setVisibility(View.GONE);
                        ageInput.setVisibility(View.GONE);
                        nextButton.setVisibility(View.GONE);
                        goToLocationButton.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        questionText.setText("Error parsing prediction.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    questionText.setText("Error sending data to server.");
                    Log.e("VolleyError", error.toString());
                    Toast.makeText(QuizActivity.this, "Failed to connect to prediction server.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };


        requestQueue.add(request);
    }
}