package org.seungkyu.board.router

import org.seungkyu.board.service.CategoryService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class CategoryRouter {

    @Bean
    fun categoryRouters(
        categoryService: CategoryService
    ) = coRouter {
        "categories".nest{
            POST("", categoryService::post)
            GET("", categoryService::get)
            PATCH("", categoryService::patch)
            DELETE("", categoryService::delete)
        }
    }
}