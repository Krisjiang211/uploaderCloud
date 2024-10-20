package com.xiangyueEducation.uploaderCloud.Service.pojoPackage;

import com.xiangyueEducation.uploaderCloud.POJO.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
* @author 86136
* @description 针对表【user_info】的数据库操作Service
* @createDate 2024-05-18 15:00:34
*/
public interface UserInfoService extends IService<UserInfo> {

    Result getUserInfo(String token);

    /**
     * 获取用户信息(User表,主要是账号信息)
     * 注意:可以通过自定义wipeOutPwd来确定是否抹除密码
     * @param token
     * @param wipeOutPwd
     * @return
     */
    Result getUser(String token,Boolean wipeOutPwd);

    Result updateUserInfo(UserInfo userInfo);

//    Result updateUserAvatar(String token, MultipartFile avatar, MultipartFile previewAvatar);
    Result updateUserAvatar(String token, MultipartFile avatar,MultipartFile previewAvatar);
    Result getUserAvatar(String token);

    Result getUserAvatarPreview(String token);


    String getUserAvatarUrl(String uuid);

    Result updateUserAvatarAdmin(String uuid,MultipartFile avatar);
}