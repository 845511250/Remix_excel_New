package com.example.zuoyun.remix_excel_new.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zuoyun.remix_excel_new.R;
import com.example.zuoyun.remix_excel_new.bean.Config;
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

public class FragmentFV extends BaseFragment {
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
        if (Config.FV_type == 0) {
            showDialogChoose();
        } else {
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
    }

    void drawText(Canvas canvas) {
        canvas.save();
        canvas.rotate(3.5f, 600, 4679);
        canvas.drawRect(600, 4679-18, 600+280, 4679, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).order_number, 600, 4679-2, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-3.5f, 950, 4692);
        canvas.drawRect(950, 4692-18, 950+280, 4692, rectPaint);
        canvas.drawText("左   " + orderItems.get(currentID).newCode, 950, 4692 - 2, paintRed);
        canvas.restore();

        canvas.save();
        canvas.rotate(3.5f, 600 + 1830, 4679);
        canvas.drawRect(600 + 1830, 4679-18, 600+280 + 1830, 4679, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).order_number, 600 + 1830, 4679-2, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-3.5f, 950 + 1830, 4692);
        canvas.drawRect(950 + 1830, 4692-18, 950+280 + 1830, 4692, rectPaint);
        canvas.drawText("右   " + orderItems.get(currentID).newCode, 950 + 1830, 4692 - 2, paintRed);
        canvas.restore();
    }
    void drawTextBlackBorder(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(0.8f, 600, 4787);
        canvas.drawRect(600, 4787-18, 600+280, 4787, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).order_number, 600, 4787 - 2, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-0.6f, 913, 4791);
        canvas.drawRect(913, 4791-18, 913+280, 4791, rectPaint);
        canvas.drawText(LR + "   " + orderItems.get(currentID).newCode, 913, 4791 - 2, paintRed);
        canvas.restore();

    }
    void drawText_jinjiang(Canvas canvas, String LR) {
        canvas.save();
        canvas.rotate(0.8f, 600, 4787);
        canvas.drawRect(600, 4787-18, 600+280, 4787, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).order_number, 600, 4787 - 2, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-0.6f, 913, 4791);
        canvas.drawRect(913, 4791-18, 913+280, 4791, rectPaint);
        canvas.drawText(LR + "   " + orderItems.get(currentID).newCode, 913, 4791 - 2, paintRed);
        canvas.restore();

    }

    public void remixx(){
        if (orderItems.get(currentID).platform.equals("testaprint")) {
            int width = 1789 + 35;//90dpi,35=1cm
            int height = 4803 + 35;

            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fv_blackborder);

            Bitmap bitmapCombine = Bitmap.createBitmap(height, width * 2 + 70, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            //left
            Bitmap bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 1789, 4803, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText_jinjiang(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);

            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            matrix.postTranslate(0, width);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //right
            bitmapTemp = Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(1), 1789, 4803, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawText_jinjiang(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width, height, true);

            matrix.postTranslate(0, width + 70);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            bitmapTemp.recycle();
            bitmapDB.recycle();

            //save
            File file = new File(sdCardPath + "/生产图/" + childPath + "/");
            if (!file.exists())
                file.mkdirs();

            String noNewCode = orderItems.get(currentID).newCode.equals("") ? orderItems.get(currentID).sku + "_" : "";
            String nameCombine = noNewCode + orderItems.get(currentID).newCode + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

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


        } else if (Config.FV_type == 1) {
            Bitmap bitmapCombine = Bitmap.createBitmap(1757 * 2 + 66, 4700 + 60, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            canvasCombine.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            canvasCombine.drawBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), 1757 + 66, 0, null);

            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fv2);
            canvasCombine.drawBitmap(bitmapDB, 0, 0, null);
            canvasCombine.drawBitmap(bitmapDB, 1757 + 66, 0, null);
            bitmapDB.recycle();
            drawText(canvasCombine);

            bitmapCombine = Bitmap.createScaledBitmap(bitmapCombine, 3720, 4940, true);

            File file = new File(sdCardPath + "/生产图/" + childPath + "/");
            if (!file.exists())
                file.mkdirs();

            String noNewCode = orderItems.get(currentID).newCode.equals("") ? orderItems.get(currentID).sku + "_" : "";
            String nameCombine = noNewCode + "_" + orderItems.get(currentID).newCode + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

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

        } else if (Config.FV_type == 2) {
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fv_blackborder);

            Bitmap bitmapCombine;
            bitmapCombine = Bitmap.createBitmap(1789, 4803, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.drawColor(0xffffffff);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            Rect rectCut = new Rect(0, 0, 1757, 4700);
            Rect rectDraw = new Rect(0, 0, 1789, 4803);
            canvasCombine.drawBitmap(MainActivity.instance.bitmaps.get(0), rectCut, rectDraw, null);
            canvasCombine.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBlackBorder(canvasCombine, "左");

            Matrix matrix = new Matrix();
            matrix.postRotate(180, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

            String noNewCode = orderItems.get(currentID).newCode.equals("") ? orderItems.get(currentID).sku + "_" : "";
            String nameCombine = noNewCode + "_" + orderItems.get(currentID).newCode + "_左_" + orderItems.get(currentID).color + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 90);

            //
            canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.drawColor(0xffffffff);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawBitmap(orderItems.get(currentID).imgs.size() == 1 ? MainActivity.instance.bitmaps.get(0) : MainActivity.instance.bitmaps.get(1), rectCut, rectDraw, null);
            canvasCombine.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBlackBorder(canvasCombine, "右");

            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

            nameCombine = noNewCode + "_" + orderItems.get(currentID).newCode + "_右_" + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 90);

            //释放bitmap
            bitmapDB.recycle();
            bitmapCombine.recycle();
        }



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

    private void showDialogChoose(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTransBackGround);
        final AlertDialog dialog_choose = builder.create();
        dialog_choose.setCancelable(false);
        dialog_choose.show();
        View view_dialog = LayoutInflater.from(context).inflate(R.layout.item_dialog_confirm, null);
        dialog_choose.setContentView(view_dialog);

        TextView tv_title_update = (TextView) view_dialog.findViewById(R.id.tv_dialog_title);
        TextView tv_content = (TextView) view_dialog.findViewById(R.id.tv_dialog_content);
        Button bt_yes = (Button) view_dialog.findViewById(R.id.bt_dialog_yes);
        Button bt_no = (Button) view_dialog.findViewById(R.id.bt_dialog_no);

        tv_title_update.setText("FV座椅套");
        tv_content.setText("请选择排版方式");
        bt_yes.setText("滚筒机");
        bt_no.setText("激光裁");

        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.FV_type = 1;
                dialog_choose.dismiss();
                remix();
            }
        });
        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config.FV_type = 2;
                dialog_choose.dismiss();
                remix();
            }
        });
    }


}
