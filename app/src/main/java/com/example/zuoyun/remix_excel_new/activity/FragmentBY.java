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
import android.widget.SeekBar;

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

public class FragmentBY extends BaseFragment {
    Context context;
    //    String sdCardPath = "/mnt/asec/share";
    String sdCardPath = "/storage/emulated/0/Pictures";
    ArrayList<OrderItem> orderItems;
    int currentID;
    String childPath;

    @BindView(R.id.iv_fg2_leftup)
    ImageView iv_leftup;
    @BindView(R.id.iv_fg2_leftdown)
    ImageView iv_leftdown;
    @BindView(R.id.iv_fg2_rightup)
    ImageView iv_rightup;
    @BindView(R.id.iv_fg2_rightdown)
    ImageView iv_rightdown;
    @BindView(R.id.bt_remix)
    Button bt_remix;
    @BindView(R.id.iv_sample1)
    ImageView iv_sample1;
    @BindView(R.id.iv_sample2)
    ImageView iv_sample2;
    @BindView(R.id.sb1)
    SeekBar sb1;
    @BindView(R.id.sbrotate1)
    SeekBar sbrotate1;
    @BindView(R.id.sb2)
    SeekBar sb2;
    @BindView(R.id.sbrotate2)
    SeekBar sbrotate2;

    int width_main, height_main,width_tongue, height_tongue;
    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint paint,paintRed,paintBlue, rectPaint;
    String time = MainActivity.instance.orderDate_Print;

    @Override
    public int getLayout() {
        return R.layout.fragment1;
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
        paint.setTextSize(20);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(34);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(40);
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
                    Log.e("fragment1", "message0");
                }
                else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
                    if(!MainActivity.instance.cb_fastmode.isChecked()){
                        iv_leftup.setImageBitmap(MainActivity.instance.bitmaps.get(0));
                    }
                    checkremix();
                }
                else if (message==3){
                    bt_remix.setClickable(false);
                }
                else if (message == 10) {
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

    void drawTextLL(Canvas canvas) {
        canvas.drawRect(500, 777 - 20, 500 + 600, 777, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + orderItems.get(currentID).color + "  左外" + "  " + time + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 500, 777 - 2, paint);
    }
    void drawTextRL(Canvas canvas) {
        canvas.drawRect(500, 777 - 20, 500 + 600, 777, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + orderItems.get(currentID).color + "  右内"  + "  " + time + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 500, 777 - 2, paint);
    }
    void drawTextLR(Canvas canvas) {
        canvas.drawRect(200, 777 - 20, 200 + 600, 777, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + orderItems.get(currentID).color + "  左内" + "  " + time + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 200, 777 - 2, paint);
    }
    void drawTextRR(Canvas canvas) {
        canvas.drawRect(200, 777 - 20, 200 + 600, 777, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + orderItems.get(currentID).color + "  右外" + "  " + time + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 200, 777 - 2, paint);
    }
    void drawTextTongue(Canvas canvas,String LR) {
        canvas.save();
        canvas.rotate(50.6f, 86, 1376);
        canvas.drawRect(86, 1376 - 20, 86 + 170, 1376, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).order_number, 86, 1376 - 2, paint);
        canvas.restore();

        canvas.drawRect(413, 1585 - 20, 413 + 110, 1585, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + orderItems.get(currentID).color + LR, 413, 1585 - 2, paint);
    }

    public void remixx(){
        setScale(orderItems.get(currentID).size);
        int margin = 60;

        //bitmapCombine
        Bitmap bitmapCombine = Bitmap.createBitmap(width_main * 2 + width_tongue * 2 + margin * 3, Math.max(height_main * 2, height_tongue), Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 6) {
            //LL
            Bitmap bitmapTemp = Bitmap.createBitmap(1485, 867, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.by_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(1485, 867, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.by_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main + margin, 0, null);

            //LT
            bitmapTemp = Bitmap.createBitmap(934, 1630, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.by_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main * 2 + margin * 2, 0, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(1485, 867, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.by_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_main, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(1485, 867, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.by_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main + margin, height_main, null);

            //RT
            bitmapTemp = Bitmap.createBitmap(934, 1630, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.by_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main * 2 + width_tongue + margin * 3, 0, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //LL
            Bitmap bitmapTemp = Bitmap.createBitmap(1485, 867, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.by_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(1485, 867, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, -867, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.by_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main + margin, 0, null);

            //LT
            bitmapTemp = Bitmap.createBitmap(934, 1630, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1485, -52, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.by_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main * 2 + margin * 2, 0, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(1485, 867, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2419, -867, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.by_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_main, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(1485, 867, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2419, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.by_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main + margin, height_main, null);

            //RT
            bitmapTemp = Bitmap.createBitmap(934, 1630, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1485, -52, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.by_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, width_main * 2 + width_tongue + margin * 3, 0, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        }

        try {
            String printColor = orderItems.get(currentID).color.equals("黑") ? "B" : "W";
            String noNewCode = orderItems.get(currentID).newCode.equals("") ? orderItems.get(currentID).sku + orderItems.get(currentID).size + "-" + orderItems.get(currentID).color + "-" : "";
            String nameCombine = noNewCode + orderItems.get(currentID).newCode + "-" + orderItems.get(currentID).order_number + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 149);

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
        if (MainActivity.instance.tb_auto.isChecked())
            remix();
    }

    void setScale(int size){
        switch (size) {
            case 35:
                width_main = 1378;
                height_main = 821;
                width_tongue = 885;
                height_tongue = 1472;
                break;
            case 36:
                width_main = 1410;
                height_main = 836;
                width_tongue = 902;
                height_tongue = 1553;
                break;
            case 37:
                width_main = 1450;
                height_main = 851;
                width_tongue = 916;
                height_tongue = 1590;
                break;
            case 38:
                width_main = 1485;
                height_main = 867;
                width_tongue = 934;
                height_tongue = 1630;
                break;
            case 39:
                width_main = 1520;
                height_main = 879;
                width_tongue = 948;
                height_tongue = 1669;
                break;
            case 40:
                width_main = 1555;
                height_main = 899;
                width_tongue = 966;
                height_tongue = 1708;
                break;
            case 41:
                width_main = 1591;
                height_main = 913;
                width_tongue = 981;
                height_tongue = 1746;
                break;

        }
    }

    boolean checkContains(String nameContains){
        for (int i = 0; i < orderItems.get(currentID).imgs.size(); i++) {
            if (orderItems.get(currentID).imgs.get(i).contains(nameContains)) {
                return true;
            }
        }
        return false;
    }
    Bitmap getBitmapWith(String nameContains){
        for (int i = 0; i < orderItems.get(currentID).imgs.size(); i++) {
            if (orderItems.get(currentID).imgs.get(i).contains(nameContains)) {
                return MainActivity.instance.bitmaps.get(i);
            }
        }
        return null;
    }

}
