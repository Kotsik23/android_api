package com.example.android_lab_2_volley;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Button fetchUsersBtn;
    private Button fetchPostsBtn;
    private Button clearListBtn;
    private Button sendEmailBtn;
    private TextView listTitle;
    private ListView listView;
    private LinearLayout emptyList;
    private EditText sendEmailEdit;
    private ProgressBar progressBar;
    private SharedPreferences preferences;
    private ArrayList<HashMap<String, String>> listData;
    private listType currentList;

    private enum listType {POSTS, USERS}

    ListAdapter usersAdapter, postsAdapter;

    private final String BASE_URL = "https://jsonplaceholder.typicode.com/";
    private String LIMIT;

    private boolean isEmailVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVariables();
        initPreferences();
        makeListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initPreferences();
        if (currentList.equals(listType.USERS)) {
            fetchUsers();
        } else {
            fetchPosts();
        }
    }

    private void initVariables() {
        listView = findViewById(R.id.listView);

        listTitle = findViewById(R.id.list_title);
        listTitle.setText(null);

        fetchPostsBtn = findViewById(R.id.fetch_posts_btn);
        fetchUsersBtn = findViewById(R.id.fetch_users_btn);
        clearListBtn = findViewById(R.id.clear_list_btn);
        sendEmailBtn = findViewById(R.id.send_email_btn);
        sendEmailEdit = findViewById(R.id.send_email_edit);
        emptyList = findViewById(R.id.empty_list);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        listData = new ArrayList<>();

        currentList = listType.USERS;

        usersAdapter = new SimpleAdapter(
                MainActivity.this,
                listData,
                R.layout.user_row,
                new String[]{"userId", "username", "email"},
                new int[]{R.id.userId, R.id.username, R.id.email}
        );

        postsAdapter = new SimpleAdapter(
                MainActivity.this,
                listData,
                R.layout.post_row,
                new String[]{"postId", "post_title"},
                new int[]{R.id.postId, R.id.post_title}
        );
    }

    private void initPreferences() {
        LIMIT = "?_limit=" + preferences.getString("key_lines_amount", "10");
        isEmailVisible = preferences.getBoolean("key_user_email", true);
    }

    private void makeListeners() {
        fetchUsersBtn.setOnClickListener(view -> fetchUsers());
        fetchPostsBtn.setOnClickListener(view -> fetchPosts());

        clearListBtn.setOnClickListener(view -> clearListData());
        sendEmailBtn.setOnClickListener(view -> sendEmail());

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(this,
                    currentList.equals(listType.USERS)
                            ? UserPageActivity.class
                            : PostPageActivity.class
            );
            intent.putExtra("data", new JSONObject(listData.get(i)).toString());
            startActivity(intent);
        });
    }

    private void clearListData() {
        if (!listData.isEmpty()) listData.clear();
        listTitle.setText(null);
        ((BaseAdapter) usersAdapter).notifyDataSetChanged();
        ((BaseAdapter) postsAdapter).notifyDataSetChanged();

        emptyList.setVisibility(View.VISIBLE);
    }

    // REQUESTS
    private void fetchUsers() {
        clearListData();
        emptyList.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        currentList = listType.USERS;

        String USERS_URL = BASE_URL + "users" + LIMIT;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, USERS_URL, null, response -> {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject user = response.getJSONObject(i);

                    String userId = user.getString("id");
                    String username = user.getString("username");
                    String fullName = user.getString("name");
                    String email = user.getString("email");

                    HashMap<String, String> item = new HashMap<>();
                    item.put("userId", userId);
                    item.put("username", username);
                    item.put("fullName", fullName);
                    item.put("email", email);
                    listData.add(item);
                }
                if (isEmailVisible) {
                    listView.setAdapter(usersAdapter);
                } else {
                    listView.setAdapter(new SimpleAdapter(
                            MainActivity.this,
                            listData,
                            R.layout.user_row,
                            new String[]{"userId", "username"},
                            new int[]{R.id.userId, R.id.username}
                    ));
                }
                listTitle.setText(R.string.users_list_title);
                progressBar.setVisibility(View.GONE);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            clearListData();
            Toast.makeText(this, "Something went wrong...", Toast.LENGTH_LONG).show();
        });

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchPosts() {
        clearListData();
        emptyList.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        currentList = listType.POSTS;

        String POSTS_URL = BASE_URL + "posts" + LIMIT;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, POSTS_URL, null, response -> {
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject post = response.getJSONObject(i);

                    String postId = post.getString("id");
                    String postTitle = post.getString("title");
                    String postBody = post.getString("body");

                    HashMap<String, String> item = new HashMap<>();
                    item.put("postId", postId);
                    item.put("post_title", postTitle);
                    item.put("post_body", postBody);
                    listData.add(item);
                }

                listView.setAdapter(postsAdapter);
                listTitle.setText(R.string.posts_list_title);
                progressBar.setVisibility(View.GONE);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            clearListData();
            Toast.makeText(this, "Something went wrong...", Toast.LENGTH_LONG).show();
        });

        Volley.newRequestQueue(this).add(request);
    }

    private void sendEmail() {
        String sendEmail;

        if (sendEmailEdit.getText().toString().isEmpty()) {
            Toast.makeText(this, "Email can't be empty", Toast.LENGTH_SHORT).show();
            return;
        } else {
            sendEmail = sendEmailEdit.getText().toString();
        }

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + sendEmail));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "CUSTOM API");
        emailIntent.putExtra(Intent.EXTRA_TEXT, new JSONArray(listData).toString());

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}