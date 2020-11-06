package org.apache.rya.web2;

import org.apache.rya.web2.services.PrincipalClaimsProvider;
import org.apache.rya.web2.services.PrincipalClaimsProviderImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.cors.CorsConfiguration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class JWTSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwksUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Value("${rya.sparql.api.clientId}")
    private String name;

    private static final CorsConfiguration corsConfiguration;

    static {
        corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedMethod(HttpMethod.GET);
        corsConfiguration.addAllowedMethod(HttpMethod.POST);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.setAllowCredentials(true);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors(c -> c.configurationSource(x -> corsConfiguration))
                .authorizeRequests(authz ->
                        authz.antMatchers(HttpMethod.GET, "/").permitAll()
                             .antMatchers(HttpMethod.POST, "/sparql").hasAuthority("SCOPE_sparql"))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt());
    }

    @Bean
    public JwtDecoder jwtDecoder() {

        ArrayList<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(new JwtTimestampValidator());
        validators.add(new JwtIssuerValidator(issuer));
        validators.add(new JwtClaimValidator<List<String>>("aud", aud -> aud.contains(name)));

        OAuth2TokenValidator<Jwt> delegatingValidator = new DelegatingOAuth2TokenValidator<>(validators);

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
        jwtDecoder.setJwtValidator(delegatingValidator);
        return jwtDecoder;
    }

    // @Bean
    // public PrincipalClaimsProvider principalClaimsProvider() {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     return new PrincipalClaimsProviderImpl(authentication);
    // }
}
