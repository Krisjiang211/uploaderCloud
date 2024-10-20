package com.xiangyueEducation.uploaderCloud.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.POJO.Department;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 86136
* @description 针对表【department】的数据库操作Mapper
* @createDate 2024-05-16 15:01:08
* @Entity com.xiangyueEducation.uploaderCloud.POJO.Department
*/
public interface DepartmentMapper extends BaseMapper<Department> {

    List<Department> selectAll(Page<Department> page);

    List<String> getAllName();

    Integer getIdByName(String departmentName);
    String getNameById(Integer departmentId);
}




