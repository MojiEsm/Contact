package com.example.contact.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.example.contact.Models.ContactModel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final String DatabaseName = "sqltest.db";
    private static final int Version = 1;
    private static final String TableName = "tbl_contact";
    private static final String dID = "id";
    private static final String dName = "name";
    private static final String dPhoneNumber = "phoneNumber";
    private static final String dProfilePicture = "profilePicture";


    public DatabaseManager(Context context) {
        super(context, DatabaseName, null, Version);
        Log.i("Mahdi", "Database Created!");
    }

    @Override
    public void onCreate(SQLiteDatabase cdb) {
        String cQuery = "CREATE TABLE " + TableName + " ( " + dID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + dName + " TEXT, " + dPhoneNumber + " TEXT, " + dProfilePicture + " TEXT);";
        cdb.execSQL(cQuery);
        Log.i("Mahdi", "Table Created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertContact(ContactModel contactModel) {
        ContentValues values = new ContentValues();
        values.put(dName, contactModel.getName());
        values.put(dPhoneNumber, contactModel.getPhoneNumber());
        values.put(dProfilePicture, contactModel.getImg());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TableName, null, values);
        db.close();
        Log.i("Mahdi", "insertPerson Method");
    }

    public ArrayList<ContactModel> listContacts() {
        String sql = "select * from " + TableName;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ContactModel> storeContacts = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                int id = Integer.valueOf(cursor.getString(0));
                String name = cursor.getString(1);
                String phno = cursor.getString(2);
                String img = cursor.getString(3);
                storeContacts.add(new ContactModel(id, name, phno, img));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return storeContacts;
    }

    public void deleteContact(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TableName, dID + " = ?", new String[]{String.valueOf(id)});
    }

    public void updateContacts(ContactModel contacts) {
        ContentValues values = new ContentValues();
        values.put(dName, contacts.getName());
        values.put(dPhoneNumber, contacts.getPhoneNumber());
        values.put(dProfilePicture, contacts.getImg());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TableName, values, dID + " = ?", new String[]{String.valueOf(contacts.getId())});
    }

//    public void insertContact(ContactModel contactModel) {
//        ContentValues values = new ContentValues();
//        values.put(dName, contactModel.getName());
//        values.put(dPhoneNumber, contactModel.getPhoneNumber());
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.insert(TableName, null, values);
//        db.close();
//        Log.i("Mahdi", "insertPerson Method");
//    }

//    public ArrayList<ContactModel> listContacts() {
//        String sql = "select * from " + TableName;
//        SQLiteDatabase db = this.getReadableDatabase();
//        ArrayList<ContactModel> storeContacts = new ArrayList<>();
//        Cursor cursor = db.rawQuery(sql, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                int id = Integer.valueOf(cursor.getString(0));
//                String name = cursor.getString(1);
//                String phno = cursor.getString(2);
//                storeContacts.add(new ContactModel(id, name, phno));
//            }
//            while (cursor.moveToNext());
//        }
//        cursor.close();
//        return storeContacts;
//    }
//
//    public void deleteContact(int id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TableName, dID + " = ?", new String[]{String.valueOf(id)});
//    }

//    public void updateContacts(ContactModel contacts) {
//        ContentValues values = new ContentValues();
//        values.put(dName, contacts.getName());
//        values.put(dPhoneNumber, contacts.getPhoneNumber());
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.update(TableName, values, dID + " = ?", new String[]{String.valueOf(contacts.getId())});
//    }
}
