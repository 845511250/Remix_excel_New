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

public class FragmentGA extends BaseFragment {
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

    int width_front, width_back, width_arm, width_belt, width_collarBack, width_collar, width_loop, width_loopTop, width_pocket;
    int height_front,height_back,height_arm,height_belt,height_collarBack,height_collar,height_loop,height_loopTop,height_pocket;

    int num;
    String strPlus = "";
    int intPlus = 1;

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

                for(num=orderItems.get(currentID).num;num>=1;num--) {
                    intPlus = orderItems.get(currentID).num - num + 1;
                    for(int i=0;i<currentID;i++) {
                        if (orderItems.get(currentID).order_number.equals(orderItems.get(i).order_number)) {
                            intPlus += orderItems.get(i).num;;
                        }
                    }
                    strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                    remixx100();
                }

            }
        }.start();
    }

    void drawTextFrontL(Canvas canvas) {
        canvas.drawRect(100, 4445 - 25, 100 + 500, 4445, rectPaint);
        canvas.drawText(time + "  GA女浴袍_" + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 100, 4445 - 2, paint);
    }
    void drawTextFrontR(Canvas canvas) {
        canvas.drawRect(1300, 4445 - 25, 1300 + 500, 4445, rectPaint);
        canvas.drawText(time + "  GA女浴袍_" + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 1300, 4445 - 2, paint);
    }
    void drawTextBack(Canvas canvas) {
        canvas.drawRect(1300, 4394 - 25, 1300 + 500, 4394, rectPaint);
        canvas.drawText(time + "  GA女浴袍_" + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 1300, 4394 - 2, paint);
    }
    void drawTextArmL(Canvas canvas) {
        canvas.drawRect(1122, 12, 1122 + 40, 12 + 25, rectPaint);
        canvas.drawText(" 左", 1122, 12 + 23, paint);

        canvas.drawRect(1000, 2472 - 25, 1000 + 500, 2472, rectPaint);
        canvas.drawText("左" + "  GA女浴袍_  " + time + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 1000, 2472 - 2, paint);
    }
    void drawTextArmR(Canvas canvas) {
        canvas.drawRect(1122, 12, 1122 + 40, 12 + 25, rectPaint);
        canvas.drawText(" 右", 1122, 12 + 23, paint);

        canvas.drawRect(1000, 2472 - 25, 1000 + 500, 2472, rectPaint);
        canvas.drawText("右" + "  GA女浴袍_  " + time + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 1000, 2472 - 2, paint);
    }
    void drawTextCollarBack(Canvas canvas) {
        canvas.drawRect(1400, 9, 1400 + 100, 9 + 25, rectPaint);
        canvas.drawText(" 后领", 1400, 9 + 23, paint);
    }
    void drawTextPocketL(Canvas canvas) {
        canvas.drawRect(360, 9, 360 + 300, 9 + 25, rectPaint);
        canvas.drawText(" 左 " + orderItems.get(currentID).order_number, 360, 9 + 23, paint);
    }
    void drawTextPocketR(Canvas canvas) {
        canvas.drawRect(360, 9, 360 + 300, 9 + 25, rectPaint);
        canvas.drawText(" 右 " + orderItems.get(currentID).order_number, 360, 9 + 23, paint);
    }

    public void remixx100(){
        Bitmap bitmapCombine = Bitmap.createBitmap(4523, 16500, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);


        if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {//jj
            if(MainActivity.instance.bitmaps.get(0).getWidth() == 4000){
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 10526, 10526, true));
            }
            //腰带
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 709, 1135, 8572, 521);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_belt);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            matrix.postTranslate(521 + 4002, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 735, 4989, 3255, 4400);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //frontR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6052, 1932, 1883, 4451);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 4599, null);

            //frontL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7935, 1932, 1882, 4451);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 1995, 4599, null);

            //loopTop
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7603, 2087, 666, 222);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_loop_top);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, 982, 15159, null);

            //collarL
            matrix.reset();
            matrix.setRotate(-90);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 806, 3087, 5072, 1008, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_collar_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(1008, 5072 + 9280);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //collarR
            matrix.reset();
            matrix.setRotate(-90);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 806, 1939, 5072, 1008, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_collar_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, 1095, 9855, null);

            //armR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4587, 6914, 2209, 2478);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_arm_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmR(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2201, 11976, null);

            //armL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7608, 6914, 2209, 2478);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_arm_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmL(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2201, 9271, null);

            //pocketL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4687, 4456, 808, 973);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_pocket);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketL(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2621, 14642, null);

            //pocketR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4687, 5536, 808, 973);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_pocket);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketR(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 3695, 14672, null);

            //collarBack
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 969, 4120, 2782, 668);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_collar_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextCollarBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 15645, null);

            //loopL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 9830, 3786, 222, 571);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_loop);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, 0, 14753, null);

            //loopR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5824, 3786, 222, 571);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_loop);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, 436, 14753, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //腰带
            Bitmap bitmapBelt = Bitmap.createBitmap(8572, 521, Bitmap.Config.ARGB_8888);
            Canvas canvasBelt = new Canvas(bitmapBelt);
            canvasBelt.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasBelt.drawColor(0xffffffff);

            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2206, 2539, 4286, 521);
            canvasBelt.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1273, 2539, 4286, 521);
            canvasBelt.drawBitmap(bitmapTemp, 4285, 0, null);
            bitmapTemp.recycle();

            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_belt);
            canvasBelt.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            matrix.postTranslate(521 + 4002, 0);
            canvasCombine.drawBitmap(bitmapBelt, matrix, null);
            bitmapBelt.recycle();

            //back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2273, 678, 3255, 4400);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp.recycle();

            //frontR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2273, 678, 1883, 4451);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextFrontR(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 4599, null);
            bitmapTemp.recycle();

            //frontL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3646, 678, 1882, 4451);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextFrontL(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 1995, 4599, null);
            bitmapTemp.recycle();

            //loopTop
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3567, 833, 666, 222);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_loop_top);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            canvasCombine.drawBitmap(bitmapTemp, 982, 15159, null);
            bitmapTemp.recycle();

            //collarL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3649, 49, 1008, 5072);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_collar_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();

            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(1008, 5072 + 9280);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();

            //collarR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3145, 49, 1008, 5072);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_collar_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            canvasCombine.drawBitmap(bitmapTemp, 1095, 9855, null);
            bitmapTemp.recycle();

            //armR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 108, 1561, 2209, 2478);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_arm_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextArmR(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2201, 11976, null);
            bitmapTemp.recycle();

            //armL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5486, 1561, 2209, 2478);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_arm_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextArmL(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2201, 9271, null);
            bitmapTemp.recycle();

            //pocketL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4475, 2990, 808, 973);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_pocket);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextPocketL(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2621, 14642, null);
            bitmapTemp.recycle();

            //pocketR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2516, 2990, 808, 973);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_pocket);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextPocketR(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 3695, 14672, null);
            bitmapTemp.recycle();

            //collarBack
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2509, 219, 2782, 668);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_collar_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextCollarBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 15645, null);
            bitmapTemp.recycle();

            //loopL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5337, 2532, 222, 571);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_loop);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            canvasCombine.drawBitmap(bitmapTemp, 0, 14753, null);
            bitmapTemp.recycle();

            //loopR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2206, 2532, 222, 571);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_loop);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            canvasCombine.drawBitmap(bitmapTemp, 436, 14753, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 4) {
            //腰带
            Bitmap bitmapBelt = Bitmap.createBitmap(8572, 521, Bitmap.Config.ARGB_8888);
            Canvas canvasBelt = new Canvas(bitmapBelt);
            canvasBelt.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasBelt.drawColor(0xffffffff);

            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 144, 2580, 3100, 377);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 4286, 521, true);
            canvasBelt.drawBitmap(bitmapTemp, 0, 0, null);
            canvasBelt.drawBitmap(bitmapTemp, 4285, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_belt);
            canvasBelt.drawBitmap(bitmapDB, 0, 0, null);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            matrix.postTranslate(521 + 4002, 0);
            canvasCombine.drawBitmap(bitmapBelt, matrix, null);
            bitmapBelt.recycle();

            //back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 459, 3255, 4400);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //frontR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 67, 629, 1883, 4451);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 4599, null);

            //frontL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1440, 629, 1882, 4451);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 1995, 4599, null);

            //loopTop
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1361, 784, 666, 222);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_loop_top);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, 982, 15159, null);

            //collarL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1443, 0, 1008, 5072);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_collar_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);

            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(1008, 5072 + 9280);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //collarR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 939, 0, 1008, 5072);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_collar_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, 1095, 9855, null);

            //armR
            bitmapTemp = Bitmap.createBitmap(2209, 2478, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_arm_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmR(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2201, 11976, null);

            //armL
            bitmapTemp = Bitmap.createBitmap(2209, 2478, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_arm_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextArmL(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2201, 9271, null);

            //pocketL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2269, 2941, 808, 973);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_pocket);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketL(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2621, 14642, null);

            //pocketR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 310, 2941, 808, 973);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_pocket);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketR(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 3695, 14672, null);

            //collarBack
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 236, 0, 2782, 668);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_collar_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextCollarBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 15645, null);

            //loopL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3131, 2483, 222, 571);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_loop);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            canvasCombine.drawBitmap(bitmapTemp, 0, 14753, null);

            //loopR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 2483, 222, 571);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ga_loop);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            canvasCombine.drawBitmap(bitmapTemp, 436, 14753, null);
            bitmapTemp.recycle();
        }


        try {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(-90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

            String nameCombine = "GA女浴袍" + orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 119);
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
        if (MainActivity.instance.tb_auto.isChecked()){
            remix();
        }
    }

    void setSize(String size) {
        width_front = 1957;
        height_front = 4573;
        width_back = 3353;
        height_back = 4522;
        width_arm = 2305;
        height_arm = 2623;
        width_belt = 8808;
        height_belt = 572;
        width_collarBack = 2811;
        height_collarBack = 694;
        width_collar = 1035;
        height_collar = 5202;
        width_loop = 222;
        height_loop = 571;
        width_loopTop = 666;
        height_loopTop = 222;
        width_pocket = 808;
        height_pocket = 973;

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
