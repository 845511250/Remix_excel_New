package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
 * Created by zuoyun on 2016/11/4.
 */

public class FragmentKP extends BaseFragment {
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
    boolean sizeOK = true;

    Paint rectPaint, paint, paintRed, paintBlue, rectBorderPaint, paintSmall;
    String time;

    @Override
    public int getLayout() {
        return R.layout.fragment_dg;
    }

    @Override
    public void initData(View view) {
        context = getContext();
        ButterKnife.bind(this, view);
        orderItems=MainActivity.instance.orderItems;
        currentID = MainActivity.instance.currentID;
        childPath = MainActivity.instance.childPath;

        //paint
        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        rectBorderPaint = new Paint();
        rectBorderPaint.setColor(0xff000000);
        rectBorderPaint.setStyle(Paint.Style.STROKE);
        rectBorderPaint.setStrokeWidth(12);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(23);
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

        paintSmall = new Paint();
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(23);
        paintSmall.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                    bt_remix.setClickable(false);
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

                if (sizeOK) {
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
            }
        }.start();

    }

    void drawText(Canvas canvas) {
        canvas.drawRect(200, 16, 200 + 450, 16 + 23, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).sku + "袖毯-" + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number, 200, 16 + 21, paint);
    }

    public void remixx(){
        Bitmap bitmapCombine = null;
        if (orderItems.get(currentID).sizeStr.equals("S")) {
            bitmapCombine = Bitmap.createBitmap(5614, 7899, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine= new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            //Main
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1944, 8, 5614, 5482);
            Canvas canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawRect(0, 0, 5614 - 6, 5482 - 6, rectBorderPaint);
            canvasTemp.drawRect(6, 6, 5614 - 6, 5482 - 6, rectBorderPaint);

            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.kps_hole_r);
            canvasTemp.drawBitmap(bitmapDB, 1586, 965, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.kps_hole_l);
            canvasTemp.drawBitmap(bitmapDB, 3406, 965, null);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //right sleeve
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 16, 821, 1863, 2374);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.kps_sleeve_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 5525, null);

            //left sleeve
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7622, 820, 1863, 2374);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.kps_sleeve_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 1907, 5525, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).sizeStr.equals("L")) {
            bitmapCombine = Bitmap.createBitmap(5677, 11107, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine= new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            //Main
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2942, 44, 5614, 8212);
            Canvas canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawRect(0, 0, 5614 - 6, 8212 - 6, rectBorderPaint);
            canvasTemp.drawRect(6, 6, 5614 - 6, 8212 - 6, rectBorderPaint);

            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.kpl_hole_r);
            canvasTemp.drawBitmap(bitmapDB, 896, 876, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.kpl_hole_l);
            canvasTemp.drawBitmap(bitmapDB, 3885, 876, null);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //right sleeve
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 36, 710, 2816, 2850);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.kpl_sleeve_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 8257, null);

            //left sleeve
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 8647, 710, 2816, 2850);
            canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.kpl_sleeve_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2861, 8257, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();
        }
        
        if (num == 1) {
            MainActivity.recycleExcelImages();
        }

//        matrix.reset();
//        matrix.postRotate(-90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
//        bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

        try {
            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 110);

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
            Label label0 = new Label(0, currentID+1, orderItems.get(currentID).order_number+orderItems.get(currentID).sku);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku);
            sheet.addCell(label1);
            int num=orderItems.get(currentID).num;
            Number number2 = new Number(2, currentID+1, num);
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



}
