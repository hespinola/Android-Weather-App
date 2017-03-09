package com.example.isomi.weatherapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.isomi.weatherapp.Model.Report;
import com.example.isomi.weatherapp.adapters.WeatherAdapter;
import com.example.isomi.weatherapp.holders.WeatherViewHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
    private final String LAT_URL = "?lat="; // 52.3746325
    private final String LON_URL = "&lon="; // 4.7581955
    private final String UNITS_MEASURE = "&units=metric";
    private final String COUNT = "&cnt=10";
    private final String API_KEY = "&appid=b94b8662b8ab1071b3b212d80c687732";
    private Double lat;
    private Double lon;

    private final String TAG = "DONKEY";
    private final int PERMISSION_LOCATION = 96;

    private final String SNOW = "Snow";
    private final String CLEAR = "Clear";
    private final String CLOUDS = "Clouds";
    private final String RAIN = "Rain";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private ArrayList<Report> reports = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<WeatherViewHolder> adapter;
    private LinearLayoutManager layoutManager;

    private ImageView logoImage;
    private TextView todayDate;
    private TextView todayMaxTemp;
    private TextView todayMinTemp;
    private ImageView todayIcon;
    private TextView todayCity;
    private TextView todayDescription;

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addOnConnectionFailedListener(this)
            .addConnectionCallbacks(this)
            .build();

        logoImage = (ImageView)findViewById(R.id.logo_image);
        todayDate = (TextView)findViewById(R.id.today_date);
        todayMaxTemp = (TextView)findViewById(R.id.today_max_temp);
        todayMinTemp = (TextView)findViewById(R.id.today_min_temp);
        todayIcon = (ImageView)findViewById(R.id.today_icon);
        todayCity = (TextView)findViewById(R.id.today_city);
        todayDescription = (TextView)findViewById(R.id.today_description);

        recyclerView = (RecyclerView)findViewById(R.id.weather_recycler);

        adapter = new WeatherAdapter(reports);

        layoutManager = new LinearLayoutManager(getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
        lat = location.getLatitude();
        lon = location.getLongitude();

        downloadData();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "GoogleApiClient Connected!");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
            Log.d(TAG, "Requesting Location Permission");
        } else {
            Log.d(TAG, "Location Permission already granted");
            startLocationServices();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleApiClient Connection Failed");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient Connection Suspended");
    }

    public void startLocationServices() {

        try {
            Log.d(TAG, "Request Location Services Started");
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Starting Location Services from Permission Result");
                    startLocationServices();
                } else {
                    Log.d(TAG, "User denied permission request");
                    Toast.makeText(this, "Can't request Weather Data without location.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void downloadData() {
        String url = BASE_URL + LAT_URL + lat.toString() + LON_URL + lon.toString() + COUNT + UNITS_MEASURE + API_KEY;

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject cityObj = response.getJSONObject("city");

                    String city = cityObj.getString("name");
                    String country = cityObj.getString("country");

                    JSONArray list = response.getJSONArray("list");
                    int count = response.getInt("cnt");

                    for (int i = 0; i < count; i++) {

                        JSONObject obj = list.getJSONObject(i);

                        int date = obj.getInt("dt");
                        JSONObject temp = obj.getJSONObject("temp");

                        Double tempMin = temp.getDouble("min");
                        Double tempMax = temp.getDouble("max");


                        JSONArray weatherArray = obj.getJSONArray("weather");
                        JSONObject weatherObj = weatherArray.getJSONObject(0);
                        String weather = weatherObj.getString("main");

                        Report report = new Report(tempMin.intValue(), tempMax.intValue(), weather, city, country, date);

                        reports.add(report);
                    }

                    updateUI(reports.get(0));
                    reports.remove(0);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    private void updateUI(Report report) {
        final String weatherType = report.getWeather();
        final String formattedDate = formatDate(report.getDate());

        switch (weatherType) {
            case SNOW:
                todayIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.snow));
                logoImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.snow));
                break;

            case CLEAR:
                logoImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.sunny));
                todayIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.sunny));
                break;

            case CLOUDS:
                todayIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.cloudy));
                logoImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.cloudy));
                break;

            case RAIN:
                logoImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rainy));
                todayIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rainy));
                break;
        }

        todayDate.setText(formattedDate);
        todayMaxTemp.setText(report.getTempMax());
        todayMinTemp.setText(report.getTempMin());
        todayCity.setText(report.getCity() + ", " + report.getCountry());
        todayDescription.setText(weatherType);
    }

    private String formatDate(int date) {
        Date time = new Date((long)date * 1000);
        SimpleDateFormat formattedDate = new SimpleDateFormat("MMM d", Locale.US);

        return "Today, " + formattedDate.format(time);
    }
}
