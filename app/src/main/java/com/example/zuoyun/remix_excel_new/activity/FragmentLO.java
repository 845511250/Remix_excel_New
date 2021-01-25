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

public class FragmentLO extends BaseFragment {
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
    int height_front,height_back, height_sleeve,height_collar;

    int num;
    String strPlus = "";
    int intPlus = 1;
    boolean sizeOK = true;

    Paint rectPaint, paint, paintRed, paintBlue, rectBorderPaint, paintSmall;
    String time;

    String gender = "";

    @Override
    public int getLayout() {
        return R.layout.fragment_dg;
    }

    @Override
    public void initData(View view) {
        context = getContext();
        ButterKnife.bind(this, view);
        orderItems=MainActivity.instance.orderItems;
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

    public void remix(){
        new Thread(){
            @Override
            public void run() {
                super.run();


                if (sizeOK) {
                    for(num=orderItems.get(currentID).num;num>=1;num--) {
                    intPlus = orderItems.get(currentID).num - num + 1;
                        for(int i=0;i<currentID;i++) {
                            if (orderItems.get(currentID).order_number.equals(orderItems.get(i).order_number)) {
                                intPlus += orderItems.get(i).num;;
                            }
                        }
                        strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                        remixx();
                    }
                }

            }
        }.start();

    }

    void drawTextFrontR(Canvas canvas) {
        canvas.drawRect(1018, 3063 - 17, 1018 + 500, 3063, rectPaint);
        canvas.drawText(gender + "-" + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode_short, 1018, 3063 - 2, paint);
    }
    void drawTextFrontL(Canvas canvas) {
        canvas.drawRect(384, 3063 - 17, 384 + 500, 3063, rectPaint);
        canvas.drawText(gender + "-" + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode_short, 384, 3063 - 2, paint);
    }
    void drawTextBack(Canvas canvas) {
        canvas.drawRect(1030, 3230 - 17, 1030 + 500, 3230, rectPaint);
        canvas.drawText(gender + "-" + orderItems.get(currentID).sizeStr + " " + time + " " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode_short, 1030, 3230 - 2, paint);
    }
    void drawTextSleeve(Canvas canvas, String LR) {
        canvas.drawRect(680, 1045 - 17, 680 + 500, 1045, rectPaint);
        canvas.drawText(gender + "-" + orderItems.get(currentID).sizeStr + " " + LR + "袖子 " + time + " " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode_short, 680, 1045 - 2, paint);
    }



    public void remixx(){
        if(orderItems.get(currentID).sku.equals("LOW")){
            setSizeLOW(orderItems.get(currentID).sizeStr);
            gender = "Z77女款（用小一码材料）";
        }else {
            setSizeLO(orderItems.get(currentID).sizeStr);
            gender = "Z77 ";
        }

        int margin = 100;
        Matrix matrix = new Matrix();

        Bitmap bitmapCombine = Bitmap.createBitmap(width_front * 2 + width_back + width_sleeve + margin, Math.max(height_back, height_sleeve * 2 + height_collar * 2 + margin * 3), Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);


        if (orderItems.get(currentID).imgs.size() == 5) {
            //front right
            Bitmap bitmapTemp = Bitmap.createBitmap(1904, 3069, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lo_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //front left
            bitmapTemp = Bitmap.createBitmap(1904, 3069, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), -1903, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lo_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back, 0, null);

            //back
            bitmapTemp = Bitmap.createBitmap(2547, 3241, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lo_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front, 0, null);

            //sleeve left
            bitmapTemp = Bitmap.createBitmap(1931, 1069, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lo_sleeve_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + margin, 0, null);

            //sleeve right
            bitmapTemp = Bitmap.createBitmap(1931, 1069, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lo_sleeve_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + margin, height_sleeve + margin, null);

            //collar
            bitmapTemp = Bitmap.createBitmap(1822, 401, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lo_collar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextCollar(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + margin, height_sleeve * 2 + margin * 2, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + margin, height_sleeve * 2 + height_collar + margin * 3, null);


            bitmapDB.recycle();
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //front right
            Bitmap bitmapTemp = Bitmap.createBitmap(1904, 3069, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -24, -41, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lo_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //front left
            bitmapTemp = Bitmap.createBitmap(1904, 3069, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1926, -41, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lo_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back, 0, null);

            //back
            bitmapTemp = Bitmap.createBitmap(2547, 3241, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3924, -481, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lo_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front, 0, null);

            //sleeve left
            bitmapTemp = Bitmap.createBitmap(1931, 1069, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1984, -3192, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lo_sleeve_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + margin, 0, null);

            //sleeve right
            bitmapTemp = Bitmap.createBitmap(1931, 1069, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -28, -3192, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lo_sleeve_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + margin, height_sleeve + margin, null);

            //collar
            bitmapTemp = Bitmap.createBitmap(1822, 401, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4287, -41, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lo_collar);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextCollar(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_collar, height_collar, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + margin, height_sleeve * 2 + margin * 2, null);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + margin, height_sleeve * 2 + height_collar + margin * 3, null);


            bitmapDB.recycle();
            bitmapTemp.recycle();
        }


        matrix.reset();
        matrix.postRotate(90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
        bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);


        String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";
        String pathSave;
        if(MainActivity.instance.cb_classify.isChecked()){
            pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
        } else
            pathSave = sdCardPath + "/生产图/" + childPath + "/";
        if(!new File(pathSave).exists())
            new File(pathSave).mkdirs();
        File fileSave = new File(pathSave + nameCombine);
        BitmapToJpg.save(bitmapCombine, fileSave, 99);
        bitmapCombine.recycle();

        try {
            //写入excel
            String writePath = sdCardPath + "/生产图/" + childPath + "/生产单.xls";
            File fileWrite = new File(writePath);
            if(!fileWrite.exists()){
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
            WritableWorkbook workbook = Workbook.createWorkbook(fileWrite,book);
            WritableSheet sheet = workbook.getSheet(0);
            Label label0 = new Label(0, currentID+1, orderItems.get(currentID).order_number+orderItems.get(currentID).sku);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku);
            sheet.addCell(label1);
            int num=orderItems.get(currentID).num;
            Number number2 = new Number(2, currentID+1, num);
            sheet.addCell(number2);
            Label label3 = new Label(3, currentID+1, orderItems.get(currentID).customer);
            sheet.addCell(label3);
            Label label4 = new Label(4, currentID + 1, MainActivity.instance.orderDate_Excel);
            sheet.addCell(label4);
            Label label6 = new Label(6, currentID+1, "平台大货");
            sheet.addCell(label6);

            workbook.write();
            workbook.close();

        }catch (Exception e){
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
    public void checkremix(){
        if (MainActivity.instance.tb_auto.isChecked())
            remix();
    }

    void setSizeLO(String size) {
        switch (size) {
            case "2XS":
                width_front = 1670;
                height_front = 2753;
                width_back = 2155;
                height_back = 2927;
                width_sleeve = 1612;
                height_sleeve = 911;
                width_collar = 1600;
                height_collar = 395;
                break;
            case "XS":
                width_front = 1729;
                height_front = 2832;
                width_back = 2252;
                height_back = 3006;
                width_sleeve = 1692;
                height_sleeve = 951;
                width_collar = 1656;
                height_collar = 397;
                break;
            case "S":
                width_front = 1787;
                height_front = 2911;
                width_back = 2351;
                height_back = 3084;
                width_sleeve = 1772;
                height_sleeve = 989;
                width_collar = 1710;
                height_collar = 398;
                break;
            case "M":
                width_front = 1846;
                height_front = 2990;
                width_back = 2449;
                height_back = 3163;
                width_sleeve = 1851;
                height_sleeve = 1030;
                width_collar = 1766;
                height_collar = 400;
                break;
            case "L":
                width_front = 1902;
                height_front = 3068;
                width_back = 2547;
                height_back = 3241;
                width_sleeve = 1931;
                height_sleeve = 1069;
                width_collar = 1822;
                height_collar = 401;
                break;
            case "XL":
                width_front = 1977;
                height_front = 3149;
                width_back = 2665;
                height_back = 3320;
                width_sleeve = 2017;
                height_sleeve = 1109;
                width_collar = 1905;
                height_collar = 403;
                break;
            case "2XL":
                width_front = 2050;
                height_front = 3229;
                width_back = 2783;
                height_back = 3398;
                width_sleeve = 2104;
                height_sleeve = 1147;
                width_collar = 1987;
                height_collar = 405;
                break;
            case "3XL":
                width_front = 2122;
                height_front = 3309;
                width_back = 2900;
                height_back = 3477;
                width_sleeve = 2190;
                height_sleeve = 1187;
                width_collar = 2069;
                height_collar = 407;
                break;
            case "4XL":
                width_front = 2196;
                height_front = 3389;
                width_back = 3018;
                height_back = 3555;
                width_sleeve = 2278;
                height_sleeve = 1226;
                width_collar = 2151;
                height_collar = 409;
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }
    }
    void setSizeLOW(String size) {
        switch (size) {
            case "XS":
                width_front = 1670;
                height_front = 2753;
                width_back = 2155;
                height_back = 2927;
                width_sleeve = 1612;
                height_sleeve = 911;
                width_collar = 1600;
                height_collar = 395;
                break;
            case "S":
                width_front = 1729;
                height_front = 2832;
                width_back = 2252;
                height_back = 3006;
                width_sleeve = 1692;
                height_sleeve = 951;
                width_collar = 1656;
                height_collar = 397;
                break;
            case "M":
                width_front = 1787;
                height_front = 2911;
                width_back = 2351;
                height_back = 3084;
                width_sleeve = 1772;
                height_sleeve = 989;
                width_collar = 1710;
                height_collar = 398;
                break;
            case "L":
                width_front = 1846;
                height_front = 2990;
                width_back = 2449;
                height_back = 3163;
                width_sleeve = 1851;
                height_sleeve = 1030;
                width_collar = 1766;
                height_collar = 400;
                break;
            case "XL":
                width_front = 1902;
                height_front = 3068;
                width_back = 2547;
                height_back = 3241;
                width_sleeve = 1931;
                height_sleeve = 1069;
                width_collar = 1822;
                height_collar = 401;
                break;
            case "2XL":
                width_front = 1977;
                height_front = 3149;
                width_back = 2665;
                height_back = 3320;
                width_sleeve = 2017;
                height_sleeve = 1109;
                width_collar = 1905;
                height_collar = 403;
                break;
            case "3XL":
                width_front = 2050;
                height_front = 3229;
                width_back = 2783;
                height_back = 3398;
                width_sleeve = 2104;
                height_sleeve = 1147;
                width_collar = 1987;
                height_collar = 405;
                break;
            case "4XL":
                width_front = 2122;
                height_front = 3309;
                width_back = 2900;
                height_back = 3477;
                width_sleeve = 2190;
                height_sleeve = 1187;
                width_collar = 2069;
                height_collar = 407;
                break;
            case "5XL":
                width_front = 2196;
                height_front = 3389;
                width_back = 3018;
                height_back = 3555;
                width_sleeve = 2278;
                height_sleeve = 1226;
                width_collar = 2151;
                height_collar = 409;
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }
    }

    public void showDialogSizeWrong(final String order_number){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog dialog_finish;
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogTransBackGround);
                dialog_finish = builder.create();
                dialog_finish.setCancelable(false);
                dialog_finish.show();
                View view_dialog = LayoutInflater.from(context).inflate(R.layout.item_dialog_finish, null);
                dialog_finish.setContentView(view_dialog);
                TextView tv_title = (TextView) view_dialog.findViewById(R.id.tv_dialog_title);
                TextView tv_content = (TextView) view_dialog.findViewById(R.id.tv_dialog_content);
                Button bt_yes = (Button) view_dialog.findViewById(R.id.bt_dialog_yes);

                tv_title.setText("错误！");
                tv_content.setText("单号："+order_number+"没有这个尺码");
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
