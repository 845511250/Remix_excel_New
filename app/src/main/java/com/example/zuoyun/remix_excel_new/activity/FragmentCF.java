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

public class FragmentCF extends BaseFragment {
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

    int width_front,width_back, width_xiuzi, width_lingkou;
    int height_front,height_back, height_xiuzi, height_lingkou;

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
        canvas.drawRect(200, 4330 - 25, 700, 4330, rectPaint);
        canvas.drawText("CF紧身T恤  " + orderItems.get(currentID).sizeStr + "   " + time + "  " + orderItems.get(currentID).order_number, 200, 4330 - 2, paint);
    }

    void drawTextBack(Canvas canvas) {
        canvas.drawRect(200, 4490 - 25, 700, 4490, rectPaint);
        canvas.drawText("CF紧身T恤  " + orderItems.get(currentID).sizeStr + "   " + time + "  " + orderItems.get(currentID).order_number, 200, 4490 - 2, paint);
    }

    void drawTextXiuziR(Canvas canvas) {
        canvas.drawRect(1060, 11, 1060 + 90, 11 + 23, rectPaint);
        canvas.drawText(" 右" + orderItems.get(currentID).sizeStr, 1060, 11 + 21, paint);
    }

    void drawTextXiuziL(Canvas canvas) {
        canvas.drawRect(1060, 11, 1060 + 90, 11 + 23, rectPaint);
        canvas.drawText(" 左" + orderItems.get(currentID).sizeStr, 1060, 11 + 21, paint);
    }

    public void remixx(){
        int margin = 100;
        Bitmap bitmapCombine = Bitmap.createBitmap(width_front + width_back + width_xiuzi + margin * 2, height_front + height_lingkou + margin * 2, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        Matrix matrix = new Matrix();

        if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
            if (MainActivity.instance.bitmaps.get(0).getWidth() != 7355) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 7355, 7355, true));
            }
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 470, 2760, 3116, 4341);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.cf_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3784, 2678, 3114, 4501);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.cf_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //领口
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2082, 2264, 2986, 273);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.cf_border);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lingkou, height_lingkou, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin * 2, null);

            //左袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4319, 184, 2204, 1862);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.cf_sleeve);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, 0, null);

            //右袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 996, 184, 2204, 1862);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_xiuzi + margin * 2, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1 && orderItems.get(currentID).platform.equals("4u2")) {
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2205, 160, 3116, 4341);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.cf_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2205, 0, 3114, 4501);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.cf_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //领口
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2269, 205, 2986, 273);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.cf_border);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lingkou, height_lingkou, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin * 2, null);

            //左袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5321, 652, 2204, 1862);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.cf_sleeve);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, 0, null);

            //右袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 652, 2204, 1862);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_xiuzi + margin * 2, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 5 && orderItems.get(currentID).platform.equals("4u2")) {
            //前
            Bitmap bitmapTemp = Bitmap.createBitmap(3116, 4341, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.cf_front);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFront(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(3114, 4501, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.cf_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //领口
            bitmapTemp = Bitmap.createBitmap(2986, 273, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(4), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.cf_border);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lingkou, height_lingkou, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //左袖子
            bitmapTemp = Bitmap.createBitmap(2204, 1862, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.cf_sleeve);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, 0, null);

            //右袖子
            bitmapTemp = Bitmap.createBitmap(2204, 1862, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin * 2, height_xiuzi + margin * 2, null);

            bitmapDB.recycle();
            bitmapTemp.recycle();
        }

        try {
            matrix.reset();
            matrix.postRotate(90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);

            String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapCombine, fileSave, 148);
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

    void setSize(String size) {
        switch (size) {
            case "S":
                width_front = 2806;
                height_front = 4205;
                width_back = 2804;
                height_back = 4367;
                width_xiuzi = 1993;
                height_xiuzi = 1844;
                width_lingkou = 2811;
                height_lingkou = 300;
                break;
            case "M":
                width_front = 2924;
                height_front = 4265;
                width_back = 2923;
                height_back = 4425;
                width_xiuzi = 2088;
                height_xiuzi = 1875;
                width_lingkou = 2895;
                height_lingkou = 300;
                break;
            case "L":
                width_front = 3042;
                height_front = 4326;
                width_back = 3041;
                height_back = 4486;
                width_xiuzi = 2184;
                height_xiuzi = 1905;
                width_lingkou = 2978;
                height_lingkou = 300;
                break;
            case "XL":
                width_front = 3160;
                height_front = 4380;
                width_back = 3160;
                height_back = 4540;
                width_xiuzi = 2244;
                height_xiuzi = 1900;
                width_lingkou = 3026;
                height_lingkou = 300;
                break;
            case "2XL":
                width_front = 3363;
                height_front = 4501;
                width_back = 3361;
                height_back = 4661;
                width_xiuzi = 2396;
                height_xiuzi = 1970;
                width_lingkou = 3140;
                height_lingkou = 300;
                break;
            case "3XL":
                width_front = 3534;
                height_front = 4606;
                width_back = 3519;
                height_back = 4745;
                width_xiuzi = 2514;
                height_xiuzi = 1994;
                width_lingkou = 3200;
                height_lingkou = 300;
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

}
