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

public class FragmentSF_F9 extends BaseFragment {
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

    int width_front1, width_front2, width_outside1, width_outside2, width_inside1, width_inside2, width_lace_big, width_lace_small, width_back, width_bar;
    int height_front1, height_front2, height_outside1, height_outside2, height_inside1, height_inside2, height_lace_big, height_lace_small, height_back, height_bar;
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
                        }
                    }
                    strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                    remixx();
                }
            }
        }.start();

    }

    void drawTextFront1(Canvas canvas, String LR) {
        canvas.drawRect(263, 199 - 22, 263 + 180, 199, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + LR + " " + time, 263, 199 - 2, paint);

        canvas.drawRect(27, 27 - 22, 27 + 220, 27, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 27, 27 - 2, paint);
    }
    void drawTextFront2_left(Canvas canvas) {
        canvas.save();
        canvas.rotate(26.4f, 323, 409);
        canvas.drawRect(323, 409 - 22, 323 + 200, 409, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).order_number, 323, 409 - 2, paint);
        canvas.restore();

        canvas.drawRect(879, 615 - 22, 879 + 90, 615, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "左", 879, 615 - 2, paint);

        canvas.save();
        canvas.rotate(-30.1f, 1394, 488);
        canvas.drawRect(1394, 488 - 22, 1394 + 190, 488, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).newCode_short, 1394, 488 - 2, paint);
        canvas.restore();
    }
    void drawTextFront2_right(Canvas canvas) {
        canvas.save();
        canvas.rotate(24.6f, 388, 470);
        canvas.drawRect(388, 470 - 22, 388 + 200, 470, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).order_number, 388, 470 - 2, paint);
        canvas.restore();

        canvas.drawRect(850, 615 - 22, 850 + 90, 615, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "右", 850, 615 - 2, paint);

        canvas.save();
        canvas.rotate(-26.1f, 1322, 496);
        canvas.drawRect(1322, 496 - 22, 1322 + 190, 496, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).newCode_short, 1322, 496 - 2, paint);
        canvas.restore();
    }
    void drawTextLL1(Canvas canvas) {
        canvas.save();
        canvas.rotate(4.4f, 201, 734);
        canvas.drawRect(201, 734 - 22, 201 + 400, 734, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).size + "左外" + orderItems.get(currentID).order_number + " " + time, 201, 734 - 2, paint);
        canvas.restore();
    }
    void drawTextRR1(Canvas canvas) {
        canvas.save();
        canvas.rotate(-4.4f, 315, 765);
        canvas.drawRect(315, 765 - 22, 315 + 400, 765, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).size + "右外" + orderItems.get(currentID).order_number + " " + time, 315, 765 - 2, paint);
        canvas.restore();
    }
    void drawTextLL2(Canvas canvas) {
        canvas.drawRect(21, 615 - 22, 21 + 500, 615, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).size + "左外" + orderItems.get(currentID).order_number + " " + time + " " + orderItems.get(currentID).newCode_short, 21, 615 - 2, paint);
    }
    void drawTextRR2(Canvas canvas) {
        canvas.drawRect(39, 615 - 22, 39 + 500, 615, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).size + "右外" + orderItems.get(currentID).order_number + " " + time + " " + orderItems.get(currentID).newCode_short, 39, 615 - 2, paint);
    }
    void drawTextLR1(Canvas canvas) {
        canvas.drawRect(306, 768 - 22, 306 + 400, 768, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).size + "左内" + orderItems.get(currentID).order_number + " " + time, 306, 768 - 2, paint);
    }
    void drawTextRL1(Canvas canvas) {
        canvas.drawRect(195, 768 - 22, 195 + 400, 768, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).size + "右内" + orderItems.get(currentID).order_number + " " + time, 195, 768 - 2, paint);
    }
    void drawTextLR2(Canvas canvas) {
        canvas.drawRect(30, 960 - 22, 30 + 500, 960, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).size + "左内" + orderItems.get(currentID).order_number + " " + time + " " + orderItems.get(currentID).newCode_short, 30, 960 - 2, paint);
    }
    void drawTextRL2(Canvas canvas) {
        canvas.drawRect(732, 960 - 22, 732 + 500, 960, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).size + "右内" + orderItems.get(currentID).order_number + " " + time + " " + orderItems.get(currentID).newCode_short, 732, 960 - 2, paint);
    }
    void drawTextBack(Canvas canvas, String LR) {
        canvas.drawRect(205, 61 - 10, 205 + 35, 61, rectPaint);
        canvas.drawText(LR, 205, 61 - 1, paint);
    }


    public void remixx() {
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(6780, 1910, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 4500) {

            //left_front1
            Bitmap bitmapTemp = Bitmap.createBitmap(701, 615, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1173, -2446, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f9_front1_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront1(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front1, height_front1, true);
            canvasCombine.drawBitmap(bitmapTemp, 1797 - width_front1 / 2, 3, null);

            //right_front1
            bitmapTemp = Bitmap.createBitmap(701, 615, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -377, -2442, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -701, 615, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront1(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front1, height_front1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4983 - width_front1 / 2, 3, null);

            //left_front2
            bitmapTemp = Bitmap.createBitmap(1821, 639, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -188, -3769, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f9_front2_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront2_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front2, height_front2, true);
            canvasCombine.drawBitmap(bitmapTemp, 1798 - width_front2 / 2, 300, null);

            //right_front2
            bitmapTemp = Bitmap.createBitmap(1821, 639, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -188, -3134, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1821, 639, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront2_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front2, height_front2, true);
            canvasCombine.drawBitmap(bitmapTemp, 4983 - width_front2 / 2, 300, null);

            //LL1
            bitmapTemp = Bitmap.createBitmap(916, 778, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3176, 999, 909, 798);
            Matrix matrix = new Matrix();
            matrix.postRotate(5.6f);
            matrix.postTranslate(25, -57);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f9_ll1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLL1(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside1, height_outside1, true);
            canvasCombine.drawBitmap(bitmapTemp, 1062 - width_outside1, 1013 + 40 * (45 - orderItems.get(currentID).size) / 9f, null);

            //RR1
            bitmapTemp = Bitmap.createBitmap(916, 778, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3176, 3585, 909, 798);
            matrix = new Matrix();
            matrix.postRotate(-5.6f);
            matrix.postTranslate(-13, 30);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -916, 778, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRR1(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside1, height_outside1, true);
            canvasCombine.drawBitmap(bitmapTemp, 5719, 1013 + 40 * (45 - orderItems.get(currentID).size) / 9f, null);

            //LL2
            bitmapTemp = Bitmap.createBitmap(556, 640, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -941, -1525, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f9_ll2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLL2(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside2, height_outside2, true);
            canvasCombine.drawBitmap(bitmapTemp, 1661 - width_outside2, 1671 - height_outside2 - 3 * (45 - orderItems.get(currentID).size) / 9f, null);

            //RR2
            bitmapTemp = Bitmap.createBitmap(556, 640, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1495, -507, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -556, 640, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRR2(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside2, height_outside2, true);
            canvasCombine.drawBitmap(bitmapTemp, 5120, 1671 - height_outside2 - 3 * (45 - orderItems.get(currentID).size) / 9f, null);

            //LR1
            bitmapTemp = Bitmap.createBitmap(907, 805, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3224, -1865, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f9_lr1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLR1(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside1, height_inside1, true);
            canvasCombine.drawBitmap(bitmapTemp, 2355, 997 + 45 * (45 - orderItems.get(currentID).size) / 9f, null);

            //RL1
            bitmapTemp = Bitmap.createBitmap(907, 805, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3233, -2727, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -907, 805, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRL1(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside1, height_inside1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4426 - width_inside1, 997 + 45 * (45 - orderItems.get(currentID).size) / 9f, null);

            //LR2
            bitmapTemp = Bitmap.createBitmap(1256, 986, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1507, -1180, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f9_lr2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLR2(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside2, height_inside2, true);
            canvasCombine.drawBitmap(bitmapTemp, 1714, 613 + 95 * (45 - orderItems.get(currentID).size) / 9f, null);

            //RL2
            bitmapTemp = Bitmap.createBitmap(1256, 986, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -228, -165, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1256, 986, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRL2(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside2, height_inside2, true);
            canvasCombine.drawBitmap(bitmapTemp, 5067 - width_inside2, 613 + 95 * (45 - orderItems.get(currentID).size) / 9f, null);


            //lace-big_left
            bitmapTemp = Bitmap.createBitmap(397, 855, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2510, -3507, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f9_lace_big_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextLace_big(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lace_big, height_lace_big, true);
            canvasCombine.drawBitmap(bitmapTemp, 491 - width_lace_big, 996 - height_lace_big, null);

            //lace-big_right
            bitmapTemp = Bitmap.createBitmap(397, 855, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2197, -2452, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -397, 855, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextLace_big(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lace_big, height_lace_big, true);
            canvasCombine.drawBitmap(bitmapTemp, 6290, 996 - height_lace_big, null);

            //lace_small_left
            bitmapTemp = Bitmap.createBitmap(326, 460, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2198, -3504, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f9_lace_small_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextLace_small(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lace_small, height_lace_small, true);
            canvasCombine.drawBitmap(bitmapTemp, 911 - width_lace_small, 3, null);

            //lace_small_right
            bitmapTemp = Bitmap.createBitmap(326, 460, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2580, -2448, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -326, 460, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextLace_small(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lace_small, height_lace_small, true);
            canvasCombine.drawBitmap(bitmapTemp, 5870, 3, null);

            //bar_left
            bitmapTemp = Bitmap.createBitmap(526, 143, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3080, -178, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f9_bar_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextLace_small(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_bar, height_bar, true);
            canvasCombine.drawBitmap(bitmapTemp, 3356 - width_bar, 3, null);

            //bar_right
            bitmapTemp = Bitmap.createBitmap(526, 143, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3096, -658, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -526, 143, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextLace_small(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_bar, height_bar, true);
            canvasCombine.drawBitmap(bitmapTemp, 3425, 3, null);

            //back_left
            bitmapTemp = Bitmap.createBitmap(448, 294, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3862, -103, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f9_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "zuo");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 3112 - width_back / 2, 205, null);

            //back_right
            bitmapTemp = Bitmap.createBitmap(448, 294, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3862, -600, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp,"you");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 3669 - width_back / 2, 205, null);


            //
            bitmapTemp.recycle();
            bitmapDB.recycle();
            bitmapCut.recycle();
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
                width_front1 = 633;
                height_front1 = 551;
                width_front2 = 1614;
                height_front2 = 567;
                width_outside1 = 832;
                height_outside1 = 709;
                width_outside2 = 496;
                height_outside2 = 581;
                width_inside1 = 830;
                height_inside1 = 725;
                width_inside2 = 1172;
                height_inside2 = 901;
                width_lace_big = 360;
                height_lace_big = 758;
                width_lace_small = 309;
                height_lace_small = 420;
                width_bar = 462;
                height_bar = 152;
                width_back = 401;
                height_back = 270;
                break;
            case 37:
                width_front1 = 646;
                height_front1 = 566;
                width_front2 = 1665;
                height_front2 = 586;
                width_outside1 = 854;
                height_outside1 = 729;
                width_outside2 = 512;
                height_outside2 = 596;
                width_inside1 = 854;
                height_inside1 = 746;
                width_inside2 = 1206;
                height_inside2 = 922;
                width_lace_big = 371;
                height_lace_big = 784;
                width_lace_small = 318;
                height_lace_small = 433;
                width_bar = 485;
                height_bar = 153;
                width_back = 420;
                height_back = 282;
                break;
            case 38:
                width_front1 = 654;
                height_front1 = 574;
                width_front2 = 1693;
                height_front2 = 595;
                width_outside1 = 868;
                height_outside1 = 738;
                width_outside2 = 519;
                height_outside2 = 603;
                width_inside1 = 866;
                height_inside1 = 756;
                width_inside2 = 1218;
                height_inside2 = 935;
                width_lace_big = 375;
                height_lace_big = 796;
                width_lace_small = 322;
                height_lace_small = 441;
                width_bar = 485;
                height_bar = 153;
                width_back = 420;
                height_back = 282;
                break;
            case 39:
                width_front1 = 669;
                height_front1 = 591;
                width_front2 = 1744;
                height_front2 = 614;
                width_outside1 = 891;
                height_outside1 = 758;
                width_outside2 = 534;
                height_outside2 = 619;
                width_inside1 = 890;
                height_inside1 = 776;
                width_inside2 = 1253;
                height_inside2 = 954;
                width_lace_big = 386;
                height_lace_big = 821;
                width_lace_small = 331;
                height_lace_small = 452;
                width_bar = 508;
                height_bar = 153;
                width_back = 439;
                height_back = 293;
                break;
            case 40:
                width_front1 = 676;
                height_front1 = 599;
                width_front2 = 1770;
                height_front2 = 621;
                width_outside1 = 904;
                height_outside1 = 768;
                width_outside2 = 542;
                height_outside2 = 628;
                width_inside1 = 903;
                height_inside1 = 786;
                width_inside2 = 1265;
                height_inside2 = 969;
                width_lace_big = 391;
                height_lace_big = 835;
                width_lace_small = 334;
                height_lace_small = 460;
                width_bar = 508;
                height_bar = 153;
                width_back = 439;
                height_back = 293;
                break;
            case 41:
                width_front1 = 691;
                height_front1 = 617;
                width_front2 = 1821;
                height_front2 = 639;
                width_outside1 = 928;
                height_outside1 = 788;
                width_outside2 = 556;
                height_outside2 = 643;
                width_inside1 = 927;
                height_inside1 = 805;
                width_inside2 = 1298;
                height_inside2 = 989;
                width_lace_big = 401;
                height_lace_big = 858;
                width_lace_small = 343;
                height_lace_small = 472;
                width_bar = 531;
                height_bar = 153;
                width_back = 457;
                height_back = 305;
                break;
            case 42:
                width_front1 = 698;
                height_front1 = 624;
                width_front2 = 1847;
                height_front2 = 649;
                width_outside1 = 939;
                height_outside1 = 797;
                width_outside2 = 563;
                height_outside2 = 651;
                width_inside1 = 940;
                height_inside1 = 816;
                width_inside2 = 1311;
                height_inside2 = 1002;
                width_lace_big = 406;
                height_lace_big = 873;
                width_lace_small = 348;
                height_lace_small = 477;
                width_bar = 531;
                height_bar = 153;
                width_back = 457;
                height_back = 305;
                break;
            case 43:
                width_front1 = 713;
                height_front1 = 644;
                width_front2 = 1900;
                height_front2 = 669;
                width_outside1 = 965;
                height_outside1 = 818;
                width_outside2 = 580;
                height_outside2 = 667;
                width_inside1 = 963;
                height_inside1 = 835;
                width_inside2 = 1342;
                height_inside2 = 1026;
                width_lace_big = 416;
                height_lace_big = 897;
                width_lace_small = 357;
                height_lace_small = 489;
                width_bar = 555;
                height_bar = 153;
                width_back = 476;
                height_back = 317;
                break;
            case 44:
                width_front1 = 721;
                height_front1 = 653;
                width_front2 = 1926;
                height_front2 = 677;
                width_outside1 = 977;
                height_outside1 = 826;
                width_outside2 = 585;
                height_outside2 = 674;
                width_inside1 = 976;
                height_inside1 = 845;
                width_inside2 = 1360;
                height_inside2 = 1036;
                width_lace_big = 421;
                height_lace_big = 910;
                width_lace_small = 361;
                height_lace_small = 498;
                width_bar = 555;
                height_bar = 153;
                width_back = 476;
                height_back = 317;
                break;
            case 45:
                width_front1 = 737;
                height_front1 = 671;
                width_front2 = 1978;
                height_front2 = 696;
                width_outside1 = 1002;
                height_outside1 = 847;
                width_outside2 = 601;
                height_outside2 = 690;
                width_inside1 = 1001;
                height_inside1 = 863;
                width_inside2 = 1392;
                height_inside2 = 1058;
                width_lace_big = 431;
                height_lace_big = 934;
                width_lace_small = 370;
                height_lace_small = 507;
                width_bar = 570;
                height_bar = 153;
                width_back = 488;
                height_back = 325;
                break;
        }

        width_front1 += 4;
        height_front1 += 4;
        width_front2 += 4;
        height_front2 += 4;
        width_outside1 += 4;
        height_outside1 += 4;
        width_outside2 += 4;
        height_outside2 += 4;
        width_inside1 += 4;
        height_inside1 += 4;
        width_inside2 += 4;
        height_inside2 += 4;
        width_lace_big += 4;
        height_lace_big += 4;
        width_lace_small += 4;
        height_lace_small += 4;
        width_bar += 4;
        height_bar += 4;
        width_back += 4;
        height_back += 4;
    }

}
