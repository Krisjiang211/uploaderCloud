package com.xiangyueEducation.uploaderCloud.Service.pojoPackage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiangyueEducation.uploaderCloud.POJO.FileCategory;
import com.xiangyueEducation.uploaderCloud.POJO.FileGroupCategory;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.FileGroupCategoryService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.mapper.FileGroupCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Kris_Jiang
* @description 针对表【file_group_category】的数据库操作Service实现
* @createDate 2024-05-16 15:01:08
*/
@Service
public class FileGroupCategoryServiceImpl extends ServiceImpl<FileGroupCategoryMapper, FileGroupCategory>
    implements FileGroupCategoryService{


    @Autowired
    private FileGroupCategoryMapper fileGroupCategoryMapper;


    @Override
    public Result getFileGroupCategory() {

        List<String> categorys = fileGroupCategoryMapper.selectAllCategory();
        return Result.ok(categorys);
    }

    @Override
    public Result getDetailTable() {
        List<FileGroupCategory> fileCategoryList = fileGroupCategoryMapper.getAll();
        return Result.ok(fileCategoryList);
    }

    @Override
    public Result addFileGroupCategory(String name) {
        List<FileGroupCategory> all = fileGroupCategoryMapper.getAll();
        for (FileGroupCategory fileGroupCategory : all) {
            if (fileGroupCategory.getCategoryName().equals(name)){
                fileGroupCategory.setIsDelete(0);
                fileGroupCategoryMapper.updateById(fileGroupCategory);
                return Result.ok("新增成功");
            }
        }
        FileGroupCategory fileGroupCategory = new FileGroupCategory();
        fileGroupCategory.setCategoryName(name);
        fileGroupCategory.setIsDelete(0);
        fileGroupCategoryMapper.insert(fileGroupCategory);
        return Result.ok("新增成功");
    }

    @Override
    public Result delFileGroupCategory(String name) {
        QueryWrapper<FileGroupCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("category_name",name);
        FileGroupCategory fileGroupCategory = fileGroupCategoryMapper.selectOne(wrapper);
        fileGroupCategory.setIsDelete(1);
        fileGroupCategoryMapper.updateById(fileGroupCategory);
        return Result.ok("删除成功");
    }
}




