package cn.cwcoffee.shirospringboot.shiro;

import cn.cwcoffee.shirospringboot.domain.User;
import cn.cwcoffee.shirospringboot.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自定义Realm
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    /**
     * 执行授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行授权逻辑");
        //给资源进行授权
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //添加资源的授权字符串
        info.addStringPermission("user:add");
        return info;
    }
    /**
     * 执行认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行认证逻辑");
        //模拟数据库的用户名和密码
        /*String name = "111";
        String password = "111";*/


        //编写Shiro的判断逻辑,判断用户名和密码
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;

        User user = userService.findByName(token.getUsername());
        System.out.println(user);
        if(user==null){
            //用户名不存在
            return null;//Shiro底层会抛出UnknownAccountException
        }
        //判断密码
        return new SimpleAuthenticationInfo("",user.getPassword(),"");
    }
}
