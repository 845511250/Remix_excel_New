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

public class FragmentDY extends BaseFragment {
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

    Paint paint,paintRed, rectPaint, paintBlue,paintRectBlack;
    String time;

    int width_side, height_side, width_front, height_front, width_tongue, height_tongue;

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
        orderItems=MainActivity.instance.orderItems;
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
                    if(!MainActivity.instance.cb_fastmode.isChecked())
                        iv_right.setImageBitmap(MainActivity.instance.bitmaps.get(0));
                    checkremix();
                } else if (message == 3) {
                    bt_remix.setClickable(false);
                } else if(message==MainActivity.LOADED_IMGS){
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

    public void remix(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                for(num=orderItems.get(currentID).num;num>=1;num--) {
                    intPlus = orderItems.get(currentID).num - num + 1;
                    for(int i=0;i<currentID;i++) {
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


    void drawTextFrontL(Canvas canvas){
        canvas.save();
        canvas.rotate(74.8f, 15, 73);
        canvas.drawRect(15, 33, 515, 73, rectPaint);
        canvas.drawText("      " + orderItems.get(currentID).newCode, 20, 70, paintRed);
        canvas.restore();
        canvas.save();
        canvas.rotate(-75.2f, 1331, 631);
        canvas.drawRect(1331, 596, 1951, 631, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).order_number + " 左" + orderItems.get(currentID).size + "码" + orderItems.get(currentID).sku, 1331, 627, paint);
        canvas.restore();
    }
    void drawTextFrontR(Canvas canvas){
        canvas.save();
        canvas.rotate(74.8f, 15, 73);
        canvas.drawRect(15, 33, 515, 73, rectPaint);
        canvas.drawText("      "+orderItems.get(currentID).newCode, 20, 70, paintRed);
        canvas.restore();
        canvas.save();
        canvas.rotate(-75.2f, 1331, 631);
        canvas.drawRect(1331, 596, 1951, 631, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).order_number + " 右" + orderItems.get(currentID).size + "码" + orderItems.get(currentID).sku, 1331, 627, paint);
        canvas.restore();
    }
    void drawTextTongueL(Canvas canvasPart4L){
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
    void drawTextTongueR(Canvas canvasPart4R){
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


    public void remixx(){
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(5364, 2748, Bitmap.Config.ARGB_8888);
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
            canvasCombine.drawBitmap(bitmapTemp, 0 + 52 * (48 - orderItems.get(currentID).size) / 13f, 0, null);

            //sideRR
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(6), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideR(canvasTemp, 14, 10, 1030, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 2385 + 52 * (48 - orderItems.get(currentID).size) / 13f, 1374, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 2197 - width_front / 2, 0, null);

            //frontR
            bitmapTemp = Bitmap.createBitmap(1499, 1221, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 822 - width_front / 2, 1328, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 4387 - width_side - 52 * (48 - orderItems.get(currentID).size) / 13f, 0, null);

            //sideRL
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideL(canvasTemp, -14, 655, 1170, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 5364 - width_side - 52 * (48 - orderItems.get(currentID).size) / 13f, 1374, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 4770 - width_tongue / 2, 0, null);

            //tongueR
            bitmapTemp = Bitmap.createBitmap(699, 1092, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(7), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueR(canvasTemp);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 2032 - width_tongue / 2, 1505, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 0 + 52 * (48 - orderItems.get(currentID).size) / 13f, 0, null);

            //sideRR
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -157, -2316, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideR(canvasTemp, 14, 10, 1030, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 2385 + 52 * (48 - orderItems.get(currentID).size) / 13f, 1374, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 2197 - width_front / 2, 0, null);

            //frontR
            bitmapTemp = Bitmap.createBitmap(1499, 1221, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1090, -3521, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 822 - width_front / 2, 1328, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 4387 - width_side - 52 * (48 - orderItems.get(currentID).size) / 13f, 0, null);

            //sideRL
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2284, -2316, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideL(canvasTemp, -14, 655, 1170, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 5364 - width_side - 52 * (48 - orderItems.get(currentID).size) / 13f, 1374, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 4770 - width_tongue / 2, 0, null);

            //tongueR
            bitmapTemp = Bitmap.createBitmap(699, 1092, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1498, -2316, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueR(canvasTemp);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 2032 - width_tongue / 2, 1505, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 0 + 52 * (48 - orderItems.get(currentID).size) / 13f, 0, null);

            //sideRR
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -26, -193, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideR(canvasTemp, 14, 10, 1030, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 2385 + 52 * (48 - orderItems.get(currentID).size) / 13f, 1374, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 2197 - width_front / 2, 0, null);

            //frontR
            bitmapTemp = Bitmap.createBitmap(1499, 1221, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1254, -673, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 822 - width_front / 2, 1328, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 4387 - width_side - 52 * (48 - orderItems.get(currentID).size) / 13f, 0, null);

            //sdieRL
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2741, -193, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideL(canvasTemp, -14, 655, 1170, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 5364 - width_side - 52 * (48 - orderItems.get(currentID).size) / 13f, 1374, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 4770 - width_tongue / 2, 0, null);

            //tongueR
            bitmapTemp = Bitmap.createBitmap(699, 1092, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3125, -1463, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueR(canvasTemp);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 2032 - width_tongue / 2, 1505, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();

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
            canvasCombine.drawBitmap(bitmapTemp, 0 + 52 * (48 - orderItems.get(currentID).size) / 13f, 0, null);

            //sideRR
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapRight, -11, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideR(canvasTemp, 14, 10, 1030, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 2385 + 52 * (48 - orderItems.get(currentID).size) / 13f, 1374, null);

            //part2
            Bitmap bitmapPart2L = Bitmap.createBitmap(bitmapLeft, 506, 1240, 1499, 1210);
            bitmapTemp = Bitmap.createBitmap(1499, 1221, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapLeft, -506, -1240, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dy_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 2197 - width_front / 2, 0, null);

            //frontR
            bitmapTemp = Bitmap.createBitmap(1499, 1221, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapRight, -506, -1240, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 822 - width_front / 2, 1328, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 4387 - width_side - 52 * (48 - orderItems.get(currentID).size) / 13f, 0, null);

            //sideRL
            bitmapTemp = Bitmap.createBitmap(1242, 1231, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapRight, -1260, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            drawTextSideL(canvasTemp, -14, 655, 1170, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 5364 - width_side - 52 * (48 - orderItems.get(currentID).size) / 13f, 1374, null);

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
            canvasCombine.drawBitmap(bitmapTemp, 4770 - width_tongue / 2, 0, null);

            //tongueR
            bitmapTemp = Bitmap.createBitmap(699, 1092, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapRight, -911, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongueR(canvasTemp);

            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 2032 - width_tongue / 2, 1505, null);

            bitmapLeft.recycle();
            bitmapRight.recycle();
            bitmapTemp.recycle();
            bitmapDB.recycle();

        }

        try {
            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

            if(orderItems.get(currentID).platform.equals("zy")){
                nameCombine = orderItems.get(currentID).sku + "_" + orderItems.get(currentID).size + "_"  + "(" + MainActivity.instance.orderDate_short + "-" + (currentID + 1) + ")_" + orderItems.get(currentID).order_number + "_共" + orderItems.get(currentID).newCode + "个" + strPlus + ".jpg";
            }

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 150);

            //释放bitmap
            bitmapCombine.recycle();

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
            Label label0 = new Label(0, currentID+1, orderItems.get(currentID).order_number+orderItems.get(currentID).sku+orderItems.get(currentID).size);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku+orderItems.get(currentID).size);
            sheet.addCell(label1);
            Number number2 = new Number(2, currentID+1, orderItems.get(currentID).num);
            sheet.addCell(number2);
            Label label3 = new Label(3, currentID+1, orderItems.get(currentID).customer);
            sheet.addCell(label3);
            Label label4 = new Label(4, currentID+1, MainActivity.instance.orderDate_Excel);
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
        if (MainActivity.instance.tb_auto.isChecked())
            remix();
    }

    void setSize(int size){
        switch (size) {
            case 35:
                width_side = 1057;
                height_side = 1109;
                width_front = 1371;
                height_front = 1062;
                width_tongue = 636;
                height_tongue = 943;
                break;
            case 36:
                width_side = 1086;
                height_side = 1128;
                width_front = 1392;
                height_front = 1092;
                width_tongue = 636;
                height_tongue = 943;
                break;
            case 37:
                width_side = 1128;
                height_side = 1147;
                width_front = 1414;
                height_front = 1122;
                width_tongue = 656;
                height_tongue = 991;
                break;
            case 38:
                width_side = 1156;
                height_side = 1166;
                width_front = 1434;
                height_front = 1146;
                width_tongue = 656;
                height_tongue = 991;
                break;
            case 39:
                width_side = 1178;
                height_side = 1191;
                width_front = 1455;
                height_front = 1177;
                width_tongue = 676;
                height_tongue = 1041;
                break;
            case 40:
                width_side = 1211;
                height_side = 1213;
                width_front = 1477;
                height_front = 1205;
                width_tongue = 676;
                height_tongue = 1041;
                break;
            case 41:
                width_side = 1242;
                height_side = 1231;
                width_front = 1499;
                height_front = 1221;
                width_tongue = 699;
                height_tongue = 1093;
                break;
            case 42:
                width_side = 1268;
                height_side = 1256;
                width_front = 1520;
                height_front = 1249;
                width_tongue = 699;
                height_tongue = 1093;
                break;
            case 43:
                width_side = 1297;
                height_side = 1282;
                width_front = 1540;
                height_front = 1277;
                width_tongue = 721;
                height_tongue = 1142;
                break;
            case 44:
                width_side = 1323;
                height_side = 1303;
                width_front = 1562;
                height_front = 1303;
                width_tongue = 721;
                height_tongue = 1142;
                break;
            case 45:
                width_side = 1373;
                height_side = 1324;
                width_front = 1583;
                height_front = 1337;
                width_tongue = 744;
                height_tongue = 1193;
                break;
            case 46:
                width_side = 1400;
                height_side = 1338;
                width_front = 1602;
                height_front = 1360;
                width_tongue = 744;
                height_tongue = 1193;
                break;
            case 47:
                width_side = 1430;
                height_side = 1360;
                width_front = 1623;
                height_front = 1390;
                width_tongue = 767;
                height_tongue = 1243;
                break;
            case 48:
                width_side = 1460;
                height_side = 1380;
                width_front = 1644;
                height_front = 1420;
                width_tongue = 767;
                height_tongue = 1243;
                break;
        }
    }

}
