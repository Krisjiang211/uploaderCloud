package com.xiangyueEducation.uploaderCloud.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.POJO.SelfCloudFileSpace;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.Map;

/**
* @author 86136
* @description 针对表【self_cloud_file_space】的数据库操作Mapper
* @createDate 2024-05-22 15:24:04
* @Entity com.xiangyueEducation.uploaderCloud.POJO.SelfCloudFileSpace
*/
public interface SelfCloudFileSpaceMapper extends BaseMapper<SelfCloudFileSpace> {

    String[] selectFileNameByUserId(String UUID,Page<String> page);

    String[] selectFileNameByUserIdDESC(String UUID,Page<String> page);


    String[] selectFileNameByFileIdASC(String UUID,Page<String> page);

    Integer getPageNumById(String uuid,Integer pageSize);

    HashMap<String, String> getFileDetailByUuidAndFileName(@Param("uuid") String uuid, @Param("fileName") String fileName);


    String[] getALLFileNameById(String uuid);

    Integer getAllSizeByUserId(String uuid);


    String[] searchFileNameByKeyWord(String uuid,String keyWord,Page<String> page);

    Integer getSearchPageNumById(String uuid,String keyWord,Integer pageSize);


    //获取 个人云空间 所有文件名字(不分页)
    String[] getAllFileNameByUuid(String uuid);

    SelfCloudFileSpace getOneByFileNameAndUuid(String uuid,String fileName);


    //删除数据库中的存在
    Integer deleteByFileNameAndUuid(String uuid,String fileName);


}




