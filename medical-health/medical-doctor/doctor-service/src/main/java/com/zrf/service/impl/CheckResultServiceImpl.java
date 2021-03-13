package com.zrf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zrf.constants.Constants;
import com.zrf.domain.CareOrderItem;
import com.zrf.domain.CheckResult;
import com.zrf.domain.OrderChargeItem;
import com.zrf.dto.CheckResultDto;
import com.zrf.dto.CheckResultFormDto;
import com.zrf.mapper.CareOrderItemMapper;
import com.zrf.mapper.CheckResultMapper;
import com.zrf.mapper.OrderChargeItemMapper;
import com.zrf.service.CheckResultService;
import com.zrf.vo.DataGridView;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 张润发
 * @date 2021/3/12
 */
@Service(methods = {@Method(name = "saveCheckResult", retries = 1)})
public class CheckResultServiceImpl implements CheckResultService {

    @Autowired
    private CheckResultMapper checkResultMapper;

    @Autowired
    private CareOrderItemMapper careOrderItemMapper;

    @Autowired
    private OrderChargeItemMapper orderChargeItemMapper;

    @Override
    @Transactional
    public int saveCheckResult(CheckResult checkResult) {
        // 保存检查项目
        int insert = checkResultMapper.insert(checkResult);

        // 更新收费详情状态
        OrderChargeItem orderChargeItem = new OrderChargeItem();
        orderChargeItem.setItemId(checkResult.getItemId());
        // 已完成
        orderChargeItem.setStatus(Constants.ORDER_DETAILS_STATUS_3);
        orderChargeItemMapper.updateById(orderChargeItem);

        // 更新处方详情状态
        CareOrderItem careOrderItem = new CareOrderItem();
        careOrderItem.setItemId(checkResult.getItemId());
        careOrderItem.setStatus(Constants.ORDER_DETAILS_STATUS_3);
        careOrderItemMapper.updateById(careOrderItem);
        return insert;
    }

    @Override
    public DataGridView queryAllCheckResultForPage(CheckResultDto checkResultDto) {
        Page<CheckResult> page = new Page<>(checkResultDto.getPageNum(), checkResultDto.getPageSize());
        QueryWrapper<CheckResult> qw = new QueryWrapper<>();
        qw.in(checkResultDto.getCheckItemIds().size() > 0, CheckResult.COL_CHECK_ITEM_ID, checkResultDto.getCheckItemIds());
        qw.like(StringUtils.isNotBlank(checkResultDto.getPatientName()), CheckResult.COL_PATIENT_NAME, checkResultDto.getPatientName());
        qw.like(StringUtils.isNotBlank(checkResultDto.getRegId()), CheckResult.COL_REG_ID, checkResultDto.getRegId());
        qw.eq(StringUtils.isNotBlank(checkResultDto.getResultStatus()), CheckResult.COL_RESULT_STATUS, checkResultDto.getResultStatus());
        checkResultMapper.selectPage(page, qw);
        return new DataGridView(page.getTotal(), page.getRecords());
    }

    @Override
    public int completeCheckResult(CheckResultFormDto checkResultFormDto) {
        CheckResult checkResult=new CheckResult();
        BeanUtil.copyProperties(checkResultFormDto,checkResult);
        //设置状态为检查完成
        checkResult.setResultStatus(Constants.RESULT_STATUS_1);
        checkResult.setUpdateBy(checkResultFormDto.getSimpleUser().getUserName());
        return this.checkResultMapper.updateById(checkResult);
    }

}
