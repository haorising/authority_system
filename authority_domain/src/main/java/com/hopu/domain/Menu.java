package com.hopu.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * 菜单表对应实体类
 */
@TableName("t_menu")
@Data
public class Menu extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String pid;  // 父级id。0：表示顶级目录
    private String menuName; // 菜单名称
    private Integer menuType; // 菜单类型。1：顶级目录；2：菜单;3：按钮
    private String menuImg; // 当前菜单对应的图标样式
    private String permiss; // 权限标识符
    private String url; // 点击菜单要跳转的路径地址
    private String functionImg;  //  打开跳转地址后对应菜单的图标样式
    private String seq; // 按钮的顺序。从1，2，3开始

    /**
     * 子项
     * @TableField(exist=false) 表示忽略映射此字段
     */
    @TableField(exist=false)
    private List<Menu> nodes;  // 找当前菜单的子菜单

//    @TableField(exist=false)
//    private boolean LAY_CHECKED=false;
    // get、set方法，toString方法
}
