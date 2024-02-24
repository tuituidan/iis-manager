package com.tuituidan.openhub.service;

import com.tuituidan.openhub.bean.AppXml;
import com.tuituidan.openhub.bean.ApppoolXml;
import com.tuituidan.openhub.bean.Site;
import com.tuituidan.openhub.bean.SiteXml;
import com.tuituidan.openhub.bean.VdirXml;
import com.tuituidan.openhub.bean.file.FileData;
import com.tuituidan.openhub.util.AppCmdUtils;
import com.tuituidan.openhub.util.IpUtils;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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
@Slf4j
public class ManagerService {

    /**
     * 查询网站列表
     *
     * @return List
     */
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

    /**
     * loadFileData
     *
     * @param siteId siteId
     * @param path path
     * @return List
     */
    public List<FileData> loadFileData(String siteId, String path) {
        String rootPath = StringUtils.isNotBlank(path) ? path : getAppPath(siteId);
        File[] files = new File(rootPath).listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(files).map(file -> new FileData()
                        .setLabel(file.getName())
                        .setPath(file.getPath())
                        .setFileSize(FileUtils.byteCountToDisplaySize(file.length()))
                        .setLastModifyTime(LocalDateTime
                                .ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .setLeaf(file.isFile()))
                .sorted(Comparator.comparing(FileData::getLeaf)
                        .thenComparing(FileData::getLabel, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    private String getAppPath(String id) {
        List<VdirXml> vdirXmls = AppCmdUtils.listVdirXml(VdirXml.class);
        String appName = new String(Base64Utils.decodeFromUrlSafeString(id));
        return vdirXmls.stream()
                .filter(item -> "/".equals(item.getPath()) && Objects.equals(appName, item.getAppName()))
                .map(VdirXml::getPhysicalPath).findFirst().orElseThrow(NullPointerException::new);

    }

}
