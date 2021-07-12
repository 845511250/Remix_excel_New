package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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

public class FragmentDJ extends BaseFragment {
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

    int mainWidth,mainHeight,sideWidth, sideHeight;
    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint paint, paintRed, paintBlue, rectPaint;
    String time = MainActivity.instance.orderDate_Print;

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
        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(35);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(35);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(35);
        paintBlue.setTypeface(Typeface.DEFAULT_BOLD);
        paintBlue.setAntiAlias(true);

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);


        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    Log.e("fragment2", "message0");
                } else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
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
    }

    public void remix(){
        new Thread(){
            @Override
            public void run() {
                super.run();
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
        }.start();

    }

    void drawTextMain_4u2(Canvas canvasLeftMain, String LR){
        paintRed.setTextSize(35);
        paint.setTextSize(35);

        canvasLeftMain.save();
        canvasLeftMain.rotate(-6.4f, 882, 56);
        canvasLeftMain.drawRect(882, 56, 882 + 350, 56 + 30, rectPaint);
        canvasLeftMain.drawText(orderItems.get(currentID).order_number + "  " + time, 882, 56 + 28, paint);
        canvasLeftMain.restore();
        canvasLeftMain.save();
        canvasLeftMain.rotate(6.8f, 92, 44);
        canvasLeftMain.drawRect(42, 8, 500, 44, rectPaint);
        canvasLeftMain.drawText(" " + LR + " " + orderItems.get(currentID).size + "码", 62, 41, paintRed);
        canvasLeftMain.restore();
    }
    void drawTextSide_4u2(Canvas canvasLeftSide, String LR) {
        paintRed.setTextSize(40);
        paint.setTextSize(40);

        canvasLeftSide.save();
        canvasLeftSide.rotate(-165.9f, 1400, 105);
        canvasLeftSide.drawRect(1400, 105 - 50, 1800, 105, rectPaint);
        canvasLeftSide.drawText(LR + " " + orderItems.get(currentID).newCode, 1400, 105 - 3, paintRed);
        canvasLeftSide.restore();
        canvasLeftSide.save();
        canvasLeftSide.rotate(167.2f, 740, 17);
        canvasLeftSide.drawRect(740, 17 - 50, 740 + 400, 17, rectPaint);
        canvasLeftSide.drawText("  " + time + "  " + orderItems.get(currentID).size + "码", 740, 17 - 3, paintRed);
        canvasLeftSide.restore();
    }

    void drawTextMain_adam(Canvas canvasLeftMain, String LR){
        paintRed.setTextSize(35);
        paint.setTextSize(35);

        canvasLeftMain.save();
        canvasLeftMain.rotate(-6.8f, 900, 80);
        canvasLeftMain.drawRect(900, 44, 1250, 80, rectPaint);
        canvasLeftMain.drawText(orderItems.get(currentID).order_number + "  " + time, 900, 80 - 6, paint);
        canvasLeftMain.restore();
        canvasLeftMain.save();
        canvasLeftMain.rotate(6.8f, 92, 44);
        canvasLeftMain.drawRect(42, 8, 500, 44, rectPaint);
        //Bitmap bitmapBarCode = BarCodeUtil.creatBarcode(orderItems.get(currentID).order_number.replace("_", ""), 460, 36);
        //canvasLeftMain.drawBitmap(bitmapBarCode, 46, 8, null);
        canvasLeftMain.drawText(" " + LR + orderItems.get(currentID).size + "码", 62, 44 - 6, paintRed);
        canvasLeftMain.restore();
    }

    void drawTextSide_adam(Canvas canvasLeftSide, String LR) {
        paintRed.setTextSize(35);
        paint.setTextSize(35);

        canvasLeftSide.save();
        canvasLeftSide.rotate(-165.9f, 1388, 110);
        canvasLeftSide.drawRect(1388, 110 - 40, 1388 + 350, 110, rectPaint);
        canvasLeftSide.drawText(" " + LR + " " + orderItems.get(currentID).newCode, 1388, 110 - 6, paintRed);
        canvasLeftSide.restore();
        canvasLeftSide.save();
        canvasLeftSide.rotate(166.4f, 810, 6);
        canvasLeftSide.drawRect(810, 6 - 40, 810 + 480, 6, rectPaint);
        canvasLeftSide.drawText(time + "  " + orderItems.get(currentID).size + "码 " + orderItems.get(currentID).order_number, 810, 6 - 6, paintRed);
        canvasLeftSide.restore();
    }

    public void remixx(){
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(sideWidth + mainHeight * 2 + 200, mainWidth + 100, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {//jj
            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2230, 979, 1939, 771, matrix, true);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1784, 669, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dj_drop_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide_4u2(canvasTemp,"左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            matrix = new Matrix();
            matrix.postTranslate(0, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);


            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2455, 2327, 1494, 1282);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1366, 1152, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dj_drop_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain_4u2(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            matrix = new Matrix();
            matrix.postRotate(-90);
            matrix.postTranslate(sideWidth + 100, mainWidth);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            matrix.reset();
            matrix.postRotate(180);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 130, 979, 1939, 771, matrix, true);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1784, 669, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dj_drop_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide_4u2(canvasTemp,"右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            matrix.reset();
            matrix.postTranslate(0, sideHeight + 100);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 362, 2327, 1494, 1282);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1366, 1152, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dj_drop_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain_4u2(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            matrix.reset();
            matrix.postRotate(-90);
            matrix.postTranslate(sideWidth + mainHeight + 200, mainWidth);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

        } else if (orderItems.get(currentID).imgs.size() <= 2 && MainActivity.instance.bitmaps.get(0).getWidth() < 1998) {//4u2
            if (orderItems.get(currentID).imgs.size() == 1) {
                Matrix matrix = new Matrix();
                matrix.postScale(-1, 1);

                MainActivity.instance.bitmaps.add(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, MainActivity.instance.bitmaps.get(0).getWidth(), MainActivity.instance.bitmaps.get(0).getHeight(), matrix, true));
            }
            //left_main
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 192, 1866 - 1152, 1366, 1152);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dj_drop_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain_4u2(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);

            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            matrix.postTranslate(sideWidth + 100, mainWidth);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //left_side
            Bitmap bitmapSide1 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 669, 892);
            Bitmap bitmapSide2 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1759 - 669, 0, 669, 892);

            bitmapTemp = Bitmap.createBitmap(1784, 669, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            matrix = new Matrix();
            matrix.postRotate(90);
            matrix.postTranslate(892, 0);
            canvasTemp.drawBitmap(bitmapSide1, matrix, null);
            matrix.reset();
            matrix.postRotate(-90);
            matrix.postTranslate(892, 669);
            canvasTemp.drawBitmap(bitmapSide2, matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dj_drop_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide_4u2(canvasTemp,"左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);

            matrix = new Matrix();
            matrix.postTranslate(0, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //right_main
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 192, 1866 - 1152, 1366, 1152);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dj_drop_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain_4u2(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);

            matrix.reset();
            matrix.postRotate(-90);
            matrix.postTranslate(sideWidth + mainHeight + 200, mainWidth);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //right_side
            bitmapSide1 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, 669, 892);
            bitmapSide2 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1759 - 669, 0, 669, 892);
            bitmapTemp = Bitmap.createBitmap(1784, 669, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postRotate(90);
            matrix.postTranslate(892, 0);
            canvasTemp.drawBitmap(bitmapSide1, matrix, null);
            matrix.reset();
            matrix.postRotate(-90);
            matrix.postTranslate(892, 669);
            canvasTemp.drawBitmap(bitmapSide2, matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dj_drop_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide_4u2(canvasTemp,"右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);

            matrix.reset();
            matrix.postTranslate(0, sideHeight + 100);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            bitmapSide1.recycle();
            bitmapSide2.recycle();
            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 2 && MainActivity.instance.bitmaps.get(0).getWidth() == 1998) {//adam
            Bitmap bitmapDB_main = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dj_adam_main);
            Bitmap bitmapDB_side = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dj_adam_side);

            //left_main
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 331, 1015, 1288, 1086);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain_adam(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);

            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            matrix.postTranslate(sideWidth + 100, mainWidth);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //left_side
            Bitmap bitmapSide1 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 297, 124, 698, 890);
            Bitmap bitmapSide2 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1062, 124, 698, 890);

            bitmapTemp = Bitmap.createBitmap(1780, 698, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Matrix matrixSide = new Matrix();
            matrixSide.postRotate(90);
            matrixSide.postTranslate(890 + 2, 0);
            canvasTemp.drawBitmap(bitmapSide1, matrixSide, null);
            matrixSide.reset();
            matrixSide.postRotate(-90);
            matrixSide.postTranslate(890 - 2, 698);
            canvasTemp.drawBitmap(bitmapSide2, matrixSide, null);
            canvasTemp.drawBitmap(bitmapDB_side, 0, 0, null);
            drawTextSide_adam(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);

            matrix = new Matrix();
            matrix.postTranslate(0, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //right_main
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 331, 1015, 1288, 1086);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain_adam(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);

            matrix.reset();
            matrix.postRotate(-90);
            matrix.postTranslate(sideWidth + mainHeight + 200, mainWidth);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //right_side
            bitmapSide1 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 297, 124, 698, 890);
            bitmapSide2 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1062, 124, 698, 890);

            bitmapTemp = Bitmap.createBitmap(1780, 698, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrixSide.reset();
            matrixSide.postRotate(90);
            matrixSide.postTranslate(890 + 2, 0);
            canvasTemp.drawBitmap(bitmapSide1, matrixSide, null);
            matrixSide.reset();
            matrixSide.postRotate(-90);
            matrixSide.postTranslate(890 - 2, 698);
            canvasTemp.drawBitmap(bitmapSide2, matrixSide, null);
            canvasTemp.drawBitmap(bitmapDB_side, 0, 0, null);
            drawTextSide_adam(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);

            matrix.reset();
            matrix.postTranslate(0, sideHeight + 100);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            bitmapDB_main.recycle();
            bitmapDB_side.recycle();
            bitmapTemp.recycle();
            bitmapSide1.recycle();
            bitmapSide2.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 4) {
            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(1784, 669);
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 1784, 669, matrix, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dj_drop_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide_4u2(canvasTemp,"左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            matrix = new Matrix();
            matrix.postTranslate(0, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            bitmapTemp = MainActivity.instance.bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dj_drop_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain_4u2(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            matrix = new Matrix();
            matrix.postRotate(-90);
            matrix.postTranslate(sideWidth + 100, mainWidth);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(1784, 669);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, 1784, 669, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dj_drop_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide_4u2(canvasTemp,"右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            matrix.reset();
            matrix.postTranslate(0, sideHeight + 100);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            bitmapTemp = MainActivity.instance.bitmaps.get(3).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dj_drop_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain_4u2(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            matrix.reset();
            matrix.postRotate(-90);
            matrix.postTranslate(sideWidth + mainHeight + 200, mainWidth);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();
        }


        try {
            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 149);



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
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + orderItems.get(currentID).size);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + orderItems.get(currentID).size);
            sheet.addCell(label1);
            Number number2 = new Number(2, currentID + 1, orderItems.get(currentID).num);
            sheet.addCell(number2);
            Label label3 = new Label(3, currentID + 1, orderItems.get(currentID).customer);
            sheet.addCell(label3);
            Label label4 = new Label(4, currentID + 1, MainActivity.instance.orderDate_Excel);
            sheet.addCell(label4);
            Label label6 = new Label(6, currentID+1, "平台大货");
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

    void setSize(int size){
        switch (size) {
            case 36:
                mainWidth = 1242;
                mainHeight = 1019;
                sideWidth = 1592;
                sideHeight = 604;
                break;
            case 37:
                mainWidth = 1267;
                mainHeight = 1045;
                sideWidth = 1629;
                sideHeight = 617;
                break;
            case 38:
                mainWidth = 1291;
                mainHeight = 1073;
                sideWidth = 1668;
                sideHeight = 630;
                break;
            case 39:
                mainWidth = 1316;
                mainHeight = 1099;
                sideWidth = 1706;
                sideHeight = 643;
                break;
            case 40:
                mainWidth = 1341;
                mainHeight = 1127;
                sideWidth = 1745;
                sideHeight = 656;
                break;
            case 41:
                mainWidth = 1367;
                mainHeight = 1152;
                sideWidth = 1784;
                sideHeight = 669;
                break;
            case 42:
                mainWidth = 1391;
                mainHeight = 1178;
                sideWidth = 1822;
                sideHeight = 681;
                break;
            case 43:
                mainWidth = 1416;
                mainHeight = 1205;
                sideWidth = 1861;
                sideHeight = 692;
                break;
            case 44:
                mainWidth = 1441;
                mainHeight = 1230;
                sideWidth = 1899;
                sideHeight = 703;
                break;
            case 45:
                mainWidth = 1466;
                mainHeight = 1257;
                sideWidth = 1938;
                sideHeight = 715;
                break;
            case 46:
                mainWidth = 1491;
                mainHeight = 1285;
                sideWidth = 1976;
                sideHeight = 726;
                break;
        }
    }

}
