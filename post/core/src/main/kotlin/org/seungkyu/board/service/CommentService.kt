package org.seungkyu.board.service

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

interface CommentService {

    suspend fun post(serverRequest: ServerRequest): ServerResponse
    suspend fun patch(serverRequest: ServerRequest): ServerResponse
    suspend fun delete(serverRequest: ServerRequest): ServerResponse
}