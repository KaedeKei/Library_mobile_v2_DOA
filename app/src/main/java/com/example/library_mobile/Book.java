package com.example.library_mobile;

public class Book {
    private int id;
    private String title;
    private String author;
    private String description;
    private byte[] cover;

    public Book(int id, String title, String author, String description, byte[] cover) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.cover = cover;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }
}
