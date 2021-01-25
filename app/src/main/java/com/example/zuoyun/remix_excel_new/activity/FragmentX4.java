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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zuoyun.remix_excel_new.R;
import com.example.zuoyun.remix_excel_new.bean.OrderItem;
import com.example.zuoyun.remix_excel_new.tools.BitmapToJpg;
import com.example.zuoyun.remix_excel_new.tools.BitmapToPng;

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

public class FragmentX4 extends BaseFragment {
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

    int width_front, width_back, width_sleeve, width_down_front, width_down_back;
    int height_front, height_back, height_sleeve, height_down_front, height_down_back;

    int width_combine, height_combine;
    int x_front, x_back, x_sleeve_left, x_sleeve_right, x_down_back_left, x_down_back_right, x_down_front;
    int y_front, y_back, y_sleeve_left, y_sleeve_right, y_down_back_left, y_down_back_right, y_down_front;

    int id_front, id_back, id_sleeve_left, id_down_front, id_down_back_left;

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

    void drawTextFront(Canvas canvas) {
        canvas.drawRect(1585, 2400 - 19, 1585 + 300, 2400, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1585, 2400 - 2, paint);
    }

    void drawTextBack(Canvas canvas) {
        canvas.save();
        canvas.rotate(-1.1f, 1637, 2507);
        canvas.drawRect(1637, 2507 - 19, 1637 + 400, 2507, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1637, 2507 - 2, paint);
        canvas.restore();
    }

    void drawTextDownFront(Canvas canvas) {
        canvas.save();
        canvas.rotate(-2.6f, 3618, 4137);
        canvas.drawRect(3618, 4137 - 19, 3618 + 300, 4137, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 3618, 4137 - 2, paint);
        canvas.restore();
    }

    void drawTextBackLeft(Canvas canvas) {
        canvas.save();
        canvas.rotate(2.4f, 3286, 4128);
        canvas.drawRect(3286, 4128 - 19, 3286 + 300, 4128, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "左后片" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number, 3286, 4128 - 2, paint);
        canvas.restore();
    }

    void drawTextBackRight(Canvas canvas) {
        canvas.save();
        canvas.rotate(-2.4f, 17, 4140);
        canvas.drawRect(17, 4140 - 19, 17 + 300, 4140, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "右后片" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number, 17, 4140 - 2, paint);
        canvas.restore();
    }

    void drawTextSleeveLeft(Canvas canvas) {
        canvas.drawRect(793, 3847 - 19, 793 + 400, 3847, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "左袖" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number, 793, 3847 - 2, paint);
    }

    void drawTextSleeveRight(Canvas canvas) {
        canvas.drawRect(793, 3847 - 19, 793 + 400, 3847, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "右袖" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 793, 3847 - 2, paint);
    }


    public void remixx() {
        Bitmap bitmapCombine = null;

        if (orderItems.get(currentID).imgs.size() == 6) {
            if (orderItems.get(currentID).sizeStr.equals("3XL") || orderItems.get(currentID).sizeStr.equals("4XL")) {//激光电雕152内
                bitmapCombine = Bitmap.createBitmap(width_combine, height_combine, Bitmap.Config.ARGB_8888);
                Canvas canvasCombine = new Canvas(bitmapCombine);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasCombine.drawColor(0xffffffff);

                //back
                Bitmap bitmapTemp = Bitmap.createBitmap(3136, 2519, Bitmap.Config.ARGB_8888);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), id_back);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBack(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, x_back, y_back, null);

                //front
                bitmapTemp = Bitmap.createBitmap(3124, 2413, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getResources(), id_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, x_front, y_front, null);

                //sleeve_left
                bitmapTemp = Bitmap.createBitmap(2108, 3859, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getResources(), id_sleeve_left);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextSleeveLeft(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, x_sleeve_left, y_sleeve_left, null);

                //sleeve_right
                bitmapTemp = Bitmap.createBitmap(2108, 3859, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
                bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -2108, 3859, true);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextSleeveRight(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, x_sleeve_right, y_sleeve_right, null);

                //down_front
                bitmapTemp = Bitmap.createBitmap(7110, 4152, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getResources(), id_down_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextDownFront(canvasTemp);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_down_front, height_down_front, true);
                canvasCombine.drawBitmap(bitmapTemp, x_down_front, y_down_front, null);

                //down_back
                bitmapTemp = Bitmap.createBitmap(3605, 4153, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 3, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getResources(), id_down_back_left);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBackLeft(canvasTemp);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_down_back, height_down_back, true);
                canvasCombine.drawBitmap(bitmapTemp, x_down_back_left, y_down_back_left, null);

                bitmapTemp = Bitmap.createBitmap(3605, 4153, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3599 + 3, 0, null);
                bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -3605, 4153, true);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBackRight(canvasTemp);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_down_back, height_down_back, true);
                canvasCombine.drawBitmap(bitmapTemp, x_down_back_right, y_down_back_right, null);

                bitmapTemp.recycle();
                bitmapDB.recycle();
            } else {//裁片印108内
                int margin = 120;
                bitmapCombine = Bitmap.createBitmap(width_down_back + width_sleeve - 220, height_front + height_back + height_down_back + width_down_front + margin * 2 + 1460, Bitmap.Config.ARGB_8888);
                Canvas canvasCombine = new Canvas(bitmapCombine);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasCombine.drawColor(0xffffffff);

                //back
                Bitmap bitmapTemp = Bitmap.createBitmap(3136, 2519, Bitmap.Config.ARGB_8888);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), id_back);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBack(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

                //front
                bitmapTemp = Bitmap.createBitmap(3124, 2413, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getResources(), id_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextFront(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

                //sleeve_left
                bitmapTemp = Bitmap.createBitmap(2108, 3859, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getResources(), id_sleeve_left);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextSleeveLeft(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, width_down_back - 220, 0, null);

                //sleeve_right
                bitmapTemp = Bitmap.createBitmap(2108, 3859, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
                bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -2108, 3859, true);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextSleeveRight(canvasTemp);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
                canvasCombine.drawBitmap(bitmapTemp, width_down_back - 220, height_sleeve + margin, null);

                //down_front
                bitmapTemp = Bitmap.createBitmap(7110, 4152, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getResources(), id_down_front);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextDownFront(canvasTemp);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_down_front, height_down_front, true);

                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                matrix.postTranslate(height_down_front, height_front + height_back + height_down_back + margin * 2 + 1460);
                canvasCombine.drawBitmap(bitmapTemp, matrix, null);

                //down_back
                bitmapTemp = Bitmap.createBitmap(3605, 4153, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 3, 0, null);
                bitmapDB = BitmapFactory.decodeResource(getResources(), id_down_back_left);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBackLeft(canvasTemp);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_down_back, height_down_back, true);
                canvasCombine.drawBitmap(bitmapTemp, width_down_back - 220 + width_sleeve - width_down_back, height_sleeve * 2 + margin * 2, null);

                bitmapTemp = Bitmap.createBitmap(3605, 4153, Bitmap.Config.ARGB_8888);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3599 + 3, 0, null);
                bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -3605, 4153, true);
                canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
                drawTextBackRight(canvasTemp);
                bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_down_back, height_down_back, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front + height_back + margin * 2, null);


                bitmapTemp.recycle();
                bitmapDB.recycle();
            }

            if (num == 1) {
                MainActivity.recycleExcelImages();
            }

        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 14900) {

        }


        try {
//            if (!(orderItems.get(currentID).sizeStr.equals("3XL") || orderItems.get(currentID).sizeStr.equals("4XL"))) {//裁片108内
//                Matrix matrix = new Matrix();
//                matrix.postRotate(90);
//                bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);
//            }

            String nameCombine = orderItems.get(currentID).sku + "_" + orderItems.get(currentID).newCode_short + "_" + orderItems.get(currentID).sizeStr + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            Log.e("aaa", pathSave + nameCombine);
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
                width_front = 2556;
                height_front = 2238;
                width_back = 2566;
                height_back = 2342;
                width_down_front = 6275;
                height_down_front = 3877;
                width_down_back = 3190;
                height_down_back = 3879;
                width_sleeve = 1769;
                height_sleeve = 3634;

                width_combine = 8205;
                height_combine = 10266;
                x_front = 0;
                y_front = 0;
                x_back = 2691;
                y_back = 0;
                x_sleeve_left = 5430;
                y_sleeve_left = 0;
                x_sleeve_right = 6372;
                y_sleeve_right = 5731;
                x_down_front = 104;
                y_down_front = 6397;
                x_down_back_left = 0;
                y_down_back_left = 2470;
                x_down_back_right = 3305;
                y_down_back_right = 2470;

                id_front = R.drawable.x4_front_m;
                id_back = R.drawable.x4_back_m;
                id_down_front = R.drawable.x2_front_down_m;
                id_down_back_left = R.drawable.x2_back_downleft_m;
                id_sleeve_left = R.drawable.x5_sleeve_left_m;
                break;
            case "M":
                width_front = 2742;
                height_front = 2294;
                width_back = 2753;
                height_back = 2398;
                width_down_front = 6554;
                height_down_front = 3969;
                width_down_back = 3327;
                height_down_back = 3969;
                width_sleeve = 1882;
                height_sleeve = 3709;

                width_combine = 8198;
                height_combine = 10458;
                x_front = 0;
                y_front = 0;
                x_back = 2871;
                y_back = 0;
                x_sleeve_left = 5715;
                y_sleeve_left = 0;
                x_sleeve_right = 6316;
                y_sleeve_right = 5332;
                x_down_front = 110;
                y_down_front = 6497;
                x_down_back_left = 0;
                y_down_back_left = 2520;
                x_down_back_right = 3440;
                y_down_back_right = 2520;

                id_front = R.drawable.x4_front_m;
                id_back = R.drawable.x4_back_m;
                id_down_front = R.drawable.x2_front_down_m;
                id_down_back_left = R.drawable.x2_back_downleft_m;
                id_sleeve_left = R.drawable.x5_sleeve_left_m;
                break;
            case "L":
                width_front = 2929;
                height_front = 2350;
                width_back = 2941;
                height_back = 2456;
                width_down_front = 6832;
                height_down_front = 4059;
                width_down_back = 3467;
                height_down_back = 4061;
                width_sleeve = 1995;
                height_sleeve = 3784;

                width_combine = 8470;
                height_combine = 10699;
                x_front = 0;
                y_front = 0;
                x_back = 3047;
                y_back = 0;
                x_sleeve_left = 6058;
                y_sleeve_left = 0;
                x_sleeve_right = 6475;
                y_sleeve_right = 5495;
                x_down_front = 88;
                y_down_front = 6647;
                x_down_back_left = 0;
                y_down_back_left = 2577;
                x_down_back_right = 3588;
                y_down_back_right = 2577;

                id_front = R.drawable.x4_front_xl;
                id_back = R.drawable.x4_back_xl;
                id_down_front = R.drawable.x2_front_down_xl;
                id_down_back_left = R.drawable.x2_back_downleft_xl;
                id_sleeve_left = R.drawable.x5_sleeve_left_xl;
                break;
            case "XL":
                width_front = 3116;
                height_front = 2406;
                width_back = 3127;
                height_back = 2513;
                width_down_front = 7109;
                height_down_front = 4152;
                width_down_back = 3605;
                height_down_back = 4153;
                width_sleeve = 2108;
                height_sleeve = 3858;

                width_combine = 8750;
                height_combine = 10931;
                x_front = 0;
                y_front = 0;
                x_back = 3229;
                y_back = 0;
                x_sleeve_left = 0;
                y_sleeve_left = 2535;
                x_sleeve_right = 6428;
                y_sleeve_right = 0;
                x_down_front = 0;
                y_down_front = 6786;
                x_down_back_left = 5151;
                y_down_back_left = 3988;
                x_down_back_right = 2470;
                y_down_back_right = 2580;

                id_front = R.drawable.x4_front_xl;
                id_back = R.drawable.x4_back_xl;
                id_down_front = R.drawable.x2_front_down_xl;
                id_down_back_left = R.drawable.x2_back_downleft_xl;
                id_sleeve_left = R.drawable.x5_sleeve_left_xl;
                break;
            case "2XL":
                width_front = 3304;
                height_front = 2443;
                width_back = 3314;
                height_back = 2551;
                width_down_front = 7386;
                height_down_front = 4224;
                width_down_back = 3745;
                height_down_back = 4224;
                width_sleeve = 2221;
                height_sleeve = 3933;

                width_combine = 8761;
                height_combine = 12860;
                x_front = 0;
                y_front = 0;
                x_back = 0;
                y_back = 2523;
                x_sleeve_left = 5796;
                y_sleeve_left = 0;
                x_sleeve_right = 3443;
                y_sleeve_right = 0;
                x_down_front = 1379;
                y_down_front = 4424;
                x_down_back_left = 1116;
                y_down_back_left = 8642;
                x_down_back_right = 5022;
                y_down_back_right = 8642;

                id_front = R.drawable.x4_front_3xl;
                id_back = R.drawable.x4_back_3xl;
                id_down_front = R.drawable.x2_front_down_3xl;
                id_down_back_left = R.drawable.x2_back_downleft_3xl;
                id_sleeve_left = R.drawable.x5_sleeve_left_3xl;
                break;
            case "3XL":
                width_front = 3492;
                height_front = 2482;
                width_back = 3502;
                height_back = 2589;
                width_down_front = 7659;
                height_down_front = 4291;
                width_down_back = 3877;
                height_down_back = 4293;
                width_sleeve = 2334;
                height_sleeve = 4009;

                width_combine = 8626;
                height_combine = 13149;
                x_front = 0;
                y_front = 0;
                x_back = 0;
                y_back = 2595;
                x_sleeve_left = 6016;
                y_sleeve_left = 0;
                x_sleeve_right = 3558;
                y_sleeve_right = 0;
                x_down_front = 968;
                y_down_front = 8859;
                x_down_back_left = 3305;
                y_down_back_left = 4169;
                x_down_back_right = 0;
                y_down_back_right = 5315;

                id_front = R.drawable.x4_front_3xl;
                id_back = R.drawable.x4_back_3xl;
                id_down_front = R.drawable.x2_front_down_3xl;
                id_down_back_left = R.drawable.x2_back_downleft_3xl;
                id_sleeve_left = R.drawable.x5_sleeve_left_3xl;
                break;
            case "4XL":
                width_front = 3680;
                height_front = 2520;
                width_back = 3690;
                height_back = 2627;
                width_down_front = 7937;
                height_down_front = 4364;
                width_down_back = 4016;
                height_down_back = 4366;
                width_sleeve = 2447;
                height_sleeve = 4083;

                width_combine = 8755;
                height_combine = 13387;
                x_front = 0;
                y_front = 0;
                x_back = 0;
                y_back = 2626;
                x_sleeve_left = 6284;
                y_sleeve_left = 0;
                x_sleeve_right = 3728;
                y_sleeve_right = 0;
                x_down_front = 817;
                y_down_front = 9023;
                x_down_back_left = 3680;
                y_down_back_left = 4084;
                x_down_back_right = 0;
                y_down_back_right = 5369;

                id_front = R.drawable.x4_front_3xl;
                id_back = R.drawable.x4_back_3xl;
                id_down_front = R.drawable.x2_front_down_3xl;
                id_down_back_left = R.drawable.x2_back_downleft_3xl;
                id_sleeve_left = R.drawable.x5_sleeve_left_3xl;
                break;

            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }
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
