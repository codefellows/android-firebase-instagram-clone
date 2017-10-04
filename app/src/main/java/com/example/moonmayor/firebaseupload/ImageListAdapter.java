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

public class ImageListAdapter extends ArrayAdapter<ImagePost> {

    Context mContext;

    private List<ImagePost> mPosts;
    private ViewHolder holder;

    public class ViewHolder {
        ImageView heart;
        TextView likes;
        TextView author;
        TextView timestamp;
        ImageView image;

        public ViewHolder(View view) {
            this.heart = view.findViewById(R.id.heart);
            this.likes = view.findViewById(R.id.likes);
            this.author = view.findViewById(R.id.author);
            this.timestamp = view.findViewById(R.id.timestamp);
            this.image = view.findViewById(R.id.image);

            // just some default text for now.
            this.likes.setText("11 likes");
            this.author.setText("slothprovider @everyone here's more sloths for you!");
            this.timestamp.setText("54 minutes ago");

            configureHeartToggler();
        }

        private void configureHeartToggler() {
            final Drawable emptyHeart = mContext.getResources().getDrawable(R.drawable.insta_heart_empty);
            final Drawable fullHeart = mContext.getResources().getDrawable(R.drawable.insta_heart_full);
            this.heart.setImageDrawable(emptyHeart);

            this.heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView image = (ImageView) view;
                    if (image.getDrawable() == emptyHeart) {
                        ViewHolder.this.heart.setImageDrawable(fullHeart);
                    } else {
                        ViewHolder.this.heart.setImageDrawable(emptyHeart);
                    }
                }
            });
        }
    }

    public ImageListAdapter(Context context, int resource, List<ImagePost> posts) {
        super(context, resource, posts);
        mContext = context;
        mPosts = posts;
    }

    public void add(ImagePost post) {
        mPosts.add(post);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.image.setImageBitmap(null);
        }

        ImagePost post = getItem(i);
        String url = post.url;
        new LoadImageTask(url, holder.image).execute();
        return convertView;
    }
}
