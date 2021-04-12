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

public class FragmentME extends BaseFragment {
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

    int width_front1, width_front2, width_back1, width_back2, width_back3, width_back4, width_outside1, width_outside2, width_outside3, width_inside1, width_inside2, width_inside3;
    int height_front1, height_front2, height_back1, height_back2, height_back3, height_back4, height_outside1, height_outside2, height_outside3, height_inside1, height_inside2, height_inside3;
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

    void drawTextFront1_left(Canvas canvas) {
        canvas.drawRect(298, 532 - 22, 298 + 65, 532, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "左" + orderItems.get(currentID).color, 298, 532 - 2, paint);

        canvas.save();
        canvas.rotate(73f, 89, 245);
        canvas.drawRect(89, 245 - 22, 89 + 165, 245, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number, 89, 245 - 2, paint);
        canvas.restore();
    }
    void drawTextFront1_right(Canvas canvas) {
        canvas.drawRect(320, 532 - 22, 320 + 65, 532, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "右" + orderItems.get(currentID).color, 320, 532 - 2, paint);

        canvas.save();
        canvas.rotate(72.1f, 86, 213);
        canvas.drawRect(86, 213 - 22, 86 + 165, 213, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).order_number, 86, 213 - 2, paint);
        canvas.restore();
    }
    void drawTextFront2_left(Canvas canvas) {
        canvas.save();
        canvas.rotate(41.1f, 131, 170);
        canvas.drawRect(131, 170 - 22, 131 + 290, 170, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "左" + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number, 131, 170 - 2, paint);
        canvas.restore();
    }
    void drawTextFront2_right(Canvas canvas) {
        canvas.save();
        canvas.rotate(36.4f, 166, 210);
        canvas.drawRect(166, 210 - 22, 166 + 200, 210, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "右" + orderItems.get(currentID).color, 166, 210 - 2, paint);
        canvas.restore();
    }
    void drawTextBack1_left(Canvas canvas) {
        canvas.drawRect(556, 409 - 22, 556 + 430, 409, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "左" + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number + " " + time + " " + orderItems.get(currentID).newCode_short, 556, 409 - 2, paint);
    }
    void drawTextBack1_right(Canvas canvas) {
        canvas.drawRect(556, 409 - 22, 556 + 430, 409, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "右" + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number + " " + time + " " + orderItems.get(currentID).newCode_short, 556, 409 - 2, paint);
    }
    void drawTextBack2_left(Canvas canvas) {
        canvas.save();
        canvas.rotate(4.5f, 731, 425);
        canvas.drawRect(731, 425 - 22, 731 + 300, 425, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "左" + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number, 731, 425 - 2, paint);
        canvas.restore();
    }
    void drawTextBack2_right(Canvas canvas) {
        canvas.save();
        canvas.rotate(4.5f, 731, 425);
        canvas.drawRect(731, 425 - 22, 731 + 300, 425, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "右" + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number, 731, 425 - 2, paint);
        canvas.restore();
    }
    void drawTextBack3_left(Canvas canvas) {
        canvas.save();
        canvas.rotate(2.75f, 137, 362);
        canvas.drawRect(137, 362 - 22, 137 + 430, 362, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "左" + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number + " " + time + " " + orderItems.get(currentID).newCode_short, 137, 362 - 2, paint);
        canvas.restore();
    }
    void drawTextBack3_right(Canvas canvas) {
        canvas.save();
        canvas.rotate(2.75f, 137, 362);
        canvas.drawRect(137, 362 - 22, 137 + 430, 362, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "右" + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number + " " + time + " " + orderItems.get(currentID).newCode_short, 137, 362 - 2, paint);
        canvas.restore();
    }
    void drawTextOutside1_left(Canvas canvas) {
        canvas.save();
        canvas.rotate(66.9f, 165, 266);
        canvas.drawRect(165, 266 - 22, 165 + 300, 266, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "左外" + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number, 165, 266 - 2, paint);
        canvas.restore();
    }
    void drawTextOutside1_right(Canvas canvas) {
        canvas.save();
        canvas.rotate(-66.4f, 1011, 536);
        canvas.drawRect(1011, 536 - 22, 1011 + 300, 536, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "右外" + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number, 1011, 536 - 2, paint);
        canvas.restore();
    }
    void drawTextInside1_left(Canvas canvas) {
        canvas.save();
        canvas.rotate(-58.6f, 1005, 502);
        canvas.drawRect(1005, 502 - 22, 1005 + 300, 502, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "左内" + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number, 1005, 502 - 2, paint);
        canvas.restore();
    }
    void drawTextInside1_right(Canvas canvas) {
        canvas.save();
        canvas.rotate(59.7f, 153, 288);
        canvas.drawRect(153, 288 - 22, 153 + 300, 288, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "右内" + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number, 153, 288 - 2, paint);
        canvas.restore();
    }
    void drawTextOutside2_left(Canvas canvas) {
        canvas.drawRect(21, 380 - 22, 21 + 25, 380, rectPaint);
        canvas.drawText("左" , 21, 380 - 2, paint);
    }
    void drawTextOutside2_right(Canvas canvas) {
        canvas.drawRect(574, 380 - 22, 574 + 25, 380, rectPaint);
        canvas.drawText("右" , 574, 380 - 2, paint);
    }
    void drawTextInside2_right(Canvas canvas) {
        canvas.drawRect(21, 380 - 22, 21 + 25, 380, rectPaint);
        canvas.drawText("右" , 21, 380 - 2, paint);
    }
    void drawTextInside2_left(Canvas canvas) {
        canvas.drawRect(574, 380 - 22, 574 + 25, 380, rectPaint);
        canvas.drawText("左" , 574, 380 - 2, paint);
    }
    void drawTextOutside3_left(Canvas canvas) {
        canvas.drawRect(238, 488 - 22, 238 + 25, 488, rectPaint);
        canvas.drawText("左" , 238, 488 - 2, paint);
    }
    void drawTextInside3_right(Canvas canvas) {
        canvas.save();
        canvas.rotate(4.6f, 249, 447);
        canvas.drawRect(249, 447 - 22, 249 + 25, 447, rectPaint);
        canvas.drawText("右", 249, 447 - 2, paint);
        canvas.restore();
    }
    void drawTextInside3_left(Canvas canvas) {
        canvas.save();
        canvas.rotate(-4.6f, 134, 446);
        canvas.drawRect(134, 446 - 22, 134 + 25, 446, rectPaint);
        canvas.drawText("左", 134, 446 - 2, paint);
        canvas.restore();
    }
    void drawTextOutide3_right(Canvas canvas) {
        canvas.drawRect(130, 488 - 22, 130 + 25, 488, rectPaint);
        canvas.drawText("右" , 130, 488 - 2, paint);
    }




    public void remixx() {
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(5764, 2260, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.size() == 24) {
            //left_front1
            Bitmap bitmapTemp = Bitmap.createBitmap(676, 542, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_front1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront1_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front1, height_front1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4925 - width_front1 / 2, 24, null);

            //left_front2
            bitmapTemp = Bitmap.createBitmap(1510, 524, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(9), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_front2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront2_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front2, height_front2, true);
            canvasCombine.drawBitmap(bitmapTemp, 4925 - width_front2 / 2 - 4 * (45 - orderItems.get(currentID).size) / 9f, 358, null);

            //left_back1
            bitmapTemp = Bitmap.createBitmap(1519, 486, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back1, height_back1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4908 - width_back1 / 2, 1205, null);

            //left_back2
            bitmapTemp = Bitmap.createBitmap(1378, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(8), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack2_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back2, height_back2, true);
            canvasCombine.drawBitmap(bitmapTemp, 4907 - width_back2 / 2, 703, null);

            //left_back3
            bitmapTemp = Bitmap.createBitmap(1161, 405, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(16), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back3);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack3_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back3, height_back3, true);
            canvasCombine.drawBitmap(bitmapTemp, 3546 - width_back3 / 2, 1106 - height_back3, null);

            //left_back4
            bitmapTemp = Bitmap.createBitmap(110, 248, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(22), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back4);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextFrontL(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back4, height_back4, true);
            canvasCombine.drawBitmap(bitmapTemp, 4935, 1976, null);

            //left_outside1
            bitmapTemp = Bitmap.createBitmap(1294, 561, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutside1_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside1, height_outside1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4328 - width_outside1, 24 + 15 * (45 - orderItems.get(currentID).size) / 9f, null);

            //left_outside2
            bitmapTemp = Bitmap.createBitmap(619, 464, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(11), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutside2_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside2, height_outside2, true);
            canvasCombine.drawBitmap(bitmapTemp, 4947 - width_outside2, 1741 + 3 * (45 - orderItems.get(currentID).size) / 9f, null);

            //left_outside3
            bitmapTemp = Bitmap.createBitmap(391, 494, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(18), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside3);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutside3_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside3, height_outside3, true);
            canvasCombine.drawBitmap(bitmapTemp, 2911 + 48 * (45 - orderItems.get(currentID).size) / 9f, 1659 - height_outside3, null);

            //left_inside1
            bitmapTemp = Bitmap.createBitmap(1289, 553, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside1_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside1, height_inside1, true);
            canvasCombine.drawBitmap(bitmapTemp, 2977, 1648 + 6 * (45 - orderItems.get(currentID).size) / 9f, null);

            //left_inside2
            bitmapTemp = Bitmap.createBitmap(619, 464, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(10), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside2_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside2, height_inside2, true);
            canvasCombine.drawBitmap(bitmapTemp, 5030, 1741, null);

            //left_inside3
            bitmapTemp = Bitmap.createBitmap(401, 457, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(17), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside3);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside3_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside3, height_inside3, true);
            canvasCombine.drawBitmap(bitmapTemp, 3819 - width_inside3 - 48 * (45 - orderItems.get(currentID).size) / 9f, 1633 - height_inside3, null);


            //right
            //right_front1
            bitmapTemp = Bitmap.createBitmap(676, 542, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_front1);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -676, 542, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront1_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front1, height_front1, true);
            canvasCombine.drawBitmap(bitmapTemp, 838 - width_front1 / 2, 24, null);

            //right_front2
            bitmapTemp = Bitmap.createBitmap(1510, 524, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(13), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_front2);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1510, 524, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront2_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front2, height_front2, true);
            canvasCombine.drawBitmap(bitmapTemp, 838 - width_front2 / 2 + 4 * (45 - orderItems.get(currentID).size) / 9f, 358, null);

            //right_back1
            bitmapTemp = Bitmap.createBitmap(1519, 486, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back1, height_back1, true);
            canvasCombine.drawBitmap(bitmapTemp, 855 - width_back1 / 2, 1205, null);

            //right_back2
            bitmapTemp = Bitmap.createBitmap(1378, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(12), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack2_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back2, height_back2, true);
            canvasCombine.drawBitmap(bitmapTemp, 856 - width_back2 / 2, 703, null);

            //right_back3
            bitmapTemp = Bitmap.createBitmap(1161, 405, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(19), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back3);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack3_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back3, height_back3, true);
            canvasCombine.drawBitmap(bitmapTemp, 2217 - width_back3 / 2, 1106 - height_back3, null);

            //right_back4
            bitmapTemp = Bitmap.createBitmap(110, 248, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(23), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back4);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextFrontL(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back4, height_back4, true);
            canvasCombine.drawBitmap(bitmapTemp, 718, 1976, null);

            //right_outside1
            bitmapTemp = Bitmap.createBitmap(1294, 561, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(7), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside1);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1294, 561, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutside1_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside1, height_outside1, true);
            canvasCombine.drawBitmap(bitmapTemp, 1435, 24 + 15 * (45 - orderItems.get(currentID).size) / 9f, null);

            //right_outside2
            bitmapTemp = Bitmap.createBitmap(619, 464, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(15), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside2);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -619, 464, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutside2_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside2, height_outside2, true);
            canvasCombine.drawBitmap(bitmapTemp, 816, 1741, null);

            //right_outside3
            bitmapTemp = Bitmap.createBitmap(391, 494, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(21), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside3);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -391, 494, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutide3_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside3, height_outside3, true);
            canvasCombine.drawBitmap(bitmapTemp, 2852 - width_outside3 - 48 * (45 - orderItems.get(currentID).size) / 9f, 1659 - height_outside3, null);

            //right_inside1
            bitmapTemp = Bitmap.createBitmap(1289, 553, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(6), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside1);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1289, 553, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside1_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside1, height_inside1, true);
            canvasCombine.drawBitmap(bitmapTemp, 2786 - width_inside1, 1648 + 6 * (45 - orderItems.get(currentID).size) / 9f, null);

            //right_inside2
            bitmapTemp = Bitmap.createBitmap(619, 464, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(14), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside2);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -619, 464, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside2_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside2, height_inside2, true);
            canvasCombine.drawBitmap(bitmapTemp, 733 - width_inside2, 1741, null);

            //right_inside3
            bitmapTemp = Bitmap.createBitmap(401, 457, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(20), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside3);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -401, 457, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside3_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside3, height_inside3, true);
            canvasCombine.drawBitmap(bitmapTemp, 1944 + 48 * (45 - orderItems.get(currentID).size) / 9f, 1633 - height_inside3, null);


            //
            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (MainActivity.instance.bitmaps.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 5000) {

            //left_front1
            Bitmap bitmapTemp = Bitmap.createBitmap(676, 542, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1400, -1121, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_front1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront1_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front1, height_front1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4925 - width_front1 / 2, 24, null);

            //left_front2
            bitmapTemp = Bitmap.createBitmap(1510, 524, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -984, -1376, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_front2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront2_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front2, height_front2, true);
            canvasCombine.drawBitmap(bitmapTemp, 4925 - width_front2 / 2 - 4 * (45 - orderItems.get(currentID).size) / 9f, 358, null);

            //left_back1
            bitmapTemp = Bitmap.createBitmap(1519, 486, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3370, -104, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back1, height_back1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4908 - width_back1 / 2, 1205, null);

            //left_back2
            bitmapTemp = Bitmap.createBitmap(1378, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3440, -590, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack2_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back2, height_back2, true);
            canvasCombine.drawBitmap(bitmapTemp, 4907 - width_back2 / 2, 703, null);

            //left_back3
            bitmapTemp = Bitmap.createBitmap(1161, 405, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3549, -1044, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back3);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack3_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back3, height_back3, true);
            canvasCombine.drawBitmap(bitmapTemp, 3546 - width_back3 / 2, 1106 - height_back3, null);

            //left_back4
            bitmapTemp = Bitmap.createBitmap(110, 248, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4075, -1449, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back4);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextFrontL(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back4, height_back4, true);
            canvasCombine.drawBitmap(bitmapTemp, 4935, 1976, null);

            //left_outside1
            bitmapTemp = Bitmap.createBitmap(1294, 561, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2076, -568, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutside1_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside1, height_outside1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4328 - width_outside1, 24 + 15 * (45 - orderItems.get(currentID).size) / 9f, null);

            //left_outside2
            bitmapTemp = Bitmap.createBitmap(619, 464, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2076, -104, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutside2_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside2, height_outside2, true);
            canvasCombine.drawBitmap(bitmapTemp, 4947 - width_outside2, 1741 + 3 * (45 - orderItems.get(currentID).size) / 9f, null);

            //left_outside3
            bitmapTemp = Bitmap.createBitmap(391, 494, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1787, -611, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside3);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutside3_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside3, height_outside3, true);
            canvasCombine.drawBitmap(bitmapTemp, 2911 + 48 * (45 - orderItems.get(currentID).size) / 9f, 1659 - height_outside3, null);

            //left_inside1
            bitmapTemp = Bitmap.createBitmap(1289, 553, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -111, -568, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside1_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside1, height_inside1, true);
            canvasCombine.drawBitmap(bitmapTemp, 2977, 1648 + 6 * (45 - orderItems.get(currentID).size) / 9f, null);

            //left_inside2
            bitmapTemp = Bitmap.createBitmap(619, 464, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -781, -104, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside2_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside2, height_inside2, true);
            canvasCombine.drawBitmap(bitmapTemp, 5030, 1741, null);

            //left_inside3
            bitmapTemp = Bitmap.createBitmap(401, 457, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1283, -648, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside3);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside3_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside3, height_inside3, true);
            canvasCombine.drawBitmap(bitmapTemp, 3819 - width_inside3 - 48 * (45 - orderItems.get(currentID).size) / 9f, 1633 - height_inside3, null);


            //right
            //right_front1
            bitmapTemp = Bitmap.createBitmap(676, 542, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1405, -2917, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_front1);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -676, 542, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront1_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front1, height_front1, true);
            canvasCombine.drawBitmap(bitmapTemp, 838 - width_front1 / 2, 24, null);

            //right_front2
            bitmapTemp = Bitmap.createBitmap(1510, 524, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -987, -3172, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_front2);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1510, 524, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront2_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front2, height_front2, true);
            canvasCombine.drawBitmap(bitmapTemp, 838 - width_front2 / 2 + 4 * (45 - orderItems.get(currentID).size) / 9f, 358, null);

            //right_back1
            bitmapTemp = Bitmap.createBitmap(1519, 486, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3370, -1900, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back1, height_back1, true);
            canvasCombine.drawBitmap(bitmapTemp, 855 - width_back1 / 2, 1205, null);

            //right_back2
            bitmapTemp = Bitmap.createBitmap(1378, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3440, -2386, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack2_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back2, height_back2, true);
            canvasCombine.drawBitmap(bitmapTemp, 856 - width_back2 / 2, 703, null);

            //right_back3
            bitmapTemp = Bitmap.createBitmap(1161, 405, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3549, -2840, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back3);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack3_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back3, height_back3, true);
            canvasCombine.drawBitmap(bitmapTemp, 2217 - width_back3 / 2, 1106 - height_back3, null);

            //right_back4
            bitmapTemp = Bitmap.createBitmap(110, 248, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4075, -3245, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back4);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextFrontL(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back4, height_back4, true);
            canvasCombine.drawBitmap(bitmapTemp, 718, 1976, null);

            //right_outside1
            bitmapTemp = Bitmap.createBitmap(1294, 561, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -111, -2364, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside1);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1294, 561, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutside1_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside1, height_outside1, true);
            canvasCombine.drawBitmap(bitmapTemp, 1435, 24 + 15 * (45 - orderItems.get(currentID).size) / 9f, null);

            //right_outside2
            bitmapTemp = Bitmap.createBitmap(619, 464, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -786, -1900, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside2);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -619, 464, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutside2_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside2, height_outside2, true);
            canvasCombine.drawBitmap(bitmapTemp, 816, 1741, null);

            //right_outside3
            bitmapTemp = Bitmap.createBitmap(391, 494, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1303, -2407, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside3);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -391, 494, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutide3_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside3, height_outside3, true);
            canvasCombine.drawBitmap(bitmapTemp, 2852 - width_outside3 - 48 * (45 - orderItems.get(currentID).size) / 9f, 1659 - height_outside3, null);

            //right_inside1
            bitmapTemp = Bitmap.createBitmap(1289, 553, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2081, -2364, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside1);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1289, 553, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside1_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside1, height_inside1, true);
            canvasCombine.drawBitmap(bitmapTemp, 2786 - width_inside1, 1648 + 6 * (45 - orderItems.get(currentID).size) / 9f, null);

            //right_inside2
            bitmapTemp = Bitmap.createBitmap(619, 464, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2081, -1900, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside2);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -619, 464, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside2_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside2, height_inside2, true);
            canvasCombine.drawBitmap(bitmapTemp, 733 - width_inside2, 1741, null);

            //right_inside3
            bitmapTemp = Bitmap.createBitmap(401, 457, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1797, -2444, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside3);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -401, 457, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside3_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside3, height_inside3, true);
            canvasCombine.drawBitmap(bitmapTemp, 1944 + 48 * (45 - orderItems.get(currentID).size) / 9f, 1633 - height_inside3, null);


            //
            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight() && MainActivity.instance.bitmaps.get(0).getWidth() == 4550) {
            //left_front1
            Bitmap bitmapTemp = Bitmap.createBitmap(676, 542, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 873, 76, 676, 542);
            Matrix matrix = new Matrix();
            matrix.postRotate(0.2f);
            matrix.postTranslate(2, -3);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_front1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront1_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front1, height_front1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4925 - width_front1 / 2, 24, null);

            //left_front2
            bitmapTemp = Bitmap.createBitmap(1510, 524, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1911, 825, 1514, 528);
            matrix = new Matrix();
            matrix.postRotate(0.33f, 0, 60);
            matrix.postTranslate(2, -6);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_front2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront2_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front2, height_front2, true);
            canvasCombine.drawBitmap(bitmapTemp, 4925 - width_front2 / 2 - 4 * (45 - orderItems.get(currentID).size) / 9f, 358, null);

            //left_back1
            bitmapTemp = Bitmap.createBitmap(1519, 486, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 77, 2112, 1514, 495);
            matrix = new Matrix();
            matrix.postRotate(-0.53f, 197, 0);
            matrix.postScale(1.01f, 1.047f, 0, -12);
            matrix.postTranslate(-6, -14);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back1, height_back1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4908 - width_back1 / 2, 1205, null);

            //left_back2
            bitmapTemp = Bitmap.createBitmap(1378, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 144, 810, 1514, 495);
            matrix = new Matrix();
            matrix.postRotate(-0.49f, 79, 0);
            matrix.postTranslate(-2, -1);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack2_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back2, height_back2, true);
            canvasCombine.drawBitmap(bitmapTemp, 4907 - width_back2 / 2, 703, null);

            //left_back3
            bitmapTemp = Bitmap.createBitmap(1161, 405, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 205, 3478, 1161, 405);
            matrix = new Matrix();
            matrix.postRotate(-0.5f, 0, 355);
            matrix.postTranslate(0, 1);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back3);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack3_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back3, height_back3, true);
            canvasCombine.drawBitmap(bitmapTemp, 3546 - width_back3 / 2, 1106 - height_back3, null);

            //left_back4
            bitmapTemp = Bitmap.createBitmap(110, 248, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4273, 4227, 110, 248);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back4);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextFrontL(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back4, height_back4, true);
            canvasCombine.drawBitmap(bitmapTemp, 4935, 1976, null);

            //left_outside1
            bitmapTemp = Bitmap.createBitmap(1294, 561, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2016, 1576, 1298, 563);
            matrix = new Matrix();
            matrix.postRotate(-0.93f, 259, 540);
            matrix.postTranslate(4, 3);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutside1_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside1, height_outside1, true);
            canvasCombine.drawBitmap(bitmapTemp, 4328 - width_outside1, 24 + 15 * (45 - orderItems.get(currentID).size) / 9f, null);

            //left_outside2
            bitmapTemp = Bitmap.createBitmap(619, 464, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3735, 589, 744, 297);
            matrix = new Matrix();
            matrix.postTranslate(17, 358);
            matrix.postRotate(-35.25f, 22, 461);
            matrix.postTranslate(2, -2);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutside2_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside2, height_outside2, true);
            canvasCombine.drawBitmap(bitmapTemp, 4947 - width_outside2, 1741 + 3 * (45 - orderItems.get(currentID).size) / 9f, null);

            //left_outside3
            bitmapTemp = Bitmap.createBitmap(391, 494, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3851, 2657, 510, 303);
            matrix = new Matrix();
            matrix.postTranslate(-116, 478);
            matrix.postRotate(76.77f, 390, 488);
            matrix.postTranslate(-1, -2);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside3);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutside3_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside3, height_outside3, true);
            canvasCombine.drawBitmap(bitmapTemp, 2911 + 48 * (45 - orderItems.get(currentID).size) / 9f, 1659 - height_outside3, null);

            //left_inside1
            bitmapTemp = Bitmap.createBitmap(1289, 553, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2018, 2360, 1292, 554);
            matrix = new Matrix();
            matrix.postRotate(-1.15f, 1005, 528);
            matrix.postScale(1.003f, 1.033f, -8, -5);
            matrix.postTranslate(3, -6);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside1_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside1, height_inside1, true);
            canvasCombine.drawBitmap(bitmapTemp, 2977, 1648 + 6 * (45 - orderItems.get(currentID).size) / 9f, null);

            //left_inside2
            bitmapTemp = Bitmap.createBitmap(619, 464, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3734, 69, 743, 299);
            matrix = new Matrix();
            matrix.postTranslate(-3, -66);
            matrix.postRotate(35.1f, 0, 25);
            matrix.postTranslate(2, -1);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside2_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside2, height_inside2, true);
            canvasCombine.drawBitmap(bitmapTemp, 5030, 1741, null);

            //left_inside3
            bitmapTemp = Bitmap.createBitmap(401, 457, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3859, 2138, 494, 300);
            matrix = new Matrix();
            matrix.postTranslate(-4, 446);
            matrix.postRotate(-73.11f, 0, 455);
            matrix.postTranslate(2, 0);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside3);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside3_left(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside3, height_inside3, true);
            canvasCombine.drawBitmap(bitmapTemp, 3819 - width_inside3 - 48 * (45 - orderItems.get(currentID).size) / 9f, 1633 - height_inside3, null);


            //right
            //right_front1
            bitmapTemp = Bitmap.createBitmap(676, 542, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 118, 73, 679, 546);
            matrix = new Matrix();
            matrix.postRotate(-0.45f);
            matrix.postTranslate(-1, 0);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_front1);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -676, 542, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront1_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front1, height_front1, true);
            canvasCombine.drawBitmap(bitmapTemp, 838 - width_front1 / 2, 24, null);

            //right_front2
            bitmapTemp = Bitmap.createBitmap(1510, 524, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1911, 70, 1514, 533);
            matrix = new Matrix();
            matrix.postRotate(0.52f, 1162, 0);
            matrix.postTranslate(3, 0);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_front2);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1510, 524, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront2_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front2, height_front2, true);
            canvasCombine.drawBitmap(bitmapTemp, 838 - width_front2 / 2 + 4 * (45 - orderItems.get(currentID).size) / 9f, 358, null);

            //right_back1
            bitmapTemp = Bitmap.createBitmap(1519, 486, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 77, 2791, 1515, 499);
            matrix = new Matrix();
            matrix.postRotate(-0.57f, 205, 0);
            matrix.postScale(1.01f, 1.05f, 0, -13);
            matrix.postTranslate(-7, -17);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back1, height_back1, true);
            canvasCombine.drawBitmap(bitmapTemp, 855 - width_back1 / 2, 1205, null);

            //right_back2
            bitmapTemp = Bitmap.createBitmap(1378, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 143, 1458, 1384, 464);
            matrix = new Matrix();
            matrix.postRotate(-0.51f, 83, 0);
            matrix.postTranslate(-1, -1);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack2_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back2, height_back2, true);
            canvasCombine.drawBitmap(bitmapTemp, 856 - width_back2 / 2, 703, null);

            //right_back3
            bitmapTemp = Bitmap.createBitmap(1161, 405, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 205, 4071, 1163, 409);
            matrix = new Matrix();
            matrix.postRotate(-0.5f, 0, 357);
            matrix.postTranslate(0, 1);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back3);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack3_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back3, height_back3, true);
            canvasCombine.drawBitmap(bitmapTemp, 2217 - width_back3 / 2, 1106 - height_back3, null);

            //right_back4
            bitmapTemp = Bitmap.createBitmap(110, 248, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3825, 4227, 110, 248);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_back4);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextFrontL(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back4, height_back4, true);
            canvasCombine.drawBitmap(bitmapTemp, 718, 1976, null);

            //right_outside1
            bitmapTemp = Bitmap.createBitmap(1294, 561, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2019, 3916, 1295, 561);
            matrix = new Matrix();
            matrix.postRotate(1.4f, 532, 551);
            matrix.postTranslate(-10, -5);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside1);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1294, 561, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutside1_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside1, height_outside1, true);
            canvasCombine.drawBitmap(bitmapTemp, 1435, 24 + 15 * (45 - orderItems.get(currentID).size) / 9f, null);

            //right_outside2
            bitmapTemp = Bitmap.createBitmap(619, 464, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3735, 1106, 743, 297);
            matrix = new Matrix();
            matrix.postTranslate(-3, -64);
            matrix.postRotate(35.02f, 0, 25);
            matrix.postTranslate(0, 0);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside2);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -619, 464, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutside2_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside2, height_outside2, true);
            canvasCombine.drawBitmap(bitmapTemp, 816, 1741, null);

            //right_outside3
            bitmapTemp = Bitmap.createBitmap(391, 494, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3851, 3184, 506, 304);
            matrix = new Matrix();
            matrix.postTranslate(0, 479);
            matrix.postRotate(-77f, 0, 488);
            matrix.postTranslate(1, -2);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_outside3);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -391, 494, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutide3_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside3, height_outside3, true);
            canvasCombine.drawBitmap(bitmapTemp, 2852 - width_outside3 - 48 * (45 - orderItems.get(currentID).size) / 9f, 1659 - height_outside3, null);

            //right_inside1
            bitmapTemp = Bitmap.createBitmap(1289, 553, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2017, 3139, 1295, 557);
            matrix = new Matrix();
            matrix.postRotate(0.91f, 59, 278);
            matrix.postScale(1, 1.02f);
            matrix.postTranslate(1, -8);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside1);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1289, 553, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside1_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside1, height_inside1, true);
            canvasCombine.drawBitmap(bitmapTemp, 2786 - width_inside1, 1648 + 6 * (45 - orderItems.get(currentID).size) / 9f, null);

            //right_inside2
            bitmapTemp = Bitmap.createBitmap(619, 464, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3735, 1624, 744, 294);
            matrix = new Matrix();
            matrix.postTranslate(-120, -62);
            matrix.postRotate(-35.16f, 619, 24);
            matrix.postTranslate(-1, 0);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside2);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -619, 464, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside2_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside2, height_inside2, true);
            canvasCombine.drawBitmap(bitmapTemp, 733 - width_inside2, 1741, null);

            //right_inside3
            bitmapTemp = Bitmap.createBitmap(401, 457, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3859, 3710, 491, 297);
            matrix = new Matrix();
            matrix.postTranslate(-199, 281);
            matrix.postRotate(73.25f, 226, 445);
            matrix.postTranslate(4, -3);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.me_left_inside3);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -401, 457, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInside3_right(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside3, height_inside3, true);
            canvasCombine.drawBitmap(bitmapTemp, 1944 + 48 * (45 - orderItems.get(currentID).size) / 9f, 1633 - height_inside3, null);


            //
            bitmapCut.recycle();
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
                width_front1 = 627;
                height_front1 = 485;
                width_front2 = 1373;
                height_front2 = 460;
                width_back1 = 1351;
                height_back1 = 447;
                width_back2 = 1215;
                height_back2 = 416;
                width_back3 = 1024;
                height_back3 = 372;
                width_back4 = 111;
                height_back4 = 236;
                width_outside1 = 1141;
                height_outside1 = 515;
                width_outside2 = 547;
                height_outside2 = 425;
                width_outside3 = 343;
                height_outside3 = 452;
                width_inside1 = 1138;
                height_inside1 = 506;
                width_inside3 = 354;
                height_inside3 = 419;
                break;
            case 37:
                width_front1 = 636;
                height_front1 = 495;
                width_front2 = 1399;
                height_front2 = 473;
                width_back1 = 1383;
                height_back1 = 453;
                width_back2 = 1247;
                height_back2 = 424;
                width_back3 = 1051;
                height_back3 = 378;
                width_back4 = 111;
                height_back4 = 236;
                width_outside1 = 1173;
                height_outside1 = 525;
                width_outside2 = 562;
                height_outside2 = 433;
                width_outside3 = 353;
                height_outside3 = 461;
                width_inside1 = 1167;
                height_inside1 = 516;
                width_inside3 = 365;
                height_inside3 = 427;
                break;
            case 38:
                width_front1 = 646;
                height_front1 = 506;
                width_front2 = 1427;
                height_front2 = 485;
                width_back1 = 1417;
                height_back1 = 461;
                width_back2 = 1280;
                height_back2 = 431;
                width_back3 = 1078;
                height_back3 = 385;
                width_back4 = 111;
                height_back4 = 236;
                width_outside1 = 1203;
                height_outside1 = 533;
                width_outside2 = 577;
                height_outside2 = 440;
                width_outside3 = 360;
                height_outside3 = 469;
                width_inside1 = 1198;
                height_inside1 = 525;
                width_inside3 = 373;
                height_inside3 = 434;
                break;
            case 39:
                width_front1 = 657;
                height_front1 = 519;
                width_front2 = 1454;
                height_front2 = 498;
                width_back1 = 1451;
                height_back1 = 469;
                width_back2 = 1313;
                height_back2 = 438;
                width_back3 = 1105;
                height_back3 = 391;
                width_back4 = 111;
                height_back4 = 236;
                width_outside1 = 1233;
                height_outside1 = 542;
                width_outside2 = 590;
                height_outside2 = 449;
                width_outside3 = 372;
                height_outside3 = 478;
                width_inside1 = 1229;
                height_inside1 = 535;
                width_inside3 = 382;
                height_inside3 = 443;
                break;
            case 40:
                width_front1 = 666;
                height_front1 = 531;
                width_front2 = 1482;
                height_front2 = 511;
                width_back1 = 1485;
                height_back1 = 477;
                width_back2 = 1346;
                height_back2 = 446;
                width_back3 = 1132;
                height_back3 = 398;
                width_back4 = 111;
                height_back4 = 248;
                width_outside1 = 1264;
                height_outside1 = 553;
                width_outside2 = 604;
                height_outside2 = 458;
                width_outside3 = 381;
                height_outside3 = 486;
                width_inside1 = 1259;
                height_inside1 = 544;
                width_inside3 = 392;
                height_inside3 = 450;
                break;
            case 41:
                width_front1 = 676;
                height_front1 = 542;
                width_front2 = 1510;
                height_front2 = 524;
                width_back1 = 1519;
                height_back1 = 486;
                width_back2 = 1378;
                height_back2 = 454;
                width_back3 = 1161;
                height_back3 = 405;
                width_back4 = 111;
                height_back4 = 248;
                width_outside1 = 1294;
                height_outside1 = 561;
                width_outside2 = 619;
                height_outside2 = 464;
                width_outside3 = 391;
                height_outside3 = 494;
                width_inside1 = 1289;
                height_inside1 = 553;
                width_inside3 = 401;
                height_inside3 = 457;
                break;
            case 42:
                width_front1 = 686;
                height_front1 = 554;
                width_front2 = 1537;
                height_front2 = 538;
                width_back1 = 1553;
                height_back1 = 491;
                width_back2 = 1410;
                height_back2 = 460;
                width_back3 = 1188;
                height_back3 = 412;
                width_back4 = 111;
                height_back4 = 248;
                width_outside1 = 1326;
                height_outside1 = 570;
                width_outside2 = 633;
                height_outside2 = 471;
                width_outside3 = 401;
                height_outside3 = 503;
                width_inside1 = 1320;
                height_inside1 = 562;
                width_inside3 = 412;
                height_inside3 = 464;
                break;
            case 43:
                width_front1 = 696;
                height_front1 = 565;
                width_front2 = 1565;
                height_front2 = 550;
                width_back1 = 1586;
                height_back1 = 500;
                width_back2 = 1442;
                height_back2 = 468;
                width_back3 = 1215;
                height_back3 = 418;
                width_back4 = 111;
                height_back4 = 261;
                width_outside1 = 1356;
                height_outside1 = 579;
                width_outside2 = 649;
                height_outside2 = 479;
                width_outside3 = 410;
                height_outside3 = 511;
                width_inside1 = 1350;
                height_inside1 = 572;
                width_inside3 = 420;
                height_inside3 = 473;
                break;
            case 44:
                width_front1 = 704;
                height_front1 = 577;
                width_front2 = 1591;
                height_front2 = 564;
                width_back1 = 1619;
                height_back1 = 507;
                width_back2 = 1474;
                height_back2 = 475;
                width_back3 = 1243;
                height_back3 = 425;
                width_back4 = 111;
                height_back4 = 261;
                width_outside1 = 1387;
                height_outside1 = 589;
                width_outside2 = 662;
                height_outside2 = 487;
                width_outside3 = 420;
                height_outside3 = 520;
                width_inside1 = 1381;
                height_inside1 = 580;
                width_inside3 = 430;
                height_inside3 = 481;
                break;
            case 45:
                width_front1 = 715;
                height_front1 = 594;
                width_front2 = 1619;
                height_front2 = 575;
                width_back1 = 1653;
                height_back1 = 515;
                width_back2 = 1507;
                height_back2 = 483;
                width_back3 = 1270;
                height_back3 = 431;
                width_back4 = 111;
                height_back4 = 261;
                width_outside1 = 1417;
                height_outside1 = 598;
                width_outside2 = 677;
                height_outside2 = 496;
                width_outside3 = 430;
                height_outside3 = 528;
                width_inside1 = 1412;
                height_inside1 = 589;
                width_inside3 = 439;
                height_inside3 = 489;
                break;
        }
        width_inside2 = width_outside2;
        height_inside2 = height_outside2;
    }

}
