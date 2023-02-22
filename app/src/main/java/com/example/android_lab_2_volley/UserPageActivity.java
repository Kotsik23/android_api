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

public class UserPageActivity extends AppCompatActivity {
    TextView usernameView;
    TextView fullNameView;
    TextView emailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initViews();

        Intent intent = getIntent();
        try {
            JSONObject data = new JSONObject(intent.getStringExtra("data"));

            String username = data.getString("username");
            String fullName = data.getString("fullName");
            String email = data.getString("email");

            usernameView.setText(username);
            fullNameView.setText(fullName);
            emailView.setText(email);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void initViews() {
        usernameView = findViewById(R.id.user_username);
        fullNameView = findViewById(R.id.user_fullname);
        emailView = findViewById(R.id.user_email);
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