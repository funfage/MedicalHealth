package com.zrf.controller.doctor;

import cn.hutool.core.bean.BeanUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zrf.config.pay.AliPayConfig;
import com.zrf.config.pay.PayService;
import com.zrf.constants.Constants;
import com.zrf.controller.BaseController;
import com.zrf.domain.CareHistory;
import com.zrf.domain.CareOrder;
import com.zrf.domain.CareOrderItem;
import com.zrf.domain.OrderCharge;
import com.zrf.dto.OrderChargeFormDto;
import com.zrf.dto.OrderChargeItemDto;
import com.zrf.service.CareService;
import com.zrf.service.OrderChargeService;
import com.zrf.utils.IdGeneratorSnowflake;
import com.zrf.utils.ShiroSecurityUtils;
import com.zrf.vo.AjaxResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author 张润发
 * @date 2021/3/8
 */
@RestController
@RequestMapping("doctor/charge")
public class OrderChargeController extends BaseController {

    @Reference
    private CareService careService;

    @Reference
    private OrderChargeService orderChargeService;

    /**
     * 根据挂号ID查询未支付的处方信息及详情
     */
    @GetMapping("getNoChargeCareHistoryByRegId/{regId}")
    @HystrixCommand
    public AjaxResult getNoChargeCareHistoryByRegId(@PathVariable String regId) {
        Map<String, Object> res = new HashMap<>();
        // 根究挂号单id查询病历信息
        CareHistory careHistory = careService.queryCareHistoryByRegId(regId);
        if (null == careHistory) {
            return AjaxResult.fail("【" + regId + "】的挂号单没有对应的病例信息，请核对后再查询");
        }
        res.put("careHistory", careHistory);
        res.put("careOrders", Collections.EMPTY_LIST);
        //声明一个可以存放careOrders的集合
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<CareOrder> careOrders = careService.queryCareOrdersByChId(careHistory.getChId());
        if (careOrders.isEmpty()) {
            return AjaxResult.fail("【" + regId + "】的挂号单没相关的处方信息，请核对后再查询");
        }
        for (CareOrder careOrder : careOrders) {
            Map<String, Object> beanToMap = BeanUtil.beanToMap(careOrder);
            beanToMap.put("careOrderItems", Collections.EMPTY_LIST);
            BigDecimal allAmount = new BigDecimal("0");
            //根据处方ID查询未支持的处方详情列表
            List<CareOrderItem> careOrderItems = this.careService.queryCareOrderItemsByCoId(careOrder.getCoId(), Constants.ORDER_DETAILS_STATUS_0);
            if (!careOrderItems.isEmpty()) {
                // 重新计算总价
                for (CareOrderItem careOrderItem : careOrderItems) {
                    allAmount = allAmount.add(careOrderItem.getAmount());
                }
                beanToMap.put("careOrderItems", careOrderItems);
                beanToMap.put("allAmount", allAmount);
                mapList.add(beanToMap);
            }
        }
        if (mapList.isEmpty()) {
            return AjaxResult.fail("【" + regId + "】的挂号单没未支付的处方信息，请核对后再查询");
        } else {
            res.put("careOrders", mapList);
            return AjaxResult.success(res);
        }
    }

    /**
     * 创建现金收费订单
     */
    @PostMapping("createOrderChargeWithCash")
    public AjaxResult createOrderChargeWithCash(@RequestBody @Validated OrderChargeFormDto orderChargeFormDto) {
        // 保存订单信息
        // 现金支付
        orderChargeFormDto.getOrderChargeDto().setPayType(Constants.PAY_TYPE_0);
        orderChargeFormDto.setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
        String orderId = IdGeneratorSnowflake.generatorIdWithProfix(Constants.ID_PROFIX_ODC);
        orderChargeFormDto.getOrderChargeDto().setOrderId(orderId);
        orderChargeService.saveOrderAndItems(orderChargeFormDto);
        // 因为是现金支付，所以直接更新订单状态
        orderChargeService.paySuccess(orderId, null);
        return AjaxResult.success("创建订单并现金支付成功");
    }

    /**
     * 创建支付宝收费订单
     */
    @PostMapping("createOrderChargeWithZfb")
    public AjaxResult createOrderChargeWithZfb(@RequestBody @Validated OrderChargeFormDto orderChargeFormDto) {
        // 保存订单信息
        // 支付宝支付
        orderChargeFormDto.getOrderChargeDto().setPayType(Constants.PAY_TYPE_1);
        orderChargeFormDto.setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
        String orderId = IdGeneratorSnowflake.generatorIdWithProfix(Constants.ID_PROFIX_ODC);
        orderChargeFormDto.getOrderChargeDto().setOrderId(orderId);
        orderChargeService.saveOrderAndItems(orderChargeFormDto);
        String outTradeNo = orderId;
        String notifyUrl = AliPayConfig.notifyUrl + outTradeNo;
        String subject = Constants.ORDER_CHARGE_SUBJECT;
        String totalAmount = orderChargeFormDto.getOrderChargeDto().getOrderAmount().toString();
        String undiscountableAmount = null;
        StringBuilder body = new StringBuilder();
        List<OrderChargeItemDto> orderChargeItemDtoList = orderChargeFormDto.getOrderChargeItemDtoList();
        for (OrderChargeItemDto orderChargeItemDto : orderChargeItemDtoList) {
            body.append(orderChargeItemDto.getItemName()).append("-").append(orderChargeItemDto.getItemPrice()).append(" ");
        }
        // 调用支付宝的支付方法
        Map<String, Object> pay = PayService.pay(outTradeNo, subject, totalAmount, undiscountableAmount, body.toString(), notifyUrl);
        // 获取二维码的url
        String qrCodde = pay.get("qrCode").toString();
        if (StringUtils.isNotBlank(qrCodde)) {
            //返回支付成功的结果集
            Map<String, Object> res = new HashMap<>();
            res.put("orderId", orderId);
            res.put("allAmount", totalAmount);
            res.put("payUrl", qrCodde);
            return AjaxResult.success(res);
        } else {
            return AjaxResult.fail(pay.get("msg").toString());
        }
    }

    /**
     * 根据订单ID查询订单信息【验证是否支付成功】
     */
    @GetMapping("queryOrderChargeOrderId/{orderId}")
    public AjaxResult queryOrderChargeOrderId(@PathVariable String orderId) {
        OrderCharge orderCharge = orderChargeService.queryOrderChargeByOrderId(orderId);
        if (null == orderCharge) {
            return AjaxResult.fail("【" + orderId + "】订单号所在的订单已撤销支付");
        }
        if (!orderCharge.getPayType().equals(Constants.PAY_TYPE_1)) {
            return AjaxResult.fail("【" + orderId + "】订单号所在的订单不是支付宝支付的订单，请核对后再输入");
        }
        return AjaxResult.success(orderCharge);
    }

    /**
     * 根据订单id删除订单信息
     */
    @DeleteMapping("deleteOrderChargeAndItemsByOrderId/{orderId}")
    public AjaxResult deleteOrderChargeAndItemsByOrderId(@PathVariable String orderId) {
        orderChargeService.deleteOrderChargeAndItemsByOrderId(orderId);
        return AjaxResult.success();
    }

}
