package com.xiangyueEducation.uploaderCloud.Service.pojoPackage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.tools.jconsole.JConsoleContext;
import com.xiangyueEducation.uploaderCloud.Controller.FileGroupController;
import com.xiangyueEducation.uploaderCloud.POJO.*;
import com.xiangyueEducation.uploaderCloud.Service.funPackage.FileGroupService;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.AuthoriseService;
import com.xiangyueEducation.uploaderCloud.Utils.BaseUtils.PathUtils;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.URLEnum;
import com.xiangyueEducation.uploaderCloud.Utils.UUID;
import com.xiangyueEducation.uploaderCloud.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author 86136
* @description 针对表【authorise】的数据库操作Service实现
* @createDate 2024-06-03 23:54:17
*/
@Service
public class AuthoriseServiceImpl extends ServiceImpl<AuthoriseMapper, Authorise>
    implements AuthoriseService{

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private AuthoriseMapper authoriseMapper;

    @Autowired
    private PublishMapper publishMapper;

    @Autowired
    private TaskContentMapper taskContentMapper;

    @Autowired
    private TaskFileGroupMapper taskFileGroupMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PathUtils pathUtils;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private FileGroupService fileGroupService;


    @Override
    public Result createAuth(String requesterId, Integer publishId, String fileName) {
        //先检测数据库中存不存在同名同publishId同requesterId的
        List<Authorise> authoriseTest = authoriseMapper.selectList(new QueryWrapper<Authorise>().eq("publish_id", publishId).eq("requester_uuid", requesterId).eq("file_name", fileName));
        if (authoriseTest.size()>1 || authoriseTest.size()==1){
            return Result.ok("已申请过");
        }
        Authorise authorise = new Authorise();
        authorise.setPublishId(publishId);
        authorise.setRequesterUuid(requesterId);
        authorise.setFileName(fileName);
        authorise.setSecretKey("none");
        authorise.setIsAuthorise(-1);
        String groupId = taskContentMapper.selectById(publishMapper.selectById(publishId).getTaskContentId()).getFileGroupId();

        QueryWrapper<TaskFileGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        List<TaskFileGroup> taskFileGroups = taskFileGroupMapper.selectList(queryWrapper);
        for (TaskFileGroup i:taskFileGroups){
            if (i.getFileName().equals(fileName)){
                authorise.setFilePath(i.getFilePath());
                break;
            }
        }
        authoriseMapper.insert(authorise);

        return Result.ok("申请成功");
    }

    @Override
    public Result handleAuth(Integer authoriseId, Integer publishId, String uuid, Integer is_authorised) {
        //不是本人操作直接返回
        Publish publish = publishMapper.selectById(publishId);
        if (!uuid.equals(publish.getUuid())){
            return Result.ok("不是领导本人操作, 操作失败");
        }
        Authorise authorise = authoriseMapper.selectById(authoriseId);
        authorise.setAuthoriseId(authoriseId);
        if (is_authorised == 1){
            authorise.setSecretKey(UUID.getUUID());
            authorise.setIsAuthorise(1);
            authoriseMapper.updateById(authorise);
            return Result.ok("已同意");
        } else if (is_authorised == 0) {
            authorise.setSecretKey("notAllowed");
            authorise.setIsAuthorise(0);
            authoriseMapper.updateById(authorise);
            return Result.ok("已拒绝");
        }
        return Result.ok("操作异常,未进行更改");

    }

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public Result getAllAuth(String uuid,Integer currentPage,String order) {
        Integer pageSize=12;
        //判断这个用户是 员工 还是 领导
        User user = userMapper.selectById(uuid);
        List<Map> res = new ArrayList<>();
        Map dataRes = new HashMap<>();

        //是员工
        if (user.getRoleId().equals(1)){
            Page<Authorise> page = new Page<>(currentPage,pageSize);
            //获取到authorise列表
            List<Authorise> authorises = authoriseMapper.selectRole1UsePage(order,uuid,page);
            Integer maxPages = authoriseMapper.selectMaxPageRole1(uuid,pageSize);
            dataRes.put("maxPages",maxPages);
            for (Authorise i:authorises){
                Map data = new HashMap<>();
                //通过uuid找到user
                data.put("requesterRealName",userInfoMapper.selectById(i.getRequesterUuid()).getRealName());

                data.put("authorise",i);

                data.put("publish",fileGroupService.getOneByPublishId(i.getPublishId()));
                res.add(data);
            }
            dataRes.put("data",res);
            return Result.ok(dataRes);
        }
        //是领导
        else if (user.getRoleId().equals(2)) {
            Page<Authorise> page = new Page<>(currentPage,pageSize);
            List<Authorise> authorises = authoriseMapper.selectRole2UsePage(order, uuid, page);
            Integer maxPages = authoriseMapper.selectMaxPageRole2(uuid,pageSize);
            dataRes.put("maxPages",maxPages);
            for (Authorise i:authorises){
                Map data = new HashMap<>();

                //通过uuid找到user
                data.put("requesterRealName",userInfoMapper.selectById(i.getRequesterUuid()).getRealName());

                data.put("authorise",i);
                data.put("publish",fileGroupService.getOneByPublishId(i.getPublishId()));
                res.add(data);
            }
            dataRes.put("data",res);
            return Result.ok(dataRes);
        }
        return Result.ok("用户无身份, uuid为"+uuid);
    }

    @Override
    public Result getOneAuth(Integer authoriseId) {
        Authorise authorise = authoriseMapper.selectById(authoriseId);
        if (authorise.getIsAuthorise()!=1){
            authorise.setTime(authorise.getTime());
            return Result.ok(authorise);
        }else{
            authorise.setTime(authorise.getTime());
            Map data = new HashMap<>();
            String urlStr= URLEnum.HOST_URL.getUrl()+"auth/download/"+authoriseId+"/"+authorise.getSecretKey();
            data.put("url",urlStr);
            data.put("authorise",authorise);
            return Result.ok(data);
        }

    }

    @Override
    public ResponseEntity<Resource> downloadAuth(Integer authoriseId, String secretKey) {
        Authorise authorise = authoriseMapper.selectById(authoriseId);
        //验证密码正确性(1.密码和数据库中相同 2.密码长度为36)
        if (authorise.getSecretKey().equals(secretKey)&&authorise.getSecretKey().toCharArray().length==36){
            String filePath = authorise.getFilePath();
            System.out.println("filePath = " + filePath);
            Path realPath = pathUtils.getRealPath(filePath);
            Resource resource = resourceLoader.getResource("file:" + realPath);

            System.out.println("realPath = " + realPath.toString());

            // 确保文件存在
            if (resource.exists() || resource.isReadable()) {
                // 设置响应头以便浏览器下载文件
                HttpHeaders headers = new HttpHeaders();
                String encodedFileName = null;
                try {
                    encodedFileName = URLEncoder.encode(realPath.getFileName().toString(), "UTF-8").replace("+", "%20");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName);
                headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return null;
    }
}




