package org.seungkyu.board.service

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

interface UserService {

    suspend fun register(serverRequest: ServerRequest): ServerResponse
    suspend fun login(request: ServerRequest): ServerResponse
    suspend fun getUserInfo(request: ServerRequest): ServerResponse
    suspend fun updatePassword(request: ServerRequest): ServerResponse
    suspend fun deleteId(request: ServerRequest): ServerResponse
    suspend fun logout(request: ServerRequest): ServerResponse
}