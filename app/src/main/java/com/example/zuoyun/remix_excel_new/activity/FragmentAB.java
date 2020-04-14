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

import static com.example.zuoyun.remix_excel_new.activity.MainActivity.recycleExcelImages;

/**
 * Created by zuoyun on 2016/11/4.
 */

public class FragmentAB extends BaseFragment {
    Context context;
//    String sdCardPath = "/mnt/asec/share";
String sdCardPath = "/storage/emulated/0/Pictures";
    ArrayList<OrderItem> orderItems;
    int currentID;int nothing;
    String childPath;
    
    @BindView(R.id.bt_remix)
    Button bt_remix;
    @BindView(R.id.iv_pillow)
    ImageView iv_pillow;

    Paint paint, paintRed, rectPaint;
    String time;

    float scaleX=1.0f, scaleY = 1.0f;
    int num;
    String strPlus = "";
    int intPlus = 1;
    
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
        paint.setTextSize(30);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(30);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;


        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                } else if (message == MainActivity.LOADED_IMGS) {
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
                    intPlus = orderItems.get(currentID).num - num + 1;
                    for(int i=0;i<currentID;i++) {
                        if (orderItems.get(currentID).order_number.equals(orderItems.get(i).order_number)) {
                            intPlus += 1;
                        }
                    }
                    strPlus = intPlus == 1 ? "" : "(" + intPlus + ")";
                    remixx();
                }
            }
        }.start();

    }

    public void remixx(){

        Bitmap bitmapCombine = null;
        String intSize = orderItems.get(currentID).sizeStr;
        if (orderItems.get(currentID).sizeStr.equals("S")) {
            if (orderItems.get(currentID).skuStr.startsWith("ABM")||orderItems.get(currentID).skuStr.startsWith("M")) {
                intSize = "7";
            } else {
                intSize = "5";
            }
        } else if (orderItems.get(currentID).sizeStr.equals("M")) {
            if (orderItems.get(currentID).skuStr.startsWith("ABM")||orderItems.get(currentID).skuStr.startsWith("M")) {
                intSize = "9";
            } else {
                intSize = "7";
            }
        } else if (orderItems.get(currentID).sizeStr.equals("L")) {
            if (orderItems.get(currentID).skuStr.startsWith("ABM")||orderItems.get(currentID).skuStr.startsWith("M")) {
                intSize = "11";
            } else {
                intSize = "9";
            }
        }

        if (!orderItems.get(currentID).platform.equals("4u2")) {
            MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 1241, 1603, true));
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ds);

            bitmapCombine = Bitmap.createBitmap(1241+59, 1603+59, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);
            canvasCombine.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            canvasCombine.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();

            String gender = (orderItems.get(currentID).skuStr.startsWith("ABM")||orderItems.get(currentID).skuStr.startsWith("M")) ? "男" : "女";

            canvasCombine.drawText(gender + intSize + "黑", 563, 1599, paint);
            canvasCombine.save();
            canvasCombine.rotate(90, 10, 1000);
            canvasCombine.drawText(time + "  " + orderItems.get(currentID).order_number, 10, 996, paint);
            canvasCombine.drawText("流水号：" + (currentID + 1), 300, 996, paintRed);
            canvasCombine.restore();

            setScale(gender + intSize);
            bitmapCombine = Bitmap.createScaledBitmap(bitmapCombine, (int) ((1241 + 59) * scaleX), (int) ((1603 + 59) * scaleY), true);

        } else if (orderItems.get(currentID).imgs.size() == 2) {//分片定制
            if (MainActivity.instance.bitmaps.get(0).getWidth() != 627) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 627, 1603, true));
                MainActivity.instance.bitmaps.set(1, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(1), 627, 1603, true));
            }
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ds);

            bitmapCombine = Bitmap.createBitmap(1241 + 59, 1603 + 59, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);

            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 17, 20, 586, 1569);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 609, 1592, true);
            canvasCombine.drawBitmap(bitmapTemp, 6, 7, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 22, 19, 586, 1569);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 609, 1592, true);
            canvasCombine.drawBitmap(bitmapTemp, 629, 7, null);
            bitmapTemp.recycle();

            canvasCombine.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();

            String gender = (orderItems.get(currentID).skuStr.startsWith("ABM")||orderItems.get(currentID).skuStr.startsWith("M")) ? "男" : "女";

            canvasCombine.drawText(gender + intSize + "黑", 563, 1599, paint);
            canvasCombine.save();
            canvasCombine.rotate(90, 10, 1000);
            canvasCombine.drawText(time + "  " + orderItems.get(currentID).order_number, 10, 996, paint);
            canvasCombine.drawText("流水号：" + (currentID + 1), 300, 996, paintRed);
            canvasCombine.restore();

            setScale(gender + intSize);
            bitmapCombine = Bitmap.createScaledBitmap(bitmapCombine, (int) ((1241 + 59) * scaleX), (int) ((1603 + 59) * scaleY), true);

        } else if (orderItems.get(currentID).imgs.size() == 1) {//批量定制
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ds);
            bitmapCombine = Bitmap.createBitmap(1241 + 59, 1603 + 59, Bitmap.Config.ARGB_8888);
            Canvas canvasCombine = new Canvas(bitmapCombine);
            canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasCombine.drawColor(0xffffffff);
            canvasCombine.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            canvasCombine.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();

            String gender = (orderItems.get(currentID).skuStr.startsWith("ABM")||orderItems.get(currentID).skuStr.startsWith("M")) ? "男" : "女";

            canvasCombine.drawText(gender + intSize + "黑", 563, 1599, paint);
            canvasCombine.save();
            canvasCombine.rotate(90, 10, 1000);
            canvasCombine.drawText(time + "  " + orderItems.get(currentID).order_number, 10, 996, paint);
            canvasCombine.drawText("流水号：" + (currentID + 1), 300, 996, paintRed);
            canvasCombine.restore();

            setScale(gender + intSize);
            bitmapCombine = Bitmap.createScaledBitmap(bitmapCombine, (int) ((1241 + 59) * scaleX), (int) ((1603 + 59) * scaleY), true);
        }


        try {

            String printColor = "B";
            String nameCombine;
            if (orderItems.get(currentID).platform.equals("4u2")) {
                String gender = (orderItems.get(currentID).skuStr.startsWith("ABM")||orderItems.get(currentID).skuStr.startsWith("M")) ? "男" : "女";
                nameCombine = orderItems.get(currentID).sku + gender + intSize + printColor + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";
            } else {
                String gender = (orderItems.get(currentID).skuStr.startsWith("ABM")||orderItems.get(currentID).skuStr.startsWith("M")) ? "男" : "女";
                nameCombine = orderItems.get(currentID).sku + gender + intSize + printColor + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";
            }

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 148);

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
            Label label0 = new Label(0, currentID+1, orderItems.get(currentID).order_number+orderItems.get(currentID).sku + orderItems.get(currentID).sizeStr + printColor);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID+1, orderItems.get(currentID).sku + orderItems.get(currentID).sizeStr + printColor);
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
            Log.e("aaa", e.getMessage());
        }
        if (num == 1) {
            recycleExcelImages();
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
    void setScale(String size){
        switch (size) {
            case "男7":
                scaleX = 1.0541f;
                scaleY = 1.03f;
                break;
            case "男8":
                scaleX = 1.0f;
                scaleY = 1.0f;
                break;
            case "男9":
                scaleX = 1.088f;
                scaleY = 1.074f;
                break;
            case "男10":
                scaleX = 1.088f;
                scaleY = 1.074f;
                break;
            case "男11":
                scaleX = 1.161f;
                scaleY = 1.147f;
                break;
            case "男12":
                scaleX = 1.161f;
                scaleY = 1.147f;
                break;
            case "女5":
                scaleX = 1.011f;
                scaleY = 0.938f;
                break;
            case "女6":
                scaleX = 1.011f;
                scaleY = 0.938f;
                break;
            case "女7":
                scaleX = 1.049f;
                scaleY = 0.998f;
                break;
            case "女8":
                scaleX = 1.049f;
                scaleY = 0.998f;
                break;
            case "女9":
                scaleX = 1.098f;
                scaleY = 1.065f;
                break;
            case "女10":
                scaleX = 1.098f;
                scaleY = 1.065f;
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                break;
        }
    }

    public void showDialogSizeWrong(final String order_number){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //-----------
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
                //-----------
            }
        });
    }

}
