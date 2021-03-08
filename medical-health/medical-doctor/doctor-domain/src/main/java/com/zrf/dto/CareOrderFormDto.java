package com.zrf.dto;

import com.zrf.domain.CareOrder;
import com.zrf.domain.CareOrderItem;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/6
 */
@ApiModel(value="com-zrf-dto-CareOrderFormDto")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CareOrderFormDto implements Serializable {

    /**
     * 处方
     */
    private CareOrderDto careOrder;

    /**
     * 处方详情
     */
    @NotEmpty(message = "处方详情不能为空")
    private List<CareOrderItemDto> careOrderItems;

}
