package com.example.muffin.weather.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import com.example.muffin.weather.R;
import com.example.muffin.weather.WeatherArrayAdapter;
import com.example.muffin.weather.WeatherLoader;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private final SharedPreferences.OnSharedPreferenceChangeListener PREF_CHANGE_LISTENER =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    loadWeather(true);
                }
            };
    public static final String PREFERENCE_LAST_CITY = "preference_last_city";
    private static final int WEATHER_LOADER_ID = 1234543321;

    private List<DayForecast> mWeatherList = new ArrayList<>();
    private WeatherForecast mWeatherForecast;
    private WeatherArrayAdapter mAdapter;
    private ListView mWeatherListView;
    GoogleApiClient mClient;

    private String mCity;
    private Location mLocation;
    private boolean mIsByLocation = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getDrawable(R.mipmap.ic_launcher));
        setSupportActionBar(toolbar);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(PREF_CHANGE_LISTENER);
        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();

        mWeatherListView = (ListView) findViewById(R.id.wetherListView);
        mAdapter = new WeatherArrayAdapter(this, mWeatherList);
        mWeatherListView.setAdapter(mAdapter);

        mCity = getLastCity();
        if (!mCity.isEmpty()) {
            loadWeather(false);
        }

        mWeatherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = WeatherPagerActivity.newIntent(MainActivity.this,
                        mWeatherForecast,
                        position);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText locationEditText = (EditText) findViewById(R.id.locationEditText);
                mCity = locationEditText.getText().toString();

                loadWeather(true);

                dismissKeyboard(locationEditText);
            }
        });
    }

    private void loadWeather(boolean restart){
        LoaderManager.LoaderCallbacks<WeatherForecast> callbacks = new WeatherCallbacks();
        if(restart){
            getSupportLoaderManager().restartLoader(WEATHER_LOADER_ID,Bundle.EMPTY,callbacks);
        }else{
            getSupportLoaderManager().initLoader(WEATHER_LOADER_ID,Bundle.EMPTY,callbacks);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mClient.disconnect();
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


    private void writeCityToPreferences(String city) {
        SharedPreferences.Editor editor = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit();
        editor.putString(PREFERENCE_LAST_CITY, city);
        editor.apply();
        editor.commit();
    }

    private void dismissKeyboard(View view) {
        //Получаем менеджер для управления устройствами ввода
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //убираем клавиатуру для заданого View
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }




    private void showWeather(WeatherForecast forecast){
        if(forecast == null) return;
        if(getSupportActionBar() != null){
            getSupportActionBar().setSubtitle(forecast.getCity().getCityName());
        }
        mWeatherForecast = forecast;
        writeCityToPreferences(forecast.getCity().getCityName());
        mWeatherList.clear();
        mWeatherList.addAll(forecast.list);
        Log.d(TAG,"List size : " + mWeatherList.size());
        mAdapter.notifyDataSetChanged();
        mWeatherListView.smoothScrollToPosition(0);
    }

    private class WeatherCallbacks implements LoaderManager.LoaderCallbacks<WeatherForecast>{

        @Override
        public Loader<WeatherForecast> onCreateLoader(int id, Bundle args) {
            if(mLocation != null && mIsByLocation){
                mIsByLocation = false;
                return new WeatherLoader(MainActivity.this,mLocation);
            }

            return new WeatherLoader(MainActivity.this,mCity);
        }

        @Override
        public void onLoadFinished(Loader<WeatherForecast> loader, WeatherForecast data) {
            showWeather(data);
        }

        @Override
        public void onLoaderReset(Loader<WeatherForecast> loader) {

        }
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
        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mIsByLocation = true;
                        mLocation = location;
                        loadWeather(true);
                    }
                });
    }
}
