package com.xiangyueEducation.uploaderCloud.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName admin
 */
@TableName(value ="admin")
@Data
public class Admin implements Serializable {
    @TableId
    private String adminId;

    private Integer roleId;

    private String account;

    private String pwd;

    private String phoneNumber;

    private Date createTime;

    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}