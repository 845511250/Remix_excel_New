package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.util.Log;
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

public class FragmentDE_Child extends BaseFragment {
    Context context;
    //    String sdCardPath = "/mnt/asec/share";
    String sdCardPath = "/storage/emulated/0/Pictures";
    ArrayList<OrderItem> orderItems;
    int currentID;
    String childPath;

    @BindView(R.id.iv_fg2_leftup)
    ImageView iv_leftup;
    @BindView(R.id.iv_fg2_leftdown)
    ImageView iv_leftdown;
    @BindView(R.id.iv_fg2_rightup)
    ImageView iv_rightup;
    @BindView(R.id.iv_fg2_rightdown)
    ImageView iv_rightdown;
    @BindView(R.id.bt_remix)
    Button bt_remix;
    @BindView(R.id.iv_sample1)
    ImageView iv_sample1;
    @BindView(R.id.iv_sample2)
    ImageView iv_sample2;

    int width, height;
    int num;
    String strPlus = "";
    int intPlus = 1;
    String isKL;

    Paint paint,paintRed,paintBlue, rectPaint;
    String time = MainActivity.instance.orderDate_Print;


    @Override
    public int getLayout() {
        return R.layout.fragment1;
    }

    @Override
    public void initData(View view) {
        context = getContext();
        ButterKnife.bind(this, view);
        orderItems=MainActivity.instance.orderItems;
        currentID = MainActivity.instance.currentID;
        childPath = MainActivity.instance.childPath;
        isKL = orderItems.get(currentID).sku.equals("KLY") ? "KLY(MD鞋底) " : orderItems.get(currentID).sku.equals("KLY2") ? "KLY2(MD鞋底-无鞋盒) " : "";

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(38);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(38);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message,String sampleurl) {
                if(message==0){
                    iv_leftup.setImageDrawable(null);
                    iv_rightup.setImageDrawable(null);
                    Log.e("fragment_dd_old", "message0");
                } else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
                    if(!MainActivity.instance.cb_fastmode.isChecked()){
                        iv_leftup.setImageBitmap(MainActivity.instance.bitmaps.get(0));
                    }
                    checkremix();
                } else if (message==3){
                    bt_remix.setClickable(false);
                } else if (message == 10) {
                    remix();
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
                            intPlus += orderItems.get(i).num;
                        }
                    }
                    strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                    remixx();
                }
            }
        }.start();

    }


    void drawTextRR(Canvas canvasRR) {
        canvasRR.drawRect(100, 530, 1490, 570, rectPaint);
        canvasRR.drawText(isKL + time + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 100, 565, paint);
        canvasRR.drawText("童鞋" + orderItems.get(currentID).size + "码 " + orderItems.get(currentID).color + " 右外", 1150, 565, paintRed);
    }
    void drawTextRL(Canvas canvasRL) {
        canvasRL.drawRect(70, 530, 1490, 570, rectPaint);
        canvasRL.drawText("童鞋" + orderItems.get(currentID).size + "码 " + orderItems.get(currentID).color + " 右内", 70, 565, paintRed);
        canvasRL.drawText(isKL + time + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 460, 565, paint);
    }
    void drawTextLR(Canvas canvasLR) {
        canvasLR.drawRect(100, 530, 1490, 570, rectPaint);
        canvasLR.drawText(isKL + time + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 100, 565, paint);
        canvasLR.drawText("童鞋" + orderItems.get(currentID).size + "码 " + orderItems.get(currentID).color + " 左内", 1150, 565, paintRed);
    }
    void drawTextLL(Canvas canvasLL) {
        canvasLL.drawRect(70, 530, 1490, 570, rectPaint);
        canvasLL.drawText("童鞋" + orderItems.get(currentID).size + "码 " + orderItems.get(currentID).color + " 左外", 70, 565, paintRed);
        canvasLL.drawText( isKL + time + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 460, 565, paint);
    }

    public void remixx(){
        setSize(orderItems.get(currentID).size);
        Bitmap bitmapDBRight = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.de_child_r);
        Bitmap bitmapDBLeft = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.de_child_l);

        Bitmap bitmapCombine = Bitmap.createBitmap(width + 60, height * 4 + 60 * 4, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.size() == 2 && MainActivity.instance.bitmaps.get(0).getWidth() >= 2839) {//adam
            MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 1464, 530, true));
            MainActivity.instance.bitmaps.set(1, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(1), 1464, 530, true));

            //RR
            Bitmap bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 28, -28, null);
            canvasTemp.drawBitmap(bitmapDBRight, 0, 0, null);
            drawTextRR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 33, -28, null);
            canvasTemp.drawBitmap(bitmapDBLeft, 0, 0, null);
            drawTextRL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            Matrix matrixCombine = new Matrix();
            matrixCombine.reset();
            matrixCombine.postRotate(180);
            matrixCombine.postTranslate(width, height * 2 + 60);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 28, -28, null);
            canvasTemp.drawBitmap(bitmapDBRight, 0, 0, null);
            drawTextLR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            matrixCombine.reset();
            matrixCombine.postTranslate(0, height * 2 + 120);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //LL
            bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 33, -28, null);
            canvasTemp.drawBitmap(bitmapDBLeft, 0, 0, null);
            drawTextLL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            matrixCombine.reset();
            matrixCombine.postRotate(180);
            matrixCombine.postTranslate(width, height * 4 + 180);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            bitmapDBLeft.recycle();
            bitmapDBRight.recycle();
            bitmapTemp.recycle();
        } else if (MainActivity.instance.bitmaps.size() == 4) {
            //RR
            Bitmap bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDBRight, 0, 0, null);
            drawTextRR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDBLeft, 0, 0, null);
            drawTextRL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            Matrix matrixCombine = new Matrix();
            matrixCombine.reset();
            matrixCombine.postRotate(180);
            matrixCombine.postTranslate(width, height * 2 + 80);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDBRight, 0, 0, null);
            drawTextLR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            matrixCombine.reset();
            matrixCombine.postTranslate(0, height * 2 + 120);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //LL
            bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDBLeft, 0, 0, null);
            drawTextLL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            matrixCombine.reset();
            matrixCombine.postRotate(180);
            matrixCombine.postTranslate(width, height * 4 + 200);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            bitmapDBLeft.recycle();
            bitmapDBRight.recycle();
            bitmapTemp.recycle();
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
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku+orderItems.get(currentID).size+printColor);
            sheet.addCell(label1);
            Number number2 = new Number(2, currentID+1, orderItems.get(currentID).num);
            sheet.addCell(number2);
            Label label3 = new Label(3, currentID+1, orderItems.get(currentID).customer);
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
            case 28:
                width = 1065;
                height = 528;
                break;
            case 29:
                width = 1101;
                height = 537;
                break;
            case 30:
                width = 1137;
                height = 546;
                break;
            case 31:
                width = 1173;
                height = 556;
                break;
            case 32:
                width = 1208;
                height = 564;
                break;
            case 33:
                width = 1244;
                height = 574;
                break;
            case 34:
                width = 1280;
                height = 583;
                break;
        }
    }

}
