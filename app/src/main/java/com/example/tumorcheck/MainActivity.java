package com.example.tumorcheck;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.tumorcheck.ml.Model;
import com.example.tumorcheck.ml.SmodelSoftmax;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERM_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private Button bgchange;
    private Button camera;
    private Button report;
    private Button logout;
    private ImageView brainimage;
    private String output;
    private Bitmap img;
    private Spinner modelSelector;
    private String modelS,Heading;
    private Uri selectedImageUri = null;


    private FirebaseAuth authProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final ConstraintLayout constraintlayout;
//camera=findViewById(R.id.camera);
        Button select = findViewById(R.id.select);
        Button predict = findViewById(R.id.predict);
        brainimage = findViewById(R.id.brainimage);
        constraintlayout = findViewById(R.id.constraintlayout);
        report = findViewById(R.id.report);
        authProfile = FirebaseAuth.getInstance();
        modelSelector = findViewById(R.id.modelSelector);
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        Heading="Brain Tumor";

//logout user


        if (firebaseUser == null) {
            Toast.makeText(MainActivity.this, "Something went wrong user details are not available at the moment", Toast.LENGTH_SHORT).show();

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
                if (img != null && output!=null) {
                    Intent intent = new Intent(MainActivity.this, Report_generation.class);
                    intent.putExtra("imageUri", selectedImageUri.toString());
                    intent.putExtra("tumorResult", output);
                    intent.putExtra("heading",Heading);// Replace with your actual text data

                    startActivity(intent);
                    // Pass the URI to the next activity


                }
//                Intent intent=new Intent(MainActivity.this,Report_generation.class);
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
                            Model model = Model.newInstance(getApplicationContext());

                            // Creates inputs for reference.
                            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
                            //Converting Images to Bitmap and then tensorimage
                            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                            tensorImage.load(img);
                            ByteBuffer byteBuffer = tensorImage.getBuffer();

                            inputFeature0.loadBuffer(byteBuffer);

                            // Runs model inference and gets result.
                            Model.Outputs outputs = model.process(inputFeature0);
                            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                            if (outputFeature0.getFloatArray()[0] == 1.0) {
                                output="Brain Tumor found";
                                Toast.makeText(MainActivity.this, "Brain Tumor Detected", Toast.LENGTH_LONG).show();


                            } else {
                                output="No Tumor Found";
                                Toast.makeText(MainActivity.this, "No Tumor Found ", Toast.LENGTH_LONG).show();

                            }



                            // output.setText(outputFeature0.getFloatArray()[0]+"\n"+outputFeature0.getFloatArray()[0]);
                            // Releases model resources if no longer used.
                            model.close();
                        } catch (IOException e) {
                            // TODO Handle the exception
                        }
                    }else if(modelS.equals("Functional Model")){
                        try {
                            SmodelSoftmax modelSoftmax = SmodelSoftmax.newInstance(getApplicationContext());

                            // Creates inputs for reference.
                            TensorBuffer inputFeature0Softmax = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
                            //Converting Images to Bitmap and then tensorimage

                            TensorImage tensorImageSoftmax = new TensorImage(DataType.FLOAT32);
                            tensorImageSoftmax.load(img);
                            ByteBuffer byteBufferSoftmax = tensorImageSoftmax.getBuffer();
                            inputFeature0Softmax.loadBuffer(byteBufferSoftmax);

                            // Runs model inference and gets result.
                            SmodelSoftmax.Outputs outputs = modelSoftmax.process(inputFeature0Softmax);
                            TensorBuffer outputFeatureSoftmax0 = outputs.getOutputFeature0AsTensorBuffer();
                            if (outputFeatureSoftmax0.getFloatArray()[0] == 1.0) {
                                output="Brain Tumor found";
                                Toast.makeText(MainActivity.this, "Brain Tumor Detected", Toast.LENGTH_LONG).show();


                            } else {
                                output="No Tumor Found";
                                Toast.makeText(MainActivity.this, "No Tumor Found ", Toast.LENGTH_LONG).show();

                            }

                            // Releases model resources if no longer used.
                            modelSoftmax.close();
                        } catch (IOException e) {
                            // TODO Handle the exception
                        }

                    }else if (modelS.equals("Xception Model")) {



                        try {
                            Model model = Model.newInstance(getApplicationContext());

                            // Creates inputs for reference.
                            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
                            //Converting Images to Bitmap and then tensorimage
                            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                            tensorImage.load(img);
                            ByteBuffer byteBuffer = tensorImage.getBuffer();

                            inputFeature0.loadBuffer(byteBuffer);

                            // Runs model inference and gets result.
                            Model.Outputs outputs = model.process(inputFeature0);
                            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                            if (outputFeature0.getFloatArray()[0] == 1.0) {
                                output="Brain Tumor found";
                                Toast.makeText(MainActivity.this, "Brain Tumor Detected", Toast.LENGTH_LONG).show();


                            } else {
                                output="No Tumor Found";
                                Toast.makeText(MainActivity.this, "No Tumor Found ", Toast.LENGTH_LONG).show();

                            }



                            // output.setText(outputFeature0.getFloatArray()[0]+"\n"+outputFeature0.getFloatArray()[0]);
                            // Releases model resources if no longer used.
                            model.close();
                        } catch (IOException e) {
                            // TODO Handle the exception
                        }
                    }else if (modelS.equals("Transfer leaning VGG16")) {



                        try {
                            Model model = Model.newInstance(getApplicationContext());

                            // Creates inputs for reference.
                            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
                            //Converting Images to Bitmap and then tensorimage
                            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                            tensorImage.load(img);
                            ByteBuffer byteBuffer = tensorImage.getBuffer();

                            inputFeature0.loadBuffer(byteBuffer);

                            // Runs model inference and gets result.
                            Model.Outputs outputs = model.process(inputFeature0);
                            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                            if (outputFeature0.getFloatArray()[0] == 1.0) {
                                output="Brain Tumor found";
                                Toast.makeText(MainActivity.this, "Brain Tumor Detected", Toast.LENGTH_LONG).show();


                            } else {
                                output="No Tumor Found";
                                Toast.makeText(MainActivity.this, "No Tumor Found ", Toast.LENGTH_LONG).show();

                            }



                            // output.setText(outputFeature0.getFloatArray()[0]+"\n"+outputFeature0.getFloatArray()[0]);
                            // Releases model resources if no longer used.
                            model.close();
                        } catch (IOException e) {
                            // TODO Handle the exception
                        }
                    }else {

//                        SimpleSequentialModel();
                        try {
                            Model model = Model.newInstance(getApplicationContext());

                            // Creates inputs for reference.
                            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
                            //Converting Images to Bitmap and then tensorimage
                            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                            tensorImage.load(img);
                            ByteBuffer byteBuffer = tensorImage.getBuffer();

                            inputFeature0.loadBuffer(byteBuffer);

                            // Runs model inference and gets result.
                            Model.Outputs outputs = model.process(inputFeature0);
                            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                            if (outputFeature0.getFloatArray()[0] == 1.0) {
                                output="Brain Tumor found";
                                Toast.makeText(MainActivity.this, "Brain Tumor Detected", Toast.LENGTH_LONG).show();


                            } else {
                                output="No Tumor Found";
                                Toast.makeText(MainActivity.this, "No Tumor Found ", Toast.LENGTH_LONG).show();

                            }


                            // output.setText(outputFeature0.getFloatArray()[0]+"\n"+outputFeature0.getFloatArray()[0]);
                            // Releases model resources if no longer used.
                            model.close();
                        } catch (IOException e) {
                            // TODO Handle the exception
                        }

                    }
                    report.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(MainActivity.this, "Please select an image first", Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
//        Toast.makeText(MainActivity.this, "Camera Open Request", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "Camera Permission Required", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            brainimage.setImageURI(data.getData());
            Uri uri = data.getData();
            selectedImageUri=data.getData();


            try {
                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        } else if (requestCode == CAMERA_REQUEST_CODE) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            brainimage.setImageBitmap(image);


//            brainimage.setImageURI(data.getData());
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