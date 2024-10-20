package com.xiangyueEducation.uploaderCloud.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName publish
 */
@TableName(value ="publish")
@Data
public class Publish implements Serializable {
    private Integer taskContentId;

    private String uuid;

    private Integer fileGroupCategoryId;

    @TableId
    private Integer publishId;

    private Date createTime;

    private Integer mainAndViceTypeId;

    private Integer departmentId;

    private Integer isDelete;

    private static final long serialVersionUID = 1L;

    public String toString(){
        return "Publish{" +
                "taskContentId=" + taskContentId +
                ", uuid='" + uuid + '\'' +
                ", fileGroupCategoryId=" + fileGroupCategoryId +
                ", publishId=" + publishId +
                ", createTime=" + createTime +
                ", mainAndViceTypeId=" + mainAndViceTypeId +
                ", departmentId=" + departmentId +
                '}';
    }
}