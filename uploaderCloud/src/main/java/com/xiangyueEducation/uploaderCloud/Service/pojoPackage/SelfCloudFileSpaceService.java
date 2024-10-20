package com.xiangyueEducation.uploaderCloud.Service.pojoPackage;

import com.xiangyueEducation.uploaderCloud.POJO.SelfCloudFileSpace;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
* @author 86136
* @description 针对表【self_cloud_file_space】的数据库操作Service
* @createDate 2024-05-22 15:24:04
*/
public interface SelfCloudFileSpaceService extends IService<SelfCloudFileSpace> {



    Result getFileNameSpace(String token,Integer currentPage,String order,Integer pageSize);


    Result getPageNumSpace(String token,Integer pageSize);

    Result getFileDetail(String token, String fileName);

    Result getSelfSpaceSize(String token);

    Result getAllFIleName(String token);

    Result getAllFIleNameAdmin(String uuid);

}
