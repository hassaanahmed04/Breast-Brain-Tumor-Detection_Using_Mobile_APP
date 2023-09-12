package com.example.tumorcheck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tumorcheck.ml.Model;
import com.example.tumorcheck.ml.Sequentialbreast;
import com.example.tumorcheck.ml.SmodelSoftmax;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class BreastMainActivity extends AppCompatActivity {

    private static final int CAMERA_PERM_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private Button bgchange;
    private Button camera;
    private Button report;
    private Button logout;
    private ImageView breastImage;
    private String output;
    private Bitmap img;
    private Spinner modelSelector;
    private String modelS,Heading;

    private Uri selectedImageUri = null;
    private FirebaseAuth authProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_breast_main);

        final ConstraintLayout constraintlayout;
//camera=findViewById(R.id.camera);
        Button select = findViewById(R.id.select);
        Button predict = findViewById(R.id.predict);
        breastImage = findViewById(R.id.breastImage);
        constraintlayout = findViewById(R.id.constraintlayout);
        report = findViewById(R.id.report);
        authProfile = FirebaseAuth.getInstance();
        modelSelector = findViewById(R.id.modelSelector);
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
Heading="Breast Cancer";

//logout user
        // Load the TensorFlow Lite model from assets
//        try {
//            tflite = new Interpreter(loadModelFile());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        if (firebaseUser == null) {
            Toast.makeText(BreastMainActivity.this, "Something went wrong user details are not available at the moment", Toast.LENGTH_SHORT).show();

        } else {
            checkifemailverified(firebaseUser);


        }


        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 100);

            }
        });
//        camera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                askCameraPermissions();
//            }
//        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (img != null) {
                    Intent intent = new Intent(BreastMainActivity.this, Report_generation.class);
                    intent.putExtra("imageUri", selectedImageUri.toString());
                    intent.putExtra("tumorResult", output);
                    intent.putExtra("heading", Heading);
                    startActivity(intent);
                }// Replace with your actual text data                }
                else{
                    Toast.makeText(BreastMainActivity.this, "Image not found", Toast.LENGTH_LONG).show();


                }
//                Intent intent=new Intent(BreastMainActivity.this,Report_generation.class);
////
//                    startActivity(intent);
            }
        });

        modelSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                modelS = modelSelector.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //passing and predicting the image got from the files using select button
        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (img != null) {
                    img = Bitmap.createScaledBitmap(img, 64, 64, true);

                    if (modelS.equals("Simple Sequential")) {
                        try {
                            Sequentialbreast model = Sequentialbreast.newInstance(getApplicationContext());

                            // Creates inputs for reference.
                            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
                            //Converting Images to Bitmap and then tensorimage

                            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                            tensorImage.load(img);
                            ByteBuffer byteBuffer = tensorImage.getBuffer();
                            inputFeature0.loadBuffer(byteBuffer);
                            // Runs model inference and gets result.
                            Sequentialbreast.Outputs outputs = model.process(inputFeature0);
                            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();


                            if (outputFeature0.getFloatArray()[0] == 1.0) {
                                output="Breast Cancer found";
                                Toast.makeText(BreastMainActivity.this, "Breast Cancer Detected", Toast.LENGTH_LONG).show();
                            } else {
                                output="No Cancer Found";
                                Toast.makeText(BreastMainActivity.this, "No Breast Cancer Found ", Toast.LENGTH_LONG).show();

                            }
                            model.close();
                        } catch (IOException e) {
                            // TODO Handle the exception
                        }




                    }else if(modelS.equals("Functional Model")){
                        try {
                            Sequentialbreast model = Sequentialbreast.newInstance(getApplicationContext());

                            // Creates inputs for reference.
                            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
                            //Converting Images to Bitmap and then tensorimage

                            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                            tensorImage.load(img);
                            ByteBuffer byteBuffer = tensorImage.getBuffer();
                            inputFeature0.loadBuffer(byteBuffer);
                            // Runs model inference and gets result.
                            Sequentialbreast.Outputs outputs = model.process(inputFeature0);
                            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();


                            if (outputFeature0.getFloatArray()[0] == 1.0) {
                                output="Breast Cancer found";
                                Toast.makeText(BreastMainActivity.this, "Breast Cancer Detected", Toast.LENGTH_LONG).show();
                            } else {
                                output="No Cancer Found";
                                Toast.makeText(BreastMainActivity.this, "No Breast Cancer Found ", Toast.LENGTH_LONG).show();

                            }
                            model.close();
                        } catch (IOException e) {
                            // TODO Handle the exception
                        }

                    }else if(modelS.equals("Xception Model")){
                        try {
                            Sequentialbreast model = Sequentialbreast.newInstance(getApplicationContext());

                            // Creates inputs for reference.
                            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
                            //Converting Images to Bitmap and then tensorimage

                            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                            tensorImage.load(img);
                            ByteBuffer byteBuffer = tensorImage.getBuffer();
                            inputFeature0.loadBuffer(byteBuffer);
                            // Runs model inference and gets result.
                            Sequentialbreast.Outputs outputs = model.process(inputFeature0);
                            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();


                            if (outputFeature0.getFloatArray()[0] == 1.0) {
                                output="Breast Cancer found";
                                Toast.makeText(BreastMainActivity.this, "Breast Cancer Detected", Toast.LENGTH_LONG).show();
                            } else {
                                output="No Cancer Found";
                                Toast.makeText(BreastMainActivity.this, "No Breast Cancer Found ", Toast.LENGTH_LONG).show();

                            }
                            model.close();
                        } catch (IOException e) {
                            // TODO Handle the exception
                        }

                    }else if(modelS.equals("Transfer leaning VGG16")){
                        try {
                            Sequentialbreast model = Sequentialbreast.newInstance(getApplicationContext());

                            // Creates inputs for reference.
                            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
                            //Converting Images to Bitmap and then tensorimage

                            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                            tensorImage.load(img);
                            ByteBuffer byteBuffer = tensorImage.getBuffer();
                            inputFeature0.loadBuffer(byteBuffer);
                            // Runs model inference and gets result.
                            Sequentialbreast.Outputs outputs = model.process(inputFeature0);
                            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();


                            if (outputFeature0.getFloatArray()[0] == 1.0) {
                                output="Breast Cancer found";
                                Toast.makeText(BreastMainActivity.this, "Breast Cancer Detected", Toast.LENGTH_LONG).show();
                            } else {
                                output="No Cancer Found";
                                Toast.makeText(BreastMainActivity.this, "No Breast Cancer Found ", Toast.LENGTH_LONG).show();

                            }
                            model.close();
                        } catch (IOException e) {
                            // TODO Handle the exception
                        }

                    }else {

//                        SimpleSequentialModel();
                        try {
                            Sequentialbreast model = Sequentialbreast.newInstance(getApplicationContext());

                            // Creates inputs for reference.
                            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
                            //Converting Images to Bitmap and then tensorimage

                            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                            tensorImage.load(img);
                            ByteBuffer byteBuffer = tensorImage.getBuffer();
                            inputFeature0.loadBuffer(byteBuffer);

                            // Runs model inference and gets result.
                            Sequentialbreast.Outputs outputs = model.process(inputFeature0);
                            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();


                            if (outputFeature0.getFloatArray()[0] == 1.0) {
                                output="Breast Cancer found";
                                Toast.makeText(BreastMainActivity.this, "Breast Cancer Detected", Toast.LENGTH_LONG).show();
                            } else {
                                output="No Tumor Found";
                                Toast.makeText(BreastMainActivity.this, "No Breast Cancer Found ", Toast.LENGTH_LONG).show();

                            }

                            model.close();
                        } catch (IOException e) {
                            // TODO Handle the exception
                        }


                    }
                    report.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(BreastMainActivity.this, "Please select an image first", Toast.LENGTH_SHORT).show();
                }
            }


        });


    }


    private void checkifemailverified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()) {
            showAlertdialog();
        }
    }

    private void showAlertdialog() {
        //setting up an alert dialog box to let user know that they didn't verified there email
        AlertDialog.Builder builder = new AlertDialog.Builder(BreastMainActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now.you cannot login without email verification next time");
        //open Email app from that alert box
        builder.setPositiveButton("Open E-mail", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK); //to open email in new window
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


//    private void askCameraPermissions() {
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_PERM_CODE);
//        }
//        else{
//            openCamera();
//        }
//    }
//
//    private void openCamera() {
//        Toast.makeText(BreastMainActivity.this, "Camera Open Request", Toast.LENGTH_SHORT).show();
//Intent camera= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//startActivityForResult(camera, CAMERA_REQUEST_CODE);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //opencamera
//                openCamera();
            } else {
                Toast.makeText(BreastMainActivity.this, "Camera Permission Required", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            breastImage.setImageURI(data.getData());
            Uri uri = data.getData();
            selectedImageUri=data.getData();

            try {
                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        } else if (requestCode == CAMERA_REQUEST_CODE) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            breastImage.setImageBitmap(image);


//            breastImage.setImageURI(data.getData());
//            Uri uri=data.getData();
//
//            try {
//                img= MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }


        }


    }
}
//
////    creating a menu
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
////inflating menu items
//        getMenuInflater().inflate(R.menu.menu_,menu);
//        return super.onCreateOptionsMenu(menu);
//    }}

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id =item.getItemId();
//        if(id==R.id.menurefresh){
//            startActivity(getIntent());
//            finish();
////            overridePendingTransition(0,0);
//
//       }
////        else if (id==R.id.menu_update_profile) {
////            Intent intent=new Intent(BreastMainActivity.this,UpdateProfile.class);
////            startActivity(intent);
////        }
////        else if (id==R.id.menu_update_email) {
////            Intent intent=new Intent(BreastMainActivity.this,Updateemail.class);
////            startActivity(intent);
////        }
////        else if (id==R.id.menu_update_profile) {
////            Intent intent=new Intent(BreastMainActivity.this,UpdateProfile.class);
////            startActivity(intent);
////        }
////        else if (id==R.id.menu_settings) {
////            Intent intent=new Intent(BreastMainActivity.this,UpdateProfile.class);
////            startActivity(intent);
////        }
////        else if (id==R.id.menu_change_password) {
////            Intent intent=new Intent(BreastMainActivity.this,ChangePassword.class);
////            startActivity(intent);
////        }
////        else if (id==R.id.menu_delete_profile) {
////            Intent intent=new Intent(BreastMainActivity.this,DeleteProfile.class);
////            startActivity(intent);
////        }
//        else if (id==R.id.logout) {
//            authProfile.signOut();
//            Toast.makeText(BreastMainActivity.this,"Logged Out",Toast.LENGTH_LONG).show();
//            Intent intent =new Intent(BreastMainActivity.this, main_page.class);
//
//            //clearing stack to prevent user coming bact to mainactiviy on pressing back button after logging out
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
//        }else {
//            Toast.makeText(BreastMainActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//}