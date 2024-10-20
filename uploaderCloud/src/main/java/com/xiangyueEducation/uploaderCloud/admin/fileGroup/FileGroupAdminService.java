package com.xiangyueEducation.uploaderCloud.admin.fileGroup;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.POJO.*;
import com.xiangyueEducation.uploaderCloud.Utils.BaseUtils.ListUtilJiang;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.ResultCodeEnum;
import com.xiangyueEducation.uploaderCloud.Utils.URLEnum;
import com.xiangyueEducation.uploaderCloud.admin.VO.FileGroupVO;
import com.xiangyueEducation.uploaderCloud.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FileGroupAdminService {

    @Autowired
    private PublishMapper publishMapper;
    @Autowired
    private TaskContentMapper taskContentMapper;
    @Autowired
    private TaskFileGroupMapper taskFileGroupMapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private MainAndViceTypeMapper mainAndViceTypeMapper;
    @Autowired
    private MainTypeMapper mainTypeMapper;
    @Autowired
    private ViceTypeMapper viceTypeMapper;
    @Autowired
    private FileGroupCategoryMapper fileGroupCategoryMapper;
    @Autowired
    private FileCategoryMapper fileCategoryMapper;

    @Value("${resource.key.download}")
    private String downloadKey;

    public Result getFileInfo(String groupId){
        QueryWrapper<TaskFileGroup> wrapper = new QueryWrapper<>();
        wrapper.eq("group_id",groupId);
        List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapper);
        ArrayList<String> data = new ArrayList<>();
        for (TaskFileGroup taskFileGroup : taskFileGroups) {
            data.add(taskFileGroup.getFileName());
        }
        return Result.ok(data);
    }


    public Result editDelFile(String groupId,List<String> fileNames){
        QueryWrapper<TaskFileGroup> wrapper = new QueryWrapper<>();
        wrapper.eq("group_id",groupId);
        List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapper);
        for (TaskFileGroup taskFileGroup : taskFileGroups) {
            for (String fileName : fileNames) {
                if (taskFileGroup.getFileName().equals(fileName)){
                    //清除数据库操作
                    taskFileGroupMapper.deleteById(taskFileGroup.getFileGroupId());
                    //物理清除操作

                }
            }
        }
        return Result.ok("删除完成");
    }



    public Result getFileGroupFileCate(Page<Publish> page,Integer fileCateGoryId){
        Map data = new HashMap<>();
        ArrayList<Map> mainDataList = new ArrayList<>();
        QueryWrapper<TaskFileGroup> wrapperTFG = new QueryWrapper<>();
        wrapperTFG.eq("file_category_id",fileCateGoryId);
        List<TaskFileGroup> taskFileGroupss = taskFileGroupMapper.selectList(wrapperTFG);
        ArrayList<String> groupIdList = new ArrayList<>();
        for (TaskFileGroup taskFileGroup : taskFileGroupss) {
                groupIdList.add(taskFileGroup.getGroupId());
        }
        List<String> groupIdListFinal = ListUtilJiang.removeDuplicates(groupIdList);
        QueryWrapper<TaskContent> wrapperTC = new QueryWrapper<>();
        wrapperTC.in("file_group_id",groupIdListFinal);
        List<TaskContent> taskContentSs = taskContentMapper.selectList(wrapperTC);
        ArrayList<Integer> taskContentIdList = new ArrayList<>();
        for (TaskContent taskContent : taskContentSs) {
                taskContentIdList.add(taskContent.getTaskContentId());
        }
        QueryWrapper<Publish> wrapperPub = new QueryWrapper<>();
        wrapperPub.in("task_content_id",taskContentIdList)
                .orderByDesc("create_time");
        Page<Publish> publishPage = publishMapper.selectPage(page, wrapperPub);
        for (Publish publish : publishPage.getRecords()) {
            Map mainDataListItem = new HashMap<>();
            //1.对每个publish表的操作
            mainDataListItem.put("publishId", publish.getPublishId());
            mainDataListItem.put("uuid",publish.getUuid());
            mainDataListItem.put("createTime",publish.getCreateTime());
            mainDataListItem.put("isDelete",publish.getIsDelete());
            //   放入departmentId和departmentName
            mainDataListItem.put("departmentId",publish.getDepartmentId());
            mainDataListItem.put("departmentName",departmentMapper.selectById(publish.getDepartmentId()).getDepartmentName());
            //  放入MainAndViceType 的 id 和name
            mainDataListItem.put("MVTypeId",publish.getMainAndViceTypeId());
            MainAndViceType mainAndViceType = mainAndViceTypeMapper.selectById(publish.getMainAndViceTypeId());
            mainDataListItem.put("mainTypeName",mainTypeMapper.selectById(mainAndViceType.getMainTypeId()).getName());
            mainDataListItem.put("viceTypeName",viceTypeMapper.selectById(mainAndViceType.getViceTypeId()).getName());
            mainDataListItem.put("MVTypeName",mainTypeMapper.selectById(mainAndViceType.getMainTypeId()).getName()+"  :  "+viceTypeMapper.selectById(mainAndViceType.getViceTypeId()).getName());
            //放入fileGroupCategory 的 id 和 name
            mainDataListItem.put("fileGroupCategoryId",publish.getFileGroupCategoryId());
            mainDataListItem.put("fileGroupCategoryName",fileGroupCategoryMapper.selectById(publish.getFileGroupCategoryId()).getCategoryName());
            //2.开始对taskContent操作
            TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
            mainDataListItem.put("taskContentId",taskContent.getTaskContentId());
            mainDataListItem.put("title",taskContent.getTitle());
            mainDataListItem.put("description",taskContent.getDescription());
            String imgName = taskContent.getPreviewImgPath().split("/")[taskContent.getPreviewImgPath().split("/").length - 1];
            String imgURL= URLEnum.HOST_URL.getUrl()+"img/"+imgName;
            mainDataListItem.put("previewImgUrl",imgURL);
            mainDataListItem.put("groupId",taskContent.getFileGroupId());
            //3.开始对taskFileGroup操作
            QueryWrapper<TaskFileGroup> wrapper = new QueryWrapper<>();
            wrapper.eq("group_id",taskContent.getFileGroupId());
            List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapper);

            ArrayList fileInfoList=new ArrayList();
            for (TaskFileGroup taskFileGroup : taskFileGroups) {
                Map fileInfoItem = new HashMap<>();
                //3.1 开始对每一个fileInfo进行操作
                fileInfoItem.put("taskFileGroupId",taskFileGroup.getFileGroupId());
                //      放入文件种类 id 和 名字
                fileInfoItem.put("fileCategoryId",taskFileGroup.getFileCategoryId());
                fileInfoItem.put("fileCategoryName",fileCategoryMapper.selectById(taskFileGroup.getFileCategoryId()).getCategoryName());

                fileInfoItem.put("fileLevel",taskFileGroup.getFileLevel());
                fileInfoItem.put("fileName",taskFileGroup.getFileName());
                fileInfoItem.put("fileSize",taskFileGroup.getFileSize());
                //放置URL
                if (taskFileGroup.getFileLevel() == 1){
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            publish.getUuid()+
                            URLEnum.FIRST_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            publish.getUuid()+"/preview"+
                            URLEnum.FIRST_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                } else if (taskFileGroup.getFileLevel() == 2) {
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+
                            URLEnum.SECOND_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+"/preview"+
                            URLEnum.SECOND_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                } else if (taskFileGroup.getFileLevel() == 3) {
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+
                            URLEnum.THIRD_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+"/preview"+
                            URLEnum.THIRD_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                }
                fileInfoList.add(fileInfoItem);

            }
            mainDataListItem.put("fileInfoList",fileInfoList);
            mainDataList.add(mainDataListItem);
        }

        data.put("maxPage", page.getPages());
        data.put("mainData",mainDataList);
        return Result.ok(data);
    }


    public Result getFileGroupVice(Page<Publish> page,Integer viceTypeId){
        Map data = new HashMap<>();
        ArrayList<Map> mainDataList = new ArrayList<>();
        //中间的选出mainAndViceType的wrapper
        QueryWrapper<MainAndViceType> wrapperMVT = new QueryWrapper<>();
        wrapperMVT.eq("vice_type_id",viceTypeId);
        List<MainAndViceType> mainAndViceTypes = mainAndViceTypeMapper.selectList(wrapperMVT);
        List<Integer> mVTIdList = new ArrayList<>();
        for (MainAndViceType mainAndViceType : mainAndViceTypes) {
            mVTIdList.add(mainAndViceType.getMainAndViceTypeId());
        }
        QueryWrapper<Publish> wrapperPub = new QueryWrapper<>();
        wrapperPub.in("main_and_vice_type_id",mVTIdList)
                .orderByDesc("create_time");
        Page<Publish> publishPage = publishMapper.selectPage(page, wrapperPub);
        for (Publish publish : publishPage.getRecords()) {
            Map mainDataListItem = new HashMap<>();
            //1.对每个publish表的操作
            mainDataListItem.put("publishId", publish.getPublishId());
            mainDataListItem.put("uuid",publish.getUuid());
            mainDataListItem.put("createTime",publish.getCreateTime());
            mainDataListItem.put("isDelete",publish.getIsDelete());
            //   放入departmentId和departmentName
            mainDataListItem.put("departmentId",publish.getDepartmentId());
            mainDataListItem.put("departmentName",departmentMapper.selectById(publish.getDepartmentId()).getDepartmentName());
            //  放入MainAndViceType 的 id 和name
            mainDataListItem.put("MVTypeId",publish.getMainAndViceTypeId());
            MainAndViceType mainAndViceType = mainAndViceTypeMapper.selectById(publish.getMainAndViceTypeId());
            mainDataListItem.put("mainTypeName",mainTypeMapper.selectById(mainAndViceType.getMainTypeId()).getName());
            mainDataListItem.put("viceTypeName",viceTypeMapper.selectById(mainAndViceType.getViceTypeId()).getName());
            mainDataListItem.put("MVTypeName",mainTypeMapper.selectById(mainAndViceType.getMainTypeId()).getName()+"  :  "+viceTypeMapper.selectById(mainAndViceType.getViceTypeId()).getName());
            //放入fileGroupCategory 的 id 和 name
            mainDataListItem.put("fileGroupCategoryId",publish.getFileGroupCategoryId());
            mainDataListItem.put("fileGroupCategoryName",fileGroupCategoryMapper.selectById(publish.getFileGroupCategoryId()).getCategoryName());
            //2.开始对taskContent操作
            TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
            mainDataListItem.put("taskContentId",taskContent.getTaskContentId());
            mainDataListItem.put("title",taskContent.getTitle());
            mainDataListItem.put("description",taskContent.getDescription());
            String imgName = taskContent.getPreviewImgPath().split("/")[taskContent.getPreviewImgPath().split("/").length - 1];
            String imgURL= URLEnum.HOST_URL.getUrl()+"img/"+imgName;
            mainDataListItem.put("previewImgUrl",imgURL);
            mainDataListItem.put("groupId",taskContent.getFileGroupId());
            //3.开始对taskFileGroup操作
            QueryWrapper<TaskFileGroup> wrapper = new QueryWrapper<>();
            wrapper.eq("group_id",taskContent.getFileGroupId());
            List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapper);

            ArrayList fileInfoList=new ArrayList();
            for (TaskFileGroup taskFileGroup : taskFileGroups) {
                Map fileInfoItem = new HashMap<>();
                //3.1 开始对每一个fileInfo进行操作
                fileInfoItem.put("taskFileGroupId",taskFileGroup.getFileGroupId());
                //      放入文件种类 id 和 名字
                fileInfoItem.put("fileCategoryId",taskFileGroup.getFileCategoryId());
                fileInfoItem.put("fileCategoryName",fileCategoryMapper.selectById(taskFileGroup.getFileCategoryId()).getCategoryName());

                fileInfoItem.put("fileLevel",taskFileGroup.getFileLevel());
                fileInfoItem.put("fileName",taskFileGroup.getFileName());
                fileInfoItem.put("fileSize",taskFileGroup.getFileSize());
                //放置URL
                if (taskFileGroup.getFileLevel() == 1){
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            publish.getUuid()+
                            URLEnum.FIRST_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            publish.getUuid()+"/preview"+
                            URLEnum.FIRST_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                } else if (taskFileGroup.getFileLevel() == 2) {
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+
                            URLEnum.SECOND_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+"/preview"+
                            URLEnum.SECOND_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                } else if (taskFileGroup.getFileLevel() == 3) {
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+
                            URLEnum.THIRD_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+"/preview"+
                            URLEnum.THIRD_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                }
                fileInfoList.add(fileInfoItem);

            }
            mainDataListItem.put("fileInfoList",fileInfoList);
            mainDataList.add(mainDataListItem);
        }

        data.put("maxPage", page.getPages());
        data.put("mainData",mainDataList);
        return Result.ok(data);
    }

    public Result getFileGroupMain(Page<Publish> page,Integer mainTypeId){
        Map data = new HashMap<>();
        ArrayList<Map> mainDataList = new ArrayList<>();
        //中间的选出mainAndViceType的wrapper
        QueryWrapper<MainAndViceType> wrapperMVT = new QueryWrapper<>();
        wrapperMVT.eq("main_type_id",mainTypeId);
        List<MainAndViceType> mainAndViceTypes = mainAndViceTypeMapper.selectList(wrapperMVT);
        List<Integer> mVTIdList = new ArrayList<>();
        for (MainAndViceType mainAndViceType : mainAndViceTypes) {
              mVTIdList.add(mainAndViceType.getMainAndViceTypeId());
        }
        QueryWrapper<Publish> wrapperPub = new QueryWrapper<>();
        wrapperPub.in("main_and_vice_type_id",mVTIdList)
                .orderByDesc("create_time");
        Page<Publish> publishPage = publishMapper.selectPage(page, wrapperPub);
        for (Publish publish : publishPage.getRecords()) {
            Map mainDataListItem = new HashMap<>();
            //1.对每个publish表的操作
            mainDataListItem.put("publishId", publish.getPublishId());
            mainDataListItem.put("uuid",publish.getUuid());
            mainDataListItem.put("createTime",publish.getCreateTime());
            mainDataListItem.put("isDelete",publish.getIsDelete());
            //   放入departmentId和departmentName
            mainDataListItem.put("departmentId",publish.getDepartmentId());
            mainDataListItem.put("departmentName",departmentMapper.selectById(publish.getDepartmentId()).getDepartmentName());
            //  放入MainAndViceType 的 id 和name
            mainDataListItem.put("MVTypeId",publish.getMainAndViceTypeId());
            MainAndViceType mainAndViceType = mainAndViceTypeMapper.selectById(publish.getMainAndViceTypeId());
            mainDataListItem.put("mainTypeName",mainTypeMapper.selectById(mainAndViceType.getMainTypeId()).getName());
            mainDataListItem.put("viceTypeName",viceTypeMapper.selectById(mainAndViceType.getViceTypeId()).getName());
            mainDataListItem.put("MVTypeName",mainTypeMapper.selectById(mainAndViceType.getMainTypeId()).getName()+"  :  "+viceTypeMapper.selectById(mainAndViceType.getViceTypeId()).getName());
            //放入fileGroupCategory 的 id 和 name
            mainDataListItem.put("fileGroupCategoryId",publish.getFileGroupCategoryId());
            mainDataListItem.put("fileGroupCategoryName",fileGroupCategoryMapper.selectById(publish.getFileGroupCategoryId()).getCategoryName());
            //2.开始对taskContent操作
            TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
            mainDataListItem.put("taskContentId",taskContent.getTaskContentId());
            mainDataListItem.put("title",taskContent.getTitle());
            mainDataListItem.put("description",taskContent.getDescription());
            String imgName = taskContent.getPreviewImgPath().split("/")[taskContent.getPreviewImgPath().split("/").length - 1];
            String imgURL= URLEnum.HOST_URL.getUrl()+"img/"+imgName;
            mainDataListItem.put("previewImgUrl",imgURL);
            mainDataListItem.put("groupId",taskContent.getFileGroupId());
            //3.开始对taskFileGroup操作
            QueryWrapper<TaskFileGroup> wrapper = new QueryWrapper<>();
            wrapper.eq("group_id",taskContent.getFileGroupId());
            List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapper);

            ArrayList fileInfoList=new ArrayList();
            for (TaskFileGroup taskFileGroup : taskFileGroups) {
                Map fileInfoItem = new HashMap<>();
                //3.1 开始对每一个fileInfo进行操作
                fileInfoItem.put("taskFileGroupId",taskFileGroup.getFileGroupId());
                //      放入文件种类 id 和 名字
                fileInfoItem.put("fileCategoryId",taskFileGroup.getFileCategoryId());
                fileInfoItem.put("fileCategoryName",fileCategoryMapper.selectById(taskFileGroup.getFileCategoryId()).getCategoryName());

                fileInfoItem.put("fileLevel",taskFileGroup.getFileLevel());
                fileInfoItem.put("fileName",taskFileGroup.getFileName());
                fileInfoItem.put("fileSize",taskFileGroup.getFileSize());
                //放置URL
                if (taskFileGroup.getFileLevel() == 1){
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            publish.getUuid()+
                            URLEnum.FIRST_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            publish.getUuid()+"/preview"+
                            URLEnum.FIRST_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                } else if (taskFileGroup.getFileLevel() == 2) {
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+
                            URLEnum.SECOND_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+"/preview"+
                            URLEnum.SECOND_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                } else if (taskFileGroup.getFileLevel() == 3) {
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+
                            URLEnum.THIRD_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+"/preview"+
                            URLEnum.THIRD_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                }
                fileInfoList.add(fileInfoItem);

            }
            mainDataListItem.put("fileInfoList",fileInfoList);
            mainDataList.add(mainDataListItem);
        }

        data.put("maxPage", page.getPages());
        data.put("mainData",mainDataList);
        return Result.ok(data);

    }
    public Result getFileGroupCate(Page<Publish>page,Integer fileGroupCategoryId){
        Map data = new HashMap<>();
        ArrayList<Map> mainDataList = new ArrayList<>();
        QueryWrapper<Publish> wrapperFileCate = new QueryWrapper<>();
        wrapperFileCate.eq("file_group_category_id",fileGroupCategoryId)
                .orderByDesc("create_time");

        Page<Publish> publishPage = publishMapper.selectPage(page, wrapperFileCate);
        for (Publish publish : publishPage.getRecords()) {
            Map mainDataListItem = new HashMap<>();
            //1.对每个publish表的操作
            mainDataListItem.put("publishId", publish.getPublishId());
            mainDataListItem.put("uuid",publish.getUuid());
            mainDataListItem.put("createTime",publish.getCreateTime());
            mainDataListItem.put("isDelete",publish.getIsDelete());
            //   放入departmentId和departmentName
            mainDataListItem.put("departmentId",publish.getDepartmentId());
            mainDataListItem.put("departmentName",departmentMapper.selectById(publish.getDepartmentId()).getDepartmentName());
            //  放入MainAndViceType 的 id 和name
            mainDataListItem.put("MVTypeId",publish.getMainAndViceTypeId());
            MainAndViceType mainAndViceType = mainAndViceTypeMapper.selectById(publish.getMainAndViceTypeId());
            mainDataListItem.put("mainTypeName",mainTypeMapper.selectById(mainAndViceType.getMainTypeId()).getName());
            mainDataListItem.put("viceTypeName",viceTypeMapper.selectById(mainAndViceType.getViceTypeId()).getName());
            mainDataListItem.put("MVTypeName",mainTypeMapper.selectById(mainAndViceType.getMainTypeId()).getName()+"  :  "+viceTypeMapper.selectById(mainAndViceType.getViceTypeId()).getName());
            //放入fileGroupCategory 的 id 和 name
            mainDataListItem.put("fileGroupCategoryId",publish.getFileGroupCategoryId());
            mainDataListItem.put("fileGroupCategoryName",fileGroupCategoryMapper.selectById(publish.getFileGroupCategoryId()).getCategoryName());
            //2.开始对taskContent操作
            TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
            mainDataListItem.put("taskContentId",taskContent.getTaskContentId());
            mainDataListItem.put("title",taskContent.getTitle());
            mainDataListItem.put("description",taskContent.getDescription());
            String imgName = taskContent.getPreviewImgPath().split("/")[taskContent.getPreviewImgPath().split("/").length - 1];
            String imgURL= URLEnum.HOST_URL.getUrl()+"img/"+imgName;
            mainDataListItem.put("previewImgUrl",imgURL);
            mainDataListItem.put("groupId",taskContent.getFileGroupId());
            //3.开始对taskFileGroup操作
            QueryWrapper<TaskFileGroup> wrapper = new QueryWrapper<>();
            wrapper.eq("group_id",taskContent.getFileGroupId());
            List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapper);

            ArrayList fileInfoList=new ArrayList();
            for (TaskFileGroup taskFileGroup : taskFileGroups) {
                Map fileInfoItem = new HashMap<>();
                //3.1 开始对每一个fileInfo进行操作
                fileInfoItem.put("taskFileGroupId",taskFileGroup.getFileGroupId());
                //      放入文件种类 id 和 名字
                fileInfoItem.put("fileCategoryId",taskFileGroup.getFileCategoryId());
                fileInfoItem.put("fileCategoryName",fileCategoryMapper.selectById(taskFileGroup.getFileCategoryId()).getCategoryName());

                fileInfoItem.put("fileLevel",taskFileGroup.getFileLevel());
                fileInfoItem.put("fileName",taskFileGroup.getFileName());
                fileInfoItem.put("fileSize",taskFileGroup.getFileSize());
                //放置URL
                if (taskFileGroup.getFileLevel() == 1){
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            publish.getUuid()+
                            URLEnum.FIRST_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            publish.getUuid()+"/preview"+
                            URLEnum.FIRST_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                } else if (taskFileGroup.getFileLevel() == 2) {
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+
                            URLEnum.SECOND_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+"/preview"+
                            URLEnum.SECOND_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                } else if (taskFileGroup.getFileLevel() == 3) {
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+
                            URLEnum.THIRD_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+"/preview"+
                            URLEnum.THIRD_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                }
                fileInfoList.add(fileInfoItem);

            }
            mainDataListItem.put("fileInfoList",fileInfoList);
            mainDataList.add(mainDataListItem);
        }

        data.put("maxPage", page.getPages());
        data.put("mainData",mainDataList);
        return Result.ok(data);

    }

    public Result getFileGroup(Page<Publish> page){
        Map data = new HashMap<>();
        ArrayList<Map> mainDataList = new ArrayList<>();
        QueryWrapper<Publish> wrapper1 = new QueryWrapper<>();
        wrapper1.orderByDesc("create_time");
        Page<Publish> publishPage = publishMapper.selectPage(page, wrapper1);
        for (Publish publish : publishPage.getRecords()) {
            Map mainDataListItem = new HashMap<>();
            //1.对每个publish表的操作
            mainDataListItem.put("publishId", publish.getPublishId());
            mainDataListItem.put("uuid",publish.getUuid());
            mainDataListItem.put("createTime",publish.getCreateTime());
            mainDataListItem.put("isDelete",publish.getIsDelete());
            //   放入departmentId和departmentName
            mainDataListItem.put("departmentId",publish.getDepartmentId());
            mainDataListItem.put("departmentName",departmentMapper.selectById(publish.getDepartmentId()).getDepartmentName());
            //  放入MainAndViceType 的 id 和name
            mainDataListItem.put("MVTypeId",publish.getMainAndViceTypeId());
            MainAndViceType mainAndViceType = mainAndViceTypeMapper.selectById(publish.getMainAndViceTypeId());
            mainDataListItem.put("mainTypeName",mainTypeMapper.selectById(mainAndViceType.getMainTypeId()).getName());
            mainDataListItem.put("viceTypeName",viceTypeMapper.selectById(mainAndViceType.getViceTypeId()).getName());
            mainDataListItem.put("MVTypeName",mainTypeMapper.selectById(mainAndViceType.getMainTypeId()).getName()+"  :  "+viceTypeMapper.selectById(mainAndViceType.getViceTypeId()).getName());
            //放入fileGroupCategory 的 id 和 name
            mainDataListItem.put("fileGroupCategoryId",publish.getFileGroupCategoryId());
            mainDataListItem.put("fileGroupCategoryName",fileGroupCategoryMapper.selectById(publish.getFileGroupCategoryId()).getCategoryName());
            //2.开始对taskContent操作
            TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
            mainDataListItem.put("taskContentId",taskContent.getTaskContentId());
            mainDataListItem.put("title",taskContent.getTitle());
            mainDataListItem.put("description",taskContent.getDescription());
            String imgName = taskContent.getPreviewImgPath().split("/")[taskContent.getPreviewImgPath().split("/").length - 1];
            String imgURL= URLEnum.HOST_URL.getUrl()+"img/"+imgName;
            mainDataListItem.put("previewImgUrl",imgURL);
            mainDataListItem.put("groupId",taskContent.getFileGroupId());
            //3.开始对taskFileGroup操作
            QueryWrapper<TaskFileGroup> wrapper = new QueryWrapper<>();
            wrapper.eq("group_id",taskContent.getFileGroupId());
            List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapper);

            ArrayList fileInfoList=new ArrayList();
            for (TaskFileGroup taskFileGroup : taskFileGroups) {
                Map fileInfoItem = new HashMap<>();
                //3.1 开始对每一个fileInfo进行操作
                fileInfoItem.put("taskFileGroupId",taskFileGroup.getFileGroupId());
                //      放入文件种类 id 和 名字
                fileInfoItem.put("fileCategoryId",taskFileGroup.getFileCategoryId());
                fileInfoItem.put("fileCategoryName",fileCategoryMapper.selectById(taskFileGroup.getFileCategoryId()).getCategoryName());

                fileInfoItem.put("fileLevel",taskFileGroup.getFileLevel());
                fileInfoItem.put("fileName",taskFileGroup.getFileName());
                fileInfoItem.put("fileSize",taskFileGroup.getFileSize());
                //放置URL
                if (taskFileGroup.getFileLevel() == 1){
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            publish.getUuid()+
                            URLEnum.FIRST_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            publish.getUuid()+"/preview"+
                            URLEnum.FIRST_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                } else if (taskFileGroup.getFileLevel() == 2) {
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+
                            URLEnum.SECOND_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+"/preview"+
                            URLEnum.SECOND_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                } else if (taskFileGroup.getFileLevel() == 3) {
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+
                            URLEnum.THIRD_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+"/preview"+
                            URLEnum.THIRD_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                }
                fileInfoList.add(fileInfoItem);

            }
            mainDataListItem.put("fileInfoList",fileInfoList);
            mainDataList.add(mainDataListItem);
        }

        data.put("maxPage", page.getPages());
        data.put("mainData",mainDataList);
        return Result.ok(data);
    }




    public Result getFileGroupSearch(Page<TaskContent> page,String searchValue){
        Map data = new HashMap<>();
        ArrayList<Map> mainDataList = new ArrayList<>();

        List<Publish> publishList=new ArrayList();
        List<TaskContent> records = page.getRecords();
        for (TaskContent taskContent : records) {
            QueryWrapper<Publish> wrapper = new QueryWrapper<>();
            wrapper.eq("task_content_id",taskContent.getTaskContentId());
            Publish publish = publishMapper.selectOne(wrapper);
            publishList.add(publish);
        }
        for (Publish publish : publishList) {
            Map mainDataListItem = new HashMap<>();
            //1.对每个publish表的操作
            mainDataListItem.put("publishId", publish.getPublishId());
            mainDataListItem.put("uuid",publish.getUuid());
            mainDataListItem.put("createTime",publish.getCreateTime());
            mainDataListItem.put("isDelete",publish.getIsDelete());
            //   放入departmentId和departmentName
            mainDataListItem.put("departmentId",publish.getDepartmentId());
            mainDataListItem.put("departmentName",departmentMapper.selectById(publish.getDepartmentId()).getDepartmentName());
            //  放入MainAndViceType 的 id 和name
            mainDataListItem.put("MVTypeId",publish.getMainAndViceTypeId());
            MainAndViceType mainAndViceType = mainAndViceTypeMapper.selectById(publish.getMainAndViceTypeId());
            mainDataListItem.put("MVTypeName",mainTypeMapper.selectById(mainAndViceType.getMainTypeId()).getName()+"  :  "+viceTypeMapper.selectById(mainAndViceType.getViceTypeId()).getName());
            //放入fileGroupCategory 的 id 和 name
            mainDataListItem.put("fileGroupCategoryId",publish.getFileGroupCategoryId());
            mainDataListItem.put("fileGroupCategoryName",fileGroupCategoryMapper.selectById(publish.getFileGroupCategoryId()).getCategoryName());
            //2.开始对taskContent操作
            TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
            mainDataListItem.put("taskContentId",taskContent.getTaskContentId());
            mainDataListItem.put("title",taskContent.getTitle());
            mainDataListItem.put("description",taskContent.getDescription());
            String imgName = taskContent.getPreviewImgPath().split("/")[taskContent.getPreviewImgPath().split("/").length - 1];
            String imgURL= URLEnum.HOST_URL.getUrl()+"img/"+imgName;
            mainDataListItem.put("previewImgUrl",imgURL);
            mainDataListItem.put("groupId",taskContent.getFileGroupId());
            //3.开始对taskFileGroup操作
            QueryWrapper<TaskFileGroup> wrapper = new QueryWrapper<>();
            wrapper.eq("group_id",taskContent.getFileGroupId());
            List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapper);

            ArrayList fileInfoList=new ArrayList();
            for (TaskFileGroup taskFileGroup : taskFileGroups) {
                Map fileInfoItem = new HashMap<>();
                //3.1 开始对每一个fileInfo进行操作
                fileInfoItem.put("taskFileGroupId",taskFileGroup.getFileGroupId());
                //      放入文件种类 id 和 名字
                fileInfoItem.put("fileCategoryId",taskFileGroup.getFileCategoryId());
                fileInfoItem.put("fileCategoryName",fileCategoryMapper.selectById(taskFileGroup.getFileCategoryId()).getCategoryName());

                fileInfoItem.put("fileLevel",taskFileGroup.getFileLevel());
                fileInfoItem.put("fileName",taskFileGroup.getFileName());
                fileInfoItem.put("fileSize",taskFileGroup.getFileSize());
                //放置URL
                if (taskFileGroup.getFileLevel() == 1){
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            publish.getUuid()+
                            URLEnum.FIRST_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            publish.getUuid()+"/preview"+
                            URLEnum.FIRST_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                } else if (taskFileGroup.getFileLevel() == 2) {
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+
                            URLEnum.SECOND_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+"/preview"+
                            URLEnum.SECOND_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                } else if (taskFileGroup.getFileLevel() == 3) {
                    fileInfoItem.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+
                            URLEnum.THIRD_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                    fileInfoItem.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                            URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                            this.downloadKey+"/"+
                            publish.getUuid()+"/preview"+
                            URLEnum.THIRD_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                            taskFileGroup.getFileName());
                }
                fileInfoList.add(fileInfoItem);

            }
            mainDataListItem.put("fileInfoList",fileInfoList);
            mainDataList.add(mainDataListItem);
        }

        data.put("maxPage", page.getPages());
        data.put("mainData",mainDataList);
        return Result.ok(data);
    }




    public Result changeStatus(Integer publishId){
        Publish publish = publishMapper.selectById(publishId);
        if (publish.getIsDelete().equals(1)){
            publish.setIsDelete(0);
            publishMapper.updateById(publish);
            return Result.ok("状态修改为: 正常");
        } else if (publish.getIsDelete().equals(0)) {
            publish.setIsDelete(1);
            publishMapper.updateById(publish);
            return Result.ok("状态修改为: 停用");
        }
        return null;

    }

    public Result edit(FileGroupVO editInfo){
        System.out.println("editInfo = " + editInfo);
        TaskContent taskContent = taskContentMapper.selectById(editInfo.getTaskContentId());
        taskContent.setTitle(editInfo.getTitle());
        taskContent.setDescription(editInfo.getDescription());
        taskContentMapper.updateById(taskContent);
        Publish publish = publishMapper.selectById(editInfo.getPublishId());
        //选部门
        String departmentName=editInfo.getDepartmentName();
        QueryWrapper<Department> wrapper = new QueryWrapper<>();
        wrapper.eq("department_name",departmentName);
        Department department = departmentMapper.selectOne(wrapper);
        if (department == null){
            return Result.build("不存在的部门",ResultCodeEnum.QUERY_EMPTY);
        }
        publish.setDepartmentId(department.getDepartmentId());
        publishMapper.updateById(publish);
        return Result.ok("修改成功");

    }

}
