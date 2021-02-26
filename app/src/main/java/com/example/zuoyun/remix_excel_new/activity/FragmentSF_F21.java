package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
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

public class FragmentSF_F21 extends BaseFragment {
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

    int width_front, width_back, width_side, width_side_lace, width_tongue;
    int height_front, height_back, height_side, height_side_lace, height_tongue;

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
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(20);
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

    void drawTextSide(Canvas canvas, String LR) {
        canvas.drawRect(516, 618 - 24, 516 + 400, 618, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).color + "-" + orderItems.get(currentID).size + "码 " + LR + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 516, 618 - 3, paint);
    }
    void drawTextFront(Canvas canvas, String LR) {
        canvas.drawRect(400, 599 - 24, 400 + 90, 599, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + LR + orderItems.get(currentID).color, 400, 599 - 3, paint);

        canvas.save();
        canvas.rotate(64.4f, 10, 180);
        canvas.drawRect(10, 180 - 24, 10 + 250, 180, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number + " " + time, 10, 180 - 3, paint);
        canvas.restore();
    }
    void drawTextBack(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(11.1f, 37, 325);
        canvas.drawRect(37, 325 - 24, 37 + 400, 325, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).color + "-" + orderItems.get(currentID).size + "码" + LR + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 37, 325 - 3, paint);
        canvas.restore();
    }
    void drawTextTongue(Canvas canvas, String LR) {
        canvas.drawRect(176, 708 - 24, 176 + 70, 708, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + LR, 176, 708 - 3, paint);
    }
    void drawTextLace(Canvas canvas, String LR) {
        canvas.drawRect(52, 191 - 19, 52 + 60, 191, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + LR, 52, 191 - 2, paintSmall);
    }


    public void remixx() {
        setSize(orderItems.get(currentID).size);

        int margin = 80;
        Bitmap bitmapCombine = Bitmap.createBitmap(width_side * 2 + width_front + width_back + margin * 3, height_side * 2 + height_side_lace + margin * 2, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight() && MainActivity.instance.bitmaps.get(0).getWidth() == 3374) {

            //side_LR
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 148, 821, 1406, 659);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f21_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //side_RR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1779, 817, 1406, 659);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_side + margin, null);

            //side_LL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 184, 83, 1406, 659);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -1406, 659, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side + margin, 0, null);

            //side_RL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1824, 83, 1406, 659);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side + margin, height_side + margin, null);

            //front_left
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 244, 1687, 871, 631);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f21_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side * 2 + margin * 2, 0, null);

            //front_right
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 244, 2593, 871, 631);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side * 2 + margin * 2, height_front + margin, null);

            //back_left
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2027, 1599, 1036, 428);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f21_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side * 2 + width_front + margin * 3, 0, null);

            //back_right
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2027, 2070, 1036, 428);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side * 2 + width_front + margin * 3, height_back + margin, null);

            //tongue_left
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1398, 1640, 419, 724);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f21_tongue);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side * 2 + width_front + margin * 3, height_side * 2 + height_side_lace + margin * 2 - height_tongue, null);

            //tongue_right
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1398, 2542, 419, 724);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side * 2 + width_front + width_back + margin * 3 - width_tongue, height_side * 2 + height_side_lace + margin * 2 - height_tongue, null);

            //lace_lr
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2686, 2550, 193, 759, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_f21_lace_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLace(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_lace, height_side_lace, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_side * 2 + margin * 2, null);

            //lace_rr
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2032, 2550, 193, 759, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLace(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_lace, height_side_lace, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side_lace * 2 + margin * 2, height_side * 2 + margin * 2, null);

            //lace_ll
            matrix.reset();
            matrix.postRotate(90);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3013, 2550, 193, 759, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -759, 193, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLace(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_lace, height_side_lace, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side_lace + margin, height_side * 2 + margin * 2, null);

            //lace_rl
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2360, 2550, 193, 759, matrix, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLace(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side_lace, height_side_lace, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side_lace * 3 + margin * 3, height_side * 2 + margin * 2, null);


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
            BitmapToJpg.save(bitmapCombine, fileSave, 149);

            //释放bitmap
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
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + orderItems.get(currentID).size);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + orderItems.get(currentID).size);
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
        width_front = 789 + (size - 36) * 17;
        height_front = 563 + (size - 36) * 14;
        width_back = 919 + (size - 36) * 24;
        height_back = 391 + (size - 36) * 8;
        width_side = 1245 + (size - 36) * 32;
        height_side = 598 + (size - 36) * 13;
        width_side_lace = 680 + (size - 36) * 16;
        height_side_lace = 184 + (size - 36) * 3;
        width_tongue = 378 + (size - 36) * 8;
        height_tongue = 642 + (size - 36) * 17;
    }

}
