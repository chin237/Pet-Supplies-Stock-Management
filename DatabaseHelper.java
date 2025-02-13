package com.example.petmanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PetDB";
    private static final int DATABASE_VERSION = 2;  // Increment this to trigger upgrade

    // Table name
    public static final String TABLE_PETS = "pets";

    // Column names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_BREED = "breed";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_AVAILABLE = "available";
    public static final String COLUMN_DETAILS = "details";
    public static final String COLUMN_IMAGE_URI = "image_uri";

    // Create table SQL query
    private static final String CREATE_PETS_TABLE =
            "CREATE TABLE " + TABLE_PETS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_BREED + " TEXT, " +
                    COLUMN_PRICE + " REAL, " +
                    COLUMN_AVAILABLE + " INTEGER, " +
                    COLUMN_DETAILS + " TEXT, " +
                    COLUMN_IMAGE_URI + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add image_uri column if upgrading from version 1
            db.execSQL("ALTER TABLE " + TABLE_PETS + 
                      " ADD COLUMN " + COLUMN_IMAGE_URI + " TEXT");
        }
    }

    // CRUD Operations for Pets
    public long insertPet(String name, String breed, double price, boolean available, String details, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_BREED, breed);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_AVAILABLE, available ? 1 : 0);
        values.put(COLUMN_DETAILS, details);
        values.put(COLUMN_IMAGE_URI, imageUri);
        return db.insert(TABLE_PETS, null, values);
    }

    public List<Pet> getAllPets() {
        List<Pet> pets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PETS, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            pets.add(new Pet(
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BREED)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AVAILABLE)) == 1,
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DETAILS)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))
            ));
        }
        cursor.close();
        return pets;
    }

    public int updatePet(Pet pet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, pet.getName());
        values.put(COLUMN_BREED, pet.getBreed());
        values.put(COLUMN_PRICE, pet.getPrice());
        values.put(COLUMN_AVAILABLE, pet.isAvailable() ? 1 : 0);
        values.put(COLUMN_DETAILS, pet.getDetails());
        values.put(COLUMN_IMAGE_URI, pet.getImageUri());

        return db.update(TABLE_PETS, values, 
            COLUMN_ID + " = ?", 
            new String[]{String.valueOf(pet.getId())});
    }

    public int deletePet(long petId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PETS, 
            COLUMN_ID + " = ?", 
            new String[]{String.valueOf(petId)});
    }

    public List<Pet> searchPets(String query) {
        List<Pet> pets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = COLUMN_NAME + " LIKE ? OR " + COLUMN_BREED + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%", "%" + query + "%"};
        
        Cursor cursor = db.query(TABLE_PETS, null, selection, selectionArgs, null, null, null);

        while (cursor.moveToNext()) {
            pets.add(new Pet(
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BREED)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AVAILABLE)) == 1,
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DETAILS)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))
            ));
        }
        cursor.close();
        return pets;
    }

    public int getTotalPetsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PETS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getAvailablePetsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "SELECT COUNT(*) FROM " + TABLE_PETS + 
            " WHERE " + COLUMN_AVAILABLE + " = 1", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public double getTotalRevenue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "SELECT SUM(" + COLUMN_PRICE + ") FROM " + TABLE_PETS + 
            " WHERE " + COLUMN_AVAILABLE + " = 0", null);
        double revenue = 0.0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            revenue = cursor.getDouble(0);
        }
        cursor.close();
        return revenue;
    }

    public double getCurrentMonthRevenue() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Assuming we want to track monthly sales, we'd need a sale_date column
        // For now, returning total revenue
        return getTotalRevenue();
    }

    public int getSoldPetsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "SELECT COUNT(*) FROM " + TABLE_PETS + 
            " WHERE " + COLUMN_AVAILABLE + " = 0", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public void clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PETS, null, null);
    }
}
