package com.lingvosphere;

import static com.lingvosphere.LoginPageActivity.isValidEmail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary_color));
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(Color.TRANSPARENT);

        ImageButton back_btn = this.findViewById(R.id.back_btn);
        Button send_link_btn = this.findViewById(R.id.send_link_btn);
        EditText ETEmail = this.findViewById(R.id.ETEmail);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        send_link_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validate email input
                String email = ETEmail.getText().toString().trim();
                if(email.isEmpty()){
                    Toast.makeText(ForgotPassword.this, "Please enter your email!", Toast.LENGTH_SHORT).show();
                }else if(!isValidEmail(email)){
                    Toast.makeText(ForgotPassword.this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                }else{
                    String base_url = Utility.getPreference(ForgotPassword.this, "base_url");
                    String url = String.format("%s/api/user/forgotpwd?email=%s", base_url, email);
                    HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
                        @Override
                        public void onSuccess(String response) throws JSONException {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getInt("code") == 0) {
                                Toast.makeText(ForgotPassword.this, "Password reset link sent to your email", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ForgotPassword.this, "Failed: " + jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
                }
            }
        });
    }

}