package com.xiangyueEducation.uploaderCloud.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.POJO.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 86136
* @description 针对表【admin】的数据库操作Mapper
* @createDate 2024-05-16 15:01:08
* @Entity com.xiangyueEducation.uploaderCloud.POJO.Admin
*/
public interface AdminMapper extends BaseMapper<Admin> {

    List<Admin> getAll(Page<Admin> page);

}




