package com.xiangyueEducation.uploaderCloud.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName main_and_vice_type
 */
@TableName(value ="main_and_vice_type")
@Data
public class MainAndViceType implements Serializable {
    @TableId
    private Integer mainAndViceTypeId;

    private Integer mainTypeId;

    private Integer viceTypeId;

    private Date time;

    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}