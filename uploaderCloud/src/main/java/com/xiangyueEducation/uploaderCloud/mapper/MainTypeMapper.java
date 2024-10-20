package com.xiangyueEducation.uploaderCloud.mapper;

import com.xiangyueEducation.uploaderCloud.POJO.MainType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 86136
* @description 针对表【main_type】的数据库操作Mapper
* @createDate 2024-05-20 22:13:25
* @Entity com.xiangyueEducation.uploaderCloud.POJO.MainType
*/
public interface MainTypeMapper extends BaseMapper<MainType> {
    String[] selectAllMainTypeName();

    //获取表中所有内容
    MainType[] selectAll();
}




