package com.lingvosphere.Fragments;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.lingvosphere.LoginPageActivity;
import com.lingvosphere.MainActivity;
import com.lingvosphere.ProfileEditingActivity;
import com.lingvosphere.R;
import com.lingvosphere.Utils.HttpUtility;
import com.lingvosphere.Utils.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;

public class ProfileFragment extends Fragment {
    private TextView newProfile;

    private TextView usernameTV;
    private ImageView profileIV;

    public ProfileFragment() {

    }

    public void update() {
        getUsername();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_profile, container, false);

        Button signout_btn = view.findViewById(R.id.signout_btn);
        Button edit_btn = view.findViewById(R.id.edit_btn);
        usernameTV = view.findViewById(R.id.username);
        profileIV = view.findViewById(R.id.profile_picture);

        getUsername();
        getProfile();

        signout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).signOut();
                }
            }
        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileEditingActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        return view;
    }

    private void getProfile() {
        try {
            String base_url = Utility.getPreference(requireActivity(), "base_url");
            String uid = Utility.getPreference(requireActivity(), "uid");
            String url = String.format("%s/api/user/getProfile?uid=%s", base_url, uid);
            HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
                @Override
                public void onSuccess(String response) throws JSONException {
                    JSONObject jsonObject = new JSONObject(response);

                    // Check if "data" key exists in the JSON response
                    if (jsonObject.has("data")) {
                        String profile = jsonObject.getString("data");
                        if(profile.equals("null")) {
                            profileIV.setImageResource(R.drawable.lingvosphere_icon);
                        } else {

                            // Use Picasso to load the image into the ImageView
                            Picasso.get().load(profile).into(profileIV);

                            Log.d("ProfileFragment", "Found profile in JSON response: " + profile);
                        }
                    } else {
                        // Handle the case where "profile" key is not present in the JSON response
                        Log.e("ProfileFragment", "No value for profile in JSON response");
                        Toast.makeText(requireActivity(), "errorr", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onError(String error) {
                    // Handle network or other errors
                    Toast.makeText(requireActivity(), "Fail to change profile picture" + error, Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUsername() {
        try {
            String base_url = Utility.getPreference(requireActivity(), "base_url");
            String uid = Utility.getPreference(requireActivity(), "uid");
            String url = String.format("%s/api/user/getUname?uid=%s", base_url, uid);
            HttpUtility.makeGetRequest(url, new HttpUtility.HttpCallback() {
                @Override
                public void onSuccess(String response) throws JSONException {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        //username retrieval successful
                        String username = jsonObject.getString("uname");
                        usernameTV.setText(username);
                    } else {
                        // Handle error
                        String errorMessage = jsonObject.getString("msg");
                        Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String error) {
                    // Handle network or other errors
                    Toast.makeText(requireActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 & resultCode == getActivity().RESULT_OK && data != null) {
            // Get the new username from ProfileEditingActivity
            String newUsername = data.getStringExtra("newUsername");
            String newProfile = data.getStringExtra("newProfileUrl");

            // Update the TextView with the new username
            if (usernameTV != null) {
                usernameTV.setText(newUsername);
            }
            Picasso.get().load(newProfile).into(profileIV);
        }
    }
}