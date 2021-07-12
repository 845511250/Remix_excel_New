package com.example.zuoyun.remix_excel_new.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
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

public class FragmentD63 extends BaseFragment {
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

    int width_front, height_front, width_collar, height_collar;

    int width_combine, height_combine;
    int x_front, x_back, x_collar_front, x_collar_back;
    int y_front, y_back, y_collar_front, y_collar_back;
    int id_front, id_collar;
    boolean needRotate_Back;

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
        paint.setTextSize(18);
        paint.setAntiAlias(true);

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
        canvas.save();
        canvas.rotate(1.7f, 103, 5512);
        canvas.drawRect(103, 5512 - 18, 103 + 500, 5512, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "前片 " + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 103, 5512 - 2, paint);
        canvas.restore();
    }
    void drawTextBack(Canvas canvas) {
        canvas.save();
        canvas.rotate(1.7f, 103, 5512);
        canvas.drawRect(103, 5512 - 18, 103 + 500, 5512, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "后片 " + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 103, 5512 - 2, paint);
        canvas.restore();
    }
    void drawTextCollar(Canvas canvas, String LR) {
        if (id_collar == R.drawable.d63_collar_m) {
            canvas.save();
            canvas.rotate(3.2f, 1690, 342);
            canvas.drawRect(1690, 342, 1690 + 160, 342 + 18, rectPaint);
            canvas.drawText(orderItems.get(currentID).sku + LR + " " + orderItems.get(currentID).sizeStr, 1690, 342 + 16, paint);
            canvas.restore();

            canvas.save();
            canvas.rotate(-4.7f, 1897, 353);
            canvas.drawRect(1897, 353, 1897 + 230, 353 + 18, rectPaint);
            canvas.drawText(orderItems.get(currentID).order_number, 1897, 353 + 16, paint);
            canvas.restore();
        } else if (id_collar == R.drawable.d63_collar_2xl) {
            canvas.save();
            canvas.rotate(2.8f, 1684, 363);
            canvas.drawRect(1684, 363, 1684 + 160, 363 + 18, rectPaint);
            canvas.drawText(orderItems.get(currentID).sku + LR + " " + orderItems.get(currentID).sizeStr, 1684, 363 + 16, paint);
            canvas.restore();

            canvas.save();
            canvas.rotate(-4f, 1895, 373);
            canvas.drawRect(1895, 373, 1895 + 230, 373 + 18, rectPaint);
            canvas.drawText(orderItems.get(currentID).order_number, 1895, 373 + 16, paint);
            canvas.restore();
        } else if (id_collar == R.drawable.d63_collar_5xl) {
            canvas.save();
            canvas.rotate(1.8f, 1680, 384);
            canvas.drawRect(1680, 384, 1680 + 160, 384 + 18, rectPaint);
            canvas.drawText(orderItems.get(currentID).sku + LR + " " + orderItems.get(currentID).sizeStr, 1680, 384 + 16, paint);
            canvas.restore();

            canvas.save();
            canvas.rotate(-3.1f, 1906, 391);
            canvas.drawRect(1906, 391, 1906 + 230, 391 + 18, rectPaint);
            canvas.drawText(orderItems.get(currentID).order_number, 1906, 391 + 16, paint);
            canvas.restore();
        }

    }

    public void remixx() {
        Bitmap bitmapCombine = Bitmap.createBitmap(width_combine, height_combine, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 4) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(2907, 5549, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front, y_front, null);

            //back
            bitmapTemp = Bitmap.createBitmap(2907, 5549, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);

            Matrix matrix = new Matrix();
            if(needRotate_Back)
                matrix.postRotate(180);
            matrix.postTranslate(x_back, y_back);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //collar_front
            bitmapTemp = Bitmap.createBitmap(3734, 1203, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_collar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextCollar(canvasTemp, "前片");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);

            matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(x_collar_front, y_collar_front);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //collar_back
            bitmapTemp = Bitmap.createBitmap(3734, 1203, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextCollar(canvasTemp, "后片");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);
            canvasCombine.drawBitmap(bitmapTemp, x_collar_back, y_collar_back, null);


            bitmapTemp.recycle();
            bitmapDB.recycle();
            if (num == 1) {
                MainActivity.recycleExcelImages();
            }

        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(2907, 5549, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -568, -1497, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), id_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, x_front, y_front, null);

            //back
            bitmapTemp = Bitmap.createBitmap(2907, 5549, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4425, -1497, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);

            Matrix matrix = new Matrix();
            if(needRotate_Back)
                matrix.postRotate(180);
            matrix.postTranslate(x_back, y_back);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //collar_front
            bitmapTemp = Bitmap.createBitmap(3734, 1203, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -154, -153, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), id_collar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextCollar(canvasTemp, "前片");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);

            matrix = new Matrix();
            matrix.postRotate(180);
            matrix.postTranslate(x_collar_front, y_collar_front);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //collar_back
            bitmapTemp = Bitmap.createBitmap(3734, 1203, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4011, -153, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextCollar(canvasTemp, "后片");
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);
            canvasCombine.drawBitmap(bitmapTemp, x_collar_back, y_collar_back, null);


            bitmapTemp.recycle();
            bitmapDB.recycle();
            if (num == 1) {
                MainActivity.recycleExcelImages();
            }
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
            Log.e("aaa", pathSave + nameCombine);
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 100);



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
                width_front = 2513;
                height_front = 5351;
                width_collar = 3142;
                height_collar = 1157;

                width_combine = 5972;
                height_combine = 6951;
                x_front = 0;
                y_front = 1600;
                x_back = 2623;
                y_back = 1600;
                x_collar_front = 3142;
                y_collar_front = 1157;
                x_collar_back = 2830;
                y_collar_back = 367;
                id_front = R.drawable.d63_front_m;
                id_collar = R.drawable.d63_collar_m;
                needRotate_Back = false;
                break;
            case "M":
                width_front = 2591;
                height_front = 5390;
                width_collar = 3261;
                height_collar = 1166;

                width_combine = 5984;
                height_combine = 7334;
                x_front = 0;
                y_front = 1944;
                x_back = 2686;
                y_back = 1944;
                x_collar_front = width_collar;
                y_collar_front = height_collar;
                x_collar_back = 2722;
                y_collar_back = 715;
                id_front = R.drawable.d63_front_m;
                id_collar = R.drawable.d63_collar_m;
                needRotate_Back = false;
                break;
            case "L":
                width_front = 2670;
                height_front = 5430;
                width_collar = 3379;
                height_collar = 1175;

                width_combine = 5430;
                height_combine = 7578;
                x_front = 0;
                y_front = 2148;
                x_back = 2760;
                y_back = 2148;
                x_collar_front = width_collar;
                y_collar_front = height_collar;
                x_collar_back = 1239;
                y_collar_back = 899;
                id_front = R.drawable.d63_front_m;
                id_collar = R.drawable.d63_collar_m;
                needRotate_Back = false;
                break;
            case "XL":
                width_front = 2749;
                height_front = 5469;
                width_collar = 3498;
                height_collar = 1184;

                width_combine = 5580;
                height_combine = 7638;
                x_front = 0;
                y_front = 2169;
                x_back = 2831;
                y_back = 2169;
                x_collar_front = width_collar;
                y_collar_front = height_collar;
                x_collar_back = 1290;
                y_collar_back = 902;
                id_front = R.drawable.d63_front_2xl;
                id_collar = R.drawable.d63_collar_2xl;
                needRotate_Back = false;
                break;
            case "2XL":
                width_front = 2828;
                height_front = 5508;
                width_collar = 3616;
                height_collar = 1194;

                width_combine = 5730;
                height_combine = 7683;
                x_front = 0;
                y_front = 2175;
                x_back = 2901;
                y_back = 2175;
                x_collar_front = width_collar;
                y_collar_front = height_collar;
                x_collar_back = 1356;
                y_collar_back = 916;
                id_front = R.drawable.d63_front_2xl;
                id_collar = R.drawable.d63_collar_2xl;
                needRotate_Back = false;
                break;
            case "3XL":
                width_front = 2907;
                height_front = 5547;
                width_collar = 3734;
                height_collar = 1203;

                width_combine = 5886;
                height_combine = 7723;
                x_front = 0;
                y_front = 2176;
                x_back = 2979;
                y_back = 2176;
                x_collar_front = width_collar;
                y_collar_front = height_collar;
                x_collar_back = 1425;
                y_collar_back = 900;
                id_front = R.drawable.d63_front_2xl;
                id_collar = R.drawable.d63_collar_2xl;
                needRotate_Back = false;
                break;
            case "4XL":
                width_front = 2985;
                height_front = 5587;
                width_collar = 3852;
                height_collar = 1211;

                width_combine = 5598;
                height_combine = 7763;
                x_front = 0;
                y_front = 2176;
                x_back = 5598;
                y_back = 7763;
                x_collar_front = width_collar;
                y_collar_front = height_collar;
                x_collar_back = 1481;
                y_collar_back = 898;
                id_front = R.drawable.d63_front_5xl;
                id_collar = R.drawable.d63_collar_5xl;
                needRotate_Back = true;
                break;
            case "5XL":
                width_front = 3061;
                height_front = 5624;
                width_collar = 3967;
                height_collar = 1218;

                width_combine = 5756;
                height_combine = 7815;
                x_front = 0;
                y_front = 2191;
                x_back = 5756;
                y_back = 7815;
                x_collar_front = width_collar;
                y_collar_front = height_collar;
                x_collar_back = 1546;
                y_collar_back = 907;
                id_front = R.drawable.d63_front_5xl;
                id_collar = R.drawable.d63_collar_5xl;
                needRotate_Back = true;
                break;
            case "6XL":
                width_front = 3139;
                height_front = 5663;
                width_collar = 4085;
                height_collar = 1227;

                width_combine = 5910;
                height_combine = 7864;
                x_front = 0;
                y_front = 2201;
                x_back = 5910;
                y_back = 7864;
                x_collar_front = width_collar;
                y_collar_front = height_collar;
                x_collar_back = 1606;
                y_collar_back = 900;
                id_front = R.drawable.d63_front_5xl;
                id_collar = R.drawable.d63_collar_5xl;
                needRotate_Back = true;
                break;
            case "7XL":
                width_front = 3218;
                height_front = 5703;
                width_collar = 4202;
                height_collar = 1236;

                width_combine = 6024;
                height_combine = 9000;
                x_front = 0;
                y_front = 3297;
                x_back = 6024;
                y_back = 7924;
                x_collar_front = width_collar;
                y_collar_front = height_collar;
                x_collar_back = 1653;
                y_collar_back = 911;
                id_front = R.drawable.d63_front_7xl;
                id_collar = R.drawable.d63_collar_5xl;
                needRotate_Back = true;
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
