package com.hopu.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色表对应实体类
 */
@Data
@TableName("t_role")
public class Role extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String role; // 角色名称
    private String remark; // 备注

    @TableField(exist=false)
    private boolean LAY_CHECKED=false;
    // get、set方法，toString方法

}
