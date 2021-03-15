package com.zrf.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author 张润发
 * @date 2021/3/15
 */

@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
public class Workload extends BaseEntity{

    private String regId;

    private String userId;

    private String doctorName;

    private String regAmount;

    private String patientName;

    private String visitDate;

}
