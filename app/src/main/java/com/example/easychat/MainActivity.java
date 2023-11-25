package com.example.easychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
RecyclerView recyclerView;
TextView welcomeTextView;
EditText messageEditText;
ImageButton sendButton;
List<Message> messageList;
Message_Adapter messageAdapter;
    public static final MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageList = new ArrayList<>(); // creating new and empty array

        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.wlcm_txt);
        messageEditText = findViewById(R.id.message_edit);
        sendButton = findViewById(R.id.send_btn);

        //setup recyclerView
        messageAdapter = new Message_Adapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this); // because we are using the chat
        llm.setStackFromEnd(true); // because we want to scroll from below to up
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener((v) -> {
            String questions = messageEditText.getText().toString().trim(); // to get the input from user
            Toast.makeText(this, "BOLDIYA", Toast.LENGTH_LONG).show(); // show user input in toast form
            addToChat(questions,Message.SENT_BY_ME);
            messageEditText.setText("");
            callAPI(questions);
            welcomeTextView.setVisibility(View.GONE);
        });
    }
        void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response){
        addToChat(response,Message.SENT_BY_BOT);
    }
    void callAPI(String questions){
        //okhttp

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model","gpt-3.5-turbo-instruct");
            jsonBody.put("prompt",questions);
            jsonBody.put("max_tokens",4000);
            jsonBody.put("temperature",0);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestBody rBody = RequestBody.create(jsonBody.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization","Bearer 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")
                .post(rBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    addResponse("Do another FUCKING TRY because "+e.getMessage()); // so the message on the failure
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = new JSONArray("Choices");
                        String result = jsonArray.getJSONObject(0).getString("Text");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    assert response.body() != null;
                    addResponse("Arre Bhai Aakhir kehna kya chahte ho");
                }
            }
        });


    }
}