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

public class FragmentX3 extends BaseFragment {
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

    int width_front, width_back, width_sleeve, width_front_down, width_back_down;
    int height_front, height_back, height_sleeve, height_front_down, height_back_down;

    int id_front, id_back, id_sleeve_left, id_front_down, id_back_down_left;

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

    void drawTextFront(Canvas canvas) {
        canvas.drawRect(1585, 2400 - 19, 1585 + 300, 2400, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1585, 2400 - 2, paint);
    }
    void drawTextBack(Canvas canvas) {
        canvas.save();
        canvas.rotate(-1.1f, 1637, 2507);
        canvas.drawRect(1637, 2507 - 19, 1637 + 400, 2507, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1637, 2507 - 2, paint);
        canvas.restore();
    }
    void drawTextFrontDown(Canvas canvas) {
        canvas.save();
        canvas.rotate(-2.6f, 3618, 4137);
        canvas.drawRect(3618, 4137 - 19, 3618 + 300, 4137, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 3618, 4137 - 2, paint);
        canvas.restore();
    }
    void drawTextBackLeft(Canvas canvas) {
        canvas.save();
        canvas.rotate(2.4f, 3286, 4128);
        canvas.drawRect(3286, 4128 - 19, 3286 + 300, 4128, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "左后片" + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number, 3286, 4128 - 2, paint);
        canvas.restore();
    }
    void drawTextBackRight(Canvas canvas) {
        canvas.save();
        canvas.rotate(-2.4f, 17, 4140);
        canvas.drawRect(17, 4140 - 19, 17 + 300, 4140, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "右后片" + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number, 17, 4140 - 2, paint);
        canvas.restore();
    }
    void drawTextSleeveLeft(Canvas canvas) {
        canvas.save();
        canvas.rotate(2.7f, 1831, 1240);
        canvas.drawRect(1831, 1240 - 19, 1831 + 350, 1240, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "左袖" + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number, 1831, 1240 - 2, paint);
        canvas.restore();
    }
    void drawTextSleeveRight(Canvas canvas) {
        canvas.save();
        canvas.rotate(2.7f, 1835, 1241);
        canvas.drawRect(1835, 1241 - 19, 1835 + 350, 1241, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "右袖" + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number, 1835, 1241 - 2, paint);
        canvas.restore();
    }


    public void remixx() {
        Bitmap bitmapCombine = null;

        if (orderItems.get(currentID).imgs.size() == 6) {
            if (orderItems.get(currentID).sizeStr.equals("2XL") || orderItems.get(currentID).sizeStr.equals("3XL") || orderItems.get(currentID).sizeStr.equals("4XL")) {//激光电雕152内
                int margin = 80;
                bitmapCombine = Bitmap.createBitmap(Math.max(width_back_down * 2 + margin, (int) (width_front_down * 0.696) + width_sleeve), height_back + margin + (int) (height_sleeve * 0.368) + height_front_down + height_back_down, Bitmap.Config.ARGB_8888);
                Canvas canvasCombine = new Canvas(bitmapCombine);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasCombine.drawColor(0xffffffff);

                //front_down
                Bitmap bitmapTemp = Bitmap.createBitmap(7110, 4152, Bitmap.Config.ARGB_8888);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), id_front_down);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFrontDown(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front_down, height_front_down, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + margin + (int) (height_sleeve * 0.368), null);

                //back left_down
                bitmapTemp = Bitmap.createBitmap(3605, 4153, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getResources(), id_back_down_left);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBackLeft(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_down, height_back_down, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_back + margin + (int) (height_sleeve * 0.368) + height_front_down, null);

                //back right_down
                bitmapTemp = Bitmap.createBitmap(3605, 4153, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3605, 0, null);
                bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -3605, 4153, true);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBackRight(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_down, height_back_down, true);
                canvasCombine.drawBitmap(bitmapTemp, width_back_down + margin, height_back + margin + (int) (height_sleeve * 0.368) + height_front_down, null);

                //front
                bitmapTemp = Bitmap.createBitmap(3124, 2413, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getResources(), id_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

                //back
                bitmapTemp = Bitmap.createBitmap(3136, 2519, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getResources(), id_back);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBack(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

                //sleeveLeft
                bitmapTemp = Bitmap.createBitmap(2253, 1271, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getResources(), id_sleeve_left);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextSleeveLeft(canvasTemp);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                Matrix matrix = new Matrix();
                matrix.postRotate(180);
                matrix.postTranslate(width_sleeve, height_sleeve + height_front + margin);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                //sleeveRight
                bitmapTemp = Bitmap.createBitmap(2253, 1271, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
                bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -2253, 1271, true);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextSleeveRight(canvasTemp);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                matrix = new Matrix();
                matrix.postRotate(180);
                matrix.postTranslate(width_sleeve + (int) (width_front_down * 0.696), height_sleeve + height_back + margin);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                bitmapTemp.recycle();
                bitmapDB.recycle();
            } else {//裁片印108内
                int margin = 60;
                bitmapCombine = Bitmap.createBitmap(width_front / 2 + width_front_down + width_back_down * 2 + margin * 4, height_sleeve + 150 + height_back_down, Bitmap.Config.ARGB_8888);
                Canvas canvasCombine = new Canvas(bitmapCombine);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasCombine.drawColor(0xffffffff);

                //front_down
                Bitmap bitmapTemp = Bitmap.createBitmap(7110, 4152, Bitmap.Config.ARGB_8888);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), id_front_down);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFrontDown(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front_down, height_front_down, true);
                canvasCombine.drawBitmap(bitmapTemp, width_front / 2, height_sleeve + 150, null);

                //back left_down
                bitmapTemp = Bitmap.createBitmap(3605, 4153, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getResources(), id_back_down_left);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBackLeft(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_down, height_back_down, true);
                canvasCombine.drawBitmap(bitmapTemp, width_front / 2 + width_front_down + margin * 2, height_sleeve + 150, null);

                //back right_down
                bitmapTemp = Bitmap.createBitmap(3605, 4153, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3605, 0, null);
                bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -3605, 4153, true);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBackRight(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_down, height_back_down, true);
                canvasCombine.drawBitmap(bitmapTemp, width_front / 2 + width_front_down + width_back_down + margin * 4, height_sleeve + 150, null);

                //front
                bitmapTemp = Bitmap.createBitmap(3124, 2413, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getResources(), id_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

                //back
                bitmapTemp = Bitmap.createBitmap(3136, 2519, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getResources(), id_back);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBack(canvasTemp);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, width_front / 2 + width_front_down + margin - width_back / 2, 0, null);

                //sleeveLeft
                bitmapTemp = Bitmap.createBitmap(2253, 1271, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getResources(), id_sleeve_left);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextSleeveLeft(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

                //sleeveRight
                bitmapTemp = Bitmap.createBitmap(2253, 1271, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
                bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -2253, 1271, true);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextSleeveRight(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, width_front / 2 + width_front_down + margin + width_back / 2 + margin * 2, 0, null);

                bitmapTemp.recycle();
                bitmapDB.recycle();

            }

            if (num == 1) {
                MainActivity.recycleExcelImages();
            }

        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 14669) {
            //激光电雕152内
            int margin = 80;
            bitmapCombine = Bitmap.createBitmap(Math.max(width_back_down * 2 + margin, (int) (width_front_down * 0.696) + width_sleeve), height_back + margin + (int) (height_sleeve * 0.368) + height_front_down + height_back_down, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            //front_down
            Bitmap bitmapTemp = Bitmap.createBitmap(7110, 4152, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -259, -2519, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), id_front_down);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontDown(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front_down, height_front_down, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + margin + (int) (height_sleeve * 0.368), null);

            //back left_down
            bitmapTemp = Bitmap.createBitmap(3605, 4153, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -7455, -2517, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_back_down_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackLeft(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_down, height_back_down, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + margin + (int) (height_sleeve * 0.368) + height_front_down, null);

            //back right_down
            bitmapTemp = Bitmap.createBitmap(3605, 4153, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -11062, -2517, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -3605, 4153, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackRight(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_down, height_back_down, true);
            canvasCombine.drawBitmap(bitmapTemp, width_back_down + margin, height_back + margin + (int) (height_sleeve * 0.368) + height_front_down, null);

            //front
            bitmapTemp = Bitmap.createBitmap(3124, 2413, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2253, -106, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //back
            bitmapTemp = Bitmap.createBitmap(3136, 2519, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -9494, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //sleeveLeft
            bitmapTemp = Bitmap.createBitmap(2253, 1271, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -5377, -656, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeveLeft(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(width_sleeve, height_sleeve + height_front + margin);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //sleeveRight
            bitmapTemp = Bitmap.createBitmap(2253, 1271, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, -656, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -2253, 1271, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeveRight(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(width_sleeve + (int) (width_front_down * 0.696), height_sleeve + height_back + margin);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

            if (num == 1) {
                MainActivity.recycleExcelImages();
            }
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
            Log.e("aaa", pathSave + nameCombine);
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
            case "S":
                width_front = 2565;
                height_front = 2245;
                width_back = 2575;
                height_back = 2349;
                width_front_down = 6275;
                height_front_down = 3877;
                width_back_down = 3190;
                height_back_down = 3879;
                width_sleeve = 1915;
                height_sleeve = 1161;

                id_front = R.drawable.x3_front_m;
                id_back = R.drawable.x3_back_m;
                id_front_down = R.drawable.x2_front_down_m;
                id_back_down_left = R.drawable.x2_back_downleft_m;
                id_sleeve_left = R.drawable.x2_sleeve_left_m;
                break;
            case "M":
                width_front = 2750;
                height_front = 2300;
                width_back = 2760;
                height_back = 2406;
                width_front_down = 6554;
                height_front_down = 3969;
                width_back_down = 3327;
                height_back_down = 3969;
                width_sleeve = 2028;
                height_sleeve = 1197;

                id_front = R.drawable.x3_front_m;
                id_back = R.drawable.x3_back_m;
                id_front_down = R.drawable.x2_front_down_m;
                id_back_down_left = R.drawable.x2_back_downleft_m;
                id_sleeve_left = R.drawable.x2_sleeve_left_m;
                break;
            case "L":
                width_front = 2938;
                height_front = 2356;
                width_back = 2948;
                height_back = 2461;
                width_front_down = 6832;
                height_front_down = 4059;
                width_back_down = 3467;
                height_back_down = 4061;
                width_sleeve = 2141;
                height_sleeve = 1237;

                id_front = R.drawable.x3_front_xl;
                id_back = R.drawable.x3_back_xl;
                id_front_down = R.drawable.x2_front_down_xl;
                id_back_down_left = R.drawable.x2_back_downleft_xl;
                id_sleeve_left = R.drawable.x2_sleeve_left_xl;
                break;
            case "XL":
                width_front = 3124;
                height_front = 2412;
                width_back = 3135;
                height_back = 2520;
                width_front_down = 7109;
                height_front_down = 4152;
                width_back_down = 3605;
                height_back_down = 4153;
                width_sleeve = 2252;
                height_sleeve = 1273;

                id_front = R.drawable.x3_front_xl;
                id_back = R.drawable.x3_back_xl;
                id_front_down = R.drawable.x2_front_down_xl;
                id_back_down_left = R.drawable.x2_back_downleft_xl;
                id_sleeve_left = R.drawable.x2_sleeve_left_xl;
                break;
            case "2XL":
                width_front = 3310;
                height_front = 2448;
                width_back = 3321;
                height_back = 2556;
                width_front_down = 7386;
                height_front_down = 4224;
                width_back_down = 3745;
                height_back_down = 4224;
                width_sleeve = 2365;
                height_sleeve = 1308;

                id_front = R.drawable.x3_front_3xl;
                id_back = R.drawable.x3_back_3xl;
                id_front_down = R.drawable.x2_front_down_3xl;
                id_back_down_left = R.drawable.x2_back_downleft_3xl;
                id_sleeve_left = R.drawable.x2_sleeve_left_3xl;
                break;
            case "3XL":
                width_front = 3496;
                height_front = 2482;
                width_back = 3503;
                height_back = 2589;
                width_front_down = 7659;
                height_front_down = 4291;
                width_back_down = 3877;
                height_back_down = 4293;
                width_sleeve = 2470;
                height_sleeve = 1342;

                id_front = R.drawable.x3_front_3xl;
                id_back = R.drawable.x3_back_3xl;
                id_front_down = R.drawable.x2_front_down_3xl;
                id_back_down_left = R.drawable.x2_back_downleft_3xl;
                id_sleeve_left = R.drawable.x2_sleeve_left_3xl;
                break;
            case "4XL":
                width_front = 3682;
                height_front = 2520;
                width_back = 3691;
                height_back = 2627;
                width_front_down = 7937;
                height_front_down = 4364;
                width_back_down = 4016;
                height_back_down = 4366;
                width_sleeve = 2583;
                height_sleeve = 1379;

                id_front = R.drawable.x3_front_3xl;
                id_back = R.drawable.x3_back_3xl;
                id_front_down = R.drawable.x2_front_down_3xl;
                id_back_down_left = R.drawable.x2_back_downleft_3xl;
                id_sleeve_left = R.drawable.x2_sleeve_left_3xl;
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
