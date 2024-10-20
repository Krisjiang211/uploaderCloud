package com.xiangyueEducation.uploaderCloud.Service.pojoPackage.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiangyueEducation.uploaderCloud.POJO.ViceType;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.ViceTypeService;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.mapper.ViceTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author 86136
* @description 针对表【vice_type】的数据库操作Service实现
* @createDate 2024-05-20 22:13:25
*/
@Service
public class ViceTypeServiceImpl extends ServiceImpl<ViceTypeMapper, ViceType>
    implements ViceTypeService{

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private ViceTypeMapper viceTypeMapper;
    @Override
    public Result getViceType(String token) {
        String uuid = jwtHelper.getUserId(token);
        String[] viceTypeNames = viceTypeMapper.selectAllViceTypeName();
        return Result.ok(viceTypeNames);
    }
}




