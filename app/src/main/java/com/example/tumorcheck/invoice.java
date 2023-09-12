package com.example.tumorcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.InputStream;

public class invoice extends AppCompatActivity {
    private FirebaseAuth authProfile;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mRef = database.getReference("Records");
    myinvoiceobj invoiceobj = new myinvoiceobj();
    Button save, PrinButton;
    EditText name, email;
    Spinner spinner;

    String[] mylist;
    ArrayAdapter<String> adapter;
    long invoiceNo = 0;
    ImageView r_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        save = findViewById(R.id.button2);
        PrinButton = findViewById(R.id.button);
        name = findViewById(R.id.editTextText);
        email = findViewById(R.id.editTextText2);
        spinner = findViewById(R.id.spinner);
        mylist = new String[]{"male", "female"};
        r_image = findViewById(R.id.r_image);

        r_image.setImageURI(getIntent().getData());

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mylist);
        spinner.setAdapter(adapter);


        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            Bitmap imageBitmap = getBitmapFromUri(imageUri);

            // Scale the Bitmap
            Bitmap scaledbmp = Bitmap.createScaledBitmap(imageBitmap, 200, 161, false);

            // Display the scaled image in the ImageView
            r_image.setImageBitmap(scaledbmp);

            // Create a new PDF file
            String pdfPath = "/sdcard/scaled_image.pdf";
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//        mRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                invoiceNo = snapshot.getChildrenCount();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


//    }
//
//    private void callOnclick() {
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                invoiceobj.invoiceNo = invoiceNo + 1;
//                invoiceobj.Name = String.valueOf(name.getText());
//                invoiceobj.eMail = String.valueOf(email.getText());
//                invoiceobj.Type = spinner.getSelectedItem().toString();
//
//                mRef.child(String.valueOf(invoiceNo + 1)).setValue(invoiceobj);
//            }
//        });
    }

