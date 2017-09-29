package com.example.moonmayor.firebaseupload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DrawableUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    private FirebaseDatabase mDB;

    TextView mMessage;
    TextView mMessage2;
    Button mUploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDB = FirebaseDatabase.getInstance();
        mMessage = (TextView) findViewById(R.id.message);
        mMessage2 = (TextView) findViewById(R.id.message2);
        mUploadButton = (Button) findViewById(R.id.upload);

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });

        loadPictures();
    }

    private void upload() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.threebody);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        StorageReference riversRef = mStorageRef.child("images/rivers.jpg");

        riversRef.putBytes(bytes)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                mMessage.setText(downloadUrl.toString());

                addPhotoToList(downloadUrl.toString());
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                mMessage.setText("Error: " + exception.getMessage());
            }
        });
    }

    private void loadPictures() {
        DatabaseReference photoRef = mDB.getReference().child("photos");
        photoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    int textId = 0;
                    int imageId = 0;
                    if (i == 0) {
                        textId = R.id.message;
                        imageId = R.id.imageView;
                    } else if (i == 1) {
                        textId = R.id.message2;
                        imageId = R.id.imageView2;
                    } else if (i == 2) {
                        textId = R.id.message3;
                        imageId = R.id.imageView3;
                    }

                    if (textId == R.id.message || textId == R.id.message2 || textId == R.id.message3) {
                        String uri = snapshot.getValue(String.class);
                        final TextView text = (TextView) findViewById(textId);
                        text.setText(uri);

                        launchAsyncImageLoader(uri, imageId);
                    }
                    i++;
                }
            }

            public void launchAsyncImageLoader(final String uri, final int imageId) {
                final ImageView imageView = (ImageView) findViewById(imageId);

                new AsyncTask<Void, Void, Bitmap>() {
                    protected void onPreExecute() {
                        // Pre Code
                    }
                    protected Bitmap doInBackground(Void... unused) {
                        try {
                            URL url = new URL(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            return bitmap;
                        } catch (IOException e) {
                            mMessage.setText("Error: " + e.getMessage());
                        }
                        return null;
                    }
                    protected void onPostExecute(Bitmap result) {
                        imageView.setImageBitmap(result);
                    }
                }.execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addPhotoToList(String url) {
        DatabaseReference photoRef = mDB.getReference().child("photos");

        DatabaseReference pushRef = photoRef.push();
        pushRef.setValue(url);

        pushRef = photoRef.push();
        pushRef.setValue(url + "2");

        pushRef = photoRef.push();
        pushRef.setValue(url + "3");
    }

    private void attachDBListeners() {
        DatabaseReference myRef = mDB.getReference().child("message");
        myRef.setValue("what up");
        myRef.push();

        DatabaseReference myRef2 = mDB.getReference().child("message2");
        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String val = dataSnapshot.getValue(String.class);
                mMessage2.setText("message changed to: " + val);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String val = dataSnapshot.getValue(String.class);
                mMessage.setText("message changed to: " + val);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void listFiles() {
    }
}
