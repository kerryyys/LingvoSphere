package com.lingvosphere;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class AccoladeOasisActivity extends AppCompatActivity {

    private ImageButton backButton;
    private TextView accoladeOasis;
    private Map<Integer, Button> selectedButtons = new HashMap<>();
    private Button buttonBC1, buttonBC2, buttonBC3, buttonBM1, buttonBM2, buttonBM3, buttonKr1, buttonKr2, buttonKr3, buttonJp1, buttonJp2, buttonJp3;
    private Spinner spinnerBC, spinnerBM, spinnerKR, spinnerJP;
    private ImageView potMandarin, potBM, potKorean, potJapanese;
    private Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accolade_oasis);

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

        backButton = findViewById(R.id.backButton);
        accoladeOasis = findViewById(R.id.accoladeOasis);

        potMandarin = findViewById(R.id.pot_mandarin);
        buttonBC1 = findViewById(R.id.button_bc1);
        buttonBC2 = findViewById(R.id.button_bc2);
        buttonBC3 = findViewById(R.id.button_bc3);
        spinnerBC = findViewById(R.id.commentSpinner_bc);

        potBM = findViewById(R.id.pot_bm);
        buttonBM1 = findViewById(R.id.button_bm1);
        buttonBM2 = findViewById(R.id.button_bm2);
        buttonBM3 = findViewById(R.id.button_bm3);
        spinnerBM = findViewById(R.id.commentSpinner_bm);

        potKorean = findViewById(R.id.pot_korean);
        buttonKr1 = findViewById(R.id.button_kr1);
        buttonKr2 = findViewById(R.id.button_kr2);
        buttonKr3 = findViewById(R.id.button_kr3);
        spinnerKR = findViewById(R.id.commentSpinner_kr);

        potJapanese = findViewById(R.id.pot_japanese);
        buttonJp1 = findViewById(R.id.button_jp1);
        buttonJp2 = findViewById(R.id.button_jp2);
        buttonJp3 = findViewById(R.id.button_jp3);
        spinnerJP = findViewById(R.id.commentSpinner_jp);

        ArrayAdapter<CharSequence> mandarinAdapter = ArrayAdapter.createFromResource(this,
                R.array.comment_options, R.layout.comment_spinner_item_layout);
        mandarinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBC.setAdapter(mandarinAdapter);

        ArrayAdapter<CharSequence> melayuAdapter = ArrayAdapter.createFromResource(this,
                R.array.comment_options, R.layout.comment_spinner_item_layout);
        melayuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBM.setAdapter(melayuAdapter);

        ArrayAdapter<CharSequence> koreanAdapter = ArrayAdapter.createFromResource(this,
                R.array.comment_options, R.layout.comment_spinner_item_layout);
        koreanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKR.setAdapter(koreanAdapter);

        ArrayAdapter<CharSequence> japaneseAdapter = ArrayAdapter.createFromResource(this,
                R.array.comment_options, R.layout.comment_spinner_item_layout);
        japaneseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJP.setAdapter(japaneseAdapter);


        setButtonClickListener( buttonBC1);
        setButtonClickListener( buttonBC2);
        setButtonClickListener( buttonBC3);

        setButtonClickListener( buttonBM1);
        setButtonClickListener( buttonBM2);
        setButtonClickListener( buttonBM3);

        setButtonClickListener( buttonKr1);
        setButtonClickListener( buttonKr2);
        setButtonClickListener( buttonKr3);

        setButtonClickListener( buttonJp1);
        setButtonClickListener( buttonJp2);
        setButtonClickListener( buttonJp3);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccoladeOasisActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setButtonClickListener(Button button) {
        button.setEnabled(getButtonCourseID(button) >= 0); // Disable the button if courseID is not recognized
        button.setOnClickListener(v -> onButtonClick(button));
    }

    private void onButtonClick(Button button) {
        int courseID = getButtonCourseID(button);

        if (courseID >= 0) {
            setButtonChecked(courseID, button);
            requestCourseData(button);
        }
    }

    private void setButtonChecked(int courseID, Button clickedButton) {
        // Remove the entry for the previous courseID
        if (!selectedButtons.isEmpty()) {
            int previousCourseID = selectedButtons.keySet().iterator().next();
            Button previousButton = selectedButtons.remove(previousCourseID);

            if (previousButton != null) {
                previousButton.setSelected(false);
            }
        }

        clickedButton.setSelected(true);
        selectedButtons.put(courseID, clickedButton);
    }

    private int getButtonCourseID(Button buttonId) {
        if (buttonId == buttonBC1) {
            return 0;
        } else if (buttonId == buttonBC2) {
            return 1;
        } else if (buttonId == buttonBC3) {
            return 2;
        } else if (buttonId == buttonJp1) {
            return 3;
        } else if (buttonId == buttonJp2) {
            return 4;
        } else if (buttonId == buttonJp3) {
            return 5;
        } else if (buttonId == buttonKr1) {
            return 6;
        } else if (buttonId == buttonKr2) {
            return 7;
        } else if (buttonId == buttonKr3) {
            return 8;
        } else if (buttonId == buttonBM1) {
            return 9;
        } else if (buttonId == buttonBM2) {
            return 10;
        } else if (buttonId == buttonBM3) {
            return 11;
        }else {
            return -1;
        }
    }

    private void requestCourseData(Button clickedButton) {
        int buttonCourseID = getButtonCourseID(clickedButton);
        String uid = Utility.getPreference(this, "uid");
        String token = Utility.getPreference(this, "token");
        String base_url = Utility.getPreference(this, "base_url");
        ImageView pot = getCourseImage(buttonCourseID);

        String requestUrl = String.format(Locale.US, "%s/api/accoladeoasis/plantgrowth?uid=%s&token=%s&courseID=%d",
                base_url, uid, token, buttonCourseID);

        HttpUtility.makeGetRequest(requestUrl, new HttpUtility.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("AccoladeOasis", "Response: " + response);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);

                        if (jsonResponse.has("progress")) {
                            int progress = jsonResponse.getInt("progress");
                            Log.d("AccoladeOasis", "Progress: " + progress);

                            updateUI(determinePlantImage(progress), pot);
                            Toast.makeText(AccoladeOasisActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle the case when the response doesn't contain the "progress" field
                            clickedButton.setSelected(false);
                            clickedButton.setEnabled(false);
                            Toast.makeText(AccoladeOasisActivity.this, "This course is not available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            Toast.makeText(AccoladeOasisActivity.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e("AccoladeOasis", "Error: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(AccoladeOasisActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
            private void updateUI(String plantImage, ImageView pot) {
                Log.d("AccoladeOasis", "Updating UI with plantImage: " + plantImage);
                switch (plantImage) {
                    case "pot_1":
                        pot.setBackgroundResource(R.drawable.pot_1);
                        break;
                    case "pot_2":
                        pot.setBackgroundResource(R.drawable.pot_2);
                        break;
                    case "pot_3":
                        pot.setBackgroundResource(R.drawable.pot_3);
                        break;
                    case "pot_4":
                        pot.setBackgroundResource(R.drawable.pot_4);
                        break;
                    case "pot_10":
                        pot.setBackgroundResource(R.drawable.pot_10);
                        break;
                    case "pot_11":
                        pot.setBackgroundResource(R.drawable.pot_11);
                        break;
                    case "pot_12":
                        pot.setBackgroundResource(R.drawable.pot_12);
                        break;
                    case "pot_13":
                        pot.setBackgroundResource(R.drawable.pot_13);
                        break;
                    case "pot_14":
                        pot.setBackgroundResource(R.drawable.pot_14);
                        break;
                    default:
                        pot.setBackgroundResource(R.drawable.pot);
                        break;
                }
            }

            private String determinePlantImage(int progress) {
                Log.d("AccoladeOasis", "Determining plantImage for progress: " + progress);
                String[] completePlant = {"pot_10", "pot_11", "pot_12", "pot_13"};
                String plantImage = "pot";

                if (progress >= 20 && progress < 40) {
                    return "pot_1";
                } else if (progress >= 40 && progress < 60) {
                    return "pot_2";
                } else if (progress >= 60 && progress < 80) {
                    return "pot_3";
                } else if (progress >= 80 && progress < 100) {
                    return "pot_4";
                } else if (progress == 100) {
                    int randomIndex = rand.nextInt(completePlant.length);
                    plantImage = completePlant[randomIndex];
                    return plantImage;
                }
                return plantImage;
            }

    private ImageView getCourseImage(int buttonCourseID) {
        if(buttonCourseID == 0 || buttonCourseID == 1 || buttonCourseID == 2){
            return potMandarin;
        }else if(buttonCourseID == 3 || buttonCourseID == 4|| buttonCourseID == 5){
            return potJapanese;
        }else if(buttonCourseID == 6 || buttonCourseID == 7 || buttonCourseID == 8){
            return potKorean;
        }else if(buttonCourseID == 9 || buttonCourseID == 10 || buttonCourseID == 11){
            return potBM;
        }
        return null;
    }
}