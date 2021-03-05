package com.zrf.controller.doctor;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zrf.controller.BaseController;
import com.zrf.domain.Patient;
import com.zrf.domain.PatientFile;
import com.zrf.dto.PatientDto;
import com.zrf.service.PatientService;
import com.zrf.vo.AjaxResult;
import com.zrf.vo.DataGridView;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 张润发
 * @date 2021/3/4
 */
@RestController
@RequestMapping("doctor/patient")
public class PatientController extends BaseController {

    @Reference
    private PatientService patientService;

    /**
     * 分页查询
     */
    @GetMapping("listPatientForPage")
    @HystrixCommand
    public AjaxResult listPatientForPage(PatientDto patientDto) {
        DataGridView gridView = patientService.listPatientForPage(patientDto);
        return AjaxResult.success("查询成功", gridView.getData(), gridView.getTotal());
    }

    /**
     * 查询一个
     */
    @GetMapping("getPatientById/{patientId}")
    @HystrixCommand
    public AjaxResult getPatientById(@PathVariable String patientId) {
        Patient patient = patientService.getPatientById(patientId);
        return AjaxResult.success(patient);
    }

    /**
     * 根据ID查询患者档案
     */
    @GetMapping("getPatientFileById/{patientId}")
    @HystrixCommand
    public AjaxResult getPatientFileById(@PathVariable String patientId) {
        PatientFile patientFile = patientService.getPatientFileById(patientId);
        return AjaxResult.success(patientFile);
    }

    /**
     * 根据患者ID查询患者信息 患者档案信息  历史病例 TODO
     */
    @GetMapping("getPatientAllMessageByPatientId/{patientId}")
    public AjaxResult getPatientAllMessageByPatientId(@PathVariable String patientId) {
        return AjaxResult.success();
    }

}
