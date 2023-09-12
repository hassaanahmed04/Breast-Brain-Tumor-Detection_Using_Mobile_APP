package com.example.tumorcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class settings extends AppCompatActivity {
Button update,delete,reset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        update=findViewById(R.id.buttonupdate);
        delete=findViewById(R.id.buttondelete);
        reset=findViewById(R.id.buttonreset);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(settings.this,uploadProfile.class));
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(settings.this,deleteProfile.class));
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(settings.this,ForgotPwd.class));
            }
        });
    }
}