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

public class FragmentGUW extends BaseFragment {
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

    int width_front, width_back, width_arm, width_pocket_in, width_pocket_out;
    int height_front,height_back,height_arm,height_pocket_in,height_pocket_out;

    int num;
    String strPlus = "";
    int intPlus = 1;
    boolean sizeOK = true;

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
        canvas.drawRect(250, 3415 - 25, 250 + 700, 3415, rectPaint);
        canvas.drawText(time + " 右 " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode, 250, 3415 - 2, paint);
    }
    void drawTextFrontL(Canvas canvas) {
        canvas.drawRect(500, 3415 - 25, 500 + 700, 3415, rectPaint);
        canvas.drawText(time + " 左 " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode, 500, 3415 - 2, paint);
    }
    void drawTextBack(Canvas canvas) {
        canvas.drawRect(1000, 3448, 1000 + 700, 3448 + 25, rectPaint);
        canvas.drawText(time + "  " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode, 1000, 3448 + 23, paint);
    }
    void drawTextXiuziL(Canvas canvas) {
        canvas.drawRect(1000, 3185-25, 1000 + 700, 3185, rectPaint);
        canvas.drawText("左  " + time + "  " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode, 1000, 3185 - 2, paint);
    }
    void drawTextXiuziR(Canvas canvas) {
        canvas.drawRect(1000, 3185-25, 1000 + 700, 3185, rectPaint);
        canvas.drawText("右  " + time + "  " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode, 1000, 3185 - 2, paint);
    }
    void drawTextPocketInL(Canvas canvas) {
        canvas.drawRect(10, 6, 10 + 250, 6 + 25, rectPaint);
        canvas.drawText("左  " + orderItems.get(currentID).order_number , 10, 6 + 23, paint);
    }
    void drawTextPocketInR(Canvas canvas) {
        canvas.drawRect(10, 6, 10 + 250, 6 + 25, rectPaint);
        canvas.drawText("右  " + orderItems.get(currentID).order_number , 10, 6 + 23, paint);
    }
    void drawTextPocketOutL(Canvas canvas) {
        canvas.drawRect(10, 946-25, 10 + 250, 946, rectPaint);
        canvas.drawText("左  " + orderItems.get(currentID).order_number , 10, 946 - 2, paint);
    }
    void drawTextPocketOutR(Canvas canvas) {
        canvas.drawRect(10, 946-25, 10 + 250, 946, rectPaint);
        canvas.drawText("右  " + orderItems.get(currentID).order_number , 10, 946 - 2, paint);
    }

    public void remixx(){
        int margin = 130;
        Matrix matrix = new Matrix();

        Bitmap bitmapCombine = Bitmap.createBitmap(width_arm * 2 + width_front * 2 + width_back + margin * 2, Math.max(height_front, height_arm + height_pocket_in + margin), Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
            if (MainActivity.instance.bitmaps.get(0).getWidth() != 8300) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 8300, 8300, true));
            }

            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2515, 366, 1666, 4090);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4123, 366, 1666, 4090);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + width_front + margin, 0, null);

            //back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2515, 4458, 3296, 3480);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm * 2 + width_front * 2 + margin, 0, null);

            //arm_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5802, 2757, 2437, 3191);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_arm_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + width_front * 2 + margin, 0, null);

            //arm_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 70, 2757, 2437, 3191);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_arm_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //pocket
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5010 + 63, 2278 + 289, 297, 983);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gum_pocket_in_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketInL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket_in, height_pocket_in, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_arm + margin, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2893 + 63, 2278 + 289, 297, 983);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gum_pocket_in_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketInR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket_in, height_pocket_in, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket_in + margin, height_arm + margin, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5010 + 63, 2278 + 289, 421, 954);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gum_pocket_out);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketOutL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket_out, height_pocket_out, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket_in * 2 + margin * 2, height_arm + margin, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2769 + 63, 2278 + 289, 421, 954);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gum_pocket_out);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextPocketOutR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket_out, height_pocket_out, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket_in * 2 + width_pocket_out + margin * 3, height_arm + margin, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2452, 76, 1666, 4090);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4082, 76, 1666, 4090);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + width_front + margin, 0, null);

            //back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2452, 33, 3296, 3480);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm * 2 + width_front * 2 + margin, 0, null);

            //arm_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5747, 708, 2437, 3191);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_arm_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + width_front * 2 + margin, 0, null);

            //arm_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 15, 708, 2437, 3191);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_arm_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //pocket
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5010, 2278, 297, 983);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gum_pocket_in_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketInL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket_in, height_pocket_in, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_arm + margin, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2893, 2278, 297, 983);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gum_pocket_in_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketInR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket_in, height_pocket_in, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket_in + margin, height_arm + margin, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 5010, 2278, 421, 954);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gum_pocket_out);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketOutL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket_out, height_pocket_out, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket_in * 2 + margin * 2, height_arm + margin, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2769, 2278, 421, 954);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gum_pocket_out);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextPocketOutR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket_out, height_pocket_out, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket_in * 2 + width_pocket_out + margin * 3, height_arm + margin, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 2) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2452, 76, 1666, 4090);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp,  width_arm, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 4082, 76, 1666, 4090);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + width_front + margin, 0, null);

            //back
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2452, 33, 3296, 3480);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm * 2 + width_front * 2 + margin, 0, null);

            //arm_l
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 5747, 708, 2437, 3191);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_arm_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + width_front * 2 + margin, 0, null);

            //arm_r
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 15, 708, 2437, 3191);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_arm_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //pocket
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 5010, 2278, 297, 983);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gum_pocket_in_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketInL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket_in, height_pocket_in, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_arm + margin, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2893, 2278, 297, 983);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gum_pocket_in_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketInR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket_in, height_pocket_in, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket_in + margin, height_arm + margin, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 5010, 2278, 421, 954);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gum_pocket_out);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketOutL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket_out, height_pocket_out, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket_in * 2 + margin * 2, height_arm + margin, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2769, 2278, 421, 954);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gum_pocket_out);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextPocketOutR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket_out, height_pocket_out, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket_in * 2 + width_pocket_out + margin * 3, height_arm + margin, null);
            bitmapTemp.recycle();
            
        } else {
            //frontR
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 0, 0, 1666, 4089);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_front_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm, 0, null);
            //frontL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1629, 0, 1666, 4089);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_front_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + width_front + margin, 0, null);

            //back
            bitmapTemp = Bitmap.createBitmap(3296, 3480, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_back);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBack(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm * 2 + width_front * 2 + margin, 0, null);

            //arm_l
            bitmapTemp = Bitmap.createBitmap(2437, 3191, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(2), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_arm_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, width_arm + width_front * 2 + margin, 0, null);

            //arm_r
            bitmapTemp = Bitmap.createBitmap(2437, 3191, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawColor(0xffffffff);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(3), 0, 0, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.guw_arm_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextXiuziR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_arm, height_arm, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            //pocket
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2558, 2202, 297, 983);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gum_pocket_in_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketInL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket_in, height_pocket_in, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_arm + margin, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 440, 2202, 297, 983);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gum_pocket_in_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketInR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket_in, height_pocket_in, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket_in + margin, height_arm + margin, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2558, 2202, 421, 954);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gum_pocket_out);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketOutL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket_out, height_pocket_out, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket_in * 2 + margin * 2, height_arm + margin, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 317, 2202, 421, 954);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.gum_pocket_out);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextPocketOutR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket_out, height_pocket_out, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket_in * 2 + width_pocket_out + margin * 3, height_arm + margin, null);
            bitmapTemp.recycle();
        }


        matrix.reset();
        matrix.postRotate(-90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
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
        if (MainActivity.instance.tb_auto.isChecked())
            remix();
    }

    void setSize(String size) {
        switch (size) {
            case "S":
                width_front = 1736;
                height_front = 4176;
                width_back = 3429;
                height_back = 3482;
                width_arm = 2341;
                height_arm = 3494;
                width_pocket_in = 309;
                height_pocket_in = 1003;
                width_pocket_out = 438;
                height_pocket_out = 974;
                break;
            case "M":
                width_front = 1809;
                height_front = 4294;
                width_back = 3577;
                height_back = 3600;
                width_arm = 2473;
                height_arm = 3583;
                width_pocket_in = 322;
                height_pocket_in = 1032;
                width_pocket_out = 457;
                height_pocket_out = 1001;
                break;
            case "L":
                width_front = 1883;
                height_front = 4411;
                width_back = 3724;
                height_back = 3717;
                width_arm = 2605;
                height_arm = 3673;
                width_pocket_in = 335;
                height_pocket_in = 1060;
                width_pocket_out = 475;
                height_pocket_out = 1028;
                break;
            case "XL":
                width_front = 1989;
                height_front = 4561;
                width_back = 3871;
                height_back = 3835;
                width_arm = 2736;
                height_arm = 3762;
                width_pocket_in = 354;
                height_pocket_in = 1096;
                width_pocket_out = 502;
                height_pocket_out = 1063;
                break;
            case "2XL":
                width_front = 2073;
                height_front = 4704;
                width_back = 4018;
                height_back = 3953;
                width_arm = 2867;
                height_arm = 3852;
                width_pocket_in = 369;
                height_pocket_in = 1130;
                width_pocket_out = 523;
                height_pocket_out = 1097;
                break;
            case "3XL":
                width_front = 2179;
                height_front = 4852;
                width_back = 4214;
                height_back = 4099;
                width_arm = 3000;
                height_arm = 3941;
                width_pocket_in = 388;
                height_pocket_in = 1166;
                width_pocket_out = 550;
                height_pocket_out = 1131;
                break;
            case "4XL":
                width_front = 2250;
                height_front = 4997;
                width_back = 4403;
                height_back = 4189;
                width_arm = 3130;
                height_arm = 4031;
                width_pocket_in = 401;
                height_pocket_in = 1200;
                width_pocket_out = 568;
                height_pocket_out = 1165;
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
