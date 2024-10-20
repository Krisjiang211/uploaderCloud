package com.xiangyueEducation.uploaderCloud.Service.pojoPackage;

import com.xiangyueEducation.uploaderCloud.POJO.FileCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;

/**
* @author 86136
* @description 针对表【file_category】的数据库操作Service
* @createDate 2024-05-16 15:01:07
*/
public interface FileCategoryService extends IService<FileCategory> {

    Result getFileCategory();

    Result getDetailTable();

    Result addFileCategory(String name);

    Result delFileCategory(String name);




}
