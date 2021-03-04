package com.zrf.dto;

import com.zrf.domain.SimpleUser;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/4
 */
@ApiModel(value = "com-zrf-dto-SchedulingFormDto")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchedulingFormDto implements Serializable {

    private SimpleUser simpleUser;

    private String beginDate;

    private List<SchedulingData> data;

    @Data
    public static class SchedulingData implements Serializable {
        private Long userId;
        private Long deptId;
        private String subsectionType; //上午 下午  晚上
        //星期的值班值
        private Collection<String> schedulingType;
    }

}
