package com.zrf.vo;

import com.zrf.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author 张润发
 * @date 2021/2/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiverUser implements Serializable {

    private User user;
    private List<String> roles = Collections.EMPTY_LIST;//角色
    private List<String> permissions = Collections.EMPTY_LIST; //权限

}
