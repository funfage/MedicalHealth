package com.zrf.pay;

import java.util.Map;
import java.util.UUID;

/**
 * @author 张润发
 * @date 2021/3/8
 */
public class TestPay {

    public static void main(String[] args) {
        String outTradeNo = UUID.randomUUID().toString();
        String subject = "医疗费用";
        String totalAmount = "50";
        String undiscountableAmount = null;
        String body = "外套";
        String notifyUrl = "http://38k07q0530.wicp.vip/pay/callback/" + outTradeNo;
        Map<String, Object> pay = PayService2.pay(outTradeNo, subject, totalAmount, undiscountableAmount, body, notifyUrl);
        System.out.println(pay);
    }

}
