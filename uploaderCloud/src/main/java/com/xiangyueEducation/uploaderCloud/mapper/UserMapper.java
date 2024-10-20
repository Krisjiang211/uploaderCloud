package com.xiangyueEducation.uploaderCloud.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.POJO.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiangyueEducation.uploaderCloud.admin.VO.UserAndUserInfoVO;

import java.util.List;

/**
* @author 86136
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-05-18 00:55:21
* @Entity com.xiangyueEducation.uploaderCloud.POJO.User
*/
public interface UserMapper extends BaseMapper<User> {


    //admin级别操作
    List<UserAndUserInfoVO> selectAll(Page<UserAndUserInfoVO> page);
    Integer selectAllMaxPage(Integer pageSize);



}




