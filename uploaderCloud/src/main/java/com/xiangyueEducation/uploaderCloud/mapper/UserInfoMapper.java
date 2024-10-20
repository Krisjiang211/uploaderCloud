package com.xiangyueEducation.uploaderCloud.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.POJO.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 86136
* @description 针对表【user_info】的数据库操作Mapper
* @createDate 2024-05-18 15:00:34
* @Entity com.xiangyueEducation.uploaderCloud.POJO.UserInfo
*/
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    List<String> getUuidByUserNameSearch(Page page, String userName);
}




