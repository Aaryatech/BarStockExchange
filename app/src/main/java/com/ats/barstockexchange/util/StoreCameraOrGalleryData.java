package com.ats.barstockexchange.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by datta on 8/2/17.
 */

public class StoreCameraOrGalleryData {

    static File folder = new File(Environment.getExternalStorageDirectory() + File.separator, "Cat");
    static String imageEncodedGallery, imageEncodedCamera;
    File f1;

    public StoreCameraOrGalleryData() {
    }

    public static String storeGalleryPhotoInSDCard(Bitmap bitmap, String currentDate, String serviceName) {

        File outputFile = new File(folder + File.separator, "Gallery_" + serviceName + "_" + currentDate + ".jpeg");
        Log.e("PATH : ", outputFile.getAbsolutePath());
        try {

            ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Log.e("--------------------", "----------------------------------------------");
            Log.e("byte array length : ", "" + byteArray.length);
            Log.e("--------------------", "----------------------------------------------");
            imageEncodedGallery = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.e("--------------------", "----------------------------------------------");
            Log.e("Gallery encoded string:", imageEncodedGallery);
            Log.e("Gallery encoded length:", "" + imageEncodedGallery.length());
            Log.e("--------------------", "----------------------------------------------");

            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageEncodedGallery;
    }

    public static String storeCameraPhotoInSDCard(Bitmap bitmap, String currentDate, String serviceName) {

        File outputFile = new File(folder + File.separator, "Camera_" + serviceName + "_" + currentDate + ".jpeg");
        Log.e("PATH : ", outputFile.getAbsolutePath());

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream(1000);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Log.e("--------------------", "----------------------------------------------");
            Log.e("byte array length : ", "" + byteArray.length);
            Log.e("--------------------", "----------------------------------------------");
            imageEncodedCamera = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.e("--------------------", "----------------------------------------------");
            Log.e("Camera encoded string:", imageEncodedCamera);
            Log.e("Camera encoded length:", "" + imageEncodedCamera.length());
            Log.e("--------------------", "----------------------------------------------");

            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageEncodedCamera;
    }

    public static Bitmap ShrinkBitmap(String file, int width, int height) {
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }

}
