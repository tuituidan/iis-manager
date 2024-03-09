package com.tuituidan.openhub.config;

import com.tuituidan.openhub.service.IManagerService;
import com.tuituidan.openhub.service.impl.DemoServiceImpl;
import com.tuituidan.openhub.service.impl.ManagerServiceImpl;
import com.tuituidan.openhub.util.AppCmdUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

/**
 * ServiceConfig.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2024/3/10
 */
@Configuration
public class ServiceConfig {

    /**
     * 非Windows环境下使用演示模式
     *
     * @return IManagerService
     * @throws IOException ex
     */
    @Bean
    public IManagerService managerService() throws IOException {
        if (SystemUtils.IS_OS_WINDOWS) {
            Assert.isTrue(new File(AppCmdUtils.PREV_CMD).exists(), "请在Windows环境且安装II7以上版本下运行");
            String checkCmd = StreamUtils.copyToString(AppCmdUtils.execCmd("list site").getInputStream(),
                    Charset.forName("GBK"));
            // 执行命令失败就不启动了，输入失败原因，一般是因为权限不足导致
            Assert.isTrue(!checkCmd.startsWith("ERROR"), checkCmd);
            return new ManagerServiceImpl();
        }
        return new DemoServiceImpl();
    }

}
