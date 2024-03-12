package com.lingvosphere;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.lingvosphere.Adapters.PostAdapter;
import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;
import com.yalantis.ucrop.UCrop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    Button pick_image_button;
    ImageButton backBtn, nextBtn;
    EditText descriptionEt;
    ImageView iv_pick_image;
    ActivityResultLauncher<String> mGetContent;
    Uri resultUri = null;
    Dialog dialog;
    PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.green));
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(Color.TRANSPARENT);

        init();

        onClickListener();

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent = new Intent(getApplicationContext(), CropperActivity.class);
                intent.putExtra("DATA", result.toString());
                startActivityForResult(intent, 101);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Set image at the preview
        if (resultCode == -1 && requestCode == 101) {

            String result = data.getStringExtra("RESULT");
            if (result != null) {
                resultUri = Uri.parse(result);
            }

            iv_pick_image.setBackground(new ColorDrawable(Color.parseColor("#FFFFFF")));
            iv_pick_image.setImageURI(resultUri);
//            nextBtn.setVisibility(View.VISIBLE);

        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, UCrop.getError(data).toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {

        iv_pick_image = findViewById(R.id.iv_pick_image);
        backBtn = findViewById(R.id.closeBtn);
        nextBtn = findViewById(R.id.nextBtn);
        descriptionEt = findViewById(R.id.descriptionEt);
        pick_image_button = findViewById(R.id.pick_image_button);

        // The loading animation
        dialog = new Dialog(AddPostActivity.this);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.dialog_bg, null));
        dialog.setCancelable(false);

        adapter = new PostAdapter(new ArrayList<>(), getApplicationContext());


    }

    private void onClickListener() {

        // Add image on create post page
        pick_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch all types of images
                mGetContent.launch("image/*");
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Button to upload the post
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
                String postText = "";
                String imageUri = "";

                try {
                    postText = descriptionEt.getText().toString();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                try {
                    imageUri = resultUri.toString();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                if (postText.isEmpty() && imageUri.isEmpty()) {
                    Toast.makeText(AddPostActivity.this, "Image and captions are empty", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    uploadPostToMangoDB(postText, imageUri);
                }
            }
        });
    }

    private void uploadPostToMangoDB(String postText, String imageUrl) {
        // Assuming you have uid and token stored in preferences
        String uid = Utility.getPreference(getApplicationContext(), "uid");
        String token = Utility.getPreference(getApplicationContext(), "token");
        String base_url = Utility.getPreference(getApplicationContext(), "base_url");

        // Create a map to hold parameters
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("token", token);

        // Add postText to params if not null or empty
        if (postText != null && !postText.isEmpty()) {
            params.put("postText", postText);
        }

        // Add imageUrl to params if not null or empty
        if (imageUrl != null && !imageUrl.isEmpty()) {
            params.put("imageUrl", imageUrl);
        }

        // Make a POST request to your server
        HttpUtility.makePostRequest(base_url + "/api/user/createPost", params, new HttpUtility.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getApplicationContext(), "Post created successfully!", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                dialog.dismiss();
                finish();
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
                // Handle error, e.g., show an error message
                Toast.makeText(getApplicationContext(), "Failed to create post...", Toast.LENGTH_SHORT).show();
            }
        });
    }

}