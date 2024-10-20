package com.xiangyueEducation.uploaderCloud.Service.pojoPackage.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiangyueEducation.uploaderCloud.POJO.User;
import com.xiangyueEducation.uploaderCloud.POJO.UserInfo;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.UserInfoService;
import com.xiangyueEducation.uploaderCloud.Service.pojoPackage.UserService;
import com.xiangyueEducation.uploaderCloud.Utils.*;
import com.xiangyueEducation.uploaderCloud.Utils.BaseUtils.PathUtils;
import com.xiangyueEducation.uploaderCloud.mapper.UserInfoMapper;
import com.xiangyueEducation.uploaderCloud.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
* @author 86136
* @description 针对表【user_info】的数据库操作Service实现
* @createDate 2024-05-18 00:55:21
*/
@Service
@Slf4j
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private PathUtils pathUtils;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result getUserInfo(String token) {
        // 获取用户信息
        UserInfo userInfo=userInfoMapper.selectById(jwtHelper.getUserId(token));
        log.info(userInfo.getRealName()+"获取了一次用户信息");
        return Result.ok(userInfo);
    }

    @Override
    public Result getUser(String token,Boolean wipeOutPwd) {
        String uuid = jwtHelper.getUserId(token);
        User user = userMapper.selectById(uuid);
        if(wipeOutPwd){
            user.setPwd(null);
        return Result.ok(user);
        }
        return Result.ok(user);
    }

    @Override
    public Result updateUserInfo(UserInfo userInfo) {

        userInfoMapper.updateById(userInfo);
        log.info(userInfo.getRealName()+"  修改了用户信息");

        return Result.ok("修改成功啦");


    }

    @Override
    public Result updateUserAvatar(String token, MultipartFile avatar, MultipartFile previewAvatar) {
        if (avatar == null && previewAvatar == null) {
            return Result.build("头像文件不能为空", 400, "MissingFile");
        }

        MultipartFile file = avatar != null ? avatar : previewAvatar;
        String fileType = avatar != null ? "avatar" : "previewAvatar";

        // 获取UUID
        String userId = jwtHelper.getUserId(token);
        UserInfo userInfo = userInfoMapper.selectById(userId);
        log.info(userInfo.toString());

        if (fileType.equals("previewAvatar")) {
            if (!userInfo.getPreviewAvatarPath().equals(PathEnum.DEFAULT_AVATAR.getPath())) {
                Path previewAvatarPath = pathUtils.getRealPath(userInfo.getPreviewAvatarPath());
                log.warn("previewAvatarPath=" + previewAvatarPath.toString());
                File previewAvatarFile = previewAvatarPath.toFile();
                previewAvatarFile.delete();
                log.warn("用户" + userInfo.getRealName() + " 删除了原头像(预览)");
            }
        } else {
            if (!userInfo.getAvatarPath().equals(PathEnum.DEFAULT_AVATAR.getPath())) {
                Path avatarPath = pathUtils.getRealPath(userInfo.getAvatarPath());
                log.warn("avatarPath=" + avatarPath.toString());
                File avatarFile = avatarPath.toFile();
                avatarFile.delete();
                log.warn("用户" + userInfo.getRealName() + " 删除了原头像(原图)");
            }
        }

        // 为头像生成名字
        String avatarName = UUID.getUUID() + ".png";

        try {
            // 为将要存入的图片设置写入路径
            // TODO 开发环境-----头像存储路径
//            String avatarPath = Paths.get(resourceLoader.getResource(PathEnum.AVATAR_FOLDER_HEADER_PATH.getPath()).getURI()).toString() + "\\" + avatarName;
            // TODO 生产环境-----头像存储路径
            String avatarPath=pathUtils.getRealPath(PathEnum.AVATAR_FOLDER_HEADER_PATH.getPath()).toString() + "\\" + avatarName;
            log.info("avatarPath=" + avatarPath);

            // 创建输入输出流
            InputStream inputStream = file.getInputStream();
            OutputStream outputStream = new FileOutputStream(avatarPath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            log.info("用户 " + userInfo.getRealName() + " 的新头像写入成功");

            // 关闭输入输出流
            inputStream.close();
            outputStream.close();

            // 写入成功,存储名字到数据库
            if (fileType.equals("avatar")) {
                userInfo.setAvatarPath(PathEnum.AVATAR_FOLDER_HEADER_PATH.getPath() + avatarName);
            } else {
                userInfo.setPreviewAvatarPath(PathEnum.AVATAR_FOLDER_HEADER_PATH.getPath() + avatarName);

            }
            log.info(userInfo.toString());
            userInfoMapper.updateById(userInfo);

        } catch (IOException e) {
            log.warn("头像上传失败,失败原因: " + e.getMessage());
            return Result.build("头像上传失败,原因" + e.getMessage(), 400, "IOException");
        }

        return Result.ok("头像上传成功");
    }

//    @Override
//    public Result updateUserAvatar(String token, MultipartFile avatar, MultipartFile previewAvatar) {
//        //获取UUID
//        String userId = jwtHelper.getUserId(token);
//
//        //判断如果有之前上传过头像,那么删除之前的头像,并准备更新新的头像
//        UserInfo userInfo = userInfoMapper.selectById(userId);
//
//        log.info(userInfo.toString());
//
//        //记得测试删除原头像(非默认头像)的功能
//        if (!(userInfo.getAvatarPath().equals(PathEnum.DEFAULT_AVATAR.getPath()))){
//            // 删除原图片
//            Path avatarPath=pathUtils.getRealPath(userInfo.getAvatarPath());
//            Path previewAvatarPath =pathUtils.getRealPath(userInfo.getPreviewAvatarPath());
//
//            log.warn("avatarPath="+avatarPath.toString());
//            log.warn("previewAvatarPath="+previewAvatarPath.toString());
//
//
//            File avatarFile = avatarPath.toFile();
//            File previewAvatarFile = previewAvatarPath.toFile();
//
//            avatarFile.delete();
//            previewAvatarFile.delete();
//            log.warn("用户" + userInfo.getRealName() + "  删除了原头像");
//        }
//
//        //为两个头像生成名字
//        String avatarName = UUID.getUUID()+".png";
//        String previewAvatarName = UUID.getUUID()+".png";
//
//        //为将要存入的两个图片设置写入路径
//        try {
//            String avatarPath=Paths.get(resourceLoader.getResource(PathEnum.AVATAR_FOLDER_HEADER_PATH.getPath()).getURI()).toString()+"\\"+avatarName;
//            String previewAvatarPath=Paths.get(resourceLoader.getResource(PathEnum.AVATAR_FOLDER_HEADER_PATH.getPath()).getURI()).toString()+"\\"+previewAvatarName;
//
//            log.info("avatarPath="+avatarPath);
//
//
//            //创建输入输出流
//            InputStream avatarInputStream = avatar.getInputStream();
//            InputStream previewAvatarInputStream = previewAvatar.getInputStream();
//            OutputStream outputStream = new FileOutputStream(avatarPath);
//            OutputStream previewOutputStream = new FileOutputStream(previewAvatarPath);
//            //buffer缓冲区为1Kb
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length=avatarInputStream.read(buffer))!=-1){
//                outputStream.write(buffer,0,length);
//            }
//            log.info("用户 "+userInfo.getRealName()+" 的新头像(original)写入成功");
//
//
//            while ((length=previewAvatarInputStream.read(buffer))!=-1){
//                previewOutputStream.write(buffer,0,length);
//            }
//            log.info("用户 "+userInfo.getRealName()+" 的新头像(preview)写入成功");
//
//            //关闭输入输出流
//
//            avatarInputStream.close();
//            outputStream.close();
//            previewAvatarInputStream.close();
//            previewOutputStream.close();
//
//            //写入成功,存储名字到数据库
//            userInfo.setAvatarPath(PathEnum.AVATAR_FOLDER_HEADER_PATH.getPath()+avatarName);
//            userInfo.setPreviewAvatarPath(PathEnum.AVATAR_FOLDER_HEADER_PATH.getPath()+previewAvatarName);
//            userInfoMapper.updateById(userInfo);
//
//        } catch (IOException e) {
//            log.warn("头像上传失败,失败原因:  "+e.getMessage());
//            return Result.build("头像上传失败,原因"+e.getMessage(),400,"IOException");
//        }
//
//
//        return Result.ok("头像上传成功");
//    }


//    @Override
//    public Result updateUserAvatar(String token, MultipartFile avatar) {
//        System.out.println("avatar = " + avatar.getName());
//        //获取UUID
//        String userId = jwtHelper.getUserId(token);
//
//        //判断如果有之前上传过头像,那么删除之前的头像,并准备更新新的头像
//        UserInfo userInfo = userInfoMapper.selectById(userId);
//
//        log.info(userInfo.toString());
//
//
//        if (avatar.getName().equals("previewAvatar")){
//            if (!(userInfo.getPreviewAvatarPath().equals(PathEnum.DEFAULT_AVATAR.getPath()))){
//                Path previewAvatarPath =pathUtils.getRealPath(userInfo.getPreviewAvatarPath());
//                log.warn("previewAvatarPath="+previewAvatarPath.toString());
//                File previewAvatarFile = previewAvatarPath.toFile();
//                previewAvatarFile.delete();
//                log.warn("用户" + userInfo.getRealName() + "  删除了原头像(预览)");
//            }
//
//        }else {
//            //删除原头像(非默认头像)的功能
//            if (!(userInfo.getAvatarPath().equals(PathEnum.DEFAULT_AVATAR.getPath()))){
//                // 删除原图片
//                Path avatarPath=pathUtils.getRealPath(userInfo.getAvatarPath());
//                log.warn("avatarPath="+avatarPath.toString());
//                File avatarFile = avatarPath.toFile();
//                avatarFile.delete();
//                log.warn("用户" + userInfo.getRealName() + "  删除了原头像(原图)");
//            }
//        }
//
//
//        //为头像生成名字
//        String avatarName = UUID.getUUID()+".png";
//
//        //为将要存入的图片设置写入路径
//        try {
//            String avatarPath=Paths.get(resourceLoader.getResource(PathEnum.AVATAR_FOLDER_HEADER_PATH.getPath()).getURI()).toString()+"\\"+avatarName;
//
//            log.info("avatarPath="+avatarPath);
//
//
//            //创建输入输出流
//            InputStream inputStream = avatar.getInputStream();
//            OutputStream outputStream = new FileOutputStream(avatarPath);
//            //buffer缓冲区为1Kb
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length=inputStream.read(buffer))!=-1){
//                outputStream.write(buffer,0,length);
//            }
//            log.info("用户 "+userInfo.getRealName()+" 的新头像写入成功");
//
//
//            //关闭输入输出流
//            inputStream.close();
//            outputStream.close();
//
//            //写入成功,存储名字到数据库
//            if (avatar.getName().equals("avatar")){
//                userInfo.setAvatarPath(PathEnum.AVATAR_FOLDER_HEADER_PATH.getPath()+avatarName);
//                userInfoMapper.updateById(userInfo);
//            }else {
//                userInfo.setPreviewAvatarPath(PathEnum.AVATAR_FOLDER_HEADER_PATH.getPath()+avatarName);
//                userInfoMapper.updateById(userInfo);
//            }
//
//
//        } catch (IOException e) {
//            log.warn("头像上传失败,失败原因:  "+e.getMessage());
//            return Result.build("头像上传失败,原因"+e.getMessage(),400,"IOException");
//        }
//
//
//        return Result.ok("头像上传成功");
//    }



    @Override
    public Result getUserAvatar(String token) {

        String userId = jwtHelper.getUserId(token);
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String[] data=userInfo.getAvatarPath().split("/");
        String fileName=data[data.length-1];
        String url= URLEnum.HOST_URL.getUrl()+URLEnum.AVATAR_RESOURCE_HEADER.getUrl()+fileName;
        log.info("用户 "+userInfo.getRealName()+"  获取了头像(原图)");
        return Result.ok(url);
    }

    @Override
    public Result getUserAvatarPreview(String token) {
        String userId = jwtHelper.getUserId(token);
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String[] data=userInfo.getPreviewAvatarPath().split("/");
        String fileName=data[data.length-1];
        String url= URLEnum.HOST_URL.getUrl()+URLEnum.AVATAR_RESOURCE_HEADER.getUrl()+fileName;
        log.info("用户 "+userInfo.getRealName()+"  获取了头像(预览图)");
        return Result.ok(url);
    }

    @Override
    public String getUserAvatarUrl(String uuid) {
        UserInfo userInfo = userInfoMapper.selectById(uuid);
        String[] data=userInfo.getAvatarPath().split("/");
        String fileName=data[data.length-1];
        String url= URLEnum.HOST_URL.getUrl()+URLEnum.AVATAR_RESOURCE_HEADER.getUrl()+fileName;
        return url;

    }

    @Override
    public Result updateUserAvatarAdmin( String uuid,MultipartFile avatar) {
        // 为头像生成名字
        String avatarName = UUID.getUUID() + ".png";

        UserInfo userInfo = userInfoMapper.selectById(uuid);

        try {
            // 为将要存入的图片设置写入路径
            // TODO 开发环境-----头像存储路径
//            String avatarPath = Paths.get(resourceLoader.getResource(PathEnum.AVATAR_FOLDER_HEADER_PATH.getPath()).getURI()).toString() + "\\" + avatarName;
            String avatarPath=pathUtils.getRealPath(PathEnum.AVATAR_FOLDER_HEADER_PATH.getPath()).toString() + "\\" + avatarName;
            // TODO 生产环境-----头像存储路径
            log.info("avatarPath=" + avatarPath);

            // 创建输入输出流
            InputStream inputStream = avatar.getInputStream();
            OutputStream outputStream = new FileOutputStream(avatarPath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            log.info("用户 " + uuid + " 的新头像写入成功");

            // 关闭输入输出流
            inputStream.close();
            outputStream.close();

            // 写入成功,存储名字到数据库
            userInfo.setAvatarPath(PathEnum.AVATAR_FOLDER_HEADER_PATH.getPath() + avatarName);
            userInfo.setPreviewAvatarPath(PathEnum.AVATAR_FOLDER_HEADER_PATH.getPath() + avatarName);
            log.info(userInfo.toString());
            userInfoMapper.updateById(userInfo);

        } catch (IOException e) {
            log.warn("头像上传失败,失败原因: " + e.getMessage());
            return Result.build("头像上传失败,原因" + e.getMessage(), 400, "IOException");
        }

        return Result.ok("头像上传成功");
    }
}




