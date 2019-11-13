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

public class FragmentGN extends BaseFragment {
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

    Paint rectPaint, paint, paintRed, paintBlue, rectBorderPaint, paintSmall;
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

        rectBorderPaint = new Paint();
        rectBorderPaint.setColor(0xff000000);
        rectBorderPaint.setStyle(Paint.Style.STROKE);
        rectBorderPaint.setStrokeWidth(2);

        paint = new Paint();
        paint.setColor(0xff000000);
        paint.setTextSize(26);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(26);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(26);
        paintBlue.setTypeface(Typeface.DEFAULT_BOLD);
        paintBlue.setAntiAlias(true);

        paintSmall = new Paint();
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(26);
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

                for(num=orderItems.get(currentID).num;num>=1;num--) {
                    for(int i=0;i<currentID;i++) {
                        if (orderItems.get(currentID).order_number.equals(orderItems.get(i).order_number)) {
                            intPlus += 1;
                        }
                    }
                    strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                    remixx100();
                    intPlus += 1;
                }

            }
        }.start();
    }

    void drawTextFrontL(Canvas canvas) {
        canvas.drawRect(100, 4445 - 25, 100 + 500, 4445, rectPaint);
        canvas.drawText(time + "  GA女浴袍_" + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 100, 4445 - 2, paint);
    }
    void drawTextFrontR(Canvas canvas) {
        canvas.drawRect(1300, 4445 - 25, 1300 + 500, 4445, rectPaint);
        canvas.drawText(time + "  GA女浴袍_" + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 1300, 4445 - 2, paint);
    }
    void drawTextBack(Canvas canvas) {
        canvas.drawRect(1300, 4394 - 25, 1300 + 500, 4394, rectPaint);
        canvas.drawText(time + "  GA女浴袍_" + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 1300, 4394 - 2, paint);
    }
    void drawTextArmL(Canvas canvas) {
        canvas.drawRect(1122, 12, 1122 + 40, 12 + 25, rectPaint);
        canvas.drawText(" 左", 1122, 12 + 23, paint);

        canvas.drawRect(1000, 2472 - 25, 1000 + 500, 2472, rectPaint);
        canvas.drawText("左" + "  GA女浴袍_  " + time + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 1000, 2472 - 2, paint);
    }
    void drawTextArmR(Canvas canvas) {
        canvas.drawRect(1122, 12, 1122 + 40, 12 + 25, rectPaint);
        canvas.drawText(" 右", 1122, 12 + 23, paint);

        canvas.drawRect(1000, 2472 - 25, 1000 + 500, 2472, rectPaint);
        canvas.drawText("右" + "  GA女浴袍_  " + time + "  " + orderItems.get(currentID).order_number + "  " + orderItems.get(currentID).newCode, 1000, 2472 - 2, paint);
    }
    void drawTextCollarBack(Canvas canvas) {
        canvas.drawRect(1400, 9, 1400 + 100, 9 + 25, rectPaint);
        canvas.drawText(" 后领", 1400, 9 + 23, paint);
    }
    void drawTextPocketL(Canvas canvas) {
        canvas.drawRect(360, 9, 360 + 300, 9 + 25, rectPaint);
        canvas.drawText(" 左 " + orderItems.get(currentID).order_number, 360, 9 + 23, paint);
    }
    void drawTextPocketR(Canvas canvas) {
        canvas.drawRect(360, 9, 360 + 300, 9 + 25, rectPaint);
        canvas.drawText(" 右 " + orderItems.get(currentID).order_number, 360, 9 + 23, paint);
    }

    public void remixx100(){
        Bitmap bitmapCombine = Bitmap.createBitmap(5781, 8607, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        Matrix matrix = new Matrix();
        if (orderItems.get(currentID).imgs.size() == 1) {
            //前口袋
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 573, 2634, 2156, 1034);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gn_qiankoudai);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //上口袋
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 630, 241, 2026, 1706);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gn_shangkoudai);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 1100, null);

            //bottom
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 305, 2321, 2663, 1430);
            canvasTemp = new Canvas(bitmapTemp);
//            drawTextBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 2910, null);

            //前面布
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 316, 420, 2660, 3603);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gn_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 4873, null);

            //side
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 788, 1947, 1725, 1804);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gn_side);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 475, 4436, null);
            canvasCombine.drawBitmap(bitmapTemp, 2850, 6550, null);

            //前袋上接布
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 558, 2327, 2168, 307);
            canvasTemp = new Canvas(bitmapTemp);
//            drawTextBack(canvasTemp);
            matrix.reset();
            matrix.postRotate(90);
            matrix.postTranslate(307 + 2350, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //侧链布1
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 222, 2660);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 239, 2723, true);
            canvasTemp = new Canvas(bitmapTemp);
//            drawTextBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2820, 0, null);

            //侧链布2
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 194, 0, 222, 2660);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 239, 2723, true);
            canvasTemp = new Canvas(bitmapTemp);
//            drawTextBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 3190, 0, null);

            //侧链布3
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2870, 0, 222, 2660);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 239, 2723, true);
            canvasTemp = new Canvas(bitmapTemp);
//            drawTextBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 3560, 0, null);

            //侧链布4
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3063, 0, 222, 2660);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 239, 2723, true);
            canvasTemp = new Canvas(bitmapTemp);
//            drawTextBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 3906, 0, null);

            //侧袋围1
            bitmapTemp = Bitmap.createBitmap(420, 3930, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 2556, 420, 1465);
            canvasTemp.drawBitmap(bitmapCut, 0, 1250, null);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(420, 3930);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);
            bitmapCut.recycle();
//            drawTextBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 4347, 0, null);

            //侧袋围2
            bitmapTemp = Bitmap.createBitmap(420, 3930, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2865, 2556, 420, 1465);
            canvasTemp.drawBitmap(bitmapCut, 0, 1250, null);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(420, 3930);
            canvasTemp.drawBitmap(bitmapCut, matrix, null);
            bitmapCut.recycle();
//            drawTextBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 4955, 0, null);

            //前袋围
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 3381, 3285, 268);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 4442, 268, true);
            canvasTemp = new Canvas(bitmapTemp);
//            drawTextBack(canvasTemp);
            matrix.reset();
            matrix.postRotate(90);
            matrix.postTranslate(268 + 5500, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //手提布
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1070, 778, 1161, 660);
            canvasTemp = new Canvas(bitmapTemp);
//            drawTextBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2980, 3180, null);

            //后片
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 316, 2003, 2670, 1748);
            canvasTemp = new Canvas(bitmapTemp);
//            drawTextBack(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2850, 4678, null);

            bitmapTemp.recycle();


        }

        try {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(-90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

            String nameCombine = "GN旅行包_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 149);
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
