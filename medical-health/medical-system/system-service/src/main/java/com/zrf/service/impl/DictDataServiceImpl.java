package com.zrf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zrf.constants.Constants;
import com.zrf.domain.DictData;
import com.zrf.dto.DictDataDto;
import com.zrf.mapper.DictDataMapper;
import com.zrf.service.DictDataService;
import com.zrf.vo.DataGridView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author 张润发
 */
@Service
public class DictDataServiceImpl implements DictDataService {

    @Autowired
    private DictDataMapper dictDataMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public DataGridView listPage(DictDataDto dictDataDto) {
        QueryWrapper<DictData> wrapper = new QueryWrapper<>();
        Page<DictData> page = new Page<>(dictDataDto.getPageNum(), dictDataDto.getPageSize());
        wrapper.eq(StringUtils.isNotBlank(dictDataDto.getDictType()), DictData.COL_DICT_TYPE, dictDataDto.getDictType())
                .like(StringUtils.isNotBlank(dictDataDto.getDictLabel()), DictData.COL_DICT_LABEL, dictDataDto.getDictLabel())
                .eq(StringUtils.isNotBlank(dictDataDto.getStatus()), DictData.COL_STATUS, dictDataDto.getStatus());
        dictDataMapper.selectPage(page, wrapper);
        return new DataGridView(page.getTotal(), page.getRecords());
    }

    @Override
    public int insert(DictDataDto dictDataDto) {
        DictData dictData = new DictData();
        BeanUtil.copyProperties(dictDataDto, dictData);
        // 设置创建者、创建时间
        dictData.setCreateBy(dictDataDto.getSimpleUser().getUserName());
        dictData.setCreateTime(DateUtil.date());
        return dictDataMapper.insert(dictData);
    }

    @Override
    public int update(DictDataDto dictDataDto) {
        DictData dictData = new DictData();
        BeanUtil.copyProperties(dictDataDto, dictData);
        //设置修改人
        dictData.setUpdateBy(dictDataDto.getSimpleUser().getUserName());
        return this.dictDataMapper.updateById(dictData);
    }

    @Override
    public int deleteDictDataByIds(Long[] dictCodeIds) {
        List<Long> ids = Arrays.asList(dictCodeIds);
        if (ids.size() > 0) {
            return this.dictDataMapper.deleteBatchIds(ids);
        } else {
            return -1;
        }
    }

    @Override
    public List<DictData> selectDictDataByDictType(String dictType) {
        String key = Constants.DICT_REDIS_PROFIX + dictType;
        // 从缓存中获取
        if (redisTemplate.hasKey(key)) {
            ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
            String json = opsForValue.get(key);
            return JSON.parseArray(json, DictData.class);
        } else { // 从数据库中查询
            QueryWrapper<DictData> qw = new QueryWrapper<>();
            qw.eq(DictData.COL_DICT_TYPE, dictType);
            //可用的
            qw.eq(DictData.COL_STATUS, Constants.STATUS_TRUE);
            return this.dictDataMapper.selectList(qw);
        }
    }

    @Override
    public DictData selectDictDataById(Long dictCode) {
        return dictDataMapper.selectById(dictCode);
    }

}
