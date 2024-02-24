package com.tuituidan.openhub.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Site.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2022/10/25
 */
@Getter
@Setter
@Accessors(chain = true)
public class Site {

    private String id;

    private String siteName;

    private String bindings;

    private String url;

    private String siteState;

    private String apppoolName;

    private String apppoolState;

    private String physicalPath;
}
