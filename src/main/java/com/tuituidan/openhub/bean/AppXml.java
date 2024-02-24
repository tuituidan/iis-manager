package com.tuituidan.openhub.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * AppResult.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2022/10/25
 */
@Getter
@Setter
@Accessors(chain = true)
public class AppXml {

    @JSONField(name = "APP.NAME")
    private String appName;

    @JSONField(name = "SITE.NAME")
    private String siteName;

    @JSONField(name = "APPPOOL.NAME")
    private String apppoolName;

    private String path;
}
