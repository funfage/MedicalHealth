package com.zrf.controller.doctor;

import cn.hutool.core.date.DateUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zrf.aspectj.annotation.Log;
import com.zrf.aspectj.enums.BusinessType;
import com.zrf.constants.Constants;
import com.zrf.controller.BaseController;
import com.zrf.domain.Dept;
import com.zrf.domain.Patient;
import com.zrf.domain.Registration;
import com.zrf.dto.PatientDto;
import com.zrf.dto.RegistrationDto;
import com.zrf.dto.RegistrationFormDto;
import com.zrf.dto.RegistrationQueryDto;
import com.zrf.service.DeptService;
import com.zrf.service.PatientService;
import com.zrf.service.RegistrationService;
import com.zrf.service.SchedulingService;
import com.zrf.utils.IdGeneratorSnowflake;
import com.zrf.utils.ShiroSecurityUtils;
import com.zrf.vo.AjaxResult;
import com.zrf.vo.DataGridView;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/5
 */
@Log4j2
@RestController
@RequestMapping("doctor/registration")
public class RegistrationController extends BaseController {

    @Reference
    private RegistrationService registrationService;

    @Reference
    private SchedulingService schedulingService;

    @Reference
    private PatientService patientService;

    @Autowired
    private DeptService deptService;

    /**
     * 分页查询所有部门id
     */
    @GetMapping("listDeptForScheduling")
    @HystrixCommand
    public AjaxResult listDeptForScheduling(RegistrationQueryDto registrationQueryDto) {
        Long deptId = registrationQueryDto.getDeptId();
        String schedulingDay = registrationQueryDto.getSchedulingDay();
        String schedulingType = registrationQueryDto.getSchedulingType();
        String subsectionType = registrationQueryDto.getSubsectionType();
        List<Long> deptIds = schedulingService.queryHasSchedulingDeptIds(deptId, schedulingDay, schedulingType, subsectionType);
        if (deptIds == null || deptIds.size() == 0) {
            return AjaxResult.success(Collections.EMPTY_LIST);
        } else {
            // 根据查询出来的部门id查询所有的部信息
            List<Dept> list = deptService.listDeptByDeptIds(deptIds);
            return AjaxResult.success(list);
        }
    }

    /**
     * 根据身份证号查询患者信息
     */
    @GetMapping("getPatientByIdCard/{idCard}")
    public AjaxResult getPatientByIdCard(@PathVariable String idCard) {
        Patient patient = patientService.getPatientByIdCard(idCard);
        if (null == patient) {
            return AjaxResult.fail(idCard + "对应的患者不存在，请在下面新建患者信息");
        } else {
            return AjaxResult.success(patient);
        }
    }

    /**
     * 添加挂号信息
     * 如果患者ID为空，则先添加患者信息
     */
    @PostMapping("addRegistration")
    @Log(title = "添加挂号信息", businessType = BusinessType.INSERT)
    @HystrixCommand
    public AjaxResult addRegistration(@RequestBody @Validated RegistrationFormDto registrationFormDto) {
        PatientDto patientDto = registrationFormDto.getPatientDto();
        RegistrationDto registrationDto = registrationFormDto.getRegistrationDto();
        Patient patient;
        // 如果患者id为空，说明是第一次挂号，此时需要保存患者信息
        if (StringUtils.isBlank(patientDto.getPatientId())) {
            // 根据雪花算法生成id
            patientDto.setPatientId(IdGeneratorSnowflake.generatorIdWithProfix(Constants.ID_PROFIX_HZ));
            // 设置所属的用户
            patientDto.setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
            patient = patientService.addPatient(patientDto);
        } else { //否则从数据库中查询患者信息
            patient = patientService.getPatientById(patientDto.getPatientId());
        }
        Dept dept = deptService.getOne(registrationDto.getDeptId());
        // 保存挂号信息
        registrationDto.setRegId(IdGeneratorSnowflake.generatorIdWithProfix(Constants.ID_PROFIX_GH));
        registrationDto.setPatientId(patient.getPatientId());
        registrationDto.setPatientName(patient.getName());
        registrationDto.setRegNumber(dept.getRegNumber() + 1);
        registrationDto.setVisitDate(registrationDto.getVisitDate().substring(0, 10));
        registrationDto.setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
        registrationService.addRegistration(registrationDto);
        //修改部门挂号信息编号
        dept.setRegNumber(dept.getRegNumber() + 1);
        deptService.updateDeptRegNumber(dept.getDeptId(), dept.getRegNumber());
        //返回挂号编号
        return AjaxResult.success("", registrationDto.getRegId());
    }

    /**
     * 分页加载挂号列表
     *
     * @param registrationDto
     * @return
     */
    @GetMapping("queryRegistrationForPage")
    @HystrixCommand
    public AjaxResult queryRegistrationForPage(RegistrationDto registrationDto) {
        if (registrationDto.getBeginTime() == null || registrationDto.getEndTime() == null) {
            registrationDto.setVisitDate(DateUtil.format(DateUtil.date(), Constants.DATE_FORMATTER));
        }
        DataGridView gridView = registrationService.queryRegistrationForPage(registrationDto);
        return AjaxResult.success("查询成功", gridView.getData(), gridView.getTotal());
    }

    /**
     * 挂号收费
     * @param registrationId
     * @return
     */
    @PostMapping("collectFee/{registrationId}")
    @Log(title = "挂号收费", businessType = BusinessType.OTHER)
    @HystrixCommand
    public AjaxResult collectFee(@PathVariable String registrationId) {
        Registration registration = registrationService.queryRegistrationByRegId(registrationId);
        if (null == registration) {
            return AjaxResult.fail("当前挂号单ID对应的挂号单不存在，请核对后再查询");
        }
        if (!registration.getRegStatus().equals(Constants.REG_STATUS_0)) {
            return AjaxResult.fail("当前挂号单状态不是未收费状态，不能收费");
        }
        registration.setRegStatus(Constants.REG_STATUS_1);
        return AjaxResult.toAjax(registrationService.updateRegistrationById(registration));
    }

    /**
     * 作废
     */
    @PostMapping("doInvalid/{regId}")
    @Log(title = "挂号作废", businessType = BusinessType.OTHER)
    @HystrixCommand
    public AjaxResult doInvalid(@PathVariable String regId){
        // 根据id查询挂号单信息并判断挂号信息是否存在
        Registration registration = registrationService.queryRegistrationByRegId(regId);
        if (null == registration) {
            return AjaxResult.fail("当前挂号单【" + regId + "对应的挂号单不存在，请核对后在查询");
        }
        // 只有挂号单id为未收费状态才可以进行作废
        if (!registration.getRegStatus().equals(Constants.REG_STATUS_0)) {
            return AjaxResult.fail("当前挂号单【" + regId + "的状态不是未收费状态，不能作废");
        }
        // 设置挂号单状态为作废状态
        registration.setRegStatus(Constants.REG_STATUS_5);
        return AjaxResult.toAjax(registrationService.updateRegistrationById(registration));
    }

    /**
     * 退费
     */
    @PostMapping("doReturn/{regId}")
    @Log(title = "挂号退费", businessType = BusinessType.OTHER)
    @HystrixCommand
    public AjaxResult doReturn(@PathVariable String regId){
        // 根据id查询挂号单信息并判断挂号信息是否存在
        Registration registration = registrationService.queryRegistrationByRegId(regId);
        if (null == registration) {
            return AjaxResult.fail("当前挂号单【" + regId + "对应的挂号单不存在，请核对后在查询");
        }
        // 只有挂号单id为待就诊状态才可以进行作废
        if (!registration.getRegStatus().equals(Constants.REG_STATUS_1)) {
            return AjaxResult.fail("当前挂号单【" + regId + "的状态不是待就诊状态，不能退费");
        }
        // 设置挂号单状态为退费状态
        registration.setRegStatus(Constants.REG_STATUS_4);
        return AjaxResult.toAjax(registrationService.updateRegistrationById(registration));
    }

}

