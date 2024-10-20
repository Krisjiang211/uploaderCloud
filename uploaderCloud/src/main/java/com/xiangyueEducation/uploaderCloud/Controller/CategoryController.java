package com.xiangyueEducation.uploaderCloud.Controller;


import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.FileCategoryService;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.FileGroupCategoryService;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.MainAndViceTypeService;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@CrossOrigin
@Slf4j
public class CategoryController {

    @Autowired
    private FileCategoryService fileCategoryService;


    @Autowired
    private FileGroupCategoryService fileGroupCategoryService;

    @Autowired
    private MainAndViceTypeService mainAndViceTypeService;

    @Autowired
    private JwtHelper jwtHelper;

    @Operation(summary = "获取文件种类",
    description = "获取文件分类,会以list的方式返回",
    tags = {"获取信息接口"},
    responses = @ApiResponse(description = "返回的有用信息是list",
    content = @Content( mediaType = MediaType.APPLICATION_JSON_VALUE,examples = @ExampleObject(value = "{\"code\":200,\"message\":\"success\",\"data\":[\"视频\",\"音频\",\"文档\",\"图片\",\"工程项目文件\",\"其他\",\"管理员发布\"]}"))))
    @GetMapping ("file")
    public Result getFileCategory(){
        Result res = fileCategoryService.getFileCategory();
        log.info("获取了一次文件分类");
        return res;
    }


    /**
     * @Description 获取整张表内容
     */
    @GetMapping ("file/detail")
    public Result getDetailFileTable(){
        Result res = fileCategoryService.getDetailTable();
        log.info("获取了fileCategory表的全部信息");
        return res;
    }

    @GetMapping ("fileGroup")
    @Operation(summary = "获取文件组种类(数组)",description = "获取文件组种类, 返回的res.data中的数据是数组",
            tags = {"获取信息接口"},
    responses = @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
    examples = @ExampleObject(value = "{\"code\":200,\"message\":\"success\",\"data\":[\"教学\",\"市场\",\"政策\",\"重要任务\",\"其他\",\"管理员发布\"]}"))))
    public Result getFileGroupCategory(){
        Result res = fileGroupCategoryService.getFileGroupCategory();
        log.info("获取了一次文件组分类");
        return res;
    }


    /**
     * @Description 获取整张表内容
     */
    @GetMapping ("fileGroup/detail")
    public Result getDetailFileGroupTable() {
        Result res = fileGroupCategoryService.getDetailTable();
        log.info("获取了fileGroupCategory表的全部信息");
        return res;
    }


    @Operation(summary = "添加一个文件种类",
                description = "添加一个文件种类,代码内部不提供重复添加验证,请在前端添加\n请求示例:   http://localhost:1314/category/file/add?name=xxx")
    @GetMapping("file/add")
    public Result addFileCategory(@RequestHeader("token") String token,
                                  @RequestParam("name") String name){
        String uuid = jwtHelper.getUserId(token);
        Result res = fileCategoryService.addFileCategory(name);
        log.info(uuid+"添加了一次文件种类,名为: "+name);
        return res;
    }

    @Operation(summary = "删除一个文件种类",
            description = "删除一个文件种类,代码内部不提供 是否存在删除对象的验证,请在前端添加\n请求示例:   http://localhost:1314/category/file/del?name=xxx")
    @GetMapping("file/del")
    public Result delFileCategory(@RequestHeader("token") String token,
                                  @RequestParam("name") String name){
        String uuid = jwtHelper.getUserId(token);
        Result res = fileCategoryService.delFileCategory(name);
        log.info(uuid+"删除了文件种类,名为: "+name);
        return res;
    }

    @GetMapping("fileGroup/add")
    public Result addFileGroupCategory(@RequestHeader("token") String token,
                                       @RequestParam("name") String name) {

        String uuid = jwtHelper.getUserId(token);
        Result res = fileGroupCategoryService.addFileGroupCategory(name);
        log.info(uuid + "添加了一次文件组种类,名为: "+name);
        return res;
    }

    @GetMapping("fileGroup/del")
    public Result delFileGroupCategory(@RequestHeader("token") String token,
                                       @RequestParam("name") String name) {

        String uuid = jwtHelper.getUserId(token);
        Result res = fileGroupCategoryService.delFileGroupCategory(name);
        log.info(uuid + "删除了文件组种类,名为: "+name);
        return res;
    }


}
