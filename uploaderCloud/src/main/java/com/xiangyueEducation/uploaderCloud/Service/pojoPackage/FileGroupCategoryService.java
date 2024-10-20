package com.xiangyueEducation.uploaderCloud.Service.pojoPackage;

import com.xiangyueEducation.uploaderCloud.POJO.FileGroupCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;

/**
* @author 86136
* @description 针对表【file_group_category】的数据库操作Service
* @createDate 2024-05-16 15:01:08
*/
public interface FileGroupCategoryService extends IService<FileGroupCategory> {
    Result getFileGroupCategory();

    Result getDetailTable();

    Result addFileGroupCategory(String name);

    Result delFileGroupCategory(String name);
}
