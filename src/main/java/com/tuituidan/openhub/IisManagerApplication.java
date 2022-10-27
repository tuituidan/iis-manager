package com.tuituidan.openhub;

import com.tuituidan.openhub.util.AppCmdUtils;
import java.io.File;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.Assert;

/**
 * IisManagerApplication
 *
 * @author tuituidan
 */
@SpringBootApplication
public class IisManagerApplication {

    /**
     * main
     *
     * @param args args
     */
    public static void main(String[] args) {
        Assert.isTrue(new File(AppCmdUtils.PREV_CMD).exists(), "请在Windows环境且安装II7以上版本下运行");
        SpringApplication.run(IisManagerApplication.class, args);
    }

}
