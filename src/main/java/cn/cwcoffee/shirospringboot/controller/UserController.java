package cn.cwcoffee.shirospringboot.controller;

import cn.cwcoffee.shirospringboot.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * created by coffeecw 2019/11/23
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 登录逻辑处理
     * @param name
     * @param password
     * @return
     */
    @RequestMapping("/login")
    public String login(String name, String password, Model model){

        System.out.println(userService.findByName(name));
        /**
         * 使用Shiro编写用户认证逻辑
         */
        //1、获取subject
        Subject subject = SecurityUtils.getSubject();
        //2、封装用户数据
        UsernamePasswordToken token= new UsernamePasswordToken(name,password);
        try {
            //3、执行登录方法
            subject.login(token);
            System.out.println("认证通过");
            //登录成功,跳转到主页
            return "redirect:index";
        } catch (UnknownAccountException e) {
//            e.printStackTrace();
            //登录失败,用户名不正确
            model.addAttribute("msg","用户名不正确");
            //跳转到登录页面
            return "login";
        }catch (IncorrectCredentialsException e) {
            //登录失败,密码错误
            model.addAttribute("msg","密码错误");
            //跳转到登录页面
            return "login";
        }
    }
}
