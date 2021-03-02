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

public class FragmentFI extends BaseFragment {
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

    int width_front, width_back, width_bottom, width_cuff, width_sleeve, width_collar;
    int height_front, height_back, height_bottom, height_cuff, height_sleeve, height_collar;

    int width_combine_Z73, height_combine_z73;
    int x_front_z73, x_back_z73, x_sleeve_left_z73, x_sleeve_right_z73, x_cuff_left_z73, x_cuff_right_z73, x_collar_z73, x_bottom_z73;
    int y_front_z73, y_back_z73, y_sleeve_left_z73, y_sleeve_right_z73, y_cuff_left_z73, y_cuff_right_z73, y_collar_z73, y_bottom_z73;
    int id_front_z73, id_back_z73;


    int num;
    String strPlus = "";
    int intPlus = 1;
    boolean sizeOK = true;

    Paint rectPaint, paint, paintRed, paintBlue, rectBorderPaint, paintSmall;
    String time;
    String isWomen = "";

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
        isWomen = orderItems.get(currentID).sku.equals("FIW") || orderItems.get(currentID).sku.equals("Z73W") ? "-女款(小一码)-" : "";

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
        canvas.drawRect(200, 3862 - 25, 800, 3862, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).sizeStr + isWomen + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 200, 3862 - 2, paint);
    }

    void drawTextXiukouR(Canvas canvas) {
        canvas.drawRect(500, 6, 500 + 500, 6 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "袖口右 " + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 500, 6 + 22, paint);
    }

    void drawTextXiukouL(Canvas canvas) {
        canvas.drawRect(500, 6, 500 + 500, 6 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "袖口左 " + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 500, 6 + 22, paint);
    }

    void drawTextLingkou(Canvas canvas) {
        canvas.drawRect(1390, 10, 1390 + 200, 10 + 25, rectPaint);
        canvas.drawText("领口 " + orderItems.get(currentID).sizeStr, 1390, 10 + 23, paint);
    }

    void drawTextXiabai(Canvas canvas) {
        canvas.drawRect(3300, 10, 3300 + 500, 10 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "下摆 " + orderItems.get(currentID).sizeStr + isWomen + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 3300, 10 + 23, paint);
    }

    void drawTextXiuziR(Canvas canvas) {
        canvas.drawRect(1300, 3570 - 25, 1300 + 500, 3570, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "袖子右 " + orderItems.get(currentID).sizeStr + isWomen + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1300, 3570 - 2, paint);
    }

    void drawTextXiuziL(Canvas canvas) {
        canvas.drawRect(1300, 3570 - 25, 1300 + 500, 3570, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "袖子左 " + orderItems.get(currentID).sizeStr + isWomen + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1300, 3570 - 2, paint);
    }

    public void remixx() {
        Bitmap bitmapCombine = null;

        if (orderItems.get(currentID).sku.startsWith("Z73")) {
            bitmapCombine = Bitmap.createBitmap(width_combine_Z73, height_combine_z73, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            if (orderItems.get(currentID).imgs.size() == 1) {
                if (MainActivity.instance.bitmaps.get(0).getWidth() != 9193) {
                    MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 9193, 4772, true));
                }

                //前
                Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2804, 0, 3585, 3868);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front_z73);
                drawTextFront(canvasTemp);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, x_front_z73, y_front_z73, null);

                //后面
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2804, 0, 3585, 3864);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back_z73);
                drawTextFront(canvasTemp);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, x_back_z73, y_back_z73, null);

                //下摆
                bitmapTemp = Bitmap.createBitmap(3585 * 2, 906, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawColor(0xffffffff);

                Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2804, 3866, 3585, 906);
                canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
                canvasTemp.drawBitmap(bitmapCut, 3585, 0, null);
                bitmapCut.recycle();

                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z73_bottom);
                drawTextXiabai(canvasTemp);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_bottom, height_bottom, true);
                canvasCombine.drawBitmap(bitmapTemp, x_bottom_z73, y_bottom_z73, null);

                //领口
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3118, 221, 2958, 322);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z73_collar);
                drawTextLingkou(canvasTemp);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);
                canvasCombine.drawBitmap(bitmapTemp, x_collar_z73, y_collar_z73, null);

                //袖口左
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7146, 3866, 1290, 906);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z73_cuff);
                drawTextXiukouL(canvasTemp);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, x_cuff_left_z73, y_cuff_left_z73, null);

                //袖口右
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 757, 3866, 1290, 906);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z73_cuff);
                drawTextXiukouR(canvasTemp);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, x_cuff_right_z73, y_cuff_right_z73, null);

                //左袖子
                bitmapTemp = Bitmap.createBitmap(2804, 3575, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -6389, -291, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z73_sleeve_l);
                drawTextXiuziL(canvasTemp);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, x_sleeve_left_z73, y_sleeve_left_z73, null);

                //右袖子
                bitmapTemp = Bitmap.createBitmap(2804, 3575, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, -291, null);
                bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -2804, 3575, true);
                drawTextXiuziR(canvasTemp);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, x_sleeve_right_z73, y_sleeve_right_z73, null);

                bitmapTemp.recycle();
                bitmapDB.recycle();
            } else if (orderItems.get(currentID).imgs.size() == 8) {
                //前
                Bitmap bitmapTemp = Bitmap.createBitmap(3585, 3868, Bitmap.Config.ARGB_8888);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front_z73);
                drawTextFront(canvasTemp);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, x_front_z73, y_front_z73, null);

                //后面
                bitmapTemp = Bitmap.createBitmap(3585, 3864, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back_z73);
                drawTextFront(canvasTemp);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, x_back_z73, y_back_z73, null);

                //下摆
                bitmapTemp = Bitmap.createBitmap(3585 * 2, 906, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawColor(0xffffffff);

                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 3585, 0, null);

                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z73_bottom);
                drawTextXiabai(canvasTemp);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_bottom, height_bottom, true);
                canvasCombine.drawBitmap(bitmapTemp, x_bottom_z73, y_bottom_z73, null);

                //领口
                bitmapTemp = Bitmap.createBitmap(2958, 322, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z73_collar);
                drawTextLingkou(canvasTemp);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);
                canvasCombine.drawBitmap(bitmapTemp, x_collar_z73, y_collar_z73, null);

                //袖口左
                bitmapTemp = Bitmap.createBitmap(1290, 906, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z73_cuff);
                drawTextXiukouL(canvasTemp);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, x_cuff_left_z73, y_cuff_left_z73, null);

                //袖口右
                bitmapTemp = Bitmap.createBitmap(1290, 906, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(6), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z73_cuff);
                drawTextXiukouR(canvasTemp);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, x_cuff_right_z73, y_cuff_right_z73, null);

                //左袖子
                bitmapTemp = Bitmap.createBitmap(2804, 3575, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z73_sleeve_l);
                drawTextXiuziL(canvasTemp);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, x_sleeve_left_z73, y_sleeve_left_z73, null);

                //右袖子
                bitmapTemp = Bitmap.createBitmap(2804, 3575, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(7), 0, 0, null);
                bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -2804, 3575, true);
                drawTextXiuziR(canvasTemp);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, x_sleeve_right_z73, y_sleeve_right_z73, null);

                bitmapTemp.recycle();
                bitmapDB.recycle();
            }

        } else {
            int margin = 100;

            bitmapCombine = Bitmap.createBitmap(width_front + height_collar + margin, height_front * 2 + height_sleeve * 2 + height_cuff + margin * 5, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {//jj
                if (MainActivity.instance.bitmaps.get(0).getWidth() != 9700) {
                    MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 9700, 9700, true));
                }

                //右袖子
                Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1338, 5018, 2804, 3575);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiuzi_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front * 2 + margin * 2, null);

                //左袖子
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5550, 5018, 2804, 3575);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiuzi_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front * 2 + height_sleeve + margin * 3, null);

                //前
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1157, 86, 3585, 3868);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

                //后面
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4970, 86, 3585, 3864);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_back);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

                //下摆
                bitmapTemp = Bitmap.createBitmap(3585 * 2, 906, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawColor(0xffffffff);

                Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1157, 4038, 3585, 906);
                canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
                bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4970, 4038, 3585, 906);
                canvasTemp.drawBitmap(bitmapCut, 3585, 0, null);
                bitmapCut.recycle();

                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiabai);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiabai(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_bottom, height_bottom, true);

                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                matrix.postTranslate(height_bottom + width_sleeve + margin, height_front * 2 + margin * 2);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                //领口
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4699, 6664, 322, 2958);
                matrix.reset();
                matrix.postRotate(90);
                bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, 322, 2958, matrix, true);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_lingkou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextLingkou(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);

                matrix = new Matrix();
                matrix.postRotate(90);
                matrix.postTranslate(height_collar + width_front + margin, 0);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                //袖口左
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6295, 8710, 1290, 906);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiukou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiukouL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front * 2 + height_sleeve * 2 + margin * 4, null);

                //袖口右
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2083, 8710, 1290, 906);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiukou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiukouR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, width_cuff + margin, height_front * 2 + height_sleeve * 2 + margin * 4, null);

                bitmapDB.recycle();
                bitmapTemp.recycle();
            } else if (orderItems.get(currentID).imgs.size() == 1 || orderItems.get(currentID).imgs.size() == 2) {
                if (MainActivity.instance.bitmaps.get(0).getWidth() != 9193) {
                    MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 9193, 4772, true));
                }
                if (orderItems.get(currentID).imgs.size() == 2) {
                    if (MainActivity.instance.bitmaps.get(1).getWidth() != 9193) {
                        MainActivity.instance.bitmaps.set(1, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(1), 9193, 4772, true));
                    }
                }
                //右袖子
                Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 291, 2804, 3575);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiuzi_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front * 2 + margin * 2, null);

                //左袖子
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6389, 291, 2804, 3575);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiuzi_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front * 2 + height_sleeve + margin * 3, null);

                //前
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2804, 0, 3585, 3868);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

                //后面
                bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 2 ? MainActivity.instance.bitmaps.get(1) : MainActivity.instance.bitmaps.get(0), 2804, 0, 3585, 3864);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_back);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

                //下摆
                bitmapTemp = Bitmap.createBitmap(3585 * 2, 906, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawColor(0xffffffff);

                canvasTemp = new Canvas(bitmapTemp);
                Bitmap bitmapCut = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 2 ? MainActivity.instance.bitmaps.get(1) : MainActivity.instance.bitmaps.get(0), 2804, 3866, 3585, 906);
                canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
                bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2804, 3866, 3585, 906);
                canvasTemp.drawBitmap(bitmapCut, 3585, 0, null);
                bitmapCut.recycle();

                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiabai);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiabai(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_bottom, height_bottom, true);

                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                matrix.postTranslate(height_bottom + width_sleeve + margin, height_front * 2 + margin * 2);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                //领口
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3118, 221, 2958, 322);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_lingkou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextLingkou(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);

                matrix = new Matrix();
                matrix.postRotate(90);
                matrix.postTranslate(height_collar + width_front + margin, 0);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                //袖口左
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7146, 3866, 1290, 906);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiukou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiukouL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front * 2 + height_sleeve * 2 + margin * 4, null);

                //袖口右
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 757, 3866, 1290, 906);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiukou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextXiukouR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, width_cuff + margin, height_front * 2 + height_sleeve * 2 + margin * 4, null);
                bitmapTemp.recycle();
            } else if (orderItems.get(currentID).imgs.size() == 8) {
                //右袖子
                Bitmap bitmapTemp = Bitmap.createBitmap(2804, 3575, Bitmap.Config.ARGB_8888);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(7), 0, 0, null);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiuzi_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front * 2 + margin * 2, null);

                //左袖子
                bitmapTemp = Bitmap.createBitmap(2804, 3575, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiuzi_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front * 2 + height_sleeve + margin * 3, null);

                //前
                bitmapTemp = Bitmap.createBitmap(3585, 3868, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

                //后面
                bitmapTemp = Bitmap.createBitmap(3585, 3864, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_back);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

                //下摆
                bitmapTemp = Bitmap.createBitmap(3585 * 2, 906, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawColor(0xffffffff);

                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 3585, 0, null);

                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiabai);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiabai(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_bottom, height_bottom, true);

                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                matrix.postTranslate(height_bottom + width_sleeve + margin, height_front * 2 + margin * 2);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                //领口
                bitmapTemp = Bitmap.createBitmap(2958, 322, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_lingkou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextLingkou(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);

                matrix = new Matrix();
                matrix.postRotate(90);
                matrix.postTranslate(height_collar + width_front + margin, 0);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                //袖口左
                bitmapTemp = Bitmap.createBitmap(1290, 906, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiukou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiukouL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front * 2 + height_sleeve * 2 + margin * 4, null);

                //袖口右
                bitmapTemp = Bitmap.createBitmap(1290, 906, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(6), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiukou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextXiukouR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, width_cuff + margin, height_front * 2 + height_sleeve * 2 + margin * 4, null);
                bitmapTemp.recycle();
            } else if (orderItems.get(currentID).imgs.size() == 6) {
                //右袖子
                Bitmap bitmapTemp = Bitmap.createBitmap(2804, 3575, Bitmap.Config.ARGB_8888);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiuzi_r);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front * 2 + margin * 2, null);

                //左袖子
                bitmapTemp = Bitmap.createBitmap(2804, 3575, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiuzi_l);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiuziL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front * 2 + height_sleeve + margin * 3, null);

                //前
                bitmapTemp = Bitmap.createBitmap(3585, 3868, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

                //后面
                bitmapTemp = Bitmap.createBitmap(3585, 3864, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_back);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

                //下摆
                bitmapTemp = Bitmap.createBitmap(3585 * 2, 906, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawColor(0xffffffff);

                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 3585, 0, null);

                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiabai);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiabai(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_bottom, height_bottom, true);

                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                matrix.postTranslate(height_bottom + width_sleeve + margin, height_front * 2 + margin * 2);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                //领口
                bitmapTemp = Bitmap.createBitmap(2958, 322, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -314, -221, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_lingkou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextLingkou(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);

                matrix = new Matrix();
                matrix.postRotate(90);
                matrix.postTranslate(height_collar + width_front + margin, 0);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                //袖口左
                bitmapTemp = Bitmap.createBitmap(1290, 906, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiukou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextXiukouL(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front * 2 + height_sleeve * 2 + margin * 4, null);

                //袖口右
                bitmapTemp = Bitmap.createBitmap(1290, 906, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fi_xiukou);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                bitmapDB.recycle();
                drawTextXiukouR(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
                canvasCombine.drawBitmap(bitmapTemp, width_cuff + margin, height_front * 2 + height_sleeve * 2 + margin * 4, null);
                bitmapTemp.recycle();
            }
        }



        try {
//            matrix.reset();
//            matrix.postRotate(-90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

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
        if (orderItems.get(currentID).sku.equals("Z73W") || orderItems.get(currentID).sku.equals("FIW")) {
            switch (size) {
                case "XS":
                    width_front = 3169;
                    height_front = 3782;
                    width_back = 3169;
                    height_back = 3782;
                    width_sleeve = 2409;
                    height_sleeve = 3431;
                    width_cuff = 1205;
                    height_cuff = 892;
                    width_bottom = 5031;
                    height_bottom = 892;
                    width_collar = 2861;
                    height_collar = 348;

                    width_combine_Z73 = 10748;
                    height_combine_z73 = 5205;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 5243;
                    y_back_z73 = 1174;
                    x_sleeve_left_z73 = 8339;
                    y_sleeve_left_z73 = 1174;
                    x_sleeve_right_z73 = 3108;
                    y_sleeve_right_z73 = 0;
                    x_bottom_z73 = 5629;
                    y_bottom_z73 = 0;
                    x_cuff_left_z73 = 0;
                    y_cuff_left_z73 = 3874;
                    x_cuff_right_z73 = 1316;
                    y_cuff_right_z73 = 3877;
                    x_collar_z73 = 0;
                    y_collar_z73 = 4857;

                    id_front_z73 = R.drawable.z73_front_xs;
                    id_back_z73 = R.drawable.z73_back_xs;
                    break;
                case "S":
                    width_front = 3316;
                    height_front = 3898;
                    width_back = 3316;
                    height_back = 3898;
                    width_sleeve = 2541;
                    height_sleeve = 3520;
                    width_cuff = 1264;
                    height_cuff = 892;
                    width_bottom = 5327;
                    height_bottom = 892;
                    width_collar = 2914;
                    height_collar = 348;

                    width_combine_Z73 = 10429;
                    height_combine_z73 = 7490;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 3444;
                    y_back_z73 = 0;
                    x_sleeve_left_z73 = 2669;
                    y_sleeve_left_z73 = 3970;
                    x_sleeve_right_z73 = 0;
                    y_sleeve_right_z73 = 3970;
                    x_bottom_z73 = 5102;
                    y_bottom_z73 = 6598;
                    x_cuff_left_z73 = 5490;
                    y_cuff_left_z73 = 5549;
                    x_cuff_right_z73 = 6933;
                    y_cuff_right_z73 = 5549;
                    x_collar_z73 = 5490;
                    y_collar_z73 = 5019;

                    id_front_z73 = R.drawable.z73_front_xs;
                    id_back_z73 = R.drawable.z73_back_xs;
                    break;
                case "M":
                    width_front = 3463;
                    height_front = 4015;
                    width_back = 3463;
                    height_back = 4015;
                    width_sleeve = 2672;
                    height_sleeve = 3609;
                    width_cuff = 1323;
                    height_cuff = 892;
                    width_bottom = 5622;
                    height_bottom = 892;
                    width_collar = 3008;
                    height_collar = 348;

                    width_combine_Z73 = 9773;
                    height_combine_z73 = 7716;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 3541;
                    y_back_z73 = 0;
                    x_sleeve_left_z73 = 7003;
                    y_sleeve_left_z73 = 0;
                    x_sleeve_right_z73 = 0;
                    y_sleeve_right_z73 = 4107;
                    x_bottom_z73 = 2673;
                    y_bottom_z73 = 4107;
                    x_cuff_left_z73 = 8450;
                    y_cuff_left_z73 = 3709;
                    x_cuff_right_z73 = 8450;
                    y_cuff_right_z73 = 4702;
                    x_collar_z73 = 2814;
                    y_collar_z73 = 5117;

                    id_front_z73 = R.drawable.z73_front_xs;
                    id_back_z73 = R.drawable.z73_back_xs;
                    break;
                case "L":
                    width_front = 3611;
                    height_front = 4132;
                    width_back = 3611;
                    height_back = 4132;
                    width_sleeve = 2803;
                    height_sleeve = 3697;
                    width_cuff = 1382;
                    height_cuff = 892;
                    width_bottom = 5917;
                    height_bottom = 892;
                    width_collar = 3061;
                    height_collar = 348;

                    width_combine_Z73 =10515;
                    height_combine_z73 = 7964;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 3819;
                    y_back_z73 = 0;
                    x_sleeve_left_z73 = 7381;
                    y_sleeve_left_z73 = 0;
                    x_sleeve_right_z73 = 0;
                    y_sleeve_right_z73 = 4267;
                    x_bottom_z73 = 2914;
                    y_bottom_z73 = 4267;
                    x_cuff_left_z73 = 9133;
                    y_cuff_left_z73 = 3821;
                    x_cuff_right_z73 = 9133;
                    y_cuff_right_z73 = 4869;
                    x_collar_z73 = 2914;
                    y_collar_z73 = 5315;

                    id_front_z73 = R.drawable.z73_front_xl;
                    id_back_z73 = R.drawable.z73_back_xl;
                    break;
                case "XL":
                    width_front = 3758;
                    height_front = 4249;
                    width_back = 3758;
                    height_back = 4249;
                    width_sleeve = 2933;
                    height_sleeve = 3786;
                    width_cuff = 1441;
                    height_cuff = 892;
                    width_bottom = 6212;
                    height_bottom = 892;
                    width_collar = 3156;
                    height_collar = 348;

                    width_combine_Z73 =10632;
                    height_combine_z73 = 8132;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 3910;
                    y_back_z73 = 0;
                    x_sleeve_left_z73 = 7667;
                    y_sleeve_left_z73 = 0;
                    x_sleeve_right_z73 = 0;
                    y_sleeve_right_z73 = 4346;
                    x_bottom_z73 = 2850;
                    y_bottom_z73 = 4346;
                    x_cuff_left_z73 = 9191;
                    y_cuff_left_z73 = 3900;
                    x_cuff_right_z73 = 9191;
                    y_cuff_right_z73 = 4907;
                    x_collar_z73 = 3013;
                    y_collar_z73 = 5340;

                    id_front_z73 = R.drawable.z73_front_xl;
                    id_back_z73 = R.drawable.z73_back_xl;
                    break;
                case "2XL":
                    width_front = 3905;
                    height_front = 4366;
                    width_back = 3905;
                    height_back = 4366;
                    width_sleeve = 3065;
                    height_sleeve = 3875;
                    width_cuff = 1500;
                    height_cuff = 892;
                    width_bottom = 6508;
                    height_bottom = 892;
                    width_collar = 3250;
                    height_collar = 348;

                    width_combine_Z73 =10710;
                    height_combine_z73 = 8341;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 6805;
                    y_back_z73 = 0;
                    x_sleeve_left_z73 = 3821;
                    y_sleeve_left_z73 = 0;
                    x_sleeve_right_z73 = 0;
                    y_sleeve_right_z73 = 4466;
                    x_bottom_z73 = 2956;
                    y_bottom_z73 = 4490;
                    x_cuff_left_z73 = 3203;
                    y_cuff_left_z73 = 5511;
                    x_cuff_right_z73 = 4845;
                    y_cuff_right_z73 = 5511;
                    x_collar_z73 = 6530;
                    y_collar_z73 = 5609;

                    id_front_z73 = R.drawable.z73_front_xl;
                    id_back_z73 = R.drawable.z73_back_xl;
                    break;
                case "3XL":
                    width_front = 4053;
                    height_front = 4483;
                    width_back = 4053;
                    height_back = 4483;
                    width_sleeve = 3195;
                    height_sleeve = 3963;
                    width_cuff = 1559;
                    height_cuff = 892;
                    width_bottom = 6803;
                    height_bottom = 892;
                    width_collar = 3303;
                    height_collar = 348;

                    width_combine_Z73 =9792;
                    height_combine_z73 = 9534;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 4181;
                    y_back_z73 = 0;
                    x_sleeve_left_z73 = 3282;
                    y_sleeve_left_z73 = 4580;
                    x_sleeve_right_z73 = 0;
                    y_sleeve_right_z73 = 4584;
                    x_bottom_z73 = 0;
                    y_bottom_z73 = 8642;
                    x_cuff_left_z73 = 8233;
                    y_cuff_left_z73 = 0;
                    x_cuff_right_z73 = 8233;
                    y_cuff_right_z73 = 997;
                    x_collar_z73 = 6006;
                    y_collar_z73 = 4635;

                    id_front_z73 = R.drawable.z73_front_xl;
                    id_back_z73 = R.drawable.z73_back_xl;
                    break;
                case "4XL":
                    width_front = 4200;
                    height_front = 4601;
                    width_back = 4200;
                    height_back = 4601;
                    width_sleeve = 3326;
                    height_sleeve = 4052;
                    width_cuff = 1618;
                    height_cuff = 892;
                    width_bottom = 7098;
                    height_bottom = 892;
                    width_collar = 3398;
                    height_collar = 348;

                    width_combine_Z73 =10142;
                    height_combine_z73 = 9750;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 4324;
                    y_back_z73 = 0;
                    x_sleeve_left_z73 = 3423;
                    y_sleeve_left_z73 = 4705;
                    x_sleeve_right_z73 = 0;
                    y_sleeve_right_z73 = 4705;
                    x_bottom_z73 = 0;
                    y_bottom_z73 = 8858;
                    x_cuff_left_z73 = 8524;
                    y_cuff_left_z73 = 0;
                    x_cuff_right_z73 = 8524;
                    y_cuff_right_z73 = 1005;
                    x_collar_z73 = 6117;
                    y_collar_z73 = 4764;

                    id_front_z73 = R.drawable.z73_front_3xl;
                    id_back_z73 = R.drawable.z73_back_3xl;
                    break;
                case "5XL":
                    width_front = 4348;
                    height_front = 4718;
                    width_back = 4348;
                    height_back = 4718;
                    width_sleeve = 3457;
                    height_sleeve = 4141;
                    width_cuff = 1677;
                    height_cuff = 892;
                    width_bottom = 7394;
                    height_bottom = 892;
                    width_collar = 3492;
                    height_collar = 348;

                    width_combine_Z73 =10505;
                    height_combine_z73 = 9957;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 4481;
                    y_back_z73 = 0;
                    x_sleeve_left_z73 = 3573;
                    y_sleeve_left_z73 = 4815;
                    x_sleeve_right_z73 = 0;
                    y_sleeve_right_z73 = 4815;
                    x_bottom_z73 = 0;
                    y_bottom_z73 = 9065;
                    x_cuff_left_z73 = 8828;
                    y_cuff_left_z73 = 0;
                    x_cuff_right_z73 = 8828;
                    y_cuff_right_z73 = 991;
                    x_collar_z73 = 6298;
                    y_collar_z73 = 4815;

                    id_front_z73 = R.drawable.z73_front_3xl;
                    id_back_z73 = R.drawable.z73_back_3xl;
                    break;
                default:
                    showDialogSizeWrong(orderItems.get(currentID).order_number);
                    sizeOK = false;
                    break;
            }

        } else {
            switch (size) {
                case "2XS":
                    width_front = 3169;
                    height_front = 3782;
                    width_back = 3169;
                    height_back = 3782;
                    width_sleeve = 2409;
                    height_sleeve = 3431;
                    width_cuff = 1205;
                    height_cuff = 892;
                    width_bottom = 5031;
                    height_bottom = 892;
                    width_collar = 2861;
                    height_collar = 348;

                    width_combine_Z73 = 10748;
                    height_combine_z73 = 5205;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 5243;
                    y_back_z73 = 1174;
                    x_sleeve_left_z73 = 8339;
                    y_sleeve_left_z73 = 1174;
                    x_sleeve_right_z73 = 3108;
                    y_sleeve_right_z73 = 0;
                    x_bottom_z73 = 5629;
                    y_bottom_z73 = 0;
                    x_cuff_left_z73 = 0;
                    y_cuff_left_z73 = 3874;
                    x_cuff_right_z73 = 1316;
                    y_cuff_right_z73 = 3877;
                    x_collar_z73 = 0;
                    y_collar_z73 = 4857;

                    id_front_z73 = R.drawable.z73_front_xs;
                    id_back_z73 = R.drawable.z73_back_xs;
                    break;
                case "XS":
                    width_front = 3316;
                    height_front = 3898;
                    width_back = 3316;
                    height_back = 3898;
                    width_sleeve = 2541;
                    height_sleeve = 3520;
                    width_cuff = 1264;
                    height_cuff = 892;
                    width_bottom = 5327;
                    height_bottom = 892;
                    width_collar = 2914;
                    height_collar = 348;

                    width_combine_Z73 = 10429;
                    height_combine_z73 = 7490;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 3444;
                    y_back_z73 = 0;
                    x_sleeve_left_z73 = 2669;
                    y_sleeve_left_z73 = 3970;
                    x_sleeve_right_z73 = 0;
                    y_sleeve_right_z73 = 3970;
                    x_bottom_z73 = 5102;
                    y_bottom_z73 = 6598;
                    x_cuff_left_z73 = 5490;
                    y_cuff_left_z73 = 5549;
                    x_cuff_right_z73 = 6933;
                    y_cuff_right_z73 = 5549;
                    x_collar_z73 = 5490;
                    y_collar_z73 = 5019;

                    id_front_z73 = R.drawable.z73_front_xs;
                    id_back_z73 = R.drawable.z73_back_xs;
                    break;
                case "S":
                    width_front = 3463;
                    height_front = 4015;
                    width_back = 3463;
                    height_back = 4015;
                    width_sleeve = 2672;
                    height_sleeve = 3609;
                    width_cuff = 1323;
                    height_cuff = 892;
                    width_bottom = 5622;
                    height_bottom = 892;
                    width_collar = 3008;
                    height_collar = 348;

                    width_combine_Z73 = 9773;
                    height_combine_z73 = 7716;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 3541;
                    y_back_z73 = 0;
                    x_sleeve_left_z73 = 7003;
                    y_sleeve_left_z73 = 0;
                    x_sleeve_right_z73 = 0;
                    y_sleeve_right_z73 = 4107;
                    x_bottom_z73 = 2673;
                    y_bottom_z73 = 4107;
                    x_cuff_left_z73 = 8450;
                    y_cuff_left_z73 = 3709;
                    x_cuff_right_z73 = 8450;
                    y_cuff_right_z73 = 4702;
                    x_collar_z73 = 2814;
                    y_collar_z73 = 5117;

                    id_front_z73 = R.drawable.z73_front_xs;
                    id_back_z73 = R.drawable.z73_back_xs;
                    break;
                case "M":
                    width_front = 3611;
                    height_front = 4132;
                    width_back = 3611;
                    height_back = 4132;
                    width_sleeve = 2803;
                    height_sleeve = 3697;
                    width_cuff = 1382;
                    height_cuff = 892;
                    width_bottom = 5917;
                    height_bottom = 892;
                    width_collar = 3061;
                    height_collar = 348;

                    width_combine_Z73 =10515;
                    height_combine_z73 = 7964;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 3819;
                    y_back_z73 = 0;
                    x_sleeve_left_z73 = 7381;
                    y_sleeve_left_z73 = 0;
                    x_sleeve_right_z73 = 0;
                    y_sleeve_right_z73 = 4267;
                    x_bottom_z73 = 2914;
                    y_bottom_z73 = 4267;
                    x_cuff_left_z73 = 9133;
                    y_cuff_left_z73 = 3821;
                    x_cuff_right_z73 = 9133;
                    y_cuff_right_z73 = 4869;
                    x_collar_z73 = 2914;
                    y_collar_z73 = 5315;

                    id_front_z73 = R.drawable.z73_front_xl;
                    id_back_z73 = R.drawable.z73_back_xl;
                    break;
                case "L":
                    width_front = 3758;
                    height_front = 4249;
                    width_back = 3758;
                    height_back = 4249;
                    width_sleeve = 2933;
                    height_sleeve = 3786;
                    width_cuff = 1441;
                    height_cuff = 892;
                    width_bottom = 6212;
                    height_bottom = 892;
                    width_collar = 3156;
                    height_collar = 348;

                    width_combine_Z73 =10632;
                    height_combine_z73 = 8132;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 3910;
                    y_back_z73 = 0;
                    x_sleeve_left_z73 = 7667;
                    y_sleeve_left_z73 = 0;
                    x_sleeve_right_z73 = 0;
                    y_sleeve_right_z73 = 4346;
                    x_bottom_z73 = 2850;
                    y_bottom_z73 = 4346;
                    x_cuff_left_z73 = 9191;
                    y_cuff_left_z73 = 3900;
                    x_cuff_right_z73 = 9191;
                    y_cuff_right_z73 = 4907;
                    x_collar_z73 = 3013;
                    y_collar_z73 = 5340;

                    id_front_z73 = R.drawable.z73_front_xl;
                    id_back_z73 = R.drawable.z73_back_xl;
                    break;
                case "XL":
                    width_front = 3905;
                    height_front = 4366;
                    width_back = 3905;
                    height_back = 4366;
                    width_sleeve = 3065;
                    height_sleeve = 3875;
                    width_cuff = 1500;
                    height_cuff = 892;
                    width_bottom = 6508;
                    height_bottom = 892;
                    width_collar = 3250;
                    height_collar = 348;

                    width_combine_Z73 =10710;
                    height_combine_z73 = 8341;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 6805;
                    y_back_z73 = 0;
                    x_sleeve_left_z73 = 3821;
                    y_sleeve_left_z73 = 0;
                    x_sleeve_right_z73 = 0;
                    y_sleeve_right_z73 = 4466;
                    x_bottom_z73 = 2956;
                    y_bottom_z73 = 4490;
                    x_cuff_left_z73 = 3203;
                    y_cuff_left_z73 = 5511;
                    x_cuff_right_z73 = 4845;
                    y_cuff_right_z73 = 5511;
                    x_collar_z73 = 6530;
                    y_collar_z73 = 5609;

                    id_front_z73 = R.drawable.z73_front_xl;
                    id_back_z73 = R.drawable.z73_back_xl;
                    break;
                case "2XL":
                    width_front = 4053;
                    height_front = 4483;
                    width_back = 4053;
                    height_back = 4483;
                    width_sleeve = 3195;
                    height_sleeve = 3963;
                    width_cuff = 1559;
                    height_cuff = 892;
                    width_bottom = 6803;
                    height_bottom = 892;
                    width_collar = 3303;
                    height_collar = 348;

                    width_combine_Z73 =9792;
                    height_combine_z73 = 9534;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 4181;
                    y_back_z73 = 0;
                    x_sleeve_left_z73 = 3282;
                    y_sleeve_left_z73 = 4580;
                    x_sleeve_right_z73 = 0;
                    y_sleeve_right_z73 = 4584;
                    x_bottom_z73 = 0;
                    y_bottom_z73 = 8642;
                    x_cuff_left_z73 = 8233;
                    y_cuff_left_z73 = 0;
                    x_cuff_right_z73 = 8233;
                    y_cuff_right_z73 = 997;
                    x_collar_z73 = 6006;
                    y_collar_z73 = 4635;

                    id_front_z73 = R.drawable.z73_front_xl;
                    id_back_z73 = R.drawable.z73_back_xl;
                    break;
                case "3XL":
                    width_front = 4200;
                    height_front = 4601;
                    width_back = 4200;
                    height_back = 4601;
                    width_sleeve = 3326;
                    height_sleeve = 4052;
                    width_cuff = 1618;
                    height_cuff = 892;
                    width_bottom = 7098;
                    height_bottom = 892;
                    width_collar = 3398;
                    height_collar = 348;

                    width_combine_Z73 =10142;
                    height_combine_z73 = 9750;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 4324;
                    y_back_z73 = 0;
                    x_sleeve_left_z73 = 3423;
                    y_sleeve_left_z73 = 4705;
                    x_sleeve_right_z73 = 0;
                    y_sleeve_right_z73 = 4705;
                    x_bottom_z73 = 0;
                    y_bottom_z73 = 8858;
                    x_cuff_left_z73 = 8524;
                    y_cuff_left_z73 = 0;
                    x_cuff_right_z73 = 8524;
                    y_cuff_right_z73 = 1005;
                    x_collar_z73 = 6117;
                    y_collar_z73 = 4764;

                    id_front_z73 = R.drawable.z73_front_3xl;
                    id_back_z73 = R.drawable.z73_back_3xl;
                    break;
                case "4XL":
                    width_front = 4348;
                    height_front = 4718;
                    width_back = 4348;
                    height_back = 4718;
                    width_sleeve = 3457;
                    height_sleeve = 4141;
                    width_cuff = 1677;
                    height_cuff = 892;
                    width_bottom = 7394;
                    height_bottom = 892;
                    width_collar = 3492;
                    height_collar = 348;

                    width_combine_Z73 =10505;
                    height_combine_z73 = 9957;
                    x_front_z73 = 0;
                    y_front_z73 = 0;
                    x_back_z73 = 4481;
                    y_back_z73 = 0;
                    x_sleeve_left_z73 = 3573;
                    y_sleeve_left_z73 = 4815;
                    x_sleeve_right_z73 = 0;
                    y_sleeve_right_z73 = 4815;
                    x_bottom_z73 = 0;
                    y_bottom_z73 = 9065;
                    x_cuff_left_z73 = 8828;
                    y_cuff_left_z73 = 0;
                    x_cuff_right_z73 = 8828;
                    y_cuff_right_z73 = 991;
                    x_collar_z73 = 6298;
                    y_collar_z73 = 4815;

                    id_front_z73 = R.drawable.z73_front_3xl;
                    id_back_z73 = R.drawable.z73_back_3xl;
                    break;
                default:
                    showDialogSizeWrong(orderItems.get(currentID).order_number);
                    sizeOK = false;
                    break;
            }
        }

        if (orderItems.get(currentID).sku.contains("Z73")) {
            width_front += 8;
            height_front += 8;
            width_back += 8;
            height_back += 8;
            width_sleeve += 8;
            height_sleeve += 8;
            width_cuff += 8;
            height_cuff += 8;
            width_bottom += 8;
            height_bottom += 8;
            width_collar += 8;
            height_collar += 8;

            width_combine_Z73 += 8;
            height_combine_z73 += 8;
        }
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

    String getColor(String color) {
        if (color.equals("White")) {
            return "白灯";
        } else if (color.equals("Green")) {
            return "绿灯";
        } else if (color.equals("Blue")) {
            return "蓝灯";
        } else if (color.equals("Red")) {
            return "红灯";
        } else {
            return "无灯";
        }
    }

    boolean checkContains(String nameContains) {
        for (int i = 0; i < orderItems.get(currentID).imgs.size(); i++) {
            if (orderItems.get(currentID).imgs.get(i).contains(nameContains)) {
                return true;
            }
        }
        return false;
    }

    Bitmap getBitmapWith(String nameContains) {
        for (int i = 0; i < orderItems.get(currentID).imgs.size(); i++) {
            if (orderItems.get(currentID).imgs.get(i).contains(nameContains)) {
                return MainActivity.instance.bitmaps.get(i);
            }
        }
        return null;
    }
}
