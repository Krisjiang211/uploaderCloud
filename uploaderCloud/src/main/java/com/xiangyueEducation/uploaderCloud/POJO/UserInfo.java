package com.xiangyueEducation.uploaderCloud.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName user_info
 */
@TableName(value ="user_info")
@Data
public class UserInfo implements Serializable {
    @TableId
    private String uuid;

    private String realName;

    private String phoneNumber;

    private String address;

    private String email;

    private Object gender;

    private String avatarPath;

    private String previewAvatarPath;

    private static final long serialVersionUID = 1L;

    public String toString(){
        return "UserInfo{" +
                "uuid='" + uuid + '\'' +
                ", realName='" + realName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", gender=" + gender +
                ", avatarPath='" + avatarPath + '\'' +
                ", previewAvatarPath='" + previewAvatarPath + '\'' +
                '}';
    }
}