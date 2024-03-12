package com.lingvosphere;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {
    public static int result = -1;
    public static String msg = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        setContentView(R.layout.activity_splash);

        Utility.setPreference(this, "base_url", "http://192.168.1.226:1024");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(result == 0) {
                    Intent it = new Intent(SplashActivity.this, WelcomePageActivity.class);
                    startActivity(it);
                    finish();
                }
                if(result == 1) {
                    Intent it = new Intent(SplashActivity.this, LoginPageActivity.class);
                    startActivity(it);
                    finish();
                }
                if(result == 2) {
                    Intent it = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(it);
                    finish();
                }
            }
        }, 2000);

        String uid = Utility.getPreference(this, "uid");
        String token = Utility.getPreference(this, "token");
        if(uid==null || token=="")
            SplashActivity.result = 1; // login needed

        if(Utility.getPreference(SplashActivity.this, "init_sign") == null)
            SplashActivity.result = 0;

        if(SplashActivity.result < 0) {
            HttpUtility.makeGetRequest(Utility.getPreference(this, "base_url") + "/api/user/checkToken?uid=" + uid + "&token=" + token, new HttpUtility.HttpCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.d("API", response);
                        if (jsonObject.getInt("code") == 0) {
                            SplashActivity.result = 2;
                        } else {
                            SplashActivity.result = 1;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {
                    Log.d("API", error);
                    Intent it = new Intent(SplashActivity.this, LoginPageActivity.class);
                    startActivity(it);
                    finish();
                }
            });
        }
        }
}