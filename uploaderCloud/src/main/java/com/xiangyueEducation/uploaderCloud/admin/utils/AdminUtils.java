package com.xiangyueEducation.uploaderCloud.admin.utils;


import com.xiangyueEducation.uploaderCloud.POJO.Admin;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminUtils {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 判断是否是超级管理员(有无修改权限)
     * @param adminId
     * @return
     */
    public Boolean isSuperAdmin(String adminId){
        Admin admin = adminMapper.selectById(adminId);
        if(admin.getRoleId().equals(1)){
            return true;
        }else{
            return false;
        }
    }
}
