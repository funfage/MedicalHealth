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
public class Refund extends BaseEntity {

    /**
     * 退费总额
     */
    private Double backAmount;

    /**
     * 退费类型
     */
    private String backType;
}
