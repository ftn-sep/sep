package org.apigateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class JwtPropagationFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Dobijanje JWT tokena iz Authorization zaglavlja
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Ako Authorization zaglavlje sadrži JWT token, dodajte ga kao novo zaglavlje za prosljeđivanje
            String jwtToken = authorizationHeader.substring(7); // Ignorisanje "Bearer " dijela
            exchange.getRequest().mutate().header("X-Jwt-Token", jwtToken);
        }

        // Nastavak lanca filtera
        return chain.filter(exchange);
    }
}