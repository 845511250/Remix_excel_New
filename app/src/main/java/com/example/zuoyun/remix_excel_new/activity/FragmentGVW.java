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

public class FragmentGVW extends BaseFragment {
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

    int width_front,width_back,width_arm,width_collar_big,width_collar_small,width_part_back,width_part1, width_part2;
    int height_front,height_back,height_arm,height_collar_big,height_collar_small,height_part_back,height_part1, height_part2;
    int widthCutPart1,heightCutPart1,xCutPart1;

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

    public void remix(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                setSize(orderItems.get(currentID).sizeStr);
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
        canvas.drawRect(1000, 3914 - 23, 1000 + 500, 3914, rectPaint);
        canvas.drawText("GVW女polo衫 " + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number, 1000, 3914 - 2, paint);
    }
    void drawTextBack(Canvas canvas) {
        canvas.drawRect(1000, 4111 - 23, 1000 + 500, 4111, rectPaint);
        canvas.drawText("GVW女polo衫 " + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number, 1000, 4111 - 2, paint);
    }

    void drawTextArmL(Canvas canvas) {
        canvas.drawRect(500, 1304 - 23, 500 + 500, 1304, rectPaint);
        canvas.drawText("左 " + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number, 500, 1304 - 2, paint);
    }
    void drawTextArmR(Canvas canvas) {
        canvas.drawRect(500, 1304 - 23, 500 + 500, 1304, rectPaint);
        canvas.drawText("右 " + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number, 500, 1304 - 2, paint);
    }

    public void remixx(){
        int margin = 120;
        Bitmap bitmapCombine = Bitmap.createBitmap(width_front + width_back + width_arm + width_collar_small + margin * 3, height_back, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 7000) {//jj
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 257, 2882, 3139, 3927);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3600, 2690, 3142, 4120);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //armL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3974, 194, 2398, 1312);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, 0, null);

            //armR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 628, 193, 2398, 1312);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm + margin, null);

            //collarBig
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2059, 1967, 2683, 472);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_big, height_collar_big, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + margin * 2, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + height_collar_big + margin * 3, null);

            //collarSamll
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2115, 2010, 2578, 348);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 2907, 349, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_small, height_collar_small, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, height_collar_small + margin - 20, null);

            //part1
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 257 + xCutPart1, 2882 + 592, widthCutPart1, heightCutPart1);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part1, height_part1, true);
            Bitmap bitmapPart2 = Bitmap.createBitmap(bitmapTemp.copy(Bitmap.Config.ARGB_8888, true), 0, 0, width_part2, height_part2);

            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gvw_qianjin_behind);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_collar_big + margin * 4, height_collar_small * 2 + margin * 2 + 100, null);
            bitmapTemp.recycle();

            //part2
            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1);
            bitmapPart2 = Bitmap.createBitmap(bitmapPart2, 0, 0, width_part2, height_part2, matrix, true);
            canvasTemp = new Canvas(bitmapPart2);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gvw_qianjin_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            canvasCombine.drawBitmap(bitmapPart2, width_front + width_back + width_collar_big + margin * 4, height_collar_small * 2 + height_part1 + margin * 3 + 100, null);
            bitmapPart2.recycle();

            //houpian
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4682, 2819, 979, 740);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gvw_houpian);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part_back, height_part_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_collar_big + width_part1 + margin * 6, height_collar_small * 2 + margin * 2 + 100, null);

        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2398, 131, 3142, 3927);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2398, 0, 3142, 4120);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //armL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5540, 236, 2398, 1312);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, 0, null);

            //armR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 235, 2398, 1312);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm + margin, null);

            //collarBigF
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2628, 0, 2683, 472);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_big, height_collar_big, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + margin * 2, null);

            //collarBigB
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2628, 0, 2683, 472);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_big, height_collar_big, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + height_collar_big + margin * 3, null);
            bitmapTemp.recycle();

            //collarSamllF
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2516, 0, 2907, 348);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_small, height_collar_small, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, 0, null);

            //collarSamllF
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2516, 0, 2907, 348);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_small, height_collar_small, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, height_collar_small + margin - 20, null);

            //part1
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2398 + xCutPart1, 592, widthCutPart1, heightCutPart1);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part1, height_part1, true);
            Bitmap bitmapPart2 = Bitmap.createBitmap(bitmapTemp.copy(Bitmap.Config.ARGB_8888, true), 0, 0, width_part2, height_part2);

            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gvw_qianjin_behind);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_collar_big + margin * 4, height_collar_small * 2 + margin * 2 + 100, null);
            bitmapTemp.recycle();

            //part2
            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1);
            bitmapPart2 = Bitmap.createBitmap(bitmapPart2, 0, 0, width_part2, height_part2, matrix, true);
            canvasTemp = new Canvas(bitmapPart2);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gvw_qianjin_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            canvasCombine.drawBitmap(bitmapPart2, width_front + width_back + width_collar_big + margin * 4, height_collar_small * 2 + height_part1 + margin * 3 + 100, null);
            bitmapPart2.recycle();

            //houpian
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3479, 131, 979, 740);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gvw_houpian);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part_back, height_part_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_collar_big + width_part1 + margin * 6, height_collar_small * 2 + margin * 2 + 100, null);

        } else if (orderItems.get(currentID).isPPSL) {
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2429, 607, 3142, 3927);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2429, 476, 3142, 4120);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //armL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 5571, 712, 2398, 1312);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, 0, null);

            //armR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 31, 711, 2398, 1312);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm + margin, null);

            //collarBig
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2658, 4, 2683, 472);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_big, height_collar_big, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + margin * 2, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + height_collar_big + margin * 3, null);

            //collarSamll
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2546, 4, 2907, 348);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_small, height_collar_small, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, height_collar_small + margin, null);

            //houpian
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3510, 607, 979, 740);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gvw_houpian);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part_back, height_part_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 4 + 800, height_collar_small * 2 + margin * 2 + 800, null);

            //part1
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2429 + xCutPart1, 1068, widthCutPart1, heightCutPart1);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part1, height_part1, true);
            Bitmap bitmapPart2 = Bitmap.createBitmap(bitmapTemp.copy(Bitmap.Config.ARGB_8888, true), 0, 0, width_part2, height_part2);

            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gvw_qianjin_behind);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 4, height_collar_small * 2 + 800 + margin * 2, null);
            bitmapTemp.recycle();

            //part2
            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1);
            bitmapPart2 = Bitmap.createBitmap(bitmapPart2, 0, 0, width_part2, height_part2, matrix, true);
            canvasTemp = new Canvas(bitmapPart2);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gvw_qianjin_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            canvasCombine.drawBitmap(bitmapPart2, width_front + width_back + width_arm + margin * 4, height_collar_small * 2 + 800 + height_part1 + margin * 3, null);
            bitmapPart2.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 5) {
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(3142, 3927, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(3142, 4120, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //armL
            bitmapTemp = Bitmap.createBitmap(2398, 1312, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, 0, null);

            //armR
            bitmapTemp = Bitmap.createBitmap(2398, 1312, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm + margin, null);

            //collarBig
            bitmapTemp = Bitmap.createBitmap(2683, 471, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_big, height_collar_big, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + margin * 2, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_arm * 2 + height_collar_big + margin * 3, null);

            //collarSamllF
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 117, 0, 2907, 348);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar_small, height_collar_small, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_arm + margin * 2, height_collar_small + margin - 20, null);

            //part1
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), xCutPart1, 461, widthCutPart1, heightCutPart1);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part1, height_part1, true);
            Bitmap bitmapPart2 = Bitmap.createBitmap(bitmapTemp.copy(Bitmap.Config.ARGB_8888, true), 0, 0, width_part2, height_part2);

            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gvw_qianjin_behind);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_collar_big + margin * 4, height_collar_small * 2 + margin * 2 + 100, null);
            bitmapTemp.recycle();

            //part2
            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1);
            bitmapPart2 = Bitmap.createBitmap(bitmapPart2, 0, 0, width_part2, height_part2, matrix, true);
            canvasTemp = new Canvas(bitmapPart2);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gvw_qianjin_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            canvasCombine.drawBitmap(bitmapPart2, width_front + width_back + width_collar_big + margin * 4, height_collar_small * 2 + height_part1 + margin * 3 + 100, null);
            bitmapPart2.recycle();

            //houpian
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 1081, 0, 979, 740);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gvw_houpian);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part_back, height_part_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + width_collar_big + width_part1 + margin * 6, height_collar_small * 2 + margin * 2 + 100, null);
        }


        try {
            Matrix matrix2 = new Matrix();
            matrix2.postRotate(90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix2, true);

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
        if (MainActivity.instance.tb_auto.isChecked()) {
            remix();
        }

    }

    void setSize(String size) {
        width_part_back = 979;
        height_part_back = 740;
        width_part1 = 503;
        height_part1 = 1100;
        width_part2 = 320;
        height_part2 = 1100;

        switch (size) {
            case "XS":
                width_front = 2846;
                height_front = 3691;
                width_back = 2846;
                height_back = 3883;
                width_arm = 2151;
                height_arm = 1253;
                width_collar_big = 2535;
                height_collar_big = 462;
                width_collar_small = 2760;
                height_collar_small = 340;

                widthCutPart1 = 555;
                heightCutPart1 = 1180;
                xCutPart1 = 1425;

                id_front = R.drawable.gvw_front_s;
                id_back = R.drawable.gvw_back_s;
                id_sleeve_left = R.drawable.gvw_sleeve_left_s;
                id_sleeve_right = R.drawable.gvw_sleeve_right_s;
                id_collar_big = R.drawable.gvw_collar_big_s;
                id_collar_small = R.drawable.gvw_collar_small_s;
                break;
            case "S":
                width_front = 2994;
                height_front = 3809;
                width_back = 2994;
                height_back = 4001;
                width_arm = 2274;
                height_arm = 1282;
                width_collar_big = 2609;
                height_collar_big = 467;
                width_collar_small = 2834;
                height_collar_small = 344;

                widthCutPart1 = 527;
                heightCutPart1 = 1144;
                xCutPart1 = 1432;

                id_front = R.drawable.gvw_front_s;
                id_back = R.drawable.gvw_back_s;
                id_sleeve_left = R.drawable.gvw_sleeve_left_s;
                id_sleeve_right = R.drawable.gvw_sleeve_right_s;
                id_collar_big = R.drawable.gvw_collar_big_s;
                id_collar_small = R.drawable.gvw_collar_small_s;
                break;
            case "M":
                width_front = 3142;
                height_front = 3927;
                width_back = 3142;
                height_back = 4120;
                width_arm = 2398;
                height_arm = 1312;
                width_collar_big = 2683;
                height_collar_big = 472;
                width_collar_small = 2907;
                height_collar_small = 348;

                widthCutPart1 = 503;
                heightCutPart1 = 1110;
                xCutPart1 = 1439;

                id_front = R.drawable.gvw_front_s;
                id_back = R.drawable.gvw_back_s;
                id_sleeve_left = R.drawable.gvw_sleeve_left_s;
                id_sleeve_right = R.drawable.gvw_sleeve_right_s;
                id_collar_big = R.drawable.gvw_collar_big_s;
                id_collar_small = R.drawable.gvw_collar_small_s;
                break;
            case "L":
                width_front = 3289;
                height_front = 4045;
                width_back = 3289;
                height_back = 4238;
                width_arm = 2520;
                height_arm = 1341;
                width_collar_big = 2755;
                height_collar_big = 476;
                width_collar_small = 2980;
                height_collar_small = 352;

                widthCutPart1 = 480;
                heightCutPart1 = 1077;
                xCutPart1 = 1444;

                id_front = R.drawable.gvw_front_xl;
                id_back = R.drawable.gvw_back_xl;
                id_sleeve_left = R.drawable.gvw_sleeve_left_xl;
                id_sleeve_right = R.drawable.gvw_sleeve_right_xl;
                id_collar_big = R.drawable.gvw_collar_big_xl;
                id_collar_small = R.drawable.gvw_collar_small_xl;
                break;
            case "XL":
                width_front = 3437;
                height_front = 4162;
                width_back = 3437;
                height_back = 4356;
                width_arm = 2644;
                height_arm = 1371;
                width_collar_big = 2830;
                height_collar_big = 480;
                width_collar_small = 3054;
                height_collar_small = 356;

                widthCutPart1 = 459;
                heightCutPart1 = 1047;
                xCutPart1 = 1450;

                id_front = R.drawable.gvw_front_xl;
                id_back = R.drawable.gvw_back_xl;
                id_sleeve_left = R.drawable.gvw_sleeve_left_xl;
                id_sleeve_right = R.drawable.gvw_sleeve_right_xl;
                id_collar_big = R.drawable.gvw_collar_big_xl;
                id_collar_small = R.drawable.gvw_collar_small_xl;
                break;
            case "2XL":
                width_front = 3585;
                height_front = 4280;
                width_back = 3585;
                height_back = 4474;
                width_arm = 2765;
                height_arm = 1404;
                width_collar_big = 2903;
                height_collar_big = 483;
                width_collar_small = 3127;
                height_collar_small = 361;

                widthCutPart1 = 440;
                heightCutPart1 = 1018;
                xCutPart1 = 1455;

                id_front = R.drawable.gvw_front_xl;
                id_back = R.drawable.gvw_back_xl;
                id_sleeve_left = R.drawable.gvw_sleeve_left_xl;
                id_sleeve_right = R.drawable.gvw_sleeve_right_xl;
                id_collar_big = R.drawable.gvw_collar_big_xl;
                id_collar_small = R.drawable.gvw_collar_small_xl;
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

}
