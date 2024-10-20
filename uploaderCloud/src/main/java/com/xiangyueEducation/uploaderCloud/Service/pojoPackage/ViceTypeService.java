package com.xiangyueEducation.uploaderCloud.Service.pojoPackage;

import com.xiangyueEducation.uploaderCloud.POJO.ViceType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import org.springframework.http.ResponseEntity;

/**
* @author 86136
* @description 针对表【vice_type】的数据库操作Service
* @createDate 2024-05-20 22:13:25
*/
public interface ViceTypeService extends IService<ViceType> {
    Result getViceType(String token);
}
