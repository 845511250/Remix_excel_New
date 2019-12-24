package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
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

import static com.example.zuoyun.remix_excel_new.activity.MainActivity.getLastNewCode;

/**
 * Created by zuoyun on 2016/11/4.
 */

public class FragmentDU extends BaseFragment {
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

    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint paint,paintRed, rectPaint;
    String time;
    int widthMain,heightMain,widthSide, heightSide;

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
        time = MainActivity.instance.orderDate_Print;

        //paint
        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(42);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(42);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);


        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_left.setImageDrawable(null);
                    iv_right.setImageDrawable(null);
                    Log.e("fragment2", "message0");
                } else if (message == MainActivity.LOADED_IMGS) {
                    Log.e("fragment2", "message2");
                    bt_remix.setClickable(true);
                    if(!MainActivity.instance.cb_fastmode.isChecked())
                        iv_right.setImageBitmap(MainActivity.instance.bitmaps.get(1));
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
                    for(int i=0;i<currentID;i++) {
                        if (orderItems.get(currentID).order_number.equals(orderItems.get(i).order_number)) {
                            intPlus += 1;
                        }
                    }
                    strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                    remixxKid();
                    intPlus += 1;
                }
            }
        }.start();

    }

    void drawTextLeftSide(Canvas canvas){
        canvas.drawRect(1264, 21, 1414, 61, rectPaint);
        canvas.drawText(getLastNewCode(orderItems.get(currentID).newCode) + "左", 1265, 57, paintRed);
    }
    void drawTextRightSide(Canvas canvas){
        canvas.drawRect(1264, 21, 1414, 61, rectPaint);
        canvas.drawText(getLastNewCode(orderItems.get(currentID).newCode) + "右", 1265, 57, paintRed);
    }
    void drawTextLeftMain(Canvas canvas){
        canvas.drawRect(460, 903, 830, 943, rectPaint);
        canvas.drawText("    左", 460, 939, paintRed);
        canvas.drawText(orderItems.get(currentID).size + "码" + orderItems.get(currentID).color, 640, 939, paint);
        canvas.save();
        canvas.rotate(-73.1f, 1121, 543);
        canvas.drawRect(1121, 503, 1621, 543, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number + "     " + time, 1121, 539, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-107.1f, 200, 530);
        canvas.drawRect(200, 490, 700, 530, rectPaint);
        //canvas.drawBitmap(bitmapBarCode, 200, 480, null);
        canvas.drawText(orderItems.get(currentID).newCode, 200, 526, paintRed);
        canvas.restore();
    }
    void drawTextRightMain(Canvas canvas){
        canvas.drawRect(460, 903, 830, 943, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "码" + orderItems.get(currentID).color, 460, 939, paint);
        canvas.drawText("   右", 640, 939, paintRed);
        canvas.save();
        canvas.rotate(-107.1f, 200, 530);
        canvas.drawRect(200, 490, 700, 530, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number + "     " + time, 200, 526, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-73.1f, 1121, 543);
        canvas.drawRect(1121, 503, 1621, 543, rectPaint);
        //canvas.drawBitmap(bitmapBarCode, 1121, 503, null);
        canvas.drawText(orderItems.get(currentID).newCode, 1121, 540, paintRed);
        canvas.restore();
    }

    public void remixxKid(){
        setSize(orderItems.get(currentID).size);
        Bitmap bitmapCombine = null;
        int margin = 100;
        Matrix matrixCombine = new Matrix();

        if (orderItems.get(currentID).imgs.size() == 4) {
            bitmapCombine = Bitmap.createBitmap(widthSide + heightMain * 2 + margin * 2, widthMain, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            Bitmap bitmapDB_main = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dff31_main);
            Bitmap bitmapDB_side = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dff31_side);//180度

            //left_main
            Bitmap bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextLeftMain(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthMain, heightMain, true);
            matrixCombine.reset();
            matrixCombine.postRotate(-90);
            matrixCombine.postTranslate(widthSide + margin, widthMain);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //left_side
            matrixCombine.reset();
            matrixCombine.postRotate(180);
            matrixCombine.postTranslate(1688, 469);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, 1688, 469, matrixCombine, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_side, 0, 0, null);
            drawTextLeftSide(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);


            //right_main
            bitmapTemp = MainActivity.instance.bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextRightMain(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthMain, heightMain, true);
            matrixCombine.reset();
            matrixCombine.postRotate(-90);
            matrixCombine.postTranslate(widthSide + heightMain + margin * 2, widthMain);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //right_side
            matrixCombine.reset();
            matrixCombine.postRotate(180);
            matrixCombine.postTranslate(1688, 469);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, 1688, 469, matrixCombine, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_side, 0, 0, null);
            drawTextRightSide(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, heightSide + margin * 2, null);

            bitmapDB_main.recycle();
            bitmapDB_side.recycle();
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            bitmapCombine = Bitmap.createBitmap(widthSide + heightMain * 2 + margin * 2, widthMain, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            Bitmap bitmapDB_main = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dff31_main);
            Bitmap bitmapDB_side = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dff31_side);//180度

            //left_main
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1938, 470, 1283, 985);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextLeftMain(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthMain, heightMain, true);
            matrixCombine.reset();
            matrixCombine.postRotate(-90);
            matrixCombine.postTranslate(widthSide + margin, widthMain);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //left_side
            matrixCombine.reset();
            matrixCombine.postRotate(180);
            matrixCombine.postTranslate(1688, 470);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1735, 0, 1688, 470, matrixCombine, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_side, 0, 0, null);
            drawTextLeftSide(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);


            //right_main
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 203, 470, 1283, 985);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextRightMain(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthMain, heightMain, true);
            matrixCombine.reset();
            matrixCombine.postRotate(-90);
            matrixCombine.postTranslate(widthSide + heightMain + margin * 2, widthMain);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //right_side
            matrixCombine.reset();
            matrixCombine.postRotate(180);
            matrixCombine.postTranslate(1688, 470);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 1688, 470, matrixCombine, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_side, 0, 0, null);
            drawTextRightSide(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, heightSide + margin * 2, null);

            bitmapDB_main.recycle();
            bitmapDB_side.recycle();
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 2) {
            MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 1286, 1515, true));
            MainActivity.instance.bitmaps.set(1, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(1), 1286, 1515, true));

            bitmapCombine = Bitmap.createBitmap(widthSide + heightMain * 2 + margin * 2, widthMain, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            //main:1284*985
            //side:1688*470(470*844)
            Bitmap bitmapDB_main = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dff31_main);
            Bitmap bitmapDB_side = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dff31_side);

            //left_main
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 534, 1284, 981);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextLeftMain(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthMain, heightMain, true);
            matrixCombine.reset();
            matrixCombine.postRotate(-90);
            matrixCombine.postTranslate(widthSide + margin, widthMain);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //left_side
            Bitmap bitmapLeftSide1 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 470, 844);
            Bitmap bitmapLeftSide2 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 816, 0, 470, 844);
            bitmapTemp = Bitmap.createBitmap(1688, 470, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Matrix matrixSide = new Matrix();
            matrixSide.postRotate(90);
            matrixSide.postTranslate(844, 0);
            canvasTemp.drawBitmap(bitmapLeftSide1, matrixSide, null);
            matrixSide.reset();
            matrixSide.postRotate(-90);
            matrixSide.postTranslate(844, 470);
            canvasTemp.drawBitmap(bitmapLeftSide2, matrixSide, null);
            canvasTemp.drawBitmap(bitmapDB_side, 0, 0, null);
            drawTextLeftSide(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);


            //right_main
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 534, 1284, 981);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB_main, 0, 0, null);
            drawTextRightMain(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthMain, heightMain, true);
            matrixCombine.reset();
            matrixCombine.postRotate(-90);
            matrixCombine.postTranslate(widthSide + heightMain + margin * 2, widthMain);
            canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

            //right_side
            Bitmap bitmapRightSide1 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, 470, 844);
            Bitmap bitmapRightSide2 = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 816, 0, 470, 844);
            bitmapTemp = Bitmap.createBitmap(1688, 470, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrixSide.reset();
            matrixSide.postRotate(90);
            matrixSide.postTranslate(844, 0);
            canvasTemp.drawBitmap(bitmapRightSide1, matrixSide, null);
            matrixSide.reset();
            matrixSide.postRotate(-90);
            matrixSide.postTranslate(844, 470);
            canvasTemp.drawBitmap(bitmapRightSide2, matrixSide, null);
            canvasTemp.drawBitmap(bitmapDB_side, 0, 0, null);
            drawTextRightSide(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, widthSide, heightSide, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, heightSide + margin * 2, null);

            bitmapDB_main.recycle();
            bitmapDB_side.recycle();
            bitmapTemp.recycle();

            //Bitmap bitmapBarCode = BarCodeUtil.creatBarcode(orderItems.get(currentID).order_number.replace("_", ""), 500, 50);

        }




        try {

            String printColor = orderItems.get(currentID).color.equals("黑") ? "B" : "W";
            String noNewCode = orderItems.get(currentID).newCode.equals("") ? orderItems.get(currentID).sku + orderItems.get(currentID).size : "";
            String nameCombine = noNewCode + orderItems.get(currentID).sku + orderItems.get(currentID).newCode + orderItems.get(currentID).color + orderItems.get(currentID).order_number + strPlus + ".jpg";

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
            Label label0 = new Label(0, currentID+1, orderItems.get(currentID).order_number+orderItems.get(currentID).sku+orderItems.get(currentID).size+printColor);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku+orderItems.get(currentID).size+printColor);
            sheet.addCell(label1);
            Number number2 = new Number(2, currentID+1, orderItems.get(currentID).num);
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
    void setSize(int size){
        switch (size) {
            case 28:
                widthMain = 1210;
                heightMain = 900;
                widthSide = 1546;
                heightSide = 433;
                break;
            case 29:
                widthMain = 1234;
                heightMain = 928;
                widthSide = 1594;
                heightSide = 446;
                break;
            case 30:
                widthMain = 1259;
                heightMain = 955;
                widthSide = 1641;
                heightSide = 458;
                break;
            case 31:
                widthMain = 1283;
                heightMain = 985;
                widthSide = 1688;
                heightSide = 470;
                break;
            case 32:
                widthMain = 1308;
                heightMain = 1015;
                widthSide = 1734;
                heightSide = 482;
                break;
            case 33:
                widthMain = 1332;
                heightMain = 1045;
                widthSide = 1781;
                heightSide = 491;
                break;
            case 34:
                widthMain = 1357;
                heightMain = 1075;
                widthSide = 1827;
                heightSide = 500;
                break;
        }
    }

}
