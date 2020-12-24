package com.atguigu.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther zzyy
 * @create 2020-02-21 18:08
 */
@RestController
@RefreshScope
// 運維工程師需要再多加一步
// curl -X POST "http://localhost:3355/actuator/refresh"
// 定點通知某個節點刷新頁面
// curl -X POST "http://localhost:3344/actuator/bus-refresh/config-client:3355"
public class ConfigClientController
{
    @Value("${config.info}")
    private String configInfo;

    @GetMapping("/configInfo")
    public String getConfigInfo()
    {
        return configInfo;
    }
}
