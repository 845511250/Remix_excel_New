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
import android.util.Log;
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

public class FragmentD48 extends BaseFragment {
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

    int width_front, width_back, width_sleeve, width_p_front, width_p_back;
    int height_front, height_back, height_sleeve, height_p_front, height_p_back;

    int width_combine, height_combine;
    int x_front_left, x_front_right, x_back, x_sleeve_left, x_sleeve_right, x_p_front_left, x_p_front_right, x_p_back_left, x_p_back_right;
    int y_front_left, y_front_right, y_back, y_sleeve_left, y_sleeve_right, y_p_front_left, y_p_front_right, y_p_back_left, y_p_back_right;
    int id_front_left, id_back, id_sleeve, id_p_front_right, id_p_back_left;

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
        paint.setTextSize(19);
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
                            }
                        }
                        strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                        remixx();
                    }
                }
            }
        }.start();

    }

    void drawTextFrontLeft(Canvas canvas) {
        canvas.drawRect(1315, 6194 - 19, 1315 + 400, 6194, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1315, 6194 - 2, paint);
    }
    void drawTextFrontRight(Canvas canvas) {
        canvas.drawRect(50, 6194 - 19, 50 + 400, 6194, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 50, 6194 - 2, paint);
    }
    void drawTextBack(Canvas canvas) {
        canvas.drawRect(2936, 6217 - 19, 2936 + 400, 6217, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 2936, 6217 - 2, paint);
    }
    void drawTextP_Back(Canvas canvas) {
        canvas.drawRect(747, 5578 - 19, 747 + 400, 5578, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 747, 5578 - 2, paint);
    }
    void drawTextP_front(Canvas canvas) {
        canvas.drawRect(515, 5457 - 19, 515 + 400, 5457, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 515, 5457 - 2, paint);
    }
    void drawTextSleeve(Canvas canvas, String LR) {
        canvas.drawRect(749, 3028 - 19, 749 + 400, 3028, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + LR + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 749, 3028 - 2, paint);
    }


    public void remixx() {
        Bitmap bitmapCombine = Bitmap.createBitmap(width_combine, height_combine, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 6) {
            //front_left
            Bitmap bitmapTemp = Bitmap.createBitmap(1733, 6209, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), -1787, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), id_front_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontLeft(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front_left, y_front_left, null);

            //front_right
            bitmapTemp = Bitmap.createBitmap(1733, 6209, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1733, 6209, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontRight(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front_right, y_front_right, null);

            //back
            bitmapTemp = Bitmap.createBitmap(3387, 6230, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);

            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(x_back, y_back);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //p_front_right
            bitmapTemp = Bitmap.createBitmap(1433, 5476, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_p_front_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextP_front(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_p_front, height_p_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_p_front_right, y_p_front_right, null);

            //p_front_left
            bitmapTemp = Bitmap.createBitmap(1433, 5476, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -1487, 0, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1433, 5476, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextP_front(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_p_front, height_p_front, true);

            matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(x_p_front_left, y_p_front_left);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //p_back_left
            bitmapTemp = Bitmap.createBitmap(1919, 5596, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_p_back_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextP_Back(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_p_back, height_p_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_p_back_left, y_p_back_left, null);

            //p_back_right
            bitmapTemp = Bitmap.createBitmap(1919, 5596, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1960, 0, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1919, 5596, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextP_Back(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_p_back, height_p_back, true);

            matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(x_p_back_right, y_p_back_right);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //sleeve_left
            bitmapTemp = Bitmap.createBitmap(1983, 3044, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_sleeve);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "左袖");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_left, y_sleeve_left, null);

            //sleeve_right
            bitmapTemp = Bitmap.createBitmap(1983, 3044, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "右袖");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_right, y_sleeve_right, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();
            if (num == 1) {
                MainActivity.recycleExcelImages();
            }

        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //front_left
            Bitmap bitmapTemp = Bitmap.createBitmap(1733, 6209, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3825, -98, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), id_front_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontLeft(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front_left, y_front_left, null);

            //front_right
            bitmapTemp = Bitmap.createBitmap(1733, 6209, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2038, -98, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1733, 6209, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontRight(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front_right, y_front_right, null);

            //back
            bitmapTemp = Bitmap.createBitmap(3387, 6230, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -5674, -77, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);

            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(x_back, y_back);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //p_front_right
            bitmapTemp = Bitmap.createBitmap(1433, 5476, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2338, -6427, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_p_front_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextP_front(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_p_front, height_p_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_p_front_right, y_p_front_right, null);

            //p_front_left
            bitmapTemp = Bitmap.createBitmap(1433, 5476, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3825, -6427, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1433, 5476, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextP_front(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_p_front, height_p_front, true);

            matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(x_p_front_left, y_p_front_left);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //p_back_left
            bitmapTemp = Bitmap.createBitmap(1919, 5596, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -5428, -6427, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_p_back_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextP_Back(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_p_back, height_p_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_p_back_left, y_p_back_left, null);

            //p_back_right
            bitmapTemp = Bitmap.createBitmap(1919, 5596, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -7388, -6427, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1919, 5596, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextP_Back(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_p_back, height_p_back, true);

            matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(x_p_back_right, y_p_back_right);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //sleeve_left
            bitmapTemp = Bitmap.createBitmap(1983, 3044, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -9060, -3044, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_sleeve);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "左袖");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_left, y_sleeve_left, null);

            //sleeve_right
            bitmapTemp = Bitmap.createBitmap(1983, 3044, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -57, -665, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "右袖");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_right, y_sleeve_right, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();
            if (num == 1) {
                MainActivity.recycleExcelImages();
            }

        }


        try {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(90);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

            String nameCombine = orderItems.get(currentID).sku + "_" + orderItems.get(currentID).newCode_short + "_" + orderItems.get(currentID).sizeStr + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            Log.e("aaa", pathSave + nameCombine);
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 120);
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
            case "S":
                width_front = 1590;
                height_front = 6067;
                width_back = 3106;
                height_back = 6087;
                width_p_front = 1290;
                height_p_front = 5334;
                width_p_back = 1775;
                height_p_back = 5458;
                width_sleeve = 1697;
                height_sleeve = 2901;

                width_combine = 6206;
                height_combine = 14549;
                x_front_left = 0;
                y_front_left = 0;
                x_front_right = 4439;
                y_front_right = 0;
                x_back = 4568;
                y_back = 6087;
                x_p_front_left = 2698;
                y_p_front_left = 11559;
                x_p_front_right = 0;
                y_p_front_right = 6225;
                x_p_back_left = 2664;
                y_p_back_left = 6163;
                x_p_back_right = 6206;
                y_p_back_right = 11621;
                x_sleeve_left = 1550;
                y_sleeve_left = 11594;
                x_sleeve_right = 3384;
                y_sleeve_right = 11594;

                id_front_left = R.drawable.d48_front_left_m;
                id_back = R.drawable.d48_back_m;
                id_p_front_right = R.drawable.d48_p_front_right_m;
                id_p_back_left = R.drawable.d48_p_back_left_m;
                id_sleeve = R.drawable.d48_sleeve_m;
                break;
            case "M":
                width_front = 1636;
                height_front = 6114;
                width_back = 3198;
                height_back = 6136;
                width_p_front = 1337;
                height_p_front = 5381;
                width_p_back = 1820;
                height_p_back = 5504;
                width_sleeve = 1792;
                height_sleeve = 2949;

                width_combine = 6238;
                height_combine = 14724;
                x_front_left = 0;
                y_front_left = 0;
                x_front_right = 4601;
                y_front_right = 0;
                x_back = 4724;
                y_back = 6136;
                x_p_front_left = 2697;
                y_p_front_left = 11685;
                x_p_front_right = 0;
                y_p_front_right = 6304;
                x_p_back_left = 2622;
                y_p_back_left = 6242;
                x_p_back_right = 6191;
                y_p_back_right = 11746;
                x_sleeve_left = 221;
                y_sleeve_left = 11775;
                x_sleeve_right = 3290;
                y_sleeve_right = 11775;

                id_front_left = R.drawable.d48_front_left_m;
                id_back = R.drawable.d48_back_m;
                id_p_front_right = R.drawable.d48_p_front_right_m;
                id_p_back_left = R.drawable.d48_p_back_left_m;
                id_sleeve = R.drawable.d48_sleeve_m;
                break;
            case "L":
                width_front = 1686;
                height_front = 6160;
                width_back = 3295;
                height_back = 6182;
                width_p_front = 1385;
                height_p_front = 5428;
                width_p_back = 1869;
                height_p_back = 5550;
                width_sleeve = 1887;
                height_sleeve = 2996;

                width_combine = 6475;
                height_combine = 14844;
                x_front_left = 0;
                y_front_left = 0;
                x_front_right = 4698;
                y_front_right = 0;
                x_back = 4836;
                y_back = 6182;
                x_p_front_left = 2800;
                y_p_front_left = 11757;
                x_p_front_right = 0;
                y_p_front_right = 6329;
                x_p_back_left = 2769;
                y_p_back_left = 6268;
                x_p_back_right = 6475;
                y_p_back_right = 11879;
                x_sleeve_left = 244;
                y_sleeve_left = 11848;
                x_sleeve_right = 3449;
                y_sleeve_right = 11848;

                id_front_left = R.drawable.d48_front_left_xl;
                id_back = R.drawable.d48_back_xl;
                id_p_front_right = R.drawable.d48_p_front_right_xl;
                id_p_back_left = R.drawable.d48_p_back_left_xl;
                id_sleeve = R.drawable.d48_sleeve_xl;
                break;
            case "XL":
                width_front = 1734;
                height_front = 6209;
                width_back = 3388;
                height_back = 6230;
                width_p_front = 1432;
                height_p_front = 5476;
                width_p_back = 1914;
                height_p_back = 5596;
                width_sleeve = 1984;
                height_sleeve = 3045;

                width_combine = 6765;
                height_combine = 15027;
                x_front_left = 0;
                y_front_left = 0;
                x_front_right = 4906;
                y_front_right = 0;
                x_back = 5018;
                y_back = 6230;
                x_p_front_left = 2945;
                y_p_front_left = 11839;
                x_p_front_right = 0;
                y_p_front_right = 6363;
                x_p_back_left = 2966;
                y_p_back_left = 6303;
                x_p_back_right = 6765;
                y_p_back_right = 11959;
                x_sleeve_left = 244;
                y_sleeve_left = 11982;
                x_sleeve_right = 3613;
                y_sleeve_right = 11982;

                id_front_left = R.drawable.d48_front_left_xl;
                id_back = R.drawable.d48_back_xl;
                id_p_front_right = R.drawable.d48_p_front_right_xl;
                id_p_back_left = R.drawable.d48_p_back_left_xl;
                id_sleeve = R.drawable.d48_sleeve_xl;
                break;
            case "2XL":
                width_front = 1781;
                height_front = 6254;
                width_back = 3484;
                height_back = 6277;
                width_p_front = 1482;
                height_p_front = 5523;
                width_p_back = 1962;
                height_p_back = 5643;
                width_sleeve = 2079;
                height_sleeve = 3091;

                width_combine = 6761;
                height_combine = 15149;
                x_front_left = 0;
                y_front_left = 0;
                x_front_right = 4981;
                y_front_right = 0;
                x_back = 5140;
                y_back = 6277;
                x_p_front_left = 3005;
                y_p_front_left = 11972;
                x_p_front_right = 0;
                y_p_front_right = 6449;
                x_p_back_left = 2928;
                y_p_back_left = 6389;
                x_p_back_right = 6760;
                y_p_back_right = 12032;
                x_sleeve_left = 262;
                y_sleeve_left = 12058;
                x_sleeve_right = 3594;
                y_sleeve_right = 12058;

                id_front_left = R.drawable.d48_front_left_3xl;
                id_back = R.drawable.d48_back_3xl;
                id_p_front_right = R.drawable.d48_p_front_right_3xl;
                id_p_back_left = R.drawable.d48_p_back_left_3xl;
                id_sleeve = R.drawable.d48_sleeve_3xl;
                break;
            case "3XL":
                width_front = 1831;
                height_front = 6302;
                width_back = 3578;
                height_back = 6324;
                width_p_front = 1530;
                height_p_front = 5569;
                width_p_back = 2010;
                height_p_back = 5690;
                width_sleeve = 2173;
                height_sleeve = 3139;

                width_combine = 6978;
                height_combine = 15335;
                x_front_left = 0;
                y_front_left = 0;
                x_front_right = 5096;
                y_front_right = 0;
                x_back = 5269;
                y_back = 6324;
                x_p_front_left = 3105;
                y_p_front_left = 12054;
                x_p_front_right = 0;
                y_p_front_right = 6485;
                x_p_back_left = 3049;
                y_p_back_left = 6424;
                x_p_back_right = 6978;
                y_p_back_right = 12175;
                x_sleeve_left = 204;
                y_sleeve_left = 12175;
                x_sleeve_right = 3644;
                y_sleeve_right = 12175;

                id_front_left = R.drawable.d48_front_left_3xl;
                id_back = R.drawable.d48_back_3xl;
                id_p_front_right = R.drawable.d48_p_front_right_3xl;
                id_p_back_left = R.drawable.d48_p_back_left_3xl;
                id_sleeve = R.drawable.d48_sleeve_3xl;
                break;
            case "4XL":
                width_front = 1879;
                height_front = 6350;
                width_back = 3672;
                height_back = 6371;
                width_p_front = 1578;
                height_p_front = 5618;
                width_p_back = 2055;
                height_p_back = 5738;
                width_sleeve = 2268;
                height_sleeve = 3186;

                width_combine = 7054;
                height_combine = 15409;
                x_front_left = 0;
                y_front_left = 0;
                x_front_right = 5154;
                y_front_right = 0;
                x_back = 5345;
                y_back = 6371;
                x_p_front_left = 3138;
                y_p_front_left = 12141;
                x_p_front_right = 0;
                y_p_front_right = 6523;
                x_p_back_left = 3059;
                y_p_back_left = 6463;
                x_p_back_right = 7054;
                y_p_back_right = 12201;
                x_sleeve_left = 194;
                y_sleeve_left = 12223;
                x_sleeve_right = 3736;
                y_sleeve_right = 12223;

                id_front_left = R.drawable.d48_front_left_3xl;
                id_back = R.drawable.d48_back_3xl;
                id_p_front_right = R.drawable.d48_p_front_right_3xl;
                id_p_back_left = R.drawable.d48_p_back_left_3xl;
                id_sleeve = R.drawable.d48_sleeve_3xl;
                break;

            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
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
