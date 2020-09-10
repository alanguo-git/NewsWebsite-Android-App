package com.example.newsapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class SearchActivity extends AppCompatActivity {
    String keyword;
    ArrayList<JSONObject> newsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //set toolbar
        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        keyword = getIntent().getExtras().getString("keyword");
        getSearchResult();
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.search_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        return true;
    }

    public void getSearchResult() {
        GuardianNewsApiCall.getSearchNews(this, keyword, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray news = responseObject.getJSONObject("response").getJSONArray("results");
                    newsList = new ArrayList<>();
                    for(int i = 0; i < news.length(); i++){
                        newsList.add(news.getJSONObject(i));
                    }
                    findViewById(R.id.search_progress_bar).setVisibility(View.INVISIBLE);
                    findViewById(R.id.search_fetching_news).setVisibility(View.INVISIBLE);
                    RecyclerView recyclerView = findViewById(R.id.search_list);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    NewsAdapter adapter = new NewsAdapter(SearchActivity.this, newsList);
                    recyclerView.setAdapter(adapter);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                            ((LinearLayoutManager) layoutManager).getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    Toolbar toolbar = findViewById(R.id.search_toolbar);
                    toolbar.setTitle("Search Results for "+keyword);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void refresh(){
        GuardianNewsApiCall.getSearchNews(this, keyword, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray news = responseObject.getJSONObject("response").getJSONArray("results");
                    newsList = new ArrayList<>();
                    for(int i = 0; i < news.length(); i++){
                        newsList.add(news.getJSONObject(i));
                    }
                    RecyclerView recyclerView = findViewById(R.id.search_list);
                    recyclerView.getAdapter().notifyDataSetChanged();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
