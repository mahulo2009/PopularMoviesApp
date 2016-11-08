package com.example.android.popularmovies.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;


public class MainFragment extends Fragment {

    ArrayAdapter<String> mMovieAdapter;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_fragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String [] moviesArray = {"A","B","C"};
        List<String> movies = Arrays.asList(moviesArray);

        mMovieAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_movie,
                R.id.list_item_movie_textview,
                movies);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView)rootView.findViewById(R.id.listview_movies);
        listView.setAdapter(mMovieAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String movieStr = mMovieAdapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(),MovieDeatilActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT,movieStr);
                startActivity(detailIntent);
            }
        });

        return rootView;
    }

}
