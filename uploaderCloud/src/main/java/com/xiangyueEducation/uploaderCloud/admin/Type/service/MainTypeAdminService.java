package com.xiangyueEducation.uploaderCloud.admin.Type.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiangyueEducation.uploaderCloud.POJO.MainType;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.mapper.MainTypeMapper;
import com.xiangyueEducation.uploaderCloud.mapper.ViceTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Service
public class MainTypeAdminService {

    @Autowired
    private MainTypeMapper mainTypeMapper;

    public Result delOne(String mainTypeName){
        QueryWrapper<MainType> wrapper = new QueryWrapper<>();
        wrapper.eq("name",mainTypeName);
        MainType mainType = mainTypeMapper.selectOne(wrapper);
        mainType.setIsDelete(1);
        mainTypeMapper.updateById(mainType);
        return Result.ok("删除成功");

    }


    public Result changeMainTypeStatus(Integer mainTypeId){
        MainType mainType = mainTypeMapper.selectById(mainTypeId);
        if (mainType.getIsDelete() == 0) {
            mainType.setIsDelete(1);
            mainTypeMapper.updateById(mainType);
            return Result.ok("修改状态成功");
        } else {
            mainType.setIsDelete(0);
            mainTypeMapper.updateById(mainType);
            return Result.ok("修改状态成功");
        }
    }





}
