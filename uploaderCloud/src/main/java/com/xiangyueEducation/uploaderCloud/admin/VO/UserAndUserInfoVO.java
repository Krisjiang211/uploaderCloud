package com.xiangyueEducation.uploaderCloud.admin.VO;

import lombok.Data;

import java.util.Date;

@Data
public class UserAndUserInfoVO {
    //user
    private String uuid;

    private Integer roleId;

    private Integer departmentId;

    private String departmentName;

    private Integer isDelete;

    private String identityCardCode;
    //userInfo
    private String realName;

    private String phoneNumber;

    private String address;

    private String email;

    private Object gender;

    private String avatarPath;

    private String previewAvatarPath;

}
