package com.example.tumorcheck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class uploadProfile extends AppCompatActivity {
    private ProgressBar progressBar;
    private ImageView uploadpic;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static  final int PICK_IMAGE_REQUEST=1;
    private Uri uriImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile);


        Button uploadchooseButton= findViewById(R.id.upload_pic_choose_button);
        Button uploadpicButton= findViewById(R.id.upload_pic_button);
        progressBar=findViewById(R.id.progressBar);
        uploadpic=findViewById(R.id.imageView_profile_dp);
        authProfile=FirebaseAuth.getInstance();

        firebaseUser=authProfile.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference("DisplayPics");



        Uri uri = firebaseUser.getPhotoUrl();

        //Set users current DP in ImageView
        Picasso.with(uploadProfile.this).load(uri).into(uploadpic);
        uploadchooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChoser();
            }
        });
        uploadpicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                UploadPic();

            }
        });
    }

    private void UploadPic() {
        if(uriImage != null){
            StorageReference fileReference=storageReference.child(authProfile.getCurrentUser().getUid()+"."+getFileExtension(uriImage));
        //upload image to storage

            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            firebaseUser =authProfile.getCurrentUser();
                            UserProfileChangeRequest profileUpdates=new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileUpdates);
                        }
                    });
                    progressBar.setVisibility(View.GONE);
                Toast.makeText(uploadProfile.this, "profile picture updated", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(uploadProfile.this,dashboard.class);
                startActivity(intent);
                finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(uploadProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(uploadProfile.this, "no file seleted", Toast.LENGTH_SHORT).show();

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChoser() {
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    if(requestCode==PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
        uriImage=data.getData();
        uploadpic.setImageURI(uriImage);
    }
    }
}