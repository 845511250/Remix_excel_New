package com.example.zuoyun.remix_excel_new.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zuoyun.remix_excel_new.R;
import com.example.zuoyun.remix_excel_new.bean.OrderItem;
import com.example.zuoyun.remix_excel_new.tools.BitmapToJpg;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * Created by zuoyun on 2016/11/4.
 */

public class FragmentHLL extends BaseFragment {
    Context context;
//    String sdCardPath = "/mnt/asec/share";
String sdCardPath = "/storage/emulated/0/Pictures";
    ArrayList<OrderItem> orderItems;
    int currentID;
    String childPath;
    
    @BindView(R.id.bt_remix)
    Button bt_remix;
    @BindView(R.id.iv_pillow)
    ImageView iv_pillow;

    int num;
    String strPlus = "";
    int intPlus = 1;
    boolean sizeOK = true;

    Paint rectPaint, rectPaintGrey, paint, paintRed, paintBlue, rectBorderPaint, paintSmall;
    String time;

    int dpi, width, height;
    int width_barcode, height_barcode;//6x2.5cm
    int height_barText;//0.4cm

    @Override
    public int getLayout() {
        return R.layout.fragment_dg;
    }

    @Override
    public void initData(View view) {
        context = getContext();
        ButterKnife.bind(this, view);
        orderItems=MainActivity.instance.orderItems;
        currentID = MainActivity.instance.currentID;
        childPath = MainActivity.instance.childPath;

        //paint
        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        rectPaintGrey = new Paint();
        rectPaintGrey.setColor(0xffececd8);
        rectPaintGrey.setStyle(Paint.Style.FILL);

        rectBorderPaint = new Paint();
        rectBorderPaint.setColor(0xff000000);
        rectBorderPaint.setStyle(Paint.Style.STROKE);
        rectBorderPaint.setStrokeWidth(8);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(25);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(30);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(30);
        paintBlue.setTypeface(Typeface.DEFAULT_BOLD);
        paintBlue.setAntiAlias(true);

        paintSmall = new Paint();
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(23);
        paintSmall.setTypeface(Typeface.DEFAULT_BOLD);
        paintSmall.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                    bt_remix.setClickable(false);
                } else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
                    if(!MainActivity.instance.cb_fastmode.isChecked())
                        iv_pillow.setImageBitmap(MainActivity.instance.bitmaps.get(0));
                    checkremix();
                } else if (message == 3) {
                    bt_remix.setClickable(false);
                } else if (message == 10) {
                    remix();
                }
            }
        });

        bt_remix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remix();
            }
        });
        bt_remix.setClickable(false);
    }

    public void remix(){
        new Thread(){
            @Override
            public void run() {
                super.run();

                if (sizeOK) {
                    for(num=orderItems.get(currentID).num;num>=1;num--) {
                        intPlus = orderItems.get(currentID).num - num + 1;
                        for(int i=0;i<currentID;i++) {
                            if (orderItems.get(currentID).order_number.equals(orderItems.get(i).order_number)) {
                                intPlus += orderItems.get(i).num;;
                            }
                        }
                        strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                        remixx();
                    }
                }
            }
        }.start();

    }

    void drawTextMain(Canvas canvas) {
        canvas.save();
        canvas.rotate(-90, width, 1200 + 1000);
        canvas.drawRect(width, 1200 + 1000 - 30, width + 1000, 1200 + 1000 - 5, rectPaint);
        canvas.drawText("空调毯-" + orderItems.get(currentID).sku + " " + orderItems.get(currentID).color + "   " + time + "   " + orderItems.get(currentID).order_number + "    " + orderItems.get(currentID).newCode, width, 1200 + 1000 - 8, paint);
        canvas.restore();
        canvas.save();
        canvas.rotate(90, 0, height - 1200 - 1000);
        canvas.drawRect(0, height - 1200 - 1000 - 30, 1000, height - 1200 - 1000 - 5, rectPaint);
        canvas.drawText("空调毯-" + orderItems.get(currentID).sku + " " + orderItems.get(currentID).color + "   " + time + "   " + orderItems.get(currentID).order_number + "    " + orderItems.get(currentID).newCode, 0, height - 1200 - 1000 - 8, paint);
        canvas.restore();
    }
    void drawTextPillow(Canvas canvas) {
        canvas.drawRect(100, 5, 100 + 1000, 5 + 25, rectPaint);
        canvas.drawText("空调毯-枕套-" + orderItems.get(currentID).sku + " " + orderItems.get(currentID).color + "   " + time + "   " + orderItems.get(currentID).order_number + "    " + orderItems.get(currentID).newCode, 100, 5 + 22, paint);
    }

    void drawTextJJ(Canvas canvas) {
        paint.setTextSize(30);

        canvas.drawRect(width - 1200, height - 38, width - 100, height - 8, rectPaint);
        canvas.drawText("下边 空调毯-" + orderItems.get(currentID).sku + " " + orderItems.get(currentID).color + "   " + time + "   " + orderItems.get(currentID).order_number + "    " + orderItems.get(currentID).newCode, width - 1200, height - 12, paint);

        canvas.drawRect(width - 1200, 8, width - 100, 8 + 30, rectPaint);
        canvas.drawText("上边 空调毯-" + orderItems.get(currentID).sku + " " + orderItems.get(currentID).color + "   " + time + "   " + orderItems.get(currentID).order_number + "    " + orderItems.get(currentID).newCode, width - 1200, 8 + 30 - 4, paint);
    }

    public void remixx(){
        if (orderItems.get(currentID).sizeStr.toLowerCase().contains("king")) {
            orderItems.get(currentID).sku = "HLL5";
        } else if (orderItems.get(currentID).sizeStr.toLowerCase().contains("queen")) {
            orderItems.get(currentID).sku = "HLL4";
        } else if (orderItems.get(currentID).sizeStr.toLowerCase().contains("twin")) {
            orderItems.get(currentID).sku = "HLL3";
        }

        setSize();
        Bitmap bitmapTemp = null;
        Canvas canvasTemp;

        if (orderItems.get(currentID).platform.endsWith("jj") && MainActivity.instance.bitmaps.get(0).getWidth() == 14800) {
            //空调毯
            Matrix matrix = new Matrix();
            matrix.postScale(width / 10926f, height / 12172f);
            if (!orderItems.get(currentID).sku.equals("HLL5")) {
                matrix.postRotate(-90);
                matrix.postTranslate(0, width);
            }
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1900, 2518, 10926, 12172, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            if (!orderItems.get(currentID).sku.equals("HLL5")) {
                canvasTemp.rotate(-90);
                canvasTemp.translate(-width, 0);
            }

            canvasTemp.drawRect(0, 0, width, height, rectBorderPaint);
            canvasTemp.drawRect(4, 4, width - 4, height - 4, rectBorderPaint);
            drawTextJJ(canvasTemp);

            //填充透明背景为白色
            for (int x = 0; x < bitmapTemp.getWidth(); x++) {
                for (int y = 0; y < bitmapTemp.getHeight(); y++) {
                    if (bitmapTemp.getPixel(x, y) == 0) {//透明点取值为0
                        bitmapTemp.setPixel(x, y, Color.WHITE);
                    }
                }
            }

            String nameCombine = "(HLL空调毯)" + orderItems.get(currentID).nameStr + strPlus + ".jpg";
            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapTemp, fileSave, dpi);

        } else if (orderItems.get(currentID).platform.endsWith("jj") && MainActivity.instance.bitmaps.get(0).getWidth() == 12000) {
            //空调毯
            Matrix matrix = new Matrix();
            matrix.postScale(width / 8850f, height / 9808f);
            if (!orderItems.get(currentID).sku.equals("HLL5")) {
                matrix.postRotate(-90);
                matrix.postTranslate(0, width);
            }
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1562, 2087, 8850, 9808, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            if (!orderItems.get(currentID).sku.equals("HLL5")) {
                canvasTemp.rotate(-90);
                canvasTemp.translate(-width, 0);
            }

            canvasTemp.drawRect(0, 0, width, height, rectBorderPaint);
            canvasTemp.drawRect(4, 4, width - 4, height - 4, rectBorderPaint);
            drawTextJJ(canvasTemp);

            //填充透明背景为白色
            for (int x = 0; x < bitmapTemp.getWidth(); x++) {
                for (int y = 0; y < bitmapTemp.getHeight(); y++) {
                    if (bitmapTemp.getPixel(x, y) == 0) {//透明点取值为0
                        bitmapTemp.setPixel(x, y, Color.WHITE);
                    }
                }
            }

            String nameCombine = "(HLL空调毯)" + orderItems.get(currentID).nameStr + strPlus + ".jpg";
            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapTemp, fileSave, dpi);

        } else if (orderItems.get(currentID).platform.endsWith("jj") && MainActivity.instance.bitmaps.get(0).getWidth() == 11000 && MainActivity.instance.bitmaps.get(0).getHeight() == 14800) {//super平台
            //空调毯
            Matrix matrix = new Matrix();
            matrix.postScale(width / 10926f, height / 12172f);
            if (!orderItems.get(currentID).sku.equals("HLL5")) {
                matrix.postRotate(-90);
                matrix.postTranslate(0, width);
            }
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 41, 2518, 10926, 12172, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            if (!orderItems.get(currentID).sku.equals("HLL5")) {
                canvasTemp.rotate(-90);
                canvasTemp.translate(-width, 0);
            }

            canvasTemp.drawRect(0, 0, width, height, rectBorderPaint);
            canvasTemp.drawRect(4, 4, width - 4, height - 4, rectBorderPaint);
            drawTextJJ(canvasTemp);

            //填充透明背景为白色
            for (int x = 0; x < bitmapTemp.getWidth(); x++) {
                for (int y = 0; y < bitmapTemp.getHeight(); y++) {
                    if (bitmapTemp.getPixel(x, y) == 0) {//透明点取值为0
                        bitmapTemp.setPixel(x, y, Color.WHITE);
                    }
                }
            }

            String nameCombine = "(空调毯" + orderItems.get(currentID).sku + ")" + orderItems.get(currentID).nameStr + strPlus + ".jpg";
            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapTemp, fileSave, dpi);

        } else if (orderItems.get(currentID).platform.endsWith("jj")) {
            //空调毯
            Matrix matrix = new Matrix();
            matrix.postScale(width / 10926f, height / 12172f);
            if (!orderItems.get(currentID).sku.equals("HLL5")) {
                matrix.postRotate(-90);
                matrix.postTranslate(0, width);
            }
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 37, 2414, 10926, 12172, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            if (!orderItems.get(currentID).sku.equals("HLL5")) {
                canvasTemp.rotate(-90);
                canvasTemp.translate(-width, 0);
            }

            canvasTemp.drawRect(0, 0, width, height, rectBorderPaint);
            canvasTemp.drawRect(4, 4, width - 4, height - 4, rectBorderPaint);
            drawTextJJ(canvasTemp);

            String nameCombine = "(HLL空调毯)" + orderItems.get(currentID).nameStr + strPlus + ".jpg";
            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapTemp, fileSave, dpi);

        } else {
            //空调毯
            Matrix matrix = new Matrix();
            matrix.postScale(width / 10926f, height / 12172f);
            if (!orderItems.get(currentID).sku.equals("HLL5")) {
                matrix.postRotate(-90);
                matrix.postTranslate(0, width);
            }
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 37, 2414, 10926, 12172, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            if (!orderItems.get(currentID).sku.equals("HLL5")) {
                canvasTemp.rotate(-90);
                canvasTemp.translate(-width, 0);
            }

            canvasTemp.drawRect(0, 0, width, height, rectBorderPaint);
            canvasTemp.drawRect(4, 4, width - 4, height - 4, rectBorderPaint);
            drawTextJJ(canvasTemp);

            String nameCombine = "(HLL空调毯)" + orderItems.get(currentID).nameStr + strPlus + ".jpg";
            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapTemp, fileSave, dpi);

        }



        //空调毯枕套
        if (orderItems.get(currentID).platform.endsWith("jj") && MainActivity.instance.bitmaps.get(0).getWidth() == 14800) {
            rectBorderPaint.setStrokeWidth(4);

            Bitmap bitmapCombine = Bitmap.createBitmap(3600, 4800 + 5, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine= new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2900, 140, 3600, 2400);
            canvasTemp= new Canvas(bitmapTemp);
            drawTextPillow(canvasTemp);
            canvasTemp.drawRect(0, 0, 3600, 2400, rectBorderPaint);
            canvasTemp.drawRect(2, 2, 3600 - 2, 2400 - 2, rectBorderPaint);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 8290, 140, 3600, 2400);
            canvasTemp= new Canvas(bitmapTemp);
            drawTextPillow(canvasTemp);
            canvasTemp.drawRect(0, 0, 3600, 2400, rectBorderPaint);
            canvasTemp.drawRect(2, 2, 3600 - 2, 2400 - 2, rectBorderPaint);
            canvasCombine.drawBitmap(bitmapTemp, 0, 2400 + 5, null);
            bitmapTemp.recycle();

            Matrix matrix90 = new Matrix();
            matrix90.postRotate(90);
            matrix90.postTranslate(bitmapCombine.getHeight(), 0);
            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix90, true);

            if (num == 1) {
                MainActivity.recycleExcelImages();
            }

            String nameCombine = "(HLL枕套)" + orderItems.get(currentID).nameStr + strPlus + ".jpg";
            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 116);

        } else if (orderItems.get(currentID).platform.endsWith("jj")  && MainActivity.instance.bitmaps.get(0).getWidth() == 11000 && MainActivity.instance.bitmaps.get(0).getHeight() == 14800) {
            rectBorderPaint.setStrokeWidth(4);

            Bitmap bitmapCombine = Bitmap.createBitmap(3600, 4800 + 5, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine= new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1000, 132, 3600, 2400);
            canvasTemp= new Canvas(bitmapTemp);
            drawTextPillow(canvasTemp);
            canvasTemp.drawRect(0, 0, 3600, 2400, rectBorderPaint);
            canvasTemp.drawRect(2, 2, 3600 - 2, 2400 - 2, rectBorderPaint);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6390, 132, 3600, 2400);
            canvasTemp= new Canvas(bitmapTemp);
            drawTextPillow(canvasTemp);
            canvasTemp.drawRect(0, 0, 3600, 2400, rectBorderPaint);
            canvasTemp.drawRect(2, 2, 3600 - 2, 2400 - 2, rectBorderPaint);
            canvasCombine.drawBitmap(bitmapTemp, 0, 2400 + 5, null);
            bitmapTemp.recycle();

            if (num == 1) {
                MainActivity.recycleExcelImages();
            }

            Matrix matrix90 = new Matrix();
            matrix90.postRotate(90);
            matrix90.postTranslate(bitmapCombine.getHeight(), 0);
            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix90, true);

            String nameCombine = "(空调毯枕套" + orderItems.get(currentID).sku + ")" + orderItems.get(currentID).nameStr + strPlus + ".jpg";
            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 116);

        } else if (orderItems.get(currentID).platform.endsWith("-jj") && MainActivity.instance.bitmaps.get(0).getWidth() == 12000) {
            rectBorderPaint.setStrokeWidth(4);

            Bitmap bitmapCombine = Bitmap.createBitmap(3600, 4800 + 5, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine= new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            Matrix matrix = new Matrix();
            matrix.setScale(3600f / 2932, 2400f / 1955);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2348, 102, 2932, 1955, matrix, true);
            canvasTemp= new Canvas(bitmapTemp);
            drawTextPillow(canvasTemp);
            canvasTemp.drawRect(0, 0, 3600, 2400, rectBorderPaint);
            canvasTemp.drawRect(2, 2, 3600 - 2, 2400 - 2, rectBorderPaint);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6720, 102, 2932, 1955, matrix, true);
            canvasTemp= new Canvas(bitmapTemp);
            drawTextPillow(canvasTemp);
            canvasTemp.drawRect(0, 0, 3600, 2400, rectBorderPaint);
            canvasTemp.drawRect(2, 2, 3600 - 2, 2400 - 2, rectBorderPaint);
            canvasCombine.drawBitmap(bitmapTemp, 0, 2400 + 5, null);
            bitmapTemp.recycle();

            if (num == 1) {
                MainActivity.recycleExcelImages();
            }

            Matrix matrix90 = new Matrix();
            matrix90.postRotate(90);
            matrix90.postTranslate(bitmapCombine.getHeight(), 0);
            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix90, true);

            String nameCombine = "(HLL枕套)" + orderItems.get(currentID).nameStr + strPlus + ".jpg";
            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 116);

        } else {
            rectBorderPaint.setStrokeWidth(4);

            Bitmap bitmapCombine = Bitmap.createBitmap(3600, 4800 + 5, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine= new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1007, 14, 3600, 2400);
            canvasTemp= new Canvas(bitmapTemp);
            drawTextPillow(canvasTemp);
            canvasTemp.drawRect(0, 0, 3600, 2400, rectBorderPaint);
            canvasTemp.drawRect(2, 2, 3600 - 2, 2400 - 2, rectBorderPaint);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6393, 14, 3600, 2400);
//            MainActivity.instance.bitmaps.clear();
            canvasTemp= new Canvas(bitmapTemp);
            drawTextPillow(canvasTemp);
            canvasTemp.drawRect(0, 0, 3600, 2400, rectBorderPaint);
            canvasTemp.drawRect(2, 2, 3600 - 2, 2400 - 2, rectBorderPaint);
            canvasCombine.drawBitmap(bitmapTemp, 0, 2400 + 5, null);
            bitmapTemp.recycle();

            if (num == 1) {
                MainActivity.recycleExcelImages();
            }

            Matrix matrix90 = new Matrix();
            matrix90.postRotate(90);
            matrix90.postTranslate(bitmapCombine.getHeight(), 0);
            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix90, true);

            String nameCombine = "(HLL枕套)" + orderItems.get(currentID).nameStr + strPlus + ".jpg";
            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 116);

        }



        try {
            //写入excel
            String writePath = sdCardPath + "/生产图/" + childPath + "/生产单.xls";
            File fileWrite = new File(writePath);
            if(!fileWrite.exists()){
                WritableWorkbook book = Workbook.createWorkbook(fileWrite);
                WritableSheet sheet = book.createSheet("sheet1", 0);
                Label label0 = new Label(0, 0, "货号");
                sheet.addCell(label0);
                Label label1 = new Label(1, 0, "结构");
                sheet.addCell(label1);
                Label label2 = new Label(2, 0, "数量");
                sheet.addCell(label2);
                Label label3 = new Label(3, 0, "业务员");
                sheet.addCell(label3);
                Label label4 = new Label(4, 0, "销售日期");
                sheet.addCell(label4);
                Label label5 = new Label(5, 0, "备注");
                sheet.addCell(label5);
                Label label6 = new Label(6, 0, "部门");
                sheet.addCell(label6);
                book.write();
                book.close();
            }

            Workbook book = Workbook.getWorkbook(fileWrite);
            WritableWorkbook workbook = Workbook.createWorkbook(fileWrite,book);
            WritableSheet sheet = workbook.getSheet(0);
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku);
            sheet.addCell(label1);
            int num=orderItems.get(currentID).num;
            Number number2 = new Number(2, currentID+1, num);
            sheet.addCell(number2);
            Label label3 = new Label(3, currentID+1, orderItems.get(currentID).customer);
            sheet.addCell(label3);
            Label label4 = new Label(4, currentID + 1, MainActivity.instance.orderDate_Excel);
            sheet.addCell(label4);
            Label label6 = new Label(6, currentID + 1, "平台大货");
            sheet.addCell(label6);

            workbook.write();
            workbook.close();

        }catch (Exception e){
        }
        if (num == 1) {
            MainActivity.recycleExcelImages();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.instance.tv_finishRemixx.setText("完成");
                    if (MainActivity.instance.tb_auto.isChecked()) {
                        MainActivity.instance.setnext();
                    }
                }
            });
        }
    }
    public void checkremix(){
        if (MainActivity.instance.tb_auto.isChecked()){
            remix();
        }
    }


    public void showDialogSizeWrong(final String order_number){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog dialog_finish;
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogTransBackGround);
                dialog_finish = builder.create();
                dialog_finish.setCancelable(false);
                dialog_finish.show();
                View view_dialog = LayoutInflater.from(context).inflate(R.layout.item_dialog_finish, null);
                dialog_finish.setContentView(view_dialog);
                TextView tv_title = (TextView) view_dialog.findViewById(R.id.tv_dialog_title);
                TextView tv_content = (TextView) view_dialog.findViewById(R.id.tv_dialog_content);
                Button bt_yes = (Button) view_dialog.findViewById(R.id.bt_dialog_yes);

                tv_title.setText("错误！");
                tv_content.setText("单号："+order_number+"读取尺码失败");
                bt_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_finish.dismiss();
                        getActivity().finish();
                    }
                });
            }
        });
    }

    void setSize(){
        switch (orderItems.get(currentID).sku) {
            case "HLL3":
                dpi = 150;
                width = 11497;
                height = 13087;
                width_barcode = 354;
                height_barcode = 148;
                height_barText = 24;
                break;
            case "HLL4":
                dpi = 138;
                width = 11306;
                height = 12719;
                width_barcode = 326;
                height_barcode = 136;
                height_barText = 22;
                break;
            case "HLL5":
                dpi = 124;
                width = 11576;
                height = 12995;
                width_barcode = 293;
                height_barcode = 122;
                height_barText = 20;
                break;
            default:
                sizeOK = false;
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                break;
        }
    }

}
