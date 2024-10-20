package com.xiangyueEducation.uploaderCloud.Service.pojoPackage;

import com.xiangyueEducation.uploaderCloud.POJO.ProxyUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;

/**
* @author 86136
* @description 针对表【proxy_user】的数据库操作Service
* @createDate 2024-09-23 11:39:13
*/
public interface ProxyUserService extends IService<ProxyUser> {
    Result getAllProxyUser();
}
