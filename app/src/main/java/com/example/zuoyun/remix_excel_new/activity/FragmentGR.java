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

public class FragmentGR extends BaseFragment {
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
                if (orderItems.get(currentID).sku.endsWith("W")) {
                    setSizeGRW(orderItems.get(currentID).sizeStr);
                } else if (orderItems.get(currentID).sku.equals("GRY")) {
                    setSizeGRY(orderItems.get(currentID).sizeStr);
                } else {
                    setSizeGRM(orderItems.get(currentID).sizeStr);
                }

                if (sizeOK) {
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

            }
        }.start();

    }

    void drawTextFrontR(Canvas canvas) {
        canvas.drawRect(1000, 4159, 1000 + 800, 4159 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 1000, 4159 + 23, paint);
        canvas.drawText(currentID + "", 1440, 4159 + 23, paintRed);
    }
    void drawTextFrontL(Canvas canvas) {
        canvas.drawRect(1000, 4159, 1000 + 800, 4159 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 1000, 4159 + 23, paint);
        canvas.drawText(currentID + "", 1440, 4159 + 23, paintRed);
    }
    void drawTextBack(Canvas canvas) {
        canvas.drawRect(1000, 4222, 1000 + 800, 4222 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 1000, 4222 + 23, paint);
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
    void drawTextPocketL(Canvas canvas) {
        canvas.drawRect(100, 90, 100 + 200, 90 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr, 100, 90 + 23, paint);
        canvas.drawText(currentID + "", 200, 90 + 23, paintRed);

        canvas.drawRect(350, 90, 350 + 300, 90 + 25, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + "  " + orderItems.get(currentID).order_number, 350, 90 + 23, paint);
    }
    void drawTextPocketR(Canvas canvas) {
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
        int margin = 120;
        Matrix matrix = new Matrix();
        int widthCombine = Math.max(width_front * 2 + height_xiabai + margin * 2, width_arm + width_maozi + margin);
        widthCombine = Math.max(widthCombine, width_xiukou * 2 + width_maozi + margin * 2);

        Bitmap bitmapCombine = Bitmap.createBitmap(widthCombine, height_front + height_back + height_maozi * 4 + margin * 3, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 8) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(2072, 4193, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), -30, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(2072, 4193, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), -2072 + 30, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //back
            bitmapTemp = Bitmap.createBitmap(4143, 4253, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //arm_l
            bitmapTemp = Bitmap.createBitmap(2903, 3784, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + margin * 2, null);

            //arm_r
            bitmapTemp = Bitmap.createBitmap(2903, 3784, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(7), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm + margin * 3, null);

            //pocket
            bitmapTemp = Bitmap.createBitmap(1397, 1576, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), -673 - 30, -2616, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_pocket_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + margin * 4, null);

            bitmapTemp = Bitmap.createBitmap(1397, 1576, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), -2072 + 30, -2616, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_pocket_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket + margin, height_back + height_front + height_arm * 2 + margin * 4, null);

            //maozi_out_l
            bitmapTemp = Bitmap.createBitmap(1928, 2368, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_out_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + margin * 2, null);

            //maozi_out_r
            bitmapTemp = Bitmap.createBitmap(1928, 2368, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), -1922, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_out_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi + margin * 3, null);

            //maozi_in_r
            bitmapTemp = Bitmap.createBitmap(1928, 2368, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_in_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi * 2 + margin * 3, null);

            //maozi_in_l
            bitmapTemp = Bitmap.createBitmap(1928, 2368, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), -1922, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_in_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi * 3 + margin * 3, null);

            //xiabai
            if (MainActivity.instance.bitmaps.get(1).getWidth() != 7907) {
                MainActivity.instance.bitmaps.set(1, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(1), 7907, 860, true));
            }
            Bitmap bitmapArm = Bitmap.createBitmap(7897, 860, Bitmap.Config.ARGB_8888);
            Canvas canvasArm= new Canvas(bitmapArm);
            canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasArm.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 5950, 0, 1947, 860);
            canvasArm.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 10, 0, 3954, 860);
            canvasArm.drawBitmap(bitmapTemp, 1947, 0, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3954, 0, 1998, 860);
            canvasArm.drawBitmap(bitmapTemp, 1947 + 3954, 0, null);
            bitmapTemp.recycle();

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_bottom_7907);
            canvasArm.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiabai(canvasArm);
            bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_xiabai, height_xiabai, true);
            matrix.reset();
            matrix.postRotate(-90);
            matrix.postTranslate(width_front * 2 + margin * 2, width_xiabai);
            canvasCombine.drawBitmap(bitmapArm, matrix, null);
            bitmapArm.recycle();

            //xiukou_l
            bitmapTemp = Bitmap.createBitmap(1628, 619, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);

            //xiukou_r
            bitmapTemp = Bitmap.createBitmap(1628, 619, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(6), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiukouR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiukou + margin, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);
            bitmapTemp.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getHeight() == 15297) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3412 + 30, 2562 + 3698, 2072, 4193);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5482 - 30, 2562 + 3698, 2072, 4193);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //back
            bitmapTemp =  Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7717, 2504 + 3698, 4143, 4253);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //arm_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 12017, 3454 + 3698, 2903, 3784);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + margin * 2, null);

            //arm_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 382, 3454 + 3698, 2903, 3784);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm + margin * 3, null);

            //pocket
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4085 + 30, 5179 + 3698, 2794 / 2, 1576);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_pocket_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + margin * 4, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5482 - 30, 5179 + 3698, 2794/2, 1576);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_pocket_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket + margin, height_back + height_front + height_arm * 2 + margin * 4, null);

            //maozi_out_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7859, 0 + 3698, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_out_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + margin * 2, null);

            //maozi_out_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 9784, 0 + 3698, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_out_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi + margin * 3, null);

            //maozi_in_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3557, 0 + 3698, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_in_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi * 2 + margin * 3, null);

            //maozi_in_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5485, 0 + 3698, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_in_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi * 3 + margin * 3, null);

            //xiabai
            Bitmap bitmapArm = Bitmap.createBitmap(3954 * 2, 860, Bitmap.Config.ARGB_8888);
            Canvas canvasArm= new Canvas(bitmapArm);
            canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasArm.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5482 - 30, 10571, 3954 / 2, 860);
            canvasArm.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7808, 10571, 3954, 860);
            canvasArm.drawBitmap(bitmapTemp, 3954/2, 0, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3505 + 30, 10571, 3954 / 2, 860);
            canvasArm.drawBitmap(bitmapTemp, 3954 / 2 + 3954, 0, null);
            bitmapTemp.recycle();

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_bottom_7907);
            canvasArm.drawBitmap(bitmapDB, 0, 0, null);

            drawTextXiabai(canvasArm);
            bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_xiabai, height_xiabai, true);

            matrix.reset();
            matrix.postRotate(-90);
            matrix.postTranslate(width_front * 2 + margin * 2, width_xiabai);
            canvasCombine.drawBitmap(bitmapArm, matrix, null);
            bitmapArm.recycle();

            //xiukou_l
            bitmapTemp =  Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 12667, 7286 + 3698, 1628, 619);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);

            //xiukou_r
            bitmapTemp =  Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1032, 7286 + 3698, 1628, 619);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiukouR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiukou + margin, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 9600) {//jj GRY
            //front R
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3306, 170, 1482, 3574);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gry_front_r);
            bitmapDB = bitmapDB.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasDB = new Canvas(bitmapDB);
            drawTextFrontR(canvasDB);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, 1482, 3574, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //front L
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4832, 170, 1482, 3574);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gry_front_l);
            bitmapDB = bitmapDB.copy(Bitmap.Config.ARGB_8888, true);
            canvasDB = new Canvas(bitmapDB);
            drawTextFrontL(canvasDB);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, 1482, 3574, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //back
            bitmapTemp =  Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1207, 4796, 2938, 3639);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gqy_back);
            bitmapDB = bitmapDB.copy(Bitmap.Config.ARGB_8888, true);
            canvasDB = new Canvas(bitmapDB);
            drawTextBack(canvasDB);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, 2938, 3639, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //arm_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6384, 759, 2401, 3113);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gqy_arm_l);
            bitmapDB = bitmapDB.copy(Bitmap.Config.ARGB_8888, true);
            canvasDB = new Canvas(bitmapDB);
            drawTextXiuziL(canvasDB);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, 2401, 3113, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + margin * 2, null);

            //arm_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 817, 759, 2401, 3113);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gqy_arm_r);
            bitmapDB = bitmapDB.copy(Bitmap.Config.ARGB_8888, true);
            canvasDB = new Canvas(bitmapDB);
            drawTextXiuziR(canvasDB);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, 2401, 3113, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm + margin * 3, null);

            //pocket
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3667, 2333, 1120, 1410);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gry_pocket_r);
            bitmapDB = bitmapDB.copy(Bitmap.Config.ARGB_8888, true);
            canvasDB = new Canvas(bitmapDB);
            drawTextPocketR(canvasDB);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, 1120, 1410, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + margin * 4, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4834, 2333, 1120, 1410);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gry_pocket_l);
            bitmapDB = bitmapDB.copy(Bitmap.Config.ARGB_8888, true);
            canvasDB = new Canvas(bitmapDB);
            drawTextPocketL(canvasDB);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, 1120, 1410, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket + margin, height_back + height_front + height_arm * 2 + margin * 4, null);

            //maozi_out_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5232, 4792, 1535, 2322);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gry_maozi_in_r);
            bitmapDB = bitmapDB.copy(Bitmap.Config.ARGB_8888, true);
            canvasDB = new Canvas(bitmapDB);
            drawTextMaoziOutL(canvasDB);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, 1535, 2322, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + margin * 2, null);

            //maozi_out_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6855, 4792, 1535, 2322);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gry_maozi_in_l);
            bitmapDB = bitmapDB.copy(Bitmap.Config.ARGB_8888, true);
            canvasDB = new Canvas(bitmapDB);
            drawTextMaoziOutR(canvasDB);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, 1535, 2322, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi + margin * 3, null);

            //maozi_in_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5229, 7106, 1535, 2322);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gry_maozi_in_r);
            bitmapDB = bitmapDB.copy(Bitmap.Config.ARGB_8888, true);
            canvasDB = new Canvas(bitmapDB);
            drawTextMaoziInR(canvasDB);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, 1535, 2322, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi * 2 + margin * 3, null);

            //maozi_in_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6852, 7106, 1535, 2322);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_in_l);
            bitmapDB = bitmapDB.copy(Bitmap.Config.ARGB_8888, true);
            canvasDB = new Canvas(bitmapDB);
            drawTextMaoziInL(canvasDB);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, 1535, 2322, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, widthCombine - width_maozi, height_front + height_back + height_maozi * 3 + margin * 3, null);

            //xiabai
            Bitmap bitmapArm = Bitmap.createBitmap(1273 * 2 + 2553, 834, Bitmap.Config.ARGB_8888);
            Canvas canvasArm= new Canvas(bitmapArm);
            canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasArm.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4834, 3792, 1273, 834);
            canvasArm.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1398, 8479, 2553, 834);
            canvasArm.drawBitmap(bitmapTemp, 1273, 0, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3512, 3792, 1273, 834);
            canvasArm.drawBitmap(bitmapTemp, 1273 + 2553, 0, null);
            bitmapTemp.recycle();

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_bottom_7907);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, 1273 * 2 + 2553, 834, true);
            canvasArm.drawBitmap(bitmapDB, 0, 0, null);

            drawTextXiabai(canvasArm);
            bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_xiabai, height_xiabai, true);

            matrix.reset();
            matrix.postRotate(-90);
            matrix.postTranslate(width_front * 2 + margin * 2, width_xiabai);
            canvasCombine.drawBitmap(bitmapArm, matrix, null);
            bitmapArm.recycle();

            //xiukou_l
            bitmapTemp =  Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6971, 3910, 1235, 835);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            bitmapDB = bitmapDB.copy(Bitmap.Config.ARGB_8888, true);
            canvasDB = new Canvas(bitmapDB);
            drawTextXiukouL(canvasDB);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, 1235, 835, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);

            //xiukou_r
            bitmapTemp =  Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1396, 3910, 1235, 835);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            bitmapDB = bitmapDB.copy(Bitmap.Config.ARGB_8888, true);
            canvasDB = new Canvas(bitmapDB);
            drawTextXiukouR(canvasDB);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, 1235, 835, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiukou + margin, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);
            bitmapDB.recycle();
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).isPPSL) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1428 + 30, 2257, 2072, 4193);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GRY") ? R.drawable.gry_front_r : R.drawable.gr_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3500 - 30, 2257, 2072, 4193);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GRY") ? R.drawable.gry_front_l : R.drawable.gr_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1429, 2197, 4143, 4253);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //arm_l
            Bitmap bitmapArm = Bitmap.createBitmap(2903, 3784, Bitmap.Config.ARGB_8888);
            Canvas canvasArm= new Canvas(bitmapArm);
            canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasArm.drawColor(0xffffffff);

            Bitmap bitmapHalf = Bitmap.createBitmap(1452, 3784, Bitmap.Config.ARGB_8888);
            Canvas canvasHalf= new Canvas(bitmapHalf);
            canvasHalf.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasHalf.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 4857, 2561, 1615, 3729);
            matrix.reset();
            matrix.postRotate(-11.4f);
            matrix.postTranslate(-136, 298);
            canvasArm.drawBitmap(bitmapTemp, matrix, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 529, 2561, 1615, 3729);
            matrix.reset();
            matrix.postRotate(11.4f);
            matrix.postTranslate(2, -19);
            canvasHalf.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();
            canvasArm.drawBitmap(bitmapHalf, 1451, 0, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_l);
            canvasArm.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiuziL(canvasArm);
            bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapArm, 0, height_back + height_front + margin * 2, null);

            //arm_r
            bitmapArm = Bitmap.createBitmap(2903, 3784, Bitmap.Config.ARGB_8888);
            canvasArm= new Canvas(bitmapArm);
            canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasArm.drawColor(0xffffffff);

            bitmapHalf = Bitmap.createBitmap(1452, 3784, Bitmap.Config.ARGB_8888);
            canvasHalf= new Canvas(bitmapHalf);
            canvasHalf.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasHalf.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4857, 2561, 1615, 3729);
            matrix.reset();
            matrix.postRotate(-11.4f);
            matrix.postTranslate(-136, 298);
            canvasArm.drawBitmap(bitmapTemp, matrix, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 529, 2561, 1615, 3729);
            matrix.reset();
            matrix.postRotate(11.4f);
            matrix.postTranslate(2, -19);
            canvasHalf.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();
            canvasArm.drawBitmap(bitmapHalf, 1451, 0, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_r);
            canvasArm.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiuziR(canvasArm);
            bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapArm, 0, height_back + height_front + height_arm + margin * 3, null);

            //pocket
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2133 + 30, 4874, 1397, 1576);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GRY") ? R.drawable.gry_pocket_r : R.drawable.gr_pocket_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextPocketR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + margin * 4, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3530 - 30, 4874, 1397, 1576);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GRY") ? R.drawable.gry_pocket_l : R.drawable.gr_pocket_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextPocketL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket + margin, height_back + height_front + height_arm * 2 + margin * 4, null);

            //xiukou_l
            bitmapArm = Bitmap.createBitmap(1628, 619, Bitmap.Config.ARGB_8888);
            canvasArm= new Canvas(bitmapArm);
            canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasArm.drawColor(0xffffffff);

            bitmapHalf = Bitmap.createBitmap(814, 619, Bitmap.Config.ARGB_8888);
            canvasHalf= new Canvas(bitmapHalf);
            canvasHalf.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasHalf.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 4828, 6018, 920, 768);
            matrix.reset();
            matrix.postRotate(-11.5f);
            matrix.postTranslate(-120, 25);
            canvasArm.drawBitmap(bitmapTemp, matrix, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1254, 6018, 920, 768);
            matrix.reset();
            matrix.postRotate(11.5f);
            matrix.postTranslate(32, -159);
            canvasHalf.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();
            canvasArm.drawBitmap(bitmapHalf, 813, 0, null);

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            canvasArm.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiukouL(canvasArm);
            bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapArm, 0, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);

            //xiukou_r
            bitmapArm = Bitmap.createBitmap(1628, 619, Bitmap.Config.ARGB_8888);
            canvasArm= new Canvas(bitmapArm);
            canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasArm.drawColor(0xffffffff);

            bitmapHalf = Bitmap.createBitmap(814, 619, Bitmap.Config.ARGB_8888);
            canvasHalf= new Canvas(bitmapHalf);
            canvasHalf.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasHalf.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4828, 6018, 920, 768);
            matrix.reset();
            matrix.postRotate(-11.5f);
            matrix.postTranslate(-120, 25);
            canvasArm.drawBitmap(bitmapTemp, matrix, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1254, 6018, 920, 768);
            matrix.reset();
            matrix.postRotate(11.5f);
            matrix.postTranslate(32, -159);
            canvasHalf.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();
            canvasArm.drawBitmap(bitmapHalf, 813, 0, null);
            bitmapHalf.recycle();

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            canvasArm.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiukouR(canvasArm);
            bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapArm, width_xiukou + margin, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);

            //maozi_out_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1572, 123, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GRY") ? R.drawable.gry_maozi_in_r : R.drawable.gr_maozi_out_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextMaoziOutL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + margin * 2, null);

            //maozi_out_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3507, 123, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GRY") ? R.drawable.gry_maozi_in_l : R.drawable.gr_maozi_out_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextMaoziOutR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi + margin * 3, null);

            //maozi_in_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1572, 123, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GRY") ? R.drawable.gry_maozi_in_r : R.drawable.gr_maozi_in_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextMaoziInR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi * 2 + margin * 3, null);

            //maozi_in_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3507, 123, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), orderItems.get(currentID).sku.equals("GRY") ? R.drawable.gry_maozi_in_l : R.drawable.gr_maozi_in_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextMaoziInL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi * 3 + margin * 3, null);

            //xiabai
            bitmapArm = Bitmap.createBitmap(7907, 860, Bitmap.Config.ARGB_8888);
            canvasArm= new Canvas(bitmapArm);
            canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasArm.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3500, 6325, 1978, 431);
            canvasArm.drawBitmap(bitmapTemp, 0, 0, null);
            canvasArm.drawBitmap(bitmapTemp, 0, 430, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1524, 6325, 3954, 431);
            canvasArm.drawBitmap(bitmapTemp, 1977, 0, null);
            canvasArm.drawBitmap(bitmapTemp, 1977, 430, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1524, 6325, 1978, 431);
            canvasArm.drawBitmap(bitmapTemp, 5930, 0, null);
            canvasArm.drawBitmap(bitmapTemp, 5930, 430, null);
            bitmapTemp.recycle();

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_bottom_7907);
            canvasArm.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiabai(canvasArm);
            bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_xiabai, height_xiabai, true);

            matrix.reset();
            matrix.postRotate(-90);
            matrix.postTranslate(width_front * 2 + margin * 2, width_xiabai);
            canvasCombine.drawBitmap(bitmapArm, matrix, null);
            bitmapArm.recycle();
            
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
            bitmapTemp =  Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2904, 2074, 4143, 4253);
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
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3574 + 30, 4751, 2794/2, 1576);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_pocket_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + margin * 4, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4975 - 30, 4751, 2794/2, 1576);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_pocket_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketL(canvasTemp);
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
            Canvas canvasArm= new Canvas(bitmapArm);
            canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasArm.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4975 - 30, 6202, 3864 / 2, 675);
            canvasArm.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3035, 6202, 3864, 675);
            canvasArm.drawBitmap(bitmapTemp, 3864/2, 0, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3035 + 30, 6202, 3864 / 2, 675);
            canvasArm.drawBitmap(bitmapTemp, 3864 / 2 + 3864, 0, null);
            bitmapTemp.recycle();

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_bottom_7907);
            canvasArm.drawBitmap(bitmapDB, 0, 0, null);

            drawTextXiabai(canvasArm);
            bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_xiabai, height_xiabai, true);

            matrix.reset();
            matrix.postRotate(-90);
            matrix.postTranslate(width_front * 2 + margin * 2, width_xiabai);
            canvasCombine.drawBitmap(bitmapArm, matrix, null);
            bitmapArm.recycle();

            //xiukou_l
            bitmapTemp =  Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7685, 6258, 1628, 619);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);

            //xiukou_r
            bitmapTemp =  Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 634, 6258, 1628, 619);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiukouR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiukou + margin, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 2) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2903 + 30, 2134, 2072, 4193);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 4975 - 30, 2134, 2072, 4193);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //back
            bitmapTemp =  Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2904, 2074, 4143, 4253);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //arm_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 7047, 2600, 2903, 3784);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + margin * 2, null);


            //arm_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 2576, 2903, 3784);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_arm_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm + margin * 3, null);

            //pocket
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3574 + 30, 4751, 2794/2, 1576);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_pocket_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + margin * 4, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 4975 - 30, 4751, 2794/2, 1576);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_pocket_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket + margin, height_back + height_front + height_arm * 2 + margin * 4, null);

            //maozi_out_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3047, 0, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_out_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + margin * 2, null);

            //maozi_out_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 4982, 0, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_out_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi + margin * 3, null);

            //maozi_in_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3047, 0, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_in_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi * 2 + margin * 3, null);

            //maozi_in_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 4982, 0, 1928, 2368);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gr_maozi_in_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + margin, height_front + height_back + height_maozi * 3 + margin * 3, null);

            //xiabai
            Bitmap bitmapArm = Bitmap.createBitmap(3864 * 2, 675, Bitmap.Config.ARGB_8888);
            Canvas canvasArm= new Canvas(bitmapArm);
            canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasArm.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 4975 - 30, 6202, 3864 / 2, 675);
            canvasArm.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3035, 6202, 3864, 675);
            canvasArm.drawBitmap(bitmapTemp, 3864/2, 0, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3035 + 30, 6202, 3864 / 2, 675);
            canvasArm.drawBitmap(bitmapTemp, 3864 / 2 + 3864, 0, null);
            bitmapTemp.recycle();

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_bottom_7907);
            canvasArm.drawBitmap(bitmapDB, 0, 0, null);

            drawTextXiabai(canvasArm);
            bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_xiabai, height_xiabai, true);

            matrix.reset();
            matrix.postRotate(-90);
            matrix.postTranslate(width_front * 2 + margin * 2, width_xiabai);
            canvasCombine.drawBitmap(bitmapArm, matrix, null);
            bitmapArm.recycle();

            //xiukou_l
            bitmapTemp =  Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 7685, 6258, 1628, 619);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);

            //xiukou_r
            bitmapTemp =  Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 634, 6258, 1628, 619);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gq_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiukouR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiukou + margin, height_back + height_front + height_arm * 2 + height_pocket + margin * 5, null);
            bitmapTemp.recycle();
        }



        String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";
        String pathSave;
        if(MainActivity.instance.cb_classify.isChecked()){
            pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
        } else
            pathSave = sdCardPath + "/生产图/" + childPath + "/";
        if(!new File(pathSave).exists())
            new File(pathSave).mkdirs();
        File fileSave = new File(pathSave + nameCombine);
        BitmapToJpg.save(bitmapCombine, fileSave, 148);



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

    void setSizeGRM(String size) {
        switch (size) {
            case "2XS":
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
            case "XS":
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
            case "S":
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
            case "M":
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
            case "L":
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
            case "XL":
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
            case "2XL":
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
            case "3XL":
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
            case "4XL":
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
    void setSizeGRW(String size) {
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

        width_front += 12;
        height_front += 12;
        width_back += 12;
        height_back += 12;
        width_arm += 12;
        height_arm += 12;
        width_maozi += 12;
        height_maozi += 12;
        width_xiabai += 12;
        height_xiabai += 12;
        width_pocket += 12;
        height_pocket += 12;
        width_xiukou += 12;
        height_xiukou += 12;
    }

    void setSizeGRY(String size) {
        switch (size) {
            case "3XS":
                width_front = 1320;
                height_front = 3220;
                width_back = 2614;
                height_back = 3282;
                width_arm = 2141;
                height_arm = 2748;
                width_maozi = 1488;
                height_maozi = 2263;
                width_xiabai = 4514;
                height_xiabai = 835;
                width_pocket = 1002;
                height_pocket = 1317;
                width_xiukou = 1115;
                height_xiukou = 835;
                break;
            case "2XS":
                width_front = 1394;
                height_front = 3401;
                width_back = 2762;
                height_back = 3460;
                width_arm = 2270;
                height_arm = 2927;
                width_maozi = 1456;
                height_maozi = 2254;
                width_xiabai = 4750;
                height_xiabai = 835;
                width_pocket = 1064;
                height_pocket = 1365;
                width_xiukou = 1174;
                height_xiukou = 835;
                break;
            case "XS":
                width_front = 1482;
                height_front = 3577;
                width_back = 2939;
                height_back = 3636;
                width_arm = 2402;
                height_arm = 3105;
                width_maozi = 1539;
                height_maozi = 2323;
                width_xiabai = 5104;
                height_xiabai = 835;
                width_pocket = 1125;
                height_pocket = 1407;
                width_xiukou = 1233;
                height_xiukou = 835;
                break;
            case "S":
                width_front = 1601;
                height_front = 3755;
                width_back = 3176;
                height_back = 3813;
                width_arm = 2532;
                height_arm = 3343;
                width_maozi = 1594;
                height_maozi = 2385;
                width_xiabai = 5518;
                height_xiabai = 835;
                width_pocket = 1186;
                height_pocket = 1452;
                width_xiukou = 1233;
                height_xiukou = 835;
                break;
            case "M":
                width_front = 1748;
                height_front = 3901;
                width_back = 3471;
                height_back = 3961;
                width_arm = 2663;
                height_arm = 3461;
                width_maozi = 1651;
                height_maozi = 2444;
                width_xiabai = 6049;
                height_xiabai = 835;
                width_pocket = 1244;
                height_pocket = 1499;
                width_xiukou = 1292;
                height_xiukou = 835;
                break;
            case "L":
                width_front = 1896;
                height_front = 4047;
                width_back = 3767;
                height_back = 4108;
                width_arm = 2789;
                height_arm = 3639;
                width_maozi = 1710;
                height_maozi = 2503;
                width_xiabai = 6522;
                height_xiabai = 835;
                width_pocket = 1302;
                height_pocket = 1534;
                width_xiukou = 1351;
                height_xiukou = 835;
                break;

            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }
        width_front += 12;
        height_front += 12;
        width_back += 12;
        height_back += 12;
        width_arm += 12;
        height_arm += 12;
        width_maozi += 12;
        height_maozi += 12;
        width_xiabai += 12;
        height_xiabai += 12;
        width_pocket += 12;
        height_pocket += 12;
        width_xiukou += 12;
        height_xiukou += 12;
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
