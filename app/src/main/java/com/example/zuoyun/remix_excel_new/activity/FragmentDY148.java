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

public class FragmentDY148 extends BaseFragment {
    Context context;
    //    String sdCardPath = "/mnt/asec/share";
    String sdCardPath = "/storage/emulated/0/Pictures";
    ArrayList<OrderItem> orderItems;
    int currentID;
    String childPath;

    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.iv_right)
    ImageView iv_right;
    @BindView(R.id.bt_remix)
    Button bt_remix;

    Paint paint, paintRed, rectPaint, paintBlue, paintRectBlack;
    String time;

    int width_side, height_side, width_front, height_front, width_tongue, height_tongue;

    int width_combine, height_combine;
    int x_rr, x_front_right, x_rl, x_tongue_right, x_lr, x_front_left, x_ll, x_tongue_left;
    int y_rr, y_front_right, y_rl, y_tongue_right, y_lr, y_front_left, y_ll, y_tongue_left;

    int num;
    String strPlus = "";
    int intPlus = 1;

    @Override
    public int getLayout() {
        return R.layout.fragment_dff;
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

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(25);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(26);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(26);
        paintBlue.setAntiAlias(true);

        paintRectBlack = new Paint();
        paintRectBlack.setColor(0xff000000);
        paintRectBlack.setStyle(Paint.Style.FILL);

        time = MainActivity.instance.orderDate_Print;

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_left.setImageDrawable(null);
                    iv_right.setImageDrawable(null);
                    Log.e("fragment2", "message0");
                } else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
                    if (!MainActivity.instance.cb_fastmode.isChecked())
                        iv_right.setImageBitmap(MainActivity.instance.bitmaps.get(0));
                    checkremix();
                } else if (message == 3) {
                    bt_remix.setClickable(false);
                } else if (message == MainActivity.LOADED_IMGS) {
                    checkremix();
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


    void drawTextFrontL(Canvas canvas) {
        canvas.save();
        canvas.rotate(74.8f, 15, 73);
        canvas.drawRect(15, 33, 515, 73, rectPaint);
        canvas.drawText("  " + orderItems.get(currentID).newCode_short, 20, 70, paintRed);
        canvas.restore();
        canvas.save();
        canvas.rotate(-75.2f, 1331, 631);
        canvas.drawRect(1331, 596, 1951, 631, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).sku + orderItems.get(currentID).size + "码左 " + orderItems.get(currentID).order_number, 1331, 627, paint);
        canvas.restore();
    }

    void drawTextFrontR(Canvas canvas) {
        canvas.save();
        canvas.rotate(74.8f, 15, 73);
        canvas.drawRect(15, 33, 515, 73, rectPaint);
        canvas.drawText("      " + orderItems.get(currentID).newCode, 20, 70, paintRed);
        canvas.restore();
        canvas.save();
        canvas.rotate(-75.2f, 1331, 631);
        canvas.drawRect(1331, 596, 1951, 631, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).sku + orderItems.get(currentID).size + "码右 " + orderItems.get(currentID).order_number, 1331, 627, paint);
        canvas.restore();
    }

    void drawTextTongueL(Canvas canvasPart4L) {
        canvasPart4L.save();
        canvasPart4L.rotate(-10.7f, 144, 1080);
        canvasPart4L.drawRect(144, 1054, 384, 1080, rectPaint);
        canvasPart4L.drawText(orderItems.get(currentID).order_number, 144, 1080 - 3, paint);
        canvasPart4L.restore();
        canvasPart4L.save();
        canvasPart4L.rotate(10, 366, 1045);
        canvasPart4L.drawRect(376, 1019, 566, 1045, rectPaint);
        canvasPart4L.drawText(" 左" + orderItems.get(currentID).size + "码", 376, 1045 - 3, paint);
        canvasPart4L.restore();
    }

    void drawTextTongueR(Canvas canvasPart4R) {
        canvasPart4R.save();
        canvasPart4R.rotate(-10.7f, 144, 1080);
        canvasPart4R.drawRect(144, 1054, 384, 1080, rectPaint);
        canvasPart4R.drawText(orderItems.get(currentID).order_number, 144, 1080 - 3, paint);
        canvasPart4R.restore();
        canvasPart4R.save();
        canvasPart4R.rotate(10, 366, 1045);
        canvasPart4R.drawRect(376, 1019, 566, 1045, rectPaint);
        canvasPart4R.drawText(" 右" + orderItems.get(currentID).size + "码", 376, 1045 - 3, paint);
        canvasPart4R.restore();
    }

    void drawTextSideR(Canvas canvas, int degree, int left, int bottom, String LR) {
        canvas.save();
        canvas.rotate(degree, (float) left, (float) bottom);
        canvas.drawRect(left, bottom - 32, left + 560, bottom, rectPaint);
        canvas.drawText(orderItems.get(currentID).newCode, left + 20, bottom - 2, paintRed);
        canvas.drawText(time + " " + LR + orderItems.get(currentID).size + "码" + orderItems.get(currentID).sku, left + 250, bottom - 2, paint);

        canvas.drawRect(left + 280, bottom + 1, left + 560, bottom + 33, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number, left + 280, bottom + 31, paintBlue);

        canvas.restore();
    }

    void drawTextSideL(Canvas canvas, int degree, int left, int bottom, String LR) {
        canvas.save();
        canvas.rotate(degree, (float) left, (float) bottom);
        canvas.drawRect(left, bottom - 32, left + 560, bottom, rectPaint);
        canvas.drawText(LR + orderItems.get(currentID).size + "码" + orderItems.get(currentID).sku + " " + time, left + 20, bottom - 2, paint);
        canvas.drawText(orderItems.get(currentID).newCode + "", left + 350, bottom - 2, paintRed);

        canvas.drawRect(left, bottom + 1, left + 270, bottom + 33, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number, left + 20, bottom + 31, paintBlue);

        canvas.restore();
    }


    public void remixx() {
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(width_combine, height_combine, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 8) {//4u2
            //sideLR
            Bitmap bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideR(canvasTemp, 14, 10, 1030, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_lr, y_lr, null);

            //sideRR
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(6), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideR(canvasTemp, 14, 10, 1030, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rr, y_rr, null);

            //frontL
            bitmapTemp = Bitmap.createBitmap(1499, 1221, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front_left, y_front_left, null);

            //frontR
            bitmapTemp = Bitmap.createBitmap(1499, 1221, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front_right, y_front_right, null);

            //sideLL
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideL(canvasTemp, -14, 655, 1170, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_ll, y_ll, null);

            //sideRL
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideL(canvasTemp, -14, 655, 1170, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rl, y_rl, null);

            //tongueL
            bitmapTemp = Bitmap.createBitmap(699, 1092, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueL(canvasTemp);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, x_tongue_left, y_tongue_left, null);

            //tongueR
            bitmapTemp = Bitmap.createBitmap(699, 1092, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(7), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueR(canvasTemp);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, x_tongue_right, y_tongue_right, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 7047) {
            //sideLR
            Bitmap bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3524, -2316, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideR(canvasTemp, 14, 10, 1030, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_lr, y_lr, null);

            //sideRR
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -157, -2316, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideR(canvasTemp, 14, 10, 1030, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rr, y_rr, null);

            //frontL
            bitmapTemp = Bitmap.createBitmap(1499, 1221, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4458, -3521, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front_left, y_front_left, null);

            //frontR
            bitmapTemp = Bitmap.createBitmap(1499, 1221, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1090, -3521, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front_right, y_front_right, null);

            //sideLL
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -5650, -2316, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideL(canvasTemp, -14, 655, 1170, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_ll, y_ll, null);

            //sideRL
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2284, -2316, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideL(canvasTemp, -14, 655, 1170, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rl, y_rl, null);

            //tongueL
            bitmapTemp = Bitmap.createBitmap(699, 1092, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4865, -2316, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueL(canvasTemp);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, x_tongue_left, y_tongue_left, null);

            //tongueR
            bitmapTemp = Bitmap.createBitmap(699, 1092, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1498, -2316, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueR(canvasTemp);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, x_tongue_right, y_tongue_right, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {//jj
            if (MainActivity.instance.bitmaps.get(0).getWidth() != 4000) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 4000, 4000, true));
            }
            //sideLR
            Bitmap bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -26, -2591, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_side_r);

            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideR(canvasTemp, 14, 10, 1030, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_lr, y_lr, null);

            //sideRR
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -26, -193, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideR(canvasTemp, 14, 10, 1030, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rr, y_rr, null);

            //frontL
            bitmapTemp = Bitmap.createBitmap(1499, 1221, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1254, -1892, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front_left, y_front_left, null);

            //frontR
            bitmapTemp = Bitmap.createBitmap(1499, 1221, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1254, -673, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front_right, y_front_right, null);

            //sideLL
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2741, -2591, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideL(canvasTemp, -14, 655, 1170, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_ll, y_ll, null);

            //sdieRL
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2741, -193, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideL(canvasTemp, -14, 655, 1170, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rl, y_rl, null);

            //tongueL
            bitmapTemp = Bitmap.createBitmap(699, 1092, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -208, -1463, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueL(canvasTemp);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, x_tongue_left, y_tongue_left, null);

            //tongueR
            bitmapTemp = Bitmap.createBitmap(699, 1092, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3125, -1463, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueR(canvasTemp);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, x_tongue_right, y_tongue_right, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();

        } else if (orderItems.get(currentID).sku.equals("KM")) {//adam
            //sideLR
            Bitmap bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -8, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideR(canvasTemp, 14, 10, 1030, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_lr, y_lr, null);

            //sideRR
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -8, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideR(canvasTemp, 14, 10, 1030, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rr, y_rr, null);

            //frontL
            bitmapTemp = Bitmap.createBitmap(1499, 1221, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -850, -1231, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front_left, y_front_left, null);

            //frontR
            bitmapTemp = Bitmap.createBitmap(1499, 1221, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -850, -1231, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front_right, y_front_right, null);

            //sideLL
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1949, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideL(canvasTemp, -14, 655, 1170, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_ll, y_ll, null);

            //sideRL
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -1949, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideL(canvasTemp, -14, 655, 1170, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rl, y_rl, null);

            //tongueL
            bitmapTemp = Bitmap.createBitmap(699, 1092, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1250, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueL(canvasTemp);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, x_tongue_left, y_tongue_left, null);

            //tongueR
            bitmapTemp = Bitmap.createBitmap(699, 1092, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -1250, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueR(canvasTemp);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, x_tongue_right, y_tongue_right, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 1 || orderItems.get(currentID).imgs.size() == 2) {//adam 同第二版
            Bitmap bitmapLeft = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 2521, 2451, true);
            Bitmap bitmapRight = null;

            if (orderItems.get(currentID).imgs.size() == 1) {
                Matrix matrix = new Matrix();
                matrix.postScale(-1, 1);
                bitmapRight = Bitmap.createBitmap(bitmapLeft, 0, 0, 2521, 2450, matrix, true);
            } else {
                bitmapRight = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(1), 2521, 2451, true);
            }

            //sideLR
            Bitmap bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapLeft, -11, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideR(canvasTemp, 14, 10, 1030, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_lr, y_lr, null);

            //sideRR
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapRight, -11, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideR(canvasTemp, 14, 10, 1030, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rr, y_rr, null);

            //part2
            bitmapTemp = Bitmap.createBitmap(1499, 1221, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapLeft, -506, -1240, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front_left, y_front_left, null);

            //frontR
            bitmapTemp = Bitmap.createBitmap(1499, 1221, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapRight, -506, -1240, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front_right, y_front_right, null);

            //sideLL
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapLeft, -1260, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideL(canvasTemp, -14, 655, 1170, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_ll, y_ll, null);

            //sideRL
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapRight, -1260, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideL(canvasTemp, -14, 655, 1170, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rl, y_rl, null);

            //tongueL
            bitmapTemp = Bitmap.createBitmap(699, 1092, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapLeft, -911, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueL(canvasTemp);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, x_tongue_left, y_tongue_left, null);

            //tongueR
            bitmapTemp = Bitmap.createBitmap(699, 1092, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapRight, -911, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueR(canvasTemp);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, x_tongue_right, y_tongue_right, null);

            bitmapLeft.recycle();
            bitmapRight.recycle();
            bitmapTemp.recycle();
            bitmapDB.recycle();

        }

        try {
            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

            if (orderItems.get(currentID).platform.equals("zy")) {
                nameCombine = orderItems.get(currentID).sku + "_" + orderItems.get(currentID).size + "_" + "(" + MainActivity.instance.orderDate_short + "-" + (currentID + 1) + ")_" + orderItems.get(currentID).order_number + "_共" + orderItems.get(currentID).newCode + "个" + strPlus + ".jpg";
            }

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
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + orderItems.get(currentID).size);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + orderItems.get(currentID).size);
            sheet.addCell(label1);
            Number number2 = new Number(2, currentID + 1, orderItems.get(currentID).num);
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
        if (MainActivity.instance.tb_auto.isChecked())
            remix();
    }

    void setSize(int size) {
        switch (size) {
            case 35:
                width_side = 1057;
                height_side = 1109;
                width_front = 1371;
                height_front = 1062;
                width_tongue = 636;
                height_tongue = 943;

                width_combine = 7077;
                height_combine = 1109;
                x_rr = 0;
                y_rr = 0;
                x_front_right = 846;
                y_front_right = 0;
                x_rl = 2011;
                y_rl = 0;
                x_tongue_right = 2956;
                y_tongue_right = 0;
                x_lr = 3485;
                y_lr = 0;
                x_front_left = 4331;
                y_front_left = 0;
                x_ll = 5496;
                y_ll = 0;
                x_tongue_left = 6441;
                y_tongue_left = 0;
                break;
            case 36:
                width_side = 1086;
                height_side = 1128;
                width_front = 1392;
                height_front = 1092;
                width_tongue = 636;
                height_tongue = 943;

                width_combine = 7222;
                height_combine = 1128;
                x_rr = 0;
                y_rr = 0;
                x_front_right = 874;
                y_front_right = 0;
                x_rl = 2059;
                y_rl = 0;
                x_tongue_right = 3030;
                y_tongue_right = 0;
                x_lr = 3556;
                y_lr = 0;
                x_front_left = 4430;
                y_front_left = 0;
                x_ll = 5615;
                y_ll = 0;
                x_tongue_left = 6586;
                y_tongue_left = 0;
                break;
            case 37:
                width_side = 1128;
                height_side = 1147;
                width_front = 1414;
                height_front = 1122;
                width_tongue = 656;
                height_tongue = 991;

                width_combine = 7483;
                height_combine = 1147;
                x_rr = 0;
                y_rr = 0;
                x_front_right = 917;
                y_front_right = 0;
                x_rl = 2125;
                y_rl = 0;
                x_tongue_right = 3140;
                y_tongue_right = 0;
                x_lr = 3687;
                y_lr = 0;
                x_front_left = 4604;
                y_front_left = 0;
                x_ll = 5812;
                y_ll = 0;
                x_tongue_left = 6827;
                y_tongue_left = 0;
                break;
            case 38:
                width_side = 1156;
                height_side = 1166;
                width_front = 1434;
                height_front = 1146;
                width_tongue = 654;
                height_tongue = 991;

                width_combine = 7620;
                height_combine = 1166;
                x_rr = 0;
                y_rr = 0;
                x_front_right = 944;
                y_front_right = 0;
                x_rl = 2170;
                y_rl = 0;
                x_tongue_right = 3211;
                y_tongue_right = 0;
                x_lr = 3755;
                y_lr = 0;
                x_front_left = 4699;
                y_front_left = 0;
                x_ll = 5925;
                y_ll = 0;
                x_tongue_left = 6966;
                y_tongue_left = 0;
                break;
            case 39:
                width_side = 1178;
                height_side = 1191;
                width_front = 1455;
                height_front = 1177;
                width_tongue = 676;
                height_tongue = 1041;

                width_combine = 7790;
                height_combine = 1191;
                x_rr = 0;
                y_rr = 0;
                x_front_right = 964;
                y_front_right = 0;
                x_rl = 2210;
                y_rl = 0;
                x_tongue_right = 3274;
                y_tongue_right = 0;
                x_lr = 3840;
                y_lr = 0;
                x_front_left = 4804;
                y_front_left = 0;
                x_ll = 6050;
                y_ll = 0;
                x_tongue_left = 7114;
                y_tongue_left = 0;
                break;
            case 40:
                width_side = 1211;
                height_side = 1213;
                width_front = 1477;
                height_front = 1205;
                width_tongue = 676;
                height_tongue = 1041;

                width_combine = 7959;
                height_combine = 1213;
                x_rr = 0;
                y_rr = 0;
                x_front_right = 997;
                y_front_right = 0;
                x_rl = 2265;
                y_rl = 0;
                x_tongue_right = 3359;
                y_tongue_right = 0;
                x_lr = 3924;
                y_lr = 0;
                x_front_left = 4921;
                y_front_left = 0;
                x_ll = 6189;
                y_ll = 0;
                x_tongue_left = 7283;
                y_tongue_left = 0;
                break;
            case 41:
                width_side = 1242;
                height_side = 1231;
                width_front = 1499;
                height_front = 1221;
                width_tongue = 699;
                height_tongue = 1093;

                width_combine = 8150;
                height_combine = 1231;
                x_rr = 0;
                y_rr = 0;
                x_front_right = 1022;
                y_front_right = 0;
                x_rl = 2308;
                y_rl = 0;
                x_tongue_right = 3431;
                y_tongue_right = 0;
                x_lr = 4020;
                y_lr = 0;
                x_front_left = 5042;
                y_front_left = 0;
                x_ll = 6328;
                y_ll = 0;
                x_tongue_left = 7451;
                y_tongue_left = 0;
                break;
            case 42:
                width_side = 1268;
                height_side = 1256;
                width_front = 1520;
                height_front = 1249;
                width_tongue = 699;
                height_tongue = 1093;

                width_combine = 8136;
                height_combine = 1321;
                x_rr = -4;
                y_rr = 65;
                x_front_right = 1012;
                y_front_right = 0;
                x_rl = 2291;
                y_rl = 58;
                x_tongue_right = 3430;
                y_tongue_right = 0;
                x_lr = 4003;
                y_lr = 65;
                x_front_left = 5019;
                y_front_left = 0;
                x_ll = 6298;
                y_ll = 58;
                x_tongue_left = 7437;
                y_tongue_left = 0;
                break;
            case 43:
                width_side = 1297;
                height_side = 1282;
                width_front = 1540;
                height_front = 1277;
                width_tongue = 721;
                height_tongue = 1142;

                width_combine = 8150;
                height_combine = 1418;
                x_rr = 0;
                y_rr = 136;
                x_front_right = 1007;
                y_front_right = 0;
                x_rl = 2264;
                y_rl = 136;
                x_tongue_right = 3420;
                y_tongue_right = 0;
                x_lr = 4009;
                y_lr = 136;
                x_front_left = 5016;
                y_front_left = 0;
                x_ll = 6273;
                y_ll = 136;
                x_tongue_left = 7429;
                y_tongue_left = 0;
                break;
            case 44:
                width_side = 1323;
                height_side = 1303;
                width_front = 1562;
                height_front = 1303;
                width_tongue = 721;
                height_tongue = 1142;

                width_combine = 8150;
                height_combine = 1487;
                x_rr = 0;
                y_rr = 184;
                x_front_right = 1005;
                y_front_right = 0;
                x_rl = 2256;
                y_rl = 184;
                x_tongue_right = 3426;
                y_tongue_right = 0;
                x_lr = 4003;
                y_lr = 184;
                x_front_left = 5008;
                y_front_left = 0;
                x_ll = 6259;
                y_ll = 184;
                x_tongue_left = 7429;
                y_tongue_left = 0;
                break;
            case 45:
                width_side = 1373;
                height_side = 1324;
                width_front = 1583;
                height_front = 1337;
                width_tongue = 744;
                height_tongue = 1193;

                width_combine = 8150;
                height_combine = 1605;
                x_rr = 0;
                y_rr = 281;
                x_front_right = 996;
                y_front_right = 0;
                x_rl = 2211;
                y_rl = 281;
                x_tongue_right = 3414;
                y_tongue_right = 0;
                x_lr = 3992;
                y_lr = 281;
                x_front_left = 4988;
                y_front_left = 0;
                x_ll = 6203;
                y_ll = 281;
                x_tongue_left = 7406;
                y_tongue_left = 0;
                break;
            case 46:
                width_side = 1400;
                height_side = 1338;
                width_front = 1602;
                height_front = 1360;
                width_tongue = 744;
                height_tongue = 1193;

                width_combine = 8138;
                height_combine = 1666;
                x_rr = 0;
                y_rr = 328;
                x_front_right = 994;
                y_front_right = 0;
                x_rl = 2197;
                y_rl = 328;
                x_tongue_right = 3415;
                y_tongue_right = 0;
                x_lr = 3979;
                y_lr = 328;
                x_front_left = 4973;
                y_front_left = 0;
                x_ll = 6176;
                y_ll = 328;
                x_tongue_left = 7394;
                y_tongue_left = 0;
                break;
            case 47:
                width_side = 1430;
                height_side = 1360;
                width_front = 1623;
                height_front = 1390;
                width_tongue = 767;
                height_tongue = 1243;

                width_combine = 8155;
                height_combine = 1742;
                x_rr = 0;
                y_rr = 382;
                x_front_right = 982;
                y_front_right = 0;
                x_rl = 2165;
                y_rl = 382;
                x_tongue_right = 3406;
                y_tongue_right = 0;
                x_lr = 3982;
                y_lr = 382;
                x_front_left = 4964;
                y_front_left = 0;
                x_ll = 6147;
                y_ll = 382;
                x_tongue_left = 7388;
                y_tongue_left = 0;
                break;
            case 48:
                width_side = 1460;
                height_side = 1380;
                width_front = 1644;
                height_front = 1420;
                width_tongue = 767;
                height_tongue = 1243;

                width_combine = 8148;
                height_combine = 1809;
                x_rr = 0;
                y_rr = 428;
                x_front_right = 976;
                y_front_right = 0;
                x_rl = 2145;
                y_rl = 428;
                x_tongue_right = 3406;
                y_tongue_right = 0;
                x_lr = 3975;
                y_lr = 428;
                x_front_left = 4951;
                y_front_left = 0;
                x_ll = 6120;
                y_ll = 428;
                x_tongue_left = 7381;
                y_tongue_left = 0;
                break;
        }
    }

}
