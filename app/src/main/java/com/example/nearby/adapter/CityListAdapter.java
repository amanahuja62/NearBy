package com.example.nearby.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CityListAdapter extends ArrayAdapter<String> {
    List<String> cities = new ArrayList<>();
    public CityListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }

    public void setCities(List<String> cities){
        this.cities.clear();
        this.cities.addAll(cities);
        notifyDataSetChanged();
    }

}
