package com.xiangyueEducation.uploaderCloud.Service.funPackage;

import com.xiangyueEducation.uploaderCloud.Utils.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileService {


    ResponseEntity<Map<String, Object>> uploadChunk(String token,int index, long chunkSize, int totalChunks, long totalSize, String fileName, byte[] file);
    ResponseEntity<Map<String, Object>> uploadChunkAdmin(String uuid,int index, long chunkSize, int totalChunks, long totalSize, String fileName, byte[] file);

    ResponseEntity<Map<String, Object>> merge(String token, String fileName);

    ResponseEntity<Map<String, Object>> mergeAdmin(String uuid,String fileName);

    Result writeToDB(String token,Map data);
    Result writeToDBAdmin(String uuid,Map data);

    Result deleteFile(String token,String fileName);

    Result getAllFile(String uuid);

}
