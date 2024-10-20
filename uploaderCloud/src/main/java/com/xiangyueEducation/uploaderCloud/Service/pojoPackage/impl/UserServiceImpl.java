package com.xiangyueEducation.uploaderCloud.Service.pojoPackage.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiangyueEducation.uploaderCloud.POJO.User;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.UserService;
import com.xiangyueEducation.uploaderCloud.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 86136
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-05-18 00:55:21
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




