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

public class FragmentZ41 extends BaseFragment {
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

    int mainWidth,mainHeight,tongueWidth, tongueHeight;
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
        paint.setTextSize(28);
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
        String isLS = orderItems.get(currentID).sku.equals("LS") ? "(有缝线)" : "";

        paint.setTextSize(35);
        paintRed.setTextSize(35);

        canvas.save();
        canvas.rotate(-79.9f, 1381, 1285);
        canvas.drawRect(1381, 1285 - 35, 1381 + 580, 1285, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + isLS + "_" + orderItems.get(currentID).size + "码" + orderItems.get(currentID).color + LR + " " + time, 1381, 1285 - 5, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(79.8f, 63, 736);
        canvas.drawRect(63, 736 - 35, 63 + 510, 736, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 63, 736 - 5, paint);
        canvas.restore();
    }

    void drawTextTongue(Canvas canvas, String LR) {
        paint.setTextSize(23);
        paintRed.setTextSize(23);

        canvas.drawRect(276, 800 - 22, 276 + 110, 800, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + orderItems.get(currentID).color + LR, 276, 800 - 3, paint);
        canvas.drawRect(180, 773 - 22, 180 + 300, 773, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + time + " " + orderItems.get(currentID).order_number, 180, 773 - 3, paint);
    }

    public void remixx(){
        setSize(orderItems.get(currentID).size);

        int margin = 70;
        Bitmap bitmapCombine = Bitmap.createBitmap(mainWidth + margin, tongueHeight + margin + mainHeight + 10 + mainHeight, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.drawColor(0xffffffff);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));


        if (orderItems.get(currentID).imgs.size() == 4) {
            //leftMain
            Bitmap bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z41_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, tongueHeight + margin + mainHeight + 10, null);

            //leftTongue
            bitmapTemp = MainActivity.instance.bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z41_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, tongueWidth, tongueHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, margin, 0, null);

            //rightMain
            bitmapTemp = MainActivity.instance.bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z41_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, tongueHeight + margin, null);

            //rightTongue
            bitmapTemp = MainActivity.instance.bitmaps.get(3).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z41_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, tongueWidth, tongueHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, margin + tongueWidth + margin, 0, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        } else if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {//jj LS Z41
            //leftMain
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2120, 1496, 1537, 1975);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z41_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, tongueHeight + margin + mainHeight + 10, null);

            //leftTongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2566, 530, 645, 816);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z41_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, tongueWidth, tongueHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, margin, 0, null);

            //rightMain
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 344, 1494, 1537, 1975);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z41_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, tongueHeight + margin, null);

            //rightTongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 790, 530, 645, 816);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z41_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, tongueWidth, tongueHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, margin + tongueWidth + margin, 0, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 7600) {//john
            //leftMain
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3965, 408, 3463, 4292);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1537, 1975, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z41_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, tongueHeight + margin + mainHeight + 10, null);

            //leftTongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5138, 153, 1116, 1412);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 645, 816, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z41_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, tongueWidth, tongueHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, margin, 0, null);

            //rightMain
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 157, 408, 3463, 4292);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1537, 1975, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z41_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, tongueHeight + margin, null);

            //rightTongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1330, 153, 1116, 1412);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 645, 816, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z41_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, tongueWidth, tongueHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, margin + tongueWidth + margin, 0, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() < 7600) {//4u2批量
            //leftMain
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2182, 0, 1537, 1975);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z41_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, tongueHeight + margin + mainHeight + 10, null);

            //leftTongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1537, 581, 645, 816);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z41_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, tongueWidth, tongueHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, margin, 0, null);

            //rightMain
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 1537, 1975);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z41_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, tongueHeight + margin, null);

            //rightTongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1537, 581, 645, 816);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.z41_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, tongueWidth, tongueHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, margin + tongueWidth + margin, 0, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        }



        try {
            Matrix matrix90 = new Matrix();
            matrix90.postRotate(90);
            matrix90.postTranslate(bitmapCombine.getHeight(), 0);
            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix90, true);

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
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + orderItems.get(currentID).size);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + orderItems.get(currentID).size);
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

    void setSize(int size){
        switch (size) {
            case 35:
                mainWidth = 1378;
                mainHeight = 1717;
                tongueWidth = 579;
                tongueHeight = 704;
                break;
            case 36:
                mainWidth = 1405;
                mainHeight = 1759;
                tongueWidth = 579;
                tongueHeight = 704;
                break;
            case 37:
                mainWidth = 1424;
                mainHeight = 1803;
                tongueWidth = 602;
                tongueHeight = 742;
                break;
            case 38:
                mainWidth = 1458;
                mainHeight = 1845;
                tongueWidth = 602;
                tongueHeight = 742;
                break;
            case 39:
                mainWidth = 1485;
                mainHeight = 1889;
                tongueWidth = 623;
                tongueHeight = 779;
                break;
            case 40:
                mainWidth = 1512;
                mainHeight = 1931;
                tongueWidth = 623;
                tongueHeight = 779;
                break;
            case 41:
                mainWidth = 1537;
                mainHeight = 1975;
                tongueWidth = 645;
                tongueHeight = 816;
                break;
            case 42:
                mainWidth = 1564;
                mainHeight = 2018;
                tongueWidth = 645;
                tongueHeight = 816;
                break;
            case 43:
                mainWidth = 1591;
                mainHeight = 2061;
                tongueWidth = 666;
                tongueHeight = 853;
                break;
            case 44:
                mainWidth = 1617;
                mainHeight = 2106;
                tongueWidth = 666;
                tongueHeight = 853;
                break;
            case 45:
                mainWidth = 1644;
                mainHeight = 2150;
                tongueWidth = 688;
                tongueHeight = 891;
                break;
            case 46:
                mainWidth = 1671;
                mainHeight = 2194;
                tongueWidth = 688;
                tongueHeight = 891;
                break;
            case 47:
                mainWidth = 1671 + 27;
                mainHeight = 2194 + 43;
                tongueWidth = 688 + 22;
                tongueHeight = 891 + 37;
                break;
            case 48:
                mainWidth = 1671 + 27 * 2;
                mainHeight = 2194 + 43 * 2;
                tongueWidth = 688 + 22;
                tongueHeight = 891 + 37;
                break;

        }
    }

}
