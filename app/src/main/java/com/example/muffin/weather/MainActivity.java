package com.example.muffin.weather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.muffin.weather.GsonModels.DayForecast;
import com.example.muffin.weather.GsonModels.WeatherForecast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements PostExecuteCallback {

    private final String TAG = "MainActivity";
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    loadWeather(getLastCity());
                }
            };
    public static final String PREFERENCE_LAST_CITY = "preference_last_city";

    private List<DayForecast> weatherList = new ArrayList<>();
    private WeatherForecast weatherForecast;
    private WeatherArrayAdapter adapter;
    private ListView weatherListView;
    GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getDrawable(R.mipmap.ic_launcher));
        setSupportActionBar(toolbar);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();

        weatherListView = (ListView) findViewById(R.id.wetherListView);
        adapter = new WeatherArrayAdapter(this, weatherList);
        weatherListView.setAdapter(adapter);

        String city = getLastCity();
        if (!city.isEmpty()) {
            loadWeather(city);
        }

        weatherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = WeatherPagerActivity.newIntent(MainActivity.this,
                        weatherForecast,
                        position);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText locationEditText = (EditText) findViewById(R.id.locationEditText);

                loadWeather(locationEditText.getText().toString());

                dismissKeyboard(locationEditText);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = SettingsActivity.newIntent(this);
                startActivity(intent);

                return true;
            case R.id.action_find_location:
                getForecastByLocation();
                return true;
            default:

                return false;
        }
    }

    private String getLastCity() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREFERENCE_LAST_CITY, "");
    }

    private void loadWeather(String city) {
        URL url = createURL(city);

        if (url != null) {
            GetWeatherTask localWeatherTask =
                    new GetWeatherTask(findViewById(R.id.coordinatorLayout), MainActivity.this);
            localWeatherTask.execute(url);
        } else {
            Snackbar.make(findViewById(R.id.coordinatorLayout),
                    R.string.invalid_url, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void writeCityToPreferences(String city) {
        SharedPreferences.Editor editor = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit();
        editor.putString(PREFERENCE_LAST_CITY, city);
        editor.commit();
    }

    private void dismissKeyboard(View view) {
        //Получаем менеджер для управления устройствами ввода
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //убираем клавиатуру для заданого View
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Nullable
    private URL createURL(String city) {
        String apiKey = getString(R.string.api_key);
        String baseURL = getString(R.string.web_service_url);
        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String countOfDays = defaultPreferences
                .getString(getString(R.string.pref_countOfDays), "");
        String tempUnits = defaultPreferences
                .getString(getString(R.string.pref_temp_units), "");

        try {
            //Создаем URL
            String urlString = Uri.parse(baseURL).buildUpon()
                    .appendQueryParameter("q", city)
                    .appendQueryParameter("units",tempUnits)
                    .appendQueryParameter("cnt",countOfDays)
                    .appendQueryParameter("APPID",apiKey)
                    .build().toString();
            return new URL(urlString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    private URL createURL(Location location) {
        String apiKey = getString(R.string.api_key);
        String baseURL = getString(R.string.web_service_url);
        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String countOfDays = defaultPreferences
                .getString(getString(R.string.pref_countOfDays), "");
        String tempUnits = defaultPreferences
                .getString(getString(R.string.pref_temp_units), "");
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());
        Log.d(TAG, "Lat :" + lat + "\nLon : " + lon);

        try {
            //Создаем URL
            String urlString = Uri.parse(baseURL).buildUpon()
                    .appendQueryParameter("lat",lat)
                    .appendQueryParameter("lon",lon)
                    .appendQueryParameter("units",tempUnits)
                    .appendQueryParameter("cnt",countOfDays)
                    .appendQueryParameter("APPID",apiKey)
                    .build().toString();
            return new URL(urlString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private void getForecastByLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        URL url = createURL(location);

                        if (url != null) {
                            GetWeatherTask localWeatherTask =
                                    new GetWeatherTask(findViewById(R.id.coordinatorLayout), MainActivity.this);
                            localWeatherTask.execute(url);
                        } else {
                            Snackbar.make(findViewById(R.id.coordinatorLayout),
                                    R.string.invalid_url, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void postExecute(WeatherForecast forecast) {
        weatherForecast = forecast;
        weatherList.clear();
        getSupportActionBar().setSubtitle(forecast.getCity().getCityName());
        writeCityToPreferences(forecast.getCity().getCityName());
        weatherList.addAll(forecast.list);
        Log.d(TAG,"List size : " + weatherList.size());
        adapter.notifyDataSetChanged();
        weatherListView.smoothScrollToPosition(0);
    }


}
