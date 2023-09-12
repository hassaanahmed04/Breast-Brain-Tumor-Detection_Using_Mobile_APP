package com.example.tumorcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private EditText LoginEmail, LoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;

    private static  final String TAG="LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//     getSupportActionBar().setTitle("Login");

      LoginEmail=findViewById(R.id.editTextTextEmailAddress);
     LoginPwd=findViewById(R.id.editTextTextPassword);
     progressBar=findViewById(R.id.progressBarLogin);

     //forget password
     TextView forgotpwd=findViewById(R.id.buttonforgots);
     forgotpwd.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             Toast.makeText(Login.this, "You can reset your password now", Toast.LENGTH_SHORT).show();
             startActivity(new Intent(Login.this,ForgotPwd.class));

         }
     });
        TextView registerlink=findViewById(R.id.textView_register_link);
        registerlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Login.this, "Register here", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Login.this,Registration.class));

            }
        });

     authProfile=FirebaseAuth.getInstance();
     //Show hide password using Eye icon
        ImageView imageViewshowhide=  findViewById(R.id.imageView_show_hide);
        imageViewshowhide.setImageResource(R.drawable.ic_hide_pwd);
        imageViewshowhide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    LoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewshowhide.setImageResource((R.drawable.ic_hide_pwd ));
                }else {
                    LoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewshowhide.setImageResource((R.drawable.ic_show_pwd));
                }
            }
        });
     //Login Us
        Button loginButton= findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String h_email=LoginEmail.getText().toString();
                String h_pwd=LoginPwd.getText().toString();

                if(TextUtils.isEmpty(h_email)){
                    Toast.makeText(Login.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    LoginEmail.setError("valid email required");
                    LoginEmail.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(h_email).matches()){
                    Toast.makeText(Login.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    LoginEmail.setError("valid email required");
                    LoginEmail.requestFocus();
                }else if(TextUtils.isEmpty(h_email)) {
                    Toast.makeText(Login.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    LoginPwd.setError("valid password required");
                    LoginPwd.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(h_email,h_pwd);
                }


            }

            private void loginUser(String email, String pwd) {
                authProfile.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(Login.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            // get the details of the user and verifing if the user verified the email or not
                            FirebaseUser firebaseUser=authProfile.getCurrentUser();
                          if(firebaseUser.isEmailVerified()) {
                              Toast.makeText(Login.this, "You are loggedin now", Toast.LENGTH_SHORT).show();

                              //Start the User profile activity
                              startActivity(new Intent(Login.this,main_dashboard.class));
                              finish();

                          }
                          else {
                              firebaseUser.sendEmailVerification();
                              authProfile.signOut();//signing out user
                              showAlertDialogbox();
                          }
                        }
                        else {
                            try {
                                throw task.getException();

                            }catch (FirebaseAuthInvalidUserException e){
                                LoginEmail.setError("User does not exists or is no longer valid. please register again.");
                                LoginEmail.requestFocus();

                            }catch (FirebaseAuthInvalidCredentialsException e){
                                LoginEmail.setError("Invalid credentials. Kindly check and re-enter ");
                                LoginEmail.requestFocus();
                            }catch (Exception e){
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                            };
                            Toast.makeText(Login.this, "Something went wrong!", Toast.LENGTH_SHORT).show();

                        }
                        progressBar.setVisibility(View.GONE);

                    }
                });
            }

            private void showAlertDialogbox() {
                //setting up an alert dialog box to let user know that they didn't verified there email
                AlertDialog.Builder builder=new AlertDialog.Builder(Login.this);
                builder.setTitle("Email not verified");
                builder.setMessage("Please verify your email now.you cannot login without email verification");
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
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser()!=null){
            Toast.makeText(Login.this, "Already loggedi In", Toast.LENGTH_SHORT).show();

            //Start the User profile activity
            Intent intent= new Intent(Login.this,main_dashboard.class);
            startActivity(intent);
            finish();



        }
        else{
            Toast.makeText(Login.this, "You can login now!", Toast.LENGTH_SHORT).show();

        }
    };
}