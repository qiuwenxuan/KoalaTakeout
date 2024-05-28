package com.example.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.common.Constants;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.service.AdminService;
import com.example.service.BusinessService;
import com.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Token工具类
 * 含创建Token方法createToken()、获取当前登录的用户对象方法getCurrentUser()
 *
 * @Component： 用于标识一个类为 Spring 组件，表示该类将由 Spring 容器进行管理。
 * @PostConstruct： 用于标识一个方法为初始化方法，表示在实例化该类后，Spring 容器会自动调用该方法。标记的方法通常可以用来进行一些初始化操作
 */
@Component
public class TokenUtils {

    private static final Logger log = LoggerFactory.getLogger(TokenUtils.class);

    private static AdminService staticAdminService;
    private static BusinessService staticBusinessService;
    private static UserService staticUserService;

    @Resource
    AdminService adminService;
    @Resource
    BusinessService businessService;
    @Resource
    UserService userService;

    @PostConstruct
    public void setUserService() {
        // 当创建TokenUtils类实例时，自动调用setUserService方法将注入的AdminService对象赋值给静态变量staticAdminService
        staticAdminService = adminService;
        staticBusinessService = businessService;
        staticUserService = userService;
    }

    /**
     * 生成token
     */
    public static String createToken(String data, String sign) {
        return JWT.create().withAudience(data) // 将 userId-role 保存到 token 里面,作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2)) // 2小时后token过期
                .sign(Algorithm.HMAC256(sign)); // 以 password动态的 作为 token 的密钥，使用HMAC256加密方式
    }

    /**
     * 获取当前登录的用户信息
     */
    public static Account getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader(Constants.TOKEN);
            if (ObjectUtil.isNotEmpty(token)) {
                // 将token解密
                String userRole = JWT.decode(token).getAudience().get(0);
                String userId = userRole.split("-")[0];  // 获取用户id
                String role = userRole.split("-")[1];    // 获取角色
                if (RoleEnum.ADMIN.name().equals(role)) {
                    return staticAdminService.selectById(Integer.valueOf(userId));
                } else if (RoleEnum.BUSINESS.name().equals(role)) {
                    return staticBusinessService.selectById(Integer.valueOf(userId));
                } else if (RoleEnum.USER.name().equals(role)) {
                    return staticUserService.selectById(Integer.valueOf(userId));
                }
            }
        } catch (Exception e) {
            log.error("获取当前用户信息出错", e);
        }
        return new Account();  // 查询失败返回空的账号对象
    }
}

