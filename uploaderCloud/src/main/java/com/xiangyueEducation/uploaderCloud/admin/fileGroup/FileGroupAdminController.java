package com.xiangyueEducation.uploaderCloud.admin.fileGroup;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.POJO.Publish;
import com.xiangyueEducation.uploaderCloud.POJO.TaskContent;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.ResultCodeEnum;
import com.xiangyueEducation.uploaderCloud.admin.VO.FileGroupVO;
import com.xiangyueEducation.uploaderCloud.admin.utils.AdminUtils;
import com.xiangyueEducation.uploaderCloud.mapper.TaskContentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/admin/fileGroup")
public class FileGroupAdminController {

    /**
     * 需要展示到前端的内容有:
     * 1. publish表:
     *   1.1 发布者ID uuid
     *   1.2 发布者名字 publishName(通过uuid间接获得name)
     *   1.3 部门名字 departmentName(通过departmentId间接获得)
     *   1.4 发布时间 createTime
     *   1.5 文件组类型 FileGroupCategoryName(通过fileGroupCategoryId间接获得)
     *   1.6 主/副类类型 MainAndViceTypeName(通过mainAndViceTypeId间接获得)
     * 2. taskContent表
     *   2.1 标题 title
     *   2.2 描述 description
     *   2.3 预览图片 previewPicture(通过preview_img_path然后 读取文件系统 获得)
     * 3. taskFileGroup表 (数组)
     *   3.1 文件名字<>数据库＋文件系统中文件,都改</>
     *   3.2 文件等级<>数据库＋文件系统中文件,都改</>
     *   3.3 文件大小  !!不可更改!!
     *   3.4 文件种类
     *   3.5 文件下载链接
     */

    @Autowired
    private FileGroupAdminService fileGroupAdminService;
    @Autowired
    private TaskContentMapper taskContentMapper;
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private AdminUtils adminUtils;

    @GetMapping("get/{currentPage}")
    public Result getFileGroup(@PathVariable("currentPage")Integer currentPage) {
        Integer pageSize=6;
        Page<Publish> page = new Page<>(currentPage, pageSize);
        Result result = fileGroupAdminService.getFileGroup(page);
        return result;
    }
    //根据文件组种类搜索
    @GetMapping("get/fileGroupCategory/{currentPage}/{fileGroupCategoryId}")
    public Result getFileGroupCate(@PathVariable("currentPage")Integer currentPage,
                                     @PathVariable("fileGroupCategoryId")Integer fileGroupCategoryId){

        Integer pageSize=6;
        Page<Publish> page = new Page<>(currentPage, pageSize);
        Result result = fileGroupAdminService.getFileGroupCate(page,fileGroupCategoryId);
        return result;
    }

    //根据主类搜索
    @GetMapping("get/mainType/{currentPage}/{mainTypeId}")
    public Result getFileGroupMain(@PathVariable("currentPage")Integer currentPage,
                                    @PathVariable("mainTypeId")Integer mainTypeId){

        Integer pageSize=6;
        Page<Publish> page = new Page<>(currentPage, pageSize);
        Result result = fileGroupAdminService.getFileGroupMain(page,mainTypeId);
        return result;
    }
    //根据副类搜索
    @GetMapping("get/viceType/{currentPage}/{viceTypeId}")
    public Result getFileGroupVice(@PathVariable("currentPage")Integer currentPage,
                                   @PathVariable("viceTypeId")Integer viceTypeId){

        Integer pageSize=6;
        Page<Publish> page = new Page<>(currentPage, pageSize);
        Result result = fileGroupAdminService.getFileGroupVice(page,viceTypeId);
        return result;
    }




    //根据文件种类搜索
    @GetMapping("get/fileCategory/{currentPage}/{fileCateGoryId}")
    public Result getFileGroupFileCate(@PathVariable("currentPage")Integer currentPage,
                                   @PathVariable("fileCateGoryId")Integer fileCateGoryId){

        Integer pageSize=6;
        Page<Publish> page = new Page<>(currentPage, pageSize);
        Result result = fileGroupAdminService.getFileGroupFileCate(page,fileCateGoryId);
        return result;
    }

    @GetMapping("get/search/{currentPage}")
    public Result getFileGroupSearch(@RequestParam("searchValue") String searchValue,
                                     @PathVariable("currentPage")Integer currentPage){
        QueryWrapper<TaskContent> wrapper = new QueryWrapper<>();
        wrapper.like("title", searchValue);
        Integer pageSize=6;
        Page<TaskContent> page = new Page<>(currentPage, pageSize);
        Page<TaskContent> taskContentPage = taskContentMapper.selectPage(page, wrapper);
        Result result = fileGroupAdminService.getFileGroupSearch(taskContentPage,searchValue);
        return result;
    }

    @GetMapping("change/status/{publishId}")
    public Result changeStatus(@PathVariable("publishId") Integer publishId,
                               @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        return fileGroupAdminService.changeStatus(publishId);
    }

    @PostMapping("edit")
    public Result edit(@RequestBody FileGroupVO editInfo,
                       @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        return fileGroupAdminService.edit(editInfo);
    }

    @PostMapping("edit/file/del/{groupId}")
    public Result editDelFile(@PathVariable("groupId") String groupId,
                              @RequestBody List<String> fileNames,
                               @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        return fileGroupAdminService.editDelFile(groupId,fileNames);
    }

//    @PostMapping("edit/file/add/{groupId}")
//    public Result editAddFile(@PathVariable("groupId") String groupId,
//                              @RequestHeader("token") String token){
//
//        String adminId = jwtHelper.getUserId(token);
//        if (!adminUtils.isSuperAdmin(adminId)){
//            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
//        }
//        return fileGroupAdminService.editAddFile(groupId);
//    }

    //获取一个文件组的文件信息
    @GetMapping("get/fileInfo/{groupId}")
    public Result getFileInfo(@PathVariable("groupId") String groupId){

        return fileGroupAdminService.getFileInfo(groupId);
    }

}
