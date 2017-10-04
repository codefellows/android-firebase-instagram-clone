package com.example.moonmayor.firebaseupload;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moonmayor on 10/4/17.
 */

public class ImagePost {
    public String url;
    public String user;
    public String description;
    public List<String> likes;

    public ImagePost(String url, String user, String description, List<String> likes) {
        this.url = url;
        this.user = user;
        this.description = description;
        this.likes = likes;
    }

    public boolean isLikedByUser(String user) {
        return likes.contains(user);
    }

    public void saveToDB(FirebaseDatabase db) {
        DatabaseReference photoRef = db.getReference().child("photos");
        DatabaseReference imageData = photoRef.push();
            imageData.child("url").setValue(url);
            imageData.child("user").setValue("slothprovider");
            imageData.child("description").setValue(description);

        DatabaseReference likes = imageData.child("likes");
            likes.push().setValue("slothprovider");
            likes.push().setValue("user2");
            likes.push().setValue("zuck");
    }

    public static ImagePost buildFromSnapshot(DataSnapshot snapshot) {
        String url = snapshot.child("url").getValue(String.class);
        String user = snapshot.child("user").getValue(String.class);
        String description = snapshot.child("description").getValue(String.class);

        List<String> likes = new ArrayList<>();
        for (DataSnapshot like : snapshot.child("likes").getChildren()) {
            likes.add(like.getValue(String.class));
        }

        ImagePost post = new ImagePost(url, user, description, likes);
        return post;
    }
}
