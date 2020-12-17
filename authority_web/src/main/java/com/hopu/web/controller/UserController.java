package com.hopu.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hopu.domain.Role;
import com.hopu.domain.User;
import com.hopu.result.ResponseEntity;
import com.hopu.service.IUserService;
import com.hopu.utils.ShiroUtils;
import com.hopu.utils.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Results;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static com.hopu.result.ResponseEntity.success;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

    // 跳转到用户角色分配页面
    @RequestMapping("/toSetRolePage")
    public String toSetRolePage(@RequestParam(value = "id") String userId, HttpServletRequest request){
        request.setAttribute("userId",userId);
        return "admin/user/user_setRole";
    }

    // 分配并保存角色
    @RequestMapping("/setRole")
    @ResponseBody
    public ResponseEntity setRole(String userId,@RequestBody List<Role> roles){
        userService.setRole(userId,roles);

        return success();
    }


//    @RequestMapping("/user/list")
//    public ResponseEntity<List<User>> list(){
//        List<User> list = userService.list();
//        return new ResponseEntity<List<User>>(list,HttpStatus.FOUND);
//    }

    // 视图页面跳转，先进入用户列表页面
    @RequiresPermissions("user:list")
    @GetMapping("/tolistPage")
    public String toUserListPage(){
        return "admin/user/user_list";
    }

    /**
     * 多条件分页查询用户列表信息 根据用户名username、电话tel，邮箱email模糊查询
     * @param page  当前页，默认1
     * @param limit  每页显示记录数 默认5
     * @param user    查询参数对象user
     * @return
     */
    @GetMapping("/list")
    @ResponseBody
    public IPage<User> findByPage(@RequestParam(value = "page",defaultValue ="1") Integer page,
                                  @RequestParam(value = "limit",defaultValue ="5") Integer limit,
                                  User user){
        // 使用mybatis-plus增强分页处理
        Page<User> page2 = new Page<>(page, limit);

        // 创建条件查询封装对象
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(user!=null){
            if(StringUtils.isNotEmpty(user.getUserName())){
                queryWrapper.like("user_name",user.getUserName());
            }

            if(StringUtils.isNotEmpty(user.getTel())) queryWrapper.like("tel",user.getTel());

            if(StringUtils.isNotEmpty(user.getEmail())) queryWrapper.like("email",user.getEmail());

        }
        IPage<User> iPage = userService.page(page2,queryWrapper);

        return iPage;
    }

    // 向添加页面跳转
    @RequestMapping("/toAddPage")
    @RequiresPermissions("user:add")
    public String toAddPage(){
        return "admin/user/user_add";
    }

    //  异步添加用户
    @ResponseBody
    @RequestMapping("/add")
    public ResponseEntity addUser(User user){
        // 可以先对用户名重名校验
        // 创建条件查询封装对象
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",user.getUserName());
        User one = userService.getOne(queryWrapper);

        if(one !=null){
            return ResponseEntity.error("用户名已经存在了");
        }

        // 开始添加用户
        user.setId(UUIDUtils.getID());
        user.setSalt(UUIDUtils.getID());
        ShiroUtils.encPass(user);
        user.setCreateTime(new Date());
        userService.save(user);
        return success();
    }

    // 向修改页面跳转
    @RequestMapping("/toUpdatePage")
    public String toUpdatePage(String id,HttpServletRequest request){
        User user = userService.getById(id);
        request.setAttribute("user",user);
        return "admin/user/user_update";
    }

    // 用户修改
    @RequestMapping("/update")
    @ResponseBody
    public ResponseEntity updateUser(User user){
        ShiroUtils.encPass(user);
        user.setUpdateTime(new Date());
        userService.updateById(user);
        return success();
    }

    // 用户删除
    @RequestMapping("/delete")
    @ResponseBody
    public ResponseEntity deleteUser(@RequestBody List<User> users){
        try {
            // 如果是root用户，禁止删除
            for (User user : users) {
                if("root".equals(user.getUserName())){
                    throw new Exception("不能删除超级管理员");
                }
//                if(user.getUserName().equals("root")){ // nullpointerException
//                    throw new Exception("不能删除超级管理员");
//                }
                userService.removeById(user.getId());
            }
            return success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.error(e.getMessage());
        }
    }

}
