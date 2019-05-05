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

public class FragmentDQ extends BaseFragment {
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
        paint.setTextSize(25);
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
        canvas.rotate(77.8f, 47, 254);
        canvas.drawRect(47, 254 - 25, 47 + 350, 254, rectPaint);
        canvas.drawText(time + "     " + orderItems.get(currentID).newCode, 47, 254 - 2, paintRed);
        canvas.restore();

        canvas.save();
        canvas.rotate(-78f, 765, 593);
        canvas.drawRect(765, 593 - 25, 765 + 500, 593, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "码" + orderItems.get(currentID).color + LR + "   " + orderItems.get(currentID).order_number, 765, 593 - 2, paint);
        canvas.restore();
    }

    void drawTextTongue(Canvas canvas, String LR) {
        paint.setTextSize(18);
        paintRed.setTextSize(18);

        canvas.drawRect(115, 441, 270, 459, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + orderItems.get(currentID).color + LR + "  " + time, 120, 457, paint);
        canvas.drawRect(88, 422, 295, 440, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number, 88, 438, paint);
        canvas.drawText(orderItems.get(currentID).newCode + "", 191, 438, paintRed);
    }

    public void remixx(){
        setScale(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(mainWidth + 59, tongueHeight + 59 + mainHeight + 10 + mainHeight, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.drawColor(0xffffffff);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        Bitmap bitmapDB_main = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dq_4u2);
        Bitmap bitmapDB_tongue = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.aq40_tongue);


        if ((orderItems.get(currentID).imgs.size() == 1) || (orderItems.get(currentID).imgs.size() == 2)) {
            if (orderItems.get(currentID).imgs.size() == 1) {
                Matrix matrix = new Matrix();
                matrix.postScale(-1, 1);

                MainActivity.instance.bitmaps.add(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, MainActivity.instance.bitmaps.get(0).getWidth(), MainActivity.instance.bitmaps.get(0).getHeight(), matrix, true));
            }
            //leftMain
            Bitmap bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, tongueHeight + 59 + mainHeight + 10, null);

            //leftTongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 253, 301, 390, 468);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_tongue, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, tongueWidth, tongueHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 59, 0, null);

            //rightMain
            bitmapTemp = MainActivity.instance.bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, tongueHeight + 59, null);

            //rightTongue
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 253, 301, 390, 468);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_tongue, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, tongueWidth, tongueHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 59 + tongueWidth + 59, 0, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 4) {
            bitmapDB_tongue = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dq_tongue_4u2);

            //leftMain
            Bitmap bitmapTemp = Bitmap.createBitmap(885, 1099, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, tongueHeight + 59 + mainHeight + 10, null);

            //leftTongue
            bitmapTemp = MainActivity.instance.bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, true);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 341, 455, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_tongue, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 390, 468, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, tongueWidth, tongueHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 59, 0, null);

            //rightMain
            bitmapTemp = Bitmap.createBitmap(885, 1099, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, mainWidth, mainHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, tongueHeight + 59, null);

            //rightTongue
            bitmapTemp = MainActivity.instance.bitmaps.get(3).copy(Bitmap.Config.ARGB_8888, true);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 341, 455, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_tongue, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 390, 468, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, tongueWidth, tongueHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 59 + tongueWidth + 59, 0, null);
            bitmapTemp.recycle();
        }

        bitmapDB_main.recycle();
        bitmapDB_tongue.recycle();



        try {
            Matrix matrix90 = new Matrix();
            matrix90.postRotate(90);
            matrix90.postTranslate(bitmapCombine.getHeight(), 0);
            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix90, true);

            String printColor = orderItems.get(currentID).color.equals("黑") ? "B" : "W";
            String noNewCode = orderItems.get(currentID).newCode.equals("") ? orderItems.get(currentID).sku + orderItems.get(currentID).size : "";
            String nameCombine = noNewCode + orderItems.get(currentID).newCode + orderItems.get(currentID).color + orderItems.get(currentID).order_number + strPlus + ".jpg";

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
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor);
            sheet.addCell(label1);
            Number number2 = new Number(2, currentID+1, orderItems.get(currentID).num);
            sheet.addCell(number2);
            Label label3 = new Label(3, currentID+1, "小左");
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

    void setScale(int size){
        switch (size) {
            case 35:
                mainWidth = 1441;
                mainHeight = 1701;
                tongueWidth = 606;
                tongueHeight = 695;
                break;
            case 36:
                mainWidth = 1469;
                mainHeight = 1745;
                tongueWidth = 606;
                tongueHeight = 695;
                break;
            case 37:
                mainWidth = 1497;
                mainHeight = 1788;
                tongueWidth = 629;
                tongueHeight = 732;
                break;
            case 38:
                mainWidth = 1524;
                mainHeight = 1831;
                tongueWidth = 629;
                tongueHeight = 732;
                break;
            case 39:
                mainWidth = 1552;
                mainHeight = 1874;
                tongueWidth = 652;
                tongueHeight = 769;
                break;
            case 40:
                mainWidth = 1580;
                mainHeight = 1918;
                tongueWidth = 652;
                tongueHeight = 769;
                break;
            case 41:
                mainWidth = 1607;
                mainHeight = 1961;
                tongueWidth = 675;
                tongueHeight = 806;
                break;
            case 42:
                mainWidth = 1635;
                mainHeight = 2004;
                tongueWidth = 675;
                tongueHeight = 806;
                break;
            case 43:
                mainWidth = 1663;
                mainHeight = 2062;
                tongueWidth = 698;
                tongueHeight = 843;
                break;
            case 44:
                mainWidth = 1690;
                mainHeight = 2106;
                tongueWidth = 698;
                tongueHeight = 843;
                break;
            case 45:
                mainWidth = 1718;
                mainHeight = 2150;
                tongueWidth = 721;
                tongueHeight = 880;
                break;
            case 46:
                mainWidth = 1746;
                mainHeight = 2193;
                tongueWidth = 721;
                tongueHeight = 880;
                break;
            case 47:
                mainWidth = 1773;
                mainHeight = 2237;
                tongueWidth = 744;
                tongueHeight = 917;
                break;
            case 48:
                mainWidth = 1801;
                mainHeight = 2281;
                tongueWidth = 744;
                tongueHeight = 917;
                break;
        }
        tongueWidth += 10;
        tongueHeight += 10;
    }

}
