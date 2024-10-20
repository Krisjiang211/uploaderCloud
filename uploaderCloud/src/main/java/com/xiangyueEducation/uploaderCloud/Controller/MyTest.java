package com.xiangyueEducation.uploaderCloud.Controller;


import cn.hutool.core.util.StrUtil;
import com.xiangyueEducation.uploaderCloud.Utils.BaseUtils.PathUtils;
import com.xiangyueEducation.uploaderCloud.Utils.QRCodeGenerator;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.URLEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("test")
public class MyTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private PathUtils pathUtils;


    @Test
    public void testRegex(){
        String originFileName = "example.txt";
        String fileName = "3-example.txt";

        if (fileName.matches("^\\d+-" + Pattern.quote(originFileName) + "$")) {
            System.out.println("匹配成功: " + fileName);
        } else {
            System.out.println("不匹配: " + fileName);
        }
    }



    @RequestMapping("logTest")
    public Result test(){
        log.info("测试成功");
        return Result.ok("测试成功");
    }

    @RequestMapping("getResourcePath")
    public Result getResourcePath() throws IOException, URISyntaxException {
        URL url=resourceLoader.getResource("classpath:fileSystem/avatar/defaultAvatar.png").getURL();
        URI uri=url.toURI();
        Path path=Paths.get(uri);

        log.info("URL  "+url.toString());
        log.info("URI  "+uri.toString());
        log.info("Path  "+path.toString());
        return Result.ok("获取成功静态资源的URL");
    }

    @RequestMapping("getResource")
    public ResponseEntity getResource() throws IOException {
//        //从classpath读取到资源
//        Resource resource=resourceLoader.getResource("classpath:fileSystem/avatar/defaultAvatar.png");
//        //转换成实体路径,并进行读取文件并存储
//        byte[] content= Files.readAllBytes(Paths.get(resource.getURI()));
        byte[] content= Files.readAllBytes(pathUtils.getRealPath("classpath:fileSystem/avatar/defaultAvatar.png"));
        //设置响应头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE,"image/png");
        //返回文件
        return new ResponseEntity(content,httpHeaders, HttpStatus.OK);

    }


    @Test
    public void test2(){
        String input = "first/second/third/fourth";
        String delimiter = "/";

        // 使用 split() 方法分割字符串
        String[] parts = input.split(delimiter);

        // 构建新的字符串，忽略第一个元素
        StringBuilder result = new StringBuilder();
        for (int i = 1; i < parts.length; i++) {
            result.append(delimiter).append(parts[i]);
        }

        // 输出结果
        String newString = result.toString();
        System.out.println(newString);
    }


    @GetMapping("get/qrCode/stream")
    public ResponseEntity<byte[]> getQrCode(){

        return QRCodeGenerator.getPngQRCodeWebStream("https://cn.pornhub.com/");

    }

    @GetMapping("get/qrCode/local")
    public Result getQrCodeLocal(){
        String pngQRCodeLocal = QRCodeGenerator.getPngQRCodeLocal("mailto:xjunjiang5@gmail.com");
        QRCodeGenerator.deleteQRCodeLocal(pngQRCodeLocal,180000);
        String QRCodeUrl= URLEnum.HOST_URL.getUrl()+URLEnum.QRCode_RESOURCE_HEADER.getUrl()+ StrUtil.split(pngQRCodeLocal,"/").get(StrUtil.split(pngQRCodeLocal,"/").size()-1);
        return Result.ok(QRCodeUrl);
    }






}
