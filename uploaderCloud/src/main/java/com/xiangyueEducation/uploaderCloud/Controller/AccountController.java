package com.xiangyueEducation.uploaderCloud.Controller;

import com.xiangyueEducation.uploaderCloud.POJO.User;
import com.xiangyueEducation.uploaderCloud.Service.funPackage.AccountService;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.UserService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RestController
@Slf4j
@CrossOrigin
@RequestMapping("account")
public class AccountController {


    @Autowired
    private AccountService accountService;


    // 登录
    @PostMapping("login")
    public Result login(@RequestBody User user){
        Result result=accountService.login(user);
        return result;
    }


    /**
     * 检查用户名是否被使用
     * @param ID
     */
    @GetMapping ("checkIDUsed")
    public Result checkIDUsed(@RequestParam("identityCardCode") String ID){
        Result<User> IDStatus = accountService.checkIDUsed(ID);
        return IDStatus;
    }


    /**
     * 注册功能
     * @param registerInfo
     */
    @PostMapping("register")
    public Result register(@RequestBody User registerInfo){
        Result<User> registerStatus=accountService.register(registerInfo);
        return registerStatus;
    }


    /**
     * 检查登录状态
     * @param token
     */
    @GetMapping("checkLogin")
    public Result checkLogin(@RequestHeader String token){
        Result loginStatus=accountService.checkLogin(token);
        return loginStatus;
    }



    @Operation(summary = "修改密码接口"
    , description = "传入原密码和新密码两个,并使用HashMap接收(需要token来确定修改用户的对象)",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "原密码和新密码的HashMap",
    content = @Content(mediaType = "application/json",
    examples = @ExampleObject(value = "{\"originalPwd\":\"123456\",\"newPwd\":\"123456789\"}"))))
    @PostMapping("changePwd")
    public Result changePwd(@RequestBody HashMap<String,String> pwdInfo,
                            @Parameter(description = "token在请求头中获取") @RequestHeader String token){
        Result result=accountService.changePwd(pwdInfo,token);
        return result;
    }

}
