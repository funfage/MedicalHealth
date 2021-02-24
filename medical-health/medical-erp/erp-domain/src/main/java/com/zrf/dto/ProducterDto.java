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
 * @date 2021/2/24
 */
@ApiModel(value="com-zrf-dto-ProducterDto")
@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
public class ProducterDto extends BaseDto{
    /**
     * 厂家ID
     */
    @ApiModelProperty(value="厂家ID")
    private Long producterId;

    /**
     * 厂家名称
     */
    @NotBlank(message = "厂家名称不能为空")
    @ApiModelProperty(value="厂家名称")
    private String producterName;

    /**
     * 厂家简码 搜索用
     */
    @NotBlank(message = "厂家简码不能为空")
    @ApiModelProperty(value="厂家简码 搜索用")
    private String producterCode;

    /**
     * 厂家地址
     */
    @ApiModelProperty(value="厂家地址 ")
    private String producterAddress;

    /**
     * 厂家电话
     */
    @NotBlank(message = "厂家电话不能为空")
    @ApiModelProperty(value="厂家电话")
    private String producterTel;

    /**
     * 联系人
     */
    @ApiModelProperty(value="联系人")
    private String producterPerson;

    /**
     * 关键字
     */
    @NotBlank(message = "关键字不能为空")
    @ApiModelProperty(value="关键字")
    private String keywords;

    /**
     * 状态标志（0正常 1停用）sys_normal_disable
     */
    @NotBlank(message = "状态标志不能为空")
    @ApiModelProperty(value="状态标志（0正常 1停用）sys_normal_disable")
    private String status;
}
