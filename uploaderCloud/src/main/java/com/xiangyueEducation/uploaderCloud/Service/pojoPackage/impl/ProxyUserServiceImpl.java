package com.xiangyueEducation.uploaderCloud.Service.pojoPackage.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiangyueEducation.uploaderCloud.POJO.ProxyUser;
import com.xiangyueEducation.uploaderCloud.POJO.User;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.ProxyUserService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.mapper.DepartmentMapper;
import com.xiangyueEducation.uploaderCloud.mapper.ProxyUserMapper;
import com.xiangyueEducation.uploaderCloud.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author 86136
* @description 针对表【proxy_user】的数据库操作Service实现
* @createDate 2024-09-23 11:39:13
*/
@Service
public class ProxyUserServiceImpl extends ServiceImpl<ProxyUserMapper, ProxyUser>
    implements ProxyUserService{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProxyUserMapper proxyUserMapper;

    @Autowired
    private DepartmentMapper departmentMapper;


    @Override
    public Result getAllProxyUser() {
        ArrayList<Map> resList = new ArrayList<>();
        List<ProxyUser> proxyUsers = proxyUserMapper.selectList(new QueryWrapper<ProxyUser>());
        for (ProxyUser proxyUser : proxyUsers) {
            User user = userMapper.selectById(proxyUser.getUuid());
            HashMap<String, Object> map = new HashMap<>();
            map.put("uuid",user.getUuid());
            map.put("departmentId",user.getDepartmentId());
            map.put("departmentName",departmentMapper.selectById(user.getDepartmentId()).getDepartmentName());
            map.put("identityCardCode",user.getIdentityCardCode());
            map.put("pwd",proxyUser.getRealPwd());
            resList.add(map);
        }
        return Result.ok(resList);


    }
}




