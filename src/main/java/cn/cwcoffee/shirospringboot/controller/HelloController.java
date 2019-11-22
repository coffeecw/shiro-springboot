package cn.cwcoffee.shirospringboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Date 2019/11/21 19:41
 */
@Controller
public class HelloController {

    @RequestMapping("/")
    public String test(){
        return "test";
    }

    @RequestMapping("/add")
    public String add(){
        return "/user/add";
    }

    @RequestMapping("/update")
    public String update(){
        return "/user/update";
    }
}
