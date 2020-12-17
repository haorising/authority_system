package com.hopu.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色
 */
@TableName("t_user_role")
@Data
public class UserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;  // 用户id
    private String roleId; // 角色id
    // get、set方法，toString方法
}
