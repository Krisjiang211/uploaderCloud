package com.xiangyueEducation.uploaderCloud.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName department_and_users
 */
@TableName(value ="department_and_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentAndUsers implements Serializable {
    private Integer departmentId;
    @TableId
    private String uuid;

    private static final long serialVersionUID = 1L;




}