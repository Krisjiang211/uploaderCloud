package com.xiangyueEducation.uploaderCloud.Service.funPackage;

import com.xiangyueEducation.uploaderCloud.Utils.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileGroupService {

    Result deleteFiles(List<Integer> ids);

    Result getOne( Integer publishId);

    //拉取顺序获取
    Result getTimeSequence(Integer departmentId,String order, Integer currentPage);
    //拉取顺序获取---mainAndViceType模式
    Result getTimeSequence(Integer departmentId,String order, Integer currentPage,Integer mainAndViceTypeId);

    Result getMine(String uuid,Integer currentPage,Integer pageSize);


    //拉取fileCate获取
    Result getFileCategory(String fileCategory,Integer departmentId,String order,Integer currentPage);

    //拉取fileCate获取---mainAndViceType模式
    Result getFileCategory(String fileCategory,Integer departmentId,String order,Integer currentPage,Integer mainAndViceTypeId);





    //拉取fileGroupCate获取
    Result getFileGroupCategory(String fileGroupCategory,Integer departmentId,String order,Integer currentPage);

    //拉取fileGroupCate获取---mainAndViceType模式
    Result getFileGroupCategory(String fileGroupCategory,Integer departmentId,String order,Integer currentPage,Integer mainAndViceTypeId);





    Result getOneByPublishId(Integer publishId);


    Result uploadFileGroupInfo(Map data);

    Result uploadFileGroupInfoAdmin(Map data);


    Result getSearchValueTimeSequence(String searchValue,Integer departmentId,String order,Integer currentPage);



    ResponseEntity<Map<String, Object>> uploadChunk(String token, int index, long chunkSize, int totalChunks, long totalSize, String fileName, byte[] file);

    ResponseEntity<Map<String, Object>> merge(String token, String fileName,Integer fileLevel);

    Result writeToDB(Map data);

    Result uploadPreviewImg(String groupId,  MultipartFile file);

    Result delete(Integer publishId,String uuid);

    Result delete(Integer publishId);

    Result deleteLogic( Integer publishId, String uuid);
    Result getOneFileDetail(String publishId, String fileName,String uuid,Integer fileLevel);

    Result getGroupId(Integer publishId);

}
