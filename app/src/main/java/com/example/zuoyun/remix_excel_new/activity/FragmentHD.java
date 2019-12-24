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

/**
 * Created by zuoyun on 2016/11/4.
 */

public class FragmentHD extends BaseFragment {
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

    int width_1, width_2, width_side;
    int height_1,height_2,height_side;

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
        rectBorderPaint.setStrokeWidth(2);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(26);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(26);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(26);
        paintBlue.setTypeface(Typeface.DEFAULT_BOLD);
        paintBlue.setAntiAlias(true);

        paintSmall = new Paint();
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(26);
        paintSmall.setTypeface(Typeface.DEFAULT_BOLD);
        paintSmall.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                    bt_remix.setClickable(false);
                } else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
                    if(!MainActivity.instance.cb_fastmode.isChecked()){
                        iv_pillow.setImageBitmap(MainActivity.instance.bitmaps.get(0));
                    }
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
                setScale(orderItems.get(currentID).sizeStr);

                if (sizeOK) {
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

            }
        }.start();

    }

    void drawText1L(Canvas canvas) {
        canvas.save();
        canvas.rotate(-72.9f, 1176, 692);
        canvas.drawRect(1176, 692 - 26, 1176 + 530, 692, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).sizeStr + "左 " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode, 1176, 692 - 3, paint);
        canvas.restore();
    }
    void drawText1R(Canvas canvas) {
        canvas.save();
        canvas.rotate(-72.9f, 1176, 692);
        canvas.drawRect(1176, 692 - 26, 1176 + 530, 692, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).sizeStr + "右 " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode, 1176, 692 - 3, paint);
        canvas.restore();
    }
    void drawText2L(Canvas canvas) {
        canvas.save();
        canvas.rotate(88.2f, 29, 1332);
        canvas.drawRect(29, 1332 - 26, 29 + 250, 1332, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number + " " + time, 29, 1332 - 3, paint);
        canvas.restore();

        canvas.drawRect(270, 1884 - 26, 270 + 100, 1884, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + " 左", 270, 1884 - 3, paint);
    }
    void drawText2R(Canvas canvas) {
        canvas.save();
        canvas.rotate(-88.2f, 713, 1587);
        canvas.drawRect(713, 1587 - 26, 713 + 250, 1587, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number + " " + time, 713, 1587 - 3, paint);
        canvas.restore();

        canvas.drawRect(381, 1884-26, 381 + 100, 1884, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + " 右", 381, 1884 - 3, paint);
    }
    void drawTextSideL(Canvas canvas) {
        canvas.save();
        canvas.rotate(-90, 210, 3780);
        canvas.drawText(time + "  " + orderItems.get(currentID).sizeStr + "左 " + orderItems.get(currentID).order_number, 210, 3780 - 2, paint);
        canvas.restore();
    }
    void drawTextSideR(Canvas canvas) {
        canvas.save();
        canvas.rotate(90, 10, 3330);
        canvas.drawText(time + "  " + orderItems.get(currentID).sizeStr + "右 " + orderItems.get(currentID).order_number, 10, 3330 - 2, paint);
        canvas.restore();
    }

    public void remixx(){
        int margin = 60;
        Matrix matrix = new Matrix();
        int combineHeight = Math.max(height_side, height_1 * 2 + height_2);

        Bitmap bitmapCombine = Bitmap.createBitmap(width_2 * 2 + width_side * 2 + margin * 3, combineHeight, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 4) {
            //1
            Bitmap bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hd_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText1L(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_1, height_1, true);
            canvasCombine.drawBitmap(bitmapTemp, (width_side * 2 + width_2 * 2) / 2 - width_1 / 2, height_2, null);

            bitmapTemp = MainActivity.instance.bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText1R(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_1, height_1, true);
            canvasCombine.drawBitmap(bitmapTemp, (width_side * 2 + width_2 * 2) / 2 - width_1 / 2, height_2 + height_1, null);

            //2
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 349, 116, 750, 1910);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hd_sole_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText2L(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_2, height_2, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side + width_2 + margin * 2, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 349, 116, 750, 1910);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hd_sole_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText2R(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_2, height_2, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side + margin, 0, null);

            //sideL
            Bitmap bitmapSide = Bitmap.createBitmap(221, 4284, Bitmap.Config.ARGB_8888);
            Canvas canvasSide = new Canvas(bitmapSide);
            canvasSide.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasSide.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 50, 0, 149, 2142);
            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(149 + 16, 2142 + 2142);
            canvasSide.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1249, 0, 149, 2142);
            canvasSide.drawBitmap(bitmapTemp, 16, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hd_side_l);
            canvasSide.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideL(canvasSide);
            bitmapSide = Bitmap.createScaledBitmap(bitmapSide, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapSide, 0, 0, null);

            //sideR
            bitmapSide = Bitmap.createBitmap(221, 4284, Bitmap.Config.ARGB_8888);
            canvasSide = new Canvas(bitmapSide);
            canvasSide.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasSide.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 1249, 0, 149, 2142);
            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(149 + 56, 2142 + 2142);
            canvasSide.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 50, 0, 149, 2142);
            canvasSide.drawBitmap(bitmapTemp, 56, 0, null);
            bitmapTemp.recycle();
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hd_side_r);
            canvasSide.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextSideR(canvasSide);
            bitmapSide = Bitmap.createScaledBitmap(bitmapSide, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapSide, width_side + width_2 * 2 + margin * 3, 0, null);
            bitmapSide.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //1
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1447, 2144, 1387, 1239);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hd_up);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText1L(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_1, height_1, true);
            canvasCombine.drawBitmap(bitmapTemp, (width_side * 2 + width_2 * 2) / 2 - width_1 / 2, height_2, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 64, 2144, 1387, 1239);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText1R(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_1, height_1, true);
            canvasCombine.drawBitmap(bitmapTemp, (width_side * 2 + width_2 * 2) / 2 - width_1 / 2, height_2 + height_1, null);

            //2
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1798, 123, 750, 1910);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hd_sole_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText2L(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_2, height_2, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side + width_2 + margin * 2, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 348, 123, 750, 1910);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hd_sole_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText2R(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_2, height_2, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side + margin, 0, null);

            //sideL
            Bitmap bitmapSide = Bitmap.createBitmap(221, 4284, Bitmap.Config.ARGB_8888);
            Canvas canvasSide = new Canvas(bitmapSide);
            canvasSide.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasSide.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1499, 7, 149, 2142);
            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(149 + 16, 2142 + 2142);
            canvasSide.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2698, 7, 149, 2142);
            canvasSide.drawBitmap(bitmapTemp, 16, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hd_side_l);
            canvasSide.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideL(canvasSide);
            bitmapSide = Bitmap.createScaledBitmap(bitmapSide, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapSide, 0, 0, null);

            //sideR
            bitmapSide = Bitmap.createBitmap(221, 4284, Bitmap.Config.ARGB_8888);
            canvasSide = new Canvas(bitmapSide);
            canvasSide.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasSide.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1248, 7, 149, 2142);
            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(149 + 56, 2142 + 2142);
            canvasSide.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 49, 7, 149, 2142);
            canvasSide.drawBitmap(bitmapTemp, 56, 0, null);
            bitmapTemp.recycle();
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hd_side_r);
            canvasSide.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextSideR(canvasSide);
            bitmapSide = Bitmap.createScaledBitmap(bitmapSide, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapSide, width_side + width_2 * 2 + margin * 3, 0, null);
            bitmapSide.recycle();
        }



        String nameCombine = orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";
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



        try {
//            matrix.reset();
//            matrix.postRotate(-90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);


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
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + orderItems.get(currentID).sizeStr);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + orderItems.get(currentID).sizeStr);
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

    void setScale(String size) {
        switch (size) {
            case "29.5":
                width_1 = 1037;
                height_1 = 809;
                width_2 = 580;
                height_2 = 1276;
                width_side = 236;
                height_side = 2926;
                break;
            case "31.5":
                width_1 = 1088;
                height_1 = 865;
                width_2 = 599;
                height_2 = 1356;
                width_side = 235;
                height_side = 3100;
                break;
            case "34.5":
                width_1 = 1164;
                height_1 = 950;
                width_2 = 627;
                height_2 = 1479;
                width_side = 236;
                height_side = 3359;
                break;
            case "36.5":
                width_1 = 1216;
                height_1 = 1005;
                width_2 = 643;
                height_2 = 1563;
                width_side = 236;
                height_side = 3532;
                break;
            case "38.5":
                width_1 = 1268;
                height_1 = 1058;
                width_2 = 663;
                height_2 = 1647;
                width_side = 236;
                height_side = 3705;
                break;
            case "40.5":
                width_1 = 1319;
                height_1 = 1116;
                width_2 = 680;
                height_2 = 1728;
                width_side = 236;
                height_side = 3877;
                break;
            case "41.5":
                width_1 = 1346;
                height_1 = 1142;
                width_2 = 690;
                height_2 = 1768;
                width_side = 236;
                height_side = 3964;
                break;
            case "43.5":
                width_1 = 1396;
                height_1 = 1199;
                width_2 = 706;
                height_2 = 1851;
                width_side = 236;
                height_side = 4137;
                break;
            case "46.5":
                width_1 = 1409;
                height_1 = 1280;
                width_2 = 742;
                height_2 = 1983;
                width_side = 236;
                height_side = 4396;
                break;
            case "48.5":
                width_1 = 1461;
                height_1 = 1336;
                width_2 = 759;
                height_2 = 2065;
                width_side = 236;
                height_side = 4569;
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }
    }

//    void setSize(String size) {
//        switch (size) {
//            case "29.5":
//                width_1 = 1034;
//                height_1 = 792;
//                width_2 = 588;
//                height_2 = 1242;
//                width_side = 230;
//                height_side = 2889;
//                break;
//            case "31.5":
//                width_1 = 1086;
//                height_1 = 846;
//                width_2 = 606;
//                height_2 = 1322;
//                width_side = 230;
//                height_side = 3059;
//                break;
//            case "34.5":
//                width_1 = 1163;
//                height_1 = 926;
//                width_2 = 625;
//                height_2 = 1445;
//                width_side = 230;
//                height_side = 3315;
//                break;
//            case "36.5":
//                width_1 = 1150;
//                height_1 = 980;
//                width_2 = 659;
//                height_2 = 1533;
//                width_side = 230;
//                height_side = 3416;
//                break;
//            case "38.5":
//                width_1 = 1266;
//                height_1 = 1035;
//                width_2 = 676;
//                height_2 = 1609;
//                width_side = 230;
//                height_side = 3657;
//                break;
//            case "40.5":
//                width_1 = 1318;
//                height_1 = 1089;
//                width_2 = 694;
//                height_2 = 1689;
//                width_side = 230;
//                height_side = 3828;
//                break;
//            case "41.5":
//                width_1 = 1344;
//                height_1 = 1116;
//                width_2 = 703;
//                height_2 = 1728;
//                width_side = 230;
//                height_side = 3913;
//                break;
//            case "43.5":
//                width_1 = 1395;
//                height_1 = 1170;
//                width_2 = 721;
//                height_2 = 1808;
//                width_side = 230;
//                height_side = 4084;
//                break;
//            case "46.5":
//                width_1 = 1408;
//                height_1 = 1249;
//                width_2 = 748;
//                height_2 = 1927;
//                width_side = 230;
//                height_side = 4340;
//                break;
//            case "48.5":
//                width_1 = 1460;
//                height_1 = 1303;
//                width_2 = 766;
//                height_2 = 2006;
//                width_side = 230;
//                height_side = 4525;
//                break;
//            default:
//                showDialogSizeWrong(orderItems.get(currentID).order_number);
//                sizeOK = false;
//                break;
//        }
//    }

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
                tv_content.setText("单号："+order_number+"读取尺码失败");
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

    String getColor(String color){
        if (color.equals("White")) {
            return "白灯";
        } else if (color.equals("Green")) {
            return "绿灯";
        } else if (color.equals("Blue")) {
            return "蓝灯";
        } else if (color.equals("Red")) {
            return "红灯";
        } else {
            return "无灯";
        }
    }
}
