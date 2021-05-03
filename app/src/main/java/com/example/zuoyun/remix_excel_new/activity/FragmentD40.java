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

public class FragmentD40 extends BaseFragment {
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

    int width_front, width_back, width_sleeve, width_collar;
    int height_front, height_back, height_sleeve, height_collar;

    int width_combine, height_combine;
    int x_front, x_back, x_sleeve_left, x_sleeve_right, x_collar;
    int y_front, y_back, y_sleeve_left, y_sleeve_right, y_collar;
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
        canvas.drawRect(1786, 5840 - 19, 1786 + 400, 5840, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1786, 5840 - 2, paint);
    }
    void drawTextFront(Canvas canvas) {
        canvas.drawRect(1786, 5758 - 19, 1786 + 400, 5758, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1786, 5758 - 2, paint);
    }
    void drawTextSleeve(Canvas canvas, String LR) {
        canvas.drawRect(637, 2990 - 19, 637 + 400, 2990, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + LR + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 637, 2990 - 2, paint);
    }

    public void remixx() {
        Bitmap bitmapCombine = Bitmap.createBitmap(width_combine, height_combine, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 5) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(2489, 5774, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front, y_front, null);

            //back
            bitmapTemp = Bitmap.createBitmap(2490, 5856, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);

            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(x_back, y_back);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //sleeve_left
            bitmapTemp = Bitmap.createBitmap(1695, 3007, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "左袖");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_left, y_sleeve_left, null);

            //sleeve_right
            bitmapTemp = Bitmap.createBitmap(1695, 3007, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1695, 3007, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "右袖");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);

            matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(x_sleeve_right, y_sleeve_right);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //collar
            bitmapTemp = Bitmap.createBitmap(2321, 250, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.d40_collar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextRight(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);
            canvasCombine.drawBitmap(bitmapTemp, x_collar, y_collar, null);


            bitmapTemp.recycle();
            bitmapDB.recycle();
            if (num == 1) {
                MainActivity.recycleExcelImages();
            }

        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(2489, 5774, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1768, -346, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front, y_front, null);

            //back
            bitmapTemp = Bitmap.createBitmap(2490, 5856, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4341, -264, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);

            Matrix matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(x_back, y_back);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //sleeve_left
            bitmapTemp = Bitmap.createBitmap(1695, 3007, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -6831, -3007, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "左袖");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_left, y_sleeve_left, null);

            //sleeve_right
            bitmapTemp = Bitmap.createBitmap(1695, 3007, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1695, -3007, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1695, 3007, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "右袖");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);

            matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(x_sleeve_right, y_sleeve_right);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //collar
            bitmapTemp = Bitmap.createBitmap(2321, 250, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1852, -79, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.d40_collar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextRight(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);
            canvasCombine.drawBitmap(bitmapTemp, x_collar, y_collar, null);


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
                width_front = 2205;
                height_front = 5632;
                width_back = 2205;
                height_back = 5714;
                width_sleeve = 1409;
                height_sleeve = 2865;
                width_collar = 2122;
                height_collar = 250;

                width_combine = 5819;
                height_combine = 5952;
                x_front = 0;
                y_front = 320;
                x_back = 5819;
                y_back = 5952;
                x_sleeve_left = 2205;
                y_sleeve_left = 0;
                x_sleeve_right = 3616;
                y_sleeve_right = 5952;
                x_collar = 80;
                y_collar = 0;

                id_front = R.drawable.d40_front_m;
                id_back = R.drawable.d40_back_m;
                id_sleeve_left = R.drawable.d40_sleeve_left_m;
                break;
            case "M":
                width_front = 2299;
                height_front = 5679;
                width_back = 2299;
                height_back = 5761;
                width_sleeve = 1503;
                height_sleeve = 2910;
                width_collar = 2188;
                height_collar = 250;

                width_combine = 6121;
                height_combine = 5959;
                x_front = 0;
                y_front = 280;
                x_back = 6121;
                y_back = 5959;
                x_sleeve_left = 2299;
                y_sleeve_left = 0;
                x_sleeve_right = 3822;
                y_sleeve_right = 5959;
                x_collar = 96;
                y_collar = 0;

                id_front = R.drawable.d40_front_m;
                id_back = R.drawable.d40_back_m;
                id_sleeve_left = R.drawable.d40_sleeve_left_m;
                break;
            case "L":
                width_front = 2394;
                height_front = 5727;
                width_back = 2394;
                height_back = 5807;
                width_sleeve = 1599;
                height_sleeve = 2958;
                width_collar = 2254;
                height_collar = 250;

                width_combine = 6387;
                height_combine = 6029;
                x_front = 0;
                y_front = 302;
                x_back = 6387;
                y_back = 6029;
                x_sleeve_left = 2394;
                y_sleeve_left = 0;
                x_sleeve_right = 3993;
                y_sleeve_right = 6029;
                x_collar = 90;
                y_collar = 0;

                id_front = R.drawable.d40_front_xl;
                id_back = R.drawable.d40_back_xl;
                id_sleeve_left = R.drawable.d40_sleeve_left_xl;
                break;
            case "XL":
                width_front = 2489;
                height_front = 5774;
                width_back = 2490;
                height_back = 5856;
                width_sleeve = 1695;
                height_sleeve = 3005;
                width_collar = 2321;
                height_collar = 250;

                width_combine = 6677;
                height_combine = 6071;
                x_front = 0;
                y_front = 297;
                x_back = 6677;
                y_back = 6071;
                x_sleeve_left = 2492;
                y_sleeve_left = 0;
                x_sleeve_right = 4184;
                y_sleeve_right = 6071;
                x_collar = 108;
                y_collar = 0;

                id_front = R.drawable.d40_front_xl;
                id_back = R.drawable.d40_back_xl;
                id_sleeve_left = R.drawable.d40_sleeve_left_xl;
                break;
            case "2XL":
                width_front = 2584;
                height_front = 5822;
                width_back = 2584;
                height_back = 5903;
                width_sleeve = 1790;
                height_sleeve = 3053;
                width_collar = 2387;
                height_collar = 250;

                width_combine = 6967;
                height_combine = 6167;
                x_front = 0;
                y_front = 345;
                x_back = 6967;
                y_back = 6167;
                x_sleeve_left = 2584;
                y_sleeve_left = 0;
                x_sleeve_right = 4374;
                y_sleeve_right = 6167;
                x_collar = 100;
                y_collar = 0;

                id_front = R.drawable.d40_front_3xl;
                id_back = R.drawable.d40_back_3xl;
                id_sleeve_left = R.drawable.d40_sleeve_left_3xl;
                break;
            case "3XL":
                width_front = 2679;
                height_front = 5869;
                width_back = 2679;
                height_back = 5951;
                width_sleeve = 1885;
                height_sleeve = 3100;
                width_collar = 2452;
                height_collar = 250;

                width_combine = 7090;
                height_combine = 6697;
                x_front = 0;
                y_front = 396;
                x_back = 7090;
                y_back = 6235;
                x_sleeve_left = 2554;
                y_sleeve_left = 0;
                x_sleeve_right = 4543;
                y_sleeve_right = 6697;
                x_collar = 206;
                y_collar = 34;

                id_front = R.drawable.d40_front_3xl;
                id_back = R.drawable.d40_back_3xl;
                id_sleeve_left = R.drawable.d40_sleeve_left_3xl;
                break;
            case "4XL":
                width_front = 2774;
                height_front = 5917;
                width_back = 2774;
                height_back = 5996;
                width_sleeve = 1980;
                height_sleeve = 3146;
                width_collar = 2517;
                height_collar = 250;

                width_combine = 6665;
                height_combine = 8862;
                x_front = 0;
                y_front = 2945;
                x_back = 6665;
                y_back = 8862;
                x_sleeve_left = 2215;
                y_sleeve_left = 0;
                x_sleeve_right = 2377;
                y_sleeve_right = 3147;
                x_collar = 4148;
                y_collar = 2455;

                id_front = R.drawable.d40_front_3xl;
                id_back = R.drawable.d40_back_3xl;
                id_sleeve_left = R.drawable.d40_sleeve_left_3xl;
                break;
            case "5XL":
                width_front = 2869;
                height_front = 5964;
                width_back = 2869;
                height_back = 6044;
                width_sleeve = 2075;
                height_sleeve = 3194;
                width_collar = 2585;
                height_collar = 251;

                width_combine = 7033;
                height_combine = 8997;
                x_front = 0;
                y_front = 3033;
                x_back = 7033;
                y_back = 8997;
                x_sleeve_left = 2355;
                y_sleeve_left = 0;
                x_sleeve_right = 2472;
                y_sleeve_right = 3194;
                x_collar = 4306;
                y_collar = 2615;

                id_front = R.drawable.d40_front_6xl;
                id_back = R.drawable.d40_back_6xl;
                id_sleeve_left = R.drawable.d40_sleeve_left_6xl;
                break;
            case "6XL":
                width_front = 2963;
                height_front = 6010;
                width_back = 2963;
                height_back = 6090;
                width_sleeve = 2169;
                height_sleeve = 3242;
                width_collar = 2650;
                height_collar = 250;

                width_combine = 7179;
                height_combine = 9116;
                x_front = 0;
                y_front = 3106;
                x_back = 7179;
                y_back = 9116;
                x_sleeve_left = 2398;
                y_sleeve_left = 0;
                x_sleeve_right = 2566;
                y_sleeve_right = 3242;
                x_collar = 4372;
                y_collar = 2682;

                id_front = R.drawable.d40_front_6xl;
                id_back = R.drawable.d40_back_6xl;
                id_sleeve_left = R.drawable.d40_sleeve_left_6xl;
                break;
            case "7XL":
                width_front = 3058;
                height_front = 6057;
                width_back = 3058;
                height_back = 6139;
                width_sleeve = 2265;
                height_sleeve = 3289;
                width_collar = 2717;
                height_collar = 250;

                width_combine = 6283;
                height_combine = 9568;
                x_front = 0;
                y_front = 3113;
                x_back = 6283;
                y_back = 9568;
                x_sleeve_left = 2509;
                y_sleeve_left = 0;
                x_sleeve_right = 2649;
                y_sleeve_right = 3289;
                x_collar = 158;
                y_collar = 9279;

                id_front = R.drawable.d40_front_6xl;
                id_back = R.drawable.d40_back_6xl;
                id_sleeve_left = R.drawable.d40_sleeve_left_6xl;
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
