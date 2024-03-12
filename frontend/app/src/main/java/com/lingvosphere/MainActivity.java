package com.lingvosphere;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lingvosphere.Fragments.HomeFragment;
import com.lingvosphere.Fragments.LearningJourneyFragment;
import com.lingvosphere.Fragments.ProfileFragment;
import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static ImageView home_btn, learning_progress_btn, mentor_btn, profile_btn;
    public static ViewPager viewPager;
    public static TextView headerTitle;

    public static int currentItem = -1;
    private long lastBackPressedTime = 0; // 上一次按键时间
    private static final int BACK_PRESS_THRESHOLD = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        decorView.setSystemUiVisibility(uiOptions);
        window.setNavigationBarColor(Color.parseColor("#fafafa"));


        DrawerLayout root = findViewById(R.id.home_root);
        final View contentView = findViewById(R.id.main_content);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, root, android.R.string.yes, android.R.string.cancel) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                contentView.setTranslationX(slideX);
            }
        };
        root.addDrawerListener(actionBarDrawerToggle);

        ImageView sidebar_btn = this.findViewById(R.id.sidebar_btn);
        sidebar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                root.open();
            }
        });

    }

    private void init_Fragments(int item) {
        viewPager.setCurrentItem(item);
    }

    private void init_side_tabs() {
        this.findViewById(R.id.tab_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, LearningTimelineActivity.class);
                startActivity(it);
            }
        });

        this.findViewById(R.id.tab_community_corner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, CommunityCornerActivity.class);
                startActivity(it);
            }
        });

        this.findViewById(R.id.tab_accolade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, AccoladeOasisActivity.class);
                startActivity(it);
            }
        });

        this.findViewById(R.id.tab_enroll_course).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, CourseEnrollActivity.class);
                startActivity(it);
            }
        });

        this.findViewById(R.id.tab_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        this.findViewById(R.id.sidebar_profile_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this,ProfileEditingActivity.class);
                startActivity(it);
            }
        });
    }

    public void signOut(){
        try{
            String base_url = Utility.getPreference(MainActivity.this,"base_url");
            String uid = Utility.getPreference(MainActivity.this,"uid");
            String token = Utility.getPreference(MainActivity.this,"token");
            String url = String.format("%s/api/user/logout?uid=%s&token=%s",base_url,uid,token);
            HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
                @Override
                public void onSuccess(String response) throws JSONException {
                    //Clean the token first
                    Utility.setPreference(MainActivity.this, "token", "");
                    Intent intent = new Intent(MainActivity.this, LoginPageActivity.class);
                    startActivity(intent);

                    //Clear Activity Stack (Optional)
                    MainActivity.this.finishAffinity();

                    Toast.makeText(MainActivity.this,"Successfully Sign Out",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(MainActivity.this, "Sign out failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void init_bottom_nav(int currentItem) {
        viewPager = findViewById(R.id.view_pager);
        headerTitle = findViewById(R.id.header_title);
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0, true);

        home_btn = MainActivity.this.findViewById(R.id.home_btn);
        learning_progress_btn = MainActivity.this.findViewById(R.id.learning_journey_btn);
        mentor_btn = MainActivity.this.findViewById(R.id.mentor_btn);
        profile_btn = MainActivity.this.findViewById(R.id.profile_btn);

        if(currentItem >= 0)
            viewPager.setCurrentItem(currentItem);

        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.currentItem = 0;
                viewPager.setCurrentItem(0);
            }
        });
        learning_progress_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.currentItem = 1;
                viewPager.setCurrentItem(1);
            }
        });
        mentor_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.currentItem = 2;
                viewPager.setCurrentItem(2);
            }
        });
        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.currentItem = 3;
                viewPager.setCurrentItem(3);
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        home_btn.setImageResource(R.drawable.home_active);
                        learning_progress_btn.setImageResource(R.drawable.progress);
                        mentor_btn.setImageResource(R.drawable.mentor);
                        profile_btn.setImageResource(R.drawable.me);
                        headerTitle.setText("Home");
                        MainActivity.currentItem = 0;
                        break;
                    case 1:
                        home_btn.setImageResource(R.drawable.home);
                        learning_progress_btn.setImageResource(R.drawable.progress_active);
                        mentor_btn.setImageResource(R.drawable.mentor);
                        profile_btn.setImageResource(R.drawable.me);
                        headerTitle.setText("Learning Schedule");
                        MainActivity.currentItem = 1;
                        break;
                    case 2:
                        home_btn.setImageResource(R.drawable.home);
                        learning_progress_btn.setImageResource(R.drawable.progress);
                        mentor_btn.setImageResource(R.drawable.mentor_active);
                        profile_btn.setImageResource(R.drawable.me);
                        headerTitle.setText("Mentorship Hub");
                        MainActivity.currentItem = 2;
                        break;
                    case 3:
                        home_btn.setImageResource(R.drawable.home);
                        learning_progress_btn.setImageResource(R.drawable.progress);
                        mentor_btn.setImageResource(R.drawable.mentor);
                        profile_btn.setImageResource(R.drawable.me_active);
                        headerTitle.setText("Profile");
                        MainActivity.currentItem = 3;
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastBackPressedTime < BACK_PRESS_THRESHOLD) {
            super.onBackPressed(); // 在阈值内再次按下则退出
            finish();
        } else {
            Toast.makeText(this, "Press Back again to quit", Toast.LENGTH_SHORT).show();
            lastBackPressedTime = System.currentTimeMillis(); // 更新上一次按键时间
        }
    }

    private void getUsername(){
        try{
            String base_url = Utility.getPreference(this,"base_url");
            String uid = Utility.getPreference(this,"uid");
            String url = String.format("%s/api/user/getUname?uid=%s",base_url,uid);
            HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
                @Override
                public void onSuccess(String response) throws JSONException {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    if(code == 0){
                        //username retrieval successful
                        String username = jsonObject.getString("uname");
                        ((TextView)findViewById(R.id.username_sidebar)).setText(username);
                    }else {
                        // Handle error
                        String errorMessage = jsonObject.getString("msg");
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onError(String error) {
                    // Handle network or other errors
                    Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        init_bottom_nav(currentItem);
        init_side_tabs();
        init_Fragments(currentItem);
        getUsername();
    }

}