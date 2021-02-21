package com.zrf.controller.system;

import com.zrf.aspectj.annotation.Log;
import com.zrf.aspectj.enums.BusinessType;
import com.zrf.dto.DictTypeDto;
import com.zrf.service.DictTypeService;
import com.zrf.utils.ShiroSecurityUtils;
import com.zrf.vo.AjaxResult;
import com.zrf.vo.DataGridView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author 张润发
 * @date 2021/2/19
 */
@RestController
@RequestMapping("system/dict/type")
public class DictTypeController {

    @Autowired
    private DictTypeService dictTypeService;

    /**
     * 分页查询
     * @param dictTypeDto
     * @return
     */
    @GetMapping("listForPage")
    public AjaxResult listForPage(DictTypeDto dictTypeDto) {
        DataGridView gridView = dictTypeService.listPage(dictTypeDto);
        return AjaxResult.success("查询成功", gridView.getData(), gridView.getTotal());
    }

    /**
     * 添加
     * @param dictTypeDto
     * @return
     */
    @PostMapping("addDictType")
    @Log(title = "添加字典类型", businessType = BusinessType.INSERT)
    public AjaxResult addDictType(@Validated DictTypeDto dictTypeDto) {
        // 判断添加的dictType是否唯一
        if (dictTypeService.checkDictTypeUnique(dictTypeDto.getDictId(), dictTypeDto.getDictType())) {
            return AjaxResult.fail("新增字典【" + dictTypeDto.getDictName() + "】失败，字典类型已存在");
        }
        dictTypeDto.setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
        // toAjax方法，如果插入成功则返回success结果封装，否则返回失败的封装
        return AjaxResult.toAjax(dictTypeService.insert(dictTypeDto));
    }

    /**
     * 修改
     * @param dictTypeDto
     * @return
     */
    @PutMapping("updateDictType")
    @Log(title = "修改字典类型",businessType = BusinessType.UPDATE)
    public AjaxResult updateDictType(@Validated DictTypeDto dictTypeDto) {
        // 判断添加的dictType是否唯一
        if (dictTypeService.checkDictTypeUnique(dictTypeDto.getDictId(), dictTypeDto.getDictType())) {
            return AjaxResult.fail("修改字典【" + dictTypeDto.getDictName() + "】失败，字典类型已存在");
        }
        // 设置当前操作的用户
        dictTypeDto.setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
        // toAjax方法，如果插入成功则返回success结果封装，否则返回失败的封装
        return AjaxResult.toAjax(dictTypeService.update(dictTypeDto));
    }

    /**
     * 根据ID查询一个字典信息
     */
    @GetMapping("getOne/{dictId}")
    public AjaxResult getDictType(@PathVariable @Validated @NotNull(message = "字典ID不能为空") Long dictId) {
        return AjaxResult.success(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 删除
     */
    @DeleteMapping("deleteDictTypeByIds/{dictIds}")
    @Log(title = "删除字典类型", businessType = BusinessType.DELETE)
    public AjaxResult updateDictType(@PathVariable @Validated @NotEmpty(message = "要删除的ID不能为空") Long[] dictIds) {
        return AjaxResult.toAjax(dictTypeService.deleteDictTypeByIds(dictIds));
    }

    /**
     * 查询所有可用的字典类型
     */
    @GetMapping("selectAllDictType")
    public AjaxResult selectAllDictType(){
        return AjaxResult.success(dictTypeService.list().getData());
    }

    /**
     * 同步缓存
     */
    @GetMapping("dictCacheAsync")
    @Log(title = "字典类型同步缓存到redis", businessType = BusinessType.OTHER)
    public AjaxResult dictCacheAsync(){
        try {
            dictTypeService.dictCacheAsync();
            return AjaxResult.success();
        }catch (Exception e){
            System.out.println(e);
            return AjaxResult.error();
        }
    }

}
