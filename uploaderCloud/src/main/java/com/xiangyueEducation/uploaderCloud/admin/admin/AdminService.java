package com.xiangyueEducation.uploaderCloud.admin.admin;


import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiangyueEducation.uploaderCloud.POJO.Admin;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.MD5Util;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.ResultCodeEnum;
import com.xiangyueEducation.uploaderCloud.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminService {


    @Autowired
    public ResourceLoader resourceLoader;

    @Autowired
    private AdminMapper adminMapper;


    @Autowired
    private JwtHelper jwtHelper;


    public Result login(Admin admin) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", admin.getAccount());
        Admin userLogin = adminMapper.selectOne(queryWrapper);
        //取到数据库中的User
        //1. 对比账号看是否存在
        if (userLogin == null) {
            return Result.build("不存在的账号", ResultCodeEnum.ID_ERROR);
        }
        //2. 查看密码是否正确

        if (!StringUtils.isEmpty(userLogin.getPwd())
                && MD5Util.encrypt(admin.getPwd()).equals(userLogin.getPwd())) {
            //查看是否停用
            if (userLogin.getIsDelete().equals(1)) {
                return Result.build("账号已被停用", ResultCodeEnum.ADMIN_BAND);
            }

            //密码正确
            //2.1 生成token
            String token = jwtHelper.createToken(userLogin.getAdminId());

            //2.2 装配token
            Map data = new HashMap();
            data.put("token", token);


            return Result.ok(data);
        }
        //密码不正确
        return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
    }


    public Result checkIDUsed(String ID) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", ID);
        Long times = adminMapper.selectCount(queryWrapper);
        if (times != 0) {
            return Result.build(null, ResultCodeEnum.ID_USED);
        }
        return Result.ok("未被使用");
    }


    /**
     * 注册成功后返回值里面会有token一起返回,方便后面的设置userInfo
     * @param admin
     * @return token
     */
    public Result register(Admin admin) {
        Result usedResult = checkIDUsed(admin.getAccount());
        if (usedResult.getCode() == 504) {
            return usedResult;
        }
        admin.setPwd(MD5Util.encrypt(admin.getPwd()));
        admin.setAdminId(UUID.randomUUID().toString());
        admin.setIsDelete(0);
        admin.setRoleId(admin.getRoleId());
        //插入admin表
        adminMapper.insert(admin);

        String token=jwtHelper.createToken(admin.getAdminId());


        Map<String,String> data = new HashMap();
        data.put("msg","恭喜账号注册成功");
        data.put("token",token);
        return Result.ok(data);
    }




    public Result checkLogin(String token) {
        if (jwtHelper.isExpiration(token)) {
            return Result.build(null, ResultCodeEnum.NOTLOGIN);
        }
        return Result.ok("登录ing");
    }

    public Result changePwd(HashMap<String, String> pwdInfo, String token) {
        //1. 检测token是否过期
        if (jwtHelper.isExpiration(token)) {
            return Result.build(null, ResultCodeEnum.NOTLOGIN);
        }
        //2. 根据token获取用户ID
        String adminId = jwtHelper.getUserId(token);

        //3. 获取admin对象
        Admin admin =adminMapper.selectById(adminId);
        //4. 查看密码是否正确
        if (!StringUtils.isEmpty(admin.getPwd())
                && MD5Util.encrypt(pwdInfo.get("originalPwd")).equals(admin.getPwd())) {
            //密码正确
            //修改新密码
            admin.setPwd(MD5Util.encrypt(pwdInfo.get("newPwd")));
            adminMapper.updateById(admin);

            return Result.ok("密码修改成功");
        }

        //密码不正确
        return Result.build("原密码错误", ResultCodeEnum.PASSWORD_ERROR);


    }


    public Result getAllAdmin(){
        Page<Admin> page = new Page<>(1,10000);
        List<Admin> all = adminMapper.getAll(page);

        return Result.ok(all);
    }


    public Result changeStatus(String adminId){
        Admin admin = adminMapper.selectById(adminId);
        if (admin.getAccount().equals("adminsuper")){
            return Result.build("无法对超级管理员操作",ResultCodeEnum.ADMIN_BAND);
        }
        if (admin.getIsDelete().equals(1)){
            admin.setIsDelete(0);
            adminMapper.updateById(admin);
            return Result.ok("账号已启用");
        } else if (admin.getIsDelete().equals(0)) {
            admin.setIsDelete(1);
            adminMapper.updateById(admin);
            return Result.ok("账号已禁用");
        }
        return null;
    }

    public Result edit(Admin admin){
        Admin admin1 = adminMapper.selectById(admin.getAdminId());
        admin1.setAccount(admin.getAccount());
        admin1.setRoleId(admin.getRoleId());
        adminMapper.updateById(admin1);
        return Result.ok("修改成功");
    }

    public Result changePwd(String newPwd,String adminId){
        Admin admin = adminMapper.selectById(adminId);
        admin.setPwd(MD5Util.encrypt(newPwd));
        adminMapper.updateById(admin);
        return Result.ok("密码修改成功");
    }

}
