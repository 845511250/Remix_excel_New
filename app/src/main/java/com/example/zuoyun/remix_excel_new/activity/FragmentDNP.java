package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
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

public class FragmentDNP extends BaseFragment {
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

    Paint rectPaint, paint, paintRed;
    String time = MainActivity.instance.orderDate_Print;
    int width, height;


    @Override
    public int getLayout() {
        return R.layout.fragmentdg;
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
        paint.setTextSize(22);
        paint.setAntiAlias(true);

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

    void drawtextDN(Canvas canvasremix) {
        canvasremix.drawRect(2, 2, 2 + 400, 2 + 22, rectPaint);
        canvasremix.drawText("DN正面 " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 20, 2 + 20, paint);
        canvasremix.drawRect(height + 2, 2, height + 2 + 400, 2 + 22, rectPaint);
        canvasremix.drawText("DN背面 " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, height + 20, 2 + 20, paint);
    }
    void drawtextDP(Canvas canvasremix) {
        canvasremix.save();
        canvasremix.rotate(-90, 2, height - 2);
        canvasremix.drawRect(2, height - 2, 2 + 400, height - 2 + 22, rectPaint);
        canvasremix.drawText("DP正面 " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 20, height - 2 + 20, paint);
        canvasremix.restore();

        canvasremix.save();
        canvasremix.rotate(-90, 2, height * 2 - 2);
        canvasremix.drawRect(2, height * 2 - 2, 2 + 400, height * 2 - 2 + 22, rectPaint);
        canvasremix.drawText("DP背面 " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 20, height * 2 - 2 + 20, paint);
        canvasremix.restore();
    }

    public void remixx(){
        Bitmap bitmapFront, bitmapBack;
        Bitmap bitmapDB;

        Bitmap bitmapremix;
        Canvas canvasremix;
        if (orderItems.get(currentID).sku.equals("DN") && orderItems.get(currentID).sizeStr.equals("M")) {
            orderItems.get(currentID).sku = "DP";
        }

        if (orderItems.get(currentID).sku.equals("DN")) {
            width = 2953 + 59;//51x28.5
            height = 1683;

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.border_dn);

            if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
                bitmapFront = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 71, 658, 2859, 1683);
                bitmapFront = Bitmap.createScaledBitmap(bitmapFront, width, height, true);
                bitmapBack = bitmapFront;
            } else if (orderItems.get(currentID).imgs.size() == 1) {
                bitmapFront = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), width, height, true);
                bitmapBack = bitmapFront;
            } else {
                bitmapFront = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(1), width, height, true);
                bitmapBack = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), width, height, true);
            }

            bitmapremix = Bitmap.createBitmap(height * 2, width, Bitmap.Config.ARGB_8888);
            canvasremix = new Canvas(bitmapremix);
            canvasremix.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasremix.drawColor(0xffffffff);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            matrix.postTranslate(height, 0);
            canvasremix.drawBitmap(bitmapFront, matrix, null);
            canvasremix.drawBitmap(bitmapDB, 0, 0, null);

            matrix.postTranslate(height, 0);
            canvasremix.drawBitmap(bitmapBack, matrix, null);
            canvasremix.drawBitmap(bitmapDB, height, 0, null);
            drawtextDN(canvasremix);

            bitmapFront.recycle();
            if (bitmapBack != null) {
                bitmapBack.recycle();
            }
            bitmapDB.recycle();

        } else {
            width = 2598;////44*54
            height = 1594;

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.border_dp);

            if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
                bitmapFront = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 71, 658, 2859, 1683);
                bitmapFront = Bitmap.createScaledBitmap(bitmapFront, width, height, true);
                bitmapBack = bitmapFront;
            } else if (orderItems.get(currentID).imgs.size() == 1) {
                bitmapFront = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), width, height, true);
                bitmapBack = bitmapFront;
            } else {
                bitmapFront = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(1), width, height, true);
                bitmapBack = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), width, height, true);
            }

            bitmapremix = Bitmap.createBitmap(width, height * 2, Bitmap.Config.ARGB_8888);//44*54
            canvasremix = new Canvas(bitmapremix);
            canvasremix.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasremix.drawColor(0xffffffff);

            canvasremix.drawBitmap(bitmapFront, 0, 0, null);
            canvasremix.drawBitmap(bitmapDB, 0, 0, null);
            canvasremix.drawBitmap(bitmapBack, 0, height, null);
            canvasremix.drawBitmap(bitmapDB, 0, height, null);
            drawtextDP(canvasremix);

            bitmapFront.recycle();
            if (bitmapBack != null) {
                bitmapBack.recycle();
            }
            bitmapDB.recycle();
        }


        try {
            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapremix, fileSave, 150);



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
            Label label4 = new Label(4, currentID+1, MainActivity.instance.orderDate_Excel);
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
