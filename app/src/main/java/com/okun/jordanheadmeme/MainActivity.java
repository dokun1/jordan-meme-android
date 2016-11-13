package com.okun.jordanheadmeme;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.net.Uri;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

    static final int REQUEST_BACK_CAMERA_CAPTURE = 1;
    static final int REQUEST_FRONT_CAMERA_CAPTURE = 2;
    static final int REQUEST_GALLERY_PHOTO_CAPTURE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageManager manager = getPackageManager();
        Button selfieButton = (Button) findViewById(R.id.button_take_selfie);

        selfieButton.setEnabled(isFrontCameraAvailable());

        findViewById(R.id.button_choose_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (getPhoto.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(getPhoto, REQUEST_GALLERY_PHOTO_CAPTURE);
                }
            }
        });

        findViewById(R.id.button_take_selfie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_FRONT_CAMERA_CAPTURE);
                }
            }
        });

        findViewById(R.id.button_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_BACK_CAMERA_CAPTURE);
                }
            }
        });

        findViewById(R.id.button_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Info Button Tapped", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BACK_CAMERA_CAPTURE && resultCode == RESULT_OK && null != data) {
            Toast.makeText(MainActivity.this, "Regular Photo Captured", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_FRONT_CAMERA_CAPTURE && resultCode == RESULT_OK && null != data) {
            Toast.makeText(MainActivity.this, "Selfie Photo Captured", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_GALLERY_PHOTO_CAPTURE && resultCode == RESULT_OK && null != data) {
            Toast.makeText(MainActivity.this, "Gallery Photo Captured", Toast.LENGTH_SHORT).show();
        }
        if (resultCode == RESULT_OK && null != data) {
            MainActivity.this.getImageViewForImage(data);
        }
    }

    private ImageView getImageViewForImage(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        ImageView imageView = new ImageView(null);
        imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        return imageView;
    }

    protected Boolean isFrontCameraAvailable() {
        Boolean supported = false;
        CameraInfo info = new CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                supported = true;
                break;
            }
        }
        return supported;
    }
}
