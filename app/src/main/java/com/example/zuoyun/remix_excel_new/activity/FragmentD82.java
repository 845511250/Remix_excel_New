package com.example.zuoyun.remix_excel_new.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
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
 * Created by zuoyun on 2016/10/6.
 */

public class FragmentD82 extends BaseFragment {
    Context context;
    //    String sdCardPath = "/mnt/asec/share";
    String sdCardPath = "/storage/emulated/0/Pictures";
    ArrayList<OrderItem> orderItems;
    int currentID;
    String childPath;

    @BindView(R.id.iv_fg2_leftup)
    ImageView iv_leftup;
    @BindView(R.id.iv_fg2_leftdown)
    ImageView iv_leftdown;
    @BindView(R.id.iv_fg2_rightup)
    ImageView iv_rightup;
    @BindView(R.id.iv_fg2_rightdown)
    ImageView iv_rightdown;
    @BindView(R.id.bt_remix)
    Button bt_remix;
    @BindView(R.id.iv_sample1)
    ImageView iv_sample1;
    @BindView(R.id.iv_sample2)
    ImageView iv_sample2;
    @BindView(R.id.sb1)
    SeekBar sb1;
    @BindView(R.id.sbrotate1)
    SeekBar sbrotate1;
    @BindView(R.id.sb2)
    SeekBar sb2;
    @BindView(R.id.sbrotate2)
    SeekBar sbrotate2;

    int width, height, width_tongue, height_tongue;
    int num;
    String strPlus = "";
    int intPlus = 1;

    Paint paint,paintRed,paintBlue, rectPaint;
    String time = MainActivity.instance.orderDate_Print;

    @Override
    public int getLayout() {
        return R.layout.fragment1;
    }

    @Override
    public void initData(View view) {
        context = getContext();
        ButterKnife.bind(this, view);
        orderItems=MainActivity.instance.orderItems;
        currentID = MainActivity.instance.currentID;
        childPath = MainActivity.instance.childPath;

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(36);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(38);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        rectPaint = new Paint();
        rectPaint.setColor(0xffffffff);
        rectPaint.setStyle(Paint.Style.FILL);

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message,String sampleurl) {
                if(message==0){
                    iv_leftup.setImageDrawable(null);
                    iv_rightup.setImageDrawable(null);
                    Log.e("fragment1", "message0");
                }
                else if (message == MainActivity.LOADED_IMGS) {
                    bt_remix.setClickable(true);
                    if(!MainActivity.instance.cb_fastmode.isChecked()){
                        iv_leftup.setImageBitmap(MainActivity.instance.bitmaps.get(0));
                    }
                    checkremix();
                }
                else if (message==3){
                    bt_remix.setClickable(false);
                }
                else if (message == 10) {
                    remix();
                }
            }
        });

        //******************************************************************************************
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

    void drawTextRight(Canvas canvas, String LR) {
        canvas.drawRect(100, 530, 1490, 570, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).order_number, 100, 565, paint);
        canvas.drawText(orderItems.get(currentID).newCode.contains("-") ? orderItems.get(currentID).newCode : orderItems.get(currentID).sku + "-" + orderItems.get(currentID).size + "-" + orderItems.get(currentID).color, 700, 565, paint);
        canvas.drawText("生产尺寸:" + (orderItems.get(currentID).size - 1) + "码" + orderItems.get(currentID).color + LR, 1120, 565, paintRed);
    }
    void drawTextLeft(Canvas canvas, String LR) {
        canvas.drawRect(70, 530, 1490, 570, rectPaint);
        canvas.drawText("生产尺寸:" + (orderItems.get(currentID).size - 1) + "码" + orderItems.get(currentID).color + LR, 70, 565, paintRed);
        canvas.drawText( orderItems.get(currentID).newCode.contains("-") ? orderItems.get(currentID).newCode : orderItems.get(currentID).sku + "-" + orderItems.get(currentID).size + "-" + orderItems.get(currentID).color, 550, 565, paint);
        canvas.drawText(time + "  " + orderItems.get(currentID).order_number, 950, 565, paint);
    }
    void drawTextTongue(Canvas canvas, String LR) {
        canvas.drawRect(420, 1303 - 38, 420 + 160, 1303, rectPaint);
        canvas.drawText(orderItems.get(currentID).size + orderItems.get(currentID).color + LR, 420, 1303 - 4, paint);

        canvas.save();
        canvas.rotate(62.9f, 16, 895);
        canvas.drawRect(16, 895 - 38, 16 + 200, 895, rectPaint);
        canvas.drawText(time, 16, 895 - 4, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-61.8f, 867, 1088);
        canvas.drawRect(867, 1088 - 38, 867 + 266, 1088, rectPaint);
        canvas.drawText(orderItems.get(currentID).order_number, 867, 1088 - 4, paint);
        canvas.restore();
    }

    public void remixx(){
        setSize(orderItems.get(currentID).size);
        Bitmap bitmapDBLeft = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.de41left);
        Bitmap bitmapDBRight = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.de41right);
        Bitmap bitmapDBTongue = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.d82_tongue);

        int margin = 100;
        //bitmapCombine
        Bitmap bitmapCombine = Bitmap.createBitmap(width * 2 + margin, height * 2 + height_tongue, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
            if (MainActivity.instance.bitmaps.get(0).getWidth() != 3232) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 3232, 3232, true));
            }
            //LL
            Bitmap bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -83, -170, null);
            canvasTemp.drawBitmap(bitmapDBLeft, 0, 0, null);
            drawTextLeft(canvasTemp, "左外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //LR
            bitmapTemp = Bitmap.createBitmap(1525,652, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1625, -170, null);
            canvasTemp.drawBitmap(bitmapDBRight, 0, 0, null);
            drawTextRight(canvasTemp, "左内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            canvasCombine.drawBitmap(bitmapTemp, width + margin, 0, null);

            //RL
            bitmapTemp = Bitmap.createBitmap(1525, 652, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -83, -844, null);
            canvasTemp.drawBitmap(bitmapDBLeft,0,0,null);
            drawTextLeft(canvasTemp, "右内");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height, null);

            //RR
            bitmapTemp = Bitmap.createBitmap(1525,652, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1625, -844, null);
            canvasTemp.drawBitmap(bitmapDBRight, 0, 0, null);
            drawTextRight(canvasTemp, "右外");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);
            canvasCombine.drawBitmap(bitmapTemp, width + margin, height, null);

            //RTongue
            bitmapTemp = Bitmap.createBitmap(983,1322, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -344, -1764, null);
            canvasTemp.drawBitmap(bitmapDBTongue, 0, 0, null);
            drawTextTongue(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, width - width_tongue - margin, height * 2, null);

            //LTongue
            bitmapTemp = Bitmap.createBitmap(983,1322, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1795, -1764, null);
            canvasTemp.drawBitmap(bitmapDBTongue, 0, 0, null);
            drawTextTongue(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_tongue, height_tongue, true);
            canvasCombine.drawBitmap(bitmapTemp, width + margin + margin, height * 2, null);

            bitmapTemp.recycle();
            bitmapDBLeft.recycle();
            bitmapDBRight.recycle();
            bitmapDBTongue.recycle();
        }

        try {
            String printColor = orderItems.get(currentID).color.equals("黑") ? "B" : "W";
            String noNewCode = orderItems.get(currentID).newCode.equals("") ? orderItems.get(currentID).sku + orderItems.get(currentID).size + "_" : "";
            String nameCombine = noNewCode + orderItems.get(currentID).newCode + orderItems.get(currentID).color + "-" + orderItems.get(currentID).order_number + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 149);

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
        switch (size - 1) {
            case 34:
                width = 1310;
                height = 600;
                width_tongue = 896;
                height_tongue = 1161;
                break;
            case 35:
                width = 1341;
                height = 609;
                width_tongue = 913;
                height_tongue = 1190;
                break;
            case 36:
                width = 1380;
                height = 620;
                width_tongue = 930;
                height_tongue = 1219;
                break;
            case 37:
                width = 1414;
                height = 631;
                width_tongue = 944;
                height_tongue = 1247;
                break;
            case 38:
                width = 1449;
                height = 640;
                width_tongue = 961;
                height_tongue = 1274;
                break;
            case 39:
                width = 1485;
                height = 651;
                width_tongue = 977;
                height_tongue = 1302;
                break;
            case 40:
                width = 1521;
                height = 661;
                width_tongue = 993;
                height_tongue = 1330;
                break;
            case 41:
                width = 1557;
                height = 671;
                width_tongue = 1009;
                height_tongue = 1359;
                break;
            case 42:
                width = 1594;
                height = 683;
                width_tongue = 1025;
                height_tongue = 1388;
                break;
            case 43:
                width = 1629;
                height = 692;
                width_tongue = 1041;
                height_tongue = 1415;
                break;
            case 44:
                width = 1665;
                height = 702;
                width_tongue = 1057;
                height_tongue = 1444;
                break;
            case 45:
                width = 1701;
                height = 715;
                width_tongue = 1073;
                height_tongue = 1472;
                break;
            case 46:
                width = 1738;
                height = 724;
                width_tongue = 1089;
                height_tongue = 1500;
                break;
            case 47:
                width = 1774;
                height = 736;
                width_tongue = 1105;
                height_tongue = 1529;
                break;
            case 48:
                width = 1810;
                height = 746;
                width_tongue = 1121;
                height_tongue = 1557;
                break;
            case 49:
                width = 1846;
                height = 757;
                width_tongue = 1137;
                height_tongue = 1587;
                break;
        }
    }

    boolean checkContains(String nameContains){
        for (int i = 0; i < orderItems.get(currentID).imgs.size(); i++) {
            if (orderItems.get(currentID).imgs.get(i).contains(nameContains)) {
                return true;
            }
        }
        return false;
    }
    Bitmap getBitmapWith(String nameContains){
        for (int i = 0; i < orderItems.get(currentID).imgs.size(); i++) {
            if (orderItems.get(currentID).imgs.get(i).contains(nameContains)) {
                return MainActivity.instance.bitmaps.get(i);
            }
        }
        return null;
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
                tv_content.setText("单号："+order_number+"：图片大小需1567x804");
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
