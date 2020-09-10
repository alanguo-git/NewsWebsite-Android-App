package com.example.newsapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TimeZone;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<JSONObject> mDataset;

    //provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        View view;
        ImageView newsImage;
        TextView newsTitle;
        TextView newsTime;
        TextView newsSection;
        ImageView bookmarkView;
        public MyViewHolder(View v) {
            super(v);
            this.view = v;
            this.newsImage = v.findViewById(R.id.news_img);
            this.newsTitle = v.findViewById(R.id.news_title);
            this.newsTime = v.findViewById(R.id.news_time);
            this.newsSection = v.findViewById(R.id.news_section);
            this.bookmarkView = v.findViewById(R.id.news_bookmark_icon);
        }
    }

    public NewsAdapter(Context context, ArrayList<JSONObject> myDataset){
        this.context = context;
        this.mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_card, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        JSONObject singleNews = mDataset.get(position);
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        try {
            String imageSrc = getImage(singleNews);
            if(imageSrc != null){
                Picasso.get().load(imageSrc).into(holder.newsImage);
            }
            else{
                holder.newsImage.setImageResource(R.drawable.guardian_default);
            }
            holder.newsTitle.setText(singleNews.getString("webTitle"));
            holder.newsTime.setText(getTimeDifference(singleNews));
            holder.newsSection.setText(singleNews.getString("sectionName"));
            if(pref.contains(singleNews.getString("id"))){
                holder.bookmarkView.setImageResource(R.drawable.ic_bookmark_red_24dp);
            }
            else{
                holder.bookmarkView.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //set onclick for bookmark
        holder.bookmarkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    SharedPreferences.Editor editor = pref.edit();
                    if(pref.contains(singleNews.getString("id"))){ //delete
                        editor.remove(singleNews.getString("id"));
                        editor.commit();
                        holder.bookmarkView.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
                        Toast.makeText(context, singleNews.getString("webTitle")+"was removed from favorites", Toast.LENGTH_LONG).show();
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
                        holder.bookmarkView.setImageResource(R.drawable.ic_bookmark_red_24dp);
                        Toast.makeText(context, singleNews.getString("webTitle")+"was added to bookmarks", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try{
                    Dialog dialog = new Dialog(context);
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
                            context.startActivity(intent);
                        }
                    });
                    ImageButton bookmarkButton = dialog.findViewById(R.id.dialog_bookmark_button);
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
                                    Toast.makeText(context, singleNews.getString("webTitle")+"was removed from favorites", Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(context, singleNews.getString("webTitle")+"was added to bookmarks", Toast.LENGTH_LONG).show();
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
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String newsId = singleNews.getString("id");
                    Bundle bundle = new Bundle();
                    bundle.putString("id", newsId);
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtras(bundle);
                    ((Activity)context).startActivityForResult(intent, 0);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public String getImage(JSONObject singleNews){
        try{
            if((singleNews.getJSONObject("blocks").has("main") &&
                    (singleNews.getJSONObject("blocks").getJSONObject("main")
                            .getJSONArray("elements").getJSONObject(0)
                            .getJSONArray("assets").length() != 0 &&
                            singleNews.getJSONObject("blocks").getJSONObject("main")
                                    .getJSONArray("elements").getJSONObject(0)
                                    .getJSONArray("assets").getJSONObject(0).has("file")))){
                return singleNews.getJSONObject("blocks").getJSONObject("main")
                        .getJSONArray("elements").getJSONObject(0)
                        .getJSONArray("assets").getJSONObject(0).getString("file");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String getTimeDifference(JSONObject singleNews){
        try{
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
            return timeText;
        } catch (Exception e){
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
