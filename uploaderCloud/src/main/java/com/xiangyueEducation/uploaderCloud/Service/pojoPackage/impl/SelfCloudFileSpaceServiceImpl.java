package com.xiangyueEducation.uploaderCloud.Service.pojoPackage.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiangyueEducation.uploaderCloud.POJO.SelfCloudFileSpace;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.SelfCloudFileSpaceService;
import com.xiangyueEducation.uploaderCloud.Utils.*;
import com.xiangyueEducation.uploaderCloud.Utils.BaseUtils.ArrayUtilsJiang;
import com.xiangyueEducation.uploaderCloud.Utils.BaseUtils.PathUtils;
import com.xiangyueEducation.uploaderCloud.mapper.SelfCloudFileSpaceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author 86136
* @description 针对表【self_cloud_file_space】的数据库操作Service实现
* @createDate 2024-05-22 15:24:04
*/
@Service
@Slf4j
public class SelfCloudFileSpaceServiceImpl extends ServiceImpl<SelfCloudFileSpaceMapper, SelfCloudFileSpace>
    implements SelfCloudFileSpaceService{

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private SelfCloudFileSpaceMapper selfCloudFileSpaceMapper;

    @Autowired
    private PathUtils pathUtils;


    /**
     * 分页获取文件名
     * @param token
     * @param currentPage
     * @param order
     * @return
     */
    @Override
    public Result getFileNameSpace(String token,Integer currentPage,String order,Integer pageSize) {
        Page<String> page=null;
        if (pageSize==null){
            page = new Page<>(currentPage,8);
        }else {
            page = new Page<>(currentPage,pageSize);
        }
        String userId = jwtHelper.getUserId(token);
        //默认排序
        if (order==null){
            String[] fileNames=selfCloudFileSpaceMapper.selectFileNameByUserId(userId,page);
            if (fileNames.length==0){
                return Result.ok("没有任何文件");
            }
            return Result.ok(fileNames);
        }
        //降序排列(最近在前)
        else if (order.equals("desc")){
            String[] fileNames=selfCloudFileSpaceMapper.selectFileNameByUserIdDESC(userId,page);
            if (fileNames.length==0){
                return Result.ok("没有任何文件");
            }
            return Result.ok(fileNames);
        }
        //升序排列(最晚在前)
        else if (order.equals("asc")){
            String[] fileNames=selfCloudFileSpaceMapper.selectFileNameByFileIdASC(userId,page);
            if (fileNames.length==0){
                return Result.ok("没有任何文件");
            }
            return Result.ok(fileNames);
        }


        return Result.build("查询文件失败", ResultCodeEnum.QUERY_FAIL);
    }



    /**
     * 获取可以有几页
     * @param token
     * @param pageSize
     * @return
     */
    @Override
    public Result getPageNumSpace(String token,Integer pageSize) {
        String uuid = jwtHelper.getUserId(token);
        //默认8个一页
        if (pageSize==null){
            Integer pageNum=selfCloudFileSpaceMapper.getPageNumById(uuid,8);
            return Result.ok(pageNum);
        }
        //自定义一页几个
        else{
            Integer pageNum=selfCloudFileSpaceMapper.getPageNumById(uuid,pageSize);
            return Result.ok(pageNum);
        }

    }


    /**
     * 获取文件详细
     * @param token
     * @param fileName
     * @return
     */
    @Override
    public Result getFileDetail(String token, String fileName) {
        String uuid = jwtHelper.getUserId(token);
        Map<String,String> data=selfCloudFileSpaceMapper.getFileDetailByUuidAndFileName(uuid,fileName);
        String filePathBeta = data.get("file_path");
        String[] parts = filePathBeta.split("/");
        StringBuilder filePath = new StringBuilder();
        for( int i=1;i<parts.length;i++){
            filePath.append("/").append(parts[i]);
        }
        String filePathFinal = URLEnum.HOST_URL_NO_SLASH.getUrl()+ filePath.toString();
        data.put("file_path",filePathFinal);
        log.info("用户(UUID)" + uuid + "请求了一次位于个人空间的"+fileName+"文件");
        return Result.ok(data);
    }

    @Override
    public Result getSelfSpaceSize(String token) {
        String uuid = jwtHelper.getUserId(token);
        Integer size=selfCloudFileSpaceMapper.getAllSizeByUserId(uuid);
        Map data = new HashMap<>();
        data.put("size",size);
        return Result.ok(data);
    }

    @Override
    public Result getAllFIleName(String token) {
        String uuid = jwtHelper.getUserId(token);
        String[] data = selfCloudFileSpaceMapper.getAllFileNameByUuid(uuid);
        return Result.ok(data);
    }

    @Override
    public Result getAllFIleNameAdmin(String uuid) {
        String[] data = selfCloudFileSpaceMapper.getAllFileNameByUuid(uuid);
        return Result.ok(data);
    }


}




