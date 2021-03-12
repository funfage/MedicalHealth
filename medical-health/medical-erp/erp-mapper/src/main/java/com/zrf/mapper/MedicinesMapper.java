package com.zrf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zrf.domain.Medicines;
import org.apache.ibatis.annotations.Param;

public interface MedicinesMapper extends BaseMapper<Medicines> {

    /**
     * 扣减库存
     *
     * @param medicinesId
     * @param num
     * @return
     */
    int deductionMedicinesStorage(@Param("medicinesId") Long medicinesId, @Param("num") long num);
}