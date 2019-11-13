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

public class FragmentHK extends BaseFragment {
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
    int mainWidth, mainHeight, tongueWidth, tongueHeight;

    Paint rectPaint, paint, paintRed, paintSmall,paintRectBlack;
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

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        paintRectBlack = new Paint();
        paintRectBlack.setColor(0xff000000);
        paintRectBlack.setStyle(Paint.Style.FILL);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(23);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(23);
        paintRed.setAntiAlias(true);

        paintSmall = new Paint();
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(20);
        paintSmall.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;


        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                    Log.e("fragment2", "message0");
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
        canvas.drawRect(562, 1338, 562 + 120, 1338 + 23, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "-" + orderItems.get(currentID).size + "码" + orderItems.get(currentID).color + LR, 562, 1338 + 21, paint);

        canvas.save();
        canvas.rotate(-74.9f, 1038, 772);
        canvas.drawRect(1038, 772 - 23, 1038 + 465, 772, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "  " + time + " " + orderItems.get(currentID).order_number + orderItems.get(currentID).newCode, 1038, 772 - 2, paint);
        canvas.restore();
    }

    void drawTextTongue(Canvas canvas, String LR) {
//        canvas.drawRect(152, 556 - 22, 152 + 220, 556, rectPaint);
//        canvas.drawText(orderItems.get(currentID).size + "码" + orderItems.get(currentID).color + LR + " " + orderItems.get(currentID).order_number, 152, 556 - 2, paintSmall);
    }


    public void remixx(){
        setSize(orderItems.get(currentID).size);
        int margin=60;

        Bitmap bitmapCombine = Bitmap.createBitmap(mainWidth * 2 + tongueWidth + margin, mainHeight, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 4) {
            //main
            Bitmap bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hk_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = MainActivity.instance.bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mainWidth + margin, 0, null);

            //tongue
            bitmapTemp = MainActivity.instance.bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hk_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, tongueWidth, tongueHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mainWidth * 2 + margin, mainHeight - tongueHeight * 2 - margin * 2, null);

            bitmapTemp = MainActivity.instance.bitmaps.get(3).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, tongueWidth, tongueHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mainWidth * 2 + margin, mainHeight - tongueHeight, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //main
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 38, 0, 1223, 1398);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hk_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 38, 0, 1223, 1398);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mainWidth + margin, 0, null);

            //tongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 386, 233, 526, 592);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hk_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, tongueWidth, tongueHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mainWidth * 2 + margin, mainHeight - tongueHeight * 2 - margin * 2, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 386, 233, 526, 592);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, tongueWidth, tongueHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, mainWidth * 2 + margin, mainHeight - tongueHeight, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        }





        try {
            String printColor = orderItems.get(currentID).color.equals("黑") ? "B" : "W";
            String nameCombine = orderItems.get(currentID).sku + "-" + orderItems.get(currentID).sizeStr + orderItems.get(currentID).color + orderItems.get(currentID).order_number + strPlus + ".jpg";

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
            if (orderItems.get(currentID).sku.startsWith("Z6")) {
                label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + "-" + orderItems.get(currentID).size);
            }
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor);
            if (orderItems.get(currentID).sku.startsWith("Z6")) {
                label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + "-" + orderItems.get(currentID).size);
            }
            sheet.addCell(label1);
            int num=orderItems.get(currentID).num;
            Number number2 = new Number(2, currentID+1, num);
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
            case 35:
                mainWidth = 1425;
                mainHeight = 1565;
                tongueWidth = 467;
                tongueHeight = 501;
                break;
            case 36:
                mainWidth = 1452;
                mainHeight = 1563;
                tongueWidth = 476;
                tongueHeight = 514;
                break;
            case 37:
                mainWidth = 1487;
                mainHeight = 1652;
                tongueWidth = 485;
                tongueHeight = 527;
                break;
            case 38:
                mainWidth = 1511;
                mainHeight = 1696;
                tongueWidth = 494;
                tongueHeight = 541;
                break;
            case 39:
                mainWidth = 1550;
                mainHeight = 1740;
                tongueWidth = 503;
                tongueHeight = 554;
                break;
            case 40:
                mainWidth = 1576;
                mainHeight = 1785;
                tongueWidth = 512;
                tongueHeight = 567;
                break;
            case 41:
                mainWidth = 1606;
                mainHeight = 1829;
                tongueWidth = 521;
                tongueHeight = 581;
                break;
            case 42:
                mainWidth = 1636;
                mainHeight = 1873;
                tongueWidth = 530;
                tongueHeight = 594;
                break;
            case 43:
                mainWidth = 1669;
                mainHeight = 1918;
                tongueWidth = 539;
                tongueHeight = 607;
                break;
            case 44:
                mainWidth = 1701;
                mainHeight = 1962;
                tongueWidth = 548;
                tongueHeight = 620;
                break;
            case 45:
                mainWidth = 1734;
                mainHeight = 2007;
                tongueWidth = 557;
                tongueHeight = 634;
                break;
            case 46:
                mainWidth = 1760;
                mainHeight = 2051;
                tongueWidth = 566;
                tongueHeight = 647;
                break;

        }
    }
}
