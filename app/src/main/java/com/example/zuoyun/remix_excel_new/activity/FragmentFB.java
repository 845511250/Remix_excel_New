package com.example.zuoyun.remix_excel_new.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
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

import static com.example.zuoyun.remix_excel_new.activity.MainActivity.getLastNewCode;


/**
 * Created by zuoyun on 2016/11/4.
 */

public class FragmentFB extends BaseFragment {
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

    int dxLeft, dxRight, dyLeft, dyRight;
    int heelWidth, heelHeight;
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

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(23);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(23);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(23);
        paintBlue.setTypeface(Typeface.DEFAULT_BOLD);
        paintBlue.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;


        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                } else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
                    if(!MainActivity.instance.cb_fastmode.isChecked())
                        iv_pillow.setImageBitmap(MainActivity.instance.bitmaps.get(0));
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
        canvas.drawRect(left, bottom - 28, left + 420, bottom, rectPaint);
        canvas.drawText(time + "   " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).sizeStr, left, bottom - 2, paint);
        canvas.drawText((currentID + 1) + "", left + 330, bottom - 2, paintRed);
    }

    void drawTextRotate(Canvas canvas, int degree, int left, int bottom, String LR) {
        canvas.save();
        canvas.rotate(degree, (float) left, (float) bottom);
        canvas.drawRect(left, bottom - 22, left + 400, bottom, rectPaint);
        canvas.drawText(orderItems.get(currentID).newCode, left, bottom - 2, paintRed);
        canvas.drawText(time + " " + orderItems.get(currentID).order_number + " "  + orderItems.get(currentID).color + LR, left + 120, bottom - 2, paint);
        canvas.restore();
    }

    void drawTextRotateHeel1(Canvas canvas, int degree, int left, int bottom) {
        canvas.save();
        canvas.rotate(degree, (float) left, (float) bottom);
        canvas.drawRect(left, bottom - 22, left + 215, bottom, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).order_number, left, bottom - 2, paint);
        canvas.restore();
    }
    void drawTextRotateHeel2(Canvas canvas, int degree, int left, int bottom, String LR) {
        canvas.save();
        canvas.rotate(degree, (float) left, (float) bottom);
        canvas.drawRect(left, bottom - 22, left + 200, bottom, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + "码" + orderItems.get(currentID).color + " " + LR, left, bottom - 2, paint);
        canvas.drawText(getLastNewCode(orderItems.get(currentID).newCode) + "", left + 140, bottom - 2, paintRed);
        canvas.restore();
    }

    public void remixx(){
        setScale(orderItems.get(currentID).size);

        Bitmap bitmapCombine = Bitmap.createBitmap(3791, 1890, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);
        Matrix matrixCombine = new Matrix();

        Matrix matrix180 = new Matrix();
        matrix180.setRotate(180, 757, 970);

        //left
        Bitmap bitmapDBLeft = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fbl_android);

        Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 1514, 1941, matrix180, true);
        Canvas canvasLeft = new Canvas(bitmapTemp);
        canvasLeft.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasLeft.drawBitmap(bitmapDBLeft, 0, 0, null);
        bitmapDBLeft.recycle();

        drawTextRotate(canvasLeft, 87, 228, 1400, "左");
        bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, heelWidth, heelHeight, true);

        matrixCombine.reset();
        matrixCombine.postTranslate(dxLeft, dyLeft);
        canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);
        bitmapTemp.recycle();

        //right
        Bitmap bitmapDBRight = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fbr_android);

        bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, 1514, 1941, matrix180, true);
        Canvas canvasRight = new Canvas(bitmapTemp);
        canvasRight.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasRight.drawBitmap(bitmapDBRight, 0, 0, null);
        bitmapDBRight.recycle();

        drawTextRotate(canvasRight, 86, 255, 1400, "右");
        bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, heelWidth, heelHeight, true);

        matrixCombine.reset();
        matrixCombine.postTranslate(dxRight, dyRight);
        canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);
        bitmapTemp.recycle();

        //heel
        Bitmap bitmapDBHeel = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fb36_heel);
        //left
        bitmapTemp = MainActivity.instance.bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvasHeelLeft = new Canvas(bitmapTemp);
        canvasHeelLeft.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasHeelLeft.drawBitmap(bitmapDBHeel, 0, 0, null);
        drawTextRotateHeel1(canvasHeelLeft, -43, 39, 217);
        drawTextRotateHeel2(canvasHeelLeft, 44, 482, 77, "左");

        matrixCombine.reset();
        matrixCombine.postTranslate(3127, 0);
        canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);

        //right
        bitmapTemp = MainActivity.instance.bitmaps.get(3).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvasHeelRight = new Canvas(bitmapTemp);
        canvasHeelRight.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasHeelRight.drawBitmap(bitmapDBHeel, 0, 0, null);
        bitmapDBHeel.recycle();
        drawTextRotateHeel1(canvasHeelRight, -43, 39, 217);
        drawTextRotateHeel2(canvasHeelRight, 44, 482, 77, "右");

        matrixCombine.reset();
        matrixCombine.postTranslate(3127, 901);
        canvasCombine.drawBitmap(bitmapTemp, matrixCombine, null);
        bitmapTemp.recycle();

        Matrix matrix90 = new Matrix();
        matrix90.postRotate(90);
        matrix90.postTranslate(1890, 0);
        bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, 3791, 1890, matrix90, true);

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
            Label label0 = new Label(0, currentID+1, orderItems.get(currentID).order_number+orderItems.get(currentID).sku+ orderItems.get(currentID).size + printColor);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku+ orderItems.get(currentID).size + printColor);
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

    void setScale(int size){
        switch (size + 1) {
//            case 36:
//                heelWidth = 1372;
//                heelHeight = 1567;
//                dxLeft = 60;
//                dyLeft = 103;
//                dxRight = 60;
//                dyRight = 1899;
//                break;
            case 37:
                heelWidth = 1388;
                heelHeight = 1605;
                dxLeft = 93;
                dyLeft = 128;
                dxRight = 1662;
                dyRight = 128;
                break;
            case 38:
                heelWidth = 1402;
                heelHeight = 1644;
                dxLeft = 86;
                dyLeft = 109;
                dxRight = 1653;
                dyRight = 109;
                break;
            case 39:
                heelWidth = 1429;
                heelHeight = 1688;
                dxLeft = 72;
                dyLeft = 87;
                dxRight = 1640;
                dyRight = 87;
                break;
            case 40:
                heelWidth = 1448;
                heelHeight = 1712;
                dxLeft = 63;
                dyLeft = 75;
                dxRight = 1631;
                dyRight = 75;
                break;
            case 41:
                heelWidth = 1469;
                heelHeight = 1741;
                dxLeft = 52;
                dyLeft = 61;
                dxRight = 1621;
                dyRight = 61;
                break;
            case 42:
                heelWidth = 1494;
                heelHeight = 1773;
                dxLeft = 40;
                dyLeft = 45;
                dxRight = 1608;
                dyRight = 45;
                break;
            case 43:
                heelWidth = 1514;
                heelHeight = 1803;
                dxLeft = 30;
                dyLeft = 30;
                dxRight = 1598;
                dyRight = 30;
                break;
        }
    }

}
