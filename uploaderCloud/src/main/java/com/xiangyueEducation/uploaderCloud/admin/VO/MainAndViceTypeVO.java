package com.xiangyueEducation.uploaderCloud.admin.VO;


import lombok.Data;

import java.util.Date;

@Data
public class MainAndViceTypeVO {
    private Integer mainAndViceTypeId;

    private String mainTypeName;

    private String viceTypeName;

    private Date time;

    private Integer isDelete;
}
