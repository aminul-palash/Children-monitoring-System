package com.example.finalproject.Adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.finalproject.Models.HistoryModel;
import com.example.finalproject.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryArrayAdapter extends ArrayAdapter<HistoryModel> {

    private Activity context;
    List<HistoryModel> artists;

    public HistoryArrayAdapter(Activity context, ArrayList<HistoryModel> artists) {
        super(context, R.layout.fragment_history_, artists);
        this.context = context;
        this.artists = artists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.custom_history_list_view, null, true);

        TextView textViewName = listViewItem.findViewById(R.id.textViewName);
        TextView textViewGenre = listViewItem.findViewById(R.id.textViewUrl);

        HistoryModel artist = artists.get(position);
        textViewName.setText(artist.getDomain());
        textViewGenre.setText(artist.getUrl());

        return listViewItem;
    }
}