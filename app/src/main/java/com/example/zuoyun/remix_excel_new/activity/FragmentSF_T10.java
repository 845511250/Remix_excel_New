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

public class FragmentSF_T10 extends BaseFragment {
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

    int width_front, width_back, width_sleeve, width_hood, width_pocket, width_cuff;
    int height_front, height_back, height_sleeve, height_hood, height_pocket, height_cuff;

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
        paint.setTextSize(24);
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
                                ;
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
        canvas.drawRect(500, 8470 - 23, 500 + 500, 8470, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).sku + " " + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 500, 8470 - 2, paint);
    }

    void drawTextBack(Canvas canvas) {
        canvas.drawRect(500, 8460 - 23, 500 + 500, 8460, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).sku + " " + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 500, 8460 - 2, paint);
    }

    void drawTextXiuziL(Canvas canvas) {
        canvas.drawRect(1500, 3352 - 23, 1500 + 500, 3352, rectPaint);
        canvas.drawText(time + "  左袖" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 1500, 3352 - 2, paint);
    }

    void drawTextXiuziR(Canvas canvas) {
        canvas.drawRect(500, 3352 - 23, 500 + 500, 3352, rectPaint);
        canvas.drawText(time + "  右袖" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 500, 3352 - 2, paint);
    }

    void drawTextMaoziOutL(Canvas canvas) {
        canvas.save();
        canvas.rotate(-5.7f, 99, 81);
        canvas.drawRect(99, 81, 99 + 400, 81 + 23, rectPaint);
        canvas.drawText("外左" + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number, 99, 81 + 21, paint);
        canvas.restore();
    }

    void drawTextMaoziInR(Canvas canvas) {
        canvas.save();
        canvas.rotate(-5.7f, 99, 81);
        canvas.drawRect(99, 81, 99 + 400, 81 + 23, rectPaint);
        canvas.drawText("内右" + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number, 99, 81 + 21, paint);
        canvas.restore();
    }

    void drawTextMaoziOutR(Canvas canvas) {
        canvas.save();
        canvas.rotate(5.7f, 1394, 44);
        canvas.drawRect(1394, 44, 1394 + 400, 44 + 23, rectPaint);
        canvas.drawText("外右" + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number, 1394, 44 + 21, paint);
        canvas.restore();
    }

    void drawTextMaoziInL(Canvas canvas) {
        canvas.save();
        canvas.rotate(5.7f, 1394, 44);
        canvas.drawRect(1394, 44, 1394 + 400, 44 + 23, rectPaint);
        canvas.drawText("内左" + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number, 1394, 44 + 21, paint);
        canvas.restore();
    }

    void drawTextXiukouL(Canvas canvas) {
        canvas.drawRect(500, 9, 500 + 400, 9 + 23, rectPaint);
        canvas.drawText("左 " + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number, 500, 9 + 21, paint);
    }

    void drawTextXiukouR(Canvas canvas) {
        canvas.drawRect(500, 9, 500 + 400, 9 + 23, rectPaint);
        canvas.drawText("右 " + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number, 500, 9 + 21, paint);
    }

    void drawTextPocket(Canvas canvas) {
        canvas.drawRect(600, 1550 - 23, 600 + 400, 1550, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number, 600, 1550 - 2, paint);
    }

    public void remixx() {
        int margin = 80;
        int width_combine = width_hood * 2 + width_sleeve * 2 + margin;
        width_combine -= 180;
        int height_combine = height_back + height_sleeve + height_cuff * 2 + margin * 3;
        Matrix matrix = new Matrix();

        Bitmap bitmapCombine = Bitmap.createBitmap(width_combine, height_combine, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {//jj
            if (MainActivity.instance.bitmaps.get(0).getWidth() != 11500) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 11500, 11500, true));
            }

            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 529, 2919, 3848, 8479);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_t10_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4464, 2920, 3848, 8469);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_t10_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin * 2, 0, null);

            //xiuziL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 8398, 7100, 2570, 3361);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_t10_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, width_hood * 2 + margin, height_back + margin, null);

            //xiuziR
            bitmapTemp = Bitmap.createBitmap(2570, 3361, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -8398, -2730, null);
            matrix = new Matrix();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(width_sleeve + width_hood * 2 + width_sleeve + margin - 180, height_sleeve + height_back + margin);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //maomianL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1237, 104, 1863, 2560);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_t10_hood_outside_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //maomianR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3167, 103, 1863, 2560);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix = new Matrix();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziOutR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
            canvasCombine.drawBitmap(bitmapTemp, width_hood + margin, height_front + margin, null);

            //maoliR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6224, 106, 1863, 2560);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_t10_hood_inside_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + height_hood + margin * 2, null);

            //maoliL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 8148, 104, 1863, 2560);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix = new Matrix();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziInL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_hood, height_hood, true);
            canvasCombine.drawBitmap(bitmapTemp, width_hood + margin, height_front + height_hood + margin * 2, null);

            //xiukouL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 8999, 10505, 1366, 884);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_t10_cuff);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
            canvasCombine.drawBitmap(bitmapTemp, width_hood * 2 + width_pocket + margin * 4, height_back + height_sleeve + margin * 2, null);

            //xiukouR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 8999, 6126, 1366, 884);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_t10_cuff);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_cuff, height_cuff, true);
            canvasCombine.drawBitmap(bitmapTemp, width_hood * 2 + width_pocket + margin * 4, height_back + height_sleeve + height_cuff + margin * 3, null);

            //pocket
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1123, 5499, 2641, 1560);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.sf_t10_pocket);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocket(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_hood * 2 + margin * 2, height_back + height_sleeve + margin * 2, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        }

        try {
//            matrix.reset();
//            matrix.postRotate(-90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

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
        if (MainActivity.instance.tb_auto.isChecked())
            remix();
    }

    void setSize(String size) {
        switch (size) {
            case "S":
                width_front = 3615;
                height_front = 8130;
                width_back = 3615;
                height_back = 8120;
                width_sleeve = 2430;
                height_sleeve = 3186;
                width_hood = 1793;
                height_hood = 2502;
                width_pocket = 2524;
                height_pocket = 1502;
                width_cuff = 1309;
                height_cuff = 884;
                break;
            case "M":
                width_front = 3731;
                height_front = 8305;
                width_back = 3731;
                height_back = 8295;
                width_sleeve = 2501;
                height_sleeve = 3273;
                width_hood = 1829;
                height_hood = 2532;
                width_pocket = 2582;
                height_pocket = 1531;
                width_cuff = 1337;
                height_cuff = 885;
                break;
            case "L":
                width_front = 3848;
                height_front = 8479;
                width_back = 3848;
                height_back = 8469;
                width_sleeve = 2571;
                height_sleeve = 3361;
                width_hood = 1863;
                height_hood = 2561;
                width_pocket = 2641;
                height_pocket = 1560;
                width_cuff = 1366;
                height_cuff = 884;
                break;
            case "XL":
                width_front = 3964;
                height_front = 8653;
                width_back = 3964;
                height_back = 8643;
                width_sleeve = 2641;
                height_sleeve = 3448;
                width_hood = 1898;
                height_hood = 2590;
                width_pocket = 2699;
                height_pocket = 1589;
                width_cuff = 1396;
                height_cuff = 885;
                break;
            case "2XL":
                width_front = 4080;
                height_front = 8828;
                width_back = 4080;
                height_back = 8817;
                width_sleeve = 2711;
                height_sleeve = 3534;
                width_hood = 1933;
                height_hood = 2679;
                width_pocket = 2757;
                height_pocket = 1619;
                width_cuff = 1425;
                height_cuff = 884;
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }
        width_front += 50;
        height_front += 50;
        width_back += 50;
        height_back += 50;
        width_sleeve += 40;
        height_sleeve += 40;
        width_hood += 40;
        height_hood += 40;
        width_pocket += 50;
        height_pocket += 50;
        width_cuff += 30;
        height_cuff += 30;
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
