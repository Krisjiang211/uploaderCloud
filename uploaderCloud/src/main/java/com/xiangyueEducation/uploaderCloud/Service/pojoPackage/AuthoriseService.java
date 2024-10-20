package com.xiangyueEducation.uploaderCloud.Service.pojoPackage;

import com.xiangyueEducation.uploaderCloud.POJO.Authorise;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Random;

/**
* @author 86136
* @description 针对表【authorise】的数据库操作Service
* @createDate 2024-06-03 23:54:17
*/
public interface AuthoriseService extends IService<Authorise> {

    Result createAuth(String requesterId, Integer publishId, String fileName);

    Result handleAuth(Integer authoriseId,Integer publishId,String uuid,Integer is_authorised);

    Result getAllAuth(String uuid,Integer currentPage,String order);

    Result getOneAuth(Integer authoriseId);

    ResponseEntity<Resource> downloadAuth(Integer authoriseId,String secretKey);
}
