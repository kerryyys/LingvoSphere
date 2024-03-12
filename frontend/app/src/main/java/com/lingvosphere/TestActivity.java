package com.lingvosphere;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private TextView learningTitle, courseName, vocab, learningIndicator;
    private Button option1, option2, option3, option4;
    private int currentQuestionIndex = 0;
    // Assuming a hypothetical class Question that holds your question data
    private List<Question> questions;
    private List<String> userAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        learningTitle = findViewById(R.id.learning_titile);
        courseName = findViewById(R.id.courseName);
        vocab = findViewById(R.id.vocab);
        learningIndicator = findViewById(R.id.learning_indicator);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        String courseId = getIntent().getStringExtra("courseId");
        String uid = Utility.getPreference(this, "uid");
        String token = Utility.getPreference(this, "token");
        String base_url = Utility.getPreference(this, "base_url");
        String url = String.format("%s/api/learning_schedule/getExamContent?uid=%s&token=%s&courseId=%s", base_url, uid, token, courseId);
        HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
            @Override
            public void onSuccess(String response) throws JSONException {
                questions = loadQuestions(response);
                userAnswers = new ArrayList<>(questions.size());
                setQuestion(0);
                option1.setOnClickListener(v -> onOptionSelected(v));
                option2.setOnClickListener(v -> onOptionSelected(v));
                option3.setOnClickListener(v -> onOptionSelected(v));
                option4.setOnClickListener(v -> onOptionSelected(v));
            }

            @Override
            public void onError(String error) {

            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    private void onOptionSelected(View view) {
        Button selectedButton = (Button) view;
        String selectedAnswer = selectedButton.getText().toString();

        // Store the selected answer
        if (currentQuestionIndex < userAnswers.size()) {
            userAnswers.set(currentQuestionIndex, selectedAnswer);
        } else {
            userAnswers.add(selectedAnswer);
        }

        // Move to the next question
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            setQuestion(currentQuestionIndex);
        } else {
            evaluateAnswers();
        }
    }

    private void evaluateAnswers() {
        int correctCount = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String correctAnswer = question.getCorrectAnswer();
            String userAnswer = userAnswers.get(i);

            if (correctAnswer.equals(userAnswer)) {
                correctCount++;
            }
        }

        makeProgress(correctCount);
    }

    private void makeProgress(int correctCount) {
        String courseId = getIntent().getStringExtra("courseId");
        String uid = Utility.getPreference(this, "uid");
        String token = Utility.getPreference(this, "token");
        String base_url = Utility.getPreference(this, "base_url");
        String url = String.format("%s/api/learning_schedule/makeProgress?uid=%s&token=%s&courseId=%s&type=0", base_url, uid, token, courseId);
        HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getInt("code")>=0) {
                    Intent it = new Intent(TestActivity.this, ResultActivity.class);
                    it.putExtra("title", "Test Passed!" + "\nYou got " + correctCount + " out of " + questions.size() + " correct");
                    if(jsonObject.getInt("code") > 0) {
                        it.putExtra("action", "cert");
                        it.putExtra("button", "Check My Certification!");
                    } else {
                        it.putExtra("button", "Great!");
                    }
                    startActivity(it);
                    finish();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }


    private void setQuestion(int questionIndex) {
        // Ensure the index is within the range of available questions
        if (questionIndex < 0 || questionIndex >= questions.size()) return;

        Question currentQuestion = questions.get(questionIndex);

        // Update UI with the current question
        vocab.setText(currentQuestion.getQuestionText());
        option1.setText(currentQuestion.getChoices().get(0));
        option2.setText(currentQuestion.getChoices().get(1));
        option3.setText(currentQuestion.getChoices().get(2));
        option4.setText(currentQuestion.getChoices().get(3));

        // Update the learning indicator
        learningIndicator.setText((questionIndex + 1) + "/" + questions.size());
    }

    private List<Question> loadQuestions(String json) {
        questions = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            String examTitle = jsonObject.getString("examTitle");
            ((TextView)findViewById(R.id.courseName)).setText(examTitle);
            JSONArray jsonQuestions = jsonObject.getJSONArray("questions");
            for (int i = 0; i < jsonQuestions.length(); i++) {
                JSONObject jsonQuestion = jsonQuestions.getJSONObject(i);
                String questionText = jsonQuestion.getString("questionText");
                String correctAnswer = jsonQuestion.getString("answer");
                JSONArray jsonChoices = jsonQuestion.getJSONArray("choices");

                Question question = new Question(questionText, correctAnswer);
                for (int j = 0; j < jsonChoices.length(); j++) {
                    question.addChoice(jsonChoices.getString(j));
                }

                questions.add(question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public static class Question {
        private String questionText;
        private List<String> choices;
        private String correctAnswer;

        public Question(String questionText, String correctAnswer) {
            this.questionText = questionText;
            this.choices = new ArrayList<>();
            this.correctAnswer = correctAnswer;
        }

        public void addChoice(String choice) {
            choices.add(choice);
        }

        public String getQuestionText() {
            return questionText;
        }

        public List<String> getChoices() {
            return choices;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

    }

}