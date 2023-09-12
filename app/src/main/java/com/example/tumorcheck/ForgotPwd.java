package com.example.tumorcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPwd extends AppCompatActivity {
    private Button ResetButton;
    private EditText  resetEmail;
    private ProgressBar progresBar;
    private FirebaseAuth authProfile;
    private final static String TAG ="ForgotPwd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pwd);

//        getSupportActionBar().setTitle("Forgot Password");

        resetEmail=findViewById(R.id.password_reset_email);
        ResetButton=findViewById(R.id.password_reset);
        progresBar=findViewById(R.id.progressBar);

        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=resetEmail.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPwd.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    resetEmail.setError("valid email required");
                    resetEmail.requestFocus();

                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ForgotPwd.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    resetEmail.setError("valid email required");
                    resetEmail.requestFocus();

                }
                else {
                    progresBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }


        });

    }
    private void resetPassword(String email) {
        authProfile=FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPwd.this, "Please check your inbox for the password reset link", Toast.LENGTH_SHORT).show();
                    Intent intent =new Intent(ForgotPwd.this, main_page.class);

                    //clearing stack to prevent user coming bact to mainactiviy on pressing back button after logging out
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else {
                    try{
                        throw task.getException();

                    }catch(FirebaseAuthInvalidUserException e){
                        resetEmail.setError("User does not exist or no longer valid. Please register again");
                        resetEmail.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(ForgotPwd.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                    Toast.makeText(ForgotPwd.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                }
                progresBar.setVisibility(View.GONE);
            }
        });
    }
}