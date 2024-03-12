package com.lingvosphere.Utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpUtility {
    private static final String TAG = "HttpUtility";

    public interface HttpCallback {
        void onSuccess(String response) throws JSONException;

        void onError(String error);
    }

    public static void makeGetRequest(String url, HttpCallback callback) {
        new GetRequestTask(callback).execute(url);
    }

    public static void makePostRequest(String url, Map<String, String> postData, HttpCallback callback) {
        new PostRequestTask(callback).execute(url, postData);
    }

    private static class GetRequestTask extends AsyncTask<String, Void, String> {
        private HttpCallback callback;

        GetRequestTask(HttpCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                } else {
                    return "HTTP GET request failed with response code: " + responseCode;
                }
            } catch (IOException e) {
                Log.e(TAG, "Error making HTTP GET request: " + e.getMessage());
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (callback != null) {
                if (!result.equals("Error")) {
                    try {
                        callback.onSuccess(result);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    callback.onError(result);
                }
            }
        }
    }



    private static class PostRequestTask extends AsyncTask<Object, Void, String> {
        private HttpCallback callback;

        PostRequestTask(HttpCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                String urlStr = (String) params[0];
                Map<String, String> postData = (Map<String, String>) params[1];

                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                StringBuilder postDataStr = new StringBuilder();
                for (Map.Entry<String, String> param : postData.entrySet()) {
                    if (postDataStr.length() != 0) {
                        postDataStr.append('&');
                    }
                    postDataStr.append(param.getKey());
                    postDataStr.append('=');
                    postDataStr.append(param.getValue());
                }

                byte[] postDataBytes = postDataStr.toString().getBytes("UTF-8");

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(postDataBytes);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                } else {
                    return "HTTP POST request failed with response code: " + responseCode;
                }
            } catch (IOException e) {
                Log.e(TAG, "Error making HTTP POST request: " + e.getMessage());
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (callback != null) {
                if (!result.equals("Error")) {
                    try {
                        callback.onSuccess(result);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    callback.onError(result);
                }
            }
        }
    }
}

