package com.jrock.apigatewayserver.filter;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class); // 부모 클래스에도 적용 안그러면 class java.lang.Object cannot be cast to class
        this.env = env;
    }

    // login -> token -> (with token) -> header(include token)
    @Override
    public GatewayFilter apply(Config config) {
        /**
         *  Custom Pre Filter
         */
        return ((exchange, chain) -> {
            // 비동기 가능 reactive (RxJava, WebFlux)
            ServerHttpRequest request = exchange.getRequest();

            // 헤더 값 확인
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0); // 헤더 값 가져오기
            String jwt = authorizationHeader.replace("Bearer", "");

            // 토근 확인
            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        });

    }

    private boolean isJwtValid(String jwt) {

        boolean returnValue = true;

        String subject = null;

        try {
            // 암호화 된 토큰 subject 파싱
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody()
                    .getSubject();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (subject.isEmpty()) {
            returnValue = false;
        }

        return returnValue;


    }

    // Mono, Flux -> Spring WebFlux
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }

    public static class Config {

    }


}
