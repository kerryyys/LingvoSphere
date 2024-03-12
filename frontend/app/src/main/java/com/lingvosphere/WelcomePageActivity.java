package com.lingvosphere;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lingvosphere.Utils.Utility;

public class WelcomePageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        Button start_btn = this.findViewById(R.id.start_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.setPreference(WelcomePageActivity.this, "init_sign", "true");
                Intent it = new Intent(WelcomePageActivity.this, LoginPageActivity.class);
                startActivity(it);
                finish();
            }
        });
    }
}