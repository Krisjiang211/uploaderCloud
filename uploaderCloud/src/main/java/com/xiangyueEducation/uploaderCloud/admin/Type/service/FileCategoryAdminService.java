package com.xiangyueEducation.uploaderCloud.admin.Type.service;


import com.xiangyueEducation.uploaderCloud.POJO.FileCategory;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.ResultCodeEnum;
import com.xiangyueEducation.uploaderCloud.mapper.FileCategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FileCategoryAdminService {

    @Autowired
    private FileCategoryMapper fileCategoryMapper;


    public Result changeStatus(Integer fileCategoryId){
        FileCategory fileCategory = fileCategoryMapper.selectById(fileCategoryId);
        if (fileCategory.getIsDelete().equals(1)){
            fileCategory.setIsDelete(0);
            fileCategoryMapper.updateById(fileCategory);
            return Result.ok("修改成功");
        }
        else{
            fileCategory.setIsDelete(1);
            fileCategoryMapper.updateById(fileCategory);
            return Result.ok("修改成功");
        }
    }

    public Result addOne(FileCategory fileCategory){
        //查重
        List<FileCategory> all = fileCategoryMapper.getAll();
        for (FileCategory item : all){
            if (item.getCategoryName().equals(fileCategory.getCategoryName())){
                return Result.build("种类名不可相同",ResultCodeEnum.QUERY_HAVE_SAME);
            }
        }

        fileCategory.setIsDelete(0);
        fileCategoryMapper.insert(fileCategory);
        return Result.ok("添加成功");
    }
}
