package com.xiangyueEducation.uploaderCloud.admin.Type.service;


import com.xiangyueEducation.uploaderCloud.POJO.FileGroupCategory;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.ResultCodeEnum;
import com.xiangyueEducation.uploaderCloud.mapper.FileGroupCategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FileGroupCategoryAdminService {

    @Autowired
    private FileGroupCategoryMapper fileGroupCategoryMapper;


    public Result changeStatus(Integer fileGroupCategoryId){
        FileGroupCategory fileGroupCategory = fileGroupCategoryMapper.selectById(fileGroupCategoryId);
        if (fileGroupCategory.getIsDelete().equals(1)){
            fileGroupCategory.setIsDelete(0);
            fileGroupCategoryMapper.updateById(fileGroupCategory);
            return Result.ok("修改成功");
        }
        else{
            fileGroupCategory.setIsDelete(1);
            fileGroupCategoryMapper.updateById(fileGroupCategory);
            return Result.ok("修改成功");
        }
    }

    public Result addOne(FileGroupCategory fileGroupCategory){
        //查重
        List<FileGroupCategory> all = fileGroupCategoryMapper.getAll();
        for (FileGroupCategory item : all){
            if (item.getCategoryName().equals(fileGroupCategory.getCategoryName())){
                return Result.build("种类名不可相同", ResultCodeEnum.QUERY_HAVE_SAME);
            }
        }

        fileGroupCategory.setIsDelete(0);
        fileGroupCategoryMapper.insert(fileGroupCategory);
        return Result.ok("添加成功");
    }
    
    
}
