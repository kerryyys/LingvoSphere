package com.lingvosphere.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.util.Util;
import com.lingvosphere.LearningActivity;
import com.lingvosphere.MainActivity;
import com.lingvosphere.R;
import com.lingvosphere.TestActivity;
import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;
import com.lingvosphere.model.ProgressCourseItem;
import com.lingvosphere.model.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;

public class LearningJourneyFragment extends Fragment {

    private View view;
    public LearningJourneyFragment() {

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_learning_journey, container, false);
        init_inProgress_course_list(view);
        init_todos(view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void update() {
        ((LinearLayout)view.findViewById(R.id.inprogress_list)).removeAllViews();
        ((LinearLayout)view.findViewById(R.id.todo_list)).removeAllViews();
        init_todos(view);
        init_inProgress_course_list(view);
    }

    private void init_inProgress_course_list(View view) {
        LinearLayout linearLayout = view.findViewById(R.id.inprogress_list);

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
                        view.findViewById(R.id.no_inprogress_hint).setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                TextView hint = view.findViewById(R.id.no_inprogress_hint);
                hint.setVisibility(View.VISIBLE);
                hint.setText("Network Error...");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init_todos(View view) {
        LinearLayout linearLayout = view.findViewById(R.id.todo_list);

        String uid = Utility.getPreference(view.getContext(), "uid");
        String token = Utility.getPreference(view.getContext(), "token");
        String base_url = Utility.getPreference(view.getContext(), "base_url");
        String timestamp = String.valueOf(Utility.LocalDate2Long(LocalDate.now())/1000);
        HttpUtility.makeGetRequest(base_url + String.format("/api/learning_schedule/getSchedulesByTimestamp?uid=%s&token=%s&scheduleTimestamp=%s", uid, token, timestamp), new HttpUtility.HttpCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray testsSchedule = jsonObject.getJSONArray("tests_schedule");
                    JSONArray chaptersSchedule = jsonObject.getJSONArray("chapters_schedule");

                    Task[] tasks = new Task[chaptersSchedule.length() + testsSchedule.length()];

                    for (int i = 0; i < testsSchedule.length(); i++) {
                        JSONObject exam = testsSchedule.getJSONObject(i);
                        String name = exam.getString("name");
                        String courseId = exam.getString("courseId");
                        String courseName = exam.getString("courseName");

                        Task task = new Task(LocalDate.now().toEpochDay(), name, Integer.parseInt(courseId), 0, courseName);
                        tasks[i + chaptersSchedule.length()] = task;
                    }


                    for (int i = 0; i < chaptersSchedule.length(); i++) {
                        JSONObject chapter = chaptersSchedule.getJSONObject(i);

                        String name = chapter.getString("name");
                        String courseId = chapter.getString("courseId");
                        String courseName = chapter.getString("courseName");

                        Task task = new Task(LocalDate.now().toEpochDay(), name, Integer.parseInt(courseId), 1, courseName);
                        tasks[i] = task;
                    }

                    for(Task task : tasks) {
                        ProgressCourseItem item = new ProgressCourseItem(Utility.getFlag(task.getCourse()), task.getName(), 0);

                        if (task.getType() == 0) {
                            // 加载自定义布局
                            View courseView = LayoutInflater.from(view.getContext()).inflate(R.layout.todo_test, linearLayout, false);

                            // 配置布局元素
                            TextView courseName = courseView.findViewById(R.id.testName);
                            courseName.setText(task.getCourse());
                            TextView progressText = courseView.findViewById(R.id.courseName_test);
                            progressText.setText(String.format("Please Finish `%s`", task.getName()));
                            courseView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent it = new Intent(view.getContext(), TestActivity.class);
                                    it.putExtra("courseId", String.valueOf(task.getId()));
                                    startActivity(it);
                                }
                            });

                            // 将配置好的视图添加到LinearLayout
                            linearLayout.addView(courseView);
                        } else {
                            View courseView = LayoutInflater.from(view.getContext()).inflate(R.layout.todo_chapter, linearLayout, false);

                            // 配置布局元素
                            TextView courseName = courseView.findViewById(R.id.courseName_chapter);
                            courseName.setText(task.getName());
                            TextView progressText = courseView.findViewById(R.id.chapterName);
                            progressText.setText(task.getCourse());
                            courseView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent it = new Intent(view.getContext(), LearningActivity.class);
                                    it.putExtra("courseId", String.valueOf(task.getId()));
                                    startActivity(it);
                                }
                            });

                            // 将配置好的视图添加到LinearLayout
                            linearLayout.addView(courseView);
                        }
                    }
                    if(tasks.length == 0)
                        view.findViewById(R.id.no_todo_hint).setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                TextView hint = view.findViewById(R.id.no_inprogress_hint);
                hint.setVisibility(View.VISIBLE);
                hint.setText("Network Error...");
            }
        });
    }
}
