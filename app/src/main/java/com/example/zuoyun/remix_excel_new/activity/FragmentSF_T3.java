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

public class FragmentSF_T3 extends BaseFragment {
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
        canvas.drawRect(1273, 2408 - 19, 1273 + 400, 2408, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1273, 2408 - 2, paint);
    }

    void drawTextBack(Canvas canvas) {
        canvas.drawRect(1267, 2510 - 19, 1267 + 400, 2510, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1267, 2510 - 2, paint);
    }

    void drawTextDownFront(Canvas canvas) {
        canvas.save();
        canvas.rotate(-3.2f, 4718, 5957);
        canvas.drawRect(4718, 5957 - 19, 4718 + 400, 5957, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 4718, 5957 - 2, paint);
        canvas.restore();
    }

    void drawTextBackLeft(Canvas canvas) {
        canvas.save();
        canvas.rotate(3.1f, 3886, 5937);
        canvas.drawRect(3886, 5937 - 19, 3886 + 400, 5937, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "左后片" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + time, 3886, 5937 - 2, paint);
        canvas.restore();
    }

    void drawTextBackRight(Canvas canvas) {
        canvas.save();
        canvas.rotate(-2.7f, 127, 5966);
        canvas.drawRect(127, 5966 - 19, 127 + 400, 5966, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "右后片" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + time, 127, 5966 - 2, paint);
        canvas.restore();
    }

    void drawTextSleeveLeft(Canvas canvas) {
        canvas.save();
        canvas.rotate(-1.7f, 480, 3608);
        canvas.drawRect(480, 3608 - 19, 480 + 400, 3608, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "左袖" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + time, 480, 3608 - 2, paint);
        canvas.restore();
    }

    void drawTextSleeveRight(Canvas canvas) {
        canvas.save();
        canvas.rotate(-1.7f, 480, 3608);
        canvas.drawRect(480, 3608 - 19, 480 + 400, 3608, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "右袖" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + time, 480, 3608 - 2, paint);
        canvas.restore();
    }


    public void remixx() {
        Bitmap bitmapCombine = Bitmap.createBitmap(width_combine, height_combine, Bitmap.Config.RGB_565);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.get(0).getWidth() == 13000) {
            //down_front
            Bitmap bitmapTemp = Bitmap.createBitmap(8953, 5973, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -514, -6805, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.sf_t3_down_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextDownFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_down_front, height_down_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_down_front, y_down_front, null);

            //down_back
            bitmapTemp = Bitmap.createBitmap(4565, 5977, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -417, -218, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.sf_t3_down_back_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackLeft(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_down_back, height_down_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_down_back_left, y_down_back_left, null);

            bitmapTemp = Bitmap.createBitmap(4565, 5977, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -5000, -230, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -4565, 5977, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextBackRight(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_down_back, height_down_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_down_back_right, y_down_back_right, null);

            //back
            bitmapTemp = Bitmap.createBitmap(2922, 2517, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -9654, -2763, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.sf_t3_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back, y_back, null);

            //front
            bitmapTemp = Bitmap.createBitmap(2925, 2417, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -9654, -218, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.sf_t3_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front, y_front, null);

            //sleeve_left
            bitmapTemp = Bitmap.createBitmap(2155, 3623, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -10041, -9156, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.sf_t3_sleeve_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeveLeft(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_left, y_sleeve_left, null);

            //sleeve_right
            bitmapTemp = Bitmap.createBitmap(2155, 3623, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -10041, -5406, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -2155, 3623, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeveRight(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, x_sleeve_right, y_sleeve_right, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

            if (num == 1) {
                MainActivity.recycleExcelImages();
            }
        }



            try {
//            if (!(orderItems.get(currentID).sizeStr.equals("3XL") || orderItems.get(currentID).sizeStr.equals("4XL"))) {//裁片108内
//                Matrix matrix = new Matrix();
//                matrix.postRotate(90);
//                bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);
//            }

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
        if (MainActivity.instance.tb_auto.isChecked()) {
            remix();
        }
    }

    void setSize(String size) {
        switch (size) {
            case "S":
                width_front = 2693;
                height_front = 2306;
                width_back = 2689;
                height_back = 2406;
                width_down_front = 8604;
                height_down_front = 5707;
                width_down_back = 4391;
                height_down_back = 5711;
                width_sleeve = 2014;
                height_sleeve = 3448;

                width_combine = 8877;
                height_combine = 13419;
                x_front = 0;
                y_front = 0;
                x_back = 6188;
                y_back = 0;
                x_sleeve_left = 6863;
                y_sleeve_left = 6247;
                x_sleeve_right = 0;
                y_sleeve_right = 6247;
                x_down_front = 88;
                y_down_front = 1125;
                x_down_back_left = 0;
                y_down_back_left = 7708;
                x_down_back_right = 4486;
                y_down_back_right = 7708;
                break;
            case "M":
                width_front = 2801;
                height_front = 2361;
                width_back = 2807;
                height_back = 2462;
                width_down_front = 8780;
                height_down_front = 5840;
                width_down_back = 4478;
                height_down_back = 5845;
                width_sleeve = 2084;
                height_sleeve = 3536;

                width_combine = 9041;
                height_combine = 13884;
                x_front = 0;
                y_front = 0;
                x_back = 6234;
                y_back = 0;
                x_sleeve_left = 6957;
                y_sleeve_left = 6503;
                x_sleeve_right = 0;
                y_sleeve_right = 6503;
                x_down_front = 98;
                y_down_front = 1243;
                x_down_back_left = 0;
                y_down_back_left = 8039;
                x_down_back_right = 4563;
                y_down_back_right = 8039;
                break;
            case "L":
                width_front = 2925;
                height_front = 2417;
                width_back = 2893;
                height_back = 2517;
                width_down_front = 8953;
                height_down_front = 5973;
                width_down_back = 4565;
                height_down_back = 5977;
                width_sleeve = 2155;
                height_sleeve = 3623;

                width_combine = 9194;
                height_combine = 14485;
                x_front = 0;
                y_front = 0;
                x_back = 6301;
                y_back = 0;
                x_sleeve_left = 7039;
                y_sleeve_left = 6860;
                x_sleeve_right = 0;
                y_sleeve_right = 6860;
                x_down_front = 103;
                y_down_front = 1427;
                x_down_back_left = 0;
                y_down_back_left = 8508;
                x_down_back_right = 4629;
                y_down_back_right = 8508;
                break;
            case "XL":
                width_front = 3042;
                height_front = 2473;
                width_back = 3039;
                height_back = 2574;
                width_down_front = 9128;
                height_down_front = 6106;
                width_down_back = 4652;
                height_down_back = 6110;
                width_sleeve = 2223;
                height_sleeve = 3710;

                width_combine = 9412;
                height_combine = 14996;
                x_front = 0;
                y_front = 0;
                x_back = 6373;
                y_back = 0;
                x_sleeve_left = 7189;
                y_sleeve_left = 7156;
                x_sleeve_right = 0;
                y_sleeve_right = 7156;
                x_down_front = 88;
                y_down_front = 1578;
                x_down_back_left = 0;
                y_down_back_left = 8885;
                x_down_back_right = 4760;
                y_down_back_right = 8885;
                break;
            case "2XL":
                width_front = 3158;
                height_front = 2529;
                width_back = 3154;
                height_back = 2629;
                width_down_front = 9303;
                height_down_front = 6239;
                width_down_back = 4739;
                height_down_back = 6244;
                width_sleeve = 2293;
                height_sleeve = 3797;

                width_combine = 9583;
                height_combine = 15341;
                x_front = 0;
                y_front = 0;
                x_back = 6429;
                y_back = 0;
                x_sleeve_left = 7290;
                y_sleeve_left = 7336;
                x_sleeve_right = 0;
                y_sleeve_right = 7336;
                x_down_front = 108;
                y_down_front = 1680;
                x_down_back_left = 0;
                y_down_back_left = 9097;
                x_down_back_right = 4844;
                y_down_back_right = 9097;
                break;

            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }
        width_front += 40;
        height_front += 40;
        width_back += 40;
        height_back += 40;
        width_down_front += 40;
        height_down_front += 40;
        width_down_back += 40;
        height_down_back += 40;
        width_sleeve += 40;
        height_sleeve += 40;

        width_combine += 40;
        height_combine += 40;
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
