package com.lingvosphere;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;

public class GeneratingCourseActivity extends AppCompatActivity {

    public static boolean result = false;
    private TextView textView;
    private String text = "Generating";
    private int dotCount = 0;
    private Handler handler = new Handler();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating_course);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#018080"));
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        decorView.setSystemUiVisibility(uiOptions);
        window.setNavigationBarColor(Color.parseColor("#018080"));
        String courseId = getIntent().getStringExtra("courseId");
        String days = getIntent().getStringExtra("days");
        textView = findViewById(R.id.textView2);
        startDotAnimation();
        //textView2
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                while(!result) ;
                Intent it = new Intent(GeneratingCourseActivity.this, ResultActivity.class);
                it.putExtra("title", "Enrollment success!");
                it.putExtra("button", "Let's Go");
                startActivity(it);
                finish();
            }
        }, 4000);
        if(days.contains("1")) {
            String uid = Utility.getPreference(this, "uid");
            String token = Utility.getPreference(this, "token");
            String base_url = Utility.getPreference(this, "base_url");
            String url = String.format("%s/api/learning_schedule/setScheduleWithPreferences?uid=%s&token=%s&courseId=%s&days=%s", base_url, uid, token, courseId, days);
            HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
                @Override
                public void onSuccess(String response) throws JSONException {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("code") == 0)
                        GeneratingCourseActivity.result = true;
                }

                @Override
                public void onError(String error) {

                }
            });
        } else {
            result = true;
        }
    }

    private void startDotAnimation() {
        final Runnable updateTextRunnable = new Runnable() {
            @Override
            public void run() {
                dotCount = (dotCount + 1) % 4; // 回绕到 0，1，2，3
                String dots = new String(new char[dotCount]).replace("\0", ".");
                textView.setText(text + dots);
                handler.postDelayed(this, 500); // 每500毫秒更新一次
            }
        };

        handler.post(updateTextRunnable);
    }
}