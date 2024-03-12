package com.lingvosphere;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lingvosphere.Adapters.PostAdapter;
import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;
import com.lingvosphere.model.PostModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommunityCornerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    PostAdapter adapter;
    private List<PostModel> list;
    private FloatingActionButton createPostFAB;
    private TextView zero_post;
    private ImageButton closeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_corner);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#F7F7F7"));
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        decorView.setSystemUiVisibility(uiOptions);
        window.setNavigationBarColor(Color.parseColor("#ffffff"));

        init();

        loadDataFromMangoDB();

        onClick();
    }

    private void init() {

        recyclerView = findViewById(R.id.recyclerView);
        createPostFAB = findViewById(R.id.createPostFAB);
        zero_post = findViewById(R.id.zero_post);

        list = new ArrayList<>();
        adapter = new PostAdapter(list, this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        closeBtn = findViewById(R.id.closeBtn);
    }

    private void onClick() {

        createPostFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddPostActivity.class);
                startActivity(intent);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void loadDataFromMangoDB() {
        String uid = Utility.getPreference(this, "uid");
        String token = Utility.getPreference(this, "token");
        String base_url = Utility.getPreference(this, "base_url");

        HttpUtility.makeGetRequest(base_url + String.format("/api/user/getPost?uid=%s&token=%s", uid, token), new HttpUtility.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray dataArray = jsonObject.getJSONArray("posts");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject postObject = dataArray.getJSONObject(i);
                        String postId = postObject.getString("postId");
                        String username = postObject.getString("username");
                        String profile = postObject.getString("profile");

                        // Handle the possibility of null for imageUrl and description
                        String description = postObject.optString("postText", "no_postText"); // Provide a default value if null
                        String imageUrl = postObject.optString("imageUrl", "no_postImage"); // Provide a default value if null

                        PostModel postModel = new PostModel(username, profile, postId, imageUrl, description);
                        list.add(postModel);
                    }

                    if (list.isEmpty()) {
                        zero_post.setVisibility(View.VISIBLE);
                    }

                    adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed

                } catch (JSONException e) {
                    Toast.makeText(CommunityCornerActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CommunityCornerActivity.this, "Error in fetching posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}