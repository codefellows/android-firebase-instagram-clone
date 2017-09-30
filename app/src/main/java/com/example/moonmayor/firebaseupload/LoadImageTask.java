package com.example.moonmayor.firebaseupload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

/**
 * Created by moonmayor on 9/30/17.
 */

public class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {
    String mUrl;
    ImageView mImageView;

    public LoadImageTask (String url, ImageView imageView) {
       mUrl = url;
        mImageView = imageView;
    }

    protected Bitmap doInBackground(Void... unused) {
        try {
            URL url = new URL(mUrl);
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bitmap;
        } catch (IOException e) {

        }
        return null;
    }

    protected void onPostExecute(Bitmap result) {
        mImageView.setImageBitmap(result);
    }
}
