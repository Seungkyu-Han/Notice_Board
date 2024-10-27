package org.seungkyu.board.router

import org.seungkyu.board.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserRouter {

    @Bean
    fun router(
        userService: UserService
    ) = coRouter {

        "/users".nest {
            POST("/register", userService::register)
            POST("/login", userService::login)
            GET("/info", userService::getUser)
            PUT("/logout", userService::logout)
            PATCH("/password", userService::patchPassword)
            DELETE("", userService::deleteId)
        }
    }
}