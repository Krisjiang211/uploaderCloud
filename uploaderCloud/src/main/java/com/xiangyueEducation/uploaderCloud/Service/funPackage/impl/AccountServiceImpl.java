package com.xiangyueEducation.uploaderCloud.Service.funPackage.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiangyueEducation.uploaderCloud.DTO.RedisUtil;
import com.xiangyueEducation.uploaderCloud.POJO.*;
import com.xiangyueEducation.uploaderCloud.Service.funPackage.AccountService;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.UserInfoService;
import com.xiangyueEducation.uploaderCloud.Utils.*;
import com.xiangyueEducation.uploaderCloud.VO.LockAccountInfo;
import com.xiangyueEducation.uploaderCloud.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class AccountServiceImpl extends ServiceImpl<UserMapper, User> implements AccountService {


    @Autowired
    public ResourceLoader resourceLoader;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private SelfCloudFileSpaceMapper selfCloudFileSpaceMapper;

    @Autowired
    private DepartmentAndUsersMapper departmentAndUsersMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private RedisUtil redisUtil;


    private UserInfoService userInfoService;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private ProxyUserMapper proxyUserMapper;


    @Override
    public Result login(User user) {
        User realUser = userMapper.selectOne(new QueryWrapper<User>().eq("identity_card_code", user.getIdentityCardCode()));
        System.out.println("realUser = " + realUser);
        boolean b = checkLocked(realUser.getUuid(), "user");
        if (b) {
            return Result.build(new LockAccountInfo(realUser.getUuid(),
                    "user",
                    (Integer) redisTemplate.opsForValue().get("login:user:"+realUser.getUuid()),
                   true,
                    redisTemplate.getExpire("login:user:"  + realUser.getUuid(),TimeUnit.MILLISECONDS)),
                    ResultCodeEnum.USER_LOGIN_LOCK);
        }


        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("identity_card_code", user.getIdentityCardCode());
        User userLogin = userMapper.selectOne(queryWrapper);
        //取到数据库中的User
        //1. 对比账号看是否存在
        if (userLogin == null) {
            return Result.build(null, ResultCodeEnum.ID_ERROR);
        }
        //2. 查看密码是否正确
        String realName=userInfoMapper.selectById(userLogin.getUuid()).getRealName();

        if (!StringUtils.isEmpty(userLogin.getPwd())
                && MD5Util.encrypt(user.getPwd()).equals(userLogin.getPwd())) {
            //是否停用
            if(userLogin.getIsDelete()==1){
                return Result.build("账户已停用", ResultCodeEnum.USER_STOP);
            }
            //密码正确
            //2.1 生成token
            String token = jwtHelper.createToken(userLogin.getUuid());

            //2.2 装配token
            Map data = new HashMap();
            data.put("token", token);

            log.info("用户(realName) "+realName+" 成功登录~~~");

            return Result.ok(data);
        }
        log.warn("用户(realName) "+realName+" 登录失败!!!!原因:  密码输入错误");
        LockAccountInfo lockInfo = lockAccount(realUser.getUuid(), "user");
        //密码不正确
        return Result.build(lockInfo, ResultCodeEnum.PASSWORD_ERROR);
    }

    @Override
    public boolean checkLocked(String uuid, String role) {

        Integer times =(Integer) redisTemplate.opsForValue().get("login:" + role + ":" + uuid);
        //没有输错过密码
        if (times==null){
            return false;
        }

        if (times.intValue()>=5){
            return true;
        }
        return false;
    }

    @Override
    public LockAccountInfo lockAccount(String uuid, String role) {
        //检查登录失败次数
        //格式  login:role:uuid : 次数
        Integer times=(Integer) redisTemplate.opsForValue().get("login:" + role + ":" + uuid);
        //如果不存在则设置初始值为1
        if (times==null){
            redisTemplate.opsForValue().set("login:" + role + ":" + uuid,1,1,TimeUnit.DAYS);
            return new LockAccountInfo(uuid,role,1,false,0L);
        }
        //为登录失败次数加一
        redisTemplate.opsForValue().increment("login:" + role + ":" + uuid);
        //如果次数为5了,那么设置过期时间为1天
        if (times.intValue()+1>=5){
            redisTemplate.opsForValue().set("login:" + role + ":" + uuid,5,1, TimeUnit.DAYS);
            return new LockAccountInfo(uuid,role,5,true,redisTemplate.getExpire("login:" + role + ":" + uuid,TimeUnit.MILLISECONDS));
        }
        return null;
    }


    @Override
    public Result checkIDUsed(String ID) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("identity_card_code", ID);
        Long times = userMapper.selectCount(queryWrapper);
        if (times != 0) {
            return Result.build(null, ResultCodeEnum.ID_USED);
        }
        return Result.ok("未被使用");
    }


    /**
     * 注册成功后返回值里面会有token一起返回,方便后面的设置userInfo
     * @param user
     * @return token
     */
    @Override
    public Result register(User user) {
        Result usedResult = checkIDUsed(user.getIdentityCardCode());
        if (usedResult.getCode() == 504) {
            return usedResult;
        }
        user.setPwd(MD5Util.encrypt(user.getPwd()));
        user.setUuid(UUID.randomUUID().toString());
        user.setIsDelete(0);
        //插入user表
        userMapper.insert(user);

        String token=jwtHelper.createToken(user.getUuid());

        UserInfo userInfo = new UserInfo();
        DepartmentAndUsers departmentAndUsers = new DepartmentAndUsers();
        userInfo.setUuid(user.getUuid());
        // 设置默认头像的路径
        userInfo.setAvatarPath(PathEnum.DEFAULT_AVATAR.getPath());
        userInfo.setPreviewAvatarPath(PathEnum.DEFAULT_AVATAR.getPath());


        departmentAndUsers.setUuid(user.getUuid());
        departmentAndUsers.setDepartmentId(user.getDepartmentId());
        userInfoMapper.insert(userInfo);
        departmentAndUsersMapper.insert(departmentAndUsers);

        Map<String,String> data = new HashMap();
        data.put("msg","恭喜账号注册成功");
        data.put("token",token);
        log.info("用户(ID) "+user.getIdentityCardCode()+" 注册成功~~~");
        return Result.ok(data);
    }




    @Override
    public Result checkLogin(String token) {
        if (jwtHelper.isExpiration(token)) {
            return Result.build(null, ResultCodeEnum.NOTLOGIN);
        }
        return Result.ok("登录ing");
    }

    @Override
    public Result changePwd(HashMap<String, String> pwdInfo, String token) {
        //1. 检测token是否过期
        if (jwtHelper.isExpiration(token)) {
            return Result.build(null, ResultCodeEnum.NOTLOGIN);
        }
        //2. 根据token获取用户ID
        String userId = jwtHelper.getUserId(token);

        //2.1 检测是是否是proxyUser用户
        List<ProxyUser> proxyUsers = proxyUserMapper.selectList(new QueryWrapper<ProxyUser>());
        for (ProxyUser proxyUser : proxyUsers) {
            if (proxyUser.getUuid().equals(userId)) {
                return Result.build("proxyUser用户无法修改密码", ResultCodeEnum.PROXY_USER_ERROR);
            }
        }


        //3. 获取User对象
        User user = userMapper.selectById(userId);

        //4. 查看密码是否正确
        if (!StringUtils.isEmpty(user.getPwd())
                && MD5Util.encrypt(pwdInfo.get("originalPwd")).equals(user.getPwd())) {
            //密码正确
            //修改新密码
            user.setPwd(MD5Util.encrypt(pwdInfo.get("newPwd")));
            userMapper.updateById(user);
            log.warn("用户(ID) "+user.getIdentityCardCode()+" 修改密码成功~~~");
            return Result.ok("密码修改成功");
        }

        //密码不正确
        log.warn("用户(ID) "+user.getIdentityCardCode()+" 修改密码失败~~~\n失败原因: 输入原密码错误");
        return Result.build("原密码错误", ResultCodeEnum.PASSWORD_ERROR);


    }



}
