package com.zrf.controller.statistics;

import cn.hutool.core.date.DateUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zrf.constants.Constants;
import com.zrf.controller.BaseController;
import com.zrf.dto.RevenueQueryDto;
import com.zrf.service.RevenueService;
import com.zrf.vo.AjaxResult;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author 张润发
 * @date 2021/3/13
 */
@RestController
@RequestMapping("statistics/revenue")
public class RevenueController extends BaseController {

    @Reference
    private RevenueService revenueService;

    /**
     * 收支统计总方法
     *  // 数组结构
     *       revenueObj: {
     *         totalRevenue: 0.00, // 合计收入
     *         overview: {// 收支概况: 总收入￥0 总退费￥0
     *           toll: 0.00,
     *           refund: 0.00
     *         },
     *         channel: {// 收入渠道: 现金支付￥0 支付宝支付￥0 现金退费￥0 支付宝退费￥0
     *           cashIncome: 0.00,
     *           alipayIncome: 0.00,
     *           cashRefund: 0.00,
     *           alipayRefund: 0.00
     *         }
     *       },
     *       // 声明图表的数据
     *       revenueOverview: { // 收支概况
     *         title: '收支概况',
     *         data: [
     *           { value: 320, name: '收费金额' },
     *           { value: 240, name: '退费金额' }
     *         ]
     *       },
     *       incomeChanel: { // 收入渠道
     *         title: '收入渠道',
     *         data: [
     *           { value: 4, name: '现金收入' },
     *           { value: 1, name: '支付宝收入' }
     *         ]
     *       },
     *       refund: { // 退款金额和渠道
     *         title: '退款',
     *         data: [
     *           { value: 200, name: '现金退费' },
     *           { value: 100, name: '支付宝退费' }
     *         ]
     *       }
     */
    @GetMapping("queryAllRevenueData")
    @HystrixCommand
    public AjaxResult queryAllRevenueData(RevenueQueryDto revenueQueryDto){
        // 如果没有开始日期和结束日期就查询当天
        if (revenueQueryDto.getBeginTime() == null) {
            revenueQueryDto.setQueryDate(DateUtil.format(DateUtil.date(), Constants.DATE_FORMATTER));
        }
        Map<String, Object> res = revenueService.queryAllRevenueData(revenueQueryDto);
        return AjaxResult.success(res);
    }

}
