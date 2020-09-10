package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class DetailActivity extends AppCompatActivity {
    private String id;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        id = getIntent().getExtras().getString("id");
        findViewById(R.id.detail_card).setVisibility(View.INVISIBLE);
        getNewsDetail();
    }
    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_OK);
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        return true;
    }

    public void getNewsDetail(){
        GuardianNewsApiCall.getDetail(this, id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject responseObject = new JSONObject(response);
                    ImageView imageView = findViewById(R.id.detail_image);
                    if(getImage(responseObject) != null){
                        Picasso.get().load(getImage(responseObject)).into(imageView);
                    }
                    else{
                        imageView.setImageResource(R.drawable.guardian_default);
                    }
                    TextView titleView = findViewById(R.id.detail_title);
                    TextView sectionView = findViewById(R.id.detail_section);
                    TextView timeView = findViewById(R.id.detail_time);
                    TextView paragraphView = findViewById(R.id.detail_paragraph);
                    Toolbar toolbar = findViewById(R.id.toolbar);

                    //convert time to local time and change format
                    LocalDateTime localDateTime = LocalDateTime.parse(responseObject.getJSONObject("response")
                            .getJSONObject("content").getString("webPublicationDate").substring(0,19));
                    ZoneId zoneId = ZoneId.of(TimeZone.getDefault().getID());
                    ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
                    ZonedDateTime zdtAtLA = zonedDateTime.withZoneSameInstant(ZoneId.of("America/Los_Angeles"));
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
                    String date = zdtAtLA.format(fmt);

                    StringBuilder paragraph = new StringBuilder();
                    int length = responseObject.getJSONObject("response").getJSONObject("content")
                                    .getJSONObject("blocks").getJSONArray("body").length();
                    for(int i = 0; i < length; i++){
                        paragraph.append(HtmlCompat.fromHtml(responseObject.getJSONObject("response").getJSONObject("content")
                                .getJSONObject("blocks").getJSONArray("body").getJSONObject(i).getString("bodyHtml"), HtmlCompat.FROM_HTML_MODE_LEGACY));
                    }
                    titleView.setText(responseObject.getJSONObject("response").getJSONObject("content").getString("webTitle"));

                    //set toolbar
                    toolbar.setTitle(responseObject.getJSONObject("response").getJSONObject("content").getString("webTitle"));
                    Toolbar.LayoutParams parms = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
                    parms.gravity = Gravity.END;
                    View v = getLayoutInflater().inflate(R.layout.detail_page_buttons, null);
                    ImageButton bookmarkButton = v.findViewById(R.id.detail_bookmark_button);
                    SharedPreferences pref = getSharedPreferences("MyPref", 0); // 0 - for private mode
                    if(pref.contains(id)){
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
                                if(pref.contains(id)){ //delete
                                    editor.remove(id);
                                    editor.commit();
                                    bookmarkButton.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
                                    Toast.makeText(DetailActivity.this, responseObject
                                            .getJSONObject("response").getJSONObject("content")
                                            .getString("webTitle")+"was removed from favorites", Toast.LENGTH_LONG).show();
                                }
                                else{ //add
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("id", id);
                                    jsonObject.put("image", getImage(responseObject));
                                    jsonObject.put("title", responseObject.getJSONObject("response").getJSONObject("content").getString("webTitle"));
                                    DateTimeFormatter newFmt = DateTimeFormatter.ofPattern("dd MMM");
                                    jsonObject.put("time", zdtAtLA.format(newFmt));
                                    jsonObject.put("section", responseObject.getJSONObject("response").getJSONObject("content").getString("sectionName"));
                                    editor.putString(id, jsonObject.toString());
                                    editor.commit(); // commit changes
                                    bookmarkButton.setImageResource(R.drawable.ic_bookmark_red_24dp);
                                    Toast.makeText(DetailActivity.this, responseObject
                                            .getJSONObject("response").getJSONObject("content")
                                            .getString("webTitle")+"was added to bookmarks", Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    //set onclick function for twitter button
                    ImageButton twitterButton = v.findViewById(R.id.detail_twitter_button);
                    String twitterUrl = "https://theguardian.com/" + id;
                    twitterButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse("https://twitter.com/intent/tweet?text=Check out this Link:&url="+twitterUrl+"&hashtags=CSCI571NewsSearch");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });
                    v.setLayoutParams(parms);
                    toolbar.addView(v);

                    //set view
                    sectionView.setText(responseObject.getJSONObject("response").getJSONObject("content").getString("sectionName"));
                    timeView.setText(date);
                    paragraphView.setText(paragraph.toString());
                    findViewById(R.id.detail_progress_bar).setVisibility(View.INVISIBLE);
                    findViewById(R.id.detail_fetching_news).setVisibility(View.INVISIBLE);
                    findViewById(R.id.detail_card).setVisibility(View.VISIBLE);
                    final String url = responseObject.getJSONObject("response").getJSONObject("content").getString("webUrl");
                    findViewById(R.id.view_full).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse(url);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });
                }catch (Exception e){
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

    public String getImage(JSONObject responseObject){
        try{
            if(responseObject.getJSONObject("response").getJSONObject("content")
                    .getJSONObject("blocks").getJSONObject("main").has("elements")&&
                    responseObject.getJSONObject("response").getJSONObject("content")
                            .getJSONObject("blocks").getJSONObject("main").getJSONArray("elements")
                    .getJSONObject(0).getJSONArray("assets").length() != 0){
                 return responseObject.getJSONObject("response").getJSONObject("content")
                        .getJSONObject("blocks").getJSONObject("main").getJSONArray("elements")
                        .getJSONObject(0).getJSONArray("assets").getJSONObject(0)
                        .getString("file");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
