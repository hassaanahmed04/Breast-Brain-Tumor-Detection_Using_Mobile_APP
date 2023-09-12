package com.example.tumorcheck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.element.Cell;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.layout.element.Table;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.layout.Document;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
//import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import android.graphics.pdf.PdfDocument;


public class Report_generation extends AppCompatActivity {
    // Write a message to the database
    private FirebaseAuth authProfile;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference userRef = mDatabase.getReference("users").child(currentUserUid);
    DatabaseReference myRef = userRef.child("record");
    DatabaseReference myRef2 = userRef.child("record");
    dataobj Dataobj = new dataobj();
    Button g_report, back;
    EditText Name, Age, phonenumber, Cnic, address;
    Spinner Gender;
    String[] reportlist;
    TextView tum_text;
    private String hs_name,hs_dep,hs_mobile,hs_email,hs_type;
    private FirebaseUser firebaseUser;
    Bitmap bmp, scaledbmp,imageBitmap,profilebmp,logobmp;
    long reportNo = 2000;
    long totalpic=2001;
    long tableCount=2001;
    int imagevalue;
    String result,characteristics;
    Date dateobj;
    DateFormat dateFormat;
    String imageUriString,tumorResult,Heading,gender;
    Bitmap profilepic;
    Uri uploadedImageUri, imageUrii;
    private StorageReference storageReference, myreference;
    //    SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
    private static final String TAG = "ReportGeneration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_report_generation);
        Name = findViewById(R.id.Name);
        Cnic = findViewById(R.id.Cnic);
        Age = findViewById(R.id.Age);
        address = findViewById(R.id.address);
        phonenumber = findViewById(R.id.phonenumber);
        g_report = findViewById(R.id.g_report);
        tum_text=findViewById(R.id.tum_text);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logobg);
        logobmp = Bitmap.createScaledBitmap(bmp, 550 , 550  , false);

        Spinner Gender = findViewById(R.id.spinner_gender);
        authProfile=FirebaseAuth.getInstance();
//        gender=Gender.getSelectedItem().toString();
        //checking the internet if it is connected or not
        if (!isInternetConnected()) {
            showInternetAlertDialog();
        }
        imageUriString = getIntent().getStringExtra("imageUri");



Heading=getIntent().getStringExtra("heading");
        tumorResult = getIntent().getStringExtra("tumorResult");
        tum_text.setText(tumorResult);
        if(tumorResult.equals("Brain Tumor found")){
            result="the presence of a brain tumor the ";
            characteristics="Specific characteristics of the tumor";

        } else if (tumorResult.equals("No Tumor Found")) {
            result="that there is no brain tumor found the ";
            characteristics="Specific characteristics of the brain image ";

        }else if (tumorResult.equals("Breast Cancer found")) {
            result="the presence of a Breast Cancer ";
            characteristics="Specific characteristics of the Breast Cancer ";

        }else if (tumorResult.equals("No Cancer Found")) {
            result="that there is no Breast Cancer is found ";
            characteristics="Specific characteristics of the breast image ";

        }


        //Gender spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        Gender.setAdapter(adapter);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportNo = snapshot.getChildrenCount() + 2000;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (imageUriString != null) {
            imageUrii = Uri.parse(imageUriString);
            Log.d("ImageInfo", "Image URI: " + imageUrii);

            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUrii);
                if (imageBitmap != null) {
                    int imageWidth = imageBitmap.getWidth();
                    int imageHeight = imageBitmap.getHeight();
                    Log.d("ImageInfo", "Image Width: " + imageWidth + ", Height: " + imageHeight);

                    scaledbmp = Bitmap.createScaledBitmap(imageBitmap, 150, 100, false);
                    Toast.makeText(Report_generation.this, "Image bitmap valid", Toast.LENGTH_SHORT).show();

                    // Create a new PDF document

                    // ... (rest of the PDF generation code)
                } else {
                    Toast.makeText(Report_generation.this, "Image bitmap is null", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(Report_generation.this, "Error loading image", Toast.LENGTH_SHORT).show();
            }


            uploadImage();

        } else {
            Toast.makeText(Report_generation.this, "Image is null", Toast.LENGTH_SHORT).show();
        }

        g_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateobj = new Date();
                dateFormat = new SimpleDateFormat("dd-MM-yyy HH:MM:SS");

                Dataobj.reportNo = reportNo + 1;

                Dataobj.patientName = String.valueOf(Name.getText());
                Dataobj.address = String.valueOf(address.getText());
                Dataobj.date = new Date().getTime();
                Dataobj.gender = Gender.getSelectedItem().toString();
                Dataobj.age = Double.parseDouble(String.valueOf(Age.getText()));
                Dataobj.cnic = Double.parseDouble(String.valueOf(Cnic.getText()));
                Dataobj.phoneNo = Double.parseDouble(String.valueOf(phonenumber.getText()));

                Dataobj.tumorResult=String.valueOf(tum_text.getText());
                myRef.child(String.valueOf(reportNo + 1)).setValue(Dataobj);


                printPDF();



            }


        });
        showUserProfile(firebaseUser);

    }
    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        DatabaseReference referencProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referencProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                regdataobj readUserDetails = snapshot.getValue(regdataobj.class);
                if (readUserDetails != null) {

                    // Set user dp (after user has uploaded)
                     uploadedImageUri = firebaseUser.getPhotoUrl();
                    hs_name=firebaseUser.getDisplayName();
                    hs_email=firebaseUser.getEmail();
                    hs_dep=readUserDetails.Department;
                    hs_mobile=readUserDetails.PhoneNo;
                    hs_type=readUserDetails.Hospital_type;
//                    Picasso.with(Report_generation.this).load(uri).into(profilepic);

                    // Download image from Firebase Storage and generate PDF
                    generatePDFWithImage();

                } else {
                    Toast.makeText(Report_generation.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Report_generation.this, "Something went wrong really", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void generatePDFWithImage() {
        // Get the FirebaseStorage instance
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Get a reference to the uploaded image using the stored URI
        StorageReference imageRef = storage.getReferenceFromUrl(uploadedImageUri.toString());
        final File localFile;
        try {
            localFile = File.createTempFile("downloaded_image", getFileExtensionFromUri(uploadedImageUri));
        } catch (IOException e) {
            e.printStackTrace();
            return; // Handle the error here
        }
        // Download the image and generate PDF
        imageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // File download succeeded, localFile contains the image
                        profilepic = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        if (profilepic != null) {
                            int imageWidth = profilepic.getWidth();
                            int imageHeight = profilepic.getHeight();
                            Log.d("ImageInfo", "Image Width: " + imageWidth + ", Height: " + imageHeight);

                            profilebmp= Bitmap.createScaledBitmap(profilepic, 120, 120, false);
//                            Toast.makeText(Report_generation.this, "profile", Toast.LENGTH_LONG).show();



                            // ... (rest of the PDF generation code)
                        } else {
                            showInternetAlertDialog();
                            Toast.makeText(Report_generation.this, "Image bitmap is null", Toast.LENGTH_SHORT).show();
                        }

                        // Generate PDF using the downloaded image

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors that occur during download
                    }
                });
    }
    private String getFileExtensionFromUri(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }




    private void uploadImage() {
        Toast.makeText(Report_generation.this,"total table"+Cnic.getText().toString(), Toast.LENGTH_SHORT).show();



        firebaseUser=authProfile.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference("patientPics");
        myreference=storageReference.child(firebaseUser.getDisplayName());
        if(imageUrii != null){

            StorageReference fileReference=myreference.child(tableCount +"."+getFileExtension(imageUrii));
            //upload image to storage

            fileReference.putFile(imageUrii).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Report_generation.this," file", Toast.LENGTH_SHORT).show();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Report_generation.this,"no file", Toast.LENGTH_SHORT).show();
                    showInternetAlertDialog();
                }
            });
        }else {
//            progressBar.setVisibility(View.GONE);
            Toast.makeText(Report_generation.this, "no file seleted", Toast.LENGTH_SHORT).show();

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void printPDF() {


        if (Name.getText().toString().length() == 0 || Cnic.getText().toString().length() == 0 || address.getText().toString().length() == 0 || Age.getText().toString().length() == 0 || phonenumber.getText().toString().length() == 0) {
            Toast.makeText(Report_generation.this, "Some fields are empty", Toast.LENGTH_LONG).show();

        } else {
            PdfDocument myPdfDocument = new PdfDocument();
            Paint paint = new Paint();
            Paint forLinePaint = new Paint();
            Paint fortitlePaint = new Paint();
            Paint paintConst = new Paint();
            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
            Canvas canvas = myPage.getCanvas();
            //logo of the application
            canvas.drawBitmap(logobmp, 45, 130, paint);
            canvas.drawBitmap(profilebmp, 20, 20, paint);
            paintConst.setTextSize(12.0f);

            canvas.drawText("Hospital Name : " + hs_name, 592 - 240, 40, paintConst);
            canvas.drawText("Department : " + hs_dep, 592 - 240, 60, paintConst);
            canvas.drawText("Email : " + hs_email, 592 - 240, 80, paintConst);
            canvas.drawText("Phone# : " + hs_mobile, 592 - 240, 100, paintConst);
            canvas.drawText("Date : " + dateFormat.format(dateobj), 592 - 240, 120, paintConst);

            paintConst.setTextSize(15.5f);
            paintConst.setColor(Color.rgb(0, 0, 0));
            paintConst.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            paintConst.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Patient Name    : ", 20, 170, paintConst);
            canvas.drawText("Patient Cnic    : ", 20, 190, paintConst);
            canvas.drawText("Patient Address : ", 20, 210, paintConst);

            paint.setTextSize(15.5f);
            paint.setColor(Color.rgb(0, 0, 0));

            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(Name.getText().toString(), 150, 170, paint);
            canvas.drawText(Cnic.getText().toString(), 150, 190, paint);
            canvas.drawText(address.getText().toString(), 150, 210, paint);
            canvas.drawText("Report No:", 350, 190, paint);

            //header box
            paint.setTextAlign(Paint.Align.RIGHT);


            paint.setStyle(Paint.Style.STROKE);

            paint.setStrokeWidth(1);
            canvas.drawRect(350, 20, 592 - 20, 150, paint);
//Patient details box after heading
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            canvas.drawRect(20, 280, 300, 400, paint);
            //details
            canvas.drawText("Name   : ", 30, 300, paintConst);
            canvas.drawText("Age    : ", 30, 323, paintConst);
            canvas.drawText("Phone# : ", 30, 346, paintConst);
            canvas.drawText("Date   : ", 30, 369, paintConst);
            canvas.drawText("Result : ", 30, 390, paintConst);

            canvas.drawText(Name.getText().toString(), 120, 300, paintConst);
            canvas.drawText(Age.getText().toString(), 120, 323, paintConst);
            canvas.drawText(phonenumber.getText().toString(), 120, 346, paintConst);
            canvas.drawText(dateFormat.format(dateobj), 120, 369, paintConst);
            canvas.drawText(tum_text.getText().toString(), 120, 390, paintConst);
            //picture of the tumor
            canvas.drawBitmap(scaledbmp, 400, 290, paint);
//Findings box
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);

            canvas.drawRect(20, 430, 592 - 20, 580, paint);
            canvas.drawText("FINDINGS : ", 30, 450, paintConst);
            //finding box internal para
            String paragraphText = "Based on MRI findings the diagnosis indicates "+result +
                    characteristics+" can be seen inside the image of the scann" +
                    "ed image. Further evaluation and management should be discussed with your healt"+
                    "hcare provider or a neurosurgeon"
                    ;

            paintConst.setColor(Color.BLACK);
            paintConst.setTextSize(15.5f);

            float x = 30; // X-coordinate for starting position
            float y = 475; // Y-coordinate for starting position
            float lineHeight = 20; // Height of each line

            // Split the paragraph into words
            String[] words = paragraphText.split(" ");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                // Measure the width of the current line
                float lineWidth = paint.measureText(currentLine.toString() + word + " ");
                if (lineWidth < 480) { // Adjust this value based on available width
                    currentLine.append(word).append(" ");
                } else {
                    // Draw the current line and reset for the next line
                    canvas.drawText(currentLine.toString(), x, y, paintConst);
                    y += lineHeight;
                    currentLine = new StringBuilder(word + " ");
                }
            }
            canvas.drawText(currentLine.toString(), x, y, paintConst);

            //Notes
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            canvas.drawRect(20, 600, 592 - 20, 730, paint);
            canvas.drawText("NOTE : ", 30, 620, paintConst);
            String paragraphTextnote = "Please note that while AI plays a vital role, your healthcare provider's assessment remains paramount." +
                    " If a tumor or cancer indication is detected, your provider will recommend further evaluation and tailored medical guidance." +
                    "Your well-being is our priority. For any questions or concerns about your report or AI's role, kindly contact your healthcare provider."+
                    " We're committed to providing you with comprehensive care and support during your medical journey."
                    ;

            paintConst.setColor(Color.BLACK);
            paintConst.setTextSize(11.0f);

            float xnote= 30; // X-coordinate for starting position
            float ynote = 640; // Y-coordinate for starting position
            float linenote = 20; // Height of each line

            // Split the paragraph into words
            String[] wordsnote = paragraphTextnote.split(" ");
            StringBuilder currentLinenote = new StringBuilder();

            for (String wordnote : wordsnote) {
                // Measure the width of the current line
                float lineWidthnote = paint.measureText(currentLinenote.toString() + wordnote + " ");
                if (lineWidthnote < 700) { // Adjust this value based on available width
                    currentLinenote.append(wordnote).append(" ");
                } else {
                    // Draw the current line and reset for the next line
                    canvas.drawText(currentLinenote.toString(), xnote, ynote, paintConst);
                    ynote += linenote;
                    currentLinenote = new StringBuilder(wordnote + " ");
                }
            }
            canvas.drawText(currentLinenote.toString(), xnote, ynote, paintConst);



            paint.setTextSize(8.5f);


            forLinePaint.setStyle(Paint.Style.STROKE);
            forLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
            forLinePaint.setStrokeWidth(2);




            fortitlePaint.setTextAlign(Paint.Align.CENTER);
            fortitlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            fortitlePaint.setTextSize(22);
            canvas.drawText(Heading+" Diagnosis Report", 595 / 2, 250, fortitlePaint);

            paint.setTextSize(15.0f);
            canvas.drawText(String.valueOf(reportNo + 1), 430, 190, paint);
            myPdfDocument.finishPage(myPage);
            File file = new File(this.getExternalFilesDir("/"), String.valueOf(reportNo + 1) + hs_name+".pdf");
            try {
                myPdfDocument.writeTo(new FileOutputStream(file));
            } catch (IOException e) {

                Log.e(TAG, e.getMessage());
                Toast.makeText(Report_generation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(Report_generation.this, "Pdf document generateed", Toast.LENGTH_LONG).show();

            myPdfDocument.close();
            ConstraintLayout parentLayout=findViewById(R.id.parentlayout);
            Snackbar.make(parentLayout, "PDF Generated Successfully", Snackbar.LENGTH_LONG)
                    .setAction("Open PDF", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Open the PDF using an Intent and FileProvider
                            Uri pdfUri = FileProvider.getUriForFile(Report_generation.this, getApplicationContext().getPackageName() + ".provider", file);

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(pdfUri, "application/pdf");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(Intent.createChooser(intent, "Open PDF"));
                        }
                    }).show();

        }
    }
    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showInternetAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Internet Not Connected")
                .setMessage("Please connect to the internet to use this app.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the app or take appropriate action
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }



}