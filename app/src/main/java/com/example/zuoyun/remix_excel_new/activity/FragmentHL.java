package com.example.zuoyun.remix_excel_new.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zuoyun.remix_excel_new.R;
import com.example.zuoyun.remix_excel_new.bean.OrderItem;
import com.example.zuoyun.remix_excel_new.tools.BarCodeUtil;
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

public class FragmentHL extends BaseFragment {
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
    boolean sizeOK = true;

    Paint rectPaint, rectPaintGrey,rectPaintRed, paint, paintRed, paintBlue, rectBorderPaint, paintSmall;
    String time;

    int dpi,width,height;
    int width_barcode, height_barcode;//6x2.5cm
    int height_barText;//0.4cm

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

        rectPaintGrey = new Paint();
        rectPaintGrey.setColor(0xfff4f4e9);
        rectPaintGrey.setStyle(Paint.Style.FILL);

        rectPaintRed = new Paint();
        rectPaintRed.setColor(0xffff0000);
        rectPaintRed.setStyle(Paint.Style.FILL);

        rectBorderPaint = new Paint();
        rectBorderPaint.setColor(0xff000000);
        rectBorderPaint.setStyle(Paint.Style.STROKE);
        rectBorderPaint.setStrokeWidth(6);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(25);
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

        paintSmall = new Paint();
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(23);
        paintSmall.setTypeface(Typeface.DEFAULT_BOLD);
        paintSmall.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                    bt_remix.setClickable(false);
                } else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
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
        bt_remix.setClickable(false);
    }

    public void remix(){
        new Thread(){
            @Override
            public void run() {
                super.run();

                if (sizeOK) {
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
            }
        }.start();

    }

    void drawText(Canvas canvas) {
        paint.setTextSize(30);

        if(orderItems.get(currentID).platform.equals("zy")){
            orderItems.get(currentID).newCode = "共" + orderItems.get(currentID).newCode + "个";
        }
        canvas.drawRect(width - 1200, height - 38, width - 100, height - 8, rectPaint);
        canvas.drawText("下边 空调毯-" + orderItems.get(currentID).sku + " " + orderItems.get(currentID).color + "   " + time + "   " + orderItems.get(currentID).order_number + "    " + orderItems.get(currentID).newCode, width - 1200, height - 8 - 4, paint);

        canvas.drawRect(width - 1200, 8, width - 100, 8 + 30, rectPaint);
        canvas.drawText("上边 空调毯-" + orderItems.get(currentID).sku + " " + orderItems.get(currentID).color + "   " + time + "   " + orderItems.get(currentID).order_number + "    " + orderItems.get(currentID).newCode, width - 1200, 8 + 30 - 4, paint);
    }

    void drawBarCodeBottom(Canvas canvasTemp){
        canvasTemp.drawRect(0, 0, width, height, rectBorderPaint);
        canvasTemp.drawRect(3, 3, width - 3, height - 3, rectBorderPaint);

        if (orderItems.get(currentID).newCode.startsWith("A") || (orderItems.get(currentID).barcode_str.endsWith("-0") && num == orderItems.get(currentID).num)) {
            String barcode = orderItems.get(currentID).barcode_str.substring(0, orderItems.get(currentID).barcode_str.indexOf("-"));
            Bitmap bitmapBarCode = BarCodeUtil.creatBarcode(barcode, width_barcode * 11 / 10, height_barcode * 2 - height_barText);

            canvasTemp.drawRect(400, height, 400 + width_barcode, height + height_barcode * 2, rectPaintGrey);
            canvasTemp.drawBitmap(bitmapBarCode, 400 - width_barcode * 1 / 20, height, null);
            paint.setTextSize(height_barText);
            canvasTemp.drawText(barcode, 400 + 50, height + height_barcode * 2 - height_barText / 10, paint);

            bitmapBarCode.recycle();
        }
        if(orderItems.get(currentID).newCode.startsWith("B")){
            int imageGroup = getImageGroup(orderItems.get(currentID).imgs.get(0));
            String imageGroupName = imageGroup == -1 ? "新图" : "图" + imageGroup + "";
            paint.setTextSize(height_barText * 5);
            canvasTemp.drawRect(400 + width_barcode + 20, height, 400 + width_barcode + 20 + height_barText * 10, height + height_barcode * 2, rectPaintRed);
            canvasTemp.drawText(imageGroupName + " #" + orderItems.get(currentID).sku, 400 + width_barcode + 20, height + height_barcode * 2 - height_barText * 5 / 10, paint);
        }
    }
    void drawBarCodeSide(Canvas canvasTemp){
        canvasTemp.drawRect(0, 0, width, height, rectBorderPaint);
        canvasTemp.drawRect(3, 3, width - 3, height - 3, rectBorderPaint);

        canvasTemp.rotate(-90);
        canvasTemp.translate(-height, width);

        if (orderItems.get(currentID).newCode.startsWith("A") || (orderItems.get(currentID).barcode_str.endsWith("-0") && num == orderItems.get(currentID).num)) {
            String barcode = orderItems.get(currentID).barcode_str.substring(0, orderItems.get(currentID).barcode_str.indexOf("-"));
            Bitmap bitmapBarCode = BarCodeUtil.creatBarcode(barcode, width_barcode * 11 / 10, height_barcode * 2 - height_barText);

            canvasTemp.drawRect(400, 0, 400 + width_barcode, 0 + height_barcode * 2, rectPaintGrey);
            canvasTemp.drawBitmap(bitmapBarCode, 400 - width_barcode * 1 / 20, 0, null);
            paint.setTextSize(height_barText);
            canvasTemp.drawText(barcode, 400 + 50, height_barcode * 2 - height_barText / 10, paint);

            bitmapBarCode.recycle();
        }
        if(orderItems.get(currentID).newCode.startsWith("B")){
            int imageGroup = getImageGroup(orderItems.get(currentID).imgs.get(0));
            String imageGroupName = imageGroup == -1 ? "新图" : "图" + imageGroup + "";
            paint.setTextSize(height_barText * 5);
            canvasTemp.drawRect(400 + width_barcode + 20, 0, 400 + width_barcode + 20 + height_barText * 10, height_barcode * 2, rectPaintRed);
            canvasTemp.drawText(imageGroupName + " #" + orderItems.get(currentID).sku, 400 + width_barcode + 20, height_barcode * 2 - height_barText * 5 / 10, paint);
        }
    }

    public void remixx(){
        if (orderItems.get(currentID).sizeStr.toLowerCase().contains("king")) {
            orderItems.get(currentID).sku = "HL5";
        } else if (orderItems.get(currentID).sizeStr.toLowerCase().contains("queen")) {
            orderItems.get(currentID).sku = "HL4";
        } else if (orderItems.get(currentID).sizeStr.toLowerCase().contains("twin")) {
            orderItems.get(currentID).sku = "HL3";
        } else if (orderItems.get(currentID).sizeStr.toLowerCase().contains("throw")) {
            orderItems.get(currentID).sku = "HL2";
        } else if (orderItems.get(currentID).sizeStr.toLowerCase().contains("crib")) {
            orderItems.get(currentID).sku = "HL1";
        }

        setSize();
        Bitmap bitmapTemp = null;

        if (orderItems.get(currentID).platform.endsWith("-jjj")) {
            //3,4需旋转
            //条码：1,3,5底部  2,4旁边

            if (orderItems.get(currentID).sku.equals("HL1") || orderItems.get(currentID).sku.equals("HL5")) {
                bitmapTemp = Bitmap.createBitmap(width, height + height_barcode * 2, Bitmap.Config.ARGB_8888);
            } else if(orderItems.get(currentID).sku.equals("HL2")){
                bitmapTemp = Bitmap.createBitmap(width + height_barcode * 2, height, Bitmap.Config.ARGB_8888);
            } else if(orderItems.get(currentID).sku.equals("HL3")){
                bitmapTemp = Bitmap.createBitmap(height + height_barcode * 2, width, Bitmap.Config.ARGB_8888);
            } else if(orderItems.get(currentID).sku.equals("HL4")){
                bitmapTemp = Bitmap.createBitmap(height, width + height_barcode * 2, Bitmap.Config.ARGB_8888);
            }
            Canvas canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);

            //draw原图
            Matrix matrix = new Matrix();
            matrix.postScale(width / 13800f, height / 15300f);
            if (orderItems.get(currentID).sku.equals("HL3")) {//旋转-90度
                matrix.postRotate(-90);
                matrix.postTranslate(0, width);
            } else if (orderItems.get(currentID).sku.equals("HL4")) {//旋转-90度
                matrix.postRotate(-90);
                matrix.postTranslate(0, width + height_barcode * 2);
            }
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);

            //将canvas坐标归位到原图左上角
            if (orderItems.get(currentID).sku.equals("HL3")) {
                canvasTemp.rotate(-90);
                canvasTemp.translate(-width, 0);
            } else if (orderItems.get(currentID).sku.equals("HL4")) {
                canvasTemp.rotate(-90);
                canvasTemp.translate(-width - height_barcode * 2, 0);
            }

            //画条码
            if (orderItems.get(currentID).sku.equals("HL1") || orderItems.get(currentID).sku.equals("HL3") || orderItems.get(currentID).sku.equals("HL5")) {
                drawBarCodeBottom(canvasTemp);
            } else {
                drawBarCodeSide(canvasTemp);
            }



        } else if (orderItems.get(currentID).platform.endsWith("-jj")) {
            Matrix matrix = new Matrix();
            if (MainActivity.instance.bitmaps.get(0).getWidth() == 13800) {//adam
                matrix.postScale(width / 13800f, height / 15300f);
            } else if (MainActivity.instance.bitmaps.get(0).getWidth() == 12000) {//4u2
                matrix.postScale(width / 12000f, height / 13000f);
            } else if (MainActivity.instance.bitmaps.get(0).getWidth() == 13500) {//jj
                matrix.postScale(width / 12000f, height / 13000f);
            }

            if (orderItems.get(currentID).sku.equals("HL3") || orderItems.get(currentID).sku.equals("HL4")) {
                matrix.postRotate(-90);
                matrix.postTranslate(0, width);
            }
            if (MainActivity.instance.bitmaps.get(0).getWidth() == 13800) {
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 13800, 15300, matrix, true);
            } else if (MainActivity.instance.bitmaps.get(0).getWidth() == 12000) {
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 12000, 13000, matrix, true);
            } else if (MainActivity.instance.bitmaps.get(0).getWidth() == 13500) {
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 750, 250, 12000, 13000, matrix, true);
            }

            Canvas canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));


            if (orderItems.get(currentID).sku.equals("HL3") || orderItems.get(currentID).sku.equals("HL4")) {
                canvasTemp.rotate(-90);
                canvasTemp.translate(-width, 0);
            }

            canvasTemp.drawRect(0, 0, width, height, rectBorderPaint);
            canvasTemp.drawRect(3, 3, width - 3, height - 3, rectBorderPaint);

            drawText(canvasTemp);
        } else {
            Matrix matrix = new Matrix();
            if (MainActivity.instance.bitmaps.get(0).getWidth() == 13800) {
                matrix.postScale(width / 13800f, height / 15300f);
            } else if (MainActivity.instance.bitmaps.get(0).getWidth() == 12000) {
                matrix.postScale(width / 12000f, height / 13000f);
            }

            if (orderItems.get(currentID).sku.equals("HL2") || orderItems.get(currentID).sku.equals("HL3") || orderItems.get(currentID).sku.equals("HL4")) {
                matrix.postRotate(-90);
                matrix.postTranslate(0, width);
            }
            if (MainActivity.instance.bitmaps.get(0).getWidth() == 13800) {
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 13800, 15300, matrix, true);
            } else if (MainActivity.instance.bitmaps.get(0).getWidth() == 12000) {
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 12000, 13000, matrix, true);
            }

            MainActivity.instance.bitmaps.clear();
            Canvas canvasTemp= new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));


            if (orderItems.get(currentID).sku.equals("HL2") || orderItems.get(currentID).sku.equals("HL3") || orderItems.get(currentID).sku.equals("HL4")) {
                canvasTemp.rotate(-90);
                canvasTemp.translate(-width, 0);
            }

            canvasTemp.drawRect(0, 0, width, height, rectBorderPaint);
            canvasTemp.drawRect(3, 3, width - 3, height - 3, rectBorderPaint);

            drawText(canvasTemp);
        }

        //填充透明背景为白色
        if (orderItems.get(currentID).platform.endsWith("jj") && MainActivity.instance.bitmaps.get(0).getWidth() == 13500) {
            for (int x = 0; x < bitmapTemp.getWidth(); x++) {
                for (int y = 0; y < bitmapTemp.getHeight(); y++) {
                    if (bitmapTemp.getPixel(x, y) == 0) {//透明点取值为0
                        bitmapTemp.setPixel(x, y, Color.WHITE);
                    }
                }
            }
        }


        try {
            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists()) {
                new File(pathSave).mkdirs();
            }
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapTemp, fileSave, dpi);


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

    void setSize(){
        switch (orderItems.get(currentID).sku) {
            case "HL1":
                dpi = 200;
                width = 9293;
                height = 10101;
                width_barcode = 472;
                height_barcode = 197;
                height_barText = 31;
                break;
            case "HL2":
                dpi = 200;
                width = 11405;
                height = 12397;
                width_barcode = 472;
                height_barcode = 197;
                height_barText = 31;
                break;
            case "HL3":
                dpi = 150;
                width = 11497;
                height = 13087;
                width_barcode = 354;
                height_barcode = 148;
                height_barText = 24;
                break;
            case "HL4":
                dpi = 138;
                width = 11306;
                height = 12719;
                width_barcode = 326;
                height_barcode = 136;
                height_barText = 22;
                break;
            case "HL5":
                dpi = 124;
                width = 11576;
                height = 12995;
                width_barcode = 293;
                height_barcode = 122;
                height_barText = 20;
                break;
            default:
                sizeOK = false;
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                break;
        }
    }

    int getImageGroup(String str){
        for (int i = 0; i < MainActivity.instance.hlPics.groups.size(); i++) {
            for (String pic : MainActivity.instance.hlPics.groups.get(i)) {
                if(str.contains(pic)){
                    return i + 1;
                }
            }
        }
        return -1;
    }

}
