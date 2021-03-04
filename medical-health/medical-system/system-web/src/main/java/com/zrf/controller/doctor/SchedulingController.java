package com.zrf.controller.doctor;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zrf.constants.Constants;
import com.zrf.controller.BaseController;
import com.zrf.domain.Scheduling;
import com.zrf.domain.User;
import com.zrf.dto.SchedulingDto;
import com.zrf.dto.SchedulingFormDto;
import com.zrf.dto.SchedulingQueryDto;
import com.zrf.service.SchedulingService;
import com.zrf.service.UserService;
import com.zrf.utils.ShiroSecurityUtils;
import com.zrf.vo.AjaxResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author 张润发
 * @date 2021/3/4
 */
@RestController
@RequestMapping("doctor/scheduling")
public class SchedulingController extends BaseController {

    @Reference
    private SchedulingService schedulingService;

    @Autowired
    private UserService userService;

    /**
     * 查询要排班的医生信息
     * 如果部门ID为空，那么查询所有要排班的用户信息
     */
    @GetMapping("queryUsersNeedScheduling")
    public AjaxResult queryUsersNeedScheduling(Long deptId) {
        List<User> users = this.userService.querySchedulingUsers(null, deptId);
        return AjaxResult.success(users);
    }

    /**
     * 查询要排班的医生的排班信息
     */
    @GetMapping("queryScheduling")
    @HystrixCommand
    public AjaxResult queryScheduling(SchedulingQueryDto schedulingQueryDto) {
        //根据部门ID用户ID查询用户信息 如果用户ID和部门ID都为空，那么就查询所有要排班的用户信息
        List<User> users = userService.querySchedulingUsers(schedulingQueryDto.getUserId(), schedulingQueryDto.getDeptId());
        return getSchedulingAjaxResult(schedulingQueryDto, users);
    }

    /**
     * 查询当前登陆用户的的排班信息
     */
    @GetMapping("queryMyScheduling")
    @HystrixCommand
    public AjaxResult queryMyScheduling(SchedulingQueryDto schedulingQueryDto) {
        //根据部门ID用户ID查询用户信息 如果用户ID和部门ID都为空，那么就查询所有要排班的用户信息
        List<User> users = userService.querySchedulingUsers(Long.valueOf(ShiroSecurityUtils.getCurrentSimpleUser().getUserId().toString()), schedulingQueryDto.getDeptId());
        return getSchedulingAjaxResult(schedulingQueryDto, users);
    }

    /**
     * 保存排班数据
     */
    @PostMapping("saveScheduling")
    @HystrixCommand
    public AjaxResult saveScheduling(@RequestBody SchedulingFormDto schedulingFormDto){
        schedulingFormDto.setSimpleUser(ShiroSecurityUtils.getCurrentSimpleUser());
        return AjaxResult.toAjax(schedulingService.saveScheduling(schedulingFormDto));
    }

    /**
     * 核心的构造页面数据的方法
     *
     * @param schedulingQueryDto
     * @param users
     * @return
     */
    private AjaxResult getSchedulingAjaxResult(SchedulingQueryDto schedulingQueryDto, List<User> users) {
        // 获取当前时间
        DateTime date = DateUtil.date();
        if (StringUtils.isNotBlank(schedulingQueryDto.getQueryDate())) {
            date = DateUtil.parse(schedulingQueryDto.getQueryDate(), Constants.DATE_FORMATTER);
            int i = DateUtil.dayOfWeek(date);
            // 判断是查询上一周还是下一周，1表示周日，2表示周一
            // 表示查询下一周
            if (i == 1) {
                date = DateUtil.offsetWeek(date, 1);
            } else {
                date = DateUtil.offsetWeek(date, -1);
            }
        }
        //计算一周的开始日期和结束日期
        DateTime beginTime = DateUtil.beginOfWeek(date);
        DateTime endTime = DateUtil.endOfWeek(date);
        //设置开始日期和结束日期到schedulingQueryDto
        schedulingQueryDto.setBeginDate(DateUtil.format(beginTime, Constants.DATE_FORMATTER));
        schedulingQueryDto.setEndDate(DateUtil.format(endTime, Constants.DATE_FORMATTER));
        //根据开始时间和结束时间去查询数据库里面存在的数据
        List<Scheduling> list = schedulingService.queryScheduling(schedulingQueryDto);
        //存放页面需要的数据的对象
        List<SchedulingDto> schedulingDtos = new ArrayList<>();
        for (User user : users) {
            // 早中晚
            SchedulingDto schedulingDto1 = new SchedulingDto(user.getUserId(), user.getDeptId(), "1", initMap(beginTime));
            SchedulingDto schedulingDto2 = new SchedulingDto(user.getUserId(), user.getDeptId(), "2", initMap(beginTime));
            SchedulingDto schedulingDto3 = new SchedulingDto(user.getUserId(), user.getDeptId(), "3", initMap(beginTime));
            //一个用户三条数据
            schedulingDtos.add(schedulingDto1);
            schedulingDtos.add(schedulingDto2);
            schedulingDtos.add(schedulingDto3);
            for (Scheduling scheduling : list) {
                // 得到用户id
                Long userId = scheduling.getUserId();
                //得到早中晚的类型
                String subsectionType = scheduling.getSubsectionType();
                //值班日期
                String schedulingDay = scheduling.getSchedulingDay();
                // 设置值班类型，门诊，急诊
                if (user.getUserId().equals(userId)) {
                    switch (subsectionType) {
                        case "1":
                            Map<String, String> record1 = schedulingDto1.getRecord();
                            record1.put(schedulingDay, scheduling.getSchedulingType());
                            break;
                        case "2":
                            Map<String, String> record2 = schedulingDto2.getRecord();
                            record2.put(schedulingDay, scheduling.getSchedulingType());
                            break;
                        case "3":
                            Map<String, String> record3 = schedulingDto3.getRecord();
                            record3.put(schedulingDay, scheduling.getSchedulingType());
                            break;
                         default:
                            break;
                    }
                }
            }
            //把map中的value转成数组数据放到schedulingType
            schedulingDto1.setSchedulingType(schedulingDto1.getRecord().values());
            schedulingDto2.setSchedulingType(schedulingDto2.getRecord().values());
            schedulingDto3.setSchedulingType(schedulingDto3.getRecord().values());
        }
        //组装返回的对象
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("tableData", schedulingDtos);
        Map<String, Object> schedulingData = new HashMap<>();
        schedulingData.put("startTimeThisWeek", schedulingQueryDto.getBeginDate());
        schedulingData.put("endTimeThisWeek", schedulingQueryDto.getEndDate());
        resMap.put("schedulingData", schedulingData);
        resMap.put("labelNames", initLabel(beginTime));
        return AjaxResult.success(resMap);
    }

    /**
     * 初始化labelNames
     *
     * @param beginTime
     * @return
     */
    private String[] initLabel(DateTime beginTime) {
        String[] labelNames = new String[7];
        for (int i = 0; i < Constants.WEEK_LENGTH; i++) {
            DateTime d = DateUtil.offsetDay(beginTime, i);
            labelNames[i] = DateUtil.format(d, Constants.DATE_FORMATTER) + formatterWeek(i);
        }
        return labelNames;
    }

    /**
     * 翻译周
     *
     * @param i
     * @return
     */
    private String formatterWeek(int i) {
        switch (i) {
            case 0:
                return "(周一)";
            case 1:
                return "(周二)";
            case 2:
                return "(周三)";
            case 3:
                return "(周四)";
            case 4:
                return "(周五)";
            case 5:
                return "(周六)";
            default:
                return "(周日)";
        }
    }
    /**
     * 初始化值班记录
     *
     * @param beginTime 当周的第一天
     * @return
     */
    private Map<String, String> initMap(DateTime beginTime) {
        //TreeMap按顺序放 2021-03-01 -  2020-03-07
        Map<String, String> map = new TreeMap<>();
        for (int i = 0; i < Constants.WEEK_LENGTH; i++) {
            DateTime d = DateUtil.offsetDay(beginTime, i);
            String key = DateUtil.format(d, Constants.DATE_FORMATTER);
            map.put(key, "");
        }
        return map;
    }

}
