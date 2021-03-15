package com.zrf.mapper;

import com.zrf.domain.Drug;
import com.zrf.dto.DrugQueryDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/15
 */
public interface DrugMapper {

    /**
     * 查询发药统计列表
     *
     * @param drugQueryDto
     * @return
     */
    List<Drug> queryDrug(@Param("drug") DrugQueryDto drugQueryDto);

    /**
     * 查询发药数量统计列表
     *
     * @param drugQueryDto
     * @return
     */
    List<Drug> queryDrugStat(@Param("drug") DrugQueryDto drugQueryDto);
}
