package com.xiangyueEducation.uploaderCloud.Utils.BaseUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
public class PathUtils {

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     *  TODO 开发环境---getRealPath方法
     */
//    public Path getRealPath(String classpath) {
//        Resource resource = resourceLoader.getResource(classpath);
//        try{
//            return Paths.get(resource.getURI());
//        }catch (IOException e){
//            log.warn("Paths.get(resource.getURI())出错,出错原因:"+e.getMessage());
//            return null;
//        }
//    }

    /**
     * TODO 生产环境---getRealPath方法
     * @param classpath
     * @return
     */
    @Value("${file.upload.directory}")
    private String uploadDirectory;

    public Path getRealPath(String classpath){
        StringBuilder pathString = new StringBuilder(classpath);
        String path = uploadDirectory+classpath.split(":")[classpath.split(":").length - 1];
        return Paths.get(path);
    }

}
