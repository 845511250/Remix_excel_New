package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class FragmentFR extends BaseFragment {
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

    int mainXLeft, mainXRight, mainYLeft, mainYRight;
    int barXLeft, barXRight, barYLeft, barYRight;
    int mianWidth, mainHeight, barWidth, barHeight;
    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint rectPaint, paint, paintRed, paintBlue;
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

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(15);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(15);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(15);
        paintBlue.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;


        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                } else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
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
        canvas.rotate(68.9f, 13, 108);
        canvas.drawRect(13, 108 - 14, 13 + 300, 108, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).sku + orderItems.get(currentID).sizeStr + "码" + LR, 13, 108 - 2, paint);
        canvas.restore();
    }

    public void remixx(){
        setScale(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(2282, 1200, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);
        Matrix matrixCombine = new Matrix();

        if (orderItems.get(currentID).imgs.size() == 4) {
            //leftMain
            Bitmap bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasLeft = new Canvas(bitmapTemp);
            canvasLeft.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDBMain = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fr_main);
            canvasLeft.drawBitmap(bitmapDBMain, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(mainXLeft, mainYLeft);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //rightMain
            bitmapTemp = MainActivity.instance.bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasRight = new Canvas(bitmapTemp);
            canvasRight.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRight.drawBitmap(bitmapDBMain, 0, 0, null);
            bitmapDBMain.recycle();
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(mainXRight, mainYRight);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);


            //leftBar
            bitmapTemp = MainActivity.instance.bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasBarLeft = new Canvas(bitmapTemp);
            canvasBarLeft.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDBBar = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fr_bar);
            canvasBarLeft.drawBitmap(bitmapDBBar, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, barWidth, barHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(barXLeft, barYLeft);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //rightBar
            bitmapTemp = MainActivity.instance.bitmaps.get(3).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasBarRight = new Canvas(bitmapTemp);
            canvasBarRight.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasBarRight.drawBitmap(bitmapDBBar, 0, 0, null);
            bitmapDBBar.recycle();
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, barWidth, barHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(barXRight, barYRight);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);
            bitmapTemp.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //leftMain
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 1024, 920);
            Canvas canvasLeft = new Canvas(bitmapTemp);
            canvasLeft.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDBMain = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fr_main);
            canvasLeft.drawBitmap(bitmapDBMain, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(mainXLeft, mainYLeft);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //leftBar
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 197, 920, 630, 92);
            Canvas canvasBarLeft = new Canvas(bitmapTemp);
            canvasBarLeft.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDBBar = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fr_bar);
            canvasBarLeft.drawBitmap(bitmapDBBar, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, barWidth, barHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(barXLeft, barYLeft);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //翻转原图
            matrixCombine.reset();
            matrixCombine.postScale(-1, 1);
            MainActivity.instance.bitmaps.set(0, Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, MainActivity.instance.bitmaps.get(0).getWidth(), MainActivity.instance.bitmaps.get(0).getHeight(), matrixCombine, true));

            //rightMain
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 1024, 920);
            Canvas canvasRight = new Canvas(bitmapTemp);
            canvasRight.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasRight.drawBitmap(bitmapDBMain, 0, 0, null);
            bitmapDBMain.recycle();
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mianWidth, mainHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(mainXRight, mainYRight);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //rightBar
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 197, 920, 630, 92);
            Canvas canvasBarRight = new Canvas(bitmapTemp);
            canvasBarRight.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasBarRight.drawBitmap(bitmapDBBar, 0, 0, null);
            bitmapDBBar.recycle();
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, barWidth, barHeight, true);

            matrixCombine.reset();
            matrixCombine.postTranslate(barXRight, barYRight);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);
            bitmapTemp.recycle();
        }

        try {
//            Matrix matrix90 = new Matrix();
//            matrix90.postRotate(90);
//            matrix90.postTranslate(1890, 0);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, 3791, 1890, matrix90, true);

            String nameCombine = orderItems.get(currentID).sku + orderItems.get(currentID).sizeStr + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

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
            Label label0 = new Label(0, currentID+1, orderItems.get(currentID).order_number+orderItems.get(currentID).sku+ orderItems.get(currentID).size);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku+ orderItems.get(currentID).size);
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

    void setScale(int size){
        switch (size) {
            case 17:
                mianWidth = 958;
                mainHeight = 836;
                barWidth = 593;
                barHeight = 92;
                mainXLeft = 66;
                mainYLeft = 84;
                mainXRight = 1206;
                mainYRight = 84;
                barXLeft = 252;
                barYLeft = 1053;
                barXRight = 1373;
                barYRight = 1053;
                break;
            case 18:
                mianWidth = 990;
                mainHeight = 878;
                barWidth = 593;
                barHeight = 92;
                mainXLeft = 50;
                mainYLeft = 63;
                mainXRight = 1190;
                mainYRight = 63;
                barXLeft = 252;
                barYLeft = 1053;
                barXRight = 1373;
                barYRight = 1053;
                break;
            case 19:
                mianWidth = 1023;
                mainHeight = 920;
                barWidth = 630;
                barHeight = 92;
                mainXLeft = 34;
                mainYLeft = 42;
                mainXRight = 1174;
                mainYRight = 42;
                barXLeft = 234;
                barYLeft = 1053;
                barXRight = 1355;
                barYRight = 1053;
                break;
            case 20:
                mianWidth = 1057;
                mainHeight = 962;
                barWidth = 630;
                barHeight = 92;
                mainXLeft = 17;
                mainYLeft = 21;
                mainXRight = 1157;
                mainYRight = 21;
                barXLeft = 234;
                barYLeft = 1053;
                barXRight = 1355;
                barYRight = 1053;
                break;
            case 21:
                mianWidth = 1090;
                mainHeight = 1004;
                barWidth = 667;
                barHeight = 92;
                mainXLeft = 0;
                mainYLeft = 0;
                mainXRight = 1141;
                mainYRight = 0;
                barXLeft = 216;
                barYLeft = 1053;
                barXRight = 1336;
                barYRight = 1053;
                break;
        }
    }

}
