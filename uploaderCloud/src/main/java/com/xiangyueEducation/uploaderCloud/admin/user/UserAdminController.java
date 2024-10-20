package com.xiangyueEducation.uploaderCloud.admin.user;


import com.xiangyueEducation.uploaderCloud.POJO.User;
import com.xiangyueEducation.uploaderCloud.POJO.UserInfo;
import com.xiangyueEducation.uploaderCloud.Service.funPackage.AccountService;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.UserInfoService;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.UserService;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.PathEnum;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.ResultCodeEnum;
import com.xiangyueEducation.uploaderCloud.admin.utils.AdminUtils;
import com.xiangyueEducation.uploaderCloud.mapper.UserInfoMapper;
import com.xiangyueEducation.uploaderCloud.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/admin/user")
@CrossOrigin
public class UserAdminController {

    @Autowired
    private UserAdminService userAdminService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private AdminUtils adminUtils;

    @GetMapping("get/all/{currentPage}")
    public Result getAllUser(@PathVariable("currentPage") Integer currentPage,
                             @RequestParam(value = "departmentName",required = false) String departmentName,
                             @RequestParam(value = "userName",required = false) String userName){

        if (departmentName==null&userName==null){
            return userAdminService.getAllUser(currentPage);
        } else if (departmentName!=null&userName==null) {
            return userAdminService.getUserByDepartmentName(currentPage,departmentName);
        }else if (departmentName==null&userName!=null){
            return userAdminService.getUserByUserName(currentPage,userName);
        }else {
            return null;
        }
    }


    // 1. 增
    @PostMapping("add")
    public Result addUser(@RequestBody User user,
                          @RequestHeader("token")String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        return accountService.register(user);
    }
    // 2. 停用

    @GetMapping("disabled/{uuid}")
    public Result disabledUser(@PathVariable("uuid") String uuid,
                               @RequestHeader("token")String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        return userAdminService.disabledUser(uuid);
    }

    // 3. 改
    //3.1 user编辑
    @PostMapping("edit/user")
    public Result editUser(@RequestBody User user,
                           @RequestHeader("token")String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
//        System.out.println("user = " + user);
        return userAdminService.editUser(user);
    }

    //3.2 userInfo编辑
    @PostMapping("edit/userInfo")
    public Result editUserInfo(@RequestBody UserInfo userInfo,
                               @RequestHeader("token")String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        return userAdminService.editUserInfo(userInfo);
    }
    //3.3 改变用户状态
    @GetMapping("edit/user/status/{uuid}")
    public Result editUserStatus(@PathVariable("uuid") String uuid,
                                 @RequestHeader("token")String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }

        System.out.println("uuid = " + uuid);
        return userAdminService.editUserStatus(uuid);
    }

    //3.4 改变头像
    @PostMapping("update/user/avatar/{uuid}")
    public Result updateUserAvatarAdmin(@RequestParam(value = "avatar", required = false) MultipartFile avatar,
                                   @PathVariable("uuid") String uuid,
                                        @RequestHeader("token")String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        Result  result = userInfoService.updateUserAvatarAdmin(uuid,avatar);
        return result;
    }
}
