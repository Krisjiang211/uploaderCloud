package com.xiangyueEducation.uploaderCloud.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName main_type
 */
@TableName(value ="main_type")
@Data
public class MainType implements Serializable {
    @TableId
    private Integer mainTypeId;

    private String name;

    private Date time;

    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}