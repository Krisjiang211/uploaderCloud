package com.xiangyueEducation.uploaderCloud.Controller;


import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.AuthoriseService;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@CrossOrigin
public class AuthoriseController {

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private AuthoriseService authoriseService;

    /**
     * 申请人 发出一个请求给 领导
     * @param token
     * @param publishId
     * @param fileName
     * @return
     */
    @GetMapping("create/{publishId}")
    public Result createAuth(@RequestHeader("token")String token,
                             @PathVariable("publishId") Integer publishId,
                             @RequestParam("fileName")String fileName){

        String uuid = jwtHelper.getUserId(token);
        Result result=authoriseService.createAuth(uuid,publishId,fileName);

        return result;
    }


    /**
     * 老板获取所有的请求授权的对象,分页查询
     *  TODO--- 只看未同意
     *  --------使用orderCode防止注入攻击
     * @param token
     * @return 返回表中所有内容
     */
    @Operation(summary = "获得所有通知信息的详细List列表(复杂数据)",description = "那个orderCode是用来防止注入攻击来的",
    tags = {"通知管理"},
            responses = @ApiResponse(description = "返回信息的list",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(value = "{\"code\":200,\"message\":\"success\",\"data\":{\"data\":[{\"authorise\":{\"authoriseId\":16,\"publishId\":22,\"requesterUuid\":\"7c01aaeb-b698-429a-b65b-c49cc809b14b\",\"isAuthorise\":1,\"filePath\":\"classpath:fileSystem/workSpace/234e78b7-1855-442e-94f4-b0936bcc4723/secondLevel/tmp.txt\",\"fileName\":\"tmp.txt\",\"secretKey\":\"f3ab66c9-07e5-40a3-96cc-d74e167a7790\",\"time\":\"2024-06-13T14:37:17.000+00:00\"},\"publish\":{\"code\":200,\"message\":\"success\",\"data\":{\"third\":[\"实验1：vgg_net().txt\"],\"mainInfo\":{\"publishTime\":\"2024-06-13 22:27:47\",\"description\":\"大萨达撒贵都不可能\",\"publisher\":\"哈哈哈1\",\"title\":\"的赛U币大四班\"},\"first\":[\"新建 文本文档.txt\"],\"second\":[\"tmp.txt\",\"uploadercloud.sql\",\"阿里巴巴Java开发手册1.4.0.pdf\"]}},\"requesterRealName\":\"Kris\"},{\"authorise\":{\"authoriseId\":15,\"publishId\":22,\"requesterUuid\":\"7c01aaeb-b698-429a-b65b-c49cc809b14b\",\"isAuthorise\":1,\"filePath\":\"classpath:fileSystem/workSpace/234e78b7-1855-442e-94f4-b0936bcc4723/secondLevel/uploadercloud.sql\",\"fileName\":\"uploadercloud.sql\",\"secretKey\":\"9963d8a6-b104-4d99-89af-30ba26362d5a\",\"time\":\"2024-06-13T14:35:13.000+00:00\"},\"publish\":{\"code\":200,\"message\":\"success\",\"data\":{\"third\":[\"实验1：vgg_net().txt\"],\"mainInfo\":{\"publishTime\":\"2024-06-13 22:27:47\",\"description\":\"大萨达撒贵都不可能\",\"publisher\":\"哈哈哈1\",\"title\":\"的赛U币大四班\"},\"first\":[\"新建 文本文档.txt\"],\"second\":[\"tmp.txt\",\"uploadercloud.sql\",\"阿里巴巴Java开发手册1.4.0.pdf\"]}},\"requesterRealName\":\"Kris\"},{\"authorise\":{\"authoriseId\":14,\"publishId\":22,\"requesterUuid\":\"7c01aaeb-b698-429a-b65b-c49cc809b14b\",\"isAuthorise\":0,\"filePath\":\"classpath:fileSystem/workSpace/234e78b7-1855-442e-94f4-b0936bcc4723/secondLevel/uploadercloud.sql\",\"fileName\":\"uploadercloud.sql\",\"secretKey\":\"notAllowed\",\"time\":\"2024-06-13T14:35:10.000+00:00\"},\"publish\":{\"code\":200,\"message\":\"success\",\"data\":{\"third\":[\"实验1：vgg_net().txt\"],\"mainInfo\":{\"publishTime\":\"2024-06-13 22:27:47\",\"description\":\"大萨达撒贵都不可能\",\"publisher\":\"哈哈哈1\",\"title\":\"的赛U币大四班\"},\"first\":[\"新建 文本文档.txt\"],\"second\":[\"tmp.txt\",\"uploadercloud.sql\",\"阿里巴巴Java开发手册1.4.0.pdf\"]}},\"requesterRealName\":\"Kris\"},{\"authorise\":{\"authoriseId\":13,\"publishId\":22,\"requesterUuid\":\"7c01aaeb-b698-429a-b65b-c49cc809b14b\",\"isAuthorise\":-1,\"filePath\":\"classpath:fileSystem/workSpace/234e78b7-1855-442e-94f4-b0936bcc4723/secondLevel/阿里巴巴Java开发手册1.4.0.pdf\",\"fileName\":\"阿里巴巴Java开发手册1.4.0.pdf\",\"secretKey\":\"none\",\"time\":\"2024-06-13T14:28:14.000+00:00\"},\"publish\":{\"code\":200,\"message\":\"success\",\"data\":{\"third\":[\"实验1：vgg_net().txt\"],\"mainInfo\":{\"publishTime\":\"2024-06-13 22:27:47\",\"description\":\"大萨达撒贵都不可能\",\"publisher\":\"哈哈哈1\",\"title\":\"的赛U币大四班\"},\"first\":[\"新建 文本文档.txt\"],\"second\":[\"tmp.txt\",\"uploadercloud.sql\",\"阿里巴巴Java开发手册1.4.0.pdf\"]}},\"requesterRealName\":\"Kris\"},{\"authorise\":{\"authoriseId\":12,\"publishId\":22,\"requesterUuid\":\"7c01aaeb-b698-429a-b65b-c49cc809b14b\",\"isAuthorise\":-1,\"filePath\":\"classpath:fileSystem/workSpace/234e78b7-1855-442e-94f4-b0936bcc4723/secondLevel/阿里巴巴Java开发手册1.4.0.pdf\",\"fileName\":\"阿里巴巴Java开发手册1.4.0.pdf\",\"secretKey\":\"none\",\"time\":\"2024-06-13T14:28:13.000+00:00\"},\"publish\":{\"code\":200,\"message\":\"success\",\"data\":{\"third\":[\"实验1：vgg_net().txt\"],\"mainInfo\":{\"publishTime\":\"2024-06-13 22:27:47\",\"description\":\"大萨达撒贵都不可能\",\"publisher\":\"哈哈哈1\",\"title\":\"的赛U币大四班\"},\"first\":[\"新建 文本文档.txt\"],\"second\":[\"tmp.txt\",\"uploadercloud.sql\",\"阿里巴巴Java开发手册1.4.0.pdf\"]}},\"requesterRealName\":\"Kris\"},{\"authorise\":{\"authoriseId\":11,\"publishId\":17,\"requesterUuid\":\"7c01aaeb-b698-429a-b65b-c49cc809b14b\",\"isAuthorise\":0,\"filePath\":\"classpath:fileSystem/workSpace/234e78b7-1855-442e-94f4-b0936bcc4723/secondLevel/d1a933c736af4625bfe6b0833cca3db0.jpg\",\"fileName\":\"d1a933c736af4625bfe6b0833cca3db0.jpg\",\"secretKey\":\"notAllowed\",\"time\":\"2024-06-13T14:14:42.000+00:00\"},\"publish\":{\"code\":200,\"message\":\"success\",\"data\":{\"third\":[\"数据库实验.docx\"],\"mainInfo\":{\"publishTime\":\"2024-06-08 20:21:21\",\"description\":\"FB被别人\",\"publisher\":\"哈哈哈1\",\"title\":\"测试\"},\"first\":[],\"second\":[\"1a5eca88c377d5c1893eb9dbdf71f9b2.jpg\",\"d1a933c736af4625bfe6b0833cca3db0.jpg\"]}},\"requesterRealName\":\"Kris\"},{\"authorise\":{\"authoriseId\":10,\"publishId\":17,\"requesterUuid\":\"7c01aaeb-b698-429a-b65b-c49cc809b14b\",\"isAuthorise\":1,\"filePath\":\"classpath:fileSystem/workSpace/234e78b7-1855-442e-94f4-b0936bcc4723/secondLevel/d1a933c736af4625bfe6b0833cca3db0.jpg\",\"fileName\":\"d1a933c736af4625bfe6b0833cca3db0.jpg\",\"secretKey\":\"469e7ce4-694c-4783-ae09-966eadc0ebe9\",\"time\":\"2024-06-13T14:14:40.000+00:00\"},\"publish\":{\"code\":200,\"message\":\"success\",\"data\":{\"third\":[\"数据库实验.docx\"],\"mainInfo\":{\"publishTime\":\"2024-06-08 20:21:21\",\"description\":\"FB被别人\",\"publisher\":\"哈哈哈1\",\"title\":\"测试\"},\"first\":[],\"second\":[\"1a5eca88c377d5c1893eb9dbdf71f9b2.jpg\",\"d1a933c736af4625bfe6b0833cca3db0.jpg\"]}},\"requesterRealName\":\"Kris\"},{\"authorise\":{\"authoriseId\":9,\"publishId\":21,\"requesterUuid\":\"7c01aaeb-b698-429a-b65b-c49cc809b14b\",\"isAuthorise\":1,\"filePath\":\"classpath:fileSystem/workSpace/234e78b7-1855-442e-94f4-b0936bcc4723/secondLevel/1713805670696.png\",\"fileName\":\"1713805670696.png\",\"secretKey\":\"fc130622-2979-42ec-ba4d-7607930c537e\",\"time\":\"2024-06-13T14:07:27.000+00:00\"},\"publish\":{\"code\":200,\"message\":\"success\",\"data\":{\"third\":[\"实验1：vgg_net().txt\"],\"mainInfo\":{\"publishTime\":\"2024-06-10 22:30:08\",\"description\":\"saddasdsa\",\"publisher\":\"哈哈哈1\",\"title\":\"大风阿萨德\"},\"first\":[\"2-2022版软件工程专业人才培养方案(1).docx\"],\"second\":[\"1713805670696.png\"]}},\"requesterRealName\":\"Kris\"},{\"authorise\":{\"authoriseId\":8,\"publishId\":21,\"requesterUuid\":\"7c01aaeb-b698-429a-b65b-c49cc809b14b\",\"isAuthorise\":-1,\"filePath\":\"classpath:fileSystem/workSpace/234e78b7-1855-442e-94f4-b0936bcc4723/secondLevel/1713805670696.png\",\"fileName\":\"1713805670696.png\",\"secretKey\":\"none\",\"time\":\"2024-06-13T14:07:24.000+00:00\"},\"publish\":{\"code\":200,\"message\":\"success\",\"data\":{\"third\":[\"实验1：vgg_net().txt\"],\"mainInfo\":{\"publishTime\":\"2024-06-10 22:30:08\",\"description\":\"saddasdsa\",\"publisher\":\"哈哈哈1\",\"title\":\"大风阿萨德\"},\"first\":[\"2-2022版软件工程专业人才培养方案(1).docx\"],\"second\":[\"1713805670696.png\"]}},\"requesterRealName\":\"Kris\"},{\"authorise\":{\"authoriseId\":7,\"publishId\":20,\"requesterUuid\":\"234e78b7-1855-442e-94f4-b0936bcc4723\",\"isAuthorise\":-1,\"filePath\":\"classpath:fileSystem/workSpace/234e78b7-1855-442e-94f4-b0936bcc4723/secondLevel/uploadercloud.sql\",\"fileName\":\"uploadercloud.sql\",\"secretKey\":\"f60d9ace-f129-446f-bee9-815e09aa4994\",\"time\":\"2024-06-12T08:51:22.000+00:00\"},\"publish\":{\"code\":200,\"message\":\"success\",\"data\":{\"third\":[],\"mainInfo\":{\"publishTime\":\"2024-06-09 16:48:37\",\"description\":\"i滴滴滴滴i\",\"publisher\":\"哈哈哈1\",\"title\":\"不可预览二级文件\"},\"first\":[\"新建 文本文档.txt\"],\"second\":[\"uploadercloud.sql\",\"阿里巴巴Java开发手册1.4.0.pdf\",\"实验1：vgg_net().txt\"]}},\"requesterRealName\":\"哈哈哈1\"},{\"authorise\":{\"authoriseId\":1,\"publishId\":20,\"requesterUuid\":\"234e78b7-1855-442e-94f4-b0936bcc4723\",\"isAuthorise\":1,\"filePath\":\"classpath:fileSystem/workSpace/234e78b7-1855-442e-94f4-b0936bcc4723/thirdLevel/实验1：vgg_net().txt\",\"fileName\":\"实验1：vgg_net().txt\",\"secretKey\":\"0e98cfde-636f-43d5-a9e8-bea50093c995\",\"time\":\"2024-06-09T15:11:50.000+00:00\"},\"publish\":{\"code\":200,\"message\":\"success\",\"data\":{\"third\":[],\"mainInfo\":{\"publishTime\":\"2024-06-09 16:48:37\",\"description\":\"i滴滴滴滴i\",\"publisher\":\"哈哈哈1\",\"title\":\"不可预览二级文件\"},\"first\":[\"新建 文本文档.txt\"],\"second\":[\"uploadercloud.sql\",\"阿里巴巴Java开发手册1.4.0.pdf\",\"实验1：vgg_net().txt\"]}},\"requesterRealName\":\"哈哈哈1\"}],\"maxPages\":1}}"))))
    @GetMapping("get/all/{currentPage}/{orderCode}")
    public Result getAllAuth(@RequestHeader("token")String token,
                             @PathVariable("currentPage") Integer currentPage,
                             @PathVariable("orderCode") Integer orderCode){
        String uuid = jwtHelper.getUserId(token);
        if (orderCode==1){
            return authoriseService.getAllAuth(uuid,currentPage,"desc");
        }else if (orderCode==2){
            return authoriseService.getAllAuth(uuid,currentPage,"asc");
        }
        return Result.ok("参数错误,恶意修改");
    }

    /**
     * 用户获取某一个对应Id的authorise表
     * 用于前端实现查看页面喔~
     * token用于保证不是用户不可调fie用接口
     * @param token
     * @return
     */
    @GetMapping("get/one/{authoriseId}")
    public Result getOneAuth(@RequestHeader("token")String token,
                             @PathVariable("authoriseId")Integer authoriseId){
        String uuid = jwtHelper.getUserId(token);
        Result result=authoriseService.getOneAuth(authoriseId);
        return result;
    }


    /**
     *  领导 针对 某个员工的请求决定是否进行确认同意..如果同意,标注is_authorised为1
     *  @Description :publishId和token用于确认是老板本人的操作
     *  @Description :authoriseId 用于确认是哪个用户的authorise
     *  @Description :is_authorised 1表示同意,0表示拒绝
     */
    @GetMapping("handle/{publishId}/{authoriseId}")
    public Result handleAuth(@PathVariable("publishId") Integer publishId,
                             @RequestHeader("token")String token,
                             @PathVariable("authoriseId") Integer authoriseId,
                             @RequestParam("status") Integer is_authorised){

        String uuid = jwtHelper.getUserId(token);
        Result result=authoriseService.handleAuth(authoriseId,publishId,uuid,is_authorised);
        return result;
    }


    /**
     * 用户下载处...
     * @return
     */
    @GetMapping("download/{authoriseId}/{secretKey}")
    public ResponseEntity<Resource> downloadAuth(@PathVariable("authoriseId") Integer authoriseId,
                                                 @PathVariable("secretKey") String secretKey){

        ResponseEntity<Resource> result=authoriseService.downloadAuth(authoriseId,secretKey);
        return result;
    }
}
