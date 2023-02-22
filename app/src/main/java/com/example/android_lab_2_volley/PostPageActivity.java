package com.example.android_lab_2_volley;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PostPageActivity extends AppCompatActivity {
    TextView titleView;
    TextView bodyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_page);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initViews();

        Intent intent = getIntent();
        try {
            JSONObject data = new JSONObject(intent.getStringExtra("data"));

            String title = data.getString("post_title");
            String body = data.getString("post_body");

            titleView.setText(title);
            bodyView.setText(body);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void initViews() {
        titleView = findViewById(R.id.post_title);
        bodyView = findViewById(R.id.post_body);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}