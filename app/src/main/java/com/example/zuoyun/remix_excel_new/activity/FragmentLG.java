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

public class FragmentLG extends BaseFragment {
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

    int width_side, height_side, width_tongue_up, height_tongue_up,width_tongue_below, height_tongue_below, width_side_up, height_side_up, width_side_below, height_side_below, width_front, height_front, width_back, height_back;
    float angle_side_below_l;
    int margin_side_below_l;

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


    void drawTextBack(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(50.7f, 167, 36);
        canvas.drawRect(167, 36 - 25, 167 + 70, 36, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + LR, 167, 36 - 3, paint);
        canvas.restore();
    }
    void drawTextFront(Canvas canvasPart4L, String LR){
        canvasPart4L.drawRect(700, 600 - 25, 700 + 100, 600, rectPaint);
        canvasPart4L.drawText(orderItems.get(currentID).size + " " + LR, 700, 600 - 3, paint);

        canvasPart4L.save();
        canvasPart4L.rotate(-37.1f, 1170, 483);
        canvasPart4L.drawRect(1170, 483 - 25, 1170 + 200, 483, rectPaint);
        canvasPart4L.drawText(orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 1170, 483 - 3, paint);
        canvasPart4L.restore();
    }
    void drawTextSideBelow_l(Canvas canvas, String LR) {
        canvas.drawRect(246, 445 - 25, 246 + 100, 445, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + LR, 246, 445 - 3, paint);
    }
    void drawTextSideBelow_r(Canvas canvas, String LR) {
        canvas.drawRect(50, 445 - 25, 50 + 100, 445, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + LR, 50, 445 - 3, paint);
    }
    void drawTextSide(Canvas canvas, String LR) {
        canvas.drawRect(482, 494 - 25, 482 + 500, 494, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).size + LR + " " + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 482, 494 - 3, paint);
    }

    void drawTextSideUp_l(Canvas canvas, String LR) {
        canvas.drawRect(8, 181 - 25, 8 + 28, 181, rectPaint);
        canvas.drawText(LR, 10, 181 - 3, paint);
    }
    void drawTextSideUp_r(Canvas canvas, String LR) {
        canvas.drawRect(509, 185 - 25, 509 + 28, 185, rectPaint);
        canvas.drawText(LR, 509, 185 - 3, paint);
    }
    void drawTextTongue_up(Canvas canvas, String LR) {
        canvas.drawRect(126, 698 - 25, 126 + 30, 698, rectPaint);
        canvas.drawText(LR, 126, 698 - 3, paint);
    }
    void drawTextTongue_below(Canvas canvas, String LR) {
        canvas.drawRect(118, 40 - 25, 118 + 30, 40, rectPaint);
        canvas.drawText(LR, 118, 40 - 3, paint);
    }


    public void remixx(){
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(5890, 2300, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 14) {
            //left_inside_up
            Bitmap bitmapTemp = Bitmap.createBitmap(541, 270, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(10), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_r(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 3601 - width_side_up, 2250 - height_side_up, null);

            //left_inside_below
            bitmapTemp = Bitmap.createBitmap(407, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(10), -335, -135, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_r(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            Matrix matrix = new Matrix();
            matrix.postTranslate(4118 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(-1 * angle_side_below_l, 4118, 2470 - height_side_below);
            matrix.postTranslate(margin_side_below_l, 0);
            //
            matrix.postRotate(56.8f, 4100, 2468);
            matrix.postTranslate(-293, -175);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //left_outside_up
            bitmapTemp = Bitmap.createBitmap(541, 270, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(11), -200, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_l(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 5142, 2250 - height_side_up, null);

            //left_outside_below
            bitmapTemp = Bitmap.createBitmap(407, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(11), 0, -135, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_l(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            matrix = new Matrix();
            matrix.postTranslate(4711 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(angle_side_below_l, 4711, 2470 - height_side_below);
            matrix.postTranslate(-1 * margin_side_below_l, 0);
            //
            matrix.postRotate(-56.8f, 4729, 2468);
            matrix.postTranslate(207, -175);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //right_inside_up
            bitmapTemp = Bitmap.createBitmap(541, 270, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(12), -200, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_l(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2169, 2250 - height_side_up, null);

            //right_inside_below
            bitmapTemp = Bitmap.createBitmap(407, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(12), 0, -135, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_l(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            matrix = new Matrix();
            matrix.postTranslate(1738 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(angle_side_below_l, 1738, 2470 - height_side_below);
            matrix.postTranslate(-1 * margin_side_below_l, 0);
            //
            matrix.postRotate(-56.8f, 1756, 2468);
            matrix.postTranslate(208, -175);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //right_outside_up
            bitmapTemp = Bitmap.createBitmap(541, 270, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(13), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_r(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 628 - width_side_up, 2250 - height_side_up, null);

            //right_outside_below
            bitmapTemp = Bitmap.createBitmap(407, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(13), -335, -135, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_r(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            matrix = new Matrix();
            matrix.postTranslate(1144 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(-1 * angle_side_below_l, 1144, 2470 - height_side_below);
            matrix.postTranslate(margin_side_below_l, 0);
            //
            matrix.postRotate(56.8f, 1129, 2468);
            matrix.postTranslate(-293, -173);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //left_inside
            bitmapTemp = Bitmap.createBitmap(1242, 504, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 2973, 1903 - height_side, null);

            //left_outside
            bitmapTemp = Bitmap.createBitmap(1242, 504, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 5890 - width_side, 1903 - height_side, null);

            //right_inside
            bitmapTemp = Bitmap.createBitmap(1242, 504, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 2917 - width_side, 1903 - height_side, null);

            //rightoutside
            bitmapTemp = Bitmap.createBitmap(1242, 504, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 1903 - height_side, null);

            //left_back
            bitmapTemp = Bitmap.createBitmap(1680, 443, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 2973, 746 + (int) (18 - 1.5 * (orderItems.get(currentID).size - 36)), null);

            //left_front
            bitmapTemp = Bitmap.createBitmap(1496, 647, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 3093, 36 - 3 * (orderItems.get(currentID).size - 36), null);

            //left_tongue
            bitmapTemp = Bitmap.createBitmap(605, 702, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(6), -2, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_up(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 5431 - width_tongue_up / 2, 808 - height_tongue_up, null);

            bitmapTemp = Bitmap.createBitmap(610, 536, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(6), 0, -651, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_below(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 5431 - width_tongue_below / 2, 881, null);


            //right_back
            bitmapTemp = Bitmap.createBitmap(1680, 443, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(7), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 746 + (int) (18 - 1.5 * (orderItems.get(currentID).size - 36)), null);

            //right_front
            bitmapTemp = Bitmap.createBitmap(1496, 647, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(8), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 120, 36 - 3 * (orderItems.get(currentID).size - 36), null);

            //right_tongue
            bitmapTemp = Bitmap.createBitmap(605, 702, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(9), -2, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_up(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2458 - width_tongue_up / 2, 808 - height_tongue_up, null);

            bitmapTemp = Bitmap.createBitmap(610, 536, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(9), 0, -651, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_below(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2458 - width_tongue_below / 2, 881, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 3500) {//4u2
            //left_inside_up
            Bitmap bitmapTemp = Bitmap.createBitmap(541, 270, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -699, -446, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_r(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 3601 - width_side_up, 2250 - height_side_up, null);

            //left_inside_below
            bitmapTemp = Bitmap.createBitmap(407, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1033, -581, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_r(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            Matrix matrix = new Matrix();
            matrix.postTranslate(4118 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(-1 * angle_side_below_l, 4118, 2470 - height_side_below);
            matrix.postTranslate(margin_side_below_l, 0);
            //
            matrix.postRotate(56.8f, 4100, 2468);
            matrix.postTranslate(-293, -175);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //left_outside_up
            bitmapTemp = Bitmap.createBitmap(541, 270, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2254, -444, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_l(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 5142, 2250 - height_side_up, null);

            //left_outside_below
            bitmapTemp = Bitmap.createBitmap(407, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2054, -579, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_l(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            matrix = new Matrix();
            matrix.postTranslate(4711 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(angle_side_below_l, 4711, 2470 - height_side_below);
            matrix.postTranslate(-1 * margin_side_below_l, 0);
            //
            matrix.postRotate(-56.8f, 4729, 2468);
            matrix.postTranslate(207, -175);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //right_inside_up
            bitmapTemp = Bitmap.createBitmap(541, 270, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2254, -2760, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_l(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2169, 2250 - height_side_up, null);

            //right_inside_below
            bitmapTemp = Bitmap.createBitmap(407, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2054, -2895, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_l(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            matrix = new Matrix();
            matrix.postTranslate(1738 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(angle_side_below_l, 1738, 2470 - height_side_below);
            matrix.postTranslate(-1 * margin_side_below_l, 0);
            //
            matrix.postRotate(-56.8f, 1756, 2468);
            matrix.postTranslate(208, -175);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //right_outside_up
            bitmapTemp = Bitmap.createBitmap(541, 270, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -699, -2762, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_r(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 628 - width_side_up, 2250 - height_side_up, null);

            //right_outside_below
            bitmapTemp = Bitmap.createBitmap(407, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1033, -2897, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_r(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            matrix = new Matrix();
            matrix.postTranslate(1144 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(-1 * angle_side_below_l, 1144, 2470 - height_side_below);
            matrix.postTranslate(margin_side_below_l, 0);
            //
            matrix.postRotate(56.8f, 1129, 2468);
            matrix.postTranslate(-293, -173);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //left_inside
            bitmapTemp = Bitmap.createBitmap(1242, 504, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -41, -535, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 2973, 1903 - height_side, null);

            //left_outside
            bitmapTemp = Bitmap.createBitmap(1242, 504, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2216, -533, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 5890 - width_side, 1903 - height_side, null);

            //right_inside
            bitmapTemp = Bitmap.createBitmap(1242, 504, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2216, -2849, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 2917 - width_side, 1903 - height_side, null);

            //rightoutside
            bitmapTemp = Bitmap.createBitmap(1242, 504, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -41, -2851, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 1903 - height_side, null);

            //left_back
            bitmapTemp = Bitmap.createBitmap(1680, 443, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -910, 1, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 2973, 746 + (int) (18 - 1.5 * (orderItems.get(currentID).size - 36)), null);

            //left_front
            bitmapTemp = Bitmap.createBitmap(1496, 647, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1002, -1635, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 3093, 36 - 3 * (orderItems.get(currentID).size - 36), null);

            //left_tongue
            bitmapTemp = Bitmap.createBitmap(605, 702, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2 - 1444, 0 - 448, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_up(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 5431 - width_tongue_up / 2, 808 - height_tongue_up, null);

            bitmapTemp = Bitmap.createBitmap(610, 536, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0 - 1444, -651 - 448, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_below(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 5431 - width_tongue_below / 2, 881, null);


            //right_back
            bitmapTemp = Bitmap.createBitmap(1680, 443, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -910, -2317, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 746 + (int) (18 - 1.5 * (orderItems.get(currentID).size - 36)), null);

            //right_front
            bitmapTemp = Bitmap.createBitmap(1496, 647, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1002, -3951, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 120, 36 - 3 * (orderItems.get(currentID).size - 36), null);

            //right_tongue
            bitmapTemp = Bitmap.createBitmap(605, 702, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2 - 1444, 0 - 2764, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_up(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2458 - width_tongue_up / 2, 808 - height_tongue_up, null);

            bitmapTemp = Bitmap.createBitmap(610, 536, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0 - 1444, -651 - 2764, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_below(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2458 - width_tongue_below / 2, 881, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

        } else if (MainActivity.instance.bitmaps.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 5807) {
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);

            //left_inside_up
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3249, 3356, 270, 541, matrix, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_r(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 3601 - width_side_up, 2250 - height_side_up, null);

            //left_inside_below
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2931, 3688, 454, 407, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_r(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            matrix = new Matrix();
            matrix.postTranslate(4118 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(-1 * angle_side_below_l, 4118, 2470 - height_side_below);
            matrix.postTranslate(margin_side_below_l, 0);
            //
            matrix.postRotate(56.8f, 4100, 2468);
            matrix.postTranslate(-293, -175);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //left_outside_up
            matrix.reset();
            matrix.postRotate(90);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5070, 3356, 270, 541, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_l(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 5142, 2250 - height_side_up, null);

            //left_outside_below
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5205, 3691, 454, 407, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_l(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            matrix = new Matrix();
            matrix.postTranslate(4711 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(angle_side_below_l, 4711, 2470 - height_side_below);
            matrix.postTranslate(-1 * margin_side_below_l, 0);
            //
            matrix.postRotate(-56.8f, 4729, 2468);
            matrix.postTranslate(207, -175);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //right_inside_up
            matrix.reset();
            matrix.postRotate(90);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2288, 3356, 270, 541, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_l(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2169, 2250 - height_side_up, null);

            //right_inside_below
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2424, 3691, 454, 407, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_l(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            matrix = new Matrix();
            matrix.postTranslate(1738 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(angle_side_below_l, 1738, 2470 - height_side_below);
            matrix.postTranslate(-1 * margin_side_below_l, 0);
            //
            matrix.postRotate(-56.8f, 1756, 2468);
            matrix.postTranslate(208, -175);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //right_outside_up
            matrix.reset();
            matrix.postRotate(-90);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 466, 3348, 270, 541, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_r(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 628 - width_side_up, 2250 - height_side_up, null);

            //right_outside_below
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 146, 3683, 454, 407, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_r(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            matrix = new Matrix();
            matrix.postTranslate(1144 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(-1 * angle_side_below_l, 1144, 2470 - height_side_below);
            matrix.postTranslate(margin_side_below_l, 0);
            //
            matrix.postRotate(56.8f, 1129, 2468);
            matrix.postTranslate(-293, -173);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //left_inside
            matrix.reset();
            matrix.postRotate(-90);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2928, 1927, 504, 1242, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 2973, 1903 - height_side, null);

            //left_outside
            matrix.reset();
            matrix.postRotate(90);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5158, 1934, 504, 1242, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 5890 - width_side, 1903 - height_side, null);

            //right_inside
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2376, 1934, 504, 1242, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 2917 - width_side, 1903 - height_side, null);

            //rightoutside
            matrix.reset();
            matrix.postRotate(-90);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 146, 1927, 504, 1242, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 1903 - height_side, null);

            //left_back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3455, 1704, 1680, 443);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 2973, 746 + (int) (18 - 1.5 * (orderItems.get(currentID).size - 36)), null);

            //left_front
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3544, 3336, 1496, 647);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 3093, 36 - 3 * (orderItems.get(currentID).size - 36), null);

            //left_tongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3993, 2147, 605, 702);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_up(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 5431 - width_tongue_up / 2, 808 - height_tongue_up, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3991, 2798, 610, 536);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_below(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 5431 - width_tongue_below / 2, 881, null);


            //right_back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 673, 1704, 1680, 443);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 746 + (int) (18 - 1.5 * (orderItems.get(currentID).size - 36)), null);

            //right_front
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 765, 3336, 1496, 647);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 120, 36 - 3 * (orderItems.get(currentID).size - 36), null);

            //right_tongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1211, 2147, 605, 702);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_up(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2458 - width_tongue_up / 2, 808 - height_tongue_up, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1209, 2798, 610, 536);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_below(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2458 - width_tongue_below / 2, 881, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

        } else if (MainActivity.instance.bitmaps.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 4000) {
            //left_inside_up
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1143, 3122, 541, 270);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_r(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 3601 - width_side_up, 2250 - height_side_up, null);

            //left_inside_below
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1482, 3259, 407, 454);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_r(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            Matrix matrix = new Matrix();
            matrix.postTranslate(4118 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(-1 * angle_side_below_l, 4118, 2470 - height_side_below);
            matrix.postTranslate(margin_side_below_l, 0);
            //
            matrix.postRotate(56.8f, 4100, 2468);
            matrix.postTranslate(-293, -175);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //left_outside_up
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 338, 3133, 541, 270);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_l(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 5142, 2250 - height_side_up, null);

            //left_outside_below
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 133, 3268, 407, 454);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_l(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            matrix = new Matrix();
            matrix.postTranslate(4711 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(angle_side_below_l, 4711, 2470 - height_side_below);
            matrix.postTranslate(-1 * margin_side_below_l, 0);
            //
            matrix.postRotate(-56.8f, 4729, 2468);
            matrix.postTranslate(207, -175);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //right_inside_up
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2356, 3133, 541, 270);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_l(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2169, 2250 - height_side_up, null);

            //right_inside_below
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2153, 3271, 407, 454);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_l(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            matrix = new Matrix();
            matrix.postTranslate(1738 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(angle_side_below_l, 1738, 2470 - height_side_below);
            matrix.postTranslate(-1 * margin_side_below_l, 0);
            //
            matrix.postRotate(-56.8f, 1756, 2468);
            matrix.postTranslate(208, -175);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //right_outside_up
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3163, 3122, 541, 270);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_r(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 628 - width_side_up, 2250 - height_side_up, null);

            //right_outside_below
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3500, 3259, 407, 454);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_r(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            matrix = new Matrix();
            matrix.postTranslate(1144 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(-1 * angle_side_below_l, 1144, 2470 - height_side_below);
            matrix.postTranslate(margin_side_below_l, 0);
            //
            matrix.postRotate(56.8f, 1129, 2468);
            matrix.postTranslate(-293, -173);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //left_inside
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 103, 1012, 1242, 504);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 2973, 1903 - height_side, null);

            //left_outside
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 83, 275, 1242, 504);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 5890 - width_side, 1903 - height_side, null);

            //right_inside
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 83, 1750, 1242, 504);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 2917 - width_side, 1903 - height_side, null);

            //rightoutside
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 103, 2487, 1242, 504);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 1903 - height_side, null);

            //left_back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2235, 1828, 1680, 443);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 2973, 746 + (int) (18 - 1.5 * (orderItems.get(currentID).size - 36)), null);

            //left_front
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2321, 275, 1496, 647);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 3093, 36 - 3 * (orderItems.get(currentID).size - 36), null);

            //left_tongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1487, 275, 605, 702);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_up(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 5431 - width_tongue_up / 2, 808 - height_tongue_up, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1486, 922, 610, 536);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_below(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 5431 - width_tongue_below / 2, 881, null);


            //right_back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2226, 2542, 1680, 443);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 746 + (int) (18 - 1.5 * (orderItems.get(currentID).size - 36)), null);

            //right_front
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2321, 1053, 1496, 647);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 120, 36 - 3 * (orderItems.get(currentID).size - 36), null);

            //right_tongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1486, 1802, 605, 702);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_up(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2458 - width_tongue_up / 2, 808 - height_tongue_up, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1486, 2451, 610, 536);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_below(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2458 - width_tongue_below / 2, 881, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

        } else if (MainActivity.instance.bitmaps.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 8100) {//adam
            //left_inside_up
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5053, 752, 541, 270);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_r(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 3601 - width_side_up, 2250 - height_side_up, null);

            //left_inside_below
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5359, 1072, 407, 454);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_r(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            Matrix matrix = new Matrix();
            matrix.postTranslate(4118 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(-1 * angle_side_below_l, 4118, 2470 - height_side_below);
            matrix.postTranslate(margin_side_below_l, 0);
            //
            matrix.postRotate(56.8f, 4100, 2468);
            matrix.postTranslate(-293, -175);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //left_outside_up
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6550, 752, 541, 270);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_l(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 5142, 2250 - height_side_up, null);

            //left_outside_below
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6381, 1074, 407, 454);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_l(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            matrix = new Matrix();
            matrix.postTranslate(4711 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(angle_side_below_l, 4711, 2470 - height_side_below);
            matrix.postTranslate(-1 * margin_side_below_l, 0);
            //
            matrix.postRotate(-56.8f, 4729, 2468);
            matrix.postTranslate(207, -175);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //right_inside_up
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2500, 752, 541, 270);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_l(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2169, 2250 - height_side_up, null);

            //right_inside_below
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2331, 1074, 407, 454);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_l(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            matrix = new Matrix();
            matrix.postTranslate(1738 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(angle_side_below_l, 1738, 2470 - height_side_below);
            matrix.postTranslate(-1 * margin_side_below_l, 0);
            //
            matrix.postRotate(-56.8f, 1756, 2468);
            matrix.postTranslate(208, -175);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //right_outside_up
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1003, 752, 541, 270);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_up_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideUp_r(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_up, height_side_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 628 - width_side_up, 2250 - height_side_up, null);

            //right_outside_below
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1310, 1072, 407, 454);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_below_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideBelow_r(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_below, height_side_below, true);
            matrix = new Matrix();
            matrix.postTranslate(1144 - width_side_below / 2, 2470 - height_side_below);
            matrix.postRotate(-1 * angle_side_below_l, 1144, 2470 - height_side_below);
            matrix.postTranslate(margin_side_below_l, 0);
            //
            matrix.postRotate(56.8f, 1129, 2468);
            matrix.postTranslate(-293, -173);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //left_inside
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4117, 1023, 1242, 504);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 2973, 1903 - height_side, null);

            //left_outside
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6790, 1023, 1242, 504);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 5890 - width_side, 1903 - height_side, null);

            //right_inside
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2740, 1023, 1242, 504);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 2917 - width_side, 1903 - height_side, null);

            //rightoutside
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 67, 1023, 1242, 504);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 1903 - height_side, null);

            //left_back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5236, 43, 1680, 443);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 2973, 746 + (int) (18 - 1.5 * (orderItems.get(currentID).size - 36)), null);

            //left_front
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5326, 1609, 1496, 647);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 3093, 36 - 3 * (orderItems.get(currentID).size - 36), null);

            //left_tongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5772, 591, 605, 702);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_up(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 5431 - width_tongue_up / 2, 808 - height_tongue_up, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5770, 1313, 610, 536);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_below(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 5431 - width_tongue_below / 2, 881, null);


            //right_back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1186, 43, 1680, 443);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 746 + (int) (18 - 1.5 * (orderItems.get(currentID).size - 36)), null);

            //right_front
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1276, 1609, 1496, 647);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 120, 36 - 3 * (orderItems.get(currentID).size - 36), null);

            //right_tongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1722, 591, 605, 702);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_up(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_up, height_tongue_up, true);
            canvasCombine.drawBitmap(bitmapTemp, 2458 - width_tongue_up / 2, 808 - height_tongue_up, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1720, 1313, 610, 536);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lg_tongue_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue_below(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue_below, height_tongue_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2458 - width_tongue_below / 2, 881, null);

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

            String printColor = orderItems.get(currentID).color.equals("白") ? "W" : "B";

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
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor);
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
                width_side_up = 497;
                height_side_up = 253;
                width_side_below = 368;
                height_side_below = 424;
                width_side = 1101;
                height_side = 460;
                width_front = 1349;
                height_front = 578;
                width_tongue_up = 556;
                height_tongue_up = 628;
                width_tongue_below = 568;
                height_tongue_below = 482;
                width_back = 1491;
                height_back = 405;
                angle_side_below_l = -1.79f;
                margin_side_below_l = 9;
                break;
            case 37:
                width_side_up = 497;
                height_side_up = 253;
                width_side_below = 368;
                height_side_below = 424;
                width_side = 1129;
                height_side = 469;
                width_front = 1380;
                height_front = 593;
                width_tongue_up = 566;
                height_tongue_up = 642;
                width_tongue_below = 576;
                height_tongue_below = 493;
                width_back = 1529;
                height_back = 412;
                angle_side_below_l = -1.79f;
                margin_side_below_l = 7;
                break;
            case 38:
                width_side_up = 518;
                height_side_up = 264;
                width_side_below = 389;
                height_side_below = 438;
                width_side = 1157;
                height_side = 478;
                width_front = 1410;
                height_front = 606;
                width_tongue_up = 575;
                height_tongue_up = 657;
                width_tongue_below = 585;
                height_tongue_below = 503;
                width_back = 1566;
                height_back = 421;
                angle_side_below_l = -1.4f;
                margin_side_below_l = 7;
                break;
            case 39:
                width_side_up = 518;
                height_side_up = 264;
                width_side_below = 389;
                height_side_below = 438;
                width_side = 1185;
                height_side = 486;
                width_front = 1438;
                height_front = 620;
                width_tongue_up = 586;
                height_tongue_up = 673;
                width_tongue_below = 594;
                height_tongue_below = 514;
                width_back = 1604;
                height_back = 427;
                angle_side_below_l = -1.4f;
                margin_side_below_l = 7;
                break;
            case 40:
                width_side_up = 541;
                height_side_up = 270;
                width_side_below = 407;
                height_side_below = 454;
                width_side = 1214;
                height_side = 495;
                width_front = 1467;
                height_front = 635;
                width_tongue_up = 596;
                height_tongue_up = 687;
                width_tongue_below = 602;
                height_tongue_below = 525;
                width_back = 1641;
                height_back = 434;
                angle_side_below_l = -1.27f;
                margin_side_below_l = 7;
                break;
            case 41:
                width_side_up = 541;
                height_side_up = 270;
                width_side_below = 407;
                height_side_below = 454;
                width_side = 1242;
                height_side = 504;
                width_front = 1496;
                height_front = 647;
                width_tongue_up = 605;
                height_tongue_up = 702;
                width_tongue_below = 610;
                height_tongue_below = 536;
                width_back = 1680;
                height_back = 443;
                angle_side_below_l = -1.27f;
                margin_side_below_l = 7;
                break;
            case 42:
                width_side_up = 567;
                height_side_up = 277;
                width_side_below = 427;
                height_side_below = 469;
                width_side = 1270;
                height_side = 512;
                width_front = 1526;
                height_front = 662;
                width_tongue_up = 614;
                height_tongue_up = 717;
                width_tongue_below = 619;
                height_tongue_below = 548;
                width_back = 1716;
                height_back = 450;
                angle_side_below_l = -0.74f;
                margin_side_below_l = 4;
                break;
            case 43:
                width_side_up = 567;
                height_side_up = 277;
                width_side_below = 427;
                height_side_below = 469;
                width_side = 1299;
                height_side = 521;
                width_front = 1555;
                height_front = 676;
                width_tongue_up = 625;
                height_tongue_up = 733;
                width_tongue_below = 628;
                height_tongue_below = 558;
                width_back = 1753;
                height_back = 459;
                angle_side_below_l = -0.74f;
                margin_side_below_l = 4;
                break;
            case 44:
                width_side_up = 588;
                height_side_up = 288;
                width_side_below = 447;
                height_side_below = 484;
                width_side = 1327;
                height_side = 530;
                width_front = 1585;
                height_front = 689;
                width_tongue_up = 635;
                height_tongue_up = 747;
                width_tongue_below = 636;
                height_tongue_below = 570;
                width_back = 1791;
                height_back = 464;
                angle_side_below_l = -0.3f;
                break;
            case 45:
                width_side_up = 588;
                height_side_up = 288;
                width_side_below = 447;
                height_side_below = 484;
                width_side = 1355;
                height_side = 539;
                width_front = 1613;
                height_front = 704;
                width_tongue_up = 644;
                height_tongue_up = 762;
                width_tongue_below = 645;
                height_tongue_below = 581;
                width_back = 1828;
                height_back = 472;
                angle_side_below_l = -0.2f;
                break;
            case 46:
                width_side_up = 605;
                height_side_up = 294;
                width_side_below = 460;
                height_side_below = 495;
                width_side = 1383;
                height_side = 547;
                width_front = 1642;
                height_front = 716;
                width_tongue_up = 654;
                height_tongue_up = 777;
                width_tongue_below = 654;
                height_tongue_below = 593;
                width_back = 1865;
                height_back = 479;
                angle_side_below_l = -0.1f;
                break;
            case 47:
                width_side_up = 624;
                height_side_up = 302;
                width_side_below = 476;
                height_side_below = 506;
                width_side = 1413;
                height_side = 557;
                width_front = 1673;
                height_front = 732;
                width_tongue_up = 670;
                height_tongue_up = 808;
                width_tongue_below = 660;
                height_tongue_below = 606;
                width_back = 1905;
                height_back = 487;
                angle_side_below_l = -0.1f;
                break;
            case 48:
                width_side_up = 624;
                height_side_up = 302;
                width_side_below = 476;
                height_side_below = 506;
                width_side = 1442;
                height_side = 566;
                width_front = 1702;
                height_front = 746;
                width_tongue_up = 670;
                height_tongue_up = 808;
                width_tongue_below = 669;
                height_tongue_below = 617;
                width_back = 1942;
                height_back = 495;
                angle_side_below_l = -0.1f;
                break;
        }
        width_side_up += 8;
        height_side_up += 8;
        width_side_below += 8;
        height_side_below += 8;
        width_side += 8;
        width_tongue_up += 8;
        height_tongue_up += 8;
        width_tongue_below += 8;
        height_tongue_below += 8;
    }
}
