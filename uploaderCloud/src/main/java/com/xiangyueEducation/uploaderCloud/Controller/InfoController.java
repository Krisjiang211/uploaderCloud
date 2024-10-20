package com.xiangyueEducation.uploaderCloud.Controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.POJO.*;
import com.xiangyueEducation.uploaderCloud.Service.funPackage.AccountService;
import com.xiangyueEducation.uploaderCloud.Service.funPackage.InfoService;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.*;
import com.xiangyueEducation.uploaderCloud.Utils.BaseUtils.BaseUtilsJiang;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.UUID;
import com.xiangyueEducation.uploaderCloud.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("info")
public class InfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private SelfCloudFileSpaceService selfCloudFileSpaceService;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private DepartmentAndUsersService departmentAndUsersService;

    @Autowired
    private MainTypeService mainTypeService;

    @Autowired
    private ViceTypeMapper viceTypeMapper;

    @Autowired
    private MainAndViceTypeService mainAndViceTypeService;

    @Autowired
    private PublishMapper publishMapper;

    @Autowired
    private InfoService infoService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private ProxyUserService proxyUserService;


    @Operation(summary = "获取整个UserInfo对象"
            ,description = "根据token获取整个UserInfo对象"
            ,tags = {"获取信息接口"}
            ,responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = "{\n" +
                            "    \"code\": 200,\n" +
                            "    \"message\": \"success\",\n" +
                            "    \"data\": {\n" +
                            "        \"uuid\": \"234e78b7-1855-442e-94f4-b0936bcc4723\",\n" +
                            "        \"realName\": \"哈哈哈\",\n" +
                            "        \"phoneNumber\": \"19511988405\",\n" +
                            "        \"address\": \"广西壮族自治区 南宁市 马山县\",\n" +
                            "        \"email\": \"123\",\n" +
                            "        \"gender\": \"男\",\n" +
                            "        \"avatarPath\": \"classpath:fileSystem/avatar/11b14dd9-c0c7-4fa8-bd00-c563b7013a86.png\",\n" +
                            "        \"previewAvatarPath\": \"classpath:fileSystem/avatar/705210ca-eed1-4912-9658-923bfd70319e.png\"\n" +
                            "    }\n" +
                            "}"))
            )
    })
    @GetMapping("get/user/base")
    Result getUserInfo(@RequestHeader String token){
        Result<Object> result =userInfoService.getUserInfo(token);
        return result;
    }


    @Operation(summary = "获取整个User对象",
    description = "根据token获取整个User对象(并且会自动过滤掉密码字段)", tags = {"获取信息接口"},
    responses = {
            @ApiResponse(responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = "{\n" +
                            "    \"code\": 200,\n" +
                            "    \"message\": \"success\",\n" +
                            "    \"data\": {\n" +
                            "        \"uuid\": \"234e78b7-1855-442e-94f4-b0936bcc4723\",\n" +
                            "        \"roleId\": 2,\n" +
                            "        \"departmentId\": 1,\n" +
                            "        \"identityCardCode\": \"1211\",\n" +
                            "        \"pwd\": null,\n" +
                            "        \"isDelete\": 0\n" +
                            "    }\n" +
                            "}")
            ))
    })
    @GetMapping("get/user")
    Result getUser(@Parameter(description = "用户认证的 token", required = true, in = ParameterIn.HEADER)
                   @RequestHeader String token,
                   @RequestParam(value = "wipeOutPwd",required = false) Boolean wipeOutPwd){
        //默认抹除密码(即使是MD5值)
        if (wipeOutPwd==null){
            return userInfoService.getUser(token,true);
        }
        return userInfoService.getUser(token,false);
    }

    @GetMapping("get/user/{uuid}")
    Result getUserByUuid(@PathVariable String uuid){

        return Result.ok(userInfoMapper.selectById(uuid));
    }

    @GetMapping("user/main/{uuid}")
    Result getUserTable(@PathVariable String uuid){
        return Result.ok(userMapper.selectById(uuid));
    }


    @Operation(summary = "更新用户的基本信息",
    description = "更新的java对象是UserInfo,其他字段根据需要更新")
    @PostMapping("update/user/base")
    Result updateUserInfo(@Parameter(description = "用户认证的 token(在请求头中查看)", required = true, in = ParameterIn.HEADER)
                          @RequestHeader String token,
                          @RequestBody UserInfo userInfo){
        userInfo.setUuid(jwtHelper.getUserId(token));
        Result<Object> result =userInfoService.updateUserInfo(userInfo);
        return result;
    }

    /**
     * 更新用户头像
     * @param token
     * @param avatar
     * @return
     */
//    @PostMapping("update/user/avatar")
//    public Result updateUserAvatar(@RequestHeader String token, @RequestParam("avatar") MultipartFile avatar,@RequestParam("previewAvatar") MultipartFile previewAvatar){
//        Result  result = userInfoService.updateUserAvatar(token,avatar,previewAvatar);
//        return result;
//    }
    @PostMapping("update/user/avatar")
    public Result updateUserAvatar(@RequestHeader("token") String token,
                                   @RequestParam(value = "avatar", required = false) MultipartFile avatar,
                                   @RequestParam(value = "previewAvatar", required = false) MultipartFile previewAvatar){
        String uuid = jwtHelper.getUserId(token);
        log.warn("用户"+uuid+"执行了一次");
        Result  result = userInfoService.updateUserAvatar(token,avatar,previewAvatar);
        return result;
    }



    @Operation(summary = "获取用户头像URL(原图)",
    description = "根据token获取用户头像URL",
    tags = {"获取信息接口"},
    responses = {
            @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(value = "{\n" +
                    "    \"code\": 200,\n" +
                    "    \"message\": \"success\",\n" +
                    "    \"data\": \"http://172.22.127.134:1314/avatar/defaultAvatar.png\"\n" +
                    "}")))
    })
    @GetMapping("get/user/avatar")
    public Result getUserAvatar(@Parameter(description = "用户认证的 token(在请求头中查看)", required = true, in = ParameterIn.HEADER)
                                    @RequestHeader String token){
        Result  result = userInfoService.getUserAvatar(token);
        return result;
    }

    /**
     * 获取用户头像(预览图)
     * @param token
     * @return
     */
    @GetMapping("get/user/avatar/preview")
    public Result getUserAvatarPreview(@RequestHeader String token){
        Result  result = userInfoService.getUserAvatarPreview(token);
        return result;
    }


    /**
     * 获取个人云空间的文件名(分页获取)
     * @param token
     * @param currentPage
     * @param order
     * @return
     */
    @Operation(summary = "获取个人云空间的文件名(分页获取)",description = "根据token可以获取到这个对应用户的云空间的文件名字",
            tags = {"个人云空间管理"},
            responses = {
                    @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = "{\"code\":200,\"message\":\"success\",\"data\":[\"951.jpg\",\"969.jpg\",\"971.jpg\",\"972.jpg\",\"973.jpg\",\"uploaderCloud_ER.png\",\"后台问题011111.docx\",\"植物大战僵尸杂交版v2.0安装程序.exe\"]}")))
            }
    )
    @GetMapping("get/file/name/space")
    public Result getFileNameSpace(@RequestHeader String token,
                                   @RequestParam("currentPage") Integer currentPage,
                                   @RequestParam(value = "order" , required = false) String order,
                                   @RequestParam(value = "pageSize",required = false) Integer pageSize){

        Result res= selfCloudFileSpaceService.getFileNameSpace(token,currentPage,order,pageSize);
        return res;
    }

    /**
     * 根据传过来的pageSize数出总共可以有多少页
     * (pageSize不写则是默认一页8个)
     * @param token
     * @param pageSize
     * @return
     */
    @GetMapping("get/pageNum/space")
    public Result getPageNumSpace(@RequestHeader String token,
                                  @RequestParam(value = "pageSize",required = false) Integer pageSize){

        Result res= selfCloudFileSpaceService.getPageNumSpace(token,pageSize);
        return res;
    }

    /**
     * 根据uuid和fileName获取文件详情(即上传时间,文件大小,文件类型)
     * @param token
     * @param fileName
     * @return
     */
    @GetMapping("get/file/detail/space")
    public Result getFileDetail(@RequestHeader String token,
                                    @RequestParam("fileName") String fileName) {
        Result res = selfCloudFileSpaceService.getFileDetail(token, fileName);
        return res;
    }

    /**
     * 获取个人云空间所占空间大小
     * @param token
     * @return
     */
    @GetMapping("get/self/space/size")
    public Result getSelfSpaceSize(@RequestHeader String token){

        return selfCloudFileSpaceService.getSelfSpaceSize(token);

    }

    /**
     * 获取所有文件名(不分页)
     * @param token
     * @return
     */
    @GetMapping("get/self/space/file/name/all")
    public Result getAllFIleName(@RequestHeader String token){
        Result result = selfCloudFileSpaceService.getAllFIleName(token);
        return result;

    }
    @GetMapping("get/self/space/file/name/all/admin")
    public Result getAllFIleNameAdmin(@RequestParam String uuid){
        Result result = selfCloudFileSpaceService.getAllFIleNameAdmin(uuid);
        return result;

    }

    /**
     * 根据uuid获取部门id以及部门名字(某个)
     */
    @GetMapping("get/department")
    public Result getDepartmentIdAndName(@RequestHeader String token){
        return departmentAndUsersService.getDepartmentByUuid(token);
    }

    @GetMapping("get/department/all")
    public Result getDepartmentIdAndNameAll(){
        return departmentAndUsersService.getDepartmentIdAndNameAll();
    }



    @GetMapping("get/type/main")
    public Result getMainType(@RequestHeader String token){
        return mainTypeService.getMainType(token);
    }



    @GetMapping("get/type/vice")
    public Result getViceType(@RequestHeader String token){
        return mainAndViceTypeService.getViceType(token);

    }

    @PostMapping ("get/type/mainVice/id")
    public Result getMainAndViceTypeId(@RequestBody Map map){

        return mainAndViceTypeService.getMainAndViceTypeId(map);
    }


    @GetMapping("get/publish/{publishId}")
    public Result getPublishByItsId(@PathVariable("publishId") Integer publishId){
        return Result.ok(publishMapper.selectById(publishId));
    }

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProxyUserMapper proxyUserMapper;

    @Autowired
    private UserMapper userMapper;

    @Operation(summary = "添加一个部门",description = "会返回一个字符串")
    @GetMapping("department/{departmentName}")
    public Result addADepartment(@PathVariable("departmentName") String departmentName){
        Page<Department> page = new Page<>(1,100000);
        List<Department> departments = departmentMapper.selectAll(page);
        for (Department department : departments){
            if (department.getDepartmentName().equals(departmentName)){
                return Result.ok("部门已存在");
            }
        }
        Department department = new Department();
        department.setDepartmentName(departmentName);
        //插入部门
        departmentMapper.insert(department);
        Department departmentFinal = departmentMapper.selectOne(new QueryWrapper<Department>().eq("department_name", departmentName));
        //注册一个proxyUser
        User user = new User();
        user.setRoleId(2);
        user.setDepartmentId(departmentFinal.getDepartmentId());
        String identity= "proxyUser"+departmentFinal.getDepartmentId();
        user.setIdentityCardCode(identity);
        String pwd=UUID.getUUID();
        user.setPwd(pwd);
        Result registerRes = accountService.register(user);
        if (registerRes.getCode()!=200){
            return registerRes;
        }
        User userFinal = userMapper.selectOne(new QueryWrapper<User>().eq("identity_card_code", identity));

        //proxyUser同步到数据库
        ProxyUser proxyUser = new ProxyUser();
        proxyUser.setUuid(userFinal.getUuid());
        proxyUser.setRealPwd(pwd);
        proxyUserMapper.insert(proxyUser);
        return Result.ok("插入"+departmentName+"成功");
    }

    @Autowired
    private BaseUtilsJiang baseUtilsJiang;
    @GetMapping("get/department/all/kv")
    public Result getDepartmentAllKV(){
        List<Department> departments = departmentMapper.selectAll(new Page<>(1, 100000));
        List<Map> data=new ArrayList<>();
        for (Department department : departments){
            Map item = baseUtilsJiang.objectToMap(department);
            item.remove("serialVersionUID");
            data.add(item);
        }
        return Result.ok(data);
    }

    @GetMapping("MVType/oneToOne")
    @Operation(summary = "获取主副类型一对一关系",description = "会返回两个数组.第一个数组是名字,第二个数组是id",
    tags = {"主副类管理"},
    responses = @ApiResponse(description = "返回主副类关系列表",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
    examples = @ExampleObject(value = "{\"code\":200,\"message\":\"success\",\"data\":{\"MVType\":[[\"医学升本\",\"生理病理+公共英语\"],[\"医学升本\",\"中医基础＋公共英语\"],[\"医学升本\",\"高等数学＋公共英语\"],[\"护理\",\"护理学\"],[\"医师\",\"临床医学\"],[\"初职\",\"初职教育\"]],\"IdList\":[1,2,3,4,5,6]}}"))))
    public Result getOneToOne(){
        return mainAndViceTypeService.getOneToOne();
    }



    @GetMapping("proxyUser")
    @Operation(summary = "获取代理用户列表",description = "会返回一个数组,里面item是proxyUser对象")
    public Result getAllProxyUser(){
        Result res=proxyUserService.getAllProxyUser();
        return res;
    }

    @GetMapping("token/{uuid}")
    @Operation(summary = "获取token",description = "会返回token")
    public Result getToken(@PathVariable String uuid){
        return Result.ok(jwtHelper.createToken(uuid));
    }

}
