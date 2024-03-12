package com.lingvosphere;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class LearningActivity extends AppCompatActivity {
    private JSONObject wordsAndDefinitions;
    private Iterator<String> wordIterator;
    private String currentWord;
    private TextView courseNameView;
    private TextView vocabView;
    private TextView definitionView;
    private TextView learningIndicatorView;
    private TextView pronounce;
    private int wordCount = 0;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);
        courseNameView = findViewById(R.id.courseName);
        vocabView = findViewById(R.id.vocab);
        definitionView = findViewById(R.id.definition);
        learningIndicatorView = findViewById(R.id.learning_indicator);
        pronounce = findViewById(R.id.pronounce);
        String courseId = getIntent().getStringExtra("courseId");
        String uid = Utility.getPreference(this, "uid");
        String token = Utility.getPreference(this, "token");
        String base_url = Utility.getPreference(this, "base_url");
        String url = String.format("%s/api/learning_schedule/getCourseContent?uid=%s&token=%s&courseId=%s", base_url, uid, token, courseId);
        HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
            @Override
            public void onSuccess(String response) throws JSONException {
                loadCourseData(response);
                setupNextButton();
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

        //loadCourseData("{\"chapterId\":\"CH1\",\"title\":\"Introduction to Hangul\",\"wordsAndDefinitions\":{\"죄송합니다 (joesonghamnida)\":\"I'm sorry\",\"감사합니다 (gamsahamnida)\":\"Thank you\",\"예 (ye)\":\"Yes\",\"아니요 (aniyo)\":\"No\",\"안녕하세요 (annyeonghaseyo)\":\"Hello\"}}");
    }

    public void loadCourseData(String jsonData) {
        try {
            JSONObject courseData = new JSONObject(jsonData);
            String courseName = courseData.getString("title");
            wordsAndDefinitions = courseData.getJSONObject("wordsAndDefinitions");

            courseNameView.setText(courseName);
            wordIterator = wordsAndDefinitions.keys();
            wordCount = wordsAndDefinitions.length();
            showNextWord();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showNextWord() throws JSONException {
        if (wordIterator.hasNext()) {
            currentWord = wordIterator.next();
            String definition = wordsAndDefinitions.getString(currentWord);
            if(currentWord.contains("(") && currentWord.contains(")")) {
                vocabView.setText(currentWord.substring(0, currentWord.indexOf("(")));
                pronounce.setText(currentWord.substring(currentWord.indexOf("("), currentWord.length()));
                pronounce.setVisibility(View.VISIBLE);
            } else {
                vocabView.setText(currentWord.replace("(", "\n )"));
                pronounce.setVisibility(View.GONE);
            }
            definitionView.setText(definition);
            currentIndex++;
            updateLearningIndicator();
        } else {
            makeProgress();
        }
    }

    private void updateLearningIndicator() {
        String indicatorText = currentIndex + "/" + wordCount;
        learningIndicatorView.setText(indicatorText);
    }

    private void setupNextButton() {
        View nextButton = findViewById(R.id.next_btn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showNextWord();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void makeProgress() {
        String courseId = getIntent().getStringExtra("courseId");
        String uid = Utility.getPreference(this, "uid");
        String token = Utility.getPreference(this, "token");
        String base_url = Utility.getPreference(this, "base_url");
        String url = String.format("%s/api/learning_schedule/makeProgress?uid=%s&token=%s&courseId=%s&type=1", base_url, uid, token, courseId);
        HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getInt("code")==0) {
                    Intent it = new Intent(LearningActivity.this, ResultActivity.class);
                    it.putExtra("title", "Course Finished!");
                    it.putExtra("button", "Let's Continue");
                    startActivity(it);
                    finish();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }
}