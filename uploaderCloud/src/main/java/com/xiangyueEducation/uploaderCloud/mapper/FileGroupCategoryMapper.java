package com.xiangyueEducation.uploaderCloud.mapper;

import com.xiangyueEducation.uploaderCloud.POJO.FileCategory;
import com.xiangyueEducation.uploaderCloud.POJO.FileGroupCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 86136
* @description 针对表【file_group_category】的数据库操作Mapper
* @createDate 2024-05-16 15:01:07
* @Entity com.xiangyueEducation.uploaderCloud.POJO.FileGroupCategory
*/
public interface FileGroupCategoryMapper extends BaseMapper<FileGroupCategory> {

    List<String> selectAllCategory();

    List<FileGroupCategory> getAll();

}




