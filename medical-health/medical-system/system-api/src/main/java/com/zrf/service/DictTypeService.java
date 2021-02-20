package com.zrf.service;

import com.zrf.domain.DictType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zrf.dto.DictTypeDto;
import com.zrf.vo.DataGridView;

public interface DictTypeService {

    /**
     * 分页查询字典类型
     * @param dictTypeDto
     * @return
     */
    DataGridView listPage(DictTypeDto dictTypeDto);

    /**
     * 查询所有字典类型
     * @return
     */
    DataGridView list();

    /**
     * 检查字典类型是否存在
     * 当添加字典类型或修改字典类型时需要检查
     * @param dictId
     * @param dictType
     * @return
     */
    Boolean checkDictTypeUnique(Long dictId, String dictType);

    /**
     * 插入新的字典类型
     * @param dictTypeDto
     * @return
     */
    int insert(DictTypeDto dictTypeDto);

    /**
     * 修改的字典类型
     * @param dictTypeDto
     * @return
     */
    int update(DictTypeDto dictTypeDto);

    /**
     * 根据id集合删除
     * @param dictIds
     * @return
     */
    int deleteDictTypeByIds(Long[] dictIds);

    /**
     * 根据id查询一个字典类型
     * @param dictId
     * @return
     */
    DictType selectDictTypeById(Long dictId);

    /**
     * 字典类型同步到缓存
     */
    void dictCacheAsync();
}
