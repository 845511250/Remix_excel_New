package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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

    int width_side, width_front, width_tongue, width_back;
    int height_side, height_front, height_tongue, height_back;

    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint rectPaint, paint, paintRed, paintBlue, paintRectBlack;
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

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        paintRectBlack = new Paint();
        paintRectBlack.setColor(0xff000000);
        paintRectBlack.setStyle(Paint.Style.FILL);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(23);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(26);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(26);
        paintBlue.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;


        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                    Log.e("fragment2", "message0");
                } else if (message == MainActivity.LOADED_IMGS) {
                    if (!MainActivity.instance.cb_fastmode.isChecked()) {
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
    }

    public void remix() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (num = orderItems.get(currentID).num; num >= 1; num--) {
                    intPlus = orderItems.get(currentID).num - num + 1;
                    for (int i = 0; i < currentID; i++) {
                        if (orderItems.get(currentID).order_number.equals(orderItems.get(i).order_number)) {
                            intPlus += orderItems.get(i).num;
                            ;
                        }
                    }
                    strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                    remixx();
                }
            }
        }.start();

    }


    void drawTextSideR(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(-3.4f, 217, 634);
        canvas.drawRect(217, 634 - 22, 217 + 500, 634, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).color + " " + orderItems.get(currentID).size + "码" + LR + " " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 217, 634 - 2, paint);
        canvas.restore();
    }
    void drawTextSideL(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(3.4f, 797, 604);
        canvas.drawRect(797, 604 - 22, 797 + 500, 604, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).color + " " + orderItems.get(currentID).size + "码" + LR + " " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 797, 604 - 2, paint);
        canvas.restore();
    }
    void drawTextFront(Canvas canvas, String LR) {
        canvas.drawRect(400, 553 - 22, 400 + 100, 553, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + LR + orderItems.get(currentID).color, 400, 553 - 2, paint);

        canvas.save();
        canvas.rotate(61.1f, 17, 223);
        canvas.drawRect(17, 223 - 22, 17 + 200, 223, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number + " " + time, 17, 223 - 2, paint);
        canvas.restore();
    }
    void drawTextBack1(Canvas canvas, String LR) {

    }


    public void remixx() {
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(2726, 3127, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight() && MainActivity.instance.bitmaps.get(0).getWidth() == 4000) {

            //left_inside
            Bitmap bitmapTemp = Bitmap.createBitmap(1512, 654, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -456, -1411, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.d69_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideR(canvasTemp, "左内");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 1770 - width_side - 4 * (45 - orderItems.get(currentID).size) / 9f, 723 - height_side - 17 * (45 - orderItems.get(currentID).size) / 9f, null);

            //right_outside
            bitmapTemp = Bitmap.createBitmap(1512, 654, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2032, -1411, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideR(canvasTemp, "右外");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 1770 - width_side - 4 * (45 - orderItems.get(currentID).size) / 9f, 1451 - height_side - 17 * (45 - orderItems.get(currentID).size) / 9f, null);

            //right_inside
            bitmapTemp = Bitmap.createBitmap(1512, 654, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2032, -579, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1512, 654, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideL(canvasTemp, "右内");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 116 + 4 * (45 - orderItems.get(currentID).size) / 9f, 2265 - height_side - 17 * (45 - orderItems.get(currentID).size) / 9f, null);

            //left_outside
            bitmapTemp = Bitmap.createBitmap(1512, 654, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -456, -579, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideL(canvasTemp, "左外");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 116 + 4 * (45 - orderItems.get(currentID).size) / 9f, 3085 - height_side - 17 * (45 - orderItems.get(currentID).size) / 9f, null);

            //left_front
            bitmapTemp = Bitmap.createBitmap(881, 598, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -456, -2529, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.d69_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 2219 - width_front / 2, 2, null);

            //right_front
            bitmapTemp = Bitmap.createBitmap(881, 598, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1428, -2529, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 2219 - width_front / 2, 700, null);

            //left_back1
            bitmapTemp = Bitmap.createBitmap(1204, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2339, -2302, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.d69_back1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1(canvasTemp, "左");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 2027 - width_back / 2, 1500 + 45 - orderItems.get(currentID).size, null);

            //right_back1
            bitmapTemp = Bitmap.createBitmap(1204, 454, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2339, -2967, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack1(canvasTemp, "右");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 2027 - width_back / 2, 2314 + 45 - orderItems.get(currentID).size, null);


            //
            bitmapTemp.recycle();
            bitmapDB.recycle();
        }


        try {
            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 150);

            //释放bitmap
            bitmapCombine.recycle();

            String printColor = orderItems.get(currentID).color.equals("白") ? "W" : "B";

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
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor);
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

    void setSize(int size) {
        switch (size) {
            case 36:
                width_side = 1331;
                height_side = 596;
                width_front = 814;
                height_front = 534;
                width_tongue = 543;
                height_tongue = 743;
                width_back = 1059;
                height_back = 411;
                break;
            case 37:
                width_side = 1368;
                height_side = 608;
                width_front = 827;
                height_front = 548;
                width_tongue = 554;
                height_tongue = 762;
                width_back = 1088;
                height_back = 419;
                break;
            case 38:
                width_side = 1404;
                height_side = 621;
                width_front = 841;
                height_front = 561;
                width_tongue = 565;
                height_tongue = 781;
                width_back = 1117;
                height_back = 428;
                break;
            case 39:
                width_side = 1439;
                height_side = 630;
                width_front = 855;
                height_front = 573;
                width_tongue = 576;
                height_tongue = 801;
                width_back = 1146;
                height_back = 437;
                break;
            case 40:
                width_side = 1476;
                height_side = 642;
                width_front = 868;
                height_front = 586;
                width_tongue = 587;
                height_tongue = 820;
                width_back = 1175;
                height_back = 445;
                break;
            case 41:
                width_side = 1512;
                height_side = 654;
                width_front = 881;
                height_front = 598;
                width_tongue = 598;
                height_tongue = 839;
                width_back = 1204;
                height_back = 454;
                break;
            case 42:
                width_side = 1547;
                height_side = 663;
                width_front = 895;
                height_front = 611;
                width_tongue = 609;
                height_tongue = 858;
                width_back = 1233;
                height_back = 462;
                break;
            case 43:
                width_side = 1583;
                height_side = 675;
                width_front = 908;
                height_front = 623;
                width_tongue = 620;
                height_tongue = 878;
                width_back = 1262;
                height_back = 471;
                break;
            case 44:
                width_side = 1619;
                height_side = 686;
                width_front = 922;
                height_front = 636;
                width_tongue = 631;
                height_tongue = 897;
                width_back = 1291;
                height_back = 479;
                break;
            case 45:
                width_side = 1654;
                height_side = 698;
                width_front = 935;
                height_front = 649;
                width_tongue = 642;
                height_tongue = 916;
                width_back = 1319;
                height_back = 488;
                break;
        }
    }

}
