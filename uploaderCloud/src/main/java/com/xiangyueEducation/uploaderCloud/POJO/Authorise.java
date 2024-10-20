package com.xiangyueEducation.uploaderCloud.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName authorise
 */
@TableName(value ="authorise")
@Data
public class Authorise implements Serializable {
    @TableId
    private Integer authoriseId;

    private Integer publishId;

    private String requesterUuid;

    private Integer isAuthorise;

    private String filePath;

    private String fileName;

    private String secretKey;

    private Date time;

    private static final long serialVersionUID = 1L;
}