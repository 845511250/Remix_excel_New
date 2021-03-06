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

public class FragmentGS160 extends BaseFragment {
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

    int width_front, width_back, width_sleeve, width_maozi, width_xiabai, width_pocket, width_xiukou;
    int height_front,height_back, height_sleeve,height_maozi,height_xiabai,height_pocket,height_xiukou;
    int width_cut, height_cut;

    int x_front,x_back, x_sleeve_l, x_sleeve_r, x_maoli_l, x_maoli_r, x_maomian_l, x_maomian_r, x_xiabai_front, x_xiabai_back,x_pocket, x_xiukou_l,x_xiukou_r;
    int y_front,y_back, y_sleeve_l, y_sleeve_r, y_maoli_l, y_maoli_r, y_maomian_l, y_maomian_r, y_xiabai_front, y_xiabai_back,y_pocket, y_xiukou_l,y_xiukou_r;
    int width_combine, height_combine;

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
        canvas.drawRect(1400, 4797 - 25, 1400 + 500, 4797, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + "_" + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode, 1400, 4797 - 2, paint);
    }
    void drawTextBack(Canvas canvas) {
        canvas.drawRect(1400, 4858 - 25, 1400 + 500, 4858, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + "_" + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode, 1400, 4858 - 2, paint);
    }
    void drawTextXiuziL(Canvas canvas) {
        canvas.drawRect(1185, 14, 1185 + 30, 14 + 25, rectPaint);
        canvas.drawText("左", 1185, 14 + 23, paint);

        canvas.drawRect(1000, 3302 - 25, 1000 + 500, 3302, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 1000, 3302 - 2, paint);
    }
    void drawTextXiuziR(Canvas canvas) {
        canvas.drawRect(1185, 14, 1185 + 30, 14 + 25, rectPaint);
        canvas.drawText("右", 1185, 14 + 23, paint);

        canvas.drawRect(1000, 3302 - 25, 1000 + 500, 3302, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 1000, 3302 - 2, paint);
    }
    void drawTextMaoziInL(Canvas canvas) {
        canvas.save();
        canvas.rotate(6, 1291, 22);
        canvas.drawRect(1291, 22, 1291 + 400, 22 + 25, rectPaint);
        canvas.drawText("内左" + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number, 1291, 22 + 23, paint);
        canvas.restore();
    }
    void drawTextMaoziInR(Canvas canvas) {
        canvas.save();
        canvas.rotate(-6, 108, 65);
        canvas.drawRect(108, 65, 108 + 400, 65 + 25, rectPaint);
        canvas.drawText("内右" + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number, 108, 65 + 23, paint);
        canvas.restore();
    }
    void drawTextMaoziOutL(Canvas canvas) {
        canvas.save();
        canvas.rotate(-6.4f, 137, 73);
        canvas.drawRect(137, 73, 137 + 400, 73 + 25, rectPaint);
        canvas.drawText("外左" + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number, 137, 73 + 23, paint);
        canvas.restore();
    }
    void drawTextMaoziOutR(Canvas canvas) {
        canvas.save();
        canvas.rotate(5.9f, 1284, 31);
        canvas.drawRect(1284, 31, 1284 + 400, 31 + 25, rectPaint);
        canvas.drawText("外右" + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number, 1284, 31 + 23, paint);
        canvas.restore();
    }
    void drawTextXiabaiFront(Canvas canvas) {
        canvas.drawRect(1500, 10, 1500 + 300, 10 + 25, rectPaint);
        canvas.drawText("前下摆  " + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number, 1500, 10 + 23, paint);
    }
    void drawTextXiabaiBack(Canvas canvas) {
        canvas.drawRect(1500, 10, 1500 + 300, 10 + 25, rectPaint);
        canvas.drawText("后下摆  " + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number, 1500, 10 + 23, paint);
    }
    void drawTextXiukouL(Canvas canvas) {
        canvas.drawRect(700, 10, 700 + 300, 10 + 25, rectPaint);
        canvas.drawText("左  " + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number, 700, 10 + 23, paint);
    }
    void drawTextXiukouR(Canvas canvas) {
        canvas.drawRect(700, 10, 700 + 300, 10 + 25, rectPaint);
        canvas.drawText("右  " + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number, 700, 8 + 23, paint);
    }
    void drawTextPocket(Canvas canvas) {
        canvas.drawRect(1000, 137, 1000 + 300, 137 + 25, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number, 1000, 137 + 23, paint);
    }

    public void remixx(){
        int margin = 50;
        int marginHeight = Math.max(height_front + height_back + margin, height_maozi * 4);
        Matrix matrix = new Matrix();

        Bitmap bitmapCombine = Bitmap.createBitmap(width_combine, height_combine, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {//jj
            if (MainActivity.instance.bitmaps.get(0).getWidth() != 12812) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 12812, 12812, true));
            }

            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2931, 4786, 3261, 4804);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front, y_front, null);

            //back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6744, 4786, 3261, 4865);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back, y_back, null);

            //xiuziL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 10093, 4786, 2412, 3309);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_arm_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_l, y_sleeve_l, null);

            //xiuziR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 320, 4786, 2412, 3309);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_arm_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_r, y_sleeve_r, null);

            //maomianL
            bitmapTemp = Bitmap.createBitmap(1806, 2475, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -6558, -2156, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_maomian_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, x_maomian_l, y_maomian_l, null);

            //maomianR
            bitmapTemp = Bitmap.createBitmap(1806, 2475, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -8367, -2156, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_maomian_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, x_maomian_r, y_maomian_r, null);

            //maoliR
            bitmapTemp = Bitmap.createBitmap(1806, 2475, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2755, -2156, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_maoli_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, x_maoli_r, y_maoli_r, null);

            //maoliL
            bitmapTemp = Bitmap.createBitmap(1806, 2475, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4563, -2156, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_maoli_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, x_maoli_l, y_maoli_l, null);

            //xiabaiFront
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2969, 9768, 3179, 892);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_xiabai_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiabaiFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiabai, height_xiabai, true);
            canvasCombine.drawBitmap(bitmapTemp, x_xiabai_front, y_xiabai_front, null);

            //xiabaiBack
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6777, 9768, 3179, 892);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_xiabai_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiabaiBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiabai, height_xiabai, true);
            canvasCombine.drawBitmap(bitmapTemp, x_xiabai_back, y_xiabai_back, null);

            //xiukouL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 10555, 8211, 1473, 892);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, x_xiukou_l, y_xiukou_l, null);

            //xiukouR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 792, 8211, 1473, 892);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, x_xiukou_r, y_xiukou_r, null);

            //pocket
            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3312 - (width_cut - 2498) / 2, 7298, width_cut, height_cut);
            bitmapCut = Bitmap.createScaledBitmap(bitmapCut, 2498, 1441, true);
            bitmapTemp = Bitmap.createBitmap(2498, 1441, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_pocket);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocket(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_pocket, y_pocket, null);
            bitmapDB.recycle();
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1 || orderItems.get(currentID).imgs.size() == 2) {//adam 同第二版
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2419, 2305, 3261, 4804);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front, y_front, null);

            //back
            bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 2419, 2244, 3261, 4865);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back, y_back, null);

            //xiuziL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5680, 2994, 2412, 3309);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_arm_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_l, y_sleeve_l, null);

            //xiuziR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7, 2994, 2412, 3309);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_arm_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_r, y_sleeve_r, null);

            //maomianL
            bitmapTemp = Bitmap.createBitmap(1806, 2475, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), -2243, -21, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_maomian_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, x_maomian_l, y_maomian_l, null);

            //maomianR
            bitmapTemp = Bitmap.createBitmap(1806, 2475, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), -4049, -21, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_maomian_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, x_maomian_r, y_maomian_r, null);

            //maoliR
            bitmapTemp = Bitmap.createBitmap(1806, 2475, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2243, -21, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_maoli_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, x_maoli_r, y_maoli_r, null);

            //maoliL
            bitmapTemp = Bitmap.createBitmap(1806, 2475, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4049, -21, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_maoli_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, x_maoli_l, y_maoli_l, null);

            //xiabaiFront
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2459, 6987, 3179, 892);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_xiabai_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiabaiFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiabai, height_xiabai, true);
            canvasCombine.drawBitmap(bitmapTemp, x_xiabai_front, y_xiabai_front, null);

            //xiabaiBack
            bitmapTemp = Bitmap.createBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 2459, 6987, 3179, 892);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_xiabai_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiabaiBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiabai, height_xiabai, true);
            canvasCombine.drawBitmap(bitmapTemp, x_xiabai_back, y_xiabai_back, null);

            //xiukouL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6150, 6184, 1473, 892);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, x_xiukou_l, y_xiukou_l, null);

            //xiukouR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 477, 6184, 1473, 892);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, x_xiukou_r, y_xiukou_r, null);

            //pocket
            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2801 - (width_cut - 2498) / 2, 4821, width_cut, height_cut);
            bitmapCut = Bitmap.createScaledBitmap(bitmapCut, 2498, 1441, true);
            bitmapTemp = Bitmap.createBitmap(2498, 1441, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_pocket);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocket(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_pocket, y_pocket, null);
            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 9) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(3261, 4804, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front, y_front, null);

            //back
            bitmapTemp = Bitmap.createBitmap(3261, 4865, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back, y_back, null);

            //xiuziL
            bitmapTemp = Bitmap.createBitmap(2412, 3309, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(6), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_arm_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_l, y_sleeve_l, null);

            //xiuziR
            bitmapTemp = Bitmap.createBitmap(2412, 3309, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(8), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_arm_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_r, y_sleeve_r, null);

            //maomianL
            bitmapTemp = Bitmap.createBitmap(1806, 2475, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_maomian_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, x_maomian_l, y_maomian_l, null);

            //maomianR
            bitmapTemp = Bitmap.createBitmap(1806, 2475, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), -1806, -0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_maomian_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, x_maomian_r, y_maomian_r, null);

            //maoliR
            bitmapTemp = Bitmap.createBitmap(1806, 2475, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_maoli_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, x_maoli_r, y_maoli_r, null);
            bitmapTemp.recycle();

            //maoliL
            bitmapTemp = Bitmap.createBitmap(1806, 2475, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_maoli_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, x_maoli_l, y_maoli_l, null);
            bitmapTemp.recycle();

            //xiabaiFront
            bitmapTemp = Bitmap.createBitmap(3179, 892, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_xiabai_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiabaiFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiabai, height_xiabai, true);
            canvasCombine.drawBitmap(bitmapTemp, x_xiabai_front, y_xiabai_front, null);

            //xiabaiBack
            bitmapTemp = Bitmap.createBitmap(3179, 892, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_xiabai_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiabaiBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiabai, height_xiabai, true);
            canvasCombine.drawBitmap(bitmapTemp, x_xiabai_back, y_xiabai_back, null);

            //xiukouL
            bitmapTemp = Bitmap.createBitmap(1473, 892, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, x_xiukou_l, y_xiukou_l, null);

            //xiukouR
            bitmapTemp = Bitmap.createBitmap(1473, 892, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(7), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, x_xiukou_r, y_xiukou_r, null);

            //pocket
            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 382 - (width_cut - 2498) / 2, 2516, width_cut, height_cut);
            bitmapCut = Bitmap.createScaledBitmap(bitmapCut, 2498, 1441, true);
            bitmapTemp = Bitmap.createBitmap(2498, 1441, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gs_pocket);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocket(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_pocket, y_pocket, null);
            bitmapDB.recycle();
            bitmapTemp.recycle();
        }



        try {
//            matrix.reset();
//            matrix.postRotate(-90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

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
        if (MainActivity.instance.tb_auto.isChecked())
            remix();
    }

    void setSize(String size) {
        switch (size) {
            case "XS":
                width_front = 2965;
                height_front = 4569;
                width_back = 2965;
                height_back = 4629;
                width_sleeve = 2174;
                height_sleeve = 3131;
                width_maozi = 1718;
                height_maozi = 2416;
                width_xiabai = 2884;
                height_xiabai = 892;
                width_pocket = 2379;
                height_pocket = 1383;
                width_xiukou = 1355;
                height_xiukou = 892;
                width_cut = 2616;
                height_cut = 1454;

                x_front = 0;
                y_front = 0;
                x_back = 3077;
                y_back = 0;
                x_sleeve_l = 2340;
                y_sleeve_l = 4739;
                x_sleeve_r =0;
                y_sleeve_r = 4731;
                x_maoli_r =6163;
                y_maoli_r =0;
                x_maoli_l =6163;
                y_maoli_l =2416;
                x_maomian_r =7432;
                y_maomian_r =5047;
                x_maomian_l =5520;
                y_maomian_l =5047;
                x_xiabai_front =0;
                y_xiabai_front =7976;
//                x_xiabai_back =3072;
                x_xiabai_back =2886;
                y_xiabai_back =7976;
                x_xiukou_l =7961;
                y_xiukou_l =2202;
                x_xiukou_r =7961;
                y_xiukou_r =3289;
                x_pocket =6296;
                y_pocket =7463;

                width_combine = 9316;
                height_combine = 8868;
                break;
            case "S":
                width_front = 3113;
                height_front = 4686;
                width_back = 3113;
                height_back = 4747;
                width_sleeve = 2292;
                height_sleeve = 3220;
                width_maozi = 1757;
                height_maozi = 2464;
                width_xiabai = 3031;
                height_xiabai = 892;
                width_pocket = 2498;
                height_pocket = 1441;
                width_xiukou = 1414;
                height_xiukou = 892;
                width_cut = 2616;
                height_cut = 1477;

                x_front = 0;
                y_front = 0;
                x_back = 0;
                y_back = 4830;
                x_sleeve_l = 5759;
                y_sleeve_l = 0;
                x_sleeve_r =3173;
                y_sleeve_r = 0;
                x_maoli_r =3261;
                y_maoli_r =3361;
                x_maoli_l =5169;
                y_maoli_l =3370;
                x_maomian_r =7481;
                y_maomian_r =5834;
                x_maomian_l =7481;
                y_maomian_l =3370;
                x_xiabai_front =3261;
                y_xiabai_front =7631;
                x_xiabai_back =3261;
                y_xiabai_back =8685;
                x_xiukou_l =5907;
                y_xiukou_l =6499;
                x_xiukou_r =6482;
                y_xiukou_r =8685;
                x_pocket =3261;
                y_pocket =5891;

                width_combine = 9238;
                height_combine = 9577;
                break;
            case "M":
                width_front = 3261;
                height_front = 4804;
                width_back = 3261;
                height_back = 4865;
                width_sleeve = 2412;
                height_sleeve = 3309;
                width_maozi = 1806;
                height_maozi = 2475;
                width_xiabai = 3179;
                height_xiabai = 892;
                width_pocket = 2498;
                height_pocket = 1441;
                width_xiukou = 1473;
                height_xiukou = 892;
                width_cut = 2498;
                height_cut = 1441;

                x_front = 0;
                y_front = 0;
                x_back = 0;
                y_back = 4896;
                x_sleeve_l = 6844;
                y_sleeve_l = 2988;
                x_sleeve_r =6844;
                y_sleeve_r = 6431;
                x_maoli_r =3425;
                y_maoli_r =0;
                x_maoli_l =5406;
                y_maoli_l =0;
                x_maomian_r =7351;
                y_maomian_r =0;
                x_maomian_l =3425;
                y_maomian_l =2540;
                x_xiabai_front =3505;
                y_xiabai_front =6736;
                x_xiabai_back =3505;
                y_xiabai_back =7775;
                x_xiukou_l =3505;
                y_xiukou_l =8848;
                x_xiukou_r =5211;
                y_xiukou_r =8848;
                x_pocket =4241;
                y_pocket =5098;

                width_combine = 9256;
                height_combine = 9760;
                break;
            case "L":
                width_front = 3408;
                height_front = 4921;
                width_back = 3408;
                height_back = 4982;
                width_sleeve = 2531;
                height_sleeve = 3398;
                width_maozi = 1858;
                height_maozi = 2523;
                width_xiabai = 3327;
                height_xiabai = 892;
                width_pocket = 2617;
                height_pocket = 1498;
                width_xiukou = 1532;
                height_xiukou = 892;
                width_cut = 2504;
                height_cut = 1462;

                x_front=0;
                y_front = 0;
                x_back = 3569;
                y_back = 0;
                x_sleeve_l = 2700;
                y_sleeve_l = 6997;
                x_sleeve_r =0;
                y_sleeve_r = 6981;
                x_maoli_r =7104;
                y_maoli_r =0;
                x_maoli_l =7104;
                y_maoli_l =2534;
                x_maomian_r =7104;
                y_maomian_r =7586;
                x_maomian_l =7104;
                y_maomian_l =5068;
                x_xiabai_front =0;
                y_xiabai_front =5022;
                x_xiabai_back =0;
                y_xiabai_back =6005;
                x_xiukou_l =5355;
                y_xiukou_l =8260;
                x_xiukou_r =5355;
                y_xiukou_r =9295;
                x_pocket =3569;
                y_pocket =5151;

                width_combine = 8963;
                height_combine = 10379;
                break;
            case "XL":
                width_front = 3615;
                height_front = 5038;
                width_back = 3615;
                height_back = 5100;
                width_sleeve = 2679;
                height_sleeve = 3488;
                width_maozi = 1926;
                height_maozi = 2641;
                width_xiabai = 3533;
                height_xiabai = 892;
                width_pocket = 2617;
                height_pocket = 1498;
                width_xiukou = 1591;
                height_xiukou = 892;
                width_cut = 2360;
                height_cut = 1428;

                x_front=0;
                y_front = 0;
                x_back = 0;
                y_back = 5142;
                x_sleeve_l = 6493;
                y_sleeve_l = 0;
                x_sleeve_r =3675;
                y_sleeve_r = 0;
                x_maoli_r =3822;
                y_maoli_r =3607;
                x_maoli_l =5907;
                y_maoli_l =3607;
                x_maomian_r =5907;
                y_maomian_r =6340;
                x_maomian_l =3822;
                y_maomian_l =6340;
                x_xiabai_front =0;
                y_xiabai_front =10345;
//                x_xiabai_back =3675;
                x_xiabai_back =3535;
                y_xiabai_back =10345;
                x_xiukou_l =7500;
                y_xiukou_l =9332;
                x_xiukou_r =7500;
                y_xiukou_r =10345;
                x_pocket =4591;
                y_pocket =8712;

                width_combine = 9172;
                height_combine = 11237;
                break;
            case "2XL":
                width_front = 3822;
                height_front = 5156;
                width_back = 3822;
                height_back = 5218;
                width_sleeve = 2829;
                height_sleeve = 3577;
                width_maozi = 1985;
                height_maozi = 2711;
                width_xiabai = 3740;
                height_xiabai = 892;
                width_pocket = 2617;
                height_pocket = 1498;
                width_xiukou = 1652;
                height_xiukou = 892;
                width_cut = 2232;
                height_cut = 1395;

                x_front=0;
                y_front = 0;
                x_back = 0;
                y_back = 5248;
                x_sleeve_l = 4099;
                y_sleeve_l = 0;
                x_sleeve_r =4099;
                y_sleeve_r = 3828;
                x_maoli_r =7391;
                y_maoli_r =0;
                x_maoli_l =7391;
                y_maoli_l =2700;
                x_maomian_r =7391;
                y_maomian_r =8100;
                x_maomian_l =7391;
                y_maomian_l =5400;
                x_xiabai_front =92;
                y_xiabai_front =10583;
//                x_xiabai_back =3990;
                x_xiabai_back =3834;
                y_xiabai_back =10583;
                x_xiukou_l =3911;
                y_xiukou_l =9450;
                x_xiukou_r =5655;
                y_xiukou_r =9450;
                x_pocket =4205;
                y_pocket =7653;

                width_combine = 9376;
                height_combine = 11475;
                break;
            case "3XL":
                width_front = 3969;
                height_front = 5273;
                width_back = 3969;
                height_back = 5336;
                width_sleeve = 2947;
                height_sleeve = 3666;
                width_maozi = 2044;
                height_maozi = 2759;
                width_xiabai = 3888;
                height_xiabai = 892;
                width_pocket = 2617;
                height_pocket = 1498;
                width_xiukou = 1710;
                height_xiukou = 892;
                width_cut = 2150;
                height_cut = 1364;

                x_front=0;
                y_front = 0;
                x_back = 0;
                y_back = 5359;
                x_sleeve_l = 4108;
                y_sleeve_l = 0;
                x_sleeve_r =4089;
                y_sleeve_r = 6930;
                x_maoli_r =7298;
                y_maoli_r =0;
                x_maoli_l =7298;
                y_maoli_l =2759;
                x_maomian_r =7298;
                y_maomian_r =8277;
                x_maomian_l =7298;
                y_maomian_l =5518;
                x_xiabai_front =0;
                y_xiabai_front =10801;
//                x_xiabai_back =4008;
                x_xiabai_back =3890;
                y_xiabai_back =10801;
                x_xiukou_l =3720;
                y_xiukou_l =5864;
                x_xiukou_r =5543;
                y_xiukou_r =5864;
                x_pocket =4147;
                y_pocket =3823;

                width_combine = 9342;
                height_combine = 11693;
                break;
            case "4XL":
                width_front = 4117;
                height_front = 5391;
                width_back = 4117;
                height_back = 5454;
                width_sleeve = 3066;
                height_sleeve = 3756;
                width_maozi = 2104;
                height_maozi = 2818;
                width_xiabai = 4035;
                height_xiabai = 892;
                width_pocket = 2617;
                height_pocket = 1498;
                width_xiukou = 1769;
                height_xiukou = 892;
                width_cut = 2072;
                height_cut = 1334;

                x_front=0;
                y_front = 0;
                x_back = 0;
                y_back = 5561;
                x_sleeve_l = 4117;
                y_sleeve_l = 0;
                x_sleeve_r =4117;
                y_sleeve_r = 5478;
                x_maoli_r =7183;
                y_maoli_r =0;
                x_maoli_l =7183;
                y_maoli_l =2830;
                x_maomian_r =7183;
                y_maomian_r =8490;
                x_maomian_l =7183;
                y_maomian_l =5660;
                x_xiabai_front =0;
                y_xiabai_front =11320;
//                x_xiabai_back =4196;
                x_xiabai_back =4037;
                y_xiabai_back =11320;
                x_xiukou_l =4750;
                y_xiukou_l =9340;
                x_xiukou_r =4750;
                y_xiukou_r =10319;
                x_pocket =4326;
                y_pocket =3876;

                width_combine = 9278;
                height_combine = 12212;
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }
        width_front += 50;
        height_front += 50;
        width_back += 50;
        height_back += 50;
        width_sleeve += 40;
        height_sleeve += 40;
        width_maozi += 40;
        height_maozi += 40;
        width_xiabai += 40;
        height_xiabai += 40;
        width_pocket += 50;
        height_pocket += 50;
        width_xiukou += 30;
        height_xiukou += 30;

        width_combine += 40;
        height_combine += 50;
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
