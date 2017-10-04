package com.example.moonmayor.firebaseupload;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.graphics.BitmapFactory.decodeFile;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_PREPARE_POST = 2;
    private String mCurrentPhotoPath;

    Context mContext;

    private StorageReference mStorageRef;
    private FirebaseDatabase mDB;

    ImageView mImageResult;
    Button mTakePictureButton;

    private ListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDB = FirebaseDatabase.getInstance();
        mImageResult = (ImageView) findViewById(R.id.imageResult);
        mTakePictureButton = (Button) findViewById(R.id.takePicture);


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
    }

    private void hidePicture() {
        mImageResult.setVisibility(View.GONE);
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
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.moonmayor.firebaseupload",
                        photoFile);
                // Including this option tells the Intent to write the photo result
                // to a file location, and it does not return an image thumbnail.
                boolean storeToFile = true;
                if (storeToFile) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                }
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                // add the picture to the phone's gallery
                galleryAddPic();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PREPARE_POST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            String filepath = extras.getString(PreparePostActivity.EXTRA_FILEPATH);
            String description = extras.getString(PreparePostActivity.EXTRA_DESCRIPTION);

            setPicFromFile(filepath);
            upload(description);
        } else if  (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            Bitmap decodedBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            if (decodedBitmap != null) {
                mImageResult.setImageBitmap(bitmap);
                // Send user to the PreparePost activity.
                Intent intent = new Intent(MainActivity.this, PreparePostActivity.class);
                intent.putExtra(PreparePostActivity.EXTRA_FILEPATH, mCurrentPhotoPath);
                startActivityForResult(intent, REQUEST_PREPARE_POST);
            } else if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    bitmap = (Bitmap) extras.get("data");
                    mImageResult.setImageBitmap(bitmap);
                } else {
                    // Send user to the PreparePost activity.
                    Intent intent = new Intent(MainActivity.this, PreparePostActivity.class);
                    intent.putExtra(PreparePostActivity.EXTRA_FILEPATH, mCurrentPhotoPath);
                    startActivityForResult(intent, REQUEST_PREPARE_POST);
                }
            }
        }
    }

    private void upload(final String description) {
        if (mImageResult.getDrawable() == null) {
            return;
        }

        Bitmap bitmap = ((BitmapDrawable) mImageResult.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        String bitmapKey = "" + bitmap.hashCode();
        StorageReference storage = mStorageRef.child("images/" + bitmapKey + ".jpg");
        storage.putBytes(bytes)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                addPhotoToList(downloadUrl.toString(), description);
                hidePicture();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    private void loadPictures() {
        DatabaseReference photoRef = mDB.getReference().child("photos");
        photoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onCancelled(DatabaseError error) { }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> urls = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String url = snapshot.child("url").getValue(String.class);
                    String user = snapshot.child("user").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    urls.add(url);
                }

                // reverse the list so oldest items appear at the end.
                Collections.reverse(urls);

                mListAdapter = new ImageListAdapter(mContext, R.layout.image_item, urls);
                ListView list = (ListView) findViewById(R.id.list);
                list.setAdapter(mListAdapter);
            }
        });
    }

    private void addPhotoToList(String url, String description) {
        DatabaseReference photoRef = mDB.getReference().child("photos");
        DatabaseReference imageData = photoRef.push();
        imageData.child("url").setValue(url);
        imageData.child("user").setValue("slothprovider");
        imageData.child("description").setValue(description);

        DatabaseReference likes = imageData.child("likes");
        likes.push().setValue("slothprovider");
        likes.push().setValue("user2");
        likes.push().setValue("zuck");
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

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPicFromFile(String filepath) {
        // Get the dimensions of the View
        int targetW = mImageResult.getWidth();
        int targetH = mImageResult.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        decodeFile(filepath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        // Always use width because the height right now is set to zero.
        int scaleFactor = photoW/targetW;

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = decodeFile(mCurrentPhotoPath, bmOptions);
        mImageResult.setImageBitmap(bitmap);
    }
}
