package com.zrf.utils;

import cn.hutool.core.date.DateUtil;

/**
 * @author 张润发
 * @date 2021/3/6
 */
public class MedicalDateUtils {

    /**
     * 得到时段
     *  1上午
     *  2下午
     *  3晚上
     */
    public static String getCurrentTimeType(){
        int hour = DateUtil.hour(DateUtil.date(), true);
        if(hour>=8&&hour<12){
            return "1";
        }else if(hour>=12&& hour<18){
            return "2";
        }else {
            return "3";
        }
    }

}
