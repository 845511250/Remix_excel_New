package com.example.zuoyun.remix_excel_new.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
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

/**
 * Created by zuoyun on 2016/11/4.
 */

public class FragmentGH extends BaseFragment {
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

    int width_front,width_back, width_xiuzi, width_lingkou;
    int height_front,height_back, height_xiuzi, height_lingkou;

    int num;
    String strPlus = "";
    int intPlus = 1;
    boolean sizeOK = true;

    Paint rectPaint, paint, paintRed, paintBlue, rectBorderPaint, paintSmall;
    String time;

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
        paint.setTextSize(18);
        paint.setAntiAlias(true);



        time = MainActivity.instance.orderDate_Print;

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                    bt_remix.setClickable(false);
                } else if (message == MainActivity.LOADED_IMGS) {
                    if(!MainActivity.instance.cb_fastmode.isChecked())
                        iv_pillow.setImageBitmap(MainActivity.instance.bitmaps.get(0));
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
                setSize(orderItems.get(currentID).sizeStr);
                if (sizeOK) {
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
            }
        }.start();

    }

    void drawTextFront(Canvas canvas) {
        canvas.save();
        canvas.rotate(88.6f, 8, 1383);
        canvas.drawRect(8, 1383 - 18, 8 + 350, 1383, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "-" + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 10, 1383 - 2, paint);
        canvas.restore();
    }
    void drawTextBack(Canvas canvas) {
        canvas.save();
        canvas.rotate(88.6f, 6, 1254);
        canvas.drawRect(6, 1254 - 18, 6 + 350, 1254, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "-" + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 8, 1254 - 2, paint);
        canvas.restore();
    }

    void drawTextXiuziR(Canvas canvas) {
        canvas.save();
        canvas.rotate(-59.5f, 1777, 687);
        canvas.drawRect(1777, 687 - 18, 1777 + 250, 687, rectPaint);
        canvas.drawText("右 " + orderItems.get(currentID).sku + "-" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number, 1779, 687 - 2, paint);
        canvas.restore();
    }
    void drawTextXiuziL(Canvas canvas) {
        canvas.save();
        canvas.rotate(-59.5f, 1777, 687);
        canvas.drawRect(1777, 687 - 18, 1777 + 250, 687, rectPaint);
        canvas.drawText("左 " + orderItems.get(currentID).sku + "-" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number, 1777, 687 - 2, paint);
        canvas.restore();
    }

    public void remixx(){
        Bitmap bitmapCombine = Bitmap.createBitmap(width_front + width_back + width_lingkou + 210, height_front, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        Matrix matrix = new Matrix();
        if (MainActivity.instance.bitmaps.get(0).getWidth() == 2989) {//dropshipping
            Bitmap bitmapF = MainActivity.instance.bitmaps.get(0);
            Bitmap bitmapB = orderItems.get(currentID).imgs.size() == 1 ? bitmapF : MainActivity.instance.bitmaps.get(1);

            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(bitmapF, 403, 0, 2202, 3192);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gh_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp.recycle();

            //后面
            bitmapTemp = Bitmap.createBitmap(bitmapB, 404, 0, 2202, 3186);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gh_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            matrix = new Matrix();
            matrix.postTranslate(width_front + 70, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();

            //领口
            bitmapTemp = Bitmap.createBitmap(bitmapF, 517, 124, 1972, 206);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gh_lingkou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
//        drawTextLingkou(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lingkou, height_lingkou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 140, height_xiuzi * 2 + 300, null);
            bitmapTemp.recycle();

            //左袖子
            Bitmap bitmapCut = Bitmap.createBitmap(bitmapF, 1921, 223, 1068, 1198);
            Bitmap bitmapHalf = Bitmap.createBitmap(954, 1028, Bitmap.Config.ARGB_8888);
            Canvas canvasHalf = new Canvas(bitmapHalf);
            canvasHalf.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasHalf.drawColor(0xffffffff);

            matrix.reset();
            matrix.postRotate(15.9f);
            matrix.postTranslate(197, -216);
            canvasHalf.drawBitmap(bitmapCut, matrix, null);
            bitmapCut.recycle();

            bitmapTemp = Bitmap.createBitmap(1908, 1028, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(bitmapHalf, 0, 0, null);

            bitmapCut = Bitmap.createBitmap(bitmapB, 0, 281, 1077, 1212);
            bitmapHalf = Bitmap.createBitmap(954, 1028, Bitmap.Config.ARGB_8888);
            canvasHalf = new Canvas(bitmapHalf);
            canvasHalf.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasHalf.drawColor(0xffffffff);

            matrix.reset();
            matrix.postRotate(-17.9f);
            matrix.postTranslate(-298, 97);
            canvasHalf.drawBitmap(bitmapCut, matrix, null);
            bitmapCut.recycle();
            canvasTemp.drawBitmap(bitmapHalf, 954, 0, null);
            bitmapHalf.recycle();

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gh_arm);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 140, 0, null);
            bitmapTemp.recycle();

            //右袖子
            bitmapCut = Bitmap.createBitmap(bitmapB, 1921, 223, 1068, 1198);
            bitmapHalf = Bitmap.createBitmap(954, 1028, Bitmap.Config.ARGB_8888);
            canvasHalf = new Canvas(bitmapHalf);
            canvasHalf.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasHalf.drawColor(0xffffffff);

            matrix.reset();
            matrix.postRotate(15.9f);
            matrix.postTranslate(197, -216);
            canvasHalf.drawBitmap(bitmapCut, matrix, null);
            bitmapCut.recycle();

            bitmapTemp = Bitmap.createBitmap(1908, 1028, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(bitmapHalf, 0, 0, null);


            bitmapCut = Bitmap.createBitmap(bitmapF, 0, 281, 1077, 1212);
            bitmapHalf = Bitmap.createBitmap(954, 1028, Bitmap.Config.ARGB_8888);
            canvasHalf = new Canvas(bitmapHalf);
            canvasHalf.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasHalf.drawColor(0xffffffff);

            matrix.reset();
            matrix.postRotate(-17.9f);
            matrix.postTranslate(-298, 97);
            canvasHalf.drawBitmap(bitmapCut, matrix, null);
            bitmapCut.recycle();
            canvasTemp.drawBitmap(bitmapHalf, 954, 0, null);
            bitmapHalf.recycle();

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gh_arm);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 140, height_xiuzi + 150, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 4902) {//大国
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(2202, 3192, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -185, -1593, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gh_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(2202, 3186, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2517, -1593, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gh_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            matrix.postTranslate(width_front + 70, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //领口
            bitmapTemp = Bitmap.createBitmap(1972, 206, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1466, -1267, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gh_lingkou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lingkou, height_lingkou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 140, height_xiuzi * 2 + 300, null);

            //左袖子
            bitmapTemp = Bitmap.createBitmap(1909, 1029, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2716, -121, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gh_arm);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 140, 0, null);

            //右袖子
            bitmapTemp = Bitmap.createBitmap(1909, 1029, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -283, -121, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gh_arm);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 140, height_xiuzi + 150, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 5) {
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(2202, 3192, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gh_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(2202, 3186, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gh_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            matrix.postTranslate(width_front + 70, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //领口
            bitmapTemp = Bitmap.createBitmap(1972, 206, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gh_lingkou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lingkou, height_lingkou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 140, height_xiuzi * 2 + 300, null);

            //左袖子
            bitmapTemp = Bitmap.createBitmap(1909, 1029, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gh_arm);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 140, 0, null);

            //右袖子
            bitmapTemp = Bitmap.createBitmap(1909, 1029, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gh_arm);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 140, height_xiuzi + 150, null);
            bitmapTemp.recycle();
        }


        try {
            matrix.reset();
            matrix.postRotate(90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";
            if(orderItems.get(currentID).platform.equals("zy")){
                nameCombine = orderItems.get(currentID).sku + "(" + MainActivity.instance.orderDate_short + "-" + (currentID + 1) + ")_" + orderItems.get(currentID).sizeStr + "_" + orderItems.get(currentID).order_number + "_共" + orderItems.get(currentID).newCode + "个" + strPlus + ".jpg";
            }

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 100);
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
        switch (size) {
            case "XS":
                width_front = 1960;
                height_front = 2950;
                width_back = 1960;
                height_back = 2950;
                width_xiuzi = 1670;
                height_xiuzi = 910;
                width_lingkou = 1730;
                height_lingkou = 207;
                break;
            case "S":
                width_front = 2043;
                height_front = 3033;
                width_back = 2043;
                height_back = 3027;
                width_xiuzi = 1752;
                height_xiuzi = 951;
                width_lingkou = 1806;
                height_lingkou = 207;
                break;
            case "M":
                width_front = 2123;
                height_front = 3112;
                width_back = 2123;
                height_back = 3107;
                width_xiuzi = 1831;
                height_xiuzi = 989;
                width_lingkou = 1890;
                height_lingkou = 207;
                break;
            case "L":
                width_front = 2202;
                height_front = 3192;
                width_back = 2202;
                height_back = 3187;
                width_xiuzi = 1910;
                height_xiuzi = 1029;
                width_lingkou = 1973;
                height_lingkou = 207;
                break;
            case "XL":
                width_front = 2284;
                height_front = 3273;
                width_back = 2284;
                height_back = 3268;
                width_xiuzi = 1989;
                height_xiuzi = 1070;
                width_lingkou = 2058;
                height_lingkou = 207;
                break;
            case "XXL":
                width_front = 2363;
                height_front = 3354;
                width_back = 2363;
                height_back = 3348;
                width_xiuzi = 2068;
                height_xiuzi = 1109;
                width_lingkou = 2141;
                height_lingkou = 207;
                break;
            case "2XL":
                width_front = 2363;
                height_front = 3354;
                width_back = 2363;
                height_back = 3348;
                width_xiuzi = 2068;
                height_xiuzi = 1109;
                width_lingkou = 2141;
                height_lingkou = 207;
                break;
            case "XXXL":
                width_front = 2442;
                height_front = 3433;
                width_back = 2442;
                height_back = 3427;
                width_xiuzi = 2147;
                height_xiuzi = 1148;
                width_lingkou = 2226;
                height_lingkou = 207;
                break;
            case "3XL":
                width_front = 2442;
                height_front = 3433;
                width_back = 2442;
                height_back = 3427;
                width_xiuzi = 2147;
                height_xiuzi = 1148;
                width_lingkou = 2226;
                height_lingkou = 207;
                break;
            case "4XL":
                width_front = 2442 + 80;
                height_front = 3433 + 80;
                width_back = 2442 + 80;
                height_back = 3427 + 80;
                width_xiuzi = 2147 + 80;
                height_xiuzi = 1148 + 40;
                width_lingkou = 2226 + 80;
                height_lingkou = 207;
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

}
