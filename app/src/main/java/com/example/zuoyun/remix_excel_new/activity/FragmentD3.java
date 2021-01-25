package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.Log;
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

public class FragmentD3 extends BaseFragment {
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

    int width_side, height_side, width_main, height_main;
    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint rectPaint, paint, paintRed, paintBlue,paintRectBlack;
    String time;

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

        paintRectBlack = new Paint();
        paintRectBlack.setColor(0xff000000);
        paintRectBlack.setStyle(Paint.Style.FILL);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(26);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(26);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(26);
        paintBlue.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;


        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                    Log.e("fragment2", "message0");
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

    void drawTextSideR(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(83.6f, 14, 116);
        canvas.drawRect(14, 116 - 25, 14 + 500, 116, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).sku + orderItems.get(currentID).color + "-" + orderItems.get(currentID).size + "码 " + LR + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 14, 116 - 3, paint);
        canvas.restore();
    }
    void drawTextSideL(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(-83.6f, 587, 688);
        canvas.drawRect(587, 688 - 25, 587 + 500, 688, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).sku + orderItems.get(currentID).color + "-" + orderItems.get(currentID).size + "码 " + LR + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 587, 688 - 3, paint);
        canvas.restore();
    }
    void drawTextMain(Canvas canvasPart2L, String LR){
        canvasPart2L.save();
        canvasPart2L.rotate(77f, 7, 129);
        canvasPart2L.drawRect(7, 129 - 25, 7 + 300, 129, rectPaint);
        canvasPart2L.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).color + "-" + orderItems.get(currentID).size + "码" + LR + " " + orderItems.get(currentID).order_number, 7, 129 - 3, paint);
        canvasPart2L.restore();

        canvasPart2L.save();
        canvasPart2L.rotate(64.4f, 170, 729);
        canvasPart2L.drawRect(170, 729 - 25, 170 + 130, 729, rectPaint);
        canvasPart2L.drawText(time, 170, 729 - 3, paint);
        canvasPart2L.restore();
    }
    public void remixx(){
        setSize(orderItems.get(currentID).size);

        int margin = 80;
        Bitmap bitmapCombine = Bitmap.createBitmap(width_side * 4 + margin * 3, height_side + height_main, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 1 && (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight())) {
            if (MainActivity.instance.bitmaps.get(0).getWidth() == 4000) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 3005, 3005, true));
            }
            //side_right_r
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 74, 341, 663, 1292);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.d3_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideR(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //side_right_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 837, 341, 663, 1292);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.d3_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideL(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side + margin, 0, null);

            //side_left_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1503, 341, 663, 1292);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.d3_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideR(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side * 2 + margin * 2, 0, null);

            //side_left_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2266, 341, 663, 1292);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.d3_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSideL(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_side, height_side, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side * 3 + margin * 3, 0, null);

            //main right
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 127, 1527, 1330, 1140);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.d3_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_side, null);

            //main left
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1556, 1527, 1330, 1140);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_main, height_main, true);
            canvasCombine.drawBitmap(bitmapTemp, width_side * 2 + margin * 2, height_side, null);

            bitmapTemp.recycle();
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
            Label label0 = new Label(0, currentID+1, orderItems.get(currentID).order_number+orderItems.get(currentID).sku+ orderItems.get(currentID).size);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku+ orderItems.get(currentID).size);
            sheet.addCell(label1);
            int num=orderItems.get(currentID).num;
            Number number2 = new Number(2, currentID+1, num);
            sheet.addCell(number2);
            Label label3 = new Label(3, currentID+1, orderItems.get(currentID).customer);
            sheet.addCell(label3);
            Label label4 = new Label(4, currentID + 1, MainActivity.instance.orderDate_Excel);
            sheet.addCell(label4);
            Label label6 = new Label(6, currentID + 1, "平台大货");
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

    void setSize(int size){
        switch (size) {
            case 36:
                width_main = 1222;
                height_main = 1007;
                width_side = 611;
                height_side = 1138;
                break;
            case 37:
                width_main = 1244;
                height_main = 1034;
                width_side = 624;
                height_side = 1167;
                break;
            case 38:
                width_main = 1265;
                height_main = 1060;
                width_side = 637;
                height_side = 1197;
                break;
            case 39:
                width_main = 1287;
                height_main = 1087;
                width_side = 650;
                height_side = 1226;
                break;
            case 40:
                width_main = 1309;
                height_main = 1113;
                width_side = 652;
                height_side = 1263;
                break;
            case 41:
                width_main = 1330;
                height_main = 1140;
                width_side = 663;
                height_side = 1292;
                break;
            case 42:
                width_main = 1352;
                height_main = 1166;
                width_side = 675;
                height_side = 1319;
                break;
            case 43:
                width_main = 1373;
                height_main = 1191;
                width_side = 687;
                height_side = 1349;
                break;
            case 44:
                width_main = 1395;
                height_main = 1216;
                width_side = 699;
                height_side = 1377;
                break;
            case 45:
                width_main = 1417;
                height_main = 1240;
                width_side = 712;
                height_side = 1408;
                break;
            case 46:
                width_main = 1438;
                height_main = 1265;
                width_side = 725;
                height_side = 1437;
                break;
        }
    }
}
