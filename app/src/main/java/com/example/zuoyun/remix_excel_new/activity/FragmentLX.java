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

public class FragmentLX extends BaseFragment {
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

    int width_front, height_front, width_inside_above, height_inside_above, width_inside_below, height_inside_below, width_outside_above,height_outside_above;
    int width_outside_below, height_outside_below, width_tongue, height_tongue;
    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint rectPaint, paint, paintRed, paintBlue,paintRectBlack;
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

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        paintRectBlack = new Paint();
        paintRectBlack.setColor(0xff000000);
        paintRectBlack.setStyle(Paint.Style.FILL);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(20);
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
                    if(!MainActivity.instance.cb_fastmode.isChecked()){
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

    public void remix(){
        new Thread(){
            @Override
            public void run() {
                super.run();
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
        }.start();

    }

    void drawTextFrontL(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(-61.5f, 662, 846);
        canvas.drawRect(662, 846 - 25, 662 + 370, 846, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "码-" + orderItems.get(currentID).color + "-" + LR + " " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 662, 846 - 3, paint);
        canvas.restore();
    }
    void drawTextFrontR(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(62f, 27, 528);
        canvas.drawRect(27, 528 - 25, 27 + 370, 528, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "码-" + orderItems.get(currentID).color + "-" + LR + " " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 27, 528 - 3, paint);
        canvas.restore();
    }
    void drawTextTongue(Canvas canvas, String LR) {
        canvas.drawRect(228, 1163 - 25, 228 + 320, 1163, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "码-" + orderItems.get(currentID).color + "-" + LR + " " + time + " " + orderItems.get(currentID).order_number, 228, 1163 - 3, paint);
    }
    void drawTextInsideBelow(Canvas canvas, String LR) {
        canvas.drawRect(417, 388 - 25, 417 + 500, 388, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "码-" + orderItems.get(currentID).color + "-" + LR + " " + time + " " + orderItems.get(currentID).newCode_short + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 417, 388 - 3, paint);
    }
    void drawTextInsideAboveL(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(-26.6f, 1284, 545);
        canvas.drawRect(1284, 545 - 25, 1284 + 420, 545, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "码-" + orderItems.get(currentID).color + "-" + LR + " " + orderItems.get(currentID).order_number, 1284, 545 - 3, paint);
        canvas.restore();
    }
    void drawTextInsideAboveR(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(27.2f, 135, 387);
        canvas.drawRect(135, 387 - 25, 135 + 420, 387, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "码-" + orderItems.get(currentID).color + "-" + LR + " " + orderItems.get(currentID).order_number, 135, 387 - 3, paint);
        canvas.restore();
    }
    void drawTextOutsideBelowL(Canvas canvas, String LR) {
        canvas.drawRect(437, 925 - 23, 437 + 500, 925, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "码-" + orderItems.get(currentID).color + "-" + LR + " " + orderItems.get(currentID).newCode_short + " " + orderItems.get(currentID).order_number, 437, 925 - 3, paint);
    }
    void drawTextOutsideBelowR(Canvas canvas, String LR) {
        canvas.drawRect(961, 925 - 23, 961 + 500, 925, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "码-" + orderItems.get(currentID).color + "-" + LR + " " + orderItems.get(currentID).newCode_short + " " + orderItems.get(currentID).order_number, 961, 925 - 3, paint);
    }

    public void remixx(){
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(5944, 2648, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 12) {
            //left_front
            Bitmap bitmapTemp = Bitmap.createBitmap(857, 1056, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_left_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 5004 + 22 * (48 - orderItems.get(currentID).size) / 13, 1434 + 112 * (48 - orderItems.get(currentID).size) / 13, null);

            //left_inside_above
            bitmapTemp = Bitmap.createBitmap(1735, 581, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_left_inside_above);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInsideAboveL(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside_above, height_inside_above, true);
            canvasCombine.drawBitmap(bitmapTemp, 3013, 645 - height_inside_above, null);

            //left_inside_below
            bitmapTemp = Bitmap.createBitmap(1271, 399, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_left_inside_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInsideBelow(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside_below, height_inside_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 3184, 951 - height_inside_below, null);

            //left_outside_above
            bitmapTemp = Bitmap.createBitmap(1373, 622, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_left_outside_above);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextBack(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside_above, height_outside_above, true);
            canvasCombine.drawBitmap(bitmapTemp, 3013, 1738 - height_outside_above, null);

            //left_outside_below
            bitmapTemp = Bitmap.createBitmap(1896, 944, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_left_outside_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutsideBelowL(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside_below, height_outside_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 3184, 2646 - height_outside_below, null);

            //left_tongue
            bitmapTemp = Bitmap.createBitmap(764, 1167, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 5945 - width_tongue, 1320 - height_tongue, null);

            //right
            //lright_front
            bitmapTemp = Bitmap.createBitmap(857, 1056, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(6), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_right_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 941 - width_front - 22 * (48 - orderItems.get(currentID).size) / 13, 1434 + 112 * (48 - orderItems.get(currentID).size) / 13, null);

            //right_inside_above
            bitmapTemp = Bitmap.createBitmap(1735, 581, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(7), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_right_inside_above);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInsideAboveR(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside_above, height_inside_above, true);
            canvasCombine.drawBitmap(bitmapTemp, 2932 - width_inside_above, 645 - height_inside_above, null);

            //right_inside_below
            bitmapTemp = Bitmap.createBitmap(1271, 399, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(8), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_right_inside_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInsideBelow(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside_below, height_inside_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2761 - width_inside_below, 951 - height_inside_below, null);

            //right_outside_above
            bitmapTemp = Bitmap.createBitmap(1373, 622, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(9), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_right_outside_above);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextBack(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside_above, height_outside_above, true);
            canvasCombine.drawBitmap(bitmapTemp, 2932 - width_outside_above, 1738 - height_outside_above, null);

            //right_outside_below
            bitmapTemp = Bitmap.createBitmap(1896, 944, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(10), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_right_outside_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutsideBelowR(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside_below, height_outside_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2761 - width_outside_below, 2646 - height_outside_below, null);

            //right_tongue
            bitmapTemp = Bitmap.createBitmap(764, 1167, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(11), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 1320 - height_tongue, null);

            //
            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 4700) {
            //left_front
            Bitmap bitmapTemp = Bitmap.createBitmap(857, 1056, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1889, -833, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_left_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 5004 + 22 * (48 - orderItems.get(currentID).size) / 13, 1434 + 112 * (48 - orderItems.get(currentID).size) / 13, null);

            //left_inside_above
            bitmapTemp = Bitmap.createBitmap(1735, 581, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -57, -252, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_left_inside_above);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInsideAboveL(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside_above, height_inside_above, true);
            canvasCombine.drawBitmap(bitmapTemp, 3013, 645 - height_inside_above, null);

            //left_inside_below
            bitmapTemp = Bitmap.createBitmap(1271, 399, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -521, -1196, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_left_inside_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInsideBelow(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside_below, height_inside_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 3184, 951 - height_inside_below, null);

            //left_outside_above
            bitmapTemp = Bitmap.createBitmap(1373, 622, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2746, -29, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_left_outside_above);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextBack(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside_above, height_outside_above, true);
            canvasCombine.drawBitmap(bitmapTemp, 3013, 1738 - height_outside_above, null);

            //left_outside_below
            bitmapTemp = Bitmap.createBitmap(1896, 944, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2746, -651, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_left_outside_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutsideBelowL(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside_below, height_outside_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 3184, 2646 - height_outside_below, null);

            //left_tongue
            bitmapTemp = Bitmap.createBitmap(764, 1167, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1792, -29, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 5945 - width_tongue, 1320 - height_tongue, null);

            //right
            //lright_front
            bitmapTemp = Bitmap.createBitmap(857, 1056, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1953, -2814, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_right_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 941 - width_front - 22 * (48 - orderItems.get(currentID).size) / 13, 1434 + 112 * (48 - orderItems.get(currentID).size) / 13, null);

            //right_inside_above
            bitmapTemp = Bitmap.createBitmap(1735, 581, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2907, -2233, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_right_inside_above);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInsideAboveR(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside_above, height_inside_above, true);
            canvasCombine.drawBitmap(bitmapTemp, 2932 - width_inside_above, 645 - height_inside_above, null);

            //right_inside_below
            bitmapTemp = Bitmap.createBitmap(1271, 399, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2907, -3177, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_right_inside_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInsideBelow(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside_below, height_inside_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2761 - width_inside_below, 951 - height_inside_below, null);

            //right_outside_above
            bitmapTemp = Bitmap.createBitmap(1373, 622, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -580, -2010, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_right_outside_above);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextBack(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside_above, height_outside_above, true);
            canvasCombine.drawBitmap(bitmapTemp, 2932 - width_outside_above, 1738 - height_outside_above, null);

            //right_outside_below
            bitmapTemp = Bitmap.createBitmap(1896, 944, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -57, -2632, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_right_outside_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutsideBelowR(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside_below, height_outside_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2761 - width_outside_below, 2646 - height_outside_below, null);

            //right_tongue
            bitmapTemp = Bitmap.createBitmap(764, 1167, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2143, -2010, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 1320 - height_tongue, null);

            //
            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
            if(MainActivity.instance.bitmaps.get(0).getWidth() != 4759){
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 4759, 4759, true));
            }

            //left_front
            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1197, 3509, 899, 1143);
            bitmapCut = Bitmap.createScaledBitmap(bitmapCut, 857, 1056, true);
            Bitmap bitmapTemp = Bitmap.createBitmap(857, 1056, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_left_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 5004 + 22 * (48 - orderItems.get(currentID).size) / 13, 1434 + 112 * (48 - orderItems.get(currentID).size) / 13, null);

            //left_inside_above
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 202, 1843, 1892, 614);
            bitmapCut = Bitmap.createScaledBitmap(bitmapCut, 1735, 581, true);
            bitmapTemp = Bitmap.createBitmap(1735, 581, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_left_inside_above);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInsideAboveL(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside_above, height_inside_above, true);
            canvasCombine.drawBitmap(bitmapTemp, 3013, 645 - height_inside_above, null);

            //left_inside_below
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2748, 132, 1357, 422);
            bitmapCut = Bitmap.createScaledBitmap(bitmapCut, 1271, 399, true);
            bitmapTemp = Bitmap.createBitmap(1271, 399, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_left_inside_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInsideBelow(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside_below, height_inside_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 3184, 951 - height_inside_below, null);

            //left_outside_above
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 313, 84, 1499, 658);
            bitmapCut = Bitmap.createScaledBitmap(bitmapCut, 1373, 622, true);
            bitmapTemp = Bitmap.createBitmap(1373, 622, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_left_outside_above);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextBack(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside_above, height_outside_above, true);
            canvasCombine.drawBitmap(bitmapTemp, 3013, 1738 - height_outside_above, null);

            //left_outside_below
            bitmapTemp = Bitmap.createBitmap(2073, 1007, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2405, 2847, 2177, 898);
            Matrix matrix = new Matrix();
            matrix.postRotate(-8.4f);
            matrix.postTranslate(-109, 289);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1896, 944, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_left_outside_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextOutsideBelowL(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside_below, height_outside_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 3184, 2646 - height_outside_below, null);

            //left_tongue
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2555, 1299, 818, 1266);
            bitmapCut = Bitmap.createScaledBitmap(bitmapCut, 764, 1167, true);
            bitmapTemp = Bitmap.createBitmap(764, 1167, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 5945 - width_tongue, 1320 - height_tongue, null);

            //right
            //lright_front
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 175, 3509, 900, 1143);
            bitmapCut = Bitmap.createScaledBitmap(bitmapCut, 857, 1056, true);
            bitmapTemp = Bitmap.createBitmap(857, 1056, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_right_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 941 - width_front - 22 * (48 - orderItems.get(currentID).size) / 13, 1434 + 112 * (48 - orderItems.get(currentID).size) / 13, null);

            //right_inside_above
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 202, 2723, 1892, 614);
            bitmapCut = Bitmap.createScaledBitmap(bitmapCut, 1735, 581, true);
            bitmapTemp = Bitmap.createBitmap(1735, 581, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_right_inside_above);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInsideAboveR(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside_above, height_inside_above, true);
            canvasCombine.drawBitmap(bitmapTemp, 2932 - width_inside_above, 645 - height_inside_above, null);

            //right_inside_below
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2748, 701, 1374, 424);
            bitmapCut = Bitmap.createScaledBitmap(bitmapCut, 1271, 399, true);
            bitmapTemp = Bitmap.createBitmap(1271, 399, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_right_inside_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextInsideBelow(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside_below, height_inside_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2761 - width_inside_below, 951 - height_inside_below, null);

            //right_outside_above
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 313, 916, 1501, 661);
            bitmapCut = Bitmap.createScaledBitmap(bitmapCut, 1373, 622, true);
            bitmapTemp = Bitmap.createBitmap(1373, 622, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_right_outside_above);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextBack(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside_above, height_outside_above, true);
            canvasCombine.drawBitmap(bitmapTemp, 2932 - width_outside_above, 1738 - height_outside_above, null);

            //right_outside_below
            bitmapTemp = Bitmap.createBitmap(2073, 1007, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2410, 3780, 2177, 898);
            matrix = new Matrix();
            matrix.postRotate(8.4f);
            matrix.postTranslate(27, -29);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1896, 944, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_right_outside_below);
            drawTextOutsideBelowR(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside_below, height_outside_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 2761 - width_outside_below, 2646 - height_outside_below, null);

            //right_tongue
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3494, 1299, 820, 1267);
            bitmapCut = Bitmap.createScaledBitmap(bitmapCut, 764, 1167, true);
            bitmapTemp = Bitmap.createBitmap(764, 1167, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lx_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 1320 - height_tongue, null);

            //
            bitmapCut.recycle();
            bitmapTemp.recycle();
            bitmapDB.recycle();
        }


        try {
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

            //释放bitmap
            bitmapCombine.recycle();

            String printColor = orderItems.get(currentID).color.equals("白") ? "W" : "B";

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

    void setSize(int size){
        switch (size) {
            case 35:
                width_front = 787;
                height_front = 924;
                width_inside_above = 1490;
                height_inside_above = 527;
                width_inside_below = 1104;
                height_inside_below = 364;
                width_outside_above = 1179;
                height_outside_above = 561;
                width_outside_below = 1634;
                height_outside_below = 843;
                width_tongue = 688;
                height_tongue = 1014;
                break;
            case 36:
                width_front = 800;
                height_front = 947;
                width_inside_above = 1530;
                height_inside_above = 533;
                width_inside_below = 1132;
                height_inside_below = 369;
                width_outside_above = 1213;
                height_outside_above = 573;
                width_outside_below = 1679;
                height_outside_below = 856;
                width_tongue = 688;
                height_tongue = 1014;
                break;
            case 37:
                width_front = 811;
                height_front = 968;
                width_inside_above = 1571;
                height_inside_above = 544;
                width_inside_below = 1159;
                height_inside_below = 375;
                width_outside_above = 1244;
                height_outside_above = 580;
                width_outside_below = 1723;
                height_outside_below = 874;
                width_tongue = 713;
                height_tongue = 1066;
                break;
            case 38:
                width_front = 823;
                height_front = 991;
                width_inside_above = 1612;
                height_inside_above = 554;
                width_inside_below = 1187;
                height_inside_below = 382;
                width_outside_above = 1276;
                height_outside_above = 593;
                width_outside_below = 1764;
                height_outside_below = 893;
                width_tongue = 713;
                height_tongue = 1066;
                break;
            case 39:
                width_front = 835;
                height_front = 1013;
                width_inside_above = 1653;
                height_inside_above = 564;
                width_inside_below = 1215;
                height_inside_below = 387;
                width_outside_above = 1308;
                height_outside_above = 602;
                width_outside_below = 1809;
                height_outside_below = 909;
                width_tongue = 739;
                height_tongue = 1117;
                break;
            case 40:
                width_front = 846;
                height_front = 1035;
                width_inside_above = 1694;
                height_inside_above = 572;
                width_inside_below = 1243;
                height_inside_below = 394;
                width_outside_above = 1341;
                height_outside_above = 612;
                width_outside_below = 1852;
                height_outside_below = 928;
                width_tongue = 739;
                height_tongue = 1117;
                break;
            case 41:
                width_front = 859;
                height_front = 1057;
                width_inside_above = 1735;
                height_inside_above = 582;
                width_inside_below = 1271;
                height_inside_below = 401;
                width_outside_above = 1373;
                height_outside_above = 624;
                width_outside_below = 1895;
                height_outside_below = 949;
                width_tongue = 766;
                height_tongue = 1168;
                break;
            case 42:
                width_front = 871;
                height_front = 1078;
                width_inside_above = 1776;
                height_inside_above = 591;
                width_inside_below = 1299;
                height_inside_below = 406;
                width_outside_above = 1405;
                height_outside_above = 634;
                width_outside_below = 1938;
                height_outside_below = 966;
                width_tongue = 766;
                height_tongue = 1168;
                break;
            case 43:
                width_front = 882;
                height_front = 1100;
                width_inside_above = 1815;
                height_inside_above = 599;
                width_inside_below = 1327;
                height_inside_below = 413;
                width_outside_above = 1437;
                height_outside_above = 645;
                width_outside_below = 1983;
                height_outside_below = 981;
                width_tongue = 792;
                height_tongue = 1219;
                break;
            case 44:
                width_front = 894;
                height_front = 1123;
                width_inside_above = 1856;
                height_inside_above = 610;
                width_inside_below = 1355;
                height_inside_below = 419;
                width_outside_above = 1469;
                height_outside_above = 655;
                width_outside_below = 2027;
                height_outside_below = 997;
                width_tongue = 791;
                height_tongue = 1219;
                break;
            case 45:
                width_front = 906;
                height_front = 1143;
                width_inside_above = 1898;
                height_inside_above = 620;
                width_inside_below = 1383;
                height_inside_below = 425;
                width_outside_above = 1500;
                height_outside_above = 666;
                width_outside_below = 2070;
                height_outside_below = 1016;
                width_tongue = 816;
                height_tongue = 1270;
                break;
            case 46:
                width_front = 917;
                height_front = 1166;
                width_inside_above = 1939;
                height_inside_above = 629;
                width_inside_below = 1411;
                height_inside_below = 430;
                width_outside_above = 1543;
                height_outside_above = 676;
                width_outside_below = 2113;
                height_outside_below = 1033;
                width_tongue = 816;
                height_tongue = 1270;
                break;
            case 47:
                width_front = 929;
                height_front = 1188;
                width_inside_above = 1980;
                height_inside_above = 639;
                width_inside_below = 1440;
                height_inside_below = 435;
                width_outside_above = 1565;
                height_outside_above = 686;
                width_outside_below = 2159;
                height_outside_below = 1050;
                width_tongue = 844;
                height_tongue = 1322;
                break;
            case 48:
                width_front = 941;
                height_front = 1211;
                width_inside_above = 2020;
                height_inside_above = 649;
                width_inside_below = 1469;
                height_inside_below = 443;
                width_outside_above = 1600;
                height_outside_above = 700;
                width_outside_below = 2201;
                height_outside_below = 1071;
                width_tongue = 844;
                height_tongue = 1322;
                break;
        }
    }

}
