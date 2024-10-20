package com.xiangyueEducation.uploaderCloud.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.POJO.Publish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 86136
* @description 针对表【publish】的数据库操作Mapper
* @createDate 2024-05-27 23:04:17
* @Entity com.xiangyueEducation.uploaderCloud.POJO.Publish
*/
public interface PublishMapper extends BaseMapper<Publish> {

    //通过departmentId来查找publish(下方为配套的mainAndViceType模式)
    Publish[] selectPublishByDepartmentId(Integer departmentId, String order,Page<Publish> page);
    Publish[] selectPublishByDepartmentIdMainAndViceTypeId(Integer mainAndViceTypeId,Integer departmentId, String order,Page<Publish> page);

    //查找最大页数(范围:部门)(下方为配套的mainAndViceType模式)
    Integer getMaxPages(Integer departmentId,Integer pageSize);
    Integer getMaxPagesMVType(Integer mainAndViceTypeId,Integer departmentId,Integer pageSize);






    //通过departmentId和fileGroupCate来查找publish
    Publish[] selectPublishByDepartmentIdAndFileGroupCate(Integer fileGroupCateId,Integer departmentId, String order,Page<Publish> page);
    Publish[] selectPublishByDepartmentIdAndFileGroupCateMVType(Integer mainAndViceTypeId,Integer fileGroupCateId,Integer departmentId, String order,Page<Publish> page);
    //配套selectPublishByDepartmentIdAndFileGroupCate选择出文件组的种类最大数量
    Integer getMaxPagesFileGroupCate(Integer fileGroupCategoryId,Integer departmentId,Integer pageSize);
    Integer getMaxPagesFileGroupCateMVType(Integer mainAndViceTypeId,Integer fileGroupCategoryId,Integer departmentId,Integer pageSize);



    //搜索模式,搜索标题
    List<Publish> getSearchValueTimeSequence(String searchValue,Integer departmentId,String order,Page<Publish> page);
    Integer getPageNumsOfSearchValueTimeSequence(String searchValue,Integer departmentId,String order,Integer pageSize);






    //通过文件种类ID,得到publishId
    Integer[] getPublishIdByFileCateGoryId(Integer fileCategoryId,Integer departmentId,String order,Page page);
    Integer[] getPublishIdByFileCateGoryIdMVType(Integer mainAndViceTypeId,Integer fileCategoryId,Integer departmentId,String order,Page page);
    //获取最大页数
    Integer getMaxPagesFileCategory(Integer fileCategoryId,Integer departmentId,Integer pageSize);
    Integer getMaxPagesFileCategoryMVType(Integer mainAndViceTypeId,Integer fileCategoryId,Integer departmentId,Integer pageSize);
}




