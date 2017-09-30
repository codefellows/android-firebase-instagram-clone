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

    private final LayoutInflater inflater;
    private List<String> mUrls;
    private ViewHolder holder;

    public class ViewHolder {
        TextView url;
        ImageView image;
    }

    public ImageListAdapter(Context context, int resource, List<String> urls) {
        super(context, resource, urls);
        mUrls = urls;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.image_item, null);
        holder = new ViewHolder();
        holder.url = view.findViewById(R.id.url);
        holder.image = view.findViewById(R.id.image);


        String url = getItem(i);
        holder.url.setText(url);

        new LoadImageTask(url, holder.image).execute();

        return view;
    }
}
