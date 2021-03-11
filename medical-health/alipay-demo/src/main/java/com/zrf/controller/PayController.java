package com.zrf.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.zrf.pay.PayConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 张润发
 * @date 2021/3/10
 */
@RestController
@RequestMapping("pay")
public class PayController {

    //输出日志
    static Log log = LogFactory.getLog("PayController");

    @PostMapping("callback/{orderId}")
    public void callback(@PathVariable String orderId, HttpServletRequest request) {
        Map<String, String> parameterMap = this.getParameterMap(request);
        String trade_status = parameterMap.get("trade_status");
        if ("TRADE_SUCCESS".equals(trade_status)) {
            try {
                boolean singVerified = AlipaySignature.rsaCheckV1(parameterMap, PayConfig.alipayPublicKey, PayConfig.charset, PayConfig.signType);
                //只有支付宝调用我们系统的接口才能去更新系统订单状态
                System.out.println("验证签名结果:" + singVerified);
                String refund_fee = parameterMap.get("refund_fee");
                // 如果refund_fee字段不为空则说明是退费的回调
                if (StringUtils.isNotBlank(refund_fee)) {
                    System.out.println("退费成功，退费的子订单id为：" + parameterMap.get("out_biz_no"));
                } else {
                    // 否则说明是支付的回调
                    System.out.println("收费成功，平台id:" + parameterMap.get("trade_no"));
                }
                System.out.println(JSON.toJSONString(parameterMap));
            } catch (AlipayApiException e) {
                e.printStackTrace();
                System.out.println("验证签名出现异常");
            }
        } else if ("WAIT_BUYER_PAY".equals(trade_status)) {
            System.out.println("二维码已扫描，等待支付");
        }
    }

    public void test() {
        /*System.out.println("orderId：" + orderId);
        System.out.println("支付宝回调方法调用成功");
        // 支付宝异步通知验签
        Map<String, String> parameterMap = getParameterMap(request);
        try {
            boolean isVerify = AlipaySignature.rsaCertCheckV1(parameterMap, "F:\\java_source\\alipay\\" + PayConfig.alipayCertPath, PayConfig.charset, PayConfig.signType);
            if (isVerify) {
                System.out.println("异步验签通过");
            }else{
                System.out.println("异步验签不通过");
            }
        } catch (AlipayApiException e) {
            System.out.println("异步验签出现异常");
            e.printStackTrace();
        }*/
    }


    /**
     * 获取request中的参数集合转Map
     * Map<String,String> parameterMap = RequestUtil.getParameterMap(request)
     *
     * @param request
     * @return
     */
    public Map<String, String> getParameterMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                if (paramValue.length() != 0) {
                    map.put(paramName, paramValue);
                }
            }
        }
        return map;
    }

}
