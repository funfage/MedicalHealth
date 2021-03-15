package com.zrf.service.impl;

import com.zrf.constants.Constants;
import com.zrf.domain.Income;
import com.zrf.domain.Refund;
import com.zrf.dto.RevenueQueryDto;
import com.zrf.mapper.RevenueMapper;
import com.zrf.service.RevenueService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 张润发
 * @date 2021/3/13
 */
@Service
public class RevenueServiceImpl implements RevenueService {

    @Autowired
    private RevenueMapper revenueMapper;

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
    @Override
    public Map<String, Object> queryAllRevenueData(RevenueQueryDto revenueQueryDto) {
        Map<String, Object> allRevenueDataMap = new HashMap<>();
        // 收入合计
        Double totalRevenue = 0.00;
        // 总收入
        Double toll = 0.00;
        // 总退费
        Double refund = 0.00;
        Double cashIncome = 0.00;
        Double alipayIncome = 0.00;
        Double cashRefund = 0.00;
        Double alipayRefund = 0.00;
        Integer incomeChanelCash = 0;
        Integer incomeChanelAlipay = 0;
        // 查询支付和退款数据
        List<Income> incomes = revenueMapper.queryIncome(revenueQueryDto);
        List<Refund> refunds = revenueMapper.queryRefund(revenueQueryDto);
        for (Income income : incomes) {
            toll += income.getOrderAmount();
            if (income.getPayType().equals(Constants.PAY_TYPE_0)) {
                cashIncome += income.getOrderAmount();
                incomeChanelCash++;
            } else if (income.getPayType().equals(Constants.PAY_TYPE_1)) {
                alipayIncome += income.getOrderAmount();
                incomeChanelAlipay++;
            }
        }
        for (Refund r : refunds) {
            refund += r.getBackAmount();
            if (r.getBackType().equals(Constants.PAY_TYPE_0)) {
                cashRefund += r.getBackAmount();
            } else if (r.getBackType().equals(Constants.PAY_TYPE_1)) {
                alipayRefund += r.getBackAmount();
            }
        }
        totalRevenue = toll - refund;
        Map<String, Object> revenueObj = new HashMap<>();
        revenueObj.put("totalRevenue", totalRevenue);
        Map<String, Object> overviewMap = new HashMap<>();
        overviewMap.put("toll", toll);
        overviewMap.put("refund", refund);
        Map<String, Object> channelMap = new HashMap<>();
        channelMap.put("cashIncome", cashIncome);
        channelMap.put("alipayIncome", alipayIncome);
        channelMap.put("cashRefund", cashRefund);
        channelMap.put("alipayRefund", alipayRefund);
        revenueObj.put("overview", overviewMap);
        revenueObj.put("channel", channelMap);

        /***********************收入渠道*********************/
        Map<String, Object> revenueOverview = new HashMap<>();
        revenueOverview.put("title", "收支情况");
        List<Map<String, Object>> overviewList = new ArrayList<>();
        Map<String, Object> overviewListData1 = new HashMap<>();
        overviewListData1.put("value", toll);
        overviewListData1.put("name", "收费金额");
        Map<String, Object> overviewListData2 = new HashMap<>();
        overviewListData2.put("value", refund);
        overviewListData2.put("name", "退费金额");
        overviewList.add(overviewListData1);
        overviewList.add(overviewListData2);
        revenueOverview.put("data", overviewList);

        /***********************收入概况*********************/
        Map<String, Object> incomeChanel = new HashMap<>();
        incomeChanel.put("title", "收入概况");
        List<Map<String, Object>> chanelList = new ArrayList<>();
        Map<String, Object> chanelListData1 = new HashMap<>();
        chanelListData1.put("value", incomeChanelCash);
        chanelListData1.put("name", "现金收入");
        Map<String, Object> chanelListData2 = new HashMap<>();
        chanelListData2.put("value", incomeChanelAlipay);
        chanelListData2.put("name", "支付宝收入");
        chanelList.add(chanelListData1);
        chanelList.add(chanelListData2);
        incomeChanel.put("data", chanelList);

        /***********************退款金额和渠道*********************/
        Map<String, Object> refundMap = new HashMap<>();
        refundMap.put("title", "退款");
        List<Map<String, Object>> refundlList = new ArrayList<>();
        Map<String, Object> refundListData1 = new HashMap<>();
        refundListData1.put("value", cashRefund);
        refundListData1.put("name", "现金退款");
        Map<String, Object> refundListData2 = new HashMap<>();
        refundListData2.put("value", alipayRefund);
        refundListData2.put("name", "支付宝退款");
        refundlList.add(refundListData1);
        refundlList.add(refundListData2);
        refundMap.put("data", refundlList);

        // 所有数据集合并
        allRevenueDataMap.put("revenueObj", revenueObj);
        allRevenueDataMap.put("revenueOverview", revenueOverview);
        allRevenueDataMap.put("incomeChanel", incomeChanel);
        allRevenueDataMap.put("refundMap", refundMap);
        return allRevenueDataMap;
    }
}
