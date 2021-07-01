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

public class FragmentBV extends BaseFragment {
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

    int width_front, width_back, width_xiuzi;
    int height_front, height_back, height_xiuzi;
    int id_front, id_back, id_sleeve_l, id_sleeve_r;


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
        paint.setTextSize(20);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(20);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(20);
        paintBlue.setAntiAlias(true);

        paintSmall = new Paint();
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(20);
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

//    void drawTextFront(Canvas canvas) {
//        canvas.drawRect(200, 3408 - 20, 200 + 500, 3408, rectPaint);
//        canvas.drawText("BV女T恤- " + orderItems.get(currentID).sizeStr + "  " + time + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode_short, 200, 3408 - 2, paint);
//    }
//    void drawTextBack(Canvas canvas) {
//        canvas.drawRect(1070, 3863 - 20, 1070 + 500, 3863, rectPaint);
//        canvas.drawText("BV女T恤- " + orderItems.get(currentID).sizeStr + "  " + time + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode_short, 1070, 3863 - 2, paint);
//    }

    void drawTextXiuziL(Canvas canvas) {
        canvas.drawRect(500, 1735 - 20, 500 + 500, 1735, rectPaint);
        canvas.drawText("左袖子" + orderItems.get(currentID).sizeStr + "  " + time + "  " + orderItems.get(currentID).order_number, 500, 1735 - 2, paint);
    }
    void drawTextXiuziR(Canvas canvas) {
        canvas.drawRect(500, 1735 - 20, 500 + 500, 1735, rectPaint);
        canvas.drawText("右袖子" + orderItems.get(currentID).sizeStr + "  " + time + "  " + orderItems.get(currentID).order_number, 500, 1735 - 2, paint);
    }
    void drawTextFront(Canvas canvas) {
        if (id_front == R.drawable.bv_front_s) {
            canvas.save();
            canvas.rotate(72f, 49, 1089);
            canvas.drawRect(49, 1089 - 20, 49 + 500, 1089, rectPaint);
            canvas.drawText("BV女T恤-前 " + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 49, 1089 - 2, paint);
            canvas.restore();
        } else if (id_front == R.drawable.bv_front_xl) {
            canvas.save();
            canvas.rotate(78.3f, 7, 993);
            canvas.drawRect(7, 993 - 20, 7 + 500, 993, rectPaint);
            canvas.drawText("BV女T恤-前 " + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 7, 993 - 2, paint);
            canvas.restore();
        } else if (id_front == R.drawable.bv_front_3xl) {
            canvas.save();
            canvas.rotate(80.5f, 11, 1026);
            canvas.drawRect(11, 1026 - 20, 11 + 500, 1026, rectPaint);
            canvas.drawText("BV女T恤-前 " + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 11, 1026 - 2, paint);
            canvas.restore();
        }
    }
    void drawTextBack(Canvas canvas) {
        if (id_back == R.drawable.bv_back_s) {
            canvas.save();
            canvas.rotate(72.3f, 62, 1390);
            canvas.drawRect(62, 1390 - 20, 62 + 500, 1390, rectPaint);
            canvas.drawText("BV女T恤-后 " + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 62, 1390 - 2, paint);
            canvas.restore();
        } else if (id_back == R.drawable.bv_back_xl) {
            canvas.save();
            canvas.rotate(78.3f, 14, 1283);
            canvas.drawRect(14, 1283 - 20, 14 + 500, 1283, rectPaint);
            canvas.drawText("BV女T恤-后 " + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 14, 1283 - 2, paint);
            canvas.restore();
        } else if (id_back == R.drawable.bv_back_3xl) {
            canvas.save();
            canvas.rotate(80f, 10, 1330);
            canvas.drawRect(10, 1330 - 20, 10 + 500, 1330, rectPaint);
            canvas.drawText("BV女T恤-后 " + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 10, 1330 - 2, paint);
            canvas.restore();
        }
    }

    public void remixx() {
        Bitmap bitmapCombine = Bitmap.createBitmap(width_front + width_back + width_xiuzi + 180, Math.max(height_back, height_xiuzi * 2 + 120), Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 4) {
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(2707, 3412, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(2707, 3870, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + 60, 0, null);

            //左袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 57, 21, 1841, 1719);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 120, 0, null);

            //右袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 27, 21, 1841, 1719);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 120, height_xiuzi + 120, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
            if (MainActivity.instance.bitmaps.get(0).getWidth() == 4000) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 5966, 5966, true));
            }
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 187, 2406, 2707, 3412);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3073, 1950, 2707, 3869);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + 60, 0, null);

            //左袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3462 + 57, 150 + 21, 1841, 1719);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1925, 1743, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 120, 0, null);

            //右袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 583 + 27, 150 + 21, 1841, 1719);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1925, 1743, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 120, height_xiuzi + 120, null);
            bitmapTemp.recycle();

        } else if (orderItems.get(currentID).isPPSL) {
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1946, 322, 2707, 3412);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1946, 15, 2707, 3870);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + 60, 0, null);

            //左袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4653, 15, 1925, 1743);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 120, 0, null);

            //右袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 21, 15, 1925, 1743);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 120, height_xiuzi + 120, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {

            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1925, 307, 2707, 3412);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1925, 0, 2707, 3869);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + 60, 0, null);

            //左袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4631 + 57, 0 + 21, 1841, 1719);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 120, 0, null);

            //右袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0 + 27, 0 + 21, 1841, 1719);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_sleeve_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + 120, height_xiuzi + 120, null);
            bitmapTemp.recycle();
        }


        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

            String nameCombine = "女T恤" + orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 148);
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
            case "XS":
                width_front = 2540;
                height_front = 3277;
                width_back = 2542;
                height_back = 3713;
                width_xiuzi = 1814;
                height_xiuzi = 1711;
                id_front = R.drawable.bv_front_s;
                id_back = R.drawable.bv_back_s;
                id_sleeve_l = R.drawable.bv_sleeve_l_s;
                id_sleeve_r = R.drawable.bv_sleeve_r_s;
                break;
            case "S":
                width_front = 2658;
                height_front = 3345;
                width_back = 2660;
                height_back = 3771;
                width_xiuzi = 1910;
                height_xiuzi = 1799;
                id_front = R.drawable.bv_front_s;
                id_back = R.drawable.bv_back_s;
                id_sleeve_l = R.drawable.bv_sleeve_l_s;
                id_sleeve_r = R.drawable.bv_sleeve_r_s;
                break;
            case "M":
                width_front = 2776;
                height_front = 3414;
                width_back = 2778;
                height_back = 3830;
                width_xiuzi = 2005;
                height_xiuzi = 1888;
                id_front = R.drawable.bv_front_s;
                id_back = R.drawable.bv_back_s;
                id_sleeve_l = R.drawable.bv_sleeve_l_s;
                id_sleeve_r = R.drawable.bv_sleeve_r_s;
                break;
            case "L":
                width_front = 3012;
                height_front = 3586;
                width_back = 3015;
                height_back = 4010;
                width_xiuzi = 2194;
                height_xiuzi = 2006;
                id_front = R.drawable.bv_front_xl;
                id_back = R.drawable.bv_back_xl;
                id_sleeve_l = R.drawable.bv_sleeve_l_xl;
                id_sleeve_r = R.drawable.bv_sleeve_r_xl;
                break;
            case "XL":
                width_front = 3249;
                height_front = 3727;
                width_back = 3251;
                height_back = 4186;
                width_xiuzi = 2384;
                height_xiuzi = 2124;
                id_front = R.drawable.bv_front_xl;
                id_back = R.drawable.bv_back_xl;
                id_sleeve_l = R.drawable.bv_sleeve_l_xl;
                id_sleeve_r = R.drawable.bv_sleeve_r_xl;
                break;
            case "2XL":
                width_front = 3486;
                height_front = 3865;
                width_back = 3488;
                height_back = 4363;
                width_xiuzi = 2573;
                height_xiuzi = 2242;
                id_front = R.drawable.bv_front_xl;
                id_back = R.drawable.bv_back_xl;
                id_sleeve_l = R.drawable.bv_sleeve_l_xl;
                id_sleeve_r = R.drawable.bv_sleeve_r_xl;
                break;
            case "3XL":
                width_front = 3722;
                height_front = 4004;
                width_back = 3724;
                height_back = 4542;
                width_xiuzi = 2762;
                height_xiuzi = 2361;
                id_front = R.drawable.bv_front_3xl;
                id_back = R.drawable.bv_back_3xl;
                id_sleeve_l = R.drawable.bv_sleeve_l_3xl;
                id_sleeve_r = R.drawable.bv_sleeve_r_3xl;
                break;
            case "4XL":
                width_front = 3959;
                height_front = 4143;
                width_back = 3960;
                height_back = 4719;
                width_xiuzi = 2951;
                height_xiuzi = 2479;
                id_front = R.drawable.bv_front_3xl;
                id_back = R.drawable.bv_back_3xl;
                id_sleeve_l = R.drawable.bv_sleeve_l_3xl;
                id_sleeve_r = R.drawable.bv_sleeve_r_3xl;
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

}
