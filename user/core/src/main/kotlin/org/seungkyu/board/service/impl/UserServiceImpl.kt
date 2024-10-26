package org.seungkyu.board.service.impl

import org.seungkyu.board.config.BCryptPasswordEncoderConfig
import org.seungkyu.board.config.JwtTokenProvider
import org.seungkyu.board.repository.UserMongoRepository
import org.seungkyu.board.service.UserService
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Service
class UserServiceImpl(
    private val bCryptPasswordEncoderConfig: BCryptPasswordEncoderConfig,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userMongoRepository: UserMongoRepository
) : UserService {

    override suspend fun register(serverRequest: ServerRequest): ServerResponse {
        TODO("Not yet implemented")
    }

    override suspend fun login(request: ServerRequest): ServerResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getUserInfo(request: ServerRequest): ServerResponse {
        TODO("Not yet implemented")
    }

    override suspend fun updatePassword(request: ServerRequest): ServerResponse {
        TODO("Not yet implemented")
    }

    override suspend fun deleteId(request: ServerRequest): ServerResponse {
        TODO("Not yet implemented")
    }

    override suspend fun logout(request: ServerRequest): ServerResponse {
        TODO("Not yet implemented")
    }
}