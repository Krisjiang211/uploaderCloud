package com.xiangyueEducation.uploaderCloud.Controller.POJOController;

import com.xiangyueEducation.uploaderCloud.POJO.User;
import com.xiangyueEducation.uploaderCloud.POJO.UserInfo;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.UserInfoService;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.UserService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.mapper.UserInfoMapper;
import com.xiangyueEducation.uploaderCloud.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("userInfo")
@CrossOrigin
public class UserInfoController {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("get/{uuid}")
    public Result getUserTableById(@PathVariable("uuid") String uuid){
        UserInfo data = userInfoMapper.selectById(uuid);
        return Result.ok(data);
    }

}