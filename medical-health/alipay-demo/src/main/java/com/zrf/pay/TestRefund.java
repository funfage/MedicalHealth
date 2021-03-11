package com.zrf.pay;

import java.util.Map;
import java.util.UUID;

/**
 * @author 张润发
 * @date 2021/3/10
 */
public class TestRefund {

    public static void main(String[] args) {
        String outTradeNo="OD123124124123412312312";
        String tradeNo="2021031022001416850501146286";
        String outRequestNo = UUID.randomUUID().toString();
        String refudAmount="5";
        String refudReason="不想要了";
        PayService2.payBack(outTradeNo,tradeNo,refudAmount,refudReason,outRequestNo);
    }

}
