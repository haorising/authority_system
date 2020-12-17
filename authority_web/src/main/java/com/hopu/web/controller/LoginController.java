package com.hopu.web.controller;

import com.hopu.domain.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller

public class LoginController {

    @PostMapping("/user/login")
    public String login (User user, Model model, HttpServletRequest request){
        // 登录用户校验
        UsernamePasswordToken token = new
                UsernamePasswordToken(user.getUserName(), user.getPassword());
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
// 登录成功后，用户放在session与对象中
            HttpSession session = WebUtils.toHttp(request).getSession();
            User user1 = (User) subject.getPrincipal();
            session.setAttribute("user",user1);
            return "admin/index";
        } catch (Exception e) {
            String msg = "账户["+ token.getPrincipal() + "]的用户名或密码错误！";
            model.addAttribute("msg", msg);
            return "forward:/login.jsp";
        }
    }


    //退出
    @RequestMapping(value = "/logout",name="用户登出")
    public String logout(){
        return "forward:/login.jsp";
    }
}
