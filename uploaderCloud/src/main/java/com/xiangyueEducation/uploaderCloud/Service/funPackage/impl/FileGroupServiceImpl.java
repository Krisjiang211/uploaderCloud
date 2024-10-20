package com.xiangyueEducation.uploaderCloud.Service.funPackage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.POJO.*;
import com.xiangyueEducation.uploaderCloud.Service.funPackage.FileGroupService;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.DepartmentService;
import com.xiangyueEducation.uploaderCloud.Utils.*;
import com.xiangyueEducation.uploaderCloud.Utils.BaseUtils.*;
import com.xiangyueEducation.uploaderCloud.Utils.UUID;
import com.xiangyueEducation.uploaderCloud.mapper.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service
public class FileGroupServiceImpl implements FileGroupService {

    @Value("${resource.key.preview}")
    private String previewKey;

    @Value("${resource.key.download}")
    private String downloadKey;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PublishMapper publishMapper;

    @Autowired
    private FileGroupCategoryMapper fileGroupCategoryMapper;

    @Autowired
    private TaskContentMapper taskContentMapper;

    @Autowired
    private PathUtils pathUtils;

    @Autowired
    private TaskFileGroupMapper taskFileGroupMapper;

    @Autowired
    private FileCategoryMapper fileCategoryMapper;

    @Autowired
    private BaseUtilsJiang baseUtilsJiang;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private MainTypeMapper mainTypeMapper;

    @Autowired
    private ViceTypeMapper viceTypeMapper;

    @Autowired
    private MainAndViceTypeMapper mainAndViceTypeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthoriseMapper authoriseMapper;

    @Resource(name = "fileHandleThreadPool")
    private Executor fileHandleThreadPool;


    private Integer pageSize=8;


    @Override
    public Result deleteFiles(List<Integer> ids) {
        System.out.println("ids = " + ids);

        //物理删除
        ids.stream().forEach(id->{
            TaskFileGroup taskFileGroup = taskFileGroupMapper.selectById(id);
            Path filePath = pathUtils.getRealPath(taskFileGroup.getFilePath());
            fileHandleThreadPool.execute(()->{
                try {
                    System.out.println(filePath+"已提交删除");
                    Files.delete(filePath);
                    taskFileGroupMapper.deleteById(taskFileGroup);
                } catch (IOException e) {
                    System.out.println("文件: "+filePath+"删除失败,失败原因:\ne = " + e);
                    throw new RuntimeException(e);
                }
                System.out.println("文件: "+filePath+"删除成功");
            });
        });


        return Result.ok(true);
    }

    @Override
    public Result getOne(Integer publishId) {
        Publish publish = publishMapper.selectById(publishId);
        TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
        //先拿取文件组的文字数据
        Map<String,Object> infoData = new HashMap<>();
        infoData.put("title",taskContent.getTitle());
        infoData.put("description",taskContent.getDescription());
        infoData.put("publisherUuid",publish.getUuid());
        infoData.put("publisherName",userInfoMapper.selectById(publish.getUuid()).getRealName());
        infoData.put("publishTime",publish.getCreateTime());
        //在拿取文件数组
        ArrayList<Map<String,Object>> fileInfoList = new ArrayList<>();
        List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(new QueryWrapper<TaskFileGroup>().eq("group_id", taskContent.getFileGroupId()));
        taskFileGroups.stream()
                .forEach(taskFileGroup -> {
                    Map<String,Object> fileInfo = new HashMap<>();
                    fileInfo.put("fileName",taskFileGroup.getFileName());
                    fileInfo.put("fileLevel",taskFileGroup.getFileLevel());
                    fileInfo.put("fileSize",taskFileGroup.getFileSize());
                    fileInfo.put("fileType",fileCategoryMapper.selectById(taskFileGroup.getFileCategoryId()).getCategoryName());
                    fileInfo.put("fileGroupId",taskFileGroup.getFileGroupId());

                    //放置URL
                    if (taskFileGroup.getFileLevel() == 1){
                        fileInfo.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                                URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                                publish.getUuid()+
                                URLEnum.FIRST_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                                taskFileGroup.getFileName());
                        fileInfo.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                                URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                                publish.getUuid()+"/preview"+
                                URLEnum.FIRST_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                                taskFileGroup.getFileName());
                    } else if (taskFileGroup.getFileLevel() == 2) {
                        fileInfo.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                                URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                                this.downloadKey+"/"+
                                publish.getUuid()+
                                URLEnum.SECOND_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                                taskFileGroup.getFileName());
                        fileInfo.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                                URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                                this.downloadKey+"/"+
                                publish.getUuid()+"/preview"+
                                URLEnum.SECOND_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                                taskFileGroup.getFileName());
                    } else if (taskFileGroup.getFileLevel() == 3) {
                        fileInfo.put("fileUrl",URLEnum.HOST_URL.getUrl()+
                                URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                                this.downloadKey+"/"+
                                publish.getUuid()+
                                URLEnum.THIRD_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                                taskFileGroup.getFileName());
                        fileInfo.put("previewUrl",URLEnum.HOST_URL.getUrl()+
                                URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                                this.downloadKey+"/"+
                                publish.getUuid()+"/preview"+
                                URLEnum.THIRD_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                                taskFileGroup.getFileName());
                    }
                    fileInfoList.add(fileInfo);
                });
        Map<String,Object> res = new HashMap<>();
        res.put("infoData",infoData);
        res.put("fileInfoList",fileInfoList);
        return Result.ok(res);
    }

    /**
     *返回json示例:
     * {
     *     image:"",
     *     title:"",
     *     description:"",
     *     count:"",
     *     category:"",
     *     time:"",
     *     tags:[]//也就是文件类型
     * }
     *
     */

    //拉取时间顺序获取
    @Override
    public Result getTimeSequence(Integer departmentId,String order, Integer currentPage) {
        Department department = departmentService.getById(departmentId);
        //获取到department
        Page<Publish> page = new Page<>(currentPage,pageSize);
        Publish[] publishes = publishMapper.selectPublishByDepartmentId(departmentId, order, page);
        if (publishes.length==0){
            return Result.ok("没有任何东西");
        }
        List<Map> activityList = new LinkedList<>();
        Map res = new HashMap<>();
        for (Publish publish : publishes){
            Map activity_item = new HashMap<>();
            //获取publishId
            activity_item.put("publishId",publish.getPublishId());
            //获取time
            activity_item.put("time", DateUtils.formatDateToString(publish.getCreateTime()));
            //获取文件组类型,即category
            String categoryName = fileGroupCategoryMapper.selectById(publish.getFileGroupCategoryId()).getCategoryName();
            activity_item.put("category",categoryName);
            //进入task_content,准备获取更多
            TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
            //放入Description
            activity_item.put("description",taskContent.getDescription());
            //放入title
            activity_item.put("title",taskContent.getTitle());
            //放入imageURL
            String imgName = taskContent.getPreviewImgPath().split("/")[taskContent.getPreviewImgPath().split("/").length - 1];
            String imgURL= URLEnum.HOST_URL.getUrl()+"img/"+imgName;
            activity_item.put("image",imgURL);

            //进入task_file_group表中
            QueryWrapper<TaskFileGroup> wrapper = new QueryWrapper<>();
            wrapper.eq("group_id",taskContent.getFileGroupId());
            List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapper);
            //装入count
            activity_item.put("count",taskFileGroups.size());
            //装入tags
            List<String> tags = new LinkedList<>();
            for (TaskFileGroup item:taskFileGroups){
                tags.add(fileCategoryMapper.selectById(item.getFileCategoryId()).getCategoryName());
            }
            List<String> deduplicatedList = ListUtilJiang.removeDuplicates(tags);
            activity_item.put("tags",deduplicatedList);

            //数据获取完成,装入activityList
            activityList.add(activity_item);
        }
        //获取最大页数
        Integer maxPages = publishMapper.getMaxPages(departmentId, pageSize);
        res.put("maxPage",maxPages);
        res.put("activity",activityList);

        log.info("员工了一次activity");

        return Result.ok(res);
    }
    //拉取时间顺序获取-mainAndViceType模式
    @Override
    public Result getTimeSequence(Integer departmentId,String order, Integer currentPage,Integer mainAndViceTypeId) {
        Department department = departmentService.getById(departmentId);
        //获取到department
        Page<Publish> page = new Page<>(currentPage,pageSize);
        Publish[] publishes = publishMapper.selectPublishByDepartmentIdMainAndViceTypeId(mainAndViceTypeId,departmentId, order, page);

        if (publishes.length==0){
            return Result.ok("没有任何东西");
        }
        List<Map> activityList = new LinkedList<>();
        Map res = new HashMap<>();
        for (Publish publish : publishes){
            Map activity_item = new HashMap<>();
            //获取publishId
            activity_item.put("publishId",publish.getPublishId());
            //获取time
            activity_item.put("time", DateUtils.formatDateToString(publish.getCreateTime()));
            //获取文件组类型,即category
            String categoryName = fileGroupCategoryMapper.selectById(publish.getFileGroupCategoryId()).getCategoryName();
            activity_item.put("category",categoryName);
            //进入task_content,准备获取更多
            TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
            //放入Description
            activity_item.put("description",taskContent.getDescription());
            //放入title
            activity_item.put("title",taskContent.getTitle());
            //放入imageURL
            String imgName = taskContent.getPreviewImgPath().split("/")[taskContent.getPreviewImgPath().split("/").length - 1];
            String imgURL= URLEnum.HOST_URL.getUrl()+"img/"+imgName;
            activity_item.put("image",imgURL);

            //进入task_file_group表中
            QueryWrapper<TaskFileGroup> wrapper = new QueryWrapper<>();
            wrapper.eq("group_id",taskContent.getFileGroupId());
            List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapper);
            //装入count
            activity_item.put("count",taskFileGroups.size());
            //装入tags
            List<String> tags = new LinkedList<>();
            for (TaskFileGroup item:taskFileGroups){
                tags.add(fileCategoryMapper.selectById(item.getFileCategoryId()).getCategoryName());
            }
            List<String> deduplicatedList = ListUtilJiang.removeDuplicates(tags);
            activity_item.put("tags",deduplicatedList);

            //数据获取完成,装入activityList
            activityList.add(activity_item);
        }
        //获取最大页数
        Integer maxPages = publishMapper.getMaxPagesMVType(mainAndViceTypeId,departmentId, pageSize);
        res.put("maxPage",maxPages);
        res.put("activity",activityList);

        log.info("员工了一次activity");

        return Result.ok(res);
    }

    @Override
    public Result getMine(String uuid,Integer currentPage,Integer pageSize) {
        Page<Publish> page = new Page<>(currentPage,pageSize);
        Page<Publish> publishesPage = publishMapper.selectPage(page, new QueryWrapper<Publish>()
                .eq("uuid", uuid)
                .orderByDesc("create_time"));
        List<Publish> publishes = publishesPage.getRecords();

        if (publishes.size()==0){
            return Result.ok("没有任何东西");
        }
        List<Map> activityList = new LinkedList<>();
        Map res = new HashMap<>();
        for (Publish publish : publishes){
            Map activity_item = new HashMap<>();
            //获取publishId
            activity_item.put("publishId",publish.getPublishId());
            //获取time
            activity_item.put("time", DateUtils.formatDateToString(publish.getCreateTime()));
            //获取文件组类型,即category
            String categoryName = fileGroupCategoryMapper.selectById(publish.getFileGroupCategoryId()).getCategoryName();
            activity_item.put("category",categoryName);
            //进入task_content,准备获取更多
            TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
            //放入Description
            activity_item.put("description",taskContent.getDescription());
            //放入title
            activity_item.put("title",taskContent.getTitle());
            //放入imageURL
            String imgName = taskContent.getPreviewImgPath().split("/")[taskContent.getPreviewImgPath().split("/").length - 1];
            String imgURL= URLEnum.HOST_URL.getUrl()+"img/"+imgName;
            activity_item.put("image",imgURL);

            //进入task_file_group表中
            QueryWrapper<TaskFileGroup> wrapper = new QueryWrapper<>();
            wrapper.eq("group_id",taskContent.getFileGroupId());
            List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapper);
            //装入count
            activity_item.put("count",taskFileGroups.size());
            //装入tags
            List<String> tags = new LinkedList<>();
            for (TaskFileGroup item:taskFileGroups){
                tags.add(fileCategoryMapper.selectById(item.getFileCategoryId()).getCategoryName());
            }
            List<String> deduplicatedList = ListUtilJiang.removeDuplicates(tags);
            activity_item.put("tags",deduplicatedList);

            //数据获取完成,装入activityList
            activityList.add(activity_item);
        }
        //获取最大页数
        Long pages = publishesPage.getPages();
        Integer maxPages=pages.intValue();
        res.put("maxPage",maxPages);
        res.put("activity",activityList);

        log.info("员工了一次activity");

        return Result.ok(res);



    }


    //拉取fileCategory
    @Override
    public Result getFileCategory(String fileCategory, Integer departmentId,String order, Integer currentPage) {
        Department department = departmentService.getById(departmentId);
        //默认配置
        Page<Publish> page = new Page<>(currentPage,pageSize);
        //获取到fileCategoryId
        QueryWrapper<FileCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("category_name",fileCategory);
        Integer fileCategoryId = fileCategoryMapper.selectOne(wrapper).getFileCategoryId();
        //如果该种类为空,那么直接返回
        QueryWrapper<TaskFileGroup> wrapperTest = new QueryWrapper<>();
        if (taskFileGroupMapper.selectList(wrapperTest.eq("file_category_id",fileCategoryId)).size()==0){
            return Result.ok("没有任何东西");
        }
        Integer[] publishIds=publishMapper.getPublishIdByFileCateGoryId(fileCategoryId,departmentId,order,page);

        //多次请求,以保证顺序性
        //因为selectBatchIds不保证顺序
        ArrayList<Publish> publishes = new ArrayList<>();
        for (Integer publishId:publishIds){
            publishes.add(publishMapper.selectById(publishId));
        }
        if (publishes.size()==0){
            return Result.ok("没有任何东西");
        }
        List<Map> activityList = new LinkedList<>();
        Map res = new HashMap<>();
        for (Publish publish : publishes){
            Map activity_item = new HashMap<>();
            //获取publishId
            activity_item.put("publishId",publish.getPublishId());
            //获取time
            activity_item.put("time", DateUtils.formatDateToString(publish.getCreateTime()));
            //获取文件组类型,即category
            String categoryName = fileGroupCategoryMapper.selectById(publish.getFileGroupCategoryId()).getCategoryName();
            activity_item.put("category",categoryName);
            //进入task_content,准备获取更多
            TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
            //放入Description
            activity_item.put("description",taskContent.getDescription());
            //放入title
            activity_item.put("title",taskContent.getTitle());
            //放入imageURL
            String imgName = taskContent.getPreviewImgPath().split("/")[taskContent.getPreviewImgPath().split("/").length - 1];
            String imgURL= URLEnum.HOST_URL.getUrl()+"img/"+imgName;
            activity_item.put("image",imgURL);

            //进入task_file_group表中
            QueryWrapper<TaskFileGroup> wrapperA = new QueryWrapper<>();
            wrapperA.eq("group_id",taskContent.getFileGroupId());
            List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapperA);
            //装入count
            activity_item.put("count",taskFileGroups.size());
            //装入tags
            List<String> tags = new LinkedList<>();
            for (TaskFileGroup item:taskFileGroups){
                tags.add(fileCategoryMapper.selectById(item.getFileCategoryId()).getCategoryName());
            }
            List<String> deduplicatedList = ListUtilJiang.removeDuplicates(tags);
            activity_item.put("tags",deduplicatedList);

            //数据获取完成,装入activityList
            activityList.add(activity_item);
        }
        //获取最大页数
        Integer maxPages = publishMapper.getMaxPagesFileCategory(fileCategoryId,departmentId, pageSize);
        res.put("maxPage",maxPages);
        res.put("activity",activityList);

        log.info("员工了一次activity");

        return Result.ok(res);
    }

    //拉取fileCategory---mainAndViceType模式
    @Override
    public Result getFileCategory(String fileCategory, Integer departmentId,String order, Integer currentPage,Integer mainAndViceTypeId) {
        Department department = departmentService.getById(departmentId);
        //默认配置

        Page<Publish> page = new Page<>(currentPage,pageSize);
        //获取到fileCategoryId
        QueryWrapper<FileCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("category_name",fileCategory);
        Integer fileCategoryId = fileCategoryMapper.selectOne(wrapper).getFileCategoryId();
        //如果该种类为空,那么直接返回
        QueryWrapper<TaskFileGroup> wrapperTest = new QueryWrapper<>();
        if (taskFileGroupMapper.selectList(wrapperTest.eq("file_category_id",fileCategoryId)).size()==0){
            return Result.ok("没有任何东西");
        }
        Integer[] publishIds=publishMapper.getPublishIdByFileCateGoryIdMVType(mainAndViceTypeId,fileCategoryId,departmentId,order,page);

        //多次请求,以保证顺序性
        //因为selectBatchIds不保证顺序
        ArrayList<Publish> publishes = new ArrayList<>();
        for (Integer publishId:publishIds){
            publishes.add(publishMapper.selectById(publishId));
        }
        if (publishes.size()==0){
            return Result.ok("没有任何东西");
        }
        List<Map> activityList = new LinkedList<>();
        Map res = new HashMap<>();
        for (Publish publish : publishes){
            Map activity_item = new HashMap<>();
            //获取publishId
            activity_item.put("publishId",publish.getPublishId());
            //获取time
            activity_item.put("time", DateUtils.formatDateToString(publish.getCreateTime()));
            //获取文件组类型,即category
            String categoryName = fileGroupCategoryMapper.selectById(publish.getFileGroupCategoryId()).getCategoryName();
            activity_item.put("category",categoryName);
            //进入task_content,准备获取更多
            TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
            //放入Description
            activity_item.put("description",taskContent.getDescription());
            //放入title
            activity_item.put("title",taskContent.getTitle());
            //放入imageURL
            String imgName = taskContent.getPreviewImgPath().split("/")[taskContent.getPreviewImgPath().split("/").length - 1];
            String imgURL= URLEnum.HOST_URL.getUrl()+"img/"+imgName;
            activity_item.put("image",imgURL);

            //进入task_file_group表中
            QueryWrapper<TaskFileGroup> wrapperA = new QueryWrapper<>();
            wrapperA.eq("group_id",taskContent.getFileGroupId());
            List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapperA);
            //装入count
            activity_item.put("count",taskFileGroups.size());
            //装入tags
            List<String> tags = new LinkedList<>();
            for (TaskFileGroup item:taskFileGroups){
                tags.add(fileCategoryMapper.selectById(item.getFileCategoryId()).getCategoryName());
            }
            List<String> deduplicatedList = ListUtilJiang.removeDuplicates(tags);
            activity_item.put("tags",deduplicatedList);

            //数据获取完成,装入activityList
            activityList.add(activity_item);
        }
        //获取最大页数
        Integer maxPages = publishMapper.getMaxPagesFileCategoryMVType(mainAndViceTypeId,fileCategoryId,departmentId, pageSize);
        res.put("maxPage",maxPages);
        res.put("activity",activityList);

        log.info("员工了一次activity");

        return Result.ok(res);
    }








    //拉取fileGroupCategory
    @Override
    public Result getFileGroupCategory(String fileGroupCategory, Integer departmentId,String order, Integer currentPage) {
        Department department = departmentService.getById(departmentId);
        //获取到fileCategoryId
        QueryWrapper<FileGroupCategory> fileGroupCategoryQueryWrapper = new QueryWrapper<>();
        fileGroupCategoryQueryWrapper.eq("category_name",fileGroupCategory);
        Integer fileGroupCategoryId = fileGroupCategoryMapper.selectOne(fileGroupCategoryQueryWrapper).getFileGroupCategoryId();


        //获取到department
        Page<Publish> page = new Page<>(currentPage,pageSize);
        Publish[] publishes = publishMapper.selectPublishByDepartmentIdAndFileGroupCate(fileGroupCategoryId,departmentId, order, page);
        if (publishes.length==0){
            return Result.ok("没有任何东西");
        }
        List<Map> activityList = new LinkedList<>();
        Map res = new HashMap<>();
        for (Publish publish : publishes){
            Map activity_item = new HashMap<>();
            //获取publishId
            activity_item.put("publishId",publish.getPublishId());
            //获取time
            activity_item.put("time", DateUtils.formatDateToString(publish.getCreateTime()));
            //获取文件组类型,即category
            String categoryName = fileGroupCategoryMapper.selectById(publish.getFileGroupCategoryId()).getCategoryName();
            activity_item.put("category",categoryName);
            //进入task_content,准备获取更多
            TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
            //放入Description
            activity_item.put("description",taskContent.getDescription());
            //放入title
            activity_item.put("title",taskContent.getTitle());
            //放入imageURL
            String imgName = taskContent.getPreviewImgPath().split("/")[taskContent.getPreviewImgPath().split("/").length - 1];
            String imgURL= URLEnum.HOST_URL.getUrl()+"img/"+imgName;
            activity_item.put("image",imgURL);

            //进入task_file_group表中
            QueryWrapper<TaskFileGroup> wrapper = new QueryWrapper<>();
            wrapper.eq("group_id",taskContent.getFileGroupId());
            List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapper);
            //装入count
            activity_item.put("count",taskFileGroups.size());
            //装入tags
            List<String> tags = new LinkedList<>();
            for (TaskFileGroup item:taskFileGroups){
                tags.add(fileCategoryMapper.selectById(item.getFileCategoryId()).getCategoryName());
            }
            List<String> deduplicatedList = ListUtilJiang.removeDuplicates(tags);
            activity_item.put("tags",deduplicatedList);

            //数据获取完成,装入activityList
            activityList.add(activity_item);
        }
        //获取最大页数
        Integer maxPages = publishMapper.getMaxPagesFileGroupCate(fileGroupCategoryId,departmentId, pageSize);
        res.put("maxPage",maxPages);
        res.put("activity",activityList);

        log.info("员工了一次activity");

        return Result.ok(res);

    }
    //拉取fileGroupCategory的mainAndViceType模式
    @Override
    public Result getFileGroupCategory(String fileGroupCategory, Integer departmentId,String order, Integer currentPage,Integer mainAndViceTypeId) {
        Department department = departmentService.getById(departmentId);
        //获取到fileCategoryId
        QueryWrapper<FileGroupCategory> fileGroupCategoryQueryWrapper = new QueryWrapper<>();
        fileGroupCategoryQueryWrapper.eq("category_name",fileGroupCategory);
        Integer fileGroupCategoryId = fileGroupCategoryMapper.selectOne(fileGroupCategoryQueryWrapper).getFileGroupCategoryId();


        //获取到department
        Page<Publish> page = new Page<>(currentPage,pageSize);
        Publish[] publishes = publishMapper.selectPublishByDepartmentIdAndFileGroupCateMVType(mainAndViceTypeId,fileGroupCategoryId,departmentId, order, page);
        if (publishes.length==0){
            return Result.ok("没有任何东西");
        }
        List<Map> activityList = new LinkedList<>();
        Map res = new HashMap<>();
        for (Publish publish : publishes){
            Map activity_item = new HashMap<>();
            //获取publishId
            activity_item.put("publishId",publish.getPublishId());
            //获取time
            activity_item.put("time", DateUtils.formatDateToString(publish.getCreateTime()));
            //获取文件组类型,即category
            String categoryName = fileGroupCategoryMapper.selectById(publish.getFileGroupCategoryId()).getCategoryName();
            activity_item.put("category",categoryName);
            //进入task_content,准备获取更多
            TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
            //放入Description
            activity_item.put("description",taskContent.getDescription());
            //放入title
            activity_item.put("title",taskContent.getTitle());
            //放入imageURL
            String imgName = taskContent.getPreviewImgPath().split("/")[taskContent.getPreviewImgPath().split("/").length - 1];
            String imgURL= URLEnum.HOST_URL.getUrl()+"img/"+imgName;
            activity_item.put("image",imgURL);

            //进入task_file_group表中
            QueryWrapper<TaskFileGroup> wrapper = new QueryWrapper<>();
            wrapper.eq("group_id",taskContent.getFileGroupId());
            List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapper);
            //装入count
            activity_item.put("count",taskFileGroups.size());
            //装入tags
            List<String> tags = new LinkedList<>();
            for (TaskFileGroup item:taskFileGroups){
                tags.add(fileCategoryMapper.selectById(item.getFileCategoryId()).getCategoryName());
            }
            List<String> deduplicatedList = ListUtilJiang.removeDuplicates(tags);
            activity_item.put("tags",deduplicatedList);

            //数据获取完成,装入activityList
            activityList.add(activity_item);
        }
        //获取最大页数
        Integer maxPages = publishMapper.getMaxPagesFileGroupCateMVType(mainAndViceTypeId,fileGroupCategoryId,departmentId, pageSize);
        res.put("maxPage",maxPages);
        res.put("activity",activityList);

        log.info("员工了一次activity");

        return Result.ok(res);

    }

    @Override
    public Result getOneByPublishId(Integer publishId) {
        Map data = new HashMap();
        Publish publish = publishMapper.selectById(publishId);
        TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());

        //需要获取总共两部分信息
        //1. 文件组的描述信息
        Map mainInfo = new HashMap<>();
        mainInfo.put("title",taskContent.getTitle());
        mainInfo.put("description",taskContent.getDescription());
        mainInfo.put("publisher",userInfoMapper.selectById(publish.getUuid()).getRealName());
        mainInfo.put("publishTime",DateUtils.formatDateToStringDetail(publish.getCreateTime()));
        data.put("mainInfo",mainInfo);
        //2. 文件组的文件名信息
        QueryWrapper<TaskFileGroup> wrapper = new QueryWrapper<>();
        wrapper.eq("group_id",taskContent.getFileGroupId());
        List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapper);
        //准备三个map,分别用于装一级二级三级文件
        List<String> firstList = new ArrayList<>();
        List<String> secondList = new ArrayList<>();
        List<String> thirdList = new ArrayList<>();
        for (TaskFileGroup taskFileGroup:taskFileGroups){
            if (taskFileGroup.getFileLevel().equals(1)){
                firstList.add(taskFileGroup.getFileName());
            } else if (taskFileGroup.getFileLevel().equals(2)) {
                secondList.add(taskFileGroup.getFileName());
            } else if (taskFileGroup.getFileLevel().equals(3)) {
                thirdList.add(taskFileGroup.getFileName());
            }
        }
        data.put("first",firstList);
        data.put("second",secondList);
        data.put("third",thirdList);
        return Result.ok(data);
    }







    @Override
    public ResponseEntity<Map<String, Object>> uploadChunk(String token, int index, long chunkSize, int totalChunks, long totalSize, String fileName, byte[] file) {
        String uuid = jwtHelper.getUserId(token);
        //这个uuidDir是每个用户各自有一个文件夹,以防发生冲突
        String workSpaceHeaderPath=pathUtils.getRealPath(PathEnum.TMP_PATH_WORK_SPACE.getPath()).toString();
        /**
         * 生产环境
         */
//        String uuidDir=selfSpaceHeaderPath+"/"+uuid;


        String uuidDir=workSpaceHeaderPath+"/"+uuid;

//        System.out.println("uuidDir = " + uuidDir);

        File uploadDir = new File(uuidDir);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        // 将接收到的分块数据写入临时文件
        Path tempFilePath = Paths.get(uuidDir, fileName + "-" + index);

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(file);
            Files.copy(byteArrayInputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            byteArrayInputStream.close();
        } catch (IOException e) {
            log.error("文件分块写入失败:  "+fileName+"-"+index);
            System.out.println("errorMsg = " + e);
            throw new RuntimeException(e);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        return ResponseEntity.ok(result);
    }


    private String checkFileName(String originFileName, String uuid) {
        ArrayList<String> allSimilarNameList = new ArrayList<>();
        boolean similarName = false;
        boolean existSameName = false;

        List<Publish> publishes = publishMapper.selectList(new QueryWrapper<Publish>().eq("uuid", uuid));
        for (Publish publish : publishes){
            String fileGroupId = taskContentMapper.selectById(publish.getTaskContentId()).getFileGroupId();
            List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(new QueryWrapper<TaskFileGroup>()
                    .eq("group_id", fileGroupId)
                    .like("file_name", originFileName));
            if (taskFileGroups.size()==0){
                continue;
            }else{
                similarName=true;
                for (TaskFileGroup taskFileGroup:taskFileGroups){
                    allSimilarNameList.add(taskFileGroup.getFileName());
                }
            }
        }
        //至此获得所有相似文件名的集合
        //验证是否存在同名的文件
        for (String name:allSimilarNameList){
            if (name.equals(originFileName)){
                existSameName=true;
            }
        }
        //准备进行正则匹配
        String finalName = originFileName;//默认finalName是originalName
        Integer matchSuccessCount = 0;
        Integer maxNumFileName = 0;//归档的文件名中数字的最大值
        ArrayList<String> matchedNameList = new ArrayList<>();
        //存在相似名字函数
        if (similarName){
            //正则匹配
            for (String name: allSimilarNameList){
                if (name.matches("^\\d+-" + Pattern.quote(originFileName) + "$")) {
                    matchedNameList.add(name);
                    matchSuccessCount++;
                    System.out.println("匹配成功: " + name);
                }
            }

            //得到归档次数的最大值
            // 情况1:当match次数是0,但是existSameName是true,说明没有匹配到,但是存在同名文件,也就是该文件现在被第一次归档
            if (matchSuccessCount==0 && existSameName){
                finalName="1-"+originFileName;
                return finalName;
            }
            //情况2: 当match不为0,切且existSameName为true,说明该文件被归档过,现在需要最大归档次数+1
            else if (matchSuccessCount!=0 && existSameName) {
                //得到匹配的所有文件的名字,准备获取其最大数字
                for (String name:matchedNameList){
                    String[] split = name.split("-");
                    int num =Integer.parseInt(split[0]);
                    if (num>maxNumFileName) {
                        maxNumFileName = num;
                    }
                }
                maxNumFileName=maxNumFileName+1;
                finalName=maxNumFileName+"-"+originFileName;
                System.out.println("finalName = " + finalName);
                return finalName;
            }
            //输出最终的文件名
        }
        //不存在相似名字,直接返回原文件名使用
        else {
            return finalName;
        }




        return finalName;
    }



    @Override
    public ResponseEntity<Map<String, Object>> merge(String token, String fileName,Integer fileLevel)  {
        String uuid = jwtHelper.getUserId(token);
        //要上传的文件的文件头
        //例子:real(classpath)/fileSystem/workSpace/real(uuid)/{level}/filename
        String workSpaceHeaderPath=pathUtils.getRealPath(PathEnum.WORK_SPACE_PATH.getPath()).toString();
        String uploadDirString= workSpaceHeaderPath+"/"+uuid;
        if (fileLevel.equals(1)){
            uploadDirString=uploadDirString+"/firstLevel";
        }else if (fileLevel.equals(2)){
            uploadDirString=uploadDirString+"/secondLevel";
        }else if (fileLevel.equals(3)){
            uploadDirString=uploadDirString+"/thirdLevel";

        }
        File uploadDir = new File(uploadDirString);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String workSpaceTmpHeaderDir=pathUtils.getRealPath(PathEnum.TMP_PATH_WORK_SPACE.getPath()).toString();
        String tmpPath=workSpaceTmpHeaderDir+"/"+uuid;
        File tempDir = new File(tmpPath);
        String[] fileNames = tempDir.list((dir, name) -> name.startsWith(fileName));
        Arrays.sort(fileNames,new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                // 提取出文件名中的数字部分进行比较
                int num1 = Integer.parseInt(o1.substring(o1.lastIndexOf('-') + 1));
                int num2 = Integer.parseInt(o2.substring(o2.lastIndexOf('-') + 1));
                return Integer.compare(num1, num2);
            }
        });
        Arrays.stream(fileNames).forEach(System.out::println);
        //在这里检测一下本账号的文件组下是否有同名文件,如果有,则进行归档处理(改名字)
        String finalName= checkFileName(fileName,uuid);
        Path finalPath = Paths.get(uploadDirString, finalName);
        try (FileOutputStream fos = new FileOutputStream(finalPath.toFile())) {
            for (String tempFileName : fileNames) {
                Path tempFilePath = Paths.get(tmpPath, tempFileName);
                Files.copy(tempFilePath, fos);
                Files.delete(tempFilePath);
            }
        }catch (IOException e) {
            log.error("文件合并失败:  "+finalName);
            throw new RuntimeException(e);
        }
        Map<String, Object> result = new HashMap<>();
        if (fileLevel.equals(1)){
            result.put("url", URLEnum.HOST_URL+"workSpace/"+uuid+"/"+"firstLevel"+"/" + finalName);
        }else if (fileLevel.equals(2)){
            result.put("url", URLEnum.HOST_URL+"workSpace/"+uuid+"/"+"secondLevel"+"/" + finalName);
        }else if (fileLevel.equals(3)){
            result.put("url", URLEnum.HOST_URL+"workSpace/"+uuid+"/"+"thirdLevel"+"/" + finalName);
        }

        return ResponseEntity.ok(result);
    }



    @Override
    public Result writeToDB(Map data) {
        System.out.println("data = " + data);
        TaskFileGroup taskFileGroup = new TaskFileGroup();
        //1.设置FileCategoryId
        QueryWrapper<FileCategory> fileCateWrapper = new QueryWrapper<>();
        fileCateWrapper.eq("category_name",data.get("fileCategory"));
        FileCategory fileCategory = fileCategoryMapper.selectOne(fileCateWrapper);
        taskFileGroup.setFileCategoryId(fileCategory.getFileCategoryId());
        //2. 设置文件等级
        taskFileGroup.setFileLevel((Integer) data.get("fileLevel"));
        //3. 设置文件名
//        taskFileGroup.setFileName((String) data.get("fileName"));
        String fileName=checkFileName((String) data.get("fileName"),(String) data.get("uuid"));
        taskFileGroup.setFileName(fileName);
        //4. 设置大小
        taskFileGroup.setFileSize((Integer) data.get("fileSize"));
        //5. 设置groupId
        taskFileGroup.setGroupId((String) data.get("groupId"));
        //6. 设置文件路径
        if (data.get("fileLevel").equals(1)){
            taskFileGroup.setFilePath(PathEnum.WORK_SPACE_PATH.getPath()+data.get("uuid")+"/"+"firstLevel"+"/"+fileName);
        } else if (data.get("fileLevel").equals(2)) {
            taskFileGroup.setFilePath(PathEnum.WORK_SPACE_PATH.getPath()+data.get("uuid")+"/"+"secondLevel"+"/"+fileName);
        } else if (data.get("fileLevel").equals(3))  {
            taskFileGroup.setFilePath(PathEnum.WORK_SPACE_PATH.getPath()+data.get("uuid")+"/"+"thirdLevel"+"/"+fileName);
        }
        taskFileGroupMapper.insert(taskFileGroup);

        return Result.ok("文件插入数据库成功");
    }



    @Override
    public Result uploadPreviewImg(String groupId, MultipartFile file) {
        String fileName=UUID.getUUID()+".png";
        Path previewImgHeaderPath =pathUtils.getRealPath(PathEnum.WORK_SPACE_PREVIEW_IMG_PATH.getPath());
        Path previewImgPath = previewImgHeaderPath.resolve(fileName);
        //1. 更新数据库
        QueryWrapper<TaskContent> wrapper = new QueryWrapper<>();
        wrapper.eq("file_group_id",groupId);
        TaskContent taskContent = taskContentMapper.selectOne(wrapper);
        taskContent.setPreviewImgPath(PathEnum.WORK_SPACE_PREVIEW_IMG_PATH.getPath()+"/"+ fileName);
        taskContentMapper.updateById(taskContent);

        try {
            InputStream inputStream = file.getInputStream();
            Files.copy(inputStream,previewImgPath, StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();
        } catch (IOException e) {
            log.error("存储头像到服务器失败,失败原因: "+e.getMessage());
            throw new RuntimeException(e);
        }


        return Result.ok("预览头像插入数据库成功");



    }

    @Override
    public Result delete(Integer publishId,String uuid) {
        Publish publish = publishMapper.selectById(publishId);
        if (publish.getUuid().equals(uuid)) {
            TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
            QueryWrapper<TaskFileGroup> wrapperTaskFileGroup = new QueryWrapper<>();
            wrapperTaskFileGroup.eq("group_id", taskContent.getFileGroupId());
            List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapperTaskFileGroup);
            //1. 先执行数据库删除操作
            //1.1 删除publish
            publishMapper.deleteById(publish);
            //1.2 删除taskContent
            taskContentMapper.deleteById(taskContent);
            //1.3 删除单体文件
            for (TaskFileGroup item : taskFileGroups) {
                taskFileGroupMapper.deleteById(item.getFileGroupId());
            }


            //2. 再执行磁盘中实体删除操作

            //2.1 删除单体文件(实体)
            for (TaskFileGroup item : taskFileGroups) {
                System.out.println("item = " + item);
                fileHandleThreadPool.execute(() -> {
                    try {
                        Files.delete(pathUtils.getRealPath(item.getFilePath()));
                    } catch (IOException e) {
                        log.warn("删除文件失败, 失败原因: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                });
            }
            //2.2 删除文件组预览图
            fileHandleThreadPool.execute(() -> {
                try {
                    Files.delete(pathUtils.getRealPath(taskContent.getPreviewImgPath()));
                } catch (IOException e) {
                    log.warn("删除文件组预览图文件失败, 失败原因: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
            );
            log.error(uuid + " 执行了删除文件组操作, 该文件组title为: " + taskContent.getTitle());
            return Result.ok("成功删除");
        }
        return Result.build(null, ResultCodeEnum.SEVERE_HANDLER);
    }

    @Override
    public Result delete(Integer publishId) {

        //1. 删除所有的通知信息
        List<Authorise> authorises = authoriseMapper.selectList(new QueryWrapper<Authorise>().eq("publish_id", publishId));
        if (authorises.size() > 0) {
            authorises.stream().forEach(item -> {
                authoriseMapper.deleteById(item);
            });
        }


        Publish publish = publishMapper.selectById(publishId);
        TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
        QueryWrapper<TaskFileGroup> wrapperTaskFileGroup = new QueryWrapper<>();
        wrapperTaskFileGroup.eq("group_id", taskContent.getFileGroupId());
        List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapperTaskFileGroup);
        //2. 先执行数据库删除操作
        //2.1 删除publish
        publishMapper.deleteById(publish);
        //2.2 删除taskContent
        taskContentMapper.deleteById(taskContent);
        //2.3 删除单体文件
        for (TaskFileGroup item : taskFileGroups) {
            taskFileGroupMapper.deleteById(item.getFileGroupId());
        }


        //3. 再执行磁盘中实体删除操作

        //3.1 删除单体文件(实体)
        for (TaskFileGroup item : taskFileGroups) {
            System.out.println("item = " + item);
            fileHandleThreadPool.execute(() -> {
                try {
                    Files.delete(pathUtils.getRealPath(item.getFilePath()));
                } catch (IOException e) {
                    log.warn("删除文件失败, 失败原因: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            });
        }
        //3.2 删除文件组预览图
        fileHandleThreadPool.execute(() -> {
                    try {
                        Files.delete(pathUtils.getRealPath(taskContent.getPreviewImgPath()));
                    } catch (IOException e) {
                        log.warn("删除文件组预览图文件失败, 失败原因: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
        );




        return Result.ok("成功删除");
    }

    @Override
    public Result deleteLogic(Integer publishId, String uuid) {
        Publish publish = publishMapper.selectById(publishId);
        if (publish.getUuid().equals(uuid)) {
            publish.setIsDelete(1);
            publishMapper.updateById(publish);
            return Result.ok("删除成功");
        }
        return null;
    }

    @Override
    public Result getOneFileDetail(String publishId, String fileName,String uuid,Integer fileLevel) {
        User user = userMapper.selectById(uuid);
        Map data = MapUtilsJiang.getAMap();
        Publish publish = publishMapper.selectById(publishId);
        TaskContent taskContent = taskContentMapper.selectById(publish.getTaskContentId());
        QueryWrapper<TaskFileGroup> wrapper = new QueryWrapper<>();
        wrapper.eq("group_id",taskContent.getFileGroupId())
                .eq("file_name",fileName);
        TaskFileGroup taskFileGroup = taskFileGroupMapper.selectOne(wrapper);
        data.put("upload_time",DateUtils.formatDateToStringDetail(publish.getCreateTime()));
        if (fileLevel == 1){
            data.put("file_path",URLEnum.HOST_URL.getUrl()+
                    URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                    publish.getUuid()+
                    URLEnum.FIRST_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                    taskFileGroup.getFileName());
        } else if (fileLevel == 2) {
            if (user.getRoleId()==1){
                data.put("file_path",URLEnum.HOST_URL.getUrl()+
                        URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                        publish.getUuid()+
                        URLEnum.SECOND_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                        taskFileGroup.getFileName()+
                        "/"+this.previewKey);
            }
            data.put("file_path",URLEnum.HOST_URL.getUrl()+
                    URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                    this.downloadKey+"/"+
                    publish.getUuid()+
                    URLEnum.SECOND_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                    taskFileGroup.getFileName());
        } else if (fileLevel == 3) {
            data.put("file_path",URLEnum.HOST_URL.getUrl()+
                    URLEnum.WORK_SPACE_RESOURCE_HEADER.getUrl()+
                    this.downloadKey+"/"+
                    publish.getUuid()+
                    URLEnum.THIRD_LEVEL_WORK_SPACE_RESOURCE.getUrl()+
                    taskFileGroup.getFileName());
        }

        //获取文件种类
        data.put("category_name",fileCategoryMapper.selectOne(new QueryWrapper<FileCategory>().eq("file_category_id",taskFileGroup.getFileCategoryId())).getCategoryName());
        data.put("file_size",taskFileGroup.getFileSize());
        return Result.ok(data);
    }

    @Override
    public Result getGroupId(Integer publishId) {
        return Result.ok(taskContentMapper.selectById(publishMapper.selectById(publishId).getTaskContentId()).getFileGroupId());
    }

    @Override
    public Result uploadFileGroupInfo(Map data) {

        //1. 先更改task_content内的内容
        //1.1 查看有无重复的title,这个东西不允许有重复
        QueryWrapper<TaskContent> taskContentWrapper = new QueryWrapper<>();
        taskContentWrapper.eq("title",data.get("title"));
        List<TaskContent> taskContents = taskContentMapper.selectList(taskContentWrapper);
        for (TaskContent item:taskContents){
            if (item.getTitle().equals(data.get("title"))){
                return Result.build("已存在过该 标题 了,请修改一下 标题 再继续上传",ResultCodeEnum.FILE_GROUP_TITLE_EXISTS_ERROR);
            }
        }
        TaskContent taskContent = new TaskContent();
        //设置taskContent
        taskContent.setTitle((String) data.get("title"));
        taskContent.setDescription((String) data.get("description"));
        taskContent.setFileGroupId((String) data.get("groupId"));
        taskContent.setPreviewImgPath(PathEnum.WORK_SPACE_PREVIEW_IMG_PATH.getPath()+"/imgNotExist.png");
        taskContentMapper.insert(taskContent);

        //获取插入后的taskContentId
        QueryWrapper<TaskContent> wrapper = new QueryWrapper<>();
        wrapper.eq("title",data.get("title"));
        TaskContent taskContentIt = taskContentMapper.selectOne(wrapper);

        //开始搞publish
        Publish publish = new Publish();
        publish.setIsDelete(0);
        publish.setTaskContentId(taskContentIt.getTaskContentId());
        publish.setUuid((String) data.get("uuid"));
        publish.setDepartmentId((Integer) data.get("departmentId"));
        //写入mainAndViceTypeId
        QueryWrapper<MainType> mainTypeWrapper = new QueryWrapper<>();
        mainTypeWrapper.eq("name",data.get("mainType"));
        MainType mainType = mainTypeMapper.selectOne(mainTypeWrapper);

        QueryWrapper<ViceType> viceTypeWrapper = new QueryWrapper<>();
        viceTypeWrapper.eq("name",data.get("viceType"));
        ViceType viceType = viceTypeMapper.selectOne(viceTypeWrapper);

        QueryWrapper<MainAndViceType> mainAndViceTypeWrapper = new QueryWrapper<>();
        mainAndViceTypeWrapper.eq("main_type_id",mainType.getMainTypeId());
        mainAndViceTypeWrapper.eq("vice_type_id",viceType.getViceTypeId());
        MainAndViceType mainAndViceType = mainAndViceTypeMapper.selectOne(mainAndViceTypeWrapper);

        publish.setMainAndViceTypeId(mainAndViceType.getMainAndViceTypeId());
        //写入FileGroupCategoryId
        QueryWrapper<FileGroupCategory> fileGroupCategoryWrapper = new QueryWrapper<>();
        fileGroupCategoryWrapper.eq("category_name",data.get("fileGroupCategory"));
        FileGroupCategory fileGroupCategory = fileGroupCategoryMapper.selectOne(fileGroupCategoryWrapper);
        publish.setFileGroupCategoryId(fileGroupCategory.getFileGroupCategoryId());

        publishMapper.insert(publish);
        return Result.ok(null);
    }

    @Override
    public Result uploadFileGroupInfoAdmin(Map data) {
        //1. 先更改task_content内的内容
        //1.1 查看有无重复的title,这个东西不允许有重复
        QueryWrapper<TaskContent> taskContentWrapper = new QueryWrapper<>();
        taskContentWrapper.eq("title",data.get("title"));
        List<TaskContent> taskContents = taskContentMapper.selectList(taskContentWrapper);
        for (TaskContent item:taskContents){
            if (item.getTitle().equals(data.get("title"))){
                return Result.build("已存在过该 标题 了,请修改一下 标题 再继续上传",ResultCodeEnum.FILE_GROUP_TITLE_EXISTS_ERROR);
            }
        }
        TaskContent taskContent = new TaskContent();
        //设置taskContent
        taskContent.setTitle((String) data.get("title"));
        taskContent.setDescription((String) data.get("description"));
        taskContent.setFileGroupId((String) data.get("groupId"));
        taskContent.setPreviewImgPath(PathEnum.WORK_SPACE_PREVIEW_IMG_PATH.getPath()+"/imgNotExist.png");
        taskContentMapper.insert(taskContent);

        //获取插入后的taskContentId
        QueryWrapper<TaskContent> wrapper = new QueryWrapper<>();
        wrapper.eq("title",data.get("title"));
        TaskContent taskContentIt = taskContentMapper.selectOne(wrapper);

        //开始搞publish
        Publish publish = new Publish();
        publish.setIsDelete(0);
        publish.setTaskContentId(taskContentIt.getTaskContentId());
        publish.setUuid((String) data.get("uuid"));
        publish.setDepartmentId((Integer) data.get("departmentId"));
        //写入mainAndViceTypeId

        publish.setMainAndViceTypeId((Integer) data.get("mainAndViceTypeId"));
        //写入FileGroupCategoryId
        QueryWrapper<FileGroupCategory> fileGroupCategoryWrapper = new QueryWrapper<>();
        fileGroupCategoryWrapper.eq("category_name",data.get("fileGroupCategory"));
        FileGroupCategory fileGroupCategory = fileGroupCategoryMapper.selectOne(fileGroupCategoryWrapper);
        publish.setFileGroupCategoryId(fileGroupCategory.getFileGroupCategoryId());

        publishMapper.insert(publish);
        return Result.ok(null);
    }

    @Override
    public Result getSearchValueTimeSequence(String searchValue, Integer departmentId, String order, Integer currentPage) {
        Map data = new HashMap<>();
        Page<Publish> page = new Page<>(currentPage,pageSize);
        List<Publish> searchValueTimeSequence = publishMapper.getSearchValueTimeSequence(searchValue, departmentId, order, page);
        List<Map> activities = new ArrayList<>();

        for (Publish publish : searchValueTimeSequence){
            Map activity = new HashMap<>();
            //装入publishId
            activity.put("publishId",publish.getPublishId());
            activity.put("time",DateUtils.formatDateToString(publish.getCreateTime()));
            QueryWrapper<FileGroupCategory> wrapperFileGroupCategory = new QueryWrapper<>();
            wrapperFileGroupCategory.eq("file_group_category_id",publish.getFileGroupCategoryId());
            FileGroupCategory fileGroupCategory = fileGroupCategoryMapper.selectOne(wrapperFileGroupCategory);
            activity.put("category",fileGroupCategory.getCategoryName());

            QueryWrapper<TaskContent> wrapperTaskContent = new QueryWrapper<>();
            wrapperTaskContent.eq("task_content_id",publish.getTaskContentId());
            TaskContent taskContent = taskContentMapper.selectOne(wrapperTaskContent);
            activity.put("title",taskContent.getTitle());
            activity.put("description",taskContent.getDescription());
            String imgName = taskContent.getPreviewImgPath().split("/")[taskContent.getPreviewImgPath().split("/").length - 1];
            String imgURL= URLEnum.HOST_URL.getUrl()+"img/"+imgName;
            activity.put("image",imgURL);

            //进入task_file_group表中
            QueryWrapper<TaskFileGroup> wrapper = new QueryWrapper<>();
            wrapper.eq("group_id",taskContent.getFileGroupId());
            List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(wrapper);
            //装入count
            activity.put("count",taskFileGroups.size());
            //装入tags
            List<String> tags = new LinkedList<>();
            for (TaskFileGroup item:taskFileGroups){
                tags.add(fileCategoryMapper.selectById(item.getFileCategoryId()).getCategoryName());
            }
            List<String> deduplicatedList = ListUtilJiang.removeDuplicates(tags);
            activity.put("tags",deduplicatedList);

            activities.add(activity);
        }

        data.put("activity",activities);
        Integer pageNumsOfSearchValueTimeSequence = publishMapper.getPageNumsOfSearchValueTimeSequence(searchValue, departmentId, order, pageSize);
        data.put("maxPage",pageNumsOfSearchValueTimeSequence);


        return Result.ok(data);
    }


}
