package com.example.zuoyun.remix_excel_new.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
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
 * Created by zuoyun on 2016/10/6.
 */

public class FragmentMD extends BaseFragment {
    Context context;
    //    String sdCardPath = "/mnt/asec/share";
    String sdCardPath = "/storage/emulated/0/Pictures";
    ArrayList<OrderItem> orderItems;
    int currentID;
    String childPath;

    @BindView(R.id.iv_fg2_leftup)
    ImageView iv_leftup;
    @BindView(R.id.iv_fg2_leftdown)
    ImageView iv_leftdown;
    @BindView(R.id.iv_fg2_rightup)
    ImageView iv_rightup;
    @BindView(R.id.iv_fg2_rightdown)
    ImageView iv_rightdown;
    @BindView(R.id.bt_remix)
    Button bt_remix;
    @BindView(R.id.iv_sample1)
    ImageView iv_sample1;
    @BindView(R.id.iv_sample2)
    ImageView iv_sample2;
    @BindView(R.id.sb1)
    SeekBar sb1;
    @BindView(R.id.sbrotate1)
    SeekBar sbrotate1;
    @BindView(R.id.sb2)
    SeekBar sb2;
    @BindView(R.id.sbrotate2)
    SeekBar sbrotate2;

    int width_side, height_side, width_front, height_front, width_back, height_back;

    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint paint, paintRed, paintBlue, rectPaint;
    String time = MainActivity.instance.orderDate_Print;

    @Override
    public int getLayout() {
        return R.layout.fragment1;
    }

    @Override
    public void initData(View view) {
        context = getContext();
        ButterKnife.bind(this, view);
        orderItems = MainActivity.instance.orderItems;
        currentID = MainActivity.instance.currentID;
        childPath = MainActivity.instance.childPath;

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(28);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(35);
        paintRed.setAntiAlias(true);

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_leftup.setImageDrawable(null);
                    iv_rightup.setImageDrawable(null);
                    Log.e("fragment1", "message0");
                } else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
                    if (!MainActivity.instance.cb_fastmode.isChecked()) {
                        iv_leftup.setImageBitmap(MainActivity.instance.bitmaps.get(0));
                    }
                    checkremix();
                } else if (message == 3) {
                    bt_remix.setClickable(false);
                } else if (message == 10) {
                    remix();
                }
            }
        });

        //******************************************************************************************
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

    void drawTextFront(Canvas canvas, String LR) {
        canvas.drawRect(692, 1077 - 28, 692 + 110, 1077, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + LR + orderItems.get(currentID).color, 692, 1077 - 3, paint);

        canvas.save();
        canvas.rotate(72.4f, 36, 81);
        canvas.drawRect(36, 81 - 28, 36 + 500, 81, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).sku + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 36, 81 - 3, paint);
        canvas.restore();
    }
    void drawTextSide(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(11.1f, 360, 453);
        canvas.drawRect(360, 453 - 28, 360 + 550, 453, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + LR + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + time, 360, 453 - 3, paint);
        canvas.restore();
    }
    void drawTextBack(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(7.5f, 35, 331);
        canvas.drawRect(35, 331 - 28, 35 + 400, 331, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + time, 35, 331 - 3, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-7.2f, 581, 369);
        canvas.drawRect(581, 369 - 28, 581 + 120, 369, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + LR + orderItems.get(currentID).color, 581, 369 - 3, paint);
        canvas.restore();
    }

    public void remixx() {
        setSize(orderItems.get(currentID).size);

        int margin = 100;
        Bitmap bitmapCombine = Bitmap.createBitmap(width_side * 2 + margin, height_front + height_side + margin, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 4100) {
            //left_front
            Bitmap bitmapTemp = Bitmap.createBitmap(1460, 1145, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2333, -1089, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.md_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back, 0, null);

            //right_front
            bitmapTemp = Bitmap.createBitmap(1460, 1145, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -306, -1089, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //left_side
            bitmapTemp = Bitmap.createBitmap(1974, 589, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2076, -500, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.md_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side + margin, height_front + margin, null);

            //right_side
            bitmapTemp = Bitmap.createBitmap(1974, 589, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -49, -500, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.md_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //left_back
            bitmapTemp = Bitmap.createBitmap(911, 395, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2607, -66, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.md_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front, margin, null);

            //right_back
            bitmapTemp = Bitmap.createBitmap(911, 395, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -580, -66, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.md_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front, margin + height_back + margin, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (MainActivity.instance.bitmaps.size() == 6) {
            //left_front
            Bitmap bitmapTemp = Bitmap.createBitmap(1460, 1145, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.md_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back, 0, null);

            //right_front
            bitmapTemp = Bitmap.createBitmap(1460, 1145, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //left_side
            bitmapTemp = Bitmap.createBitmap(1974, 589, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.md_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side + margin, height_front + margin, null);

            //right_side
            bitmapTemp = Bitmap.createBitmap(1974, 589, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.md_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //left_back
            bitmapTemp = Bitmap.createBitmap(911, 395, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.md_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front, margin, null);

            //right_back
            bitmapTemp = Bitmap.createBitmap(911, 395, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.md_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front, margin + height_back + margin, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();
        }

        try {
            String printColor = orderItems.get(currentID).color.equals("黑") ? "B" : "W";
            String nameCombine = orderItems.get(currentID).sku + "_" + orderItems.get(currentID).newCode_short + "_" + orderItems.get(currentID).size + "码" + orderItems.get(currentID).color + "-" + orderItems.get(currentID).order_number + strPlus + ".jpg";

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
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku + orderItems.get(currentID).size + printColor);
            sheet.addCell(label1);
            Number number2 = new Number(2, currentID + 1, orderItems.get(currentID).num);
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
        if (MainActivity.instance.tb_auto.isChecked())
            remix();
    }

    void setSize(int size) {
        switch (size) {
            case 36:
                width_front = 1325;
                height_front = 1007;
                width_side = 1774;
                height_side = 539;
                width_back = 843;
                height_back = 371;
                break;
            case 37:
                width_front = 1353;
                height_front = 1038;
                width_side = 1819;
                height_side = 550;
                width_back = 864;
                height_back = 377;
                break;
            case 38:
                width_front = 1381;
                height_front = 1068;
                width_side = 1865;
                height_side = 563;
                width_back = 885;
                height_back = 383;
                break;
            case 39:
                width_front = 1408;
                height_front = 1097;
                width_side = 1909;
                height_side = 575;
                width_back = 906;
                height_back = 389;
                break;
            case 40:
                width_front = 1436;
                height_front = 1129;
                width_side = 1954;
                height_side = 587;
                width_back = 928;
                height_back = 396;
                break;
            case 41:
                width_front = 1464;
                height_front = 1158;
                width_side = 1999;
                height_side = 600;
                width_back = 949;
                height_back = 403;
                break;
            case 42:
                width_front = 1492;
                height_front = 1188;
                width_side = 2044;
                height_side = 613;
                width_back = 970;
                height_back = 410;
                break;
            case 43:
                width_front = 1520;
                height_front = 1218;
                width_side = 2089;
                height_side = 626;
                width_back = 991;
                height_back = 417;
                break;
            case 44:
                width_front = 1548;
                height_front = 1248;
                width_side = 2134;
                height_side = 639;
                width_back = 1012;
                height_back = 424;
                break;
            case 45:
                width_front = 1576;
                height_front = 1278;
                width_side = 2179;
                height_side = 652;
                width_back = 1033;
                height_back = 431;
                break;
        }
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
                tv_content.setText("单号：" + order_number + "，尺码错误");
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
