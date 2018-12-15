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

public class FragmentGE extends BaseFragment {
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

    int width_front,width_back,width_down_front,width_down_back,width_strap;
    int height_front,height_back,height_down_front,height_down_back,height_strap;

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
        paint.setTextSize(25);
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

    void drawTextFront(Canvas canvas) {
        canvas.drawRect(1000, 4370-25, 1000+500, 4370, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + "   " + time + "  " + orderItems.get(currentID).order_number, 1000, 4370 - 2, paint);
    }
    void drawTextBack(Canvas canvas) {
        canvas.drawRect(1000, 4164-25, 1000+500, 4164, rectPaint);
        canvas.drawText( orderItems.get(currentID).sizeStr + "   " + time + "  " + orderItems.get(currentID).order_number, 1000, 4164 - 2, paint);
    }
    void drawTextDownFront(Canvas canvas) {
        canvas.drawRect(300, 439 - 25, 300 + 200, 439, rectPaint);
        canvas.drawText("前 " + orderItems.get(currentID).order_number, 300, 439 - 2, paint);
    }
    void drawTextDownBack(Canvas canvas) {
        canvas.drawRect(305, 358 - 25, 305 + 200, 358, rectPaint);
        canvas.drawText("后 " + orderItems.get(currentID).order_number, 305, 358 - 2, paint);
    }

    public void remixx(){
        int margin = 120;

        Bitmap bitmapCombine = Bitmap.createBitmap(width_front + width_back + width_strap + margin * 2, height_front + height_down_front + margin, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        Bitmap bitmapF = MainActivity.instance.bitmaps.get(0);
        Bitmap bitmapB = orderItems.get(currentID).imgs.size() == 1 ? bitmapF : MainActivity.instance.bitmaps.get(1);

        //前
        Bitmap bitmapTemp = Bitmap.createBitmap(bitmapF, 0, 578, 1760, 2200);
        Canvas canvasTemp = new Canvas(bitmapTemp);
        Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ge_front);
        canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
        bitmapDB.recycle();
//        drawTextFront(canvasTemp);
        bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
        canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
        bitmapTemp.recycle();

        //后面
        bitmapTemp = Bitmap.createBitmap(bitmapB, 0, 460, 1859, 1920);
        canvasTemp = new Canvas(bitmapTemp);
        bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ge_back);
        canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
        bitmapDB.recycle();
//        drawTextBack(canvasTemp);
        bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
        canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);
        bitmapTemp.recycle();

        //左肩带
        Bitmap bitmapArm = Bitmap.createBitmap(225, 1036, Bitmap.Config.ARGB_8888);
        Canvas canvasArm= new Canvas(bitmapArm);
        canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasArm.drawColor(0xffffffff);

        bitmapTemp = Bitmap.createBitmap(bitmapB, 405, 0, 225, 500);
        Matrix matrix = new Matrix();
        matrix.postRotate(180);
        matrix.postTranslate(225, 500);
        canvasArm.drawBitmap(bitmapTemp, matrix, null);

        bitmapTemp = Bitmap.createBitmap(bitmapF, 1178, 82, 225, 536);
        canvasArm.drawBitmap(bitmapTemp, 0, 500, null);
        bitmapTemp.recycle();

        bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_strap, height_strap, true);
        canvasCombine.drawBitmap(bitmapArm, width_front + width_back + margin * 2, 0, null);
        bitmapArm.recycle();

        //右肩带
        bitmapArm = Bitmap.createBitmap(225, 1036, Bitmap.Config.ARGB_8888);
        canvasArm= new Canvas(bitmapArm);
        canvasArm.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasArm.drawColor(0xffffffff);

        bitmapTemp = Bitmap.createBitmap(bitmapB, 1233, 0, 225, 500);
        matrix = new Matrix();
        matrix.postRotate(180);
        matrix.postTranslate(225, 500);
        canvasArm.drawBitmap(bitmapTemp, matrix, null);

        bitmapTemp = Bitmap.createBitmap(bitmapF, 353, 82, 225, 536);
        canvasArm.drawBitmap(bitmapTemp, 0, 500, null);
        bitmapTemp.recycle();

        bitmapArm = Bitmap.createScaledBitmap(bitmapArm, width_strap, height_strap, true);
        canvasCombine.drawBitmap(bitmapArm, width_front + width_back + margin * 2, height_strap + margin, null);
        bitmapArm.recycle();

        //前下片
        bitmapTemp = Bitmap.createBitmap(bitmapF, 472, 2311, 807, 443);
        canvasTemp = new Canvas(bitmapTemp);
        bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ge_down_front);
        canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
        bitmapDB.recycle();
        drawTextDownFront(canvasTemp);
        bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_down_front, height_down_front, true);
        canvasCombine.drawBitmap(bitmapTemp, 0, height_front + 60, null);
        bitmapTemp.recycle();

        //后下片
        bitmapTemp = Bitmap.createBitmap(bitmapB, 506, 2018, 844, 362);
        canvasTemp = new Canvas(bitmapTemp);
        bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.ge_down_back);
        canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
        bitmapDB.recycle();
        drawTextDownBack(canvasTemp);
        bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_down_back, height_down_back, true);
        canvasCombine.drawBitmap(bitmapTemp, 2000, height_front + 60, null);
        bitmapTemp.recycle();




        try {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

            String nameCombine = "女连体衣 " + orderItems.get(currentID).sizeStr + "_" + orderItems.get(currentID).order_number + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
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
            Label label3 = new Label(3, currentID+1, "小左");
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

    void setScale(String size) {
        switch (size) {
            case "S":
                width_front = 2636;
                height_front = 3486;
                width_back = 2735;
                height_back = 3731;
                width_down_front = 1191;
                height_down_front = 687;
                width_down_back = 1309;
                height_down_back = 687;
                width_strap = 391;
                height_strap = 1691;
                break;
            case "M":
                width_front = 2755;
                height_front = 3609;
                width_back = 2856;
                height_back = 3855;
                width_down_front = 1191;
                height_down_front = 687;
                width_down_back = 1336;
                height_down_back = 687;
                width_strap = 391;
                height_strap = 1750;
                break;
            case "L":
                width_front = 2876;
                height_front = 3735;
                width_back = 2979;
                height_back = 3980;
                width_down_front = 1196;
                height_down_front = 687;
                width_down_back = 1365;
                height_down_back = 687;
                width_strap = 391;
                height_strap = 1810;
                break;
            case "XL":
                width_front = 2992;
                height_front = 3856;
                width_back = 3096;
                height_back = 4103;
                width_down_front = 1208;
                height_down_front = 687;
                width_down_back = 1388;
                height_down_back = 687;
                width_strap = 391;
                height_strap = 1868;
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

}
