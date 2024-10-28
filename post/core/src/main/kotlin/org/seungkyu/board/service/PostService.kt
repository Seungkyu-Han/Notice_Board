package org.seungkyu.board.service

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

interface PostService {

    suspend fun post(serverRequest: ServerRequest): ServerResponse
    suspend fun patch(serverRequest: ServerRequest): ServerResponse
    suspend fun get(serverRequest: ServerRequest): ServerResponse
    suspend fun delete(serverRequest: ServerRequest): ServerResponse

}