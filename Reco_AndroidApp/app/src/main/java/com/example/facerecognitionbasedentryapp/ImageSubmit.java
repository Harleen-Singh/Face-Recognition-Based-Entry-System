package com.example.facerecognitionbasedentryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;

public class ImageSubmit  extends AppCompatActivity {

    //for imagespage
    boolean isFirstPic = true;
    private static int IMAGE_CAPTURE_RC = 123;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private String imageFilePath,token;
    private ArrayList<File> mImages;
    private ArrayList<String> mImagesPath;
    RecyclerView recyclerView;
    ReportImageRecyAdapter adapter;
    ImageButton clickPhoto;
    Button openCamera;
    Button save;
    private int photosUploadedCount = 0;
    int i=1;
    String Id;

    AlertDialog reportSubmitLoading;
    String imageUploadUrl = "https://frozen-wildwood-57358.herokuapp.com/api/face_recognition/addImage/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_submit);



        Intent intent= getIntent();
        Id = intent.getStringExtra("userid");

        SharedPreferences preferences =getSharedPreferences("tokenFile", Context.MODE_PRIVATE);
        token = preferences.getString("key", "");




        recyclerView = findViewById(R.id.rvimages);
        mImagesPath = new ArrayList<>();
        mImages = new ArrayList<>();
        adapter = new ReportImageRecyAdapter(this, mImagesPath);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);





        openCamera=findViewById(R.id.click_images);
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///cehckdkk
                openCameraIntent();
                //Toast.makeText(getApplicationContext(),"open camera ",Toast.LENGTH_LONG).show();
            }

        });


        save = findViewById(R.id.submit_images);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //imagesPage.setVisibility(View.GONE);
                //reportPage.setVisibility(View.VISIBLE);
                //title_top.setText("Report");
                if(mImagesPath.size()==0)
                    Toast.makeText(getApplicationContext(),"You have not uploaded any images",Toast.LENGTH_LONG).show();
                else if(mImagesPath.size()<9)
                        Toast.makeText(getApplicationContext(),"Please upload more images",Toast.LENGTH_LONG).show();
                else
                    postPhotos();
                    //uploadingPhotos();



            }
        });


    }


    private void openCameraIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("camera","entered if");
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Log.d("camera","entered if if");
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            } else {
                Log.d("camera","entered if else");
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {

                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        Log.d("camera","entered try");
                        //number_of_images++;
                    } catch (IOException e) {
                        Log.d("camera","entered catch");
                        Log.d("exception", "openCameraIntent: IOEXCEPTION PHOTOFILE: " + e.getMessage());
                        e.printStackTrace();
                        return;
                    }
                    Log.d("camera","check1");
                    Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", photoFile);//provider added in manifest file
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Log.d("camera","check2");
                }
                Log.d("camera","check3");
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }
        else {//lower spi
            Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (pictureIntent.resolveActivity(getPackageManager()) != null) {

                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    Log.d("camera_check", "openCameraIntent: IOEXCEPTION PHOTOFILE: " + e.getMessage());
                    Toast.makeText(getApplicationContext(),"Exception occured while creating ImageFile",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    return;
                }
                Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                pictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(pictureIntent, IMAGE_CAPTURE_RC);
                Log.d("camera_check","check2");
            }
        }
    }

    private File createImageFile() throws IOException {
        //Toast.makeText(getApplicationContext(),"check1",Toast.LENGTH_LONG).show();
        Log.d("camera_check","check1");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();
        Log.d("camera_check","check1 "+image);
        //Toast.makeText(getApplicationContext(),"Returned value "+image,Toast.LENGTH_LONG).show();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("camera_check","check3 enter");
        //Toast.makeText(getApplicationContext(),"in onActivity Result with request code "+requestCode,Toast.LENGTH_LONG).show();
        ////if (requestCode == IMAGE_CAPTURE_RC) {
            if (requestCode == 1888) {
            Log.d("camera_check","check3 if");
            //Toast.makeText(getApplicationContext(),"Entered if with result code "+resultCode,Toast.LENGTH_LONG).show();
            if (resultCode == RESULT_OK) {
                //Toast.makeText(getApplicationContext(),"Entered if if ",Toast.LENGTH_LONG).show();
                Log.d("camera_check","check3 if if ");
                File file = new File(imageFilePath);
                try {
                    mImagesPath.add(imageFilePath);
                    File compressedFile = new Compressor(getApplicationContext()).compressToFile(file);
                    mImages.add(compressedFile);
                    adapter.notifyDataSetChanged();
                    Log.d("camera_check","check3 try");
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Unable to load Image, please try again!",
                            Toast.LENGTH_SHORT).show();
                    Log.d("camera_check","check3 catch");

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadingPhotos(){
        // Toast.makeText(getApplicationContext(),"Eneterd uploading photos function",Toast.LENGTH_LONG).show();
        reportSubmitLoading = new SpotsDialog.Builder().setContext(ImageSubmit.this).setMessage("Uploading Images")
                .setTheme(R.style.CustomDialog)
                .setCancelable(false)
                .build();
        reportSubmitLoading.show();
        /*

        AndroidNetworking.upload(imageUploadUrl)
                .addHeaders("Authorization", "Token " + token)
                //.addHeaders("Authorization", "detail" + token)
                .addMultipartParameter("userId",Id)
                .addMultipartFile("image", mImages.get(PhotosUploadedCount))
                .setTag("Upload Images")
                .setPriority(Priority.HIGH)
                .build()






                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        //if (bytesUploaded == totalBytes) {
                        //PhotosUploadedCount++;
                        //}
                        // Toast.makeText(getApplicationContext(),"uploading image "+PhotosUploadedCount+"bytesUploaded are "+String.valueOf(bytesUploaded)+"total bytes are "+String.valueOf(totalBytes),Toast.LENGTH_LONG).show();
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                                     @Override
                                     public void onResponse(JSONObject response) {
                                         Log.d("camera", "onResponse: " + response);
                                         PhotosUploadedCount++;
                                         Toast.makeText(getApplicationContext(),"response is "+response.toString(),Toast.LENGTH_LONG).show();
                                         if(PhotosUploadedCount==mImages.size()) {
                                             reportSubmitLoading.dismiss();
                                             //submit_btn.setText("Submitted");
                                         }
                                     }

                                     @Override
                                     public void onError(ANError anError) {
                                         Log.d("camera", "onError: " + anError.getErrorBody());
                                         Toast.makeText(getApplicationContext(), "Photos Upload failed, please try again ", Toast.LENGTH_SHORT).show();
                                         reportSubmitLoading.dismiss();
                                     }

                                 }
                );

                 */

    }

    public void postPhotos(){

        reportSubmitLoading = new SpotsDialog.Builder().setContext(ImageSubmit.this).setMessage("Uploading Images")
                .setTheme(R.style.CustomDialog)
                .setCancelable(false)
                .build();
        reportSubmitLoading.show();

        ANRequest request = AndroidNetworking.upload(imageUploadUrl)
                .addHeaders("Authorization", "Token " + token)
                .addMultipartParameter("userId",Id)
                .addMultipartFile("image", mImages.get(photosUploadedCount))
                .setTag("Upload Images")
                .setPriority(Priority.HIGH)
                .build();

        request.getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                //Toast.makeText(getApplicationContext(),"response for image "+i + " is "+response.toString(),Toast.LENGTH_LONG).show();
                Log.d("upload here", "onResponse: " + response);
                Log.d("Location ID", "onResponse ID: " + mImages.size());
                Log.d("response check","uploaded " + photosUploadedCount );
                photosUploadedCount++;
                i++;
                if (photosUploadedCount == mImages.size()) {
                    //afterUploading();
                    reportSubmitLoading.dismiss();
                    Toast.makeText(ImageSubmit.this, "Images Uploaded successfully.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ImageSubmit.this,MainActivity.class);
                    startActivity(i);

                }
                else {
                    Toast.makeText(getApplicationContext(),"Uploading Image " + (i),Toast.LENGTH_LONG).show();
                    postPhotos();
                    //uploadingPhotos();
                }
            }

            @Override
            public void onError(ANError anError) {
                Log.d("upload", "onError: " + anError.getErrorBody());
                Toast.makeText(getApplicationContext(), "Photos Upload failed, please try again ", Toast.LENGTH_SHORT).show();
                reportSubmitLoading.dismiss();
            }

        });

    }


}
