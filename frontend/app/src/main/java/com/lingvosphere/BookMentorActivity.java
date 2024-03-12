package com.lingvosphere;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.lingvosphere.Adapters.CalendarAdapter;
import com.lingvosphere.Fragments.MentorshipHubFragment;
import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;
import com.lingvosphere.model.CalendarItem;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookMentorActivity extends AppCompatActivity {

    private String selectedDate; // Variable to store the selected date
    private long todayInMillis = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mentor);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setMinDate(todayInMillis);

        Button confirmBtn = findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if a date has been selected
                if (selectedDate != null) {
                    // Handle the logic when the user clicks the confirm button after selecting a date
                    uploadDateToMongoDB(selectedDate);
                    Toast.makeText(BookMentorActivity.this, "Booking confirmed for " + selectedDate, Toast.LENGTH_SHORT).show();
                } else {
                    // Display a message if no date is selected
                    Toast.makeText(BookMentorActivity.this, "Please select a date first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Handle the selected date
                selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            }
        });
    }

    private void uploadDateToMongoDB(String date){
        String base_url = Utility.getPreference(BookMentorActivity.this,"base_url");
        String uid = Utility.getPreference(this,"uid");
        String token = Utility.getPreference(this,"token");
        String url = String.format("%s/api/mentorshiphub/creatementorsession",base_url,uid,date);

        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("token", token);
        params.put("date", date);

        // Make a POST request to your server
        HttpUtility.makePostRequest(url, params, new HttpUtility.HttpCallback() {
            @Override
            public void onSuccess(String response) throws JSONException {
//                Toast.makeText(getApplicationContext(), "Session booked successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                // Handle error, e.g., show an error message
                Toast.makeText(getApplicationContext(), "Failed to book session...", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
