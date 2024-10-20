package com.xiangyueEducation.uploaderCloud.Controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiangyueEducation.uploaderCloud.POJO.TaskContent;
import com.xiangyueEducation.uploaderCloud.POJO.User;
import com.xiangyueEducation.uploaderCloud.Service.funPackage.FileGroupService;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.ResultCodeEnum;
import com.xiangyueEducation.uploaderCloud.admin.utils.AdminUtils;
import com.xiangyueEducation.uploaderCloud.mapper.TaskContentMapper;
import com.xiangyueEducation.uploaderCloud.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.Resource;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("fileGroup")
public class FileGroupController {


    @Autowired
    private FileGroupService fileGroupService;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserMapper userMapper;


    @GetMapping("mine")
    public Result getMine(@RequestHeader("token") String token,
                          @RequestParam Integer currentPage,
                          @RequestParam(value = "pageSize",required = false) Integer pageSize){
        String uuid = jwtHelper.getUserId(token);
        if (pageSize == null){
            pageSize = 8;
        }
        return fileGroupService.getMine(uuid,currentPage,pageSize);
    }


    @GetMapping("one/{publishId}")
    @Operation(summary = "获取单个文件组",description = "获取单个文件组",
    responses = @ApiResponse(content = @Content( mediaType = "application/json",examples =
            @ExampleObject(value = "{\"code\":200,\"message\":\"success\",\"data\":{\"fileInfoList\":[{\"fileLevel\":1,\"fileName\":\"7-架构图.png\",\"fileGroupId\":259,\"fileSize\":65531,\"fileUrl\":\"http://172.22.127.134:1314/workSpace/234e78b7-1855-442e-94f4-b0936bcc4723/firstLevel/7-架构图.png\",\"fileType\":\"其他\"},{\"fileLevel\":1,\"fileName\":\"9-前端页面设计初稿.png\",\"fileGroupId\":261,\"fileSize\":61980,\"fileUrl\":\"http://172.22.127.134:1314/workSpace/234e78b7-1855-442e-94f4-b0936bcc4723/firstLevel/9-前端页面设计初稿.png\",\"fileType\":\"其他\"},{\"fileLevel\":1,\"fileName\":\"4-uploadercloud.sql\",\"fileGroupId\":263,\"fileSize\":69777,\"fileUrl\":\"http://172.22.127.134:1314/workSpace/234e78b7-1855-442e-94f4-b0936bcc4723/firstLevel/4-uploadercloud.sql\",\"fileType\":\"其他\"}],\"infoData\":{\"publisherUuid\":\"234e78b7-1855-442e-94f4-b0936bcc4723\",\"publisherName\":\"哈哈哈1\",\"description\":\"大大是的\",\"title\":\"2312额温枪大青蛙\"}}}"))))
    public Result getOne(@PathVariable("publishId") Integer publishId){
        return fileGroupService.getOne(publishId);
    }






    @GetMapping("get")
    @Operation(summary = "获取文件组",description = "默认获取8个文件组***(新改变,其实加入了fileGroupId,即文件ID)",
    responses = @ApiResponse(content = @Content( mediaType = "application/json",examples =
    @ExampleObject(value = "{\"code\":200,\"message\":\"success\",\"data\":{\"activity\":[{\"image\":\"http://172.22.127.134:1314/img/641922e5-b053-4f86-9c1d-8462c70e362f.png\",\"count\":1,\"description\":\"1G测试\",\"time\":\"2024-07-07\",\"category\":\"管理员发布\",\"title\":\"大文件测试\",\"publishId\":36,\"tags\":[\"管理员发布\"]},{\"image\":\"http://172.22.127.134:1314/img/0ca133c4-d903-4945-8687-9cf0654ced90.png\",\"count\":5,\"description\":\"321\",\"time\":\"2024-07-07\",\"category\":\"管理员发布\",\"title\":\"2131231\",\"publishId\":35,\"tags\":[\"管理员发布\"]},{\"image\":\"http://172.22.127.134:1314/img/e2bd8fca-adbe-4900-a68c-7a1ab8408e5c.png\",\"count\":3,\"description\":\"好大啊怕都\",\"time\":\"2024-07-07\",\"category\":\"管理员发布\",\"title\":\"关闭测试\",\"publishId\":34,\"tags\":[\"管理员发布\"]},{\"image\":\"http://172.22.127.134:1314/img/01e29928-02e0-49b5-a486-23dc32d4dd91.png\",\"count\":2,\"description\":\"大萨达撒大大发发斯蒂芬个阿发额\",\"time\":\"2024-07-07\",\"category\":\"管理员发布\",\"title\":\"_(:з」∠)_飒飒\",\"publishId\":33,\"tags\":[\"管理员发布\"]},{\"image\":\"http://172.22.127.134:1314/img/5158626f-39b0-43a8-aba0-0bb57dff59fd.png\",\"count\":1,\"description\":\"大萨达实打实\",\"time\":\"2024-07-07\",\"category\":\"管理员发布\",\"title\":\"测试预览\",\"publishId\":32,\"tags\":[\"管理员发布\"]},{\"image\":\"http://172.22.127.134:1314/img/e4b91576-9a31-4839-974a-75a2e765019b.png\",\"count\":1,\"description\":\"_(:з」∠)_撒打算\",\"time\":\"2024-07-06\",\"category\":\"管理员发布\",\"title\":\"最后一次测试\",\"publishId\":31,\"tags\":[\"管理员发布\"]},{\"image\":\"http://172.22.127.134:1314/img/4b325c8f-f68d-4b65-bdbf-9ba1be5b1c90.png\",\"count\":2,\"description\":\"94618+44448465\",\"time\":\"2024-07-06\",\"category\":\"管理员发布\",\"title\":\"文件完整\",\"publishId\":30,\"tags\":[\"管理员发布\"]},{\"image\":\"http://172.22.127.134:1314/img/9909fab5-ad53-4b3e-abba-fcc4494521cf.png\",\"count\":1,\"description\":\"dasiyfuasiuocbaivl\",\"time\":\"2024-07-06\",\"category\":\"管理员发布\",\"title\":\"dsajdyuasgidkjba\",\"publishId\":29,\"tags\":[\"管理员发布\"]}],\"maxPage\":5}}"))))
    public Result get(
                @RequestHeader("token") String token,
                @RequestParam("order") String order,
                @RequestParam("currentPage") Integer currentPage,
                @RequestParam("departmentId") Integer departmentId,
                @RequestParam(value = "fileCategory",required = false) String fileCategory,
                @RequestParam(value = "fileGroupCategory",required = false) String fileGroupCategory,
                @RequestParam(value = "mainAndViceTypeId",required = false) Integer mainAndViceTypeId,
                @RequestParam(value = "searchValue",required = false) String searchValue) {
        //测试为本公司员工
        String uuid = jwtHelper.getUserId(token);
        User user = userMapper.selectById(uuid);
        if (user==null){
            log.error("不明身份的人请求获取一次文件组,所用token为: "+token);
            return null;
        }

        //搜索模式
        if (searchValue != null)  {
            System.out.println("执行了搜所功能");
            return fileGroupService.getSearchValueTimeSequence(searchValue, departmentId,order, currentPage);
        }
        if (fileGroupCategory == null&&fileCategory == null) {
            if (mainAndViceTypeId!=null){
                return fileGroupService.getTimeSequence(departmentId,order, currentPage,mainAndViceTypeId);
            }
            return fileGroupService.getTimeSequence(departmentId,order, currentPage);
        } else if (fileGroupCategory != null) {
            if (mainAndViceTypeId!=null){
                return fileGroupService.getFileGroupCategory(fileGroupCategory, departmentId,order, currentPage,mainAndViceTypeId);
            }
            return fileGroupService.getFileGroupCategory(fileGroupCategory, departmentId,order, currentPage);
        } else if (fileCategory != null)  {
            if (mainAndViceTypeId!=null){
                return fileGroupService.getFileCategory(fileCategory, departmentId,order, currentPage,mainAndViceTypeId);
            }
            return fileGroupService.getFileCategory(fileCategory, departmentId,order, currentPage);
        }

        return null;
    }

    @DeleteMapping("files")
    @Operation(summary = "删除文件组中文件",description = "通过文件的ID(对应于数据库中的task_file_group表中的file_group_id字段)进行物理删除文件操作")
    public Result deleteFiles(@RequestBody List<Integer> ids){
        Result result=fileGroupService.deleteFiles(ids);
        return result;
    }


    @GetMapping("get/one/{publishId}")
    public Result getOneByPublishId(
            @PathVariable("publishId") Integer publishId
    ){
        return fileGroupService.getOneByPublishId(publishId);
    }


    /**
     * @Description用于前端的上传文件组信息的接口..一共分为四个接口:
     * 1.  上传文件组的文件及其文件信息
     * 2. 写入到数据库
     * 3. 用于上传文件组信息
     * 4. 上传文件组的预览图片
     *
     *
     * @param data
     * @return
     */
    //1.  上传文件组的文件及其文件信息
    //1.1 分块上传
    @PostMapping("upload/workSpace")
    ResponseEntity<Map<String, Object>> uploadChunk(@RequestHeader String token,
                                                    @RequestParam int index,
                                                    @RequestParam long chunkSize,
                                                    @RequestParam int totalChunks,
                                                    @RequestParam long totalSize,
                                                    @RequestParam("filename") String fileName,
                                                    @RequestBody byte[] file){

        ResponseEntity<Map<String, Object>> res=fileGroupService.uploadChunk(token, index,chunkSize,totalChunks,totalSize,fileName,file);
        return res;
    }
    //1.2 合并分块
    @GetMapping("merge/workSpace")
    public ResponseEntity<Map<String, Object>> merge(@RequestParam("token") String token,
                                                     @RequestParam("filename") String fileName,
                                                     @RequestParam("fileLevel") Integer fileLevel){

        ResponseEntity<Map<String, Object>> res=fileGroupService.merge(token,fileName,fileLevel);
        return res;
    }

    // 2. 把上传的 文件 信息写入到数据库
    @PostMapping("write/db/workSpace")
    public Result writeToDB(@RequestBody Map data){
        return fileGroupService.writeToDB(data);
    }

    //3. 用于上传文件组信息
    @PostMapping("upload/info")
    public Result uploadFileGroupInfo(@RequestBody Map data){
        System.out.println("data = " + data);

        return fileGroupService.uploadFileGroupInfo(data);

    }

    //3. 用于上传 文件组 信息(后台管理版本)
    @PostMapping("upload/info/admin")
    public Result uploadFileGroupInfoAdmin(@RequestBody Map data){
        System.out.println("data = " + data);

        return fileGroupService.uploadFileGroupInfoAdmin(data);

    }

    //4. 上传文件组的预览图片
    @PostMapping("upload/previewImg")
    public Result uploadPreviewImg(@RequestParam("groupId") String groupId, @RequestParam("previewImg") MultipartFile file){
        return fileGroupService.uploadPreviewImg(groupId,file);
    }


    /**
     * 根据publishId删除对应的文件组
     * @param publishId
     * @param token
     * @return
     */
    @GetMapping("del/{publishId}")
    public Result delete(@PathVariable("publishId") Integer publishId,
                         @RequestHeader("token") String token//这里的token防止有人恶意调用API执行删除操作,危险等级高
    ){
        String uuid = jwtHelper.getUserId(token);
        return fileGroupService.delete(publishId,uuid);
    }

    @Autowired
    private AdminUtils adminUtils;

    @GetMapping("del/admin/{publishId}")
    public Result deleteAdmin(@PathVariable("publishId") Integer publishId,
                            @RequestHeader("token") String token){
        System.out.println("token = " + token);
        String adminId = jwtHelper.getUserId(token);
        System.out.println("adminId = " + adminId);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够", ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        return fileGroupService.delete(publishId);
    }



    @GetMapping("del/logic/{publishId}")
    public Result deleteLogic(@PathVariable("publishId") Integer publishId,
                         @RequestHeader("token") String token//这里的token防止有人恶意调用API执行删除操作,危险等级高
    ){
        String uuid = jwtHelper.getUserId(token);
        return fileGroupService.deleteLogic(publishId,uuid);
    }

    /**
     * 根据publishId获取对应的文件组中某一个文件的信息
     * @param publishId
     * @param fileName
     * @return
     */
    @GetMapping("get/detail/space/{publishId}/{fileLevel}")
    public Result getOneFileDetail(
                                @RequestHeader("token") String token,
                                @PathVariable("publishId") String publishId,
                                @RequestParam("fileName") String fileName,
                                @PathVariable("fileLevel") Integer fileLevel) {
        String uuid = jwtHelper.getUserId(token);
        Result res = fileGroupService.getOneFileDetail(publishId, fileName,uuid,fileLevel);
        return res;
    }

    @GetMapping("groupId/{publishId}")
    public Result getGroupId(@PathVariable("publishId") Integer publishId){
        return fileGroupService.getGroupId(publishId);
    }

    @Autowired
    private TaskContentMapper taskContentMapper;

    @GetMapping("check/title")
    public Result chackTitle(@RequestParam("title") String title){
        QueryWrapper<TaskContent> taskContentWrapper = new QueryWrapper<>();
        taskContentWrapper.eq("title",title);
        List<TaskContent> taskContents = taskContentMapper.selectList(taskContentWrapper);
        for (TaskContent item:taskContents){
            if (item.getTitle().equals(title)){
                return Result.build("已存在过该 标题 了,请修改一下 标题 再继续上传",ResultCodeEnum.FILE_GROUP_TITLE_EXISTS_ERROR);
            }
        }
        return Result.ok("true");
    }

}
