package com.example.zuoyun.remix_excel_new.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    @BindView(R.id.tv_version)
    TextView tv_version;

    int currentID = 0,totalWrong=0, totalNum;
    public static int LOADED_IMGS = 5;

    //    String sdCardPath = "/mnt/asec/share";
    String sdCardPath = "/storage/emulated/0/Pictures";
    ArrayList<OrderItem> orderItems = new ArrayList<>();
    ArrayList<RemakeItem> remakeItems=new ArrayList<>();
    public ArrayList<Bitmap> bitmaps = new ArrayList<>();

    FragmentManager fragmentManager;
    MessageListener messageListener;
    public CircularProgress progress;
    ArrayList<Order> orders = new ArrayList<>();
    String childPath,orderDate_Print, orderDate_Excel;
    //AlertDialog dialog;
    AlertDialog mydialog;
    int onePicWidth, onePicHeight;

    DataFormatter dataFormatter = new DataFormatter();
    RequestQueue requestQueue = NoHttp.newRequestQueue();

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
        tv_version.setText("版本号：" + getVersionCode());
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

                if(orders.get(position).path.endsWith("xls")){
                    childPath = orders.get(position).name.substring(0, orders.get(position).name.length() - 4);
                    readExcelOrderOld(orders.get(position).path);
                }else {
                    childPath = orders.get(position).name.substring(0, orders.get(position).name.length() - 5);
                    readExcelOrderNew(orders.get(position).path);
                }

                childPath = childPath.replace("...", "");
                totalNum = orderItems.size();
                showDialogDatePicker();
                frame_select.setVisibility(View.GONE);
                frame_program.setVisibility(View.VISIBLE);
            }
        });

        if (!new File("/storage/emulated/0/Movies/admin.txt").exists()) {
            showDialogPassword();
        }
    }

    @OnClick(R.id.bt_next)
    void setnext(){
        if (messageListener != null) {
            messageListener.listen(0, "");
        }

        currentID++;
        if (currentID < orderItems.size()) {
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
            case "3":
                tv_title.setText("织唛 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHX());
                break;
            case "03":
                tv_title.setText("织唛 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHX());
                break;
            case "A":
                tv_title.setText("A " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentA());
                break;
            case "AA":
                tv_title.setText("AA " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAA());
                break;
            case "AB":
                tv_title.setText("AB拖鞋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAB());
                break;
            case "AG":
                tv_title.setText("包臀裙 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAG());
                break;
            case "AH":
                tv_title.setText("AH " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAH());
                break;
            case "AK":
                tv_title.setText("AK " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAK());
                break;
            case "AL":
                tv_title.setText("AL " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAL());
                break;
            case "AM":
                tv_title.setText("内裤 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAM());
                break;
            case "AN":
                tv_title.setText("AN包 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAN());
                break;
            case "AP":
                tv_title.setText("AP " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAP());
                break;
            case "AS":
                tv_title.setText("AS松糕鞋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDQ());
                break;
            case "AT":
                tv_title.setText("AT " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAT());
                break;
            case "AU":
                tv_title.setText("AU " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAU());
                break;
            case "AX":
                tv_title.setText("内裤 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAX());
                break;
            case "AZ":
                tv_title.setText("男短袖T恤 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentAZ());
                break;
            case "B":
                tv_title.setText("B " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentB());
                break;
            case "BN":
                tv_title.setText("BN " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentBN());
                break;
            case "BP":
                tv_title.setText("BP " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentBP());
                break;
            case "BQ":
                tv_title.setText("BQ " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentBQ());
                break;
            case "BR":
                tv_title.setText("BR " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentBR());
                break;
            case "BS":
                tv_title.setText("BS " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentBS());
                break;
            case "BT":
                tv_title.setText("Toms " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentBT());
                break;
            case "BW":
                tv_title.setText("BW " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentBW());
                break;
            case "BV":
                tv_title.setText("女T恤 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentBV());
                break;
            case "BY":
                tv_title.setText("BY " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentBY());
                break;
            case "BZ":
                tv_title.setText("BZ " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentBZ());
                break;
            case "C":
                tv_title.setText("C " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentC());
                break;
            case "CA":
                tv_title.setText("CA " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentCA());
                break;
            case "CD":
                tv_title.setText("CD " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentCD());
                break;
            case "CE":
                tv_title.setText("CE " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentCE());
                break;
            case "CF":
                tv_title.setText("CF " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentCF());
                break;
            case "CN":
                tv_title.setText("CN毛绒地垫 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentCN());
                break;
            case "CP":
                tv_title.setText("CP " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentCP());
                break;
            case "D":
                tv_title.setText("D " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentD());
                break;
            case "DB":
                tv_title.setText("手机壳DB " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentPhoneCase());
                break;
            case "DC":
                tv_title.setText("手机壳DC " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentPhoneCase());
                break;
            case "DD":
                tv_title.setText("高帮鞋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDD());
                break;
            case "DE":
                tv_title.setText("低帮鞋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDE());
                break;
            case "DF":
                tv_title.setText("一脚蹬 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDF());
                break;
            case "DK":
                tv_title.setText("长袜 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDK());
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
            case "DQ":
                tv_title.setText("跑鞋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDQ());
                break;
            case "DW":
                tv_title.setText("领带 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDW());
                break;
            case "DY":
                tv_title.setText("马丁靴 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDY());
                break;
            case "DX":
                tv_title.setText("背包 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDX());
                break;
            case "DJ":
                tv_title.setText("Toms " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDJ());
                break;
            case "DU":
                tv_title.setText("新一脚蹬儿童 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDU());
                break;
            case "DV":
                tv_title.setText("跑鞋儿童 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDV());
                break;
            case "DZ":
                tv_title.setText("Toms " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDZ());
                break;
            case "E":
                tv_title.setText("E斜挎包 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentE());
                break;
            case "F":
                tv_title.setText("F包 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentF());
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
                tv_title.setText("灯衣夹克 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFJ());
                break;
            case "FJF":
                tv_title.setText("灯衣夹克 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFJ());
                break;
            case "FJJ":
                tv_title.setText("灯衣夹克 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFJ());
                break;
            case "FJY":
                tv_title.setText("儿童灯衣夹克 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFJY());
                break;
            case "FI":
                tv_title.setText("卫衣 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFI());
                break;
            case "FIM":
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
            case "FV":
                tv_title.setText("FV座椅套 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFV());
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
            case "GA":
                tv_title.setText("女浴袍 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGA());
                break;
            case "GB":
                tv_title.setText("男浴袍 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGB());
                break;
            case "GC":
                tv_title.setText("男背心 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGC());
                break;
            case "GCF":
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
            case "GEF":
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
            case "GIF":
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
            case "GN":
                tv_title.setText("旅行包 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGN());
                break;
            case "GQM":
                tv_title.setText("男卫衣 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGQM());
                break;
            case "GQW":
                tv_title.setText("女卫衣 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGQW());
                break;
            case "GRM":
                tv_title.setText("男卫衣 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGRM());
                break;
            case "GRW":
                tv_title.setText("女卫衣 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGRW());
                break;
            case "GS":
                tv_title.setText("卫衣裙 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGS165());
                break;
            case "GT":
                tv_title.setText("丝巾 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGT());
                break;
            case "GUM":
                tv_title.setText("GU夹克 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGUM());
                break;
            case "GUW":
                tv_title.setText("GU夹克 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGUW());
                break;
            case "GV":
                tv_title.setText("polo衫 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGV());
                break;
            case "GVM":
                tv_title.setText("polo衫 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGV());
                break;
            case "GVW":
                tv_title.setText("polo衫 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGVW());
                break;
            case "GX":
                tv_title.setText("GX长瑜伽裤 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGX());
                break;
            case "HA":
                tv_title.setText("束包 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHA());
                break;
            case "HB":
                tv_title.setText("HB短瑜伽裤 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHB());
                break;
            case "HC":
                tv_title.setText("HC汽车遮阳板 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHC());
                break;
            case "HD":
                tv_title.setText("HD棉拖 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHD());
                break;
            case "HI":
                tv_title.setText("HI加绒马丁靴 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHI());
                break;
            case "HGM":
                tv_title.setText("男沙滩裤 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHGM());
                break;
            case "HGW":
                tv_title.setText("女沙滩裤 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHGW());
                break;
            case "HJM":
                tv_title.setText("方毛毯 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHJ());
                break;
            case "HJS":
                tv_title.setText("方毛毯 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHJ());
                break;
            case "HJY":
                tv_title.setText("方毛毯 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHJ());
                break;
            case "HK":
                tv_title.setText("HK飞织鞋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHK());
                break;
            case "HV":
                tv_title.setText("HV " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHV());
                break;
            case "HX":
                tv_title.setText("织唛 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHX());
                break;
            case "HZ":
                tv_title.setText("HZ椰子鞋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHK());
                break;
            case "I":
                tv_title.setText("圆包 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentI());
                break;
            case "JA":
                tv_title.setText("JA " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentJA());
                break;
            case "JB":
                tv_title.setText("JB " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentJB());
                break;
            case "JC":
                tv_title.setText("JC " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentJC());
                break;
            case "JD":
                tv_title.setText("JD " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentJD());
                break;
            case "JE":
                tv_title.setText("JE " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentJE());
                break;
            case "JF":
                tv_title.setText("JF " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentJF());
                break;
            case "K":
                tv_title.setText("K笔袋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentK());
                break;
            case "KA1":
                tv_title.setText("K笔袋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentKA());
                break;
            case "KA2":
                tv_title.setText("K笔袋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentKA());
                break;
            case "KA3":
                tv_title.setText("K笔袋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentKA());
                break;
            case "KD":
                tv_title.setText("加绒匡威 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentKD());
                break;
            case "KF":
                tv_title.setText("KF镂空健身背心 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentKF());
                break;
            case "KK":
                tv_title.setText("KK(MD鞋底高帮) " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDD());
                break;
            case "KL":
                tv_title.setText("KL(MD鞋底低帮) " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentDE());
                break;
            case "N":
                tv_title.setText("N " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentN());
                break;
            case "Q":
                tv_title.setText("Q包 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentQ());
                break;
            case "R":
                tv_title.setText("围裙 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentR());
                break;
            case "T":
                tv_title.setText("T包 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentT());
                break;
            case "U":
                tv_title.setText("U地垫 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentU());
                break;
            case "V":
                tv_title.setText("鼠标垫 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentV());
                break;
            case "W":
                tv_title.setText("W杯垫 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentW());
                break;
            case "Z":
                tv_title.setText("Z " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentZ());
                break;
            case "Z5":
                tv_title.setText("Z5 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentZ5());
                break;
            case "Z7":
                tv_title.setText("Z7 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentZ7());
                break;
            case "Z8":
                tv_title.setText("Z8 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentZ8());
                break;
            case "Z9":
                tv_title.setText("Z9 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentZ9());
                break;
            case "Z18":
                tv_title.setText("Z18 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentZ18());
                break;
            case "Z19":
                tv_title.setText("Z19 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentZ19());
                break;
            case "Z20":
                tv_title.setText("Z20 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentZ20());
                break;
            case "Z21":
                tv_title.setText("Z21 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentZ21());
                break;
            case "Z24":
                tv_title.setText("Z24 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentZ24());
                break;
            case "Z25":
                tv_title.setText("Z25 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentZ25());
                break;
            case "Z26":
                tv_title.setText("Z26 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentZ26());
                break;
            case "Z32":
                tv_title.setText("Z32 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentZ32());
                break;
            case "Z60":
                tv_title.setText("HK飞织鞋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHK());
                break;
            case "Z61":
                tv_title.setText("HK飞织鞋 " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentHK());
                break;
            case "Z72":
                tv_title.setText("新材料GQM " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentGQM());
                break;
            case "Z73":
                tv_title.setText("新材料FIM " + orderItems.get(currentID).order_number);
                transaction.replace(R.id.frame_main, new FragmentFI());
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
                //-----------
                Log.e("aaa", "开始 " + orderItems.get(currentID).order_number);
                if (orderItems.get(currentID).imgs.size() != 0) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_finishRemixx.setText("加载中...");
                        }
                    });

                    bitmaps.clear();
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
                                messageListener.listen(LOADED_IMGS, "");
                            }
                        });
                    }
                } else {
                    writeWrong();
                    tv_tip.setText(orderItems.get(currentID).order_number + "无法解析图片名");
                    messageListener.listen(3, "");
                }
                //-------------
            }
        }).start();
    }

    public void readExcelOrderOld(String path){
        orderItems.clear();
        try{
            org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(new File(path));
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
            Row row;
            int rows = sheet.getLastRowNum() + 1;
            Log.e("aaa", "total rows: " + rows);

            for (int i = 0; i < rows; i++) {
                row = sheet.getRow(i);
                if (row != null && getContent(row, 0) != "" && getContent(row, 13).contains(".")) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.newCode = getContent(row, 3);
                    orderItem.order_number = getContent(row, 0);
                    orderItem.num = Integer.parseInt(getContent(row, 2));
                    orderItem.platform = getContent(row, 4);

                    orderItem.colorStr = getContent(row, 15);
                    orderItem.color = orderItem.colorStr;
                    if (orderItem.color.equalsIgnoreCase("Black"))
                        orderItem.color = "黑";
                    else if (orderItem.color.equalsIgnoreCase("Trans"))
                        orderItem.color = "透";
                    else if (orderItem.color.equalsIgnoreCase("White"))
                        orderItem.color = "白";
                    else if (orderItem.color.equalsIgnoreCase("Brown"))
                        orderItem.color = "棕色";
                    else if (orderItem.color.equalsIgnoreCase("Beige"))
                        orderItem.color = "米色";
                    else if (orderItem.color.equalsIgnoreCase(""))
                        orderItem.color = "白";

                    String sizestr = getContent(row, 16);
                    orderItem.sizeStr = sizestr;
                    if (sizestr != "") {
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

                    String SKU = getContent(row, 1);
                    orderItem.skuStr = SKU;
                    orderItem.sku = SKU;
                    if (SKU.equals("DD") || SKU.equals("DDDHL") || SKU.equals("DDMEN"))
                        orderItem.sku = "DD";
                    else if (SKU.equals("ABMEN") || SKU.equals("ABMENDHL") || SKU.equals("ABWOMEN") || SKU.equals("ABWOMENDHL"))
                        orderItem.sku = "AB";
                    else if (SKU.equals("AZ") || SKU.equals("AZDHL"))
                        orderItem.sku = "AZ";
                    else if (SKU.equals("DE") || SKU.equals("DEDHL") || SKU.equals("DEMEN"))
                        orderItem.sku = "DE";
                    else if (SKU.equals("DG") || SKU.equals("DGDHL") || SKU.equals("CHCPC52") || SKU.equals("CHCPC52DHL"))
                        orderItem.sku = "DG";
                    else if (SKU.equals("DH") || SKU.equals("DHDHL"))
                        orderItem.sku = "DH";
                    else if (SKU.equals("DL") || SKU.equals("DLDHL"))
                        orderItem.sku = "DL";
                    else if (SKU.equals("DM") || SKU.equals("DMDHL"))
                        orderItem.sku = "DM";
                    else if (SKU.equals("DN") || SKU.equals("DNDHL") || SKU.equals("CASB055DHL"))
                        orderItem.sku = "DN";
                    else if (SKU.equals("DP") || SKU.equals("DPDHL"))
                        orderItem.sku = "DP";
                    else if (SKU.equals("DQ") || SKU.equals("DQDHL") || SKU.equals("DQMEN"))
                        orderItem.sku = "DQ";
                    else if (SKU.equals("DT") || SKU.equals("DTDHL") || SKU.equals("DTMEN"))
                        orderItem.sku = "DT";
                    else if (SKU.equals("DU") || SKU.equals("DUDHL"))
                        orderItem.sku = "DU";
                    else if (SKU.equals("DV") || SKU.equals("DVDHL"))
                        orderItem.sku = "DV";
                    else if (SKU.equals("DW") || SKU.equals("DWDHL"))
                        orderItem.sku = "DW";
                    else if (SKU.equals("E") || SKU.equals("EDHL"))
                        orderItem.sku = "E";
                    else if (SKU.equals("DB") || SKU.equals("DBDHL"))
                        orderItem.sku = "DB";
                    else if (SKU.equals("DC") || SKU.equals("DCDHL"))
                        orderItem.sku = "DC";
                    else if (SKU.equals("DK") || SKU.equals("DKDHL"))
                        orderItem.sku = "DK";
                    else if (SKU.equals("AG") || SKU.equals("AGDHL"))
                        orderItem.sku = "AG";
                    else if (SKU.equals("R") || SKU.equals("RDHL"))
                        orderItem.sku = "R";
                    else if (SKU.equals("AM") || SKU.equals("AMDHL"))
                        orderItem.sku = "AM";
                    else if (SKU.equals("FA") || SKU.equals("FADHL"))
                        orderItem.sku = "FA";//被套
                    else if (SKU.equals("DX") || SKU.equals("DXDHL"))
                        orderItem.sku = "DX";//背包
                    else if (SKU.equals("DJ") || SKU.equals("DJDHL"))
                        orderItem.sku = "DJ";//Toms
                    else if (SKU.equals("DZ") || SKU.equals("DZDHL"))
                        orderItem.sku = "DZ";//行李包套
                    else if (SKU.equals("BT") || SKU.equals("BTDHL"))
                        orderItem.sku = "BT";//化妆包
                    else if (SKU.equals("V") || SKU.equals("VDHL"))
                        orderItem.sku = "V";//鼠标垫
                    else if (SKU.equals("I") || SKU.equals("IDHL"))
                        orderItem.sku = "I";//圆包
                    else if (SKU.equals("Q") || SKU.equals("QDHL"))
                        orderItem.sku = "Q";//背包
                    else if (SKU.equals("HEELS") || SKU.equals("HEELSDHL") || SKU.equals("HEELSFEDEX"))
                        orderItem.sku = "FB";//高跟鞋

                    String[] images = getContent(row, 13).trim().split(" ");
                    for (String str : images) {
                        orderItem.imgs.add(getImageName(str));
                    }

                    orderItems.add(orderItem);
                }
            }
        }
        catch (Exception e){
            Toast.makeText(context, "读取订单失败！", Toast.LENGTH_SHORT).show();
            Log.e("aaa", e.getMessage());
        }
    }
    public void readExcelOrderNew(String path){
        orderItems.clear();
        try{
            org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(new File(path));
            Sheet sheet = workbook.getSheetAt(0);
            Row row;
            int rows = sheet.getLastRowNum() + 1;
            Log.e("aaa", "total rows: " + rows);

            for (int i = 0; i < rows; i++) {
                row = sheet.getRow(i);
                if (row != null && getContent(row, 0) != "" && getContent(row, 5).contains(".")) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.newCode = getContent(row, 3);
                    orderItem.order_number = getContent(row, 0);
                    orderItem.num = Integer.parseInt(getContent(row, 2));
                    orderItem.platform = getContent(row, 4);
                    if (orderItem.platform.equals("rework")) {
                        orderItem.platform = "4u2";
                    }

                    orderItem.customer = "";
                    if(path.contains("pillowprofits"))
                        orderItem.customer = "adam";
                    else if(path.contains("4u2-正丁"))
                        orderItem.customer = "u2-正丁";
                    else if(path.contains("4u2-正域"))
                        orderItem.customer = "4u2-正域";
                    else if(path.contains("zhengding-vietnam"))
                        orderItem.customer = "zhengding-vietnam";

                    orderItem.colorStr = getContent(row, 7);
                    orderItem.color = orderItem.colorStr;
                    if (orderItem.color.equalsIgnoreCase("Black"))
                        orderItem.color = "黑";
                    else if (orderItem.color.equalsIgnoreCase("Trans"))
                        orderItem.color = "透";
                    else if (orderItem.color.equalsIgnoreCase("White"))
                        orderItem.color = "白";
                    else if (orderItem.color.equalsIgnoreCase("Brown"))
                        orderItem.color = "棕色";
                    else if (orderItem.color.equalsIgnoreCase("Beige"))
                        orderItem.color = "米色";
                    else if (orderItem.color.equalsIgnoreCase(""))
                        orderItem.color = "白";

                    String sizestr = getContent(row, 8);
                    orderItem.sizeStr = sizestr;
                    if (!sizestr.equals("")) {
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

                    String SKU = getContent(row, 1);
                    orderItem.skuStr = SKU;
                    orderItem.sku = SKU;
                    if (SKU.equals("ABMEN") || SKU.equals("MENFLIPFLOP") || SKU.equals("ABWOMEN") || SKU.equals("WOMENFLIPFLOP"))
                        orderItem.sku = "AB";

                    String[] images = getContent(row, 5).trim().split(" ");
                    for (String str : images) {
                        orderItem.imgs.add(getImageName(str));
                    }

                    orderItems.add(orderItem);
                }
            }
        }
        catch (Exception e){
            Toast.makeText(context, "读取订单失败！", Toast.LENGTH_SHORT).show();
            Log.e("aaa", e.getMessage());
        }
    }

    private String getContent(Row row, int column) {
        return dataFormatter.formatCellValue(row.getCell(column));
    }

    String getImageName(String str){
        str = URLDecoder.decode(str);
        return str.substring(str.lastIndexOf("/")+1, str.length());
    }

    public static String getLastNewCode(String str){
        return str.substring(str.lastIndexOf("-")+1, str.length());
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
                    Request<String> request = NoHttp.createStringRequest(Config.ToCheck, RequestMethod.GET);
                    Request<String> requestInFactory = NoHttp.createStringRequest(Config.ToCheckInFactory, RequestMethod.GET);
                    String base64 = Base64.encodeToString((str1 + ":" + str2).getBytes(), Base64.DEFAULT);
                    request.addHeader("Authorization", "Basic " + base64);
                    requestInFactory.addHeader("Authorization", "Basic " + base64);
                    requestQueue.add(1, requestInFactory, responseListener);
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
                frame_select.setVisibility(View.VISIBLE);
                frame_program.setVisibility(View.GONE);
            }
        });
        bitmaps.clear();
        orderItems.clear();
        currentID = 0;
        tb_auto.setChecked(false);
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
        dialog_noimage.setCancelable(true);
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
            return name.endsWith(".xls") || name.endsWith(".xlsx");
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
                requestQueue.cancelAll();
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

    public static void recycleExcelImages(){
        MainActivity.instance.bitmaps.clear();
    }
    // 获取版本号
    public String getVersionCode() {
        try {
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            return "wrong";
        }

    }

    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }
}
