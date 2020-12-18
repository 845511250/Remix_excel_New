package com.example.zuoyun.remix_excel_new.tools;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by zuoyun on 2017/4/17.
 */

public class BitmapToJpg {
    public static void save(Bitmap bitmap, File file, int dpi) {
        try {
            long timeStart = new Date().getTime();

            ByteArrayOutputStream imageByteArray = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageByteArray);
            byte[] imageData = imageByteArray.toByteArray();
            imageByteArray.close();
            setDpi(imageData, dpi);

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(imageData);
            fileOutputStream.flush();
            fileOutputStream.close();
            imageData = null;
            Log.e("BitmapToJpg", "saved");

//            ByteArrayOutputStream imageByteArray = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, imageByteArray);
//            bitmap.recycle();
//            byte[] imageData = imageByteArray.toByteArray();
//            imageByteArray.close();
//            setDpi(imageData, dpi);
//            bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
//            imageData = null;
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
//            fileOutputStream.flush();
//            fileOutputStream.close();

            Log.e("aaa", "耗时：" + (new Date().getTime() - timeStart));
        }catch (Exception e){
            Log.e("BitmapToJpg", "Wrong in Class 'BitmapToJpg'");
        }
    }

    private static void setDpi(byte[] imageData, int dpi) {
        imageData[13] = 1;
        imageData[14] = (byte) (dpi >> 8);
        imageData[15] = (byte) (dpi & 0xff);
        imageData[16] = (byte) (dpi >> 8);
        imageData[17] = (byte) (dpi & 0xff);
    }
}
