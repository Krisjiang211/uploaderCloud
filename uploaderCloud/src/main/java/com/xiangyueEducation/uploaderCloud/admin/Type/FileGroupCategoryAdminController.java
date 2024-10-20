package com.xiangyueEducation.uploaderCloud.admin.Type;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiangyueEducation.uploaderCloud.POJO.FileGroupCategory;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.FileGroupCategoryService;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.ResultCodeEnum;
import com.xiangyueEducation.uploaderCloud.admin.Type.service.FileGroupCategoryAdminService;
import com.xiangyueEducation.uploaderCloud.admin.utils.AdminUtils;
import com.xiangyueEducation.uploaderCloud.mapper.FileGroupCategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/admin/type/fileGroup")
public class FileGroupCategoryAdminController {




    @Autowired
    private FileGroupCategoryMapper fileGroupCategoryMapper;
    @Autowired
    private FileGroupCategoryAdminService fileGroupCategoryAdminService;
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private AdminUtils adminUtils;
    //1. 获取所有的文件类型表
    @GetMapping("all")
    public Result getAllFileGroupCategory(){
        List<FileGroupCategory> all = fileGroupCategoryMapper.getAll();
        return Result.ok(all);
    }

    //2. 增加一个
    @PostMapping("add")
    public Result addOne(@RequestBody FileGroupCategory fileGroupCategory,
                         @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够", ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
//        fileGroupCategory.setIsDelete(0);
//        fileGroupCategoryMapper.insert(fileGroupCategory);
        Result result = fileGroupCategoryAdminService.addOne(fileGroupCategory);
        return result;
    }

    //3. (逻辑)删除一个
    @GetMapping("del")
    public Result delOne(@RequestParam("cateName") String cateName,
                         @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }

        QueryWrapper<FileGroupCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("category_name",cateName);
        FileGroupCategory fileGroupCategory = fileGroupCategoryMapper.selectOne(wrapper);
        fileGroupCategory.setIsDelete(1);
        fileGroupCategoryMapper.updateById(fileGroupCategory);
        return Result.ok("逻辑删除成功");

    }

    //4. 修改一个
    @PostMapping("update")
    public Result updateOne(@RequestBody FileGroupCategory newfileGroupCategory,
                            @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        fileGroupCategoryMapper.updateById(newfileGroupCategory);
        return Result.ok("修改 文件种类名字 成功");
    }

    //4.2 修改状态(is_delete)
    @GetMapping("update/status/{fileGroupCategoryId}")
    public Result updateStatus(@PathVariable("fileGroupCategoryId") Integer fileGroupCategoryId,
                               @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        Result result = fileGroupCategoryAdminService.changeStatus(fileGroupCategoryId);
        return result;

    }
}
