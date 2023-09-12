package com.example.tumorcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {
Button reg_button;
EditText reg_email, department,phoneNo,password,confirmPassword;
Spinner hospitalType,hospitalName;
ProgressBar progressBar;
private static final  String TAG="RegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        reg_button=findViewById(R.id.register_button);
        reg_email=findViewById(R.id.email_of_hostpital);
        department=findViewById(R.id.dep_name);
        phoneNo=findViewById(R.id.hospital_phone);
        password=findViewById(R.id.password);
        confirmPassword=findViewById(R.id.confirm_password);
        Spinner hospitalType=findViewById(R.id.hospital_type);
        Spinner hospitalName=findViewById(R.id.hospital_name);
        progressBar = findViewById(R.id.progressBar);
        //hospital name spinner array adapter
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.hostpitals, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        hospitalName.setAdapter(adapter);

        //hospital type spinner array adapter
        ArrayAdapter<CharSequence> adapterType=ArrayAdapter.createFromResource(this, R.array.hospital_type, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        hospitalType.setAdapter(adapterType);

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String h_name=hospitalName.getSelectedItem().toString();
                String h_type=hospitalType.getSelectedItem().toString();
                String h_email=reg_email.getText().toString();
                String h_dep=department.getText().toString();
                String h_phone=phoneNo.getText().toString();
                String h_pwd=password.getText().toString();
                String h_confirm_pwd=confirmPassword.getText().toString();

                // Validating the mobile using Matcher and pattern (Regular Expression)

                String mobileRegex="[3][0-9]{9}"; //First number must be 3 and rest of 9 can be any numbers
                Matcher mobileMatcher;
                Pattern mobilePattern=Pattern.compile(mobileRegex);
                mobileMatcher=mobilePattern.matcher(h_phone);


                if (TextUtils.isEmpty(h_email)  ){
                    Toast.makeText(Registration.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    reg_email.setError("Please write valid email");
                    reg_email.requestFocus();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(h_email).matches()){
                    Toast.makeText(Registration.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    reg_email.setError("valid email required");
                    reg_email.requestFocus();
                }
                else if (TextUtils.isEmpty(h_dep)  ){
                    Toast.makeText(Registration.this, "Please select one hospital", Toast.LENGTH_SHORT).show();
                    department.setError("Please write department name");
                    department.requestFocus();
                }
                else if (TextUtils.isEmpty(h_phone)  ){
                    Toast.makeText(Registration.this, "Please enter a Mobile No ", Toast.LENGTH_SHORT).show();
                    phoneNo.setError("Enter Mobile Number");
                    phoneNo.requestFocus();
                }
                else if (h_phone.length( )!=10  ){
                    Toast.makeText(Registration.this, "Please re-enter your mobile number", Toast.LENGTH_SHORT).show();
                    phoneNo.setError("Mobile No. should be 10  digits");
                    phoneNo.requestFocus();
                } else if (!mobileMatcher.find()) {
                    Toast.makeText(Registration.this, "Please re-enter your mobile number", Toast.LENGTH_SHORT).show();
                    phoneNo.setError("Mobile No. is not valid");
                    phoneNo.requestFocus();

                } else if (TextUtils.isEmpty(h_pwd)  ){
                    Toast.makeText(Registration.this, "Enter a password", Toast.LENGTH_SHORT).show();
                    password.setError("Enter a password");
                    password.requestFocus();
                }
                else if (h_pwd.length() < 6 ){
                    Toast.makeText(Registration.this, "Password should be atleat 6 digits", Toast.LENGTH_SHORT).show();
                    password.setError("Password is too weak");
                    password.requestFocus();
                }
                else if (TextUtils.isEmpty(h_confirm_pwd)  ){
                    Toast.makeText(Registration.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    confirmPassword.setError("Password confirmation is required");
                    confirmPassword.requestFocus();
                }
                else if (!h_pwd.equals(h_confirm_pwd)  ){
                    Toast.makeText(Registration.this, "Please enter same password", Toast.LENGTH_SHORT).show();
                    confirmPassword.setError("Password confirmation is required");
                    confirmPassword.requestFocus();
                    //clearing the entered passwords
                    password.clearComposingText();
                    confirmPassword.clearComposingText();;
                }
                else {
                    progressBar.setVisibility((View.VISIBLE));
                    registerUser(h_name,h_type,h_email,h_dep,h_phone,h_pwd,h_confirm_pwd);
                }


            }
        });
    }
//REgister user using entered credentials
    private void registerUser(String h_name, String h_type, String h_email, String h_dep, String h_phone, String h_pwd, String h_confirm_pwd) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(h_email,h_pwd).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                     FirebaseUser firebaseUser = auth.getCurrentUser();

                     //Update display name of the user
                    UserProfileChangeRequest profileChangeRequest= new UserProfileChangeRequest.Builder().setDisplayName(h_name).build();
                    firebaseUser.updateProfile(profileChangeRequest);


                     // After completing registration pro cess we will enter data in rtdb firebase
                    regdataobj writeUserDetails = new regdataobj(h_type,h_dep,h_phone);
                    DatabaseReference refrenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
                    refrenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //send Verification Email

                                firebaseUser.sendEmailVerification();
                                Toast.makeText(Registration.this, "User registered successfully,", Toast.LENGTH_LONG).show();
                                Toast.makeText(Registration.this, "please check your email to verify", Toast.LENGTH_LONG).show();



                                //Open User Profile after successfull registeration
                                Intent intent = new Intent(Registration.this, main_dashboard.class);
                                //to prevent user from returning back to Register bAcivity on pressing back button after registration
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish(); // to close the register activiy

                            }
                            else {
                                Toast.makeText(Registration.this, "User, registration failed ", Toast.LENGTH_LONG).show();
                                Toast.makeText(Registration.this, "Contact customer support", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility((View.GONE));
                            }


                        }
                    });

                    



                } else{
                    try{
                        throw task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException e){
                        password.setError("Your Password is too weak. kindly use a mix of alphabets, numbers and characters");
                        password.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        reg_email.setError("Invalid email or already registered. kindly reenter");
                        reg_email.requestFocus();
                    }catch(FirebaseAuthUserCollisionException e){
                        reg_email.setError("User already registered with this email. Use another");
                        reg_email.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Registration.this, e.getMessage(), Toast.LENGTH_LONG).show();


                    }
                    progressBar.setVisibility((View.GONE));


                }
            }
        });


    }
//private void registerUser(String h_name, String h_type, String h_email, String h_dep, String h_phone, String h_pwd, String h_confirm_pwd) {
//
//    // Validate input fields (e.g., email, password, confirm password)
//
//    // Check if password and confirm password match
//    if (!h_pwd.equals(h_confirm_pwd)) {
//        Toast.makeText(Registration.this, "Password and Confirm Password do not match", Toast.LENGTH_LONG).show();
//        return;
//    }
//
//    FirebaseAuth auth = FirebaseAuth.getInstance();
//    auth.createUserWithEmailAndPassword(h_email, h_pwd).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
//
//        @Override
//        public void onComplete(@NonNull Task<AuthResult> task) {
//            if (task.isSuccessful()) {
//                Toast.makeText(Registration.this, "User registered successfully", Toast.LENGTH_LONG).show();
//                FirebaseUser firebaseUser = auth.getCurrentUser();
//
//                // Send verification email
//                sendVerificationEmail(firebaseUser);
//
//                // Redirect to another activity after email verification
//                // Implement your logic here
//
//            } else {
//                Toast.makeText(Registration.this, "User registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }
//    });
//}
//
//    private void sendVerificationEmail(FirebaseUser firebaseUser) {
//        firebaseUser.sendEmailVerification()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(Registration.this, "Verification email sent", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(Registration.this, "Failed to send verification email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
}