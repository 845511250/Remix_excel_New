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

public class FragmentKO extends BaseFragment {
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

    Paint rectPaint, paint, paintRed, paintBlue, rectBorderPaint, paintSmall;
    String time;

    int width,height;

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

        rectBorderPaint = new Paint();
        rectBorderPaint.setColor(0xff000000);
        rectBorderPaint.setStyle(Paint.Style.STROKE);
        rectBorderPaint.setStrokeWidth(4);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(21);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(21);
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

    void drawText(Canvas canvas, String number) {
        canvas.save();
        canvas.rotate(-1, 2349, 15);
        canvas.drawRect(2349, 15, 2349 + 400, 15 + 20, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).order_number + number + orderItems.get(currentID).color + "  " + orderItems.get(currentID).newCode_short, 2349, 15 + 18, paint);
        canvas.restore();

        canvas.drawRect(1343, 1070 - 20, 1343 + 400, 1070, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).order_number + number + orderItems.get(currentID).color + orderItems.get(currentID).newCode_short, 1343, 1070 - 2, paint);

        canvas.drawRect(1343, 8178 - 20, 1343 + 400, 8178, rectPaint);
        canvas.drawText(time + " " + orderItems.get(currentID).order_number + number + orderItems.get(currentID).color + orderItems.get(currentID).newCode_short, 1343, 8178 - 2, paint);
    }

    public void remixx(){

        //1
        width = 3071;
        height = 4264;
        Bitmap bitmapCombine = Bitmap.createBitmap(width, height * 2, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        Bitmap bitmapTemp = null;
        if (orderItems.get(currentID).imgs.size() == 3) {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 14, 17, 3071, 4264);
        } else {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 14, 17, 3071, 4264);
        }
        canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
        Bitmap bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.ko_main_old);
        canvasCombine.drawBitmap(bitmapDB, 0, 0, null);

        Matrix matrix = new Matrix();
        matrix.postRotate(180);
        matrix.postTranslate(width, height * 2);
        canvasCombine.drawBitmap(bitmapTemp, matrix, null);
        canvasCombine.drawBitmap(bitmapDB, matrix, null);

        if (orderItems.get(currentID).imgs.size() == 3) {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1133, 177, 832, 776);
        } else {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1133, 177, 832, 776);
        }
        canvasCombine.drawBitmap(bitmapTemp, 1133, 300, null);
        bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.ko_pocket2);
        canvasCombine.drawBitmap(bitmapDB, 1133, 300, null);

        if(orderItems.get(currentID).imgs.size()==3) {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1133, 18, 832, 935);
        }
        else {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1133, 18, 832, 935);
        }
        canvasCombine.drawBitmap(bitmapTemp, 1133, 7250, null);
        bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.ko_pocket1);
        canvasCombine.drawBitmap(bitmapDB, 1133, 7250, null);
        bitmapTemp.recycle();
        bitmapDB.recycle();

        drawText(canvasCombine,"(3-1)");

        String nameCombine = orderItems.get(currentID).nameStr + "(3_1)" + strPlus + ".jpg";
        String pathSave;
        if(MainActivity.instance.cb_classify.isChecked()){
            pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
        } else
            pathSave = sdCardPath + "/生产图/" + childPath + "/";
        if(!new File(pathSave).exists()) {
            new File(pathSave).mkdirs();
        }
        File fileSave = new File(pathSave + nameCombine);
        BitmapToJpg.save(bitmapCombine, fileSave, 149);
        bitmapCombine.recycle();

        //2
        bitmapCombine = Bitmap.createBitmap(width, height * 2, Bitmap.Config.ARGB_8888);
        canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if(orderItems.get(currentID).imgs.size()==3) {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 14, 17, 3071, 4264);
        }
        else {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 14 + 3100, 17, 3071, 4264);
        }
        canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
        bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.ko_main_old);
        canvasCombine.drawBitmap(bitmapDB, 0, 0, null);

        matrix = new Matrix();
        matrix.postRotate(180);
        matrix.postTranslate(width, height * 2);
        canvasCombine.drawBitmap(bitmapTemp, matrix, null);
        canvasCombine.drawBitmap(bitmapDB, matrix, null);

        if (orderItems.get(currentID).imgs.size() == 3) {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1133, 177, 832, 776);
        } else {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1133 + 3100, 177, 832, 776);
        }
        canvasCombine.drawBitmap(bitmapTemp, 1133, 300, null);
        bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.ko_pocket2);
        canvasCombine.drawBitmap(bitmapDB, 1133, 300, null);

        if (orderItems.get(currentID).imgs.size() == 3) {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1133, 18, 832, 935);
        } else {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1133 + 3100, 18, 832, 935);
        }
        canvasCombine.drawBitmap(bitmapTemp, 1133, 7250, null);
        bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.ko_pocket1);
        canvasCombine.drawBitmap(bitmapDB, 1133, 7250, null);
        bitmapTemp.recycle();
        bitmapDB.recycle();

        drawText(canvasCombine,"(3-2)");

        nameCombine = orderItems.get(currentID).nameStr + "(3_2)" + strPlus + ".jpg";
        fileSave = new File(pathSave + nameCombine);
        BitmapToJpg.save(bitmapCombine, fileSave, 149);
        bitmapCombine.recycle();

        //3
        bitmapCombine = Bitmap.createBitmap(width, height * 2, Bitmap.Config.ARGB_8888);
        canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 3) {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 14, 17, 3071, 4264);
        } else {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 14 + 3100 * 2, 17, 3071, 4264);
        }
        canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
        bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.ko_main_old);
        canvasCombine.drawBitmap(bitmapDB, 0, 0, null);

        matrix = new Matrix();
        matrix.postRotate(180);
        matrix.postTranslate(width, height * 2);
        canvasCombine.drawBitmap(bitmapTemp, matrix, null);
        canvasCombine.drawBitmap(bitmapDB, matrix, null);

        if (orderItems.get(currentID).imgs.size() == 3) {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 1133, 177, 832, 776);
        } else {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1133 + 3100 * 2, 177, 832, 776);
        }
        canvasCombine.drawBitmap(bitmapTemp, 1133, 300, null);
        bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.ko_pocket2);
        canvasCombine.drawBitmap(bitmapDB, 1133, 300, null);

        if (orderItems.get(currentID).imgs.size() == 3) {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 1133, 18, 832, 935);
        } else {
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1133 + 3100 * 2, 18, 832, 935);
        }
        canvasCombine.drawBitmap(bitmapTemp, 1133, 7250, null);
        bitmapDB = BitmapFactory.decodeResource(getResources(), R.drawable.ko_pocket1);
        canvasCombine.drawBitmap(bitmapDB, 1133, 7250, null);
        bitmapTemp.recycle();
        bitmapDB.recycle();

        drawText(canvasCombine,"(3-3)");

        nameCombine = orderItems.get(currentID).nameStr + "(3_3)" + strPlus + ".jpg";
        fileSave = new File(pathSave + nameCombine);
        BitmapToJpg.save(bitmapCombine, fileSave, 149);
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
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku);
            sheet.addCell(label1);
            int num = orderItems.get(currentID).num;
            Number number2 = new Number(2, currentID + 1, num);
            sheet.addCell(number2);
            Label label3 = new Label(3, currentID + 1, orderItems.get(currentID).customer);
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


}
