package com.lingvosphere.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lingvosphere.AIChatActivity;
import com.lingvosphere.BookMentorActivity;
import com.lingvosphere.R;
import com.lingvosphere.ViewMentorProfileActivity1;
import com.lingvosphere.ViewMentorProfileActivity2;

public class MentorshipHubFragment extends Fragment {
    public MentorshipHubFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_mentorship_hub, container, false);

        Button BtnAIChat = view.findViewById(R.id.AImentorButton);
        BtnAIChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AIChatActivity.class);
                startActivity(intent);
            }
        });

        Button BtnBookMentor = view.findViewById(R.id.bookMentorButton);
        BtnBookMentor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BookMentorActivity.class);
                startActivity(intent);
            }
        });

        Button mentor1Btn = view.findViewById(R.id.mentor1Btn);
        mentor1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewMentorProfileActivity1.class);
                startActivity(intent);
            }
        });

        Button mentor2Btn = view.findViewById(R.id.mentor2Btn);
        mentor2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ViewMentorProfileActivity2.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
