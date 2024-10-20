package com.xiangyueEducation.uploaderCloud.Service.pojoPackage;

import com.xiangyueEducation.uploaderCloud.POJO.MainAndViceType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;

import java.util.Map;

/**
* @author 86136
* @description 针对表【main_and_vice_type】的数据库操作Service
* @createDate 2024-05-20 22:13:25
*/
public interface MainAndViceTypeService extends IService<MainAndViceType> {
    //获取viceType(其实是mainType和对应的viceType组成的map)
    Result getViceType(String token);


    //获取mainAndViceTypeId
    Result getMainAndViceTypeId(Map map);


    Result getOneToOne();


}
