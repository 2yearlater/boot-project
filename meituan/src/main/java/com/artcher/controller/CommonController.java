package com.artcher.controller;

import com.artcher.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    //@Value("${从配置文件读取值}")
    @Value("${reggie.bathPath}")
    public String bathPath;

    /**
     * 浏览器上传文件到服务器
     *  因为上传的文件是临时文件,因此需要将文件重新在磁盘中存储
     * @param file
     * @return
     */
    @RequestMapping("/upload")
    public R<String> upload(MultipartFile file){
        //File是一个临时文件,需要转存到指定位置,否则本次请求完毕后临时文件会删除
        //获取原始文件名file.getOriginalFilename()
        String originalFilename = file.getOriginalFilename();
        //获取原始文件的suffix
        String suffix = originalFilename.substring(originalFilename.indexOf("."));
        //使用UUID生成新的文件名
        String fileName = UUID.randomUUID().toString() + suffix;
        //创建一个目录对象,文件转存的地址
        File dir = new File(bathPath);
        //判断
        if (!dir.exists()){
            //目录不存在,创建目录
            dir.mkdirs();
        }
        //将临时文件转存到指定位置
        try {
            file.transferTo(new File(bathPath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //返回服务器磁盘中文件名
        return R.success(fileName);

    }

    /**
     * 浏览器下载服务端的文件,显示在浏览器中
     *  运用流的方式,将服务器磁盘中的文件读取出来,响应给浏览器
     * @param name
     * @param response
     */
    @RequestMapping("/download")
    public void download(@RequestParam String name, HttpServletResponse response){

        //读取磁盘中的文件
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(bathPath+name));
            int len = 0;
            byte [] bytes = new byte[1024];
            //输出流
            ServletOutputStream outputStream = response.getOutputStream();
            //设置响应内容
            response.setContentType("image/jpeg");
            //循环,用输出流将文件写到浏览器
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //关闭流
            outputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
