package com.lingvosphere;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.lingvosphere.Adapters.CalendarAdapter;
import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;
import com.lingvosphere.model.CalendarItem;
import com.lingvosphere.model.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class LearningTimelineActivity extends AppCompatActivity {
    public static int offset = 0;
    private static CalendarAdapter adapter;
    public static TextView selected_date;
    public static TextView calendar_month_text;
    private static TextView no_schedule_hint;
    private static LinearLayout task_list;
    private static LinearLayout booking_list;
    public static int selected;

    public static Task[] tasks;
    public static Task[] bookings;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_timeline);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#f7f7f7"));
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

        LocalDate today = LocalDate.now();
        RecyclerView recyclerView = findViewById(R.id.calendarRecyclerView); // Replace with your RecyclerView ID
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7)); // 7 items per row
        calendar_month_text = this.findViewById(R.id.calendar_month_text);
        calendar_month_text.setText(today.getMonth().name().charAt(0) + today.getMonth().name().substring(1).toLowerCase() + " " + String.valueOf(today.getYear()));
        adapter = new CalendarAdapter(pad_dates(offset)); // Replace with your data list
        recyclerView.setAdapter(adapter);
        selected_date = findViewById(R.id.selected_date);
        no_schedule_hint = findViewById(R.id.no_schedule_hint);
        task_list = findViewById(R.id.schedule_list);
        booking_list = findViewById(R.id.booking_list);
        selected = today.getDayOfMonth();
        load_tasks();
        updateTasks(today);

        findViewById(R.id.new_schedule_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivitySelectDialog dialog = new ActivitySelectDialog();
                String uid = Utility.getPreference(calendar_month_text.getContext(), "uid");
                String token = Utility.getPreference(calendar_month_text.getContext(), "token");
                String base_url = Utility.getPreference(calendar_month_text.getContext(), "base_url");
                String url = String.format("%s/api/learning_schedule/getActivities?uid=%s&token=%s", base_url, uid, token);
                HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
                    @Override
                    public void onSuccess(String response) throws JSONException {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            int code = jsonObject.getInt("code");
                            if (code != 0)
                                return;

                            JSONArray testsSchedule = jsonObject.getJSONArray("tests");
                            JSONArray chaptersSchedule = jsonObject.getJSONArray("chapters");

                            Task[] tasks = new Task[chaptersSchedule.length() + testsSchedule.length()];

                            for (int i = 0; i < testsSchedule.length(); i++) {
                                JSONObject exam = testsSchedule.getJSONObject(i);
                                String name = exam.getString("name");
                                String courseId = exam.getString("courseId");
                                String courseName = exam.getString("courseName");

                                Task task = new Task(LocalDate.now().toEpochDay(), name, Integer.parseInt(courseId), 0, courseName);
                                tasks[i] = task;
                            }


                            for (int i = 0; i < chaptersSchedule.length(); i++) {
                                JSONObject chapter = chaptersSchedule.getJSONObject(i);

                                String name = chapter.getString("name");
                                String courseId = chapter.getString("courseId");
                                String courseName = chapter.getString("courseName");

                                Task task = new Task(LocalDate.now().toEpochDay(), name, Integer.parseInt(courseId), 1, courseName);
                                tasks[i + testsSchedule.length()] = task;
                            }

                            for(Task task : tasks) {
                                View taskView = LayoutInflater.from(LearningTimelineActivity.no_schedule_hint.getContext()).inflate(R.layout.activity_item, task_list, false);

                                // 配置布局元素
                                TextView courseName = taskView.findViewById(R.id.task_course_name);
                                TextView taskName = taskView.findViewById(R.id.task_name);
                                courseName.setText(task.getCourse());
                                taskName.setText((task.getType() == 0 ? "Test: " : "Chapter: ") + task.getName());
                                ImageView flag = taskView.findViewById(R.id.task_flag);
                                flag.setImageResource(Utility.getFlag(task.getCourse()));
                                taskView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        LocalDate time_selected = LocalDate.from(LocalDate.now().minusMonths(-LearningTimelineActivity.offset).withDayOfMonth(selected));
                                        long time_selected_second = Utility.LocalDate2Long(time_selected);
                                        long time_selected_day_end = Utility.LocalDate2Long(LocalDate.from(time_selected.withDayOfMonth(selected+1).atStartOfDay()));
                                        for(int i = LearningTimelineActivity.tasks.length - 1;i > 0;i --) {
                                            if(LearningTimelineActivity.tasks[i].getCourse().equals(task.getCourse()) && LearningTimelineActivity.tasks[i].getType() == task.getType()) {
                                                if(LearningTimelineActivity.tasks[i].getTimestamp() >= time_selected_day_end) {
                                                    Toast.makeText(view.getContext(), "Please add this activity after other activities of this course.", Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                            }
                                        }

                                        if(time_selected_second < Utility.LocalDate2Long(LocalDate.now())) {
                                            Toast.makeText(view.getContext(), "You can't set an activity in the past.", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        String epochSecond = String.valueOf(time_selected_second/1000);
                                        String uid = Utility.getPreference(LearningTimelineActivity.no_schedule_hint.getContext(), "uid");
                                        String token = Utility.getPreference(LearningTimelineActivity.no_schedule_hint.getContext(), "token");
                                        String courseId = String.valueOf(task.getId());
                                        String type = String.valueOf(task.getType());
                                        String base_url = Utility.getPreference(LearningTimelineActivity.no_schedule_hint.getContext(), "base_url");
                                        String url = String.format("%s/api/learning_schedule/setSchedule?uid=%s&token=%s&courseId=%s&type=%s&scheduleTimestamp=%s", base_url, uid, token, courseId, type, epochSecond);
                                        HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
                                            @Override
                                            public void onSuccess(String response) throws JSONException {
                                                JSONObject jsonObject = new JSONObject(response);
                                                if(jsonObject.getInt("code") == 0) {
                                                    Toast.makeText(view.getContext(), "Schedule added successfully.", Toast.LENGTH_LONG).show();
                                                    dialog.close();
                                                    adapter.setmCalendarData(pad_dates(offset));
                                                    load_tasks();
                                                    showTasks(selected);
                                                } else {
                                                    Toast.makeText(view.getContext(), "Operation Failed.", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onError(String error) {
                                                Toast.makeText(view.getContext(), "Operation Failed.", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });
                                dialog.AddItem(taskView);
                            }
                            dialog.show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        });

        findViewById(R.id.calendar_previous_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LearningTimelineActivity.offset -= 1;
                adapter.setmCalendarData(pad_dates(offset));
                calendar_month_text.setText(today.minusMonths(-offset).getMonth().name().charAt(0) + today.minusMonths(-offset).getMonth().name().substring(1).toLowerCase() + " " + String.valueOf(today.minusMonths(-offset).getYear()));
                selected = 1;
                showTasks(selected);
            }
        });

        findViewById(R.id.calendar_next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LearningTimelineActivity.offset += 1;
                adapter.setmCalendarData(pad_dates(offset));
                calendar_month_text.setText(today.minusMonths(-offset).getMonth().name().charAt(0) + today.minusMonths(-offset).getMonth().name().substring(1).toLowerCase() + " " + String.valueOf(today.minusMonths(-offset).getYear()));
                selected = 1;
                showTasks(selected);
            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static CalendarItem[] pad_dates(int offset) {
        CalendarItem[] res = new CalendarItem[49];
        res[0] = new CalendarItem("S", CalendarItem.TYPE_NULL);
        res[1] = new CalendarItem("M", CalendarItem.TYPE_NULL);
        res[2] = new CalendarItem("T", CalendarItem.TYPE_NULL);
        res[3] = new CalendarItem("W", CalendarItem.TYPE_NULL);
        res[4] = new CalendarItem("T", CalendarItem.TYPE_NULL);
        res[5] = new CalendarItem("F", CalendarItem.TYPE_NULL);
        res[6] = new CalendarItem("S", CalendarItem.TYPE_NULL);

        LocalDate today;
        if(offset != 0)
            today = LocalDate.now().minusMonths(-offset);
        else
            today = LocalDate.now();

        int dayOfWeek_firstDay = today.withDayOfMonth(1).getDayOfWeek().getValue()%7;
        int daysInMonth = today.lengthOfMonth();
        int daysInLastMonth = today.minusMonths(1).lengthOfMonth();
        int difference = dayOfWeek_firstDay ;
        int idx = 7;
        for(int i = 1;i <= difference;i ++) {
            CalendarItem calendarItem = new CalendarItem(String.valueOf(daysInLastMonth - (difference - i)), 0);
            res[idx++] = calendarItem;
        }
        for(int i = 1;i <= daysInMonth;i ++) {
            CalendarItem calendarItem = new CalendarItem(String.valueOf(i), CalendarItem.TYPE_IN_MONTH);

            if(i == selected)
                calendarItem.setType(CalendarItem.TYPE_IN_MONTH | CalendarItem.TYPE_FOCUSED);
            res[idx++] = calendarItem;
        }
        int pad = 49 - idx;
        for(int i = 1;i <= pad;i ++) {
            CalendarItem calendarItem = new CalendarItem(String.valueOf(i), 0);
            res[idx++] = calendarItem;
        }

        return res;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void updateTasks(LocalDate today) {
        selected_date.setText(" - " + today.getDayOfWeek().name().charAt(0) + today.getDayOfWeek().name().substring(1,3).toLowerCase() + " " + today.getMonth().name().charAt(0) + today.getMonth().name().substring(1).toLowerCase() + " " + String.valueOf(today.getDayOfMonth()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void load_tasks() {
        String uid = Utility.getPreference(LearningTimelineActivity.no_schedule_hint.getContext(), "uid");
        String token = Utility.getPreference(LearningTimelineActivity.no_schedule_hint.getContext(), "token");
        String timestamp = String.valueOf(LocalDate.now().toEpochDay());
        String base_url = Utility.getPreference(LearningTimelineActivity.no_schedule_hint.getContext(), "base_url");
        String url = String.format("%s/api/learning_schedule/getSchedules?uid=%s&token=%s&scheduleTimestamp=%s", base_url, uid, token, timestamp);
        HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
            @Override
            public void onSuccess(String response) throws JSONException {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    int code = jsonObject.getInt("code");
                    if(code != 0)
                        return;

                    JSONArray testsSchedule = jsonObject.getJSONArray("tests_schedule");
                    JSONArray chaptersSchedule = jsonObject.getJSONArray("chapters_schedule");

                    Task[] tasks = new Task[chaptersSchedule.length()+testsSchedule.length()];

                    for (int i = 0; i < testsSchedule.length(); i++) {
                        JSONObject exam = testsSchedule.getJSONObject(i);
                        String name = exam.getString("name");
                        long timestamp = exam.getLong("timestamp");
                        String courseId = exam.getString("courseId");
                        String courseName = exam.getString("courseName");

                        Task task = new Task(timestamp, name, Integer.parseInt(courseId), 0, courseName);
                        tasks[i] = task;
                    }


                    for (int i = 0; i < chaptersSchedule.length(); i++) {
                        JSONObject chapter = chaptersSchedule.getJSONObject(i);

                        String name = chapter.getString("name");
                        long timestamp = chapter.getLong("timestamp");
                        String courseId = chapter.getString("courseId");
                        String courseName = chapter.getString("courseName");

                        Task task = new Task(timestamp, name, Integer.parseInt(courseId), 1, courseName);
                        tasks[i+testsSchedule.length()] = task;
                    }

                    LearningTimelineActivity.tasks = tasks;
                    LearningTimelineActivity.showTasks(selected);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                System.out.println(error);
            }
        });

        url = String.format("%s/api/mentorshiphub/getmentorsession?uid=%s&token=%s", base_url, uid, token);
        HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
            @Override
            public void onSuccess(String response) throws JSONException {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getInt("code") == 0) {
                    JSONArray jsonArray = jsonObject.getJSONArray("sessions");
                    bookings = new Task[jsonArray.length()];
                    for(int i = 0;i < jsonArray.length();i ++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        String timestamp = json.getString("date");
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = null;
                        try {
                            date = sdf.parse(timestamp);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        bookings[i] = new Task(date.getTime(), "Mentor Booking", -1,2, "HinaKo N.");
                    }
                    show_bookings(selected);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void showTasks(int select) {
        task_list.removeAllViews();
        no_schedule_hint.setVisibility(View.GONE);
        for(Task task : tasks) {
            View taskView = LayoutInflater.from(LearningTimelineActivity.no_schedule_hint.getContext()).inflate(R.layout.layout_task_item, task_list, false);

            // 配置布局元素
            TextView courseName = taskView.findViewById(R.id.task_course_name);
            TextView taskName = taskView.findViewById(R.id.task_name);
            courseName.setText(task.getCourse());
            taskName.setText((task.getType() == 0 ? "Test: " : "Chapter: ") + task.getName());
            ImageView flag = taskView.findViewById(R.id.task_flag);
            ImageView del_btn = taskView.findViewById(R.id.delete_schedule);
            flag.setImageResource(Utility.getFlag(task.getCourse()));

            del_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int i = tasks.length - 1;i > 0;i --) {
                        if(tasks[i].getCourse().equals(task.getCourse())) {
                            if(task.getName().equals(tasks[i].getName()) && task.getType() == tasks[i].getType()) {
                                break;
                            } else {
                                Toast.makeText(view.getContext(), "Please remove the last activity of this course first!", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }

                    String uid = Utility.getPreference(LearningTimelineActivity.no_schedule_hint.getContext(), "uid");
                    String token = Utility.getPreference(LearningTimelineActivity.no_schedule_hint.getContext(), "token");
                    String courseId = String.valueOf(task.getId());
                    String type = String.valueOf(task.getType());
                    String base_url = Utility.getPreference(LearningTimelineActivity.no_schedule_hint.getContext(), "base_url");
                    String url = String.format("%s/api/learning_schedule/removeSchedule?uid=%s&token=%s&courseId=%s&type=%s", base_url, uid, token, courseId, type);
                    HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
                        @Override
                        public void onSuccess(String response) throws JSONException {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getInt("code") == 0) {
                                Toast.makeText(view.getContext(), "Deletion Success.", Toast.LENGTH_LONG).show();
                                adapter.setmCalendarData(pad_dates(offset));
                                load_tasks();
                            } else {
                                Toast.makeText(view.getContext(), "Operation Failed:" + jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(view.getContext(), "Operation Failed.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });



            LocalDate time_selected = LocalDate.from(LocalDate.now().minusMonths(-LearningTimelineActivity.offset).withDayOfMonth(select));
            int days_in_month = LocalDate.now().minusMonths(-LearningTimelineActivity.offset).lengthOfMonth();
            LocalDate time_start = LocalDate.from(LocalDate.now().minusMonths(-LearningTimelineActivity.offset).withDayOfMonth(select).atStartOfDay());
            LocalDate time_end = LocalDate.from(LocalDate.now().minusMonths(-LearningTimelineActivity.offset).withDayOfMonth(select+1).atStartOfDay());
            long time_s = Utility.LocalDate2Long(time_start);
            long time_e = Utility.LocalDate2Long(time_end);
            long epochMilli = task.getTimestamp() * 1000;
            if(epochMilli >= time_s && epochMilli < time_e) {
                LearningTimelineActivity.task_list.addView(taskView);
            }
            for(int i = 1;i <= days_in_month;i ++) {

                LocalDate time_iteration = LocalDate.from(LocalDate.now().minusMonths(-LearningTimelineActivity.offset).withDayOfMonth(i));
                LocalDate time_it_start = LocalDate.from(time_iteration.atStartOfDay());
                LocalDate time_it_end = LocalDate.from(time_iteration.minusDays(-1).atStartOfDay());

                long time_it_s = Utility.LocalDate2Long(time_it_start);
                long time_it_e = Utility.LocalDate2Long(time_it_end);

                if(epochMilli >= time_it_s && epochMilli < time_it_e) {
                    adapter.setType(adapter.getType(time_iteration.getDayOfMonth()) | CalendarItem.TYPE_SELECTED, time_iteration.getDayOfMonth());
                }

            }
        }
        if(task_list.getChildCount() == 0)
            no_schedule_hint.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void show_bookings(int select) {
        booking_list.removeAllViews();
        no_schedule_hint.setVisibility(View.GONE);
        boolean task_today = false;
        for(Task task : bookings) {
            View taskView = LayoutInflater.from(LearningTimelineActivity.no_schedule_hint.getContext()).inflate(R.layout.layout_task_item, task_list, false);

            // 配置布局元素
            TextView courseName = taskView.findViewById(R.id.task_name);
            TextView taskName = taskView.findViewById(R.id.task_course_name);
            courseName.setText(task.getCourse());
            taskName.setText(task.getName());
            ImageView flag = taskView.findViewById(R.id.task_flag);
            flag.setImageResource(R.drawable.flag_jp);

            LocalDate time_selected = LocalDate.from(LocalDate.now().minusMonths(-LearningTimelineActivity.offset).withDayOfMonth(select));
            int days_in_month = LocalDate.now().minusMonths(-LearningTimelineActivity.offset).lengthOfMonth();
            LocalDate time_start = LocalDate.from(LocalDate.now().minusMonths(-LearningTimelineActivity.offset).withDayOfMonth(select).atStartOfDay());
            LocalDate time_end = LocalDate.from(LocalDate.now().minusMonths(-LearningTimelineActivity.offset).withDayOfMonth(select+1).atStartOfDay());
            long time_s = Utility.LocalDate2Long(time_start);
            long time_e = Utility.LocalDate2Long(time_end);
            long epochMilli = task.getTimestamp();
            if(epochMilli >= time_s && epochMilli < time_e) {
                LearningTimelineActivity.booking_list.addView(taskView);
            }
            for(int i = 1;i <= days_in_month;i ++) {

                LocalDate time_iteration = LocalDate.from(LocalDate.now().minusMonths(-LearningTimelineActivity.offset).withDayOfMonth(i));
                LocalDate time_it_start = LocalDate.from(time_iteration.atStartOfDay());
                LocalDate time_it_end = LocalDate.from(time_iteration.minusDays(-1).atStartOfDay());

                long time_it_s = Utility.LocalDate2Long(time_it_start);
                long time_it_e = Utility.LocalDate2Long(time_it_end);

                if(epochMilli >= time_it_s && epochMilli < time_it_e) {
                    adapter.setType(adapter.getType(time_iteration.getDayOfMonth()) | CalendarItem.TYPE_SELECTED, time_iteration.getDayOfMonth());
                }

            }
        }
        if(task_list.getChildCount() == 0)
            no_schedule_hint.setVisibility(View.VISIBLE);
    }

    private class ActivitySelectDialog {
        // 创建Dialog实例
        private Dialog dialog;
        private LinearLayout list;

        public ActivitySelectDialog() {
            dialog = new Dialog(LearningTimelineActivity.this);
            // 设置自定义布局
            dialog.setContentView(R.layout.dialog_activity_select);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // 使用MATCH_PARENT或具体的像素值
            window.setAttributes(layoutParams);

            // 初始化布局中的元素
            TextView text = (TextView) dialog.findViewById(R.id.textView);
            Button dismissButton = (Button) dialog.findViewById(R.id.buttonDismiss);
            list = dialog.findViewById(R.id.activity_list);

            // 设置关闭按钮的点击事件
            dismissButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        public void AddItem(View view) {
            list.addView(view);
        }

        public void show() {
            dialog.show();
        }

        public void close() {
            dialog.dismiss();
        }

    }

}