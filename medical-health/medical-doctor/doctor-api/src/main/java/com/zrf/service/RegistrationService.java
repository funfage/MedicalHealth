package com.zrf.service;

import com.zrf.domain.Registration;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zrf.dto.RegistrationDto;
import com.zrf.vo.DataGridView;

public interface RegistrationService {

    /**
     * 保存挂号单信息
     * @param registrationDto
     */
    void addRegistration(RegistrationDto registrationDto);

    /**
     * 分页加载挂号列表【默认当天的】
     * @param registrationDto
     * @return
     */
    DataGridView queryRegistrationForPage(RegistrationDto registrationDto);

    /**
     * 更新挂号单信息
     * @param registration
     * @return
     */
    int updateRegistrationById(Registration registration);

    /**
     * 根据挂号ID查询挂号信息
     * @param registrationId
     * @return
     */
    Registration queryRegistrationByRegId(String registrationId);

}
