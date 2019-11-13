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

public class FragmentJD extends BaseFragment {
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

    int width_up_front,width_up_back,width_down_front, width_down_back;
    int height_up_front,height_up_back,height_down_front, height_down_back;

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
                setScale(orderItems.get(currentID).sizeStr);
                if (sizeOK) {
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
            }
        }.start();

    }

    void drawTextUpFront(Canvas canvas) {
        canvas.drawRect(595, 5, 595 + 170, 5 + 19, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "-" + orderItems.get(currentID).sizeStr + "- " + orderItems.get(currentID).order_number, 595, 5 + 17, paint);
    }
    void drawTextUpBack(Canvas canvas) {
        canvas.save();
        canvas.rotate(-2.3f, 1328, 51);
        canvas.drawRect(1328, 51, 1328 + 300, 51 + 19, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "-" + orderItems.get(currentID).sizeStr + "- " + orderItems.get(currentID).order_number + "- " + time, 1328, 51 + 17, paint);
        canvas.restore();
    }
    void drawTextDownFront(Canvas canvas) {
        canvas.drawRect(1054, 1821 - 19, 1054 + 270, 1821, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "-" + orderItems.get(currentID).sizeStr + "- " + orderItems.get(currentID).order_number + "- " + time, 1054, 1821 - 2, paint);
    }
    void drawTextDownBack(Canvas canvas) {
        canvas.drawRect(1037, 1640 - 19, 1037 + 270, 1640, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "-" + orderItems.get(currentID).sizeStr + "- " + orderItems.get(currentID).order_number + "- " + time, 1037, 1640 - 2, paint);
    }

    public void remixx(){
        int margin = 100;
        Bitmap bitmapCombine = Bitmap.createBitmap(width_up_front + width_down_back + margin, height_up_front + height_down_back + margin, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 4) {
            //up_front
            Bitmap bitmapTemp = MainActivity.instance.bitmaps.get(3).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jd_up_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextUpFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_up_front, height_up_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //up_back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 4, 2, 2384, 840);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jd_up_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextUpBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_up_back, height_up_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_up_front + margin, 0, null);

            //down_front
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 42, 12, 2370, 1826);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jd_down_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextDownFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_down_front, height_down_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_up_front + margin, height_up_back + margin, null);

            //down_back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 60, 0, 2331, 1646);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jd_down_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextDownBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_down_back, height_down_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_up_front + margin, null);
            bitmapTemp.recycle();

        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //up_front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 2518, 1072);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jd_up_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextUpFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_up_front, height_up_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //up_back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4 + 63, 2 + 112, 2384, 840);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jd_up_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextUpBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_up_back, height_up_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_up_front + margin, 0, null);

            //down_front
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 42 + 38, 12 + 1072, 2370, 1826);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jd_down_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextDownFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_down_front, height_down_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_up_front + margin, height_up_back + margin, null);

            //down_back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 60+38, 0+1072, 2331, 1646);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jd_down_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextDownBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_down_back, height_down_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_up_front + margin, null);
            bitmapTemp.recycle();
        }


        try {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

            String nameCombine = orderItems.get(currentID).sku + "-" + orderItems.get(currentID).sizeStr + "-" + orderItems.get(currentID).order_number + strPlus + ".jpg";

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
        if (MainActivity.instance.tb_auto.isChecked()) {
            remix();
        }
    }

    void setScale(String size) {
        switch (size) {
            case "XS":
                width_up_front = 2165;
                height_up_front = 866;
                width_up_back = 2042;
                height_up_back = 672;
                width_down_front = 2088;
                height_down_front = 1686;
                width_down_back = 2088;
                height_down_back = 1505;
                break;
            case "S":
                width_up_front = 2282;
                height_up_front = 935;
                width_up_back = 2159;
                height_up_back = 731;
                width_down_front = 2206;
                height_down_front = 1745;
                width_down_back = 2206;
                height_down_back = 1564;
                break;
            case "M":
                width_up_front = 2400;
                height_up_front = 1004;
                width_up_back = 2276;
                height_up_back = 790;
                width_down_front = 2324;
                height_down_front = 1803;
                width_down_back = 2324;
                height_down_back = 1623;
                break;
            case "L":
                width_up_front = 2518;
                height_up_front = 1072;
                width_up_back = 2393;
                height_up_back = 848;
                width_down_front = 2442;
                height_down_front = 1862;
                width_down_back = 2442;
                height_down_back = 1681;
                break;
            case "XL":
                width_up_front = 2635;
                height_up_front = 1141;
                width_up_back = 2510;
                height_up_back = 907;
                width_down_front = 2561;
                height_down_front = 1921;
                width_down_back = 2560;
                height_down_back = 1740;
                break;
            case "2XL":
                width_up_front = 2753;
                height_up_front = 1210;
                width_up_back = 2628;
                height_up_back = 966;
                width_down_front = 2679;
                height_down_front = 1980;
                width_down_back = 2678;
                height_down_back = 1799;
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
