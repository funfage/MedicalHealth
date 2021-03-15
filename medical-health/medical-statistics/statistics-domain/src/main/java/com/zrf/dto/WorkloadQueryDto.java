package com.zrf.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author 张润发
 * @date 2021/3/15
 */

@ApiModel(value="com-zrf-dto-WorkloadQueryDto")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class WorkloadQueryDto extends BaseDto {

    /**
     * 医生名字
     */
    private String doctorName;

    /**
     * 默认查询当天日期
     */
    private String queryDate;

}
