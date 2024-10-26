package org.seungkyu.board.config

import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class WebFluxSecurityFilter(
    private val jwtTokenProvider: JwtTokenProvider
): WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val authorizationHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return chain.filter(exchange)

        if(authorizationHeader.startsWith("Bearer ")){
            val token = authorizationHeader.substring(7)
            val authentication = SeungkyuAuthentication(
                id = jwtTokenProvider.getId(token),
                role = jwtTokenProvider.getRole(token)
            )

            return chain.filter(exchange)
                .contextWrite {
                    it.put("authentication", ReactiveSecurityContextHolder.withAuthentication(
                        authentication
                    ))
                }
        }

        return chain.filter(exchange)
    }
}