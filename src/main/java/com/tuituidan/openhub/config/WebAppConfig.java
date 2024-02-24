package com.tuituidan.openhub.config;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebAppConfig.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2024/2/17
 */
@Configuration
@Slf4j
public class WebAppConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        try {
            Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(
                    new PathMatchingResourcePatternResolver()).getResources("templates/*.html");
            for (Resource resource : resources) {
                String fileName = FilenameUtils.getBaseName(resource.getFilename());
                registry.addViewController("/" + fileName).setViewName(fileName);
            }
        } catch (IOException ex) {
            log.error("默认路由生成失败", ex);
        }
    }

}
