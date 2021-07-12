package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.zuoyun.remix_excel_new.R;
import com.example.zuoyun.remix_excel_new.bean.OrderItem;
import com.example.zuoyun.remix_excel_new.tools.BitmapToJpg;
import com.example.zuoyun.remix_excel_new.tools.BitmapToPng;

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

public class FragmentFS extends BaseFragment {
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

    int mainXLeft, mainXRight, mainYLeft, mainYRight;
    int barXLeft, barXRight, barYLeft, barYRight;
    int sideXLeft, sideXRight, sideYLeft, sideYRight;
    int mianWidth, mainHeight, barWidth, barHeight, sideWidth, sideHeight;
    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint rectPaint, paint, paintRed, paintBlue;
    String time;

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

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(15);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(15);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(15);
        paintBlue.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;


        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
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

    void drawTextMain(Canvas canvas, String LR) {
        canvas.drawRect(403, 696 - 14, 403 + 45, 696, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + LR, 403, 696 - 2, paint);
    }
    void drawTextSide(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(6.7f, 204, 435);
        canvas.drawRect(204, 435 - 14, 204 + 180, 435, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).order_number, 204, 435 - 2, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-6.7f, 613, 454);
        canvas.drawRect(613, 454 - 14, 613 + 160, 454, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).sizeStr + "码" + LR, 613, 454 - 2, paint);
        canvas.restore();
    }

    public void remixx(){
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(2001, 1620, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);
        Matrix matrixCombine = new Matrix();

        if (MainActivity.instance.bitmaps.get(0).getWidth() == 2488) {
            //left main
            Bitmap bitmapTemp = Bitmap.createBitmap(853, 711, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1409, -1033, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(mainXLeft, mainYLeft);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //left bar
            bitmapTemp = Bitmap.createBitmap(560, 188, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1556, -1743, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_bar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, barWidth, barHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(barXLeft, barYLeft);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //left side
            bitmapTemp = Bitmap.createBitmap(1176, 477, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1248, -557, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);

            Matrix matrix90 = new Matrix();
            matrix90.postRotate(-90);
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix90, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(sideXLeft, sideYLeft);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);


            //right main
            bitmapTemp = Bitmap.createBitmap(853, 711, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -223, -1036, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(mainXRight, mainYRight);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //right bar
            bitmapTemp = Bitmap.createBitmap(560, 188, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -370, -1743, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_bar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, barWidth, barHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(barXRight, barYRight);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);


            //right side
            bitmapTemp = Bitmap.createBitmap(1176, 477, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -65, -558, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);

            matrix90 = new Matrix();
            matrix90.postRotate(-90);
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix90, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(sideXRight, sideYRight);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 6 && orderItems.get(currentID).imgs.get(0).contains("PrintL")) {//dropshipping
            //left main
            Bitmap bitmapDBMain = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_main_dropshipping);

            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 14, 0, 871, 786);
            Canvas canvasLeft_main = new Canvas(bitmapTemp);
            canvasLeft_main.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLeft_main.drawBitmap(bitmapDBMain, 0, 0, null);

            //drawTextMain(canvasLeft_main, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(mainXLeft, mainYLeft);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //right main
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(5), 14, 0, 871, 786);
            Canvas canvasRight_main = new Canvas(bitmapTemp);
            canvasRight_main.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRight_main.drawBitmap(bitmapDBMain, 0, 0, null);
            bitmapDBMain.recycle();

            //drawTextMain(canvasRight_main, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(mainXRight, mainYRight);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);


            //left bar
            Bitmap bitmapDBBar = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_bar_dropshipping);
            MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 620, 214, true));
            bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasBarLeft = new Canvas(bitmapTemp);
            canvasBarLeft.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasBarLeft.drawBitmap(bitmapDBBar, 0, 0, null);

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, barWidth, barHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(barXLeft, barYLeft);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //right bar
            MainActivity.instance.bitmaps.set(3, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(3), 620, 214, true));
            bitmapTemp = MainActivity.instance.bitmaps.get(3).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasBarRight = new Canvas(bitmapTemp);
            canvasBarRight.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasBarRight.drawBitmap(bitmapDBBar, 0, 0, null);
            bitmapDBBar.recycle();

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, barWidth, barHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(barXRight, barYRight);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);


            //left side
            Bitmap bitmapDBSide = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_side_dropshipping);

            bitmapTemp = Bitmap.createBitmap(1006, 466, Bitmap.Config.ARGB_8888);
            Canvas canvasLeft_side = new Canvas(bitmapTemp);
            canvasLeft_side.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            matrixCombine.reset();
            matrixCombine.postRotate(9.7f);
            matrixCombine.postTranslate(24, -170);
            canvasLeft_side.drawBitmap(MainActivity.instance.bitmaps.get(1), matrixCombine, null);

            canvasLeft_side.drawBitmap(bitmapDBSide, 0, 0, null);
//        drawTextSide(canvasLeft_side, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);

            Matrix matrix90 = new Matrix();
            matrix90.postRotate(-90);
            matrix90.postTranslate(0, bitmapTemp.getWidth());
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix90, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(sideXLeft, sideYLeft);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //right side
            bitmapTemp = Bitmap.createBitmap(1006, 466, Bitmap.Config.ARGB_8888);
            Canvas canvasRight_side = new Canvas(bitmapTemp);
            canvasRight_side.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            matrixCombine.reset();
            matrixCombine.postRotate(9.7f);
            matrixCombine.postTranslate(24, -170);
            canvasRight_side.drawBitmap(MainActivity.instance.bitmaps.get(4), matrixCombine, null);

            canvasRight_side.drawBitmap(bitmapDBSide, 0, 0, null);
            bitmapDBSide.recycle();
//        drawTextSide(canvasRight_side, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);

            matrix90.reset();
            matrix90.postRotate(-90);
            matrix90.postTranslate(0, bitmapTemp.getWidth());
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix90, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(sideXRight, sideYRight);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 6) {
            //left main
            Bitmap bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(mainXLeft, mainYLeft);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //left bar
            Bitmap bitmapLeftBar = MainActivity.instance.bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapLeftBar);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_bar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapLeftBar = Bitmap.createScaledBitmap(bitmapLeftBar, barWidth, barHeight, true);

            //left side
            bitmapTemp = MainActivity.instance.bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);

            Matrix matrix90 = new Matrix();
            matrix90.postRotate(-90);
            matrix90.postTranslate(0, bitmapTemp.getWidth());
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix90, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(sideXLeft, sideYLeft);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //right main
            bitmapTemp = MainActivity.instance.bitmaps.get(3).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(mainXRight, mainYRight);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //right bar
            bitmapTemp = MainActivity.instance.bitmaps.get(5).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_bar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, barWidth, barHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(barXRight, barYRight);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);
            matrixCombine.reset();
            matrixCombine.postTranslate(barXLeft, barYLeft);
            canvasCombine.drawBitmap(bitmapLeftBar, matrixCombine, null);

            //right side
            bitmapTemp = MainActivity.instance.bitmaps.get(4).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);

            matrix90.reset();
            matrix90.postRotate(-90);
            matrix90.postTranslate(0, bitmapTemp.getWidth());
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix90, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(sideXRight, sideYRight);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //left main
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 161, 477, 853, 711);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(mainXLeft, mainYLeft);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //left bar
            Bitmap bitmapLeftBar = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 308, 1188, 560, 188);
            canvasTemp = new Canvas(bitmapLeftBar);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_bar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapLeftBar = Bitmap.createScaledBitmap(bitmapLeftBar, barWidth, barHeight, true);

            //left side
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 1176, 477);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);

            Matrix matrix90 = new Matrix();
            matrix90.postRotate(-90);
            matrix90.postTranslate(0, bitmapTemp.getWidth());
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix90, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(sideXLeft, sideYLeft);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //翻转原图
            matrixCombine.reset();
            matrixCombine.postScale(-1, 1);
            MainActivity.instance.bitmaps.set(0, Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, MainActivity.instance.bitmaps.get(0).getWidth(), MainActivity.instance.bitmaps.get(0).getHeight(), matrixCombine, true));
            //right main
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 161, 477, 853, 711);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(mainXRight, mainYRight);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //right bar
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 308, 1188, 560, 188);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_bar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, barWidth, barHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(barXRight, barYRight);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);
            matrixCombine.reset();
            matrixCombine.postTranslate(barXLeft, barYLeft);
            canvasCombine.drawBitmap(bitmapLeftBar, matrixCombine, null);

            //right side
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 1176, 477);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fs_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);

            matrix90 = new Matrix();
            matrix90.postRotate(-90);
            matrix90.postTranslate(0, bitmapTemp.getWidth());
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix90, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(sideXRight, sideYRight);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        }


        try {
//            Matrix matrix90 = new Matrix();
//            matrix90.postRotate(90);
//            matrix90.postTranslate(1890, 0);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, 3791, 1890, matrix90, true);

            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 150);



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
            Label label0 = new Label(0, currentID+1, orderItems.get(currentID).order_number+orderItems.get(currentID).sku+ orderItems.get(currentID).size);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku+ orderItems.get(currentID).size);
            sheet.addCell(label1);
            int num=orderItems.get(currentID).num;
            Number number2 = new Number(2, currentID+1, num);
            sheet.addCell(number2);
            Label label3 = new Label(3, currentID+1, orderItems.get(currentID).customer);
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
            case 17:
                mianWidth = 806;
                mainHeight = 643;
                sideWidth = 1065;
                sideHeight = 449;
                barWidth = 529;
                barHeight = 171;
                mainXLeft = 45;
                mainYLeft = 136;
                mainXRight = 45;
                mainYRight = 938;
                sideXLeft = 955;
                sideYLeft = 112;
                sideXRight = 1473;
                sideYRight = 112;
                barXLeft = 773;
                barYLeft = 1386;
                barXRight = 1386;
                barYRight = 1321;
                break;
            case 18:
                mianWidth = 829;
                mainHeight = 677;
                sideWidth = 1120;
                sideHeight = 464;
                barWidth = 529;
                barHeight = 171;
                mainXLeft = 34;
                mainYLeft = 102;
                mainXRight = 34;
                mainYRight = 905;
                sideXLeft = 947;
                sideYLeft = 84;
                sideXRight = 1466;
                sideYRight = 84;
                barXLeft = 773;
                barYLeft = 1386;
                barXRight = 1386;
                barYRight = 1321;
                break;
            case 19:
                mianWidth = 853;
                mainHeight = 711;
                sideWidth = 1176;
                sideHeight = 477;
                barWidth = 560;
                barHeight = 188;
                mainXLeft = 22;
                mainYLeft = 68;
                mainXRight = 22;
                mainYRight = 871;
                sideXLeft = 942;
                sideYLeft = 56;
                sideXRight = 1459;
                sideYRight = 55;
                barXLeft = 773;
                barYLeft = 1386;
                barXRight = 1386;
                barYRight = 1321;
                break;
            case 20:
                mianWidth = 876;
                mainHeight = 745;
                sideWidth = 1231;
                sideHeight = 490;
                barWidth = 560;
                barHeight = 188;
                mainXLeft = 11;
                mainYLeft = 34;
                mainXRight = 11;
                mainYRight = 837;
                sideXLeft = 936;
                sideYLeft = 28;
                sideXRight = 1456;
                sideYRight = 28;
                barXLeft = 773;
                barYLeft = 1386;
                barXRight = 1386;
                barYRight = 1321;
                break;
            case 21:
                mianWidth = 898;
                mainHeight = 779;
                sideWidth = 1287;
                sideHeight = 502;
                barWidth = 575;
                barHeight = 196;
                mainXLeft = 0;
                mainYLeft = 0;
                mainXRight = 0;
                mainYRight = 810;
                sideXLeft = 932;
                sideYLeft = 0;
                sideXRight = 1450;
                sideYRight = 0;
                barXLeft = 773;
                barYLeft = 1386;
                barXRight = 1386;
                barYRight = 1321;
                break;
        }
    }

}
