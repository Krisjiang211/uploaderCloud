package com.xiangyueEducation.uploaderCloud.Service.funPackage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


public interface SearchService {

    Result searchSelfSpaceFileName(String token, String keyWord, Integer currentPage,Integer pageSize);


    Result searchSelfSpaceFileNum(String token,String keyWord);
}
