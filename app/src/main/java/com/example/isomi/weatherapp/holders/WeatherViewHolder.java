package com.example.isomi.weatherapp.holders;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.isomi.weatherapp.Model.Report;
import com.example.isomi.weatherapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Isomi on 3/8/17.
 */

public class WeatherViewHolder extends RecyclerView.ViewHolder {

    private ImageView forecastImage;
    private TextView forecastDay;
    private TextView forecastDescription;
    private TextView forecastMaxTemp;
    private TextView forecastMinTemp;

    private final String SNOW = "Snow";
    private final String CLEAR = "Clear";
    private final String CLOUDS = "Clouds";
    private final String RAIN = "Rain";
    private Context context;


    public WeatherViewHolder(View itemView) {
        super(itemView);

        forecastImage = (ImageView)itemView.findViewById(R.id.forecast_img);
        forecastDay = (TextView)itemView.findViewById(R.id.forecast_day);
        forecastDescription = (TextView)itemView.findViewById(R.id.forecast_description);
        forecastMaxTemp = (TextView)itemView.findViewById(R.id.forecast_max_temp);
        forecastMinTemp = (TextView)itemView.findViewById(R.id.forecast_min_temp);

        context = itemView.getContext();
    }

    public void updateUI(Report report) {
        final String weatherType = report.getWeather();
        final String formattedDate = formatDate(report.getDate());

        switch (weatherType) {
            case SNOW:
                forecastImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.snow_mini));
                break;

            case CLEAR:
                forecastImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.sunny_mini));
                break;

            case CLOUDS:
                forecastImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cloudy_mini));
                break;

            case RAIN:
                forecastImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rainy_mini));
                break;
        }


        forecastDay.setText(formattedDate);
        forecastDescription.setText(weatherType);
        forecastMaxTemp.setText(report.getTempMax());
        forecastMinTemp.setText(report.getTempMin());
    }

    private String formatDate(int date) {
        Date time = new Date((long)date * 1000);
        SimpleDateFormat formattedDate = new SimpleDateFormat("EEEE, d", Locale.US);

        return formattedDate.format(time);
    }
}
