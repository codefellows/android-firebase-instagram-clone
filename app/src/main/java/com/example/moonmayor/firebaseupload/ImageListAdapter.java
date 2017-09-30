package com.example.moonmayor.firebaseupload;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moonmayor on 9/29/17.
 */

public class ImageListAdapter extends ArrayAdapter<String> {

    private List<String> mUrls;
    private ViewHolder holder;

    public class ViewHolder {
        TextView url;
        ImageView image;
    }

    public ImageListAdapter(Context context, int resource, List<String> urls) {
        super(context, resource, urls);
        mUrls = urls;
    }

    public void add(String url) {
        mUrls.add(url);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_item, parent, false);
        }

        holder = new ViewHolder();
        holder.url = convertView.findViewById(R.id.url);
        holder.image = convertView.findViewById(R.id.image);


        String url = getItem(i);
        holder.url.setText(url);

        new LoadImageTask(url, holder.image).execute();
        return convertView;
    }
}
