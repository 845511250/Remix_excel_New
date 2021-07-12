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

public class FragmentFJY extends BaseFragment {
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
        paint.setTextSize(27);
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

    void drawTextFrontR(Canvas canvas) {
        canvas.drawRect(200, 4285-28, 1200, 4285, rectPaint);
        canvas.drawText("FJY儿童灯衣  " + orderItems.get(currentID).sizeStr + "   " + time + "  " + orderItems.get(currentID).order_number + "   " + getColor(orderItems.get(currentID).colorStr), 200, 4285 - 3, paint);
    }
    void drawTextFrontL(Canvas canvas) {
        canvas.drawRect(200, 4285-28, 1200, 4285, rectPaint);
        canvas.drawText("FJY儿童灯衣  " + orderItems.get(currentID).sizeStr + "   " + time + "  " + orderItems.get(currentID).order_number + "   " + getColor(orderItems.get(currentID).colorStr), 200, 4285 - 3, paint);
    }
    void drawTextBack(Canvas canvas) {
        canvas.drawRect(300, 4286 - 28, 300 + 1000, 4286, rectPaint);
        canvas.drawText("FJY儿童灯衣  " + orderItems.get(currentID).sizeStr + "   " + time + "  " + orderItems.get(currentID).order_number + "   " + getColor(orderItems.get(currentID).colorStr), 300, 4286 - 2, paint);
    }

    void drawTextLalianR(Canvas canvas) {
        canvas.save();
        canvas.rotate(-90f, 875, 2000);
        canvas.drawRect(875, 1999 - 28, 1700, 1999, rectPaint);
        canvas.drawText("FJY儿童灯衣  " + orderItems.get(currentID).sizeStr + "   " + time + "  " + orderItems.get(currentID).order_number + "   " + getColor(orderItems.get(currentID).colorStr), 875, 1999 - 3, paint);
        canvas.restore();
    }
    void drawTextLalianL(Canvas canvas) {
        canvas.save();
        canvas.rotate(90f, 8, 1000);
        canvas.drawRect(8, 1000 - 28, 900, 1000, rectPaint);
        canvas.drawText("FJY儿童灯衣  " + orderItems.get(currentID).sizeStr + "   " + time + "  " + orderItems.get(currentID).order_number + "   " + getColor(orderItems.get(currentID).colorStr), 8, 1000 - 2, paint);
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
        canvas.drawRect(50, 1418 - 28, 150, 1418, rectPaint);
        canvas.drawText("袋眉右", 50, 1418 - 3, paint);
    }
    void drawTextDaimeiL(Canvas canvas) {
        canvas.drawRect(50, 1418 - 28, 150, 1418, rectPaint);
        canvas.drawText("袋眉左", 50, 1418 - 3, paint);
    }
    void drawTextXiabai(Canvas canvas) {
        canvas.drawRect(6146, 6, 6246, 6 + 27, rectPaint);
        canvas.drawText("下摆", 6146, 34, paint);
    }
    void drawTextXiuziR(Canvas canvas) {
        canvas.drawRect(1380, 3511-28, 1480, 3511, rectPaint);
        canvas.drawText("袖子右", 1380, 3511 - 2, paint);
    }
    void drawTextXiuziL(Canvas canvas) {
        canvas.drawRect(1380, 3511-28, 1480, 3511, rectPaint);
        canvas.drawText("袖子左", 1380, 3511 - 2, paint);
    }
    //
    void drawTextMaoziL_out(Canvas canvas) {
        canvas.drawRect(592, 13, 692, 13 + 28, rectPaint);
        canvas.drawText("左外侧", 592, 13 + 26, paint);
    }
    void drawTextMaoziR_in(Canvas canvas) {
        canvas.drawRect(592, 13, 692, 13 + 28, rectPaint);
        canvas.drawText("右内侧", 592, 13 + 26, paint);
    }

    void drawTextMaoziR_out(Canvas canvas) {
        canvas.drawRect(1000, 13, 1100, 13 + 28, rectPaint);
        canvas.drawText("右外侧", 1000, 13 + 26, paint);
    }
    void drawTextMaoziL_in(Canvas canvas) {
        canvas.drawRect(1000, 13, 1100, 13 + 28, rectPaint);
        canvas.drawText("左内侧", 1000, 13 + 26, paint);
    }


    public void remixx(){
        int margin = 30;
        Bitmap bitmapCombine = Bitmap.createBitmap(width_maozi * 4 + width_dadai + margin * 4, height_front + height_maozi + height_xiuzi + height_xiabai + margin * 3, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (orderItems.get(currentID).imgs.size() == 1) {
            if (MainActivity.instance.bitmaps.get(0).getWidth() == 5790) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 9650, 7406, true));
            }

            //右前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2864, 2317, 1961, 4295);
            Canvas canvasTemp = new Canvas(bitmapTemp);

            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //左前
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4825, 2317, 1961, 4295);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + 30, 0, null);

            //右帽子内
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4876 - width_maozi, 2881 - height_maozi, width_maozi, height_maozi);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_hood_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziR_in(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + 30, null);

            //右帽子外
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4773, 2881 - height_maozi, width_maozi, height_maozi);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_hood_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziR_out(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi + 30, height_front + 30, null);

            //左帽子外
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4876 - width_maozi, 2881 - height_maozi, width_maozi, height_maozi);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_hood_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziL_out(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 2 + 60, height_front + 30, null);

            //左帽子内
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4773, 2881 - height_maozi, width_maozi, height_maozi);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_hood_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziL_in(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 3 + 90, height_front + 30, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2898, 2317, 3854, 4297);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + 60, 0, null);

            //右袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 3089, 2864, 3523);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_sleeve_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_maozi + 30, null);

            //左袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6786, 3089, 2864, 3523);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_sleeve_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi + 30, height_back + height_maozi + 30, null);

            //下摆
            bitmapTemp = Bitmap.createBitmap(6256, 792, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);

            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4825, 6614, 1564, 792);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3261, 6614, 1564 * 2, 792);
            canvasTemp.drawBitmap(bitmapCut, 1564, 0, null);
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3261, 6614, 1564, 792);
            canvasTemp.drawBitmap(bitmapCut, 1564 * 3, 0, null);
            bitmapCut.recycle();

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiabai);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiabai(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiabai, height_xiabai, true);
            Matrix matrix = new Matrix();
//        matrix.postRotate(90);
            matrix.postTranslate(0, height_back + height_maozi + height_xiuzi + 90);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //小袋右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3507, 4778, 1282, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_pocket_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiaodaiR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiaodai, height_xiaodai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + 90, 0, null);

            //小袋左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4863, 4778, 1282, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_pocket_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiaodaiL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiaodai, height_xiaodai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + 90, height_xiaodai + 30, null);

            //拉链右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3942, 2319, 883, 4301);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_lalian_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLalianR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lalian, height_lalian, true);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 4 + 120, height_dadai * 4 + 120, null);

            //拉链左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4825, 2319, 883, 4301);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_lalian_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLalianL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lalian, height_lalian, true);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 4 + width_lalian + 150, height_dadai * 4 + 120, null);

            //袋眉右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3220, 4889, 400, 1426);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_daimei);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextDaimeiR(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2 + 60, height_back + height_maozi + 60, null);

            //袋眉左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6038, 4889, 400, 1426);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_daimei);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextDaimeiL(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2 + width_daimei + 90, height_back + height_maozi + 60, null);

            //袖口右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 726, 6612, 1412, 792);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2, height_back + height_maozi + height_daimei + 90, null);

            //袖口左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7512, 6612, 1412, 792);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2, height_back + height_maozi + height_daimei + height_xiukou + 120, null);

            //大袋右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3389, 4778, 1400, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_pocket_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextDadaiR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_dadai, height_dadai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + 90, height_xiaodai * 2 + 60, null);

            //大袋左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4863, 4778, 1400, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_pocket_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextDadaiL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_dadai, height_dadai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + 90, height_xiaodai * 2 + height_dadai + 90, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 2) {
            //右前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2864, 2317, 1961, 4295);
            Canvas canvasTemp = new Canvas(bitmapTemp);

            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //左前
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4825, 2317, 1961, 4295);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + 30, 0, null);

            //右帽子内
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4876 - width_maozi, 2881 - height_maozi, width_maozi, height_maozi);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_hood_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziR_in(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + 30, null);

            //右帽子外
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 4773, 2881 - height_maozi, width_maozi, height_maozi);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_hood_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziR_out(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi + 30, height_front + 30, null);

            //左帽子外
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 4876 - width_maozi, 2881 - height_maozi, width_maozi, height_maozi);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_hood_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziL_out(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 2 + 60, height_front + 30, null);

            //左帽子内
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4773, 2881 - height_maozi, width_maozi, height_maozi);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_hood_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziL_in(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 3 + 90, height_front + 30, null);

            //后面
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2898, 2317, 3854, 4297);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + 60, 0, null);

            //右袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 0, 3089, 2864, 3523);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_sleeve_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_back + height_maozi + 30, null);

            //左袖子
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6786, 3089, 2864, 3523);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_sleeve_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi + 30, height_back + height_maozi + 30, null);

            //下摆
            bitmapTemp = Bitmap.createBitmap(6256, 792, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);

            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4825, 6614, 1564, 792);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3261, 6614, 1564 * 2, 792);
            canvasTemp.drawBitmap(bitmapCut, 1564, 0, null);
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3261, 6614, 1564, 792);
            canvasTemp.drawBitmap(bitmapCut, 1564 * 3, 0, null);
            bitmapCut.recycle();

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiabai);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiabai(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiabai, height_xiabai, true);
            Matrix matrix = new Matrix();
//        matrix.postRotate(90);
            matrix.postTranslate(0, height_back + height_maozi + height_xiuzi + 90);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //小袋右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3507, 4778, 1282, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_pocket_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiaodaiR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiaodai, height_xiaodai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + 90, 0, null);

            //小袋左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4863, 4778, 1282, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_pocket_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiaodaiL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiaodai, height_xiaodai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + 90, height_xiaodai + 30, null);

            //拉链右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3942, 2319, 883, 4301);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_lalian_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLalianR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lalian, height_lalian, true);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 4 + 120, height_dadai * 4 + 120, null);

            //拉链左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4825, 2319, 883, 4301);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_lalian_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLalianL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lalian, height_lalian, true);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 4 + width_lalian + 150, height_dadai * 4 + 120, null);

            //袋眉右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3220, 4889, 400, 1426);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_daimei);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextDaimeiR(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2 + 60, height_back + height_maozi + 60, null);

            //袋眉左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 6038, 4889, 400, 1426);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_daimei);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextDaimeiL(canvasTemp);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2 + width_daimei + 90, height_back + height_maozi + 60, null);

            //袖口右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 726, 6612, 1412, 792);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2, height_back + height_maozi + height_daimei + 90, null);

            //袖口左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 7512, 6612, 1412, 792);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2, height_back + height_maozi + height_daimei + height_xiukou + 120, null);

            //大袋右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3389, 4778, 1400, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_pocket_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextDadaiR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_dadai, height_dadai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + 90, height_xiaodai * 2 + 60, null);

            //大袋左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4863, 4778, 1400, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_pocket_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextDadaiL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_dadai, height_dadai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + width_back + 90, height_xiaodai * 2 + height_dadai + 90, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 8) {
            //右前
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, 1961, 4295);
            Canvas canvasTemp = new Canvas(bitmapTemp);

            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //左前
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 1961, 0, 1961, 4295);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + 30, 0, null);

            //右帽子内
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_hood_l);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, 1783, 2601);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziR_in(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + 30, null);

            //右帽子外
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_hood_r);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 1680, 0, 1783, 2601);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziR_out(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi + 30, height_front + 30, null);

            //左帽子外
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_hood_l);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, 1783, 2601);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziL_out(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 2 + 60, height_front + 30, null);

            //左帽子内
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_hood_r);
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(3), 1680, 0, 1783, 2601);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextMaoziL_in(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_maozi, height_maozi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 3 + 90, height_front + 30, null);

            //后面
            bitmapTemp = (MainActivity.instance.bitmaps.get(0)).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front * 2 + 60, 0, null);

            //右袖子
            bitmapTemp = (MainActivity.instance.bitmaps.get(7)).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_sleeve_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + height_maozi + margin * 2, null);

            //左袖子
            bitmapTemp = (MainActivity.instance.bitmaps.get(5)).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_sleeve_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiuzi, height_xiuzi, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi + 30, height_front + height_maozi + margin * 2, null);

            //下摆
            bitmapTemp = Bitmap.createBitmap(7624, 792, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);

            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1906, 0, 1906, 792);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, 1906 * 2, 792);
            canvasTemp.drawBitmap(bitmapCut, 1906, 0, null);
            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, 1906, 792);
            canvasTemp.drawBitmap(bitmapCut, 1906 * 3, 0, null);
            bitmapCut.recycle();

            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_bottom);
            drawTextXiabai(canvasTemp);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiabai, height_xiabai, true);
            Matrix matrix = new Matrix();
//        matrix.postRotate(90);
            matrix.postTranslate(0, height_back + height_maozi + height_xiuzi + 90);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //小袋右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 643, 2461, 1282, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_pocket_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiaodaiR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiaodai, height_xiaodai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 4 + margin * 4, 0, null);

            //小袋左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 1999, 2461, 1282, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_pocket_small);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiaodaiL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiaodai, height_xiaodai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 4 + margin * 4, height_xiaodai + 30, null);

            //拉链右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 1078, 0, 883, 4301);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_lalian_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLalianR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lalian, height_lalian, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2 + width_xiukou, height_front + height_maozi + margin * 2, null);

            //拉链左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 1961, 0, 883, 4301);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_lalian_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextLalianL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_lalian, height_lalian, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2 + width_xiukou + width_lalian + margin, height_front + height_maozi + margin * 2, null);

            //袋眉右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 356, 2572, 400, 1426);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_daimei);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextDaimeiR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_daimei, height_daimei, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2 + margin * 2, height_front + height_maozi + 60, null);

            //袋眉左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 3174, 2590, 400, 1426);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_daimei);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextDaimeiL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_daimei, height_daimei, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2 + width_daimei + +margin * 3, height_front + height_maozi + 60, null);

            //袖口右
            bitmapTemp = (MainActivity.instance.bitmaps.get(6)).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2 + margin * 2, height_front + height_maozi + height_daimei + margin * 3, null);

            //袖口左
            bitmapTemp = (MainActivity.instance.bitmaps.get(4)).copy(Bitmap.Config.ARGB_8888, true);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.jiake_xiukou);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiukouL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_xiukou, height_xiukou, true);
            canvasCombine.drawBitmap(bitmapTemp, width_xiuzi * 2 + margin * 2, height_front + height_maozi + height_daimei + height_xiukou + margin * 4, null);

            //大袋右
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 525, 2461, 1400, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_pocket_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextDadaiR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_dadai, height_dadai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 4 + margin * 4, height_xiaodai * 2 + 60, null);

            //大袋左
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(2), 1999, 2461, 1400, 1648);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.fjy_pocket_big);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextDadaiL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_dadai, height_dadai, true);
            canvasCombine.drawBitmap(bitmapTemp, width_maozi * 4 + margin * 4, height_xiaodai * 2 + height_dadai + 90, null);
            bitmapTemp.recycle();
        }


        try {
            String fluo = orderItems.get(currentID).sku.equals("FJYF") ? "荧光" : "";
            String nameCombine = fluo + "儿童灯衣" + orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
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
        if (MainActivity.instance.tb_auto.isChecked()){
            remix();
        }
    }

    void setSize(String size) {
        switch (size) {
            case "5-6":
                width_front = 1225;
                height_front = 2820;
                width_back = 2417;
                height_back = 2813;
                width_dadai = 812;
                height_dadai = 1132;
                width_xiaodai = 688;
                height_xiaodai = 1132;
                width_daimei = 300;
                height_daimei = 931;
                width_lalian = 715;
                height_lalian = 2820;
                width_maozi = 1375;
                height_maozi = 1932;
                width_xiabai = 4079;
                height_xiabai = 654;
                width_xiukou = 972;
                height_xiukou = 654;
                width_xiuzi = 1776;
                height_xiuzi = 2588;
                break;
            case "7-8":
                width_front = 1314;
                height_front = 3056;
                width_back = 2594;
                height_back = 3049;
                width_dadai = 871;
                height_dadai = 1132;
                width_xiaodai = 747;
                height_xiaodai = 1132;
                width_daimei = 300;
                height_daimei = 931;
                width_lalian = 737;
                height_lalian = 3056;
                width_maozi = 1430;
                height_maozi = 1993;
                width_xiabai = 4433;
                height_xiabai = 653;
                width_xiukou = 1091;
                height_xiukou = 654;
                width_xiuzi = 1953;
                height_xiuzi = 2795;
                break;
            case "9-10":
                width_front = 1402;
                height_front = 3233;
                width_back = 2771;
                height_back = 3225;
                width_dadai = 930;
                height_dadai = 1265;
                width_xiaodai = 806;
                height_xiaodai = 1265;
                width_daimei = 300;
                height_daimei = 1020;
                width_lalian = 758;
                height_lalian = 3233;
                width_maozi = 1488;
                height_maozi = 2056;
                width_xiabai = 4787;
                height_xiabai = 772;
                width_xiukou = 1209;
                height_xiukou = 772;
                width_xiuzi = 2131;
                height_xiuzi = 2942;
                break;
            case "11-12":
                width_front = 1491;
                height_front = 3469;
                width_back = 2948;
                height_back = 3461;
                width_dadai = 1019;
                height_dadai = 1265;
                width_xiaodai = 895;
                height_xiaodai = 1265;
                width_daimei = 300;
                height_daimei = 1020;
                width_lalian = 779;
                height_lalian = 3469;
                width_maozi = 1543;
                height_maozi = 2115;
                width_xiabai = 5142;
                height_xiabai = 772;
                width_xiukou = 1268;
                height_xiukou = 772;
                width_xiuzi = 2309;
                height_xiuzi = 3149;
                break;
            case "13-15":
                width_front = 1579;
                height_front = 3705;
                width_back = 3124;
                height_back = 3698;
                width_dadai = 1108;
                height_dadai = 1353;
                width_xiaodai = 984;
                height_xiaodai = 1353;
                width_daimei = 300;
                height_daimei = 1079;
                width_lalian = 800;
                height_lalian = 3705;
                width_maozi = 1601;
                height_maozi = 2181;
                width_xiabai = 5496;
                height_xiabai = 772;
                width_xiukou = 1268;
                height_xiukou = 772;
                width_xiuzi = 2487;
                height_xiuzi = 3356;
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
