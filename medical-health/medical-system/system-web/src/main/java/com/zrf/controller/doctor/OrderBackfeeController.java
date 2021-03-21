package com.zrf.controller.doctor;

import cn.hutool.core.bean.BeanUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zrf.aspectj.annotation.Log;
import com.zrf.aspectj.enums.BusinessType;
import com.zrf.config.pay.AliPayConfig;
import com.zrf.config.pay.PayService;
import com.zrf.constants.Constants;
import com.zrf.controller.BaseController;
import com.zrf.domain.*;
import com.zrf.dto.*;
import com.zrf.service.CareService;
import com.zrf.service.OrderBackfeeService;
import com.zrf.service.OrderChargeService;
import com.zrf.utils.IdGeneratorSnowflake;
import com.zrf.utils.ShiroSecurityUtils;
import com.zrf.vo.AjaxResult;
import com.zrf.vo.DataGridView;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author 张润发
 * @date 2021/3/8
 */
@RestController
@RequestMapping("doctor/backfee")
public class OrderBackfeeController extends BaseController {

    @Reference
    private CareService careService;

    @Reference
    private OrderChargeService orderChargeService;

    @Reference
    private OrderBackfeeService orderBackfeeService;


    /**
     * 根据挂号ID查询未支付的处方信息及详情
     */
    @GetMapping("getChargedCareHistoryByRegId/{regId}")
    @HystrixCommand
    public AjaxResult getChargedCareHistoryByRegId(@PathVariable String regId) {
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
            //根据处方ID查询已支付的处方详情列表
            List<CareOrderItem> careOrderItems = careService.queryCareOrderItemsByCoId(careOrder.getCoId(), Constants.ORDER_DETAILS_STATUS_1);
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
            return AjaxResult.fail("【" + regId + "】的挂号单没已支付的处方信息，请核对后再查询");
        } else {
            res.put("careOrders", mapList);
            return AjaxResult.success(res);
        }
    }

    /**
     * 创建现金退费订单
     */
    @PostMapping("createOrderBackfeeWithCash")
    @Log(title = "创建现金退费订单", businessType = BusinessType.OTHER)
    @HystrixCommand
    public AjaxResult createOrderBackfeeWithCash(@RequestBody @Validated OrderBackfeeFormDto orderBackfeeFormDto) {
        // 保存现金退费订单信息
        orderBackfeeFormDto.getOrderBackfeeDto().setBackType(Constants.PAY_TYPE_0);
        orderBackfeeFormDto.setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
        // 设置退费单号
        String backId = IdGeneratorSnowflake.generatorIdWithProfix(Constants.ID_PROFIX_ODB);
        orderBackfeeFormDto.getOrderBackfeeDto().setBackId(backId);
        // 找到退费之前的收费单id
        String itemId = orderBackfeeFormDto.getOrderBackfeeItemDtoList().get(0).getItemId();
        OrderChargeItem orderChargeItem = orderChargeService.queryOrderChargeItemByItemId(itemId);
        //根据订单号找到之前的支付定单对象
        OrderCharge orderCharge = orderChargeService.queryOrderChargeByOrderId(orderChargeItem.getOrderId());
        if (orderCharge == null) {
            return AjaxResult.fail("【" + orderBackfeeFormDto.getOrderBackfeeDto().getRegId() + "】的挂号单之前没有收费记录，不能使用支付宝退费，请核对后再查询");
        }
        // 设置退费订单对应的收费单id
        orderBackfeeFormDto.getOrderBackfeeDto().setOrderId(orderChargeItem.getOrderId());
        orderBackfeeService.saveOrderAndItems(orderBackfeeFormDto);
        // 因为是现金退费，所以直接更新订单状态
        orderBackfeeService.backSuccess(backId, null, Constants.PAY_TYPE_0);
        return AjaxResult.success("创建退费订单并现金退费成功");
    }

    /**
     * 创建支付宝退费订单
     */
    @PostMapping("createOrderBackfeeWithZfb")
    @Log(title = "创建支付宝退费订单", businessType = BusinessType.OTHER)
    @HystrixCommand
    public AjaxResult createOrderBackfeeWithZfb(@RequestBody @Validated OrderBackfeeFormDto orderBackfeeFormDto) {
        // 保存现金退费订单信息
        orderBackfeeFormDto.getOrderBackfeeDto().setBackType(Constants.PAY_TYPE_0);
        orderBackfeeFormDto.setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
        // 生成退费单号
        String backId = IdGeneratorSnowflake.generatorIdWithProfix(Constants.ID_PROFIX_ODB);
        orderBackfeeFormDto.getOrderBackfeeDto().setBackId(backId);
        // 找到退费之前的收费单id
        String itemId = orderBackfeeFormDto.getOrderBackfeeItemDtoList().get(0).getItemId();
        OrderChargeItem orderChargeItem = orderChargeService.queryOrderChargeItemByItemId(itemId);
        //根据订单号找到之前的支付定单对象 判断之前是否使用支付宝支付
        OrderCharge orderCharge = orderChargeService.queryOrderChargeByOrderId(orderChargeItem.getOrderId());
        if (orderCharge == null) {
            return AjaxResult.fail("【" + orderBackfeeFormDto.getOrderBackfeeDto().getRegId() + "】的挂号单之前没有收费记录，不能使用支付宝退费，请核对后再查询");
        }
        if (!orderCharge.getPayType().equals(Constants.PAY_TYPE_1)) {
            return AjaxResult.fail("【" + orderBackfeeFormDto.getOrderBackfeeDto().getRegId() + "】的挂号单之前的支付方式为现金，不能使用支付宝退费，请核对后再查询");
        }
        // 设置退费订单对应的收费单id
        orderBackfeeFormDto.getOrderBackfeeDto().setOrderId(orderChargeItem.getOrderId());
        orderBackfeeService.saveOrderAndItems(orderBackfeeFormDto);
        // 因为是支付宝退费，所以需要调用支付宝退费接口
        String outTradeNo = orderCharge.getOrderId();
        String tradeNo = orderCharge.getPayPlatformId();
        String refundAmount = orderBackfeeFormDto.getOrderBackfeeDto().getBackAmount().toString();
        String refundReason = "不想要了";
        String outRequestNo = backId;
        Map<String, Object> map = PayService.payBack(outTradeNo, tradeNo, refundAmount, refundReason, outRequestNo);
        if ("200".equals(map.get("code").toString())) {
            orderBackfeeService.backSuccess(backId, map.get("tradeNo").toString(), Constants.PAY_TYPE_1);
            return AjaxResult.success();
        } else {
            return AjaxResult.fail(map.get("msg").toString());
        }
    }

    /**
     * 分页查询所有退费单
     */
    @GetMapping("queryAllOrderBackfeeForPage")
    @HystrixCommand
    public AjaxResult queryAllOrderChargeForPage(OrderBackfeeDto orderBackfeeDto) {
        DataGridView dataGridView = orderBackfeeService.queryAllOrderBackfeeForPage(orderBackfeeDto);
        return AjaxResult.success("查询成功", dataGridView.getData(), dataGridView.getTotal());
    }

    /**
     * 根据退费单的ID查询退费详情信息
     */
    @GetMapping("queryOrderBackfeeItemByBackId/{backId}")
    @HystrixCommand
    public AjaxResult queryOrderBackfeeItemByBackId(@PathVariable String backId) {
        List<OrderBackfeeItem> list = orderBackfeeService.queryrderBackfeeItemByBackId(backId);
        return AjaxResult.success(list);
    }

}
