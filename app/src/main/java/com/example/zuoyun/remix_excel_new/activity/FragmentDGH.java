package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
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
 * Created by zuoyun on 2016/11/4.
 */

public class FragmentDGH extends BaseFragment {
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

    int num;
    String strPlus = "";
    int intPlus = 1;

    int width_DH;

    Paint rectPaint, paint, paintSmall;
    String time;

    @Override
    public int getLayout() {
        return R.layout.fragmentdg;
    }

    @Override
    public void initData(View view) {
        context = getContext();
        ButterKnife.bind(this, view);
        orderItems=MainActivity.instance.orderItems;
        currentID = MainActivity.instance.currentID;
        childPath = MainActivity.instance.childPath;

        time = MainActivity.instance.orderDate_Print;

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(23);
        paint.setAntiAlias(true);

        paintSmall = new Paint();
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(20);
        paintSmall.setAntiAlias(true);


        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                } else if (message == MainActivity.LOADED_IMGS) {
                    Log.e("fragmentDG", "message4");
                    if(!MainActivity.instance.cb_fastmode.isChecked())
                        iv_pillow.setImageBitmap(MainActivity.instance.bitmaps.get(0));
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

    void drawTextDG(Canvas canvasCombine, String str) {
        canvasCombine.drawRect(1000, 23, 1000 + 400, 23 - 19, rectPaint);
        canvasCombine.drawText(orderItems.get(currentID).sku + str + " " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 1000, 23 - 2, paint);
    }
    void drawTextDH(Canvas canvasCombine) {
        canvasCombine.drawRect(1000, width_DH - 4 - 23, 1000 + 500, width_DH - 4, rectPaint);
        canvasCombine.drawText(orderItems.get(currentID).sku + "购物袋 " + time + " " + orderItems.get(currentID).order_number, 1000, width_DH - 4 - 2, paint);

        canvasCombine.drawRect(3600, width_DH - 4 - 23, 3600 + 500, width_DH - 4, rectPaint);
        canvasCombine.drawText(orderItems.get(currentID).sku + "购物袋 " + time + " " + orderItems.get(currentID).order_number, 3600, width_DH - 4 - 2, paint);
    }

    public void remixx(){
        Bitmap bitmapCombine;
        Canvas canvasCombine;
        Bitmap bitmapBorderDG = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.border_dg);
        Bitmap bitmapBorderDH = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.border_dh);

        if (orderItems.get(currentID).sku.equals("DG")) {
            if (orderItems.get(currentID).imgs.size() == 1) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 2924, 2924, true));
                bitmapCombine = Bitmap.createBitmap(2924, 2924, Bitmap.Config.ARGB_8888);//49.5
                canvasCombine = new Canvas(bitmapCombine);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasCombine.drawColor(0xffffffff);

                canvasCombine.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
                canvasCombine.drawBitmap(bitmapBorderDG, 0, 0, null);
                bitmapBorderDG.recycle();
                drawTextDG(canvasCombine, "抱枕套");

                //save bitmap
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
            } else if (orderItems.get(currentID).imgs.size() == 2){
                bitmapCombine = Bitmap.createBitmap(2924 * 2 + 100, 2924, Bitmap.Config.ARGB_8888);//49.5
                canvasCombine = new Canvas(bitmapCombine);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasCombine.drawColor(0xffffffff);

                Bitmap bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 2924, 2924, true);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(bitmapBorderDG, 0, 0, null);
                drawTextDG(canvasTemp, "抱枕套正面");
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

                bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(1), 2924, 2924, true);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(bitmapBorderDG, 2924 + 100, 0, null);
                drawTextDG(canvasTemp, "抱枕套背面");
                canvasCombine.drawBitmap(bitmapTemp, 2924 + 100, 0, null);

                bitmapBorderDG.recycle();
                //save bitmap
                String nameCombine = "双面打印抱枕" + orderItems.get(currentID).nameStr + strPlus + ".jpg";

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
            }


        } else if (orderItems.get(currentID).sku.equals("DH")) {
            width_DH = 2540 + 90;//43+1.5cm
            int margin = 24;

            bitmapBorderDH = Bitmap.createScaledBitmap(bitmapBorderDH, width_DH, width_DH, true);

            if (MainActivity.instance.bitmaps.get(0).getWidth() == 3158) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 79, 79, 3000, 3000));
            }

            if (MainActivity.instance.bitmaps.get(0).getWidth() != width_DH) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), width_DH, width_DH, true));
            }
            if (orderItems.get(currentID).imgs.size() == 2 && MainActivity.instance.bitmaps.get(1).getWidth() != width_DH) {
                MainActivity.instance.bitmaps.set(1, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(1), width_DH, width_DH, true));
            }

            bitmapCombine = Bitmap.createBitmap(width_DH * 2 + margin, width_DH, Bitmap.Config.ARGB_8888);
            canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            canvasCombine.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            canvasCombine.drawBitmap(bitmapBorderDH, 0, 0, null);
            canvasCombine.drawBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), width_DH + margin, 0, null);
            canvasCombine.drawBitmap(bitmapBorderDH, width_DH + margin, 0, null);
            bitmapBorderDH.recycle();
            drawTextDH(canvasCombine);

            //save bitmap
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
        }





        try {
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
            int num = orderItems.get(currentID).num;
            Number number2 = new Number(2, currentID+1, num);
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

}
