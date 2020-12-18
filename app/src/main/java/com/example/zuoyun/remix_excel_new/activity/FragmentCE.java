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

public class FragmentCE extends BaseFragment {
    Context context;
//    String sdCardPath = "/mnt/asec/share";
String sdCardPath = "/storage/emulated/0/Pictures";
    ArrayList<OrderItem> orderItems;
    int currentID;
    String childPath;

    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.iv_right)
    ImageView iv_right;
    @BindView(R.id.bt_remix)
    Button bt_remix;

    Paint paint,paintRed, rectPaint, paintBlue,paintRectBlack;
    String time;

    int widthSide, heightSide, widthMain, heightMain;

    int num;
    String strPlus = "";
    int intPlus = 1;

    @Override
    public int getLayout() {
        return R.layout.fragment_dff;
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

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(27);
        paint.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_left.setImageDrawable(null);
                    iv_right.setImageDrawable(null);
                    Log.e("fragment2", "message0");
                } else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
                    if(!MainActivity.instance.cb_fastmode.isChecked())
                        iv_right.setImageBitmap(MainActivity.instance.bitmaps.get(0));
                    checkremix();
                } else if (message == 3) {
                    bt_remix.setClickable(false);
                } else if(message==MainActivity.LOADED_IMGS){
                    checkremix();
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


    void drawTextMain(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(74.2f, 20, 236);
        canvas.drawRect(20, 236 - 26, 20 + 400, 236, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "码 " + LR + "  " + time + "  " + orderItems.get(currentID).order_number, 20, 236 - 2, paint);
        canvas.restore();
    }

    void drawTextsideL(Canvas canvas, String LR) {
        canvas.drawRect(496, 684 - 26, 496 + 400, 684, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "码 " + LR + "  " + time + "  " + orderItems.get(currentID).order_number, 496, 684 - 2, paint);
    }
    void drawTextsideR(Canvas canvas, String LR) {
        canvas.drawRect(278, 684 - 26, 278 + 400, 684, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).size + "码 " + LR + "  " + time + "  " + orderItems.get(currentID).order_number, 278, 684 - 2, paint);
    }


    public void remixx(){
        setSize(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(2815, 2988, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 6) {
            //leftMain
            Bitmap bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ce_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthMain, heightMain, true);
            canvasCombine.drawBitmap(bitmapTemp, 760 - widthMain / 2, 0, null);

            //rightMain
            bitmapTemp = MainActivity.instance.bitmaps.get(3).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthMain, heightMain, true);
            canvasCombine.drawBitmap(bitmapTemp, 760 - widthMain / 2, 1529, null);

            //Lin
            bitmapTemp = MainActivity.instance.bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ce_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextsideR(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapTemp, 1583, 711 - heightSide, null);

            //Rout
            bitmapTemp = MainActivity.instance.bitmaps.get(5).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextsideR(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapTemp, 1583, 1422 - heightSide, null);

            //Lout
            bitmapTemp = MainActivity.instance.bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ce_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextsideL(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapTemp, 2815 - widthSide, 2197 - heightSide, null);

            //Rin
            bitmapTemp = MainActivity.instance.bitmaps.get(4).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextsideL(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapTemp, 2815 - widthSide, 2908 - heightSide, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //leftMain
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 1482, 1402);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ce_main);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthMain, heightMain, true);
            canvasCombine.drawBitmap(bitmapTemp, 760 - widthMain / 2, 0, null);

            //rightMain
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 1402, 1482, 1402);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMain(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthMain, heightMain, true);
            canvasCombine.drawBitmap(bitmapTemp, 760 - widthMain / 2, 1529, null);

            //Lin
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1481, 693, 1177, 693);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ce_side_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextsideR(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapTemp, 1583, 711 - heightSide, null);

            //Rout
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1481, 2095, 1177, 693);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextsideR(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapTemp, 1583, 1422 - heightSide, null);

            //Lout
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1481, 0, 1177, 693);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ce_side_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextsideL(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapTemp, 2815 - widthSide, 2197 - heightSide, null);

            //Rin
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1481, 1402, 1177, 693);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextsideL(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapTemp, 2815 - widthSide, 2908 - heightSide, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        }

        try {
            String nameCombine = orderItems.get(currentID).sku + orderItems.get(currentID).size + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 148);

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
            Label label0 = new Label(0, currentID+1, orderItems.get(currentID).order_number+orderItems.get(currentID).sku+orderItems.get(currentID).size);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku+orderItems.get(currentID).size);
            sheet.addCell(label1);
            Number number2 = new Number(2, currentID+1, orderItems.get(currentID).num);
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
        if (MainActivity.instance.tb_auto.isChecked())
            remix();
    }

    void setSize(int size){
        switch (size) {
            case 41:
                widthMain = 1442;
                heightMain = 1343;
                widthSide = 1126;
                heightSide = 676;
                break;
            case 42:
                widthMain = 1461;
                heightMain = 1374;
                widthSide = 1153;
                heightSide = 681;
                break;
            case 43:
                widthMain = 1482;
                heightMain = 1403;
                widthSide = 1177;
                heightSide = 695;
                break;
            case 44:
                widthMain = 1506;
                heightMain = 1432;
                widthSide = 1202;
                heightSide = 704;
                break;
            case 45:
                widthMain = 1521;
                heightMain = 1459;
                widthSide = 1232;
                heightSide = 711;
                break;
        }
    }

}
