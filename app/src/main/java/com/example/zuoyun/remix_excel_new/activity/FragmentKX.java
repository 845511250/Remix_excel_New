package com.example.zuoyun.remix_excel_new.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
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

public class FragmentKX extends BaseFragment {
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
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(26);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(26);
        paintBlue.setAntiAlias(true);

        paintSmall = new Paint();
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(26);
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

    void drawTextFront(Canvas canvas) {
        canvas.drawRect(435, 2772 - 25, 435 + 500, 2772, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "前片 " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 435, 2772 - 3, paint);
    }
    void drawTextBottom(Canvas canvas) {
        canvas.drawRect(167, 29 - 25, 167 + 500, 29, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "底片 " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 167, 29 - 3, paint);
    }
    void drawTextSide1Left(Canvas canvas) {
        canvas.save();
        canvas.rotate(90f, 799, 114);
        canvas.drawRect(799, 114 - 25, 799 + 500, 114, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "侧片 " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 799, 114 - 3, paint);
        canvas.restore();
    }
    void drawTextSide1Right(Canvas canvas) {
        canvas.save();
        canvas.rotate(-90f, 28, 623);
        canvas.drawRect(28, 623 - 25, 28 + 500, 623, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "侧片 " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 28, 623 - 3, paint);
        canvas.restore();
    }
    void drawTextSide2Left(Canvas canvas) {
        canvas.save();
        canvas.rotate(90f, 917, 226);
        canvas.drawRect(917, 226 - 25, 917 + 500, 226, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "侧片 " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 917, 226 - 3, paint);
        canvas.restore();
    }
    void drawTextSide2Right(Canvas canvas) {
        canvas.save();
        canvas.rotate(-90f, 28, 725);
        canvas.drawRect(28, 725 - 25, 28 + 500, 725, rectPaint);
        canvas.drawText(orderItems.get(currentID).sku + "侧片 " + time + " " + orderItems.get(currentID).order_number + " " + orderItems.get(currentID).newCode_short, 28, 725 - 3, paint);
        canvas.restore();
    }

    public void remixx(){
        Bitmap bitmapCombine = Bitmap.createBitmap(5570, 4218, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        Matrix matrix = new Matrix();
        if (orderItems.get(currentID).imgs.size() == 1) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 991, 955, 1919, 2777);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //back_left
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1216, 1016, 711, 2716);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_back_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextRight(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 4847, 1007, null);

            //back_right
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1975, 1015, 711, 2716);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_back_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextRight(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 4116, 0, null);

            //side_left1
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 46, 944, 827, 1565);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_side1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1Left(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2032, 2641, null);

            //side_right1
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3028, 986, 827, 1565);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1Right(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 3118, 2641, null);

            //side_left2
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 46, 2509, 945, 1211);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_side2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Left(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 1973, 0, null);

            //side_right2
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2910, 2551, 945, 1211);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Right(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 3058, 0, null);

            //bottom
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1026, 3744, 1848, 945);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_bottom);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBottom(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2015, 1387, null);

            //top1
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1331, 11, 1240, 650);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_top1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextRight(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 219, 2919, null);

            //top2
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1508, 188, 887, 558);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_top2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextRight(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 392, 3648, null);

            //top3
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1331, 646, 1240, 295);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_top3);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextRight(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 4185, 3910, null);


            bitmapDB.recycle();
            bitmapTemp.recycle();


        } else if (orderItems.get(currentID).imgs.size() == 6) {
            //front
            Bitmap bitmapTemp = MainActivity.instance.bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //back_left
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 711, 2716);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_back_left);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextRight(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 4847, 1007, null);

            //back_right
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 758, 0, 711, 2716);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_back_right);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextRight(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 4116, 0, null);

            //side_left1
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, 827, 1565);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_side1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1Left(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2032, 2641, null);

            //side_right1
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(4), 116, 0, 827, 1565);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide1Right(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 3118, 2641, null);

            //side_left2
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 0, 1565, 943, 1211);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_side2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Left(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 1973, 0, null);

            //side_right2
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(4), 0, 1565, 943, 1211);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextSide2Right(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 3058, 0, null);

            //bottom
            bitmapTemp = MainActivity.instance.bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_bottom);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBottom(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 2015, 1387, null);

            //top1
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, 1240, 650);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_top1);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextRight(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 219, 2919, null);

            //top2
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(5), 176, 177, 887, 558);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_top2);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextRight(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 392, 3648, null);

            //top3
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(5), 0, 635, 1240, 295);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.kx_top3);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
//            drawTextRight(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 4185, 3910, null);


            bitmapDB.recycle();
            bitmapTemp.recycle();


        }



        try {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(-90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

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

}
