package com.example.tumorcheck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class main_page extends AppCompatActivity {
Button login,signup;

    private FirebaseAuth authProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        login=findViewById(R.id.login);
        signup=findViewById(R.id.signup);
        authProfile= FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(main_page.this, Login.class);
                startActivity(intent);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(main_page.this, Registration.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser()!=null){
            Toast.makeText(main_page.this, "Already loggedi In", Toast.LENGTH_SHORT).show();

            //Start the User profile activity
            Intent intent= new Intent(main_page.this,main_dashboard.class);
            startActivity(intent);
            finish();



        }
        else{
            Toast.makeText(main_page.this, "You can login now!", Toast.LENGTH_SHORT).show();

        }
    };
}