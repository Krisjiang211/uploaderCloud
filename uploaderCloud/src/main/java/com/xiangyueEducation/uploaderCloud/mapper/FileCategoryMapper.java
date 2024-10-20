package com.xiangyueEducation.uploaderCloud.mapper;

import com.xiangyueEducation.uploaderCloud.POJO.FileCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
* @author 86136
* @description 针对表【file_category】的数据库操作Mapper
* @createDate 2024-05-16 15:01:07
* @Entity com.xiangyueEducation.uploaderCloud.POJO.FileCategory
*/
public interface FileCategoryMapper extends BaseMapper<FileCategory> {

    List<String> selectAllCategory();

    List<FileCategory> getAll();

    Integer selectCategoryIdByName(String categoryName);



}




