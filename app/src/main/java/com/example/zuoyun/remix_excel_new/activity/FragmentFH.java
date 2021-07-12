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

public class FragmentFH extends BaseFragment {
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
        paintRed.setTextSize(30);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff000000);
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
                    iv_rightup.setImageDrawable(null);
                    Log.e("fragment_dq", "message0");
                }else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
                    if(!MainActivity.instance.cb_fastmode.isChecked())
                        iv_rightup.setImageBitmap(MainActivity.instance.bitmaps.get(0));
                    checkremix();
                } else if (message == 3) {
                    bt_remix.setClickable(false);
                } else if(message==MainActivity.LOADED_IMGS){
                    checkremix();
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

    void drawTextMain(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(74.6f, 20, 577);
        canvas.drawRect(30, 545, 400, 575, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).order_number, 30, 572, paint);
        canvas.restore();

        canvas.drawRect(554, 1300, 694, 1330, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "码 " + LR + " " + orderItems.get(currentID).color, 554, 1327, paint);

        canvas.save();
        canvas.rotate(-75f, 1105, 950);
        canvas.drawRect(1105, 920, 1485, 950, rectPaint);
        canvas.drawText(orderItems.get(currentID).newCode, 1105, 947, paintRed);
        canvas.restore();
    }

    void drawTextL(Canvas canvas, String LR) {
        canvas.drawRect(720, 565, 1200, 595, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "码" + LR + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number + " " + time, 720, 592, paint);

        canvas.drawRect(270, 630, 640, 660, rectPaint);
        canvas.drawText(orderItems.get(currentID).newCode , 270, 657, paintRed);
    }

    void drawTextR(Canvas canvas, String LR) {
        canvas.drawRect(80, 565, 560, 595, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "码" + LR + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number + " " + time, 80, 592, paint);

        canvas.drawRect(645, 630, 1020, 660, rectPaint);
        canvas.drawText(orderItems.get(currentID).newCode, 645, 657, paintRed);
    }
    public void remixx(){
        setSize(orderItems.get(currentID).size);

        int margin = 70;

        Bitmap bitmapCombine;
        bitmapCombine = Bitmap.createBitmap(mianWidth + sideWidth + margin, mainHeight * 2 + margin, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.drawColor(0xffffffff);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        if (MainActivity.instance.bitmaps.get(0).getWidth() == 2400) {//adam
            //left main
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 582, 753, 1235, 1401);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //LL
            bitmapTemp = Bitmap.createBitmap(1289, 690, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1535, 50, 847, 1188);
            Matrix matrix = new Matrix();
            matrix.postRotate(68.2f);
            matrix.postTranslate(1080, -250);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextL(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mianWidth + margin, 0, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(1289, 690, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 18, 48, 847, 1188);
            matrix.reset();
            matrix.postRotate(-68.2f);
            matrix.postTranslate(-107, 538);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextR(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mianWidth + margin, sideHeight, null);


            //right main
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 582, 753, 1235, 1401);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, mainHeight + margin, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(1289, 690, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1535, 50, 847, 1188);
            matrix.reset();
            matrix.postRotate(68.2f);
            matrix.postTranslate(1080, -250);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextL(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mianWidth + margin, mainHeight + margin, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(1289, 690, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 18, 48, 847, 1188);
            matrix.reset();
            matrix.postRotate(-68.2f);
            matrix.postTranslate(-107, 538);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextR(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mianWidth + margin, mainHeight + margin + sideHeight, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();
            bitmapCut.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 6) {
            //left main
            Bitmap bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 1235, 1401, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //LL
            bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(2), 1289, 690, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextL(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mianWidth + margin, 0, null);

            //LR
            bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(1), 1289, 690, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextR(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mianWidth + margin, sideHeight, null);

            //right main
            bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(3), 1235, 1401, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, mainHeight + margin, null);

            //RL
            bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(4), 1289, 690, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextL(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mianWidth + margin, mainHeight + margin, null);

            //RR
            bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(5), 1289, 690, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextR(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mianWidth + margin, mainHeight + margin + sideHeight, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight() && MainActivity.instance.bitmaps.get(0).getWidth() == 4211) {
            //left main
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1455, 575, 1299, 1472);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1235, 1401, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //LL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2754, 574, 1327, 700);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1289, 690, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextL(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mianWidth + margin, 0, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 128, 574, 1327, 700);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1289, 690, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextR(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mianWidth + margin, sideHeight, null);

            //right main
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1455, 2164, 1299, 1472);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1235, 1401, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, mainHeight + margin, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2754, 2163, 1327, 700);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1289, 690, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextL(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mianWidth + margin, mainHeight + margin, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 128, 2163, 1327, 700);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1289, 690, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fh_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextR(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, sideWidth, sideHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mianWidth + margin, mainHeight + margin + sideHeight, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (MainActivity.instance.bitmaps.size() == 1) {

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
            BitmapToJpg.save(bitmapCombine, fileSave, 149);



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
            Label label6 = new Label(6, currentID + 1, "平台大货");
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

    void setSize(int size){
        switch (size) {
            case 35:
                mianWidth = 1165;
                mainHeight = 1263;
                sideWidth = 1120;
                sideHeight = 626;
                break;
            case 36:
                mianWidth = 1184;
                mainHeight = 1290;
                sideWidth = 1148;
                sideHeight = 637;
                break;
            case 37:
                mianWidth = 1203;
                mainHeight = 1320;
                sideWidth = 1179;
                sideHeight = 646;
                break;
            case 38:
                mianWidth = 1223;
                mainHeight = 1350;
                sideWidth = 1207;
                sideHeight = 657;
                break;
            case 39:
                mianWidth = 1241;
                mainHeight = 1376;
                sideWidth = 1241;
                sideHeight = 670;
                break;
            case 40:
                mianWidth = 1262;
                mainHeight = 1408;
                sideWidth = 1269;
                sideHeight = 682;
                break;
            case 41:
                mianWidth = 1280;
                mainHeight = 1440;
                sideWidth = 1300;
                sideHeight = 694;
                break;
            case 42:
                mianWidth = 1299;
                mainHeight = 1472;
                sideWidth = 1327;
                sideHeight = 700;
                break;
            case 43:
                mianWidth = 1319;
                mainHeight = 1504;
                sideWidth = 1358;
                sideHeight = 713;
                break;
            case 44:
                mianWidth = 1337;
                mainHeight = 1535;
                sideWidth = 1389;
                sideHeight = 724;
                break;
            case 45:
                mianWidth = 1356;
                mainHeight = 1567;
                sideWidth = 1419;
                sideHeight = 736;
                break;
            case 46:
                mianWidth = 1376;
                mainHeight = 1600;
                sideWidth = 1447;
                sideHeight = 745;
                break;
            case 47:
                mianWidth = 1425;
                mainHeight = 1632;
                sideWidth = 1477;
                sideHeight = 760;
                break;
            case 48:
                mianWidth = 1451;
                mainHeight = 1664;
                sideWidth = 1506;
                sideHeight = 769;
                break;
        }
    }

}
