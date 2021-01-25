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

public class FragmentF9 extends BaseFragment {
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
        paint.setTextSize(18);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(18);
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

    void drawTextFront(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(3.3f, 583, 4675);
        canvas.drawRect(583, 4675 - 18, 583 + 280, 4675, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).sku + " " + orderItems.get(currentID).order_number, 583, 4675 - 2, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-3.1f, 906, 4691);
        canvas.drawRect(906, 4691 - 18, 906 + 280, 4691, rectPaint);
        canvas.drawText("  " +LR + "  " + orderItems.get(currentID).newCode_short, 906, 4691 - 2, paintRed);
        canvas.restore();

    }
    void drawTextBack(Canvas canvas, String LR) {
        canvas.drawRect(1000, 4, 1000 + 400, 4 + 18, rectPaint);
        canvas.drawText("  " + LR + "  " + time + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode_short, 1000, 4 + 16, paint);
    }

    public void remixx(){
        int margin = 120;
        int width_front = 1757 + 60;
        int height_front = 4700 + 140;
        int width_back_back = 4288 + 60;
        int height_back_back = 2746 + 60;
        int width_back_below = 4288 + 60;
        int height_back_below = 2144 + 60;


        Bitmap bitmapCombine = null;

        if (orderItems.get(currentID).imgs.size() == 4) {
            bitmapCombine = Bitmap.createBitmap(width_back_back, height_back_back + height_back_below + height_front + margin * 2, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            //front right
            Bitmap bitmapTemp = Bitmap.createBitmap(1757, 4700, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fv2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back_back + height_back_below + margin * 2, null);

            //front_left
            bitmapTemp = Bitmap.createBitmap(1757, 4700, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, height_back_back + height_back_below + margin * 2, null);

            //back_back
            bitmapTemp = Bitmap.createBitmap(4288, 2746, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f9_back_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "靠背面");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_back, height_back_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //back_below
            bitmapTemp = Bitmap.createBitmap(4288, 2144, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f9_back_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "底座面");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_below, height_back_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back_back + margin, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            bitmapCombine = Bitmap.createBitmap(width_back_back, height_back_back + height_back_below + height_front + margin * 2, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            //front right
            Bitmap bitmapTemp = Bitmap.createBitmap(1757, 4700, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -198, -5074, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fv2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back_back + height_back_below + margin * 2, null);

            //front_left
            bitmapTemp = Bitmap.createBitmap(1757, 4700, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2342, -5074, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, height_back_back + height_back_below + margin * 2, null);

            //back_back
            bitmapTemp = Bitmap.createBitmap(4288, 2746, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -5, -25, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f9_back_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "靠背面");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_back, height_back_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //back_below
            bitmapTemp = Bitmap.createBitmap(4288, 2144, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -5, -2851, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f9_back_below);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp, "底座面");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_below, height_back_below, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back_back + margin, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 2 && MainActivity.instance.bitmaps.get(0).getWidth() < 2000) {
            bitmapCombine = Bitmap.createBitmap(width_front * 2 + margin, height_front, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            //front right
            Bitmap bitmapTemp = Bitmap.createBitmap(1757, 4700, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fv2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //front_left
            bitmapTemp = Bitmap.createBitmap(1757, 4700, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        }


//        Bitmap bitmapCombine = Bitmap.createBitmap(width_front * 2 + margin, height_front + width_back_back + width_back_below + margin * 2, Bitmap.Config.ARGB_8888);
//        Canvas canvasCombine = new Canvas(bitmapCombine);
//        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//        canvasCombine.drawColor(0xffffffff);
//
//        if (orderItems.get(currentID).imgs.size() == 4) {
//            //front right
//            Bitmap bitmapTemp = Bitmap.createBitmap(1757, 4700, Bitmap.Config.ARGB_8888);
//            Canvas canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
//            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fv2);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextFront(canvasTemp, "右");
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
//            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
//
//            //front_left
//            bitmapTemp = Bitmap.createBitmap(1757, 4700, Bitmap.Config.ARGB_8888);
//            canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextFront(canvasTemp, "左");
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
//            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);
//
//            //back_back
//            bitmapTemp = Bitmap.createBitmap(4288, 2746, Bitmap.Config.ARGB_8888);
//            canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f9_back_back);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextBack(canvasTemp, "靠背面");
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_back, height_back_back, true);
//
//            Matrix matrix = new Matrix();
//            matrix.postRotate(90);
//            matrix.postTranslate(height_back_back, height_front + margin);
//            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
//
//            //back_below
//            bitmapTemp = Bitmap.createBitmap(4288, 2144, Bitmap.Config.ARGB_8888);
//            canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f9_back_below);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextBack(canvasTemp, "底座面");
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_below, height_back_below, true);
//
//            matrix = new Matrix();
//            matrix.postRotate(90);
//            matrix.postTranslate(height_back_below, height_front + width_back_back + margin * 2);
//            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
//
//            bitmapDB.recycle();
//            bitmapTemp.recycle();
//        } else if (orderItems.get(currentID).imgs.size() == 1) {
//            //front right
//            Bitmap bitmapTemp = Bitmap.createBitmap(1757, 4700, Bitmap.Config.ARGB_8888);
//            Canvas canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -198, -5074, null);
//            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fv2);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextFront(canvasTemp, "右");
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
//            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
//
//            //front_left
//            bitmapTemp = Bitmap.createBitmap(1757, 4700, Bitmap.Config.ARGB_8888);
//            canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2342, -5074, null);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextFront(canvasTemp, "左");
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
//            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);
//
//            //back_back
//            bitmapTemp = Bitmap.createBitmap(4288, 2746, Bitmap.Config.ARGB_8888);
//            canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -5, -25, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f9_back_back);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextBack(canvasTemp, "靠背面");
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_back, height_back_back, true);
//
//            Matrix matrix = new Matrix();
//            matrix.postRotate(90);
//            matrix.postTranslate(height_back_back, height_front + margin);
//            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
//
//            //back_below
//            bitmapTemp = Bitmap.createBitmap(4288, 2144, Bitmap.Config.ARGB_8888);
//            canvasTemp = new Canvas(bitmapTemp);
//            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -5, -2851, null);
//            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.f9_back_below);
//            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextBack(canvasTemp, "底座面");
//            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back_below, height_back_below, true);
//
//            matrix = new Matrix();
//            matrix.postRotate(90);
//            matrix.postTranslate(height_back_below, height_front + width_back_back + margin * 2);
//            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
//
//            bitmapDB.recycle();
//            bitmapTemp.recycle();
//        }



        File file = new File(sdCardPath + "/生产图/" + childPath + "/");
        if (!file.exists())
            file.mkdirs();

        String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

        String pathSave;
        if (MainActivity.instance.cb_classify.isChecked()) {
            pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
        } else
            pathSave = sdCardPath + "/生产图/" + childPath + "/";
        if (!new File(pathSave).exists())
            new File(pathSave).mkdirs();
        File fileSave = new File(pathSave + nameCombine);
        BitmapToJpg.save(bitmapCombine, fileSave, 90);

        //释放bitmap
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
