package com.tuituidan.openhub.service;

import com.tuituidan.openhub.bean.AppXml;
import com.tuituidan.openhub.bean.ApppoolXml;
import com.tuituidan.openhub.bean.Site;
import com.tuituidan.openhub.bean.SiteXml;
import com.tuituidan.openhub.bean.VdirXml;
import com.tuituidan.openhub.util.AppCmdUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * SiteService.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2022/10/25
 */
@Service
public class ManagerService {

    private Map<String, Site> dataMap = new HashMap<>();

    /**
     * 查询网站列表
     *
     * @return List
     */
    public List<Site> listSite() {
        List<AppXml> appXmls = AppCmdUtils.listAppXml(AppXml.class);
        List<SiteXml> siteXmls = AppCmdUtils.listSiteXml(SiteXml.class);
        List<VdirXml> vdirXmls = AppCmdUtils.listVdirXml(VdirXml.class);
        List<ApppoolXml> apppoolXmls = AppCmdUtils.listApppoolXml(ApppoolXml.class);
        Map<String, Site> siteMap = siteXmls.stream().collect(Collectors.toMap(SiteXml::getSiteName,
                item -> new Site().setId(item.getId())
                        .setSiteName(item.getSiteName())
                        .setBindings(item.getBindings()).setSiteState(item.getState())));
        Map<String, String> apppollMap = apppoolXmls.stream()
                .collect(Collectors.toMap(ApppoolXml::getApppoolName, ApppoolXml::getState));
        Map<String, String> vdirMap = vdirXmls.stream()
                .collect(Collectors.toMap(VdirXml::getAppName, VdirXml::getPhysicalPath));
        List<Site> list = new ArrayList<>();
        for (AppXml appXml : appXmls) {
            Site site = siteMap.get(appXml.getSiteName());
            if (site != null) {
                site.setApppoolName(appXml.getApppoolName());
                site.setApppoolState(apppollMap.get(appXml.getApppoolName()));
                site.setPhysicalPath(vdirMap.get(appXml.getAppName()));
                list.add(site);
            }
        }
        dataMap = list.stream().collect(Collectors.toMap(Site::getId, Function.identity()));
        return list;
    }

    /**
     * 修改网站状态
     *
     * @param id id
     * @param state 状态
     */
    public void siteState(String id, String state) {
        String siteName = Optional.ofNullable(dataMap.get(id)).map(Site::getSiteName)
                .orElseThrow(NullPointerException::new);
        if ("restart".equals(state)) {
            AppCmdUtils.siteState(siteName, "stop");
            AppCmdUtils.siteState(siteName, "start");
        } else {
            AppCmdUtils.siteState(siteName, state);
        }
    }

    /**
     * 修改应用程序池状态
     *
     * @param id id
     * @param state 状态
     */
    public void apppoolState(String id, String state) {
        String apppoolName = Optional.ofNullable(dataMap.get(id)).map(Site::getApppoolName)
                .orElseThrow(NullPointerException::new);
        AppCmdUtils.apppoolState(apppoolName, state);
    }

}
