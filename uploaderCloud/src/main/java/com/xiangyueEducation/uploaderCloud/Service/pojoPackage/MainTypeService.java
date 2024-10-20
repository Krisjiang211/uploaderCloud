package com.xiangyueEducation.uploaderCloud.Service.pojoPackage;

import com.xiangyueEducation.uploaderCloud.POJO.MainType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import org.springframework.core.io.ResourceLoader;

/**
* @author 86136
* @description 针对表【main_type】的数据库操作Service
* @createDate 2024-05-20 22:13:25
*/
public interface MainTypeService extends IService<MainType> {

    Result getMainType(String token);
}
