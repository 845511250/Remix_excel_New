package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
 * Created by zuoyun on 2016/10/6.
 */

public class FragmentDQ extends BaseFragment {
    Context context;
    //    String sdCardPath = "/mnt/asec/share";
    String sdCardPath = "/storage/emulated/0/Pictures";
    ArrayList<OrderItem> orderItems;
    int currentID;
    String childPath;

    @BindView(R.id.iv_fg2_leftup)
    ImageView iv_leftup;
    @BindView(R.id.iv_fg2_rightup)
    ImageView iv_rightup;
    @BindView(R.id.bt_remix)
    Button bt_remix;

    int width_main, height_main, width_tongue, height_tongue;
    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint paint,paintRed,paintBlue, rectPaint;
    String time = MainActivity.instance.orderDate_Print;

    @Override
    public int getLayout() {
        return R.layout.fragment_dq;
    }

    @Override
    public void initData(View view) {
        context = getContext();
        ButterKnife.bind(this, view);
        orderItems=MainActivity.instance.orderItems;
        currentID = MainActivity.instance.currentID;
        childPath = MainActivity.instance.childPath;

        //paint
        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(25);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(25);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff000000);
        paintBlue.setTextSize(25);
        paintBlue.setTypeface(Typeface.DEFAULT_BOLD);
        paintBlue.setAntiAlias(true);

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message,String sampleurl) {
                if (message == 0) {
                    iv_leftup.setImageDrawable(null);
                    iv_rightup.setImageDrawable(null);
                } else {
                    if (message == MainActivity.LOADED_IMGS) {
                        bt_remix.setClickable(true);
                        if (!MainActivity.instance.cb_fastmode.isChecked())
                            iv_rightup.setImageBitmap(MainActivity.instance.bitmaps.get(0));
                        checkremix();
                    } else {
                        if (message == 3) {
                            bt_remix.setClickable(false);
                        } else if (message == 10) {
                            remix();
                        }
                    }
                }
            }
        });

        //******************************************************************************************
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

    void drawTextMain(Canvas canvas, String LR) {
        paint.setTextSize(25);
        paintRed.setTextSize(25);

        canvas.save();
        canvas.rotate(77.8f, 47, 254);
        canvas.drawRect(47, 254 - 25, 47 + 350, 254, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).newCode, 47, 254 - 2, paintRed);
        canvas.restore();

        canvas.save();
        canvas.rotate(-78f, 765, 593);
        canvas.drawRect(765, 593 - 25, 765 + 500, 593, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "-" + orderItems.get(currentID).size + "码" + orderItems.get(currentID).color + LR + "   " + orderItems.get(currentID).order_number, 765, 593 - 2, paint);
        canvas.restore();
    }
    void drawTextMainAdam(Canvas canvas, String LR) {
        paint.setTextSize(25);
        paintRed.setTextSize(25);

        canvas.save();
        canvas.rotate(77.4f, 69, 323);
        canvas.drawRect(69, 323 - 25, 420, 323, rectPaint);
        canvas.drawText(time + "     " + orderItems.get(currentID).newCode, 69, 323 - 3, paintRed);
        canvas.restore();

        canvas.save();
        canvas.rotate(-76.8f, 830, 641);
        canvas.drawRect(830, 641 - 25, 830 + 500, 641, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "-" + orderItems.get(currentID).size + "码" + orderItems.get(currentID).color + LR + "   " + orderItems.get(currentID).order_number, 830, 641 - 3, paint);
        canvas.restore();
    }
    void drawTextTongue(Canvas canvas, String LR) {
        paint.setTextSize(19);
        paintRed.setTextSize(19);

        canvas.drawRect(115, 441, 270, 459, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + orderItems.get(currentID).color + LR + "  " + time, 120, 457, paint);
        canvas.drawRect(88, 422, 305, 440, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number, 88, 438, paint);
        canvas.drawText(orderItems.get(currentID).newCode_short, 235, 438, paintRed);

    }

    public void remixx(){
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = null, bitmapDB_tongue = null, bitmapDB_main = null;

        if ((orderItems.get(currentID).imgs.size() == 1) || (orderItems.get(currentID).imgs.size() == 2)) {
            if (orderItems.get(currentID).imgs.size() == 1) {
                Matrix matrix = new Matrix();
                matrix.postScale(-1, 1);

                MainActivity.instance.bitmaps.add(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, MainActivity.instance.bitmaps.get(0).getWidth(), MainActivity.instance.bitmaps.get(0).getHeight(), matrix, true));
            }
            if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {//jj
                if (MainActivity.instance.bitmaps.get(0).getWidth() != 2000) {
                    MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 2000, 2000, true));
                }

                if (orderItems.get(currentID).sku.equals("AS")) {
                    bitmapCombine = Bitmap.createBitmap(width_main + 59, height_tongue + 59 + height_main + 10 + height_main, Bitmap.Config.ARGB_8888);
                    Canvas canvasCombine = new Canvas(bitmapCombine);
                    canvasCombine.drawColor(0xffffffff);
                    canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

                    bitmapDB_main = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dq_4u2);
                    bitmapDB_tongue = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.as_tongue);

                    //leftMain
                    Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1027, 766, 885, 1099);
                    Canvas canvasTemp = new Canvas(bitmapTemp);
                    canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                    canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
                    drawTextMain(canvasTemp, "左");
                    bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
                    canvasCombine.drawBitmap(bitmapTemp, 0, height_tongue + 59 + height_main + 10, null);

                    //leftTongue
                    bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1294, 134, 354, 468);
                    canvasTemp = new Canvas(bitmapTemp);
                    canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                    canvasTemp.drawBitmap(bitmapDB_tongue, 0, 0, null);
                    drawTextTongue(canvasTemp, "左");
                    bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
                    canvasCombine.drawBitmap(bitmapTemp, 59, 0, null);

                    //rightMain
                    bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 87, 764, 885, 1099);
                    canvasTemp = new Canvas(bitmapTemp);
                    canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                    canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
                    drawTextMain(canvasTemp, "右");
                    bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
                    canvasCombine.drawBitmap(bitmapTemp, 0, height_tongue + 59, null);

                    //rightTongue
                    bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 354, 137, 354, 468);
                    canvasTemp = new Canvas(bitmapTemp);
                    canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                    canvasTemp.drawBitmap(bitmapDB_tongue, 0, 0, null);
                    drawTextTongue(canvasTemp, "右");
                    bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
                    canvasCombine.drawBitmap(bitmapTemp, 59 + width_tongue + 59, 0, null);

                    bitmapTemp.recycle();
                    bitmapDB_main.recycle();
                    bitmapDB_tongue.recycle();

                    Matrix matrix90 = new Matrix();
                    matrix90.postRotate(90);
                    matrix90.postTranslate(bitmapCombine.getHeight(), 0);
                    bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix90, true);
                } else{
                    bitmapCombine = Bitmap.createBitmap(width_main + 59, height_tongue + 59 + height_main + 10 + height_main, Bitmap.Config.ARGB_8888);
                    Canvas canvasCombine = new Canvas(bitmapCombine);
                    canvasCombine.drawColor(0xffffffff);
                    canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

                    bitmapDB_main = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dq_4u2);
                    bitmapDB_tongue = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.aq40_tongue);

                    //leftMain
                    Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1021, 703, 885, 1099);
                    Canvas canvasTemp = new Canvas(bitmapTemp);
                    canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                    canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
                    drawTextMain(canvasTemp, "左");
                    bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
                    canvasCombine.drawBitmap(bitmapTemp, 0, height_tongue + 59 + height_main + 10, null);

                    //leftTongue
                    bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1269, 182, 390, 468);
                    canvasTemp = new Canvas(bitmapTemp);
                    canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                    canvasTemp.drawBitmap(bitmapDB_tongue, 0, 0, null);
                    drawTextTongue(canvasTemp, "左");
                    bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
                    canvasCombine.drawBitmap(bitmapTemp, 59, 0, null);

                    //rightMain
                    bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 91, 703, 885, 1099);
                    canvasTemp = new Canvas(bitmapTemp);
                    canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                    canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
                    drawTextMain(canvasTemp, "右");
                    bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
                    canvasCombine.drawBitmap(bitmapTemp, 0, height_tongue + 59, null);

                    //rightTongue
                    bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 339, 182, 390, 468);
                    canvasTemp = new Canvas(bitmapTemp);
                    canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                    canvasTemp.drawBitmap(bitmapDB_tongue, 0, 0, null);
                    drawTextTongue(canvasTemp, "右");
                    bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
                    canvasCombine.drawBitmap(bitmapTemp, 59 + width_tongue + 59, 0, null);

                    bitmapTemp.recycle();
                    bitmapDB_main.recycle();
                    bitmapDB_tongue.recycle();

                    Matrix matrix90 = new Matrix();
                    matrix90.postRotate(90);
                    matrix90.postTranslate(bitmapCombine.getHeight(), 0);
                    bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix90, true);
                }

            } else if (MainActivity.instance.bitmaps.get(0).getWidth() != 950) {
                bitmapCombine = Bitmap.createBitmap(width_main + 59, height_tongue + 59 + height_main + 10 + height_main, Bitmap.Config.ARGB_8888);
                Canvas canvasCombine = new Canvas(bitmapCombine);
                canvasCombine.drawColor(0xffffffff);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

                bitmapDB_main = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dq_4u2);
                bitmapDB_tongue = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.aq40_tongue);

                //leftMain
                Bitmap bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
                drawTextMain(canvasTemp, "左");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_tongue + 59 + height_main + 10, null);

                //leftTongue
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 253, 301, 390, 468);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(bitmapDB_tongue, 0, 0, null);
                drawTextTongue(canvasTemp, "左");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
                canvasCombine.drawBitmap(bitmapTemp, 59, 0, null);

                //rightMain
                bitmapTemp = MainActivity.instance.bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, true);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
                drawTextMain(canvasTemp, "右");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_tongue + 59, null);

                //rightTongue
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 253, 301, 390, 468);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(bitmapDB_tongue, 0, 0, null);
                drawTextTongue(canvasTemp, "右");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
                canvasCombine.drawBitmap(bitmapTemp, 59 + width_tongue + 59, 0, null);

                bitmapTemp.recycle();
                bitmapDB_main.recycle();
                bitmapDB_tongue.recycle();

                Matrix matrix90 = new Matrix();
                matrix90.postRotate(90);
                matrix90.postTranslate(bitmapCombine.getHeight(), 0);
                bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix90, true);

            } else {//adam
                bitmapCombine = Bitmap.createBitmap(width_main * 2 + width_tongue, height_main, Bitmap.Config.ARGB_8888);
                Canvas canvasCombine = new Canvas(bitmapCombine);
                canvasCombine.drawColor(0xffffffff);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

                bitmapDB_main = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dq_adam);
                bitmapDB_tongue = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dq_tongue_adam);

                Matrix matrix = new Matrix();
                matrix.postRotate(180);
                matrix.postTranslate(width_tongue + width_main, height_tongue + height_main - height_tongue * 2 - 100);
                //left tongue
                Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 288, 336, 382, 467);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(bitmapDB_tongue, 0, 0, null);
                drawTextTongue(canvasTemp, "左");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                //left main
                bitmapTemp = Bitmap.createBitmap(970, 1128, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 10, -35, null);
                canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
                drawTextMainAdam(canvasTemp, "左");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
                canvasCombine.drawBitmap(bitmapTemp, width_main + width_tongue, 0, null);

                //right tongue
                matrix.postTranslate(0, height_tongue + 100);
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 288, 336, 382, 467);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(bitmapDB_tongue, 0, 0, null);
                drawTextTongue(canvasTemp, "右");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                //right main
                bitmapTemp = Bitmap.createBitmap(970, 1128, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 10, -35, null);
                canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
                drawTextMainAdam(canvasTemp, "右");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

                bitmapTemp.recycle();
                bitmapDB_main.recycle();
                bitmapDB_tongue.recycle();
            }
        } else if (orderItems.get(currentID).imgs.size() == 4) {
            bitmapCombine = Bitmap.createBitmap(width_main + 59, height_tongue + 59 + height_main + 10 + height_main, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.drawColor(0xffffffff);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapDB_main = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dq_4u2);
            bitmapDB_tongue = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dq_tongue_4u2);

            //leftMain
            Bitmap bitmapTemp = Bitmap.createBitmap(885, 1099, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(checkContains("left_side") ? getBitmapWith("left_side") : MainActivity.instance.bitmaps.get(0), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_tongue + 59 + height_main + 10, null);

            //leftTongue
            bitmapTemp = (checkContains("left_tongue") ? getBitmapWith("left_tongue") : MainActivity.instance.bitmaps.get(1)).copy(Bitmap.Config.ARGB_8888, true);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 341, 455, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_tongue, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 390, 468, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 59, 0, null);

            //rightMain
            bitmapTemp = Bitmap.createBitmap(885, 1099, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(checkContains("right_side") ? getBitmapWith("right_side") : MainActivity.instance.bitmaps.get(2), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_tongue + 59, null);

            //rightTongue
            bitmapTemp = (checkContains("right_tongue") ? getBitmapWith("right_tongue") : MainActivity.instance.bitmaps.get(3)).copy(Bitmap.Config.ARGB_8888, true);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 341, 455, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_tongue, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 390, 468, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, 59 + width_tongue + 59, 0, null);

            bitmapTemp.recycle();
            bitmapDB_main.recycle();
            bitmapDB_tongue.recycle();

            Matrix matrix90 = new Matrix();
            matrix90.postRotate(90);
            matrix90.postTranslate(bitmapCombine.getHeight(), 0);
            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix90, true);
        }




        try {
            String printColor = orderItems.get(currentID).color.equals("黑") ? "B" : "W";
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
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor);
            if (orderItems.get(currentID).sku.equals("AS")) {
                label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + orderItems.get(currentID).size);
            }
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor);
            if (orderItems.get(currentID).sku.equals("AS")) {
                label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + orderItems.get(currentID).size);
            }
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
        if (MainActivity.instance.tb_auto.isChecked()){
            remix();
        }
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

    void setSize(int size){
        switch (size) {
            case 35:
                width_main = 1441;
                height_main = 1701;
                width_tongue = 606;
                height_tongue = 695;
                break;
            case 36:
                width_main = 1469;
                height_main = 1745;
                width_tongue = 606;
                height_tongue = 695;
                break;
            case 37:
                width_main = 1497;
                height_main = 1788;
                width_tongue = 629;
                height_tongue = 732;
                break;
            case 38:
                width_main = 1524;
                height_main = 1831;
                width_tongue = 629;
                height_tongue = 732;
                break;
            case 39:
                width_main = 1552;
                height_main = 1874;
                width_tongue = 652;
                height_tongue = 769;
                break;
            case 40:
                width_main = 1580;
                height_main = 1918;
                width_tongue = 652;
                height_tongue = 769;
                break;
            case 41:
                width_main = 1607;
                height_main = 1961;
                width_tongue = 675;
                height_tongue = 806;
                break;
            case 42:
                width_main = 1635;
                height_main = 2004;
                width_tongue = 675;
                height_tongue = 806;
                break;
            case 43:
                width_main = 1663;
                height_main = 2062;
                width_tongue = 698;
                height_tongue = 843;
                break;
            case 44:
                width_main = 1690;
                height_main = 2106;
                width_tongue = 698;
                height_tongue = 843;
                break;
            case 45:
                width_main = 1718;
                height_main = 2150;
                width_tongue = 721;
                height_tongue = 880;
                break;
            case 46:
                width_main = 1746;
                height_main = 2193;
                width_tongue = 721;
                height_tongue = 880;
                break;
            case 47:
                width_main = 1773;
                height_main = 2237;
                width_tongue = 744;
                height_tongue = 917;
                break;
            case 48:
                width_main = 1801;
                height_main = 2281;
                width_tongue = 744;
                height_tongue = 917;
                break;
        }
        width_tongue += 10;
        height_tongue += 10;
        if (orderItems.get(currentID).sku.equals("AS")) {
            width_main -= 25;
        }
    }

}
