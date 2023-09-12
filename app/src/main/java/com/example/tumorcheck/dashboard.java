package com.example.tumorcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class dashboard extends AppCompatActivity {
    private ImageView profilepic;
    private TextView hos_name,depar_name,mobileno,email_add,hos_type;
    private String hs_name,hs_dep,hs_mobile,hs_email,hs_type;
    private ProgressBar progresssBar;

    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

//        getSupportActionBar().setTitle("Home");

        profilepic=findViewById(R.id.profilepic);
        hos_name=findViewById(R.id.hospitalname);
        depar_name=findViewById(R.id.departmentname);
        email_add=findViewById(R.id.emailReg);
        mobileno=findViewById(R.id.mobileno);
        progresssBar=findViewById(R.id.progresssBar);
        hos_type=findViewById(R.id.hospitalType);

        //Setting on upload image
        profilepic=findViewById(R.id.profilepic);
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(dashboard.this,uploadProfile.class);
                startActivity(intent);
            }
        });

        Button logoutButton=findViewById(R.id.Logoutbutton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authProfile.signOut();
                Toast.makeText(dashboard.this,"Logged Out",Toast.LENGTH_LONG).show();
                Intent intent =new Intent(dashboard.this, main_page.class);

                //clearing stack to prevent user coming bact to mainactiviy on pressing back button after logging out
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });
        if (!isInternetConnected()) {
            showInternetAlertDialog();
        }

        authProfile= FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();

        if(firebaseUser==null){
            Toast.makeText(dashboard.this,"Something went wrong user details are not available at the moment",Toast.LENGTH_SHORT).show();

        }
        else {
            checkifemailverified(firebaseUser);
            progresssBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);

        }




    }

    private void checkifemailverified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            showAlertdialog();
        }
    }

    private void showAlertdialog() {
        //setting up an alert dialog box to let user know that they didn't verified there email
        AlertDialog.Builder builder=new AlertDialog.Builder(dashboard.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now.you cannot login without email verification next time");
        //open Email app from that alert box
        builder.setPositiveButton("Open E-mail", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent= new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK); //to open email in new window
                startActivity(intent);
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID =firebaseUser.getUid();

        DatabaseReference referencProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referencProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                regdataobj readUserDetails= snapshot.getValue(regdataobj.class);
                if(readUserDetails!=null){
                    //getting user details from database
                    hs_name=firebaseUser.getDisplayName();
                    hs_email=firebaseUser.getEmail();
                    hs_dep=readUserDetails.Department;
                    hs_mobile=readUserDetails.PhoneNo;
                    hs_type=readUserDetails.Hospital_type;
                    //displaying user details

                    hos_name.setText(hs_name);
                    depar_name.setText(hs_dep);
                    email_add.setText(hs_email);
                    mobileno.setText(hs_mobile);
                    hos_type.setText(hs_type);

                    //set user dp (after user has uploaded)

                    Uri uri =firebaseUser.getPhotoUrl();

                    Picasso.with(dashboard.this).load(uri).into(profilepic);

                }else{
                    Toast.makeText(dashboard.this,"Something went wrong ",Toast.LENGTH_SHORT).show();

                }

                progresssBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(dashboard.this,"Something went wrong ",Toast.LENGTH_SHORT).show();
                progresssBar.setVisibility(View.GONE);
            }
        });
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