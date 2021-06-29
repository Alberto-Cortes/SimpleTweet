package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ComposeActivity extends AppCompatActivity {

    // Declare logic part of visual elements
    EditText etCompose;
    Button btnTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // Connect logic part with visual counterpart
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        // Define click listener
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make sure text is present and under character limit
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Please put some text on the tweet", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tweetContent.length() >= 140) {
                    Toast.makeText(ComposeActivity.this, "Please put less text on the tweet", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Send a POST request to the API.
            }
        });


    }
}