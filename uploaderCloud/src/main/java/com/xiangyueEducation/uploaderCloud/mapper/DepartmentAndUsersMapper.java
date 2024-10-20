package com.xiangyueEducation.uploaderCloud.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.POJO.DepartmentAndUsers;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;

import java.util.List;
import java.util.Random;

/**
* @author 86136
* @description 针对表【department_and_users】的数据库操作Mapper
* @createDate 2024-05-16 15:01:08
* @Entity com.xiangyueEducation.uploaderCloud.POJO.DepartmentAndUsers
*/
public interface DepartmentAndUsersMapper extends BaseMapper<DepartmentAndUsers> {
    List<String> getUserPageByDepartmentName(Page page, Integer departmentId);
}




