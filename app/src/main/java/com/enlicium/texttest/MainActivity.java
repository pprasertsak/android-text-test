package com.enlicium.texttest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button sendButton;
    private TextView resultText;

    private String SERVER_URL = "https://salty-mesa-76549.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendButton = (Button)findViewById(R.id.button);
        resultText = (TextView)findViewById(R.id.result);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "คุณอัลเฟรด นัดหมายช่างค่ะ";
                resultText.setText("Q: " + text + "\n");

                postText(text);
            }
        });
    }

    private void postText(String text) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("user_id", "001");
            postData.put("text", text);

            new SendText().execute(SERVER_URL, postData.toString());
            System.out.println(postData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SendText extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String data = "";
            HttpURLConnection httpURLConnection = null;

            try {
                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.write(params[1].getBytes()); // wr.writeBytes(params[1]) causes errors
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data

            try {

                JSONObject resultObject = new JSONObject(result);
                String reply = resultObject.getString("reply");
                int flag = resultObject.getInt("flag");

                resultText.append("A: " + reply + ", " + flag);

            } catch(JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
}
