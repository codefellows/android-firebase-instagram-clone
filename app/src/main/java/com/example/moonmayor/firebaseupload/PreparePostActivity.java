package com.example.moonmayor.firebaseupload;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by moonmayor on 9/30/17.
 */

public class PreparePostActivity extends AppCompatActivity {
    public static final String EXTRA_FILEPATH = "filepath";
    public static final String EXTRA_DESCRIPTION = "description";

    String mFilepath;

    Button mCancel;
    Button mPost;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prepare_post);

        mFilepath = getIntent().getStringExtra(EXTRA_FILEPATH);

        mCancel = (Button) findViewById(R.id.cancel);
        mPost = (Button) findViewById(R.id.post);

        attachClickHandlers();
    }

    private void attachClickHandlers() {
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send the user back to wherever they came from.
                onBackPressed();
            }
        });

        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent result = new Intent(PreparePostActivity.this, MainActivity.class);

                EditText editText = (EditText) findViewById(R.id.description);
                String message = editText.getText().toString();
                result.putExtra(EXTRA_FILEPATH, mFilepath);
                result.putExtra(EXTRA_DESCRIPTION, message);

                setResult(RESULT_OK, result);
                finish();
            }
        });
    }
}
