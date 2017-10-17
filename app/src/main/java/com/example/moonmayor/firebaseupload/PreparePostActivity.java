package com.example.moonmayor.firebaseupload;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by moonmayor on 9/30/17.
 */

public class PreparePostActivity extends AppCompatActivity {
    public static final String EXTRA_FILEPATH = "filepath";
    public static final String EXTRA_DESCRIPTION = "description";

    String mFilepath;

    @BindView(R.id.preview) ImageView mImage;
    @BindView(R.id.cancel) Button mCancel;
    @BindView(R.id.post) Button mPost;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prepare_post);
        ButterKnife.bind(this);

        mFilepath = getIntent().getStringExtra(EXTRA_FILEPATH);
        setPicFromFile(mFilepath);
    }

    @OnClick(R.id.cancel)
    public void cancel() {
        // send the user back to wherever they came from.
        onBackPressed();
    }

    @OnClick(R.id.post)
    public void post() {
        Intent result = new Intent(PreparePostActivity.this, MainActivity.class);

        EditText editText = (EditText) findViewById(R.id.description);
        String message = editText.getText().toString();
        result.putExtra(EXTRA_FILEPATH, mFilepath);
        result.putExtra(EXTRA_DESCRIPTION, message);

        setResult(RESULT_OK, result);
        finish();
    }

    private void setPicFromFile(String filepath) {
        // Get the dimensions of the View
        int targetW = mImage.getWidth();
        int targetH = mImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // prevent divide by zero errors
        targetW = targetW == 0 ? photoW : targetW;
        targetH = targetH == 0 ? photoH : targetH;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(filepath, bmOptions);
        mImage.setImageBitmap(bitmap);
    }
}
