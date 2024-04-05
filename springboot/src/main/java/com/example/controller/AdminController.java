package com.example.controller;

import com.example.common.Result;
import com.example.entity.Admin;
import com.example.service.AdminService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 管理员前端操作接口
 * controller层：
 * @RestController: 表示返回的内容是请求体
 * @RequestMapping("/admin")： 第一层接口路径
 * @xxxMapping("/add")： 第二层接口路径
 *  xxx表示请求方式：
 *      Post:新增
 *      Delete:删除
 *      Put:修改
 *      Get:查询
 * @Resource： 表示依赖注入
 * @RequestBody: 表示从http请求的请求体当中获取数据
 **/

@RestController
@RequestMapping("/admin")
public class AdminController {

    // @Resource 告诉Spring需要自动注入一个类型为 AdminService 的 Bean 到 adminService 这个字段，不需要手动去实例化 AdminService 获取对象
    @Resource
    private AdminService adminService;

    /**
     * 新增
     * @RequestBody 是SpringMVC的注解，用于指示方法参数应该从 HTTP 请求的主体部分中获取数据
     * add(@RequestBody Admin admin)表示客户端发送的 POST 请求应该包含一个 JSON 格式的请求体，其中包含了 Admin 对象的属性。Spring MVC 将会自动将这个 JSON 数据转换为 Admin 对象，并传递给 add() 方法进行处理
     */
    @PostMapping("/add")
    public Result add(@RequestBody Admin admin) {
        adminService.add(admin);
        return Result.success();
    }

    /**
     * 删除
     * @PathVariable 是 Spring MVC 中的一个注解，用于将 URL 中的数据动态绑定到变量上，如/users/{id}
     * @PathVariable Integer id ，用于标记方法的参数 id，告诉 Spring MVC 框架要从 URL 中获取 id 对应的值，并将其绑定到方法的参数上。
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        adminService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        adminService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Admin admin) {
        adminService.updateById(admin);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Admin admin = adminService.selectById(id);
        return Result.success(admin);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Admin admin) {
        List<Admin> list = adminService.selectAll(admin);
        return Result.success(list);
    }

    /**
     * @RequestParam 是 Spring MVC 中的一个注解，用于从 HTTP 请求url的参数中获取数据，并将其绑定到方法的参数上，只适用于获取简单的查询参数，如 ?page=1&size=10
     * 如：@RequestParam(defaultValue = "1") Integer pageNum：这个注解表示将 HTTP 请求中名为 pageNum 的查询参数的值绑定到方法的参数 pageNum 上。如果请求中没有指定 pageNum 参数，则默认值为 1
     * pageNum：当前页码
     * pageSize：分页个数
     */
    @GetMapping("/selectPage")
    public Result selectPage(Admin admin,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Admin> page = adminService.selectPage(admin, pageNum, pageSize);
        return Result.success(page);
    }

}