package com.xiangyueEducation.uploaderCloud.Service.pojoPackage.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiangyueEducation.uploaderCloud.POJO.MainAndViceType;
import com.xiangyueEducation.uploaderCloud.POJO.MainType;
import com.xiangyueEducation.uploaderCloud.POJO.ViceType;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.MainAndViceTypeService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.mapper.MainAndViceTypeMapper;
import com.xiangyueEducation.uploaderCloud.mapper.MainTypeMapper;
import com.xiangyueEducation.uploaderCloud.mapper.ViceTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
* @author 86136
* @description 针对表【main_and_vice_type】的数据库操作Service实现
* @createDate 2024-05-20 22:13:25
*/
@Service
@Slf4j
public class MainAndViceTypeServiceImpl extends ServiceImpl<MainAndViceTypeMapper, MainAndViceType>
    implements MainAndViceTypeService{

    @Autowired
    private MainTypeMapper mainTypeMapper;

    @Autowired
    private MainAndViceTypeMapper mainAndViceTypeMapper;

    @Autowired
    private ViceTypeMapper viceTypeMapper;




    @Override
    public Result getViceType(String token) {
        ArrayList<List<Integer>> listViceTypeWithMainType = new ArrayList<>();
        // 先拿到所有的mainType的id
        MainType[] mainTypes = mainTypeMapper.selectAll();
        ArrayList<Integer> listMainTypeId = new ArrayList<>();
        //listViceTypeWithMainType的子列表
        ArrayList<Integer> subListViceTypeId = new ArrayList<>();

        for (MainType mainType:mainTypes){
            listMainTypeId.add(mainType.getMainTypeId());
        }
        // 之后选出mainAndViceType表中所有东西
        MainAndViceType[] mainAndViceTypes = mainAndViceTypeMapper.selectAll();

        for (Integer mainTypeId:listMainTypeId){
            for (MainAndViceType mainAndViceType:mainAndViceTypes){
                if (mainAndViceType.getMainTypeId().equals(mainTypeId)){
                    subListViceTypeId.add(mainAndViceType.getViceTypeId());
                }
            }
            listViceTypeWithMainType.add(new ArrayList<>(subListViceTypeId));
            subListViceTypeId.clear();
        }
        Map data = new HashMap<>();
        //new一个tmpList(临时列表,用于存储viceType列表)
        List<String> tmpList = new ArrayList<>();
        for (int i=0;i<mainTypes.length;i++){
            //获取mainType
            MainType mainType = mainTypes[i];
            //获取viceType
            for (Integer viceTypeId:listViceTypeWithMainType.get(i)){
                tmpList.add(viceTypeMapper.selectById(viceTypeId).getName());
            }
            data.put(mainType.getName(),new ArrayList<>(tmpList));
            tmpList.clear();
        }
        return Result.ok(data);
    }

    @Override
    public Result getMainAndViceTypeId(Map map) {
        String mainType = (String) map.get("mainType");
        QueryWrapper<MainType> mainTypeWrapper = new QueryWrapper<>();
        Integer mainTypeId= mainTypeMapper.selectOne(mainTypeWrapper.eq("name", mainType)).getMainTypeId();
        String viceType = (String) map.get("viceType");
        QueryWrapper<ViceType> viceTypeWrapper = new QueryWrapper<>();
        Integer viceTypeId = viceTypeMapper.selectOne(viceTypeWrapper.eq("name", viceType)).getViceTypeId();
        QueryWrapper<MainAndViceType> wrapper = new QueryWrapper<>();
        wrapper.eq("main_type_id",mainTypeId).eq("vice_type_id",viceTypeId);
        Integer mainAndViceTypeId = mainAndViceTypeMapper.selectOne(wrapper).getMainAndViceTypeId();
        Map<String,Integer> resData = new HashMap<>();
        resData.put("mainAndViceTypeId",mainAndViceTypeId);
        return Result.ok(resData);
    }

    @Override
    public Result getOneToOne() {

        List<MainAndViceType> mainAndViceTypes = mainAndViceTypeMapper.selectList(null);
        List<MainType> mainTypes = mainTypeMapper.selectList(null);
        List<ViceType> viceTypes = viceTypeMapper.selectList(null);
        ArrayList<List> MVTypeList = new ArrayList<>();
        ArrayList<Integer> IdList = new ArrayList<>();

        mainAndViceTypes.stream().forEach(mainAndViceType -> {
            IdList.add(mainAndViceType.getMainAndViceTypeId());
            ArrayList<String> MVTypeName = new ArrayList<>();

            mainTypes.stream().anyMatch(mainType -> {
                if (mainAndViceType.getMainTypeId().equals(mainType.getMainTypeId())){
                    MVTypeName.add(mainType.getName());
                    return true;
                }
                return false;
            });

            viceTypes.stream().anyMatch(viceType -> {
                if (mainAndViceType.getViceTypeId().equals(viceType.getViceTypeId())){
                    MVTypeName.add(viceType.getName());
                    return true;
                }
                return false;
            });
            MVTypeList.add(MVTypeName);
        });
        HashMap<String, Object> map = MapUtil.of("IdList", IdList);
        map.put("MVType", MVTypeList);

        return Result.ok(map);
    }


}




