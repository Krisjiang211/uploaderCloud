package com.xiangyueEducation.uploaderCloud.admin.Type;


import com.xiangyueEducation.uploaderCloud.POJO.MainAndViceType;
import com.xiangyueEducation.uploaderCloud.POJO.MainType;
import com.xiangyueEducation.uploaderCloud.POJO.ViceType;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.MainAndViceTypeService;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.MainTypeService;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.ResultCodeEnum;
import com.xiangyueEducation.uploaderCloud.admin.Type.service.MainAndViceTypeAdminService;
import com.xiangyueEducation.uploaderCloud.admin.Type.service.MainTypeAdminService;
import com.xiangyueEducation.uploaderCloud.admin.Type.service.ViceTypeAdminService;
import com.xiangyueEducation.uploaderCloud.admin.VO.MainAndViceTypeVO;
import com.xiangyueEducation.uploaderCloud.admin.utils.AdminUtils;
import com.xiangyueEducation.uploaderCloud.mapper.MainAndViceTypeMapper;
import com.xiangyueEducation.uploaderCloud.mapper.MainTypeMapper;
import com.xiangyueEducation.uploaderCloud.mapper.ViceTypeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("admin/type")
public class MainAndViceTypeAdminController {
    /**
     * 不支持修改
     */

    @Autowired
    private MainTypeMapper mainTypeMapper;
    @Autowired
    private ViceTypeMapper viceTypeMapper;
    @Autowired
    private MainAndViceTypeMapper mainAndViceTypeMapper;
    @Autowired
    private MainAndViceTypeAdminService mainAndViceTypeAdminService;
    @Autowired
    private MainTypeAdminService mainTypeAdminService;
    @Autowired
    private ViceTypeAdminService viceTypeAdminService;
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private AdminUtils adminUtils;

    //1. 查
    //1.1 获得mainType
    @GetMapping("mainType")
    public Result getMainType(){
        MainType[] mainTypes = mainTypeMapper.selectAll();
        return Result.ok(mainTypes);
    }
    //1.2 获得viceType
    @GetMapping("viceType")
    public Result getViceType(){
        ViceType[] viceTypes = viceTypeMapper.getAll();
        return Result.ok(viceTypes);
    }
    //1.3 获得main和ViceType的连接关系
    @GetMapping("mainAndViceType")
    @Operation(summary = "获取MVType的连接关系(detail)",description = "获取MVType的连接关系, 以嵌套的方式,也会详细的标出id等属性",
    responses = @ApiResponse(description = "返回是res.data里面是数组.里面多层嵌套了mainType和viceType",
    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,examples = @ExampleObject(value = "{\"code\":200,\"message\":\"success\",\"data\":[{\"mainType\":{\"mainTypeId\":1,\"name\":\"医学升本\",\"time\":\"2024-05-27T06:41:43.000+00:00\",\"isDelete\":0},\"viceType\":[{\"viceTypeId\":1,\"name\":\"生理病理+公共英语\",\"time\":\"2024-05-27T06:43:08.000+00:00\",\"isDelete\":1},{\"viceTypeId\":2,\"name\":\"中医基础＋公共英语\",\"time\":\"2024-05-27T06:43:23.000+00:00\",\"isDelete\":0},{\"viceTypeId\":3,\"name\":\"高等数学＋公共英语\",\"time\":\"2024-05-27T06:43:34.000+00:00\",\"isDelete\":0}]},{\"mainType\":{\"mainTypeId\":2,\"name\":\"护理\",\"time\":\"2024-05-27T06:41:52.000+00:00\",\"isDelete\":0},\"viceType\":[{\"viceTypeId\":4,\"name\":\"护理学\",\"time\":\"2024-05-27T06:43:51.000+00:00\",\"isDelete\":0}]},{\"mainType\":{\"mainTypeId\":3,\"name\":\"医师\",\"time\":\"2024-05-27T06:42:03.000+00:00\",\"isDelete\":0},\"viceType\":[{\"viceTypeId\":5,\"name\":\"临床医学\",\"time\":\"2024-05-27T06:44:00.000+00:00\",\"isDelete\":0}]},{\"mainType\":{\"mainTypeId\":4,\"name\":\"初职\",\"time\":\"2024-05-27T06:42:10.000+00:00\",\"isDelete\":0},\"viceType\":[{\"viceTypeId\":6,\"name\":\"初职教育\",\"time\":\"2024-05-27T06:44:16.000+00:00\",\"isDelete\":0}]},{\"mainType\":{\"mainTypeId\":5,\"name\":\"期末考试\",\"time\":\"2024-05-27T06:42:16.000+00:00\",\"isDelete\":0},\"viceType\":[]}]}"))))
    public Result getMainAndViceType(){
//        List<MainAndViceTypeVO> mainAndViceTypeVOS = mainAndViceTypeMapper.getAllVO();
//        return Result.ok(mainAndViceTypeVOS);
        Result result= mainAndViceTypeAdminService.getAllTable();
        return result;
    }
    //1.4 获取MainAndViceType表
    @GetMapping("mainAndViceTypeTable")
    public Result getMainAndViceTypeTable(){

        Result result = mainAndViceTypeAdminService.getAll();
        return result;
    }



    //2. 增加
    //2.1 增加一个mainType
    @PostMapping("mainType/add")
    public Result addMainType(@RequestBody MainType mainType,
                              @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        for (MainType item: mainTypeMapper.selectAll()){
            if (item.getName().equals(mainType.getName())){
                return Result.build("数据库已经有这个主类了",ResultCodeEnum.QUERY_HAVE_SAME);
            }
        }
        mainType.setIsDelete(0);
        mainTypeMapper.insert(mainType);
        return Result.ok("单独插入一个MainType成功");
    }
    //2.2 增加一个viceType
    @PostMapping("viceType/add")
    public Result addViceType(@RequestBody ViceType viceType,
                              @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        for (ViceType item: viceTypeMapper.getAll()){
            if (item.getName().equals(viceType.getName())){
                return Result.build("数据库已经有这个副类了",ResultCodeEnum.QUERY_HAVE_SAME);
            }
        }
        viceType.setIsDelete(0);
        viceTypeMapper.insert(viceType);
        return Result.ok("单独插入一个ViceType成功");
    }

    //2.3 增加一个main和ViceType的连接关系
    //后端接受mainType和viceType两个String
    @GetMapping("mainAndViceType/add")
    public Result addMainAndViceType(@RequestParam("mainTypeName") String mainTypeName,
                                     @RequestParam("viceTypeName") String viceTypeName,
                                     @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        Result result= mainAndViceTypeAdminService.addOnePair(mainTypeName,viceTypeName);
        return result;
    }


    @PostMapping("MVType/edit")
    public Result editMVType(@RequestBody Map mainAndViceType,
                             @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        Result result= mainAndViceTypeAdminService.editOnePair(mainAndViceType);
        return result;
    }


    //3. (逻辑)删除        !!!!!!!!!   废弃!!!!!!!
    //-***** 下列的递归的指的就是,若逻辑删除一个,与删除对象有关联得也要一并删除  ********-
    //3.1 (递归的)删除一个MainType
    @GetMapping("mainType/del")
    public Result delMainType(@RequestParam("mainTypeName") String mainTypeName,
                              @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        Result result= mainTypeAdminService.delOne(mainTypeName);
        return result;
    }
    //3.2 (递归的)删除一个ViceType
    @GetMapping("viceType/del")
    public Result delViceType(@RequestParam("viceTypeName") String viceTypeName,
                              @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        Result result= viceTypeAdminService.delOne(viceTypeName);

        return result;
    }

    //3.3 删除一个main和ViceType的连接关系
    @GetMapping("mainAndViceType/del")
    public Result delMainAndViceType(@RequestParam("mainTypeName") String mainTypeName,
                                     @RequestParam("viceTypeName") String viceTypeName,
                                     @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }

        Result result= mainAndViceTypeAdminService.delOnePair(mainTypeName,viceTypeName);
        return result;
    }

    //修改状态
    @GetMapping("mainType/status/{mainTypeId}")
    public Result changeMainTypeStatus(@PathVariable("mainTypeId") Integer mainTypeId,
                                       @RequestHeader("token") String token) {
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        return mainTypeAdminService.changeMainTypeStatus(mainTypeId);
    }


    @GetMapping("viceType/status/{viceTypeId}")
    public Result changeViceTypeStatus(@PathVariable("viceTypeId") Integer viceTypeId,
                                       @RequestHeader("token") String token) {
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }

        return viceTypeAdminService.changeViceTypeStatus(viceTypeId);
    }

    @GetMapping("mainAndViceType/status/{mainTypeId}/{viceTypeId}")
    public Result changeMainAndViceTypeStatus(@PathVariable("mainTypeId") Integer mainTypeId,
                                              @PathVariable("viceTypeId") Integer viceTypeId,
                                              @RequestHeader("token") String token) {
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }

        return mainAndViceTypeAdminService.changeMainAndViceTypeStatus(mainTypeId,viceTypeId);
    }

    @DeleteMapping("viceType/{viceTypeId}")
    public Result delViceType(@PathVariable("viceTypeId") Integer viceTypeId,
                            @RequestHeader("token") String token){
        String adminId = jwtHelper.getUserId(token);
        if (!adminUtils.isSuperAdmin(adminId)){
            return Result.build("权限不够",ResultCodeEnum.ADMIN_LIMITED_AUTHORITY);
        }
        return viceTypeAdminService.delViceType(viceTypeId);
    }



}