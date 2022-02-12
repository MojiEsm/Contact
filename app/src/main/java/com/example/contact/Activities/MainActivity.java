package com.example.contact.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contact.Adapters.Adapter_RV_Home;
import com.example.contact.Models.ContactModel;
import com.example.contact.R;
import com.example.contact.database.DatabaseManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;

    public Adapter_RV_Home adapterRvHome;
    private ArrayList<ContactModel> allContacts;
    private DatabaseManager dbm;

    private String encodedImage;

    private EditText edt_Name, edt_PhoneNumber;
    private Button btn_AddContact;
    private ImageView btn_Dialog_Close;
    private CircleImageView btn_add_Image;
    private AlertDialog alertDialog;

    private Toolbar toolbar;
    public RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        designs();
        setAdapter();
        setListener();

        dbm = new DatabaseManager(this);
    }

    private void designs() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void findViews() {
        recyclerView = findViewById(R.id.rv_Home);
        toolbar = findViewById(R.id.toolbar_home);
        floatingActionButton = findViewById(R.id.floating);
    }


    private void setListener() {
        floatingActionButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
            ViewGroup viewGroup = findViewById(R.id.custom);
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_add, viewGroup, false);
            builder.setView(view);
            alertDialog = builder.create();

            btn_Dialog_Close = view.findViewById(R.id.btn_Dialog_Close);
            btn_add_Image = view.findViewById(R.id.btn_Add_CircleImageView);
            edt_Name = view.findViewById(R.id.edt_FullName);
            edt_PhoneNumber = view.findViewById(R.id.edt_phoneNumber);
            btn_AddContact = view.findViewById(R.id.btn_Add);

            setSubListener();


            alertDialog.show();
        });
    }

    private void setSubListener() {
        btn_add_Image.setOnClickListener(vi -> {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        });

        btn_AddContact.setOnClickListener(vi -> {
            final String name = edt_Name.getText().toString();
            final String ph_no = edt_PhoneNumber.getText().toString();
            if (edt_PhoneNumber.getText().toString().equals("") || edt_Name.getText().toString().equals("")) {
                Toast.makeText(this, "لطفا فیلد ها رو پر کنید.", Toast.LENGTH_SHORT).show();
            } else {
                ContactModel newContact = new ContactModel(name, ph_no, encodedImage);
                dbm.insertContact(newContact);
                Toast.makeText(this, "ثبت شد!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }
        });


        btn_Dialog_Close.setOnClickListener(btn -> {
            alertDialog.dismiss();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bitmap != null) {
                btn_add_Image.setBackground(getDrawable(R.color.white));
                btn_add_Image.setImageBitmap(bitmap);
                byte[] imageBytes = imageToByteArray(bitmap);
                encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                Log.e("qqq", "onActivityResult: test  encodedImage " + encodedImage );

            }
        }

        if (resultCode == RESULT_OK && requestCode == adapterRvHome.PICK_IMAGE) {

            Uri imageURI = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                adapterRvHome.circleImageView.setBackground(getDrawable(R.color.white));
                adapterRvHome.circleImageView.setImageBitmap(bitmap);
                byte[] imageBytes = imageToByteArray(bitmap);
                adapterRvHome.encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            }else {
                Toast.makeText(this, "XD", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private byte[] imageToByteArray(Bitmap bitmapImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        return baos.toByteArray();
    }

    private void setAdapter() {
        dbm = new DatabaseManager(this);
        allContacts = dbm.listContacts();
        if (allContacts.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            adapterRvHome = new Adapter_RV_Home(this, allContacts);
            recyclerView.setAdapter(adapterRvHome);
        } else {
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(this, "There is no contact in the database. Start adding now", Toast.LENGTH_LONG).show();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                switch (direction) {
                    case ItemTouchHelper.RIGHT:
                        Uri numberPhone = Uri.parse("tel:" + allContacts.get(position).getPhoneNumber());
                        Intent callIntent = new Intent(Intent.ACTION_DIAL, numberPhone);
                        startActivity(callIntent);
                        adapterRvHome.notifyDataSetChanged();
                        break;
                    case ItemTouchHelper.LEFT:
                        Uri numberSMS = Uri.parse("sms:" + allContacts.get(position).getPhoneNumber());
                        Intent messageIntent = new Intent(Intent.ACTION_VIEW, numberSMS);
                        startActivity(messageIntent);
                        adapterRvHome.notifyDataSetChanged();
                        break;
                }

            }

            @SuppressLint("WrongConstant")
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorMessage))
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_message_white_24)
                        .addSwipeLeftLabel("پیام")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(MainActivity.this,R.color.white))
                        .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP,16)
                        .setSwipeLeftLabelTypeface(Typeface.defaultFromStyle(R.font.iransans))
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorCall))
                        .addSwipeRightActionIcon(R.drawable.ic_baseline_call_white_24)
                        .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP,16)
                        .setSwipeRightLabelTypeface(Typeface.defaultFromStyle(R.font.iransans))
                        .addSwipeRightLabel("تماس")
                        .setSwipeRightLabelColor(ContextCompat.getColor(MainActivity.this,R.color.white))
                        .setIconHorizontalMargin(TypedValue.COMPLEX_UNIT_DIP, 10)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);
    }


}