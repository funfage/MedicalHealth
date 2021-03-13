package com.zrf.service;

import com.zrf.domain.CheckResult;
import com.zrf.dto.CheckResultDto;
import com.zrf.dto.CheckResultFormDto;
import com.zrf.vo.DataGridView;

/**
 * @author 张润发
 * @date 2021/3/12
 */
public interface CheckResultService {

    /**
     * 保存检查项目信息
     * @param checkResult
     * @return
     */
    int saveCheckResult(CheckResult checkResult);

    /**
     * 分解条件分页查询检查
     * @param checkResultDto
     * @return
     */
    DataGridView queryAllCheckResultForPage(CheckResultDto checkResultDto);

    /**
     * 完成检查
     * @param checkResultFormDto
     * @return
     */
    int completeCheckResult(CheckResultFormDto checkResultFormDto);
}
