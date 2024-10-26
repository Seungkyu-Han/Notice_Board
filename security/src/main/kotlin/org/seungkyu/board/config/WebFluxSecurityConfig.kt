package org.seungkyu.board.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
@ComponentScan(basePackages = ["org.seungkyu.board.config"])
open class WebFluxSecurityConfig(
    private val webFluxSecurityFilter: WebFluxSecurityFilter
) {

    @Bean
    open fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http

            .csrf{
                it.disable()
            }

            .formLogin {
                it.disable()
            }
            .logout {
                it.disable()
            }
            .authorizeExchange {

            }
            .addFilterAt(webFluxSecurityFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }

}