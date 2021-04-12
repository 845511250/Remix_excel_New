package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
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

public class FragmentDK extends BaseFragment {
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

    int num;
    String strPlus = "";
    int intPlus = 1;

    String time = MainActivity.instance.orderDate_Print;

    Paint rectPaint, paint, paintWhite, paintRed;

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

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(30);
        paint.setAntiAlias(true);

        paintWhite = new Paint();
        paintWhite.setColor(0xffffffff);
        paintWhite.setStrokeWidth(2f);
        paintWhite.setTypeface(Typeface.DEFAULT_BOLD);
        paintWhite.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(38);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);


        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                } else if (message == MainActivity.LOADED_IMGS) {
                    if(!MainActivity.instance.cb_fastmode.isChecked()){
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

    public void remix(){
        new Thread(){
            @Override
            public void run() {
                super.run();
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
        }.start();

    }

    public void remixx(){
        Bitmap bitmapRFront = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 609, 2220);
        Bitmap bitmapRBack = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 18, 0, 609, 2220);
        Bitmap bitmapLFront = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 609, 2220);
        Bitmap bitmapLBack = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 18, 0, 609, 2220);

        if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
            if (MainActivity.instance.bitmaps.get(0).getWidth() != 2738) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 2738, 2738, true));
            }
            bitmapLFront = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1385, 259, 609, 2220);
            bitmapLBack = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2043 + 21, 259, 609, 2220);
            bitmapRFront = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 728, 259, 609, 2220);
            bitmapRBack = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 68 + 21, 259, 609, 2220);
        } else if (orderItems.get(currentID).imgs.size() == 2 && orderItems.get(currentID).imgs.get(0).contains("back")) {//4u2订单左右脚一样，前后不一样
            bitmapRFront = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, 609, 2220);
            bitmapLFront = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, 609, 2220);
        } else if (orderItems.get(currentID).imgs.size() == 2) {//左右脚不一样
            bitmapLFront = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, 609, 2220);
            bitmapLBack = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 18, 0, 609, 2220);
        } else if (orderItems.get(currentID).imgs.size() == 4) {//4u2订单左前 左后 右前 右后
            bitmapLBack = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 18, 0, 609, 2220);
            bitmapLFront = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, 609, 2220);
            bitmapRBack = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 18, 0, 609, 2220);
            bitmapRFront = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, 609, 2220);
        }

        orderItems.get(currentID).sizeStr = orderItems.get(currentID).sizeStr.replace("/", "-");
        orderItems.get(currentID).newCode = orderItems.get(currentID).newCode.replace("/", "-");


        int width, height_front, height_back;
        int id_DB;
        if (orderItems.get(currentID).sizeStr.contains("L")) {
            width = 1196 / 2;
            height_front = 2412;
            height_back = (int) (height_front * 0.794);
            id_DB = R.drawable.dk_l_xl;
        } else {
            width = 1196 / 2;
//            height_front = 2284;
            height_front = 2284 - 230;
            height_back = (int) (height_front * 0.794);
            id_DB = R.drawable.dk_s_m;
        }

        //----------------------如果是亚当袜子 后面back无需缩放
        boolean isPPSL = orderItems.get(currentID).platform.startsWith("pillow") || orderItems.get(currentID).platform.startsWith("shoe");
        if (isPPSL) {
            height_back = height_front;
        }
        //-----------------------

        int margin = 70;
        Bitmap bitmapComnine = Bitmap.createBitmap(width * 4 + margin, height_front, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapComnine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        bitmapLFront = Bitmap.createScaledBitmap(bitmapLFront, width, height_front, true);
        canvasCombine.drawBitmap(bitmapLFront, 0, 0, null);
        bitmapLBack = Bitmap.createScaledBitmap(bitmapLBack, width, height_back, true);
        canvasCombine.drawBitmap(bitmapLBack, width, 0, null);
        if (!isPPSL) {
            bitmapLBack = Bitmap.createScaledBitmap(bitmapLBack, width, -height_back, true);
            canvasCombine.drawBitmap(bitmapLBack, width, height_back, null);
        }
        Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), id_DB);
        canvasCombine.drawBitmap(bitmapDB, 0, 0, null);

        bitmapRFront = Bitmap.createScaledBitmap(bitmapRFront, width, height_front, true);
        canvasCombine.drawBitmap(bitmapRFront, width * 2 + margin, 0, null);
        bitmapRBack = Bitmap.createScaledBitmap(bitmapRBack, width, height_back, true);
        canvasCombine.drawBitmap(bitmapRBack, width * 3 + margin, 0, null);
        if (!isPPSL) {
            bitmapRBack = Bitmap.createScaledBitmap(bitmapRBack, width, -height_back, true);
            canvasCombine.drawBitmap(bitmapRBack, width * 3 + margin, height_back, null);
        }
        canvasCombine.drawBitmap(bitmapDB, width * 2 + margin, 0, null);

        //drawText
        canvasCombine.drawRect(750, 1840, 750 + 300, 1840 + 170, rectPaint);
        canvasCombine.drawText(orderItems.get(currentID).newCode, 750, 1840 + 40, paintRed);
        canvasCombine.drawText(orderItems.get(currentID).order_number, 750, 1840 + 80, paint);
        canvasCombine.drawText(orderItems.get(currentID).sizeStr.contains("L") ? "大号袜子" : "中号袜子", 750, 1840 + 120, paint);
        canvasCombine.drawText(time, 750, 1840 + 160, paint);

        canvasCombine.drawRect(width * 2 + margin + 750, 1840, 2250 + 300, 1840 + 170, rectPaint);
        canvasCombine.drawText(orderItems.get(currentID).newCode, width * 2 + margin + 750, 1840 + 40, paintRed);
        canvasCombine.drawText(orderItems.get(currentID).order_number, width * 2 + margin + 750, 1840 + 80, paint);
        canvasCombine.drawText(orderItems.get(currentID).sizeStr.contains("L") ? "大号袜子" : "中号袜子", width * 2 + margin + 750, 1840 + 120, paint);
        canvasCombine.drawText(time, width * 2 + margin + 750, 1840 + 160, paint);


        try {
            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

            if (orderItems.get(currentID).platform.equals("zy")) {
                nameCombine = orderItems.get(currentID).sku + "_" + orderItems.get(currentID).sizeStr+ "(" + MainActivity.instance.orderDate_short + "-" + (currentID + 1) + ")_" + orderItems.get(currentID).order_number + strPlus + "_共" + orderItems.get(currentID).newCode + "个" + ".jpg";
            }

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapComnine, fileSave, 150);

            //释放bitmap
            bitmapDB.recycle();
            bitmapRFront.recycle();
            bitmapRBack.recycle();
            bitmapLFront.recycle();
            bitmapLBack.recycle();
            bitmapComnine.recycle();

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
            int num = orderItems.get(currentID).num;
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
        if (MainActivity.instance.tb_auto.isChecked()){
            remix();
        }
    }

}
