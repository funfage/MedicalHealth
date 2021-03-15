package com.zrf.service.impl;

import com.zrf.domain.Drug;
import com.zrf.dto.DrugQueryDto;
import com.zrf.mapper.DrugMapper;
import com.zrf.service.DrugService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/15
 */
@Service
public class DrugServiceImpl implements DrugService {

    @Autowired
    private DrugMapper drugMapper;

    @Override
    public List<Drug> queryDrug(DrugQueryDto drugQueryDto) {
        return drugMapper.queryDrug(drugQueryDto);
    }

    @Override
    public List<Drug> queryDrugStat(DrugQueryDto drugQueryDto) {
        return drugMapper.queryDrugStat(drugQueryDto);
    }

}
