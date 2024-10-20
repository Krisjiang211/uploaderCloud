package com.xiangyueEducation.uploaderCloud.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName self_cloud_file_space
 */
@TableName(value ="self_cloud_file_space")
@Data
public class SelfCloudFileSpace implements Serializable {
    @TableId
    private String uuid;

    private Integer fileId;

    private Integer fileCategoryId;

    private String filePath;

    private String fileName;

    private Integer fileSize;

    private Date uploadTime;

    private static final long serialVersionUID = 1L;
}