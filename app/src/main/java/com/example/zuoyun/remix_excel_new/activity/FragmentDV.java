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

public class FragmentDV extends BaseFragment {
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

    Matrix matrixprint1=new Matrix(), matrixprint2 = new Matrix();

    int width_main,height_main,width_tongue, height_tongue;
    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint paint,paintRed, rectPaint;
    String time;

    @Override
    public int getLayout() {
        return R.layout.fragment_bn;
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
        paint.setTextSize(30);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(30);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        time = MainActivity.instance.orderDate_Print;


        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message,String sampleurl) {
                if(message==0){
                    iv_leftup.setImageDrawable(null);
                    iv_rightup.setImageDrawable(null);
                } else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
                    if (!MainActivity.instance.cb_fastmode.isChecked())
                        iv_rightup.setImageBitmap(MainActivity.instance.bitmaps.get(0));
//                    Glide.with(context).load(sampleurl).into(iv_sample2);
                    checkremix();
                } else {
                    if (message == 3) {
                        bt_remix.setClickable(false);
                    } else if (message == 10) {
                        remix();
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

    void drawTextLM(Canvas canvasLeft_main) {
        //left_main
        canvasLeft_main.drawRect(463, 1007, 563, 1037, rectPaint);
        canvasLeft_main.drawText(orderItems.get(currentID).size + orderItems.get(currentID).color + "左", 463, 1034, paint);
        canvasLeft_main.save();
        canvasLeft_main.rotate(72.5f, 40, 378);
        canvasLeft_main.drawRect(40, 348, 440, 378, rectPaint);
        canvasLeft_main.drawText(time + "   " + orderItems.get(currentID).order_number, 40, 375, paint);
        canvasLeft_main.drawText("", 300, 375, paintRed);
        canvasLeft_main.restore();

        canvasLeft_main.save();
        canvasLeft_main.rotate(-71.8f, 842, 756);
        canvasLeft_main.drawRect(842, 718, 1242, 758, rectPaint);
        //canvasLeft_main.drawBitmap(bitmapBarCode, 832, 718, null);
        canvasLeft_main.drawText(orderItems.get(currentID).newCode, 842, 755, paintRed);
        canvasLeft_main.restore();
    }
    void drawTextRM(Canvas canvasRight_main){
        //right_main
        canvasRight_main.drawRect(463, 1007, 563, 1037, rectPaint);
        canvasRight_main.drawText(orderItems.get(currentID).size + orderItems.get(currentID).color + "右", 463, 1034, paint);
        canvasRight_main.save();
        canvasRight_main.rotate(72.5f, 40, 378);
        canvasRight_main.drawRect(40, 348, 440, 378, rectPaint);
        canvasRight_main.drawText(time+"   "+orderItems.get(currentID).order_number, 40, 375, paint);
        canvasRight_main.drawText("", 300, 375, paintRed);
        canvasRight_main.restore();

        canvasRight_main.save();
        canvasRight_main.rotate(-71.8f, 842, 756);
        canvasRight_main.drawRect(842, 718, 1242, 758, rectPaint);
        //canvasRight_main.drawBitmap(bitmapBarCode, 832, 718, null);
        canvasRight_main.drawText(orderItems.get(currentID).newCode, 842, 755, paintRed);
        canvasRight_main.restore();
    }
    void drawTextLT(Canvas canvasLeft_tongue){
        paint.setTextSize(24);
        paintRed.setTextSize(24);
        //left_tongue
        canvasLeft_tongue.drawRect(107, 450, 277, 472, rectPaint);
        canvasLeft_tongue.drawText(orderItems.get(currentID).size + orderItems.get(currentID).color + "左", 107, 470, paint);
    }
    void drawTextRT(Canvas canvasRight_tongue){
        //right_tongue
        canvasRight_tongue.drawRect(107, 450, 277, 472, rectPaint);
        canvasRight_tongue.drawText(orderItems.get(currentID).size + orderItems.get(currentID).color + "右", 107, 470, paint);
    }

    void drawTextMain(Canvas canvasTemp, String LR) {
        paint.setTextSize(30);
        canvasTemp.drawRect(655, 1480 - 30, 655 + 120, 1480, rectPaint);
        canvasTemp.drawText(orderItems.get(currentID).size + orderItems.get(currentID).color + LR, 655, 1480 - 4, paint);

        canvasTemp.save();
        canvasTemp.rotate(72.6f, 59, 534);
        canvasTemp.drawRect(59, 534 - 30, 59 + 500, 534, rectPaint);
        canvasTemp.drawText(time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 59, 534 - 4, paint);
        canvasTemp.restore();
    }
    void drawTextTongue(Canvas canvasTemp, String LR) {
        paint.setTextSize(24);
        //left_tongue
        canvasTemp.drawRect(212, 648 - 24, 212 + 100, 648, rectPaint);
        canvasTemp.drawText(LR + orderItems.get(currentID).size + orderItems.get(currentID).color, 212, 648 - 2, paint);
    }


    public void remixx(){
        setSize(orderItems.get(currentID).size);

        int margin = 120;
        Bitmap bitmapCombine = Bitmap.createBitmap(width_main * 2 + width_tongue + margin * 2, height_main, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
            if(MainActivity.instance.bitmaps.get(0).getWidth() != 2993){
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 2993, 2993, true));
            }

            Bitmap bitmapTemp = Bitmap.createBitmap(1417, 1520, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0),-1497,-1094,null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.dv_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp,"左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1960, 385, 509, 655);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.dv_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main * 2 + margin * 2, 0, null);

            bitmapTemp = Bitmap.createBitmap(1417, 1520, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0),-75,-1094,null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.dv_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main + margin, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 531, 385, 509, 655);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.dv_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main * 2 + margin * 2, height_tongue + margin, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 1418, 1521, true));

            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1);
            MainActivity.instance.bitmaps.add(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, MainActivity.instance.bitmaps.get(0).getWidth(), MainActivity.instance.bitmaps.get(0).getHeight(), matrix, true));

            Bitmap bitmapTemp = Bitmap.createBitmap(1417, 1520, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0),0,0,null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.dv_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp,"左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 454, 348, 509, 655);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.dv_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main * 2 + margin * 2, 0, null);

            bitmapTemp = Bitmap.createBitmap(1417, 1520, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1),0,0,null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.dv_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main + margin, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 454, 348, 509, 655);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.dv_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main * 2 + margin * 2, height_tongue + margin, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 4 && orderItems.get(currentID).platform.equals("4u2")) {
            MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 1418, 1521, true));
            MainActivity.instance.bitmaps.set(1, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(1), 510, 656, true));
            MainActivity.instance.bitmaps.set(2, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(2), 1418, 1521, true));
            MainActivity.instance.bitmaps.set(3, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(3), 510, 656, true));

            Bitmap bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.dv_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = MainActivity.instance.bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.dv_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main * 2 + margin * 2, 0, null);

            bitmapTemp = MainActivity.instance.bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.dv_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main + margin, 0, null);

            bitmapTemp = MainActivity.instance.bitmaps.get(3).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.dv_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main * 2 + margin * 2, height_tongue + margin, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 2) {//adam
            //main:1000*1058
            //tongue:370*484
            MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 1060, 1042, true));
            MainActivity.instance.bitmaps.set(1, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(1), 1060, 1042, true));

            //left
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 29, 0, 1000, 1042);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.dv_main_adam);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLM(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 345, 258, 370, 484);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 370, 484, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.dv_tongue_adam);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLT(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main * 2 + margin * 2, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 29, 0, 1000, 1042);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.dv_main_adam);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRM(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main + margin, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 345, 258, 370, 484);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.dv_tongue_adam);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRT(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main * 2 + margin * 2, height_tongue + margin, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

        }


        try {
            String noNewCode = orderItems.get(currentID).newCode.equals("") ? orderItems.get(currentID).size + orderItems.get(currentID).color : "";

            String nameCombine = orderItems.get(currentID).sku + "_" + noNewCode + orderItems.get(currentID).newCode + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 148);

            //释放bitmap
            bitmapCombine.recycle();

            String printColor = orderItems.get(currentID).color.equals("黑") ? "B" : "W";

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
            Label label0 = new Label(0, currentID+1, orderItems.get(currentID).order_number+orderItems.get(currentID).sku+orderItems.get(currentID).size+printColor);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku+orderItems.get(currentID).size+printColor);
            sheet.addCell(label1);
            Number number2 = new Number(2, currentID+1, orderItems.get(currentID).num);
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

    void setSize(int size){
        switch (size) {
            case 28:
                width_main = 1292;
                height_main = 1329;
                width_tongue = 469;
                height_tongue = 575;
                break;
            case 29:
                width_main = 1322;
                height_main = 1372;
                width_tongue = 469;
                height_tongue = 575;
                break;
            case 30:
                width_main = 1352;
                height_main = 1414;
                width_tongue = 490;
                height_tongue = 613;
                break;
            case 31:
                width_main = 1382;
                height_main = 1457;
                width_tongue = 490;
                height_tongue = 613;
                break;
            case 32:
                width_main = 1412;
                height_main = 1501;
                width_tongue = 511;
                height_tongue = 651;
                break;
            case 33:
                width_main = 1442;
                height_main = 1544;
                width_tongue = 511;
                height_tongue = 651;
                break;
            case 34:
                width_main = 1472;
                height_main = 1588;
                width_tongue = 521;
                height_tongue = 670;
                break;
        }
    }

}
