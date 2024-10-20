package com.xiangyueEducation.uploaderCloud.Utils;

import java.io.File;

public enum PathEnum {

    DEFAULT_AVATAR("classpath:fileSystem/avatar/defaultAvatar.png"),
    AVATAR_FOLDER_HEADER_PATH("classpath:fileSystem/avatar/"),
    TMP_PATH_SELF_SPACE("classpath:fileSystem/tmpFilePath/selfSpaceTmpPath/"),
    TMP_PATH_WORK_SPACE("classpath:fileSystem/tmpFilePath/workSpaceTmpPath/"),
    WORK_SPACE_PATH("classpath:fileSystem/workSpace/"),
    SELF_SPACE_PATH("classpath:fileSystem/selfSpace/"),
    WORK_SPACE_PREVIEW_IMG_PATH("classpath:fileSystem/image"),
    // TODO 生产环境的路径,自己来看看最好是
    PRODUCE_ENV("file:/source/uploaderCloud/server/fileSystem/"),
    PRODUCE_ENV_NO_SLASH("file:/source/uploaderCloud/server/fileSystem"),
    DEV_ENV("classpath:fileSystem/"),
    DEV_ENV_NO_SLASH("classpath:fileSystem"),

    //TODO 看看生产环境中这个地方会不会错
    QRCodeDirPath(new File(new File("").getAbsolutePath()).getParent()+"/"+"fileSystem/QRCode"),
    ;



    private String path;
    PathEnum(String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }
}
