package com.example.mohamed.challengeapp.model;

public class ChatMessage {
    private String uid;
    private String author;
    private String text;
    private String fileUri;

    public ChatMessage() {
    }

    public ChatMessage(String uid, String author, String text, String fileUri) {
        this.uid = uid;
        this.author = author;
        this.text = text;
        this.fileUri = fileUri;
    }

    public String getUid() {
        return uid;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public String getFileUri() {
        return fileUri;
    }
}
