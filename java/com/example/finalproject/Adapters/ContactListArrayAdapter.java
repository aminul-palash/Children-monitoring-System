package com.example.finalproject.Adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.finalproject.Models.ContactListModel;

import com.example.finalproject.R;

import java.util.ArrayList;
import java.util.List;

public class ContactListArrayAdapter extends ArrayAdapter<ContactListModel> {

    private Activity context;
    List<ContactListModel> artists;

    public ContactListArrayAdapter(Activity context, ArrayList<ContactListModel> artists) {
        super(context, R.layout.fragment_contact__list_, artists);
        this.context = context;
        this.artists = artists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.custom_contact_list, null, true);

        TextView textViewName = listViewItem.findViewById(R.id.textViewName);
        TextView textViewGenre = listViewItem.findViewById(R.id.textViewNumber);

        ContactListModel artist = artists.get(position);
        textViewName.setText(artist.getName());
        textViewGenre.setText(artist.getNumber());

        return listViewItem;
    }
}