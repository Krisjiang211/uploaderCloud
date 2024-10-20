package com.xiangyueEducation.uploaderCloud.admin.Type.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiangyueEducation.uploaderCloud.POJO.MainAndViceType;
import com.xiangyueEducation.uploaderCloud.POJO.MainType;
import com.xiangyueEducation.uploaderCloud.POJO.ViceType;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.mapper.MainAndViceTypeMapper;
import com.xiangyueEducation.uploaderCloud.mapper.ViceTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@Service
public class ViceTypeAdminService {
    @Autowired
    private ViceTypeMapper viceTypeMapper;
    @Autowired
    private MainAndViceTypeMapper mainAndViceTypeMapper;

    public Result delOne(String viceTypeName){
        QueryWrapper<ViceType> wrapper = new QueryWrapper<>();
        wrapper.eq("name",viceTypeName);
        ViceType viceType = viceTypeMapper.selectOne(wrapper);
        viceType.setIsDelete(1);
        viceTypeMapper.updateById(viceType);

        return Result.ok("删除成功");
    }

    public Result changeViceTypeStatus(Integer viceTypeId){
        ViceType viceType = viceTypeMapper.selectById(viceTypeId);
        if (viceType.getIsDelete().equals(1)){
            viceType.setIsDelete(0);
            viceTypeMapper.updateById(viceType);
            return Result.ok("恢复活跃状态成功");
        }
        else if (viceType.getIsDelete().equals(0)){
            //更新viceType
            viceType.setIsDelete(1);
            viceTypeMapper.updateById(viceType);
            //更新 主-副类
            QueryWrapper<MainAndViceType> wrapper = new QueryWrapper<>();
            wrapper.eq("vice_type_id",viceTypeId);
            List<MainAndViceType> mainAndViceTypes = mainAndViceTypeMapper.selectList(wrapper);

            for (MainAndViceType mainAndViceType : mainAndViceTypes) {
                mainAndViceType.setIsDelete(1);
                mainAndViceTypeMapper.updateById(mainAndViceType);
            }
            return Result.ok("设为停用状态成功");
        }
        return null;
    }

    public Result delViceType(Integer viceTypeId){
        ViceType viceType = viceTypeMapper.selectById(viceTypeId);
        //先去MainAndViceType中删除引用
        mainAndViceTypeMapper.delete(new QueryWrapper<MainAndViceType>().eq("vice_type_id", viceTypeId));
        viceTypeMapper.deleteById(viceTypeId);
        return Result.ok("删除成功");
    }

}
