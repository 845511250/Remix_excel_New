package com.example.zuoyun.remix_excel_new.tools;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by zuoyun on 2017/4/17.
 */

public class BitmapToPng {
    public static Bitmap cut(Bitmap bitmap, Bitmap bitmapPath) {
        for (int x = 0; x < bitmapPath.getWidth(); x++) {
            for (int y = 0; y < bitmapPath.getHeight(); y++) {
                if (bitmapPath.getPixel(x, y) == -1) {//白色点取值为-1
                    bitmap.setPixel(x, y, Color.TRANSPARENT);
                }
            }
        }
        return bitmap;
    }

    public static void save(Bitmap bitmap, File file, int dpi) {
        try {
            ByteArrayOutputStream imageByteArray = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageByteArray);
            bitmap.recycle();
            byte[] imageData = imageByteArray.toByteArray();
            imageByteArray.flush();
            imageByteArray.close();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(setDpi(imageData, dpi));
            fileOutputStream.flush();
            fileOutputStream.close();
            imageData = null;
            Log.e("aaa", "saved");
        } catch (Exception e) {
            Log.e("aaa", "Wrong in Class 'BitmapToPng'");
            Log.e("aaa", e.getMessage());
        }
    }

    private static byte[] setDpi(byte[] imageData, int dpi) {
        byte[] imageDataCopy = new byte[imageData.length + 21];
        System.arraycopy(imageData, 0, imageDataCopy, 0, 33);
        System.arraycopy(imageData, 33, imageDataCopy, 33 + 21, imageData.length - 33);

        int[] sPHs = new int[]{0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 23, 18, 0, 0, 23, 18, 1, 103, 159, 210, 82};//default value:150dpi----

        for (int i = 0; i < 21; i++) {
            imageDataCopy[i + 33] = (byte) (sPHs[i] & 0xff);
        }

        dpi = (int) (dpi / 0.0254);
        imageDataCopy[41] = (byte) (dpi >> 24);
        imageDataCopy[42] = (byte) (dpi >> 16);
        imageDataCopy[43] = (byte) (dpi >> 8);
        imageDataCopy[44] = (byte) (dpi & 0xff);

        imageDataCopy[45] = (byte) (dpi >> 24);
        imageDataCopy[46] = (byte) (dpi >> 16);
        imageDataCopy[47] = (byte) (dpi >> 8);
        imageDataCopy[48] = (byte) (dpi & 0xff);

//        for (int i = 0; i < 30; i++) {
//            String line = "";
//            for (int j = 0; j < 16; j++) {
//                int temp = imageDataCopy[16 * i + j] & 0xFF;
//                line += Integer.toHexString(temp) + " ";
//            }
//            Log.e(i + "", line);
//        }
        return imageDataCopy;
    }
}
