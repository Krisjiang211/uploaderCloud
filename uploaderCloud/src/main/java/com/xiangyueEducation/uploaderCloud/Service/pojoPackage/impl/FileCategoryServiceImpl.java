package com.xiangyueEducation.uploaderCloud.Service.pojoPackage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiangyueEducation.uploaderCloud.POJO.FileCategory;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.FileCategoryService;
import com.xiangyueEducation.uploaderCloud.Utils.DateUtils;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.mapper.FileCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author 86136
* @description 针对表【file_category】的数据库操作Service实现
* @createDate 2024-05-16 15:01:07
*/
@Service
public class FileCategoryServiceImpl extends ServiceImpl<FileCategoryMapper, FileCategory>
    implements FileCategoryService{

    @Autowired
    private FileCategoryMapper fileCategoryMapper;


    @Override
    public Result getFileCategory() {

        List<String> categorys = fileCategoryMapper.selectAllCategory();
        return Result.ok(categorys);
    }

    @Override
    public Result getDetailTable() {
        List<FileCategory> fileCategoryList = fileCategoryMapper.getAll();
        return Result.ok(fileCategoryList);
    }

    @Override
    public Result addFileCategory(String name) {
        QueryWrapper<FileCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("category_name",name);
        FileCategory fileCategory = fileCategoryMapper.selectOne(wrapper);
        if (fileCategory != null){
            fileCategory.setIsDelete(0);
            fileCategoryMapper.updateById(fileCategory);
            return Result.ok("新增成功");
        }
        FileCategory fileCate = new FileCategory();
        fileCate.setCategoryName(name);
        fileCate.setIsDelete(0);
        fileCategoryMapper.insert(fileCate);
        return Result.ok("新增成功");


    }

    @Override
    public Result delFileCategory(String name) {
        QueryWrapper<FileCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("category_name",name);
        FileCategory fileCategory = fileCategoryMapper.selectOne(wrapper);
        fileCategory.setIsDelete(1);
        fileCategoryMapper.updateById(fileCategory);
        return Result.ok("删除成功");
    }


}




