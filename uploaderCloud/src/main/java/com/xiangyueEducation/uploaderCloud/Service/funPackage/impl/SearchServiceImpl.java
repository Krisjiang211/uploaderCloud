package com.xiangyueEducation.uploaderCloud.Service.funPackage.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.POJO.SelfCloudFileSpace;
import com.xiangyueEducation.uploaderCloud.Service.funPackage.SearchService;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.mapper.SelfCloudFileSpaceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {


    @Autowired
    private SelfCloudFileSpaceMapper selfCloudFileSpaceMapper;

    @Autowired
    private JwtHelper jwtHelper;


    @Override
    public Result searchSelfSpaceFileName(String token, String keyWord,Integer currentPage,Integer pageSize) {
        Page<String> page= null;
        if (pageSize==null){
            //不输入pageSize默认显示8个
            page = new Page<>(currentPage, 8);
        }else {
            page = new Page<>(currentPage, pageSize);
        }
        String uuid = jwtHelper.getUserId(token);
        String[] fileNames = selfCloudFileSpaceMapper.searchFileNameByKeyWord(uuid, keyWord, page);

        if (fileNames.length==0){
            return Result.ok("没有找到相关文件");
        }
        return Result.ok(fileNames);
    }

    @Override
    public Result searchSelfSpaceFileNum(String token, String keyWord) {
        String uuid = jwtHelper.getUserId(token);
        Integer pageSize=8;
        Integer num = selfCloudFileSpaceMapper.getSearchPageNumById(uuid, keyWord, pageSize);

        return Result.ok(num);
    }


    /**
     * 获取搜索的页数
     */

}
