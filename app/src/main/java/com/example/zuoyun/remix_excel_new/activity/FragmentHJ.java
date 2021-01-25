package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
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

public class FragmentHJ extends BaseFragment {
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

    String time = MainActivity.instance.orderDate_Print;
    Paint rectPaint,paint, rectBorderPaint;

    int width,height, dpi, strokeWidth, textSize;

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

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(24);
        paint.setAntiAlias(true);

        rectBorderPaint = new Paint();
        rectBorderPaint.setColor(0xff000000);
        rectBorderPaint.setStyle(Paint.Style.STROKE);
        rectBorderPaint.setStrokeWidth(10);

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                } else if (message == MainActivity.LOADED_IMGS) {
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
                            intPlus += orderItems.get(i).num;
                        }
                    }
                    strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                    remixx();
                }
            }
        }.start();

    }

    void drawText(Canvas canvas) {
        canvas.drawRect(3000, strokeWidth + 2, 3000 + 800, strokeWidth + 5 + textSize, rectPaint);
        canvas.drawText("毛毯上边 尺码" + orderItems.get(currentID).sku.substring(2) + "  " + time + "  " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode, 3000, strokeWidth + 5 + textSize - 4, paint);

        canvas.drawRect(3000, height - strokeWidth - textSize - 5, 3000 + 800, height - strokeWidth - 2, rectPaint);
        canvas.drawText("毛毯下边 尺码" + orderItems.get(currentID).sku.substring(2) + "  " + time + "  " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode, 3000, height - strokeWidth - 5 - 2, paint);
    }
    void drawText_4u2(Canvas canvas) {
        canvas.drawRect(3000, height - 14 - textSize, 3000 + 800, height - 14, rectPaint);
        canvas.drawText("毛毯 尺码" + orderItems.get(currentID).sku.substring(2) + "  " + time + "  " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode, 3000, height - 14 - 2, paint);
    }

    public void remixx(){
        if (orderItems.get(currentID).sku.equals("HJ")) {
            if (orderItems.get(currentID).sizeStr.equals("XS") || orderItems.get(currentID).sizeStr.equals("Youth")|| orderItems.get(currentID).sizeStr.equals("Y")) {
                orderItems.get(currentID).sku = "HJY";
            } else if (orderItems.get(currentID).sizeStr.equals("S")) {
                orderItems.get(currentID).sku = "HJS";
            } else if (orderItems.get(currentID).sizeStr.equals("M")) {
                orderItems.get(currentID).sku = "HJM";
            }
        }

        setSize();
        rectBorderPaint.setStrokeWidth(strokeWidth);
        paint.setTextSize(textSize);

        Bitmap bitmapTemp = null;


        if(orderItems.get(currentID).platform.endsWith("-jj")){
            if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
                if (MainActivity.instance.bitmaps.get(0).getWidth() != 8632) {
                    MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 8632, 8632, true));
                }

                bitmapTemp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawColor(0xffffffff);

                Matrix matrix = new Matrix();
                matrix.postScale(width / 6284f, height / 8291f);
                canvasTemp.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1178, 175, 6284, 8291, matrix, true), 0, 0, null);
                rectBorderPaint.setColor(0xffffffff);
                canvasTemp.drawRect(strokeWidth, strokeWidth, width - strokeWidth, height - strokeWidth, rectBorderPaint);
                canvasTemp.drawRect(strokeWidth + strokeWidth / 2, strokeWidth + strokeWidth / 2, width - strokeWidth- strokeWidth / 2, height - strokeWidth- strokeWidth / 2, rectBorderPaint);

                drawText(canvasTemp);

                rectBorderPaint.setColor(0xffff0000);
                canvasTemp.drawRect(0, 0, width, height, rectBorderPaint);
                canvasTemp.drawRect(strokeWidth / 2, strokeWidth / 2, width - strokeWidth / 2, height - strokeWidth / 2, rectBorderPaint);

            } else {//adam 同第二版
                bitmapTemp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawColor(0xffffffff);

                canvasTemp.drawBitmap(Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), width - 110, height - 120, true), 60, 60, null);

                rectBorderPaint.setColor(0xffffffff);
                canvasTemp.drawRect(strokeWidth, strokeWidth, width - strokeWidth, height - strokeWidth, rectBorderPaint);
                canvasTemp.drawRect(strokeWidth + strokeWidth / 2, strokeWidth + strokeWidth / 2, width - strokeWidth- strokeWidth / 2, height - strokeWidth- strokeWidth / 2, rectBorderPaint);

                drawText(canvasTemp);

                rectBorderPaint.setColor(0xffff0000);
                canvasTemp.drawRect(0, 0, width, height, rectBorderPaint);
                canvasTemp.drawRect(strokeWidth / 2, strokeWidth / 2, width - strokeWidth / 2, height - strokeWidth / 2, rectBorderPaint);
            }

        }else {
            bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), width, height, true);
            Canvas canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            rectBorderPaint.setStrokeWidth(12);
            canvasTemp.drawRect(0, 0, width, height, rectBorderPaint);
            canvasTemp.drawRect(6, 6, width - 6, height - 6, rectBorderPaint);
            drawText_4u2(canvasTemp);
        }


        Matrix matrix = new Matrix();
        switch (orderItems.get(currentID).sku) {
            case "HJY":
                matrix.postRotate(90, bitmapTemp.getWidth() / 2, bitmapTemp.getHeight() / 2);
                bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix, true);
                break;
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
        BitmapToJpg.save(bitmapTemp, fileSave, dpi);
        bitmapTemp.recycle();

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
            int num=orderItems.get(currentID).num;
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

    void setSize() {
        switch (orderItems.get(currentID).sku) {
            case "HJY":
                width = 6174;
                height = 7888;
                dpi = 136;
                textSize = 32;
                strokeWidth = 20;
                break;
            case "HJS":
                width = 6160;
                height = 7920;
                dpi = 110;
                textSize = 26;
                strokeWidth = 16;
                break;
            case "HJM":
                width = 6201;
                height = 8200;
                dpi = 100;
                textSize = 24;
                strokeWidth = 16;
                break;
        }
    }


}
