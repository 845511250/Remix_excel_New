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

public class FragmentLH extends BaseFragment {
    Context context;
//    String sdCardPath = "/mnt/asec/share";
String sdCardPath = "/storage/emulated/0/Pictures";
    ArrayList<OrderItem> orderItems;
    int currentID;
    String childPath;

    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.iv_right)
    ImageView iv_right;
    @BindView(R.id.bt_remix)
    Button bt_remix;

    Paint paint,paintRed, rectPaint, paintSmall,paintRectBlack;
    String time;

    int width_back1,width_back2,width_front,width_side1,width_side2_up,width_side2_below,width_tongue_up,width_tongue_below;
    int height_back1,height_back2,height_front,height_side1,height_side2_up,height_side2_below,height_tongue_up,height_tongue_below;

    int num;
    String strPlus = "";
    int intPlus = 1;

    @Override
    public int getLayout() {
        return R.layout.fragment_dff;
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

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(26);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(26);
        paintRed.setAntiAlias(true);

        paintSmall = new Paint();
        paintSmall.setColor(0xff0000ff);
        paintSmall.setTextSize(24);
        paintSmall.setAntiAlias(true);

        paintRectBlack = new Paint();
        paintRectBlack.setColor(0xff000000);
        paintRectBlack.setStyle(Paint.Style.FILL);

        time = MainActivity.instance.orderDate_Print;

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_left.setImageDrawable(null);
                    iv_right.setImageDrawable(null);
                    Log.e("fragment2", "message0");
                } else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
                    if(!MainActivity.instance.cb_fastmode.isChecked())
                        iv_right.setImageBitmap(MainActivity.instance.bitmaps.get(0));
                    checkremix();
                } else if (message == 3) {
                    bt_remix.setClickable(false);
                } else if(message==MainActivity.LOADED_IMGS){
                    checkremix();
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
                            intPlus += orderItems.get(i).num;
                        }
                    }
                    strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                    remixx();
                }
            }
        }.start();

    }


    void drawTextBack1(Canvas canvas, String LR) {
        canvas.drawRect(592, 296 - 26, 592 + 450, 296, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + "码" + orderItems.get(currentID).color + LR + "  " + time + "  " + orderItems.get(currentID).order_number, 592, 296 - 3, paint);
        canvas.drawRect(592, 335 - 26, 592 + 250, 335, rectPaint);
        canvas.drawText(orderItems.get(currentID).newCode, 592, 335 - 2, paintRed);
    }
    void drawTextBack2(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(11f, 678, 308);
        canvas.drawRect(678, 308 - 23, 678 + 280, 308, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + "码" + orderItems.get(currentID).color + LR + " " + orderItems.get(currentID).order_number, 678, 308 - 2, paint);
        canvas.restore();
    }
    void drawTextfront(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(-10.6f, 74, 103);
        canvas.drawRect(74, 103, 74 + 300, 103 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + "码" + orderItems.get(currentID).color + LR + " " + orderItems.get(currentID).order_number, 74, 103 + 22, paint);
        canvas.restore();
    }
    void drawTextSide1(Canvas canvas, String LR) {
        canvas.drawRect(350, 580 - 25, 350 + 550, 580, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + "码" + orderItems.get(currentID).color + LR + "  " + time + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode_short, 350, 580 - 3, paint);
    }
    void drawTextSide2Up_L(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(71.4f, 21, 423);
        canvas.drawRect(21, 423 - 25, 21 + 92, 423, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + " " + LR, 21, 423 - 3, paintRed);
        canvas.restore();
    }
    void drawTextSide2Up_R(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(-72.2f, 532, 509);
        canvas.drawRect(532, 509 - 25, 532 + 92, 509, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + " " + LR, 532, 509 - 3, paintRed);
        canvas.restore();
    }
    void drawTextSide2Below_L(Canvas canvas, String LR) {
        canvas.drawRect(268, 475 - 25, 268 + 100, 475, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + " " + LR, 268, 475 - 3, paintRed);
    }
    void drawTextSide2Below_R(Canvas canvas, String LR) {
        canvas.drawRect(57, 475 - 25, 57 + 100, 475, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + " " + LR, 57, 475 - 3, paintRed);
    }
    void drawTextTongueUp(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(-8.6f, 185, 1046);
        canvas.drawRect(185, 1046 - 25, 185 + 100, 1046, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + " " + LR, 185, 1046 - 3, paintRed);
        canvas.restore();
    }
    void drawTextTongueBelow(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(-57.4f, 130, 94);
        canvas.drawRect(130, 94 - 25, 130 + 86, 94, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + LR, 130, 94 - 3, paintRed);
        canvas.restore();
    }

    public void remixx(){
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(6827, 2691, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 14) {
            //back1
            //left
            Bitmap bitmapTemp = Bitmap.createBitmap(1535, 453, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(8), 0, 0, null);
            Bitmap bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_back1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back1, height_back1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4353 - width_back1 / 2, 727, null);

            //right
            bitmapTemp = Bitmap.createBitmap(1535, 453, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(11), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back1, height_back1, true);
            canvasCombine.drawBitmap(bitmapTemp, 908 - width_back1 / 2, 727, null);

            //back2
            //left
            bitmapTemp = Bitmap.createBitmap(1297, 369, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_back2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack2(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back2, height_back2, true);
            canvasCombine.drawBitmap(bitmapTemp, 4353 - width_back2 / 2, 1331, null);

            //right
            bitmapTemp = Bitmap.createBitmap(1297, 369, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack2(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back2, height_back2, true);
            canvasCombine.drawBitmap(bitmapTemp, 908 - width_back2 / 2, 1331, null);

            //front
            //left
            bitmapTemp = Bitmap.createBitmap(1593, 666, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(6), 0, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextfront(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 4353 - width_front / 2, 1821, null);

            //right
            bitmapTemp = Bitmap.createBitmap(1593, 666, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(7), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextfront(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 908 - width_front / 2, 1821, null);

            //side1
            //LL
            bitmapTemp = Bitmap.createBitmap(1215, 594, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4858 - width_side1, 668 - height_side1, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(1215, 594, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4918, 668 - height_side1, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(1215, 594, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 1413 - width_side1, 668 - height_side1, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(1215, 594, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 1473, 668 - height_side1, null);

            //side2_up
            //LL
            bitmapTemp = Bitmap.createBitmap(581, 530, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(10), -212, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_L(canvasTemp,  "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6827 - width_side2_up, 2613 - height_side2_up, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(581, 530, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(9), 0, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_R(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6157, 1331 - height_side2_up, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(581, 530, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(12), -212, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_L(canvasTemp,"右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 3382 - width_side2_up, 2613 - height_side2_up, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(581, 530, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(13), 0, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_R(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2712, 1331 - height_side2_up, null);

            //side2_below
            //LL
            bitmapTemp = Bitmap.createBitmap(430, 485, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(10), 0, -390, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_L(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 6157, 1927 - height_side2_below, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(430, 485, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(9), -363, -390, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_R(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 6827 - width_side2_below, 668 - height_side2_below, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(430, 485, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(12), 0, -390, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_L(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2712, 1927 - height_side2_below, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(430, 485, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(13), -363, -390, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_R(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 3382 - width_side2_below, 668 - height_side2_below, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 16) {
            //back1
            //left
            Bitmap bitmapTemp = Bitmap.createBitmap(1535, 453, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(10), 0, 0, null);
            Bitmap bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_back1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back1, height_back1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4353 - width_back1 / 2, 727, null);

            //right
            bitmapTemp = Bitmap.createBitmap(1535, 453, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(13), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back1, height_back1, true);
            canvasCombine.drawBitmap(bitmapTemp, 908 - width_back1 / 2, 727, null);

            //back2
            //left
            bitmapTemp = Bitmap.createBitmap(1297, 369, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_back2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack2(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back2, height_back2, true);
            canvasCombine.drawBitmap(bitmapTemp, 4353 - width_back2 / 2, 1331, null);

            //right
            bitmapTemp = Bitmap.createBitmap(1297, 369, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack2(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back2, height_back2, true);
            canvasCombine.drawBitmap(bitmapTemp, 908 - width_back2 / 2, 1331, null);

            //front
            //left
            bitmapTemp = Bitmap.createBitmap(1593, 666, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(6), 0, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextfront(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 4353 - width_front / 2, 1821, null);

            //right
            bitmapTemp = Bitmap.createBitmap(1593, 666, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(8), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextfront(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 908 - width_front / 2, 1821, null);

            //side1
            //LL
            bitmapTemp = Bitmap.createBitmap(1215, 594, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4858 - width_side1, 668 - height_side1, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(1215, 594, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4918, 668 - height_side1, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(1215, 594, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 1413 - width_side1, 668 - height_side1, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(1215, 594, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 1473, 668 - height_side1, null);

            //side2_up
            //LL
            bitmapTemp = Bitmap.createBitmap(581, 530, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(12), -212, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_L(canvasTemp,  "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6827 - width_side2_up, 2613 - height_side2_up, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(581, 530, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(11), 0, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_R(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6157, 1331 - height_side2_up, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(581, 530, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(14), -212, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_L(canvasTemp,"右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 3382 - width_side2_up, 2613 - height_side2_up, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(581, 530, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(15), 0, 0, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_R(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2712, 1331 - height_side2_up, null);

            //side2_below
            //LL
            bitmapTemp = Bitmap.createBitmap(430, 485, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(12), 0, -390, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_L(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 6157, 1927 - height_side2_below, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(430, 485, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(11), -363, -390, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_R(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 6827 - width_side2_below, 668 - height_side2_below, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(430, 485, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(14), 0, -390, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_L(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2712, 1927 - height_side2_below, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(430, 485, Bitmap.Config.ARGB_8888);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(15), -363, -390, null);
            bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_R(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 3382 - width_side2_below, 668 - height_side2_below, null);

            if (orderItems.get(currentID).imgs.size() == 16) {
                //tongue_left
                //up
                bitmapTemp = Bitmap.createBitmap(683, 1058, Bitmap.Config.ARGB_8888);
                canvasTemp= new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawColor(0xffffffff);
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(7), 0, 0, null);
                bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_tongue_up);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextTongueUp(canvasTemp, "左");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
                canvasCombine.drawBitmap(bitmapTemp, 5710 - width_tongue_up / 2, 727, null);
                //below
                bitmapTemp = Bitmap.createBitmap(649, 557, Bitmap.Config.ARGB_8888);
                canvasTemp= new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawColor(0xffffffff);
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(7), -15, -992, null);
                bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_tongue_below);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextTongueBelow(canvasTemp, "左");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
                canvasCombine.drawBitmap(bitmapTemp, 5710 - width_tongue_below / 2, 2020, null);

                //tongue_right
                //up
                bitmapTemp = Bitmap.createBitmap(683, 1058, Bitmap.Config.ARGB_8888);
                canvasTemp= new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawColor(0xffffffff);
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(9), 0, 0, null);
                bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_tongue_up);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextTongueUp(canvasTemp, "右");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
                canvasCombine.drawBitmap(bitmapTemp, 2265 - width_tongue_up / 2, 727, null);
                //below
                bitmapTemp = Bitmap.createBitmap(649, 557, Bitmap.Config.ARGB_8888);
                canvasTemp= new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawColor(0xffffffff);
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(9), -15, -992, null);
                bitmapDB= BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_tongue_below);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextTongueBelow(canvasTemp, "右");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
                canvasCombine.drawBitmap(bitmapTemp, 2265 - width_tongue_below / 2, 2020, null);
            }

            bitmapTemp.recycle();
            bitmapDB.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 3500) {
            //back1
            //left
            Bitmap bitmapTemp = Bitmap.createBitmap(1535, 453, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -983, -32, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_back1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back1, height_back1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4353 - width_back1 / 2, 727, null);

            //right
            bitmapTemp = Bitmap.createBitmap(1535, 453, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -983, -2433, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back1, height_back1, true);
            canvasCombine.drawBitmap(bitmapTemp, 908 - width_back1 / 2, 727, null);

            //back2
            //left
            bitmapTemp = Bitmap.createBitmap(1297, 369, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1102, -112, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_back2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack2(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back2, height_back2, true);
            canvasCombine.drawBitmap(bitmapTemp, 4353 - width_back2 / 2, 1331, null);

            //right
            bitmapTemp = Bitmap.createBitmap(1297, 369, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1103, -2513, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack2(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back2, height_back2, true);
            canvasCombine.drawBitmap(bitmapTemp, 908 - width_back2 / 2, 1331, null);

            //front
            //left
            bitmapTemp = Bitmap.createBitmap(1593, 666, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -953, -1701, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextfront(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 4353 - width_front / 2, 1821, null);

            //right
            bitmapTemp = Bitmap.createBitmap(1593, 666, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -954, -4102, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextfront(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 908 - width_front / 2, 1821, null);

            //side1
            //LL
            bitmapTemp = Bitmap.createBitmap(1215, 594, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2248, -769, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4858 - width_side1, 668 - height_side1, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(1215, 594, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -36, -769, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4918, 668 - height_side1, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(1215, 594, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2249, -3170, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 1413 - width_side1, 668 - height_side1, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(1215, 594, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -37, -3170, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 1473, 668 - height_side1, null);

            //side2_up
            //LL
            bitmapTemp = Bitmap.createBitmap(581, 530, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2303, -485, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_L(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6827 - width_side2_up, 2613 - height_side2_up, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(581, 530, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -615, -485, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_R(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6157, 1331 - height_side2_up, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(581, 530, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2303, -2886, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_L(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 3382 - width_side2_up, 2613 - height_side2_up, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(581, 530, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -616, -2886, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_R(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2712, 1331 - height_side2_up, null);

            //side2_below
            //LL
            bitmapTemp = Bitmap.createBitmap(430, 485, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2091, -875, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_L(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 6157, 1927 - height_side2_below, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(430, 485, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -978, -875, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_R(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 6827 - width_side2_below, 668 - height_side2_below, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(430, 485, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2092, -3276, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_L(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2712, 1927 - height_side2_below, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(430, 485, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -979, -3276, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_R(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 3382 - width_side2_below, 668 - height_side2_below, null);

            //tongue_left
            //up
            bitmapTemp = Bitmap.createBitmap(683, 1058, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1408, -485, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_tongue_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueUp(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 5710 - width_tongue_up / 2, 727, null);
            //below
            bitmapTemp = Bitmap.createBitmap(649, 557, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1408 - 15, -485 - 992, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_tongue_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueBelow(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 5710 - width_tongue_below / 2, 2020, null);

            //tongue_right
            //up
            bitmapTemp = Bitmap.createBitmap(683, 1058, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1409, -2886, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_tongue_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueUp(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2265 - width_tongue_up / 2, 727, null);
            //below
            bitmapTemp = Bitmap.createBitmap(649, 557, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1409 - 15, -2886 - 992, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_tongue_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueBelow(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2265 - width_tongue_below / 2, 2020, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 4500) {
            //back1
            //left
            Bitmap bitmapTemp = Bitmap.createBitmap(1535, 453, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2915, -50, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_back1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back1, height_back1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4353 - width_back1 / 2, 727, null);

            //right
            bitmapTemp = Bitmap.createBitmap(1535, 453, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2915, -742, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back1, height_back1, true);
            canvasCombine.drawBitmap(bitmapTemp, 908 - width_back1 / 2, 727, null);

            //back2
            //left
            bitmapTemp = Bitmap.createBitmap(1297, 369, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1454, -50, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_back2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack2(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back2, height_back2, true);
            canvasCombine.drawBitmap(bitmapTemp, 4353 - width_back2 / 2, 1331, null);

            //right
            bitmapTemp = Bitmap.createBitmap(1297, 369, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1454, -742, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack2(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back2, height_back2, true);
            canvasCombine.drawBitmap(bitmapTemp, 908 - width_back2 / 2, 1331, null);

            //front
            //left
            bitmapTemp = Bitmap.createBitmap(1593, 666, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2857, -1434, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextfront(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 4353 - width_front / 2, 1821, null);

            //right
            bitmapTemp = Bitmap.createBitmap(1593, 666, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2857, -2340, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextfront(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 908 - width_front / 2, 1821, null);

            //side1
            //LL
            bitmapTemp = Bitmap.createBitmap(1215, 594, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -50, -50, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4858 - width_side1, 668 - height_side1, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(1215, 594, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -50, -837, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4918, 668 - height_side1, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(1215, 594, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -50, -1625, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 1413 - width_side1, 668 - height_side1, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(1215, 594, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -50, -2412, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 1473, 668 - height_side1, null);

            //side2_up
            //LL
            bitmapTemp = Bitmap.createBitmap(581, 530, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -50, -3259, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_L(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6827 - width_side2_up, 2613 - height_side2_up, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(581, 530, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1326, -3259, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_R(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6157, 1331 - height_side2_up, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(581, 530, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2599, -3259, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_L(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 3382 - width_side2_up, 2613 - height_side2_up, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(581, 530, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3872, -3259, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_R(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2712, 1331 - height_side2_up, null);

            //side2_below
            //LL
            bitmapTemp = Bitmap.createBitmap(430, 485, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -50, -3969, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_L(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 6157, 1927 - height_side2_below, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(430, 485, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1377, -3969, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_R(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 6827 - width_side2_below, 668 - height_side2_below, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(430, 485, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2700, -3969, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_L(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2712, 1927 - height_side2_below, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(430, 485, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4025, -3969, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_R(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 3382 - width_side2_below, 668 - height_side2_below, null);

            //tongue_left
            //up
            bitmapTemp = Bitmap.createBitmap(683, 1058, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2084, -1457, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_tongue_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueUp(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 5710 - width_tongue_up / 2, 727, null);
            //below
            bitmapTemp = Bitmap.createBitmap(649, 557, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2084 - 15, -1457 - 992, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_tongue_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueBelow(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 5710 - width_tongue_below / 2, 2020, null);

            //tongue_right
            //up
            bitmapTemp = Bitmap.createBitmap(683, 1058, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1356, -1457, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_tongue_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueUp(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2265 - width_tongue_up / 2, 727, null);
            //below
            bitmapTemp = Bitmap.createBitmap(649, 557, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1356 - 15, -1457 - 992, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_tongue_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueBelow(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2265 - width_tongue_below / 2, 2020, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
            if(MainActivity.instance.bitmaps.get(0).getWidth() == 4000){
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 7017, 7017, true));
            }
            //back1
            //left
            Bitmap bitmapTemp = Bitmap.createBitmap(1535, 453, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4451, -2078, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_back1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back1, height_back1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4353 - width_back1 / 2, 727, null);

            //right
            bitmapTemp = Bitmap.createBitmap(1535, 453, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1011, -2078, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back1, height_back1, true);
            canvasCombine.drawBitmap(bitmapTemp, 908 - width_back1 / 2, 727, null);

            //back2
            //left
            bitmapTemp = Bitmap.createBitmap(1297, 369, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4566, -1515, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_back2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack2(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back2, height_back2, true);
            canvasCombine.drawBitmap(bitmapTemp, 4353 - width_back2 / 2, 1331, null);

            //right
            bitmapTemp = Bitmap.createBitmap(1297, 369, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1127, -1515, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack2(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back2, height_back2, true);
            canvasCombine.drawBitmap(bitmapTemp, 908 - width_back2 / 2, 1331, null);

            //front
            //left
            bitmapTemp = Bitmap.createBitmap(1593, 666, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4421, -4835, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextfront(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 4353 - width_front / 2, 1821, null);

            //right
            bitmapTemp = Bitmap.createBitmap(1593, 666, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -982, -4835, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextfront(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 908 - width_front / 2, 1821, null);

            //side1
            //LL
            Matrix matrix = new Matrix();
            matrix.setRotate(90);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5980, 2789, 594, 1215, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4858 - width_side1, 668 - height_side1, null);

            //LR
            matrix.setRotate(-90);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3856, 2789, 594, 1215, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4918, 668 - height_side1, null);

            //RL
            matrix.setRotate(90);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2539, 2789, 594, 1215, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 1413 - width_side1, 668 - height_side1, null);

            //RR
            matrix.setRotate(-90);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 418, 2789, 594, 1215, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side1_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side1, height_side1, true);
            canvasCombine.drawBitmap(bitmapTemp, 1473, 668 - height_side1, null);

            //side2_up
            //LL
            matrix.setRotate(90);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5696, 4162, 530, 581, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_L(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6827 - width_side2_up, 2613 - height_side2_up, null);

            //LR
            matrix.setRotate(-90);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4204, 4162, 530, 581, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_R(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 6157, 1331 - height_side2_up, null);

            //RL
            matrix.setRotate(90);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2258, 4162, 530, 581, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_L(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 3382 - width_side2_up, 2613 - height_side2_up, null);

            //RR
            matrix.setRotate(-90);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 765, 4162, 530, 581, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Up_R(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_up, height_side2_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2712, 1331 - height_side2_up, null);

            //side2_below
            //LL
            matrix.setRotate(90);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6360, 4252, 485, 430, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_L(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 6157, 1927 - height_side2_below, null);

            //LR
            matrix.setRotate(-90);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3615, 4252, 485, 430, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_R(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 6827 - width_side2_below, 668 - height_side2_below, null);

            //RL
            matrix.setRotate(90);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2920, 4252, 485, 430, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_L(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2712, 1927 - height_side2_below, null);

            //RR
            matrix.setRotate(-90);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 175, 4252, 485, 430, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_side2_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Below_R(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2_below, height_side2_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 3382 - width_side2_below, 668 - height_side2_below, null);

            //tongue_left
            //up
            bitmapTemp = Bitmap.createBitmap(683, 1058, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4874, -2752, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_tongue_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueUp(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 5710 - width_tongue_up / 2, 727, null);
            //below
            bitmapTemp = Bitmap.createBitmap(649, 557, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4874 - 15, -2752 - 992, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_tongue_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueBelow(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 5710 - width_tongue_below / 2, 2020, null);

            //tongue_right
            //up
            bitmapTemp = Bitmap.createBitmap(683, 1058, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1435, -2752, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_tongue_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueUp(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2265 - width_tongue_up / 2, 727, null);
            //below
            bitmapTemp = Bitmap.createBitmap(649, 557, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1435 - 15, -2752 - 992, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lh_tongue_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueBelow(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2265 - width_tongue_below / 2, 2020, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

        }

        try {
            String printColor = orderItems.get(currentID).color.equals("黑") ? "B" : "W";
            String nameCombine = orderItems.get(currentID).sku + orderItems.get(currentID).size + "_" + orderItems.get(currentID).color + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

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
            Label label0 = new Label(0, currentID+1, orderItems.get(currentID).order_number+orderItems.get(currentID).sku+orderItems.get(currentID).size+printColor);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku+orderItems.get(currentID).size+printColor);
            sheet.addCell(label1);
            Number number2 = new Number(2, currentID+1, orderItems.get(currentID).num);
            sheet.addCell(number2);
            Label label3 = new Label(3, currentID+1, orderItems.get(currentID).customer);
            sheet.addCell(label3);
            Label label4 = new Label(4, currentID+1, MainActivity.instance.orderDate_Excel);
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
        if (MainActivity.instance.tb_auto.isChecked())
            remix();
    }

    void setSize(int size){
        switch (size) {
            case 36:
                width_back1 = 1356;
                height_back1 = 421;
                width_back2 = 1172;
                height_back2 = 348;
                width_front = 1435;
                height_front = 597;
                width_side1 = 1072;
                height_side1 = 543;
                width_side2_up = 504;
                height_side2_up = 500;
                width_side2_below = 378;
                height_side2_below = 451;
                width_tongue_up = 628;
                height_tongue_up = 942;
                width_tongue_below = 603;
                height_tongue_below = 502;
                break;
            case 37:
                width_back1 = 1391;
                height_back1 = 427;
                width_back2 = 1172;
                height_back2 = 348;
                width_front = 1468;
                height_front = 614;
                width_side1 = 1100;
                height_side1 = 554;
                width_side2_up = 526;
                height_side2_up = 500;
                width_side2_below = 378;
                height_side2_below = 451;
                width_tongue_up = 640;
                height_tongue_up = 966;
                width_tongue_below = 612;
                height_tongue_below = 512;
                break;
            case 38:
                width_back1 = 1427;
                height_back1 = 434;
                width_back2 = 1229;
                height_back2 = 358;
                width_front = 1499;
                height_front = 629;
                width_side1 = 1128;
                height_side1 = 564;
                width_side2_up = 537;
                height_side2_up = 510;
                width_side2_below = 405;
                height_side2_below = 472;
                width_tongue_up = 649;
                height_tongue_up = 988;
                width_tongue_below = 621;
                height_tongue_below = 522;
                break;
            case 39:
                width_back1 = 1463;
                height_back1 = 440;
                width_back2 = 1229;
                height_back2 = 358;
                width_front = 1532;
                height_front = 641;
                width_side1 = 1155;
                height_side1 = 574;
                width_side2_up = 556;
                height_side2_up = 515;
                width_side2_below = 405;
                height_side2_below = 472;
                width_tongue_up = 661;
                height_tongue_up = 1012;
                width_tongue_below = 629;
                height_tongue_below = 534;
                break;
            case 40:
                width_back1 = 1498;
                height_back1 = 447;
                width_back2 = 1284;
                height_back2 = 366;
                width_front = 1563;
                height_front = 645;
                width_side1 = 1183;
                height_side1 = 585;
                width_side2_up = 585;
                height_side2_up = 523;
                width_side2_below = 420;
                height_side2_below = 484;
                width_tongue_up = 671;
                height_tongue_up = 1036;
                width_tongue_below = 639;
                height_tongue_below = 548;
                break;
            case 41:
                width_back1 = 1535;
                height_back1 = 453;
                width_back2 = 1284;
                height_back2 = 366;
                width_front = 1593;
                height_front = 666;
                width_side1 = 1215;
                height_side1 = 594;
                width_side2_up = 582;
                height_side2_up = 530;
                width_side2_below = 420;
                height_side2_below = 484;
                width_tongue_up = 683;
                height_tongue_up = 1058;
                width_tongue_below = 649;
                height_tongue_below = 557;
                break;
            case 42:
                width_back1 = 1570;
                height_back1 = 460;
                width_back2 = 1341;
                height_back2 = 378;
                width_front = 1626;
                height_front = 682;
                width_side1 = 1242;
                height_side1 = 605;
                width_side2_up = 610;
                height_side2_up = 542;
                width_side2_below = 446;
                height_side2_below = 503;
                width_tongue_up = 694;
                height_tongue_up = 1083;
                width_tongue_below = 657;
                height_tongue_below = 569;
                break;
            case 43:
                width_back1 = 1606;
                height_back1 = 465;
                width_back2 = 1341;
                height_back2 = 378;
                width_front = 1657;
                height_front = 695;
                width_side1 = 1266;
                height_side1 = 616;
                width_side2_up = 624;
                height_side2_up = 532;
                width_side2_below = 446;
                height_side2_below = 503;
                width_tongue_up = 704;
                height_tongue_up = 1107;
                width_tongue_below = 667;
                height_tongue_below = 580;
                break;
            case 44:
                width_back1 = 1641;
                height_back1 = 472;
                width_back2 = 1396;
                height_back2 = 385;
                width_front = 1688;
                height_front = 710;
                width_side1 = 1298;
                height_side1 = 626;
                width_side2_up = 615;
                height_side2_up = 566;
                width_side2_below = 464;
                height_side2_below = 519;
                width_tongue_up = 717;
                height_tongue_up = 1129;
                width_tongue_below = 676;
                height_tongue_below = 593;
                break;
            case 45:
                width_back1 = 1677;
                height_back1 = 479;
                width_back2 = 1396;
                height_back2 = 385;
                width_front = 1719;
                height_front = 722;
                width_side1 = 1322;
                height_side1 = 637;
                width_side2_up = 640;
                height_side2_up = 559;
                width_side2_below = 464;
                height_side2_below = 519;
                width_tongue_up = 727;
                height_tongue_up = 1154;
                width_tongue_below = 685;
                height_tongue_below = 605;
                break;
            case 46:
                width_back1 = 1713;
                height_back1 = 485;
                width_back2 = 1438;
                height_back2 = 393;
                width_front = 1750;
                height_front = 737;
                width_side1 = 1355;
                height_side1 = 645;
                width_side2_up = 646;
                height_side2_up = 576;
                width_side2_below = 475;
                height_side2_below = 527;
                width_tongue_up = 738;
                height_tongue_up = 1177;
                width_tongue_below = 694;
                height_tongue_below = 617;
                break;
            case 47:
                width_back1 = 1747;
                height_back1 = 491;
                width_back2 = 1481;
                height_back2 = 399;
                width_front = 1781;
                height_front = 762;
                width_side1 = 1385;
                height_side1 = 656;
                width_side2_up = 658;
                height_side2_up = 585;
                width_side2_below = 488;
                height_side2_below = 538;
                width_tongue_up = 748;
                height_tongue_up = 1201;
                width_tongue_below = 703;
                height_tongue_below = 628;
                break;
            case 48:
                width_back1 = 1780;
                height_back1 = 499;
                width_back2 = 1481;
                height_back2 = 399;
                width_front = 1812;
                height_front = 780;
                width_side1 = 1411;
                height_side1 = 666;
                width_side2_up = 668;
                height_side2_up = 595;
                width_side2_below = 494;
                height_side2_below = 539;
                width_tongue_up = 759;
                height_tongue_up = 1225;
                width_tongue_below = 712;
                height_tongue_below = 640;
                break;
        }

        width_back1 += 5;
        height_back1 += 5;
        width_back2 += 5;
        height_back2 += 5;
        width_front += 5;
        height_front += 5;
        width_side1 += 5;
        height_side1 += 5;
        width_side2_up += 5;
        height_side2_up += 5;
        width_side2_below += 5;
        height_side2_below += 5;
        width_tongue_up += 5;
        height_tongue_up += 5;
        width_tongue_below += 5;
        height_tongue_below += 5;
    }


}
