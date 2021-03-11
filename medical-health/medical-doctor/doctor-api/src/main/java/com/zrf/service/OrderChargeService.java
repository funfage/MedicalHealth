package com.zrf.service;

import com.zrf.domain.OrderCharge;
import com.zrf.dto.OrderChargeFormDto;

/**
 * @author 张润发
 * @date 2021/3/8
 */
public interface OrderChargeService {

    /**
     * 保存订单及详情信息
     * @param orderChargeFormDto
     */
    void saveOrderAndItems(OrderChargeFormDto orderChargeFormDto);

    /**
     * 支付成功后更新订单状态
     * @param orderId
     * @param payPlatformId 平台交易id，使用支付宝支付时需要使用
     */
    void paySuccess(String orderId, String payPlatformId);

    /**
     * 根据订单id查询订单信息
     * @param orderId
     * @return
     */
    OrderCharge queryOrderChargeByOrderId(String orderId);

    /**
     * 根据orderid删除无用的数据
     * @param orderId
     */
    void deleteOrderChargeAndItemsByOrderId(String orderId);
}
