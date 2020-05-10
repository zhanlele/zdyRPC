package com.quanle.controller;

import com.quanle.pojo.DataSourceProperty;
import com.quanle.pojo.Result;
import com.quanle.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    /**
     * 默认跳转index页面
     *
     * @return
     */
    @RequestMapping("")
    public String toIndex() {
        return "forward:config/index";
    }


    @RequestMapping("/index")
    public String index(Model model) {
        DataSourceProperty property = configService.getDbConfig();
        model.addAttribute("config", property);
        return "config_index";
    }


    @RequestMapping("/update")
    @ResponseBody
    public Result updateConfig(DataSourceProperty property) {
        try {
            configService.updateConfig(property);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e);
        }

    }
}
