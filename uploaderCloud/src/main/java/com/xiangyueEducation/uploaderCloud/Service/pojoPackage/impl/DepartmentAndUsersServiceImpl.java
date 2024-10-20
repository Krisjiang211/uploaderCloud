package com.xiangyueEducation.uploaderCloud.Service.pojoPackage.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiangyueEducation.uploaderCloud.POJO.Department;
import com.xiangyueEducation.uploaderCloud.POJO.DepartmentAndUsers;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.DepartmentAndUsersService;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.mapper.DepartmentAndUsersMapper;
import com.xiangyueEducation.uploaderCloud.mapper.DepartmentMapper;
import org.junit.AfterClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 86136
* @description 针对表【department_and_users】的数据库操作Service实现
* @createDate 2024-05-16 15:01:08
*/
@Service
public class DepartmentAndUsersServiceImpl extends ServiceImpl<DepartmentAndUsersMapper, DepartmentAndUsers>
    implements DepartmentAndUsersService{


    @Autowired
    private DepartmentAndUsersMapper departmentAndUsersMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    public Result getDepartmentByUuid(String token) {
        String uuid = jwtHelper.getUserId(token);
        DepartmentAndUsers departmentAndUsers = departmentAndUsersMapper.selectById(uuid);
        Integer departmentId = departmentAndUsers.getDepartmentId();
        Department department = departmentMapper.selectById(departmentId);
        return Result.ok(department);
    }

    @Override
    public Result getDepartmentIdAndNameAll() {
        List<String> allName = departmentMapper.getAllName();

        return Result.ok(allName);
    }
}




