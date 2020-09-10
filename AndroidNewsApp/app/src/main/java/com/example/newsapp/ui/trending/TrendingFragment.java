package com.example.newsapp.ui.trending;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import com.example.newsapp.GoogleTrendsApiCall;
import com.example.newsapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrendingFragment extends Fragment {

    private TrendingViewModel trendingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        trendingViewModel =
                ViewModelProviders.of(this).get(TrendingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_trending, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.home_swipe_refresh);
        swipeRefreshLayout.setEnabled(false);
        EditText trendingTerm = view.findViewById(R.id.trending_term);
        trendingTerm.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    getTrend(view, v.getText().toString());
                    return true;
                }
                return false;
            }
        });
        getTrend(view, "Coronavirus".toString());
    }

    public void getTrend(View view, String term){
        GoogleTrendsApiCall.make(getActivity(), term, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray data = responseObject.getJSONObject("default").getJSONArray("timelineData");
                    LineChart lineChart = view.findViewById(R.id.lineChart);
                    List<Entry> entrys = new ArrayList<Entry>();
                    for(int i = 0; i < data.length(); i++){
                        Entry e = new Entry(i, Float.valueOf(data.getJSONObject(i).getJSONArray("value").getString(0)));
                        entrys.add(e);
                    }
                    LineDataSet lineDataSet = new LineDataSet(entrys, term);
                    lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    //set dataset color
                    lineDataSet.setColor(Color.parseColor("#6200EE"));
                    lineDataSet.setCircleColor(Color.parseColor("#6200EE"));
                    lineDataSet.setCircleHoleColor(Color.parseColor("#6200EE"));
                    lineDataSet.setValueTextColor(Color.parseColor("#6200EE"));

                    List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                    dataSets.add(lineDataSet);
                    LineData lineData = new LineData(dataSets);
                    lineChart.setData(lineData);
                    //set legend color
                    LegendEntry customeColor = new LegendEntry("Trending Chart for "+term, Legend.LegendForm.DEFAULT,
                            10f, 2f, null, Color.parseColor("#6200EE"));
                    lineChart.getLegend().setCustom(new LegendEntry[]{customeColor});

                    //disable grid background
                    lineChart.getXAxis().setDrawGridLines(false);
                    lineChart.getAxisLeft().setDrawGridLines(false);
                    lineChart.getAxisRight().setDrawGridLines(false);
                    //refresh
                    lineChart.invalidate();
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
