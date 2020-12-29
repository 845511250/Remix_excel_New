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

public class FragmentHV extends BaseFragment {
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

    int width, height;
    int num;
    String strPlus = "";
    int intPlus = 1;
    Paint paint,paintRed,paintBlue, rectPaint, rectPaintRed;
    String time = MainActivity.instance.orderDate_Print;


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

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(22);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(22);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(22);
        paintBlue.setTypeface(Typeface.DEFAULT_BOLD);
        paintBlue.setAntiAlias(true);

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        rectPaintRed = new Paint();
        rectPaintRed.setColor(0xffff0000);
        rectPaintRed.setStyle(Paint.Style.FILL);

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message,String sampleurl) {
                if(message==0){
                    iv_pillow.setImageDrawable(null);
                    bt_remix.setClickable(false);
                } else if (message == MainActivity.LOADED_IMGS) {
                    if(!MainActivity.instance.cb_fastmode.isChecked())
                        iv_pillow.setImageBitmap(MainActivity.instance.bitmaps.get(0));
                    bt_remix.setClickable(true);
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
                            intPlus += orderItems.get(i).num;;
                        }
                    }
                    strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                    remixx();
                }
            }
        }.start();

    }

    void drawTextL(Canvas canvas) {
        String isWhite = orderItems.get(currentID).colorStr.equalsIgnoreCase("white") ? " W" : "";

        canvas.save();
        canvas.rotate(74.1f, 2, 48);
        paint.setColor(0xffffffff);
        canvas.drawText(orderItems.get(currentID).sizeStr + isWhite, 2, 48 - 2, paint);
        paint.setColor(0xff000000);
        canvas.drawText(orderItems.get(currentID).sizeStr + isWhite, 2 + 3, 48 - 2 - 3, paint);
        canvas.restore();
    }
    void drawTextR(Canvas canvas) {
        String isWhite = orderItems.get(currentID).colorStr.equalsIgnoreCase("white") ? " W" : "";

        canvas.save();
        canvas.rotate(-75f, 1101, 119);
        paint.setColor(0xffffffff);
        canvas.drawText(orderItems.get(currentID).sizeStr + isWhite, 1101, 119 - 2, paint);
        paint.setColor(0xff000000);
        canvas.drawText(orderItems.get(currentID).sizeStr + isWhite, 1101 + 3, 119 - 2 - 3, paint);
        canvas.restore();
    }

    public void remixx(){
        setSize(orderItems.get(currentID).sizeStr);
        Bitmap bitmapDBRight = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hv_r);
        Bitmap bitmapDBLeft = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hv_l);

        //bitmapCombine
        Bitmap bitmapCombine = Bitmap.createBitmap(2760, 585, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
            if (MainActivity.instance.bitmaps.get(0).getWidth() != 2526) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 2526, 2526, true));
            }

            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 101, 1012, 1123, 499);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDBLeft, 0, 0, null);
            bitmapDBLeft.recycle();
//        drawTextL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(width, height);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1302, 1012, 1123, 499);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDBRight, 0, 0, null);
            bitmapDBRight.recycle();
//        drawTextR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            matrix.postTranslate(1380, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();
        }else {
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 38, 0, 1123, 499);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDBLeft, 0, 0, null);
            bitmapDBLeft.recycle();
//        drawTextL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(width, height);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 39, 0, 1123, 499);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDBRight, 0, 0, null);
            bitmapDBRight.recycle();
//        drawTextR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            matrix.postTranslate(1380, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();

//        canvasCombine.drawRect(0, 0, 2, 565, rectPaintRed);
//        canvasCombine.drawRect(1379, 0, 1381, 565, rectPaintRed);
        }


        try {
            String printColor = orderItems.get(currentID).color.equals("黑") ? "B" : "W";
            String nameCombine = orderItems.get(currentID).sku + orderItems.get(currentID).sizeStr + orderItems.get(currentID).color + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 150);
            bitmapCombine.recycle();

            //写入excel
            String writePath = sdCardPath + "/生产图/" + childPath + "/生产单.xls";
            File fileWrite = new File(writePath);
            if (!fileWrite.exists()) {
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
            WritableWorkbook workbook = Workbook.createWorkbook(fileWrite, book);
            WritableSheet sheet = workbook.getSheet(0);
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor);
            sheet.addCell(label1);
            Number number2 = new Number(2, currentID + 1, orderItems.get(currentID).num);
            sheet.addCell(number2);
            Label label3 = new Label(3, currentID + 1, orderItems.get(currentID).customer);
            sheet.addCell(label3);
            Label label4 = new Label(4, currentID + 1, MainActivity.instance.orderDate_Excel);
            sheet.addCell(label4);
            Label label6 = new Label(6, currentID + 1, "平台大货");
            sheet.addCell(label6);

            workbook.write();
            workbook.close();

        } catch (Exception e) {

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

    void setSize(String size){
        switch (size) {
            case "20":
                width = 733;
                height = 335;
                break;
            case "21":
                width = 733;
                height = 335;
                break;
            case "20-21":
                width = 733;
                height = 335;
                break;
            case "22":
                width = 790;
                height = 352;
                break;
            case "23":
                width = 790;
                height = 352;
                break;
            case "22-23":
                width = 790;
                height = 352;
                break;
            case "24":
                width = 832;
                height = 364;
                break;
            case "25":
                width = 832;
                height = 364;
                break;
            case "24-25":
                width = 832;
                height = 364;
                break;
            case "26":
                width = 875;
                height = 388;
                break;
            case "27":
                width = 875;
                height = 388;
                break;
            case "26-27":
                width = 875;
                height = 388;
                break;
            case "28":
                width = 911;
                height = 408;
                break;
            case "29":
                width = 911;
                height = 408;
                break;
            case "30":
                width = 911;
                height = 408;
                break;
            case "29-30":
                width = 911;
                height = 408;
                break;
            case "31":
                width = 967;
                height = 424;
                break;
            case "32":
                width = 967;
                height = 424;
                break;
            case "31-32":
                width = 967;
                height = 424;
                break;
            case "33":
                width = 999;
                height = 443;
                break;
            case "34":
                width = 999;
                height = 443;
                break;
            case "33-34":
                width = 999;
                height = 443;
                break;
            case "35":
                width = 1034;
                height = 460;
                break;
            case "36":
                width = 1034;
                height = 460;
                break;
            case "37":
                width = 1067;
                height = 476;
                break;
            case "38":
                width = 1094;
                height = 479;
                break;
            case "39":
                width = 1123;
                height = 499;
                break;
            case "40":
                width = 1160;
                height = 529;
                break;
            case "41":
                width = 1188;
                height = 532;
                break;
            case "42":
                width = 1225;
                height = 551;
                break;
            case "43":
                width = 1254;
                height = 549;
                break;
            case "44":
                width = 1283;
                height = 566;
                break;
            case "45":
                width = 1320;
                height = 566;
                break;
        }
        width += 5;
        height += 16;
    }

}
