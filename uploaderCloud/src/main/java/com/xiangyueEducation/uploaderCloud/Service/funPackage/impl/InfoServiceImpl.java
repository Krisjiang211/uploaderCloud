package com.xiangyueEducation.uploaderCloud.Service.funPackage.impl;


import com.xiangyueEducation.uploaderCloud.POJO.Publish;
import com.xiangyueEducation.uploaderCloud.Service.funPackage.InfoService;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.mapper.PublishMapper;
import com.xiangyueEducation.uploaderCloud.mapper.TaskContentMapper;
import com.xiangyueEducation.uploaderCloud.mapper.TaskFileGroupMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InfoServiceImpl implements InfoService {

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private PublishMapper publishMapper;

    @Autowired
    private TaskContentMapper taskContentMapper;

    @Autowired
    private TaskFileGroupMapper taskFileGroupMapper;

}
