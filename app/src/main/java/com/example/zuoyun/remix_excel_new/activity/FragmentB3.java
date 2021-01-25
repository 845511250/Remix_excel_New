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

public class FragmentB3 extends BaseFragment {
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

    int width_front, width_back, width_sleeve, width_sleeve2, width_sleeve3, width_collar;
    int height_front, height_back, height_sleeve, height_sleeve2, height_sleeve3, height_collar;

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
        paint.setTextSize(22);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
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
        canvas.drawRect(1268, 3458 - 22, 1268 + 500, 3458, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1268, 3458 - 2, paint);
    }
    void drawTextFront(Canvas canvas) {
        canvas.drawRect(1338, 3541 - 22, 1338 + 500, 3541, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short + " " + time, 1338, 3541 - 2, paint);
    }
    void drawTextSleeve(Canvas canvas, String LR) {
        canvas.drawRect(1580, 31 - 22, 1580 + 200, 31, rectPaint);
        canvas.drawText(LR + orderItems.get(currentID).sizeStr + " " + orderItems.get(currentID).order_number, 1580, 31 - 2, paint);
    }
    void drawTextSleeve2(Canvas canvas, String LR) {
        canvas.drawRect(1803, 612 - 22, 1803 + 30, 612, rectPaint);
        canvas.drawText(LR, 1803, 612 - 2, paint);
    }
    void drawTextSleeve3(Canvas canvas, String LR) {
        canvas.drawRect(1960, 698 - 22, 1960 + 30, 698, rectPaint);
        canvas.drawText(LR, 1960, 698 - 2, paint);
    }


    public void remixx() {
        int margin = 100;
        Bitmap bitmapCombine = Bitmap.createBitmap(width_front + width_back + height_sleeve * 2 + height_sleeve2 * 2 + height_sleeve3 * 2 + height_collar + margin * 4, width_sleeve3, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 4) {
            //back
            Bitmap bitmapTemp = Bitmap.createBitmap(3177, 3464, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.b3_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //front
            bitmapTemp = Bitmap.createBitmap(3183, 3548, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.b3_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //sleeveL
            bitmapTemp = Bitmap.createBitmap(3302, 1890, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), -325, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.b3_sleeve);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            matrix.postTranslate(height_sleeve + width_front + width_back + margin * 2, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //sleeveR
            bitmapTemp = Bitmap.createBitmap(3302, 1890, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), -325, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.b3_sleeve);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);

            matrix.postTranslate(height_sleeve + margin, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //sleeve3L
            bitmapTemp = Bitmap.createBitmap(3952, 698, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, -2027, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.b3_sleeve3);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve3(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve3, height_sleeve3, true);

            matrix.reset();
            matrix.postRotate(90);
            matrix.postTranslate(height_sleeve3 + width_front + width_back + height_sleeve * 2 + margin * 4, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //sleeve2L
            bitmapTemp = Bitmap.createBitmap(3616, 612, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), -168, -1678, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.b3_sleeve2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve2(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve2, height_sleeve2, true);

            matrix.reset();
            matrix.postRotate(90);
            matrix.postTranslate(height_sleeve2 + width_front + width_back + height_sleeve * 2 + height_sleeve3 + margin * 4, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //sleeve3R
            bitmapTemp = Bitmap.createBitmap(3952, 698, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, -2027, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.b3_sleeve3);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve3(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve3, height_sleeve3, true);

            matrix.reset();
            matrix.postRotate(90);
            matrix.postTranslate(height_sleeve3 + width_front + width_back + height_sleeve * 2 + height_sleeve3 + height_sleeve2 + margin * 4, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //sleeve2R
            bitmapTemp = Bitmap.createBitmap(3616, 612, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), -168, -1678, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.b3_sleeve2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve2(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve2, height_sleeve2, true);

            matrix.reset();
            matrix.postRotate(90);
            matrix.postTranslate(height_sleeve2 + width_front + width_back + height_sleeve * 2 + height_sleeve3 * 2 + height_sleeve2 + margin * 4, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //collar
            bitmapTemp = Bitmap.createBitmap(1659, 1306, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), -762, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.b3_collar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);

            matrix.reset();
            matrix.postRotate(90);
            matrix.postTranslate(height_collar + width_front + width_back + height_sleeve * 2 + height_sleeve3 * 2 + height_sleeve2 * 2 + margin * 4, width_sleeve2 / 2 - width_collar / 2);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);


            bitmapTemp.recycle();
            bitmapDB.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 1) {

        }


        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

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
                width_front = 2710;
                height_front = 3406;
                width_back = 2704;
                height_back = 3322;
                width_sleeve = 3006;
                height_sleeve = 1750;
                width_sleeve2 = 3325;
                height_sleeve2 = 600;
                width_sleeve3 = 3664;
                height_sleeve3 = 685;
                width_collar = 1586;
                height_collar = 1264;
                break;
            case "M":
                width_front = 2821;
                height_front = 3452;
                width_back = 2820;
                height_back = 3370;
                width_sleeve = 3101;
                height_sleeve = 1794;
                width_sleeve2 = 3420;
                height_sleeve2 = 600;
                width_sleeve3 = 3757;
                height_sleeve3 = 685;
                width_collar = 1610;
                height_collar = 1278;
                break;
            case "L":
                width_front = 2991;
                height_front = 3500;
                width_back = 2987;
                height_back = 3416;
                width_sleeve = 3200;
                height_sleeve = 1843;
                width_sleeve2 = 3520;
                height_sleeve2 = 600;
                width_sleeve3 = 3854;
                height_sleeve3 = 685;
                width_collar = 1634;
                height_collar = 1291;
                break;
            case "XL":
                width_front = 3183;
                height_front = 3548;
                width_back = 3177;
                height_back = 3464;
                width_sleeve = 3302;
                height_sleeve = 1890;
                width_sleeve2 = 3616;
                height_sleeve2 = 600;
                width_sleeve3 = 3952;
                height_sleeve3 = 685;
                width_collar = 1659;
                height_collar = 1306;
                break;
            case "2XL":
                width_front = 3374;
                height_front = 3642;
                width_back = 3370;
                height_back = 3558;
                width_sleeve = 3403;
                height_sleeve = 1938;
                width_sleeve2 = 3714;
                height_sleeve2 = 600;
                width_sleeve3 = 4044;
                height_sleeve3 = 685;
                width_collar = 1679;
                height_collar = 1319;
                break;
            case "3XL":
                width_front = 3515;
                height_front = 3600;
                width_back = 3515;
                height_back = 3517;
                width_sleeve = 3479;
                height_sleeve = 1991;
                width_sleeve2 = 3769;
                height_sleeve2 = 636;
                width_sleeve3 = 4106;
                height_sleeve3 = 721;
                width_collar = 1689;
                height_collar = 1317;
                break;
            case "4XL":
                width_front = 3702;
                height_front = 3649;
                width_back = 3704;
                height_back = 3563;
                width_sleeve = 3573;
                height_sleeve = 2039;
                width_sleeve2 = 3842;
                height_sleeve2 = 646;
                width_sleeve3 = 4198;
                height_sleeve3 = 729;
                width_collar = 1712;
                height_collar = 1332;
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
