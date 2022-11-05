package com.tuituidan.openhub.service;

import com.tuituidan.openhub.bean.AppXml;
import com.tuituidan.openhub.bean.ApppoolXml;
import com.tuituidan.openhub.bean.Site;
import com.tuituidan.openhub.bean.SiteXml;
import com.tuituidan.openhub.util.AppCmdUtils;
import com.tuituidan.openhub.util.IpUtils;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
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
        List<ApppoolXml> apppoolXmls = AppCmdUtils.listApppoolXml(ApppoolXml.class);
        Map<String, Site> siteMap = siteXmls.stream().collect(Collectors.toMap(SiteXml::getSiteName,
                item -> new Site().setSiteName(item.getSiteName())
                        .setBindings(item.getBindings()).setSiteState(item.getState())));
        Map<String, String> apppollMap = apppoolXmls.stream()
                .collect(Collectors.toMap(ApppoolXml::getApppoolName, ApppoolXml::getState));
        List<Site> list = new ArrayList<>();
        for (AppXml appXml : appXmls) {
            Site site = siteMap.get(appXml.getSiteName());
            if (site != null) {
                site.setId(Base64Utils.encodeToUrlSafeString(appXml.getAppName().getBytes(StandardCharsets.UTF_8)));
                site.setApppoolName(appXml.getApppoolName());
                site.setApppoolState(apppollMap.get(appXml.getApppoolName()));
                site.setUrl(getUrl(site.getBindings()));
                list.add(site);
            }
        }
        return list;
    }

    private String getUrl(String bindings) {
        List<String> bindingList = Arrays.stream(StringUtils.split(bindings, ","))
                .filter(item -> item.startsWith("http")).collect(Collectors.toList());
        // 根据冒号切分，长度为3的表示绑定了域名，优先使用
        String realBinding = bindingList.stream()
                .filter(binding -> StringUtils.split(binding, ":").length == 3)
                .findFirst().orElse("");
        if (StringUtils.isBlank(realBinding)) {
            // 都没有绑定域名，就优先找最长的，一般就是绑定了IP的
            realBinding = bindingList.stream().max(Comparator.comparing(String::length)).orElse("");
        }
        String[] realBindingArr = StringUtils.split(realBinding, "/");
        if (realBindingArr.length != 2) {
            return "";
        }
        String[] split = StringUtils.split(realBindingArr[1], ":");
        if (split.length < 2) {
            return "";
        }
        String domain = split[0];
        if (split.length > 2) {
            // 有域名直接使用域名
            domain = split[2];
        } else if ("*".equals(domain)) {
            // 没有域名，且未绑定IP，就获取本地IP
            domain = IpUtils.getLocalIp();
        }
        String protocol = realBindingArr[0];
        String port = split[1];
        String result = protocol + "://" + domain;
        // 默认的端口就不显示
        if (!StringUtils.containsAny(protocol + port, "http80", "https443")) {
            result += ":" + split[1];
        }
        return result;
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
