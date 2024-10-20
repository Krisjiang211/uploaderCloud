package com.xiangyueEducation.uploaderCloud.Utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.xiangyueEducation.uploaderCloud.Utils.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class QRCodeGenerator {

    private String data;

    private int width = 300;

    private int height=300;

    private String fileType="png";

    private static String absolutePath;

    static {
        //默认的 二维码文件夹 保存路径
        absolutePath=PathEnum.QRCodeDirPath.getPath();
    }

    //创建保存二维码的文件夹
    private static void mkQRCodeDir(){

        File file = new File(absolutePath);
        if (!file.exists()){
            boolean mkdir = file.mkdirs();
            System.out.println("PROJECT/fileSystem/QRCode文件夹不存在, 创建结果: " + mkdir);
        }
    }




//获取图片二维码并保存本地
    private static String getPngQRCodeLocal(String data, int width, int height, String fileType){
        mkQRCodeDir();

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            String filePath=absolutePath+"/"+ UUID.getUUID()+".png";

            Path path = new File(filePath).toPath();

            //矫正字符编码
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            data = new String(bytes, StandardCharsets.UTF_8);
            System.out.println("编码成功");

            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
            MatrixToImageWriter.writeToPath(bitMatrix, fileType, path);
            System.out.println("图片已保存到: "+path.toAbsolutePath() + "并会在3min后删除,请尽快使用");
            return filePath;
        } catch (WriterException | IOException e) {
            System.err.println("二维码生成失败: " + e.getMessage());
        }
        return null;
    }

    public static String getPngQRCodeLocal(String data){
        return getPngQRCodeLocal(data,300,300,"png");
    }

    public static String getPngQRCodeLocal(String data,int width,int height){
        return getPngQRCodeLocal(data,width,height,"png");
    }


    //删除二维码(异步执行)
    public static void deleteQRCodeLocal(String localPath,long ms){
        //线程池创建
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(()->{
            try {
                Thread.sleep(ms);
                Files.delete(Path.of(localPath));
            } catch (IOException e) {
                System.out.println("删除操作发生错误,有IOException:  " + e);
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                System.out.println("线程休眠错误 :  " + e);
                throw new RuntimeException(e);
            }
        });



    }





//获取web流 二维码
    private static ResponseEntity<byte[]> generatePngQRCodeWebStream(String data, int width, int height, String fileType){
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            ByteArrayOutputStream  outputStream = new ByteArrayOutputStream();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
            MatrixToImageWriter.writeToStream(bitMatrix, fileType,outputStream );
            byte[] byteArray = outputStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(byteArray.length);
            headers.setContentDispositionFormData("attachment", "qrcode.png");

            return new ResponseEntity<>(byteArray,headers, HttpStatus.OK);
        } catch (WriterException e) {
            System.err.println("二维码生成失败(WriterException): " + e.getMessage());
        } catch (IOException e) {
            System.err.println("二维码生成失败(IOException): " + e.getMessage());
        }
        return null;
    }

    //自定义data的获取流
    public static ResponseEntity<byte[]> getPngQRCodeWebStream(String data){
        return generatePngQRCodeWebStream(data,300,300,"png");
    }
    //自定义data、width、height的获取流
    public static ResponseEntity<byte[]> getPngQRCodeWebStream(String data,int width,int height){
        return generatePngQRCodeWebStream(data,width,height,"png");
    }




}
