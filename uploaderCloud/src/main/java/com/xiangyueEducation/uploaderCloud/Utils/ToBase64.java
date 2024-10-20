package com.xiangyueEducation.uploaderCloud.Utils;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;


public class ToBase64 {

    public static String imgToBase64(String imgPath) throws IOException {
        File file = new File(imgPath);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] imageData = new byte[(int) file.length()];
        fileInputStream.read(imageData);
        fileInputStream.close();

        // 将图片数据转换为Base64编码
        String base64Image = Base64.getEncoder().encodeToString(imageData);

        return base64Image;
    }


}
