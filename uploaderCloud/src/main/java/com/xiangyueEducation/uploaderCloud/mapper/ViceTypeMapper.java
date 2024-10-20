package com.xiangyueEducation.uploaderCloud.mapper;

import com.xiangyueEducation.uploaderCloud.POJO.ViceType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 86136
* @description 针对表【vice_type】的数据库操作Mapper
* @createDate 2024-05-20 22:13:25
* @Entity com.xiangyueEducation.uploaderCloud.POJO.ViceType
*/
public interface ViceTypeMapper extends BaseMapper<ViceType> {
    String[] selectAllViceTypeName();

    ViceType[] getAll();
}




