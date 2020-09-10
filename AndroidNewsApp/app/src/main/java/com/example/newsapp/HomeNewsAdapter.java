package com.example.newsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class HomeNewsAdapter extends ArrayAdapter {
    Context context;

    public HomeNewsAdapter(Context context, int resource, List<JSONObject> news){
        super(context, resource, news);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Check if an existing view is being reused, otherwise inflate the vie
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.news_card, parent, false);
        }
        ImageView newsImage = convertView.findViewById(R.id.news_img);
        TextView newsTitle = convertView.findViewById(R.id.news_title);
        TextView newsTime = convertView.findViewById(R.id.news_time);
        TextView newsSection = convertView.findViewById(R.id.news_section);
        JSONObject singleNews = (JSONObject) getItem(position);
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        ImageView bookmarkView = convertView.findViewById(R.id.news_bookmark_icon);
        try {
            if(singleNews.getJSONObject("fields").has("thumbnail")){
                Picasso.get().load(singleNews.getJSONObject("fields").getString("thumbnail")).into(newsImage);
            }
            else{
                newsImage.setImageResource(R.drawable.guardian_default);
            }
            newsTitle.setText(singleNews.getString("webTitle"));
            //transfer time to ZonedDateTime of default time zone
            LocalDateTime localDateTime = LocalDateTime.parse(singleNews.getString("webPublicationDate").substring(0,19));
            ZoneId zoneId = ZoneId.of(TimeZone.getDefault().getID());
            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
            //get current time
            ZonedDateTime now = ZonedDateTime.now();
            //calculate difference
            Duration duration = Duration.between(zonedDateTime, now);
            //convert to xxh ago/ xxm ago/ xxs ago
            String timeText = new String();
            if(duration.toDays() >= 1){
                timeText = duration.toDays() + "d ago";
            }
            else if(duration.toHours() >= 1){
                timeText = duration.toHours() + "h ago";
            }
            else if(duration.toMinutes() >= 1){
                timeText = duration.toMinutes() + "m ago";
            }
            else{
                timeText = duration.toMillis()/1000 + "s ago";
            }
            newsTime.setText(timeText);
            newsSection.setText(singleNews.getString("sectionName"));
            if(pref.contains(singleNews.getString("id"))){
                bookmarkView.setImageResource(R.drawable.ic_bookmark_red_24dp);
            }
            else{
                bookmarkView.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //set onclick for bookmark
        bookmarkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    SharedPreferences.Editor editor = pref.edit();
                    if(pref.contains(singleNews.getString("id"))){ //delete
                        editor.remove(singleNews.getString("id"));
                        editor.commit();
                        bookmarkView.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
                        Toast.makeText(context, newsTitle.getText()+"was removed from favorites", Toast.LENGTH_LONG).show();
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
                        bookmarkView.setImageResource(R.drawable.ic_bookmark_red_24dp);
                        Toast.makeText(context, newsTitle.getText()+"was added to bookmarks", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        return convertView;
    }
    public String getImage(JSONObject singleNews){
        try{
            if(singleNews.getJSONObject("fields").has("thumbnail")){
                return singleNews.getJSONObject("fields").getString("thumbnail");
            }
        }catch (Exception e){
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
