package com.xiangyueEducation.uploaderCloud.Service.funPackage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiangyueEducation.uploaderCloud.POJO.SelfCloudFileSpace;
import com.xiangyueEducation.uploaderCloud.Service.funPackage.FileService;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.SelfCloudFileSpaceService;
import com.xiangyueEducation.uploaderCloud.Utils.BaseUtils.PathUtils;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.PathEnum;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.URLEnum;
import com.xiangyueEducation.uploaderCloud.mapper.FileCategoryMapper;
import com.xiangyueEducation.uploaderCloud.mapper.SelfCloudFileSpaceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;


@Slf4j
@Service
public class FileServiceImpl implements FileService {


    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private SelfCloudFileSpaceMapper selfCloudFileSpaceMapper;

    @Autowired
    private PathUtils pathUtils;

    @Autowired
    private FileCategoryMapper fileCategoryMapper;



    @Override
    public ResponseEntity<Map<String, Object>> uploadChunk(String token,int index, long chunkSize, int totalChunks, long totalSize, String fileName, byte[] file) {
        String uuid = jwtHelper.getUserId(token);
        //这个uuidDir是每个用户各自有一个文件夹,以防发生冲突
        String selfSpaceHeaderPath=pathUtils.getRealPath(PathEnum.TMP_PATH_SELF_SPACE.getPath()).toString();
        /**
         * 生产环境
         */
//        String uuidDir=selfSpaceHeaderPath+"/"+uuid;


        String uuidDir=selfSpaceHeaderPath+"/"+uuid;


        File uploadDir = new File(uuidDir);
        if (!uploadDir.exists()) {

            System.out.println("创建了一次文件夹");

            uploadDir.mkdirs();

            System.out.println("创建成功");
        }
        // 将接收到的分块数据写入临时文件
        Path tempFilePath = Paths.get(uuidDir, fileName + "-" + index);

        try {
            Files.copy(new ByteArrayInputStream(file), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("文件分块写入失败:  "+fileName+"-"+index);
            throw new RuntimeException(e);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Map<String, Object>> uploadChunkAdmin(String uuid, int index, long chunkSize, int totalChunks, long totalSize, String fileName, byte[] file) {
        String selfSpaceHeaderPath=pathUtils.getRealPath(PathEnum.TMP_PATH_SELF_SPACE.getPath()).toString();
        /**
         * 生产环境
         */
//        String uuidDir=selfSpaceHeaderPath+"/"+uuid;


        String uuidDir=selfSpaceHeaderPath+"/"+uuid;


        File uploadDir = new File(uuidDir);
        if (!uploadDir.exists()) {

            System.out.println("创建了一次文件夹");

            uploadDir.mkdirs();

            System.out.println("创建成功");
        }
        // 将接收到的分块数据写入临时文件
        Path tempFilePath = Paths.get(uuidDir, fileName + "-" + index);

        try {
            Files.copy(new ByteArrayInputStream(file), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("文件分块写入失败:  "+fileName+"-"+index);
            throw new RuntimeException(e);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        return ResponseEntity.ok(result);
    }


    @Override
    public ResponseEntity<Map<String, Object>> merge(String token, String fileName) {
        String uuid = jwtHelper.getUserId(token);
        //要上传的文件的文件头
        //例子:real(classpath)/fileSystem/selfSpace/real(uuid)
        String selfSpaceHeaderPath=pathUtils.getRealPath(PathEnum.SELF_SPACE_PATH.getPath()).toString();
        String uploadDirString= selfSpaceHeaderPath+"/"+uuid;
        File uploadDir = new File(uploadDirString);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String selfSpaceTmpHeaderDir=pathUtils.getRealPath(PathEnum.TMP_PATH_SELF_SPACE.getPath()).toString();
        String tmpPath=selfSpaceTmpHeaderDir+"/"+uuid;
        File tempDir = new File(tmpPath);
        String[] fileNames = tempDir.list((dir, name) -> name.startsWith(fileName));
        Arrays.sort(fileNames);

        Path finalPath = Paths.get(uploadDirString, fileName);
        try (FileOutputStream fos = new FileOutputStream(finalPath.toFile())) {
            for (String tempFileName : fileNames) {
                Path tempFilePath = Paths.get(tmpPath, tempFileName);
                Files.copy(tempFilePath, fos);
                Files.delete(tempFilePath);
            }
        }catch (IOException e) {
            log.error("文件合并失败:  "+fileName);
            throw new RuntimeException(e);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("url", URLEnum.HOST_URL+"selfSpace/"+uuid+"/" + fileName);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Map<String, Object>> mergeAdmin(String uuid, String fileName) {
        String selfSpaceHeaderPath=pathUtils.getRealPath(PathEnum.SELF_SPACE_PATH.getPath()).toString();
        String uploadDirString= selfSpaceHeaderPath+"/"+uuid;
        File uploadDir = new File(uploadDirString);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String selfSpaceTmpHeaderDir=pathUtils.getRealPath(PathEnum.TMP_PATH_SELF_SPACE.getPath()).toString();
        String tmpPath=selfSpaceTmpHeaderDir+"/"+uuid;
        File tempDir = new File(tmpPath);
        String[] fileNames = tempDir.list((dir, name) -> name.startsWith(fileName));
        Arrays.sort(fileNames);

        Path finalPath = Paths.get(uploadDirString, fileName);
        try (FileOutputStream fos = new FileOutputStream(finalPath.toFile())) {
            for (String tempFileName : fileNames) {
                Path tempFilePath = Paths.get(tmpPath, tempFileName);
                Files.copy(tempFilePath, fos);
                Files.delete(tempFilePath);
            }
        }catch (IOException e) {
            log.error("文件合并失败:  "+fileName);
            throw new RuntimeException(e);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("url", URLEnum.HOST_URL+"selfSpace/"+uuid+"/" + fileName);
        return ResponseEntity.ok(result);
    }


    @Override
    public Result writeToDB(String token, Map data) {
        String uuid = jwtHelper.getUserId(token);
        SelfCloudFileSpace selfCloudFileSpace = new SelfCloudFileSpace();
        selfCloudFileSpace.setFileName((String) data.get("fileName"));
        selfCloudFileSpace.setUuid(uuid);
        selfCloudFileSpace.setFileSize((Integer) data.get("fileSize"));
        selfCloudFileSpace.setFilePath(PathEnum.SELF_SPACE_PATH.getPath()+uuid+"/"+data.get("fileName"));
        selfCloudFileSpace.setFileCategoryId(fileCategoryMapper.selectCategoryIdByName((String) data.get("fileCategory")));
        selfCloudFileSpaceMapper.insert(selfCloudFileSpace);
        log.info("用户 "+uuid+" 添加了文件 "+data.get("fileName")+" 文件分类为 "+data.get("fileCategory"));

        return Result.ok("上传文件成功");
    }

    @Override
    public Result writeToDBAdmin(String uuid, Map data) {

        SelfCloudFileSpace selfCloudFileSpace = new SelfCloudFileSpace();
        selfCloudFileSpace.setFileName((String) data.get("fileName"));
        selfCloudFileSpace.setUuid(uuid);
        selfCloudFileSpace.setFileSize((Integer) data.get("fileSize"));
        selfCloudFileSpace.setFilePath(PathEnum.SELF_SPACE_PATH.getPath()+uuid+"/"+data.get("fileName"));
        selfCloudFileSpace.setFileCategoryId(fileCategoryMapper.selectCategoryIdByName((String) data.get("fileCategory")));
        selfCloudFileSpaceMapper.insert(selfCloudFileSpace);
        log.info("用户 "+uuid+" 添加了文件 "+data.get("fileName")+" 文件分类为 "+data.get("fileCategory"));

        return Result.ok("上传文件成功");
    }


    @Override
    public Result deleteFile(String token, String fileName) {
        String uuid = jwtHelper.getUserId(token);

        //获取这个文件对象
        SelfCloudFileSpace selfCloudFileSpace=selfCloudFileSpaceMapper.getOneByFileNameAndUuid(uuid,fileName);
        String fileClasspath = selfCloudFileSpace.getFilePath();
        Path realPath = pathUtils.getRealPath(fileClasspath);
        try {
            Files.delete(realPath);
        } catch (IOException e) {
            log.error("用户 "+uuid+" 删除文件失败:  "+fileName+" \n原因是"+e.getMessage());
            throw new RuntimeException(e);
        }

        //删除数据库中的存在
        Integer lines= selfCloudFileSpaceMapper.deleteByFileNameAndUuid(uuid,fileName);
        System.out.println("lines = " + lines);
        log.warn("用户 "+uuid+ " 删除了文件 "+fileName);
        return Result.ok("删除文件成功");

    }

    @Override
    public Result getAllFile(String uuid) {
        List<Map> data=new ArrayList<>();

        QueryWrapper<SelfCloudFileSpace> wrapper = new QueryWrapper<>();
        wrapper.eq("uuid",uuid);
        List<SelfCloudFileSpace> selfCloudFileSpaces = selfCloudFileSpaceMapper.selectList(wrapper);
        for (SelfCloudFileSpace selfCloudFileSpace : selfCloudFileSpaces) {
            Map<Object, Object> item = new HashMap<>();
            item.put("fileName",selfCloudFileSpace.getFileName());
            item.put("fileSize",selfCloudFileSpace.getFileSize());
            item.put("fileCategoryName",fileCategoryMapper.selectById(selfCloudFileSpace.getFileCategoryId()).getCategoryName());
            String filePathBeta = selfCloudFileSpace.getFilePath();
            String[] parts = filePathBeta.split("/");
            StringBuilder filePath = new StringBuilder();
            for( int i=1;i<parts.length;i++){
                filePath.append("/").append(parts[i]);
            }
            String filePathFinal = URLEnum.HOST_URL_NO_SLASH.getUrl()+ filePath.toString();
            item.put("fileUrl",filePathFinal);
            data.add(item);
        }

        return Result.ok(data);
    }


}
