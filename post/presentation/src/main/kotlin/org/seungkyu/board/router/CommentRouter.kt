package org.seungkyu.board.router

import org.seungkyu.board.service.CommentService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class CommentRouter {

    @Bean
    fun commentRouters(
        commentService: CommentService
    ) = coRouter {
        "/comments".nest{
            POST("", commentService::post)
            PATCH("", commentService::patch)
            DELETE("", commentService::delete)
        }
    }
}