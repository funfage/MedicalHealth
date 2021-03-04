package com.zrf.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * @author 张润发
 * @date 2021/3/4
 */
@ApiModel(value="com-zrf-dto-SchedulingDto")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchedulingDto implements Serializable {

    private Long userId;

    private Long deptId;

    /**
     * 上午 下午 晚上
     */
    private String subsectionType;


    private Collection<String> schedulingType;

    /**
     * 一周值班的记录，key是日期
     */
    @JsonIgnore
    private Map<String, String> record;

    public SchedulingDto(Long userId, Long deptId, String subsectionType, Map<String,String> map) {
        this.userId = userId;
        this.subsectionType = subsectionType;
        this.record = map;
        this.deptId=deptId;
    }
}
