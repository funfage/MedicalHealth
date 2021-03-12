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
 * @date 2021/3/11
 */
@ApiModel(value="com-zrf-dto-OrderBackfeeFormDto")
@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderBackfeeFormDto extends BaseDto {

    /**
     * 退费主表
     */
    private OrderBackfeeDto orderBackfeeDto;

    /**
     * 退费详情表
     */
    @NotEmpty(message = "退费订单详情不能为空")
    private List<OrderBackfeeItemDto> orderBackfeeItemDtoList;


}
