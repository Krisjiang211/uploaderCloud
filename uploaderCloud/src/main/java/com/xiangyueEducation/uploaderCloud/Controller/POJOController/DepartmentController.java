package com.xiangyueEducation.uploaderCloud.Controller.POJOController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.POJO.Department;
import com.xiangyueEducation.uploaderCloud.POJO.User;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.DepartmentService;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.UserService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.mapper.DepartmentMapper;
import com.xiangyueEducation.uploaderCloud.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("department")
@CrossOrigin
public class DepartmentController {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private DepartmentService departmentService;

    /**
     * 根据名字或Id获取 某一个 Department信息
     * @param object
     * @param mode
     * @return
     */

    @GetMapping("get/{mode}/{object}")
    public Result getUserTableById(@PathVariable("object") Object object,
                                   @PathVariable("mode") Integer mode){
        //根据名字选择Table
        if (mode == 1){
            String depName=(String) object;
            QueryWrapper<Department> wrapper = new QueryWrapper<>();
            wrapper.eq("department_name",depName);
            Department department = departmentMapper.selectOne(wrapper);
            return Result.ok(department);
        }
        //根据Id选择Table
        else if (mode == 2){
            Integer depId=(Integer) object;
            Department department = departmentMapper.selectById(depId);
            return Result.ok(department);
        }
        return null;
    }


    @GetMapping("getAll/{currentPage}")
    public Result getAllDepartment(@PathVariable("currentPage") Integer currentPage){
        Integer pageSize=15;
        Page<Department> page = new Page<>(currentPage, pageSize);
        List<Department> departments = departmentMapper.selectAll(page);
        return Result.ok(departments);
    }

}