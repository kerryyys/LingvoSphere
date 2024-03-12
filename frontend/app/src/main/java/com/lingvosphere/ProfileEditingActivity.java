package com.lingvosphere;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.api.Http;
import com.lingvosphere.Adapters.PostAdapter;
import com.lingvosphere.Fragments.HomeFragment;
import com.lingvosphere.Fragments.ProfileFragment;
import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ProfileEditingActivity extends AppCompatActivity {
    ActivityResultLauncher<String> mGetContent;
    TextView SelectPhoto_btn;
    Button submit_btn;
    ImageButton back_btn;
    EditText ETUsername;
    Uri resultUri = null;
    ImageView profile_picture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editing);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary_color));
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

    private void init(){
        SelectPhoto_btn = this.findViewById(R.id.TVChange_pic);
         submit_btn= this.findViewById(R.id.submit_btn);
         back_btn = this.findViewById(R.id.back_btn);
         ETUsername = this.findViewById(R.id.ETUsername);
        profile_picture = this.findViewById(R.id.profile_picture);

    }

    private void onClickListener(){
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //retrieve new username
                String username = ETUsername.getText().toString().trim();
                if(username.isEmpty()){
                    Toast.makeText(ProfileEditingActivity.this,"Username cannot leave empty!",Toast.LENGTH_SHORT).show();
                }else{
                    SetUname(username);
                    setProfile(String.valueOf(resultUri));
                }
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        SelectPhoto_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch all types of images
                mGetContent.launch("image/*");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Set image at the preview
        if (resultCode == RESULT_OK && requestCode == 101 &&data!=null) {
            String result = data.getStringExtra("RESULT");
            //initialise Uri
             resultUri = Uri.parse(result);
             profile_picture.setImageURI(resultUri);

        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, UCrop.getError(data).toString(), Toast.LENGTH_SHORT).show();
        }
    }
    //upload profile to mongoDB
    private void setProfile(final String newProfile) {
        try {
            String uid = Utility.getPreference(getApplicationContext(), "uid");
            String token = Utility.getPreference(getApplicationContext(), "token");
            String base_url = Utility.getPreference(getApplicationContext(), "base_url");
            String url = String.format("%s/api/user/setProfile?uid=%s&token=%s&profile=%s",base_url,uid,token,newProfile);

            // Make a GET request
            HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
                @Override
                public void onSuccess(String response) throws JSONException {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    if(code==0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Update UI
                                Picasso.get().load(newProfile).into(profile_picture);

                                // Set result for the calling activity
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("newProfileUrl", newProfile);
                                setResult(RESULT_OK, resultIntent);

                                // Notify the ProfileFragment about the change
                                notifyProfileFragment(newProfile);

                                // Finish the ProfileEditingActivity
                                finish();
                                Intent homeIntent = new Intent(ProfileEditingActivity.this, MainActivity.class);
                                startActivity(homeIntent);
                                finish();
                            }
                        });
                    }
                    Toast.makeText(getApplicationContext(), "success changed", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onError(String error) {
                    Toast.makeText(getApplicationContext(), "Failed to change profile picture", Toast.LENGTH_SHORT).show();
                    // Dismiss any dialogs in case of an error
                    // dialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void notifyProfileFragment(String newProfileUrl) {
        // Check if the fragment is attached to the activity
        ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("PrfoileFragment");
        if (profileFragment != null) {
            // Notify the fragment about the profile change
            Picasso.get().load(newProfileUrl).into(profile_picture);
        }
    }
    private void SetUname(String newUname){
        try {
            String base_url = Utility.getPreference(ProfileEditingActivity.this,"base_url");
            String uid = Utility.getPreference(this,"uid");
            String token = Utility.getPreference(this,"token");
            String uname = Utility.getPreference(this,"uname");
            String url = String.format("%s/api/user/setUname?uid=%s&token=%s&uname=%s",base_url,uid,token,newUname);
            HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
                @Override
                public void onSuccess(String response) throws JSONException {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    if(code==0){ //successful
                        //update local data
                        Utility.setPreference(ProfileEditingActivity.this,"uname",newUname);

                        //Update UI
                        runOnUiThread(()-> {
                                    TextView usernameTV = findViewById(R.id.username);
                                    if (usernameTV != null) {
                                        usernameTV.setText(newUname);

                                    }
                                });
                        Toast.makeText(ProfileEditingActivity.this, "Username set successfully!", Toast.LENGTH_SHORT).show();
                        // Set result for the calling activity
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("newUsername", newUname);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                        Intent homeIntent = new Intent(ProfileEditingActivity.this, MainActivity.class);
                        startActivity(homeIntent);
                        finish();


                    }else{ //fail
                        Toast.makeText(ProfileEditingActivity.this,"Failed to set username, please try again!",Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onError(String error) {
                    Toast.makeText(ProfileEditingActivity.this, "Error :(", Toast.LENGTH_SHORT);

                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}