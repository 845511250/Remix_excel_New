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

public class FragmentJA extends BaseFragment {
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

    int width_front, width_back, width_sleeve, width_border, width_zipper;
    int height_front, height_back, height_sleeve, height_border, height_zipper;

    int num;
    String strPlus = "";
    int intPlus = 1;
    boolean sizeOK = true;

    Paint rectPaint, paint, paintRed, paintBlue, rectBorderPaint, paintSmall;
    String time;

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
        paint.setTextSize(19);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(20);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(20);
        paintBlue.setTypeface(Typeface.DEFAULT_BOLD);
        paintBlue.setAntiAlias(true);

        paintSmall = new Paint();
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(20);
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
                    if(!MainActivity.instance.cb_fastmode.isChecked())
                        iv_pillow.setImageBitmap(MainActivity.instance.bitmaps.get(0));
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
                setSize(orderItems.get(currentID).sizeStr);
                if (sizeOK) {
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
            }
        }.start();

    }

    void drawTextFront(Canvas canvas) {
        canvas.drawRect(1080, 4100 - 19, 1080 + 300, 4100, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "-" + orderItems.get(currentID).sizeStr + "- " + orderItems.get(currentID).order_number + "- " + time, 1080, 4100 - 2, paint);
    }
    void drawTextBack(Canvas canvas) {
        canvas.drawRect(1080, 4122 - 19, 1080 + 300, 4122, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "-" + orderItems.get(currentID).sizeStr + "- " + orderItems.get(currentID).order_number + "- " + time, 1080, 4122 - 2, paint);
    }

    void drawTextSleeve(Canvas canvas, String LR) {
        canvas.drawRect(829, 2546 - 19, 829 + 300, 2546, rectPaint);
        canvas.drawText(LR + "-" + orderItems.get(currentID).sku + "-" + orderItems.get(currentID).sizeStr + "- " + orderItems.get(currentID).order_number + "- " + time, 829, 2546 - 2, paint);
    }
    void drawTextZipper(Canvas canvas) {
        canvas.drawRect(145, 2064 - 19, 145 + 200, 2064, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "-" + orderItems.get(currentID).sizeStr + "- " + orderItems.get(currentID).order_number, 145, 2064 - 2, paint);
    }

    public void remixx(){
        int margin = 100;
        Bitmap bitmapCombine = Bitmap.createBitmap(Math.max(width_front + width_back + margin, width_sleeve * 2 + width_zipper + height_border + margin * 3) + +margin, height_front + Math.max(height_sleeve + margin, width_border), Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 5) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 36, 13, 2395, 4103);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ja_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //zipper
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 974, 59, 496, 2068);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ja_zipper);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextZipper(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_zipper, height_zipper, true);
            canvasCombine.drawBitmap(bitmapTemp, width_sleeve * 2 + margin * 2, height_front + margin, null);

            //back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 22, 5, 2384, 4126);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ja_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //sleeveR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(4), 17, 35, 1944, 2549);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ja_sleee_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //sleeveL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 17, 35, 1944, 2549);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ja_sleee_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, width_sleeve + margin, height_front + margin, null);

            //border
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, 2588, 294);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ja_border);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_border, height_border, true);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            matrix.postTranslate(height_border + width_sleeve * 2 + width_zipper + margin * 3, height_front);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 36 + 1976, 13 + 408, 2395, 4103);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ja_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //zipper
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 974 + 1976, 59 + 408, 496, 2068);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ja_zipper);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextZipper(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_zipper, height_zipper, true);
            canvasCombine.drawBitmap(bitmapTemp, width_sleeve * 2 + margin * 2, height_front + margin, null);

            //back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 22 + 1986, 5 + 295, 2384, 4126);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ja_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //sleeveR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 17, 35 + 214, 1944, 2549);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ja_sleee_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "右");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //sleeveL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 17 + 4424, 35 + 214, 1944, 2549);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ja_sleee_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSleeve(canvasTemp, "左");
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_sleeve, height_sleeve, true);
            canvasCombine.drawBitmap(bitmapTemp, width_sleeve + margin, height_front + margin, null);

            //border
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0 + 1906, 0, 2588, 294);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ja_border);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_border, height_border, true);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            matrix.postTranslate(height_border + width_sleeve * 2 + width_zipper + margin * 3, height_front);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();
        }


        try {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            Log.e("aaa", pathSave + nameCombine);
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 150);



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
        if (MainActivity.instance.tb_auto.isChecked()) {
            remix();
        }
    }

    void setSize(String size) {
        switch (size) {
            case "XS":
                width_front = 2214;
                height_front = 3917;
                width_back = 2193;
                height_back = 3897;
                width_sleeve = 1762;
                height_sleeve = 2473;
                width_border = 2353;
                height_border = 295;
                width_zipper = 496;
                height_zipper = 2068;
                break;
            case "S":
                width_front = 2332;
                height_front = 4036;
                width_back = 2311;
                height_back = 4015;
                width_sleeve = 1869;
                height_sleeve = 2528;
                width_border = 2471;
                height_border = 295;
                width_zipper = 496;
                height_zipper = 2068;
                break;
            case "M":
                width_front = 2449;
                height_front = 4153;
                width_back = 2429;
                height_back = 4133;
                width_sleeve = 1976;
                height_sleeve = 2584;
                width_border = 2589;
                height_border = 295;
                width_zipper = 496;
                height_zipper = 2068;
                break;
            case "L":
                width_front = 2567;
                height_front = 4271;
                width_back = 2547;
                height_back = 4251;
                width_sleeve = 2084;
                height_sleeve = 2640;
                width_border = 2707;
                height_border = 295;
                width_zipper = 496;
                height_zipper = 2186;
                break;
            case "XL":
                width_front = 2685;
                height_front = 4388;
                width_back = 2665;
                height_back = 4369;
                width_sleeve = 2191;
                height_sleeve = 2696;
                width_border = 2825;
                height_border = 295;
                width_zipper = 496;
                height_zipper = 2186;
                break;
            case "2XL":
                width_front = 2802;
                height_front = 4507;
                width_back = 2783;
                height_back = 4487;
                width_sleeve = 2298;
                height_sleeve = 2753;
                width_border = 2943;
                height_border = 295;
                width_zipper = 496;
                height_zipper = 2186;
                break;

            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
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
                tv_content.setText("单号："+order_number+"没有这个尺码");
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

}
