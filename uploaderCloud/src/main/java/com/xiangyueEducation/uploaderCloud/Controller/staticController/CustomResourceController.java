package com.xiangyueEducation.uploaderCloud.Controller.staticController;

import com.xiangyueEducation.uploaderCloud.POJO.Admin;
import com.xiangyueEducation.uploaderCloud.POJO.User;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.UserInfoService;
import com.xiangyueEducation.uploaderCloud.Utils.BaseUtils.PathUtils;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.mapper.AdminMapper;
import com.xiangyueEducation.uploaderCloud.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/workSpace")
public class CustomResourceController {

    @Value("${custom.resource.base-path}")
    private String basePath;

    @Value("${resource.key.preview}")
    private String previewKey;

    @Value("${resource.key.download}")
    private String downloadKey;


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private PathUtils pathUtils;


    private final ResourceLoader resourceLoader;

    public CustomResourceController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }




    /**
     * 对外暴露一级文件:可以下载也可以预览
     * 配置Resource支持流式传输
     * @param uuid
     * @param request
     * @return
     * @throws IOException
     */
    @GetMapping("/{uuid}/firstLevel/**")
    @ResponseBody
    public ResponseEntity<Resource> getResourceFirstLevel(
            @PathVariable("uuid") String uuid,
            HttpServletRequest request) throws IOException {

        /**
         * 获取完整路径: request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE) 获取当前请求的完整路径。
         * 提取子路径: remainingPath.substring(remainingPath.indexOf("/firstLevel/")) 找到 /firstLevel/ 在路径中的位置，并提取从 /firstLevel/ 开始的子路径。
         */
        String remainingPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        remainingPath = remainingPath.substring(remainingPath.indexOf("/firstLevel/"));
        remainingPath = URLDecoder.decode(remainingPath, StandardCharsets.UTF_8.toString());
        String resourcePath = String.format("classpath:%s/workSpace/%s%s", basePath, uuid, remainingPath);
        //TODO 开发环境---一级文件配置映射
//        Resource resource = resourceLoader.getResource(resourcePath);
        //TODO 生产环境---一级文件配置映射
        Resource resource=resourceLoader.getResource("file:"+pathUtils.getRealPath(resourcePath).toString());
        System.out.println("resourcePath --------------------------------= " + "file:"+pathUtils.getRealPath(resourcePath).toString());
        if (!resource.exists() || !resource.isReadable()) {
            System.out.println("******************没找到文件**************************");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        System.out.println("********************文件存在**************************");
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        // 原代码
        // headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());

        // 修改后的代码
        String fileName = resource.getFilename();
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");

        // 使用流式响应
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


    @GetMapping("/{uuid}/preview/firstLevel/**")
    @ResponseBody
    public ResponseEntity<Resource> getResourceFirstLevelPreview(
            @PathVariable("uuid") String uuid,
            HttpServletRequest request) throws IOException {

        /**
         * 获取完整路径: request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE) 获取当前请求的完整路径。
         * 提取子路径: remainingPath.substring(remainingPath.indexOf("/firstLevel/")) 找到 /firstLevel/ 在路径中的位置，并提取从 /firstLevel/ 开始的子路径。
         */
        String remainingPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        remainingPath = remainingPath.substring(remainingPath.indexOf("/firstLevel/"));
        remainingPath = URLDecoder.decode(remainingPath, StandardCharsets.UTF_8.toString());
        String resourcePath = String.format("classpath:%s/workSpace/%s%s", basePath, uuid, remainingPath);
        //TODO 开发环境---一级文件配置映射
//        Resource resource = resourceLoader.getResource(resourcePath);
        //TODO 生产环境---一级文件配置映射
        Resource resource=resourceLoader.getResource("file:"+pathUtils.getRealPath(resourcePath).toString());
        System.out.println("resourcePath --------------------------------= " + "file:"+pathUtils.getRealPath(resourcePath).toString());
        if (!resource.exists() || !resource.isReadable()) {
            System.out.println("******************没找到文件**************************");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        System.out.println("********************文件存在**************************");
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        // 原代码
        // headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());

        // 修改后的代码
        String fileName = resource.getFilename();
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedFileName + "\"");

        MediaType mediaType = judgeMediaType(fileName);

        // 使用流式响应
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(mediaType)
                .body(resource);
    }









    /**对外暴露二级文件:
     * 领导和admin下载的话,需要在请求中携带上自己的uuid
     * 前端预览查看需要previewKey
     * @param uuid
     * @param request
     * @param adminId
     * @return
     * @throws IOException
     */

    @GetMapping("{downloadKey}/{uuid}/secondLevel/**")
    @ResponseBody
    public ResponseEntity<Resource> getResourceSecondLevel(
            @PathVariable("uuid") String uuid,
            HttpServletRequest request,
            @RequestParam(value = "previewKey",required = false) String previewKey,
            @PathVariable(value = "downloadKey",required = false) String downloadKey,
            @RequestParam(value = "adminId",required = false) String adminId
            ) throws IOException {



        String remainingPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        remainingPath = remainingPath.substring(remainingPath.indexOf("/secondLevel/"));
        remainingPath = URLDecoder.decode(remainingPath, StandardCharsets.UTF_8.toString());

        String resourcePath = String.format("classpath:%s/workSpace/%s%s", basePath, uuid, remainingPath);

        //TODO 开发环境---二级文件配置映射
//        Resource resource = resourceLoader.getResource(resourcePath);
        //TODO 生产环境---二级文件配置映射
        Resource resource=resourceLoader.getResource("file:"+pathUtils.getRealPath(resourcePath).toString());

        System.out.println("resourcePath --------------------------------= " + "file:"+pathUtils.getRealPath(resourcePath).toString());

        if (!resource.exists() || !resource.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        System.out.println("********************文件存在**************************");
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
        // 修改后的代码
        String fileName = resource.getFilename();
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");


        // 使用流式响应
        ResponseEntity<Resource> res=ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
        log.warn("有人请求二级文件,参数为 previewKey="+previewKey+" adminId="+adminId);

        //是领导
        if(downloadKey!=null){
            if (downloadKey.equals(this.downloadKey)){
                return res;
            }
            log.error("有人试图请求二级文件,但是downloadKey错误,请求失败......请求文件为: "+resourcePath);
        }
        //是admin
        else if (adminId!=null) {
            Admin admin = adminMapper.selectById(adminId);
            if (admin!=null){
                log.warn("请求二级文件成功,请求对象为:"+adminId);
                return res;
            }
        }
        //有previewKey
        else if (previewKey!=null) {
            if (previewKey.equals(this.previewKey)){
                log.warn("请求二级文件成功,请求对象为:previewKey");
                return res;
            }
        }
        log.error("有人请求二级文件,但是权限不足,请求失败");
        return null;
    }




    @GetMapping("{downloadKey}/{uuid}/preview/secondLevel/**")
    @ResponseBody
    public ResponseEntity<Resource> getResourceSecondLevelPreview(
            @PathVariable("uuid") String uuid,
            HttpServletRequest request,
            @RequestParam(value = "previewKey",required = false) String previewKey,
            @PathVariable(value = "downloadKey",required = false) String downloadKey,
            @RequestParam(value = "adminId",required = false) String adminId
    ) throws IOException {



        String remainingPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        remainingPath = remainingPath.substring(remainingPath.indexOf("/secondLevel/"));
        remainingPath = URLDecoder.decode(remainingPath, StandardCharsets.UTF_8.toString());

        String resourcePath = String.format("classpath:%s/workSpace/%s%s", basePath, uuid, remainingPath);

        //TODO 开发环境---二级文件配置映射
//        Resource resource = resourceLoader.getResource(resourcePath);
        //TODO 生产环境---二级文件配置映射
        Resource resource=resourceLoader.getResource("file:"+pathUtils.getRealPath(resourcePath).toString());

        System.out.println("resourcePath --------------------------------= " + "file:"+pathUtils.getRealPath(resourcePath).toString());

        if (!resource.exists() || !resource.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        System.out.println("********************文件存在**************************");
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
        // 修改后的代码
        String fileName = resource.getFilename();
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedFileName + "\"");

        MediaType mediaType = judgeMediaType(fileName);

        // 使用流式响应
        ResponseEntity<Resource> res=ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(mediaType)
                .body(resource);
        log.warn("有人请求二级文件,参数为 previewKey="+previewKey+" adminId="+adminId);

        //是领导
        if(downloadKey!=null){
            if (downloadKey.equals(this.downloadKey)){
                return res;
            }
            log.error("有人试图请求二级文件,但是downloadKey错误,请求失败......请求文件为: "+resourcePath);
        }
        //是admin
        else if (adminId!=null) {
            Admin admin = adminMapper.selectById(adminId);
            if (admin!=null){
                log.warn("请求二级文件成功,请求对象为:"+adminId);
                return res;
            }
        }
        //有previewKey
        else if (previewKey!=null) {
            if (previewKey.equals(this.previewKey)){
                log.warn("请求二级文件成功,请求对象为:previewKey");
                return res;
            }
        }
        log.error("有人请求二级文件,但是权限不足,请求失败");
        return null;
    }



















    @GetMapping("{downloadKey}/{uuid}/thirdLevel/**")
    @ResponseBody
    public ResponseEntity<Resource> getResourceThirdLevel(
            @PathVariable("uuid") String uuid,
            HttpServletRequest request,
            @PathVariable(value = "downloadKey",required = false) String downloadKey,
            @RequestParam(value = "adminId",required = false) String adminId
    ) throws IOException {

        String remainingPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        remainingPath = remainingPath.substring(remainingPath.indexOf("/thirdLevel/"));
        remainingPath = URLDecoder.decode(remainingPath, StandardCharsets.UTF_8.toString());

        String resourcePath = String.format("classpath:%s/workSpace/%s%s", basePath, uuid, remainingPath);

        //TODO 开发环境---三级文件配置映射
//        Resource resource = resourceLoader.getResource(resourcePath);
        //TODO 生产环境---三级文件配置映射
        Resource resource=resourceLoader.getResource("file:"+pathUtils.getRealPath(resourcePath).toString());

        System.out.println("resourcePath --------------------------------= " + "file:"+pathUtils.getRealPath(resourcePath).toString());

        if (!resource.exists() || !resource.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        System.out.println("********************文件存在**************************");

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
        // 修改后的代码
        String fileName = resource.getFilename();
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
        // 使用流式响应
        ResponseEntity<Resource> res=ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

        log.warn("有人请求三级文件,参数为:"+" adminId="+adminId);

        //是领导
        if(downloadKey!=null){
            if (downloadKey.equals(this.downloadKey)){
                log.warn("请求三级文件成功,请求对象为:downloadKey");
                return res;
            }
            log.error("有人请求三级文件,但是downloadKey错误,请求失败");
        }
        //是admin
        else if (adminId!=null) {
            Admin admin = adminMapper.selectById(adminId);
            if (admin!=null){
                log.warn("请求三级文件成功,请求对象为:"+adminId);
                return res;
            }
        }
        log.error("有人请求三级文件,但是权限不足,请求失败");
        return null;
    }



    @GetMapping("{downloadKey}/{uuid}/preview/thirdLevel/**")
    @ResponseBody
    public ResponseEntity<Resource> getResourceThirdLevelPreview(
            @PathVariable("uuid") String uuid,
            HttpServletRequest request,
            @PathVariable(value = "downloadKey",required = false) String downloadKey,
            @RequestParam(value = "adminId",required = false) String adminId
    ) throws IOException {

        String remainingPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        remainingPath = remainingPath.substring(remainingPath.indexOf("/thirdLevel/"));
        remainingPath = URLDecoder.decode(remainingPath, StandardCharsets.UTF_8.toString());

        String resourcePath = String.format("classpath:%s/workSpace/%s%s", basePath, uuid, remainingPath);

        //TODO 开发环境---三级文件配置映射
//        Resource resource = resourceLoader.getResource(resourcePath);
        //TODO 生产环境---三级文件配置映射
        Resource resource=resourceLoader.getResource("file:"+pathUtils.getRealPath(resourcePath).toString());

        System.out.println("resourcePath --------------------------------= " + "file:"+pathUtils.getRealPath(resourcePath).toString());

        if (!resource.exists() || !resource.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        System.out.println("********************文件存在**************************");

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
        // 修改后的代码
        String fileName = resource.getFilename();
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedFileName + "\"");

        MediaType mediaType = judgeMediaType(fileName);

        // 使用流式响应
        ResponseEntity<Resource> res=ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(mediaType)
                .body(resource);

        log.warn("有人请求三级文件,参数为:"+" adminId="+adminId);

        //是领导
        if(downloadKey!=null){
            if (downloadKey.equals(this.downloadKey)){
                log.warn("请求三级文件成功,请求对象为:downloadKey");
                return res;
            }
            log.error("有人请求三级文件,但是downloadKey错误,请求失败");
        }
        //是admin
        else if (adminId!=null) {
            Admin admin = adminMapper.selectById(adminId);
            if (admin!=null){
                log.warn("请求三级文件成功,请求对象为:"+adminId);
                return res;
            }
        }
        log.error("有人请求三级文件,但是权限不足,请求失败");
        return null;
    }



    public MediaType judgeMediaType(String fileName){
        MediaType mediaType;
        if (fileName.endsWith(".pdf")) {
            mediaType = MediaType.APPLICATION_PDF;
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            mediaType = MediaType.IMAGE_JPEG;
        } else if (fileName.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else if (fileName.endsWith(".mp4")) {
            mediaType = MediaType.parseMediaType("video/mp4");
        } else if (fileName.endsWith(".docx")) {
            mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        } else {
            // 默认为通用的二进制流
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return mediaType;
    }

}
