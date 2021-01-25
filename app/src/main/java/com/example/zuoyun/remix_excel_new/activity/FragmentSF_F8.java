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

public class FragmentSF_F8 extends BaseFragment {
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
    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint rectPaint, paint, paintRed, paintBlue, paintRectBlack;
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


    public void remixx() {
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(4580, 2254, Bitmap.Config.ARGB_8888);
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
            canvasCombine.drawBitmap(bitmapTemp, 3408 - width_front1 / 2, 1557, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 4187 - width_front1 / 2, 1557, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 1048 - width_front2 / 2, 0, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 3123 - width_front2 / 2, 0, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 24, 2204 - height_side2, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 1555, 1482 - height_side2, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 1479 - width_side2, 1482 - height_side2, null);

            //RL2
            bitmapTemp = Bitmap.createBitmap(792, 600, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2869, -1733, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRL2(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side2, height_side2, true);
            canvasCombine.drawBitmap(bitmapTemp, 3014 - width_side2, 2204 - height_side2, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 717, 2204 - height_inside1, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 2325 - width_inside1, 2204 - height_inside1, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 793 - width_outside1, 1482 - height_outside1, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 2242, 1482 - height_outside1, null);

            //lace_left
            bitmapTemp = Bitmap.createBitmap(363, 790, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -599, -400, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_lace);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lace, height_lace, true);
            canvasCombine.drawBitmap(bitmapTemp, 3874 - width_lace / 2, 1482 - height_lace, null);

            //lace_right
            bitmapTemp = Bitmap.createBitmap(363, 790, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1468, -400, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_lace);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lace, height_lace, true);
            canvasCombine.drawBitmap(bitmapTemp, 4332 - width_lace / 2, 1482 - height_lace, null);

            //back_left
            bitmapTemp = Bitmap.createBitmap(553, 290, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2153, -3212, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 3305 - width_back / 2, 793, null);

            //right_left
            bitmapTemp = Bitmap.createBitmap(553, 290, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3043, -3212, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f8_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 3305 - width_back / 2, 1170, null);


            //
            bitmapTemp.recycle();
            bitmapDB.recycle();
        }


        try {
            String nameCombine = orderItems.get(currentID).sku + "_" + orderItems.get(currentID).size + "_" + orderItems.get(currentID).color + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 150);

            //释放bitmap
            bitmapCombine.recycle();

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
                break;
        }
        width_front1 += 4;
        height_front1 += 4;
        width_front2 += 4;
        height_front2 += 4;
        width_back += 4;
        height_back += 4;
        width_lace += 4;
        height_lace += 4;
        width_outside1 += 4;
        height_outside1 += 4;
        width_inside1 += 4;
        height_inside1 += 4;
        width_side2 += 4;
        height_side2 += 4;
    }

}
