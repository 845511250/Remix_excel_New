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

public class FragmentKVKY extends BaseFragment {
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
    @BindView(R.id.tv_current_order_num)
    TextView tv_current_order_num;

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
        paint.setTextSize(16);
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
                    updateNum();
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

    void updateNum(){
        if (orderItems.get(currentID).num > 2) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_current_order_num.setText("当前单号进度：" + intPlus + "/" + orderItems.get(currentID).num);
                }
            });
        }
    }

    void drawTextKYS(Canvas canvas) {
        canvas.save();
        canvas.rotate(53.7f, 668, 719);
        canvas.drawRect(668, 719 - 15, 668 + 195, 719, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number + strPlus + " " + time, 668, 719 - 2, paint);
        canvas.restore();
    }
    void drawTextKYL(Canvas canvas) {
        canvas.save();
        canvas.rotate(50.2f, 730, 882);
        canvas.drawRect(730, 882 - 15, 730 + 195, 882, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number + strPlus + " " + time, 730, 882 - 2, paint);
        canvas.restore();
    }
    void drawTextKV(Canvas canvas) {
        canvas.save();
        canvas.rotate(50.6f, 897, 904);
        canvas.drawRect(897, 904 - 15, 897 + 200, 904, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).color + " " + orderItems.get(currentID).order_number + strPlus + " " + time, 897, 904 - 2, paint);
        canvas.restore();
    }

    void drawBigTextKV(Canvas canvas) {
        canvas.save();
        canvas.rotate(50.6f, 897, 904);
        canvas.drawRect(897, 904 - 15, 897 + 200, 904, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).color + orderItems.get(currentID).order_number + "共" + orderItems.get(currentID).newCode + "个" + strPlus + " " + time, 897, 904 - 2, paint);
        canvas.restore();

        if (orderItems.get(currentID).platform.equals("zy")) {
            paintRed.setTextSize(50);
            canvas.drawText(time + " " + orderItems.get(currentID).color + "耳挂", 1300, 970, paintRed);
            canvas.drawText(orderItems.get(currentID).order_number + " 共" + orderItems.get(currentID).newCode + "个", 1135, 1068, paintRed);
        }
    }
    void drawBigTextKYS(Canvas canvas) {
        canvas.save();
        canvas.rotate(53.7f, 668, 719);
        canvas.drawRect(668, 719 - 15, 668 + 195, 719, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).color + orderItems.get(currentID).order_number + "共" + orderItems.get(currentID).newCode + "个" + strPlus + " " + time, 668, 719 - 2, paint);
        canvas.restore();

        if (orderItems.get(currentID).platform.equals("zy")) {
            paintRed.setTextSize(30);
            canvas.drawText(time + " " + orderItems.get(currentID).color, 1100, 838, paintRed);
            canvas.drawText(orderItems.get(currentID).order_number + " 共" + orderItems.get(currentID).newCode + "个", 950, 885, paintRed);
        }
    }
    void drawBigTextKYL(Canvas canvas) {
        canvas.save();
        canvas.rotate(50.2f, 721, 872);
        canvas.drawRect(721, 872 - 15, 721 + 210, 872, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + orderItems.get(currentID).color + orderItems.get(currentID).order_number + "共" + orderItems.get(currentID).newCode + "个" + strPlus + " " + time, 721, 872 - 2, paint);
        canvas.restore();

        if (orderItems.get(currentID).platform.equals("zy")) {
            paintRed.setTextSize(32);
            canvas.drawText(orderItems.get(currentID).order_number + " 共" + orderItems.get(currentID).newCode + "个", 1050, 70, paintRed);
        }
    }

    public void remixx(){
        Bitmap bitmapCombine = null;
        Bitmap bitmapTemp = null;
        Bitmap bitmapDB = null;
        boolean blackBorder = false;
        boolean isJinJiang = false;

        if (new File("/storage/emulated/0/Movies/admin.txt").exists()) {
            blackBorder = false;
            isJinJiang = true;
        }

        if (orderItems.get(currentID).sku.equals("KY") || orderItems.get(currentID).sku.equals("KYL")) {
            orderItems.get(currentID).sku = "KYL";
            bitmapCombine = Bitmap.createBitmap(1410, 1050, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            if (MainActivity.instance.bitmaps.get(0).getWidth() < 1400) {
                bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 1410, 1050, true);
            } else if (MainActivity.instance.bitmaps.get(0).getWidth() == 1410) {
                bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            } else if (MainActivity.instance.bitmaps.get(0).getWidth() == 1500) {
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 45, 25, 1410, 1050);
            } else if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight() && MainActivity.instance.bitmaps.get(0).getWidth() == 2000) {//jj
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 295, 475, 1410, 1050);
            }

            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp.recycle();
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), blackBorder ? R.drawable.kyl_black_border : R.drawable.kyl);
            canvasCombine.drawBitmap(bitmapDB, 0, 0, null);
            if (orderItems.get(currentID).platform.equals("zy")) {
                drawBigTextKYL(canvasCombine);
            } else {
                drawTextKYL(canvasCombine);
            }
        } else if (orderItems.get(currentID).sku.equals("KYS")) {
            bitmapCombine = Bitmap.createBitmap(1300, 890, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            if (MainActivity.instance.bitmaps.get(0).getWidth() == 1500) {
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 49, 78, 1406, 963);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1300, 890, true);
            } else if(MainActivity.instance.bitmaps.get(0).getWidth() == 1800){
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 188, 0, 1424, 1098);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1300, 890, true);
            } else if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight() && MainActivity.instance.bitmaps.get(0).getWidth() == 2000) {//jj
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 350, 555, 1300, 890);
            } else if (MainActivity.instance.bitmaps.get(0).getWidth() == 1300) {
                bitmapTemp = MainActivity.instance.bitmaps.get(0).copy(Bitmap.Config.ARGB_8888, true);
            }
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp.recycle();
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), blackBorder ? R.drawable.kys_black_border : R.drawable.kys);
            canvasCombine.drawBitmap(bitmapDB, 0, 0, null);
            if (orderItems.get(currentID).platform.equals("zy")) {
                drawBigTextKYS(canvasCombine);
            } else {
                drawTextKYS(canvasCombine);
            }
        } else if (orderItems.get(currentID).sku.equals("KV") || orderItems.get(currentID).sku.equals("D8")) {
            if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight() && MainActivity.instance.bitmaps.get(0).getWidth() == 3859) {
                bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 97 - 15, 806 - 15, 3666 + 30, 2248 + 30);
                bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 1760, 1076, true);

                bitmapCombine = Bitmap.createBitmap(1760, 1076, Bitmap.Config.ARGB_8888);
                Canvas canvasCombine = new Canvas(bitmapCombine);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasCombine.drawColor(0xffffffff);
                canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
                bitmapTemp.recycle();

                bitmapDB = BitmapFactory.decodeResource(getResources(), blackBorder ? R.drawable.kv_black_border : R.drawable.kv);
                canvasCombine.drawBitmap(bitmapDB, 0, 0, null);
                if (orderItems.get(currentID).platform.equals("zy")) {
                    drawBigTextKV(canvasCombine);
                } else {
                    drawTextKV(canvasCombine);
                }

            } else if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
                if (MainActivity.instance.bitmaps.get(0).getWidth() == 4000) {
                    MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 1977, 1977, true));
                }
                bitmapCombine = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 108, 450, 1760, 1076);
                Canvas canvasCombine = new Canvas(bitmapCombine);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

                bitmapDB = BitmapFactory.decodeResource(getResources(), blackBorder ? R.drawable.kv_black_border : R.drawable.kv);
                canvasCombine.drawBitmap(bitmapDB, 0, 0, null);
                if (orderItems.get(currentID).platform.equals("zy")) {
                    drawBigTextKV(canvasCombine);
                } else {
                    drawTextKV(canvasCombine);
                }

            } else {
                bitmapCombine = Bitmap.createBitmap(1760, 1076, Bitmap.Config.ARGB_8888);
                Canvas canvasCombine = new Canvas(bitmapCombine);
                canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvasCombine.drawColor(0xffffffff);

                bitmapDB = BitmapFactory.decodeResource(getResources(), blackBorder ? R.drawable.kv_black_border : R.drawable.kv);

                if (isJinJiang) {
                    bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 1760, 1066, true);
                    canvasCombine.drawBitmap(bitmapTemp, 0, 4, null);
                    canvasCombine.drawBitmap(bitmapDB, 0, 0, null);
                    bitmapTemp.recycle();
                } else {
                    bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 20, 12, 1760, 1076);
                    canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
                    canvasCombine.drawBitmap(bitmapDB, 0, 0, null);
                    bitmapTemp.recycle();
                }
                if (orderItems.get(currentID).platform.equals("zy")) {
                    drawBigTextKV(canvasCombine);
                } else {
                    drawTextKV(canvasCombine);
                }
            }

        } else if (orderItems.get(currentID).sku.equals("KVF")) {
            bitmapCombine = Bitmap.createBitmap(1760, 1076, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 20, 12, 1760, 1076);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp.recycle();
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kv);
            canvasCombine.drawBitmap(bitmapDB, 0, 0, null);
            drawTextKV(canvasCombine);
        }

        bitmapCombine = Bitmap.createScaledBitmap(bitmapCombine, bitmapCombine.getWidth() + 20, bitmapCombine.getHeight() + 20, true);

        try {
            File file=new File(sdCardPath+"/生产图/"+childPath+"/");
            if(!file.exists())
                file.mkdirs();

            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";
            if (orderItems.get(currentID).platform.equals("zy")) {
                nameCombine = orderItems.get(currentID).sku + "(" + MainActivity.instance.orderDate_short + "-" + (currentID + 1) + ")_" + orderItems.get(currentID).order_number + strPlus + "_" + orderItems.get(currentID).color + "耳挂_共" + orderItems.get(currentID).newCode + "个" + ".jpg";
//                nameCombine = orderItems.get(currentID).sku + "_" + orderItems.get(currentID).order_number + strPlus + "_" + orderItems.get(currentID).color + "耳挂_共" + orderItems.get(currentID).newCode + "个" + ".jpg";
            }

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
            bitmapDB.recycle();
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
