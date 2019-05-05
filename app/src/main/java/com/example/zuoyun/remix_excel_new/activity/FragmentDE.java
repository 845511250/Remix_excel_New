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
import android.widget.SeekBar;

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

public class FragmentDE extends BaseFragment {
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
    @BindView(R.id.sb1)
    SeekBar sb1;
    @BindView(R.id.sbrotate1)
    SeekBar sbrotate1;
    @BindView(R.id.sb2)
    SeekBar sb2;
    @BindView(R.id.sbrotate2)
    SeekBar sbrotate2;

    int width, height;
    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint paint,paintRed, paintBlue, rectPaint;
    String time = MainActivity.instance.orderDate_Print;

    @Override
    public int getLayout() {
        return R.layout.fragment2;
    }

    @Override
    public void initData(View view) {
        context = getContext();
        ButterKnife.bind(this, view);
        orderItems=MainActivity.instance.orderItems;
        currentID = MainActivity.instance.currentID;
        childPath = MainActivity.instance.childPath;

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

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(30);
        paintBlue.setTypeface(Typeface.DEFAULT_BOLD);
        paintBlue.setAntiAlias(true);

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message,String sampleurl) {
                if(message==0){
                    iv_leftup.setImageDrawable(null);
                    iv_leftdown.setImageDrawable(null);
                    iv_rightup.setImageDrawable(null);
                    iv_rightdown.setImageDrawable(null);
                    Log.e("fragment2", "message0");
                }
                else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
                    if(!MainActivity.instance.cb_fastmode.isChecked()){
                        iv_leftup.setImageBitmap(MainActivity.instance.bitmaps.get(0));
                    }
                    checkremix();
                }
                else if (message==3){
                    bt_remix.setClickable(false);
                }
                else if (message == 10) {
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
        }.start();

    }

    void drawTextRR(Canvas canvasRR) {
        canvasRR.drawRect(1020, 520, 1450, 570, rectPaint);
        canvasRR.drawText(orderItems.get(currentID).newCode + " 右外", 1020, 565, paintRed);
        canvasRR.drawText(orderItems.get(currentID).size + orderItems.get(currentID).color + (orderItems.get(currentID).sku.equals("FX") ? "(新)" : ""), 1360, 565, paint);
        canvasRR.drawText(orderItems.get(currentID).order_number, 1160, 590, paint);
        canvasRR.drawRect(100, 520, 300, 570, rectPaint);
        canvasRR.drawText(time, 100, 565, paint);
    }
    void drawTextRL(Canvas canvasRL) {
        canvasRL.drawRect(70,520,450,570,rectPaint);
        canvasRL.drawText(orderItems.get(currentID).size+orderItems.get(currentID).color + (orderItems.get(currentID).sku.equals("FX") ? "(新)" : ""),70,565,paint);
        canvasRL.drawText("右内 " + orderItems.get(currentID).newCode, 230, 565, paintRed);
        canvasRL.drawText(orderItems.get(currentID).order_number,100,595,paint);
        canvasRL.drawRect(1100, 520, 1300, 570, rectPaint);
        canvasRL.drawText(time, 1100, 565, paint);
    }
    void drawTextLR(Canvas canvasLR) {
        canvasLR.drawRect(1020, 520, 1450, 570, rectPaint);
        canvasLR.drawText(orderItems.get(currentID).newCode + " 左内", 1020, 565, paintRed);
        canvasLR.drawText(orderItems.get(currentID).size + orderItems.get(currentID).color + (orderItems.get(currentID).sku.equals("FX") ? "(新)" : ""), 1360, 565, paint);
        canvasLR.drawText(orderItems.get(currentID).order_number, 1160, 590, paint);
        canvasLR.drawRect(100, 520, 300, 570, rectPaint);
        canvasLR.drawText(time, 100, 565, paint);
    }
    void drawTextLL(Canvas canvasLL) {
        canvasLL.drawRect(70, 520, 450, 570, rectPaint);
        canvasLL.drawText(orderItems.get(currentID).size + orderItems.get(currentID).color + (orderItems.get(currentID).sku.equals("FX") ? "(新)" : ""), 70, 565, paint);
        canvasLL.drawText("左外 " + orderItems.get(currentID).newCode, 230, 565, paintRed);
        canvasLL.drawText(orderItems.get(currentID).order_number, 100, 595, paint);
        canvasLL.drawRect(1100, 520, 1300, 570, rectPaint);
        canvasLL.drawText(time, 1100, 565, paint);
    }

    public void remixx(){
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapDBLeft = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.de41left);
        Bitmap bitmapDBRight = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.de41right);

        Bitmap bitmapCombine = Bitmap.createBitmap(width + 60, height * 4 + 60 * 4, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);
        Matrix matrix = new Matrix();

        if (orderItems.get(currentID).imgs.size() == 4) {
            //RR
            Bitmap bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(checkContains("RR") ? getBitmapWith("RR") : MainActivity.instance.bitmaps.get(3), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDBRight, 0, 0, null);
            drawTextRR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(checkContains("RL") ? getBitmapWith("RL") : MainActivity.instance.bitmaps.get(2), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDBLeft,0,0,null);
            drawTextRL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);

            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(width, height * 2 + 60);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(orderItems.get(currentID).platform.equals("4u2") ? MainActivity.instance.bitmaps.get(0) : checkContains("LR") ? getBitmapWith("LR") : MainActivity.instance.bitmaps.get(1), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDBRight, 0, 0, null);
            drawTextLR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);

            matrix.reset();
            matrix.postTranslate(0, height * 2 + 120);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
            bitmapDBRight.recycle();

            //LL
            bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(orderItems.get(currentID).platform.equals("4u2") ? MainActivity.instance.bitmaps.get(1) : checkContains("LL") ? getBitmapWith("LL") : MainActivity.instance.bitmaps.get(0), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDBLeft, 0, 0, null);
            drawTextLL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);

            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(width, height * 4 + 180);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();
            bitmapDBLeft.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            matrix.reset();
            matrix.postScale(-1, 1);
            matrix.postTranslate(MainActivity.instance.bitmaps.get(0).getWidth(), 0);

            //RR
            Bitmap bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            Canvas canvasRR = new Canvas(bitmapTemp);
            canvasRR.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRR.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);
            canvasRR.drawBitmap(bitmapDBRight, 0, 0, null);
            drawTextRR(canvasRR);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            Canvas canvasRL = new Canvas(bitmapTemp);
            canvasRL.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRL.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            canvasRL.drawBitmap(bitmapDBLeft,0,0,null);
            drawTextRL(canvasRL);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(width, height * 2 + 60);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //LR
            matrix.reset();
            matrix.postScale(-1, 1);
            matrix.postTranslate(MainActivity.instance.bitmaps.get(0).getWidth(), 0);

            bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            Canvas canvasLR = new Canvas(bitmapTemp);
            canvasLR.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLR.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);
            canvasLR.drawBitmap(bitmapDBRight, 0, 0, null);
            drawTextLR(canvasLR);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            matrix.reset();
            matrix.postTranslate(0, height * 2 + 120);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
            bitmapDBRight.recycle();

            //LL
            bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            Canvas canvasLL = new Canvas(bitmapTemp);
            canvasLL.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLL.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            canvasLL.drawBitmap(bitmapDBLeft, 0, 0, null);
            drawTextLL(canvasLL);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(width, height * 4 + 180);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();
            bitmapDBLeft.recycle();
        }

        try {
            String printColor = orderItems.get(currentID).color.equals("黑") ? "B" : "W";
            String noNewCode = orderItems.get(currentID).newCode.equals("") ? orderItems.get(currentID).sku + orderItems.get(currentID).size : "";
            String nameCombine = noNewCode + orderItems.get(currentID).newCode + orderItems.get(currentID).color + (orderItems.get(currentID).sku.equals("FX") ? "(新)" : "") + orderItems.get(currentID).order_number + strPlus + ".jpg";

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
            Label label0 = new Label(0, currentID+1, orderItems.get(currentID).order_number+orderItems.get(currentID).sku+orderItems.get(currentID).size+printColor);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku+orderItems.get(currentID).size+printColor);
            sheet.addCell(label1);
            Number number2 = new Number(2, currentID+1, orderItems.get(currentID).num);
            sheet.addCell(number2);
            Label label3 = new Label(3, currentID+1, "小左");
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
    void setSize(int size){
        switch (size) {
            case 35:
                width = 1341;
                height = 609;
                break;
            case 36:
                width = 1380;
                height = 620;
                break;
            case 37:
                width = 1414;
                height = 631;
                break;
            case 38:
                width = 1449;
                height = 640;
                break;
            case 39:
                width = 1485;
                height = 651;
                break;
            case 40:
                width = 1521;
                height = 661;
                break;
            case 41:
                width = 1557;
                height = 671;
                break;
            case 42:
                width = 1594;
                height = 683;
                break;
            case 43:
                width = 1629;
                height = 692;
                break;
            case 44:
                width = 1665;
                height = 702;
                break;
            case 45:
                width = 1701;
                height = 715;
                break;
            case 46:
                width = 1738;
                height = 724;
                break;
            case 47:
                width = 1774;
                height = 736;
                break;
            case 48:
                width = 1810;
                height = 746;
                break;
            case 49:
                width = 1846;
                height = 757;
                break;
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

}
