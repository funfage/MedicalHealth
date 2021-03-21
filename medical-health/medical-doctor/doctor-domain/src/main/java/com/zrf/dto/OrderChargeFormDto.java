package com.zrf.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/8
 */
@ApiModel(value="com-zrf-dto-OrderChargeDto")
@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderChargeFormDto extends BaseDto {

    /**
     * 主订单
     */
    private OrderChargeDto orderChargeDto;

    /**
     * 订单详情
     */
    @NotEmpty(message = "订单详情不能为空")
    private List<OrderChargeItemDto> orderChargeItemDtoList;
}
