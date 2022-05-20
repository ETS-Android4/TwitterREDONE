package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityFollowBinding;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class FollowActivity extends AppCompatActivity {
    public  static final String TAG="FollowActivity";
    User user;
    ActivityFollowBinding binding;
    TwitterClient client;
    List<User> userList;
    FollowAdapter adapter;
    RecyclerView rvFollow;
    MenuItem progressBar;
    ImageView ivProfile;
    TextView tvFollow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFollowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        user=Parcels.unwrap(getIntent().getParcelableExtra("user"));

        rvFollow=binding.rvFollow;
        client=TwitterApp.getRestClient(this);
        userList=new ArrayList<>();
        adapter=new FollowAdapter(this, userList);

        rvFollow.setAdapter(adapter);
        rvFollow.setLayoutManager(new LinearLayoutManager(this));

        Glide.with(this).load(user.publicImageUrl).into(binding.ivPF);
        getSupportActionBar().setTitle(user.name);

        binding.tvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userList.clear();
                if (binding.tvFollow.getText().toString()=="Following")
                {
                    binding.tvFollow.setText("Follower");
                    loadFollower();
                }
                else
                {
                    binding.tvFollow.setText("Following");
                    loadFollowing();
                }
            }
        });
        binding.tvFollow.setText("Following");
        loadFollowing();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.compose).setVisible(false);
        progressBar=menu.findItem(R.id.miActionProgress);
        return super.onPrepareOptionsMenu(menu);
    }

    public void loadFollowing()
    {
        if (progressBar!=null)
            progressBar.setVisible(true);
        client.getFollowing(String.valueOf(user.id), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    // get user data and figure out how to parse it
                    JSONArray jsonArray = jsonObject.getJSONArray("users");
                    for (int i = 0; i<jsonArray.length(); i++) {
                        JSONObject user = jsonArray.getJSONObject(i);
                        userList.add(User.fromJson(user));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progressBar!=null)
                    progressBar.setVisible(false);
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
            }
        });
    }

    public void loadFollower()
    {
        if (progressBar!=null)
            progressBar.setVisible(true);
        client.getFollowers(String.valueOf(user.id), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    // get user data and figure out how to parse it
                    JSONArray jsonArray = jsonObject.getJSONArray("users");
                    for (int i = 0; i<jsonArray.length(); i++) {
                        JSONObject user = jsonArray.getJSONObject(i);
                        userList.add(User.fromJson(user));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progressBar!=null)
                    progressBar.setVisible(false);
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
            }
        });
    }

}