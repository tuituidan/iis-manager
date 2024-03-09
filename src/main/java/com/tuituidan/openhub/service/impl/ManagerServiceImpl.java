package com.tuituidan.openhub.service.impl;

import com.tuituidan.openhub.bean.AppXml;
import com.tuituidan.openhub.bean.ApppoolXml;
import com.tuituidan.openhub.bean.Site;
import com.tuituidan.openhub.bean.SiteXml;
import com.tuituidan.openhub.bean.VdirXml;
import com.tuituidan.openhub.service.IManagerService;
import com.tuituidan.openhub.util.AppCmdUtils;
import com.tuituidan.openhub.util.IpUtils;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;

/**
 * SiteService.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2022/10/25
 */
@Slf4j
public class ManagerServiceImpl implements IManagerService {

    @Override
    public List<Site> listSite() {
        List<AppXml> appXmls = AppCmdUtils.listAppXml(AppXml.class);
        Map<String, AppXml> appXmlMap = appXmls.stream().filter(item -> "/".equals(item.getPath()))
                .collect(Collectors.toMap(AppXml::getSiteName, Function.identity()));

        List<ApppoolXml> apppoolXmls = AppCmdUtils.listApppoolXml(ApppoolXml.class);
        Map<String, String> apppollMap = apppoolXmls.stream()
                .collect(Collectors.toMap(ApppoolXml::getApppoolName, ApppoolXml::getState));

        List<VdirXml> vdirXmls = AppCmdUtils.listVdirXml(VdirXml.class);
        Map<String, String> vdirMap = vdirXmls.stream()
                .filter(item -> "/".equals(item.getPath()))
                .collect(Collectors.toMap(VdirXml::getAppName, VdirXml::getPhysicalPath));

        List<Site> list = new ArrayList<>();
        List<SiteXml> siteXmls = AppCmdUtils.listSiteXml(SiteXml.class);
        for (SiteXml siteXml : siteXmls) {
            AppXml appXml = appXmlMap.get(siteXml.getSiteName());
            list.add(new Site()
                    .setId(Base64Utils.encodeToUrlSafeString(appXml.getAppName().getBytes(StandardCharsets.UTF_8)))
                    .setSiteName(siteXml.getSiteName())
                    .setBindings(siteXml.getBindings())
                    .setSiteState(siteXml.getState())
                    .setApppoolName(appXml.getApppoolName())
                    .setApppoolState(apppollMap.get(appXml.getApppoolName()))
                    .setUrl(getUrl(siteXml.getBindings()))
                    .setPhysicalPath(vdirMap.get(appXml.getAppName())));
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

    @Override
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

    @Override
    public void apppoolState(String id, String state) {
        List<AppXml> appXmls = AppCmdUtils.listAppXml(AppXml.class);
        String appName = new String(Base64Utils.decodeFromUrlSafeString(id));
        AppXml appXml = appXmls.stream().filter(item -> Objects.equals(appName, item.getAppName()))
                .findFirst().orElseThrow(NullPointerException::new);
        AppCmdUtils.apppoolState(appXml.getApppoolName(), state);
    }

}
