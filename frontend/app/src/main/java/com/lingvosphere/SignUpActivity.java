package com.lingvosphere;

import static com.lingvosphere.LoginPageActivity.isStrongPassword;
import static com.lingvosphere.LoginPageActivity.isValidEmail;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SignUpActivity extends AppCompatActivity {

    private static int Character_Selection = 0;
    private static int register_status = 0;
    ActivityResultLauncher<String> mGetContent;
    Uri resultUri;
    EditText upload_edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary_color));
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(Color.TRANSPARENT);


        RadioButton learner_btn = this.findViewById(R.id.learner_button);
        RadioButton mentor_btn = this.findViewById(R.id.mentor_button);
        RelativeLayout upload_block = this.findViewById(R.id.upload_relative_layout);
        Button signup_btn = this.findViewById(R.id.signup_button);
        Button choose_file_button = this.findViewById(R.id.choose_file_button);
        EditText email_edit = this.findViewById(R.id.email_edit);
        EditText pwd_edit = this.findViewById(R.id.pwd_edit);
        EditText pwd_rep_edit = this.findViewById(R.id.pwd_repeat_edit);
        EditText uname_edit = this.findViewById(R.id.uname_edit);
        upload_edittext = this.findViewById(R.id.upload_edittext);

        learner_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                learner_btn.setBackgroundResource(R.drawable.checked_radio_button);
                learner_btn.setTextColor(Color.parseColor("#ffffff"));
                mentor_btn.setBackgroundResource(R.drawable.unchecked_radio_button);
                mentor_btn.setTextColor(Color.parseColor("#5eb18a"));
                upload_block.setVisibility(View.GONE);
                SignUpActivity.Character_Selection = 0;
            }
        });

        mentor_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                learner_btn.setBackgroundResource(R.drawable.unchecked_radio_button);
                learner_btn.setTextColor(Color.parseColor("#5eb18a"));
                mentor_btn.setBackgroundResource(R.drawable.checked_radio_button);
                mentor_btn.setTextColor(Color.parseColor("#ffffff"));
                upload_block.setVisibility(View.VISIBLE);
                SignUpActivity.Character_Selection = 1;
            }
        });

        this.findViewById(R.id.toLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(SignUpActivity.this, LoginPageActivity.class);
                startActivity(it);
                finish();
            }
        });

        this.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        init_edits(email_edit, pwd_edit, pwd_rep_edit, uname_edit, signup_btn);

        signup_btn.setOnClickListener(new View.OnClickListener() {
            // Todo: Add Cert uploading check (Mentor)
            @Override
            public void onClick(View view) {
                String email = email_edit.getText().toString();
                String uname = uname_edit.getText().toString();
                String pwd = pwd_edit.getText().toString();
                String pwd_rep = pwd_rep_edit.getText().toString();
                if(SignUpActivity.register_status == 0) {
                    if(uname.length() == 0) {
                        Toast.makeText(SignUpActivity.this, "Username must not be null!", Toast.LENGTH_SHORT).show();
                    } else if(!isValidEmail(email)) {
                        Toast.makeText(SignUpActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show();
                    } else if (pwd.length() < 6) {
                        Toast.makeText(SignUpActivity.this, "Password Length must be greater than 6!", Toast.LENGTH_SHORT).show();
                    } else if(!isStrongPassword(pwd)){
                        Toast.makeText(SignUpActivity.this, "Please include at least one digit, one special character, and one letter", Toast.LENGTH_SHORT).show();
                    }else if (!pwd_rep.equals(pwd)) {
                        Toast.makeText(SignUpActivity.this, "2 passwords doesn't match!", Toast.LENGTH_SHORT).show();
                    }
                } else if(SignUpActivity.register_status == 1){
                    SignUp(uname, email, pwd, SignUpActivity.Character_Selection);
                    SignUpActivity.register_status = 2;
                    signup_btn.setBackgroundResource(R.drawable.login_button_inactive);
                    signup_btn.setText("Signing Up...");
                } else {
                    Toast.makeText(SignUpActivity.this, "Signing in...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent = new Intent(getApplicationContext(), CropperActivity.class);
                intent.putExtra("DATA", result.toString());
                startActivityForResult(intent, 101);
            }
        });

        choose_file_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Launch all types of images
                mGetContent.launch("image/*");
            }
        });
    }

    private void SignUp(String uname, String email, String pwd, int character) {
        try {
        String url = Utility.getPreference(SignUpActivity.this, "base_url") + String.format("/api/user/register?uname=%s&email=%s&pwd=%s&character=%d", uname, URLEncoder.encode(email, StandardCharsets.UTF_8.toString()), pwd, character);
            HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
                @Override
                public void onSuccess(String response) throws JSONException {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getInt("code") == 0) {
                        Toast.makeText(SignUpActivity.this, "Sign up successfully!", Toast.LENGTH_SHORT).show();
                        Utility.setPreference(SignUpActivity.this, "email", email);
                        Intent it = new Intent(SignUpActivity.this, LoginPageActivity.class);
                        startActivity(it);
                        finish();
                    }
                    else {
                        Toast.makeText(SignUpActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(SignUpActivity.this, "Network Error!!", Toast.LENGTH_SHORT);
                }
            });
        } catch (UnsupportedEncodingException e) {
            //throw new RuntimeException(e);
            Toast.makeText(SignUpActivity.this, "URL Encoding error:\n"+ e.toString(), Toast.LENGTH_SHORT);
        }
    }

    private void init_edits(EditText email_edit, EditText pwd_edit, EditText pwd_rep_edit, EditText uname_edit, Button signup_btn) {
        email_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                CheckInputs(email_edit, pwd_edit, pwd_rep_edit, uname_edit, signup_btn);
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
                CheckInputs(email_edit, pwd_edit, pwd_rep_edit, uname_edit, signup_btn);
            }
        });

        uname_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                CheckInputs(email_edit, pwd_edit, pwd_rep_edit,  uname_edit, signup_btn);
            }
        });

        pwd_rep_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                CheckInputs(email_edit, pwd_edit, pwd_rep_edit,  uname_edit, signup_btn);
            }
        });
    }

    private void CheckInputs(EditText email_edit, EditText pwd_edit, EditText pwd_rep_edit, EditText uname_edit, Button signup_btn) {
        if(SignUpActivity.register_status == 2) return;
        String email = String.valueOf(email_edit.getText());
        String password = String.valueOf(pwd_edit.getText());
        //if all the requirement meet set status to 1
        if(password.length() > 5 && isStrongPassword(password) && isValidEmail(email) && uname_edit.getText().toString().length() > 0 && pwd_rep_edit.getText().toString().equals(password)) {
            signup_btn.setBackgroundResource(R.drawable.login_button_active);
            SignUpActivity.register_status = 1;
        }else {
            signup_btn.setBackgroundResource(R.drawable.login_button_inactive);
            SignUpActivity.register_status = 0;
        }
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

            upload_edittext.setText(resultUri.toString());

        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, UCrop.getError(data).toString(), Toast.LENGTH_SHORT).show();
        }
    }

}