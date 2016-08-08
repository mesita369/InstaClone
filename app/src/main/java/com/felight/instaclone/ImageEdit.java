package com.felight.instaclone;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageEdit extends AppCompatActivity {
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Uri imageUri;
    private ImageView originalImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        originalImage = (ImageView) findViewById(R.id.image);
    }
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }

    }
    //capture method
    private void onCaptureImageResult(Intent data) {
        imageUri = data.getData();
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 50, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".png");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(destination.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotateImage(thumbnail, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotateImage(thumbnail, 180);
                break;
            // etc.
        }
        originalImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        imageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(imageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;

        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        originalImage.setImageBitmap(bm);

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }
    public void getPicture(View v){
        switch (v.getId()){
            case R.id.cam:
                getPictureFromCam();
                break;
            case R.id.gallery:
                getPictureFromGallery();
                break;
            default:
        }
    }

    private Bitmap getPictureFromCam() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
        return null;
    }

    private Bitmap getPictureFromGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                SELECT_FILE);
        return null;
    }

public void addEffect(View v){
    originalImage.setDrawingCacheEnabled(true);
    originalImage.buildDrawingCache();
    Bitmap originalBitmap = originalImage.getDrawingCache();
    Bitmap bitmapEffect;
    switch (v.getId()){
       case R.id.grayscale:
           bitmapEffect =  ImageEffects.doGreyscale(originalBitmap);
            originalImage.setImageBitmap(bitmapEffect);
           break;
        case R.id.sepia:
            bitmapEffect =  ImageEffects.createSepiaToningEffect(originalBitmap);
            originalImage.setImageBitmap(bitmapEffect);
            originalImage.setDrawingCacheEnabled(false);
            break;
        case R.id.snow:
            bitmapEffect =  ImageEffects.applySnowEffect(originalBitmap);
            originalImage.setImageBitmap(bitmapEffect);
            originalImage.setDrawingCacheEnabled(false);
            break;
        case R.id.sharp:
            bitmapEffect =  ImageEffects.sharpen(originalBitmap);
            originalImage.setImageBitmap(bitmapEffect);
            originalImage.setDrawingCacheEnabled(false);
            break;
        case R.id.blur:
            bitmapEffect =  ImageEffects.applyGaussianBlur(originalBitmap);
            originalImage.setImageBitmap(bitmapEffect);
            originalImage.setDrawingCacheEnabled(false);
            break;
        case R.id.reflect:
            bitmapEffect =  ImageEffects.applyReflection(originalBitmap);
            originalImage.setImageBitmap(bitmapEffect);
            originalImage.setDrawingCacheEnabled(false);
            break;
    }
}
}
