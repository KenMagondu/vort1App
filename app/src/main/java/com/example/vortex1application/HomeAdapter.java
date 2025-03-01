package com.example.vortex1application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class HomeAdapter extends ArrayAdapter<HomeListview> {

    Context context;
    List<HomeListview> arraylistview;


    public HomeAdapter(@NonNull Context context, List<HomeListview> arraylistview) {
        super(context, R.layout.listview,arraylistview);

        this.context = context;
        this.arraylistview = arraylistview;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview,null,true);

        MaterialTextView tvuserid = view.findViewById(R.id.userid);
        MaterialTextView tvusername = view.findViewById(R.id.username);

        tvusername.setText(arraylistview.get(position).getUsername());
        tvuserid.setText(arraylistview.get(position).getUserid());


        return view;

    }
}
