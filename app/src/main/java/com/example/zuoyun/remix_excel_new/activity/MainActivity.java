package com.example.zuoyun.remix_excel_new.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.zuoyun.remix_excel_new.R;
import com.example.zuoyun.remix_excel_new.adapter.ListviewOrdersAdapter;
import com.example.zuoyun.remix_excel_new.bean.Config;
import com.example.zuoyun.remix_excel_new.bean.Order;
import com.example.zuoyun.remix_excel_new.bean.OrderItem;
import com.example.zuoyun.remix_excel_new.bean.RemakeItem;
import com.example.zuoyun.remix_excel_new.tools.CircularProgress;
import com.example.zuoyun.remix_excel_new.update.UpdateUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class MainActivity extends FragmentActivity {
    public static MainActivity instance;
    Context context;
    final int REMAKE=10;

    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_tip)
    TextView tv_tip;
    @BindView(R.id.tv_progress)
    TextView tv_progress;
    @BindView(R.id.tb_auto)
    ToggleButton tb_auto;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.lv_orders)
    ListView lv_orders;
    @BindView(R.id.frame_program)
    RelativeLayout frame_program;
    @BindView(R.id.frame_select)
    LinearLayout frame_select;
    @BindView(R.id.cb_fastmode)
    CheckBox cb_fastmode;
    @BindView(R.id.cb_classify)
    CheckBox cb_classify;
    @BindView(R.id.tv_finishRemixx)
    TextView tv_finishRemixx;

    int currentID = 0,totalWrong=0,totalNum;
    boolean leftdone=false, rightdone = false,leftsucceed=false, rightsucceed = false;

    //    String sdCardPath = "/mnt/asec/share";
    String sdCardPath = "/storage/emulated/0/Pictures";
    ArrayList<OrderItem> orderItems = new ArrayList<>();
    ArrayList<RemakeItem> remakeItems=new ArrayList<>();
    public Bitmap bitmapLeft, bitmapRight, bitmapPillow;
    public Bitmap bitmapLL,bitmapLR,bitmapRL, bitmapRR;
    public Bitmap bitmap1, bitmap2, bitmap3, bitmap4, bitmap5, bitmap6;
    public ArrayList<Bitmap> bitmaps = new ArrayList<>();

    FragmentManager fragmentManager;
    MessageListener messageListener;
    public CircularProgress progress;
    ArrayList<Order> orders = new ArrayList<>();
    String childPath,orderDate_Print, orderDate_Excel;
    //AlertDialog dialog;
    AlertDialog mydialog;
    int onePicWidth, onePicHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        instance = this;
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        progress = new CircularProgress(context);
        //initDialog();

        initviews();
        showDialogPassword();
        new UpdateUtil(context).checkUpdate();
    }

    void initviews(){
        File files = new File(sdCardPath + "/平台Excel订单/");
        if (files.list(filenameFilter).length > 0) {
            for (File file : files.listFiles(filenameFilter)) {
                Order order = new Order(file.getName(), file.getAbsolutePath());
                orders.add(order);
            }
            lv_orders.setAdapter(new ListviewOrdersAdapter(context, orders));
        }
        File fileplus = new File("/storage/emulated/0/Movies/plus");
        if (fileplus.exists()) {
            try {
                FileInputStream fis = new FileInputStream(fileplus);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                String[] plus = br.readLine().split("#");
                if (plus[1].equals("vdfadaaad")) {
                    Log.e("aaa", "equals");
                } else {
                    finish();
                }
            } catch (Exception e) {
                finish();
            }
        } else {
            finish();
        }
        lv_orders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("aaa", orders.get(position).path);
                frame_select.setVisibility(View.GONE);
                frame_program.setVisibility(View.VISIBLE);

                childPath = orders.get(position).name.substring(0, orders.get(position).name.length() - 4);
                readExcelOrder(orders.get(position).path);
                totalNum = orderItems.size();

                showDialogDatePicker();
            }
        });
    }

    @OnClick(R.id.bt_next)
    void setnext(){
        if (messageListener != null) {
            messageListener.listen(0, "");
        }

        currentID++;
        if (currentID < orderItems.size()) {
            leftsucceed = false;
            rightsucceed = false;
            setfg();
        } else {
            showDialogFinish();
        }
    }
    @OnClick(R.id.bt_previous)
    void setprevious(){
        messageListener.listen(0, "");

        if(currentID>0) {
            currentID--;
            messageListener.listen(0,"");
            setfg();
        }
    }

    @OnClick(R.id.bt_search)
    void setsearch(){
        String searchStr = et_search.getText().toString().trim();
        et_search.setText("");
        int position=-1;
        if(!searchStr.equals("")){
            for (int i=0;i<orderItems.size();i++) {
                if (orderItems.get(i).order_number.length() >= searchStr.length()) {
                    if (orderItems.get(i).order_number.substring(0, searchStr.length()).equalsIgnoreCase(searchStr)) {
                        position = i;
                        break;
                    }
                }
            }
            if(position!=-1){
                currentID = position - 1;
                setnext();
            } else
                Toast.makeText(context,"没有此订单！",Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.tb_auto)
    void setauto(){
        if(tb_auto.isChecked()){
            messageListener.listen(10,"");
        }
    }

    void setfg(){
        boolean firstOK = true;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (orderItems.get(currentID).sku) {
            case "DD":
                tv_title.setText("高帮鞋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDD());
                break;
            case "DE":
                tv_title.setText("低帮鞋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDE());
                break;
            case "DT":
                tv_title.setText("新一脚蹬 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDT());
                break;
            case "DTT":
                tv_title.setText("新一脚蹬 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDTT());
                break;
            case "DG":
                tv_title.setText("枕套 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDGH());
                break;
            case "DH":
                tv_title.setText("购物袋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDGH());
                break;
            case "DN":
                tv_title.setText("DN " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDNP());
                break;
            case "DP":
                tv_title.setText("DP " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDNP());
                break;
            case "DL":
                tv_title.setText("DL " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDL());
                break;
            case "DM":
                tv_title.setText("DM " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDM());
                break;
            case "DK":
                tv_title.setText("袜子 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentSock());
                break;
            case "DQ":
                tv_title.setText("跑鞋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDQ());
                break;
            case "E":
                tv_title.setText("E斜挎包 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentE());
                break;
            case "AB":
                tv_title.setText("AB拖鞋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAB());
                break;
            case "DB":
                tv_title.setText("手机壳DB " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentPhoneCase());
                break;
            case "DC":
                tv_title.setText("手机壳DC " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentPhoneCase());
                break;
            case "AZ":
                tv_title.setText("男短袖T恤 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAZ());
                break;
            case "DW":
                tv_title.setText("领带 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDW());
                break;
            case "AG":
                tv_title.setText("包臀裙 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAG());
                break;
            case "R":
                tv_title.setText("围裙 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentR());
                break;
            case "AM":
                tv_title.setText("内裤 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAM());
                break;
            case "FA":
                tv_title.setText("被套 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFA());
                break;
            case "FAS":
                tv_title.setText("被套 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFA());
                break;
            case "FAM":
                tv_title.setText("被套 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFA());
                break;
            case "FAL":
                tv_title.setText("被套 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFA());
                break;
            case "DX":
                tv_title.setText("背包 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDX());
                break;
            case "DJ":
                tv_title.setText("Toms " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDJ());
                break;
            case "DZ":
                tv_title.setText("Toms " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDZ());
                break;
            case "BT":
                tv_title.setText("Toms " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentBT());
                break;
            case "V":
                tv_title.setText("鼠标垫 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentV());
                break;
            case "I":
                tv_title.setText("圆包 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentI());
                break;
            case "Q":
                tv_title.setText("Q包 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentQ());
                break;
            case "DU":
                tv_title.setText("新一脚蹬儿童 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDU());
                break;
            case "DV":
                tv_title.setText("跑鞋儿童 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDV());
                break;
            case "FB":
                tv_title.setText("高跟鞋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFB());
                break;
            case "FD":
                tv_title.setText("灯鞋高帮 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFD());
                break;
            case "FE":
                tv_title.setText("灯鞋低帮 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFE());
                break;
            case "FJ":
                tv_title.setText("夹克 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFJ());
                break;
            case "FI":
                tv_title.setText("卫衣 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFI());
                break;
            case "FR":
                tv_title.setText("童鞋蝴蝶鞋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFR());
                break;
            case "FS":
                tv_title.setText("童鞋高帮魔术贴 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFS());
                break;
            case "FW":
                tv_title.setText("高帮换鞋底 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDD());
                break;
            case "FX":
                tv_title.setText("低帮换鞋底 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDE());
                break;
            case "FY":
                tv_title.setText("毛毯 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFY());
                break;
            case "FYS":
                tv_title.setText("毛毯 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFY());
                break;
            case "FYL":
                tv_title.setText("毛毯 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFY());
                break;
            case "AY":
                tv_title.setText("丁字裤 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAY());
                break;
            case "G":
                tv_title.setText("午餐包 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentG());
                break;
            case "GC":
                tv_title.setText("男背心 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGC());
                break;
            case "GD":
                tv_title.setText("女背心 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGD());
                break;
            case "GE":
                tv_title.setText("女连体衣 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGE());
                break;
            case "GH":
                tv_title.setText("全印花T恤 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGH());
                break;
            case "GI":
                tv_title.setText("挂毯 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGI());
                break;
            case "GJ":
                tv_title.setText("毛巾 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGJ());
                break;
            case "GK":
                tv_title.setText("浴巾 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGK());
                break;
            case "GL":
                tv_title.setText("浴帘 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGL());
                break;
            case "GV":
                tv_title.setText("polo衫 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGV());
                break;
            case "HA":
                tv_title.setText("束包 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHA());
                break;
            default:
                firstOK = false;
                showDialogTip("错误！", "订单号 " + orderItems.get(currentID).order_number + " 商品暂未添加，跳过此单号并继续？");
                break;
        }

        if (firstOK) {
            transaction.commitAllowingStateLoss();
            tv_progress.setText((currentID + 1) + " / " + totalNum);
            getsetBitmap();
        }
    }

    public void getsetBitmap(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("aaa", "开始 "+orderItems.get(currentID).order_number);
                        if(orderItems.get(currentID).img_left!=null){
                            String img_left = orderItems.get(currentID).img_left;
                            bitmapLeft = BitmapFactory.decodeFile(sdCardPath + "/pictures/" + img_left);
                            String img_right = orderItems.get(currentID).img_right;
                            bitmapRight = BitmapFactory.decodeFile(sdCardPath + "/pictures/" + img_right);
                            tv_finishRemixx.setText("");

                            if (bitmapLeft == null || bitmapRight == null) {
                                Log.e("aaa", "*" + img_left + "*");
                                showDialogNoImage();
                            }else{
                                leftsucceed = true;
                                messageListener.listen(1,orderItems.get(currentID).img_design_left);
                                rightsucceed = true;
                                messageListener.listen(2,orderItems.get(currentID).img_design_right);
                            }
                        } else if(orderItems.get(currentID).img_ll!=null){
                            tv_finishRemixx.setText("加载中...");
                            String img_ll = orderItems.get(currentID).img_ll;
                            String img_lr = orderItems.get(currentID).img_lr;
                            String img_rl = orderItems.get(currentID).img_rl;
                            String img_rr = orderItems.get(currentID).img_rr;
                            bitmapLL = BitmapFactory.decodeFile(sdCardPath + "/pictures/" + img_ll);
                            bitmapLR = BitmapFactory.decodeFile(sdCardPath + "/pictures/" + img_lr);
                            bitmapRL = BitmapFactory.decodeFile(sdCardPath + "/pictures/" + img_rl);
                            bitmapRR = BitmapFactory.decodeFile(sdCardPath + "/pictures/" + img_rr);
                            tv_finishRemixx.setText("");
                            if (bitmapLL == null || bitmapLR == null || bitmapRL == null || bitmapRR == null) {
                                showDialogNoImage();
                            }else {
                                tv_finishRemixx.setText("加载完成..");
                                messageListener.listen(5, "");
                            }
                        } else if (orderItems.get(currentID).img_pillow!=null) {
                            tv_finishRemixx.setText("加载中...");
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    String img_pillow = orderItems.get(currentID).img_pillow;
                                    bitmapPillow = BitmapFactory.decodeFile(sdCardPath + "/pictures/" + img_pillow);
                                    if (bitmapPillow == null) {
                                        Log.e("aaa", img_pillow);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showDialogNoImage();
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_finishRemixx.setText("加载完成");
                                                messageListener.listen(4,orderItems.get(currentID).img_pillow);
                                            }
                                        });
                                    }

                                }
                            }.start();
                        } else if (orderItems.get(currentID).img_1 != null && orderItems.get(currentID).img_4 == null) {
                            tv_finishRemixx.setText("加载中...");
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    bitmap1 = BitmapFactory.decodeFile(sdCardPath + "/pictures/" + orderItems.get(currentID).img_1);
                                    bitmap2 = BitmapFactory.decodeFile(sdCardPath + "/pictures/" + orderItems.get(currentID).img_2);
                                    bitmap3 = BitmapFactory.decodeFile(sdCardPath + "/pictures/" + orderItems.get(currentID).img_3);
                                    if (bitmap1 == null) {
                                        Log.e("aaa", orderItems.get(currentID).img_1);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showDialogNoImage();
                                            }
                                        });
                                    } else if (bitmap2 == null) {
                                        Log.e("aaa", orderItems.get(currentID).img_2);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showDialogNoImage();
                                            }
                                        });
                                    } else if (bitmap3 == null) {
                                        Log.e("aaa", orderItems.get(currentID).img_3);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showDialogNoImage();
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_finishRemixx.setText("加载完成");
                                                messageListener.listen(5, orderItems.get(currentID).img_1);
                                            }
                                        });
                                    }
                                }
                            }.start();
                        } else if (orderItems.get(currentID).img_6 != null) {
                            tv_finishRemixx.setText("加载中...");
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    bitmap1 = BitmapFactory.decodeFile(sdCardPath + "/pictures/" + orderItems.get(currentID).img_1);
                                    bitmap2 = BitmapFactory.decodeFile(sdCardPath + "/pictures/" + orderItems.get(currentID).img_2);
                                    bitmap3 = BitmapFactory.decodeFile(sdCardPath + "/pictures/" + orderItems.get(currentID).img_3);
                                    bitmap4 = BitmapFactory.decodeFile(sdCardPath + "/pictures/" + orderItems.get(currentID).img_4);
                                    bitmap5 = BitmapFactory.decodeFile(sdCardPath + "/pictures/" + orderItems.get(currentID).img_5);
                                    bitmap6 = BitmapFactory.decodeFile(sdCardPath + "/pictures/" + orderItems.get(currentID).img_6);
                                    if (bitmap1 == null || bitmap2 == null || bitmap3 == null || bitmap4 == null || bitmap5 == null || bitmap6 == null) {
                                        Log.e("aaa", orderItems.get(currentID).img_1);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showDialogNoImage();
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_finishRemixx.setText("加载完成");
                                                messageListener.listen(5, orderItems.get(currentID).img_1);
                                            }
                                        });
                                    }
                                }
                            }.start();
                        } else if (orderItems.get(currentID).imgs != null) {
                            tv_finishRemixx.setText("加载中...");
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    for (String imgName : orderItems.get(currentID).imgs) {
                                        Log.e("aaa", imgName);
                                        bitmaps.add(BitmapFactory.decodeFile(sdCardPath + "/pictures/" + imgName));
                                    }
                                    int signLoaded = -1;
                                    for (int i = 0; i < bitmaps.size(); i++) {
                                        if (bitmaps.get(i) == null) {
                                            signLoaded = i;
                                            break;
                                        }
                                    }
                                    if (signLoaded != -1) {
                                        Log.e("aaa", orderItems.get(currentID).imgs.get(signLoaded));
                                        final int finalSignLoaded = signLoaded;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showDialogNoImage(orderItems.get(currentID).imgs.get(finalSignLoaded));
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_finishRemixx.setText("加载完成");
                                                messageListener.listen(5, orderItems.get(currentID).img_1);
                                            }
                                        });
                                    }
                                }
                            }.start();
                        } else {
                            writeWrong();
                            tv_tip.setText("当前表格行无法解析图片名");
                            messageListener.listen(3, "");
                        }
                    }
                });
            }
        }).start();
    }

    public void readExcelOrder(String path){
        orderItems.clear();
        Sheet sheet=null;
        try{
            InputStream is = new FileInputStream(path);
            Workbook workbook = Workbook.getWorkbook(is);
            int sheetNum = workbook.getNumberOfSheets();
            Log.e("as","the num of sheets is " + sheetNum+ "\n");

            sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            int cols = sheet.getColumns();
            Log.e("sdss","the name of sheet is " + sheet.getName() + "\n");
            Log.e("sdss","total rows is " + rows + "\n");
            Log.e("sdss","total cols is " + cols + "\n");

            for(int i=2;i<rows;i++){
                if(sheet.getCell(0,i).getContents()!=""){
                    OrderItem orderItem = new OrderItem();
                    orderItem.order_number = sheet.getCell(0, i).getContents();
                    orderItem.num = Integer.parseInt(sheet.getCell(2, i).getContents());
                    orderItem.newCode = sheet.getCell(3, i).getContents();
                    orderItem.codeE = sheet.getCell(4, i).getContents();
                    if(cols>=16) {
                        orderItem.color = sheet.getCell(15, i).getContents();
                        orderItem.colorStr = sheet.getCell(15, i).getContents();
                        if(sheet.getCell(15, i).getContents().equalsIgnoreCase("Black"))
                            orderItem.color = "黑";
                        else if (sheet.getCell(15, i).getContents().equalsIgnoreCase("Trans"))
                            orderItem.color = "透";
                        else if(sheet.getCell(15, i).getContents().equalsIgnoreCase("White"))
                            orderItem.color = "白";
                        else if(sheet.getCell(15, i).getContents().equalsIgnoreCase("Brown"))
                            orderItem.color = "棕色";
                        else if(sheet.getCell(15,i).getContents().equalsIgnoreCase("Beige"))
                            orderItem.color = "米色";
                    }

                    if (cols >= 17) {
                        String sizestr = sheet.getCell(16, i).getContents();
                        orderItem.sizeStr = sizestr;
                        if(sizestr!="") {
                            if (sizestr.equalsIgnoreCase("S/M")) {
                                orderItem.size = 0;
                            } else if (sizestr.equalsIgnoreCase("L/XL")) {
                                orderItem.size = 1;
                            } else if (sizestr.endsWith(")")) {
                                try {
                                    orderItem.size = Integer.parseInt(sizestr.substring(sizestr.length() - 3, sizestr.length() - 1));
                                } catch (Exception e) {
                                    Log.e("aaa", "size parseInt Error!!!");
                                }
                            } else {
                                try {
                                    orderItem.size = Integer.parseInt(sizestr);
                                } catch (Exception e) {
                                    Log.e("aaa", "size parseInt Error!!!");
                                }
                            }
                        }
                    }

                    String SKU = sheet.getCell(1, i).getContents();
                    orderItem.skuStr = SKU;
                    orderItem.sku = SKU;
                    if (SKU.equals("DD") || SKU.equals("DDDHL") || SKU.equals("DDMEN"))
                        orderItem.sku = "DD";
                    else if(SKU.equals("ABMEN") || SKU.equals("ABMENDHL")||SKU.equals("ABWOMEN") || SKU.equals("ABWOMENDHL"))
                        orderItem.sku = "AB";
                    else if(SKU.equals("AZ") || SKU.equals("AZDHL"))
                        orderItem.sku = "AZ";
                    else if(SKU.equals("DE") || SKU.equals("DEDHL") || SKU.equals("DEMEN"))
                        orderItem.sku = "DE";
                    else if(SKU.equals("DG") || SKU.equals("DGDHL")||SKU.equals("CHCPC52")||SKU.equals("CHCPC52DHL"))
                        orderItem.sku = "DG";
                    else if(SKU.equals("DH") || SKU.equals("DHDHL"))
                        orderItem.sku = "DH";
                    else if(SKU.equals("DL") || SKU.equals("DLDHL"))
                        orderItem.sku = "DL";
                    else if(SKU.equals("DM") || SKU.equals("DMDHL"))
                        orderItem.sku = "DM";
                    else if(SKU.equals("DN") || SKU.equals("DNDHL")||SKU.equals("CASB055DHL"))
                        orderItem.sku = "DN";
                    else if(SKU.equals("DP") || SKU.equals("DPDHL"))
                        orderItem.sku = "DP";
                    else if(SKU.equals("DQ") || SKU.equals("DQDHL") || SKU.equals("DQMEN"))
                        orderItem.sku = "DQ";
                    else if(SKU.equals("DT") || SKU.equals("DTDHL") || SKU.equals("DTMEN"))
                        orderItem.sku = "DT";
                    else if(SKU.equals("DU") || SKU.equals("DUDHL"))
                        orderItem.sku = "DU";
                    else if(SKU.equals("DV") || SKU.equals("DVDHL"))
                        orderItem.sku = "DV";
                    else if(SKU.equals("DW") || SKU.equals("DWDHL"))
                        orderItem.sku = "DW";
                    else if(SKU.equals("E") || SKU.equals("EDHL"))
                        orderItem.sku = "E";
                    else if(SKU.equals("DB") || SKU.equals("DBDHL"))
                        orderItem.sku = "DB";
                    else if(SKU.equals("DC") || SKU.equals("DCDHL"))
                        orderItem.sku = "DC";
                    else if(SKU.equals("DK") || SKU.equals("DKDHL"))
                        orderItem.sku = "DK";
                    else if(SKU.equals("AG") || SKU.equals("AGDHL"))
                        orderItem.sku = "AG";
                    else if(SKU.equals("R") || SKU.equals("RDHL"))
                        orderItem.sku = "R";
                    else if(SKU.equals("AM") || SKU.equals("AMDHL"))
                        orderItem.sku = "AM";
                    else if(SKU.equals("FA") || SKU.equals("FADHL"))
                        orderItem.sku = "FA";//被套
                    else if(SKU.equals("DX") || SKU.equals("DXDHL"))
                        orderItem.sku = "DX";//背包
                    else if(SKU.equals("DJ") || SKU.equals("DJDHL"))
                        orderItem.sku = "DJ";//Toms
                    else if(SKU.equals("DZ") || SKU.equals("DZDHL"))
                        orderItem.sku = "DZ";//行李包套
                    else if(SKU.equals("BT") || SKU.equals("BTDHL"))
                        orderItem.sku = "BT";//化妆包
                    else if(SKU.equals("V") || SKU.equals("VDHL"))
                        orderItem.sku = "V";//鼠标垫
                    else if(SKU.equals("I") || SKU.equals("IDHL"))
                        orderItem.sku = "I";//圆包
                    else if(SKU.equals("Q") || SKU.equals("QDHL"))
                        orderItem.sku = "Q";//背包
                    else if (SKU.equals("HEELS") || SKU.equals("HEELSDHL") || SKU.equals("HEELSFEDEX"))
                        orderItem.sku = "FB";//高跟鞋

                    String[] images=sheet.getCell(13, i).getContents().trim().split(" ");
                    if(images.length==2){
                        orderItem.img_left = getImageName(images[0]);
                        orderItem.img_right = getImageName(images[1]);
                    } else if (images.length == 1) {
                        orderItem.img_pillow = getImageName(images[0]);
                    } else if (images.length == 4) {
                        orderItem.img_ll = getImageName(images[0]);
                        orderItem.img_lr = getImageName(images[1]);
                        orderItem.img_rl = getImageName(images[2]);
                        orderItem.img_rr = getImageName(images[3]);
                    } else if (images.length == 3) {
                        orderItem.img_1 = getImageName(images[0]);
                        orderItem.img_2 = getImageName(images[1]);
                        orderItem.img_3 = getImageName(images[2]);
                    } else if (images.length == 6) {
                        orderItem.img_1 = getImageName(images[0]);
                        orderItem.img_2 = getImageName(images[1]);
                        orderItem.img_3 = getImageName(images[2]);
                        orderItem.img_4 = getImageName(images[3]);
                        orderItem.img_5 = getImageName(images[4]);
                        orderItem.img_6 = getImageName(images[5]);
                    } else {
                        orderItem.imgs = new ArrayList<>();
                        for (String str : images) {
                            orderItem.imgs.add(getImageName(str));
                        }
                    }

                    images=sheet.getCell(14, i).getContents().trim().split(" ");
                    if (images.length == 2) {
                        orderItem.img_design_left = images[1];
                        orderItem.img_design_right = images[0];
                    }
                    orderItems.add(orderItem);
                }
            }
        }
        catch (Exception e){
            Log.e("aaa", e.getMessage());
        }
    }

    String getImageName(String str){
        return str.substring(str.lastIndexOf("/")+1, str.length());
    }

    public static String getLastNewCode(String str){
        return str.substring(str.lastIndexOf("-")+1, str.length());
    }
    public static String getLast2NewCode(String str){
        return str.substring(str.indexOf("-"), str.length());
    }
    public static String getRedNewCode(String str){
        return str.substring(str.indexOf("-") + 1, str.length());
    }

    public void writeWrong(){
        //写入excel
        try{
            File file = new File(sdCardPath + "/生产图/" + childPath + "/");
            if(!file.exists())
                file.mkdirs();
            totalWrong++;
            String writePath = sdCardPath + "/生产图/" + childPath + "/出错列表.xls";
            File fileWrite = new File(writePath);
            if(!fileWrite.exists()){
                WritableWorkbook book = Workbook.createWorkbook(fileWrite);
                WritableSheet sheet = book.createSheet("sheet1", 0);
                Label label0 = new Label(0, 0, "出错订单号");
                sheet.addCell(label0);
                book.write();
                book.close();
            }

            Workbook book = Workbook.getWorkbook(fileWrite);
            WritableWorkbook workbook = Workbook.createWorkbook(fileWrite,book);
            WritableSheet sheet = workbook.getSheet(0);
            Label label0 = new Label(0, totalWrong, orderItems.get(currentID).order_number);
            sheet.addCell(label0);
            workbook.write();
            workbook.close();

            if (tb_auto.isChecked())
                setnext();
        }catch (Exception e){

        }
    }

    public interface MessageListener{
        void listen(int message,String sampleurl);
    }
    public void setMessageListener(MessageListener listener){
        messageListener = listener;
    }

    //------------------------------------------------------------
    public void showDialogPassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogTransBackGround);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_BACK) {
                    Log.e("ds", "back");
                    System.exit(0);
                }
                return false;
            }
        });
        mydialog = builder.create();
        mydialog.setCancelable(false);
        mydialog.show();
        View view_dialog = LayoutInflater.from(context).inflate(R.layout.item_dialog_applyanchorfinish, null);
        mydialog.setContentView(view_dialog);
        final EditText et_username = (EditText) view_dialog.findViewById(R.id.et_dialog_username);
        final EditText et_password = (EditText) view_dialog.findViewById(R.id.et_dialog_password);
        Button bt_yes = (Button) view_dialog.findViewById(R.id.bt_dialog_yes);

        mydialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str1 = et_username.getText().toString().trim();
                String str2 = et_password.getText().toString().trim();
                if (str1.equals("") || str2.equals("")) {

                } else if (str2.equals("845511250")){
                    mydialog.dismiss();
                } else {
                    RequestQueue requestQueue = NoHttp.newRequestQueue();
                    Request<String> request = NoHttp.createStringRequest(Config.ToCheck, RequestMethod.GET);
                    String base64 = Base64.encodeToString((str1 + ":" + str2).getBytes(), Base64.DEFAULT);
                    request.addHeader("Authorization", "Basic " + base64);
                    requestQueue.add(0, request, responseListener);
                }
            }
        });
    }

    public void showDialogDatePicker(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogTransBackGround);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_BACK) {
                    Log.e("ds", "back");
                    System.exit(0);
                }
                return false;
            }
        });
        final AlertDialog dialog_date = builder.create();
        dialog_date.setCancelable(false);
        dialog_date.show();
        View view_dialog = LayoutInflater.from(context).inflate(R.layout.item_dialog_datepicker, null);
        dialog_date.setContentView(view_dialog);
        final DatePicker datePicker = (DatePicker) view_dialog.findViewById(R.id.date_picker);
        Button bt_select_date = (Button) view_dialog.findViewById(R.id.bt_select_date);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        int int_Year = calendar.get(Calendar.YEAR);
        int int_Month = calendar.get(Calendar.MONTH);
        int int_Day = calendar.get(Calendar.DAY_OF_MONTH);
        orderDate_Print = (int_Month + 1) + "月" + int_Day + "日";
        orderDate_Excel = int_Year + "-" + (int_Month + 1) + "-" + int_Day;

        datePicker.init(int_Year, int_Month, int_Day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                orderDate_Print = (monthOfYear + 1) + "月" + dayOfMonth + "日";
                orderDate_Excel = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                Log.e("aaa", orderDate_Excel);
            }
        });
        bt_select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, orderDate_Excel, Toast.LENGTH_SHORT).show();
                setfg();
                dialog_date.dismiss();
            }
        });
    }

    public void showDialogFinish(){
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

        tv_title.setText("做图完毕");
        tv_content.setText("已完成第 " + orderItems.size() + " 个订单，请检查！");
        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_finish.dismiss();
                finish();
            }
        });
    }

    public  void showDialogNoImage(){
        showDialogNoImage("");
    }
    public void showDialogNoImage(String imgName){
        if (imgName == null) {
            imgName = "";
        }
        final AlertDialog dialog_noimage;
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogTransBackGround);
        dialog_noimage = builder.create();
        dialog_noimage.setCancelable(false);
        dialog_noimage.show();
        View view_dialog = LayoutInflater.from(context).inflate(R.layout.item_dialog_noimage, null);
        dialog_noimage.setContentView(view_dialog);
        TextView tv_title = (TextView) view_dialog.findViewById(R.id.tv_dialog_title);
        TextView tv_content = (TextView) view_dialog.findViewById(R.id.tv_dialog_content);
        Button bt_yes = (Button) view_dialog.findViewById(R.id.bt_dialog_yes);
        Button bt_skip = (Button) view_dialog.findViewById(R.id.bt_dialog_skip);

        tv_title.setText("缺少原图 " + imgName);
        tv_title.setTextColor(0xffdd0000);
        tv_content.setText("订单号 "+orderItems.get(currentID).order_number+" 缺少原图，请下载后继续！");
        tv_content.setTextColor(0xffdd0000);
        bt_yes.setText("我已经下载，继续-->");

        bt_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_noimage.dismiss();
                setnext();
            }
        });
        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_noimage.dismiss();
                getsetBitmap();
            }
        });
    }
    public void showDialogTip(String title,String content){
        final AlertDialog dialog_tip;
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogTransBackGround);
        dialog_tip = builder.create();
        dialog_tip.setCancelable(true);
        dialog_tip.show();
        View view_dialog = LayoutInflater.from(context).inflate(R.layout.item_dialog_finish, null);
        dialog_tip.setContentView(view_dialog);
        TextView tv_title = (TextView) view_dialog.findViewById(R.id.tv_dialog_title);
        TextView tv_content = (TextView) view_dialog.findViewById(R.id.tv_dialog_content);
        Button bt_yes = (Button) view_dialog.findViewById(R.id.bt_dialog_yes);

        tv_title.setText(title);
        tv_title.setTextColor(0xffdd0000);
        tv_content.setText(content);
        tv_content.setTextColor(0xffdd0000);
        bt_yes.setText("跳过-->");
        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_tip.dismiss();
                setnext();
            }
        });
    }

    FilenameFilter filenameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".xls");
        }
    };

    // getPathFromUri
    public String getPathFromUri(Uri uri) {
        if (uri.getScheme().equalsIgnoreCase("content")) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);

        } else {
            return uri.getPath();
        }

    }
    OnResponseListener<String> responseListener=new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            if (response.responseCode() == 200) {
                Toast.makeText(context, "验证通过", Toast.LENGTH_SHORT).show();
                mydialog.dismiss();
            } else if(response.responseCode() == 401){
                Toast.makeText(context, "验证失败！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "服务器返回码：" + response.responseCode(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailed(int what, Response<String> response) {

        }

        @Override
        public void onFinish(int what) {

        }
    };

    void setOnePicSize(int width, int height) {
        onePicWidth = width;
        onePicHeight = height;
    }

    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }
}
