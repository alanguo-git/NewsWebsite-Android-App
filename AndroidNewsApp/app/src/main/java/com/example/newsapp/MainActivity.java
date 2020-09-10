package com.example.newsapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.widget.SearchView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.newsapp.ui.bookmarks.BookmarksFragment;
import com.example.newsapp.ui.headlines.HeadlinesFragment;
import com.example.newsapp.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AutoSuggestAdapter autoSuggestAdapter;
    private Handler handler;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private LocationManager locationManager;
    private int REQUEST_CODE = 99; //request code that used to get permission of access location
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWeather();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavInflater navInflater = navController.getNavInflater();
        NavGraph navGraph = navInflater.inflate(R.navigation.mobile_navigation);
        navController.setGraph(navGraph, new Bundle());
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener(){
            @Override
            public void onDestinationChanged(NavController controller, NavDestination destination, Bundle arguments) {
                if(destination.getId() == R.id.navigation_home) {
                    getWeather();
                }
            }
        });
        NavigationUI.setupWithNavController(navView, navController);

        //set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void getWeather(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //check if ACCESS_FINE_LOCATION permission is enabled
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        //get current location
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(lastKnownLocation == null){
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String city = addresses.get(0).getLocality();
        final String state = addresses.get(0).getAdminArea();
        //make api call to get weather information
        OpenWeatherApiCall.make(this, addresses.get(0).getLocality(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    String temperature = Long.toString(Math.round(Double.parseDouble(responseObject.getJSONObject("main").getString("temp"))));
                    String summary = responseObject.getJSONArray("weather").getJSONObject(0).getString("main");
                    Fragment frag = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                    //set text view in home fragment
                    ((TextView)frag.getView().findViewById(R.id.text_city)).setText(city);
                    ((TextView)frag.getView().findViewById(R.id.text_state)).setText(state);
                    ((TextView)frag.getView().findViewById(R.id.text_temperature)).setText(temperature + " \u2103");
                    ((TextView)frag.getView().findViewById(R.id.text_summary)).setText(summary);
                    switch (summary){
                        case "Clouds":
                            ((ImageView)frag.getView().findViewById(R.id.weather_image)).setImageResource(R.drawable.cloudy_weather);
                            break;
                        case "Clear":
                            ((ImageView)frag.getView().findViewById(R.id.weather_image)).setImageResource(R.drawable.clear_weather);
                            break;
                        case "Snow":
                            ((ImageView)frag.getView().findViewById(R.id.weather_image)).setImageResource(R.drawable.snowy_weather);
                            break;
                        case "Rain":
                            ((ImageView)frag.getView().findViewById(R.id.weather_image)).setImageResource(R.drawable.rainy_weather);
                            break;
                        case "Drizzle":
                            ((ImageView)frag.getView().findViewById(R.id.weather_image)).setImageResource(R.drawable.rainy_weather);
                            break;
                        case"Thunderstorm":
                            ((ImageView)frag.getView().findViewById(R.id.weather_image)).setImageResource(R.drawable.thunder_weather);
                            break;
                        default:
                            ((ImageView)frag.getView().findViewById(R.id.weather_image)).setImageResource(R.drawable.sunny_weather);
                    }

                } catch (Exception e) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_menu);
        SearchView searchView = (SearchView) menuItem.getActionView();
        //searchView.setQueryHint("Search Here!");
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        //set text to black
        searchAutoComplete.setTextColor(Color.BLACK);

        if (autoSuggestAdapter == null) {
            autoSuggestAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);
        }
        searchAutoComplete.setAdapter(autoSuggestAdapter);



        // Listen to search view item on click event.
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
            }
        });

        // Below event is triggered when submit search query.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                bundle.putString("keyword", query);
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //only make an API call to the Bing Autosuggest after the user enters 3 characters
                if(newText.length() < 3){
                    autoSuggestAdapter.setData(new ArrayList<>());
                    autoSuggestAdapter.notifyDataSetChanged();
                }
                else{
                    handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                    handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                            AUTO_COMPLETE_DELAY);
                }
                return false;
            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAutoComplete.getText())) {
                        makeApiCall(searchAutoComplete.getText().toString());
                    }
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void makeApiCall(String keyword){
        AutosuggestApiCall.make(this, keyword, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<String> stringList = new ArrayList<>();
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray array = responseObject.getJSONArray("suggestionGroups").getJSONObject(0).getJSONArray("searchSuggestions");
                    //Restrict suggestions to 5 values.
                    for (int i = 0; i < Math.min(5, array.length()); i++) {
                        JSONObject row = array.getJSONObject(i);
                        stringList.add(row.getString("displayText"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                autoSuggestAdapter.setData(stringList);
                autoSuggestAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        NavHostFragment navHostFragment;
        switch(navController.getCurrentDestination().getId()){
            case R.id.navigation_home:
                navHostFragment = (NavHostFragment) getSupportFragmentManager().getPrimaryNavigationFragment();
                HomeFragment homeFragment = (HomeFragment) navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
                homeFragment.getNews();
                break;
            case R.id.navigation_headlines:
                break;
            case R.id.navigation_bookmarks:
                navHostFragment = (NavHostFragment) getSupportFragmentManager().getPrimaryNavigationFragment();
                BookmarksFragment bookmarksFragment= (BookmarksFragment) navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
                bookmarksFragment.refresh();
                break;
            default:
        }
    }
}
