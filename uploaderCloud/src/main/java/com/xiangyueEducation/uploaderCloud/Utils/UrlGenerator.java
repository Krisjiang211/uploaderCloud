package com.xiangyueEducation.uploaderCloud.Utils;

import com.xiangyueEducation.uploaderCloud.POJO.Publish;
import com.xiangyueEducation.uploaderCloud.POJO.TaskContent;
import com.xiangyueEducation.uploaderCloud.mapper.PublishMapper;
import com.xiangyueEducation.uploaderCloud.mapper.TaskContentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlGenerator {
    @Value("${resource.key.download}")
    private String downloadKey;

    @Autowired
    private PublishMapper publishMapper;

    @Autowired
    private TaskContentMapper taskContentMapper;

//    public String firstLevelDownloadUrl(Publish publish,String fileName){
//        TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
//
//        String url= URLEnum.HOST_URL.getUrl()+
//                URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
//                publish.getUuid()+
//                URLEnum.FIRST_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
//                fileName;
//        return url;
//    }



}
