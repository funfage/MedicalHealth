package com.zrf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zrf.constants.Constants;
import com.zrf.domain.Producter;
import com.zrf.dto.ProducterDto;
import com.zrf.mapper.ProducterMapper;
import com.zrf.service.ProducterService;
import com.zrf.vo.DataGridView;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
/**
 * methods = {@Method(name = "addProducter",retries = 0)
 * 代表当前类里面的addProducter这个主就去只调一次，不重试
 */
@Service(methods = {@Method(name = "addProducter", retries = 0)})
public class ProducterServiceImpl implements ProducterService{

    @Autowired
    private ProducterMapper producterMapper;

    @Override
    public DataGridView listProducterPage(ProducterDto producterDto) {
        Page<Producter> page=new Page<>(producterDto.getPageNum(),producterDto.getPageSize());
        QueryWrapper<Producter> qw=new QueryWrapper<>();
        qw.like(StringUtils.isNotBlank(producterDto.getProducterName()),Producter.COL_PRODUCTER_NAME,producterDto.getProducterName());
        qw.like(StringUtils.isNotBlank(producterDto.getKeywords()),Producter.COL_KEYWORDS,producterDto.getKeywords());
        qw.like(StringUtils.isNotBlank(producterDto.getProducterTel()),Producter.COL_PRODUCTER_TEL,producterDto.getProducterTel());
        qw.eq(StringUtils.isNotBlank(producterDto.getStatus()),Producter.COL_STATUS,producterDto.getStatus());

        qw.ge(producterDto.getBeginTime()!=null,Producter.COL_CREATE_TIME,producterDto.getBeginTime());
        qw.le(producterDto.getEndTime()!=null,Producter.COL_CREATE_TIME,producterDto.getEndTime());
        this.producterMapper.selectPage(page,qw);
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    @Override
    public Producter getOne(Long producterId) {
        return this.producterMapper.selectById(producterId);
    }

    @Override
    public int addProducter(ProducterDto producterDto) {
        Producter producter=new Producter();
        BeanUtil.copyProperties(producterDto,producter);
        producter.setCreateTime(DateUtil.date());
        producter.setCreateBy(producterDto.getSimpleUser().getUserName());
        return this.producterMapper.insert(producter);
    }

    @Override
    public int updateProducter(ProducterDto producterDto) {
        Producter producter=new Producter();
        BeanUtil.copyProperties(producterDto,producter);
        producter.setUpdateBy(producterDto.getSimpleUser().getUserName());
        return this.producterMapper.updateById(producter);
    }

    @Override
    public int deleteProducterByIds(Long[] producterIds) {
        List<Long> ids= Arrays.asList(producterIds);
        if(ids.size()>0){
            return this.producterMapper.deleteBatchIds(ids);
        }
        return 0;
    }

    @Override
    public List<Producter> selectAllProducter() {
        QueryWrapper<Producter> qw=new QueryWrapper<>();
        qw.eq(Producter.COL_STATUS, Constants.STATUS_TRUE);
        return this.producterMapper.selectList(qw);
    }
}
