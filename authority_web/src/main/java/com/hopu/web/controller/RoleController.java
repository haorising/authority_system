package com.hopu.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hopu.domain.Menu;
import com.hopu.domain.Role;
import com.hopu.domain.RoleMenu;
import com.hopu.domain.UserRole;
import com.hopu.result.PageEntity;
import com.hopu.result.ResponseEntity;
import com.hopu.service.IRoleMenuService;
import com.hopu.service.IRoleService;
import com.hopu.service.IUserRoleService;
import com.hopu.utils.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.hopu.result.ResponseEntity.error;
import static com.hopu.result.ResponseEntity.success;

@Controller // 将当前类RoleController可以被扫描，自动创建bean对象，放在ioc容器中
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IRoleMenuService roleMenuService;
    @Autowired
    private IUserRoleService userRoleService;

    @RequestMapping("/roleList")
    @ResponseBody
    public PageEntity list(String userId){
        // 先查询指定用户已经有哪些角色
        List<UserRole> userRoleList = userRoleService.list(new QueryWrapper<UserRole>().eq("user_id", userId));

        // 查询所有角色信息
        List<Role> list = roleService.list();

        // 判断用户哪些角色已经绑定，添加LAY_CHECKED字段为true
        list.forEach(role -> {
            List<String> roleIds = userRoleList.stream().map(userRole -> userRole.getRoleId()).collect(Collectors.toList());
            if(roleIds.contains(role.getId())){
                role.setLAY_CHECKED(true);
            }
        });
//        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
//        list.forEach(role -> {
//            // 先需要把对象转换为JSON格式
//            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(role));
//            // 判断是否已经有了对应的权限
//            List<String> roleIds = userRoleList.stream().map(userRole -> userRole.getRoleId()).collect(Collectors.toList());
//            if(roleIds.contains(role.getId())){
//                jsonObject.put("LAY_CHECKED",true);
//            }
//            jsonObjects.add(jsonObject);
//        });

//        return new PageEntity(jsonObjects.size(),jsonObjects);
        return new PageEntity(list.size(),list);
    }


    // 跳转到权限分配页面
    @RequestMapping("/toSetMenuPage")
    public String toSetMenuPage(@RequestParam(value = "id") String roleId, HttpServletRequest request){
        request.setAttribute("roleId",roleId);
        return "admin/role/role_setMenu";
    }

    // 分配并保存权限
    @RequestMapping("/setMenu")
    @ResponseBody
    public ResponseEntity save(String roleId,@RequestBody List<Menu> menus){
        roleService.setMenu(roleId,menus);

        // 避免重复或者无法删除
        // 先清除与当前角色管理的所有权限，然后再重新赋值权限
//        RoleMenu roleMenu = new RoleMenu();
//        roleMenu.setRoleId(roleId);
//        menus.forEach(menu -> {
//            roleMenu.setMenuId(menu.getId());
//            roleMenuService.save(roleMenu);
//        });

        return success();
    }



    // localhost:8080/role/toListPage
//    @RequestMapping(value = "/role/toListPage",method = RequestMethod.GET)
    @GetMapping("/toListPage")
    public String toRoleListPage(){
        return "admin/role/role_list";
    }

    // localhost:8080/role/list?page=1&limit=5&role=vip
    @RequestMapping("/list")
    @ResponseBody
    public PageEntity list(@RequestParam(value = "page",defaultValue = "1") Integer pageNum,
                           @RequestParam(value = "limit",defaultValue = "3") Integer pageSize,
                           Role role){
        // 设置分页
        Page<Role> page = new Page<>(pageNum,pageSize);
        // 封装查询条件
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        if(role !=null && StringUtils.isNotEmpty(role.getRole())){
            queryWrapper.like("role",role.getRole());
        }

        IPage<Role> iPage = roleService.page(page,queryWrapper);
        return new PageEntity(iPage);
    }

    // 向角色添加页面跳转
    @RequestMapping("/toAddPage")
    public String toAddPage(){
        return "admin/role/role_add";
    }
    /**
     * 保存
     */
    @ResponseBody
    @RequestMapping("/save")
    public ResponseEntity addUser(Role role){
        Role role2 = roleService.getOne(new QueryWrapper<Role>().eq("role", role.getRole()));
        if (role2!=null) {
            return error("角色名已存在");
        }
        role.setId(UUIDUtils.getID());
        role.setCreateTime(new Date());
        roleService.save(role);
        return success();
    }
    /**
     * 跳转修改界面
     */
    @RequestMapping("/toUpdatePage")
    public String toUpdatePage(String id, Model model){
        Role role = roleService.getById(id);
        model.addAttribute("role", role);
        return "admin/role/role_update";
    }
    /**
     * 修改
     */
    @ResponseBody
    @RequestMapping("/update")
    public ResponseEntity updateUser(Role role){
        role.setUpdateTime(new Date());
        roleService.updateById(role);
        return success();
    }

    /**
     * 删除（支持批量删除）
     */
    @ResponseBody
    @RequestMapping("/delete")
    public ResponseEntity delete(@RequestBody ArrayList<Role> roles){
        try{
            List<String> list = new ArrayList<String>();
            for (Role role : roles) {
                if ("root".equals(role.getRole())) {
                    throw new Exception("root角色不能被删除");
                }
                list.add(role.getId());
            }
            roleService.removeByIds(list);
        } catch (Exception e) {
            e.printStackTrace();
            return error(e.getMessage());
        }
        return success();
    }


}
