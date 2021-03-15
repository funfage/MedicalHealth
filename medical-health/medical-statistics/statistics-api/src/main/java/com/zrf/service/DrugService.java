package com.zrf.service;

import com.zrf.domain.Drug;
import com.zrf.dto.DrugQueryDto;

import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/15
 */
public interface DrugService {

    /**
     * 查询发药统计列表
     * @param drugQueryDto
     * @return
     */
    List<Drug> queryDrug(DrugQueryDto drugQueryDto);

    /**
     * 查询发药数量统计列表
     * @param drugQueryDto
     * @return
     */
    List<Drug> queryDrugStat(DrugQueryDto drugQueryDto);
}
