package com.tuituidan.openhub.util;

import com.alibaba.fastjson2.JSON;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 执行工具appcmd命令获取结果，默认的输出需要截取字符串，容易出问题，都输出xml格式来解析.
 *
 * @author zhujunhan
 * @version 1.0
 * @date 2022/10/25
 */
@UtilityClass
@Slf4j
public class AppCmdUtils {

    public static final String PREV_CMD = "C:\\Windows\\System32\\inetsrv\\appcmd.exe";

    /**
     * 查询网站
     *
     * @param cls cls
     * @param <T> T
     * @return List
     */
    public static <T> List<T> listSiteXml(Class<T> cls) {
        return toList("list site /xml", cls);
    }

    /**
     * 查询应用
     *
     * @param cls cls
     * @param <T> T
     * @return List
     */
    public static <T> List<T> listAppXml(Class<T> cls) {
        return toList("list app /xml", cls);
    }

    /**
     * 查询应用程序池
     *
     * @param cls cls
     * @param <T> T
     * @return List
     */
    public <T> List<T> listApppoolXml(Class<T> cls) {
        return toList("list apppool /xml", cls);
    }

    /**
     * 查询虚拟目录
     *
     * @param cls cls
     * @param <T> T
     * @return List
     */
    public static <T> List<T> listVdirXml(Class<T> cls) {
        return toList("list vdir /xml", cls);
    }

    /**
     * 修改网站状态
     *
     * @param siteName 网站名称
     * @param state 状态
     */
    public static void siteState(String siteName, String state) {
        execCmd(state + " site \"" + siteName + "\"");
    }

    /**
     * 修改应用程序池状态
     *
     * @param apppoolName 应用程序池名称
     * @param state 状态
     */
    public static void apppoolState(String apppoolName, String state) {
        execCmd(state + " apppool \"" + apppoolName + "\"");
    }

    private static <T> List<T> toList(String cmd, Class<T> cls) {
        List<T> list = new ArrayList<>();
        List<Element> elements = getDocument(cmd).getRootElement().elements();
        for (Element element : elements) {
            Map<String, String> itemMap = element.attributes().stream()
                    .collect(Collectors.toMap(Attribute::getName, Attribute::getValue));
            list.add(JSON.parseObject(JSON.toJSONString(itemMap), cls));
        }
        return list;
    }

    private static Document getDocument(String cmd) {
        try (InputStream inputStream = execCmd(cmd).getInputStream()) {
            return SAXReader.createDefault().read(inputStream);
        } catch (Exception ex) {
            log.error("xml数据读取失败", ex);
            throw new IllegalStateException("xml数据读取失败", ex);
        }
    }

    private Process execCmd(String cmd) {
        try {
            return Runtime.getRuntime().exec(PREV_CMD + " " + cmd);
        } catch (Exception ex) {
            log.error("命令执行失败", ex);
            throw new IllegalStateException("命令执行失败", ex);
        }

    }

}
