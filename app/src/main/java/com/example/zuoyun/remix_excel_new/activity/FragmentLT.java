package com.example.zuoyun.remix_excel_new.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

public class FragmentLT extends BaseFragment {
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

    int width_front, width_back, width_sleeve, width_collar;
    int height_front, height_back, height_sleeve, height_collar;

    int width_combine, height_combine;
    int x_front, x_back, x_sleeve_left, x_sleeve_right, x_collar;
    int y_front, y_back, y_sleeve_left, y_sleeve_right, y_collar;
    int id_front, id_back, id_sleeve_left, id_sleeve_right, id_collar;

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
        paint.setTextSize(25);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);


        time = MainActivity.instance.orderDate_Print;

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                    bt_remix.setClickable(false);
                } else if (message == MainActivity.LOADED_IMGS) {
                    if (!MainActivity.instance.cb_fastmode.isChecked())
                        iv_pillow.setImageBitmap(MainActivity.instance.bitmaps.get(0));
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
        canvas.drawRect(100, 4843 - 25, 100 + 600, 4843, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 100, 4843 - 3, paint);
    }

    void drawTextBack(Canvas canvas) {
        canvas.drawRect(100, 4965 - 25, 100 + 600, 4965, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 100, 4965 - 3, paint);
    }

    void drawTextSleeve(Canvas canvas, String LR) {
        canvas.drawRect(500, 1669 - 25, 500 + 400, 1669, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + LR + " " + time + " " + orderItems.get(currentID).order_number, 500, 1669 - 3, paint);
    }

    public void remixx() {
        if (orderItems.get(currentID).sizeStr.equals("XS") || orderItems.get(currentID).sizeStr.equals("S") || orderItems.get(currentID).sizeStr.equals("M")) {
            id_back = R.drawable.lt_back_s;
            id_front = R.drawable.lt_front_s;
            id_sleeve_left = R.drawable.lt_sleeve_left_s;
            id_sleeve_right = R.drawable.lt_sleeve_right_s;
            id_collar = R.drawable.lt_collar_s;
        } else if (orderItems.get(currentID).sizeStr.equals("L") || orderItems.get(currentID).sizeStr.equals("XL") || orderItems.get(currentID).sizeStr.equals("2XL")) {
            id_back = R.drawable.lt_back_xl;
            id_front = R.drawable.lt_front_xl;
            id_sleeve_left = R.drawable.lt_sleeve_left_xl;
            id_sleeve_right = R.drawable.lt_sleeve_right_xl;
            id_collar = R.drawable.lt_collar_s;
        } else if (orderItems.get(currentID).sizeStr.equals("3XL") || orderItems.get(currentID).sizeStr.equals("4XL") || orderItems.get(currentID).sizeStr.equals("5XL")) {
            id_back = R.drawable.lt_back_4xl;
            id_front = R.drawable.lt_front_4xl;
            id_sleeve_left = R.drawable.lt_sleeve_left_4xl;
            id_sleeve_right = R.drawable.lt_sleeve_right_4xl;
            id_collar = R.drawable.lt_collar_s;
        }

        Bitmap bitmapCombine = Bitmap.createBitmap(width_combine, height_combine, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 1) {
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(3769, 4856, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -60, -367, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front , y_front, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(3770, 4980, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3869, -51, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back, y_back, null);

            //领口
            bitmapTemp = Bitmap.createBitmap(3336, 274, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -277, -51, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);
            canvasCombine.drawBitmap(bitmapTemp, x_collar, y_collar, null);

            //左袖子
            bitmapTemp = Bitmap.createBitmap(3029, 1680, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3243, -5268, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "左");

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);

            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_left, y_sleeve_left, null);

            //右袖子
            bitmapTemp = Bitmap.createBitmap(3029, 1680, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -60, -5268, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "右");

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_right, y_sleeve_right, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 5) {
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(3769, 4856, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front , y_front, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(3770, 4980, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back, y_back, null);

            //领口
            bitmapTemp = Bitmap.createBitmap(3336, 274, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_collar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);
            canvasCombine.drawBitmap(bitmapTemp, x_collar, y_collar, null);

            //左袖子
            bitmapTemp = Bitmap.createBitmap(3029, 1680, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "左");

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_left, y_sleeve_left, null);

            //右袖子
            bitmapTemp = Bitmap.createBitmap(3029, 1680, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "右");

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_right, y_sleeve_right, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        }

//        int margin = 200;
//        Matrix matrix = new Matrix();
//        Bitmap bitmapCombine = Bitmap.createBitmap(width_front + width_back + width_sleeve + margin * 2, Math.max(height_back, height_front + margin + height_collar), Bitmap.Config.ARGB_8888);
//        Canvas canvasCombine = new Canvas(bitmapCombine);
//        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//        canvasCombine.drawColor(0xffffffff);
//
//        if (orderItems.get(currentID).imgs.size() == 1) {
//            //前
//            Bitmap bitmapTemp = Bitmap.createBitmap(3769, 4856, Bitmap.Config.ARGB_8888);
//            Canvas canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -60, -367, null);
//            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lt_front);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextLeft(canvasTemp);
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
//            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
//
//            //后面
//            bitmapTemp = Bitmap.createBitmap(3770, 4980, Bitmap.Config.ARGB_8888);
//            canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3869, -51, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lt_back);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextRight(canvasTemp);
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
//            matrix.postTranslate(width_front + margin, 0);
//            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
//
//            //领口
//            bitmapTemp = Bitmap.createBitmap(3336, 274, Bitmap.Config.ARGB_8888);
//            canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -277, -51, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lt_collar);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);
//            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);
//
//            //左袖子
//            bitmapTemp = Bitmap.createBitmap(3029, 1680, Bitmap.Config.ARGB_8888);
//            canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3243, -5268, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lt_sleeve_left);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextSleeve(canvasTemp, "左");
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
//            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, 0, null);
//
//            //右袖子
//            bitmapTemp = Bitmap.createBitmap(3029, 1680, Bitmap.Config.ARGB_8888);
//            canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -60, -5268, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lt_sleeve_right);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            bitmapDB.recycle();
//            drawTextSleeve(canvasTemp, "右");
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
//            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_sleeve + margin, null);
//            bitmapTemp.recycle();
//        } else if (orderItems.get(currentID).imgs.size() == 5) {
//            //前
//            Bitmap bitmapTemp = Bitmap.createBitmap(3769, 4856, Bitmap.Config.ARGB_8888);
//            Canvas canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
//            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lt_front);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextLeft(canvasTemp);
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
//            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
//
//            //后面
//            bitmapTemp = Bitmap.createBitmap(3770, 4980, Bitmap.Config.ARGB_8888);
//            canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lt_back);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextRight(canvasTemp);
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
//            matrix.postTranslate(width_front + margin, 0);
//            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
//
//            //领口
//            bitmapTemp = Bitmap.createBitmap(3336, 274, Bitmap.Config.ARGB_8888);
//            canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lt_collar);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);
//            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);
//
//            //左袖子
//            bitmapTemp = Bitmap.createBitmap(3029, 1680, Bitmap.Config.ARGB_8888);
//            canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lt_sleeve_left);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextSleeve(canvasTemp, "左");
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
//            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, 0, null);
//
//            //右袖子
//            bitmapTemp = Bitmap.createBitmap(3029, 1680, Bitmap.Config.ARGB_8888);
//            canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lt_sleeve_right);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            bitmapDB.recycle();
//            drawTextSleeve(canvasTemp, "右");
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
//            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_sleeve + margin, null);
//            bitmapTemp.recycle();
//        }


        try {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(90);
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
        switch (size) {
            case "XS":
                width_front = 2766;
                height_front = 4237;
                width_back = 2766;
                height_back = 4358;
                width_sleeve = 2460;
                height_sleeve = 1443;
                width_collar = 2670;
                height_collar = 274;

                width_combine = 8284;
                height_combine = 4358;

                x_front = 0;
                y_front = 0;
                x_back = 2911;
                y_back = 0;
                x_sleeve_left = 5821;
                y_sleeve_left = 1345;
                x_sleeve_right = 5821;
                y_sleeve_right = 2879;
                x_collar = 5614;
                y_collar = 810;
                break;
            case "S":
                width_front = 3002;
                height_front = 4384;
                width_back = 3002;
                height_back = 4506;
                width_sleeve = 2602;
                height_sleeve = 1502;
                width_collar = 2826;
                height_collar = 274;

                width_combine = 8830;
                height_combine = 4506;

                x_front = 0;
                y_front = 0;
                x_back = 3101;
                y_back = 0;
                x_sleeve_left = 6211;
                y_sleeve_left = 1332;
                x_sleeve_right = 6211;
                y_sleeve_right = 2961;
                x_collar = 6004;
                y_collar = 883;
                break;
            case "M":
                width_front = 3238;
                height_front = 4531;
                width_back = 3238;
                height_back = 4654;
                width_sleeve = 2745;
                height_sleeve = 1562;
                width_collar = 2982;
                height_collar = 274;

                width_combine = 9474;
                height_combine = 4654;

                x_front = 0;
                y_front = 0;
                x_back = 3347;
                y_back = 0;
                x_sleeve_left = 6705;
                y_sleeve_left = 1319;
                x_sleeve_right = 6693;
                y_sleeve_right = 3013;
                x_collar = 6492;
                y_collar = 925;
                break;
            case "L":
                width_front = 3474;
                height_front = 4679;
                width_back = 3474;
                height_back = 4802;
                width_sleeve = 2887;
                height_sleeve = 1621;
                width_collar = 3139;
                height_collar = 274;

                width_combine = 10039;
                height_combine = 4802;

                x_front = 0;
                y_front = 0;
                x_back = 6553;
                y_back = 0;
                x_sleeve_left = 3570;
                y_sleeve_left = 1231;
                x_sleeve_right = 3570;
                y_sleeve_right = 2951;
                x_collar = 3427;
                y_collar = 810;
                break;
            case "XL":
                width_front = 3769;
                height_front = 4856;
                width_back = 3770;
                height_back = 4980;
                width_sleeve = 3029;
                height_sleeve = 1680;
                width_collar = 3336;
                height_collar = 274;

                width_combine = 8649;
                height_combine = 6744;

                x_front = 0;
                y_front = 0;
                x_back = 3901;
                y_back = 0;
                x_sleeve_left = 0;
                y_sleeve_left = 5064;
                x_sleeve_right = 5620;
                y_sleeve_right = 5064;
                x_collar = 2640;
                y_collar = 5076;
                break;
            case "2XL":
                width_front = 4065;
                height_front = 5033;
                width_back = 4065;
                height_back = 5157;
                width_sleeve = 3171;
                height_sleeve = 1739;
                width_collar = 3534;
                height_collar = 274;

                width_combine = 9277;
                height_combine = 6992;

                x_front = 0;
                y_front = 0;
                x_back = 4264;
                y_back = 0;
                x_sleeve_left = 0;
                y_sleeve_left = 5181;
                x_sleeve_right = 6106;
                y_sleeve_right = 5253;
                x_collar = 2919;
                y_collar = 5253;
                break;
            case "3XL":
                width_front = 4360;
                height_front = 5210;
                width_back = 4360;
                height_back = 5334;
                width_sleeve = 3313;
                height_sleeve = 1798;
                width_collar = 3731;
                height_collar = 274;

                width_combine = 8905;
                height_combine = 7445;

                x_front = 0;
                y_front = 0;
                x_back = 4545;
                y_back = 0;
                x_sleeve_left = 0;
                y_sleeve_left = 5674;
                x_sleeve_right = 5591;
                y_sleeve_right = 5647;
                x_collar = 2554;
                y_collar = 5445;
                break;
            case "4XL":
                width_front = 4655;
                height_front = 5387;
                width_back = 4655;
                height_back = 5512;
                width_sleeve = 3456;
                height_sleeve = 1857;
                width_collar = 3929;
                height_collar = 274;

                width_combine = 10039;
                height_combine = 7491;

                x_front = 0;
                y_front = 0;
                x_back = 5114;
                y_back = 0;
                x_sleeve_left = 0;
                y_sleeve_left = 5512;
                x_sleeve_right = 6500;
                y_sleeve_right = 5634;
                x_collar = 3092;
                y_collar = 5634;
                break;
            case "5XL":
                width_front = 4951;
                height_front = 5564;
                width_back = 4951;
                height_back = 5689;
                width_sleeve = 3598;
                height_sleeve = 1916;
                width_collar = 4126;
                height_collar = 274;

                width_combine = 10039;
                height_combine = 7705;

                x_front = 0;
                y_front = 0;
                x_back = 5088;
                y_back = 0;
                x_sleeve_left = 0;
                y_sleeve_left = 5789;
                x_sleeve_right = 6409;
                y_sleeve_right = 5789;
                x_collar = 2920;
                y_collar = 5785;
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }

        width_combine += 20;
        height_combine += 20;
        width_front += 20;
        height_front += 20;
        width_back += 20;
        height_back += 20;
        width_sleeve += 20;
        height_sleeve += 20;
        width_collar += 20;
        height_collar += 20;
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
