package com.xiangyueEducation.uploaderCloud.Service.pojoPackage;

import com.xiangyueEducation.uploaderCloud.POJO.DepartmentAndUsers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import org.springframework.web.bind.annotation.RequestHeader;

/**
* @author 86136
* @description 针对表【department_and_users】的数据库操作Service
* @createDate 2024-05-16 15:01:08
*/
public interface DepartmentAndUsersService extends IService<DepartmentAndUsers> {
    Result getDepartmentByUuid(String token);

    Result getDepartmentIdAndNameAll();
}
