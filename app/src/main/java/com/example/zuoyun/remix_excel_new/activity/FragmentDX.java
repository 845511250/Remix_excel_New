package com.example.zuoyun.remix_excel_new.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import static android.graphics.Bitmap.createBitmap;
import static android.graphics.Bitmap.createScaledBitmap;

/**
 * Created by zuoyun on 2016/11/4.
 */

public class FragmentDX extends BaseFragment {
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

    Paint rectPaint, paint, paintRed, paintBlue;
    String time;

    int width_part1, width_part2, width_part3, width_part4, width_part5, width_part6, width_part7, width_part8;
    int height_part1, height_part2, height_part3, height_part4, height_part5, height_part6, height_part7, height_part8;

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

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(30);
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

        time = MainActivity.instance.orderDate_Print;

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                } else if (message == MainActivity.LOADED_IMGS) {
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
                setSize(orderItems.get(currentID).sizeStr);
                if (sizeOK) {
                    for(num=orderItems.get(currentID).num;num>=1;num--) {
                    intPlus = orderItems.get(currentID).num - num + 1;
                        for(int i=0;i<currentID;i++) {
                            if (orderItems.get(currentID).order_number.equals(orderItems.get(i).order_number)) {
                                intPlus += 1;
                            }
                        }
                        strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                        remixx();
                    }
                }
            }
        }.start();

    }

    void drawText(Canvas canvas, int left, int bottom) {
        canvas.drawRect(left, bottom - 28, left + 500, bottom, rectPaint);
        canvas.drawText(time + "   " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).sizeStr, left, bottom - 2, paint);
        canvas.drawText(orderItems.get(currentID).newCode, left + 330, bottom - 2, paintRed);
    }
    void drawTextRotate(Canvas canvas, int degree, int left, int bottom) {
        canvas.save();
        canvas.rotate(degree, (float) left, (float) bottom);
        canvas.drawRect(left, bottom - 28, left + 500, bottom, rectPaint);
        canvas.drawText(time + "   " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).sizeStr, left, bottom - 2, paint);
        canvas.drawText(orderItems.get(currentID).newCode, left + 330, bottom - 2, paintRed);
        canvas.restore();
    }

    public void remixx(){
        int margin = 80;
        Bitmap bitmapCombine = createBitmap(height_part3 + height_part5 + width_part6 + margin * 2, height_part8 + height_part2 + height_part1 + width_part3 + height_part4 + margin * 5, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);
        Matrix matrixCombine = new Matrix();
        if (orderItems.get(currentID).imgs.size() == 1) {
            //part1
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part1);
            Bitmap bitmapTemp = createBitmap(MainActivity.instance.bitmaps.get(0), 319, 0, 3440, 845);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp, 1110, 845);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part1, height_part1, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_part8 + margin, null);

            //part2
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part2);
            bitmapTemp = createBitmap(MainActivity.instance.bitmaps.get(0), 319, 681, 3440, 387);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp, 1110, 28);
            drawText(canvasTemp, 1110, 387);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part2, height_part2, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_part8 + height_part1 + margin * 2, null);

            //part3
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part3);
            bitmapTemp = createBitmap(MainActivity.instance.bitmaps.get(0), 1116, 914, 1839, 2366);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp, 424, 2366);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part3, height_part3, true);

            matrixCombine.reset();
            matrixCombine.postRotate(90);
            matrixCombine.postTranslate(height_part3, height_part8 + height_part1 + height_part2 + margin * 3);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //part4
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part4);
            bitmapTemp = createBitmap(MainActivity.instance.bitmaps.get(0), 930, 2000, 2198, 621);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp, 600, 621);
            drawTextRotate(canvasTemp, 90, 5, 220);
            drawTextRotate(canvasTemp, -90, 2194, 570);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part4, height_part4, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_part8 + height_part1 + height_part2 + width_part3 + margin * 4, null);

            //part5
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part5);
            bitmapTemp = createBitmap(MainActivity.instance.bitmaps.get(0), 930, 2503, 2194, 1147);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp, 600, 28);
            drawText(canvasTemp, 600, 1147);
            drawTextRotate(canvasTemp, 90, 2, 220);
            drawTextRotate(canvasTemp, -90, 2192, 570);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part5, height_part5, true);

            matrixCombine.reset();
            matrixCombine.postRotate(90);
            matrixCombine.postTranslate(height_part5 + height_part3 + margin, height_part8 + height_part1 + height_part2 + margin * 3);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //part6
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part6);
            bitmapTemp = createBitmap(MainActivity.instance.bitmaps.get(0), 34, 2097, 1010, 1364);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRotate(canvasTemp, 91, 980, 500);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part6, height_part6, true);
            canvasCombine.drawBitmap(bitmapTemp, height_part3 + height_part5 + margin * 2, height_part8 + margin * 2, null);

            //part7
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part7);
            bitmapTemp = createBitmap(MainActivity.instance.bitmaps.get(0), 3007, 2097, 1010, 1364);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRotate(canvasTemp, -91, 40, 850);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part7, height_part7, true);
            canvasCombine.drawBitmap(bitmapTemp, height_part3 + height_part5 + margin * 2, height_part8 + height_part6 + margin * 4, null);

            //part8
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part8);
            bitmapTemp = createBitmap(4100, 968, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);

            Bitmap bitmapCut = createBitmap(MainActivity.instance.bitmaps.get(0), 62, 2122, 968, 826);
            matrixCombine.reset();
            matrixCombine.postRotate(-90);
            matrixCombine.postTranslate(0, 968);
            canvasTemp.drawBitmap(bitmapCut, matrixCombine, null);

            bitmapCut = createBitmap(MainActivity.instance.bitmaps.get(0), 826, 3532, 2448, 968);
            canvasTemp.drawBitmap(bitmapCut, 826, 0, null);

            bitmapCut = createBitmap(MainActivity.instance.bitmaps.get(0), 3033, 2122, 968, 826);
            matrixCombine.reset();
            matrixCombine.postRotate(90);
            matrixCombine.postTranslate(4100, 0);
            canvasTemp.drawBitmap(bitmapCut, matrixCombine, null);
            bitmapCut.recycle();
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawText(canvasTemp, 1537, 28);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part8, height_part8, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 5) {
            //part1
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part1);
            Bitmap bitmapTemp = createBitmap(3440, 845, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp, 1110, 845);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part1, height_part1, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_part8 + margin, null);

            //part2
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part2);
            bitmapTemp = createBitmap(3440, 387, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, -681, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp, 1110, 28);
            drawText(canvasTemp, 1110, 387);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part2, height_part2, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_part8 + height_part1 + margin * 2, null);

            //part3
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part3);
            bitmapTemp = createBitmap(1839, 2366, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -186, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp, 424, 2366);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part3, height_part3, true);

            matrixCombine.reset();
            matrixCombine.postRotate(90);
            matrixCombine.postTranslate(height_part3, height_part8 + height_part1 + height_part2 + margin * 3);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //part4
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part4);
            bitmapTemp = createBitmap(2198, 621, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, -1086, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp, 600, 621);
            drawTextRotate(canvasTemp, 90, 5, 220);
            drawTextRotate(canvasTemp, -90, 2194, 570);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part4, height_part4, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_part8 + height_part1 + height_part2 + width_part3 + margin * 4, null);

            //part5
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part5);
            bitmapTemp = createBitmap(2194, 1147, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, -1589, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp, 600, 28);
            drawText(canvasTemp, 600, 1147);
            drawTextRotate(canvasTemp, 90, 2, 220);
            drawTextRotate(canvasTemp, -90, 2192, 570);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part5, height_part5, true);

            matrixCombine.reset();
            matrixCombine.postRotate(90);
            matrixCombine.postTranslate(height_part5 + height_part3 + margin, height_part8 + height_part1 + height_part2 + margin * 3);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //part6
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part6);
            bitmapTemp = createBitmap(1010, 1364, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRotate(canvasTemp, 91, 980, 500);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part6, height_part6, true);
            canvasCombine.drawBitmap(bitmapTemp, height_part3 + height_part5 + margin * 2, height_part8 + margin * 2, null);

            //part7
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part7);
            bitmapTemp = createBitmap(1010, 1364, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRotate(canvasTemp, -91, 40, 850);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part7, height_part7, true);
            canvasCombine.drawBitmap(bitmapTemp, height_part3 + height_part5 + margin * 2, height_part8 + height_part6 + margin * 4, null);

            //part8
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part8);
            bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            bitmapTemp = createScaledBitmap(bitmapTemp, 4100, 968, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawText(canvasTemp, 1537, 28);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_part8, height_part8, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp.recycle();
        }

        try {
            String noNewCode = orderItems.get(currentID).newCode.equals("") ? orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + "_" : "";
            String nameCombine = noNewCode + orderItems.get(currentID).newCode + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

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

    void setSize(String size) {
        if (orderItems.get(currentID).imgs.size() == 1 & MainActivity.instance.bitmaps.get(0).getHeight() != 4500) {
            showDialogImageWrong(orderItems.get(currentID).order_number);
            sizeOK = false;
            return;
        }
        switch (size) {
            case "S":
                width_part1 = 2716;
                height_part1 = 608;
                width_part2 = 2716;
                height_part2 = 313;
                width_part3 = 1458;
                height_part3 = 1907;
                width_part4 = 1742;
                height_part4 = 572;
                width_part5 = 1742;
                height_part5 = 933;
                width_part6 = 826;
                height_part6 = 1181;
                width_part7 = 826;
                height_part7 = 1181;
                width_part8 = 3307;
                height_part8 = 779;
                break;
            case "M":
                width_part1 = 3425;
                height_part1 = 726;
                width_part2 = 3425;
                height_part2 = 383;
                width_part3 = 1842;
                height_part3 = 2362;
                width_part4 = 2185;
                height_part4 = 620;
                width_part5 = 2185;
                height_part5 = 1151;
                width_part6 = 1009;
                height_part6 = 1370;
                width_part7 = 1009;
                height_part7 = 1370;
                width_part8 = 4075;
                height_part8 = 968;
                break;
            case "L":
                width_part1 = 3779;
                height_part1 = 826;
                width_part2 = 3779;
                height_part2 = 419;
                width_part3 = 2008;
                height_part3 = 2598;
                width_part4 = 2362;
                height_part4 = 637;
                width_part5 = 2362;
                height_part5 = 1234;
                width_part6 = 1151;
                height_part6 = 1500;
                width_part7 = 1151;
                height_part7 = 1500;
                width_part8 = 4488;
                height_part8 = 1098;
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }
    }

    public void showDialogSizeWrong(final String order_number){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog dialog_finish;
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogTransBackGround);
                dialog_finish = builder.create();
                dialog_finish.setCancelable(false);
                dialog_finish.show();
                View view_dialog = LayoutInflater.from(context).inflate(R.layout.item_dialog_finish, null);
                dialog_finish.setContentView(view_dialog);
                TextView tv_title = (TextView) view_dialog.findViewById(R.id.tv_dialog_title);
                TextView tv_content = (TextView) view_dialog.findViewById(R.id.tv_dialog_content);
                Button bt_yes = (Button) view_dialog.findViewById(R.id.bt_dialog_yes);

                tv_title.setText("错误！");
                tv_content.setText("单号："+order_number+"没有这个尺码");
                bt_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_finish.dismiss();
                        getActivity().finish();
                    }
                });
            }
        });
    }
    public void showDialogImageWrong(final String order_number){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog dialog_finish;
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogTransBackGround);
                dialog_finish = builder.create();
                dialog_finish.setCancelable(false);
                dialog_finish.show();
                View view_dialog = LayoutInflater.from(context).inflate(R.layout.item_dialog_finish, null);
                dialog_finish.setContentView(view_dialog);
                TextView tv_title = (TextView) view_dialog.findViewById(R.id.tv_dialog_title);
                TextView tv_content = (TextView) view_dialog.findViewById(R.id.tv_dialog_content);
                Button bt_yes = (Button) view_dialog.findViewById(R.id.bt_dialog_yes);

                tv_title.setText("错误！");
                tv_content.setText("单号："+order_number+" 图片大小错误，应为4100x4500,请联系改图");
                bt_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_finish.dismiss();
                        getActivity().finish();
                    }
                });
            }
        });
    }

}
