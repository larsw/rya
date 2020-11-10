package org.apache.rya.web2.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class RyaAuthorizer {

    final static Pattern VisibilityPattern = Pattern.compile("[a-z0-9]+", Pattern.CASE_INSENSITIVE);

    Logger logger = LoggerFactory.getLogger(RyaAuthorizer.class);

    private final Authentication principal;
    @Value("${rya.sparql.api.authorizationsClaim:azk}")
    private String authorizationsClaimName;

    public RyaAuthorizer() {

        this.principal = SecurityContextHolder.getContext().getAuthentication();
    }

    public Optional<Set<String>> authorize(Optional<String> optionalVisibility) {

        Set<String> visibilitiesSet = optionalVisibility.map(this::getVisibilities).orElse(Collections.emptySet());
        Optional<Set<String>> authorizationsSet;

        JwtAuthenticationToken token = (JwtAuthenticationToken)principal;
        final Object claim = token.getTokenAttributes().get(authorizationsClaimName);
        if (claim == null) {
            authorizationsSet = Optional.empty();
        }
        else if (claim instanceof String) {
            final String stringClaim = (String) claim;
            authorizationsSet = Optional.of(Arrays.stream(stringClaim.split(",")).collect(Collectors.toSet()));
        } else if (claim instanceof List<?>) {
            final List<String> listOfAuthorizations = Collections.unmodifiableList((List<String>) claim);
            authorizationsSet = Optional.of(new HashSet<>(listOfAuthorizations));
        } else {
            throw new UnsupportedOperationException("Unknown claim type: " + claim.getClass().getName());
        }

        if (!authorizationsSet.isPresent())  {
            return Optional.empty();
        } else {
            if (optionalVisibility.isPresent()) {
                if (!authorizationsSet.get().containsAll(visibilitiesSet)) {
                    logger.error("Unauthorized access: tried to use the following visibilities: {}, while only authorized for: {}",
                            visibilitiesSet,
                            authorizationsSet
                    );
                    return Optional.empty();
                }
            }
            return authorizationsSet;
        }
    }

    private Set<String> getVisibilities(String visibility) {
        Matcher matcher = VisibilityPattern.matcher(visibility);
        Set<String> parts = new HashSet<>();
        while(matcher.find()) {
            parts.add(matcher.group());
        }
        return parts;
    }
}
