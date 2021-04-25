# Spring Cloud Gateway Server(API Gateway)
- - -
## 소프트웨어 구성
1. OpenJDK 11
2. Spring Boot 2.3.8.RELEASE
3. Netflix-Eureka-Client
4. Spring-Cloud-Gateway (Netty 사용(비동기허용), Zuul 은 톰캣)
5. Lombok
- - -
Build
 * IDE VM 옵션 Build
> - -Dserver.port={port}
 * Maven Build
> - mvn spring-boot:run -Dspring-boot.run.jvmArguments='-Dserver.port={port}'
> - mvn spring-boot:run (port random)
 * Maven Package After jar Build
> - mvn clean
> - mvn compile package
> - java -jar -Dserver.port={port} ./target/user-service-0.1.jar   // nohub 으로 할 것


## API Gateway Service
1. 인증 및 권한 부여
2. 서비스 검색 통합
3. 응답캐싱
4. 정책, 회로 차단기 및 QoS 다시 시도
5. 속도제한
6. 부하분산
7. 로깅, 추적, 상관관계
8. 헤더, 쿼리 문자열 및 청구 변환
9. IP 허용 목록에 추가
- Ribbon, Zuul 은 Spring Boot 2.4 이후 버전은 Maintenance 상태 이므로 하위 2.3.8 이하 버전 사용할 것

- Gateway Client -> Gateway Handler -> Global Filter -> Custom Filter -> LoggingFilter -> Proxied Service
- Load Balancer Setting
- User-Service ADD
- AuthorizationHeaderFilter ADD
  * Jwt 토큰 유효성 체크 후 Api 요청 및 에러 반환 