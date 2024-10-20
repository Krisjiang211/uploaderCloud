package com.xiangyueEducation.uploaderCloud.VO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LockAccountInfo {

    private String uuid;
    private String role;
    private Integer failTimes;
    private Boolean isLock;
    private Long lockTime;//单位是ms


}
