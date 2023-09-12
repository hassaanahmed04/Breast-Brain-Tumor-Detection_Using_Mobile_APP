package com.example.tumorcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ir.androidexception.datatable.DataTable;
import ir.androidexception.datatable.model.DataTableHeader;
import ir.androidexception.datatable.model.DataTableRow;

public class manageReports extends AppCompatActivity {
    private FirebaseAuth authProfile;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference myRef = database.getReference("users").child(currentUserUid).child("record");
    DatabaseReference retriveRef;


    Button printReport;
    EditText reportID;
    DataTable dataTable;
    DataTableHeader header;

    SimpleDateFormat datePatternFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
    ArrayList<DataTableRow> rows = new ArrayList<>();
    Bitmap bmp, scaledbmp;
    private static final String TAG = "Manage Reports";
    private FirebaseUser firebaseUser;

    long reportNo;
    String patientName;
    long databaseDate;
    long cnic;
    long age;
    long phoneNo;
    String address;
    String gender;
    String tumorResult;
    String Heading;
    String result;
    String characteristics;
Bitmap logobmp,profilebmp;
    Bitmap profilepic;
    private String hs_name,hs_dep,hs_mobile,hs_email,hs_type;

    Uri uploadedImageUri, imageUrii;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_reports);

        printReport = findViewById(R.id.previousReportButton);
        reportID = findViewById(R.id.enterReportId);
        dataTable=findViewById(R.id.data_table);
        authProfile=FirebaseAuth.getInstance();

        firebaseUser=authProfile.getCurrentUser();
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.notfount);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 150, 150, false);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logobg);
        logobmp = Bitmap.createScaledBitmap(bmp, 550 , 550  , false);

//making header of the manage report to display all the reports
        header = new DataTableHeader.Builder()
                .item("Report No.", 5)
                .item("Patient Name", 5)
                .item("Cnic", 5)
                .item("Gender", 5)
                .item("Date", 5)
                .build();
        loadTable();
        listeners();
        showUserProfile(firebaseUser);

    }

    private void listeners() {
        printReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                               printPdf(reportID.getText().toString());
            }
        });
    }
    private String getFileExtensionFromReportId(String reportId) {
        int lastDotIndex = reportId.lastIndexOf(".");
        if (lastDotIndex != -1 && lastDotIndex < reportId.length() - 1) {
            return reportId.substring(lastDotIndex + 1);
        }
        return ""; // Default empty extension
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
                    Toast.makeText(manageReports.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(manageReports.this, "Something went wrong really", Toast.LENGTH_SHORT).show();
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
//                            showInternetAlertDialog();
                            Toast.makeText(manageReports.this, "Image bitmap is null", Toast.LENGTH_SHORT).show();
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
    private void printPdf(String ID) {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        retriveRef = database.getReference("users").child(currentUserUid).child("record").child(ID);

        retriveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportNo=(long) snapshot.child("reportNo").getValue();
                cnic=(long) snapshot.child("cnic").getValue();
                age=(long) snapshot.child("age").getValue();
                phoneNo=(long) snapshot.child("phoneNo").getValue();
                databaseDate=(long) snapshot.child("date").getValue();
                address=(String) snapshot.child("address").getValue();
                gender=(String) snapshot.child("gender").getValue();
                patientName=(String) snapshot.child("patientName").getValue();
                tumorResult=(String) snapshot.child("tumorResult").getValue();
                if(address==null){
                    address="N/A";
                }

                if(tumorResult.equals("Brain Tumor found")){
                    Heading="Brain Tumor";
                    result="the presence of a brain tumor the ";
                    characteristics="Specific characteristics of the tumor";

                } else if (tumorResult.equals("No Tumor Found")) {

                    Heading="Brain Tumor";
                    result="that there is no brain tumor found the ";
                    characteristics="Specific characteristics of the brain image ";

                }else if (tumorResult.equals("Breast Cancer found")) {
                    Heading="Breast Cancer";
                    result="the presence of a Breast Cancer ";
                    characteristics="Specific characteristics of the Breast Cancer ";

                }else if (tumorResult.equals("No Cancer Found")) {
                    Heading="Breast Cancer";
                    result="that there is no Breast Cancer is found ";
                    characteristics="Specific characteristics of the breast image ";

                }
//
                PdfDocument myPdfDocument = new PdfDocument();
                Paint paint = new Paint();
                Paint forLinePaint = new Paint();
                Paint fortitlePaint = new Paint();
                Paint paintConst = new Paint();
                PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
                Canvas canvas = myPage.getCanvas();
                canvas.drawBitmap(logobmp, 45, 130, paint);
                canvas.drawBitmap(profilebmp, 20, 20, paint);
                paintConst.setTextSize(12.0f);

                canvas.drawText("Hospital Name : " + hs_name, 592 - 240, 40, paintConst);
                canvas.drawText("Department : " + hs_dep, 592 - 240, 60, paintConst);
                canvas.drawText("Email : " + hs_email, 592 - 240, 80, paintConst);
                canvas.drawText("Phone# : " + hs_mobile, 592 - 240, 100, paintConst);
                canvas.drawText("Date : " + String.valueOf(databaseDate), 592 - 240, 120, paintConst);
//
                paintConst.setTextSize(15.5f);
                paintConst.setColor(Color.rgb(0, 0, 0));
                paintConst.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                paintConst.setTextAlign(Paint.Align.LEFT);
                canvas.drawText("Patient Name    : ", 20, 170, paintConst);
                canvas.drawText("Patient Cnic    : ", 20, 190, paintConst);
                canvas.drawText("Patient Address : ", 20, 210, paintConst);
//
//                //header box
                paint.setTextSize(15.5f);
                paint.setColor(Color.rgb(0, 0, 0));

                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(String.valueOf(patientName), 150, 170, paint);
                canvas.drawText(String.valueOf(cnic), 150, 190, paint);
                canvas.drawText(String.valueOf(address), 150, 210, paint);
                canvas.drawText("Report No:", 350, 190, paint);
//                //details
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
                canvas.drawText(String.valueOf(patientName), 120, 300, paintConst);
                canvas.drawText(String.valueOf(age), 120, 323, paintConst);
                canvas.drawText(String.valueOf(phoneNo), 120, 346, paintConst);
                canvas.drawText(String.valueOf(databaseDate), 120, 369, paintConst);
               canvas.drawText(String.valueOf(tumorResult), 120, 390, paintConst);
//picture of the tumor
                canvas.drawBitmap(scaledbmp, 400, 290, paint);
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
                canvas.drawText(String.valueOf(reportNo ), 430, 190, paint);
                myPdfDocument.finishPage(myPage);
                File file = new File(manageReports.this.getExternalFilesDir("/"), reportNo+"_"+patientName + ".pdf");
                try {
                    myPdfDocument.writeTo(new FileOutputStream(file));
                } catch (IOException e) {

                    Log.e(TAG, e.getMessage());
                    Toast.makeText(manageReports.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(manageReports.this, "Pdf document generateed", Toast.LENGTH_LONG).show();

                myPdfDocument.close();
                RelativeLayout parentLayout=findViewById(R.id.parentlayout);
                Snackbar.make(parentLayout, "PDF Generated Successfully", Snackbar.LENGTH_LONG)

                        .setAction("Open PDF", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Open the PDF using an Intent and FileProvider
                                Uri pdfUri = FileProvider.getUriForFile(manageReports.this, getApplicationContext().getPackageName() + ".provider", file);

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(pdfUri, "application/pdf");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(Intent.createChooser(intent, "Open PDF"));
                            }
                        }).show();
//
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadTable() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot myDataSnapshot : snapshot.getChildren()) {
                    DataTableRow row = new DataTableRow.Builder()
                            .value(String.valueOf(myDataSnapshot.child("reportNo").getValue()))
                            .value(String.valueOf(myDataSnapshot.child("patientName").getValue()))
                            .value(String.valueOf(myDataSnapshot.child("cnic").getValue()))
                            .value(String.valueOf(myDataSnapshot.child("gender").getValue()))
                            .value(datePatternFormat.format(myDataSnapshot.child("date").getValue()))
                            .build();
                    rows.add(row);

                }
                dataTable.setHeader(header);
                dataTable.setRows(rows);
                dataTable.inflate(manageReports.this);
                if(rows.isEmpty()){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}