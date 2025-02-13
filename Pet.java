package com.example.petmanagement;

public class Pet {
    private long id;
    private String name;
    private String breed;
    private double price;
    private boolean available;
    private String details;
    private String imageUri;

    public Pet(long id, String name, String breed, double price, boolean available, String details, String imageUri) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.price = price;
        this.available = available;
        this.details = details;
        this.imageUri = imageUri;
    }

    // Getters and Setters
    public long getId() { return id; }
    public String getName() { return name; }
    public String getBreed() { return breed; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public String getDetails() { return details; }
    public String getImageUri() { return imageUri; }

    public void setName(String name) { this.name = name; }
    public void setBreed(String breed) { this.breed = breed; }
    public void setPrice(double price) { this.price = price; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setDetails(String details) { this.details = details; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
}
