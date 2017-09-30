package com.example.moonmayor.firebaseupload;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
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

    Context mContext;

    private List<String> mUrls;
    private ViewHolder holder;

    public class ViewHolder {
        ImageView heart;
        TextView likes;
        TextView author;
        TextView timestamp;
        ImageView image;
    }

    public ImageListAdapter(Context context, int resource, List<String> urls) {
        super(context, resource, urls);
        mContext = context;
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
        holder.heart = convertView.findViewById(R.id.heart);
        holder.likes = convertView.findViewById(R.id.likes);
        holder.author = convertView.findViewById(R.id.author);
        holder.timestamp = convertView.findViewById(R.id.timestamp);
        holder.image = convertView.findViewById(R.id.image);

        configureHeartToggler();

        holder.likes.setText("11 likes");
        holder.author.setText("slothprovider @everyone here's more sloths for you!");
        holder.timestamp.setText("54 minutes ago");

        String url = getItem(i);
        new LoadImageTask(url, holder.image).execute();
        return convertView;
    }

    private void configureHeartToggler() {
        final Drawable emptyHeart = mContext.getResources().getDrawable(R.drawable.insta_heart_empty);
        final Drawable fullHeart = mContext.getResources().getDrawable(R.drawable.insta_heart_full);
        holder.heart.setImageDrawable(emptyHeart);

        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView image = (ImageView) view;
                if (image.getDrawable() == emptyHeart) {
                    holder.heart.setImageDrawable(fullHeart);
                } else {
                    holder.heart.setImageDrawable(emptyHeart);
                }
            }
        });

    }
}
