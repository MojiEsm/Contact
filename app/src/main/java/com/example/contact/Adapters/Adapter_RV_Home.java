package com.example.contact.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contact.Activities.MainActivity;
import com.example.contact.Models.ContactModel;
import com.example.contact.R;
import com.example.contact.database.DatabaseManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_RV_Home extends RecyclerView.Adapter<Adapter_RV_Home.MyViewHolder> {
    public static final int PICK_IMAGE = 101;
    private Activity activity;
    private ArrayList<ContactModel> listContacts;
    private ArrayList<ContactModel> mArrayList;
    private ContactModel contactModel;
    private DatabaseManager dbm;
    public String encodedImage;
    public CircleImageView circleImageView;
    private Bitmap decodeImage;

    private boolean isExpanded;

    public Adapter_RV_Home(Activity activity, ArrayList<ContactModel> listContacts) {
        this.activity = activity;
        this.listContacts = listContacts;
        this.mArrayList = listContacts;
        dbm = new DatabaseManager(activity);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.item_rv_home, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        contactModel = listContacts.get(position);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageByte = baos.toByteArray();
        String ImageString = contactModel.img;
        if (ImageString == null) {
            holder.imgView.setImageResource(R.drawable.person);
        } else {
            imageByte = Base64.decode(ImageString, Base64.DEFAULT);
            decodeImage = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
            holder.imgView.setBackground( activity.getDrawable(R.color.white));
            holder.imgView.setImageBitmap(decodeImage);
        }


        holder.txt_Name.setText(contactModel.getName());
        holder.txt_PhoneNO.setText(contactModel.getPhoneNumber());
        isExpanded = contactModel.isExpanded();
        holder.constraintLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return listContacts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txt_Name, txt_PhoneNO;
        private ConstraintLayout constraintLayout;
        private CardView cardView;
        private ImageView imgView, img_Call, img_Message, img_Delete, img_Edit;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_Name = itemView.findViewById(R.id.txt_itemRV_Name);
            txt_PhoneNO = itemView.findViewById(R.id.txt_itemRV_PhoneNumber);
            imgView = itemView.findViewById(R.id.img_ItemRV_Profile);
            img_Call = itemView.findViewById(R.id.img_ItemRV_Call);
            img_Message = itemView.findViewById(R.id.img_ItemRV_Message);
            img_Delete = itemView.findViewById(R.id.img_ItemRV_Delete);
            img_Edit = itemView.findViewById(R.id.img_ItemRV_Edit);

            cardView = itemView.findViewById(R.id.cardView_ItemRV);
            constraintLayout = itemView.findViewById(R.id.expandedLayout);

            setListener();

            cardView.setOnClickListener(this);
        }

        private void setListener() {
            img_Call.setOnClickListener(v -> {
                Uri number = Uri.parse("tel:" + listContacts.get(getAdapterPosition()).getPhoneNumber());
                activity.startActivity(new Intent(Intent.ACTION_DIAL, number));
            });

            img_Message.setOnClickListener(v -> {
                Uri number = Uri.parse("sms:" + listContacts.get(getAdapterPosition()).getPhoneNumber());
                activity.startActivity(new Intent(Intent.ACTION_VIEW, number));
            });
            img_Delete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("حذف");
                builder.setMessage("آیا مایل به حذف مخاطب هستید؟");
                builder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbm.deleteContact(listContacts.get(getAdapterPosition()).getId());
                        listContacts.remove(getAdapterPosition());
                        notifyDataSetChanged();
                        Toast.makeText(activity, "حذف شد!", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
            img_Edit.setOnClickListener(v -> {
                editAction();
            });
        }

        private void editAction() {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
            ViewGroup viewGroup = activity.findViewById(R.id.custom);
            View view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit, viewGroup, false);
            builder.setView(view);
            AlertDialog alertDialog = builder.create();

            ImageView btn_Close = view.findViewById(R.id.btn_DialogEdit_Close);
            LinearLayout lnr_ChangePic = view.findViewById(R.id.btn_edit_CircleImageViewAddImage);
            EditText edt_Name = view.findViewById(R.id.edt_FullName_DialogEdit);
            EditText edt_PhoneNumber = view.findViewById(R.id.edt_phoneNumber_DialogEdit);
            CircleImageView civ = view.findViewById(R.id.btn_edit_CircleImageView);
            Button btn_EditContact = view.findViewById(R.id.btn_edit);
            circleImageView = view.findViewById(R.id.btn_edit_CircleImageView);

            lnr_ChangePic.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                activity.startActivityForResult(intent, PICK_IMAGE);
            });

            if (contactModel != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] imageByte = baos.toByteArray();
                String ImageString = contactModel.getImg();
                if (ImageString == null) {
                    civ.setImageResource(R.drawable.account_circle_);
                } else {
                    imageByte = Base64.decode(ImageString, Base64.DEFAULT);
                    Bitmap decodeImage = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                    civ.setImageBitmap(decodeImage);
                }
                edt_Name.setText(contactModel.name);
                edt_PhoneNumber.setText(String.valueOf(contactModel.phoneNumber));

            }


            btn_EditContact.setOnClickListener(vi -> {
                final int id = listContacts.get(getAdapterPosition()).getId();

                final String name = edt_Name.getText().toString();
                final String ph_no = edt_PhoneNumber.getText().toString();
                final String img = encodedImage;
                if (edt_Name.getText().toString().equals("") || edt_PhoneNumber.getText().toString().equals("")) {
                    Toast.makeText(activity, "فیلد های خالی را پر کنید!", Toast.LENGTH_LONG).show();
                } else {
                    dbm.updateContacts(new ContactModel(id, name, ph_no,img));
                    Toast.makeText(activity, "ویرایش شد!", Toast.LENGTH_SHORT).show();
                    ((Activity) activity).finish();
                    activity.startActivity(((Activity) activity).getIntent());
                }
            });

            btn_Close.setOnClickListener(btn -> {
                alertDialog.dismiss();
            });

            alertDialog.show();
        }

        @Override
        public void onClick(View view) {
            ContactModel contactModel = listContacts.get(getAdapterPosition());
            int position = contactModel.getId();

            contactModel.setExpanded(!contactModel.isExpanded());
            notifyItemChanged(getAdapterPosition());
        }
    }


}
