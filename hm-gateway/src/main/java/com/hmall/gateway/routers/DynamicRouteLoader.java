package com.hmall.gateway.routers;


import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@Component
@Slf4j
@RequiredArgsConstructor
public class DynamicRouteLoader {

    //拿到ConfigService
    private final NacosConfigManager nacosConfigManager;

    private final RouteDefinitionWriter writer;

    private final Set<String> routeIds = new HashSet<>();
    private final String dataId = "gateway-routes.json";
    private final String group = "DEFAULT_GROUP";

    //在Bean初始化之后执行的配置
    @PostConstruct
    public void initRouterConfigListener() throws NacosException {

        //1.项目启动时，先拉取一次配置，并且添加配置监听器
        String configInfo = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(dataId, group, 5000, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        //2.监听到配置变更，需要去更新路由表
                        updateConfigInfo(configInfo);
                    }
                });
        //3.第一次读取到配置，也需要更新到路由表
        updateConfigInfo(configInfo);

    }

    public void updateConfigInfo(String configInfo) {
        log.debug("update gateway route config: {}", configInfo);
        //1.解析配置文件，转为RouterDefinition对象
        List<RouteDefinition> routeDefinitions = JSONUtil.toList(configInfo, RouteDefinition.class);
        //2.删除旧的路由表
        for (String routeId : routeIds) {
            writer.delete(Mono.just(routeId));
        }
        routeIds.clear();
        //3.更新路由表
        for (RouteDefinition routeDefinition : routeDefinitions) {
            writer.save(Mono.just(routeDefinition)).subscribe();
            //记录路由id，方便删除
            routeIds.add(routeDefinition.getId());
        }
    }
}
