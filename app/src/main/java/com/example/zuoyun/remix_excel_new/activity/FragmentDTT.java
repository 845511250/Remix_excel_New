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

import static com.example.zuoyun.remix_excel_new.activity.MainActivity.getLastNewCode;

/**
 * Created by zuoyun on 2016/11/4.
 */

public class FragmentDTT extends BaseFragment {
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

    Paint paint,paintRed, rectPaint;
    String time;

    int width_main, height_main,width_side,height_side;
    int width_sideNew,height_sideNew;

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
        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(40);
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
        //Bitmap bitmapBarCode = BarCodeUtil.creatBarcode(orderItems.get(currentID).order_number.replace("_", ""), 500, 50);



        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_left.setImageDrawable(null);
                    iv_right.setImageDrawable(null);
                    Log.e("fragment2", "message0");
                } else if (message == MainActivity.LOADED_IMGS) {
                    Log.e("fragment2", "message2");
                    bt_remix.setClickable(true);
                    if(!MainActivity.instance.cb_fastmode.isChecked())
                        iv_right.setImageBitmap(MainActivity.instance.bitmaps.get(1));
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


    void drawTextLeftSide(Canvas canvas){
        canvas.drawRect(1655, 85, 1800, 115, rectPaint);
        canvas.drawText(getLastNewCode("左 " + orderItems.get(currentID).newCode), 1656, 111, paintRed);
    }
    void drawTextLeftMain(Canvas canvasLeftMain){
        canvasLeftMain.drawRect(615, 1170, 1011, 1212, rectPaint);
        canvasLeftMain.drawText("     左", 615, 1206, paintRed);
        canvasLeftMain.drawText(orderItems.get(currentID).size + "码" + orderItems.get(currentID).color, 750, 1206, paint);
        canvasLeftMain.save();
        canvasLeftMain.rotate(-68.5f, 1296, 882);
        canvasLeftMain.drawRect(1298, 832, 1800, 882, rectPaint);
        canvasLeftMain.drawText(orderItems.get(currentID).order_number + "     " + time, 1300, 876, paint);
        canvasLeftMain.restore();

        canvasLeftMain.save();
        canvasLeftMain.rotate(-111.5f, 373, 862);
        canvasLeftMain.drawRect(373, 800, 873, 850, rectPaint);
        //canvasLeftMain.drawBitmap(bitmapBarCode, 373, 800, null);
        canvasLeftMain.drawText(orderItems.get(currentID).newCode, 373, 846, paintRed);
        canvasLeftMain.restore();
    }
    void drawTextRightSide(Canvas canvasRightSide){
        canvasRightSide.drawRect(1655, 85, 1800, 115, rectPaint);
        canvasRightSide.drawText("右 " + getLastNewCode(orderItems.get(currentID).newCode), 1656, 111, paintRed);
    }
    void drawTextRightMain(Canvas canvasRightMain){
        canvasRightMain.drawRect(615, 1170, 1011, 1212, rectPaint);
        canvasRightMain.drawText(orderItems.get(currentID).size + "码" + orderItems.get(currentID).color, 615, 1206, paint);
        canvasRightMain.drawText(" 右", 860, 1206, paintRed);
        canvasRightMain.save();
        canvasRightMain.rotate(-111.5f, 373, 862);
        canvasRightMain.drawRect(373, 800, 873, 850, rectPaint);
        canvasRightMain.drawText(orderItems.get(currentID).order_number + "     " + time, 375, 844, paint);
        canvasRightMain.restore();

        canvasRightMain.save();
        canvasRightMain.rotate(-68.5f, 1296, 882);
        canvasRightMain.drawRect(1298, 832, 1800, 882, rectPaint);
        //canvasRightMain.drawBitmap(bitmapBarCode, 1298, 832, null);
        canvasRightMain.drawText(orderItems.get(currentID).newCode, 1298, 878, paintRed);
        canvasRightMain.restore();
    }


    public void remixx(){
        setScale(orderItems.get(currentID).size);
        int margin = 60;
        Matrix matrixCombine = new Matrix();

        Bitmap bitmapCombine = Bitmap.createBitmap(width_sideNew + height_main * 2 + margin, width_main + 100, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 1613, 1942, true));
        MainActivity.instance.bitmaps.set(1, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(1), 1613, 1942, true));
        //main:1612*1262
        //side:2172*578
        Bitmap bitmapDB_main = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.df41_main);
        Bitmap bitmapDB_side = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dt_side_new);

        //left_main
        Bitmap bitmapLeftMain = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 680, 1612, 1262);
        Canvas canvasLeftMain = new Canvas(bitmapLeftMain);
        canvasLeftMain.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasLeftMain.drawBitmap(bitmapDB_main, 0, 0, null);
        drawTextLeftMain(canvasLeftMain);
        bitmapLeftMain = Bitmap.createScaledBitmap(bitmapLeftMain, width_main, height_main, true);

        matrixCombine.reset();
        matrixCombine.postRotate(-90);
        matrixCombine.postTranslate(width_sideNew, width_main);
        canvasCombine.drawBitmap(bitmapLeftMain, matrixCombine, null);
        bitmapLeftMain.recycle();

        //left_side
        Bitmap bitmapLeftSide1 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 578, 1086);
        Bitmap bitmapLeftSide2 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1034, 0, 578, 1086);
        Bitmap bitmapLeftSide = Bitmap.createBitmap(2172, 578, Bitmap.Config.ARGB_8888);
        Canvas canvasLeftSide = new Canvas(bitmapLeftSide);
        canvasLeftSide.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        Matrix matrixSide = new Matrix();
        matrixSide.postRotate(90);
        matrixSide.postTranslate(1086, 0);
        canvasLeftSide.drawBitmap(bitmapLeftSide1, matrixSide, null);
        matrixSide.reset();
        matrixSide.postRotate(-90);
        matrixSide.postTranslate(1086, 578);
        canvasLeftSide.drawBitmap(bitmapLeftSide2, matrixSide, null);
        canvasLeftSide.drawBitmap(bitmapDB_side, 0, 0, null);
        drawTextLeftSide(canvasLeftSide);
        bitmapLeftSide = Bitmap.createScaledBitmap(bitmapLeftSide, width_sideNew, height_sideNew, true);

        canvasCombine.drawBitmap(bitmapLeftSide, 0, 0, null);
        bitmapLeftSide1.recycle();
        bitmapLeftSide2.recycle();
        bitmapLeftSide.recycle();

        //right_main
        Bitmap bitmapRightMain = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 680, 1612, 1262);
        Canvas canvasRightMain = new Canvas(bitmapRightMain);
        canvasRightMain.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasRightMain.drawBitmap(bitmapDB_main, 0, 0, null);
        drawTextRightMain(canvasRightMain);
        bitmapRightMain = Bitmap.createScaledBitmap(bitmapRightMain, width_main, height_main, true);

        matrixCombine.reset();
        matrixCombine.postRotate(-90);
        matrixCombine.postTranslate(width_sideNew + height_main + margin, width_main);
        canvasCombine.drawBitmap(bitmapRightMain, matrixCombine, null);
        bitmapRightMain.recycle();
        bitmapDB_main.recycle();

        //right_side
        Bitmap bitmapRightSide1 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, 578, 1086);
        Bitmap bitmapRightSide2 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1034, 0, 578, 1086);
        Bitmap bitmapRightSide = Bitmap.createBitmap(2172, 578, Bitmap.Config.ARGB_8888);
        Canvas canvasRightSide = new Canvas(bitmapRightSide);
        canvasRightSide.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        matrixSide.reset();
        matrixSide.postRotate(90);
        matrixSide.postTranslate(1086, 0);
        canvasRightSide.drawBitmap(bitmapRightSide1, matrixSide, null);
        matrixSide.reset();
        matrixSide.postRotate(-90);
        matrixSide.postTranslate(1086, 578);
        canvasRightSide.drawBitmap(bitmapRightSide2, matrixSide, null);
        canvasRightSide.drawBitmap(bitmapDB_side, 0, 0, null);
        drawTextRightSide(canvasRightSide);
        bitmapRightSide = Bitmap.createScaledBitmap(bitmapRightSide, width_sideNew, height_sideNew, true);

        matrixCombine.reset();
        matrixCombine.postTranslate(0, height_sideNew + 130);
        canvasCombine.drawBitmap(bitmapRightSide, matrixCombine, null);
        bitmapRightSide1.recycle();
        bitmapRightSide2.recycle();
        bitmapRightSide.recycle();
        bitmapDB_side.recycle();


        try {
            String printColor = orderItems.get(currentID).color.equals("黑") ? "B" : "W";
            String nameCombine = orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor + orderItems.get(currentID).order_number + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 149);

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
    void setScale(int size){
        switch (size) {
            case 36:
                width_main = 1488;
                height_main = 1121;
                width_side = 1934;
                height_side = 535;
                width_sideNew = 1934;
                height_sideNew = 562;
                break;
            case 37:
                width_main = 1511;
                height_main = 1151;
                width_side = 1979;
                height_side = 544;
                width_sideNew = 1979;
                height_sideNew = 573;
                break;
            case 38:
                width_main = 1538;
                height_main = 1177;
                width_side = 2025;
                height_side = 550;
                width_sideNew = 2025;
                height_sideNew = 586;
                break;
            case 39:
                width_main = 1563;
                height_main = 1208;
                width_side = 2073;
                height_side = 560;
                width_sideNew = 2073;
                height_sideNew = 599;
                break;
            case 40:
                width_main = 1590;
                height_main = 1240;
                width_side = 2119;
                height_side = 571;
                width_sideNew = 2119;
                height_sideNew = 612;
                break;
            case 41:
                width_main = 1612;
                height_main = 1262;
                width_side = 2167;
                height_side = 581;
                width_sideNew = 2167;
                height_sideNew = 626;
                break;
            case 42:
                width_main = 1638;
                height_main = 1286;
                width_side = 2213;
                height_side = 592;
                width_sideNew = 2213;
                height_sideNew = 640;
                break;
            case 43:
                width_main = 1664;
                height_main = 1320;
                width_side = 2261;
                height_side = 602;
                width_sideNew = 2261;
                height_sideNew = 653;
                break;
            case 44:
                width_main = 1687;
                height_main = 1350;
                width_side = 2306;
                height_side = 613;
                width_sideNew = 2306;
                height_sideNew = 667;
                break;
            case 45:
                width_main = 1710;
                height_main = 1374;
                width_side = 2352;
                height_side = 621;
                width_sideNew = 2352;
                height_sideNew = 679;
                break;
            case 46:
                width_main = 1740;
                height_main = 1410;
                width_side = 2400;
                height_side = 627;
                width_sideNew = 2400;
                height_sideNew = 691;
                break;
        }
    }

}
