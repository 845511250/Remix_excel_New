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

public class FragmentFJ extends BaseFragment {
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

    int width_front,width_back,width_dadai,width_xiaodai,width_daimei,width_lalian,width_maozi,width_xiabai,width_xiukou, width_xiuzi;
    int height_front,height_back,height_dadai,height_xiaodai,height_daimei,height_lalian,height_maozi,height_xiabai,height_xiukou, height_xiuzi;

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
        paint.setTextSize(30);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(30);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(30);
        paintBlue.setTypeface(Typeface.DEFAULT_BOLD);
        paintBlue.setAntiAlias(true);

        paintSmall = new Paint();
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(30);
        paintSmall.setTypeface(Typeface.DEFAULT_BOLD);
        paintSmall.setAntiAlias(true);

        time = MainActivity.instance.orderDate_Print;

        MainActivity.instance.setMessageListener(new MainActivity.MessageListener() {
            @Override
            public void listen(int message, String sampleurl) {
                if (message == 0) {
                    iv_pillow.setImageDrawable(null);
                    bt_remix.setClickable(false);
                } else if (message == 4) {
                    Log.e("jiake", "message4");
//                    if(!MainActivity.instance.cb_fastmode.isChecked())
//                        iv_pillow.setImageBitmap(MainActivity.instance.bitmapPillow);
                    bt_remix.setClickable(true);
                    checkremix();
                } else if (message == 5) {
                    Log.e("imgs", "message5");
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

    void drawTextFrontR(Canvas canvas) {
        canvas.save();
        canvas.rotate(81.1f, 291, 548);
        canvas.drawRect(291, 548 - 28, 391, 548, rectPaint);
        canvas.drawText("袖子右", 291, 548 - 2, paint);
        canvas.restore();

        canvas.drawRect(1860, 4288-28, 1904, 4288, rectPaint);
        canvas.drawText("下摆", 1860, 4288 - 2, paint);

        canvas.drawRect(200, 4288-28, 1200, 4288, rectPaint);
        canvas.drawText("FJ带帽夹克  " + orderItems.get(currentID).sizeStr + "   " + time + "  " + orderItems.get(currentID).order_number + "   " + getColor(orderItems.get(currentID).colorStr), 200, 4288 - 2, paint);
    }
    void drawTextFrontL(Canvas canvas) {
        canvas.save();
        canvas.rotate(-81.4f, 1646, 703);
        canvas.drawRect(1646, 703 - 28, 1746, 703, rectPaint);
        canvas.drawText("袖子左", 1646, 703 - 2, paint);
        canvas.restore();
    }

    void drawTextLalianR(Canvas canvas) {
        canvas.save();
        canvas.rotate(-90f, 875, 2000);
        canvas.drawRect(875, 2000 - 28, 1700, 2000, rectPaint);
        canvas.drawText("FJ带帽夹克  " + orderItems.get(currentID).sizeStr + "   " + time + "  " + orderItems.get(currentID).order_number + "   " + getColor(orderItems.get(currentID).colorStr), 875, 2000 - 2, paint);
        canvas.restore();
    }
    void drawTextLalianL(Canvas canvas) {
        canvas.save();
        canvas.rotate(90f, 8, 1000);
        canvas.drawRect(8, 1000 - 28, 900, 1000, rectPaint);
        canvas.drawText("FJ带帽夹克  " + orderItems.get(currentID).sizeStr + "   " + time + "  " + orderItems.get(currentID).order_number + "   " + getColor(orderItems.get(currentID).colorStr), 8, 1000 - 2, paint);
        canvas.restore();
    }

    void drawTextDadaiR(Canvas canvas) {
        canvas.drawRect(100, 10, 200, 38, rectPaint);
        canvas.drawText("大袋右", 100, 38 - 2, paint);
    }
    void drawTextDadaiL(Canvas canvas) {
        canvas.drawRect(100, 10, 200, 38, rectPaint);
        canvas.drawText("大袋左", 100, 38 - 2, paint);
    }
    void drawTextXiaodaiR(Canvas canvas) {
        canvas.drawRect(100, 10, 200, 38, rectPaint);
        canvas.drawText("小袋右", 100, 38 - 2, paint);
    }
    void drawTextXiaodaiL(Canvas canvas) {
        canvas.drawRect(100, 10, 200, 38, rectPaint);
        canvas.drawText("小袋左", 100, 38 - 2, paint);
    }
    void drawTextXiukouR(Canvas canvas) {
        canvas.drawRect(650, 8, 750, 36, rectPaint);
        canvas.drawText("袖口右", 650, 36 - 2, paint);
    }
    void drawTextXiukouL(Canvas canvas) {
        canvas.drawRect(650, 8, 750, 36, rectPaint);
        canvas.drawText("袖口左", 650, 36 - 2, paint);
    }
    void drawTextDaimeiR(Canvas canvas) {
        canvas.save();
        canvas.rotate(2.8f, 49, 1403);
        canvas.drawRect(49, 1403 - 28, 149, 1403, rectPaint);
        canvas.drawText("袋眉右", 49, 1403 - 2, paint);
        canvas.restore();
    }
    void drawTextDaimeiL(Canvas canvas) {
        canvas.save();
        canvas.rotate(-1.9f, 87, 1412);
        canvas.drawRect(87, 1412 - 28, 187, 1412, rectPaint);
        canvas.drawText("袋眉左", 87, 1412 - 2, paint);
        canvas.restore();
    }
    void drawTextXiabai(Canvas canvas) {
        canvas.drawRect(6146, 8, 6246, 36, rectPaint);
        canvas.drawText("下摆", 6146, 34, paint);
    }
    void drawTextXiuziR(Canvas canvas) {
        canvas.save();
        canvas.rotate(-141.2f, 2221, 349);
        canvas.drawRect(2221, 349 - 28, 2321, 349, rectPaint);
        canvas.drawText("袖子右", 2221, 349 - 2, paint);
        canvas.restore();

        canvas.drawRect(1380, 3516-28, 1480, 3516, rectPaint);
        canvas.drawText("袖口右", 1380, 3516 - 2, paint);
    }
    void drawTextXiuziL(Canvas canvas) {
        canvas.save();
        canvas.rotate(141.5f, 745, 267);
        canvas.drawRect(745, 267 - 28, 845, 267, rectPaint);
        canvas.drawText("袖子左", 745, 267 - 2, paint);
        canvas.restore();

        canvas.drawRect(1380, 3516-28, 1480, 3516, rectPaint);
        canvas.drawText("袖口左", 1380, 3516 - 2, paint);
    }
    //
    void drawTextMaoziL_out(Canvas canvas) {
        if (orderItems.get(currentID).sizeStr.equals("S")) {
            canvas.drawRect(550, 12, 650, 40, rectPaint);
            canvas.drawText("左外侧", 550, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("M")) {
            canvas.drawRect(533, 12, 633, 40, rectPaint);
            canvas.drawText("左外侧", 533, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("L")) {
            canvas.drawRect(543, 12, 643, 40, rectPaint);
            canvas.drawText("左外侧", 543, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("XL")) {
            canvas.drawRect(566, 12, 666, 40, rectPaint);
            canvas.drawText("左外侧", 566, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("XXL")) {
            canvas.drawRect(596, 12, 696, 40, rectPaint);
            canvas.drawText("左外侧", 596, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("XXXL")) {
            canvas.drawRect(592, 12, 692, 40, rectPaint);
            canvas.drawText("左外侧", 592, 38, paint);
        }
    }
    void drawTextMaoziR_in(Canvas canvas) {
        if (orderItems.get(currentID).sizeStr.equals("S")) {
            canvas.drawRect(550, 12, 650, 40, rectPaint);
            canvas.drawText("右内侧", 550, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("M")) {
            canvas.drawRect(533, 12, 633, 40, rectPaint);
            canvas.drawText("右内侧", 533, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("L")) {
            canvas.drawRect(543, 12, 643, 40, rectPaint);
            canvas.drawText("右内侧", 543, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("XL")) {
            canvas.drawRect(566, 12, 666, 40, rectPaint);
            canvas.drawText("右内侧", 566, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("XXL")) {
            canvas.drawRect(596, 12, 696, 40, rectPaint);
            canvas.drawText("右内侧", 596, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("XXXL")) {
            canvas.drawRect(592, 12, 692, 40, rectPaint);
            canvas.drawText("右内侧", 592, 38, paint);
        }
    }

    void drawTextMaoziR_out(Canvas canvas) {
        if (orderItems.get(currentID).sizeStr.equals("S")) {
            canvas.drawRect(1065, 12, 1165, 40, rectPaint);
            canvas.drawText("右外侧", 1065, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("M")) {
            canvas.drawRect(1083, 12, 1183, 40, rectPaint);
            canvas.drawText("右外侧", 1083, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("L")) {
            canvas.drawRect(1120, 12, 1220, 40, rectPaint);
            canvas.drawText("右外侧", 1120, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("XL")) {
            canvas.drawRect(1158, 12, 1258, 40, rectPaint);
            canvas.drawText("右外侧", 1158, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("XXL")) {
            canvas.drawRect(1253, 12, 1353, 40, rectPaint);
            canvas.drawText("右外侧", 1253, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("XXXL")) {
            canvas.drawRect(1300, 12, 1400, 40, rectPaint);
            canvas.drawText("右外侧", 1300, 38, paint);
        }
    }
    void drawTextMaoziL_in(Canvas canvas) {
        if (orderItems.get(currentID).sizeStr.equals("S")) {
            canvas.drawRect(1065, 12, 1165, 40, rectPaint);
            canvas.drawText("左内侧", 1065, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("M")) {
            canvas.drawRect(1083, 12, 1183, 40, rectPaint);
            canvas.drawText("左内侧", 1083, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("L")) {
            canvas.drawRect(1120, 12, 1220, 40, rectPaint);
            canvas.drawText("左内侧", 1120, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("XL")) {
            canvas.drawRect(1158, 12, 1258, 40, rectPaint);
            canvas.drawText("左内侧", 1158, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("XXL")) {
            canvas.drawRect(1253, 12, 1353, 40, rectPaint);
            canvas.drawText("左内侧", 1253, 38, paint);
        } else if (orderItems.get(currentID).sizeStr.equals("XXXL")) {
            canvas.drawRect(1300, 12, 1400, 40, rectPaint);
            canvas.drawText("左内侧", 1300, 38, paint);
        }
    }


    public void remixx(){
        Bitmap bitmapCombine = Bitmap.createBitmap(width_front * 2 + width_back + width_dadai + 30 * 3, height_front + height_maozi + height_xiuzi + height_xiabai + 30 * 3, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs == null) {
            if (MainActivity.instance.bitmapPillow.getWidth() == 5790) {
                MainActivity.instance.bitmapPillow = Bitmap.createScaledBitmap(MainActivity.instance.bitmapPillow, 9650, 7406, true);
            }

            //右前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 2864, 2317, 1961, 4295);
            Canvas canvasTemp = new Canvas(bitmapTemp);

            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp.recycle();

            //左前
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 4825, 2317, 1961, 4295);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + 30, 0, null);
            bitmapTemp.recycle();

            //右帽子内
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 4876 - width_maozi, 2881 - height_maozi, width_maozi, height_maozi);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), getDbMaoziId_l());
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextMaoziR_in(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + 30, null);
            bitmapTemp.recycle();

            //右帽子外
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 4773, 2881 - height_maozi, width_maozi, height_maozi);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), getDbMaoziId_r());
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextMaoziR_out(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi + 30, height_front + 30, null);
            bitmapTemp.recycle();

            //左帽子外
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 4876 - width_maozi, 2881 - height_maozi, width_maozi, height_maozi);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), getDbMaoziId_l());
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextMaoziL_out(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 2 + 60, height_front + 30, null);
            bitmapTemp.recycle();

            //左帽子内
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 4773, 2881 - height_maozi, width_maozi, height_maozi);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), getDbMaoziId_r());
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextMaoziL_in(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 3 + 90, height_front + 30, null);
            bitmapTemp.recycle();

            //后面
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 2898, 2317, 3854, 4297);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + 60, 0, null);
            bitmapTemp.recycle();

            //右袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 0, 3089, 2864, 3523);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiuzi);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_maozi, null);
            bitmapTemp.recycle();

            //左袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 6786, 3089, 2864, 3523);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiuzi);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi+30, height_back + height_maozi+30, null);
            bitmapTemp.recycle();

            //下摆
            bitmapTemp = Bitmap.createBitmap(6256, 792, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);

            Bitmap bitmapCut=Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 4825, 6614, 1564, 792);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 3261, 6614, 1564 * 2, 792);
            canvasTemp.drawBitmap(bitmapCut, 1564, 0, null);
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 3261, 6614, 1564, 792);
            canvasTemp.drawBitmap(bitmapCut, 1564 * 3, 0, null);
            bitmapCut.recycle();

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiabai);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiabai(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiabai, height_xiabai, true);
            Matrix matrix = new Matrix();
//        matrix.postRotate(90);
            matrix.postTranslate(0, height_back + height_maozi + height_xiuzi + 90);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();

            //小袋右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 3507, 4778, 1282, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiaodai_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiaodaiR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiaodai, height_xiaodai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + 90, 0, null);
            bitmapTemp.recycle();

            //小袋左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 4863, 4778, 1282, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiaodai_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiaodaiL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiaodai, height_xiaodai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + 90, height_xiaodai + 30, null);
            bitmapTemp.recycle();

            //拉链右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 3942, 2319, 883, 4301);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_lalian_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextLalianR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lalian, height_lalian, true);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 4 + 120, height_dadai * 4 + 120, null);
            bitmapTemp.recycle();

            //拉链左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 4825, 2319, 883, 4301);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_lalian_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextLalianL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lalian, height_lalian, true);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 4 + width_lalian + 150, height_dadai * 4 + 120, null);
            bitmapTemp.recycle();

            //袋眉右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 3220, 4889, 400, 1426);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_daimei_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextDaimeiR(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2 + 60, height_back + height_maozi + 60, null);
            bitmapTemp.recycle();

            //袋眉左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 6038, 4889, 400, 1426);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_daimei_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextDaimeiL(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2 + width_daimei + 90, height_back + height_maozi + 60, null);
            bitmapTemp.recycle();

            //袖口右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 726, 6612, 1412, 792);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiukouR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2, height_back + height_maozi + height_daimei + 90, null);
            bitmapTemp.recycle();

            //袖口左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 7512, 6612, 1412, 792);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiukouL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2, height_back + height_maozi + height_daimei + height_xiukou + 120, null);
            bitmapTemp.recycle();

            //大袋右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 3389, 4778, 1400, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_dadai_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();drawTextDadaiR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_dadai, height_dadai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + 90, height_xiaodai * 2 + 60, null);
            bitmapTemp.recycle();

            //大袋左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmapPillow, 4863, 4778, 1400, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_dadai_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextDadaiL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_dadai, height_dadai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + 90, height_xiaodai * 2 + height_dadai + 90, null);
            bitmapTemp.recycle();
        }else {
            //右前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, 1961, 4295);
            Canvas canvasTemp = new Canvas(bitmapTemp);

            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);
            bitmapTemp.recycle();

            //左前
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1961, 0, 1961, 4295);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + 30, 0, null);
            bitmapTemp.recycle();

            //右帽子内
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), getDbMaoziId_l());
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(6), 0, 0, 1783, 2601);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, bitmapDB.getWidth(), bitmapDB.getHeight(), true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextMaoziR_in(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + 30, null);
            bitmapTemp.recycle();

            //右帽子外
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), getDbMaoziId_r());
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(6), 1680, 0, 1783, 2601);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, bitmapDB.getWidth(), bitmapDB.getHeight(), true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextMaoziR_out(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi + 30, height_front + 30, null);
            bitmapTemp.recycle();

            //左帽子外
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), getDbMaoziId_l());
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(6), 0, 0, 1783, 2601);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, bitmapDB.getWidth(), bitmapDB.getHeight(), true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextMaoziL_out(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 2 + 60, height_front + 30, null);
            bitmapTemp.recycle();

            //左帽子内
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), getDbMaoziId_r());
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(6), 1680, 0, 1783, 2601);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, bitmapDB.getWidth(), bitmapDB.getHeight(), true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextMaoziL_in(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 3 + 90, height_front + 30, null);
            bitmapTemp.recycle();

            //后面
            bitmapTemp = MainActivity.instance.bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + 60, 0, null);
            bitmapTemp.recycle();

            //右袖子
            bitmapTemp = MainActivity.instance.bitmaps.get(3).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiuzi);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_maozi, null);
            bitmapTemp.recycle();

            //左袖子
            bitmapTemp = MainActivity.instance.bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiuzi);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi+30, height_back + height_maozi+30, null);
            bitmapTemp.recycle();

            //下摆
            bitmapTemp = Bitmap.createBitmap(7624, 792, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);

            Bitmap bitmapCut=Bitmap.createBitmap(MainActivity.instance.bitmaps.get(5), 1906, 0, 1906, 792);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, 1906 * 2, 792);
            canvasTemp.drawBitmap(bitmapCut, 1906, 0, null);
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(5), 0, 0, 1906, 792);
            canvasTemp.drawBitmap(bitmapCut, 1906 * 3, 0, null);
            bitmapCut.recycle();

            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, 6256, 792, true);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiabai);
            drawTextXiabai(canvasTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiabai, height_xiabai, true);
            Matrix matrix = new Matrix();
//        matrix.postRotate(90);
            matrix.postTranslate(0, height_back + height_maozi + height_xiuzi + 90);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();

            //小袋右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 643, 2461, 1282, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiaodai_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiaodaiR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiaodai, height_xiaodai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + 90, 0, null);
            bitmapTemp.recycle();

            //小袋左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1999, 2461, 1282, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiaodai_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiaodaiL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiaodai, height_xiaodai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + 90, height_xiaodai + 30, null);
            bitmapTemp.recycle();

            //拉链右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1078, 0, 883, 4301);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_lalian_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextLalianR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lalian, height_lalian, true);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 4 + 120, height_dadai * 4 + 120, null);
            bitmapTemp.recycle();

            //拉链左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1961, 0, 883, 4301);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_lalian_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextLalianL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lalian, height_lalian, true);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 4 + width_lalian + 150, height_dadai * 4 + 120, null);
            bitmapTemp.recycle();

            //袋眉右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 356, 2572, 400, 1426);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_daimei_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextDaimeiR(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2 + 60, height_back + height_maozi + 60, null);
            bitmapTemp.recycle();

            //袋眉左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3174, 2590, 400, 1426);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_daimei_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextDaimeiL(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2 + width_daimei + 90, height_back + height_maozi + 60, null);
            bitmapTemp.recycle();

            //袖口右
            bitmapTemp = MainActivity.instance.bitmaps.get(4).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiukouR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2, height_back + height_maozi + height_daimei + 90, null);
            bitmapTemp.recycle();

            //袖口左
            bitmapTemp = MainActivity.instance.bitmaps.get(4).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextXiukouL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2, height_back + height_maozi + height_daimei + height_xiukou + 120, null);
            bitmapTemp.recycle();

            //大袋右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 525, 2461, 1400, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_dadai_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();drawTextDadaiR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_dadai, height_dadai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + 90, height_xiaodai * 2 + 60, null);
            bitmapTemp.recycle();

            //大袋左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1999, 2461, 1400, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_dadai_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextDadaiL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_dadai, height_dadai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + 90, height_xiaodai * 2 + height_dadai + 90, null);
            bitmapTemp.recycle();
        }


        try {
            String nameCombine = "卫衣夹克" + orderItems.get(currentID).sizeStr + getColor(orderItems.get(currentID).colorStr) + orderItems.get(currentID).order_number + strPlus + ".jpg";

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
            if (MainActivity.instance.bitmapPillow != null) {
                MainActivity.instance.bitmapPillow.recycle();
            }
            if (MainActivity.instance.bitmaps.size() != 0) {
                for (int i = 0; i < MainActivity.instance.bitmaps.size(); i++) {
                    if (MainActivity.instance.bitmaps.get(i) != null)
                        MainActivity.instance.bitmaps.get(i).recycle();
                }
            }

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
                width_front = 1832;
                height_front = 4043;
                width_back = 3600;
                height_back = 4040;
                width_dadai = 1268;
                height_dadai = 1634;
                width_xiaodai = 1150;
                height_xiaodai = 1634;
                width_daimei = 400;
                height_daimei = 1426;
                width_lalian = 801;
                height_lalian = 4044;
                width_maozi = 1718;
                height_maozi = 2601;
                width_xiabai = 5769;
                height_xiabai = 778;
                width_xiukou = 1280;
                height_xiukou = 778;
                width_xiuzi = 2601;
                height_xiuzi = 3389;
                break;
            case "M":
                width_front = 1892;
                height_front = 4165;
                width_back = 3720;
                height_back = 4161;
                width_dadai = 1327;
                height_dadai = 1634;
                width_xiaodai = 1209;
                height_xiaodai = 1634;
                width_daimei = 400;
                height_daimei = 1426;
                width_lalian = 840;
                height_lalian = 4165;
                width_maozi = 1746;
                height_maozi = 2602;
                width_xiabai = 6005;
                height_xiabai = 778;
                width_xiukou = 1339;
                height_xiukou = 778;
                width_xiuzi = 2726;
                height_xiuzi = 3449;
                break;
            case "L":
                width_front = 1961;
                height_front = 4295;
                width_back = 3854;
                height_back = 4297;
                width_dadai = 1400;
                height_dadai = 1648;
                width_xiaodai = 1282;
                height_xiaodai = 1648;
                width_daimei = 400;
                height_daimei = 1426;
                width_lalian = 883;
                height_lalian = 4301;
                width_maozi = 1783;
                height_maozi = 2601;
                width_xiabai = 6256;
                height_xiabai = 792;
                width_xiukou = 1412;
                height_xiukou = 792;
                width_xiuzi = 2864;
                height_xiuzi = 3523;
                break;
            case "XL":
                width_front = 2012;
                height_front = 4408;
                width_back = 3960;
                height_back = 4404;
                width_dadai = 1445;
                height_dadai = 1634;
                width_xiaodai = 1327;
                height_xiaodai = 1634;
                width_daimei = 400;
                height_daimei = 1426;
                width_lalian = 899;
                height_lalian = 4408;
                width_maozi = 1858;
                height_maozi = 2664;
                width_xiabai = 6477;
                height_xiabai = 778;
                width_xiukou = 1457;
                height_xiukou = 778;
                width_xiuzi = 2974;
                height_xiuzi = 3569;
                break;
            case "XXL":
                width_front = 2072;
                height_front = 4530;
                width_back = 4080;
                height_back = 4526;
                width_dadai = 1504;
                height_dadai = 1634;
                width_xiaodai = 1386;
                height_xiaodai = 1634;
                width_daimei = 400;
                height_daimei = 1426;
                width_lalian = 929;
                height_lalian = 4530;
                width_maozi = 1935;
                height_maozi = 2664;
                width_xiabai = 6713;
                height_xiabai = 778;
                width_xiukou = 1516;
                height_xiukou = 778;
                width_xiuzi = 3098;
                height_xiuzi = 3629;
                break;
            case "XXXL":
                width_front = 2132;
                height_front = 4652;
                width_back = 4199;
                height_back = 4647;
                width_dadai = 1563;
                height_dadai = 1634;
                width_xiaodai = 1445;
                height_xiaodai = 1634;
                width_daimei = 400;
                height_daimei = 1426;
                width_lalian = 958;
                height_lalian = 4652;
                width_maozi = 2010;
                height_maozi = 2663;
                width_xiabai = 6950;
                height_xiabai = 778;
                width_xiukou = 1576;
                height_xiukou = 778;
                width_xiuzi = 3222;
                height_xiuzi = 3690;
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

    int getDbMaoziId_r(){
        if (orderItems.get(currentID).sizeStr.equals("S")) {
            return R.drawable.jiake_maozi_r_s;
        } else if (orderItems.get(currentID).sizeStr.equals("M")) {
            return R.drawable.jiake_maozi_r_m;
        } else if (orderItems.get(currentID).sizeStr.equals("L")) {
            return R.drawable.jiake_maozi_r_l;
        } else if (orderItems.get(currentID).sizeStr.equals("XL")) {
            return R.drawable.jiake_maozi_r_xl;
        } else if (orderItems.get(currentID).sizeStr.equals("XXL")) {
            return R.drawable.jiake_maozi_r_xxl;
        } else {
            return R.drawable.jiake_maozi_r_xxxl;
        }
    }
    int getDbMaoziId_l(){
        if (orderItems.get(currentID).sizeStr.equals("S")) {
            return R.drawable.jiake_maozi_l_s;
        } else if (orderItems.get(currentID).sizeStr.equals("M")) {
            return R.drawable.jiake_maozi_l_m;
        } else if (orderItems.get(currentID).sizeStr.equals("L")) {
            return R.drawable.jiake_maozi_l_l;
        } else if (orderItems.get(currentID).sizeStr.equals("XL")) {
            return R.drawable.jiake_maozi_l_xl;
        } else if (orderItems.get(currentID).sizeStr.equals("XXL")) {
            return R.drawable.jiake_maozi_l_xxl;
        } else {
            return R.drawable.jiake_maozi_l_xxxl;
        }
    }

    String getColor(String color){
        if (color.equals("White")) {
            return "白灯";
        } else if (color.equals("Green")) {
            return "绿灯";
        } else if (color.equals("Blue")) {
            return "蓝灯";
        } else if (color.equals("Red")) {
            return "红灯";
        } else {
            return "无灯";
        }
    }
}
