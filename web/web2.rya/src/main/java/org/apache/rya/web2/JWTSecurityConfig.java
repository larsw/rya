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
//	    return http.csrf().disable()
//		    .cors()
//		    .and()
//		    .authorizeExchange()
//		    .pathMatchers("/")
//		    .permitAll()
//		    .and()
//		    .build();
        return http
                .csrf().disable()
                .cors()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .and()
                .and()
                .authorizeExchange()
                .pathMatchers(HttpMethod.GET, "/")
                .hasAuthority("SCOPE_sparql")
                .pathMatchers(HttpMethod.POST, "/sparql")
                .hasAnyAuthority("SCOPE_sparql:query", "SCOPE_sparql:update")
                .and()
                .build();
    }

    // TODO
    // Add issuer and audience validation:
    // https://docs.spring.io/spring-security/site/docs/5.1.13.BUILD-SNAPSHOT/reference/html/webflux-oauth2.html
}
