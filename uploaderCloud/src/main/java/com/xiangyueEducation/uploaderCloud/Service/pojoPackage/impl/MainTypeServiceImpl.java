package com.xiangyueEducation.uploaderCloud.Service.pojoPackage.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiangyueEducation.uploaderCloud.POJO.MainType;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.MainTypeService;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.mapper.MainTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author 86136
* @description 针对表【main_type】的数据库操作Service实现
* @createDate 2024-05-20 22:13:25
*/
@Service
public class MainTypeServiceImpl extends ServiceImpl<MainTypeMapper, MainType>
    implements MainTypeService{

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private MainTypeMapper mainTypeMapper;

    @Override
    public Result getMainType(String token) {
        String uuid = jwtHelper.getUserId(token);
        String[] mainTypeNames = mainTypeMapper.selectAllMainTypeName();
        return Result.ok(mainTypeNames);

    }
}




