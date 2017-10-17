package com.example.moonmayor.firebaseupload;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by moonmayor on 10/4/17.
 */

public class ImagePost {
    public String url;
    public String user;
    public String description;
    public List<String> likes;
    public Long timestamp;

    private DatabaseReference mLikesRef;

    public ImagePost(String url, String user, String description, List<String> likes) {
        this.url = url;
        this.user = user;
        this.description = description;
        this.likes = likes;
        this.timestamp = (new Date()).getTime();
    }

    public ImagePost(String url, String user, String description, List<String> likes, long timestamp) {
        this(url, user, description, likes);
        this.timestamp = timestamp;
    }

    public boolean isLikedByUser(String user) {
        return likes.contains(user);
    }

    public void addLike(String username) {
        if (!likes.contains(username)) {
            likes.add(username);
            mLikesRef.push().setValue(user);
        }
    }

    public void removeLike(String username) {
        if (likes.contains(username)) {
            likes.remove(username);

            mLikesRef.removeValue();
            for (String user : likes) {
                mLikesRef.push().setValue(user);
            }
        }
    }

    public DatabaseReference getLikesRef() {
        return null;
        //return db.getReference().child("photos").child(url).child("likes");
    }

    public void saveToDB(FirebaseDatabase db) {
        // derive the DB key from the url so we can look this post up and modify it later.
        String key = "" + url.hashCode();

        DatabaseReference photoRef = db.getReference().child("photos");
        DatabaseReference imageData = photoRef.child(key);
        imageData.child("url").setValue(url);
        imageData.child("user").setValue(user);
        imageData.child("description").setValue(description);
        imageData.child("timestamp").setValue(ServerValue.TIMESTAMP);

        mLikesRef = imageData.child("likes");
        mLikesRef.child("slothprovider").setValue(true);
        mLikesRef.child("user2").setValue(true);
        mLikesRef.child("zuck").setValue(true);
    }

    public static ImagePost buildFromSnapshot(DataSnapshot snapshot) {
        String url = (String) snapshot.child("url").getValue();
        String user = (String) snapshot.child("user").getValue();
        String description = (String) snapshot.child("description").getValue();

        Long timestamp = (new Date()).getTime();
        if (snapshot.hasChild("timestamp")) {
            timestamp = (Long) snapshot.child("timestamp").getValue();
        }

        List<String> likes = new ArrayList<>();
        for (DataSnapshot like : snapshot.child("likes").getChildren()) {
            likes.add(like.getKey());
        }

        ImagePost post = new ImagePost(url, user, description, likes, timestamp);
        return post;
    }
}
