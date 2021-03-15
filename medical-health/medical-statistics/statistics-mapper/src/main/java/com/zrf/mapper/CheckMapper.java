package com.zrf.mapper;

import com.zrf.domain.Check;
import com.zrf.domain.CheckStat;
import com.zrf.dto.CheckQueryDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/15
 */
public interface CheckMapper {

    /**
     * 查询检查项列表
     *
     * @param checkQueryDto
     * @return
     */
    List<Check> queryCheck(@Param("check") CheckQueryDto checkQueryDto);

    /**
     * 查询检查项统计列表
     *
     * @param checkQueryDto
     * @return
     */
    List<CheckStat> queryCheckStat(@Param("check") CheckQueryDto checkQueryDto);

}
