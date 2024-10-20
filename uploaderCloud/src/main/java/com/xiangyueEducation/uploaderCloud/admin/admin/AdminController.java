package com.xiangyueEducation.uploaderCloud.admin.admin;


import com.xiangyueEducation.uploaderCloud.POJO.Admin;
import com.xiangyueEducation.uploaderCloud.POJO.User;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.ResultCodeEnum;
import com.xiangyueEducation.uploaderCloud.admin.utils.AdminUtils;
import com.xiangyueEducation.uploaderCloud.mapper.AdminMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/admin/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private AdminUtils adminUtils;
    @Autowired
    private AdminMapper adminMapper;
    // 登录
    @PostMapping("login")
    public Result login(@RequestBody Admin admin){
        Result result=adminService.login(admin);
        return result;
    }


    /**
     * 检查用户名是否被使用
     * @param ID
     */
    @GetMapping("checkIDUsed")
    public Result checkIDUsed(@RequestParam("identityCardCode") String ID){
        Result<User> IDStatus = adminService.checkIDUsed(ID);
        return IDStatus;
    }


    /**
     * 注册功能
     * @param registerInfo
     */
    @PostMapping("register")
    public Result register(@RequestBody Admin registerInfo,
                           @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够", ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        Result<Admin> registerStatus=adminService.register(registerInfo);
        return registerStatus;
    }


    /**
     * 检查登录状态
     * @param token
     */
    @GetMapping("checkLogin")
    public Result checkLogin(@RequestHeader String token){
        Result loginStatus=adminService.checkLogin(token);
        return loginStatus;
    }


    /**
     * 修改密码
     * @param pwdInfo
     * @param token
     */
    @PostMapping("changePwd")
    public Result changePwd(@RequestBody HashMap<String,String> pwdInfo, @RequestHeader String token){
        Result result=adminService.changePwd(pwdInfo,token);
        return result;
    }

    @GetMapping ("changePwd/{adminId}")
    public Result changePwd(@RequestParam("newPwd") String newPwd,
                           @PathVariable("adminId") String adminId,
                           @RequestHeader("token") String token){
        String adminIdDo = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminIdDo)){
            return Result.build("权限不够", ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        Result result=adminService.changePwd(newPwd,adminId);
        return result;
    }


    @GetMapping("get/all")
    public Result getAllAdmin(){
        return adminService.getAllAdmin();
    }

    @GetMapping("change/status/{adminId}")
    public Result changeStatus(@PathVariable("adminId") String adminId,
                               @RequestHeader("token") String token){
        String adminId1 = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId1)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }

        return adminService.changeStatus(adminId);
    }

    @PostMapping("edit")
    public Result edit(@RequestBody Admin admin,
                       @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }

        Admin admin1 = adminMapper.selectById(admin.getAdminId());
        if (admin1.getAccount().equals("adminsuper")){
            return Result.build("超级管理员无法修改", ResultCodeEnum.ADMIN_BAND);
        }
        return adminService.edit(admin);
    }


}
