package com.xiangyueEducation.uploaderCloud.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.POJO.Authorise;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 86136
* @description 针对表【authorise】的数据库操作Mapper
* @createDate 2024-06-03 23:54:17
* @Entity com.xiangyueEducation.uploaderCloud.POJO.Authorise
*/
public interface AuthoriseMapper extends BaseMapper<Authorise> {

    List<Authorise> selectRole1UsePage(String order,String roleUuid, Page<Authorise> page);

    List<Authorise> selectRole2UsePage(String order,String uuid, Page<Authorise> page);

    Integer selectMaxPageRole1(String uuid,Integer pageSize);

    Integer selectMaxPageRole2(String uuid,Integer pageSize);



}




