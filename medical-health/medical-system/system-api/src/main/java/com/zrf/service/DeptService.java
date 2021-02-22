package com.zrf.service;

import com.zrf.domain.Dept;
import com.zrf.dto.DeptDto;
import com.zrf.vo.DataGridView;

import java.util.List;

public interface DeptService {

    /**
     * 分页查询
     * @param deptDto
     * @return
     */
    DataGridView listPage(DeptDto deptDto);

    /**
     * 查询所有有效部门
     * @return
     */
    List<Dept> list();

    /**
     * 根据ID查询一个
     * @param deptId
     * @return
     */
    Dept getOne(Long deptId);

    /**
     * 添加一个部门
     * @param deptDto
     * @return
     */
    int addDept(DeptDto deptDto);

    /**
     * 修改部门
     * @param deptDto
     * @return
     */
    int updateDept(DeptDto deptDto);

    /**
     * 根据IDS删除部门
     * @param deptIds
     * @return
     */
    int deleteDeptByIds(Long[] deptIds);

}
