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

public class FragmentGRW extends BaseFragment {
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

    int width_front, width_back, width_arm, width_maozi, width_xiabai, width_pocket, width_xiukou;
    int height_front,height_back,height_arm,height_maozi,height_xiabai,height_pocket,height_xiukou;

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
                setScale(orderItems.get(currentID).sizeStr);

                if (sizeOK) {
                    for(num=orderItems.get(currentID).num;num>=1;num--) {
                        for(int i=0;i<currentID;i++) {
                            if (orderItems.get(currentID).order_number.equals(orderItems.get(i).order_number)) {
                                intPlus += 1;
                            }
                        }
                        strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                        remixx();
                        intPlus += 1;
                    }
                }

            }
        }.start();

    }

    void drawTextFrontR(Canvas canvas) {
        canvas.drawRect(1000, 4159, 1000 + 700, 4159 + 25, rectPaint);
        canvas.drawText("正面右 " + time + "  " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode, 1000, 4159 + 23, paint);
        canvas.drawText(currentID + "", 1440, 4159 + 23, paintRed);
    }
    void drawTextFrontL(Canvas canvas) {
        canvas.drawRect(1000, 4159, 1000 + 700, 4159 + 25, rectPaint);
        canvas.drawText("正面左 " + time + "  " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode, 1000, 4159 + 23, paint);
        canvas.drawText(currentID + "", 1440, 4159 + 23, paintRed);
    }
    void drawTextBack(Canvas canvas) {
        canvas.drawRect(1000, 4222, 1000 + 700, 4222 + 25, rectPaint);
        canvas.drawText("背面 " + time + "  " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode, 1000, 4222 + 23, paint);
        canvas.drawText(currentID + "", 1440, 4222 + 23, paintRed);
    }
    void drawTextXiuziL(Canvas canvas) {
        canvas.drawRect(1438, 13, 1438 + 130, 13 + 25, rectPaint);
        canvas.drawText("左 " + orderItems.get(currentID).sizeStr, 1438, 13 + 23, paint);
        canvas.drawText(currentID + "", 1520, 13 + 23, paintRed);

        canvas.drawRect(1000, 3777-25, 1000 + 300, 3777, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 1000, 3777 - 2, paint);
    }
    void drawTextXiuziR(Canvas canvas) {
        canvas.drawRect(1335, 13, 1335 + 130, 13 + 25, rectPaint);
        canvas.drawText("右 " + orderItems.get(currentID).sizeStr, 1335, 13 + 23, paint);
        canvas.drawText(currentID + "", 1415, 13 + 23, paintRed);

        canvas.drawRect(1000, 3777-25, 1000 + 300, 3777, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 1000, 3777 - 2, paint);
    }
    void drawTextMaoziInL(Canvas canvas) {
        canvas.drawRect(1130, 7, 1130 + 180, 7 + 25, rectPaint);
        canvas.drawText("内左 " + orderItems.get(currentID).sizeStr, 1130, 7 + 23, paint);
        canvas.drawText(currentID + "", 1230, 7 + 23, paintRed);

        canvas.save();
        canvas.rotate(5.1f, 1582, 22);
        canvas.drawRect(1582, 22, 1582 + 300, 22 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 1582, 22 + 23, paint);
        canvas.restore();
    }
    void drawTextMaoziInR(Canvas canvas) {
        canvas.drawRect(650, 7, 650 + 180, 7 + 25, rectPaint);
        canvas.drawText("内右 " + orderItems.get(currentID).sizeStr, 650, 7 + 23, paint);
        canvas.drawText(currentID + "", 750, 7 + 23, paintRed);

        canvas.save();
        canvas.rotate(-5.1f, 58, 48);
        canvas.drawRect(58, 48, 58 + 300, 48 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 58, 48 + 23, paint);
        canvas.restore();
    }
    void drawTextMaoziOutL(Canvas canvas) {
        canvas.drawRect(920, 8, 920 + 100, 8 + 25, rectPaint);
        canvas.drawText("外左", 920, 8 + 23, paint);
        canvas.drawText(currentID + "", 970, 8 + 23, paintRed);

        canvas.save();
        canvas.rotate(-5.2f, 64, 58);
        canvas.drawRect(64, 58, 64 + 300, 58 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 64, 58 + 23, paint);
        canvas.restore();
    }
    void drawTextMaoziOutR(Canvas canvas) {
        canvas.drawRect(920, 8, 920 + 100, 8 + 25, rectPaint);
        canvas.drawText("外右", 920, 8 + 23, paint);
        canvas.drawText(currentID + "", 970, 8 + 23, paintRed);

        canvas.save();
        canvas.rotate(5f, 1551, 30);
        canvas.drawRect(1551, 30, 1551 + 300, 30 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 1551, 30 + 23, paint);
        canvas.restore();
    }
    void drawTextPocket(Canvas canvas) {
        canvas.drawRect(800, 90, 800 + 200, 90 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr, 800, 90 + 23, paint);
        canvas.drawText(currentID + "", 900, 90 + 23, paintRed);

        canvas.drawRect(1050, 90, 1050 + 300, 90 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 1050, 90 + 23, paint);
    }
    void drawTextXiabai(Canvas canvas) {
        canvas.drawRect(3900, 7, 3900 + 200, 7 + 25, rectPaint);
        canvas.drawText("下摆 " + orderItems.get(currentID).sizeStr, 3900, 7 + 23, paint);
        canvas.drawText(currentID + "", 4000, 7 + 23, paintRed);

        canvas.drawRect(4200, 7, 4200 + 300, 7 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 4200, 7 + 23, paint);
    }
    void drawTextXiukouL(Canvas canvas) {
        canvas.drawRect(760, 8, 760 + 160, 8 + 25, rectPaint);
        canvas.drawText("左 " + orderItems.get(currentID).sizeStr, 760, 8 + 23, paint);
        canvas.drawText(currentID + "", 860, 8 + 23, paintRed);

        canvas.drawRect(1000, 8, 1000 + 300, 8 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 1000, 8 + 23, paint);
    }
    void drawTextXiukouR(Canvas canvas) {
        canvas.drawRect(760, 8, 760 + 160, 8 + 25, rectPaint);
        canvas.drawText("右 " + orderItems.get(currentID).sizeStr, 760, 8 + 23, paint);
        canvas.drawText(currentID + "", 860, 8 + 23, paintRed);

        canvas.drawRect(1000, 8, 1000 + 300, 8 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 1000, 8 + 23, paint);
    }

    public void remixx(){
        int margin = 130;
        Matrix matrix = new Matrix();
        int widthMargin = Math.max(width_front, width_arm + height_xiabai + 100);

        Bitmap bitmapCombine = Bitmap.createBitmap(width_arm + width_maozi + margin, height_front + height_back + height_maozi * 4 + margin * 3, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 8 & orderItems.get(currentID).platform.equals("4u2")) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 30, 0, 2072, 4193);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 2072 - 30, 0, 2072, 4193);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //back
            bitmapTemp = (MainActivity.instance.bitmaps.get(0)).copy(Bitmap.Config.ARGB_8888, true);
            MainActivity.instance.bitmaps.get(0).recycle();
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //arm_l
            bitmapTemp = (MainActivity.instance.bitmaps.get(5)).copy(Bitmap.Config.ARGB_8888, true);
            MainActivity.instance.bitmaps.get(5).recycle();
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + margin * 2, null);

            //arm_r
            bitmapTemp = (MainActivity.instance.bitmaps.get(7)).copy(Bitmap.Config.ARGB_8888, true);
            MainActivity.instance.bitmaps.get(7).recycle();
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm + margin * 3, null);

            //pocket
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 673 + 30, 2616, 2794 / 2, 1576);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_pocket_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocket(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + margin * 4, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 2072 - 30, 2616, 2794 / 2, 1576);
            MainActivity.instance.bitmaps.get(2).recycle();
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_pocket_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocket(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket + margin, height_back + height_front + height_arm * 2 + margin * 4, null);

            //maozi_out_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, 1924, 2366);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_out_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + margin * 2, null);

            //maozi_out_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 1921, 0, 1924, 2366);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_out_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi + margin * 3, null);

            //maozi_in_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, 1924, 2366);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_in_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi * 2 + margin * 3, null);

            //maozi_in_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 1921, 0, 1924, 2366);
            MainActivity.instance.bitmaps.get(3).recycle();
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_in_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi * 3 + margin * 3, null);

            //xiabai
            bitmapTemp = (MainActivity.instance.bitmaps.get(1)).copy(Bitmap.Config.ARGB_8888, true);
            MainActivity.instance.bitmaps.get(1).recycle();
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiabai);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiabai(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiabai, height_xiabai, true);
            matrix.reset();
            matrix.postRotate(-90);
            matrix.postTranslate(width_front * 2 + margin * 2, width_xiabai);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //xiukou_l
            bitmapTemp = (MainActivity.instance.bitmaps.get(4)).copy(Bitmap.Config.ARGB_8888, true);
            MainActivity.instance.bitmaps.get(4).recycle();
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);

            //xiukou_r
            bitmapTemp = (MainActivity.instance.bitmaps.get(6)).copy(Bitmap.Config.ARGB_8888, true);
            MainActivity.instance.bitmaps.get(6).recycle();
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiukouR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiukou + margin, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);
            bitmapTemp.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 8) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap((checkContains("front") ? getBitmapWith("front") : MainActivity.instance.bitmaps.get(0)), 30, 0, 2072, 4193);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap((checkContains("front") ? getBitmapWith("front") : MainActivity.instance.bitmaps.get(0)), 2072 - 30, 0, 2072, 4193);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //back
            bitmapTemp = (checkContains("back") ? getBitmapWith("back") : MainActivity.instance.bitmaps.get(1)).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //arm_l
            bitmapTemp = (checkContains("left_sleeve") ? getBitmapWith("left_sleeve") : MainActivity.instance.bitmaps.get(2)).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + margin * 2, null);


            //arm_r
            bitmapTemp = (checkContains("right_sleeve") ? getBitmapWith("right_sleeve") : MainActivity.instance.bitmaps.get(3)).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm + margin * 3, null);

            //pocket
            bitmapTemp = Bitmap.createBitmap(checkContains("front") ? getBitmapWith("front") : MainActivity.instance.bitmaps.get(0), 673 + 30, 2616, 2794 / 2, 1576);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_pocket_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocket(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + margin * 4, null);

            bitmapTemp = Bitmap.createBitmap(checkContains("front") ? getBitmapWith("front") : MainActivity.instance.bitmaps.get(0), 2072 - 30, 2616, 2794 / 2, 1576);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_pocket_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocket(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket + margin, height_back + height_front + height_arm * 2 + margin * 4, null);

            //maozi_out_l
            bitmapTemp = Bitmap.createBitmap(checkContains("hat") ? getBitmapWith("hat") : MainActivity.instance.bitmaps.get(7), 0, 0, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_out_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + margin * 2, null);

            //maozi_out_r
            bitmapTemp = Bitmap.createBitmap(checkContains("hat") ? getBitmapWith("hat") : MainActivity.instance.bitmaps.get(7), 1928, 0, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_out_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi + margin * 3, null);

            //maozi_in_r
            bitmapTemp = Bitmap.createBitmap(checkContains("hat") ? getBitmapWith("hat") : MainActivity.instance.bitmaps.get(7), 0, 0, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_in_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi * 2 + margin * 3, null);

            //maozi_in_l
            bitmapTemp = Bitmap.createBitmap(checkContains("hat") ? getBitmapWith("hat") : MainActivity.instance.bitmaps.get(7), 1928, 0, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_in_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi * 3 + margin * 3, null);

            //xiabai
            bitmapTemp = (checkContains("bottom") ? getBitmapWith("bottom") : MainActivity.instance.bitmaps.get(6)).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiabai);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiabai(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiabai, height_xiabai, true);
            matrix.reset();
            matrix.postRotate(-90);
            matrix.postTranslate(width_front + margin, width_xiabai);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //xiukou_l
            bitmapTemp = (checkContains("left_cuff") ? getBitmapWith("left_cuff") : MainActivity.instance.bitmaps.get(4)).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);

            //xiukou_r
            bitmapTemp = (checkContains("right_cuff") ? getBitmapWith("right_cuff") : MainActivity.instance.bitmaps.get(5)).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiukouR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiukou + margin, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2903 + 30, 2134, 2072, 4193);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4975 - 30, 2134, 2072, 4193);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2904, 2074, 4143, 4253);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //arm_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7047, 2600, 2903, 3784);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + margin * 2, null);


            //arm_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 2576, 2903, 3784);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm + margin * 3, null);

            //pocket
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3574 + 30, 4751, 2794 / 2, 1576);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_pocket_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocket(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + margin * 4, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4975 - 30, 4751, 2794 / 2, 1576);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_pocket_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocket(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket + margin, height_back + height_front + height_arm * 2 + margin * 4, null);

            //maozi_out_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3047, 0, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_out_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + margin * 2, null);

            //maozi_out_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4982, 0, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_out_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi + margin * 3, null);

            //maozi_in_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3047, 0, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_in_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi * 2 + margin * 3, null);

            //maozi_in_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4982, 0, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_in_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi * 3 + margin * 3, null);

            //xiabai
            Bitmap bitmapArm = Bitmap.createBitmap(3864 * 2, 675, Bitmap.Config.ARGB_8888);
            Canvas canvasArm = new Canvas(bitmapArm);
            canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasArm.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4975 - 30, 6202, 3864 / 2, 675);
            canvasArm.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3035, 6202, 3864, 675);
            canvasArm.drawBitmap(bitmapTemp, 3864 / 2, 0, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3035 + 30, 6202, 3864 / 2, 675);
            canvasArm.drawBitmap(bitmapTemp, 3864 / 2 + 3864, 0, null);
            bitmapTemp.recycle();

            drawTextXiabai(canvasArm);
            bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_xiabai, height_xiabai, true);

            matrix.reset();
            matrix.postRotate(-90);
            matrix.postTranslate(width_front * 2 + margin * 2, width_xiabai);
            canvasCombine.drawBitmap(bitmapArm, matrix, null);
            bitmapArm.recycle();

            //xiukou_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7685, 6258, 1628, 619);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);

            //xiukou_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 634, 6258, 1628, 619);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiukouR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiukou + margin, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);
            bitmapTemp.recycle();
        }



        String nameCombine = orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";
        String pathSave;
        if(MainActivity.instance.cb_classify.isChecked()){
            pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
        } else
            pathSave = sdCardPath + "/生产图/" + childPath + "/";
        if(!new File(pathSave).exists())
            new File(pathSave).mkdirs();
        File fileSave = new File(pathSave + nameCombine);
        BitmapToJpg.save(bitmapCombine, fileSave, 150);
        bitmapCombine.recycle();


        try {
//            matrix.reset();
//            matrix.postRotate(-90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);


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

    void setScale(String size) {
        switch (size) {
            case "XS":
                width_front = 1610;
                height_front = 3755;
                width_back = 3175;
                height_back = 3813;
                width_arm = 2533;
                height_arm = 3343;
                width_maozi = 1593;
                height_maozi = 2386;
                width_xiabai = 5358;
                height_xiabai = 835;
                width_pocket = 1080;
                height_pocket = 1408;
                width_xiukou = 1233;
                height_xiukou = 834;
                break;
            case "S":
                width_front = 3354 / 2;
                height_front = 3872;
                width_back = 3354;
                height_back = 3932;
                width_arm = 2664;
                height_arm = 3462;
                width_maozi = 1709;
                height_maozi = 2429;
                width_xiabai = 5764 - 40;
                height_xiabai = 834;
                width_pocket = 2255 / 2;
                height_pocket = 1457;
                width_xiukou = 1292;
                height_xiukou = 834;
                break;
            case "M":
                width_front = 3530 / 2;
                height_front = 3990;
                width_back = 3530;
                height_back = 4050;
                width_arm = 2794;
                height_arm = 3580;
                width_maozi = 1802;
                height_maozi = 2499;
                width_xiabai = 6118 - 40;
                height_xiabai = 834;
                width_pocket = 2376 / 2;
                height_pocket = 1501;
                width_xiukou = 1352;
                height_xiukou = 834;
                break;
            case "L":
                width_front = 3708 / 2;
                height_front = 4106;
                width_back = 3708;
                height_back = 4167;
                width_arm = 2926;
                height_arm = 3699;
                width_maozi = 1861;
                height_maozi = 2558;
                width_xiabai = 6473 - 40;
                height_xiabai = 834;
                width_pocket = 2498 / 2;
                height_pocket = 1544;
                width_xiukou = 1411;
                height_xiukou = 834;
                break;
            case "XL":
                width_front = 3885 / 2;
                height_front = 4225;
                width_back = 3885;
                height_back = 4285;
                width_arm = 3056;
                height_arm = 3818;
                width_maozi = 1894;
                height_maozi = 2607;
                width_xiabai = 6827 - 40;
                height_xiabai = 834;
                width_pocket = 2619 / 2;
                height_pocket = 1588;
                width_xiukou = 1470;
                height_xiukou = 834;
                break;
            case "2XL":
                width_front = 4061 / 2;
                height_front = 4342;
                width_back = 4061;
                height_back = 4403;
                width_arm = 3187;
                height_arm = 3935;
                width_maozi = 1956;
                height_maozi = 2666;
                width_xiabai = 7182 - 40;
                height_xiabai = 834;
                width_pocket = 2739 / 2;
                height_pocket = 1631;
                width_xiukou = 1529;
                height_xiukou = 834;
                break;
            case "3XL":
                width_front = 4238 / 2;
                height_front = 4459;
                width_back = 4238;
                height_back = 4521;
                width_arm = 3317;
                height_arm = 4054;
                width_maozi = 2038;
                height_maozi = 2736;
                width_xiabai = 7536 - 40;
                height_xiabai = 834;
                width_pocket = 2860 / 2;
                height_pocket = 1675;
                width_xiukou = 1588;
                height_xiukou = 834;
                break;
            case "4XL":
                width_front = 4415 / 2;
                height_front = 4576;
                width_back = 4415;
                height_back = 4638;
                width_arm = 3448;
                height_arm = 4172;
                width_maozi = 2098;
                height_maozi = 2795;
                width_xiabai = 7891 - 40;
                height_xiabai = 834;
                width_pocket = 2981 / 2;
                height_pocket = 1719;
                width_xiukou = 1647;
                height_xiukou = 834;
                break;
            case "5XL":
                width_front = 4591 / 2;
                height_front = 4576;
                width_back = 4591;
                height_back = 4637;
                width_arm = 3479;
                height_arm = 4172;
                width_maozi = 2165;
                height_maozi = 2853;
                width_xiabai = 8244 - 40;
                height_xiabai = 834;
                width_pocket = 3096 / 2;
                height_pocket = 1721;
                width_xiukou = 1705;
                height_xiukou = 834;
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }
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
                tv_content.setText("单号："+order_number+"读取尺码失败");
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

    boolean checkContains(String nameContains){
        for (int i = 0; i < orderItems.get(currentID).imgs.size(); i++) {
            if (orderItems.get(currentID).imgs.get(i).contains(nameContains)) {
                return true;
            }
        }
        return false;
    }
    Bitmap getBitmapWith(String nameContains){
        for (int i = 0; i < orderItems.get(currentID).imgs.size(); i++) {
            if (orderItems.get(currentID).imgs.get(i).contains(nameContains)) {
                return MainActivity.instance.bitmaps.get(i);
            }
        }
        return null;
    }
}
