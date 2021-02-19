package com.zrf.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张润发
 * 构造菜单返回给前台的vo
 * @date 2021/2/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuTreeVo {
    private String id;

    // 菜单表里面的url
    private String serPath;

    //是否显示
    private boolean show = true;

    public MenuTreeVo(String id, String serPath) {
        this.id = id;
        this.serPath = serPath;
    }
}
