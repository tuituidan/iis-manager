package com.tuituidan.openhub.controller;

import com.tuituidan.openhub.bean.Site;
import com.tuituidan.openhub.service.ManagerService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * ManagerController.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2022/10/25
 */
@RestController
@RequestMapping("/api/v1")
public class ManagerController {

    @Resource
    private ManagerService managerService;

    /**
     * 查询网站列表
     *
     * @return List
     */
    @GetMapping("/site")
    public List<Site> listSite() {
        return managerService.listSite();
    }

    /**
     * 修改网站状态
     *
     * @param id id
     * @param state 状态
     */
    @PatchMapping("/site/{id}/actions/{state:start|stop|restart}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void siteState(@PathVariable String id, @PathVariable String state) {
        managerService.siteState(id, state);
    }

    /**
     * 修改应用程序池状态
     *
     * @param id id
     * @param state 状态
     */
    @PatchMapping("/apppool/{id}/actions/{state:start|stop|recycle}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void apppoolState(@PathVariable String id, @PathVariable String state) {
        managerService.apppoolState(id, state);
    }

}
