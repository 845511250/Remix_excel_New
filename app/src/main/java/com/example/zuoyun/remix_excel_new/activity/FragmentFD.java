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
 * Created by zuoyun on 2016/10/6.
 */

public class FragmentFD extends BaseFragment {
    Context context;
    //    String sdCardPath = "/mnt/asec/share";
    String sdCardPath = "/storage/emulated/0/Pictures";
    ArrayList<OrderItem> orderItems;
    int currentID;
    String childPath;

    @BindView(R.id.iv_fg2_leftup)
    ImageView iv_leftup;
    @BindView(R.id.iv_fg2_rightup)
    ImageView iv_rightup;
    @BindView(R.id.bt_remix)
    Button bt_remix;

    int widthMain, widthTongue, widthSide;
    int heightMain, heightTongue, heightSide;
    int x_l_main, x_l_side, x_l_tongue, x_r_main, x_r_side, x_r_tongue;
    int y_l_main, y_l_side, y_l_tongue, y_r_main, y_r_side, y_r_tongue;
    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint paint,paintRed,paintBlue, rectPaint;
    String time = MainActivity.instance.orderDate_Print;

    @Override
    public int getLayout() {
        return R.layout.fragment_dq;
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
        paint.setTextSize(26);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(26);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff000000);
        paintBlue.setTextSize(26);
        paintBlue.setTypeface(Typeface.DEFAULT_BOLD);
        paintBlue.setAntiAlias(true);

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message,String sampleurl) {
                if(message==0){
                    iv_leftup.setImageDrawable(null);
                    iv_rightup.setImageDrawable(null);
                } else if(message==MainActivity.LOADED_IMGS){
                    bt_remix.setClickable(true);
                    if(!MainActivity.instance.cb_fastmode.isChecked())
                        iv_rightup.setImageBitmap(MainActivity.instance.bitmaps.get(0));
//                    Glide.with(context).load(sampleurl).into(iv_sample2);
                    checkremix();
                } else if (message==3){
                    bt_remix.setClickable(false);
                } else if (message == 10) {
                    remix();
                }
            }
        });

        //******************************************************************************************
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
        canvas.save();
        canvas.rotate(66f, 92, 925);
        canvas.drawRect(92, 925-26, 470, 925, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).order_number, 92, 923, paint);
        canvas.restore();

        canvas.drawRect(645, 1530-26, 790, 1530, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "码" + LR + orderItems.get(currentID).color, 645, 1528, paint);

        canvas.save();
        canvas.rotate(-64.8f, 1121, 1289);
        canvas.drawRect(1121, 1289-26, 1500, 1289, rectPaint);
        canvas.drawText(orderItems.get(currentID).newCode, 1121, 1287, paintRed);
        canvas.restore();
    }

    void drawTextSide(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(174f, 675, 9);
        canvas.drawRect(675, -21, 1175, 9, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).size + "码 " + LR, 675, 6, paint);
        canvas.restore();

//        canvas.drawRect(270, 630, 640, 660, rectPaint);
//        canvas.drawText(orderItems.get(currentID).newCode + "      验片码" + orderItems.get(currentID).platform, 270, 657, paintSmall);
    }

    void drawTextTongue(Canvas canvas, String LR) {
        canvas.drawRect(282, 1106, 482, 1136, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "码 " + LR + "  " + orderItems.get(currentID).order_number, 282, 1133, paint);

//        canvas.drawRect(645, 630, 1020, 660, rectPaint);
//        canvas.drawText(orderItems.get(currentID).newCode + "      验片码" + orderItems.get(currentID).platform, 645, 657, paintSmall);
    }
    public void remixx(){
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine;
        bitmapCombine = Bitmap.createBitmap(3214, 3500, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.drawColor(0xffffffff);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        Bitmap bitmapDB_main = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.light_high_41main);
        Bitmap bitmapDB_tongue = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.light_high_41tongue);
        Bitmap bitmapDB_side1 = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.light_high_41side1);
        Bitmap bitmapDB_side2 = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.light_high_41side2);
        Bitmap bitmapDB_side = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fd_side);

        if (orderItems.get(currentID).imgs.size() == 2) {
            //left mian
            Bitmap bitmapLeft_main = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 179, 361, 1387, 1577);
            Canvas canvasLeft_main = new Canvas(bitmapLeft_main);
            canvasLeft_main.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLeft_main.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasLeft_main, "左");

            bitmapLeft_main = Bitmap.createScaledBitmap(bitmapLeft_main, widthMain, heightMain, true);
            canvasCombine.drawBitmap(bitmapLeft_main, x_l_main, y_l_main, null);
            bitmapLeft_main.recycle();

            //left tongue
            Bitmap bitmapLeft_tongue = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 497, 304, 751, 1149);
            Canvas canvasLeft_tongue = new Canvas(bitmapLeft_tongue);
            canvasLeft_tongue.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLeft_tongue.drawBitmap(bitmapDB_tongue, 0, 0, null);
            drawTextTongue(canvasLeft_tongue, "左");

            bitmapLeft_tongue = Bitmap.createScaledBitmap(bitmapLeft_tongue, widthTongue, heightTongue, true);
            canvasCombine.drawBitmap(bitmapLeft_tongue, x_l_tongue, y_l_tongue, null);
            bitmapLeft_tongue.recycle();

            //left side
            Bitmap bitmap_cut1 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 1046, 891);
            Bitmap bitmap_cut2 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 701, 0, 1046, 891);

            Bitmap bitmap_side1 = Bitmap.createBitmap(762, 914, Bitmap.Config.ARGB_8888);
            Canvas canvas_side1 = new Canvas(bitmap_side1);
            canvas_side1.drawColor(0xffffffff);
            canvas_side1.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Matrix matrix = new Matrix();
            matrix.postRotate(102);
            matrix.postTranslate(903, 38);
            canvas_side1.drawBitmap(bitmap_cut1, matrix, null);
            canvas_side1.drawBitmap(bitmapDB_side1, 0, 0, null);
            bitmap_cut1.recycle();

            Bitmap bitmap_side2 = Bitmap.createBitmap(762, 914, Bitmap.Config.ARGB_8888);
            Canvas canvas_side2 = new Canvas(bitmap_side2);
            canvas_side2.drawColor(0xffffffff);
            canvas_side2.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postRotate(-102);
            matrix.postTranslate(71, 1059);
            canvas_side2.drawBitmap(bitmap_cut2, matrix, null);
            canvas_side2.drawBitmap(bitmapDB_side2, 0, 0, null);
            bitmap_cut2.recycle();

            Bitmap bitmapLeft_side = Bitmap.createBitmap(1524, 914, Bitmap.Config.ARGB_8888);
            Canvas canvasLeft_side = new Canvas(bitmapLeft_side);
            canvasLeft_side.drawColor(0xffffffff);
            canvasLeft_side.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLeft_side.drawBitmap(bitmap_side1, 0, 0, null);
            canvasLeft_side.drawBitmap(bitmap_side2, 762, 0, null);
            bitmap_side1.recycle();
            bitmap_side2.recycle();
            drawTextSide(canvasLeft_side, "左");

            bitmapLeft_side = Bitmap.createScaledBitmap(bitmapLeft_side, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapLeft_side, x_l_side, y_l_side, null);
            bitmapLeft_side.recycle();

            //right main
            Bitmap bitmapRight_main = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 179, 361, 1387, 1577);
            Canvas canvasRight_main = new Canvas(bitmapRight_main);
            canvasRight_main.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRight_main.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasRight_main, "右");

            bitmapRight_main = Bitmap.createScaledBitmap(bitmapRight_main, widthMain, heightMain, true);
            canvasCombine.drawBitmap(bitmapRight_main, x_r_main, y_r_main, null);
            bitmapRight_main.recycle();

            //right tongue
            Bitmap bitmapRight_tongue = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 497, 304, 751, 1149);
            Canvas canvasright_tongue = new Canvas(bitmapRight_tongue);
            canvasright_tongue.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasright_tongue.drawBitmap(bitmapDB_tongue, 0, 0, null);
            drawTextTongue(canvasright_tongue, "右");

            bitmapRight_tongue = Bitmap.createScaledBitmap(bitmapRight_tongue, widthTongue, heightTongue, true);
            canvasCombine.drawBitmap(bitmapRight_tongue, x_r_tongue, y_r_tongue, null);
            bitmapRight_tongue.recycle();

            //right side
            bitmap_cut1 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, 1046, 891);
            bitmap_cut2 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 701, 0, 1046, 891);

            bitmap_side1 = Bitmap.createBitmap(762, 914, Bitmap.Config.ARGB_8888);
            canvas_side1 = new Canvas(bitmap_side1);
            canvas_side1.drawColor(0xffffffff);
            canvas_side1.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postRotate(102);
            matrix.postTranslate(903, 38);
            canvas_side1.drawBitmap(bitmap_cut1, matrix, null);
            canvas_side1.drawBitmap(bitmapDB_side1, 0, 0, null);
            bitmap_cut1.recycle();

            bitmap_side2 = Bitmap.createBitmap(762, 914, Bitmap.Config.ARGB_8888);
            canvas_side2 = new Canvas(bitmap_side2);
            canvas_side2.drawColor(0xffffffff);
            canvas_side2.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postRotate(-102);
            matrix.postTranslate(71, 1059);
            canvas_side2.drawBitmap(bitmap_cut2, matrix, null);
            canvas_side2.drawBitmap(bitmapDB_side2, 0, 0, null);
            bitmap_cut2.recycle();

            Bitmap bitmapRight_side = Bitmap.createBitmap(1524, 914, Bitmap.Config.ARGB_8888);
            Canvas canvasRight_side = new Canvas(bitmapRight_side);
            canvasRight_side.drawColor(0xffffffff);
            canvasRight_side.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRight_side.drawBitmap(bitmap_side1, 0, 0, null);
            canvasRight_side.drawBitmap(bitmap_side2, 762, 0, null);
            bitmap_side1.recycle();
            bitmap_side2.recycle();
            drawTextSide(canvasRight_side, "右");

            bitmapRight_side = Bitmap.createScaledBitmap(bitmapRight_side, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapRight_side, x_r_side, y_r_side, null);
            bitmapRight_side.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 6) {
            //left mian
            Bitmap bitmapLeft_main = MainActivity.instance.bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasLeft_main = new Canvas(bitmapLeft_main);
            canvasLeft_main.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLeft_main.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasLeft_main, "左");

            bitmapLeft_main = Bitmap.createScaledBitmap(bitmapLeft_main, widthMain, heightMain, true);
            canvasCombine.drawBitmap(bitmapLeft_main, x_l_main, y_l_main, null);
            bitmapLeft_main.recycle();

            //left side
            Bitmap bitmapLeft_side = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            Matrix matrix = new Matrix();
            matrix.postRotate(180, bitmapLeft_side.getWidth() / 2, bitmapLeft_side.getHeight() / 2);
            bitmapLeft_side = Bitmap.createBitmap(bitmapLeft_side, 0, 0, bitmapLeft_side.getWidth(), bitmapLeft_side.getHeight(), matrix, true);

            Canvas canvasLeft_side = new Canvas(bitmapLeft_side);
            canvasLeft_side.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLeft_side.drawBitmap(bitmapDB_side, 0, 0, null);

            drawTextSide(canvasLeft_side, "左");

            bitmapLeft_side = Bitmap.createScaledBitmap(bitmapLeft_side, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapLeft_side, x_l_side, y_l_side, null);
            bitmapLeft_side.recycle();

            //left tongue
            Bitmap bitmapLeft_tongue = MainActivity.instance.bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasLeft_tongue = new Canvas(bitmapLeft_tongue);
            canvasLeft_tongue.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLeft_tongue.drawBitmap(bitmapDB_tongue, 0, 0, null);
            drawTextTongue(canvasLeft_tongue, "左");

            bitmapLeft_tongue = Bitmap.createScaledBitmap(bitmapLeft_tongue, widthTongue, heightTongue, true);
            canvasCombine.drawBitmap(bitmapLeft_tongue, x_l_tongue, y_l_tongue, null);
            bitmapLeft_tongue.recycle();

            //right main
            Bitmap bitmapRight_main = MainActivity.instance.bitmaps.get(5).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasRight_main = new Canvas(bitmapRight_main);
            canvasRight_main.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRight_main.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasRight_main, "右");

            bitmapRight_main = Bitmap.createScaledBitmap(bitmapRight_main, widthMain, heightMain, true);
            canvasCombine.drawBitmap(bitmapRight_main, x_r_main, y_r_main, null);
            bitmapRight_main.recycle();

            //right tongue
            Bitmap bitmapRight_tongue = MainActivity.instance.bitmaps.get(4).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasright_tongue = new Canvas(bitmapRight_tongue);
            canvasright_tongue.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasright_tongue.drawBitmap(bitmapDB_tongue, 0, 0, null);
            drawTextTongue(canvasright_tongue, "右");

            bitmapRight_tongue = Bitmap.createScaledBitmap(bitmapRight_tongue, widthTongue, heightTongue, true);
            canvasCombine.drawBitmap(bitmapRight_tongue, x_r_tongue, y_r_tongue, null);
            bitmapRight_tongue.recycle();

            //right side
            Bitmap bitmapRight_side = MainActivity.instance.bitmaps.get(3).copy(Bitmap.Config.ARGB_8888, true);
            matrix = new Matrix();
            matrix.postRotate(180, bitmapRight_side.getWidth() / 2, bitmapRight_side.getHeight() / 2);
            bitmapRight_side = Bitmap.createBitmap(bitmapRight_side, 0, 0, bitmapRight_side.getWidth(), bitmapRight_side.getHeight(), matrix, true);

            Canvas canvasRight_side = new Canvas(bitmapRight_side);
            canvasRight_side.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRight_side.drawBitmap(bitmapDB_side, 0, 0, null);

            drawTextSide(canvasRight_side, "右");

            bitmapRight_side = Bitmap.createScaledBitmap(bitmapRight_side, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapRight_side, x_r_side, y_r_side, null);
            bitmapRight_side.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //left mian
            Bitmap bitmapLeft_main = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2276, 914, 1387, 1577);
            Canvas canvasLeft_main = new Canvas(bitmapLeft_main);
            canvasLeft_main.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLeft_main.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasLeft_main, "左");

            bitmapLeft_main = Bitmap.createScaledBitmap(bitmapLeft_main, widthMain, heightMain, true);
            canvasCombine.drawBitmap(bitmapLeft_main, x_l_main, y_l_main, null);
            bitmapLeft_main.recycle();

            //left side
            Bitmap bitmapLeft_side = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2207, 0, 1524, 914);
            Matrix matrix = new Matrix();
            matrix.postRotate(180, bitmapLeft_side.getWidth() / 2, bitmapLeft_side.getHeight() / 2);
            bitmapLeft_side = Bitmap.createBitmap(bitmapLeft_side, 0, 0, bitmapLeft_side.getWidth(), bitmapLeft_side.getHeight(), matrix, true);

            Canvas canvasLeft_side = new Canvas(bitmapLeft_side);
            canvasLeft_side.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLeft_side.drawBitmap(bitmapDB_side, 0, 0, null);

            drawTextSide(canvasLeft_side, "左");

            bitmapLeft_side = Bitmap.createScaledBitmap(bitmapLeft_side, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapLeft_side, x_l_side, y_l_side, null);
            bitmapLeft_side.recycle();

            //left tongue
            Bitmap bitmapLeft_tongue = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1456, 914, 751, 1149);
            Canvas canvasLeft_tongue = new Canvas(bitmapLeft_tongue);
            canvasLeft_tongue.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLeft_tongue.drawBitmap(bitmapDB_tongue, 0, 0, null);
            drawTextTongue(canvasLeft_tongue, "左");

            bitmapLeft_tongue = Bitmap.createScaledBitmap(bitmapLeft_tongue, widthTongue, heightTongue, true);
            canvasCombine.drawBitmap(bitmapLeft_tongue, x_l_tongue, y_l_tongue, null);
            bitmapLeft_tongue.recycle();

            //right main
            Bitmap bitmapRight_main = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 69, 914, 1387, 1577);
            Canvas canvasRight_main = new Canvas(bitmapRight_main);
            canvasRight_main.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRight_main.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasRight_main, "右");

            bitmapRight_main = Bitmap.createScaledBitmap(bitmapRight_main, widthMain, heightMain, true);
            canvasCombine.drawBitmap(bitmapRight_main, x_r_main, y_r_main, null);
            bitmapRight_main.recycle();

            //right tongue
            Bitmap bitmapRight_tongue = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1456, 914, 751, 1149);
            Canvas canvasright_tongue = new Canvas(bitmapRight_tongue);
            canvasright_tongue.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasright_tongue.drawBitmap(bitmapDB_tongue, 0, 0, null);
            drawTextTongue(canvasright_tongue, "右");

            bitmapRight_tongue = Bitmap.createScaledBitmap(bitmapRight_tongue, widthTongue, heightTongue, true);
            canvasCombine.drawBitmap(bitmapRight_tongue, x_r_tongue, y_r_tongue, null);
            bitmapRight_tongue.recycle();

            //right side
            Bitmap bitmapRight_side = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 1524, 914);
            matrix = new Matrix();
            matrix.postRotate(180, bitmapRight_side.getWidth() / 2, bitmapRight_side.getHeight() / 2);
            bitmapRight_side = Bitmap.createBitmap(bitmapRight_side, 0, 0, bitmapRight_side.getWidth(), bitmapRight_side.getHeight(), matrix, true);

            Canvas canvasRight_side = new Canvas(bitmapRight_side);
            canvasRight_side.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRight_side.drawBitmap(bitmapDB_side, 0, 0, null);

            drawTextSide(canvasRight_side, "右");

            bitmapRight_side = Bitmap.createScaledBitmap(bitmapRight_side, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapRight_side, x_r_side, y_r_side, null);
            bitmapRight_side.recycle();
        }

        //recycle
        bitmapDB_main.recycle();
        bitmapDB_tongue.recycle();
        bitmapDB_side1.recycle();
        bitmapDB_side2.recycle();
        bitmapDB_side.recycle();

        try {
            String printColor = orderItems.get(currentID).color.equals("黑") ? "B" : "W";
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
            Label label0 = new Label(0, currentID+1, orderItems.get(currentID).order_number+orderItems.get(currentID).sku+orderItems.get(currentID).size+printColor);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku+orderItems.get(currentID).size+printColor);
            sheet.addCell(label1);
            Number number2 = new Number(2, currentID+1, orderItems.get(currentID).num);
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
            Log.e("aaa", e.toString());
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
                widthMain = 1260;
                heightMain = 1402;
                widthSide = 1341;
                heightSide = 830;
                widthTongue = 682;
                heightTongue = 1012;

                x_l_main = 124;
                y_l_main = 175;
                x_r_main = 124;
                y_r_main = 1924;
                x_l_side = 1694;
                y_l_side = 90;
                x_r_side = 1689;
                y_r_side = 1095;
                x_l_tongue = 1579;
                y_l_tongue = 2351;
                x_r_tongue = 2465;
                y_r_tongue = 2350;
                break;
            case 37:
                widthMain = 1286;
                heightMain = 1437;
                widthSide = 1377;
                heightSide = 847;
                widthTongue = 695;
                heightTongue = 1041;

                x_l_main = 111;
                y_l_main = 157;
                x_r_main = 111;
                y_r_main = 1907;
                x_l_side = 1676;
                y_l_side = 81;
                x_r_side = 1671;
                y_r_side = 1085;
                x_l_tongue = 1573;
                y_l_tongue = 2336;
                x_r_tongue = 2459;
                y_r_tongue = 2336;
                break;
            case 38:
                widthMain = 1311;
                heightMain = 1473;
                widthSide = 1413;
                heightSide = 864;
                widthTongue = 709;
                heightTongue = 1068;

                x_l_main = 100;
                y_l_main = 138;
                x_r_main = 100;
                y_r_main = 1889;
                x_l_side = 1657;
                y_l_side = 72;
                x_r_side = 1653;
                y_r_side = 1076;
                x_l_tongue = 1566;
                y_l_tongue = 2323;
                x_r_tongue = 2452;
                y_r_tongue = 2323;
                break;
            case 39:
                widthMain = 1336;
                heightMain = 1508;
                widthSide = 1449;
                heightSide = 882;
                widthTongue = 723;
                heightTongue = 1095;

                x_l_main = 87;
                y_l_main = 121;
                x_r_main = 88;
                y_r_main = 1871;
                x_l_side = 1637;
                y_l_side = 63;
                x_r_side = 1635;
                y_r_side = 1067;
                x_l_tongue = 1558;
                y_l_tongue = 2309;
                x_r_tongue = 2443;
                y_r_tongue = 2309;
                break;
            case 40:
                widthMain = 1361;
                heightMain = 1544;
                widthSide = 1485;
                heightSide = 899;
                widthTongue = 736;
                heightTongue = 1123;

                x_l_main = 73;
                y_l_main = 103;
                x_r_main = 75;
                y_r_main = 1853;
                x_l_side = 1621;
                y_l_side = 56;
                x_r_side = 1617;
                y_r_side = 1058;
                x_l_tongue = 1550;
                y_l_tongue = 2295;
                x_r_tongue = 2436;
                y_r_tongue = 2295;
                break;
            case 41:
                widthMain = 1387;
                heightMain = 1577;
                widthSide = 1524;
                heightSide = 914;
                widthTongue = 751;
                heightTongue = 1149;

                x_l_main = 61;
                y_l_main = 85;
                x_r_main = 61;
                y_r_main = 1836;
                x_l_side = 1601;
                y_l_side = 47;
                x_r_side = 1599;
                y_r_side = 1050;
                x_l_tongue = 1543;
                y_l_tongue = 2281;
                x_r_tongue = 2429;
                y_r_tongue = 2281;
                break;
            case 42:
                widthMain = 1411;
                heightMain = 1613;
                widthSide = 1558;
                heightSide = 934;
                widthTongue = 764;
                heightTongue = 1177;

                x_l_main = 49;
                y_l_main = 68;
                x_r_main = 49;
                y_r_main = 1818;
                x_l_side = 1583;
                y_l_side = 37;
                x_r_side = 1584;
                y_r_side = 1041;
                x_l_tongue = 1536;
                y_l_tongue = 2268;
                x_r_tongue = 2424;
                y_r_tongue = 2268;
                break;
            case 43:
                widthMain = 1436;
                heightMain = 1647;
                widthSide = 1594;
                heightSide = 951;
                widthTongue = 777;
                heightTongue = 1205;

                x_l_main = 37;
                y_l_main = 51;
                x_r_main = 37;
                y_r_main = 1801;
                x_l_side = 1566;
                y_l_side = 29;
                x_r_side = 1563;
                y_r_side = 1033;
                x_l_tongue = 1530;
                y_l_tongue = 2254;
                x_r_tongue = 2417;
                y_r_tongue = 2254;
                break;
            case 44:
                widthMain = 1462;
                heightMain = 1681;
                widthSide = 1630;
                heightSide = 968;
                widthTongue = 791;
                heightTongue = 1232;

                x_l_main = 24;
                y_l_main = 34;
                x_r_main = 24;
                y_r_main = 1784;
                x_l_side = 1547;
                y_l_side = 21;
                x_r_side = 1547;
                y_r_side = 1025;
                x_l_tongue = 1524;
                y_l_tongue = 2241;
                x_r_tongue = 2410;
                y_r_tongue = 2241;
                break;
            case 45:
                widthMain = 1487;
                heightMain = 1716;
                widthSide = 1666;
                heightSide = 986;
                widthTongue = 805;
                heightTongue = 1259;

                x_l_main = 13;
                y_l_main = 17;
                x_r_main = 13;
                y_r_main = 1767;
                x_l_side = 1529;
                y_l_side = 12;
                x_r_side = 1527;
                y_r_side = 1015;
                x_l_tongue = 1517;
                y_l_tongue = 2227;
                x_r_tongue = 2403;
                y_r_tongue = 2227;
                break;
            case 46:
                widthMain = 1512;
                heightMain = 1750;
                widthSide = 1702;
                heightSide = 1003;
                widthTongue = 818;
                heightTongue = 1287;

                x_l_main = 0;
                y_l_main = 0;
                x_r_main = 0;
                y_r_main = 1750;
                x_l_side = 1511;
                y_l_side = 3;
                x_r_side = 1512;
                y_r_side = 1006;
                x_l_tongue = 1511;
                y_l_tongue = 2213;
                x_r_tongue = 2397;
                y_r_tongue = 2213;
                break;
        }
    }

    Bitmap rotateBitmap(Bitmap bm){
        Matrix matrix90 = new Matrix();
        matrix90.postRotate(90);
        matrix90.postTranslate(bm.getHeight(), 0);
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix90, true);
    }

}
