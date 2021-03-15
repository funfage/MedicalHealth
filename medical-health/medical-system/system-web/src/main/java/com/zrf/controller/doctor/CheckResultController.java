package com.zrf.controller.doctor;

import cn.hutool.core.date.DateUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zrf.constants.Constants;
import com.zrf.controller.BaseController;
import com.zrf.domain.CareHistory;
import com.zrf.domain.CareOrder;
import com.zrf.domain.CareOrderItem;
import com.zrf.domain.CheckResult;
import com.zrf.dto.CheckResultDto;
import com.zrf.dto.CheckResultFormDto;
import com.zrf.service.CareService;
import com.zrf.service.CheckResultService;
import com.zrf.utils.ShiroSecurityUtils;
import com.zrf.vo.AjaxResult;
import com.zrf.vo.DataGridView;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 张润发
 * @date 2021/3/12
 */
@RestController
@RequestMapping("doctor/check")
public class CheckResultController extends BaseController {

    @Reference
    private CareService careService;

    @Reference
    private CheckResultService checkResultService;

    /**
     * 根据挂号ID查询已支付的检查处方信息及详情
     */
    @PostMapping("queryNeedCheckItem")
    public AjaxResult queryNeedCheckItem(@RequestBody CheckResultDto checkResultDto) {
        List<CareOrderItem> res = new ArrayList<>();
        // 只查询挂号单支付的检查项目
        if (StringUtils.isNotBlank(checkResultDto.getRegId())) {
            CareHistory careHistory = careService.queryCareHistoryByRegId(checkResultDto.getRegId());
            if (null == careHistory) {
                return AjaxResult.success(res);
            }
            // 获取对应的所有的处方信息
            List<CareOrder> careOrders = careService.queryCareOrdersByChId(careHistory.getChId());
            for (CareOrder careOrder : careOrders) {
                // 只查询检查处方信息
                if (careOrder.getCoType().equals(Constants.CO_TYPE_CHECK)) {
                    // 查询处方对应的所有已支付处方详情信息
                    List<CareOrderItem> careOrderItems = careService.queryCareOrderItemsByCoId(careOrder.getCoId(), Constants.ORDER_DETAILS_STATUS_1);
                    // 过滤查询条件
                    for (CareOrderItem careOrderItem : careOrderItems) {
                        if (checkResultDto.getCheckItemIds().contains(Integer.valueOf(careOrderItem.getItemRefId()))) {
                            res.add(careOrderItem);
                        }
                    }
                }
            }
            return AjaxResult.success(res);
        } else {
            // 否则返回所有已支付的检查项目
            List<CareOrderItem> careOrderItems = careService.queryCareOrderItemsByStatus(Constants.CO_TYPE_CHECK, Constants.ORDER_DETAILS_STATUS_1);
            // 过滤查询条件
            for (CareOrderItem careOrderItem : careOrderItems) {
                if (checkResultDto.getCheckItemIds().contains(Integer.valueOf(careOrderItem.getItemRefId()))) {
                    res.add(careOrderItem);
                }
            }
            return AjaxResult.success(res);
        }
    }

    /**
     * 根据检查单号查询要检查的项目详情
     */
    @GetMapping("queryCheckItemByItemId/{itemId}")
    public AjaxResult queryCheckItemByItemId(@PathVariable String itemId) {
        CareOrderItem careOrderItem = careService.queryCareOrderItemByItemId(itemId);
        // 判断检查单号的数据是否存在
        if (null == careOrderItem) {
            return AjaxResult.fail("【" + itemId + "】的检查单号的数据不存在，请核对后再查询");
        }
        // 判断检查单是否已支付
        if (!careOrderItem.getStatus().equals(Constants.ORDER_DETAILS_STATUS_1)) {
            return AjaxResult.fail("【" + itemId + "】的检查单号的没有支付，请支付后再来检查");
        }
        // 判断检查单的类型是否为项目检查类型
        if (!careOrderItem.getItemType().equals(Constants.CO_TYPE_CHECK)) {
            return AjaxResult.fail("【" + itemId + "】的检查单号不是检查项目，请核对后再查询");
        }
        CareOrder careOrder = careService.queryCareOrderByCoId(careOrderItem.getCoId());
        CareHistory careHistory = careService.queryCareHistoryByChId(careOrder.getChId());
        Map<String, Object> res = new HashMap<>();
        res.put("item", careOrderItem);
        res.put("careOrder", careOrder);
        res.put("careHistory", careHistory);
        return AjaxResult.success(res);
    }

    /**
     * 开始检查
     */
    @PostMapping("startCheck/{itemId}")
    public AjaxResult startCheck(@PathVariable String itemId) {
        CareOrderItem careOrderItem = careService.queryCareOrderItemByItemId(itemId);
        if (careOrderItem == null) {
            return AjaxResult.fail("【" + itemId + "】的检查单号的数据不存在，请核对后再查询");
        }
        if (!careOrderItem.getStatus().equals(Constants.ORDER_DETAILS_STATUS_1)) {
            return AjaxResult.fail("【" + itemId + "】的检查单号的没有支付，请支付后再来检查");
        }
        if (!careOrderItem.getItemType().equals(Constants.CO_TYPE_CHECK)) {
            return AjaxResult.fail("【" + itemId + "】的单号不是检查项目，请核对后再查询");
        }
        CareOrder careOrder = careService.queryCareOrderByCoId(careOrderItem.getCoId());
        CareHistory careHistory = careService.queryCareHistoryByChId(careOrder.getChId());
        CheckResult checkResult = new CheckResult();
        checkResult.setItemId(itemId);
        checkResult.setCheckItemId(Integer.valueOf(careOrderItem.getItemRefId()));
        checkResult.setCheckItemName(careOrderItem.getItemName());
        checkResult.setPatientId(careOrder.getPatientId());
        checkResult.setPatientName(careOrder.getPatientName());
        checkResult.setPrice(careOrderItem.getPrice());
        checkResult.setRegId(careHistory.getRegId());
        //设置状态为检查中
        checkResult.setResultStatus(Constants.RESULT_STATUS_0);
        checkResult.setCreateTime(DateUtil.date());
        checkResult.setCreateBy(ShiroSecurityUtils.getCurrentUserName());
        return AjaxResult.toAjax(checkResultService.saveCheckResult(checkResult));
    }

    /**
     * 分页查询所有检查中的项目
     */
    @PostMapping("queryAllCheckingResultForPage")
    public AjaxResult queryAllCheckingResultForPage(@RequestBody CheckResultDto checkResultDto){
        //检查中的
        checkResultDto.setResultStatus(Constants.RESULT_STATUS_0);
        DataGridView dataGridView=checkResultService.queryAllCheckResultForPage(checkResultDto);
        return AjaxResult.success("查询成功",dataGridView.getData(),dataGridView.getTotal());
    }

    /**
     * 上传检查结果并完成检查
     */
    @PostMapping("completeCheckResult")
    public AjaxResult completeCheckResult(@RequestBody @Validated CheckResultFormDto checkResultFormDto){
        checkResultFormDto.setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
        return AjaxResult.toAjax(checkResultService.completeCheckResult(checkResultFormDto));
    }

    /**
     * 查询所有检查中的和检查完成了的项目
     */
    @PostMapping("queryAllCheckResultForPage")
    public AjaxResult queryAllCheckResultForPage(@RequestBody CheckResultDto checkResultDto){
        DataGridView dataGridView=checkResultService.queryAllCheckResultForPage(checkResultDto);
        return AjaxResult.success("查询成功",dataGridView.getData(),dataGridView.getTotal());
    }

}
