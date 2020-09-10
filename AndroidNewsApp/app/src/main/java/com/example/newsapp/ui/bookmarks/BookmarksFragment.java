package com.example.newsapp.ui.bookmarks;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newsapp.FavortieNewsAdapter;
import com.example.newsapp.NewsPair;
import com.example.newsapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookmarksFragment extends Fragment {

    private BookmarksViewModel bookmarksViewModel;
    FavortieNewsAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bookmarksViewModel =
                ViewModelProviders.of(this).get(BookmarksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.home_swipe_refresh);
        swipeRefreshLayout.setEnabled(false);
        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        if(pref.getAll().size() != 0){//contains favortie article
            TextView textView = view.findViewById(R.id.text_bookmarks);
            textView.setVisibility(View.INVISIBLE);
            Map<String, ?> allEntries = pref.getAll();
            List<NewsPair> newsPairList = new ArrayList<>();
            NewsPair pair = new NewsPair();
            int count = 0;
            for(Map.Entry<String, ?> entry : allEntries.entrySet()){
                count++;
                if(count%2 == 1){
                    pair.setLeft(entry.getValue().toString());
                }
                else{
                    pair.setRight(entry.getValue().toString());
                    newsPairList.add(pair);
                    pair = new NewsPair();
                }
            }
            if(count%2 == 1){
                newsPairList.add(pair);
            }
            ListView listView = view.findViewById(R.id.bookmark_news);
            adapter = new FavortieNewsAdapter(getActivity(), R.layout.bookmark_news_card, newsPairList, this);
            listView.setAdapter(adapter);
        }
        else{
            TextView textView = view.findViewById(R.id.text_bookmarks);
            textView.setVisibility(View.VISIBLE);
        }
    }

    public void deleteNews(String id, String title){
        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(id);
        editor.commit();
        Toast.makeText(getActivity(), title+"was removed from favorites", Toast.LENGTH_LONG).show();
        if(pref.getAll().size() != 0) {//contains favorite article
            TextView textView = getView().findViewById(R.id.text_bookmarks);
            textView.setVisibility(View.INVISIBLE);
            Map<String, ?> allEntries = pref.getAll();
            List<NewsPair> newsPairList = new ArrayList<>();
            NewsPair pair = new NewsPair();
            int count = 0;
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                count++;
                if (count % 2 == 1) {
                    pair.setLeft(entry.getValue().toString());
                } else {
                    pair.setRight(entry.getValue().toString());
                    newsPairList.add(pair);
                    pair = new NewsPair();
                }
            }
            if (count % 2 == 1) {
                newsPairList.add(pair);
            }
            adapter.clear();
            adapter.addAll(newsPairList);
            adapter.notifyDataSetChanged();
        }
        else{
            adapter.clear();
            adapter.notifyDataSetChanged();
            TextView textView = getView().findViewById(R.id.text_bookmarks);
            textView.setVisibility(View.VISIBLE);
        }
    }

    public void refresh(){
        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        if(pref.getAll().size() != 0){//contains favortie article
            TextView textView = getView().findViewById(R.id.text_bookmarks);
            textView.setVisibility(View.INVISIBLE);
            Map<String, ?> allEntries = pref.getAll();
            List<NewsPair> newsPairList = new ArrayList<>();
            NewsPair pair = new NewsPair();
            int count = 0;
            for(Map.Entry<String, ?> entry : allEntries.entrySet()){
                count++;
                if(count%2 == 1){
                    pair.setLeft(entry.getValue().toString());
                }
                else{
                    pair.setRight(entry.getValue().toString());
                    newsPairList.add(pair);
                    pair = new NewsPair();
                }
            }
            if(count%2 == 1){
                newsPairList.add(pair);
            }
            adapter.clear();
            adapter.addAll(newsPairList);
            adapter.notifyDataSetChanged();
        }
        else{
            adapter.clear();
            adapter.notifyDataSetChanged();
            TextView textView = getView().findViewById(R.id.text_bookmarks);
            textView.setVisibility(View.VISIBLE);
        }
    }
}
