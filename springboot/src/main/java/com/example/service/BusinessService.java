package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Business;
import com.example.entity.Collect;
import com.example.exception.CustomException;
import com.example.mapper.BusinessMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 商家相关的业务方法
 */
@Service
public class BusinessService {

    @Resource
    private BusinessMapper businessMapper;
    @Resource
    private CollectService collectService;

    /**
     * 新增商家
     */
    public void add(Business business) {
        if (ObjectUtil.isEmpty(business.getUsername()) || ObjectUtil.isEmpty(business.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        Business dbBusiness = this.selectByUsername(business.getUsername());
        // 如果根据新增数据的账号查询查到了数据  那么这个数据不允许插入，因为账号不能重复
        if (ObjectUtil.isNotEmpty(dbBusiness)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        business.setRole(RoleEnum.BUSINESS.name());  // 默认角色为BUSINESS
        businessMapper.insert(business);
    }

    /**
     * 单个删除
     */
    public void deleteById(Integer id) {
        businessMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    /**
     * 修改商家
     */
    public void updateById(Business business) {
        // 先根据id查询商家是否存在，商家不存在那就返回错误信息
        Business dbBusiness1 = selectById(business.getId());
        if (ObjectUtil.isEmpty(dbBusiness1)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        Business dbBusiness2 = this.selectByUsername(business.getUsername());
        // 如果当前修改的用户查询到数据库中存在名称相同，但是id不同的用户，那么当前的更新是不合法的及数据重复了（id为主键不相同，但是用户名称可能相同）
        if (ObjectUtil.isNotEmpty(dbBusiness2) && !Objects.equals(dbBusiness2.getId(), business.getId())) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        businessMapper.updateById(business);
    }

    /**
     * 查询所有
     */
    public List<Business> selectAll(Business business) {
        return businessMapper.selectAll(business);
    }

    /**
     * 根据账号查询
     */
    public Business selectByUsername(String username) {
        Business params = new Business();
        params.setUsername(username);
        List<Business> list = this.selectAll(params);
        return list.size() == 0 ? null : list.get(0);
    }

    /**
     * 根据ID查询
     */
    public Business selectById(Integer id) {
        Business params = new Business();
        params.setId(id);
        List<Business> list = this.selectAll(params);
        Business business = list.size() == 0 ? null : list.get(0);
        // 当business不为空
        if (ObjectUtil.isNotEmpty(business)) {
            Account currentUser = TokenUtils.getCurrentUser();
            Collect collect = collectService.selectByUserIdAndBusinessId(currentUser.getId(), id);
            // 如果查询到用户收藏了某商家，则返回该商家的isCollect为true
            business.setIsCollect(ObjectUtil.isNotEmpty(collect));
        }

        return business;
    }

    /**
     * 分页条件查询
     */
    public PageInfo<Business> selectPage(Business business, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);  // 调用PageHelper插件配置分页查询的规则
        List<Business> list = businessMapper.selectAll(business);  // 获取所有查询的数据
        return PageInfo.of(list);  // 将查询的数据返回到分页查询规则的方法内返回数据
    }

    /**
     * 注册商家
     **/
    public void register(Account account) {
        Business business = new Business();
        BeanUtils.copyProperties(account, business); // 拷贝 账号和密码两个属性到对象business内
        if (ObjectUtil.isEmpty(business.getName())) { // 如果用户的name为空，将username设置为name
            business.setName(business.getUsername());
        }
        this.add(business); // 添加账户信息
    }

    /**
     * 商家登录
     **/
    public Account login(Account account) {
        Account dbBusiness = this.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbBusiness)) {  // 查询数据库是否有该用户
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbBusiness.getPassword())) {  // 比较用户名密码是否一致
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbBusiness.getId() + "-" + RoleEnum.BUSINESS.name();
        String token = TokenUtils.createToken(tokenData, dbBusiness.getPassword());
        dbBusiness.setToken(token);
        return dbBusiness;
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        Business dbBusiness = this.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbBusiness)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbBusiness.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbBusiness.setPassword(account.getNewPassword());
        this.updateById(dbBusiness);
    }

    /**
     * 检查商家的权限，查看是否可以新增数据
     **/
    public void checkBusinessAuth() {
        Account currentUser = TokenUtils.getCurrentUser(); // 获取当前的用户信息
        if (RoleEnum.BUSINESS.name().equals(currentUser.getRole())) { // 如果用户是商家的话
            Business business = selectById(currentUser.getId());
            if (!"通过".equals(business.getStatus())) { // 如果商家的status不是通过，抛出无权限异常
                throw new CustomException(ResultCodeEnum.USER_NOAUTH_ERROR);
            }
        }

    }
}