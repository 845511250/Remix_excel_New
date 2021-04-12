package com.example.zuoyun.remix_excel_new.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

public class FragmentGQ extends BaseFragment {
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

    int width_front, width_back, width_sleeve, width_hood, width_bottom, width_pocket, width_cuff;
    int height_front,height_back, height_sleeve, height_hood, height_bottom,height_pocket, height_cuff;

    int width_Z72_combine, height_Z72_combine;
    int x_Z72_front, x_Z72_back, x_Z72_sleeve_left, x_Z72_sleeve_right, x_Z72_hood_left, x_Z72_hood_right, x_Z72_cuff_left, x_Z72_cuff_right, x_Z72_pocket, x_Z72_bottom;
    int y_Z72_front, y_Z72_back, y_Z72_sleeve_left, y_Z72_sleeve_right, y_Z72_hood_left, y_Z72_hood_right, y_Z72_cuff_left, y_Z72_cuff_right, y_Z72_pocket, y_Z72_bottom;

    int num;
    String strPlus = "";
    int intPlus = 1;
    boolean sizeOK = true;

    Paint rectPaint, paint, paintRed, paintBlue, rectBorderPaint, paintSmall;
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

        //paint
        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        rectBorderPaint = new Paint();
        rectBorderPaint.setColor(0xff000000);
        rectBorderPaint.setStyle(Paint.Style.STROKE);
        rectBorderPaint.setStrokeWidth(2);

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
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(26);
        paintBlue.setTypeface(Typeface.DEFAULT_BOLD);
        paintBlue.setAntiAlias(true);

        paintSmall = new Paint();
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(26);
        paintSmall.setTypeface(Typeface.DEFAULT_BOLD);
        paintSmall.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                    bt_remix.setClickable(false);
                } else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
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
        bt_remix.setClickable(false);
    }

    public void remix(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                if (orderItems.get(currentID).sku.endsWith("W")) {
                    setSizeGQW(orderItems.get(currentID).sizeStr);
                } else if (orderItems.get(currentID).sku.equals("GQY")) {
                    setSizeGQY(orderItems.get(currentID).sizeStr);
                } else {
                    setSizeGQM(orderItems.get(currentID).sizeStr);
                }

                if (sizeOK) {
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

            }
        }.start();

    }

    void drawTextFront(Canvas canvas) {
        canvas.drawRect(1000, 4159, 1000 + 800, 4159 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1000, 4159 + 23, paint);
        canvas.drawText(currentID + "", 1440, 4159 + 23, paintRed);
    }
    void drawTextBack(Canvas canvas) {
        canvas.drawRect(1000, 4219, 1000 + 800, 4219 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1000, 4219 + 23, paint);
        canvas.drawText(currentID + "", 1440, 4222 + 23, paintRed);
    }
    void drawTextXiuziL(Canvas canvas) {
        canvas.drawRect(1438, 13, 1438 + 130, 13 + 25, rectPaint);
        canvas.drawText("左 " + orderItems.get(currentID).sizeStr, 1438, 13 + 23, paint);
        canvas.drawText(currentID + "", 1520, 13 + 23, paintRed);

        canvas.drawRect(1000, 3775 - 25, 1000 + 300, 3775, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 1000, 3775 - 2, paint);
    }
    void drawTextXiuziR(Canvas canvas) {
        canvas.drawRect(1335, 13, 1335 + 130, 13 + 25, rectPaint);
        canvas.drawText("右 " + orderItems.get(currentID).sizeStr, 1335, 13 + 23, paint);
        canvas.drawText(currentID + "", 1415, 13 + 23, paintRed);

        canvas.drawRect(1000, 3775 - 25, 1000 + 300, 3777, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 1000, 3775 - 2, paint);
    }
    void drawTextMaoziInL(Canvas canvas) {
        canvas.drawRect(1038, 7, 1038 + 180, 7 + 25, rectPaint);
        canvas.drawText("内左 " + orderItems.get(currentID).sizeStr, 1038, 7 + 23, paint);
        canvas.drawText(currentID + "", 1138, 7 + 23, paintRed);

        canvas.save();
        canvas.rotate(5.3f, 1502, 23);
        canvas.drawRect(1502, 23, 1502 + 300, 23 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 1502, 23 + 23, paint);
        canvas.restore();
    }
    void drawTextMaoziInR(Canvas canvas) {
        canvas.drawRect(709, 7, 709 + 180, 7 + 25, rectPaint);
        canvas.drawText("内右 " + orderItems.get(currentID).sizeStr, 709, 7 + 23, paint);
        canvas.drawText(currentID + "", 809, 7 + 23, paintRed);

        canvas.save();
        canvas.rotate(-5.2f, 133, 50);
        canvas.drawRect(133, 50, 133 + 300, 50 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 133, 50 + 23, paint);
        canvas.restore();
    }
    void drawTextMaoziOutL(Canvas canvas) {
        canvas.drawRect(924, 10, 924 + 100, 10 + 25, rectPaint);
        canvas.drawText("外左", 924, 10 + 23, paint);
        canvas.drawText(currentID + "", 974, 10 + 23, paintRed);

        canvas.save();
        canvas.rotate(-5.4f, 179, 58);
        canvas.drawRect(179, 58, 179 + 300, 58 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 179, 58 + 23, paint);
        canvas.restore();
    }
    void drawTextMaoziOutR(Canvas canvas) {
        canvas.drawRect(924, 10, 924 + 100, 10 + 25, rectPaint);
        canvas.drawText("外右", 924, 10 + 23, paint);
        canvas.drawText(currentID + "", 974, 10 + 23, paintRed);

        canvas.save();
        canvas.rotate(5.2f, 1475, 32);
        canvas.drawRect(1475, 32, 1475 + 300, 32 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 1475, 32 + 23, paint);
        canvas.restore();
    }
    void drawTextPocket(Canvas canvas) {
        canvas.drawRect(800, 90, 800 + 200, 90 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr, 800, 90 + 23, paint);
        canvas.drawText(currentID + "", 900, 90 + 23, paintRed);

        canvas.drawRect(1050, 90, 1050 + 300, 90 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 1050, 90 + 23, paint);
    }
    void drawTextXiabai(Canvas canvas) {
        canvas.drawRect(3900, 9, 3900 + 200, 9 + 25, rectPaint);
        canvas.drawText("下摆 " + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).newCode_short, 3900, 9 + 23, paint);

        canvas.drawRect(4200, 9, 4200 + 300, 9 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 4200, 9 + 23, paint);
    }
    void drawTextXiukouL(Canvas canvas) {
        canvas.drawRect(760, 9, 760 + 160, 9 + 25, rectPaint);
        canvas.drawText("左 " + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).newCode_short, 760, 9 + 23, paint);

        canvas.drawRect(1000, 9, 1000 + 300, 9 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 1000, 9 + 23, paint);
    }
    void drawTextXiukouR(Canvas canvas) {
        canvas.drawRect(760, 9, 760 + 160, 9 + 25, rectPaint);
        canvas.drawText("右 " + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).newCode_short, 760, 9 + 23, paint);

        canvas.drawRect(1000, 9, 1000 + 300, 9 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 1000, 9 + 23, paint);
    }

    public void remixx(){
        Bitmap bitmapCombine = null;

        if (orderItems.get(currentID).sku.contains("Z72")) {//Z72
            bitmapCombine = Bitmap.createBitmap(width_Z72_combine, height_Z72_combine, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine= new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            if (orderItems.get(currentID).imgs.size() == 8) {
                //front
                Bitmap bitmapTemp = Bitmap.createBitmap(4144, 4193, Bitmap.Config.ARGB_8888);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("front") ? getBitmapWith("front") : MainActivity.instance.bitmaps.get(0), 0, 0, null);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_front, y_Z72_front, null);

                //back
                bitmapTemp = Bitmap.createBitmap(4143, 4253, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("back") ? getBitmapWith("back") : MainActivity.instance.bitmaps.get(1), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_back);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBack(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_back, y_Z72_back, null);

                //arm_l
                bitmapTemp = Bitmap.createBitmap(2903, 3784, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("left_sleeve") ? getBitmapWith("left_sleeve") : MainActivity.instance.bitmaps.get(2), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_sleeve_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziL(canvasTemp);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_sleeve_left, y_Z72_sleeve_left, null);

                //arm_r
                bitmapTemp = Bitmap.createBitmap(2903, 3784, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("right_sleeve") ? getBitmapWith("right_sleeve") : MainActivity.instance.bitmaps.get(3), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_sleeve_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziR(canvasTemp);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_sleeve_right, y_Z72_sleeve_right, null);

                //pocket
                bitmapTemp = Bitmap.createBitmap(2793, 1576, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("front") ? getBitmapWith("front") : MainActivity.instance.bitmaps.get(0), -673, -2617, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_pocket);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextPocket(canvasTemp);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_pocket, y_Z72_pocket, null);

                //maozi_out_l
                bitmapTemp = Bitmap.createBitmap(1928, 2368, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("hat") ? getBitmapWith("hat") : MainActivity.instance.bitmaps.get(7), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_hood_out_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziOutL(canvasTemp);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_hood_left, y_Z72_hood_left, null);

                //maozi_out_r
                bitmapTemp = Bitmap.createBitmap(1928, 2368, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("hat") ? getBitmapWith("hat") : MainActivity.instance.bitmaps.get(7), -1928, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_hood_out_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziOutR(canvasTemp);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_hood_right, y_Z72_hood_right, null);

                //xiabai
                bitmapTemp = Bitmap.createBitmap(7907, 860, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("bottom") ? getBitmapWith("bottom") : MainActivity.instance.bitmaps.get(6), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_bottom_7909);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiabai(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_bottom, height_bottom, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_bottom, y_Z72_bottom, null);

                //xiukou_l
                bitmapTemp = Bitmap.createBitmap(1628, 619, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("left_cuff") ? getBitmapWith("left_cuff") : MainActivity.instance.bitmaps.get(4), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_cuff);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiukouL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_cuff_left, y_Z72_cuff_left, null);

                //xiukou_r
                bitmapTemp = Bitmap.createBitmap(1628, 619, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("right_cuff") ? getBitmapWith("right_cuff") : MainActivity.instance.bitmaps.get(5), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_cuff);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextXiukouR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_cuff_right, y_Z72_cuff_right, null);
                bitmapTemp.recycle();

            } else if (orderItems.get(currentID).imgs.size() == 1 || orderItems.get(currentID).imgs.size() == 2) {
                //front
                Bitmap bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 2903, 2134, 4144, 4193);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_front, y_Z72_front, null);

                //back
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2904, 2074, 4143, 4253);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_back);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBack(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_back, y_Z72_back, null);

                //arm_l
                bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 7047, 2600, 2903, 3784);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_sleeve_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_sleeve_left, y_Z72_sleeve_left, null);

                //arm_r
                bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 0, 2576, 2903, 3784);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_sleeve_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_sleeve_right, y_Z72_sleeve_right, null);

                //pocket
                bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 3574, 4751, 2794, 1576);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_pocket);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextPocket(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_pocket, y_Z72_pocket, null);

                //maozi_out_l
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3047, 0, 1928, 2368);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_hood_out_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziOutL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_hood_left, y_Z72_hood_left, null);

                //maozi_out_r
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4982, 0, 1928, 2368);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_hood_out_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziOutR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_hood_right, y_Z72_hood_right, null);

                //xiabai
                bitmapTemp = Bitmap.createBitmap(3863 * 2, 675, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                Bitmap bitmapCut = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 3035, 6202, 3863, 675);
                canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
                canvasTemp.drawBitmap(bitmapCut, 3862, 0, null);
                bitmapCut.recycle();
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_bottom_7725);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiabai(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_bottom, height_bottom, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_bottom, y_Z72_bottom, null);

                //xiukou_l
                bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 7685, 6258, 1628, 619);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_cuff);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiukouL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_cuff_left, y_Z72_cuff_left, null);

                //xiukou_r
                bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 634, 6258, 1628, 619);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z72_cuff);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextXiukouR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, x_Z72_cuff_right, y_Z72_cuff_right, null);
                bitmapTemp.recycle();
            }

        } else {//GQ
            int margin = 120;
            Matrix matrix = new Matrix();
            int x_hood = Math.max(width_sleeve + margin, width_cuff * 2 + margin * 2);
            bitmapCombine = Bitmap.createBitmap(x_hood + width_hood, height_front + height_back + height_hood * 4 + margin * 3, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine= new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            if (orderItems.get(currentID).imgs.size() == 8) {
                //front
                Bitmap bitmapTemp = Bitmap.createBitmap(4144, 4193, Bitmap.Config.ARGB_8888);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("front") ? getBitmapWith("front") : MainActivity.instance.bitmaps.get(0), 0, 0, null);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

                //back
                bitmapTemp = Bitmap.createBitmap(4143, 4253, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("back") ? getBitmapWith("back") : MainActivity.instance.bitmaps.get(1), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_back);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBack(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

                //arm_l
                bitmapTemp = Bitmap.createBitmap(2903, 3784, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("left_sleeve") ? getBitmapWith("left_sleeve") : MainActivity.instance.bitmaps.get(2), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + margin * 2, null);

                //arm_r
                bitmapTemp = Bitmap.createBitmap(2903, 3784, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("right_sleeve") ? getBitmapWith("right_sleeve") : MainActivity.instance.bitmaps.get(3), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_sleeve + margin * 3, null);

                //pocket
                bitmapTemp = Bitmap.createBitmap(2793, 1576, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("front") ? getBitmapWith("front") : MainActivity.instance.bitmaps.get(0), -673, -2617, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_pocket);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextPocket(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_sleeve * 2 + margin * 4, null);

                //maozi_out_l
                bitmapTemp = Bitmap.createBitmap(1928, 2368, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("hat") ? getBitmapWith("hat") : MainActivity.instance.bitmaps.get(7), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_maozi_out_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziOutL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + margin * 2, null);

                //maozi_out_r
                bitmapTemp = Bitmap.createBitmap(1928, 2368, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("hat") ? getBitmapWith("hat") : MainActivity.instance.bitmaps.get(7), -1928, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_maozi_out_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziOutR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + height_hood + margin * 3, null);

                //maozi_in_r
                bitmapTemp = Bitmap.createBitmap(1928, 2368, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("hat") ? getBitmapWith("hat") : MainActivity.instance.bitmaps.get(7), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_maozi_in_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziInR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + height_hood * 2 + margin * 3, null);

                //maozi_in_l
                bitmapTemp = Bitmap.createBitmap(1928, 2368, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("hat") ? getBitmapWith("hat") : MainActivity.instance.bitmaps.get(7), -1928, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_maozi_in_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziInL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + height_hood * 3 + margin * 3, null);

                //xiabai
                bitmapTemp = Bitmap.createBitmap(7907, 860, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("bottom") ? getBitmapWith("bottom") : MainActivity.instance.bitmaps.get(6), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_bottom_7907);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiabai(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_bottom, height_bottom, true);
                matrix.reset();
                matrix.postRotate(-90);
                matrix.postTranslate(width_front + margin, width_bottom);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                //xiukou_l
                bitmapTemp = Bitmap.createBitmap(1628, 619, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("left_cuff") ? getBitmapWith("left_cuff") : MainActivity.instance.bitmaps.get(4), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiukouL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_sleeve * 2 + height_pocket + margin * 5, null);

                //xiukou_r
                bitmapTemp = Bitmap.createBitmap(1628, 619, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(checkContains("right_cuff") ? getBitmapWith("right_cuff") : MainActivity.instance.bitmaps.get(5), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextXiukouR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, width_cuff + margin, height_back + height_front + height_sleeve * 2 + height_pocket + margin * 5, null);
                bitmapTemp.recycle();

            } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getHeight() == 15297) {//jj

                //front
                Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3412, 2562 + 3698, 4144, 4193);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

                //back
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7717, 2504 + 3698, 4143, 4253);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_back);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBack(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

                //arm_l
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 12017, 3454 + 3698, 2903, 3784);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + margin * 2, null);

                //arm_r
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 382, 3454 + 3698, 2903, 3784);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_sleeve + margin * 3, null);

                //pocket
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4085, 5179 + 3698, 2794, 1576);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_pocket);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextPocket(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_sleeve * 2 + margin * 4, null);

                //maozi_out_l
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7859, 0 + 3698, 1928, 2368);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_maozi_out_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziOutL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + margin * 2, null);

                //maozi_out_r
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 9784, 0 + 3698, 1928, 2368);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_maozi_out_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziOutR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + height_hood + margin * 3, null);

                //maozi_in_r
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3557, 0 + 3698, 1928, 2368);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_maozi_in_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziInR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + height_hood * 2 + margin * 3, null);

                //maozi_in_l
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5485, 0 + 3698, 1928, 2368);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_maozi_in_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziInL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + height_hood * 3 + margin * 3, null);

                //xiabai
                bitmapTemp = Bitmap.createBitmap(3954 * 2, 860, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3505, 6874 + 3698, 3954, 860);
                canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
                bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7808, 6874 + 3698, 3954, 860);
                canvasTemp.drawBitmap(bitmapCut, 3954, 0, null);
                bitmapCut.recycle();
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_bottom_7907);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiabai(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_bottom, height_bottom, true);
                matrix.reset();
                matrix.postRotate(-90);
                matrix.postTranslate(width_front + margin, width_bottom);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                //xiukou_l
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 12667, 7286 + 3698, 1628, 619);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiukouL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_sleeve * 2 + height_pocket + margin * 5, null);

                //xiukou_r
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1032, 7286 + 3698, 1628, 619);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextXiukouR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, width_cuff + margin, height_back + height_front + height_sleeve * 2 + height_pocket + margin * 5, null);
                bitmapTemp.recycle();

            } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getHeight() == 9400) {//jj-GQY
                //front
                Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3243, 74, 2936, 3572);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 4144, 4193, true);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gqy_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
                bitmapTemp.recycle();

                //back
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1121, 4613, 2938, 3634);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 4144, 4253, true);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gqy_back);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextBack(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);
                bitmapTemp.recycle();

                //arm_l
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6343, 540, 2397, 3106);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 2903, 3784, true);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gqy_arm_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + margin * 2, null);

                //arm_r
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 666, 540, 2397, 3106);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 2903, 3784, true);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gqy_arm_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_sleeve + margin * 3, null);

                //pocket
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3654, 2325, 2122, 1408);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 2794, 1576, true);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gqy_pocket);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextPocket(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_sleeve * 2 + margin * 4, null);

                //maozi_out_l
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4706, 6970, 1605, 2324);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1928, 2368, true);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gqy_maozi_in_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziOutL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + margin * 2, null);

                //maozi_out_r
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6402, 6970, 1605, 2324);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1928, 2368, true);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gqy_maozi_in_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziOutR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + height_hood + margin * 3, null);

                //maozi_in_r
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4730, 4648, 1585, 2324);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1928, 2368, true);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gqy_maozi_in_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziInR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + height_hood * 2 + margin * 3, null);

                //maozi_in_l
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6402, 4648, 1585, 2324);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1928, 2368, true);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gqy_maozi_in_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziInL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + height_hood * 3 + margin * 3, null);

                //xiabai
                Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3417, 3774, 2593, 841);
                bitmapCut = Bitmap.createScaledBitmap(bitmapCut, 3863, 860, true);
                bitmapTemp = Bitmap.createBitmap(3863 * 2, 860, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(bitmapCut, 0, 0, null);

                bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1296, 8287, 2593, 841);
                bitmapCut = Bitmap.createScaledBitmap(bitmapCut, 3863, 860, true);
                canvasTemp.drawBitmap(bitmapCut, 3862, 0, null);
                bitmapCut.recycle();
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_bottom_7725);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiabai(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_bottom, height_bottom, true);
                matrix.reset();
                matrix.postRotate(-90);
                matrix.postTranslate(width_front + margin, width_bottom);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                //xiukou_l
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6923, 3686, 1233, 841);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1628, 619, true);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gqy_xiukou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiukouL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_sleeve * 2 + height_pocket + margin * 5, null);

                //xiukou_r
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1246, 3686, 1233, 841);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1628, 619, true);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gqy_xiukou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextXiukouR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, width_cuff + margin, height_back + height_front + height_sleeve * 2 + height_pocket + margin * 5, null);
                bitmapTemp.recycle();
            } else if (orderItems.get(currentID).isPPSL) {
                //front
                Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1428, 2257, 4144, 4193);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GQY") ? R.drawable.gqy_front : R.drawable.gq_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
                bitmapTemp.recycle();

                //back
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1429, 2197, 4143, 4253);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GQY") ? R.drawable.gqy_back : R.drawable.gq_back);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextBack(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);
                bitmapTemp.recycle();

                //arm_l
                Bitmap bitmapArm = Bitmap.createBitmap(2903, 3784, Bitmap.Config.ARGB_8888);
                Canvas canvasArm = new Canvas(bitmapArm);
                canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasArm.drawColor(0xffffffff);

                Bitmap bitmapHalf = Bitmap.createBitmap(1452, 3784, Bitmap.Config.ARGB_8888);
                Canvas canvasHalf = new Canvas(bitmapHalf);
                canvasHalf.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasHalf.drawColor(0xffffffff);

                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 4857, 2561, 1615, 3729);
                matrix.reset();
                matrix.postRotate(-11.4f);
                matrix.postTranslate(-136, 298);
                canvasArm.drawBitmap(bitmapTemp, matrix, null);
                bitmapTemp.recycle();

                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 529, 2561, 1615, 3729);
                matrix.reset();
                matrix.postRotate(11.4f);
                matrix.postTranslate(2, -19);
                canvasHalf.drawBitmap(bitmapTemp, matrix, null);
                bitmapTemp.recycle();
                canvasArm.drawBitmap(bitmapHalf, 1451, 0, null);
                bitmapHalf.recycle();

                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GQY") ? R.drawable.gqy_arm_l : R.drawable.gq_arm_l);
                canvasArm.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextXiuziL(canvasArm);
                bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapArm, 0, height_back + height_front + margin * 2, null);
                bitmapArm.recycle();

                //arm_r
                bitmapArm = Bitmap.createBitmap(2903, 3784, Bitmap.Config.ARGB_8888);
                canvasArm = new Canvas(bitmapArm);
                canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasArm.drawColor(0xffffffff);

                bitmapHalf = Bitmap.createBitmap(1452, 3784, Bitmap.Config.ARGB_8888);
                canvasHalf = new Canvas(bitmapHalf);
                canvasHalf.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasHalf.drawColor(0xffffffff);

                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4857, 2561, 1615, 3729);
                matrix.reset();
                matrix.postRotate(-11.4f);
                matrix.postTranslate(-136, 298);
                canvasArm.drawBitmap(bitmapTemp, matrix, null);
                bitmapTemp.recycle();

                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 529, 2561, 1615, 3729);
                matrix.reset();
                matrix.postRotate(11.4f);
                matrix.postTranslate(2, -19);
                canvasHalf.drawBitmap(bitmapTemp, matrix, null);
                bitmapTemp.recycle();
                canvasArm.drawBitmap(bitmapHalf, 1451, 0, null);
                bitmapHalf.recycle();

                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GQY") ? R.drawable.gqy_arm_r : R.drawable.gq_arm_r);
                canvasArm.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextXiuziR(canvasArm);
                bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapArm, 0, height_back + height_front + height_sleeve + margin * 3, null);
                bitmapArm.recycle();

                //pocket
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2099, 4874, 2794, 1576);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GQY") ? R.drawable.gqy_pocket : R.drawable.gq_pocket);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextPocket(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_sleeve * 2 + margin * 4, null);
                bitmapTemp.recycle();

                //maozi_out_l
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1572, 123, 1928, 2368);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GQY") ? R.drawable.gqy_maozi_in_r : R.drawable.gq_maozi_out_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextMaoziOutL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + margin * 2, null);
                bitmapTemp.recycle();

                //maozi_out_r
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3507, 123, 1928, 2368);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GQY") ? R.drawable.gqy_maozi_in_l : R.drawable.gq_maozi_out_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextMaoziOutR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + height_hood + margin * 3, null);
                bitmapTemp.recycle();

                //maozi_in_r
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1572, 123, 1928, 2368);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GQY") ? R.drawable.gqy_maozi_in_r : R.drawable.gq_maozi_in_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextMaoziInR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + height_hood * 2 + margin * 3, null);
                bitmapTemp.recycle();

                //maozi_in_l
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3507, 123, 1928, 2368);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GQY") ? R.drawable.gqy_maozi_in_l : R.drawable.gq_maozi_in_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextMaoziInL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + height_hood * 3 + margin * 3, null);
                bitmapTemp.recycle();

                //xiabai
                bitmapArm = Bitmap.createBitmap(7725, 860, Bitmap.Config.ARGB_8888);
                canvasArm = new Canvas(bitmapArm);
                canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasArm.drawColor(0xffffffff);

                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1560, 6326, 3863, 430);
                canvasArm.drawBitmap(bitmapTemp, 0, 0, null);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 3863, -430, true);
                canvasArm.drawBitmap(bitmapTemp, 0, 430, null);

                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1604, 6326, 3863, 430);
                canvasArm.drawBitmap(bitmapTemp, 3862, 0, null);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 3863, -430, true);
                canvasArm.drawBitmap(bitmapTemp, 3862, 430, null);
                bitmapTemp.recycle();

                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_bottom_7725);
                canvasArm.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextXiabai(canvasArm);
                bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_bottom, height_bottom, true);

                matrix.reset();
                matrix.postRotate(-90);
                matrix.postTranslate(width_front + margin, width_bottom);
                canvasCombine.drawBitmap(bitmapArm, matrix, null);
                bitmapArm.recycle();

                //xiukou_l
                bitmapArm = Bitmap.createBitmap(1628, 619, Bitmap.Config.ARGB_8888);
                canvasArm = new Canvas(bitmapArm);
                canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasArm.drawColor(0xffffffff);

                bitmapHalf = Bitmap.createBitmap(814, 619, Bitmap.Config.ARGB_8888);
                canvasHalf = new Canvas(bitmapHalf);
                canvasHalf.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasHalf.drawColor(0xffffffff);

                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 4828, 6018, 920, 768);
                matrix.reset();
                matrix.postRotate(-11.5f);
                matrix.postTranslate(-120, 25);
                canvasArm.drawBitmap(bitmapTemp, matrix, null);
                bitmapTemp.recycle();

                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1254, 6018, 920, 768);
                matrix.reset();
                matrix.postRotate(11.5f);
                matrix.postTranslate(32, -159);
                canvasHalf.drawBitmap(bitmapTemp, matrix, null);
                bitmapTemp.recycle();
                canvasArm.drawBitmap(bitmapHalf, 813, 0, null);
                bitmapHalf.recycle();

                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GQY") ? R.drawable.gqy_xiukou : R.drawable.gq_xiukou);
                canvasArm.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextXiukouL(canvasArm);
                bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapArm, 0, height_back + height_front + height_sleeve * 2 + height_pocket + margin * 5, null);
                bitmapArm.recycle();

                //xiukou_r
                bitmapArm = Bitmap.createBitmap(1628, 619, Bitmap.Config.ARGB_8888);
                canvasArm = new Canvas(bitmapArm);
                canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasArm.drawColor(0xffffffff);

                bitmapHalf = Bitmap.createBitmap(814, 619, Bitmap.Config.ARGB_8888);
                canvasHalf = new Canvas(bitmapHalf);
                canvasHalf.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasHalf.drawColor(0xffffffff);

                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4828, 6018, 920, 768);
                matrix.reset();
                matrix.postRotate(-11.5f);
                matrix.postTranslate(-120, 25);
                canvasArm.drawBitmap(bitmapTemp, matrix, null);
                bitmapTemp.recycle();

                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1254, 6018, 920, 768);
                matrix.reset();
                matrix.postRotate(11.5f);
                matrix.postTranslate(32, -159);
                canvasHalf.drawBitmap(bitmapTemp, matrix, null);
                bitmapTemp.recycle();
                canvasArm.drawBitmap(bitmapHalf, 813, 0, null);
                bitmapHalf.recycle();

                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GQY") ? R.drawable.gqy_xiukou : R.drawable.gq_xiukou);
                canvasArm.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextXiukouR(canvasArm);
                bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapArm, width_cuff + margin, height_back + height_front + height_sleeve * 2 + height_pocket + margin * 5, null);
                bitmapArm.recycle();
            } else if (orderItems.get(currentID).imgs.size() == 1 || orderItems.get(currentID).imgs.size() == 2) {
                //front
                Bitmap bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 2903, 2134, 4144, 4193);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

                //back
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2904, 2074, 4143, 4253);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_back);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBack(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

                //arm_l
                bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 7047, 2600, 2903, 3784);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + margin * 2, null);


                //arm_r
                bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 0, 2576, 2903, 3784);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_sleeve + margin * 3, null);

                //pocket
                bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 3574, 4751, 2794, 1576);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_pocket);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextPocket(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_sleeve * 2 + margin * 4, null);

                //maozi_out_l
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3047, 0, 1928, 2368);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_maozi_out_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziOutL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + margin * 2, null);

                //maozi_out_r
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4982, 0, 1928, 2368);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_maozi_out_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziOutR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + height_hood + margin * 3, null);

                //maozi_in_r
                bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 3047, 0, 1928, 2368);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_maozi_in_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziInR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + height_hood * 2 + margin * 3, null);

                //maozi_in_l
                bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 4982, 0, 1928, 2368);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_maozi_in_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextMaoziInL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
                canvasCombine.drawBitmap(bitmapTemp, x_hood, height_front + height_back + height_hood * 3 + margin * 3, null);

                //xiabai
                bitmapTemp = Bitmap.createBitmap(3863 * 2, 675, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                Bitmap bitmapCut = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 3035, 6202, 3863, 675);
                canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
                canvasTemp.drawBitmap(bitmapCut, 3862, 0, null);
                bitmapCut.recycle();
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_bottom_7725);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiabai(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_bottom, height_bottom, true);
                matrix.reset();
                matrix.postRotate(-90);
                matrix.postTranslate(width_front + margin, width_bottom);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                //xiukou_l
                bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 7685, 6258, 1628, 619);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiukouL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_sleeve * 2 + height_pocket + margin * 5, null);

                //xiukou_r
                bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 634, 6258, 1628, 619);
                canvasTemp = new Canvas(bitmapTemp);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextXiukouR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, width_cuff + margin, height_back + height_front + height_sleeve * 2 + height_pocket + margin * 5, null);
                bitmapTemp.recycle();
            }
        }



        String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";
        String pathSave;
        if(MainActivity.instance.cb_classify.isChecked()){
            pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
        } else
            pathSave = sdCardPath + "/生产图/" + childPath + "/";
        if(!new File(pathSave).exists())
            new File(pathSave).mkdirs();
        File fileSave = new File(pathSave + nameCombine);
        BitmapToJpg.save(bitmapCombine, fileSave, 148);
        bitmapCombine.recycle();


        try {
//            matrix.reset();
//            matrix.postRotate(-90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);


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
            Label label0 = new Label(0, currentID+1, orderItems.get(currentID).order_number+orderItems.get(currentID).sku);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku);
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

    void setSizeGQM(String size) {
        switch (size) {
            case "2XS":
                width_front = 3175;
                height_front = 3755;
                width_back = 3175;
                height_back = 3813;
                width_sleeve = 2533;
                height_sleeve = 3343;
                width_hood = 1593;
                height_hood = 2386;
                width_bottom = 5358;
                height_bottom = 835;
                width_pocket = 2112;
                height_pocket = 1408;
                width_cuff = 1233;
                height_cuff = 834;

                width_Z72_combine = 10564;
                height_Z72_combine = 6169;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 3261;
                y_Z72_back = 0;
                x_Z72_sleeve_left = 6362;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 8030;
                y_Z72_sleeve_right = 2776;
                x_Z72_hood_left = 8896;
                y_Z72_hood_left = 0;
                x_Z72_hood_right = 6400;
                y_Z72_hood_right = 3627;
                x_Z72_pocket = 0;
                y_Z72_pocket = 3836;
                x_Z72_cuff_left = 2525;
                y_Z72_cuff_left = 4191;
                x_Z72_cuff_right = 4011;
                y_Z72_cuff_right = 4196;
                x_Z72_bottom = 0;
                y_Z72_bottom = 5335;
                break;
            case "XS":
                width_front = 3354;
                height_front = 3872;
                width_back = 3354;
                height_back = 3932;
                width_sleeve = 2664;
                height_sleeve = 3462;
                width_hood = 1709;
                height_hood = 2429;
                width_bottom = 5764;
                height_bottom = 834;
                width_pocket = 2255;
                height_pocket = 1457;
                width_cuff = 1292;
                height_cuff = 834;

                width_Z72_combine = 9494;
                height_Z72_combine = 7274;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 3477;
                y_Z72_back = 0;
                x_Z72_sleeve_left = 6830;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 6830;
                y_Z72_sleeve_right = 3576;
                x_Z72_hood_left = 0;
                y_Z72_hood_left = 3932;
                x_Z72_hood_right = 1805;
                y_Z72_hood_right = 3937;
                x_Z72_pocket = 3656;
                y_Z72_pocket = 3998;
                x_Z72_cuff_left = 3777;
                y_Z72_cuff_left = 5535;
                x_Z72_cuff_right = 5237;
                y_Z72_cuff_right = 5540;
                x_Z72_bottom = 0;
                y_Z72_bottom = 6440;
                break;
            case "S":
                width_front = 3530;
                height_front = 3990;
                width_back = 3530;
                height_back = 4050;
                width_sleeve = 2794;
                height_sleeve = 3580;
                width_hood = 1802;
                height_hood = 2499;
                width_bottom = 6118;
                height_bottom = 834;
                width_pocket = 2376;
                height_pocket = 1501;
                width_cuff = 1352;
                height_cuff = 834;

                width_Z72_combine = 9936;
                height_Z72_combine = 7471;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 3612;
                y_Z72_back = 0;
                x_Z72_sleeve_left = 7142;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 6898;
                y_Z72_sleeve_right = 3724;
                x_Z72_hood_left = 0;
                y_Z72_hood_left = 4058;
                x_Z72_hood_right = 1868;
                y_Z72_hood_right = 4058;
                x_Z72_pocket = 4522;
                y_Z72_pocket = 5056;
                x_Z72_cuff_left = 3692;
                y_Z72_cuff_left = 4146;
                x_Z72_cuff_right = 5193;
                y_Z72_cuff_right = 4146;
                x_Z72_bottom = 0;
                y_Z72_bottom = 6638;
                break;
            case "M":
                width_front = 3708;
                height_front = 4106;
                width_back = 3708;
                height_back = 4167;
                width_sleeve = 2926;
                height_sleeve = 3699;
                width_hood = 1861;
                height_hood = 2558;
                width_bottom = 6473;
                height_bottom = 834;
                width_pocket = 2498;
                height_pocket = 1544;
                width_cuff = 1411;
                height_cuff = 834;

                width_Z72_combine = 10438;
                height_Z72_combine = 7700;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 3805;
                y_Z72_back = 0;
                x_Z72_sleeve_left = 7512;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 7289;
                y_Z72_sleeve_right = 3869;
                x_Z72_hood_left = 0;
                y_Z72_hood_left = 4243;
                x_Z72_hood_right = 1909;
                y_Z72_hood_right = 4243;
                x_Z72_pocket = 3866;
                y_Z72_pocket = 5207;
                x_Z72_cuff_left = 4150;
                y_Z72_cuff_left = 4270;
                x_Z72_cuff_right = 5658;
                y_Z72_cuff_right = 4270;
                x_Z72_bottom = 0;
                y_Z72_bottom = 6867;
                break;
            case "L":
                width_front = 3885;
                height_front = 4225;
                width_back = 3885;
                height_back = 4285;
                width_sleeve = 3056;
                height_sleeve = 3818;
                width_hood = 1894;
                height_hood = 2607;
                width_bottom = 6827;
                height_bottom = 834;
                width_pocket = 2619;
                height_pocket = 1588;
                width_cuff = 1470;
                height_cuff = 834;

                width_Z72_combine = 10807;
                height_Z72_combine = 7832;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 3973;
                y_Z72_back = 0;
                x_Z72_sleeve_left = 7740;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 7553;
                y_Z72_sleeve_right = 4014;
                x_Z72_hood_left = 0;
                y_Z72_hood_left = 4300;
                x_Z72_hood_right = 2031;
                y_Z72_hood_right = 4300;
                x_Z72_pocket = 5096;
                y_Z72_pocket = 5282;
                x_Z72_cuff_left = 4173;
                y_Z72_cuff_left = 4376;
                x_Z72_cuff_right = 5754;
                y_Z72_cuff_right = 4376;
                x_Z72_bottom = 0;
                y_Z72_bottom = 6998;
                break;
            case "XL":
                width_front = 4061;
                height_front = 4342;
                width_back = 4061;
                height_back = 4403;
                width_sleeve = 3187;
                height_sleeve = 3935;
                width_hood = 1956;
                height_hood = 2666;
                width_bottom = 7182;
                height_bottom = 834;
                width_pocket = 2739;
                height_pocket = 1631;
                width_cuff = 1529;
                height_cuff = 834;

                width_Z72_combine = 10247;
                height_Z72_combine = 9312;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 4135;
                y_Z72_back = 0;
                x_Z72_sleeve_left = 3311;
                y_Z72_sleeve_left = 4468;
                x_Z72_sleeve_right = 0;
                y_Z72_sleeve_right = 4437;
                x_Z72_hood_left = 8268;
                y_Z72_hood_left = 0;
                x_Z72_hood_right = 8268;
                y_Z72_hood_right = 2677;
                x_Z72_pocket = 6115;
                y_Z72_pocket = 6600;
                x_Z72_cuff_left = 6661;
                y_Z72_cuff_left = 4539;
                x_Z72_cuff_right = 6661;
                y_Z72_cuff_right = 5550;
                x_Z72_bottom = 0;
                y_Z72_bottom = 8478;
                break;
            case "2XL":
                width_front = 4238;
                height_front = 4459;
                width_back = 4238;
                height_back = 4521;
                width_sleeve = 3317;
                height_sleeve = 4054;
                width_hood = 2038;
                height_hood = 2736;
                width_bottom = 7536;
                height_bottom = 834;
                width_pocket = 2860;
                height_pocket = 1675;
                width_cuff = 1588;
                height_cuff = 834;

                width_Z72_combine = 10671;
                height_Z72_combine = 9574;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 4336;
                y_Z72_back = 0;
                x_Z72_sleeve_left = 3452;
                y_Z72_sleeve_left = 4594;
                x_Z72_sleeve_right = 0;
                y_Z72_sleeve_right = 4543;
                x_Z72_hood_left = 8633;
                y_Z72_hood_left = 0;
                x_Z72_hood_right = 8633;
                y_Z72_hood_right = 2736;
                x_Z72_pocket = 6385;
                y_Z72_pocket = 6777;
                x_Z72_cuff_left = 6917;
                y_Z72_cuff_left = 4662;
                x_Z72_cuff_right = 6932;
                y_Z72_cuff_right = 5637;
                x_Z72_bottom = 0;
                y_Z72_bottom = 8740;
                break;
            case "3XL":
                width_front = 4415;
                height_front = 4576;
                width_back = 4415;
                height_back = 4638;
                width_sleeve = 3448;
                height_sleeve = 4172;
                width_hood = 2098;
                height_hood = 2795;
                width_bottom = 7891;
                height_bottom = 834;
                width_pocket = 2981;
                height_pocket = 1719;
                width_cuff = 1647;
                height_cuff = 834;

                width_Z72_combine = 9942;
                height_Z72_combine = 10182;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 0;
                y_Z72_back = 4646;
                x_Z72_sleeve_left = 4344;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 4237;
                y_Z72_sleeve_right = 4283;
                x_Z72_hood_left = 7862;
                y_Z72_hood_left = 0;
                x_Z72_hood_right = 7803;
                y_Z72_hood_right = 2780;
                x_Z72_pocket = 6982;
                y_Z72_pocket = 7512;
                x_Z72_cuff_left = 7733;
                y_Z72_cuff_left = 5648;
                x_Z72_cuff_right = 7733;
                y_Z72_cuff_right = 6566;
                x_Z72_bottom = 0;
                y_Z72_bottom = 9348;
                break;
            case "4XL":
                width_front = 4591;
                height_front = 4694;
                width_back = 4591;
                height_back = 4754;
                width_sleeve = 3578;
                height_sleeve = 4291;
                width_hood = 2165;
                height_hood = 2853;
                width_bottom = 8244;
                height_bottom = 834;
                width_pocket = 3096;
                height_pocket = 1763;
                width_cuff = 1705;
                height_cuff = 834;

                width_Z72_combine = 10238;
                height_Z72_combine = 10527;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 0;
                y_Z72_back = 4812;
                x_Z72_sleeve_left = 4480;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 4331;
                y_Z72_sleeve_right = 4412;
                x_Z72_hood_left = 8073;
                y_Z72_hood_left = 0;
                x_Z72_hood_right = 8073;
                y_Z72_hood_right = 2853;
                x_Z72_pocket = 7154;
                y_Z72_pocket = 7711;
                x_Z72_cuff_left = 8059;
                y_Z72_cuff_left = 5813;
                x_Z72_cuff_right = 8059;
                y_Z72_cuff_right = 6750;
                x_Z72_bottom = 0;
                y_Z72_bottom = 9694;
                break;
            case "5XL":
                width_front = 4777;
                height_front = 4823;
                width_back = 4777;
                height_back = 4881;
                width_sleeve = 3715;
                height_sleeve = 4415;
                width_hood = 2335;
                height_hood = 2859;
                width_bottom = 8606;
                height_bottom = 834;
                width_pocket = 3221;
                height_pocket = 1817;
                width_cuff = 1772;
                height_cuff = 834;

                width_Z72_combine = 10808;
                height_Z72_combine = 10827;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 0;
                y_Z72_back = 4983;
                x_Z72_sleeve_left = 4777;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 4777;
                y_Z72_sleeve_right = 4541;
                x_Z72_hood_left = 8473;
                y_Z72_hood_left = 0;
                x_Z72_hood_right = 8473;
                y_Z72_hood_right = 2860;
                x_Z72_pocket = 7587;
                y_Z72_pocket = 8047;
                x_Z72_cuff_left = 8755;
                y_Z72_cuff_left = 5960;
                x_Z72_cuff_right = 8755;
                y_Z72_cuff_right = 7004;
                x_Z72_bottom = 0;
                y_Z72_bottom = 9988;
                break;
            case "6XL":
                width_front = 4954;
                height_front = 4821;
                width_back = 4954;
                height_back = 4881;
                width_sleeve = 3842;
                height_sleeve = 4415;
                width_hood = 2398;
                height_hood = 2859;
                width_bottom = 8959;
                height_bottom = 834;
                width_pocket = 3221;
                height_pocket = 1817;
                width_cuff = 1832;
                height_cuff = 834;

                width_Z72_combine = 10808;
                height_Z72_combine = 11821;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 0;
                y_Z72_back = 4938;
                x_Z72_sleeve_left = 4954;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 4954;
                y_Z72_sleeve_right = 4544;
                x_Z72_hood_left = 8412;
                y_Z72_hood_left = 7916;
                x_Z72_hood_right = 8305;
                y_Z72_hood_right = 2985;
                x_Z72_pocket = 5084;
                y_Z72_pocket = 9053;
                x_Z72_cuff_left = 0;
                y_Z72_cuff_left = 9936;
                x_Z72_cuff_right = 2199;
                y_Z72_cuff_right = 9936;
                x_Z72_bottom = 0;
                y_Z72_bottom = 10980;
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }
    }





    void setSizeGQW(String size) {
        switch (size) {
            case "XS":
                width_front = 3175;
                height_front = 3755;
                width_back = 3175;
                height_back = 3813;
                width_sleeve = 2533;
                height_sleeve = 3343;
                width_hood = 1593;
                height_hood = 2386;
                width_bottom = 5358;
                height_bottom = 835;
                width_pocket = 2112;
                height_pocket = 1408;
                width_cuff = 1233;
                height_cuff = 834;

                width_Z72_combine = 10564;
                height_Z72_combine = 6169;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 3261;
                y_Z72_back = 0;
                x_Z72_sleeve_left = 6362;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 8030;
                y_Z72_sleeve_right = 2776;
                x_Z72_hood_left = 8896;
                y_Z72_hood_left = 0;
                x_Z72_hood_right = 6400;
                y_Z72_hood_right = 3627;
                x_Z72_pocket = 0;
                y_Z72_pocket = 3836;
                x_Z72_cuff_left = 2525;
                y_Z72_cuff_left = 4191;
                x_Z72_cuff_right = 4011;
                y_Z72_cuff_right = 4196;
                x_Z72_bottom = 0;
                y_Z72_bottom = 5335;
                break;
            case "S":
                width_front = 3354;
                height_front = 3872;
                width_back = 3354;
                height_back = 3932;
                width_sleeve = 2664;
                height_sleeve = 3462;
                width_hood = 1709;
                height_hood = 2429;
                width_bottom = 5764;
                height_bottom = 834;
                width_pocket = 2255;
                height_pocket = 1457;
                width_cuff = 1292;
                height_cuff = 834;

                width_Z72_combine = 9494;
                height_Z72_combine = 7274;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 3477;
                y_Z72_back = 0;
                x_Z72_sleeve_left = 6830;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 6830;
                y_Z72_sleeve_right = 3576;
                x_Z72_hood_left = 0;
                y_Z72_hood_left = 3932;
                x_Z72_hood_right = 1805;
                y_Z72_hood_right = 3937;
                x_Z72_pocket = 3656;
                y_Z72_pocket = 3998;
                x_Z72_cuff_left = 3777;
                y_Z72_cuff_left = 5535;
                x_Z72_cuff_right = 5237;
                y_Z72_cuff_right = 5540;
                x_Z72_bottom = 0;
                y_Z72_bottom = 6440;
                break;
            case "M":
                width_front = 3530;
                height_front = 3990;
                width_back = 3530;
                height_back = 4050;
                width_sleeve = 2794;
                height_sleeve = 3580;
                width_hood = 1802;
                height_hood = 2499;
                width_bottom = 6118;
                height_bottom = 834;
                width_pocket = 2376;
                height_pocket = 1501;
                width_cuff = 1352;
                height_cuff = 834;

                width_Z72_combine = 9936;
                height_Z72_combine = 7471;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 3612;
                y_Z72_back = 0;
                x_Z72_sleeve_left = 7142;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 6898;
                y_Z72_sleeve_right = 3724;
                x_Z72_hood_left = 0;
                y_Z72_hood_left = 4058;
                x_Z72_hood_right = 1868;
                y_Z72_hood_right = 4058;
                x_Z72_pocket = 4522;
                y_Z72_pocket = 5056;
                x_Z72_cuff_left = 3692;
                y_Z72_cuff_left = 4146;
                x_Z72_cuff_right = 5193;
                y_Z72_cuff_right = 4146;
                x_Z72_bottom = 0;
                y_Z72_bottom = 6638;
                break;
            case "L":
                width_front = 3708;
                height_front = 4106;
                width_back = 3708;
                height_back = 4167;
                width_sleeve = 2926;
                height_sleeve = 3699;
                width_hood = 1861;
                height_hood = 2558;
                width_bottom = 6473;
                height_bottom = 834;
                width_pocket = 2498;
                height_pocket = 1544;
                width_cuff = 1411;
                height_cuff = 834;

                width_Z72_combine = 10438;
                height_Z72_combine = 7700;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 3805;
                y_Z72_back = 0;
                x_Z72_sleeve_left = 7512;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 7289;
                y_Z72_sleeve_right = 3869;
                x_Z72_hood_left = 0;
                y_Z72_hood_left = 4243;
                x_Z72_hood_right = 1909;
                y_Z72_hood_right = 4243;
                x_Z72_pocket = 3866;
                y_Z72_pocket = 5207;
                x_Z72_cuff_left = 4150;
                y_Z72_cuff_left = 4270;
                x_Z72_cuff_right = 5658;
                y_Z72_cuff_right = 4270;
                x_Z72_bottom = 0;
                y_Z72_bottom = 6867;
                break;
            case "XL":
                width_front = 3885;
                height_front = 4225;
                width_back = 3885;
                height_back = 4285;
                width_sleeve = 3056;
                height_sleeve = 3818;
                width_hood = 1894;
                height_hood = 2607;
                width_bottom = 6827;
                height_bottom = 834;
                width_pocket = 2619;
                height_pocket = 1588;
                width_cuff = 1470;
                height_cuff = 834;

                width_Z72_combine = 10807;
                height_Z72_combine = 7832;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 3973;
                y_Z72_back = 0;
                x_Z72_sleeve_left = 7740;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 7553;
                y_Z72_sleeve_right = 4014;
                x_Z72_hood_left = 0;
                y_Z72_hood_left = 4300;
                x_Z72_hood_right = 2031;
                y_Z72_hood_right = 4300;
                x_Z72_pocket = 5096;
                y_Z72_pocket = 5282;
                x_Z72_cuff_left = 4173;
                y_Z72_cuff_left = 4376;
                x_Z72_cuff_right = 5754;
                y_Z72_cuff_right = 4376;
                x_Z72_bottom = 0;
                y_Z72_bottom = 6998;
                break;
            case "2XL":
                width_front = 4061;
                height_front = 4342;
                width_back = 4061;
                height_back = 4403;
                width_sleeve = 3187;
                height_sleeve = 3935;
                width_hood = 1956;
                height_hood = 2666;
                width_bottom = 7182;
                height_bottom = 834;
                width_pocket = 2739;
                height_pocket = 1631;
                width_cuff = 1529;
                height_cuff = 834;

                width_Z72_combine = 10247;
                height_Z72_combine = 9312;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 4135;
                y_Z72_back = 0;
                x_Z72_sleeve_left = 3311;
                y_Z72_sleeve_left = 4468;
                x_Z72_sleeve_right = 0;
                y_Z72_sleeve_right = 4437;
                x_Z72_hood_left = 8268;
                y_Z72_hood_left = 0;
                x_Z72_hood_right = 8268;
                y_Z72_hood_right = 2677;
                x_Z72_pocket = 6115;
                y_Z72_pocket = 6600;
                x_Z72_cuff_left = 6661;
                y_Z72_cuff_left = 4539;
                x_Z72_cuff_right = 6661;
                y_Z72_cuff_right = 5550;
                x_Z72_bottom = 0;
                y_Z72_bottom = 8478;
                break;
            case "3XL":
                width_front = 4238;
                height_front = 4459;
                width_back = 4238;
                height_back = 4521;
                width_sleeve = 3317;
                height_sleeve = 4054;
                width_hood = 2038;
                height_hood = 2736;
                width_bottom = 7536;
                height_bottom = 834;
                width_pocket = 2860;
                height_pocket = 1675;
                width_cuff = 1588;
                height_cuff = 834;

                width_Z72_combine = 10671;
                height_Z72_combine = 9574;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 4336;
                y_Z72_back = 0;
                x_Z72_sleeve_left = 3452;
                y_Z72_sleeve_left = 4594;
                x_Z72_sleeve_right = 0;
                y_Z72_sleeve_right = 4543;
                x_Z72_hood_left = 8633;
                y_Z72_hood_left = 0;
                x_Z72_hood_right = 8633;
                y_Z72_hood_right = 2736;
                x_Z72_pocket = 6385;
                y_Z72_pocket = 6777;
                x_Z72_cuff_left = 6917;
                y_Z72_cuff_left = 4662;
                x_Z72_cuff_right = 6932;
                y_Z72_cuff_right = 5637;
                x_Z72_bottom = 0;
                y_Z72_bottom = 8740;
                break;
            case "4XL":
                width_front = 4415;
                height_front = 4576;
                width_back = 4415;
                height_back = 4638;
                width_sleeve = 3448;
                height_sleeve = 4172;
                width_hood = 2098;
                height_hood = 2795;
                width_bottom = 7891;
                height_bottom = 834;
                width_pocket = 2981;
                height_pocket = 1719;
                width_cuff = 1647;
                height_cuff = 834;

                width_Z72_combine = 9942;
                height_Z72_combine = 10182;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 0;
                y_Z72_back = 4646;
                x_Z72_sleeve_left = 4344;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 4237;
                y_Z72_sleeve_right = 4283;
                x_Z72_hood_left = 7862;
                y_Z72_hood_left = 0;
                x_Z72_hood_right = 7803;
                y_Z72_hood_right = 2780;
                x_Z72_pocket = 6982;
                y_Z72_pocket = 7512;
                x_Z72_cuff_left = 7733;
                y_Z72_cuff_left = 5648;
                x_Z72_cuff_right = 7733;
                y_Z72_cuff_right = 6566;
                x_Z72_bottom = 0;
                y_Z72_bottom = 9348;
                break;
            case "5XL":
                width_front = 4591;
                height_front = 4694;
                width_back = 4591;
                height_back = 4754;
                width_sleeve = 3578;
                height_sleeve = 4291;
                width_hood = 2165;
                height_hood = 2853;
                width_bottom = 8244;
                height_bottom = 834;
                width_pocket = 3096;
                height_pocket = 1763;
                width_cuff = 1705;
                height_cuff = 834;

                width_Z72_combine = 10238;
                height_Z72_combine = 10527;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 0;
                y_Z72_back = 4812;
                x_Z72_sleeve_left = 4480;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 4331;
                y_Z72_sleeve_right = 4412;
                x_Z72_hood_left = 8073;
                y_Z72_hood_left = 0;
                x_Z72_hood_right = 8073;
                y_Z72_hood_right = 2853;
                x_Z72_pocket = 7154;
                y_Z72_pocket = 7711;
                x_Z72_cuff_left = 8059;
                y_Z72_cuff_left = 5813;
                x_Z72_cuff_right = 8059;
                y_Z72_cuff_right = 6750;
                x_Z72_bottom = 0;
                y_Z72_bottom = 9694;
                break;
            case "6XL":
                width_front = 4777;
                height_front = 4823;
                width_back = 4777;
                height_back = 4881;
                width_sleeve = 3715;
                height_sleeve = 4415;
                width_hood = 2335;
                height_hood = 2859;
                width_bottom = 8606;
                height_bottom = 834;
                width_pocket = 3221;
                height_pocket = 1817;
                width_cuff = 1772;
                height_cuff = 834;

                width_Z72_combine = 10808;
                height_Z72_combine = 10827;
                x_Z72_front = 0;
                y_Z72_front = 0;
                x_Z72_back = 0;
                y_Z72_back = 4983;
                x_Z72_sleeve_left = 4777;
                y_Z72_sleeve_left = 0;
                x_Z72_sleeve_right = 4777;
                y_Z72_sleeve_right = 4541;
                x_Z72_hood_left = 8473;
                y_Z72_hood_left = 0;
                x_Z72_hood_right = 8473;
                y_Z72_hood_right = 2860;
                x_Z72_pocket = 7587;
                y_Z72_pocket = 8047;
                x_Z72_cuff_left = 8755;
                y_Z72_cuff_left = 5960;
                x_Z72_cuff_right = 8755;
                y_Z72_cuff_right = 7004;
                x_Z72_bottom = 0;
                y_Z72_bottom = 9988;
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }
    }

    void setSizeGQY(String size) {
        switch (size) {
            case "3XS":
                width_front = 2616;
                height_front = 3225;
                width_back = 2616;
                height_back = 3283;
                width_sleeve = 2142;
                height_sleeve = 2748;
                width_hood = 1555;
                height_hood = 2262;
                width_bottom = 4583;
                height_bottom = 833;
                width_pocket = 1891;
                height_pocket = 1325;
                width_cuff = 1115;
                height_cuff = 833;
                break;
            case "2XS":
                width_front = 2763;
                height_front = 3401;
                width_back = 2763;
                height_back = 3460;
                width_sleeve = 2272;
                height_sleeve = 2927;
                width_hood = 1555;
                height_hood = 2262;
                width_bottom = 4819;
                height_bottom = 833;
                width_pocket = 2012;
                height_pocket = 1369;
                width_cuff = 1174;
                height_cuff = 833;
                break;
            case "XS":
                width_front = 2940;
                height_front = 3578;
                width_back = 2940;
                height_back = 3638;
                width_sleeve = 2402;
                height_sleeve = 3105;
                width_hood = 1612;
                height_hood = 2321;
                width_bottom = 5173;
                height_bottom = 833;
                width_pocket = 2133;
                height_pocket = 1413;
                width_cuff = 1233;
                height_cuff = 833;
                break;
            case "S":
                width_front = 3176;
                height_front = 3754;
                width_back = 3176;
                height_back = 3814;
                width_sleeve = 2532;
                height_sleeve = 3343;
                width_hood = 1669;
                height_hood = 2381;
                width_bottom = 5587;
                height_bottom = 833;
                width_pocket = 2254;
                height_pocket = 1456;
                width_cuff = 1233;
                height_cuff = 833;
                break;
            case "M":
                width_front = 3472;
                height_front = 3901;
                width_back = 3472;
                height_back = 3961;
                width_sleeve = 2663;
                height_sleeve = 3461;
                width_hood = 1730;
                height_hood = 2440;
                width_bottom = 6118;
                height_bottom = 833;
                width_pocket = 2375;
                height_pocket = 1500;
                width_cuff = 1292;
                height_cuff = 833;
                break;
            case "L":
                width_front = 3769;
                height_front = 4048;
                width_back = 3768;
                height_back = 4108;
                width_sleeve = 2793;
                height_sleeve = 3639;
                width_hood = 1792;
                height_hood = 2499;
                width_bottom = 6591;
                height_bottom = 833;
                width_pocket = 2496;
                height_pocket = 1544;
                width_cuff = 1351;
                height_cuff = 833;
                break;

            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }
    }
    

    public void showDialogSizeWrong(final String order_number){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog dialog_finish;
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogTransBackGround);
                dialog_finish = builder.create();
                dialog_finish.setCancelable(false);
                dialog_finish.show();
                View view_dialog = LayoutInflater.from(context).inflate(R.layout.item_dialog_finish, null);
                dialog_finish.setContentView(view_dialog);
                TextView tv_title = (TextView) view_dialog.findViewById(R.id.tv_dialog_title);
                TextView tv_content = (TextView) view_dialog.findViewById(R.id.tv_dialog_content);
                Button bt_yes = (Button) view_dialog.findViewById(R.id.bt_dialog_yes);

                tv_title.setText("错误！");
                tv_content.setText("单号："+order_number+"没有这个尺码");
                bt_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_finish.dismiss();
                        getActivity().finish();
                    }
                });
            }
        });
    }

    boolean checkContains(String nameContains){
        for (int i = 0; i < orderItems.get(currentID).imgs.size(); i++) {
            if (orderItems.get(currentID).imgs.get(i).contains(nameContains)) {
                return true;
            }
        }
        return false;
    }
    Bitmap getBitmapWith(String nameContains){
        for (int i = 0; i < orderItems.get(currentID).imgs.size(); i++) {
            if (orderItems.get(currentID).imgs.get(i).contains(nameContains)) {
                return MainActivity.instance.bitmaps.get(i);
            }
        }
        return null;
    }
}
