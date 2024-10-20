package com.xiangyueEducation.uploaderCloud.Controller;


import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("utils")
public class UtilsController {

    @Autowired
    private JwtHelper jwtHelper;


    @GetMapping("uuid")
    public Result getUUID(){
        return Result.ok(UUID.getUUID());
    }

    @GetMapping("tokenToId")
    public Result tokenToId(@RequestHeader("token") String token){
        return Result.ok(jwtHelper.getUserId(token));
    }
}
