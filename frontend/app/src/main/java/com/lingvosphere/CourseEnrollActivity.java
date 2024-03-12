package com.lingvosphere;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.BoringLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CourseEnrollActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_enroll);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary_color));
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        decorView.setSystemUiVisibility(uiOptions);
        window.setNavigationBarColor(Color.parseColor("#f7f7f7"));

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        load_courses();
    }

    private void load_courses() {
        String uid = Utility.getPreference(this, "uid");
        String token = Utility.getPreference(this, "token");
        String base_url = Utility.getPreference(this, "base_url");
        String url = String.format("%s/api/learning_schedule/getCourses?uid=%s&token=%s", base_url, uid, token);
        HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
            @Override
            public void onSuccess(String response) throws JSONException {
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    JSONArray dataArray = jsonObj.getJSONArray("data");

                    boolean flag_enroll = true;
                    boolean flag_to_enroll = true;
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject languageObj = dataArray.getJSONObject(i);
                        String language = languageObj.getString("language");
                        JSONArray coursesArray = languageObj.getJSONArray("data");

                        System.out.println("Language: " + language);

                        boolean enroll_status = false;
                        for (int j = 0; j < coursesArray.length(); j++) {
                            JSONObject courseObj = coursesArray.getJSONObject(j);
                            if(courseObj.optBoolean("enrolled", false))
                                enroll_status = true;
                        }
                        View courseView;
                        LinearLayout linearLayout;
                        if(enroll_status) {
                            linearLayout = findViewById(R.id.enrolled_list);
                            flag_enroll = false;
                        } else {
                            linearLayout = findViewById(R.id.to_enroll_list);
                            flag_to_enroll = false;
                        }
                        courseView = LayoutInflater.from(CourseEnrollActivity.this).inflate((enroll_status ? R.layout.enroll_item : R.layout.to_enroll_item), linearLayout, false);

                        TextView courseName = courseView.findViewById(R.id.name);
                        courseName.setText(language);
                        ImageView courseFlag = courseView.findViewById(R.id.flag);
                        courseFlag.setImageResource(Utility.getFlag(language));
                        final int finalI = i;

                        courseView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent it = new Intent(CourseEnrollActivity.this, LevelSelcetionActivity.class);
                                it.putExtra("data", coursesArray.toString());
                                it.putExtra("language", language);
                                startActivity(it);
                                finish();
                            }
                        });
                        linearLayout.addView(courseView);
                    }

                    if(flag_enroll)
                        findViewById(R.id.no_enrollment_hint).setVisibility(View.VISIBLE);
                    if(flag_to_enroll)
                        findViewById(R.id.no_to_enroll_hint).setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }
}