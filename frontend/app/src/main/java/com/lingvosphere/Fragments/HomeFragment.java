package com.lingvosphere.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.lingvosphere.CourseEnrollActivity;
import com.lingvosphere.LearningTimelineActivity;
import com.lingvosphere.R;
import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;
import com.lingvosphere.model.ProgressCourseItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private Button enroll_new_button;
    private ArrayList<ProgressCourseItem> inProgressCourses;

    private View view;
    public HomeFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_homepage, container, false);
        inProgressCourses = new ArrayList<>();

        enroll_new_button = view.findViewById(R.id.enroll_new_button);
        enroll_new_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getContext(), CourseEnrollActivity.class);
                startActivity(it);
            }
        });
        init_inProgress_course_list(view);
        init_enrolled_courses(view);
        return view;
    }

    public void update() {
        ((LinearLayout)view.findViewById(R.id.home_in_progress_list)).removeAllViews();
        ((LinearLayout)view.findViewById(R.id.enrolled_course_list)).removeAllViews();
        init_enrolled_courses(view);
        init_inProgress_course_list(view);
    }

    private void init_inProgress_course_list(View view) {
        LinearLayout linearLayout = view.findViewById(R.id.home_in_progress_list);

        String uid = Utility.getPreference(view.getContext(), "uid");
        String token = Utility.getPreference(view.getContext(), "token");
        String base_url = Utility.getPreference(view.getContext(), "base_url");
        HttpUtility.makeGetRequest(base_url + String.format("/api/learning_schedule/getCourseStatus?uid=%s&token=%s", uid, token), new HttpUtility.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray dataArray = jsonObject.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject courseObject = dataArray.getJSONObject(i);
                        String course = courseObject.getString("course");
                        int progress = courseObject.getInt("progress");
                        String chapter = courseObject.getString("chapter");

                        ProgressCourseItem item = new ProgressCourseItem(Utility.getFlag(course), chapter, progress);

                        // 加载自定义布局
                        View courseView = LayoutInflater.from(view.getContext()).inflate(R.layout.inprogress_course_item, linearLayout, false);

                        // 配置布局元素
                        TextView courseName = courseView.findViewById(R.id.inprogress_course_name);
                        courseName.setText(course);
                        TextView progressText = courseView.findViewById(R.id.inprogress_course_progress_text);
                        progressText.setText(chapter + " - " + String.valueOf(progress) + "%");
                        ProgressBar progressBar = courseView.findViewById(R.id.inprogress_course_progress_bar);
                        progressBar.setMax(100);
                        progressBar.setProgress(/*progress*/50);
                        ImageView flag = courseView.findViewById(R.id.inprogress_course_flag);
                        flag.setImageResource(item.getFlagResource());

                        courseView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Go to Learning Page;
                            }
                        });

                        // 将配置好的视图添加到LinearLayout
                        linearLayout.addView(courseView);
                    }

                    if(dataArray.length() == 0)
                        view.findViewById(R.id.no_course_hint).setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                TextView hint = view.findViewById(R.id.no_course_hint);
                hint.setVisibility(View.VISIBLE);
                hint.setText("Network Error...");
            }
        });
    }

    private void init_enrolled_courses(View view) {
        LinearLayout linearLayout = view.findViewById(R.id.enrolled_course_list);

        String uid = Utility.getPreference(view.getContext(), "uid");
        String token = Utility.getPreference(view.getContext(), "token");
        String base_url = Utility.getPreference(view.getContext(), "base_url");
        HttpUtility.makeGetRequest(base_url + String.format("/api/learning_schedule/getEnrolledCourses?uid=%s&token=%s", uid, token), new HttpUtility.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray dataArray = jsonObject.getJSONArray("data");

                    for (int i = 0; i < dataArray.length(); i++) {
                        String course = dataArray.getString(i);

                        ProgressCourseItem item = new ProgressCourseItem(Utility.getFlag(course), "", 0);

                        // 加载自定义布局
                        View courseView = LayoutInflater.from(view.getContext()).inflate(R.layout.enrolled_course_item, linearLayout, false);

                        // 配置布局元素
                        TextView courseName = courseView.findViewById(R.id.enrolled_Course_name);
                        courseName.setText(course);
                        ImageView flag = courseView.findViewById(R.id.enrolled_course_flag);
                        flag.setImageResource(item.getFlagResource());
                        courseView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent it = new Intent(view.getContext(), LearningTimelineActivity.class);
                                startActivity(it);
                            }
                        });
                        // 将配置好的视图添加到LinearLayout
                        linearLayout.addView(courseView);

                    }

                    if(dataArray.length() == 0)
                        view.findViewById(R.id.no_enrollment_hint).setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                TextView hint = view.findViewById(R.id.no_enrollment_hint);
                hint.setVisibility(View.VISIBLE);
                hint.setText("Network Error...");
            }
        });
    }
}
