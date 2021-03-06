# simple-session

基于Redis的分布式会话管理
---

* 基本使用

    * Maven依赖:

        ```xml
        <dependency>
           <groupId>org.simple.session</groupId>
               <artifactId>simple-session</artifactId>
               <version>0.0.1-SNAPSHOT</version>
        </dependency>
        ```

    * 在classpath下配置session.properties:

        ```bash
        session.redis.host=localhost
        session.redis.port=6379
        session.redis.pool.max.total=5
        session.redis.pool.max.idle=2
        session.redis.prefix=rsid
        ```
    * web.xml中配置Filter:

        ```xml
        <filter>
            <filter-name>RedisSessionFilter</filter-name>
            <filter-class>org.simple.session.core.filter.RedisSessionFilter</filter-class>
            <init-param>
                <param-name>sessionCookieName</param-name>
                <param-value>rsid</param-value>
            </init-param>
            <init-param>
                <param-name>maxInactiveInterval</param-name>
                <param-value>1800</param-value>
            </init-param>
            <init-param>
                <param-name>cookieContextPath</param-name>
                <param-value>/</param-value>
            </init-param>
            <init-param>
                <param-name>cookieMaxAge</param-name>
                <param-value>1800</param-value>
            </init-param>
        </filter>
        <filter-mapping>
            <filter-name>RedisSessionFilter</filter-name>
            <url-pattern>/*</url-pattern>
        </filter-mapping>
        ```
