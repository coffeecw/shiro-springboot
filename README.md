 ## SpringBoot与Shiro整合实现用户认证    

### 1、分析Shiro的核心API  
```java
Subject:用户主体(把操作交给SecurityManager)  
SecurityManager:安全管理器(关联Realm)  
Realm:Shiro连接数据的桥梁
```  
### 2、SpringBoot整合Shiro  
- 导入shiro与Spring的整合依赖  
- 自定义Realm类  
- 编写Shiro的配置类(*)  

### 3、使用Shiro内置过滤器实现页面拦截  

3.1 添加Shiro内置过滤器  
```xml
Shiro内置过滤器，可以实现权限相关的的拦截器
          常用的过滤器:
                anon:无需认证(登录)可以访问
                authc:必须认证才可以访问
                user:使用rememberMe功能可以直接访问
                perms:该资源必须得到资源权限才可以访问
                role:该资源必须得到角色权限才可以访问
```
```java
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
```  
### 4、实现用户认证(登录)  
1. 设计登录页面  

```xml
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>登录</title>
</head>
<body>
<h3>用户登录</h3>
<form action="">
    用户名:<input type="text" name="name"/><br>
    密码: <input type="password" name="password"/><br>
    <input type="submit" value="登录"/>
</form>
</body>
</html>
```  
2. 编写Controller的登录逻辑  
```java
/**
     * 登录逻辑处理
     * @param name
     * @param password
     * @return
     */
    @RequestMapping("/login")
    public String login(String name, String password, Model model){
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
```
3. 编写realm的判断逻辑  
```java
 /**
     * 执行认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行认证逻辑");
        //模拟数据库的用户名和密码
        String name = "111";
        String password = "111";

        //编写Shiro的判断逻辑,判断用户名和密码
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        if(!name.equals(token.getUsername())){
            //用户名不存在
            return null;//Shiro底层会抛出UnknownAccountException
        }
        //判断密码
        return new SimpleAuthenticationInfo("",password,"");
    }
```  
###　5、整合MyBatis实现登录  

1. 导入MyBatis的相关依赖  
```xml
<!--导入MyBatis的相关依赖-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.0.14</version>
        </dependency>
        <!--mysql-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>
        <!--SpringBoot的MyBatis的启动器-->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>2.0.1</version>
    </dependency>
```
2. 配置application.properties(位置在src/main/resources目录下)

```properties
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/shiro_springboot
spring.datasource.username=root
spring.datasource.password=123456

spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
#别名包扫描,使用这个包下的类名作为别名
mybatis.type-aliases-package=cn.cwcoffee.shirospringboot.domain
#尽量使用这种方式扫描**Mapper.xml
mybatis.mapper-locations=classpath:/mapper/**Mapper.xml
```  
3. 编写User实体  
```java
/**
 * created by coffeecw 2019/11/23
 */
public class User {
    private Integer id;
    private String name;
    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

```  
4. 编写mapper接口  
```java
public interface UserMapper {
    User findByName(String name);
}
```  
5. 编写UserMapper.xml的映射文件  
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="cn.cwcoffee.shirospringboot.mapper.UserMapper">

    <select id="findByName" parameterType="string" resultType="cn.cwcoffee.shirospringboot.domain.User">
        select *
        from user
        where name=#{value}
    </select>
</mapper>
```  
6. 编写业务接口和实现  

接口:
```java
@Mapper
public interface UserService {
    User findByName(String name);
}
```  
实现:
```java
/**
 * created by coffeecw 2019/11/23
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public User findByName(String name) {
        return userMapper.findByName(name);
    }
}

```  
7. 修改UserRealm  
```java
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
        return null;
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
        if(user==null){
            //用户名不存在
            return null;//Shiro底层会抛出UnknownAccountException
        }
        //判断密码
        return new SimpleAuthenticationInfo("",user.getPassword(),"");
    }
}

```
### 6、SpringBoot与Shiro整合实现用户授权  
1. 使用Shiro内置过滤器拦截资源  
```java
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

        //授权过滤器
        //注意当前授权拦截后,shiro会自动跳转到未授权页面
        filterMap.put("/add","perms[user:add]");

        //所有的一级请求路径会被拦截,/**代表所有的请求路径都会被拦截
       filterMap.put("/*","authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        //修改被拦截后跳转的登录页面
        shiroFilterFactoryBean.setLoginUrl("/toLogin");
        //设置未授权的提示页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/unAuth");
        return shiroFilterFactoryBean;
    }

```
2. 完成Shiro资源授权  
UserRealm:
```java
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
```
### 7、thymeleaf与shiro标签整合使用  
1. 导入thymeleaf的扩展坐标  
```xml
 <!--thymeleaf对shiro的扩展坐标-->
    <dependency>
        <groupId>com.github.theborakompanioni</groupId>
        <artifactId>thymeleaf-extras-shiro</artifactId>
        <version>2.0.0</version>
    </dependency>
```
2. 配置ShiroDialect  
ShiroConfig:
```java
   /**
     * 配置ShiroDialect,用于thymeleaf和shiro标签配合使用
     */
    @Bean
    public ShiroDialect getShiroDialect(){
        return new ShiroDialect();
    }
```  
3. 在页面上使用shiro标签  
index.html:
```xml
<!DOCTYPE html>
<html lang="en"  xmlns:shiro="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <title>主页</title>
</head>
<body>
<div shiro:hasPermission="user:add">
    添加用户功能: <a href="add">添加用户</a>
</div>

<div shiro:hasPermission="user:update">
    修改用户功能: <a href="update">修改用户</a>
</div>
<a href="/toLogin">登录</a>
</body>
</html>
```



