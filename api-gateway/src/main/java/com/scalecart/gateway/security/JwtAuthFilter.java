package com.scalecart.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter extends
        AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            String path = exchange.getRequest().getPath().toString();
            log.info("Gateway processing request: {}", path);

            // Extract Authorization header
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            // No token present
            if (authHeader == null || authHeader.isBlank()) {
                log.warn("Gateway: missing JWT for path={}", path);
                return onError(exchange, HttpStatus.UNAUTHORIZED,
                        "Missing or invalid Authorization header");
            }

            String token = extractToken(authHeader);
            if (token == null) {
                log.warn("Gateway: missing JWT for path={}", path);
                return onError(exchange, HttpStatus.UNAUTHORIZED,
                        "Missing or invalid Authorization header");
            }

            // Validate JWT using RSA public key
            if (!jwtService.isTokenValid(token)) {
                log.warn("Gateway: invalid JWT for path={}", path);
                return onError(exchange, HttpStatus.UNAUTHORIZED,
                        "Invalid or expired JWT token");
            }

            // Token valid — extract email and forward as header
            // Downstream services can trust X-User-Email since it
            // was set by the gateway after JWT validation
            String email = jwtService.extractEmail(token);

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-User-Email", email)
                            .build())
                    .build();

            log.info("Gateway: JWT valid for user={}, forwarding to {}",
                    email, path);

            // Continue filter chain with mutated request
            return chain.filter(mutatedExchange);
        };
    }

    /**
     * Returns an error response without forwarding to the service.
     * Mono.empty() = reactive equivalent of "done, nothing more to do"
     */
    private Mono<Void> onError(ServerWebExchange exchange,
                               HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders()
                .add("X-Gateway-Error", message);
        return exchange.getResponse().setComplete();
    }

    private String extractToken(String authHeader) {
        String trimmed = authHeader.trim();
        if (trimmed.regionMatches(true, 0, "Bearer", 0, 6)) {
            return trimmed.substring(6).trim();
        }
        if (trimmed.startsWith("eyJ")) {
            return trimmed;
        }
        return null;
    }

    // Config class required by AbstractGatewayFilterFactory
    // Empty for now — could hold filter configuration params
    public static class Config {}
}