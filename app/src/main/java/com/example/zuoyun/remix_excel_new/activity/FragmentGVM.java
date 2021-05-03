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

public class FragmentGVM extends BaseFragment {
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

    int width_front, width_back, width_arm, width_collar_big, width_collar_small, width_part_back, width_part1, width_part2;
    int height_front, height_back, height_arm, height_collar_big, height_collar_small, height_part_back, height_part1, height_part2;
    int widthCutPart1, heightCutPart1, xCutPart1;

    int id_front, id_back, id_sleeve_left, id_sleeve_right, id_collar_big, id_collar_small;
    

    int num;
    String strPlus = "";
    int intPlus = 1;
    boolean sizeOK = true;

    Paint rectPaint, paint, paintRed, paintBlue, rectBorderPaint, paintSmall;
    String time;

    @Override
    public int getLayout() {
        return R.layout.fragmentdg;
    }

    @Override
    public void initData(View view) {
        context = getContext();
        ButterKnife.bind(this, view);
        orderItems = MainActivity.instance.orderItems;
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
        paint.setTextSize(24);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(20);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(20);
        paintBlue.setTypeface(Typeface.DEFAULT_BOLD);
        paintBlue.setAntiAlias(true);

        paintSmall = new Paint();
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(20);
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

    public void remix() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                setSize(orderItems.get(currentID).sizeStr);
                if (sizeOK) {
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
            }
        }.start();

    }

    void drawTextFront(Canvas canvas) {
        canvas.drawRect(1000, 4987 - 23, 1000 + 500, 4987, rectPaint);
        canvas.drawText("GVM男polo衫 " + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number, 1000, 4987 - 2, paint);
    }

    void drawTextBack(Canvas canvas) {
        canvas.drawRect(1000, 4991 - 23, 1000 + 500, 4991, rectPaint);
        canvas.drawText("GVM男polo衫 " + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number, 1000, 4991 - 2, paint);
    }

    void drawTextArmL(Canvas canvas) {
        canvas.drawRect(1318, 38 - 23, 1318 + 167, 38, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + " 左", 1318, 38 - 2, paint);
    }

    void drawTextArmR(Canvas canvas) {
        canvas.drawRect(1318, 38 - 23, 1318 + 167, 38, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + " 右", 1318, 38 - 2, paint);
    }

    void drawTextPart1(Canvas canvas) {
        canvas.drawRect(20, 1188 - 23, 20 + 200, 1188, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number, 20, 1188 - 2, paint);
    }

    public void remixx() {
        int margin = 80;
        Bitmap bitmapCombine = Bitmap.createBitmap(width_front + width_back + width_arm + width_collar_small + margin * 3, height_back, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
            if (MainActivity.instance.bitmaps.get(0).getWidth() != 8263) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 8263, 8263, true));
            }
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 399, 3055, 3616, 5000);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4132, 3055, 3728, 5000);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //armL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4611, 207, 2800, 1800);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, 0, null);

            //armR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 811, 207, 2800, 1800);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm + margin, null);

            //collarBig
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2556, 2352, 2913, 525);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_big, height_collar_big, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + margin * 2, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + height_collar_big + margin * 3, null);

            //collarSamll
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2556, 2352, 2913, 458);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 3013, 458, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_small, height_collar_small, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, height_collar_small + margin, null);

            //houpian
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5715, 3211, 603, 469);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gv_houpian);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part_back, height_part_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 4 + 800, height_collar_small * 2 + margin * 2 + 800, null);

            //part1
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 399 + xCutPart1, 3475, widthCutPart1, heightCutPart1);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 502, 1194, true);
            Bitmap bitmapPart2 = Bitmap.createBitmap(bitmapTemp.copy(Bitmap.Config.ARGB_8888, true), 0, 0, 324, 1194);

            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gv_part1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPart1(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 4, height_collar_small * 2 + 800 + margin * 2, null);

            //part2
            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1);
            bitmapPart2 = Bitmap.createBitmap(bitmapPart2, 0, 0, 324, 1194, matrix, true);
            canvasTemp = new Canvas(bitmapPart2);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gv_part2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPart1(canvasTemp);
            canvasCombine.drawBitmap(bitmapPart2, width_front + width_back + width_arm + margin * 4, height_collar_small * 2 + 800 + height_part1 + margin * 3, null);

            bitmapPart2.recycle();
            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).isPPSL) {
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2887, 562, 3616, 5000);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2831, 562, 3728, 5000);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //armL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 6564, 1394, 2800, 1800);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, 0, null);

            //armR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 36, 1396, 2800, 1800);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm + margin, null);


            //collarBig
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3237, 37, 2913, 525);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_big, height_collar_big, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + margin * 2, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + height_collar_big + margin * 3, null);

            //collarSamll
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3188, 37, 3013, 458);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_small, height_collar_small, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, height_collar_small + margin, null);

            //houpian
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4410, 718, 603, 469);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gv_houpian);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part_back, height_part_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 4 + 800, height_collar_small * 2 + margin * 2 + 800, null);

            //part1
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2887 + xCutPart1, 982, widthCutPart1, heightCutPart1);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 502, 1194, true);
            Bitmap bitmapPart2 = Bitmap.createBitmap(bitmapTemp.copy(Bitmap.Config.ARGB_8888, true), 0, 0, 324, 1194);

            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gv_part1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPart1(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 4, height_collar_small * 2 + 800 + margin * 2, null);
            bitmapTemp.recycle();

            //part2
            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1);
            bitmapPart2 = Bitmap.createBitmap(bitmapPart2, 0, 0, 324, 1194, matrix, true);
            canvasTemp = new Canvas(bitmapPart2);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gv_part2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextPart1(canvasTemp);
            canvasCombine.drawBitmap(bitmapPart2, width_front + width_back + width_arm + margin * 4, height_collar_small * 2 + 800 + height_part1 + margin * 3, null);
            bitmapPart2.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2851, 0, 3616, 5000);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2800, 0, 3728, 5000);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //armL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 1800, 2800, 1800);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, 0, null);

            //armR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 2800, 1800);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm + margin, null);

            //collarBig
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3208, 0, 2913, 525);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_big, height_collar_big, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + margin * 2, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + height_collar_big + margin * 3, null);

            //collarSamll
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3152, 0, 3013, 458);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_small, height_collar_small, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, height_collar_small + margin, null);

            //houpian
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4374, 156, 603, 469);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gv_houpian);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part_back, height_part_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 4 + 800, height_collar_small * 2 + margin * 2 + 800, null);

            //part1
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2851 + xCutPart1, 422, widthCutPart1, heightCutPart1);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 502, 1194, true);
            Bitmap bitmapPart2 = Bitmap.createBitmap(bitmapTemp.copy(Bitmap.Config.ARGB_8888, true), 0, 0, 324, 1194);

            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gv_part1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPart1(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 4, height_collar_small * 2 + 800 + margin * 2, null);
            bitmapTemp.recycle();

            //part2
            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1);
            bitmapPart2 = Bitmap.createBitmap(bitmapPart2, 0, 0, 324, 1194, matrix, true);
            canvasTemp = new Canvas(bitmapPart2);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gv_part2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextPart1(canvasTemp);
            canvasCombine.drawBitmap(bitmapPart2, width_front + width_back + width_arm + margin * 4, height_collar_small * 2 + 800 + height_part1 + margin * 3, null);
            bitmapPart2.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 2) {
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 536, 0, 3616, 5000);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp.recycle();

            //后面
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 368, 0, 3728, 5000);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);
            bitmapTemp.recycle();

            //armL
            Bitmap bitmapArm = Bitmap.createBitmap(2800, 1800, Bitmap.Config.ARGB_8888);
            Canvas canvasArm = new Canvas(bitmapArm);
            canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasArm.drawColor(0xffffffff);

            Bitmap bitmapHalf = Bitmap.createBitmap(1400, 1800, Bitmap.Config.ARGB_8888);
            Canvas canvasHalf = new Canvas(bitmapHalf);
            canvasHalf.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasHalf.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2734, 324, 1763, 2076);
            Matrix matrix = new Matrix();
            matrix.postRotate(12.5f);
            matrix.postTranslate(77, -296);
            canvasArm.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 379, 1763, 2076);
            matrix.reset();
            matrix.postRotate(-12.5f);
            matrix.postTranslate(-370, 86);
            canvasHalf.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();
            canvasArm.drawBitmap(bitmapHalf, 1400, 0, null);
            bitmapHalf.recycle();

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_left);
            canvasArm.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextArmL(canvasArm);
            bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, 0, null);
            bitmapArm.recycle();

            //armR
            bitmapArm = Bitmap.createBitmap(2800, 1800, Bitmap.Config.ARGB_8888);
            canvasArm = new Canvas(bitmapArm);
            canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasArm.drawColor(0xffffffff);

            bitmapHalf = Bitmap.createBitmap(1400, 1800, Bitmap.Config.ARGB_8888);
            canvasHalf = new Canvas(bitmapHalf);
            canvasHalf.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasHalf.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2746, 324, 1763, 2076);
            matrix = new Matrix();
            matrix.postRotate(12.5f);
            matrix.postTranslate(77, -296);
            canvasArm.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 89, 379, 1763, 2076);
            matrix.reset();
            matrix.postRotate(-12.5f);
            matrix.postTranslate(-370, 86);
            canvasHalf.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();
            canvasArm.drawBitmap(bitmapHalf, 1400, 0, null);
            bitmapHalf.recycle();

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_right);
            canvasArm.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextArmR(canvasArm);
            bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapArm, width_front + width_back + margin * 2, height_arm + margin, null);
            bitmapArm.recycle();


            //collarBigF
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 893, 0, 2913, 525);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_big, height_collar_big, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + margin * 2, null);
            bitmapTemp.recycle();

            //collarBigB
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 893, 0, 2913, 525);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_big, height_collar_big, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + height_collar_big + margin * 3, null);
            bitmapTemp.recycle();

            //collarSamllF
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 837, 0, 3013, 458);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_small, height_collar_small, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, 0, null);
            bitmapTemp.recycle();

            //collarSamllF
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 837, 0, 3013, 458);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_small, height_collar_small, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, height_collar_small + margin, null);
            bitmapTemp.recycle();

            //houpian
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1942, 156, 603, 469);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gv_houpian);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part_back, height_part_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 4 + 800, height_collar_small * 2 + margin * 2 + 800, null);
            bitmapTemp.recycle();

            //part1
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 536 + xCutPart1, 422, widthCutPart1, heightCutPart1);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 502, 1194, true);
            Bitmap bitmapPart2 = Bitmap.createBitmap(bitmapTemp.copy(Bitmap.Config.ARGB_8888, true), 0, 0, 324, 1194);

            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gv_part1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextPart1(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 4, height_collar_small * 2 + 800 + margin * 2, null);
            bitmapTemp.recycle();

            //part2
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapPart2 = Bitmap.createBitmap(bitmapPart2, 0, 0, 324, 1194, matrix, true);
            canvasTemp = new Canvas(bitmapPart2);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gv_part2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextPart1(canvasTemp);
            canvasCombine.drawBitmap(bitmapPart2, width_front + width_back + width_arm + margin * 4, height_collar_small * 2 + 800 + height_part1 + margin * 3, null);
            bitmapPart2.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 5) {
            //前
            Bitmap bitmapTemp = MainActivity.instance.bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //armL
            bitmapTemp = MainActivity.instance.bitmaps.get(3).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, 0, null);

            //armR
            bitmapTemp = MainActivity.instance.bitmaps.get(4).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm + margin, null);

            //collarBig
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 87, 0, 2723, 525);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 2913, 525, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_big, height_collar_big, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + margin * 2, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + height_collar_big + margin * 3, null);

            //collarSamll
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 302, 0 + 360, 3013, 458);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_small, height_collar_small, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, height_collar_small + margin, null);

            //houpian
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1523, 156 + 260, 603, 469);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gv_houpian);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part_back, height_part_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 4 + 800, height_collar_small * 2 + margin * 2 + 800, null);

            //part1
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), xCutPart1, 422, widthCutPart1, heightCutPart1);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 502, 1194, true);
            Bitmap bitmapPart2 = Bitmap.createBitmap(bitmapTemp.copy(Bitmap.Config.ARGB_8888, true), 0, 0, 324, 1194);

            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gv_part1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPart1(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 4, height_collar_small * 2 + 800 + margin * 2, null);
            bitmapTemp.recycle();

            //part2
            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1);
            bitmapPart2 = Bitmap.createBitmap(bitmapPart2, 0, 0, 324, 1194, matrix, true);
            canvasTemp = new Canvas(bitmapPart2);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gv_part2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextPart1(canvasTemp);
            canvasCombine.drawBitmap(bitmapPart2, width_front + width_back + width_arm + margin * 4, height_collar_small * 2 + 800 + height_part1 + margin * 3, null);
            bitmapPart2.recycle();
        }


        try {
            Matrix matrix2 = new Matrix();
            matrix2.postRotate(90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix2, true);

            String nameCombine = "Polo衫男_ " + orderItems.get(currentID).sizeStr + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 150);
            bitmapCombine.recycle();

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
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku);
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

    void setSize(String size) {
        width_part_back = 1082;
        height_part_back = 850;
        width_part1 = 502;
        height_part1 = 1194;
        width_part2 = 324;
        height_part2 = 1194;

        switch (size) {
            case "S":
                width_front = 3429;
                height_front = 4584;
                width_back = 3456;
                height_back = 4878;
                width_arm = 2663;
                height_arm = 1707;
                width_collar_big = 2683;
                height_collar_big = 515;
                width_collar_small = 2841;
                height_collar_small = 454;

                widthCutPart1 = 529;
                heightCutPart1 = 1302;
                xCutPart1 = 1668;

                id_front = R.drawable.gvm_front_m;
                id_back = R.drawable.gvm_back_m;
                id_sleeve_left = R.drawable.gvm_sleeve_left_m;
                id_sleeve_right = R.drawable.gvm_sleeve_right_m;
                id_collar_big = R.drawable.gvm_collar_big_m;
                id_collar_small = R.drawable.gvm_collar_small_m;
                break;
            case "M":
                width_front = 3606;
                height_front = 4694;
                width_back = 3634;
                height_back = 4987;
                width_arm = 2787;
                height_arm = 1783;
                width_collar_big = 2802;
                height_collar_big = 521;
                width_collar_small = 2959;
                height_collar_small = 461;

                widthCutPart1 = 503;
                heightCutPart1 = 1271;
                xCutPart1 = 1675;

                id_front = R.drawable.gvm_front_m;
                id_back = R.drawable.gvm_back_m;
                id_sleeve_left = R.drawable.gvm_sleeve_left_m;
                id_sleeve_right = R.drawable.gvm_sleeve_right_m;
                id_collar_big = R.drawable.gvm_collar_big_m;
                id_collar_small = R.drawable.gvm_collar_small_m;
                break;
            case "L":
                width_front = 3783;
                height_front = 4803;
                width_back = 3810;
                height_back = 5097;
                width_arm = 2911;
                height_arm = 1858;
                width_collar_big = 2920;
                height_collar_big = 528;
                width_collar_small = 3078;
                height_collar_small = 468;

                widthCutPart1 = 479;
                heightCutPart1 = 1242;
                xCutPart1 = 1681;

                id_front = R.drawable.gvm_front_m;
                id_back = R.drawable.gvm_back_m;
                id_sleeve_left = R.drawable.gvm_sleeve_left_m;
                id_sleeve_right = R.drawable.gvm_sleeve_right_m;
                id_collar_big = R.drawable.gvm_collar_big_m;
                id_collar_small = R.drawable.gvm_collar_small_m;
                break;
            case "XL":
                width_front = 4020;
                height_front = 4950;
                width_back = 4047;
                height_back = 5244;
                width_arm = 3069;
                height_arm = 1934;
                width_collar_big = 3065;
                height_collar_big = 536;
                width_collar_small = 3224;
                height_collar_small = 475;

                widthCutPart1 = 451;
                heightCutPart1 = 1206;
                xCutPart1 = 1689;

                id_front = R.drawable.gvm_front_2xl;
                id_back = R.drawable.gvm_back_2xl;
                id_sleeve_left = R.drawable.gvm_sleeve_left_2xl;
                id_sleeve_right = R.drawable.gvm_sleeve_right_2xl;
                id_collar_big = R.drawable.gvm_collar_big_2xl;
                id_collar_small = R.drawable.gvm_collar_small_2xl;
                break;
            case "2XL":
                width_front = 4256;
                height_front = 5097;
                width_back = 4282;
                height_back = 5390;
                width_arm = 3227;
                height_arm = 2009;
                width_collar_big = 3211;
                height_collar_big = 543;
                width_collar_small = 3370;
                height_collar_small = 482;

                widthCutPart1 = 426;
                heightCutPart1 = 1171;
                xCutPart1 = 1695;

                id_front = R.drawable.gvm_front_2xl;
                id_back = R.drawable.gvm_back_2xl;
                id_sleeve_left = R.drawable.gvm_sleeve_left_2xl;
                id_sleeve_right = R.drawable.gvm_sleeve_right_2xl;
                id_collar_big = R.drawable.gvm_collar_big_2xl;
                id_collar_small = R.drawable.gvm_collar_small_2xl;
                break;
            case "3XL":
                width_front = 4492;
                height_front = 5244;
                width_back = 4518;
                height_back = 5537;
                width_arm = 3384;
                height_arm = 2085;
                width_collar_big = 3357;
                height_collar_big = 551;
                width_collar_small = 3516;
                height_collar_small = 490;

                widthCutPart1 = 404;
                heightCutPart1 = 1138;
                xCutPart1 = 1701;

                id_front = R.drawable.gvm_front_2xl;
                id_back = R.drawable.gvm_back_2xl;
                id_sleeve_left = R.drawable.gvm_sleeve_left_2xl;
                id_sleeve_right = R.drawable.gvm_sleeve_right_2xl;
                id_collar_big = R.drawable.gvm_collar_big_2xl;
                id_collar_small = R.drawable.gvm_collar_small_2xl;
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }
        
        width_front += 20;
        height_front += 20;
        width_back += 20;
        height_back += 20;
        width_arm += 20;
        height_arm += 20;
        width_collar_big += 20;
        height_collar_big += 20;
        width_collar_small += 20;
        height_collar_small += 20;
    }

    public void showDialogSizeWrong(final String order_number) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog dialog_finish;
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTransBackGround);
                dialog_finish = builder.create();
                dialog_finish.setCancelable(false);
                dialog_finish.show();
                View view_dialog = LayoutInflater.from(context).inflate(R.layout.item_dialog_finish, null);
                dialog_finish.setContentView(view_dialog);
                TextView tv_title = (TextView) view_dialog.findViewById(R.id.tv_dialog_title);
                TextView tv_content = (TextView) view_dialog.findViewById(R.id.tv_dialog_content);
                Button bt_yes = (Button) view_dialog.findViewById(R.id.bt_dialog_yes);

                tv_title.setText("错误！");
                tv_content.setText("单号：" + order_number + "没有这个尺码");
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

}
