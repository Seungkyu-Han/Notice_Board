package org.seungkyu.board.router

import org.seungkyu.board.service.PostService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class PostRouter {

    @Bean
    fun postRouters(
        postService: PostService
    ) = coRouter {
        "/posts".nest {
            GET("", postService::get)
            POST("", postService::post)
            PATCH("", postService::patch)
            DELETE("", postService::delete)
            GET("/find", postService::find)
        }
    }
}