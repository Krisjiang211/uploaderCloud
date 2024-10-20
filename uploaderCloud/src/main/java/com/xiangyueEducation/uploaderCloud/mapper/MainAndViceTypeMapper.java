package com.xiangyueEducation.uploaderCloud.mapper;

import com.xiangyueEducation.uploaderCloud.POJO.MainAndViceType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiangyueEducation.uploaderCloud.admin.VO.MainAndViceTypeVO;

import java.util.List;

/**
* @author 86136
* @description 针对表【main_and_vice_type】的数据库操作Mapper
* @createDate 2024-05-20 22:13:25
* @Entity com.xiangyueEducation.uploaderCloud.POJO.MainAndViceType
*/
public interface MainAndViceTypeMapper extends BaseMapper<MainAndViceType> {

    //获取表中所有内容
    MainAndViceType[] selectAll();

    MainAndViceType[] getAll();

    List<MainAndViceTypeVO> getAllVO();
}




