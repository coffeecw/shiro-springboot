package cn.cwcoffee.shirospringboot.shiro;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro的配置类
 */
@Configuration
public class ShiroConfig {

    /**
     * 创建ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //添加Shiro内置过滤器
        /**
         * Shiro内置过滤器，可以实现权限相关的的拦截器
         * 常用的过滤器:
         *       anon:无需认证(登录)可以访问
         *       authc:必须认证才可以访问
         *       user:使用rememberMe功能可以直接访问
         *       perms:该资源必须得到资源权限才可以访问
         *       role:该资源必须得到角色权限才可以访问
         */

        Map<String,String> filterMap = new LinkedHashMap<>();
       /* filterMap.put("/add","authc");
        filterMap.put("/update","authc");*/

        //不过滤主页，主页可以访问
       filterMap.put("/","anon");
       filterMap.put("/index","anon");
       //放行登录认证逻辑请求
       filterMap.put("/login","anon");
        //所有的一级请求路径会被拦截,/**代表所有的请求路径都会被拦截
       filterMap.put("/*","authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        shiroFilterFactoryBean.setLoginUrl("/toLogin");
        return shiroFilterFactoryBean;
    }

    /**
     * 创建DefaultWebSecurityManager
     */
    @Bean("securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联Realm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    /**
     * 创建Realm
     */
    @Bean("userRealm")
    public UserRealm getUserRealm(){
        return new UserRealm();
    }


}
