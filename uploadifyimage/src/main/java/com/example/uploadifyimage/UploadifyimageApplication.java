package com.example.uploadifyimage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan//扫描这个包下面的包
public class UploadifyimageApplication {
    public static void main(String[] args) {
        SpringApplication.run(UploadifyimageApplication.class, args);
    }
}
