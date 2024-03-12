package com.lingvosphere;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginPageActivity extends AppCompatActivity {
    private static int login_status = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary_color));
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(Color.TRANSPARENT);

        Button login_btn = this.findViewById(R.id.login_button);
        EditText email_edit = this.findViewById(R.id.email_edit);
        EditText pwd_edit = this.findViewById(R.id.password_edit);
        TextView forgotPwd_btn = this.findViewById(R.id.forgot_password);

        init_edits(email_edit, pwd_edit, login_btn);
        login_status = 0;

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_edit.getText().toString();
                String pwd = pwd_edit.getText().toString();
                if(LoginPageActivity.login_status == 0) {
                    if(!isValidEmail(email)) {
                        Toast.makeText(LoginPageActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show();
                    } else if (pwd.length() < 6) {
                        Toast.makeText(LoginPageActivity.this, "Password Length must be greater than 6!", Toast.LENGTH_SHORT).show();
                    }
                } else if(LoginPageActivity.login_status == 1){
                    Login(email, pwd);
                    LoginPageActivity.login_status = 2;
                    login_btn.setBackgroundResource(R.drawable.login_button_inactive);
                    login_btn.setText("Logging in...");
                } else {
                    Toast.makeText(LoginPageActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView signup_btn = this.findViewById(R.id.create_account_btn);
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(LoginPageActivity.this, SignUpActivity.class);
                startActivity(it);
            }
        });

        this.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String email_stored = Utility.getPreference(LoginPageActivity.this, "email");
        if(email_stored != null) {
            email_edit.setText(email_stored);
        }

        forgotPwd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPageActivity.this,ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    private void init_edits(EditText email_edit, EditText pwd_edit, Button login_btn) {
        email_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                CheckInputs(email_edit, pwd_edit, login_btn);
            }
        });

        pwd_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                CheckInputs(email_edit, pwd_edit, login_btn);
            }
        });
    }

    private void CheckInputs(EditText email_edit, EditText pwd_edit, Button login_btn) {
        if(LoginPageActivity.login_status == 2) return;
        String email = String.valueOf(email_edit.getText());
        String password = String.valueOf(pwd_edit.getText());
        if(password.length() > 5 && isValidEmail(email)) {
            login_btn.setBackgroundResource(R.drawable.login_button_active);
            LoginPageActivity.login_status = 1;
        }else {
            login_btn.setBackgroundResource(R.drawable.login_button_inactive);
            LoginPageActivity.login_status = 0;
        }
    }

    private void Login(String email, String pwd) {
        try {
            String url = Utility.getPreference(LoginPageActivity.this, "base_url") + String.format("/api/user/login?email=%s&pwd=%s", URLEncoder.encode(email, StandardCharsets.UTF_8.toString()), pwd);
            HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
                @Override
                public void onSuccess(String response) throws JSONException {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("code") == 0) {
                        Toast.makeText(LoginPageActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        String token = jsonObject.getString("token");
                        Utility.setPreference(LoginPageActivity.this, "email", email);
                        Utility.setPreference(LoginPageActivity.this, "token", token);
                        String uid = jsonObject.getString("uid");
                        Utility.setPreference(LoginPageActivity.this, "uid", uid);
                        Intent it = new Intent(LoginPageActivity.this, MainActivity.class);
                        startActivity(it);
                        finish();
                    } else {
                        Toast.makeText(LoginPageActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onError(String error) {
                    Toast.makeText(LoginPageActivity.this, "Network Error!!", Toast.LENGTH_SHORT);
                }
            });
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(LoginPageActivity.this, "URL Encoding error:\n"+ e.toString(), Toast.LENGTH_SHORT);
        }
    }
    static boolean isValidEmail(String email) {
        String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        if (email == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    static boolean isStrongPassword(String pwd){
        String PASSWORD_REGEX = "^(?=.*[0-9])" + "(?=.*[a-zA-Z])" + "(?=.*[!*@#$%^&+=])" + "(?=\\S+$).{6,20}$";
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        if(pwd == null){
            return false;
        }
        Matcher matcher = pattern.matcher(pwd);
        return matcher.matches();

    }
}