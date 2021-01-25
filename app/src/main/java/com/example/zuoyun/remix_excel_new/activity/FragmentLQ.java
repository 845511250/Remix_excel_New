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
 * Created by zuoyun on 2016/10/6.
 */

public class FragmentLQ extends BaseFragment {
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

    int width_main, height_main, width_side, height_side;
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

    void drawTextMainL(Canvas canvas) {
        canvas.drawRect(495, 1627 - 28, 495 + 120, 1627, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " - " + orderItems.get(currentID).size + "-" + orderItems.get(currentID).color, 495, 1627 - 4, paint);

        canvas.save();
        canvas.rotate(-62f, 896, 1466);
        canvas.drawRect(896, 1466 - 28, 896 + 300, 1466, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).order_number, 896, 1466 - 4, paint);
        canvas.restore();
    }
    void drawTextMainR(Canvas canvas) {
        canvas.drawRect(696, 1627 - 28, 696 + 120, 1627, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " - " + orderItems.get(currentID).size + "-" + orderItems.get(currentID).color, 696, 1627 - 4, paint);

        canvas.save();
        canvas.rotate(62.9f, 218, 1171);
        canvas.drawRect(218, 1171 - 28, 218 + 300, 1171, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).order_number, 218, 1171 - 4, paint);
        canvas.restore();
    }

    public void remixx(){
        setSize(orderItems.get(currentID).size);

        int margin = 100;
        Bitmap bitmapCombine = Bitmap.createBitmap(width_main * 2 + width_side * 2 + margin, height_main, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 1) {
            //left mian
            Bitmap bitmapTemp = Bitmap.createBitmap(1268, 1704, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1794, -48, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.lq_left_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMainL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main + width_side * 2 + margin, 0, null);

            //left side
            bitmapTemp = Bitmap.createBitmap(482, 1261, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1576, -48, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.lq_left_inside);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextMainAdam(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main + width_side + margin, 0, null);

            //right side
            bitmapTemp = Bitmap.createBitmap(482, 1261, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1041, -48, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.lq_right_inside);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextMainAdam(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main, 0, null);

            //right main
            bitmapTemp = Bitmap.createBitmap(1268, 1704, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -37, -48, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.lq_right_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMainR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 2) {
            //left mian
            Bitmap bitmapTemp = Bitmap.createBitmap(1268, 1704, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -218, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.lq_left_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMainL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main + width_side * 2 + margin, 0, null);

            //left side
            bitmapTemp = Bitmap.createBitmap(482, 1261, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.lq_left_inside);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextMainAdam(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main + width_side + margin, 0, null);

            //right side
            bitmapTemp = Bitmap.createBitmap(482, 1261, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -1004, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.lq_right_inside);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextMainAdam(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main, 0, null);

            //right main
            bitmapTemp = Bitmap.createBitmap(1268, 1704, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.lq_right_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMainR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();
        }



        try {
            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 149);
            bitmapCombine.recycle();

            String printColor = orderItems.get(currentID).color.equals("白") ? "W" : "B";

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

    void setSize(int size){
        switch (size) {
            case 35:
                width_main = 1258;
                height_main = 1564;
                width_side = 472;
                height_side = 1175;
                break;
            case 36:
                width_main = 1261;
                height_main = 1610;
                width_side = 476;
                height_side = 1204;
                break;
            case 37:
                width_main = 1264;
                height_main = 1657;
                width_side = 478;
                height_side = 1231;
                break;
            case 38:
                width_main = 1268;
                height_main = 1704;
                width_side = 482;
                height_side = 1261;
                break;
            case 39:
                width_main = 1269;
                height_main = 1750;
                width_side = 487;
                height_side = 1288;
                break;
            case 40:
                width_main = 1271;
                height_main = 1797;
                width_side = 490;
                height_side = 1317;
                break;
            case 41:
                width_main = 1271;
                height_main = 1844;
                width_side = 493;
                height_side = 1345;
                break;

        }
        width_side += 10;
        height_side += 10;
    }

}
