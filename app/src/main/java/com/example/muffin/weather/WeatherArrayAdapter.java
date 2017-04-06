package com.example.muffin.weather;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muffin.weather.GsonModels.DayForecast;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherArrayAdapter extends ArrayAdapter<DayForecast>{

    private final String TAG = "WeatherArrayAdapter";


    private Map<String,Bitmap> bitmaps = new HashMap<>();

    public WeatherArrayAdapter(Context context, List<DayForecast> forecast){
        super(context,-1,forecast);
    }

    private static class ViewHolder{
        public ImageView conditionImageView;
        public TextView dayTextView;
        public TextView lowTextView;
        public TextView hiTextView;
        public TextView descriptionTextView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d(TAG,"Init view!");
        final DayForecast day = getItem(position);

        final ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item,parent,false);
            //Иниициализация компонентов ViewHolder
            viewHolder.conditionImageView = (ImageView)
                    convertView.findViewById(R.id.conditionImageView);
            viewHolder.dayTextView = (TextView)
                    convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView = (TextView)
                    convertView.findViewById(R.id.lowTextView);
            viewHolder.hiTextView = (TextView)
                    convertView.findViewById(R.id.highTextView);
            viewHolder.descriptionTextView = (TextView)
                    convertView.findViewById(R.id.description_TextView);
            convertView.setTag(viewHolder);
        }else{
            // Cуществующий объект ViewHolder используется заново
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(bitmaps.containsKey(day.getIconUrl())){
            Log.d(TAG,"Has icons!");
            viewHolder.conditionImageView.setImageBitmap(bitmaps.get(day.getIconUrl()));
        }else{
            // Загрузить и вывести значок погодных условий
            Log.d(TAG,"Loading icon");

            new LoadImageTask(viewHolder.conditionImageView).execute(
                     day.getIconUrl());
        }

        viewHolder.dayTextView.setText(day.getDayOfWeek());
        viewHolder.lowTextView.setText(day.temp.getMin());
        viewHolder.hiTextView.setText(day.temp.getMax());
        viewHolder.descriptionTextView.setText(day.weather.get(0).description);

        return convertView;
    }

    private class LoadImageTask extends AsyncTask<String,Void,Bitmap>{
        private ImageView imageView;

        public LoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try{
                bitmap = Picasso.with(getContext()).load(params[0]).get();
                bitmaps.put(params[0],bitmap);
                }catch (Exception e){
                    e.printStackTrace();
                }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
