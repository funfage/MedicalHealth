package com.zrf.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author 张润发
 * 封装挂号填写的患者信息和挂号信息数据
 * @date 2021/3/5
 */
@ApiModel(value = "com-bjsxt-dto-RegistrationFormDto")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationFormDto extends BaseDto {

    /**
     * 患者信息
     */
    private PatientDto patientDto;

    /**
     * 挂号信息
     */
    private RegistrationDto registrationDto;

}
