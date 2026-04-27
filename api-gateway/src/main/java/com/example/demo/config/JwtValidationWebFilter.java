package com.example.demo.config;

import java.util.List;

import org.springframework.http.HttpHeaders;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.server.ServerWebExchange;

import org.springframework.web.server.WebFilter;

import org.springframework.web.server.WebFilterChain;

import io.jsonwebtoken.Claims;

import reactor.core.publisher.Mono;

@Component

public class JwtValidationWebFilter implements WebFilter {

	private final JwtTokenUtil jwtTokenUtil;

	public JwtValidationWebFilter(JwtTokenUtil jwtTokenUtil) {

		this.jwtTokenUtil = jwtTokenUtil;

	}

	@Override

	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		String authHeader =

				exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			if (jwtTokenUtil.isTokenValid(token)) {

				Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);

				// String username = claims.getSubject();

				Long userId = claims.get("userId", Long.class);
				String email = claims.get("email", String.class);
				String role = claims.get("role", String.class);

				Authentication auth = new AbstractAuthenticationToken(
						List.of(new SimpleGrantedAuthority("ROLE_" + role))) {
					@Override
					public Object getCredentials() {
						return null;
					}

					@Override
					public Object getPrincipal() {
						return email;
					}
				};
				auth.setAuthenticated(true);

				ServerWebExchange mutatedExchange = exchange.mutate()
						.request(exchange.getRequest().mutate().header("X-User-Id", String.valueOf(userId))
								.header("X-User-Email", email).header("X-User-Role", role).build())
						.build();

				System.out.printf("✅ Gateway authenticated userId=%d, email=%s, role=%s%n", userId, email, role);

				return chain.filter(mutatedExchange)

						.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));

			}

		}

		return chain.filter(exchange);

	}

}
