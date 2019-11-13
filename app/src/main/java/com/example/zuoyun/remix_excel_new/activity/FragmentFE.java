package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

public class FragmentFE extends BaseFragment {
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

    int mianWidth, mainHeight, sideWidth, sideHeight;
    int x_l_main, x_ll, x_lr, x_rr, x_rl, x_r_main;
    int y_l_main, y_ll, y_lr, y_rr, y_rl, y_r_main;
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
        paint.setTextSize(26);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(26);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff000000);
        paintBlue.setTextSize(26);
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
                    iv_rightup.setImageDrawable(null);
                } else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
                    if(!MainActivity.instance.cb_fastmode.isChecked())
                        iv_leftup.setImageBitmap(MainActivity.instance.bitmaps.get(0));
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

    void drawTextMain(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(60.6f, 6, 1026);
        canvas.drawRect(6, 1026 - 26, 216, 1026, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number, 6, 1024, paint);
        canvas.restore();

        canvas.drawRect(458, 1415-26, 600, 1415, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "码" + LR + orderItems.get(currentID).color, 458, 1413, paint);

        canvas.save();
        canvas.rotate(-61.9f, 929, 1165);
        canvas.drawRect(929, 1165-26, 1070, 1165, rectPaint);
        canvas.drawText(time, 929, 1163, paintRed);
        canvas.restore();
    }

    void drawTextR(Canvas canvas, String LR) {
        canvas.drawRect(100, 562 - 26, 100 + 500, 562, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).size + "码 " + LR + orderItems.get(currentID).color, 100, 560, paint);
    }

    void drawTextL(Canvas canvas, String LR) {
        canvas.drawRect(1000, 562 - 26, 1000 + 500, 562, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).size + "码 " + LR + orderItems.get(currentID).color, 1000, 560, paint);
    }
    public void remixx(){
        setScale(orderItems.get(currentID).size);

        Bitmap bitmapCombine;
        bitmapCombine = Bitmap.createBitmap(5556, 1525, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.drawColor(0xffffffff);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        Bitmap bitmapDB_main = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lightlow_tongue);
        Bitmap bitmapDB_l = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lightlow_l);
        Bitmap bitmapDB_r = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lightlow_r);

        if (orderItems.get(currentID).imgs.size() == 6 && orderItems.get(currentID).platform.equals("4u2")) {
            //left mian
            Bitmap bitmapTemp = Bitmap.createBitmap(1014, 1460, Bitmap.Config.ARGB_8888);
            Canvas canvasLeft_main = new Canvas(bitmapTemp);
            canvasLeft_main.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLeft_main.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            canvasLeft_main.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasLeft_main, "左");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_l_main, y_l_main, null);

            //lr
            bitmapTemp = Bitmap.createBitmap(1488, 648, Bitmap.Config.ARGB_8888);
            Canvas canvasLR = new Canvas(bitmapTemp);
            canvasLR.drawColor(0xffffffff);
            canvasLR.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLR.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            canvasLR.drawBitmap(bitmapDB_r, 0, 0, null);
            drawTextR(canvasLR, "左");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_lr, y_lr, null);

            //ll
            bitmapTemp = Bitmap.createBitmap(1488, 648, Bitmap.Config.ARGB_8888);
            Canvas canvasLL = new Canvas(bitmapTemp);
            canvasLL.drawColor(0xffffffff);
            canvasLL.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLL.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            canvasLL.drawBitmap(bitmapDB_l, 0, 0, null);
            drawTextL(canvasLL, "左");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_ll, y_ll, null);

            //right main
            bitmapTemp = Bitmap.createBitmap(1014, 1460, Bitmap.Config.ARGB_8888);
            Canvas canvasRight_main = new Canvas(bitmapTemp);
            canvasRight_main.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRight_main.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            canvasRight_main.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasRight_main, "右");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_r_main, y_r_main, null);
            bitmapDB_main.recycle();

            //rr
            bitmapTemp = Bitmap.createBitmap(1488, 648, Bitmap.Config.ARGB_8888);
            Canvas canvasRR = new Canvas(bitmapTemp);
            canvasRR.drawColor(0xffffffff);
            canvasRR.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRR.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            canvasRR.drawBitmap(bitmapDB_r, 0, 0, null);
            drawTextR(canvasRR, "右");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rr, y_rr, null);
            bitmapDB_r.recycle();

            //rl
            bitmapTemp = Bitmap.createBitmap(1488, 648, Bitmap.Config.ARGB_8888);
            Canvas canvasRL = new Canvas(bitmapTemp);
            canvasRL.drawColor(0xffffffff);
            canvasRL.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRL.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            canvasRL.drawBitmap(bitmapDB_l, 0, 0, null);
            drawTextL(canvasRL, "右");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rl, y_rl, null);
            bitmapTemp.recycle();
            bitmapDB_l.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1 && orderItems.get(currentID).platform.equals("4u2")) {
            //left mian
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1488, 0, 1014, 1460);
            Canvas canvasLeft_main = new Canvas(bitmapTemp);
            canvasLeft_main.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLeft_main.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasLeft_main, "左");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_l_main, y_l_main, null);

            //lr
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 648, 1488, 648);
            Canvas canvasLR = new Canvas(bitmapTemp);
            canvasLR.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLR.drawBitmap(bitmapDB_r, 0, 0, null);
            drawTextR(canvasLR, "左");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_lr, y_lr, null);

            //ll
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 1488, 648);
            Canvas canvasLL = new Canvas(bitmapTemp);
            canvasLL.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLL.drawBitmap(bitmapDB_l, 0, 0, null);
            drawTextL(canvasLL, "左");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_ll, y_ll, null);

            //right main
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1488, 0, 1014, 1460);
            Canvas canvasRight_main = new Canvas(bitmapTemp);
            canvasRight_main.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRight_main.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasRight_main, "右");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_r_main, y_r_main, null);
            bitmapDB_main.recycle();

            //rr
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2502, 0, 1488, 648);
            Canvas canvasRR = new Canvas(bitmapTemp);
            canvasRR.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRR.drawBitmap(bitmapDB_r, 0, 0, null);
            drawTextR(canvasRR, "右");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rr, y_rr, null);
            bitmapDB_r.recycle();

            //rl
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2502, 648, 1488, 648);
            Canvas canvasRL = new Canvas(bitmapTemp);
            canvasRL.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRL.drawBitmap(bitmapDB_l, 0, 0, null);
            drawTextL(canvasRL, "右");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rl, y_rl, null);
            bitmapTemp.recycle();
            bitmapDB_l.recycle();
        } else {
            //left mian
            Bitmap bitmapTemp = Bitmap.createBitmap(1014, 1460, Bitmap.Config.ARGB_8888);
            Canvas canvasLeft_main = new Canvas(bitmapTemp);
            canvasLeft_main.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLeft_main.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            canvasLeft_main.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasLeft_main, "左");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_l_main, y_l_main, null);

            //lr
            bitmapTemp = Bitmap.createBitmap(1488, 648, Bitmap.Config.ARGB_8888);
            Canvas canvasLR = new Canvas(bitmapTemp);
            canvasLR.drawColor(0xffffffff);
            canvasLR.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLR.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            canvasLR.drawBitmap(bitmapDB_r, 0, 0, null);
            drawTextR(canvasLR, "左");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_lr, y_lr, null);

            //ll
            bitmapTemp = Bitmap.createBitmap(1488, 648, Bitmap.Config.ARGB_8888);
            Canvas canvasLL = new Canvas(bitmapTemp);
            canvasLL.drawColor(0xffffffff);
            canvasLL.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasLL.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            canvasLL.drawBitmap(bitmapDB_l, 0, 0, null);
            drawTextL(canvasLL, "左");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_ll, y_ll, null);

            //right main
            bitmapTemp = Bitmap.createBitmap(1014, 1460, Bitmap.Config.ARGB_8888);
            Canvas canvasRight_main = new Canvas(bitmapTemp);
            canvasRight_main.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRight_main.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            canvasRight_main.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasRight_main, "右");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_r_main, y_r_main, null);
            bitmapDB_main.recycle();

            //rr
            bitmapTemp = Bitmap.createBitmap(1488, 648, Bitmap.Config.ARGB_8888);
            Canvas canvasRR = new Canvas(bitmapTemp);
            canvasRR.drawColor(0xffffffff);
            canvasRR.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRR.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            canvasRR.drawBitmap(bitmapDB_r, 0, 0, null);
            drawTextR(canvasRR, "右");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rr, y_rr, null);
            bitmapDB_r.recycle();

            //rl
            bitmapTemp = Bitmap.createBitmap(1488, 648, Bitmap.Config.ARGB_8888);
            Canvas canvasRL = new Canvas(bitmapTemp);
            canvasRL.drawColor(0xffffffff);
            canvasRL.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRL.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            canvasRL.drawBitmap(bitmapDB_l, 0, 0, null);
            drawTextL(canvasRL, "右");

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, x_rl, y_rl, null);
            bitmapTemp.recycle();
            bitmapDB_l.recycle();
        }


        try {
            String printColor = orderItems.get(currentID).color.equals("黑") ? "B" : "W";
            String noNewCode = orderItems.get(currentID).newCode.equals("") ? orderItems.get(currentID).sku + orderItems.get(currentID).size + "_" : "";
            String nameCombine = noNewCode + orderItems.get(currentID).sku + "_" + orderItems.get(currentID).newCode + orderItems.get(currentID).color + orderItems.get(currentID).order_number + strPlus + ".jpg";

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
            Label label3 = new Label(3, currentID+1, orderItems.get(currentID).customer);
            sheet.addCell(label3);
            Label label4 = new Label(4, currentID + 1, MainActivity.instance.orderDate_Excel);
            sheet.addCell(label4);
            Label label6 = new Label(6, currentID+1, "平台大货");
            sheet.addCell(label6);

            workbook.write();
            workbook.close();

        }catch (Exception e){
            Log.e("aaa", e.toString());
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

    String getColor(String color){
        if (color.equals("White")) {
            return "白灯";
        } else if (color.equals("Green")) {
            return "绿灯";
        } else if (color.equals("Blue")) {
            return "蓝灯";
        } else if (color.equals("Red")) {
            return "红灯";
        } else {
            return "无灯";
        }
    }

    void setScale(int size){
        switch (size) {
            case 36:
                mianWidth = 916;
                mainHeight = 1220;
                sideWidth = 1310;
                sideHeight = 608;
                x_l_main = 85;
                y_l_main = 153;
                x_lr = 1262;
                y_lr = 98;
                x_ll = 1259;
                y_ll = 798;
                x_rr = 2952;
                y_rr = 98;
                x_rl = 2950;
                y_rl = 820;
                x_r_main = 4557;
                y_r_main = 153;
                break;
            case 37:
                mianWidth = 932;
                mainHeight = 1251;
                sideWidth = 1346;
                sideHeight = 619;
                x_l_main = 77;
                y_l_main = 138;
                x_lr = 1244;
                y_lr = 87;
                x_ll = 1241;
                y_ll = 787;
                x_rr = 2934;
                y_rr = 87;
                x_rl = 2932;
                y_rl = 809;
                x_r_main = 4550;
                y_r_main = 138;
                break;
            case 38:
                mianWidth = 950;
                mainHeight = 1280;
                sideWidth = 1380;
                sideHeight = 630;
                x_l_main = 68;
                y_l_main = 123;
                x_lr = 1226;
                y_lr = 76;
                x_ll = 1224;
                y_ll = 780;
                x_rr = 2917;
                y_rr = 76;
                x_rl = 2915;
                y_rl = 798;
                x_r_main = 4541;
                y_r_main = 123;
                break;
            case 39:
                mianWidth = 967;
                mainHeight = 1311;
                sideWidth = 1414;
                sideHeight = 643;
                x_l_main = 60;
                y_l_main = 108;
                x_lr = 1209;
                y_lr = 63;
                x_ll = 1207;
                y_ll = 768;
                x_rr = 2899;
                y_rr = 63;
                x_rl = 2898;
                y_rl = 785;
                x_r_main = 4532;
                y_r_main = 106;
                break;
            case 40:
                mianWidth = 984;
                mainHeight = 1341;
                sideWidth = 1449;
                sideHeight = 656;
                x_l_main = 53;
                y_l_main = 91;
                x_lr = 1191;
                y_lr = 50;
                x_ll = 1190;
                y_ll = 762;
                x_rr = 2882;
                y_rr = 50;
                x_rl = 2881;
                y_rl = 762;
                x_r_main = 4524;
                y_r_main = 91;
                break;
            case 41:
                mianWidth = 1000;
                mainHeight = 1373;
                sideWidth = 1484;
                sideHeight = 666;
                x_l_main = 43;
                y_l_main = 76;
                x_lr = 1173;
                y_lr = 40;
                x_ll = 1173;
                y_ll = 757;
                x_rr = 2864;
                y_rr = 40;
                x_rl = 2863;
                y_rl = 762;
                x_r_main = 4516;
                y_r_main = 76;
                break;
            case 42:
                mianWidth = 1019;
                mainHeight = 1402;
                sideWidth = 1520;
                sideHeight = 676;
                x_l_main = 34;
                y_l_main = 62;
                x_lr = 1156;
                y_lr = 35;
                x_ll = 1155;
                y_ll = 752;
                x_rr = 2846;
                y_rr = 30;
                x_rl = 2845;
                y_rl = 762;
                x_r_main = 4506;
                y_r_main = 61;
                break;
            case 43:
                mianWidth = 1035;
                mainHeight = 1434;
                sideWidth = 1555;
                sideHeight = 688;
                x_l_main = 27;
                y_l_main = 45;
                x_lr = 1138;
                y_lr = 23;
                x_ll = 1138;
                y_ll = 746;
                x_rr = 2828;
                y_rr = 18;
                x_rl = 2828;
                y_rl = 746;
                x_r_main = 4498;
                y_r_main = 46;
                break;
            case 44:
                mianWidth = 1052;
                mainHeight = 1464;
                sideWidth = 1589;
                sideHeight = 699;
                x_l_main = 17;
                y_l_main = 30;
                x_lr = 1121;
                y_lr = 16;
                x_ll = 1121;
                y_ll = 735;
                x_rr = 2811;
                y_rr = 12;
                x_rl = 2810;
                y_rl = 735;
                x_r_main = 4490;
                y_r_main = 30;
                break;
            case 45:
                mianWidth = 1070;
                mainHeight = 1494;
                sideWidth = 1623;
                sideHeight = 712;
                x_l_main = 10;
                y_l_main = 16;
                x_lr = 1105;
                y_lr = 6;
                x_ll = 1105;
                y_ll = 728;
                x_rr = 2794;
                y_rr = 5;
                x_rl = 2794;
                y_rl = 728;
                x_r_main = 4481;
                y_r_main = 16;
                break;
            case 46:
                mianWidth = 1087;
                mainHeight = 1525;
                sideWidth = 1659;
                sideHeight = 723;
                x_l_main = 0;
                y_l_main = 0;
                x_lr = 1087;
                y_lr = 0;
                x_ll = 1087;
                y_ll = 723;
                x_rr = 2776;
                y_rr = 0;
                x_rl = 2776;
                y_rl = 723;
                x_r_main = 4469;
                y_r_main = 0;
                break;
        }
    }

}
