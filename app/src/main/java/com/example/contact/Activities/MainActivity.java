package com.example.contact;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> arrayName = new ArrayList<>();
            "علی","علی","علی","علی",
            "علی","علی","علی","علی",
            "علی","علی","علی","علی",
            "علی","علی","علی","علی"


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayName.addAll(Arrays.asList(())

    }
}