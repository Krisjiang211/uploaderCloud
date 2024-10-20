package com.xiangyueEducation.uploaderCloud.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Map;

import lombok.Data;

/**
 * @TableName task_content
 */
@TableName(value ="task_content")
@Data
public class TaskContent implements Serializable {
    @TableId
    private Integer taskContentId;

    private String fileGroupId;

    private String description;

    private String previewImgPath;

    private String title;

    private static final long serialVersionUID = 1L;

    public TaskContent(){};

    public TaskContent(Map data){
        this.description = (String) data.get("description");
        this.title = (String) data.get("title");
    }

    public String toString(){

        return "TaskContent{" +
                "taskContentId=" + taskContentId +
                ", fileGroupId=" + fileGroupId +
                ", description=" + description +
                ", previewImgPath=" + previewImgPath +
                ", title=" + title +
                '}';
    }
}