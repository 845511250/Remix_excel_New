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

public class FragmentGP extends BaseFragment {
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
        paint.setTextSize(19);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(20);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

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
                            intPlus += orderItems.get(i).num;
                        }
                    }
                    strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                    remixx();
                }
            }
        }.start();

    }

    void drawText(Canvas canvas, String LR) {
        canvas.drawRect(900, 4, 900 + 300, 4 + 18, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).order_number, 900, 4 + 17, paint);
    }

    public void remixx(){
        int frontWidth = 2120, frontHeight = 3294, backWidth = 2145, backHeight = 1637;

        Bitmap bitmapCombine = Bitmap.createBitmap(backWidth * 2 + 50, frontHeight + backHeight + 150, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).isPPSL) {
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 17, 20, 2065, 3224);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gp_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, frontWidth, frontHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp.recycle();

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 17, 3302, 2065, 1557);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gp_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, backWidth, backHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, frontHeight + 100, null);
            bitmapTemp.recycle();

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 17, 20, 2065, 3224);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gp_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, frontWidth, frontHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, backWidth + 50, 0, null);
            bitmapTemp.recycle();

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 17, 3302, 2065, 1557);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gp_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, backWidth, backHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, backWidth + 50, frontHeight + 100, null);
            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 4) {
            if (orderItems.get(currentID).sizeStr.equals("4PCS")) {
                //左前
                Bitmap bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(2), 2085, 3250, true);
                bitmapTemp = Bitmap.createBitmap(bitmapTemp, 10, 20, 2065, 3224);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                Bitmap bitmapDB_front = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gp_front);
                canvasTemp.drawBitmap(bitmapDB_front, 0, 0, null);
//        drawText(canvasTemp, "左");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, frontWidth, frontHeight, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

                //左后
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 10, 2065, 1557);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                Bitmap bitmapDB_back = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gp_back);
                canvasTemp.drawBitmap(bitmapDB_back, 0, 0, null);
//        drawText(canvasTemp, "左");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, backWidth, backHeight, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, frontHeight + 100, null);

                //右前
                bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(3), 2085, 3250, true);
                bitmapTemp = Bitmap.createBitmap(bitmapTemp, 10, 20, 2065, 3224);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(bitmapDB_front, 0, 0, null);
//        drawText(canvasTemp, "右");
                bitmapDB_front.recycle();
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, frontWidth, frontHeight, true);
                canvasCombine.drawBitmap(bitmapTemp, backWidth + 50, 0, null);

                //右后
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 10, 2065, 1557);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(bitmapDB_back, 0, 0, null);
//        drawText(canvasTemp, "右");
                bitmapDB_back.recycle();
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, backWidth, backHeight, true);
                canvasCombine.drawBitmap(bitmapTemp, backWidth + 50, frontHeight + 100, null);
                bitmapTemp.recycle();
            } else if (orderItems.get(currentID).sizeStr.equals("2PCS")) {
                bitmapCombine = Bitmap.createBitmap(frontWidth * 2 + 50, frontHeight + 60, Bitmap.Config.ARGB_8888);
                canvasCombine= new Canvas(bitmapCombine);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasCombine.drawColor(0xffffffff);

                //左前
                Bitmap bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(2), 2085, 3250, true);
                bitmapTemp = Bitmap.createBitmap(bitmapTemp, 10, 20, 2065, 3224);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                Bitmap bitmapDB_front = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gp_front);
                canvasTemp.drawBitmap(bitmapDB_front, 0, 0, null);
//        drawText(canvasTemp, "左");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, frontWidth, frontHeight, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

                //右前
                bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(3), 2085, 3250, true);
                bitmapTemp = Bitmap.createBitmap(bitmapTemp, 10, 20, 2065, 3224);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.drawBitmap(bitmapDB_front, 0, 0, null);
//        drawText(canvasTemp, "右");
                bitmapDB_front.recycle();
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, frontWidth, frontHeight, true);
                canvasCombine.drawBitmap(bitmapTemp, frontWidth + 50, 0, null);
                bitmapTemp.recycle();
            }
        } else if (orderItems.get(currentID).imgs.size() == 2) {
            //左前
            Bitmap bitmapTemp = MainActivity.instance.bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB_front = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gp_front);
            canvasTemp.drawBitmap(bitmapDB_front, 0, 0, null);
//        drawText(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, frontWidth, frontHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //左后
            bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB_back = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gp_back);
            canvasTemp.drawBitmap(bitmapDB_back, 0, 0, null);
//        drawText(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, backWidth, backHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, frontHeight + 100, null);

            //右前
            bitmapTemp = MainActivity.instance.bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_front, 0, 0, null);
//        drawText(canvasTemp, "右");
            bitmapDB_front.recycle();
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, frontWidth, frontHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, backWidth + 50, 0, null);

            //右后
            bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_back, 0, 0, null);
//        drawText(canvasTemp, "右");
            bitmapDB_back.recycle();
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, backWidth, backHeight, true);
            canvasCombine.drawBitmap(bitmapTemp, backWidth + 50, frontHeight + 100, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            if (orderItems.get(currentID).sizeStr.equals("4PCS")) {
                //左前
                Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 17, 20, 2065, 3224);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                Bitmap bitmapDB_front = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gp_front);
                canvasTemp.drawBitmap(bitmapDB_front, 0, 0, null);
//        drawText(canvasTemp, "左");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, frontWidth, frontHeight, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

                //左后
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 17, 3302, 2065, 1557);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                Bitmap bitmapDB_back = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gp_back);
                canvasTemp.drawBitmap(bitmapDB_back, 0, 0, null);
//        drawText(canvasTemp, "左");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, backWidth, backHeight, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, frontHeight + 100, null);

                //右前
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 17, 20, 2065, 3224);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(bitmapDB_front, 0, 0, null);
//        drawText(canvasTemp, "右");
                bitmapDB_front.recycle();
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, frontWidth, frontHeight, true);
                canvasCombine.drawBitmap(bitmapTemp, backWidth + 50, 0, null);

                //右后
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 17, 3302, 2065, 1557);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasTemp.drawBitmap(bitmapDB_back, 0, 0, null);
//        drawText(canvasTemp, "右");
                bitmapDB_back.recycle();
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, backWidth, backHeight, true);
                canvasCombine.drawBitmap(bitmapTemp, backWidth + 50, frontHeight + 100, null);
                bitmapTemp.recycle();
            } else if (orderItems.get(currentID).sizeStr.equals("2PCS")) {
                bitmapCombine = Bitmap.createBitmap(frontWidth * 2 + 50, frontHeight + 60, Bitmap.Config.ARGB_8888);
                canvasCombine= new Canvas(bitmapCombine);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasCombine.drawColor(0xffffffff);

                //左前
                Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 17, 20, 2065, 3224);
                Canvas canvasTemp = new Canvas(bitmapTemp);
                Bitmap bitmapDB_front = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gp_front);
                canvasTemp.drawBitmap(bitmapDB_front, 0, 0, null);
//        drawText(canvasTemp, "左");
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, frontWidth, frontHeight, true);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

                //右前
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 17, 20, 2065, 3224);
                canvasTemp = new Canvas(bitmapTemp);
                canvasTemp.drawBitmap(bitmapDB_front, 0, 0, null);
//        drawText(canvasTemp, "右");
                bitmapDB_front.recycle();
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, frontWidth, frontHeight, true);
                canvasCombine.drawBitmap(bitmapTemp, frontWidth + 50, 0, null);
                bitmapTemp.recycle();
            }
        }



        try {
            File file=new File(sdCardPath+"/生产图/"+childPath+"/");
            if(!file.exists())
                file.mkdirs();

            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 120);



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
