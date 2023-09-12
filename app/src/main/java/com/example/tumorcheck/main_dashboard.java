package com.example.tumorcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class main_dashboard extends AppCompatActivity {
    ImageView menuOpen, profilepic;
    LinearLayout drawer;
    private FirebaseAuth authProfile;
    ConstraintLayout mainConstraint;
    MaterialCardView brainPost, BreastPost, ReportsPost;
    //all buttons in the linear layout
    LinearLayout BreasCancer, BrainTumor, manageReport, viewProfile, updateProfile, settings, logout, donation, shareUs, contactUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);
        menuOpen = findViewById(R.id.menuOpen);
        drawer = findViewById(R.id.mainDrawer);
        BrainTumor = findViewById(R.id.menuBrainTumor);
        BreasCancer = findViewById(R.id.menuBreastCancer);
        manageReport = findViewById(R.id.menuManageReports);
        viewProfile = findViewById(R.id.menuViewProfile);
        updateProfile = findViewById(R.id.menuUpdateProfile);
        settings = findViewById(R.id.menuSettings);
        logout = findViewById(R.id.menuLogout);
        donation = findViewById(R.id.menuDonation);
        shareUs = findViewById(R.id.menuShare);
        contactUs = findViewById(R.id.menuContactUs);
        mainConstraint = findViewById(R.id.mainContraint);
        BreastPost = findViewById(R.id.breast_cancer_poster);
        ReportsPost = findViewById(R.id.manage_reports_post);
        brainPost = findViewById(R.id.brain_tumor_post);
        profilepic = findViewById(R.id.ic_profile);
        if (!isInternetConnected()) {
            showInternetAlertDialog();
        }
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(main_dashboard.this, "Something went wrong user details are not available at the moment", Toast.LENGTH_SHORT).show();

        } else {

            showUserProfile(firebaseUser);

        }
//main dashboard with slides

        brainPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(main_dashboard.this, MainActivity.class));
            }
        });
        BreastPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(main_dashboard.this, BreastMainActivity.class));
            }
        });
        ReportsPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(main_dashboard.this, manageReports.class));
            }
        });
//menu inside drawer
        BrainTumor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(main_dashboard.this, MainActivity.class));
            }
        });
        BreasCancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(main_dashboard.this, BreastMainActivity.class));
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(main_dashboard.this, settings.class));
            }
        });
        menuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.setVisibility(View.VISIBLE);
                menuOpen.setVisibility(View.GONE);
            }
        });
        mainConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.setVisibility(View.GONE);
                menuOpen.setVisibility(View.VISIBLE);

            }
        });
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(main_dashboard.this, dashboard.class));

            }
        });
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(main_dashboard.this, updateProfile.class));

            }
        });
        manageReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(main_dashboard.this, manageReports.class));

            }
        });
        donation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(main_dashboard.this, getAmount.class));

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setting up an alert dialog box to let user know that they didn't verified there email
                AlertDialog.Builder builder = new AlertDialog.Builder(main_dashboard.this);
                AlertDialog alert = builder.create();

                builder.setTitle("Logout");
                builder.setMessage("Are you sure, you want to Logout");
                //open Email app from that alert box
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        authProfile.signOut();
                        Toast.makeText(main_dashboard.this, "Logged Out", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(main_dashboard.this, main_page.class);

                        //clearing stack to prevent user coming bact to mainactiviy on pressing back button after logging out
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alert.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://www.softwand.net";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
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
    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        DatabaseReference referencProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referencProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                regdataobj readUserDetails = snapshot.getValue(regdataobj.class);
                if (readUserDetails != null) {

                    //set user dp (after user has uploaded)

                    Uri uri = firebaseUser.getPhotoUrl();

                    Picasso.with(main_dashboard.this).load(uri).into(profilepic);

                } else {
                    Toast.makeText(main_dashboard.this, "Something went wrong ", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(main_dashboard.this, "Something went wrong ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}