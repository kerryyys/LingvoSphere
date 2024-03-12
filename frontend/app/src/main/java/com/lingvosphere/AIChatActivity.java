package com.lingvosphere;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lingvosphere.Fragments.MentorshipHubFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AIChatActivity extends AppCompatActivity {
    private TextView userChatTextView;
    private TextView aiChatTextView;
    private static EditText messageEditText;
    private  static LinearLayout recyclerView;
    private static String[] msgs = new String[] {"Hi, I'm your AI mentor, feel free to ask me any questions.", "Hi. The phrase \"get back on track\" means to return to the correct course of action or to regain focus and progress toward a goal after facing a setback, distraction, or deviation. It implies getting back to the original plan or intended path of achieving something. If you have any further questions or need clarification, feel free to ask.", "You are welcome."};
    private static int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        messageEditText = findViewById(R.id.ETinput);
        ImageButton sendButton = findViewById(R.id.BtnSend);

        findViewById(R.id.backButton).setOnClickListener(v -> {
            onBackPressed();
        });

        recyclerView = findViewById(R.id.recyclerView);
        //String messageContent = messageEditText.getText().toString();

        //sendButton.setOnClickListener(v -> sendMessage());

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View msgView = LayoutInflater.from(view.getContext()).inflate(R.layout.sender_chat_layout, recyclerView, false);
                TextView sendermessage = msgView.findViewById(R.id.sendermessage);
                sendermessage.setText(messageEditText.getText());
                messageEditText.setText("");
                recyclerView.addView(msgView);

                View msgView2 = LayoutInflater.from(view.getContext()).inflate(R.layout.receiver_chat_layout, recyclerView, false);
                TextView sendermessage2 = msgView2.findViewById(R.id.sendermessage);
                sendermessage2.setText(AIChatActivity.msgs[AIChatActivity.count++%3]);
                recyclerView.addView(msgView2);
            }
        });
    }

    // 0 for user, 1 for AI
    /*private static void addMessage(String content, int role) {




        // Clear the input field
        messageEditText.getText().clear();
    }

    //下面是api的code
    private void sendMessage() {
        String message = messageEditText.getText().toString();
        userChatTextView.setText(message + "\n"); // Display user's message

        // Perform network request in the background
        new GptRequestTask().execute(message);
    }

    private class GptRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String message = params[0];
            return chatGPT(message);
        }

        @Override
        protected void onPostExecute(String reply) {
            aiChatTextView.append("AI: " + reply + "\n"); // Display AI's reply
        }
    }

    public static String chatGPT(String message) {
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = "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"; // Replace with your actual API key
        String model = "gpt-3.5-turbo";

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");

            // Build the request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return extractContentFromResponse(response.toString());
            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                throw new RuntimeException("Rate limit exceeded. Please wait before making another request.");
            } else {
                throw new RuntimeException("HTTP error code: " + responseCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // This method extracts the response expected from chatgpt and returns it.
    public static String extractContentFromResponse(String response) {
        int startMarker = response.indexOf("content")+11; // Marker for where the content starts.
        int endMarker = response.indexOf("\"", startMarker); // Marker for where the content ends.
        return response.substring(startMarker, endMarker); // Returns the substring containing only the response.
    }*/
}