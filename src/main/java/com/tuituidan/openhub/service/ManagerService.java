package com.tuituidan.openhub.service;

import com.tuituidan.openhub.bean.AppXml;
import com.tuituidan.openhub.bean.ApppoolXml;
import com.tuituidan.openhub.bean.Site;
import com.tuituidan.openhub.bean.SiteXml;
import com.tuituidan.openhub.util.AppCmdUtils;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

/**
 * SiteService.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2022/10/25
 */
@Service
public class ManagerService {

    /**
     * 查询网站列表
     *
     * @return List
     */
    public List<Site> listSite() {
        List<AppXml> appXmls = AppCmdUtils.listAppXml(AppXml.class);
        List<SiteXml> siteXmls = AppCmdUtils.listSiteXml(SiteXml.class);
        // List<VdirXml> vdirXmls = AppCmdUtils.listVdirXml(VdirXml.class);
        List<ApppoolXml> apppoolXmls = AppCmdUtils.listApppoolXml(ApppoolXml.class);
        Map<String, Site> siteMap = siteXmls.stream().collect(Collectors.toMap(SiteXml::getSiteName,
                item -> new Site().setSiteName(item.getSiteName())
                        .setBindings(item.getBindings()).setSiteState(item.getState())));
        Map<String, String> apppollMap = apppoolXmls.stream()
                .collect(Collectors.toMap(ApppoolXml::getApppoolName, ApppoolXml::getState));
//        Map<String, String> vdirMap = vdirXmls.stream()
//                .collect(Collectors.toMap(VdirXml::getAppName, VdirXml::getPhysicalPath));
        List<Site> list = new ArrayList<>();
        for (AppXml appXml : appXmls) {
            Site site = siteMap.get(appXml.getSiteName());
            if (site != null) {
                site.setId(Base64Utils.encodeToUrlSafeString(appXml.getAppName().getBytes(StandardCharsets.UTF_8)));
                site.setApppoolName(appXml.getApppoolName());
                site.setApppoolState(apppollMap.get(appXml.getApppoolName()));
                //site.setPhysicalPath(vdirMap.get(appXml.getAppName()));
                list.add(site);
            }
        }
        return list;
    }

    /**
     * 修改网站状态
     *
     * @param id id
     * @param state 状态
     */
    public void siteState(String id, String state) {
        List<AppXml> appXmls = AppCmdUtils.listAppXml(AppXml.class);
        String appName = new String(Base64Utils.decodeFromUrlSafeString(id));
        AppXml appXml = appXmls.stream().filter(item -> Objects.equals(appName, item.getAppName()))
                .findFirst().orElseThrow(NullPointerException::new);
        if ("restart".equals(state)) {
            AppCmdUtils.siteState(appXml.getSiteName(), "stop");
            AppCmdUtils.siteState(appXml.getSiteName(), "start");
        } else {
            AppCmdUtils.siteState(appXml.getSiteName(), state);
        }
    }

    /**
     * 修改应用程序池状态
     *
     * @param id id
     * @param state 状态
     */
    public void apppoolState(String id, String state) {
        List<AppXml> appXmls = AppCmdUtils.listAppXml(AppXml.class);
        String appName = new String(Base64Utils.decodeFromUrlSafeString(id));
        AppXml appXml = appXmls.stream().filter(item -> Objects.equals(appName, item.getAppName()))
                .findFirst().orElseThrow(NullPointerException::new);
        AppCmdUtils.apppoolState(appXml.getApppoolName(), state);
    }

}
