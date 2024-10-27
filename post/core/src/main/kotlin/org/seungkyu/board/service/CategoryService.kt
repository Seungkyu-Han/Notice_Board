package org.seungkyu.board.service

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

interface CategoryService {

    suspend fun post(serverRequest: ServerRequest): ServerResponse
    suspend fun get(serverRequest: ServerRequest): ServerResponse
}