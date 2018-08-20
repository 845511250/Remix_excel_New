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

public class FragmentDX extends BaseFragment {
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

    float scaleX=1.0f, scaleY = 1.0f;
    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint rectPaint, paint, paintRed, paintBlue;
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

        //paint
        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(30);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(30);
        paintBlue.setTypeface(Typeface.DEFAULT_BOLD);
        paintBlue.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                } else if (message == 4) {
                    Log.e("fragmentDL", "message4");
                    if(!MainActivity.instance.cb_fastmode.isChecked())
                        iv_pillow.setImageBitmap(MainActivity.instance.bitmapPillow);
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
                    remixx();
                    intPlus += 1;
                }
            }
        }.start();

    }

    void drawText(Canvas canvas, int left, int bottom) {
        canvas.drawRect(left, bottom - 28, left + 500, bottom, rectPaint);
        canvas.drawText(time + "   " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).sizeStr, left, bottom - 2, paint);
        canvas.drawText(orderItems.get(currentID).newCode, left + 330, bottom - 2, paintRed);
    }
    void drawTextRotate(Canvas canvas, int degree, int left, int bottom) {
        canvas.save();
        canvas.rotate(degree, (float) left, (float) bottom);
        canvas.drawRect(left, bottom - 28, left + 500, bottom, rectPaint);
        canvas.drawText(time + "   " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).sizeStr, left, bottom - 2, paint);
        canvas.drawText(orderItems.get(currentID).newCode, left + 330, bottom - 2, paintRed);
        canvas.restore();
    }

    public void remixx(){
        String sizeStr = orderItems.get(currentID).sizeStr;

        //bitmapCombine
        Bitmap bitmapCombine;
        if(sizeStr.equals("S"))
            bitmapCombine = Bitmap.createBitmap(3802, 4007 + 180, Bitmap.Config.ARGB_8888);
        else if(sizeStr.equals("M"))
            bitmapCombine = Bitmap.createBitmap(4650, 4964 + 180, Bitmap.Config.ARGB_8888);
        else if(sizeStr.equals("L"))
            bitmapCombine = Bitmap.createBitmap(5131, 5278 + 180, Bitmap.Config.ARGB_8888);
        else {
            showDialogSizeWrong(orderItems.get(currentID).order_number);
            return;
        }

        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);
        Matrix matrixCombine = new Matrix();

        //part1
        Bitmap bitmapDBPart1 = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part1);
        Bitmap bitmapPart1 = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 319, 0, 3440, 845);
        Canvas canvasPart1 = new Canvas(bitmapPart1);
        canvasPart1.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasPart1.drawBitmap(bitmapDBPart1, 0, 0, null);
        drawText(canvasPart1, 1110, 845);
        bitmapDBPart1.recycle();

        setScale(sizeStr, 1);
        bitmapPart1 = Bitmap.createScaledBitmap(bitmapPart1, (int) (scaleX * 3440), (int) (scaleY * 845), true);
        if(sizeStr.equals("S")){
            matrixCombine.reset();
            matrixCombine.postTranslate(0, 847);
            canvasCombine.drawBitmap(bitmapPart1, matrixCombine, null);
        }
        else if(sizeStr.equals("M")){
            matrixCombine.reset();
            matrixCombine.postTranslate(0, 1040);
            canvasCombine.drawBitmap(bitmapPart1, matrixCombine, null);
        }
        else if(sizeStr.equals("L")){
            matrixCombine.reset();
            matrixCombine.postTranslate(0, 1160);
            canvasCombine.drawBitmap(bitmapPart1, matrixCombine, null);
        }
        bitmapPart1.recycle();

        //part2
        Bitmap bitmapDBPart2 = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part2);
        Bitmap bitmapPart2 = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 319, 681, 3440, 387);
        Canvas canvasPart2 = new Canvas(bitmapPart2);
        canvasPart2.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasPart2.drawBitmap(bitmapDBPart2, 0, 0, null);
        drawText(canvasPart2, 1110, 28);
        drawText(canvasPart2, 1110, 387);
        bitmapDBPart2.recycle();

        setScale(sizeStr, 2);
        bitmapPart2 = Bitmap.createScaledBitmap(bitmapPart2, (int) (scaleX * 3440), (int) (scaleY * 387), true);
        if(sizeStr.equals("S")){
            matrixCombine.reset();
            matrixCombine.postTranslate(0, 1528);
            canvasCombine.drawBitmap(bitmapPart2, matrixCombine, null);
        }
        else if(sizeStr.equals("M")){
            matrixCombine.reset();
            matrixCombine.postTranslate(0, 1957);
            canvasCombine.drawBitmap(bitmapPart2, matrixCombine, null);
        }
        else if(sizeStr.equals("L")){
            matrixCombine.reset();
            matrixCombine.postTranslate(0, 2071);
            canvasCombine.drawBitmap(bitmapPart2, matrixCombine, null);
        }
        bitmapPart2.recycle();

        //part3
        Bitmap bitmapDBPart3 = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part3);
        Bitmap bitmapPart3 = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 1116, 914, 1839, 2366);
        Canvas canvasPart3 = new Canvas(bitmapPart3);
        canvasPart3.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasPart3.drawBitmap(bitmapDBPart3, 0, 0, null);
        //drawText(canvasPart3, 744, 30);
        drawText(canvasPart3, 424, 2366);
        bitmapDBPart3.recycle();

        setScale(sizeStr, 3);
        bitmapPart3 = Bitmap.createScaledBitmap(bitmapPart3, (int) (scaleX * 1839), (int) (scaleY * 2366), true);
        if(sizeStr.equals("S")){
            matrixCombine.reset();
            matrixCombine.postRotate(90);
            matrixCombine.postTranslate(1902, 1911);
            canvasCombine.drawBitmap(bitmapPart3, matrixCombine, null);
        }
        else if(sizeStr.equals("M")){
            matrixCombine.reset();
            matrixCombine.postRotate(90);
            matrixCombine.postTranslate(2366, 2424);
            canvasCombine.drawBitmap(bitmapPart3, matrixCombine, null);
        }
        else if(sizeStr.equals("L")){
            matrixCombine.reset();
            matrixCombine.postRotate(90);
            matrixCombine.postTranslate(2599, 2561);
            canvasCombine.drawBitmap(bitmapPart3, matrixCombine, null);
        }
        bitmapPart3.recycle();

        //part4
        Bitmap bitmapDBPart4 = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part4);
        Bitmap bitmapPart4 = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 930, 2000, 2198, 621);
        Canvas canvasPart4 = new Canvas(bitmapPart4);
        canvasPart4.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasPart4.drawBitmap(bitmapDBPart4, 0, 0, null);
        //drawText(canvasPart4, 924, 28);
        drawText(canvasPart4, 600, 621);
        drawTextRotate(canvasPart4, 90, 5, 220);
        drawTextRotate(canvasPart4, -90, 2194, 570);
        bitmapDBPart4.recycle();

        setScale(sizeStr, 4);
        bitmapPart4 = Bitmap.createScaledBitmap(bitmapPart4, (int) (scaleX * 2198), (int) (scaleY * 621), true);
        if(sizeStr.equals("S")){
            matrixCombine.reset();
            matrixCombine.postTranslate(0, 3434);
            canvasCombine.drawBitmap(bitmapPart4, matrixCombine, null);
        }
        else if(sizeStr.equals("M")){
            matrixCombine.reset();
            matrixCombine.postTranslate(0, 4343);
            canvasCombine.drawBitmap(bitmapPart4, matrixCombine, null);
        }
        else if(sizeStr.equals("L")){
            matrixCombine.reset();
            matrixCombine.postTranslate(0, 4657);
            canvasCombine.drawBitmap(bitmapPart4, matrixCombine, null);
        }
        bitmapPart4.recycle();

        //part5
        Bitmap bitmapDBPart5 = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part5);
        Bitmap bitmapPart5 = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 930, 2503, 2194, 1147);
        Canvas canvasPart5 = new Canvas(bitmapPart5);
        canvasPart5.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasPart5.drawBitmap(bitmapDBPart5, 0, 0, null);
        drawText(canvasPart5, 600, 28);
        drawText(canvasPart5, 600, 1147);
        drawTextRotate(canvasPart5, 90, 2, 220);
        drawTextRotate(canvasPart5, -90, 2192, 570);
        bitmapDBPart5.recycle();

        setScale(sizeStr, 5);
        bitmapPart5 = Bitmap.createScaledBitmap(bitmapPart5, (int) (scaleX * 2194), (int) (scaleY * 1147), true);
        if(sizeStr.equals("S")){
            matrixCombine.reset();
            matrixCombine.postRotate(90);
            matrixCombine.postTranslate(2905, 1979);
            canvasCombine.drawBitmap(bitmapPart5, matrixCombine, null);
        }
        else if(sizeStr.equals("M")){
            matrixCombine.reset();
            matrixCombine.postRotate(90);
            matrixCombine.postTranslate(3577, 2537);
            canvasCombine.drawBitmap(bitmapPart5, matrixCombine, null);
        }
        else if(sizeStr.equals("L")){
            matrixCombine.reset();
            matrixCombine.postRotate(90);
            matrixCombine.postTranslate(3910, 2745);
            canvasCombine.drawBitmap(bitmapPart5, matrixCombine, null);
        }
        bitmapPart5.recycle();

        //part6
        Bitmap bitmapDBPart6 = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part6);
        Bitmap bitmapPart6 = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 34, 2097, 1010, 1364);
        Canvas canvasPart6 = new Canvas(bitmapPart6);
        canvasPart6.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasPart6.drawBitmap(bitmapDBPart6, 0, 0, null);
        drawTextRotate(canvasPart6, 91, 980, 500);
        bitmapDBPart6.recycle();

        setScale(sizeStr, 6);
        bitmapPart6 = Bitmap.createScaledBitmap(bitmapPart6, (int) (scaleX * 1010), (int) (scaleY * 1364), true);
        if(sizeStr.equals("S")){
            matrixCombine.reset();
            matrixCombine.postTranslate(2975, 1148);
            canvasCombine.drawBitmap(bitmapPart6, matrixCombine, null);
        }
        else if(sizeStr.equals("M")){
            matrixCombine.reset();
            matrixCombine.postTranslate(3640, 1275);
            canvasCombine.drawBitmap(bitmapPart6, matrixCombine, null);
        }
        else if(sizeStr.equals("L")){
            matrixCombine.reset();
            matrixCombine.postTranslate(3979, 1228);
            canvasCombine.drawBitmap(bitmapPart6, matrixCombine, null);
        }
        bitmapPart6.recycle();

        //part7
        Bitmap bitmapDBPart7 = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part7);
        Bitmap bitmapPart7 = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 3007, 2097, 1010, 1364);
        Canvas canvasPart7 = new Canvas(bitmapPart7);
        canvasPart7.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasPart7.drawBitmap(bitmapDBPart7, 0, 0, null);
        drawTextRotate(canvasPart7, -91, 40, 850);
        bitmapDBPart7.recycle();

        setScale(sizeStr, 7);
        bitmapPart7 = Bitmap.createScaledBitmap(bitmapPart7, (int) (scaleX * 1010), (int) (scaleY * 1364), true);
        if(sizeStr.equals("S")){
            matrixCombine.reset();
            matrixCombine.postTranslate(2975, 2452);
            canvasCombine.drawBitmap(bitmapPart7, matrixCombine, null);
        }
        else if(sizeStr.equals("M")){
            matrixCombine.reset();
            matrixCombine.postTranslate(3640, 2899);
            canvasCombine.drawBitmap(bitmapPart7, matrixCombine, null);
        }
        else if(sizeStr.equals("L")){
            matrixCombine.reset();
            matrixCombine.postTranslate(3979, 2886);
            canvasCombine.drawBitmap(bitmapPart7, matrixCombine, null);
        }
        bitmapPart7.recycle();

        //part8
        Bitmap bitmapDBPart8 = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.dx_part8);
        Bitmap bitmapPart8 = Bitmap.createBitmap(4100, 968, Bitmap.Config.ARGB_8888);
        Canvas canvasPart8 = new Canvas(bitmapPart8);
        canvasPart8.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        Bitmap bitmapPart8_1 = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 62, 2122, 968, 826);
        Bitmap bitmapPart8_2 = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 826, 3532, 2448, 968);
        Bitmap bitmapPart8_3 = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 3033, 2122, 968, 826);
        matrixCombine.reset();
        matrixCombine.postRotate(-90);
        matrixCombine.postTranslate(0, 968);
        canvasPart8.drawBitmap(bitmapPart8_1, matrixCombine, null);
        canvasPart8.drawBitmap(bitmapPart8_2, 826, 0, null);
        matrixCombine.reset();
        matrixCombine.postRotate(90);
        matrixCombine.postTranslate(4100, 0);
        canvasPart8.drawBitmap(bitmapPart8_3, matrixCombine, null);
        canvasPart8.drawBitmap(bitmapDBPart8, 0, 0, null);
        drawText(canvasPart8, 1537, 28);

        bitmapPart8_1.recycle();
        bitmapPart8_2.recycle();
        bitmapPart8_3.recycle();
        bitmapDBPart8.recycle();

        setScale(sizeStr, 8);
        bitmapPart8 = Bitmap.createScaledBitmap(bitmapPart8, (int) (scaleX * 4100), (int) (scaleY * 968), true);
        if(sizeStr.equals("S")){
            matrixCombine.reset();
            matrixCombine.postTranslate(0, 0);
            canvasCombine.drawBitmap(bitmapPart8, matrixCombine, null);
        }
        else if(sizeStr.equals("M")){
            matrixCombine.reset();
            matrixCombine.postTranslate(0, 0);
            canvasCombine.drawBitmap(bitmapPart8, matrixCombine, null);
        }
        else if(sizeStr.equals("L")){
            matrixCombine.reset();
            matrixCombine.postTranslate(0, 0);
            canvasCombine.drawBitmap(bitmapPart8, matrixCombine, null);
        }
        bitmapPart8.recycle();

        try {
            String noNewCode = orderItems.get(currentID).newCode.equals("") ? orderItems.get(currentID).sku + orderItems.get(currentID).sizeStr : "";
            String nameCombine = noNewCode + orderItems.get(currentID).newCode + orderItems.get(currentID).order_number + strPlus + ".jpg";

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
            Label label3 = new Label(3, currentID+1, "小左");
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
            MainActivity.instance.bitmapPillow.recycle();
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

    void setScale(String size, int part) {
        switch (size + part) {
            case "S1":
                scaleX = 0.79f;
                scaleY = 0.72f;
                break;
            case "S2":
                scaleX = 0.79f;
                scaleY = 0.809f;
                break;
            case "S3":
                scaleX = 0.794f;
                scaleY = 0.806f;
                break;
            case "S4":
                scaleX = 0.793f;
                scaleY = 0.923f;
                break;
            case "S5":
                scaleX = 0.794f;
                scaleY = 0.814f;
                break;
            case "S6":
                scaleX = 0.819f;
                scaleY = 0.866f;
                break;
            case "S7":
                scaleX = 0.819f;
                scaleY = 0.866f;
                break;
            case "S8":
                scaleX = 0.807f;
                scaleY = 0.806f;
                break;

            case "M1":
                scaleX = 0.997f;
                scaleY = 0.86f;
                break;
            case "M2":
                scaleX = 0.997f;
                scaleY = 0.992f;
                break;
            case "M3":
                scaleX = 1.002f;
                scaleY = 0.998f;
                break;
            case "M4":
                scaleX = 0.995f;
                scaleY = 1.0f;
                break;
            case "M5":
                scaleX = 0.996f;
                scaleY = 1.005f;
                break;
            case "M6":
                scaleX = 1.0f;
                scaleY = 1.005f;
                break;
            case "M7":
                scaleX = 1.0f;
                scaleY = 1.005f;
                break;
            case "M8":
                scaleX = 0.994f;
                scaleY = 1.002f;
                break;

            case "L1":
                scaleX = 1.099f;
                scaleY = 0.979f;
                break;
            case "L2":
                scaleX = 1.099f;
                scaleY = 1.083f;
                break;
            case "L3":
                scaleX = 1.092f;
                scaleY = 1.098f;
                break;
            case "L4":
                scaleX = 1.075f;
                scaleY = 1.028f;
                break;
            case "L5":
                scaleX = 1.077f;
                scaleY = 1.076f;
                break;
            case "L6":
                scaleX = 1.141f;
                scaleY = 1.1f;
                break;
            case "L7":
                scaleX = 1.141f;
                scaleY = 1.1f;
                break;
            case "L8":
                scaleX = 1.095f;
                scaleY = 1.135f;
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
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
                tv_content.setText("单号："+order_number+"读取尺码失败");
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
