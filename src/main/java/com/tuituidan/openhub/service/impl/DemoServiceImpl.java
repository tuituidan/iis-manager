package com.tuituidan.openhub.service.impl;

import com.tuituidan.openhub.bean.Site;
import com.tuituidan.openhub.service.IManagerService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * DemoService.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2024/3/10
 */
public class DemoServiceImpl implements IManagerService, ApplicationRunner {

    private final List<Site> siteList = new ArrayList<>();

    @Override
    public List<Site> listSite() {
        return siteList;
    }

    @Override
    public void siteState(String id, String state) {
        for (Site site : siteList) {
            if (Objects.equals(id, site.getId())) {
                site.setSiteState("stop".equals(state) ? "Stopped" : "Started");
                break;
            }
        }
    }

    @Override
    public void apppoolState(String id, String state) {
        for (Site site : siteList) {
            if (Objects.equals(id, site.getId())) {
                site.setApppoolState("stop".equals(state) ? "Stopped" : "Started");
                break;
            }
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        siteList.add(new Site().setId("1")
                .setSiteName("演示模式-当前目录")
                .setPhysicalPath(SystemUtils.USER_DIR));
    }

}
