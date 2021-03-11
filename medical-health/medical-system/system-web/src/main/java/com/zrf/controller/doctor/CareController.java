package com.zrf.controller.doctor;

import cn.hutool.core.date.DateUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zrf.constants.Constants;
import com.zrf.controller.BaseController;
import com.zrf.domain.*;
import com.zrf.dto.CareHistoryDto;
import com.zrf.dto.CareOrderFormDto;
import com.zrf.service.CareService;
import com.zrf.service.DeptService;
import com.zrf.service.PatientService;
import com.zrf.service.RegistrationService;
import com.zrf.utils.IdGeneratorSnowflake;
import com.zrf.utils.MedicalDateUtils;
import com.zrf.utils.ShiroSecurityUtils;
import com.zrf.vo.AjaxResult;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 张润发
 * @date 2021/3/6
 */
@RestController
@RequestMapping("doctor/care")
public class CareController extends BaseController {

    @Reference
    private RegistrationService registrationService;

    @Reference
    private PatientService patientService;

    @Reference
    private CareService careService;

    @Autowired
    private DeptService deptService;

    /**
     * 查询待就诊的挂号信息
     * GET/doctor/care/queryToBeSeenRegistration/{scheudlingType}
     */
    @GetMapping("queryToBeSeenRegistration/{schedulingType}")
    public AjaxResult queryToBeSeenRegistration(@PathVariable String schedulingType) {
        // 得到当前用户的部门id
        Long deptId = ShiroSecurityUtils.getCurrentUser().getDeptId();
        // 查询待就诊的信息
        String regStatus = Constants.REG_STATUS_1;
        Long userId = null;
        List<Registration> list = registrationService.queryRegistration(deptId, null, schedulingType, regStatus, userId);
        return AjaxResult.success(list);
    }

    /**
     * 查询就诊中的挂号信息
     * GET/doctor/care/queryVisitingRegistration/{scheudlingType}
     */
    @GetMapping("queryVisitingRegistration/{scheudlingType}")
    public AjaxResult queryVisitingRegistration(@PathVariable String scheudlingType) {
        //得到当前用户的部门ID
        Long deptId = ShiroSecurityUtils.getCurrentUser().getDeptId();
        //设置要查询的状态  只能是挂号单的待就诊的挂号信息
        String regStatus = Constants.REG_STATUS_2;
        //计算时段
        Long userId = ShiroSecurityUtils.getCurrentUser().getUserId();
        List<Registration> list = registrationService.queryRegistration(deptId, null, scheudlingType, regStatus, userId);
        return AjaxResult.success(list);
    }

    /**
     * 查询就诊完成的挂号信息
     * GET/doctor/care/queryVisitCompletedRegistration/{scheudlingType}
     */
    @GetMapping("queryVisitCompletedRegistration/{schedulingType}")
    public AjaxResult queryVisitCompletedRegistration(@PathVariable String schedulingType) {
        //得到当前用户的部门ID
        Long deptId = ShiroSecurityUtils.getCurrentUser().getDeptId();
        //设置要查询的状态 只能是挂号单的待就诊的挂号信息
        String regStatus = Constants.REG_STATUS_3;
        //查询
        Long userId = ShiroSecurityUtils.getCurrentUser().getUserId();
        List<Registration> list = registrationService.queryRegistration(deptId, null, schedulingType, regStatus, userId);
        return AjaxResult.success(list);
    }

    /**
     * 接诊
     * POST/doctor/care/receivePatient/{regId}
     */
    @PostMapping("receivePatient/{regId}")
    public AjaxResult receivePatient(@PathVariable String regId) {
        // 防止并发就诊问题
        synchronized (this) {
            Registration registration = registrationService.queryRegistrationByRegId(regId);
            if (null == registration) {
                return AjaxResult.fail("【" + regId + "】挂号单不存在，不能就诊");
            }
            // 只有就诊状态为待就诊状态才能接诊
            if (registration.getRegStatus().equals(Constants.REG_STATUS_1)) {
                // 设置状态为就诊状态
                registration.setRegStatus(Constants.REG_STATUS_2);
                registration.setUserId(ShiroSecurityUtils.getCurrentUser().getUserId());
                registration.setDoctorName(ShiroSecurityUtils.getCurrentUser().getUserName());
                return AjaxResult.toAjax(registrationService.updateRegistrationById(registration));
            } else {
                return AjaxResult.fail("【" + regId + "】挂号单的状态不是待就诊状态，不能接诊");
            }
        }
    }

    /**
     * 根据患者ID获取患者信息、档案信息、病历信息
     * GET/doctor/care/getPatientAllMessageByPatientId/{patientId}
     */
    @GetMapping("getPatientAllMessageByPatientId/{patientId}")
    public AjaxResult getPatientAllMessageByPatientId(@PathVariable String patientId) {
        //查询患者信息
        Patient patient = patientService.getPatientById(patientId);
        //查询档案
        PatientFile patientFile = patientService.getPatientFileById(patientId);
        //查询病历表
        List<CareHistory> careHistories = careService.queryCareHistoryByPatientId(patientId);
        Map<String, Object> res = new HashMap<>();
        res.put("patient", patient);
        res.put("patientFile", patientFile);
        res.put("careHistoryList", careHistories);
        return AjaxResult.success(res);
    }

    /**
     * 保存病例信息
     */
    @PostMapping("saveCareHistory")
    public AjaxResult saveCareHistory(@RequestBody CareHistoryDto careHistoryDto) {
        // 设置接诊的医生信息
        careHistoryDto.setUserId(ShiroSecurityUtils.getCurrentUser().getUserId());
        careHistoryDto.setUserName(ShiroSecurityUtils.getCurrentUser().getUserName());
        // 设置接诊的医生的科室信息
        careHistoryDto.setDeptId(ShiroSecurityUtils.getCurrentUser().getDeptId());
        Dept dept = deptService.getOne(ShiroSecurityUtils.getCurrentUser().getDeptId());
        careHistoryDto.setDeptName(dept.getDeptName());
        careHistoryDto.setCareDate(DateUtil.date());
        CareHistory careHistory = careService.saveOrUpdateCareHistory(careHistoryDto);
        return AjaxResult.success(careHistory);
    }

    /**
     * 根据挂号单位ID查询对应的病历信息
     */
    @GetMapping("getCareHistoryByRegId/{regId}")
//    @HystrixCommand
    public AjaxResult getCareHistoryByRegId(@PathVariable String regId) {
        CareHistory careHistory = careService.queryCareHistoryByRegId(regId);
        return AjaxResult.success(careHistory);
    }

    /**
     * 根据病历ID查询处方信息及详情信息
     */
    @GetMapping("queryCareOrdersByChId/{chId}")
//    @HystrixCommand
    public AjaxResult queryCareOrdersByChId(@PathVariable String chId) {
        // 根据病历id查询所有的处方信息
        List<CareOrder> careOrders = careService.queryCareOrdersByChId(chId);
        List<Map<String, Object>> res = new ArrayList<>();
        // 遍历所有的处方信息，查询每个处方对应的详情信息，并构造结果集返回
        for (CareOrder careOrder : careOrders) {
            Map<String, Object> map = new HashMap<>();
            map.put("careOrder", careOrder);
            List<CareOrderItem> careOrderItems = this.careService.queryCareOrderItemsByCoId(careOrder.getCoId(), null);
            map.put("careOrderItems", careOrderItems);
            res.add(map);
        }
        return AjaxResult.success(res);
    }

    /**
     * 保存处方及详情信息
     */
    @PostMapping("saveCareOrderItem")
//    @HystrixCommand
    public AjaxResult saveCareOrderItem(@RequestBody @Validated CareOrderFormDto careOrderFormDto) {
        // 查询病例信息是否保存
        CareHistory careHistory = careService.queryCareHistoryByChId(careOrderFormDto.getCareOrder().getChId());
        if (null == careHistory) {
            return AjaxResult.fail("病历ID不存在，请核对后再提交");
        }
        // 生成处方id
        careOrderFormDto.getCareOrder().setCoId(IdGeneratorSnowflake.generatorIdWithProfix(Constants.ID_PROFIX_CO));
        // 设置患者信息
        careOrderFormDto.getCareOrder().setPatientId(careHistory.getPatientId());
        careOrderFormDto.getCareOrder().setPatientName(careHistory.getPatientName());
        // 设置接诊的医生信息
        careOrderFormDto.getCareOrder().setUserId(ShiroSecurityUtils.getCurrentUser().getUserId());
        careOrderFormDto.getCareOrder().setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
        return AjaxResult.toAjax(careService.saveCareOrderItem(careOrderFormDto));
    }

    /**
     * 根据处方详情ID删除处方详情【只能删除未支付的】
     */
    @DeleteMapping("deleteCareOrderItemById/{itemId}")
//    @HystrixCommand
    public AjaxResult deleteCareOrderItemById(@PathVariable String itemId) {
        // 判断处方详情是否保存
        CareOrderItem careOrderItem = careService.queryCareOrderItemByItemId(itemId);
        if (null == careOrderItem) {
            return AjaxResult.fail("处方详情ID不存在");
        }
        // 判断是否是未支付状态
        if (!careOrderItem.getStatus().equals(Constants.ORDER_DETAILS_STATUS_0)) {
            return AjaxResult.fail("【" + itemId + "】不是未支付状态，不能删除");
        }
        return AjaxResult.toAjax(this.careService.deleteCareOrderItemByItemId(itemId));
    }

    /**
     *完成就诊
     */
    @PostMapping("visitComplete/{regId}")
//    @HystrixCommand
    public AjaxResult visitComplete(@PathVariable String regId){
        // 判断挂号单是否存在
        Registration registration = registrationService.queryRegistrationByRegId(regId);
        if (null == registration) {
            return AjaxResult.fail("【"+regId+"】挂号单号不存在，请核对后再提交");
        }
        // 如果不是就诊中状态则不能进行就诊
        if(!registration.getRegStatus().equals(Constants.REG_STATUS_2)){
            return AjaxResult.fail("【"+regId+"】状态不是就诊中状态，不能完成就诊");
        }
        //更改挂号单的状态
        return AjaxResult.toAjax(this.careService.visitComplete(regId));
    }


}










