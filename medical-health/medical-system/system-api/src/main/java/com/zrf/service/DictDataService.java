package com.zrf.service;

import com.zrf.domain.DictData;
import com.zrf.dto.DictDataDto;
import com.zrf.vo.DataGridView;

import java.util.List;

public interface DictDataService {

    /**
     * 分页查询字典数据类型
     *
     * @param dictDataDto
     * @return
     */
    DataGridView listPage(DictDataDto dictDataDto);


    /**
     * 插入新的字典类型
     *
     * @param dictDataDto
     * @return
     */
    int insert(DictDataDto dictDataDto);

    /**
     * 修改的字典类型
     *
     * @param dictDataDto
     * @return
     */
    int update(DictDataDto dictDataDto);

    /**
     * 根据ID删除字典类型
     *
     * @param dictCodeIds
     * @return
     */
    int deleteDictDataByIds(Long[] dictCodeIds);

    /**
     * 根据字典类型查询字典数据
     * 因为我们做到redis的缓存，所以 现在要去redis里面去查询
     * @param dictType
     * @return
     */
    List<DictData> selectDictDataByDictType(String dictType);

    /**
     * 根据ID查询一个字典类型
     *
     * @param dictCode
     * @return
     */
    DictData selectDictDataById(Long dictCode);

}
