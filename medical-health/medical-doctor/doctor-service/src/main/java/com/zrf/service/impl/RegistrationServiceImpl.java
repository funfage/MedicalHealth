package com.zrf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zrf.constants.Constants;
import com.zrf.domain.Registration;
import com.zrf.dto.RegistrationDto;
import com.zrf.mapper.RegistrationMapper;
import com.zrf.service.RegistrationService;
import com.zrf.vo.DataGridView;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private RegistrationMapper registrationMapper;

    @Override
    public void addRegistration(RegistrationDto registrationDto) {
        Registration registration = new Registration();
        BeanUtil.copyProperties(registrationDto, registration);
        // 设置状态为未交费
        registration.setRegStatus(Constants.REG_STATUS_0);
        registration.setCreateBy(registrationDto.getSimpleUser().getUserName());
        registration.setCreateTime(DateUtil.date());
        registrationMapper.insert(registration);
    }

    @Override
    public DataGridView queryRegistrationForPage(RegistrationDto registrationDto) {
        Page<Registration> page = new Page<>(registrationDto.getPageNum(), registrationDto.getPageSize());
        QueryWrapper<Registration> qw = new QueryWrapper<>();
        qw.eq(registrationDto.getDeptId() != null, Registration.COL_DEPT_ID, registrationDto.getDeptId());
        qw.eq(StringUtils.isNotBlank(registrationDto.getSchedulingType()), Registration.COL_SCHEDULING_TYPE, registrationDto.getSchedulingType());
        qw.eq(StringUtils.isNotBlank(registrationDto.getSubsectionType()), Registration.COL_SUBSECTION_TYPE, registrationDto.getSubsectionType());
        qw.eq(StringUtils.isNotBlank(registrationDto.getRegStatus()), Registration.COL_REG_STATUS, registrationDto.getRegStatus());
        qw.eq(StringUtils.isNotBlank(registrationDto.getVisitDate()), Registration.COL_VISIT_DATE, registrationDto.getVisitDate());
        qw.ge(registrationDto.getBeginTime() != null, Registration.COL_VISIT_DATE, DateUtil.format(registrationDto.getBeginTime(), Constants.DATE_FORMATTER));
        qw.le(registrationDto.getEndTime() != null, Registration.COL_VISIT_DATE, DateUtil.format(registrationDto.getEndTime(), Constants.DATE_FORMATTER));
        qw.orderByDesc(Registration.COL_CREATE_TIME);
        registrationMapper.selectPage(page, qw);
        return new DataGridView(page.getTotal(), page.getRecords());
    }

    @Override
    public int updateRegistrationById(Registration registration) {
        return registrationMapper.updateById(registration);
    }

    @Override
    public Registration queryRegistrationByRegId(String registrationId) {
        return registrationMapper.selectById(registrationId);
    }

    @Override
    public List<Registration> queryRegistration(Long deptId, String subsectionType, String schedulingType, String regStatus, Long userId) {
        QueryWrapper<Registration> qw = new QueryWrapper<>();
        qw.eq(Registration.COL_DEPT_ID, deptId);
        qw.eq(StringUtils.isNotBlank(subsectionType), Registration.COL_SUBSECTION_TYPE, subsectionType);
        qw.eq(Registration.COL_SCHEDULING_TYPE, schedulingType);
        qw.eq(Registration.COL_REG_STATUS, regStatus);
        // 只查询当天的挂号信息
        qw.eq(Registration.COL_VISIT_DATE, DateUtil.format(DateUtil.date(), Constants.DATE_FORMATTER));
        qw.eq(null != userId, Registration.COL_USER_ID, userId);
        qw.orderByAsc(Registration.COL_REG_NUMBER);
        return registrationMapper.selectList(qw);
    }

}
