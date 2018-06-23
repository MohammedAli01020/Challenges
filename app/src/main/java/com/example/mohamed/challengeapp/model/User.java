package com.example.mohamed.challengeapp.model;


public class User {
    private String username;
    private String email;
    private String photoUrl;

    public User() {
    }

    public User(String username, String email, String photoUrl) {
        this.username = username;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
