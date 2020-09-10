package com.example.newsapp.ui.headlines;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.newsapp.DetailActivity;
import com.example.newsapp.GuardianNewsApiCall;
import com.example.newsapp.NewsAdapter;
import com.example.newsapp.R;
import com.example.newsapp.SearchActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class HeadlinesFragment extends Fragment {

    private HeadlinesViewModel HeadlinesViewModel;
    DemoCollectionAdapter demoCollectionAdapter;
    ViewPager2 viewPager;
    static SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HeadlinesViewModel =
                ViewModelProviders.of(this).get(HeadlinesViewModel.class);

        View root = inflater.inflate(R.layout.fragment_headlines, container, false);
        return root;
    }

    public DemoCollectionAdapter getDemoCollectionAdaper(){
        return demoCollectionAdapter;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        demoCollectionAdapter = new DemoCollectionAdapter(this);
        viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(demoCollectionAdapter);
        HashMap<Integer, String> hm = new HashMap<>();
        hm.put(1, "WORLD");
        hm.put(2, "BUSINESS");
        hm.put(3, "POLITICS");
        hm.put(4, "SPORTS");
        hm.put(5, "TECHNOLOGY");
        hm.put(6, "SCIENCE");
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(hm.get(position + 1))
        ).attach();
        swipeRefreshLayout = getActivity().findViewById(R.id.home_swipe_refresh);
        swipeRefreshLayout.setEnabled(true);
    }

    public class DemoCollectionAdapter extends FragmentStateAdapter {
        public DemoCollectionAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return a NEW fragment instance in createFragment(int)
            Fragment fragment = new DemoObjectFragment();
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putInt(DemoObjectFragment.ARG_OBJECT, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 6;
        }
    }

    public static class DemoObjectFragment extends Fragment {
        public static final String ARG_OBJECT = "object";
        static String currentSection;
        ArrayList<JSONObject> newsList;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.headlines_viewpager, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            Bundle args = getArguments();
            int page = args.getInt(ARG_OBJECT);
            switch(page){
                case 1:
                    currentSection = "World";
                    getNews("World");
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            refresh("World");
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    break;
                case 2:
                    currentSection = "Business";
                    getNews("Business");
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            refresh("Business");
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    break;
                case 3:
                    currentSection = "Politics";
                    getNews("Politics");
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            refresh("Politics");
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    break;
                case 4:
                    currentSection = "Sports";
                    getNews("Sport");
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            refresh("Sport");
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    break;
                case 5:
                    currentSection = "Technology";
                    getNews("Technology");
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            refresh("Technology");
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    break;
                default:
                    currentSection = "Science";
                    getNews("Science");
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            refresh("Science");
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
            }
        }

        public void getNews(String section){
            GuardianNewsApiCall.getSectionNews(getActivity(), section, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        final JSONArray news = responseObject.getJSONObject("response").getJSONArray("results");
                        newsList = new ArrayList<>();
                        for(int i = 0; i < news.length(); i++){
                            newsList.add(news.getJSONObject(i));
                        }
                        getView().findViewById(R.id.headlines_progress_bar).setVisibility(View.INVISIBLE);
                        getView().findViewById(R.id.headlines_fetching_news).setVisibility(View.INVISIBLE);
                        RecyclerView recyclerView = getView().findViewById(R.id.headlines_news_list);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(layoutManager);
                        NewsAdapter adapter = new NewsAdapter(getActivity(), newsList);
                        recyclerView.setAdapter(adapter);
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                ((LinearLayoutManager) layoutManager).getOrientation());
                        recyclerView.addItemDecoration(dividerItemDecoration);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        public void refresh(String section){
            GuardianNewsApiCall.getSectionNews(getActivity(), section, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        final JSONArray news = responseObject.getJSONObject("response").getJSONArray("results");
                        ArrayList<JSONObject> newNewsList = new ArrayList<>();
                        for(int i = 0; i < news.length(); i++){
                            newsList.add(news.getJSONObject(i));
                        }
                        RecyclerView recyclerView = getView().findViewById(R.id.headlines_news_list);
                        NewsAdapter adapter = (NewsAdapter) recyclerView.getAdapter();
                        newsList = newNewsList;
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
