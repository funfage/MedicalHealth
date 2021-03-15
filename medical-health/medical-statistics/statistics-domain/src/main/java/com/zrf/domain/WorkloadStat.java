package com.zrf.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author 张润发
 * @date 2021/3/15
 */
@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
public class WorkloadStat extends BaseEntity {

    private String userId;

    private String doctorName;

    private BigDecimal totalAmount;

    private Long count;


}
