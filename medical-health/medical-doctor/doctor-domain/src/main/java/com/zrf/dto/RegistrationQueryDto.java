package com.zrf.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author 张润发
 * 封装挂号查询表单的数据
 * @date 2021/3/5
 */
@ApiModel(value = "com-bjsxt-dto-RegistrationQueryDto")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationQueryDto extends BaseDto{

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 挂号类型
     */
    @NotBlank(message = "挂号类型不能为空")
    private String schedulingType;

    /**
     * 挂号时段
     */
    @NotBlank(message = "挂号时段不能为空")
    private String subsectionType;

    /**
     * 查询时间
     */
    @NotBlank(message = "挂号日期不能为空")
    private String schedulingDay;

}
