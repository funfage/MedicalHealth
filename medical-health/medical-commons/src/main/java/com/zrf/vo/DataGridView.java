package com.zrf.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 张润发
 * 表格数据传输对象
 * @date 2021/2/19
 */
@ApiModel(value="com-zrf-vo-DataGridView")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataGridView {

    /**
     * 总条数
     */
    private Long total;

    /**
     * 数据
     */
    private List<?> data;

}
