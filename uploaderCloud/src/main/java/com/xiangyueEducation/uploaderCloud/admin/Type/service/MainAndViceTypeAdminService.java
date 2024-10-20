package com.xiangyueEducation.uploaderCloud.admin.Type.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiangyueEducation.uploaderCloud.POJO.MainAndViceType;
import com.xiangyueEducation.uploaderCloud.POJO.MainType;
import com.xiangyueEducation.uploaderCloud.POJO.ViceType;
import com.xiangyueEducation.uploaderCloud.Utils.BaseUtils.BaseUtilsJiang;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.ResultCodeEnum;
import com.xiangyueEducation.uploaderCloud.mapper.MainAndViceTypeMapper;
import com.xiangyueEducation.uploaderCloud.mapper.MainTypeMapper;
import com.xiangyueEducation.uploaderCloud.mapper.ViceTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MainAndViceTypeAdminService {

    @Autowired
    private MainTypeMapper mainTypeMapper;

    @Autowired
    private MainAndViceTypeMapper mainAndViceTypeMapper;

    @Autowired
    private ViceTypeMapper viceTypeMapper;

    public Result getAll(){
        List<Map> data = new ArrayList<>();
        MainAndViceType[] mainAndViceTypes = mainAndViceTypeMapper.selectAll();
        for (MainAndViceType mainAndViceType : mainAndViceTypes){
            Map item = new HashMap<>();
            item.put("mainAndViceTypeId",mainAndViceType.getMainAndViceTypeId());
            item.put("mainTypeId",mainAndViceType.getMainTypeId());
            item.put("viceTypeId",mainAndViceType.getViceTypeId());
            item.put("mainTypeName",mainTypeMapper.selectById(mainAndViceType.getMainTypeId()).getName());
            item.put("viceTypeName",viceTypeMapper.selectById(mainAndViceType.getViceTypeId()).getName());
            item.put("MVTypeName", mainTypeMapper.selectById(mainAndViceType.getMainTypeId()).getName()+"   "+viceTypeMapper.selectById(mainAndViceType.getViceTypeId()).getName());
            item.put("time", mainAndViceType.getTime());
            item.put("isDelete",mainAndViceType.getIsDelete());
            data.add(item);
        }
        return Result.ok(data);
    }

    public Result getAllTable(){
//        Map data = new HashMap<>();
        ArrayList data = new ArrayList<>();
        MainType[] mainTypes = mainTypeMapper.selectAll();
        for (MainType mainType : mainTypes){
            Map item = new HashMap<>();
            item.put("mainType",mainType);
            //获取一个mainType对应的viceType
            QueryWrapper<MainAndViceType> wrapper = new QueryWrapper<>();
            wrapper.eq("main_type_id",mainType.getMainTypeId());
            List<MainAndViceType> mainAndViceTypes = mainAndViceTypeMapper.selectList(wrapper);
            ArrayList viceTypeList = new ArrayList<>();
            for (MainAndViceType mainAndViceType : mainAndViceTypes){
                ViceType viceType = viceTypeMapper.selectById(mainAndViceType.getViceTypeId());
                viceTypeList.add(viceType);
            }
            item.put("viceType",viceTypeList);
            data.add(item);
        }
        return Result.ok(data);
    }


    public Result addOnePair(String mainTypeName, String viceTypeName) {
        //取出mainType类
        QueryWrapper<MainType> wrapperM = new QueryWrapper<>();
        wrapperM.eq("name",mainTypeName);
        MainType mainType = mainTypeMapper.selectOne(wrapperM);
        if (mainType == null){
            return Result.build("这个主类不存在于数据库中,请再检查一下",ResultCodeEnum.QUERY_EMPTY);
        }

        //取出ViceType
        QueryWrapper<ViceType> wrapperV = new QueryWrapper<>();
        wrapperV.eq("name",viceTypeName);
        ViceType viceType = viceTypeMapper.selectOne(wrapperV);

        if (viceType == null){
            return Result.build("这个副类不存在于数据库中,请再检查一下",ResultCodeEnum.QUERY_EMPTY);
        }
        //查重
        QueryWrapper<MainAndViceType> wrapperMV = new QueryWrapper<>();
        wrapperMV.eq("main_type_id",mainType.getMainTypeId())
                .eq("vice_type_id",viceType.getViceTypeId());
        MainAndViceType mainAndViceTypeTest = mainAndViceTypeMapper.selectOne(wrapperMV);
        if (mainAndViceTypeTest != null){
            return Result.build("这个联系已经存在",ResultCodeEnum.QUERY_HAVE_SAME);
        }

        //添加一个联系
        MainAndViceType mainAndViceType = new MainAndViceType();
        mainAndViceType.setMainTypeId(mainType.getMainTypeId());
        mainAndViceType.setViceTypeId(viceType.getViceTypeId());
        //判断一下主类 和 副类有没有为isDelete为1的
        if (mainType.getIsDelete().equals(1) || viceType.getIsDelete().equals(1)){
            mainAndViceType.setIsDelete(1);
            mainAndViceTypeMapper.insert(mainAndViceType);
            return Result.ok("添加联系成功  "+mainTypeName+"和"+viceTypeName+"但由于其中一方为已删除状态,所以联系状态为也设为删除状态");
        }
        mainAndViceType.setIsDelete(0);
        //添加
        mainAndViceTypeMapper.insert(mainAndViceType);
        return Result.ok("添加联系成功  "+mainTypeName+"和"+viceTypeName);
    }



    public Result delOnePair(String mainTypeName, String viceTypeName) {
        //取出mainType类
        QueryWrapper<MainType> wrapperM = new QueryWrapper<>();
        wrapperM.eq("name",mainTypeName);
        MainType mainType = mainTypeMapper.selectOne(wrapperM);

        //取出ViceType
        QueryWrapper<ViceType> wrapperV = new QueryWrapper<>();
        wrapperV.eq("name",viceTypeName);
        ViceType viceType = viceTypeMapper.selectOne(wrapperV);

        //取出MainAndViceType
        QueryWrapper<MainAndViceType> wrapperMV = new QueryWrapper<>();
        wrapperMV.eq("main_type_id",mainType.getMainTypeId())
                .eq("vice_type_id",viceType.getViceTypeId());
        MainAndViceType mainAndViceType = mainAndViceTypeMapper.selectOne(wrapperMV);
        mainAndViceType.setIsDelete(1);
        int i = mainAndViceTypeMapper.updateById(mainAndViceType);
        return Result.ok("删除联系成功  "+mainTypeName+"和"+viceTypeName);

    }


    public Result editOnePair(Map mainAndViceType){
        Integer mainTypeId = (Integer)mainAndViceType.get("mainTypeId");
        Integer viceTypeId = (Integer)mainAndViceType.get("viceTypeId");
        String mainTypeName = (String)mainAndViceType.get("mainTypeName");
        String viceTypeName = (String)mainAndViceType.get("viceTypeName");

        //取出mainType类
        MainType mainType = mainTypeMapper.selectById(mainTypeId);
        mainType.setName(mainTypeName);
        mainTypeMapper.updateById(mainType);

        //取出ViceType
        ViceType viceType = viceTypeMapper.selectById(viceTypeId);
        viceType.setName(viceTypeName);
        viceTypeMapper.updateById(viceType);

        return Result.ok("修改联系成功  已经修改为: "+mainTypeName+"和"+viceTypeName);
    }


    public Result changeMainAndViceTypeStatus(Integer mainTypeId,Integer viceTypeId){
        //取出mainType类
        MainType mainType = mainTypeMapper.selectById(mainTypeId);
        //取出ViceType
        ViceType viceType = viceTypeMapper.selectById(viceTypeId);
        //取出MainAndViceType
        QueryWrapper<MainAndViceType> wrapperMV = new QueryWrapper<>();
        wrapperMV.eq("main_type_id",mainType.getMainTypeId())
                .eq("vice_type_id",viceType.getViceTypeId());
        MainAndViceType mainAndViceType = mainAndViceTypeMapper.selectOne(wrapperMV);
        if (mainAndViceType.getIsDelete().equals(1)){
            mainAndViceType.setIsDelete(0);
            mainAndViceTypeMapper.updateById(mainAndViceType);
            return Result.ok("已经修改为活跃状态");
        }else if (mainAndViceType.getIsDelete().equals(0)){
            mainAndViceType.setIsDelete(1);
            mainAndViceTypeMapper.updateById(mainAndViceType);
            return Result.ok("已经修改为停用状态");
        }
        return null;
    }
}
