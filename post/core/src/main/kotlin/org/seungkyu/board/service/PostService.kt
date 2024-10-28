package org.seungkyu.board.service

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

interface PostService {

    fun post(serverRequest: ServerRequest): ServerResponse
    fun patch(serverRequest: ServerRequest): ServerResponse
    fun get(serverRequest: ServerRequest): ServerResponse
    fun delete(serverRequest: ServerRequest): ServerResponse

}