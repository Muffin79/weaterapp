package com.example.muffin.weather.fragments;


import android.preference.PreferenceFragment;

import android.support.annotation.Nullable;
import android.os.Bundle;

import com.example.muffin.weather.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

}
