package com.xiangyueEducation.uploaderCloud.Controller;


import com.xiangyueEducation.uploaderCloud.Service.funPackage.FileService;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.SelfCloudFileSpaceService;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.mapper.SelfCloudFileSpaceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("file")
public class FileController {


    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private SelfCloudFileSpaceMapper selfCloudFileSpaceMapper;

    @Autowired
    private SelfCloudFileSpaceService selfCloudFileSpaceService;

    @Autowired
    private FileService fileService;



    /**
     * 检测个人云空间的文件名是否重复
     * @param fileName
     * @return
     */


    @GetMapping("isFileNameSame")
    public Boolean isFileNameSame(@RequestHeader String token,
                                  @RequestParam("fileName") String fileName){
        String uuid = jwtHelper.getUserId(token);
        String[] allFileName = selfCloudFileSpaceMapper.getALLFileNameById(uuid);
        for (String item : allFileName) {
            if (item.equals(fileName)){
                return true;
            }
        }
        return false;
    }





    /**
     * 分片上传文件(与"HOST/file/merge/selfSpace"接口相配合)
     * @param token
     * @param index
     * @param chunkSize
     * @param totalChunks
     * @param totalSize
     * @param fileName
     * @param file
     * @return
     */
    @PostMapping("upload/selfSpace")
    ResponseEntity<Map<String, Object>> uploadChunk(@RequestHeader String token,
                                                    @RequestParam int index,
                                                    @RequestParam long chunkSize,
                                                    @RequestParam int totalChunks,
                                                    @RequestParam long totalSize,
                                                    @RequestParam("filename") String fileName,
                                                    @RequestBody byte[] file){
        ResponseEntity<Map<String, Object>> res=fileService.uploadChunk(token, index,chunkSize,totalChunks,totalSize,fileName,file);
        return res;
    }

    @PostMapping("upload/selfSpace/admin")
    ResponseEntity<Map<String, Object>> uploadChunkAdmin(@RequestParam String uuid,
                                                    @RequestParam int index,
                                                    @RequestParam long chunkSize,
                                                    @RequestParam int totalChunks,
                                                    @RequestParam long totalSize,
                                                    @RequestParam("filename") String fileName,
                                                    @RequestBody byte[] file){
        ResponseEntity<Map<String, Object>> res=fileService.uploadChunkAdmin(uuid, index,chunkSize,totalChunks,totalSize,fileName,file);
        return res;
    }


    /**
     * 合并分片的文件(与"HOST/file/upload/selfSpace"接口相配合)
     * @param token
     * @param fileName
     * @return
     */
    @GetMapping("merge/selfSpace")
    public ResponseEntity<Map<String, Object>> merge(@RequestParam("token") String token,
                                                     @RequestParam("filename") String fileName){

        ResponseEntity<Map<String, Object>> res=fileService.merge(token,fileName);
        return res;
    }
    @GetMapping("merge/selfSpace/admin")
    public ResponseEntity<Map<String, Object>> mergeAdmin(@RequestParam("uuid") String uuid,
                                                     @RequestParam("filename") String fileName){

        ResponseEntity<Map<String, Object>> res=fileService.mergeAdmin(uuid,fileName);
        return res;
    }
    /**
     * 将文件信息写入数据库的self_cloud_file_space表中
     * @param token
     * @param data
     * @return
     */

    @PostMapping("write/db/selfSpace")
    public Result writeToDB(@RequestHeader String token, @RequestBody Map data){

        return fileService.writeToDB(token,data);
    }
    @PostMapping("write/db/selfSpace/admin")
    public Result writeToDBAdmin(@RequestParam("uuid") String uuid, @RequestBody Map data){

        return fileService.writeToDBAdmin(uuid,data);
    }



    @GetMapping("delete/selfSpace")
    public Result deleteFile(@RequestHeader String token,@RequestParam("fileName") String fileName){
        return fileService.deleteFile(token,fileName);
    }

    @GetMapping("get/selfSpace/all/{uuid}")
    public Result getAllFile(@PathVariable("uuid")String uuid){
        return  fileService.getAllFile(uuid);
    }





}
