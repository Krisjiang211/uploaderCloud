package com.xiangyueEducation.uploaderCloud.Utils;

public enum URLEnum {

    //TODO 老板云服务器环境URL
    HOST_URL("https://save.woyaoxueyi.com:1314/"),
    HOST_URL_NO_SLASH("https://save.woyaoxueyi.com:1314"),

    //TODO 生产环境URLEnum
//    HOST_URL("http://119.29.145.73:1314/"),
//    HOST_URL_NO_SLASH("http://119.29.145.73:1314"),
    //TODO 开发环境URLEnum
//    HOST_URL("http://172.22.127.134:1314/"),
//    HOST_URL_NO_SLASH("http://172.22.127.134:1314"),
    AVATAR_RESOURCE_HEADER("avatar/"),
    SELF_SPACE_RESOURCE_HEADER("selfSpace/"),
    WORK_SPACE_RESOURCE_HEADER("workSpace/"),
    FIRST_LEVEL_WORK_SPACE_RESOURCE("/firstLevel/"),
    SECOND_LEVEL_WORK_SPACE_RESOURCE("/secondLevel/"),
    THIRD_LEVEL_WORK_SPACE_RESOURCE("/thirdLevel/"),
    QRCode_RESOURCE_HEADER("QRCode/"),;


    private String url;
    URLEnum(String url){
        this.url=url;
    }


    public String getUrl() {
        return url;
    }

}
