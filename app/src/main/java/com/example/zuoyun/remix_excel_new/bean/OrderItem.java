package com.example.zuoyun.remix_excel_new.bean;

import java.util.ArrayList;

/**
 * Created by zuoyun on 2016/11/3.
 */

public class OrderItem {
    public String order_number,color,sku;
    public ArrayList<String> imgs = new ArrayList<>();
    public int num,size;
    public String colorStr;
    public String sizeStr, skuStr;
    public String newCode;
    public String newCode_short = "";
    public String platform;
    public String customer;
    public String order_id;
    public String barcode_str;
    public String remark;
    public String nameStr;
    public boolean isPPSL;
}
