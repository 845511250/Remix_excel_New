package com.example.zuoyun.remix_excel_new.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.util.Log;
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

public class FragmentD69 extends BaseFragment {
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

    int width_front, width_back, width_sleeve, width_collar, width_cuff;
    int height_front, height_back, height_sleeve, height_collar, height_cuff;

    int width_combine, height_combine;
    int x_front, x_back, x_sleeve_left, x_sleeve_right, x_collar, x_cuff_left, x_cuff_right;
    int y_front, y_back, y_sleeve_left, y_sleeve_right, y_collar, y_cuff_left, y_cuff_right;
    int id_front, id_back, id_sleeve_left;

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
        orderItems = MainActivity.instance.orderItems;
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
        paint.setTextSize(19);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(20);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(20);
        paintBlue.setTypeface(Typeface.DEFAULT_BOLD);
        paintBlue.setAntiAlias(true);

        paintSmall = new Paint();
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(20);
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
                    if (!MainActivity.instance.cb_fastmode.isChecked())
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

    public void remix() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                setSize(orderItems.get(currentID).sizeStr);
                if (sizeOK) {
                    for (num = orderItems.get(currentID).num; num >= 1; num--) {
                        intPlus = orderItems.get(currentID).num - num + 1;
                        for (int i = 0; i < currentID; i++) {
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

    void drawTextBack(Canvas canvas) {
        canvas.drawRect(1000, 5574 - 19, 1000 + 400, 5574, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1000, 5574 - 2, paint);
    }
    void drawTextFront(Canvas canvas) {
        canvas.drawRect(1000, 5603 - 19, 1000 + 400, 5603, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1000, 5603 - 2, paint);
    }
    void drawTextSleeve(Canvas canvas, String LR) {
        canvas.drawRect(201, 2571 - 19, 201 + 400, 2571, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + LR + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 201, 2571 - 2, paint);
    }
    void drawTextCuff(Canvas canvas, String LR) {
        canvas.drawRect(19, 30 - 19, 19 + 400, 30, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + LR + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 19, 30 - 2, paint);
    }

    public void remixx() {
        Bitmap bitmapCombine = Bitmap.createBitmap(width_combine, height_combine, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 7) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(2917, 5618, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front, y_front, null);

            //back
            bitmapTemp = Bitmap.createBitmap(2912, 5589, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back, y_back, null);

            //sleeve_left
            bitmapTemp = Bitmap.createBitmap(1934, 2584, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "左袖");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_left, y_sleeve_left, null);

            //sleeve_right
            bitmapTemp = Bitmap.createBitmap(1934, 2584, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(6), 0, 0, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1934, 2584, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "右袖");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_right, y_sleeve_right, null);

            //collar
            bitmapTemp = Bitmap.createBitmap(5084, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.d69_collar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextRight(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);
            canvasCombine.drawBitmap(bitmapTemp, x_collar, y_collar, null);

            //cuff_left
            bitmapTemp = Bitmap.createBitmap(1107, 568, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.d69_cuff);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextCuff(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
            canvasCombine.drawBitmap(bitmapTemp, x_cuff_left, y_cuff_left, null);

            //cuff_right
            bitmapTemp = Bitmap.createBitmap(1107, 568, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextCuff(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
            canvasCombine.drawBitmap(bitmapTemp, x_cuff_right, y_cuff_right, null);


            bitmapTemp.recycle();
            bitmapDB.recycle();
            if (num == 1) {
                MainActivity.recycleExcelImages();
            }

        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(2917, 5618, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1985, -707, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front, y_front, null);

            //back
            bitmapTemp = Bitmap.createBitmap(2912, 5589, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -6833, -707, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back, y_back, null);

            //sleeve_left
            bitmapTemp = Bitmap.createBitmap(1934, 2584, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4799, -707, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "左袖");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_left, y_sleeve_left, null);

            //sleeve_right
            bitmapTemp = Bitmap.createBitmap(1934, 2584, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -154, -707, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1934, 2584, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "右袖");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_right, y_sleeve_right, null);

            //collar
            bitmapTemp = Bitmap.createBitmap(5084, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -901, -174, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.d69_collar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextRight(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);
            canvasCombine.drawBitmap(bitmapTemp, x_collar, y_collar, null);

            //cuff_left
            bitmapTemp = Bitmap.createBitmap(1107, 568, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -5213, -3371, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.d69_cuff);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextCuff(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
            canvasCombine.drawBitmap(bitmapTemp, x_cuff_left, y_cuff_left, null);

            //cuff_right
            bitmapTemp = Bitmap.createBitmap(1107, 568, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -567, -3371, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextCuff(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
            canvasCombine.drawBitmap(bitmapTemp, x_cuff_right, y_cuff_right, null);


            bitmapTemp.recycle();
            bitmapDB.recycle();
            if (num == 1) {
                MainActivity.recycleExcelImages();
            }
        }


        try {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(90);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            Log.e("aaa", pathSave + nameCombine);
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 120);



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
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku);
            sheet.addCell(label1);
            int num = orderItems.get(currentID).num;
            Number number2 = new Number(2, currentID + 1, num);
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

    public void checkremix() {
        if (MainActivity.instance.tb_auto.isChecked()) {
            remix();
        }
    }

    void setSize(String size) {
        switch (size) {
            case "S":
                width_front = 2408;
                height_front = 5249;
                width_back = 2437;
                height_back = 5218;
                width_sleeve = 1559;
                height_sleeve = 2215;
                width_collar = 3956;
                height_collar = 460;
                width_cuff = 923;
                height_cuff = 573;

                width_combine = 6594;
                height_combine = 5806;
                x_front = 0;
                y_front = 560;
                x_back = 2505;
                y_back = 590;
                x_sleeve_left = 4941;
                y_sleeve_left = 0;
                x_sleeve_right = 5035;
                y_sleeve_right = 2290;
                x_collar = 0;
                y_collar = 0;
                x_cuff_left = 5343;
                y_cuff_left = 4584;
                x_cuff_right = 5343;
                y_cuff_right = 5233;

                id_front = R.drawable.d69_front_m;
                id_back = R.drawable.d69_back_m;
                id_sleeve_left = R.drawable.d69_sleeve_left_m;
                break;
            case "M":
                width_front = 2533;
                height_front = 5342;
                width_back = 2557;
                height_back = 5313;
                width_sleeve = 1654;
                height_sleeve = 2310;
                width_collar = 4240;
                height_collar = 460;
                width_cuff = 970;
                height_cuff = 573;

                width_combine = 6969;
                height_combine = 5902;
                x_front = 0;
                y_front = 560;
                x_back = 2645;
                y_back = 586;
                x_sleeve_left = 5315;
                y_sleeve_left = 692;
                x_sleeve_right = 5315;
                y_sleeve_right = 3107;
                x_collar = 0;
                y_collar = 0;
                x_cuff_left = 4889;
                y_cuff_left = 0;
                x_cuff_right = 5975;
                y_cuff_right = 0;

                id_front = R.drawable.d69_front_m;
                id_back = R.drawable.d69_back_m;
                id_sleeve_left = R.drawable.d69_sleeve_left_m;
                break;
            case "L":
                width_front = 2656;
                height_front = 5437;
                width_back = 2676;
                height_back = 5406;
                width_sleeve = 1751;
                height_sleeve = 2408;
                width_collar = 4522;
                height_collar = 459;
                width_cuff = 1017;
                height_cuff = 573;

                width_combine = 7184;
                height_combine = 6200;
                x_front = 0;
                y_front = 572;
                x_back = 2737;
                y_back = 751;
                x_sleeve_left = 5432;
                y_sleeve_left = 0;
                x_sleeve_right = 5432;
                y_sleeve_right = 2493;
                x_collar = 0;
                y_collar = 0;
                x_cuff_left = 5799;
                y_cuff_left = 4976;
                x_cuff_right = 5799;
                y_cuff_right = 5627;

                id_front = R.drawable.d69_front_xl;
                id_back = R.drawable.d69_back_xl;
                id_sleeve_left = R.drawable.d69_sleeve_left_xl;
                break;
            case "XL":
                width_front = 2778;
                height_front = 5529;
                width_back = 2796;
                height_back = 5500;
                width_sleeve = 1844;
                height_sleeve = 2496;
                width_collar = 4805;
                height_collar = 459;
                width_cuff = 1065;
                height_cuff = 572;

                width_combine = 5682;
                height_combine = 8677;
                x_front = 0;
                y_front = 548;
                x_back = 2886;
                y_back = 548;
                x_sleeve_left = 1964;
                y_sleeve_left = 6181;
                x_sleeve_right = 0;
                y_sleeve_right = 6181;
                x_collar = 0;
                y_collar = 0;
                x_cuff_left = 4010;
                y_cuff_left = 6181;
                x_cuff_right = 4010;
                y_cuff_right = 6992;

                id_front = R.drawable.d69_front_xl;
                id_back = R.drawable.d69_back_xl;
                id_sleeve_left = R.drawable.d69_sleeve_left_xl;
                break;
            case "2XL":
                width_front = 2898;
                height_front = 5619;
                width_back = 2912;
                height_back = 5590;
                width_sleeve = 1935;
                height_sleeve = 2587;
                width_collar = 5084;
                height_collar = 459;
                width_cuff = 1107;
                height_cuff = 568;

                width_combine = 5933;
                height_combine = 8861;
                x_front = 0;
                y_front = 552;
                x_back = 3021;
                y_back = 581;
                x_sleeve_left = 2054;
                y_sleeve_left = 6274;
                x_sleeve_right = 0;
                y_sleeve_right = 6274;
                x_collar = 0;
                y_collar = 0;
                x_cuff_left = 4170;
                y_cuff_left = 6351;
                x_cuff_right = 4171;
                y_cuff_right = 7128;

                id_front = R.drawable.d69_front_xl;
                id_back = R.drawable.d69_back_xl;
                id_sleeve_left = R.drawable.d69_sleeve_left_xl;
                break;
            case "3XL":
                width_front = 3026;
                height_front = 5721;
                width_back = 3037;
                height_back = 5691;
                width_sleeve = 2035;
                height_sleeve = 2686;
                width_collar = 5372;
                height_collar = 459;
                width_cuff = 1158;
                height_cuff = 573;

                width_combine = 6210;
                height_combine = 9071;
                x_front = 0;
                y_front = 556;
                x_back = 3152;
                y_back = 556;
                x_sleeve_left = 2139;
                y_sleeve_left = 6385;
                x_sleeve_right = 0;
                y_sleeve_right = 6385;
                x_collar = 0;
                y_collar = 0;
                x_cuff_left = 4362;
                y_cuff_left = 6385;
                x_cuff_right = 4362;
                y_cuff_right = 7084;

                id_front = R.drawable.d69_front_3xl;
                id_back = R.drawable.d69_back_3xl;
                id_sleeve_left = R.drawable.d69_sleeve_left_3xl;
                break;
            case "4XL":
                width_front = 3149;
                height_front = 5814;
                width_back = 3156;
                height_back = 5784;
                width_sleeve = 2130;
                height_sleeve = 2780;
                width_collar = 5655;
                height_collar = 459;
                width_cuff = 1206;
                height_cuff = 574;

                width_combine = 6614;
                height_combine = 9258;
                x_front = 0;
                y_front = 568;
                x_back = 3301;
                y_back = 568;
                x_sleeve_left = 2225;
                y_sleeve_left = 6478;
                x_sleeve_right = 0;
                y_sleeve_right = 6478;
                x_collar = 0;
                y_collar = 0;
                x_cuff_left = 4511;
                y_cuff_left = 6478;
                x_cuff_right = 4511;
                y_cuff_right = 7201;

                id_front = R.drawable.d69_front_3xl;
                id_back = R.drawable.d69_back_3xl;
                id_sleeve_left = R.drawable.d69_sleeve_left_3xl;
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }
        width_collar += 20;
        height_collar += 18;
    }

    public void showDialogSizeWrong(final String order_number) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog dialog_finish;
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTransBackGround);
                dialog_finish = builder.create();
                dialog_finish.setCancelable(false);
                dialog_finish.show();
                View view_dialog = LayoutInflater.from(context).inflate(R.layout.item_dialog_finish, null);
                dialog_finish.setContentView(view_dialog);
                TextView tv_title = (TextView) view_dialog.findViewById(R.id.tv_dialog_title);
                TextView tv_content = (TextView) view_dialog.findViewById(R.id.tv_dialog_content);
                Button bt_yes = (Button) view_dialog.findViewById(R.id.bt_dialog_yes);

                tv_title.setText("错误！");
                tv_content.setText("单号：" + order_number + "没有这个尺码");
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

    boolean checkContains(String nameContains) {
        for (int i = 0; i < orderItems.get(currentID).imgs.size(); i++) {
            if (orderItems.get(currentID).imgs.get(i).contains(nameContains)) {
                return true;
            }
        }
        return false;
    }

    Bitmap getBitmapWith(String nameContains) {
        for (int i = 0; i < orderItems.get(currentID).imgs.size(); i++) {
            if (orderItems.get(currentID).imgs.get(i).contains(nameContains)) {
                return MainActivity.instance.bitmaps.get(i);
            }
        }
        return null;
    }

}
