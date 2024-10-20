package com.xiangyueEducation.uploaderCloud.admin.Type;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiangyueEducation.uploaderCloud.POJO.FileCategory;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.ResultCodeEnum;
import com.xiangyueEducation.uploaderCloud.admin.Type.service.FileCategoryAdminService;
import com.xiangyueEducation.uploaderCloud.admin.utils.AdminUtils;
import com.xiangyueEducation.uploaderCloud.mapper.FileCategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/admin/type/file")
public class FileCategoryController {

    @Autowired
    private FileCategoryMapper fileCategoryMapper;
    @Autowired
    private FileCategoryAdminService fileCategoryAdminService;
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private AdminUtils adminUtils;
    //1. 获取所有的文件类型表
    @GetMapping("all")
    public Result getAllFileCategory(){
        List<FileCategory> all = fileCategoryMapper.getAll();
        return Result.ok(all);
    }

    //2. 增加一个
    @PostMapping("add")
    public Result addOne(@RequestBody FileCategory fileCategory,
                         @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够", ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        Result result = fileCategoryAdminService.addOne(fileCategory);
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

        QueryWrapper<FileCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("category_name",cateName);
        FileCategory fileCategory = fileCategoryMapper.selectOne(wrapper);
        fileCategory.setIsDelete(1);
        fileCategoryMapper.updateById(fileCategory);
        return Result.ok("逻辑删除成功");

    }

    //4. 修改一个
    @PostMapping("update")
    public Result updateOne(@RequestBody FileCategory newFileCategory,
                            @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }

        fileCategoryMapper.updateById(newFileCategory);
        return Result.ok("修改 文件种类名字 成功");
    }

    //4.2 修改状态(is_delete)
    @GetMapping("update/status/{fileCategoryId}")
    public Result updateStatus(@PathVariable("fileCategoryId") Integer fileCategoryId,
                               @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        Result result = fileCategoryAdminService.changeStatus(fileCategoryId);
        return result;

    }

}
