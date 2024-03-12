package com.lingvosphere;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lingvosphere.Adapters.CommentAdapter;
import com.lingvosphere.model.CommentModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    EditText commentEt;
    LinearLayout no_commentTv;
    ImageButton sendBtn, backBtn;
    RecyclerView recyclerView;
    CommentAdapter commentAdapter;
    List<CommentModel> commentList;
    String postId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#F7F7F7"));
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        decorView.setSystemUiVisibility(uiOptions);
        window.setNavigationBarColor(Color.TRANSPARENT);

        // Retrieve postId from the intent
        Intent intent = getIntent();
        if (intent != null) {
            postId = intent.getStringExtra("postId");
        }

        init();

        fetchCommentsFromMongoDB(postId);

        clickListener();
    }

    private void clickListener() {

        sendBtn.setOnClickListener(v -> {

            String comment = commentEt.getText().toString();

            if (comment.isEmpty()) {
                Toast.makeText(getApplication(), "Enter comment", Toast.LENGTH_SHORT).show();
                return;
            } else {
                uploadCommentToMongoDB(postId, comment);
                commentEt.setText("");
                fetchCommentsFromMongoDB(postId);
                recyclerView.scrollToPosition(commentList.size() - 1);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void fetchCommentsFromMongoDB(String postId) {

        String uid = Utility.getPreference(this, "uid");
        String token = Utility.getPreference(this, "token");
        String base_url = Utility.getPreference(this, "base_url");

        HttpUtility.makeGetRequest(base_url + String.format("/api/user/getComments?uid=%s&token=%s&postId=%s", uid, token, postId), new HttpUtility.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    // Clear the commentList before adding new comments
                    commentList.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray commentsArray = jsonObject.getJSONArray("comments");

                    for (int i = 0; i < commentsArray.length(); i++) {
                        JSONObject commentObject = commentsArray.getJSONObject(i);
                        String userId = commentObject.getString("uid");
                        String commentText = commentObject.getString("commentText");
                        String username = commentObject.getString("username");
                        String profile = commentObject.getString("profile");

                        CommentModel comment = new CommentModel(username, profile, commentText);
                        commentList.add(comment);
                    }

                    // Update the RecyclerView adapter with the loaded comments
                    commentAdapter.notifyDataSetChanged();

                    if (commentList.size() == 0) {
                        no_commentTv.setVisibility(View.VISIBLE);
                    } else {
                        no_commentTv.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    Toast.makeText(CommentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CommentActivity.this, "Error in fetching comments.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadCommentToMongoDB(String postId, String commentText) {
        // Assuming you have uid and token stored in preferences
        String uid = Utility.getPreference(this, "uid");
        String token = Utility.getPreference(this, "token");
        String base_url = Utility.getPreference(this, "base_url");

        // Create a map to hold parameters
        Map<String, String> params = new HashMap<>();
        params.put("postId", postId);
        params.put("uid", uid);
        params.put("token", token);
        params.put("commentText", commentText);

        // Make a POST request to your server
        HttpUtility.makePostRequest(base_url + "/api/user/addComment", params, new HttpUtility.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                // Handle success, e.g., show a success message
                Toast.makeText(CommentActivity.this, "Comment added successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                // Handle error, e.g., show an error message
                Toast.makeText(CommentActivity.this, "Failed to add comment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {

        no_commentTv = findViewById(R.id.no_commentTv);
        commentEt = findViewById(R.id.commentET);
        sendBtn = findViewById(R.id.sendBtn);
        backBtn = findViewById(R.id.closeBtn);
        recyclerView = findViewById(R.id.commentRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        recyclerView.setAdapter(commentAdapter);

    }
}