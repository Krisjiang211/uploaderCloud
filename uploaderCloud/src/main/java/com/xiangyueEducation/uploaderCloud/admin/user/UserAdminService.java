package com.xiangyueEducation.uploaderCloud.admin.user;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.POJO.DepartmentAndUsers;
import com.xiangyueEducation.uploaderCloud.POJO.User;
import com.xiangyueEducation.uploaderCloud.POJO.UserInfo;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.UserInfoService;
import com.xiangyueEducation.uploaderCloud.Utils.BaseUtils.BaseUtilsJiang;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.ResultCodeEnum;
import com.xiangyueEducation.uploaderCloud.Utils.URLEnum;
import com.xiangyueEducation.uploaderCloud.admin.VO.UserAndUserInfoVO;
import com.xiangyueEducation.uploaderCloud.mapper.DepartmentAndUsersMapper;
import com.xiangyueEducation.uploaderCloud.mapper.DepartmentMapper;
import com.xiangyueEducation.uploaderCloud.mapper.UserInfoMapper;
import com.xiangyueEducation.uploaderCloud.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
@Slf4j
public class UserAdminService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DepartmentMapper departmentMapper;


    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private BaseUtilsJiang baseUtilsJiang;

    @Autowired
    private DepartmentAndUsersMapper departmentAndUsersMapper;

    @Autowired
    private UserInfoService userInfoService;
    public Result getAllUser(Integer currentPage){
        Map res= new HashMap();
        Integer pageSize=12;
        Page<UserAndUserInfoVO> page = new Page<>(currentPage,pageSize);
        List<UserAndUserInfoVO> userAndUserInfoVOS = userMapper.selectAll(page);
        List<Map> data=new ArrayList<>();
        for (UserAndUserInfoVO userAndUserInfoVO:userAndUserInfoVOS){


            Map item = baseUtilsJiang.objectToMap(userAndUserInfoVO);
            item.remove("avatarPath");
            item.remove("previewAvatarPath");
            //获取图片的URL
            item.put("imgURL", URLEnum.HOST_URL.getUrl()+URLEnum.AVATAR_RESOURCE_HEADER.getUrl()+userAndUserInfoVO.getAvatarPath().split("/")[userAndUserInfoVO.getAvatarPath().split("/").length-1]);
            data.add(item);

        }
        res.put("list",data);
        res.put("maxPage",page.getPages());

        return Result.ok(res);
    }



    public Result disabledUser(String uuid){
        User user = userMapper.selectById(uuid);
        user.setIsDelete(1);
        userMapper.updateById(user);
        return Result.ok("禁用成功");
    }

    public Result editUser(User user){
        User oldUser = userMapper.selectById(user.getUuid());
        //阻止对uuid进行的更改
        if (!oldUser.getUuid().equals(user.getUuid())){
            log.error("有危险的异常操作,uuid被篡改,对数据库进行攻击");
            return Result.ok("危险的异常操作,已经报告");
        }
        //检查是否更新了这个部门
        if (!oldUser.getDepartmentId().equals(user.getDepartmentId())){
            QueryWrapper<DepartmentAndUsers> wrapper = new QueryWrapper<>();
            wrapper.eq("uuid",user.getUuid());
            departmentAndUsersMapper.delete(wrapper);//删除原 员工-部门 信息
            departmentAndUsersMapper.insert(new DepartmentAndUsers(user.getDepartmentId(),user.getUuid()));//增加新改变的 员工-部门 信息
            userMapper.updateById(user);//更新user表
            return Result.ok("用户信息修改成功");
        }
        userMapper.updateById(user);
        return Result.ok("用户信息修改成功");
    }

    public Result editUserInfo(UserInfo userInfo){
        userInfoMapper.updateById(userInfo);
        return Result.ok("用户信息修改成功");
    }





    //通过department来查询用户
    Result getUserByDepartmentName(Integer currentPage,String departmentName){
        Map res= new HashMap();
        Integer pageSize=12;
        Page<Map> page = new Page<>(currentPage,pageSize);
        //获取到departmentId
        Integer depId = departmentMapper.getIdByName(departmentName);
        List<String> uuidS = departmentAndUsersMapper.getUserPageByDepartmentName(page, depId);
        if (uuidS.size()==0){
            return Result.build("没有任何结果", ResultCodeEnum.QUERY_EMPTY);
        }
        List<Map> data=new ArrayList<>();
        for (String uuid:uuidS){
            User user = userMapper.selectById(uuid);
            UserInfo userInfo = userInfoMapper.selectById(uuid);

            Map itemA = baseUtilsJiang.objectToMap(user);
            Map itemB = baseUtilsJiang.objectToMap(userInfo);
            itemA.putAll(itemB);
            itemA.remove("avatarPath");
            itemA.remove("previewAvatarPath");
            itemA.remove("departmentId");
            itemA.remove("pwd");
            itemA.put("departmentName",departmentName);
            itemA.put("imgURL",userInfoService.getUserAvatarUrl(uuid));
            data.add(itemA);
        }
        res.put("list",data);
        res.put("maxPage",page.getPages());

        return Result.ok(res);

    }

    Result getUserByUserName(Integer currentPage,String userName){
        Map res= new HashMap();
        Integer pageSize=12;
        Page<Map> page = new Page<>(currentPage,pageSize);
        List<String> uuidS = userInfoMapper.getUuidByUserNameSearch(page, userName);
        if (uuidS.size()==0){
            return Result.build("没有任何结果", ResultCodeEnum.QUERY_EMPTY);
        }
        List<Map> data=new ArrayList<>();
        for (String uuid:uuidS){
            User user = userMapper.selectById(uuid);
            UserInfo userInfo = userInfoMapper.selectById(uuid);

            Map itemA = baseUtilsJiang.objectToMap(user);
            Map itemB = baseUtilsJiang.objectToMap(userInfo);
            itemA.putAll(itemB);
            itemA.remove("avatarPath");
            itemA.remove("previewAvatarPath");
            itemA.remove("departmentId");
            itemA.remove("pwd");
            itemA.put("departmentName",departmentMapper.getNameById(user.getDepartmentId()));
            itemA.put("imgURL",userInfoService.getUserAvatarUrl(uuid));
            data.add(itemA);
        }
        res.put("list",data);
        res.put("maxPage",page.getPages());

        return Result.ok(res);
    }


    public Result editUserStatus(String uuid){
        User user = userMapper.selectById(uuid);
        Integer isDelete = user.getIsDelete();
        if (isDelete==null){
            user.setIsDelete(0);
            userMapper.updateById(user);
            return Result.build("不存在状态,已经默认设为0", ResultCodeEnum.QUERY_EMPTY);
        }
        if (isDelete.equals(1)){
            user.setIsDelete(0);
            userMapper.updateById(user);
        }else{
            user.setIsDelete(1);
            userMapper.updateById(user);
        }
        return Result.ok("操作成功");
    }
}
