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

public class FragmentX1 extends BaseFragment {
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

    int width_front, width_back, width_pocket;
    int height_front, height_back, height_pocket;


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
        canvas.save();
        canvas.rotate(-2.2f, 3277, 3800);
        canvas.drawRect(3277, 3800 - 19, 3277 + 300, 3800, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 3277, 3800 - 2, paint);
        canvas.restore();
    }
    void drawTextPocketLeft(Canvas canvas) {
        canvas.save();
        canvas.rotate(-105.5f, 1985, 871);
        canvas.drawRect(1985, 871 - 19, 1985 + 400, 871, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "左口袋" + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1985, 871 - 2, paint);
        canvas.restore();
    }
    void drawTextPocketRight(Canvas canvas) {
        canvas.save();
        canvas.rotate(105.5f, 272, 367);
        canvas.drawRect(272, 367 - 19, 272 + 400, 367, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "右口袋" + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 272, 367 - 2, paint);
        canvas.restore();
    }
    void drawTextBackLeft(Canvas canvas) {
        canvas.save();
        canvas.rotate(1.9f, 2981, 3902);
        canvas.drawRect(2981, 3902 - 19, 2981 + 300, 3902, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "左后片" + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 2981, 3902 - 2, paint);
        canvas.restore();
    }
    void drawTextBackRight(Canvas canvas) {
        canvas.save();
        canvas.rotate(-1.9f, 23, 3912);
        canvas.drawRect(23, 3912 - 19, 23 + 300, 3912, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "右后片" + orderItems.get(currentID).sizeStr + "  " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 23, 3912 - 2, paint);
        canvas.restore();
    }


    public void remixx() {
        int margin = 120;

        Bitmap bitmapCombine = Bitmap.createBitmap((int) (width_front * 0.848) + width_back * 2 + margin, Math.max(height_front + height_pocket + margin, (int) (height_front * 0.486) + height_back), Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 2) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(6496, 3804, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.x1_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //pocket_left
            bitmapTemp = Bitmap.createBitmap(2120, 1904, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -2750, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.x1_pocket_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketLeft(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket + margin, height_front + margin, null);

            //pocket_right
            bitmapTemp = Bitmap.createBitmap(2120, 1904, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -1630, 0, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -2120, 1904, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketRight(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //back_left
            bitmapTemp = Bitmap.createBitmap(3308, 3916, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.x1_back_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackLeft(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, (int) (width_front * 0.848), (int) (height_front * 0.486), null);

            //back_right
            bitmapTemp = Bitmap.createBitmap(3308, 3916, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3308, 0, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -3308, 3916, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackRight(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, (int) (width_front * 0.848) + width_back + margin, (int) (height_front * 0.486), null);

            bitmapTemp.recycle();
            bitmapDB.recycle();


            if (num == 1) {
                MainActivity.recycleExcelImages();
            }
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(6496, 3804, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -94, -204, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.x1_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //pocket_left
            bitmapTemp = Bitmap.createBitmap(2120, 1904, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2844, -204, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.x1_pocket_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketLeft(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket + margin, height_front + margin, null);

            //pocket_right
            bitmapTemp = Bitmap.createBitmap(2120, 1904, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1724, -204, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -2120, 1904, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketRight(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //back_left
            bitmapTemp = Bitmap.createBitmap(3308, 3916, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -6690, -92, null);
            bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.x1_back_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackLeft(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, (int) (width_front * 0.848), (int) (height_front * 0.486), null);

            //back_right
            bitmapTemp = Bitmap.createBitmap(3308, 3916, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -9998, -92, null);
            bitmapDB = Bitmap.createScaledBitmap(bitmapDB, -3308, 3916, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackRight(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, (int) (width_front * 0.848) + width_back + margin, (int) (height_front * 0.486), null);

            bitmapTemp.recycle();
            bitmapDB.recycle();


            if (num == 1) {
                MainActivity.recycleExcelImages();
            }

        }


        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

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
        width_pocket = 2120;
        height_pocket = 1908;

        switch (size) {
            case "S":
                width_front = 5784;
                height_front = 3489;
                width_back = 2952;
                height_back = 3593;
                break;
            case "M":
                width_front = 5995;
                height_front = 3595;
                width_back = 3057;
                height_back = 3703;
                break;
            case "L":
                width_front = 6207;
                height_front = 3695;
                width_back = 3162;
                height_back = 3806;
                break;
            case "XL":
                width_front = 6496;
                height_front = 3804;
                width_back = 3308;
                height_back = 3916;
                break;
            case "2XL":
                width_front = 6786;
                height_front = 3904;
                width_back = 3452;
                height_back = 4018;
                break;
            case "3XL":
                width_front = 7077;
                height_front = 4004;
                width_back = 3598;
                height_back = 4119;
                break;
            case "4XL":
                width_front = 7366;
                height_front = 4105;
                width_back = 3743;
                height_back = 4220;
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
