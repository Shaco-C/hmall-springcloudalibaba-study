package com.hmall.gateway.filters;

import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
public class PrintAnyGatewayFilterFactory extends AbstractGatewayFilterFactory<PrintAnyGatewayFilterFactory.Config>  {
    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter(new GatewayFilter(){

            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                System.out.println("PrintAnyGatewayFilterFactory");
                //从YAML中获取配置的属性
                String name = config.getName();
                String age = config.getAge();

                System.out.println("name:"+name+",age:"+age);
                return chain.filter(exchange);
            }}, 1);
    }


    //自定义配置属性
    @Data
    public static class Config {
        private String name;
        private String age;
    }

    //变量名依次返回，顺序很重要，读取参数按照顺序来
    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("name","age");
    }

    //将Config字节码传递给父类，父类会自动将配置属性与Config中的属性进行绑定
    public PrintAnyGatewayFilterFactory(){
        super(Config.class);
    }

}
