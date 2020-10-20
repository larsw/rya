package org.apache.rya.web2;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class JWTSecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .cors()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .and()
                .and()
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST, "/sparql")
                .hasAuthority("SCOPE_execute")
                .pathMatchers(HttpMethod.GET, "/")
                .permitAll()
                .and()
                .build();
    }
}