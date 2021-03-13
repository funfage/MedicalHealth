package com.zrf.service;

import com.zrf.domain.CareHistory;
import com.zrf.domain.CareOrder;
import com.zrf.domain.CareOrderItem;
import com.zrf.dto.CareHistoryDto;
import com.zrf.dto.CareOrderFormDto;

import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/6
 */
public interface CareService {

    /**
     * 根据患者ID查询历史病历
     * @param patientId
     * @return
     */
    List<CareHistory> queryCareHistoryByPatientId(String patientId);

    /**
     * 保存或更新病例信息
     * @param careHistoryDto
     * @return
     */
    CareHistory saveOrUpdateCareHistory(CareHistoryDto careHistoryDto);

    /**
     * 根据挂号单位ID查询对应的病历信息
     * @param regId
     * @return
     */
    CareHistory queryCareHistoryByRegId(String regId);

    /**
     * 根据病历ID查询处方信息及详情信息
     * @param chId
     * @return
     */
    List<CareOrder> queryCareOrdersByChId(String chId);

    /**
     * 根据处方id查询处方对应的详情信息
     * @param coId
     * @return
     */
    List<CareOrderItem> queryCareOrderItemsByCoId(String coId, String status);

    /**
     * 根据病历id查询病历信息
     * @param chId
     * @return
     */
    CareHistory queryCareHistoryByChId(String chId);


    /**
     * 保存处方及详情信息
     * @param careOrderFormDto
     * @return
     */
    int saveCareOrderItem(CareOrderFormDto careOrderFormDto);

    /**
     * 根据处方详情id查询处方详情信息
     * @param itemId
     * @return
     */
    CareOrderItem queryCareOrderItemByItemId(String itemId);

    /**
     * 根据处方详情id删除处方详情信息
     * @param itemId
     * @return
     */
    int deleteCareOrderItemByItemId(String itemId);

    /**
     * 完成就诊
     * @param regId
     * @return
     */
    int visitComplete(String regId);

    /**
     * 发药
     * @param itemIds
     * @return
     */
    String doMedicine(List<String> itemIds);

    /**
     * 根据处方类型和状态查询
     * @param coType
     * @param status
     * @return
     */
    List<CareOrderItem> queryCareOrderItemsByStatus(String coType, String status);

    /**
     * 根据处方id查询处方信息
     * @param coId
     * @return
     */
    CareOrder queryCareOrderByCoId(String coId);
}
