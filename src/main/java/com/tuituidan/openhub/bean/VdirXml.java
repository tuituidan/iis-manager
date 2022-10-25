package com.tuituidan.openhub.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * AppResult.
 *
 * @author zhujunhan
 * @version 1.0
 * @date 2022/10/25
 */
@Getter
@Setter
@Accessors(chain = true)
public class VdirXml {

    @JSONField(name = "APP.NAME")
    private String appName;

    private String physicalPath;

}
