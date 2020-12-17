package com.hopu.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户表
 */
@TableName(value = "t_user")
@Data
public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String userName; // 用户名
    private String password; // 密码
    private String salt; // 加密用户的盐
    private String nickname; // 昵称
    private String userImg; // 用户头像
    private String tel;  // 手机号码
    private Integer sex; // 性别：1、男；-1、女
    private String email; // 邮箱
    private String status;  // 状态：on、可用；其它、禁用
    // get、set方法，toString方法

}
