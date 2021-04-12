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
import com.example.zuoyun.remix_excel_new.tools.BitmapToPng;

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

public class FragmentHGW extends BaseFragment {
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

    int width_front, width_back, width_pocket, width_borderTop, width_borderPocket;
    int height_front, height_back, height_pocket, height_borderTop, height_borderPocket;
    int id_frontR, id_backL;

    int width_combine, height_combine;
    int x_front_l, x_front_r, x_back_l, x_back_r, x_pocket_l, x_pockrt_r, x_border_r, x_border_l, x_top;
    int y_front_l, y_front_r, y_back_l, y_back_r, y_pocket_l, y_pockrt_r, y_border_r, y_border_l, y_top;

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
        orderItems = MainActivity.instance.orderItems;
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
        paint.setTextSize(22);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(22);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(22);
        paintBlue.setTypeface(Typeface.DEFAULT_BOLD);
        paintBlue.setAntiAlias(true);

        paintSmall = new Paint();
        paintSmall.setColor(0xff000000);
        paintSmall.setTextSize(22);
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
                    if (!MainActivity.instance.cb_fastmode.isChecked()) {
                        iv_pillow.setImageBitmap(MainActivity.instance.bitmaps.get(0));
                    }
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

    public void remix() {
        setSize(orderItems.get(currentID).sizeStr);

        new Thread() {
            @Override
            public void run() {
                super.run();
                if (sizeOK) {
                    for (num = orderItems.get(currentID).num; num >= 1; num--) {
                        intPlus = orderItems.get(currentID).num - num + 1;
                        for (int i = 0; i < currentID; i++) {
                            if (orderItems.get(currentID).order_number.equals(orderItems.get(i).order_number)) {
                                intPlus += orderItems.get(i).num;
                                ;
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
        canvas.save();
        canvas.rotate(2.4f, 827, 18);
        canvas.drawRect(827, 18, 827 + 500, 18 + 22, rectPaint);
        canvas.drawText(time + "  HGW_" + orderItems.get(currentID).sizeStr + "  前右  " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode_short, 827, 18 + 20, paint);
        canvas.restore();
    }

    void drawTextFrontL(Canvas canvas) {
        canvas.save();
        canvas.rotate(-2.4f, 541, 41);
        canvas.drawRect(541, 41, 541 + 500, 41 + 22, rectPaint);
        canvas.drawText(time + "  HGW_" + orderItems.get(currentID).sizeStr + "  前左  " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode_short, 541, 41 + 20, paint);
        canvas.restore();
    }

    void drawTextBackL(Canvas canvas) {
        canvas.save();
        canvas.rotate(-10f, 830, 158);
        canvas.drawRect(830, 158, 830 + 500, 158 + 22, rectPaint);
        canvas.drawText(time + "  HGW女" + orderItems.get(currentID).sizeStr + "  后左  " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode_short, 830, 158 + 20, paint);
        canvas.restore();
    }

    void drawTextBackR(Canvas canvas) {
        canvas.save();
        canvas.rotate(10f, 1019, 53);
        canvas.drawRect(1019, 53, 1019 + 500, 53 + 22, rectPaint);
        canvas.drawText(time + "  HGW女" + orderItems.get(currentID).sizeStr + "  后右  " + orderItems.get(currentID).order_number + "   " + orderItems.get(currentID).newCode_short, 1019, 53 + 20, paint);
        canvas.restore();
    }

    void drawTextPocketR(Canvas canvas) {
        canvas.save();
        canvas.rotate(6.2f, 518, 60);
        canvas.drawRect(518, 60, 518 + 400, 60 + 22, rectPaint);
        canvas.drawText(time + "  HGW_" + orderItems.get(currentID).sizeStr + "  右  " + orderItems.get(currentID).order_number, 518, 60 + 20, paint);
        canvas.restore();
    }

    void drawTextPocketL(Canvas canvas) {
        canvas.save();
        canvas.rotate(-5.8f, 438, 69);
        canvas.drawRect(438, 69, 438 + 400, 69 + 22, rectPaint);
        canvas.drawText(time + "  HGW_" + orderItems.get(currentID).sizeStr + "  左  " + orderItems.get(currentID).order_number, 438, 69 + 20, paint);
        canvas.restore();
    }

    void drawTextBorderTop(Canvas canvas) {
        canvas.drawRect(100, 10, 100 + 400, 10 + 22, rectPaint);
        canvas.drawText(orderItems.get(currentID).sizeStr + "  " + time + "  " + orderItems.get(currentID).order_number, 100, 10 + 20, paint);
    }

    void drawTextborderPocketL(Canvas canvas) {
        canvas.save();
        canvas.rotate(4.1f, 686, 7);
        canvas.drawRect(686, 7, 686 + 230, 7 + 22, rectPaint);
        canvas.drawText(time + " 左袋边 " + orderItems.get(currentID).order_number, 686, 7 + 20, paint);
        canvas.restore();
    }

    void drawTextborderPocketR(Canvas canvas) {
        canvas.save();
        canvas.rotate(-4.1f, 27, 23);
        canvas.drawRect(27, 23, 27 + 230, 23 + 22, rectPaint);
        canvas.drawText(time + " 右袋边 " + orderItems.get(currentID).order_number, 27, 23 + 20, paint);
        canvas.restore();
    }


    public void remixx() {

        /*
        int margin = 120;
        Matrix matrix = new Matrix();
        int combineWidth = width_front + width_back + height_borderTop + margin * 2;
        int combineHeight = Math.max(width_borderTop, height_front + height_pocket + height_back + margin * 2);
        if (orderItems.get(currentID).sizeStr.equals("4XL")) {
            combineWidth = width_front + width_back + height_borderTop + margin * 2 - 350;
        }

        Bitmap bitmapCombine = Bitmap.createBitmap(combineWidth, combineHeight, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine= new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {
            if (MainActivity.instance.bitmaps.get(0).getWidth() != 6167) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 6167, 6167, true));
            }

            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1183, 966, 1898, 1923);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_frontR);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3083, 966, 1898, 1923);
            canvasTemp = new Canvas(bitmapTemp);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //backL
            bitmapTemp = Bitmap.createBitmap(2438, 2315, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postTranslate(-635, -3629);
            matrix.postRotate(-8.3f);
            matrix.postTranslate(-232, 282);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_backL);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + height_pocket + margin * 2, null);

            //backR
            bitmapTemp = Bitmap.createBitmap(2438, 2315, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postTranslate(-3084, -3629);
            matrix.postRotate(8.3f);
            matrix.postTranslate(252, -71);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);

            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, height_front + margin, null);

            //pocketR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1187, 907, 1079, 1298);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hgw_pocket_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //pocketL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3902, 905, 1079, 1298);
            canvasTemp = new Canvas(bitmapTemp);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket + margin, height_front + margin, null);

            //borderPocketL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1183, 968, 936, 736);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hgw_border_pocket_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextborderPocketL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderPocket, height_borderPocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin - width_borderPocket, combineHeight - height_borderPocket, null);

            //borderPocketR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4044, 966, 937, 736);
            canvasTemp = new Canvas(bitmapTemp);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextborderPocketR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderPocket, height_borderPocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_back + margin, height_front + height_back + margin * 2, null);

            //borderTop
            bitmapTemp = Bitmap.createBitmap(1615 * 2 + 1368 * 2, 562, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4683 - 1615, 2974, 1615, 562);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);

            bitmapCut = Bitmap.createBitmap(1368, 562, Bitmap.Config.ARGB_8888);
            Canvas canvasCut = new Canvas(bitmapCut);
            matrix.reset();
            matrix.postRotate(-4f);
            matrix.postTranslate(-40, 0);
            canvasCut.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1694, 154, 1405, 662), matrix, null);
            canvasTemp.drawBitmap(bitmapCut, 1615, 0, null);

            bitmapCut = Bitmap.createBitmap(1368, 562, Bitmap.Config.ARGB_8888);
            canvasCut = new Canvas(bitmapCut);
            matrix.reset();
            matrix.postRotate(4f);
            matrix.postTranslate(6, -100);
            canvasCut.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3057, 154, 1405, 662), matrix, null);
            canvasTemp.drawBitmap(bitmapCut, 1615 + 1368, 0, null);

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1585, 2974, 1615, 562);
            canvasTemp.drawBitmap(bitmapCut, 1615 + 1368 * 2, 0, null);
            bitmapCut.recycle();
            canvasTemp.drawRect(0, 0, 1615 * 2 + 1368 * 2, 562, rectBorderPaint);

            drawTextBorderTop(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderTop, height_borderTop, true);
            matrix.reset();
            matrix.postRotate(90);
            matrix.postTranslate(height_borderTop + width_front + width_back + margin * 2, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).isPPSL) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 552, 747, 1898, 1923);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_frontR);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2450, 746, 1898, 1923);
            canvasTemp = new Canvas(bitmapTemp);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //backL
            bitmapTemp = Bitmap.createBitmap(2438, 2315, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postTranslate(-10, -481);
            matrix.postRotate(-8.3f);
            matrix.postTranslate(-232, 282);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_backL);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            if (orderItems.get(currentID).sizeStr.equals("4XL")) {
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front + height_back + margin * 3, null);
            } else {
                canvasCombine.drawBitmap(bitmapTemp, 0, height_front + height_pocket + margin * 2, null);
            }


            //backR
            bitmapTemp = Bitmap.createBitmap(2438, 2315, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postTranslate(-2450, -481);
            matrix.postRotate(8.3f);
            matrix.postTranslate(252, -71);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);

            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            if (orderItems.get(currentID).sizeStr.equals("4XL")) {
                canvasCombine.drawBitmap(bitmapTemp, width_front + margin - 350, height_front + margin, null);
            } else {
                canvasCombine.drawBitmap(bitmapTemp, width_front + margin, height_front + margin, null);
            }

            //pocketR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 556, 685, 1079, 1298);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hgw_pocket_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front, null);

            //pocketL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3269, 685, 1079, 1298);
            canvasTemp = new Canvas(bitmapTemp);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket + margin, height_front + margin, null);

            //borderPocketR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 552, 746, 936, 736);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hgw_border_pocket_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, 936, 736, matrix, true);
            drawTextborderPocketR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderPocket, height_borderPocket, true);
            canvasCombine.drawBitmap(bitmapTemp, combineWidth - width_borderPocket - height_borderTop - margin, combineHeight - height_borderPocket, null);

            //borderPocketL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 3411, 746, 937, 736);
            canvasTemp = new Canvas(bitmapTemp);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, 936, 736, matrix, true);
            drawTextborderPocketL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderPocket, height_borderPocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_back + margin, height_front + height_back + margin * 2, null);

            //borderTop
            bitmapTemp = Bitmap.createBitmap(1615 * 2 + 1368 * 2, 562, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2912, 32, 1615, 562);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);

            bitmapCut = Bitmap.createBitmap(1368, 562, Bitmap.Config.ARGB_8888);
            Canvas canvasCut = new Canvas(bitmapCut);
            matrix.reset();
            matrix.postRotate(-4f);
            matrix.postTranslate(-40, 0);
            canvasCut.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 674, 230, 1405, 662), matrix, null);
            canvasTemp.drawBitmap(bitmapCut, 1615, 0, null);

            bitmapCut = Bitmap.createBitmap(1368, 562, Bitmap.Config.ARGB_8888);
            canvasCut = new Canvas(bitmapCut);
            matrix.reset();
            matrix.postRotate(4f);
            matrix.postTranslate(6, -100);
            canvasCut.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2823, 230, 1405, 662), matrix, null);
            canvasTemp.drawBitmap(bitmapCut, 1615 + 1368, 0, null);

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 375, 32, 1615, 562);
            canvasTemp.drawBitmap(bitmapCut, 1615 + 1368 * 2, 0, null);
            bitmapCut.recycle();
            canvasTemp.drawRect(0, 0, 1615 * 2 + 1368 * 2, 562, rectBorderPaint);

            drawTextBorderTop(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderTop, height_borderTop, true);
            matrix.reset();
            matrix.postRotate(90);
            if (orderItems.get(currentID).sizeStr.equals("4XL")) {
                matrix.postTranslate(height_borderTop + width_front + width_back + margin * 2 - 350, 0);
            } else {
                matrix.postTranslate(height_borderTop + width_front + width_back + margin * 2, 0);
            }
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {

            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 440, 747 - 230, 1898, 1923);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_frontR);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1561, 746 - 230, 1898, 1923);
            canvasTemp = new Canvas(bitmapTemp);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //backL
            bitmapTemp = Bitmap.createBitmap(2438, 2315, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postTranslate(-31, -481);
            matrix.postRotate(-8.3f);
            matrix.postTranslate(-232, 282);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_backL);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + height_pocket + margin * 2, null);

            //backR
            bitmapTemp = Bitmap.createBitmap(2438, 2315, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postTranslate(-1429, -481);
            matrix.postRotate(8.3f);
            matrix.postTranslate(252, -71);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);

            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, height_front + margin, null);

            //pocketR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 444, 685 - 230, 1079, 1298);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hgw_pocket_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //pocketL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2380, 685 - 230, 1079, 1298);
            canvasTemp = new Canvas(bitmapTemp);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket + margin, height_front + margin, null);

            //borderPocketL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 440, 746 - 230, 936, 736);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hgw_border_pocket_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextborderPocketL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderPocket, height_borderPocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin - width_borderPocket, width_borderTop - height_borderPocket, null);

            //borderPocketR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2522, 746 - 230, 937, 736);
            canvasTemp = new Canvas(bitmapTemp);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            drawTextborderPocketR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderPocket, height_borderPocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_back + margin, height_front + height_back + margin * 2, null);

            //borderTop
            bitmapTemp = Bitmap.createBitmap(1615 * 2 + 1368 * 2, 562, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1891, 32, 1615, 562);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);

            bitmapCut = Bitmap.createBitmap(1368, 562, Bitmap.Config.ARGB_8888);
            Canvas canvasCut = new Canvas(bitmapCut);
            matrix.reset();
            matrix.postRotate(-4f);
            matrix.postTranslate(-40, 0);
            canvasCut.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 562, 230 - 230, 1405, 662), matrix, null);
            canvasTemp.drawBitmap(bitmapCut, 1615, 0, null);

            bitmapCut = Bitmap.createBitmap(1368, 562, Bitmap.Config.ARGB_8888);
            canvasCut = new Canvas(bitmapCut);
            matrix.reset();
            matrix.postRotate(4f);
            matrix.postTranslate(6, -100);
            canvasCut.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1934, 230 - 230, 1405, 662), matrix, null);
            canvasTemp.drawBitmap(bitmapCut, 1615 + 1368, 0, null);

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 396, 32, 1615, 562);
            canvasTemp.drawBitmap(bitmapCut, 1615 + 1368 * 2, 0, null);
            bitmapCut.recycle();
            canvasTemp.drawRect(0, 0, 1615 * 2 + 1368 * 2, 562, rectBorderPaint);

            drawTextBorderTop(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderTop, height_borderTop, true);
            matrix.reset();
            matrix.postRotate(90);
            matrix.postTranslate(height_borderTop + width_front + width_back + margin * 2, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 2) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 440, 747, 1898, 1923);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_frontR);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, 0, null);

            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1561, 746, 1898, 1923);
            canvasTemp = new Canvas(bitmapTemp);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, 0, null);

            //backL
            bitmapTemp = Bitmap.createBitmap(2438, 2315, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postTranslate(-31, -481);
            matrix.postRotate(-8.3f);
            matrix.postTranslate(-232, 282);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_backL);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + height_pocket + margin * 2, null);

            //backR
            bitmapTemp = Bitmap.createBitmap(2438, 2315, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postTranslate(-1429, -481);
            matrix.postRotate(8.3f);
            matrix.postTranslate(252, -71);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);

            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + margin, height_front + margin, null);

            //pocketR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 444, 685, 1079, 1298);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hgw_pocket_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, 0, height_front + margin, null);

            //pocketL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2380, 685, 1079, 1298);
            canvasTemp = new Canvas(bitmapTemp);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_pocket + margin, height_front + margin, null);

            //borderPocketR
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 440, 746, 936, 736);
            canvasTemp = new Canvas(bitmapTemp);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hgw_border_pocket_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, 936, 736, matrix, true);
            drawTextborderPocketR(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderPocket, height_borderPocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_front + width_back + margin - width_borderPocket, width_borderTop - height_borderPocket, null);

            //borderPocketL
            bitmapTemp = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2522, 746, 937, 736);
            canvasTemp = new Canvas(bitmapTemp);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            bitmapDB.recycle();
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, 936, 736, matrix, true);
            drawTextborderPocketL(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderPocket, height_borderPocket, true);
            canvasCombine.drawBitmap(bitmapTemp, width_back + margin, height_front + height_back + margin * 2, null);

            //borderTop
            bitmapTemp = Bitmap.createBitmap(1615 * 2 + 1368 * 2, 562, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1891, 32, 1615, 562);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);

            bitmapCut = Bitmap.createBitmap(1368, 562, Bitmap.Config.ARGB_8888);
            Canvas canvasCut = new Canvas(bitmapCut);
            matrix.reset();
            matrix.postRotate(-4f);
            matrix.postTranslate(-40, 0);
            canvasCut.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 562, 230, 1405, 662), matrix, null);
            canvasTemp.drawBitmap(bitmapCut, 1615, 0, null);

            bitmapCut = Bitmap.createBitmap(1368, 562, Bitmap.Config.ARGB_8888);
            canvasCut = new Canvas(bitmapCut);
            matrix.reset();
            matrix.postRotate(4f);
            matrix.postTranslate(6, -100);
            canvasCut.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1934, 230, 1405, 662), matrix, null);
            canvasTemp.drawBitmap(bitmapCut, 1615 + 1368, 0, null);

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 396, 32, 1615, 562);
            canvasTemp.drawBitmap(bitmapCut, 1615 + 1368 * 2, 0, null);
            bitmapCut.recycle();
            canvasTemp.drawRect(0, 0, 1615 * 2 + 1368 * 2, 562, rectBorderPaint);

            drawTextBorderTop(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderTop, height_borderTop, true);
            matrix.reset();
            matrix.postRotate(90);
            matrix.postTranslate(height_borderTop + width_front + width_back + margin * 2, 0);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);
            bitmapTemp.recycle();
        }


         */


        Matrix matrix = new Matrix();
        Bitmap bitmapCombine = Bitmap.createBitmap(width_combine, height_combine, Bitmap.Config.ARGB_8888);
        Canvas canvasCombine = new Canvas(bitmapCombine);
        canvasCombine.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvasCombine.drawColor(0xffffffff);

        if (MainActivity.instance.bitmaps.get(0).getWidth() == MainActivity.instance.bitmaps.get(0).getHeight()) {//jj
            if (MainActivity.instance.bitmaps.get(0).getWidth() != 6167) {
                MainActivity.instance.bitmaps.set(0, Bitmap.createScaledBitmap(MainActivity.instance.bitmaps.get(0), 6167, 6167, true));
            }

            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(1898, 1923, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1183, -966, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_frontR);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(width_front + x_front_r, height_front + y_front_r);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            bitmapTemp = Bitmap.createBitmap(1898, 1923, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3083, -966, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(width_front + x_front_l, height_front + y_front_l);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //backL
            bitmapTemp = Bitmap.createBitmap(2438, 2315, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postTranslate(-635, -3629);
            matrix.postRotate(-8.3f);
            matrix.postTranslate(-232, 282);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_backL);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back_l, y_back_l, null);

            //backR
            bitmapTemp = Bitmap.createBitmap(2438, 2315, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postTranslate(-3084, -3629);
            matrix.postRotate(8.3f);
            matrix.postTranslate(252, -71);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);

            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back_r, y_back_r, null);

            //pocketR
            bitmapTemp = Bitmap.createBitmap(1079, 1298, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1187, -907, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hgw_pocket_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_pockrt_r, y_pockrt_r, null);

            //pocketL
            bitmapTemp = Bitmap.createBitmap(1079, 1298, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -3902, -905, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_pocket_l, y_pocket_l, null);

            //borderPocketR
            bitmapTemp = Bitmap.createBitmap(936, 736, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1183, -968, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hgw_border_pocket_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, 936, 736, matrix, true);//翻转为R
            drawTextborderPocketL(canvasTemp);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderPocket, height_borderPocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_border_r, y_border_r, null);

            //borderPocketL
            bitmapTemp = Bitmap.createBitmap(936, 736, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -4044, -966, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, 936, 736, matrix, true);//翻转为L
            drawTextborderPocketR(canvasTemp);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderPocket, height_borderPocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_border_l, y_border_l, null);

            //borderTop
            bitmapTemp = Bitmap.createBitmap(1615 * 2 + 1368 * 2, 562, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 4683 - 1615, 2974, 1615, 562);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);

            bitmapCut = Bitmap.createBitmap(1368, 562, Bitmap.Config.ARGB_8888);
            Canvas canvasCut = new Canvas(bitmapCut);
            matrix.reset();
            matrix.postRotate(-4f);
            matrix.postTranslate(-40, 0);
            canvasCut.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1694, 154, 1405, 662), matrix, null);
            canvasTemp.drawBitmap(bitmapCut, 1615, 0, null);

            bitmapCut = Bitmap.createBitmap(1368, 562, Bitmap.Config.ARGB_8888);
            canvasCut = new Canvas(bitmapCut);
            matrix.reset();
            matrix.postRotate(4f);
            matrix.postTranslate(6, -100);
            canvasCut.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 3057, 154, 1405, 662), matrix, null);
            canvasTemp.drawBitmap(bitmapCut, 1615 + 1368, 0, null);

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1585, 2974, 1615, 562);
            canvasTemp.drawBitmap(bitmapCut, 1615 + 1368 * 2, 0, null);
            bitmapCut.recycle();
            canvasTemp.drawRect(0, 0, 1615 * 2 + 1368 * 2, 562, rectBorderPaint);

            drawTextBorderTop(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderTop, height_borderTop, true);
            canvasCombine.drawBitmap(bitmapTemp, x_top, y_top, null);
            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).isPPSL) {
            //frontR
            Bitmap bitmapTemp = Bitmap.createBitmap(1898, 1923, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -552, -747, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_frontR);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(width_front + x_front_r, height_front + y_front_r);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //frontL
            bitmapTemp = Bitmap.createBitmap(1898, 1923, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -2450, -746, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(width_front + x_front_l, height_front + y_front_l);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //backL
            bitmapTemp = Bitmap.createBitmap(2438, 2315, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postTranslate(-10, -481);
            matrix.postRotate(-8.3f);
            matrix.postTranslate(-232, 282);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_backL);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back_l, y_back_l, null);

            //backR
            bitmapTemp = Bitmap.createBitmap(2438, 2315, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postTranslate(-2450, -481);
            matrix.postRotate(8.3f);
            matrix.postTranslate(252, -71);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);

            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back_r, y_back_r, null);

            //pocketR
            bitmapTemp = Bitmap.createBitmap(1079, 1298, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -556, -685, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hgw_pocket_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_pockrt_r, y_pockrt_r, null);

            //pocketL
            bitmapTemp = Bitmap.createBitmap(1079, 1298, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -3269, -685, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_pocket_l, y_pocket_l, null);

            //borderPocketR
            bitmapTemp = Bitmap.createBitmap(936, 736, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -552, -746, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hgw_border_pocket_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, 936, 736, matrix, true);//翻转为R
            drawTextborderPocketR(canvasTemp);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderPocket, height_borderPocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_border_r, y_border_r, null);

            //borderPocketL
            bitmapTemp = Bitmap.createBitmap(936, 736, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -3411, -746, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, 936, 736, matrix, true);//翻转为L
            drawTextborderPocketL(canvasTemp);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderPocket, height_borderPocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_border_l, y_border_l, null);

            //borderTop
            bitmapTemp = Bitmap.createBitmap(1615 * 2 + 1368 * 2, 562, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 2912, 32, 1615, 562);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);

            bitmapCut = Bitmap.createBitmap(1368, 562, Bitmap.Config.ARGB_8888);
            Canvas canvasCut = new Canvas(bitmapCut);
            matrix.reset();
            matrix.postRotate(-4f);
            matrix.postTranslate(-40, 0);
            canvasCut.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 674, 230, 1405, 662), matrix, null);
            canvasTemp.drawBitmap(bitmapCut, 1615, 0, null);

            bitmapCut = Bitmap.createBitmap(1368, 562, Bitmap.Config.ARGB_8888);
            canvasCut = new Canvas(bitmapCut);
            matrix.reset();
            matrix.postRotate(4f);
            matrix.postTranslate(6, -100);
            canvasCut.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 2823, 230, 1405, 662), matrix, null);
            canvasTemp.drawBitmap(bitmapCut, 1615 + 1368, 0, null);

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 375, 32, 1615, 562);
            canvasTemp.drawBitmap(bitmapCut, 1615 + 1368 * 2, 0, null);
            bitmapCut.recycle();
            canvasTemp.drawRect(0, 0, 1615 * 2 + 1368 * 2, 562, rectBorderPaint);

            drawTextBorderTop(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderTop, height_borderTop, true);
            canvasCombine.drawBitmap(bitmapTemp, x_top, y_top, null);
            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(1898, 1923, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -440, -517, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_frontR);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(width_front + x_front_r, height_front + y_front_r);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            bitmapTemp = Bitmap.createBitmap(1898, 1923, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -1561, -516, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(width_front + x_front_l, height_front + y_front_l);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //backL
            bitmapTemp = Bitmap.createBitmap(2438, 2315, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postTranslate(-31, -481);
            matrix.postRotate(-8.3f);
            matrix.postTranslate(-232, 282);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_backL);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back_l, y_back_l, null);

            //backR
            bitmapTemp = Bitmap.createBitmap(2438, 2315, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postTranslate(-1429, -481);
            matrix.postRotate(8.3f);
            matrix.postTranslate(252, -71);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);

            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back_r, y_back_r, null);

            //pocketR
            bitmapTemp = Bitmap.createBitmap(1079, 1298, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -444, -455, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hgw_pocket_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_pockrt_r, y_pockrt_r, null);

            //pocketL
            bitmapTemp = Bitmap.createBitmap(1079, 1298, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2380, -455, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_pocket_l, y_pocket_l, null);

            //borderPocketR
            bitmapTemp = Bitmap.createBitmap(936, 736, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -440, -516, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hgw_border_pocket_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, 936, 736, matrix, true);//翻转为R
            drawTextborderPocketR(canvasTemp);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderPocket, height_borderPocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_border_r, y_border_r, null);

            //borderPocketL
            bitmapTemp = Bitmap.createBitmap(936, 736, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), -2522, -516, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, 936, 736, matrix, true);//翻转为L
            drawTextborderPocketR(canvasTemp);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderPocket, height_borderPocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_border_l, y_border_l, null);

            //borderTop
            bitmapTemp = Bitmap.createBitmap(1615 * 2 + 1368 * 2, 562, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1891, 32, 1615, 562);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);

            bitmapCut = Bitmap.createBitmap(1368, 562, Bitmap.Config.ARGB_8888);
            Canvas canvasCut = new Canvas(bitmapCut);
            matrix.reset();
            matrix.postRotate(-4f);
            matrix.postTranslate(-40, 0);
            canvasCut.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 562, 230 - 230, 1405, 662), matrix, null);
            canvasTemp.drawBitmap(bitmapCut, 1615, 0, null);

            bitmapCut = Bitmap.createBitmap(1368, 562, Bitmap.Config.ARGB_8888);
            canvasCut = new Canvas(bitmapCut);
            matrix.reset();
            matrix.postRotate(4f);
            matrix.postTranslate(6, -100);
            canvasCut.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1934, 230 - 230, 1405, 662), matrix, null);
            canvasTemp.drawBitmap(bitmapCut, 1615 + 1368, 0, null);

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 396, 32, 1615, 562);
            canvasTemp.drawBitmap(bitmapCut, 1615 + 1368 * 2, 0, null);
            bitmapCut.recycle();
            canvasTemp.drawRect(0, 0, 1615 * 2 + 1368 * 2, 562, rectBorderPaint);

            drawTextBorderTop(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderTop, height_borderTop, true);
            canvasCombine.drawBitmap(bitmapTemp, x_top, y_top, null);
            bitmapTemp.recycle();
            bitmapDB.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 2) {
            //front
            Bitmap bitmapTemp = Bitmap.createBitmap(1898, 1923, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -440, -747, null);
            Bitmap bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_frontR);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(width_front + x_front_r, height_front + y_front_r);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            bitmapTemp = Bitmap.createBitmap(1898, 1923, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -1561, -746, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextFrontL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_front, height_front, true);
            matrix.reset();
            matrix.postRotate(180);
            matrix.postTranslate(width_front + x_front_l, height_front + y_front_l);
            canvasCombine.drawBitmap(bitmapTemp, matrix, null);

            //backL
            bitmapTemp = Bitmap.createBitmap(2438, 2315, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postTranslate(-31, -481);
            matrix.postRotate(-8.3f);
            matrix.postTranslate(-232, 282);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), id_backL);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back_l, y_back_l, null);

            //backR
            bitmapTemp = Bitmap.createBitmap(2438, 2315, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            matrix.reset();
            matrix.postTranslate(-1429, -481);
            matrix.postRotate(8.3f);
            matrix.postTranslate(252, -71);
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(0), matrix, null);

            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextBackR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_back, height_back, true);
            canvasCombine.drawBitmap(bitmapTemp, x_back_r, y_back_r, null);

            //pocketR
            bitmapTemp = Bitmap.createBitmap(1079, 1298, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -444, -685, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hgw_pocket_r);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketR(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_pockrt_r, y_pockrt_r, null);

            //pocketL
            bitmapTemp = Bitmap.createBitmap(1079, 1298, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -2380, -685, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            drawTextPocketL(canvasTemp);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_pocket, height_pocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_pocket_l, y_pocket_l, null);

            //borderPocketR
            bitmapTemp = Bitmap.createBitmap(936, 736, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -440, -746, null);
            bitmapDB = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.hgw_border_pocket_l);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, 936, 736, matrix, true);
            drawTextborderPocketL(canvasTemp);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderPocket, height_borderPocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_border_r, y_border_r, null);

            //borderPocketR
            bitmapTemp = Bitmap.createBitmap(936, 736, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasTemp.drawBitmap(MainActivity.instance.bitmaps.get(1), -2522, -746, null);
            canvasTemp.drawBitmap(bitmapDB, 0, 0, null);
            matrix.reset();
            matrix.postScale(-1, 1);
            bitmapTemp = Bitmap.createBitmap(bitmapTemp, 0, 0, 936, 736, matrix, true);
            drawTextborderPocketR(canvasTemp);
            bitmapDB = Bitmap.createBitmap(bitmapDB, 0, 0, bitmapDB.getWidth(), bitmapDB.getHeight(), matrix, true);
            bitmapTemp = BitmapToPng.cut(bitmapTemp, bitmapDB);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderPocket, height_borderPocket, true);
            canvasCombine.drawBitmap(bitmapTemp, x_border_l, y_border_l, null);

            //borderTop
            bitmapTemp = Bitmap.createBitmap(1615 * 2 + 1368 * 2, 562, Bitmap.Config.ARGB_8888);
            canvasTemp = new Canvas(bitmapTemp);
            canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

            Bitmap bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 1891, 32, 1615, 562);
            canvasTemp.drawBitmap(bitmapCut, 0, 0, null);

            bitmapCut = Bitmap.createBitmap(1368, 562, Bitmap.Config.ARGB_8888);
            Canvas canvasCut = new Canvas(bitmapCut);
            matrix.reset();
            matrix.postRotate(-4f);
            matrix.postTranslate(-40, 0);
            canvasCut.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 562, 230, 1405, 662), matrix, null);
            canvasTemp.drawBitmap(bitmapCut, 1615, 0, null);

            bitmapCut = Bitmap.createBitmap(1368, 562, Bitmap.Config.ARGB_8888);
            canvasCut = new Canvas(bitmapCut);
            matrix.reset();
            matrix.postRotate(4f);
            matrix.postTranslate(6, -100);
            canvasCut.drawBitmap(Bitmap.createBitmap(MainActivity.instance.bitmaps.get(1), 1934, 230, 1405, 662), matrix, null);
            canvasTemp.drawBitmap(bitmapCut, 1615 + 1368, 0, null);

            bitmapCut = Bitmap.createBitmap(MainActivity.instance.bitmaps.get(0), 396, 32, 1615, 562);
            canvasTemp.drawBitmap(bitmapCut, 1615 + 1368 * 2, 0, null);
            bitmapCut.recycle();
            canvasTemp.drawRect(0, 0, 1615 * 2 + 1368 * 2, 562, rectBorderPaint);

            drawTextBorderTop(canvasTemp);
            bitmapTemp = Bitmap.createScaledBitmap(bitmapTemp, width_borderTop, height_borderTop, true);
            canvasCombine.drawBitmap(bitmapTemp, x_top, y_top, null);
            bitmapTemp.recycle();
            bitmapDB.recycle();
        }



        String nameCombine = orderItems.get(currentID).nameStr + strPlus + ".jpg";
        String pathSave;
        if (MainActivity.instance.cb_classify.isChecked()) {
            pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
        } else
            pathSave = sdCardPath + "/生产图/" + childPath + "/";
        if (!new File(pathSave).exists())
            new File(pathSave).mkdirs();
        File fileSave = new File(pathSave + nameCombine);
        BitmapToJpg.save(bitmapCombine, fileSave, 148);
        bitmapCombine.recycle();


        try {
//            matrix.reset();
//            matrix.postRotate(-90, bitmapCombine.getWidth() / 2, bitmapCombine.getHeight() / 2);
//            bitmapCombine = Bitmap.createBitmap(bitmapCombine, 0, 0, bitmapCombine.getWidth(), bitmapCombine.getHeight(), matrix, true);


            //写入excel
            String writePath = sdCardPath + "/生产图/" + childPath + "/生产单.xls";
            File fileWrite = new File(writePath);
            if (!fileWrite.exists()) {
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
            WritableWorkbook workbook = Workbook.createWorkbook(fileWrite, book);
            WritableSheet sheet = workbook.getSheet(0);
            Label label0 = new Label(0, currentID + 1, orderItems.get(currentID).order_number + orderItems.get(currentID).sku);
            sheet.addCell(label0);
            Label label1 = new Label(1, currentID + 1, orderItems.get(currentID).sku);
            sheet.addCell(label1);
            int num = orderItems.get(currentID).num;
            Number number2 = new Number(2, currentID + 1, num);
            sheet.addCell(number2);
            Label label3 = new Label(3, currentID + 1, orderItems.get(currentID).customer);
            sheet.addCell(label3);
            Label label4 = new Label(4, currentID + 1, MainActivity.instance.orderDate_Excel);
            sheet.addCell(label4);
            Label label6 = new Label(6, currentID + 1, "平台大货");
            sheet.addCell(label6);

            workbook.write();
            workbook.close();

        } catch (Exception e) {
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

    public void checkremix() {
        if (MainActivity.instance.tb_auto.isChecked()) {
            remix();
        }
    }

    void setSize(String size) {
        switch (size) {
            case "XS":
                width_front = 1631;
                height_front = 1745;
                width_back = 2171;
                height_back = 2140;
                width_pocket = 1037;
                height_pocket = 1244;
                width_borderTop = 4984;
                height_borderTop = 562;
                width_borderPocket = 907;
                height_borderPocket = 706;
                id_frontR = R.drawable.hgw_front_r_s;
                id_backL = R.drawable.hgw_back_l_s;

                width_combine = 8081;
                height_combine = 2760;
                x_back_l = 0;
                y_back_l = 0;
                x_back_r = 4787;
                y_back_r = 0;
                x_front_l = 1810;
                y_front_l = 0;
                x_front_r = 3497;
                y_front_r = 0;
                x_pocket_l = 7045;
                y_pocket_l = 0;
                x_pockrt_r = 7045;
                y_pockrt_r = 1337;
                x_border_r = 6325;
                y_border_r = 2054;
                x_border_l = 5364;
                y_border_l = 2054;
                x_top = 0;
                y_top = 2199;
                break;
            case "S":
                width_front = 1750;
                height_front = 1804;
                width_back = 2290;
                height_back = 2199;
                width_pocket = 1037;
                height_pocket = 1244;
                width_borderTop = 5457;
                height_borderTop = 562;
                width_borderPocket = 907;
                height_borderPocket = 706;
                id_frontR = R.drawable.hgw_front_r_s;
                id_backL = R.drawable.hgw_back_l_s;

                width_combine = 8521;
                height_combine = 3112;
                x_back_l = 0;
                y_back_l = 0;
                x_back_r = 6231;
                y_back_r = 0;
                x_front_l = 1934;
                y_front_l = 0;
                x_front_r = 4830;
                y_front_r = 0;
                x_pocket_l = 3747;
                y_pocket_l = 0;
                x_pockrt_r = 3747;
                y_pockrt_r = 1263;
                x_border_r = 6524;
                y_border_r = 2406;
                x_border_l = 5574;
                y_border_l = 2406;
                x_top = 0;
                y_top = 2551;
                break;
            case "M":
                width_front = 1868;
                height_front = 1864;
                width_back = 2408;
                height_back = 2256;
                width_pocket = 1079;
                height_pocket = 1298;
                width_borderTop = 5929;
                height_borderTop = 562;
                width_borderPocket = 937;
                height_borderPocket = 736;
                id_frontR = R.drawable.hgw_front_r_s;
                id_backL = R.drawable.hgw_back_l_s;

                width_combine = 8351;
                height_combine = 3250;
                x_back_l = 0;
                y_back_l = 0;
                x_back_r = 3924;
                y_back_r = 0;
                x_front_l = 2056;
                y_front_l = 0;
                x_front_r = 6476;
                y_front_r = 0;
                x_pocket_l = 6125;
                y_pocket_l = 1917;
                x_pockrt_r = 7273;
                y_pockrt_r = 1917;
                x_border_r = 3388;
                y_border_r = 1892;
                x_border_l = 2408;
                y_border_l = 1891;
                x_top = 0;
                y_top = 2689;
                break;
            case "L":
                width_front = 1958;
                height_front = 1923;
                width_back = 2498;
                height_back = 2315;
                width_pocket = 1079;
                height_pocket = 1298;
                width_borderTop = 6283;
                height_borderTop = 562;
                width_borderPocket = 936;
                height_borderPocket = 736;
                id_frontR = R.drawable.hgw_front_r_xl;
                id_backL = R.drawable.hgw_back_l_xl;

                width_combine = 8553;
                height_combine = 3318;
                x_back_l = 0;
                y_back_l = 0;
                x_back_r = 3885;
                y_back_r = 0;
                x_front_l = 2150;
                y_front_l = 0;
                x_front_r = 6450;
                y_front_r = 0;
                x_pocket_l = 6351;
                y_pocket_l = 2008;
                x_pockrt_r = 7475;
                y_pockrt_r = 2020;
                x_border_r = 3341;
                y_border_r = 1973;
                x_border_l = 2329;
                y_border_l = 1973;
                x_top = 0;
                y_top = 2757;
                break;
            case "XL":
                width_front = 2077;
                height_front = 2012;
                width_back = 2617;
                height_back = 2408;
                width_pocket = 1127;
                height_pocket = 1383;
                width_borderTop = 6756;
                height_borderTop = 562;
                width_borderPocket = 965;
                height_borderPocket = 765;
                id_frontR = R.drawable.hgw_front_r_xl;
                id_backL = R.drawable.hgw_back_l_xl;

                width_combine = 8504;
                height_combine = 4277;
                x_back_l = 0;
                y_back_l = 0;
                x_back_r = 5887;
                y_back_r = 1244;
                x_front_l = 2290;
                y_front_l = 0;
                x_front_r = 4205;
                y_front_r = 995;
                x_pocket_l = 2617;
                y_pocket_l = 2052;
                x_pockrt_r = 7377;
                y_pockrt_r = 0;
                x_border_r = 5555;
                y_border_r = 0;
                x_border_l = 4492;
                y_border_l = 0;
                x_top = 0;
                y_top = 3716;
                break;
            case "2XL":
                width_front = 2166;
                height_front = 2101;
                width_back = 2706;
                height_back = 2499;
                width_pocket = 1134;
                height_pocket = 1413;
                width_borderTop = 7110;
                height_borderTop = 562;
                width_borderPocket = 965;
                height_borderPocket = 765;
                id_frontR = R.drawable.hgw_front_r_xl;
                id_backL = R.drawable.hgw_back_l_xl;

                width_combine = 8460;
                height_combine = 4875;
                x_back_l = 0;
                y_back_l = 0;
                x_back_r = 5754;
                y_back_r = 0;
                x_front_l = 2472;
                y_front_l = 0;
                x_front_r = 3645;
                y_front_r = 2137;
                x_pocket_l = 0;
                y_pocket_l = 2499;
                x_pockrt_r = 1216;
                y_pockrt_r = 2499;
                x_border_r = 7004;
                y_border_r = 2499;
                x_border_l = 5939;
                y_border_l = 2499;
                x_top = 0;
                y_top = 4314;
                break;
            case "3XL":
                width_front = 2374;
                height_front = 2249;
                width_back = 2914;
                height_back = 2588;
                width_pocket = 1153;
                height_pocket = 1502;
                width_borderTop = 7937;
                height_borderTop = 562;
                width_borderPocket = 965;
                height_borderPocket = 819;
                id_frontR = R.drawable.hgw_front_r_3xl;
                id_backL = R.drawable.hgw_back_l_3xl;

                width_combine = 7937;
                height_combine = 5492;
                x_back_l = 0;
                y_back_l = 0;
                x_back_r = 4662;
                y_back_r = 0;
                x_front_l = 2482;
                y_front_l = 0;
                x_front_r = 0;
                y_front_r = 3243;
                x_pocket_l = 2482;
                y_pocket_l = 3243;
                x_pockrt_r = 3678;
                y_pockrt_r = 3243;
                x_border_r = 6080;
                y_border_r = 3331;
                x_border_l = 5026;
                y_border_l = 3331;
                x_top = 0;
                y_top = 2632;
                break;
            case "4XL":
                width_front = 2527;
                height_front = 2344;
                width_back = 3071;
                height_back = 2678;
                width_pocket = 1160;
                height_pocket = 1533;
                width_borderTop = 8527;
                height_borderTop = 562;
                width_borderPocket = 965;
                height_borderPocket = 819;
                id_frontR = R.drawable.hgw_front_r_3xl;
                id_backL = R.drawable.hgw_back_l_3xl;

                width_combine = 8562;
                height_combine = 5699;
                x_back_l = 0;
                y_back_l = 0;
                x_back_r = 4991;
                y_back_r = 0;
                x_front_l = 2718;
                y_front_l = 0;
                x_front_r = 0;
                y_front_r = 3355;
                x_pocket_l = 2655;
                y_pocket_l = 3343;
                x_pockrt_r = 3950;
                y_pockrt_r = 3343;
                x_border_r = 3942;
                y_border_r = 4876;
                x_border_l = 2906;
                y_border_l = 4876;
                x_top = 0;
                y_top = 2735;
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }
    }

    public void showDialogSizeWrong(final String order_number) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog dialog_finish;
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTransBackGround);
                dialog_finish = builder.create();
                dialog_finish.setCancelable(false);
                dialog_finish.show();
                View view_dialog = LayoutInflater.from(context).inflate(R.layout.item_dialog_finish, null);
                dialog_finish.setContentView(view_dialog);
                TextView tv_title = (TextView) view_dialog.findViewById(R.id.tv_dialog_title);
                TextView tv_content = (TextView) view_dialog.findViewById(R.id.tv_dialog_content);
                Button bt_yes = (Button) view_dialog.findViewById(R.id.bt_dialog_yes);

                tv_title.setText("错误！");
                tv_content.setText("单号：" + order_number + "没有这个尺码");
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
