package com.xiangyueEducation.uploaderCloud.Service.funPackage;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiangyueEducation.uploaderCloud.POJO.User;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.VO.LockAccountInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;
import java.util.Map;

public interface AccountService extends IService<User> {

    Result login(User user);

    boolean checkLocked(String uuid,String role);

    LockAccountInfo lockAccount(String uuid, String role);

    Result checkIDUsed(String ID);

    Result register(User registerInfo);

    Result checkLogin(String token);

    Result changePwd(HashMap<String,String> pwdInfo,String token);
}
