package com.zrf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zrf.constants.Constants;
import com.zrf.domain.Dept;
import com.zrf.dto.DeptDto;
import com.zrf.mapper.DeptMapper;
import com.zrf.service.DeptService;
import com.zrf.vo.DataGridView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
@Service
public class DeptServiceImpl implements DeptService{

    @Autowired
    private DeptMapper deptMapper;

    @Override
    public DataGridView listPage(DeptDto deptDto) {
        Page<Dept> page = new Page<>(deptDto.getPageNum(), deptDto.getPageSize());
        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(deptDto.getDeptName()), Dept.COL_DEPT_NAME, deptDto.getDeptName())
                .eq(StringUtils.isNotBlank(deptDto.getStatus()), Dept.COL_STATUS, deptDto.getStatus())
                .ge(deptDto.getBeginTime() != null, Dept.COL_CREATE_TIME, deptDto.getBeginTime())
                .le(deptDto.getEndTime() != null, Dept.COL_CREATE_TIME, deptDto.getEndTime())
                .orderByAsc(Dept.COL_ORDER_NUM);
        deptMapper.selectPage(page, wrapper);
        return new DataGridView(page.getTotal(), page.getRecords());
    }

    @Override
    public List<Dept> list() {
        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        wrapper.eq(Dept.COL_STATUS, Constants.STATUS_TRUE);
        wrapper.orderByAsc(Dept.COL_ORDER_NUM);
        return deptMapper.selectList(wrapper);
    }

    @Override
    public Dept getOne(Long deptId) {
        return deptMapper.selectById(deptId);
    }

    @Override
    public int addDept(DeptDto deptDto) {
        Dept dept = new Dept();
        BeanUtil.copyProperties(deptDto, dept);
        // 设置创建者，创建时间
        dept.setCreateBy(deptDto.getSimpleUser().getUserName());
        dept.setCreateTime(DateUtil.date());
        return deptMapper.insert(dept);
    }

    @Override
    public int updateDept(DeptDto deptDto) {
        Dept dept = new Dept();
        BeanUtil.copyProperties(deptDto, dept);
        // 设置修改人
        dept.setUpdateBy(deptDto.getSimpleUser().getUserName());
        return deptMapper.updateById(dept);
    }

    @Override
    public int deleteDeptByIds(Long[] deptIds) {
        List<Long> ids = Arrays.asList(deptIds);
        if(ids.size() > 0){
            return this.deptMapper.deleteBatchIds(ids);
        }
        return 0;
    }
}
