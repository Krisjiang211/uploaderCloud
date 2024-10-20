package com.xiangyueEducation.uploaderCloud.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName file_group_category
 */
@TableName(value ="file_group_category")
@Data
public class FileGroupCategory implements Serializable {
    @TableId
    private Integer fileGroupCategoryId;

    private String categoryName;

    private Date createTime;

    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}