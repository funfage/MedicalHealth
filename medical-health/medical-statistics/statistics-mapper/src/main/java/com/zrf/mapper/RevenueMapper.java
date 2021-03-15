package com.zrf.mapper;

import com.zrf.domain.Income;
import com.zrf.domain.Refund;
import com.zrf.dto.RevenueQueryDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 张润发
 * @date 2021/3/13
 */
public interface RevenueMapper {

    /**
     * 查询收入的数据
     *
     * @param revenueQueryDto
     * @return
     */
    List<Income> queryIncome(@Param("revenue") RevenueQueryDto revenueQueryDto);

    /**
     * 查询退费的数据
     *
     * @param revenueQueryDto
     * @return
     */
    List<Refund> queryRefund(@Param("revenue") RevenueQueryDto revenueQueryDto);

}
