package com.example.zuoyun.remix_excel_new.tools;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by zuoyun on 2017/4/17.
 */

public class BitmapToJpg {
    public static void save(Bitmap bitmap, File file, int dpi) {
        try {
            ByteArrayOutputStream imageByteArray = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageByteArray);
            bitmap.recycle();//释放bitmap内存

            byte[] imageData = imageByteArray.toByteArray();
            imageByteArray.flush();
            imageByteArray.close();

            setDpi(imageData, dpi);

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(imageData);
            fileOutputStream.flush();
            fileOutputStream.close();
            imageData = null;
            Log.e("BitmapToJpg", "saved");

        }catch (Exception e){
            Log.e("BitmapToJpg", "Wrong in Class 'BitmapToJpg'\n" + e.getMessage());
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
