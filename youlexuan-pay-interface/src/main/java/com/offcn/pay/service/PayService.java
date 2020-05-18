package com.offcn.pay.service;

import java.util.*;

public interface PayService {

    /*与下单请求*/
    public Map createNative(String out_trade_no,String total_fee);
    /*查看支付状态*/
    public Map queryPayStatus(String out_trade_no);

    public Map closePay(String out_trade_no);
}
