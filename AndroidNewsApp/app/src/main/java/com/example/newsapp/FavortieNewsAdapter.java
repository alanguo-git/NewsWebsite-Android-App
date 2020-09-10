package com.example.newsapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.newsapp.ui.bookmarks.BookmarksFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

public class FavortieNewsAdapter extends ArrayAdapter {
    Context context;
    BookmarksFragment fragment;

    public FavortieNewsAdapter(Context context, int resource, List<NewsPair> news, Fragment fragment){
        super(context, resource, news);
        this.context = context;
        this.fragment = (BookmarksFragment) fragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Check if an existing view is being reused, otherwise inflate the vie
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bookmark_news_card, parent, false);
        }
        try{
            NewsPair pair = (NewsPair) getItem(position);
            String leftNews = pair.getLeft();
            String rightNews = pair.getRight();
            CardView leftCard = convertView.findViewById(R.id.bookmark_left_card);
            CardView rightCard = convertView.findViewById(R.id.bookmark_right_card);
            leftCard.setVisibility(View.INVISIBLE);
            rightCard.setVisibility(View.INVISIBLE);
            if(leftNews != null){
                JSONObject jsonObject = new JSONObject(leftNews);
                ImageView leftImageView = convertView.findViewById(R.id.bookmark_news_img_left);
                TextView leftTitle = convertView.findViewById(R.id.bookmark_news_title_left);
                TextView leftDate = convertView.findViewById(R.id.bookmark_news_time_left);
                TextView leftSection = convertView.findViewById(R.id.bookmark_news_section_left);
                ImageView leftBookmark = convertView.findViewById(R.id.bookmark_share_icon_left);
                if(jsonObject.getString("image") != null){
                    Picasso.get().load(jsonObject.getString("image")).into(leftImageView);
                }
                else{
                    leftImageView.setImageResource(R.drawable.guardian_default);
                }
                leftTitle.setText(jsonObject.getString("title"));
                leftDate.setText(jsonObject.getString("time"));
                leftSection.setText(jsonObject.getString("section"));
                leftCard.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v){
                        try{
                            Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.custom);
                            ImageView dialogImage = dialog.findViewById(R.id.dialog_image);
                            Picasso.get().load(jsonObject.getString("image")).into(dialogImage);
                            TextView dialogText = dialog.findViewById(R.id.dialog_title);
                            dialogText.setText(jsonObject.getString("title"));
                            ImageButton twitterButton = dialog.findViewById(R.id.dialog_twitter_button);
                            String url = "https://theguardian.com/" + jsonObject.getString("id");
                            twitterButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse("https://twitter.com/intent/tweet?text=Check out this Link:&url="+url+"&hashtags=CSCI571NewsSearch");
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    context.startActivity(intent);
                                }
                            });
                            ImageButton bookmarkButton = dialog.findViewById(R.id.dialog_bookmark_button);
                            bookmarkButton.setImageResource(R.drawable.ic_bookmark_red_24dp);
                            bookmarkButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try{
                                        fragment.deleteNews(jsonObject.getString("id"), jsonObject.getString("title"));
                                        dialog.dismiss();
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
                leftCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        try {
                            Bundle bundle = new Bundle();
                            bundle.putString("id", jsonObject.getString("id"));
                            Intent intent = new Intent(context, DetailActivity.class);
                            intent.putExtras(bundle);
                            ((Activity)context).startActivityForResult(intent, 0);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                leftBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            fragment.deleteNews(jsonObject.getString("id"), jsonObject.getString("title"));
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                leftCard.setVisibility(View.VISIBLE);
            }
            if(rightNews != null){
                JSONObject jsonObject = new JSONObject(rightNews);
                ImageView rightImageView = convertView.findViewById(R.id.bookmark_news_img_right);
                TextView rightTitle = convertView.findViewById(R.id.bookmark_news_title_right);
                TextView rightDate = convertView.findViewById(R.id.bookmark_news_time_right);
                TextView rightSection = convertView.findViewById(R.id.bookmark_news_section_right);
                ImageView rightBookmark = convertView.findViewById(R.id.bookmark_share_icon_right);
                if(jsonObject.getString("image") != null){
                    Picasso.get().load(jsonObject.getString("image")).into(rightImageView);
                }
                else{
                    rightImageView.setImageResource(R.drawable.guardian_default);
                }
                rightTitle.setText(jsonObject.getString("title"));
                rightDate.setText(jsonObject.getString("time"));
                rightSection.setText(jsonObject.getString("section"));
                rightCard.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v){
                        try{
                            Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.custom);
                            ImageView dialogImage = dialog.findViewById(R.id.dialog_image);
                            Picasso.get().load(jsonObject.getString("image")).into(dialogImage);
                            TextView dialogText = dialog.findViewById(R.id.dialog_title);
                            dialogText.setText(jsonObject.getString("title"));
                            ImageButton twitterButton = dialog.findViewById(R.id.dialog_twitter_button);
                            String url = "https://theguardian.com/" + jsonObject.getString("id");
                            twitterButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse("https://twitter.com/intent/tweet?text=Check out this Link:&url="+url+"&hashtags=CSCI571NewsSearch");
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    context.startActivity(intent);
                                }
                            });
                            ImageButton bookmarkButton = dialog.findViewById(R.id.dialog_bookmark_button);
                            bookmarkButton.setImageResource(R.drawable.ic_bookmark_red_24dp);
                            bookmarkButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try{
                                        fragment.deleteNews(jsonObject.getString("id"), jsonObject.getString("title"));
                                        dialog.dismiss();
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
                rightCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        try {
                            Bundle bundle = new Bundle();
                            bundle.putString("id", jsonObject.getString("id"));
                            Intent intent = new Intent(context, DetailActivity.class);
                            intent.putExtras(bundle);
                            ((Activity)context).startActivityForResult(intent, 0);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                rightBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            fragment.deleteNews(jsonObject.getString("id"), jsonObject.getString("title"));
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                rightCard.setVisibility(View.VISIBLE);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }
}
