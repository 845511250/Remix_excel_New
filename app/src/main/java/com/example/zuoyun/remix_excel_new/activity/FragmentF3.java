package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

public class FragmentF3 extends BaseFragment {
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

    int width_side, height_side, width_tongue, height_tongue, width_back_up, height_back_up, width_back_below, height_back_below;
    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint rectPaint, paint, paintSmall, paintBlue,paintRectBlack;
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

        paintSmall = new Paint();
        paintSmall.setColor(0xffff0000);
        paintSmall.setTextSize(12);
        paintSmall.setAntiAlias(true);

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

    void drawTextSide(Canvas canvas, String LR) {
        canvas.drawRect(470, 585 - 25, 470 + 600, 585, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).sku + "_" + orderItems.get(currentID).size + "码" + orderItems.get(currentID).color + LR + " " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode_short, 470, 585 - 3, paint);
    }
    void drawTextTongue(Canvas canvasPart4L, String LR) {
        canvasPart4L.drawRect(430, 1269 - 25, 430 + 100, 1269, rectPaint);
        canvasPart4L.drawText(orderItems.get(currentID).size + orderItems.get(currentID).color + " " + LR, 430, 1269 - 3, paint);

        canvasPart4L.save();
        canvasPart4L.rotate(-57f, 809, 1141);
        canvasPart4L.drawRect(809, 1141 - 25, 809 + 200, 1141, rectPaint);
        canvasPart4L.drawText(orderItems.get(currentID).order_number, 809, 1141 - 3, paint);
        canvasPart4L.restore();
    }
    void drawTextBack_up(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(19.2f, 14, 61);
        canvas.drawRect(14, 61, 14 + 24, 61 + 10, rectPaint);
        canvas.drawText(LR, 16, 61 + 8, paintSmall);
        canvas.restore();
    }
    void drawTextBack_below(Canvas canvas, String LR) {
        canvas.drawRect(3, 373 - 25, 3 + 115, 373, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + orderItems.get(currentID).color + " " + LR, 3, 373 - 3, paint);
    }

    public void remixx(){
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(6567, 1578, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 8) {
            //left_inside
            Bitmap bitmapTemp = Bitmap.createBitmap(1510, 675, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 1484 - height_side, null);

            //left_outside
            bitmapTemp = Bitmap.createBitmap(1510, 675, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 1762 - width_side, 739 - height_side, null);

            //left_tongue
            bitmapTemp = Bitmap.createBitmap(941, 1334, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 4049 - width_tongue / 2, 0, null);

            //right
            //right_inside
            bitmapTemp = Bitmap.createBitmap(1510, 675, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 3524-width_side, 739 - height_side, null);

            //right_outside
            bitmapTemp = Bitmap.createBitmap(1510, 675, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(6), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 1762, 1484 - height_side, null);

            //right_tongue
            bitmapTemp = Bitmap.createBitmap(941, 1334, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(7), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 5125 - width_tongue / 2, 0, null);

            //back
            //L_back_up
            bitmapTemp = Bitmap.createBitmap(876, 281, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_back_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack_up(canvasTemp, "zuo");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_up, height_back_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6056 - width_back_up / 2 - 3, 0, null);

            //L_back_below
            bitmapTemp = Bitmap.createBitmap(121, 383, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -383, -172, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_back_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack_below(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_below, height_back_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 5881 - 3, 848, null);

            //right_back_up
            bitmapTemp = Bitmap.createBitmap(876, 281, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_back_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack_up(canvasTemp, "you");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_up, height_back_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6056 - width_back_up / 2 - 3, 372, null);

            //right_back_below
            bitmapTemp = Bitmap.createBitmap(121, 383, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), -383, -172, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_back_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack_below(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_below, height_back_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 6112 - 3, 848, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getHeight() == 2700) {
            //left_inside
            Bitmap bitmapTemp = Bitmap.createBitmap(1510, 675, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -13, -249, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 1484 - height_side, null);

            //left_outside
            bitmapTemp = Bitmap.createBitmap(1510, 675, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2476, -249, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 1762 - width_side, 739 - height_side, null);

            //left_tongue
            bitmapTemp = Bitmap.createBitmap(941, 1334, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1523, -16, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 4049 - width_tongue / 2, 0, null);

            //right
            //right_inside
            bitmapTemp = Bitmap.createBitmap(1510, 675, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2476, -1583, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 3524 - width_side, 739 - height_side, null);

            //right_outside
            bitmapTemp = Bitmap.createBitmap(1510, 675, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -13, -1583, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 1762, 1484 - height_side, null);

            //right_tongue
            bitmapTemp = Bitmap.createBitmap(941, 1334, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1523, -1350, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 5125 - width_tongue / 2, 0, null);

            //back
            //L_back_up
            bitmapTemp = Bitmap.createBitmap(876, 281, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -13, -924, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_back_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack_up(canvasTemp, "zuo");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_up, height_back_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6056 - width_back_up / 2 - 3, 0, null);

            //L_back_below
            bitmapTemp = Bitmap.createBitmap(121, 383, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -13 - 383, -924 - 172, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_back_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack_below(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_below, height_back_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 5881 - 3, 848, null);

            //right_back_up
            bitmapTemp = Bitmap.createBitmap(876, 281, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3110, -924, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_back_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack_up(canvasTemp, "you");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_up, height_back_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6056 - width_back_up / 2 - 3, 372, null);

            //right_back_below
            bitmapTemp = Bitmap.createBitmap(121, 383, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3110 - 383, -924 - 172, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_back_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack_below(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_below, height_back_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 6112 - 3, 848, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getHeight() == 4211) {
            //left_inside
            Bitmap bitmapTemp = Bitmap.createBitmap(1510, 675, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -118, -1006, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 1484 - height_side, null);

            //left_outside
            bitmapTemp = Bitmap.createBitmap(1510, 675, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2583, -1006, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 1762 - width_side, 739 - height_side, null);

            //left_tongue
            bitmapTemp = Bitmap.createBitmap(941, 1334, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1633, -770, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 4049 - width_tongue / 2, 0, null);

            //right
            //right_inside
            bitmapTemp = Bitmap.createBitmap(1510, 675, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2583, -2340, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 3524 - width_side, 739 - height_side, null);

            //right_outside
            bitmapTemp = Bitmap.createBitmap(1510, 675, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -118, -2340, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 1762, 1484 - height_side, null);

            //right_tongue
            bitmapTemp = Bitmap.createBitmap(941, 1334, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1633, -2106, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 5125 - width_tongue / 2, 0, null);

            //back
            //L_back_up
            bitmapTemp = Bitmap.createBitmap(876, 281, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -118, -1681, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_back_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack_up(canvasTemp, "zuo");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_up, height_back_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6056 - width_back_up / 2 - 3, 0, null);

            //L_back_below
            bitmapTemp = Bitmap.createBitmap(121, 383, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -118 - 383, -1681 - 172, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_back_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack_below(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_below, height_back_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 5881 - 3, 848, null);

            //right_back_up
            bitmapTemp = Bitmap.createBitmap(876, 281, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3217, -1681, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_back_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack_up(canvasTemp, "you");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_up, height_back_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6056 - width_back_up / 2 - 3, 372, null);

            //right_back_below
            bitmapTemp = Bitmap.createBitmap(121, 383, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3217 - 383, -1681 - 172, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_back_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack_below(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_below, height_back_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 6112 - 3, 848, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getHeight() == 4000) {
            //left_inside
            Bitmap bitmapTemp = Bitmap.createBitmap(1510, 675, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -258, -1291, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 1484 - height_side, null);

            //left_outside
            bitmapTemp = Bitmap.createBitmap(1510, 675, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -258, -547, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 1762 - width_side, 739 - height_side, null);

            //left_tongue
            bitmapTemp = Bitmap.createBitmap(941, 1334, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2790, -547, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 4049 - width_tongue / 2, 0, null);

            //right
            //right_inside
            bitmapTemp = Bitmap.createBitmap(1510, 675, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -258, -2034, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 3524 - width_side, 739 - height_side, null);

            //right_outside
            bitmapTemp = Bitmap.createBitmap(1510, 675, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -259, -2778, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 1762, 1484 - height_side, null);

            //right_tongue
            bitmapTemp = Bitmap.createBitmap(941, 1334, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2790, -2034, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 5125 - width_tongue / 2, 0, null);

            //back
            //L_back_up
            bitmapTemp = Bitmap.createBitmap(876, 281, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1853, -547, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_back_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack_up(canvasTemp, "zuo");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_up, height_back_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6056 - width_back_up / 2 - 3, 0, null);

            //L_back_below
            bitmapTemp = Bitmap.createBitmap(121, 383, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1853 - 383, -547 - 172, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_back_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack_below(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_below, height_back_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 5881 - 3, 848, null);

            //right_back_up
            bitmapTemp = Bitmap.createBitmap(876, 281, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1853, -2034, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_back_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack_up(canvasTemp, "you");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_up, height_back_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6056 - width_back_up / 2 - 3, 372, null);

            //right_back_below
            bitmapTemp = Bitmap.createBitmap(121, 383, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1853 - 383, -2034 - 172, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f3_back_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack_below(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_below, height_back_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 6112 - 3, 848, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

        }

        try {
            String nameCombine = orderItems.get(currentID).sku + "_" + orderItems.get(currentID).size + "_" + orderItems.get(currentID).color + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

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

            String colorStr = orderItems.get(currentID).color.equals("黑") ? "B" : "W";

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
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + orderItems.get(currentID).size + colorStr);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + orderItems.get(currentID).size + colorStr);
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
            case 36:
                width_side = 1332;
                height_side = 624;
                width_tongue = 882;
                height_tongue = 1185;
                width_back_up = 795;
                height_back_up = 262;
                width_back_below = 122;
                height_back_below = 363;
                break;
            case 37:
                width_side = 1367;
                height_side = 634;
                width_tongue = 896;
                height_tongue = 1214;
                width_back_up = 795;
                height_back_up = 262;
                width_back_below = 122;
                height_back_below = 363;
                break;
            case 38:
                width_side = 1404;
                height_side = 645;
                width_tongue = 910;
                height_tongue = 1245;
                width_back_up = 835;
                height_back_up = 272;
                width_back_below = 122;
                height_back_below = 363;
                break;
            case 39:
                width_side = 1439;
                height_side = 655;
                width_tongue = 925;
                height_tongue = 1275;
                width_back_up = 835;
                height_back_up = 272;
                width_back_below = 122;
                height_back_below = 383;
                break;
            case 40:
                width_side = 1477;
                height_side = 665;
                width_tongue = 939;
                height_tongue = 1306;
                width_back_up = 876;
                height_back_up = 281;
                width_back_below = 122;
                height_back_below = 383;
                break;
            case 41:
                width_side = 1511;
                height_side = 676;
                width_tongue = 953;
                height_tongue = 1336;
                width_back_up = 876;
                height_back_up = 281;
                width_back_below = 122;
                height_back_below = 383;
                break;
            case 42:
                width_side = 1546;
                height_side = 684;
                width_tongue = 967;
                height_tongue = 1365;
                width_back_up = 918;
                height_back_up = 294;
                width_back_below = 122;
                height_back_below = 401;
                break;
            case 43:
                width_side = 1583;
                height_side = 694;
                width_tongue = 981;
                height_tongue = 1395;
                width_back_up = 918;
                height_back_up = 294;
                width_back_below = 122;
                height_back_below = 401;
                break;
            case 44:
                width_side = 1619;
                height_side = 704;
                width_tongue = 994;
                height_tongue = 1424;
                width_back_up = 959;
                height_back_up = 303;
                width_back_below = 122;
                height_back_below = 401;
                break;
            case 45:
                width_side = 1654;
                height_side = 714;
                width_tongue = 1008;
                height_tongue = 1453;
                width_back_up = 959;
                height_back_up = 303;
                width_back_below = 122;
                height_back_below = 415;
                break;
            case 46:
                width_side = 1690;
                height_side = 722;
                width_tongue = 1023;
                height_tongue = 1485;
                width_back_up = 980;
                height_back_up = 309;
                width_back_below = 122;
                height_back_below = 415;
                break;
            case 47:
                width_side = 1726;
                height_side = 735;
                width_tongue = 1038;
                height_tongue = 1515;
                width_back_up = 980;
                height_back_up = 309;
                width_back_below = 122;
                height_back_below = 415;
                break;
            case 48:
                width_side = 1762;
                height_side = 743;
                width_tongue = 1052;
                height_tongue = 1545;
                width_back_up = 1021;
                height_back_up = 319;
                width_back_below = 122;
                height_back_below = 428;
                break;
        }
        width_back_up += 6;
        height_back_up += 6;
        width_back_below += 6;
    }
}
