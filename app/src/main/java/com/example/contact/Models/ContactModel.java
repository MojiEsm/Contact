package com.example.contact.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ContactModel {
    public String name, phoneNumber ,img;
    public int id;
    public Bitmap decodeImage;

    public boolean expanded;
    public ContactModel(int id,String name,String phoneNumber){
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
        expanded = false;
    }
    public ContactModel(String name,String phone,String img){
        this.name = name;
        this.phoneNumber = phone;
        this.img = img;
        expanded = false;
    }
    public ContactModel(int id,String name, String phno,String img) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phno;
        this.img = img;
        this.expanded = false;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getImg() {
        return img;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
