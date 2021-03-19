package com.example.facerecognitionbasedentryapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import id.zelory.compressor.Compressor;

public class ClickImage extends AppCompatActivity {
    ImageButton l1,l2,l3,r1,r2,r3,f1,f2,f3;

    //for imagespage
    boolean isFirstPic = true;
    private static int IMAGE_CAPTURE_RC = 123;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private ArrayList<File> mImages;
    private String imageFilePath;
    private ArrayList<String> mImagesPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.click_image);

        l1= findViewById(R.id.imageButtonl1);
        l2= findViewById(R.id.imageButtonl2);
        l3= findViewById(R.id.imageButtonl3);
        r1= findViewById(R.id.imageButtonr1);
        r2= findViewById(R.id.imageButtonr2);
        r3= findViewById(R.id.imageButtonr3);
        f1= findViewById(R.id.imageButtonf1);
        f2= findViewById(R.id.imageButtonf2);
        f3= findViewById(R.id.imageButtonf3);

        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickImage();
                //l1.setImageResource(R.drawable.);

            }

        });


    }

    public void clickImage(){
        openCameraIntent();


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
                    Log.d("else", "openCameraIntent: IOEXCEPTION PHOTOFILE: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }
                Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                pictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(pictureIntent, IMAGE_CAPTURE_RC);
            }
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CAPTURE_RC) {
            if (resultCode == RESULT_OK) {
                File file = new File(imageFilePath);
                try {
                    mImagesPath.add(imageFilePath);
                    File compressedFile = new Compressor(getApplicationContext()).compressToFile(file);
                    mImages.add(compressedFile);
                    //adapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Unable to load Image, please try again!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
