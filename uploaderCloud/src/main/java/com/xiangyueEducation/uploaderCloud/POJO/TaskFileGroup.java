package com.xiangyueEducation.uploaderCloud.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.net.Inet4Address;

import lombok.Data;

/**
 * @TableName task_file_group
 */
@TableName(value ="task_file_group")
@Data
public class TaskFileGroup implements Serializable {
    @TableId
    private Integer fileGroupId;

    private Integer fileId;

    private Integer fileCategoryId;

    private Integer fileLevel;

    private String filePath;

    private String fileName;

    private Integer fileSize;

    private String groupId;


    private static final long serialVersionUID = 1L;

    public String toString(){

        return "TaskFileGroup{" +
                "fileGroupId=" + fileGroupId +
                ", fileId=" + fileId +
                ", fileCategoryId=" + fileCategoryId +
                ", fileLevel=" + fileLevel +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", groupId='" + groupId + '\'' +
                '}';
    }


}