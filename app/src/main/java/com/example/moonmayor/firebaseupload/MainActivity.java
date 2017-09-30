package com.example.moonmayor.firebaseupload;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DrawableUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath;

    Context mContext;

    private StorageReference mStorageRef;
    private FirebaseDatabase mDB;

    TextView mMessage;
    ImageView mImageResult;
    Button mTakePictureButton;
    Button mUploadButton;

    private List<String> imageUrls;
    private ListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDB = FirebaseDatabase.getInstance();
        mMessage = (TextView) findViewById(R.id.message);
        mImageResult = (ImageView) findViewById(R.id.imageResult);
        mTakePictureButton = (Button) findViewById(R.id.takePicture);
        mUploadButton = (Button) findViewById(R.id.upload);


        attachClickListeners();
        loadPictures();
    }

    private void attachClickListeners() {
        mTakePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                mMessage.setText("Error: " + ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.moonmayor.firebaseupload",
                        photoFile);
                // Including this option tells the Intent to write the photo result
                // to a file location, and it does not return an image thumbnail.
                boolean storeToFile = false;
                if (storeToFile) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                }
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                // add the picture to the phone's gallery
                //galleryAddPic();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if  (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            if (data != null) {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
                mImageResult.setImageBitmap(bitmap);
            }
        }
    }

    private void upload() {
        if (mImageResult.getDrawable() == null) {
            mMessage.setText("Take or select a photo before uploading.");
            return;
        }

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
        final List<String> urls = new ArrayList<>();

        DatabaseReference photoRef = mDB.getReference().child("photos");
        photoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onCancelled(DatabaseError error) { }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uri = snapshot.getValue(String.class);
                    urls.add(uri);
                    i++;
                }

                mListAdapter = new ImageListAdapter(mContext, R.layout.image_item, urls);
                ListView list = (ListView) findViewById(R.id.list);
                list.setAdapter(mListAdapter);
                mMessage.setText("urls: " + urls.size());

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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
