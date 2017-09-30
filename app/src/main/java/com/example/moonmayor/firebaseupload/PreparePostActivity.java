package com.example.moonmayor.firebaseupload;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 * Created by moonmayor on 9/30/17.
 */

public class PreparePostActivity extends AppCompatActivity {
    Button mCancel;
    Button mPost;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prepare_post);

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
                Intent intent = new Intent(PreparePostActivity.this, MainActivity.class);

                EditText editText = (EditText) findViewById(R.id.description);
                String message = editText.getText().toString();
                intent.putExtra(EXTRA_MESSAGE, message);

                startActivity(intent);
            }
        });
    }
}
