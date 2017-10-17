package com.example.moonmayor.firebaseupload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by moonmayor on 9/30/17.
 */

public class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {
    String mUrl;
    ImageView mImageView;

    private static Map<String, Bitmap> cache = new HashMap<>();

    public LoadImageTask (String url, ImageView imageView) {
        mUrl = url;
        mImageView = imageView;
    }

    protected Bitmap doInBackground(Void... unused) {
        if (cache.containsKey(mUrl)) {
            return cache.get(mUrl);
        }

        try {
            URL url = new URL(mUrl);
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }

    protected void onPostExecute(Bitmap result) {
        mImageView.setImageBitmap(result);
    }
}
