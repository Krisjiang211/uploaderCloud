package com.xiangyueEducation.uploaderCloud.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName proxy_user
 */
@TableName(value ="proxy_user")
@Data
public class ProxyUser implements Serializable {
    private String uuid;

    private String realPwd;

    private static final long serialVersionUID = 1L;
}