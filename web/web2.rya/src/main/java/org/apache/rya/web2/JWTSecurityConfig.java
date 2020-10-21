package org.apache.rya.web2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.ArrayList;
import java.util.List;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class JWTSecurityConfig {
    @Autowired
    private Environment env;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwksUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Value("${rya.sparql.api.name}")
    private String name;

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
                .pathMatchers(HttpMethod.GET, "/")
                .permitAll()
                .pathMatchers(HttpMethod.POST, "/sparql")
                .hasAuthority("SCOPE_sparql")
//                .hasAnyAuthority("SCOPE_sparql:query", "SCOPE_sparql:update")
                .and()
                .build();
    }

    // https://docs.spring.io/spring-security/site/docs/5.1.13.BUILD-SNAPSHOT/reference/html/webflux-oauth2.html
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {

        ArrayList<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(new JwtTimestampValidator());
        validators.add(new JwtIssuerValidator(issuer));
        validators.add(new JwtClaimValidator<List<String>>("aud", aud -> aud.contains(name)));

        OAuth2TokenValidator<Jwt> delegatingValidator = new DelegatingOAuth2TokenValidator<>(validators);

        NimbusReactiveJwtDecoder jwtDecoder = NimbusReactiveJwtDecoder.withJwkSetUri(jwksUri).build();
        jwtDecoder.setJwtValidator(delegatingValidator);
        return jwtDecoder;
    }
}
