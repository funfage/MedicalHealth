package com.zrf.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zrf.constants.Constants;
import com.zrf.domain.Scheduling;
import com.zrf.dto.SchedulingFormDto;
import com.zrf.dto.SchedulingQueryDto;
import com.zrf.mapper.SchedulingMapper;
import com.zrf.service.SchedulingService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SchedulingServiceImpl implements SchedulingService {

    @Autowired
    private SchedulingMapper schedulingMapper;

    @Override
    public List<Scheduling> queryScheduling(SchedulingQueryDto schedulingDto) {
        QueryWrapper<Scheduling> qw = new QueryWrapper<>();
        qw.eq(null != schedulingDto.getUserId(), Scheduling.COL_USER_ID, schedulingDto.getUserId());
        qw.eq(null != schedulingDto.getDeptId(), Scheduling.COL_DEPT_ID, schedulingDto.getDeptId());
        qw.ge(StringUtils.isNotBlank(schedulingDto.getBeginDate()), Scheduling.COL_SCHEDULING_DAY, schedulingDto.getBeginDate());
        qw.le(StringUtils.isNotBlank(schedulingDto.getEndDate()), Scheduling.COL_SCHEDULING_DAY, schedulingDto.getEndDate());
        return schedulingMapper.selectList(qw);
    }

    @Override
    public int saveScheduling(SchedulingFormDto schedulingFormDto) {
        if (null != schedulingFormDto.getData() && schedulingFormDto.getData().size() > 0) {
            // 根据传过来的日期，获取当前周的第一天和最后一天
            DateTime dateTime = DateUtil.parse(schedulingFormDto.getBeginDate());
            Date date = DateUtil.beginOfWeek(dateTime);
            String beginDate = DateUtil.format(DateUtil.beginOfWeek(dateTime), Constants.DATE_FORMATTER);
            String endDate = DateUtil.format(DateUtil.endOfWeek(dateTime), Constants.DATE_FORMATTER);
            Long userId = schedulingFormDto.getData().get(0).getUserId();
            Long deptId = schedulingFormDto.getData().get(0).getDeptId();
            if (null != userId) {
                // 删除原来一周之间的的排班
                QueryWrapper<Scheduling> wrapper = new QueryWrapper<>();
                wrapper.eq(Scheduling.COL_USER_ID, userId);
                wrapper.eq(Scheduling.COL_DEPT_ID, deptId);
                wrapper.between(Scheduling.COL_SCHEDULING_DAY, beginDate, endDate);
                schedulingMapper.delete(wrapper);
                // 添加新的一周的排班信息
                // 初始化一周的排班日期
                List<String> schedulingDays = initSchedulingDay(date);
                for (SchedulingFormDto.SchedulingData d : schedulingFormDto.getData()) {
                    Scheduling scheduling;
                    int i = 0;
                    for (String s : d.getSchedulingType()) {
                        if (StringUtils.isNotBlank(s)) {
                            scheduling = new Scheduling(userId, deptId, schedulingDays.get(i),
                                    d.getSubsectionType(), s, DateUtil.date(),
                                    schedulingFormDto.getSimpleUser().getUserName());
                            schedulingMapper.insert(scheduling);
                        }
                        i++;
                    }
                }
                // 返回影响的条数
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public List<Long> queryHasSchedulingDeptIds(Long deptId, String schedulingDay, String schedulingType, String subsectionType) {
        return schedulingMapper.queryHasSchedulingDeptIds(deptId,schedulingDay,schedulingType,subsectionType);
    }

    /**
     * 初始化值班记录
     *
     * @param startDate 当周第一天
     * @return
     */
    private static List<String> initSchedulingDay(Date startDate) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < Constants.WEEK_LENGTH; i++) {
            DateTime dateTime = DateUtil.offsetDay(startDate, i);
            list.add(DateUtil.format(dateTime, Constants.DATE_FORMATTER));
        }
        return list;
    }

}
