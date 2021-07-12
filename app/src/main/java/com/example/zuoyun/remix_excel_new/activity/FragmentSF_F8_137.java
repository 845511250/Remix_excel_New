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

public class FragmentSF_F8_137 extends BaseFragment {
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

    int width_front1, width_front2, width_back, width_lace, width_outside1, width_inside1, width_side2;
    int height_front1, height_front2, height_back, height_lace, height_outside1, height_inside1, height_side2;
    int width_combine, height_combine;
    int x_front1_left, x_front1_right, x_front2_left, x_front2_right, x_back_left, x_back_right, x_lace_left, x_lace_right, x_ll1, x_ll2, x_lr1, x_lr2, x_rl1, x_rl2, x_rr1, x_rr2;
    int y_front1_left, y_front1_right, y_front2_left, y_front2_right, y_back_left, y_back_right, y_lace_left, y_lace_right, y_ll1, y_ll2, y_lr1, y_lr2, y_rl1, y_rl2, y_rr1, y_rr2;

    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint rectPaint, paint, paintRed, paintSmall, paintRectBlack;
    String time;

    @Override
    public int getLayout() {
        return R.layout.fragment_dg;
    }

    @Override
    public void initData(View view) {
        context = getContext();
        ButterKnife.bind(this, view);
        orderItems = MainActivity.instance.orderItems;
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
        paint.setTextSize(22);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(26);
        paintRed.setAntiAlias(true);

        paintSmall = new Paint();
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(12);
        paintSmall.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;


        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                    Log.e("fragment2", "message0");
                } else if (message == MainActivity.LOADED_IMGS) {
                    if (!MainActivity.instance.cb_fastmode.isChecked()) {
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

    public void remix() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (num = orderItems.get(currentID).num; num >= 1; num--) {
                    intPlus = orderItems.get(currentID).num - num + 1;
                    for (int i = 0; i < currentID; i++) {
                        if (orderItems.get(currentID).order_number.equals(orderItems.get(i).order_number)) {
                            intPlus += orderItems.get(i).num;
                            ;
                        }
                    }
                    strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                    remixx();
                }
            }
        }.start();

    }

    void drawTextFront1(Canvas canvas, String LR) {
        canvas.drawRect(269, 202 - 22, 269 + 85, 202, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + LR + orderItems.get(currentID).color, 269, 202 - 2, paint);

        canvas.save();
        canvas.rotate(74.3f, 12, 151);
        canvas.drawRect(12, 151 - 22, 12 + 155, 151, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number, 12, 151 - 2, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-4f, 89, 37);
        canvas.drawRect(89, 37 - 22, 89 + 102, 37, rectPaint);
        canvas.drawText(time, 89, 37 - 2, paint);
        canvas.restore();
    }

    void drawTextFront2_left(Canvas canvas) {
        canvas.save();
        canvas.rotate(-18.6f, 1073, 616);
        canvas.drawRect(1073, 616 - 22, 1073 + 150, 616, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).size + "左" + orderItems.get(currentID).color, 1073, 616 - 2, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(21.8f, 606, 560);
        canvas.drawRect(606, 560 - 22, 606 + 150, 560, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number, 606, 560 - 2, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(10.7f, 792, 628);
        canvas.drawRect(792, 628 - 22, 792 + 102, 628, rectPaint);
        canvas.drawText(time, 792, 628 - 2, paint);
        canvas.restore();
    }

    void drawTextFront2_right(Canvas canvas) {
        canvas.save();
        canvas.rotate(10.2f, 723, 617);
        canvas.drawRect(723, 617 - 22, 723 + 150, 617, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).size + "右" + orderItems.get(currentID).color, 723, 617 - 2, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-20.2f, 1022, 620);
        canvas.drawRect(1022, 620 - 22, 1022 + 150, 620, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number, 1022, 620 - 2, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-10.1f, 902, 643);
        canvas.drawRect(902, 643 - 22, 902 + 102, 643, rectPaint);
        canvas.drawText(time, 902, 643 - 2, paint);
        canvas.restore();
    }

    void drawTextLR2(Canvas canvas) {
        canvas.drawRect(33, 588 - 22, 33 + 450, 588, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "左内" + orderItems.get(currentID).color + " " + orderItems.get(currentID).sku + " " + orderItems.get(currentID).order_number + " " + time, 33, 588 - 2, paint);
    }

    void drawTextRR2(Canvas canvas) {
        canvas.drawRect(33, 588 - 22, 33 + 450, 588, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "右外" + orderItems.get(currentID).color + " " + orderItems.get(currentID).sku + " " + orderItems.get(currentID).order_number + " " + time, 33, 588 - 2, paint);
    }

    void drawTextLL2(Canvas canvas) {
        canvas.drawRect(299, 588 - 22, 299 + 450, 588, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "左外" + orderItems.get(currentID).color + " " + orderItems.get(currentID).sku + " " + orderItems.get(currentID).order_number + " " + time, 299, 588 - 2, paint);
    }

    void drawTextRL2(Canvas canvas) {
        canvas.drawRect(299, 588 - 22, 299 + 450, 588, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "右内" + orderItems.get(currentID).color + " " + orderItems.get(currentID).sku + " " + orderItems.get(currentID).order_number + " " + time, 299, 588 - 2, paint);
    }

    void drawTextLR1(Canvas canvas) {
        canvas.drawRect(19, 517 - 22, 19 + 434, 517, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "左内" + orderItems.get(currentID).color + " " + orderItems.get(currentID).sku + " " + orderItems.get(currentID).order_number + " " + time, 19, 517 - 2, paint);
    }

    void drawTextRL1(Canvas canvas) {
        canvas.drawRect(209, 517 - 22, 209 + 434, 517, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "右内" + orderItems.get(currentID).color + " " + orderItems.get(currentID).sku + " " + orderItems.get(currentID).order_number + " " + time, 209, 517 - 2, paint);
    }

    void drawTextLL1(Canvas canvas) {
        canvas.drawRect(224, 499 - 22, 224 + 434, 499, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "左外" + orderItems.get(currentID).color + " " + orderItems.get(currentID).sku + " " + orderItems.get(currentID).order_number + " " + time, 224, 499 - 2, paint);
    }

    void drawTextRR1(Canvas canvas) {
        canvas.drawRect(23, 499 - 22, 23 + 434, 499, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "右外" + orderItems.get(currentID).color + " " + orderItems.get(currentID).sku + " " + orderItems.get(currentID).order_number + " " + time, 23, 499 - 2, paint);
    }

    void drawTextBack(Canvas canvas, String LR) {
        canvas.drawRect(189, 13 - 10, 189 + 35, 13, rectPaint);
        canvas.drawText(LR, 190, 13 - 2, paint);
    }


    public void remixx() {
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(width_combine, height_combine, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 4000) {

            //left_front1
            Bitmap bitmapTemp = Bitmap.createBitmap(641, 603, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -461, -1426, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_front1_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront1(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front1, height_front1, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front1_left, y_front1_left, null);

            //right_front1
            bitmapTemp = Bitmap.createBitmap(641, 603, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1329, -1426, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -640, 602, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront1(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front1, height_front1, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front1_right, y_front1_right, null);

            //left_front2
            bitmapTemp = Bitmap.createBitmap(1786, 672, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -334, -2927, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_front2_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront2_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front2, height_front2, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front2_left, y_front2_left, null);

            //right_front2
            bitmapTemp = Bitmap.createBitmap(1786, 672, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -336, -2196, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1786, 672, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront2_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front2, height_front2, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front2_right, y_front2_right, null);

            //LR2
            bitmapTemp = Bitmap.createBitmap(792, 600, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2193, -1062, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_lr2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLR2(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2, height_side2, true);
            canvasCombine.drawBitmap(bitmapTemp, x_lr2, y_lr2, null);

            //RR2
            bitmapTemp = Bitmap.createBitmap(792, 600, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2194, -2404, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_lr2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRR2(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2, height_side2, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rr2, y_rr2, null);

            //LL2
            bitmapTemp = Bitmap.createBitmap(792, 600, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2871, -398, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -792, 600, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLL2(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2, height_side2, true);
            canvasCombine.drawBitmap(bitmapTemp, x_ll2, y_ll2, null);

            //RL2
            bitmapTemp = Bitmap.createBitmap(792, 600, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2869, -1733, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRL2(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2, height_side2, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rl2, y_rl2, null);

            //LR1
            bitmapTemp = Bitmap.createBitmap(664, 566, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3003, -1107, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_lr1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLR1(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside1, height_inside1, true);
            canvasCombine.drawBitmap(bitmapTemp, x_lr1, y_lr1, null);

            //RL1
            bitmapTemp = Bitmap.createBitmap(664, 566, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2188, -1777, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -664, 566, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRL1(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside1, height_inside1, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rl1, y_rl1, null);

            //LL1
            bitmapTemp = Bitmap.createBitmap(682, 550, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2193, -449, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_ll1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLL1(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside1, height_outside1, true);
            canvasCombine.drawBitmap(bitmapTemp, x_ll1, y_ll1, null);

            //RR1
            bitmapTemp = Bitmap.createBitmap(682, 550, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2983, -2454, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -682, 550, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRR1(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside1, height_outside1, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rr1, y_rr1, null);

            //lace_left
            bitmapTemp = Bitmap.createBitmap(363, 790, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -599, -400, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_lace);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lace, height_lace, true);
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            matrix.postTranslate(x_lace_left, y_lace_left + width_lace);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //lace_right
            bitmapTemp = Bitmap.createBitmap(363, 790, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1468, -400, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_lace);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lace, height_lace, true);
            matrix = new Matrix();
            matrix.postRotate(90);
            matrix.postTranslate(x_lace_right + height_lace, y_lace_right);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //back_left
            bitmapTemp = Bitmap.createBitmap(553, 290, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2153, -3212, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "zuo");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back_left, y_back_left, null);

            //back_right
            bitmapTemp = Bitmap.createBitmap(553, 290, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3043, -3212, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "you");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back_right, y_back_right, null);


            //
            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (MainActivity.instance.bitmaps.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 4001) {

            //left_front1
            Bitmap bitmapTemp = Bitmap.createBitmap(641, 603, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -461, -1426, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_front1_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront1(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front1, height_front1, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front1_left, y_front1_left, null);

            //right_front1
            bitmapTemp = Bitmap.createBitmap(641, 603, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1329, -1426, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -640, 602, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront1(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front1, height_front1, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front1_right, y_front1_right, null);

            //left_front2
            bitmapTemp = Bitmap.createBitmap(1786, 672, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -334, -2927, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_front2_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront2_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front2, height_front2, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front2_left, y_front2_left, null);

            //right_front2
            bitmapTemp = Bitmap.createBitmap(1786, 672, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -336, -2196, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1786, 672, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront2_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front2, height_front2, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front2_right, y_front2_right, null);

            //LR2
            bitmapTemp = Bitmap.createBitmap(792, 600, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2528, -1063, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_lr2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLR2(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2, height_side2, true);
            canvasCombine.drawBitmap(bitmapTemp, x_lr2, y_lr2, null);

            //RR2
            bitmapTemp = Bitmap.createBitmap(792, 600, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2526, -2406, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_lr2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRR2(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2, height_side2, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rr2, y_rr2, null);

            //LL2
            bitmapTemp = Bitmap.createBitmap(792, 600, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2871, -398, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -792, 600, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLL2(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2, height_side2, true);
            canvasCombine.drawBitmap(bitmapTemp, x_ll2, y_ll2, null);

            //RL2
            bitmapTemp = Bitmap.createBitmap(792, 600, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2869, -1733, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRL2(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2, height_side2, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rl2, y_rl2, null);

            //LR1
            bitmapTemp = Bitmap.createBitmap(664, 566, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3003, -1107, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_lr1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLR1(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside1, height_inside1, true);
            canvasCombine.drawBitmap(bitmapTemp, x_lr1, y_lr1, null);

            //RL1
            bitmapTemp = Bitmap.createBitmap(664, 566, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2530, -1778, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -664, 566, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRL1(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside1, height_inside1, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rl1, y_rl1, null);

            //LL1
            bitmapTemp = Bitmap.createBitmap(682, 550, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2522, -448, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_ll1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLL1(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside1, height_outside1, true);
            canvasCombine.drawBitmap(bitmapTemp, x_ll1, y_ll1, null);

            //RR1
            bitmapTemp = Bitmap.createBitmap(682, 550, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2983, -2454, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -682, 550, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRR1(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside1, height_outside1, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rr1, y_rr1, null);

            //lace_left
            bitmapTemp = Bitmap.createBitmap(363, 790, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -599, -400, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_lace);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lace, height_lace, true);
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            matrix.postTranslate(x_lace_left, y_lace_left + width_lace);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //lace_right
            bitmapTemp = Bitmap.createBitmap(363, 790, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1468, -400, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_lace);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lace, height_lace, true);
            matrix = new Matrix();
            matrix.postRotate(90);
            matrix.postTranslate(x_lace_right + height_lace, y_lace_right);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //back_left
            bitmapTemp = Bitmap.createBitmap(553, 290, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2153, -3212, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "zuo");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back_left, y_back_left, null);

            //back_right
            bitmapTemp = Bitmap.createBitmap(553, 290, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3043, -3212, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "you");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back_right, y_back_right, null);


            //
            bitmapTemp.recycle();
            bitmapDB.recycle();
        }


        try {
            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 150);

            String printColor = orderItems.get(currentID).color.equals("白") ? "W" : "B";

            //写入excel
            String writePath = sdCardPath + "/生产图/" + childPath + "/生产单.xls";
            File fileWrite = new File(writePath);
            if (!fileWrite.exists()) {
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
            WritableWorkbook workbook = Workbook.createWorkbook(fileWrite, book);
            WritableSheet sheet = workbook.getSheet(0);
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor);
            sheet.addCell(label1);
            int num = orderItems.get(currentID).num;
            Number number2 = new Number(2, currentID + 1, num);
            sheet.addCell(number2);
            Label label3 = new Label(3, currentID + 1, orderItems.get(currentID).customer);
            sheet.addCell(label3);
            Label label4 = new Label(4, currentID + 1, MainActivity.instance.orderDate_Excel);
            sheet.addCell(label4);
            Label label6 = new Label(6, currentID + 1, "平台大货");
            sheet.addCell(label6);

            workbook.write();
            workbook.close();

        } catch (Exception e) {
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

    public void checkremix() {
        if (MainActivity.instance.tb_auto.isChecked()) {
            remix();
        }
    }

    void setSize(int size) {
        switch (size) {
            case 36:
                width_front1 = 583;
                height_front1 = 542;
                width_front2 = 1590;
                height_front2 = 604;
                width_back = 488;
                height_back = 260;
                width_lace = 326;
                height_lace = 696;
                width_outside1 = 614;
                height_outside1 = 502;
                width_inside1 = 597;
                height_inside1 = 516;
                width_side2 = 699;
                height_side2 = 542;

                width_combine = 7702;
                height_combine = 733;

                x_front1_left = 1495;
                y_front1_left = 0;
                x_front2_left = 979;
                y_front2_left = 129;
                x_ll1 = 0;
                y_ll1 = 231;
                x_ll2 = 382;
                y_ll2 = 191;
                x_lr1 = 3252;
                y_lr1 = 217;
                x_lr2 = 2761;
                y_lr2 = 191;
                x_lace_left = 2569;
                y_lace_left = 0;
                x_back_left = 593;
                y_back_left = 0;

                x_front1_right = 5624;
                y_front1_right = 0;
                x_front2_right = 5133;
                y_front2_right = 129;
                x_rr1 = 7088;
                y_rr1 = 231;
                x_rr2 = 6621;
                y_rr2 = 191;
                x_rl1 = 3853;
                y_rl1 = 217;
                x_rl2 = 4242;
                y_rl2 = 191;
                x_lace_right = 4437;
                y_lace_right = 0;
                x_back_right = 6621;
                y_back_right = 0;
                break;
            case 37:
                width_front1 = 598;
                height_front1 = 557;
                width_front2 = 1639;
                height_front2 = 621;
                width_back = 505;
                height_back = 267;
                width_lace = 335;
                height_lace = 720;
                width_outside1 = 629;
                height_outside1 = 514;
                width_inside1 = 613;
                height_inside1 = 528;
                width_side2 = 722;
                height_side2 = 556;

                width_combine = 7937;
                height_combine = 752;

                x_front1_left = 1546;
                y_front1_left = 0;
                x_front2_left = 1011;
                y_front2_left = 131;
                x_ll1 = 0;
                y_ll1 = 238;
                x_ll2 = 390;
                y_ll2 = 196;
                x_lr1 = 3354;
                y_lr1 = 224;
                x_lr2 = 2849;
                y_lr2 = 196;
                x_lace_left = 2650;
                y_lace_left = 0;
                x_back_left = 558;
                y_back_left = 0;

                x_front1_right = 5793;
                y_front1_right = 0;
                x_front2_right = 5287;
                y_front2_right = 131;
                x_rr1 = 7308;
                y_rr1 = 238;
                x_rr2 = 6825;
                y_rr2 = 196;
                x_rl1 = 3970;
                y_rl1 = 224;
                x_rl2 = 4366;
                y_rl2 = 196;
                x_lace_right = 4567;
                y_lace_right = 0;
                x_back_right = 6874;
                y_back_right = 0;
                break;
            case 38:
                width_front1 = 605;
                height_front1 = 565;
                width_front2 = 1664;
                height_front2 = 630;
                width_back = 513;
                height_back = 272;
                width_lace = 340;
                height_lace = 731;
                width_outside1 = 639;
                height_outside1 = 520;
                width_inside1 = 622;
                height_inside1 = 533;
                width_side2 = 733;
                height_side2 = 563;

                width_combine = 8052;
                height_combine = 761;

                x_front1_left = 1572;
                y_front1_left = 0;
                x_front2_left = 1024;
                y_front2_left = 131;
                x_ll1 = 0;
                y_ll1 = 237;
                x_ll2 = 396;
                y_ll2 = 194;
                x_lr1 = 3402;
                y_lr1 = 224;
                x_lr2 = 2891;
                y_lr2 = 194;
                x_lace_left = 2688;
                y_lace_left = 0;
                x_back_left = 561;
                y_back_left = 0;

                x_front1_right = 5875;
                y_front1_right = 0;
                x_front2_right = 5364;
                y_front2_right = 131;
                x_rr1 = 7413;
                y_rr1 = 237;
                x_rr2 = 6923;
                y_rr2 = 194;
                x_rl1 = 4028;
                y_rl1 = 224;
                x_rl2 = 4428;
                y_rl2 = 194;
                x_lace_right = 4633;
                y_lace_right = 0;
                x_back_right = 6978;
                y_back_right = 0;
                break;
            case 39:
                width_front1 = 618;
                height_front1 = 580;
                width_front2 = 1714;
                height_front2 = 648;
                width_back = 529;
                height_back = 279;
                width_lace = 349;
                height_lace = 755;
                width_outside1 = 655;
                height_outside1 = 532;
                width_inside1 = 638;
                height_inside1 = 547;
                width_side2 = 758;
                height_side2 = 579;

                width_combine = 8094;
                height_combine = 810;

                x_front1_left = 1561;
                y_front1_left = 7;
                x_front2_left = 1001;
                y_front2_left = 140;
                x_ll1 = 0;
                y_ll1 = 278;
                x_ll2 = 402;
                y_ll2 = 231;
                x_lr1 = 3407;
                y_lr1 = 263;
                x_lr2 = 2878;
                y_lr2 = 231;
                x_lace_left = 2705;
                y_lace_left = 0;
                x_back_left = 517;
                y_back_left = 0;

                x_front1_right = 5915;
                y_front1_right = 7;
                x_front2_right = 5379;
                y_front2_right = 140;
                x_rr1 = 7439;
                y_rr1 = 278;
                x_rr2 = 6933;
                y_rr2 = 231;
                x_rl1 = 4049;
                y_rl1 = 263;
                x_rl2 = 4458;
                y_rl2 = 231;
                x_lace_right = 4630;
                y_lace_right = 0;
                x_back_right = 7047;
                y_back_right = 0;
                break;
            case 40:
                width_front1 = 626;
                height_front1 = 587;
                width_front2 = 1739;
                height_front2 = 658;
                width_back = 537;
                height_back = 283;
                width_lace = 354;
                height_lace = 766;
                width_outside1 = 664;
                height_outside1 = 537;
                width_inside1 = 646;
                height_inside1 = 552;
                width_side2 = 770;
                height_side2 = 586;

                width_combine = 8083;
                height_combine = 835;

                x_front1_left = 1544;
                y_front1_left = 0;
                x_front2_left = 976;
                y_front2_left = 132;
                x_ll1 = 0;
                y_ll1 = 298;
                x_ll2 = 407;
                y_ll2 = 249;
                x_lr1 = 3392;
                y_lr1 = 283;
                x_lr2 = 2855;
                y_lr2 = 249;
                x_lace_left = 2705;
                y_lace_left = 0;
                x_back_left = 0;
                y_back_left = 0;

                x_front1_right = 5913;
                y_front1_right = 0;
                x_front2_right = 5368;
                y_front2_right = 132;
                x_rr1 = 7419;
                y_rr1 = 298;
                x_rr2 = 6906;
                y_rr2 = 249;
                x_rl1 = 4045;
                y_rl1 = 283;
                x_rl2 = 4458;
                y_rl2 = 249;
                x_lace_right = 4612;
                y_lace_right = 0;
                x_back_right = 7546;
                y_back_right = 0;
                break;
            case 41:
                width_front1 = 640;
                height_front1 = 602;
                width_front2 = 1792;
                height_front2 = 678;
                width_back = 553;
                height_back = 290;
                width_lace = 363;
                height_lace = 790;
                width_outside1 = 682;
                height_outside1 = 550;
                width_inside1 = 664;
                height_inside1 = 566;
                width_side2 = 792;
                height_side2 = 600;

                width_combine = 7390;
                height_combine = 915;

                x_front1_left = 1529;
                y_front1_left = 0;
                x_front2_left = 936;
                y_front2_left = 133;
                x_ll1 = 0;
                y_ll1 = 365;
                x_ll2 = 416;
                y_ll2 = 315;
                x_lr1 = 3035;
                y_lr1 = 349;
                x_lr2 = 2482;
                y_lr2 = 315;
                x_lace_left = 2901;
                y_lace_left = 0;
                x_back_left = 0;
                y_back_left = 0;

                x_front1_right = 5225;
                y_front1_right = 0;
                x_front2_right = 4668;
                y_front2_right = 133;
                x_rr1 = 6709;
                y_rr1 = 365;
                x_rr2 = 6185;
                y_rr2 = 315;
                x_rl1 = 3699;
                y_rl1 = 349;
                x_rl2 = 4123;
                y_rl2 = 315;
                x_lace_right = 3707;
                y_lace_right = 0;
                x_back_right = 6838;
                y_back_right = 0;
                break;
            case 42:
                width_front1 = 647;
                height_front1 = 610;
                width_front2 = 1816;
                height_front2 = 687;
                width_back = 562;
                height_back = 294;
                width_lace = 368;
                height_lace = 802;
                width_outside1 = 689;
                height_outside1 = 556;
                width_inside1 = 671;
                height_inside1 = 572;
                width_side2 = 804;
                height_side2 = 608;

                width_combine = 7422;
                height_combine = 929;

                x_front1_left = 1530;
                y_front1_left = 0;
                x_front2_left = 932;
                y_front2_left = 134;
                x_ll1 = 0;
                y_ll1 = 373;
                x_ll2 = 420;
                y_ll2 = 321;
                x_lr1 = 3044;
                y_lr1 = 357;
                x_lr2 = 2483;
                y_lr2 = 321;
                x_lace_left = 2905;
                y_lace_left = 0;
                x_back_left = 0;
                y_back_left = 0;

                x_front1_right = 5250;
                y_front1_right = 0;
                x_front2_right = 4680;
                y_front2_right = 134;
                x_rr1 = 6734;
                y_rr1 = 373;
                x_rr2 = 6201;
                y_rr2 = 321;
                x_rl1 = 3715;
                y_rl1 = 357;
                x_rl2 = 4142;
                y_rl2 = 321;
                x_lace_right = 3723;
                y_lace_right = 0;
                x_back_right = 6861;
                y_back_right = 0;
                break;
            case 43:
                width_front1 = 661;
                height_front1 = 625;
                width_front2 = 1866;
                height_front2 = 705;
                width_back = 577;
                height_back = 300;
                width_lace = 377;
                height_lace = 826;
                width_outside1 = 706;
                height_outside1 = 568;
                width_inside1 = 688;
                height_inside1 = 586;
                width_side2 = 828;
                height_side2 = 622;

                width_combine = 7585;
                height_combine = 955;

                x_front1_left = 1541;
                y_front1_left = 0;
                x_front2_left = 938;
                y_front2_left = 140;
                x_ll1 = 0;
                y_ll1 = 386;
                x_ll2 = 429;
                y_ll2 = 332;
                x_lr1 = 3106;
                y_lr1 = 371;
                x_lr2 = 2530;
                y_lr2 = 332;
                x_lace_left = 2965;
                y_lace_left = 0;
                x_back_left = 0;
                y_back_left = 0;

                x_front1_right = 5385;
                y_front1_right = 0;
                x_front2_right = 4783;
                y_front2_right = 140;
                x_rr1 = 6880;
                y_rr1 = 386;
                x_rr2 = 6330;
                y_rr2 = 332;
                x_rl1 = 3794;
                y_rl1 = 371;
                x_rl2 = 4231;
                y_rl2 = 332;
                x_lace_right = 3797;
                y_lace_right = 0;
                x_back_right = 7008;
                y_back_right = 0;
                break;
            case 44:
                width_front1 = 668;
                height_front1 = 632;
                width_front2 = 1891;
                height_front2 = 715;
                width_back = 585;
                height_back = 304;
                width_lace = 382;
                height_lace = 837;
                width_outside1 = 715;
                height_outside1 = 574;
                width_inside1 = 696;
                height_inside1 = 592;
                width_side2 = 840;
                height_side2 = 629;

                width_combine = 7685;
                height_combine = 967;

                x_front1_left = 1577;
                y_front1_left = 0;
                x_front2_left = 953;
                y_front2_left = 133;
                x_ll1 = 0;
                y_ll1 = 390;
                x_ll2 = 432;
                y_ll2 = 336;
                x_lr1 = 3143;
                y_lr1 = 376;
                x_lr2 = 2562;
                y_lr2 = 336;
                x_lace_left = 3003;
                y_lace_left = 0;
                x_back_left = 0;
                y_back_left = 0;

                x_front1_right = 5442;
                y_front1_right = 0;
                x_front2_right = 4843;
                y_front2_right = 132;
                x_rr1 = 6972;
                y_rr1 = 390;
                x_rr2 = 6415;
                y_rr2 = 336;
                x_rl1 = 3845;
                y_rl1 = 376;
                x_rl2 = 4285;
                y_rl2 = 336;
                x_lace_right = 3848;
                y_lace_right = 0;
                x_back_right = 7100;
                y_back_right = 0;
                break;
            case 45:
                width_front1 = 682;
                height_front1 = 647;
                width_front2 = 1942;
                height_front2 = 734;
                width_back = 602;
                height_back = 312;
                width_lace = 391;
                height_lace = 860;
                width_outside1 = 732;
                height_outside1 = 586;
                width_inside1 = 711;
                height_inside1 = 604;
                width_side2 = 862;
                height_side2 = 642;

                width_combine = 7851;
                height_combine = 990;

                x_front1_left = 1624;
                y_front1_left = 0;
                x_front2_left = 972;
                y_front2_left = 138;
                x_ll1 = 0;
                y_ll1 = 404;
                x_ll2 = 444;
                y_ll2 = 348;
                x_lr1 = 3214;
                y_lr1 = 386;
                x_lr2 = 2612;
                y_lr2 = 348;
                x_lace_left = 3065;
                y_lace_left = 0;
                x_back_left = 0;
                y_back_left = 0;

                x_front1_right = 5544;
                y_front1_right = 0;
                x_front2_right = 4937;
                y_front2_right = 138;
                x_rr1 = 7119;
                y_rr1 = 404;
                x_rr2 = 6545;
                y_rr2 = 348;
                x_rl1 = 3926;
                y_rl1 = 386;
                x_rl2 = 4377;
                y_rl2 = 348;
                x_lace_right = 3926;
                y_lace_right = 0;
                x_back_right = 7249;
                y_back_right = 0;
                break;
        }
    }

}
