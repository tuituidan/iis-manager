package com.tuituidan.openhub.service;

import com.tuituidan.openhub.bean.Site;
import java.util.List;

/**
 * IManagerService.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2024/3/10
 */
public interface IManagerService {

    /**
     * 查询网站列表
     *
     * @return List
     */
    List<Site> listSite();

    /**
     * 修改网站状态
     *
     * @param id id
     * @param state 状态
     */
    void siteState(String id, String state);

    /**
     * 修改应用程序池状态
     *
     * @param id id
     * @param state 状态
     */
    void apppoolState(String id, String state);

}
