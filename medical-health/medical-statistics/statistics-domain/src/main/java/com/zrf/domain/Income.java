package com.zrf.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author 张润发
 * @date 2021/3/13
 */
@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
public class Income extends BaseEntity {

    /**
     * 订单总额
     */
    private Double orderAmount;

    /**
     * 支付类型
     */
    private String payType;

}
