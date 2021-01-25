package com.example.zuoyun.remix_excel_new.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
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

public class FragmentFA extends BaseFragment {
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

    int cutWidth_quilt, cutHeight_quilt, cutWidth_pillow, cutHeight_pillow;
    int pillow1X, pillow1Y, pillow2X, pillow2Y, quiltX, quiltY;
    int drawWidth, drawHeight;
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
        paint.setTextSize(40);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);

        paintRed = new Paint();
        paintRed.setColor(0xffff0000);
        paintRed.setTextSize(40);
        paintRed.setTypeface(Typeface.DEFAULT_BOLD);
        paintRed.setAntiAlias(true);

        paintBlue = new Paint();
        paintBlue.setColor(0xff0000ff);
        paintBlue.setTextSize(40);
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
//                    if(!MainActivity.instance.cb_fastmode.isChecked())
//                        iv_pillow.setImageBitmap(MainActivity.instance.bitmaps.get(0));
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

        Log.e("aaa", orderItems.get(currentID).imgs.size() + "");
        for(String str:orderItems.get(currentID).imgs){
            Log.e("aaa", str);
        }
    }

    public void remix(){
        new Thread(){
            @Override
            public void run() {
                super.run();

                orderItems.get(currentID).sizeStr = orderItems.get(currentID).sizeStr.replace("/", ".");
                orderItems.get(currentID).newCode = orderItems.get(currentID).newCode.replace("/", ".");
                if (MainActivity.instance.bitmaps.get(0).getWidth() == 12000) {
                    setSizeAdam(orderItems.get(currentID).sizeStr);
                } else if (MainActivity.instance.bitmaps.get(0).getWidth() == 12600) {
                    setSizeJJ(orderItems.get(currentID).sizeStr);
                } else {
                    setSize(orderItems.get(currentID).sizeStr);
                }

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

    void drawText(Canvas canvas, int left, int bottom) {
        canvas.drawRect(left, bottom - 20, left + 600, bottom, rectPaint);
        canvas.drawText("  FA枕套  " + time + "   " + orderItems.get(currentID).order_number + "    尺码：" + orderItems.get(currentID).sizeStr + "    " + orderItems.get(currentID).color, left, bottom - 2, paintSmall);
    }

    void drawTextRotateQuilt(Canvas canvas, int degree, int left, int bottom) {
        canvas.save();
        canvas.rotate(degree, (float) left, (float) bottom);
        canvas.drawRect(left, bottom - 20, left + 1000, bottom, rectPaint);
        canvas.drawText("  FA被套  " + time + "   " + orderItems.get(currentID).order_number + "    尺码：" + orderItems.get(currentID).sizeStr + "    " + orderItems.get(currentID).color + "  " + orderItems.get(currentID).newCode_short, left, bottom - 2, paintSmall);
        canvas.restore();
    }

    public void remixx(){
        int pillowWidthPrint = 3300 + (int) (43 * 3.5);
        int pillowHeightPrint = 2200 + (int) (43 * 3.5);
        if (orderItems.get(currentID).sizeStr.equals("L")) {
            pillowWidthPrint = 3960 + (int) (43 * 3.5);
        }

        if (orderItems.get(currentID).imgs.size() == 3) {
            //bitmapPrintPillow
            Bitmap bitmapPrintPillow = Bitmap.createBitmap(pillowWidthPrint + 6, pillowHeightPrint * 2 + 6, Bitmap.Config.ARGB_8888);
            Canvas canvasPrintPillow = new Canvas(bitmapPrintPillow);
            canvasPrintPillow.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasPrintPillow.drawColor(0xffffffff);

            Rect rectDraw = new Rect(0, 0, pillowWidthPrint, pillowHeightPrint);
            canvasPrintPillow.drawBitmap(MainActivity.instance.bitmaps.get(0), null, rectDraw, null);
            canvasPrintPillow.drawRect(rectDraw, rectBorderPaint);
            drawText(canvasPrintPillow, pillowWidthPrint / 2, pillowHeightPrint);

            rectDraw = new Rect(0, pillowHeightPrint + 6, pillowWidthPrint, pillowHeightPrint + 6 + pillowHeightPrint);
            if (orderItems.get(currentID).imgs.get(1).contains("printsAB")) {
                canvasPrintPillow.drawBitmap(MainActivity.instance.bitmaps.get(1), null, rectDraw, null);
            } else {
                canvasPrintPillow.drawBitmap(MainActivity.instance.bitmaps.get(2), null, rectDraw, null);
            }
            canvasPrintPillow.drawRect(rectDraw, rectBorderPaint);
            drawText(canvasPrintPillow, pillowWidthPrint / 2, pillowHeightPrint + 6 + pillowHeightPrint);

            Matrix matrix90 = new Matrix();
            matrix90.postRotate(90);
            matrix90.postTranslate(bitmapPrintPillow.getHeight(), 0);
            bitmapPrintPillow = Bitmap.createBitmap(bitmapPrintPillow, 0, 0, bitmapPrintPillow.getWidth(), bitmapPrintPillow.getHeight(), matrix90, true);

            //save pillow
            String nameCombinePillow = "枕套" + orderItems.get(currentID).nameStr + strPlus + ".jpg";
            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSavePillow = new File(pathSave + nameCombinePillow);
            BitmapToJpg.save(bitmapPrintPillow, fileSavePillow, 110);
            bitmapPrintPillow.recycle();


            //bitmapPrintQuilt
            Bitmap bitmapPrintQuilt = Bitmap.createBitmap(drawHeight, drawWidth + 6, Bitmap.Config.ARGB_8888);
            Canvas canvasPrintQuilt = new Canvas(bitmapPrintQuilt);
            canvasPrintQuilt.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasPrintQuilt.drawColor(0xffffffff);

            canvasPrintQuilt.save();
            canvasPrintQuilt.rotate(90, drawHeight, 0);
            Rect rectCut = new Rect(quiltX, quiltY, quiltX + cutWidth_quilt, quiltY + cutHeight_quilt);
            rectDraw = new Rect(drawHeight, 0, drawHeight + drawWidth, drawHeight);
            if (orderItems.get(currentID).imgs.get(2).contains("printsAC")) {
                canvasPrintQuilt.drawBitmap(MainActivity.instance.bitmaps.get(2), rectCut, rectDraw, null);
            } else {
                canvasPrintQuilt.drawBitmap(MainActivity.instance.bitmaps.get(1), rectCut, rectDraw, null);
            }
            canvasPrintQuilt.drawRect(rectDraw, rectBorderPaint);

            Bitmap bitmapTriangleBottom = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_triangle_bottom);
            Bitmap bitmapTriangleUp = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_triangle_up);
            Bitmap bitmapTriangleLeft = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_triangle_left);
            Bitmap bitmapTriangleRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_triangle_right);
            canvasPrintQuilt.drawBitmap(bitmapTriangleUp, drawHeight + drawWidth / 2, 0, null);
            canvasPrintQuilt.drawBitmap(bitmapTriangleBottom, drawHeight + drawWidth / 2, drawHeight - 17, null);
            canvasPrintQuilt.drawBitmap(bitmapTriangleLeft, drawHeight, drawHeight / 2, null);
            canvasPrintQuilt.drawBitmap(bitmapTriangleRight, drawHeight + drawWidth-17, drawHeight / 2, null);
            bitmapTriangleBottom.recycle();
            bitmapTriangleUp.recycle();
            bitmapTriangleLeft.recycle();
            bitmapTriangleRight.recycle();
            canvasPrintQuilt.restore();
            drawTextRotateQuilt(canvasPrintQuilt, 90, 0, drawWidth / 2 + 100);

            //saveQuilt
            String nameCombine = "被套" + orderItems.get(currentID).nameStr + strPlus + ".jpg";
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapPrintQuilt, fileSave, 110);
            bitmapPrintQuilt.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 12000) {//adam
            pillowWidthPrint = 3300 + (int) (43 * 3.5);
            pillowHeightPrint = 2200 + (int) (43 * 3.5);

            //bitmapPrintPillow
            Bitmap bitmapPrintPillow = Bitmap.createBitmap(pillowWidthPrint + 6, pillowHeightPrint * 2 + 6, Bitmap.Config.ARGB_8888);
            Canvas canvasPrintPillow = new Canvas(bitmapPrintPillow);
            canvasPrintPillow.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasPrintPillow.drawColor(0xffffffff);

            Rect rectCut = new Rect(pillow1X, pillow1Y, pillow1X + cutWidth_pillow, pillow1Y + cutHeight_pillow);
            Rect rectDraw = new Rect(0, 0, pillowWidthPrint, pillowHeightPrint);
            canvasPrintPillow.drawBitmap(MainActivity.instance.bitmaps.get(0), rectCut, rectDraw, null);
            canvasPrintPillow.drawRect(rectDraw, rectBorderPaint);
            drawText(canvasPrintPillow, pillowWidthPrint / 2, pillowHeightPrint - 2);

            rectCut = new Rect(pillow2X, pillow2Y, pillow2X + cutWidth_pillow, pillow2Y + cutHeight_pillow);
            rectDraw = new Rect(0, pillowHeightPrint + 6, pillowWidthPrint, pillowHeightPrint + 6 + pillowHeightPrint);
            canvasPrintPillow.drawBitmap(MainActivity.instance.bitmaps.get(0), rectCut, rectDraw, null);
            canvasPrintPillow.drawRect(rectDraw, rectBorderPaint);
            drawText(canvasPrintPillow, pillowWidthPrint / 2, pillowHeightPrint + 6 + pillowHeightPrint - 2);

            Matrix matrix90 = new Matrix();
            matrix90.postRotate(90);
            matrix90.postTranslate(bitmapPrintPillow.getHeight(), 0);
            bitmapPrintPillow = Bitmap.createBitmap(bitmapPrintPillow, 0, 0, bitmapPrintPillow.getWidth(), bitmapPrintPillow.getHeight(), matrix90, true);
            //save pillow
            String nameCombinePillow = "枕套" + orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if(MainActivity.instance.cb_classify.isChecked()){
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if(!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSavePillow = new File(pathSave + nameCombinePillow);
            BitmapToJpg.save(bitmapPrintPillow, fileSavePillow, 110);
            bitmapPrintPillow.recycle();


            //bitmapPrintQuilt
            Bitmap bitmapPrintQuilt = Bitmap.createBitmap(drawHeight, drawWidth + 6, Bitmap.Config.ARGB_8888);
            Canvas canvasPrintQuilt = new Canvas(bitmapPrintQuilt);
            canvasPrintQuilt.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasPrintQuilt.drawColor(0xffffffff);

            canvasPrintQuilt.save();
            canvasPrintQuilt.rotate(90, drawHeight, 0);
            rectCut = new Rect(quiltX, quiltY, quiltX + cutWidth_quilt, quiltY + cutHeight_quilt);
            rectDraw = new Rect(drawHeight, 0, drawHeight + drawWidth, drawHeight);
            canvasPrintQuilt.drawBitmap(MainActivity.instance.bitmaps.get(0), rectCut, rectDraw, null);
            canvasPrintQuilt.drawRect(rectDraw, rectBorderPaint);

            Bitmap bitmapTriangleBottom = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_triangle_bottom);
            Bitmap bitmapTriangleUp = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_triangle_up);
            Bitmap bitmapTriangleLeft = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_triangle_left);
            Bitmap bitmapTriangleRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_triangle_right);
            canvasPrintQuilt.drawBitmap(bitmapTriangleUp, drawHeight + drawWidth / 2, 0, null);
            canvasPrintQuilt.drawBitmap(bitmapTriangleBottom, drawHeight + drawWidth / 2, drawHeight - 17, null);
            canvasPrintQuilt.drawBitmap(bitmapTriangleLeft, drawHeight, drawHeight / 2, null);
            canvasPrintQuilt.drawBitmap(bitmapTriangleRight, drawHeight + drawWidth-17, drawHeight / 2, null);
            bitmapTriangleBottom.recycle();
            bitmapTriangleUp.recycle();
            bitmapTriangleLeft.recycle();
            bitmapTriangleRight.recycle();
            canvasPrintQuilt.restore();
            drawTextRotateQuilt(canvasPrintQuilt, 90, 0, drawWidth / 2 + 100);

            String nameCombine = "被套" + orderItems.get(currentID).nameStr + strPlus + ".jpg";

            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapPrintQuilt, fileSave, 110);
            bitmapPrintQuilt.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1 && MainActivity.instance.bitmaps.get(0).getWidth() == 12600) {//jj
            //bitmapPrintPillow
            Bitmap bitmapPrintPillow = Bitmap.createBitmap(pillowWidthPrint + 6, pillowHeightPrint * 2 + 6, Bitmap.Config.ARGB_8888);
            Canvas canvasPrintPillow = new Canvas(bitmapPrintPillow);
            canvasPrintPillow.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasPrintPillow.drawColor(0xffffffff);

            Rect rectCut = new Rect(pillow1X, pillow1Y, pillow1X + cutWidth_pillow, pillow1Y + cutHeight_pillow);
            Rect rectDraw = new Rect(0, 0, pillowWidthPrint, pillowHeightPrint);
            canvasPrintPillow.drawBitmap(MainActivity.instance.bitmaps.get(0), rectCut, rectDraw, null);
            canvasPrintPillow.drawRect(rectDraw, rectBorderPaint);
            drawText(canvasPrintPillow, pillowWidthPrint / 2, pillowHeightPrint - 2);

            rectCut = new Rect(pillow2X, pillow2Y, pillow2X + cutWidth_pillow, pillow2Y + cutHeight_pillow);
            rectDraw = new Rect(0, pillowHeightPrint + 6, pillowWidthPrint, pillowHeightPrint + 6 + pillowHeightPrint);
            canvasPrintPillow.drawBitmap(MainActivity.instance.bitmaps.get(0), rectCut, rectDraw, null);
            canvasPrintPillow.drawRect(rectDraw, rectBorderPaint);
            drawText(canvasPrintPillow, pillowWidthPrint / 2, pillowHeightPrint + 6 + pillowHeightPrint - 2);

            Matrix matrix90 = new Matrix();
            matrix90.postRotate(90);
            matrix90.postTranslate(bitmapPrintPillow.getHeight(), 0);
            bitmapPrintPillow = Bitmap.createBitmap(bitmapPrintPillow, 0, 0, bitmapPrintPillow.getWidth(), bitmapPrintPillow.getHeight(), matrix90, true);
            //save pillow
            String nameCombinePillow = "枕套(" + orderItems.get(currentID).sizeStr + ")" + orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSavePillow = new File(pathSave + nameCombinePillow);
            BitmapToJpg.save(bitmapPrintPillow, fileSavePillow, 110);
            bitmapPrintPillow.recycle();


            //bitmapPrintQuilt
            Bitmap bitmapPrintQuilt = Bitmap.createBitmap(drawHeight, drawWidth + 6, Bitmap.Config.ARGB_8888);
            Canvas canvasPrintQuilt = new Canvas(bitmapPrintQuilt);
            canvasPrintQuilt.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasPrintQuilt.drawColor(0xffffffff);

            canvasPrintQuilt.save();
            canvasPrintQuilt.rotate(90, drawHeight, 0);
            rectCut = new Rect(quiltX, quiltY, quiltX + cutWidth_quilt, quiltY + cutHeight_quilt);
            rectDraw = new Rect(drawHeight, 0, drawHeight + drawWidth, drawHeight);
            canvasPrintQuilt.drawBitmap(MainActivity.instance.bitmaps.get(0), rectCut, rectDraw, null);
            canvasPrintQuilt.drawRect(rectDraw, rectBorderPaint);

            Bitmap bitmapTriangleBottom = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_triangle_bottom);
            Bitmap bitmapTriangleUp = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_triangle_up);
            Bitmap bitmapTriangleLeft = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_triangle_left);
            Bitmap bitmapTriangleRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_triangle_right);
            canvasPrintQuilt.drawBitmap(bitmapTriangleUp, drawHeight + drawWidth / 2, 0, null);
            canvasPrintQuilt.drawBitmap(bitmapTriangleBottom, drawHeight + drawWidth / 2, drawHeight - 17, null);
            canvasPrintQuilt.drawBitmap(bitmapTriangleLeft, drawHeight, drawHeight / 2, null);
            canvasPrintQuilt.drawBitmap(bitmapTriangleRight, drawHeight + drawWidth - 17, drawHeight / 2, null);
            bitmapTriangleBottom.recycle();
            bitmapTriangleUp.recycle();
            bitmapTriangleLeft.recycle();
            bitmapTriangleRight.recycle();
            canvasPrintQuilt.restore();
            drawTextRotateQuilt(canvasPrintQuilt, 90, 0, drawWidth / 2 + 100);

            //saveQuilt
            String nameCombine = "被套(" + orderItems.get(currentID).sizeStr + ")" + orderItems.get(currentID).nameStr + strPlus + ".jpg";
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapPrintQuilt, fileSave, 110);
            bitmapPrintQuilt.recycle();
        } else if (orderItems.get(currentID).imgs.size() == 1) {//4u2
            //bitmapPrintPillow
            Bitmap bitmapPrintPillow = Bitmap.createBitmap(pillowWidthPrint + 6, pillowHeightPrint * 2 + 6, Bitmap.Config.ARGB_8888);
            Canvas canvasPrintPillow = new Canvas(bitmapPrintPillow);
            canvasPrintPillow.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvasPrintPillow.drawColor(0xffffffff);

            Rect rectCut = new Rect(pillow1X, pillow1Y, pillow1X + cutWidth_pillow, pillow1Y + cutHeight_pillow);
            Rect rectDraw = new Rect(0, 0, pillowWidthPrint, pillowHeightPrint);
            canvasPrintPillow.drawBitmap(MainActivity.instance.bitmaps.get(0), rectCut, rectDraw, null);
            canvasPrintPillow.drawRect(rectDraw, rectBorderPaint);
            drawText(canvasPrintPillow, pillowWidthPrint / 2, pillowHeightPrint - 2);

            rectCut = new Rect(pillow2X, pillow2Y, pillow2X + cutWidth_pillow, pillow2Y + cutHeight_pillow);
            rectDraw = new Rect(0, pillowHeightPrint + 6, pillowWidthPrint, pillowHeightPrint + 6 + pillowHeightPrint);
            canvasPrintPillow.drawBitmap(MainActivity.instance.bitmaps.get(0), rectCut, rectDraw, null);
            canvasPrintPillow.drawRect(rectDraw, rectBorderPaint);
            drawText(canvasPrintPillow, pillowWidthPrint / 2, pillowHeightPrint + 6 + pillowHeightPrint - 2);

            Matrix matrix90 = new Matrix();
            matrix90.postRotate(90);
            matrix90.postTranslate(bitmapPrintPillow.getHeight(), 0);
            bitmapPrintPillow = Bitmap.createBitmap(bitmapPrintPillow, 0, 0, bitmapPrintPillow.getWidth(), bitmapPrintPillow.getHeight(), matrix90, true);
            //save pillow
            String nameCombinePillow = "枕套" + orderItems.get(currentID).nameStr + strPlus + ".jpg";

            String pathSave;
            if (MainActivity.instance.cb_classify.isChecked()) {
                pathSave = sdCardPath + "/生产图/" + childPath + "/" + orderItems.get(currentID).sku + "/";
            } else
                pathSave = sdCardPath + "/生产图/" + childPath + "/";
            if (!new File(pathSave).exists())
                new File(pathSave).mkdirs();
            File fileSavePillow = new File(pathSave + nameCombinePillow);
            BitmapToJpg.save(bitmapPrintPillow, fileSavePillow, 110);
            bitmapPrintPillow.recycle();


            //bitmapPrintQuilt
            Bitmap bitmapPrintQuilt = Bitmap.createBitmap(drawHeight, drawWidth + 6, Bitmap.Config.ARGB_8888);
            Canvas canvasPrintQuilt = new Canvas(bitmapPrintQuilt);
            canvasPrintQuilt.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            canvasPrintQuilt.drawColor(0xffffffff);

            canvasPrintQuilt.save();
            canvasPrintQuilt.rotate(90, drawHeight, 0);
            rectCut = new Rect(quiltX, quiltY, quiltX + cutWidth_quilt, quiltY + cutHeight_quilt);
            rectDraw = new Rect(drawHeight, 0, drawHeight + drawWidth, drawHeight);
            canvasPrintQuilt.drawBitmap(MainActivity.instance.bitmaps.get(0), rectCut, rectDraw, null);
            canvasPrintQuilt.drawRect(rectDraw, rectBorderPaint);

            Bitmap bitmapTriangleBottom = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_triangle_bottom);
            Bitmap bitmapTriangleUp = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_triangle_up);
            Bitmap bitmapTriangleLeft = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_triangle_left);
            Bitmap bitmapTriangleRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_triangle_right);
            canvasPrintQuilt.drawBitmap(bitmapTriangleUp, drawHeight + drawWidth / 2, 0, null);
            canvasPrintQuilt.drawBitmap(bitmapTriangleBottom, drawHeight + drawWidth / 2, drawHeight - 17, null);
            canvasPrintQuilt.drawBitmap(bitmapTriangleLeft, drawHeight, drawHeight / 2, null);
            canvasPrintQuilt.drawBitmap(bitmapTriangleRight, drawHeight + drawWidth - 17, drawHeight / 2, null);
            bitmapTriangleBottom.recycle();
            bitmapTriangleUp.recycle();
            bitmapTriangleLeft.recycle();
            bitmapTriangleRight.recycle();
            canvasPrintQuilt.restore();
            drawTextRotateQuilt(canvasPrintQuilt, 90, 0, drawWidth / 2 + 100);

            //saveQuilt
            String nameCombine = "被套" + orderItems.get(currentID).nameStr + strPlus + ".jpg";
            File fileSave = new File(pathSave + nameCombine);
            BitmapToJpg.save(bitmapPrintQuilt, fileSave, 110);
            bitmapPrintQuilt.recycle();
        }


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
        if (MainActivity.instance.tb_auto.isChecked()){
            remix();
        }
    }

    void setSize(String size) {
        cutWidth_pillow = 4267;
        cutHeight_pillow = 2507;
        pillow1X = 770;
        pillow1Y = 0;
        pillow2X = 6576;
        pillow2Y = 0;
        switch (size) {
            case "S":
                cutWidth_quilt = 7653;
                cutHeight_quilt = 9853;
                quiltX = 1979;
                quiltY = 0;
                drawWidth = 7653 + (int) (43 * 0.5);//7653:176.7
                drawHeight = 9853 + (int) (43 * 4.5);
                break;
            case "M":
                cutWidth_quilt = 9853;
                cutHeight_quilt = 9853;
                quiltX = 868;
                quiltY = 0;
                drawWidth = 9853 + (int) (43 * 4.5);//9853:227.5
                drawHeight = 9853 + (int) (43 * 1.5);
                break;
            case "L":
                cutWidth_quilt = 11613;
                cutHeight_quilt = 9853;
                quiltX = 0;
                quiltY = 0;
                drawWidth = 11613 + (int) (43 * 4);//11613:268
                drawHeight = 9853 + (int) (43 * 1.5);
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }



        if (orderItems.get(currentID).imgs.size() == 1) {
            switch (size) {
                case "S":
                    quiltX = 1979;
                    quiltY = 2598;
                    break;
                case "M":
                    quiltX = 880;
                    quiltY = 2598;
                    break;
                case "L":
                    quiltX = 0;
                    quiltY = 2598;
                    break;

            }
        }
    }

    void setSizeJJ(String size) {
        cutWidth_pillow = 4267;
        cutHeight_pillow = 2507;
        pillow1X = 1263;
        pillow1Y = 74;
        pillow2X = 7069;
        pillow2Y = 74;
        switch (size) {
            case "S":
                cutWidth_quilt = 7653;
                cutHeight_quilt = 9853;
                quiltX = 2473;
                quiltY = 2672;
                drawWidth = 7653 + (int) (43 * 0.5);//7653:176.7
                drawHeight = 9853 + (int) (43 * 4.5);
                break;
            case "M":
                cutWidth_quilt = 9853;
                cutHeight_quilt = 9853;
                quiltX = 1373;
                quiltY = 2672;
                drawWidth = 9853 + (int) (43 * 4.5);//9853:227.5
                drawHeight = 9853 + (int) (43 * 1.5);
                break;
            case "L":
                cutWidth_quilt = 11613;
                cutHeight_quilt = 9853;
                quiltX = 493;
                quiltY = 2672;
                drawWidth = 11613 + (int) (43 * 4);//11613:268
                drawHeight = 9853 + (int) (43 * 1.5);
                break;
            default:
                showDialogSizeWrong(orderItems.get(currentID).order_number);
                sizeOK = false;
                break;
        }

    }

    void setSizeAdam(String size) {
        cutWidth_pillow = 3717;
        cutHeight_pillow = 2479;
        pillow1X = 1232;
        pillow1Y = 281;
        pillow2X = 7044;
        pillow2Y = 281;
        switch (size) {
            case "S":
                cutWidth_quilt = 7571;
                cutHeight_quilt = 9744;
                quiltX = 2200;
                quiltY = 2911;
                drawWidth = 7653 + (int) (43 * 0.5);//7653:176.7
                drawHeight = 9853 + (int) (43 * 4.5);
                break;
            case "M":
                cutWidth_quilt = 9744;
                cutHeight_quilt = 9744;
                quiltX = 1113;
                quiltY = 2911;
                drawWidth = 9853 + (int) (43 * 4.5);//9853:227.5
                drawHeight = 9853 + (int) (43 * 1.5);
                break;
            case "L":
                cutWidth_quilt = 11487;
                cutHeight_quilt = 9744;
                quiltX = 242;
                quiltY = 2911;
                drawWidth = 11613 + (int) (43 * 4);//11613:268
                drawHeight = 9853 + (int) (43 * 1.5);
                break;

            case "US TWIN":
                cutWidth_quilt = 7571;
                cutHeight_quilt = 9744;
                quiltX = 2200;
                quiltY = 2911;
                drawWidth = 7653 + (int) (43 * 0.5);//7653:176.7
                drawHeight = 9853 + (int) (43 * 4.5);
                break;
            case "US QUEEN.FULL":
                cutWidth_quilt = 9744;
                cutHeight_quilt = 9744;
                quiltX = 1113;
                quiltY = 2911;
                drawWidth = 9853 + (int) (43 * 4.5);//9853:227.5
                drawHeight = 9853 + (int) (43 * 1.5);
                break;
            case "US KING":
                cutWidth_quilt = 11487;
                cutHeight_quilt = 9744;
                quiltX = 242;
                quiltY = 2911;
                drawWidth = 11613 + (int) (43 * 4);//11613:268
                drawHeight = 9853 + (int) (43 * 1.5);
                break;
            case "US CALIFORNIA KING":
                cutWidth_quilt = 11487;
                cutHeight_quilt = 9744;
                quiltX = 242;
                quiltY = 2911;
                drawWidth = 11613 + (int) (43 * 4);//11613:268
                drawHeight = 9853 + (int) (43 * 1.5);
                break;

            case "AU SINGLE":
                cutWidth_quilt = 6496;
                cutHeight_quilt = 9744;
                quiltX = 2736;
                quiltY = 2911;
                drawWidth = 6063 + (int) (43 * 4);
                drawHeight = 9094 + (int) (43 * 8);
                break;
            case "AU DOUBLE":
                cutWidth_quilt = 8352;
                cutHeight_quilt = 9744;
                quiltX = 1808;
                quiltY = 2911;
                drawWidth = 7795 + (int) (43 * 4);
                drawHeight = 9094 + (int) (43 * 8);
                break;
            case "AU QUEEN":
                cutWidth_quilt = 9744;
                cutHeight_quilt = 9744;
                quiltX = 1113;
                quiltY = 2911;
                drawWidth = 9094 + (int) (43 * 7);
                drawHeight = 9094 + (int) (43 * 8);
                break;
            case "AU KING":
                cutWidth_quilt = 10629;
                cutHeight_quilt = 9744;
                quiltX = 670;
                quiltY = 2911;
                drawWidth = 10394 + (int) (43 * 8);
                drawHeight = 9528 + (int) (43 * 8);
                break;

            case "UK SINGLE":
                cutWidth_quilt = 6577;
                cutHeight_quilt = 9744;
                quiltX = 2696;
                quiltY = 2911;
                drawWidth = 5846 + (int) (43 * 4);
                drawHeight = 8661 + (int) (43 * 7);
                break;
            case "UK DOUBLE":
                cutWidth_quilt = 9744;
                cutHeight_quilt = 9744;
                quiltX = 1113;
                quiltY = 2911;
                drawWidth = 8661 + (int) (43 * 7);
                drawHeight = 8661 + (int) (43 * 7);
                break;
            case "UK KING":
                cutWidth_quilt = 10186;
                cutHeight_quilt = 9744;
                quiltX = 891;
                quiltY = 2911;
                drawWidth = 9961 + (int) (43 * 8);
                drawHeight = 9528 + (int) (43 * 8);
                break;
            case "UK SUPER KING":
                cutWidth_quilt = 11515;
                cutHeight_quilt = 9744;
                quiltX = 227;
                quiltY = 2911;
                drawWidth = 11260 + (int) (43 * 9);
                drawHeight = 9528 + (int) (43 * 8);
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
