package com.example.tumorcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class getAmount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_amount);

        EditText value=findViewById(R.id.donationValue);

        Button donateButton=findViewById(R.id.donateButton);

        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getAmount.this, Donation.class);
                //startActivity(new Intent(MainActivity.this, PaymentActivity.class));
                i.putExtra("price", value.getText().toString());

                startActivityForResult(i, 0);
                //startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == 0 && resultCode == RESULT_OK) {
            // Get String data from Intent
            String ResponseCode = data.getStringExtra("pp_ResponseCode");
            System.out.println("DateFn: ResponseCode:" + ResponseCode);
            if(ResponseCode.equals("000")) {
                Toast.makeText(getApplicationContext(), "Payment Success", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Payment Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    }
