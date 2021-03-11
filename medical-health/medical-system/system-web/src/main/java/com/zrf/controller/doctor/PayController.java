package com.zrf.controller.doctor;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.zrf.config.pay.AliPayConfig;
import com.zrf.constants.Constants;
import com.zrf.service.OrderChargeService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PathVariable;
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
@Log4j2
public class PayController {

    @Reference
    private OrderChargeService orderChargeService;

    @RequestMapping("callback/{orderId}")
    public void callback(@PathVariable String orderId, HttpServletRequest request) {
        Map<String, String> parameterMap = this.getParameterMap(request);
        String tradeStatus = parameterMap.get("trade_status");
        System.out.println("trade_status:" + tradeStatus);
        System.out.println("异步回调参数:"+ JSON.toJSONString(parameterMap));
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            try {
                //只有支付宝调用我们系统的接口才能去更新系统订单状态
                boolean isVerified = AlipaySignature.rsaCheckV1(parameterMap, AliPayConfig.alipayPublicKey, AliPayConfig.charset, AliPayConfig.signType);
                if (isVerified) {
                    log.info("异步通知验证签名结果成功");
                    String refundFee = parameterMap.get("refund_fee");
                    // 如果refund_fee字段不为空则说明是退费的回调
                    if (StringUtils.isNotBlank(refundFee)) {
                        log.info("退费成功，退费的子订单id为：{}", parameterMap.get("out_biz_no"));
                        log.info("退款的金额为：{}", refundFee);
                    } else {
                        // 否则说明是支付的回调
                        String tradeNo = parameterMap.get("trade_no");
                        log.info("收费成功，交易id为：{}", tradeNo);
                        orderChargeService.paySuccess(orderId, tradeNo, Constants.PAY_TYPE_1);
                    }
                } else {
                    log.error("异步通知验证签名结果失败");
                }
            } catch (AlipayApiException e) {
                e.printStackTrace();
                log.error("验证签名出现异常");
            }
        } else if ("WAIT_BUYER_PAY".equals(tradeStatus)) {
            log.error("二维码已扫描，等待支付");
        }
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
