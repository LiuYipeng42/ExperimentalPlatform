package com.guet.ExperimentalPlatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@ServletComponentScan
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ExperimentalPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExperimentalPlatformApplication.class, args);
    }

}
