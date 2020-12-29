package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

public class FragmentLM extends BaseFragment {
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
    Paint rectPaint,paint, paintRed;


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
        paint.setTextSize(20);
        paint.setAntiAlias(true);

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                } else if (message == MainActivity.LOADED_IMGS) {
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

    void drawTextLML(Canvas canvas) {
        canvas.save();
        canvas.rotate(-20.6f, 32, 259);
        canvas.drawRect(32, 259, 32 + 300, 259 + 20, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + time + " " + orderItems.get(currentID).order_number, 32, 259 + 18, paint);
        canvas.restore();
    }
    void drawTextLMS(Canvas canvas) {
        canvas.save();
        canvas.rotate(-24.1f, 24, 269);
        canvas.drawRect(24, 269, 24 + 300, 269 + 20, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + " " + time + " " + orderItems.get(currentID).order_number, 24, 269 + 18, paint);
        canvas.restore();
    }

    public void remixx(){
        Bitmap bitmapCombine = null;

        if (orderItems.get(currentID).sku.equalsIgnoreCase("LML")) {
            if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
                if (MainActivity.instance.bitmaps.get(0).getWidth() == 4000) {
                    MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 4159, 4159, true));
                }
                bitmapCombine = Bitmap.createBitmap(3951, 2194, Bitmap.Config.ARGB_8888);
                Canvas canvasCombine= new Canvas(bitmapCombine);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasCombine.drawColor(0xffffffff);

                canvasCombine.drawBitmap(MainActivity.instance.bitmaps.get(0), -111, -987, null);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lml);
                canvasCombine.drawBitmap(bitmapDB, 0, 0, null);

                drawTextLML(canvasCombine);
                bitmapDB.recycle();

            } else {
                bitmapCombine = Bitmap.createBitmap(3951, 2194, Bitmap.Config.ARGB_8888);
                Canvas canvasCombine= new Canvas(bitmapCombine);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasCombine.drawColor(0xffffffff);

                canvasCombine.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lml);
                canvasCombine.drawBitmap(bitmapDB, 0, 0, null);

                drawTextLML(canvasCombine);
                bitmapDB.recycle();
            }

        } else if (orderItems.get(currentID).sku.equalsIgnoreCase("LMS")) {
            if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
                if (MainActivity.instance.bitmaps.get(0).getWidth() == 4000) {
                    MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 3660, 3660, true));
                }

                bitmapCombine = Bitmap.createBitmap(3472, 1886, Bitmap.Config.ARGB_8888);
                Canvas canvasCombine= new Canvas(bitmapCombine);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasCombine.drawColor(0xffffffff);

                canvasCombine.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 93, 885, 3472, 1886), 0, 0, null);
                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lms);
                canvasCombine.drawBitmap(bitmapDB, 0, 0, null);

                drawTextLMS(canvasCombine);
                bitmapDB.recycle();
            } else {
                bitmapCombine = Bitmap.createBitmap(3472, 1886, Bitmap.Config.ARGB_8888);
                Canvas canvasCombine= new Canvas(bitmapCombine);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasCombine.drawColor(0xffffffff);

                canvasCombine.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2, 0, 3472, 1886), 0, 0, null);

                Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.lms);
                canvasCombine.drawBitmap(bitmapDB, 0, 0, null);

                drawTextLMS(canvasCombine);
                bitmapDB.recycle();
            }

        }

        try {
            File file=new File(sdCardPath+"/生产图/"+childPath+"/");
            if(!file.exists())
                file.mkdirs();

            String nameCombine = orderItems.get(currentID).sku + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 150);

            //释放bitmap
            bitmapCombine.recycle();

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
