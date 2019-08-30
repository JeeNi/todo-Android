package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    private ArrayList<HashMap<String, String>> Data = new ArrayList<>();
    private HashMap<String,String> InputData1 = new HashMap<>();
    private HashMap<String,String> InputData2 = new HashMap<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.todoListView);

        //데이터 초기화
        InputData1.put("school","서울대");
        InputData1.put("name","유혁");
        Data.add(InputData1);

        InputData2.put("school","연세대");
        InputData2.put("name","유재석");
        Data.add(InputData2);

        //simpleAdapter 생성
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                Data,
                R.layout.todo_list_item_view,
                new String[]{"name", "school"},
                new int[]{R.id.name,R.id.content});
        listView.setAdapter(simpleAdapter);
    }

    private HashMap<String, String> getTodoItem(JSONObject order) throws JSONException {
        String title = order.getString("title");
        String content = order.getString("content");

        HashMap<String,String> newIndex = new HashMap<>();
        newIndex.put("name", title);
        newIndex.put("school", content);
        return newIndex;
    }

    @Override
    protected void onStart() {
        super.onStart();

        final TextView messageView = findViewById(R.id.textView2);
        messageView.setText("Hello Text view. I am hanwool. Nice to meet you.");

        Log.i("Server", "On Create");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://172.30.43.27:8080/persons")
                .build();

        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Log.i("Server", "request fail");
                    messageView.setText("Data loading failed.Please check network connection.");
                }

                class Todo {
                    String title;
                    String content;
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resString = response.body().string();
                    Log.i("Server", resString);
                    messageView.setText("Data loading succeed.");

                    try {
                        JSONArray ja = new JSONArray(resString);
                        for(int i = 0; i < ja.length(); i++) {
                            JSONObject order = ja.getJSONObject(i);
                            Data.add(getTodoItem(order));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}

