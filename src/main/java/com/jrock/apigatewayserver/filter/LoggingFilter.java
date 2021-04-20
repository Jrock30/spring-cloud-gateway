package com.jrock.apigatewayserver.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Custom Filter
 *
 * extends AbstractGatewayFilterFactory
 */
@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        /**
         *  Custom Pre Filter
         *
         *  GatewayFilter 의 구현체 new OrderedGatewayFilter()
         *  Web Flux 에서는 MVC 메소드인 HttpServletRequest, HttpServletResponse 가 아닌
         *  ServerHttpRequest, ServerHttpResponse 를 지원한다.
         *  이 것은 ServerWebExchange 객체에서 얻어온다.
         *
         *  Ordered.HIGHEST_PRECEDENCE 를 주면 때문에 우선순위가 가장 높다.
         */
//        return (exchange, chain) -> {
            // 비동기 가능 reactive (RxJava, WebFlux)
//            ServerHttpRequest request = exchange.getRequest();
//            ServerHttpResponse response = exchange.getResponse();
//
//            log.info("Global Filter baseMessage -> {}", config.getBaseMessage());
//
//            if (config.isPreLogger()) {
//                log.info("Global Filter Start: request id -> {}", request.getId());
//            }
//
//            /**
//             * Custom Post Filter
//             *
//             * Mono -> 스프링 5 WebFlux 추가 ( 비동기를 지원할 때 사용 )
//             */
//            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//                if (config.isPostLogger()) {
//                    log.info("Global Filter End: request id -> {}", response.getStatusCode());
//                }
//            }));
//        };
        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging Filter baseMessage -> {}", config.getBaseMessage());

            if (config.isPreLogger()) {
                log.info("Logging Pre Filter: request id -> {}", request.getId());
            }

            /**
             * Custom Post Filter
             * Mono -> 스프링 5 WebFlux 추가 ( 비동기를 지원할 때 사용 )
             */
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("Logging Post Filter: response id -> {}", response.getStatusCode());
                }
            }));
        }, Ordered.LOWEST_PRECEDENCE); // Ordered.HIGHEST_PRECEDENCE 를 주면 필터 우선 순위가 가장 높다.

        return filter;
    }

    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
