package com.example.isomi.weatherapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.isomi.weatherapp.Model.Report;
import com.example.isomi.weatherapp.R;
import com.example.isomi.weatherapp.holders.WeatherViewHolder;

import java.util.ArrayList;

/**
 * Created by Isomi on 3/8/17.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherViewHolder> {

    private ArrayList<Report> reports = new ArrayList<>();

    public WeatherAdapter(ArrayList<Report> reports) {
        this.reports = reports;
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        Report report = reports.get(position);
        holder.updateUI(report);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_card, parent, false);
        WeatherViewHolder weatherViewHolder = new WeatherViewHolder(v);
        return weatherViewHolder;
    }
}
