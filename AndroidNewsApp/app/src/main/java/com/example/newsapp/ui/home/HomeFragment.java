package com.example.newsapp.ui.home;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.newsapp.DetailActivity;
import com.example.newsapp.GuardianNewsApiCall;
import com.example.newsapp.HomeNewsAdapter;
import com.example.newsapp.MainActivity;
import com.example.newsapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.view = view;
        getNews();
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.home_swipe_refresh);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNews();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void getNews(){
        GuardianNewsApiCall.make(getActivity(), "home", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    final JSONArray news = responseObject.getJSONObject("response").getJSONArray("results");
                    List<JSONObject> newsList = new ArrayList<>();
                    for(int i = 0; i < news.length(); i++){
                        newsList.add(news.getJSONObject(i));
                    }
                    view.findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.fetching_news).setVisibility(View.INVISIBLE);
                    final ListView listView = view.findViewById(R.id.news_list);
                    HomeNewsAdapter adapter = new HomeNewsAdapter(getActivity(), R.layout.news_card, newsList);
                    //set new height for listView
                    int height = 0;
                    for(int i = 0; i < adapter.getCount(); i++){
                        View listItem = adapter.getView(i, null, listView);
                        listItem.measure(0,0);
                        height += listItem.getMeasuredHeight();
                    }
                    ViewGroup.LayoutParams params = listView.getLayoutParams();
                    params.height = height + (listView.getDividerHeight() * (listView.getCount() - 1));
                    listView.setLayoutParams(params);
                    listView.requestLayout();
                    //set adapter
                    listView.setAdapter(adapter);
                    listView.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                            try{
                                JSONObject singleNews = news.getJSONObject(position);
                                Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.custom);
                                ImageView dialogImage = dialog.findViewById(R.id.dialog_image);
                                if(getImage(singleNews) != null){
                                    Picasso.get().load(getImage(singleNews)).into(dialogImage);
                                }
                                else{
                                    dialogImage.setImageResource(R.drawable.guardian_default);
                                }
                                TextView dialogText = dialog.findViewById(R.id.dialog_title);
                                dialogText.setText(singleNews.getString("webTitle"));
                                ImageButton twitterButton = dialog.findViewById(R.id.dialog_twitter_button);
                                String url = "https://theguardian.com/" + singleNews.getString("id");
                                twitterButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Uri uri = Uri.parse("https://twitter.com/intent/tweet?text=Check out this Link:&url="+url+"&hashtags=CSCI571NewsSearch");
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }
                                });
                                ImageButton bookmarkButton = dialog.findViewById(R.id.dialog_bookmark_button);
                                SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                if(pref.contains(singleNews.getString("id"))){
                                    bookmarkButton.setImageResource(R.drawable.ic_bookmark_red_24dp);
                                }
                                else{
                                    bookmarkButton.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
                                }
                                bookmarkButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try{
                                            SharedPreferences.Editor editor = pref.edit();
                                            if(pref.contains(singleNews.getString("id"))){ //delete
                                                editor.remove(singleNews.getString("id"));
                                                editor.commit();
                                                bookmarkButton.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
                                                ImageView bookmark = view.findViewById(R.id.news_bookmark_icon);
                                                bookmark.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
                                                Toast.makeText(getActivity(), singleNews.getString("webTitle")+"was removed from favorites", Toast.LENGTH_LONG).show();
                                            }
                                            else{ //add
                                                JSONObject jsonObject = new JSONObject();
                                                jsonObject.put("id", singleNews.getString("id"));
                                                jsonObject.put("image", getImage(singleNews));
                                                jsonObject.put("title", singleNews.getString("webTitle"));
                                                jsonObject.put("time", getLocalTime(singleNews));
                                                jsonObject.put("section", singleNews.getString("sectionName"));
                                                editor.putString(singleNews.getString("id"), jsonObject.toString());
                                                editor.commit(); // commit changes
                                                bookmarkButton.setImageResource(R.drawable.ic_bookmark_red_24dp);
                                                ImageView bookmark = view.findViewById(R.id.news_bookmark_icon);
                                                bookmark.setImageResource(R.drawable.ic_bookmark_red_24dp);
                                                Toast.makeText(getActivity(), singleNews.getString("webTitle")+"was added to bookmarks", Toast.LENGTH_LONG).show();
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                dialog.show();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            return true;
                        }
                    });
                    listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,int position, long id){
                            try {
                                String newsId = news.getJSONObject(position).getString("id");
                                Bundle bundle = new Bundle();
                                bundle.putString("id", newsId);
                                Intent intent = new Intent(getActivity(), DetailActivity.class);
                                intent.putExtras(bundle);
                                startActivityForResult(intent, 0);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
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

    public String getImage(JSONObject singleNews){
        try{
            if(singleNews.getJSONObject("fields").has("thumbnail")){
                return singleNews.getJSONObject("fields").getString("thumbnail");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String getLocalTime(JSONObject singleNews){
        try{
            //convert time to local time and change format
            LocalDateTime localDateTime = LocalDateTime.parse(singleNews.getString("webPublicationDate").substring(0,19));
            ZoneId zoneId = ZoneId.of(TimeZone.getDefault().getID());
            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
            ZonedDateTime zdtAtLA = zonedDateTime.withZoneSameInstant(ZoneId.of("America/Los_Angeles"));
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM");
            String date = zdtAtLA.format(fmt);
            return date;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
