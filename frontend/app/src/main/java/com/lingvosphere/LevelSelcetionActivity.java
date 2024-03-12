package com.lingvosphere;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class LevelSelcetionActivity extends AppCompatActivity {

    private LinearLayout beginnerLayout, intermediateLayout, advancedLayout;
    private LinearLayout[] reasonLayouts;
    private boolean[] reasonLayouts_status;

    private Button[] days_select;
    private boolean[] days_status;
    private int selected_level = -1;
    private boolean requesting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selcetion);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#377e7f"));
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        decorView.setSystemUiVisibility(uiOptions);
        window.setNavigationBarColor(Color.parseColor("#377e7f"));

        init_views();
    }

    private void init_views() {

        String language = getIntent().getStringExtra("language");
        TextView title = findViewById(R.id.language);
        title.setText(language);
        ImageView level_select_close_btn = this.findViewById(R.id.level_select_close_btn);
        level_select_close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        beginnerLayout = findViewById(R.id.beginner_layout); // Replace with your ID
        intermediateLayout = findViewById(R.id.intermediate_layout); // Replace with your ID
        advancedLayout = findViewById(R.id.advanced_layout); // Replace with your ID

        // Initialize reason layouts
        reasonLayouts = new LinearLayout[]{
                findViewById(R.id.purpose_brain_training), // Replace with your IDs
                findViewById(R.id.purpose_travel),
                findViewById(R.id.purpose_school),
                findViewById(R.id.purpose_family_friends),
                findViewById(R.id.purpose_job_required),
                findViewById(R.id.purpose_other)
        };

        reasonLayouts_status = new boolean[] {
                false,
                false,
                false,
                false,
                false,
                false
        };

        days_select = new Button[] {
                findViewById(R.id.select_sun),
                findViewById(R.id.select_mon),
                findViewById(R.id.select_tue),
                findViewById(R.id.select_wed),
                findViewById(R.id.select_thu),
                findViewById(R.id.select_fri),
                findViewById(R.id.select_sat),
        };

        days_status = new boolean[] {
                false,
                false,
                false,
                false,
                false,
                false,
                false
        };

        // Set click listeners for reasons
        init_ReasonsClickListener();
        init_DaysClickListener();
        init_LevelSelect();

        findViewById(R.id.enroll_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_level < 0) {
                    Toast.makeText(LevelSelcetionActivity.this, "Please select your level first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Enroll();
            }
        });
    }

    private void Enroll() {
        if(requesting)
            return;
        requesting = true;
        String uid = Utility.getPreference(this, "uid");
        String token = Utility.getPreference(this, "token");
        String base_url = Utility.getPreference(this, "base_url");
        String url = String.format("%s/api/learning_schedule/enrollCourse?uid=%s&token=%s&courseId=%d", base_url, uid, token, selected_level);
        String days = "";
        for(boolean select : days_status)
            days += (select ? "1" : "0");
        final String days_final = days;
        HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getInt("code") == 0) {
                    Intent it = new Intent(LevelSelcetionActivity.this, GeneratingCourseActivity.class);
                    it.putExtra("courseId", String.valueOf(selected_level));
                    it.putExtra("days", days_final);
                    startActivity(it);
                    finish();
                } else {
                    Toast.makeText(LevelSelcetionActivity.this, "Request Failed!", Toast.LENGTH_SHORT).show();
                    requesting = false;
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void init_LevelSelect() {
        String data = getIntent().getStringExtra("data");
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String course = jsonObject.getString("course");
                int courseId = jsonObject.getInt("courseId");
                boolean enrolled = jsonObject.getBoolean("enrolled");
                if(enrolled) {
                    if(course.contains("eginner") || course.contains("Basic")) {
                        findViewById(R.id.beginner_done).setVisibility(View.VISIBLE);
                        beginnerLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(LevelSelcetionActivity.this, "Already enrolled in this course.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if(course.contains("ntermediate")) {
                        findViewById(R.id.intermediate_done).setVisibility(View.VISIBLE);
                        intermediateLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(LevelSelcetionActivity.this, "Already enrolled in this course.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if(course.contains("dvanced")) {
                        findViewById(R.id.advanced_done).setVisibility(View.VISIBLE);
                        advancedLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(LevelSelcetionActivity.this, "Already enrolled in this course.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    if(course.contains("eginner") || course.contains("Basic")) {
                        setLevelClickListener(beginnerLayout, R.drawable.round_corner_yellow, R.drawable.round_corner_dark_cyan, courseId);
                    }
                    if(course.contains("ntermediate")) {
                        setLevelClickListener(intermediateLayout, R.drawable.round_corner_yellow, R.drawable.round_corner_dark_cyan, courseId);
                    }
                    if(course.contains("dvanced")) {
                        setLevelClickListener(advancedLayout, R.drawable.round_corner_yellow, R.drawable.round_corner_dark_cyan, courseId);
                    }
                }
            }
        } catch (JSONException e) {
            /*throw new RuntimeException(e);*/
            Toast.makeText(this, "Failed to fetch course data!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private void init_DaysClickListener() {
        for (int i = 0;i < days_select.length;i ++) {
            Button button = days_select[i];
            final int finalI = i;
            days_select[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!days_status[finalI]) {
                        button.setBackgroundResource(R.drawable.round_corner_yellow);
                        button.setTextColor(Color.BLACK);
                    } else {
                        button.setBackgroundResource(R.drawable.round_corner_dark_cyan);
                        button.setTextColor(Color.WHITE);
                    }
                    days_status[finalI] = !days_status[finalI];
                }
            });

        }
    }
    private void setLevelClickListener(final LinearLayout layout, final int selectedDrawable, final int defaultDrawable, final int courseId) {
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color;
                selected_level = courseId;
                layout.setBackgroundResource(selectedDrawable);
                if(selectedDrawable == R.drawable.round_corner_yellow) {
                    color = Color.BLACK;
                } else {
                    color = Color.WHITE;
                }
                TextView textView = (TextView) layout.getChildAt(1);
                textView.setTextColor(color);
                if(color == Color.WHITE)
                    color = Color.BLACK;
                else
                    color = Color.WHITE;
                if (layout != beginnerLayout) {
                    beginnerLayout.setBackgroundResource(defaultDrawable);
                    TextView mtextView = (TextView) beginnerLayout.getChildAt(1);
                    mtextView.setTextColor(color);
                }
                if (layout != intermediateLayout) {
                    intermediateLayout.setBackgroundResource(defaultDrawable);
                    TextView mtextView = (TextView) intermediateLayout.getChildAt(1);
                    mtextView.setTextColor(color);
                }
                if (layout != advancedLayout) {
                    advancedLayout.setBackgroundResource(defaultDrawable);
                    TextView mtextView = (TextView) advancedLayout.getChildAt(1);
                    mtextView.setTextColor(color);
                }
            }
        });
    }

    private void init_ReasonsClickListener() {
        for (int i = 0;i < reasonLayouts.length;i ++) {
            LinearLayout layout = reasonLayouts[i];
            final int finalI = i;
            reasonLayouts[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!reasonLayouts_status[finalI]) {
                        layout.setBackgroundResource(R.drawable.round_corner_yellow);
                        ((TextView)layout.getChildAt(0)).setTextColor(Color.BLACK);
                    } else {
                        layout.setBackgroundResource(R.drawable.round_corner_dark_cyan);
                        ((TextView)layout.getChildAt(0)).setTextColor(Color.WHITE);
                    }
                    reasonLayouts_status[finalI] = !reasonLayouts_status[finalI];
                }
            });

        }
    }
}