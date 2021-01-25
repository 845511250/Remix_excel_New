package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

public class FragmentLK extends BaseFragment {
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

    int quantityPerGroup = 100;

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
        rectBorderPaint.setStrokeWidth(2);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(42);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
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
        paintSmall.setTypeface(Typeface.DEFAULT_BOLD);
        paintSmall.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                    bt_remix.setClickable(false);
                }  else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
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
        bt_remix.setClickable(false);
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

    void drawText(Canvas canvas, int number) {
        canvas.save();
        canvas.rotate(90, 15, 297);
        canvas.drawRect(15, 297 - 44, 15 + 900, 297, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number + "(3-" + number + ")   " + time + "   " + orderItems.get(currentID).newCode, 15, 297 - 6, paint);
        canvas.restore();

    }

    public void remixx(){
        int width = 6260;
        int height = 6200;
//        int height_side = 6142;

        Bitmap bitmapCombine = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        Bitmap bitmapTemp = null;

        //1
        if (MainActivity.instance.bitmaps.size() == 3) {
            bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), width / 2, height, true);
        } else {
            Matrix matrix = new Matrix();
            matrix.postScale(width / 2 / 2952f, height / 6022f);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 2952, 6022, matrix, true);
        }
        canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
        canvasCombine.drawBitmap(bitmapTemp, width / 2, 0, null);
        bitmapTemp.recycle();
        canvasCombine.drawRect(0, 0, width, height, rectBorderPaint);
        canvasCombine.drawRect(1, 1, width - 1, height - 1, rectBorderPaint);
        drawText(canvasCombine, 1);

        String nameCombine = orderItems.get(currentID).nameStr + "(3-1)" + strPlus + ".jpg";
        String pathSave;
        if(MainActivity.instance.cb_classify.isChecked()){
            pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
        } else
            pathSave = sdCardPath + "/生产图/" + childPath + "/";
        if(!new File(pathSave).exists()) {
            new File(pathSave).mkdirs();
        }
        File fileSave = new File(pathSave + nameCombine);
        BitmapToJpg.save(bitmapCombine, fileSave, 300);
        bitmapCombine.recycle();

        //2
        bitmapCombine = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.size() == 3) {
            bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(1), width / 2, height, true);
        } else {
            Matrix matrix = new Matrix();
            matrix.postScale(width / 2 / 2952f, height / 6022f);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2994, 0, 2952, 6022, matrix, true);
        }
        canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
        canvasCombine.drawBitmap(bitmapTemp, width / 2, 0, null);
        bitmapTemp.recycle();
        canvasCombine.drawRect(0, 0, width, height, rectBorderPaint);
        canvasCombine.drawRect(1, 1, width - 1, height - 1, rectBorderPaint);
        drawText(canvasCombine, 2);

        nameCombine = orderItems.get(currentID).nameStr + "(3-2)" + strPlus + ".jpg";
        if(MainActivity.instance.cb_classify.isChecked()){
            pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
        } else
            pathSave = sdCardPath + "/生产图/" + childPath + "/";
        if(!new File(pathSave).exists()) {
            new File(pathSave).mkdirs();
        }
        fileSave = new File(pathSave + nameCombine);
        BitmapToJpg.save(bitmapCombine, fileSave, 300);
        bitmapCombine.recycle();

        //3
        bitmapCombine = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.size() == 3) {
            bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(2), width / 2, height, true);
        } else {
            Matrix matrix = new Matrix();
            matrix.postScale(width / 2 / 2952f, height / 6022f);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5974, 0, 2952, 6022, matrix, true);
        }
        canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
        canvasCombine.drawBitmap(bitmapTemp, width / 2, 0, null);
        bitmapTemp.recycle();
        canvasCombine.drawRect(0, 0, width, height, rectBorderPaint);
        canvasCombine.drawRect(1, 1, width - 1, height - 1, rectBorderPaint);
        drawText(canvasCombine, 3);

        nameCombine = orderItems.get(currentID).nameStr + "(3-3)" + strPlus + ".jpg";
        if(MainActivity.instance.cb_classify.isChecked()){
            pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
        } else
            pathSave = sdCardPath + "/生产图/" + childPath + "/";
        if(!new File(pathSave).exists()) {
            new File(pathSave).mkdirs();
        }
        fileSave = new File(pathSave + nameCombine);
        BitmapToJpg.save(bitmapCombine, fileSave, 300);
        bitmapCombine.recycle();


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
            Label label0 = new Label(0, currentID+1, orderItems.get(currentID).order_number);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku);
            sheet.addCell(label1);
            int num=orderItems.get(currentID).num;
            Number number2 = new Number(2, currentID+1, num);
            sheet.addCell(number2);
            Label label4 = new Label(4, currentID + 1, MainActivity.instance.orderDate_Excel);
            sheet.addCell(label4);

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

    int groupID(){
        int groupID = (currentID / quantityPerGroup) + 1;
        if (groupID == (orderItems.size()-1) / quantityPerGroup + 1) {
            if ((orderItems.size() - 1) % quantityPerGroup <= 30 && orderItems.size() > 100) {
                groupID--;
            }
        }
        return groupID;
    }
    int idInGroup(){
        int startID = (groupID() - 1) * quantityPerGroup;

        int sum = 0;
        for (int id = startID; id < currentID; id++) {
            sum += orderItems.get(id).num;
        }
        return sum * 3 + ((intPlus - 1) * 3) + 1;
    }
    String groupName(){
        return "LK" + time + "-" + groupID() + "(共" + sumInGroup() + "套-" + (sumInGroup() * 3) + "图)";
    }
    int sumInGroup(){
        int startID = (groupID() - 1) * quantityPerGroup;
        int endID = 0;

        if (currentID / quantityPerGroup + 1 == (orderItems.size() - 1) / quantityPerGroup + 1) {
            endID = orderItems.size() - 1;
        } else if (currentID / quantityPerGroup + 1 == (orderItems.size() - 1) / quantityPerGroup + 1 - 1 && (orderItems.size() - 1) % quantityPerGroup <= 30) {

            endID = orderItems.size() - 1;
        } else {
            endID = groupID() * quantityPerGroup - 1;
        }

        int sum = 0;
        for (int id = startID; id <= endID; id++) {
            sum += orderItems.get(id).num;
        }
        return sum;
    }


     static void writeTxtAppend(File file, String str) {
        try {
            if (!new File(file.getParent()).exists()) {
                new File(file.getParent()).mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.newLine();
            bw.write(str);
            bw.close();
            fw.close();
        } catch (Exception e) {
            Log.e("aaa", "LK writeTxtAppend:" + e.toString());
        }
    }

}
