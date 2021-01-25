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

public class FragmentD18 extends BaseFragment {
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

    int width_outside, height_outside, width_inside, height_inside;

    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint rectPaint, paint, paintSmall, paintBlue, paintRectBlack;
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
        paint.setTextSize(24);
        paint.setAntiAlias(true);

        paintSmall = new Paint();
        paintSmall.setColor(0xffff0000);
        paintSmall.setTextSize(14);
        paintSmall.setAntiAlias(true);

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
                        }
                    }
                    strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                    remixx();
                }
            }
        }.start();

    }

    void drawTextLL(Canvas canvas, String LR) {
        canvas.drawRect(792, 683 - 25, 792 + 500, 683, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).size + "码" + orderItems.get(currentID).color + " " + LR + " " + orderItems.get(currentID).newCode_short + " " + orderItems.get(currentID).order_number + " " + time, 792, 683 - 3, paint);
    }
    void drawTextLR(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(2.3f, 217, 678);
        canvas.drawRect(217, 678 - 25, 217 + 500, 678, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).size + "码" + orderItems.get(currentID).color + " " + LR + " " + orderItems.get(currentID).newCode_short + " " + orderItems.get(currentID).order_number + " " + time, 217, 678 - 3, paint);
        canvas.restore();
    }
    void drawTextRL(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(-2.3f, 683, 699);
        canvas.drawRect(683, 699 - 25, 683 + 500, 699, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).size + "码" + orderItems.get(currentID).color + " " + LR + " " + orderItems.get(currentID).newCode_short + " " + orderItems.get(currentID).order_number + " " + time, 683, 699 - 3, paint);
        canvas.restore();
    }
    void drawTextRR(Canvas canvas, String LR) {
        canvas.drawRect(170, 683 - 25, 170 + 500, 683, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).size + "码" + orderItems.get(currentID).color + " " + LR + " " + orderItems.get(currentID).newCode_short + " " + orderItems.get(currentID).order_number + " " + time, 170, 683 - 3, paint);
    }

    public void remixx() {
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(4327, 2120, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 2983) {
            //LL
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 82, 507, 1410, 711);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.d18_ll);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLL(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside, height_outside, true);
            canvasCombine.drawBitmap(bitmapTemp, 2106 - width_outside, 1004 - height_outside - 10 - 5 * (35 - orderItems.get(currentID).size) / 7f, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1498, 1752, 1412, 726);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.d18_lr);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLR(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside, height_inside, true);
            canvasCombine.drawBitmap(bitmapTemp, 2225, 2076 - height_inside - 21 - 7 * (35 - orderItems.get(currentID).size) / 7f, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 71, 1752, 1412, 726);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.d18_rl);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRL(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_inside, height_inside, true);
            canvasCombine.drawBitmap(bitmapTemp, 2106 - width_inside, 2076 - height_inside - 21 - 7 * (35 - orderItems.get(currentID).size) / 7f, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1500, 507, 1410, 711);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.d18_rr);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextRR(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_outside, height_outside, true);
            canvasCombine.drawBitmap(bitmapTemp, 2225, 1004 - height_outside - 10 - 5 * (35 - orderItems.get(currentID).size) / 7f, null);

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

            String colorStr = orderItems.get(currentID).color.equals("黑") ? "B" : "W";

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
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + orderItems.get(currentID).size + colorStr);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + orderItems.get(currentID).size + colorStr);
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
            case 28:
                width_outside = 1233;
                height_outside = 651;
                width_inside = 1247;
                height_inside = 662;
                break;
            case 29:
                width_outside = 1277;
                height_outside = 666;
                width_inside = 1292;
                height_inside = 678;
                break;
            case 30:
                width_outside = 1321;
                height_outside = 681;
                width_inside = 1337;
                height_inside = 693;
                break;
            case 31:
                width_outside = 1366;
                height_outside = 696;
                width_inside = 1382;
                height_inside = 709;
                break;
            case 32:
                width_outside = 1410;
                height_outside = 711;
                width_inside = 1412;
                height_inside = 726;
                break;
            case 33:
                width_outside = 1455;
                height_outside = 726;
                width_inside = 1473;
                height_inside = 740;
                break;
            case 34:
                width_outside = 1499;
                height_outside = 741;
                width_inside = 1518;
                height_inside = 755;
                break;
            case 35:
                width_outside = 1544;
                height_outside = 756;
                width_inside = 1564;
                height_inside = 769;
                break;

        }
        width_outside += 10;
        height_outside += 10;
        width_inside += 10;
        height_inside += 10;
    }
}
