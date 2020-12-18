package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
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

public class FragmentLR extends BaseFragment {
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

    int width_side, height_side, width_main, height_main, width_tongue, height_tongue, width_back, height_back;
    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint rectPaint, paint, paintRed, paintBlue,paintRectBlack;
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

        paintRectBlack = new Paint();
        paintRectBlack.setColor(0xff000000);
        paintRectBlack.setStyle(Paint.Style.FILL);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(25);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(26);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(26);
        paintBlue.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;


        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                    Log.e("fragment2", "message0");
                } else if (message == MainActivity.LOADED_IMGS) {
                    if(!MainActivity.instance.cb_fastmode.isChecked()){
                        iv_pillow.setImageBitmap(MainActivity.instance.bitmaps.get(0));
                    }
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

    void drawTextSideR(Canvas canvas, String LR) {
        canvas.drawText(time + " " + orderItems.get(currentID).sku + orderItems.get(currentID).size + "码" + LR + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 33, 1086 - 3, paint);
        canvas.drawText(orderItems.get(currentID).newCode, 300, 1130 - 3, paint);
    }
    void drawTextSideL(Canvas canvas, String LR) {
        canvas.drawText(time + " " + orderItems.get(currentID).sku + orderItems.get(currentID).size + "码" + LR + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 625, 1086 - 3, paint);
        canvas.drawText(orderItems.get(currentID).newCode, 645, 1130 - 3, paint);
    }
    void drawTextMain(Canvas canvasPart2L, String LR){
        canvasPart2L.save();
        canvasPart2L.rotate(75.4f, 26, 88);
        canvasPart2L.drawText(time + " " + orderItems.get(currentID).sku + orderItems.get(currentID).size + "码" + LR + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode, 26, 88 - 3, paint);
        canvasPart2L.restore();
    }
    void drawTextTongue(Canvas canvasPart4L, String LR) {
        canvasPart4L.save();
        canvasPart4L.rotate(-12.2f, 149, 1020);
        canvasPart4L.drawRect(149, 1020 - 25, 149 + 90, 1020, rectPaint);
        canvasPart4L.drawText(orderItems.get(currentID).size + "码" + LR, 149, 1020 - 3, paint);

        canvasPart4L.restore();
        canvasPart4L.save();
        canvasPart4L.rotate(12.4f, 417, 995);
        canvasPart4L.drawRect(417, 995 - 25, 417 + 140, 995, rectPaint);
        canvasPart4L.drawText(orderItems.get(currentID).order_number, 417, 995 - 3, paint);
        canvasPart4L.restore();
    }
    void drawTextBack(Canvas canvas, String LR) {
        canvas.drawRect(107, 896 - 25, 107 + 85, 896, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + " " + LR, 107, 896 - 3, paint);
    }

    public void remixx(){
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(5484, 3040, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 10) {
            //L_back
            Bitmap bitmapTemp = Bitmap.createBitmap(307, 902, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);

            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(width_back, height_back);
            matrix.postTranslate(176 - width_back / 2, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //L_sideR
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideR(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 351, 1260 - height_side, null);

            //L_tongue
            bitmapTemp = Bitmap.createBitmap(700, 1029, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 2063 - width_tongue / 2, 0, null);

            //L_sideL
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideL(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 3775 - width_side, 1260 - height_side, null);

            //L_main
            bitmapTemp = Bitmap.createBitmap(1551, 1329, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 4629 - width_main / 2, 0, null);

            //R
            //R_back
            bitmapTemp = Bitmap.createBitmap(307, 902, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);

            matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(width_back, height_back);
            matrix.postTranslate(176 - width_back / 2, 0 + 1519);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //R_sideR
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(8), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideR(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 351, 1260 - height_side + 1519, null);

            //R_tongue
            bitmapTemp = Bitmap.createBitmap(700, 1029, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(9), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 2063 - width_tongue / 2, 0 + 1519, null);

            //R_sideL
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(7), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideL(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 3775 - width_side, 1260 - height_side + 1519, null);

            //R_main
            bitmapTemp = Bitmap.createBitmap(1551, 1329, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(6), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 4629 - width_main / 2, 0 + 1519, null);

            //
            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 6100) {
            //L_back
            Bitmap bitmapTemp = Bitmap.createBitmap(307, 902, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3131, -1372, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);

            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(width_back, height_back);
            matrix.postTranslate(176 - width_back / 2, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //L_sideR
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3088, -12, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideR(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 351, 1260 - height_side, null);

            //L_tongue
            bitmapTemp = Bitmap.createBitmap(700, 1029, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4236, -67, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 2063 - width_tongue / 2, 0, null);

            //L_sideL
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4936, -12, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideL(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 3775 - width_side, 1260 - height_side, null);

            //L_main
            bitmapTemp = Bitmap.createBitmap(1551, 1329, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3810, -1159, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 4629 - width_main / 2, 0, null);

            //R
            //R_back
            bitmapTemp = Bitmap.createBitmap(307, 902, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2642, -1372, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);

            matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(width_back, height_back);
            matrix.postTranslate(176 - width_back / 2, 0 + 1519);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //R_sideR
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -16, -12, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideR(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 351, 1260 - height_side + 1519, null);

            //R_tongue
            bitmapTemp = Bitmap.createBitmap(700, 1029, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1164, -67, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 2063 - width_tongue / 2, 0 + 1519, null);

            //R_sideL
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1864, -12, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideL(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 3775 - width_side, 1260 - height_side + 1519, null);

            //R_main
            bitmapTemp = Bitmap.createBitmap(1551, 1329, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -738, -1159, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 4629 - width_main / 2, 0 + 1519, null);

            //
            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 4211) {//大国
            //L_back
            Bitmap bitmapTemp = Bitmap.createBitmap(307, 902, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2200, -3131, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);

            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(width_back, height_back);
            matrix.postTranslate(176 - width_back / 2, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //L_sideR
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -227,-1372, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideR(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 351, 1260 - height_side, null);

            //L_tongue
            bitmapTemp = Bitmap.createBitmap(700, 1029, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1758, -1424, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 2063 - width_tongue / 2, 0, null);

            //L_sideL
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2835, -1372, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideL(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 3775 - width_side, 1260 - height_side, null);

            //L_main
            bitmapTemp = Bitmap.createBitmap(1551, 1329, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2434, -2654, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 4629 - width_main / 2, 0, null);

            //R
            //R_back
            bitmapTemp = Bitmap.createBitmap(307, 902, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1714, -3131, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);

            matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(width_back, height_back);
            matrix.postTranslate(176 - width_back / 2, 0 + 1519);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //R_sideR
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -227, -227, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideR(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 351, 1260 - height_side + 1519, null);

            //R_tongue
            bitmapTemp = Bitmap.createBitmap(700, 1029, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1758, -287, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 2063 - width_tongue / 2, 0 + 1519, null);

            //R_sideL
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2835, -227, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideL(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 3775 - width_side, 1260 - height_side + 1519, null);

            //R_main
            bitmapTemp = Bitmap.createBitmap(1551, 1329, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -227, -2654, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 4629 - width_main / 2, 0 + 1519, null);

            //
            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {//大国
            if(MainActivity.instance.bitmaps.get(0).getWidth() == 4000){
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 6421, 6421, true));
            }
            //L_back
            Bitmap bitmapTemp = Bitmap.createBitmap(307, 902, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3293, -3334, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);

            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(width_back, height_back);
            matrix.postTranslate(176 - width_back / 2, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //L_sideR
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3248,-1972, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideR(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 351, 1260 - height_side, null);

            //L_tongue
            bitmapTemp = Bitmap.createBitmap(700, 1029, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4399, -2027, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 2063 - width_tongue / 2, 0, null);

            //L_sideL
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -5097, -1972, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideL(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 3775 - width_side, 1260 - height_side, null);

            //L_main
            bitmapTemp = Bitmap.createBitmap(1551, 1329, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3970, -3119, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 4629 - width_main / 2, 0, null);

            //R
            //R_back
            bitmapTemp = Bitmap.createBitmap(307, 902, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2804, -3334, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);

            matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(width_back, height_back);
            matrix.postTranslate(176 - width_back / 2, 0 + 1519);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //R_sideR
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -176, -1972, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideR(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 351, 1260 - height_side + 1519, null);

            //R_tongue
            bitmapTemp = Bitmap.createBitmap(700, 1029, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1326, -2027, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 2063 - width_tongue / 2, 0 + 1519, null);

            //R_sideL
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2024, -1972, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideL(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 3775 - width_side, 1260 - height_side + 1519, null);

            //R_main
            bitmapTemp = Bitmap.createBitmap(1551, 1329, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -899, -3119, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 4629 - width_main / 2, 0 + 1519, null);

            //
            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1 && (MainActivity.instance.bitmaps.get(0).getWidth() == 14400 || MainActivity.instance.bitmaps.get(0).getWidth() == 7200)) {//adam
            if (MainActivity.instance.bitmaps.get(0).getWidth() == 14400) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 7200, 2500, true));
            }

            //L_back
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5261, 1408, 307, 902);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);

            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(width_back, height_back);
            matrix.postTranslate(176 - width_back / 2, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //L_sideR
            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3771, 20, 1246, 1233);
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);

            matrix = new Matrix();
            matrix.postTranslate(-80, 0);
            matrix.postRotate(-4);
            matrix.postScale(1148f / 1172, 1140f / 1157);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideR(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 351, 1260 - height_side, null);

            //L_tongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5064, 173, 700, 1029);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            Log.e("aaa", bitmapTemp.getWidth() + "--" + bitmapTemp.getHeight());
            Log.e("aaa", width_side + "," + width_tongue);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 2063 - width_tongue / 2, 0, null);

            //L_sideL
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5808, 20, 1246, 1233);
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);

            matrix = new Matrix();
            matrix.postTranslate(0, -81);
            matrix.postRotate(4);
            matrix.postScale(1148f / 1172, 1140f / 1157);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideL(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 3775 - width_side, 1260 - height_side, null);

            //L_main
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4592, 1033, 1645, 1382);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1551, 1329, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 4629 - width_main / 2, 0, null);


            //right
            //R_back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1639, 1408, 307, 902);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);

            matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(width_back, height_back);
            matrix.postTranslate(176 - width_back / 2, 0 + 1519);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //R_sideR
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 145, 20, 1246, 1233);
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);

            matrix = new Matrix();
            matrix.postTranslate(-80, 0);
            matrix.postRotate(-4);
            matrix.postScale(1148f / 1172, 1140f / 1157);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideR(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 351, 1260 - height_side + 1519, null);

            //R_tongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1429, 173, 700, 1029);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 2063 - width_tongue / 2, 0 + 1519, null);

            //R_sideL
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2194, 20, 1246, 1233);
            bitmapTemp = Bitmap.createBitmap(1148, 1140, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);

            matrix = new Matrix();
            matrix.postTranslate(0, -81);
            matrix.postRotate(4);
            matrix.postScale(1148f / 1172, 1140f / 1157);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideL(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 3775 - width_side, 1260 - height_side + 1519, null);

            //R_main
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 971, 1033, 1645, 1382);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1551, 1329, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lr_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 4629 - width_main / 2, 0 + 1519, null);

            //
            bitmapCut.recycle();
            bitmapTemp.recycle();
            bitmapDB.recycle();
        }


        try {
            String nameCombine = orderItems.get(currentID).sku + "_" + orderItems.get(currentID).size + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 150);

            //释放bitmap
            bitmapCombine.recycle();

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

    void setSize(int size){
        switch (size) {
            case 35:
                width_main = 1413;
                height_main = 1124;
                width_side = 967;
                height_side = 1037;
                width_tongue = 639;
                height_tongue = 886;
                width_back = 264;
                height_back = 821;
                break;
            case 36:
                width_main = 1436;
                height_main = 1155;
                width_side = 994;
                height_side = 1053;
                width_tongue = 639;
                height_tongue = 886;
                width_back = 264;
                height_back = 821;
                break;
            case 37:
                width_main = 1460;
                height_main = 1185;
                width_side = 1022;
                height_side = 1071;
                width_tongue = 662;
                height_tongue = 932;
                width_back = 278;
                height_back = 850;
                break;
            case 38:
                width_main = 1482;
                height_main = 1216;
                width_side = 1050;
                height_side = 1088;
                width_tongue = 662;
                height_tongue = 932;
                width_back = 278;
                height_back = 850;
                break;
            case 39:
                width_main = 1505;
                height_main = 1245;
                width_side = 1078;
                height_side = 1105;
                width_tongue = 684;
                height_tongue = 980;
                width_back = 293;
                height_back = 879;
                break;
            case 40:
                width_main = 1528;
                height_main = 1276;
                width_side = 1105;
                height_side = 1122;
                width_tongue = 684;
                height_tongue = 980;
                width_back = 293;
                height_back = 879;
                break;
            case 41:
                width_main = 1551;
                height_main = 1307;
                width_side = 1132;
                height_side = 1140;
                width_tongue = 707;
                height_tongue = 1025;
                width_back = 307;
                height_back = 909;
                break;
            case 42:
                width_main = 1573;
                height_main = 1337;
                width_side = 1160;
                height_side = 1156;
                width_tongue = 707;
                height_tongue = 1025;
                width_back = 307;
                height_back = 909;
                break;
            case 43:
                width_main = 1596;
                height_main = 1366;
                width_side = 1187;
                height_side = 1173;
                width_tongue = 730;
                height_tongue = 1072;
                width_back = 321;
                height_back = 938;
                break;
            case 44:
                width_main = 1619;
                height_main = 1397;
                width_side = 1215;
                height_side = 1190;
                width_tongue = 730;
                height_tongue = 1072;
                width_back = 321;
                height_back = 938;
                break;
            case 45:
                width_main = 1642;
                height_main = 1428;
                width_side = 1242;
                height_side = 1208;
                width_tongue = 752;
                height_tongue = 1119;
                width_back = 336;
                height_back = 968;
                break;
            case 46:
                width_main = 1664;
                height_main = 1459;
                width_side = 1269;
                height_side = 1226;
                width_tongue = 752;
                height_tongue = 1119;
                width_back = 336;
                height_back = 968;
                break;
            case 47:
                width_main = 1687;
                height_main = 1489;
                width_side = 1297;
                height_side = 1244;
                width_tongue = 776;
                height_tongue = 1165;
                width_back = 351;
                height_back = 997;
                break;
            case 48:
                width_main = 1709;
                height_main = 1520;
                width_side = 1324;
                height_side = 1260;
                width_tongue = 776;
                height_tongue = 1165;
                width_back = 351;
                height_back = 997;
                break;
        }
        width_main += 3;
        height_main += 3;
        width_side += 3;
        height_side += 3;
        width_tongue += 3;
        height_tongue += 3;
        width_back += 3;
        height_back += 3;
    }
}
