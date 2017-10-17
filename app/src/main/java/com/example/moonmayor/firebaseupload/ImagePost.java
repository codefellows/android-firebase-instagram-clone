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
    private FirebaseDatabase db;
    public String url;
    public String user;
    public String description;
    public List<String> likes;
    public Long timestamp;

    public ImagePost(FirebaseDatabase db, String url, String user, String description, List<String> likes) {
        this.db = db;
        this.url = url;
        this.user = user;
        this.description = description;
        this.likes = likes;
        this.timestamp = (new Date()).getTime();
    }

    public ImagePost(FirebaseDatabase db, String url, String user, String description, List<String> likes, long timestamp) {
        this(db, url, user, description, likes);
        this.timestamp = timestamp;
    }

    public boolean isLikedByUser(String user) {
        return likes.contains(user);
    }

    public void addLike(String username) {
        if (!likes.contains(username)) {
            likes.add(username);

            DatabaseReference likesRef = this.getLikesRef();
            likesRef.child(username).setValue(true);
        }
    }

    public void removeLike(String username) {
        if (likes.contains(username)) {
            likes.remove(username);

            DatabaseReference likesRef = this.getLikesRef();
            likesRef.child(username).removeValue();
        }
    }

    public DatabaseReference getLikesRef() {
        return this.db.getReference().child("photos").child(this.dbKey()).child("likes");
    }

    public String dbKey() {
        return "" + url.hashCode();
    }

    public void saveToDB() {
        // derive the DB key from the url so we can look this post up and modify it later.
        String key = this.dbKey();

        DatabaseReference photoRef = db.getReference().child("photos");
        DatabaseReference imageData = photoRef.child(key);
        imageData.child("url").setValue(url);
        imageData.child("user").setValue(user);
        imageData.child("description").setValue(description);
        imageData.child("timestamp").setValue(ServerValue.TIMESTAMP);

        DatabaseReference likesRef = imageData.child("likes");
        likesRef.child("slothprovider").setValue(true);
        likesRef.child("user2").setValue(true);
        likesRef.child("zuck").setValue(true);
    }

    public static ImagePost buildFromSnapshot(FirebaseDatabase db, DataSnapshot snapshot) {
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

        ImagePost post = new ImagePost(db, url, user, description, likes, timestamp);
        return post;
    }
}
