package com.zrf.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author 张润发
 * @date 2021/3/12
 */
@ApiModel(value="com-bjsxt-dto-CheckResultDto")
@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
public class CheckResultFormDto extends BaseDto{

    /**
     * 处方检查项ID
     */
    @NotBlank(message = "处方检查项ID不能为空")
    @ApiModelProperty(value="处方检查项ID")
    private String itemId;

    /**
     * 检查结果描述
     */
    @NotBlank(message = "检查结果描述不能为空")
    @ApiModelProperty(value="检查结果描述")
    private String resultMsg;

    /**
     * 检查结果扫描附件[json表示]
     */
    @NotBlank(message = "检查结果扫描附件不能为空")
    @ApiModelProperty(value="检查结果扫描附件[json表示]")
    private String resultImg;

}
