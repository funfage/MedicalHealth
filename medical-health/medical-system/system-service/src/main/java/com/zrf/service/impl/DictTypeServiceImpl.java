package com.zrf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zrf.constants.Constants;
import com.zrf.domain.DictData;
import com.zrf.domain.DictType;
import com.zrf.dto.DictTypeDto;
import com.zrf.mapper.DictDataMapper;
import com.zrf.mapper.DictTypeMapper;
import com.zrf.service.DictTypeService;
import com.zrf.vo.DataGridView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DictTypeServiceImpl implements DictTypeService {


    @Autowired
    private DictTypeMapper dictTypeMapper;

    @Autowired
    private DictDataMapper dictDataMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public DataGridView listPage(DictTypeDto dictTypeDto) {
        QueryWrapper<DictType> wrapper = new QueryWrapper<>();
        Page<DictType> page = new Page<>(dictTypeDto.getPageNum(), dictTypeDto.getPageSize());
        // 如果字典类型名称不为空则进行模糊查询
        // 如果字典类型不为空则进行模糊查询
        // 如果字典状态不为空则根据状态查询
        wrapper.like(StringUtils.isNoneBlank(dictTypeDto.getDictName()), DictType.COL_DICT_NAME, dictTypeDto.getDictName())
                .like(StringUtils.isNotBlank(dictTypeDto.getDictType()), DictType.COL_DICT_TYPE, dictTypeDto.getDictType())
                .eq(StringUtils.isNotBlank(dictTypeDto.getStatus()), DictType.COL_STATUS, dictTypeDto.getStatus())
                .ge(null != dictTypeDto.getBeginTime(), DictType.COL_CREATE_TIME, dictTypeDto.getBeginTime())
                .le(null != dictTypeDto.getEndTime(), DictType.COL_CREATE_TIME, dictTypeDto.getEndTime());
        dictTypeMapper.selectPage(page, wrapper);
        return new DataGridView(page.getTotal(), page.getRecords());
    }

    @Override
    public DataGridView list() {
        QueryWrapper<DictType> wrapper = new QueryWrapper<>();
        //只查询出可用的状态
        wrapper.eq(DictType.COL_STATUS, Constants.STATUS_TRUE);
        return new DataGridView(null, dictTypeMapper.selectList(wrapper));
    }

    @Override
    public Boolean checkDictTypeUnique(Long dictId, String dictType) {
        // 如果id为空则置为-1
        dictId = dictId == null ? -1L : dictId;
        QueryWrapper<DictType> queryWrapper = new QueryWrapper<>();
        // 根据dictType从数据库中查询
        queryWrapper.eq(DictType.COL_DICT_TYPE, dictType);
        // 从数据库中查询一个数据
        DictType sysDictType = dictTypeMapper.selectOne(queryWrapper);
        // 当sysDictType不为空并且id不等于dictId时才不唯一
        return null != sysDictType && dictId.longValue() != sysDictType.getDictId().longValue();
    }

    @Override
    public int insert(DictTypeDto dictTypeDto) {
        // 将dictTypeDto转换成DICType
        DictType dictType = new DictType();
        BeanUtil.copyProperties(dictTypeDto, dictType);
        // 设置创建时间和创建者
        dictType.setCreateTime(DateUtil.date());
        dictType.setCreateBy(dictTypeDto.getSimpleUser().getUserName());
        return dictTypeMapper.insert(dictType);
    }

    @Override
    public int update(DictTypeDto dictTypeDto) {
        // 将dictTypeDto转换成DICType
        DictType dictType = new DictType();
        BeanUtil.copyProperties(dictTypeDto, dictType);
        // 设置修改者
        dictType.setUpdateBy(dictTypeDto.getSimpleUser().getUserName());
        return dictTypeMapper.updateById(dictType);
    }

    @Override
    public int deleteDictTypeByIds(Long[] dictIds) {
        List<Long> ids = Arrays.asList(dictIds);
        if (ids.size() > 0) {
            return dictTypeMapper.deleteBatchIds(ids);
        } else {
            return -1;
        }
    }

    @Override
    public DictType selectDictTypeById(Long dictId) {
        return dictTypeMapper.selectById(dictId);
    }

    @Override
    public void dictCacheAsync() {
        // 查询所有可用的dictType
        QueryWrapper<DictType> wrapper = new QueryWrapper<>();
        // 可用
        wrapper.eq(DictType.COL_STATUS, Constants.STATUS_TRUE);
        List<DictType> dictTypes = dictTypeMapper.selectList(wrapper);
        // 获取操作字符串的redis操作对象
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        // 遍历dictTypes，根据dictType查询对应的dictData
        for (DictType dictType : dictTypes) {
            QueryWrapper<DictData> dwrapper = new QueryWrapper<>();
            // 可用且dicType符合
            dwrapper.eq(DictData.COL_DICT_TYPE, dictType.getDictType());
            dwrapper.eq(DictData.COL_STATUS, Constants.STATUS_TRUE);
            List<DictData> dictDatas = dictDataMapper.selectList(dwrapper);
            // key的构造：dict:dicType
            // value的构造：dictDatas的json串
            opsForValue.set(Constants.DICT_REDIS_PROFIX + dictType.getDictType(), JSON.toJSONString(dictDatas));
        }
    }

}
