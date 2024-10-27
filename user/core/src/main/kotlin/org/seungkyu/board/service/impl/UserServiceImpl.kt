package org.seungkyu.board.service.impl

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.seungkyu.board.config.JwtTokenProvider
import org.seungkyu.board.data.enums.Role
import org.seungkyu.board.dto.req.LoginReq
import org.seungkyu.board.dto.req.PatchPasswordReq
import org.seungkyu.board.dto.req.RegisterReq
import org.seungkyu.board.dto.res.LoginRes
import org.seungkyu.board.dto.res.UserInfoRes
import org.seungkyu.board.entity.UserDocument
import org.seungkyu.board.repository.UserMongoRepository
import org.seungkyu.board.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import reactor.core.publisher.Mono

@Service
class UserServiceImpl(
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userMongoRepository: UserMongoRepository
) : UserService {

    companion object{
        private val log = LoggerFactory.getLogger(UserServiceImpl::class.java)
    }

    override suspend fun register(serverRequest: ServerRequest): ServerResponse {
        val registerReq = serverRequest.bodyToMono(RegisterReq::class.java).awaitSingle()

        if(userMongoRepository.existsByUserId(registerReq.userId).awaitSingle()){
            log.error("Duplicate ID : {}", registerReq.userId)
            return ServerResponse.status(HttpStatus.CONFLICT).buildAndAwait()
        }

        val userDocument = UserDocument(
            id = null,
            userId = registerReq.userId,
            password = bCryptPasswordEncoder.encode(registerReq.password),
            nickName = registerReq.nickname,
            isAdmin = false,
            isWithDraw = false,
            status = Role.USER.name,
            createdAt = null,
            updatedAt = null
        )

        userMongoRepository.save(userDocument).awaitSingleOrNull()

        return ServerResponse.status(201).buildAndAwait()
    }

    override suspend fun login(request: ServerRequest): ServerResponse {
        val loginReq = request.bodyToMono(LoginReq::class.java).awaitSingle()

        val user = userMongoRepository.findByUserId(loginReq.id).awaitSingleOrNull()

        return if(user != null && bCryptPasswordEncoder.matches(loginReq.password, user.password)){
            ServerResponse.ok().bodyValueAndAwait(LoginRes(jwtTokenProvider.getJwtToken(user.userId, user.status)))
        }else{
            ServerResponse.status(403).buildAndAwait()
        }
    }

    override suspend fun getUser(request: ServerRequest): ServerResponse {
        val userId = this.getUserIdByContext().awaitSingleOrNull() ?: return ServerResponse.status(403).buildAndAwait()

        val user = userMongoRepository.findByUserId(userId).awaitSingleOrNull()

        return if(user != null){
            ServerResponse.ok().bodyValueAndAwait(
                UserInfoRes(
                    userId = user.userId, nickname = user.nickName, isAdmin = (Role.ADMIN.name == user.status)
                )
            )
        } else{
            ServerResponse.status(404).buildAndAwait()
        }
    }

    override suspend fun patchPassword(request: ServerRequest): ServerResponse {
        val updatePasswordReq = request.bodyToMono(PatchPasswordReq::class.java).awaitSingle()

        val userId = getUserIdByContext().awaitSingleOrNull() ?: return ServerResponse.status(403).buildAndAwait()

        val user = userMongoRepository.findByUserId(userId).awaitSingleOrNull()

        if(user != null && bCryptPasswordEncoder.matches(updatePasswordReq.beforePassword, user.password)){
            user.password = bCryptPasswordEncoder.encode(updatePasswordReq.afterPassword)
            userMongoRepository.save(user).awaitSingle()
            return ServerResponse.ok().buildAndAwait()
        }else{
            return ServerResponse.status(403).buildAndAwait()
        }
    }

    override suspend fun deleteId(request: ServerRequest): ServerResponse {
        val userId = getUserIdByContext().awaitSingle()

        val user = userMongoRepository.findByUserId(userId).awaitSingleOrNull()
            ?: return ServerResponse.status(403).buildAndAwait()

        userMongoRepository.delete(user).awaitSingleOrNull()

        return ServerResponse.ok().buildAndAwait()
    }

    override suspend fun logout(request: ServerRequest): ServerResponse {
        return ServerResponse.ok().buildAndAwait()
    }

    fun getUserIdByContext(): Mono<String> {
        return ReactiveSecurityContextHolder.getContext()
            .map {
                it.authentication.name
            }
    }

}