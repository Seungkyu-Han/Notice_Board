package org.seungkyu.board.service.impl

import org.seungkyu.board.repository.PostMongoRepository
import org.seungkyu.board.service.PostService
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Service
class PostServiceImpl(
    private val postMongoRepository: PostMongoRepository
): PostService {

    companion object {
        private val log = LoggerFactory.getLogger(PostServiceImpl::class.java)
    }

    override fun post(serverRequest: ServerRequest): ServerResponse {
        TODO("Not yet implemented")
    }

    override fun patch(serverRequest: ServerRequest): ServerResponse {
        TODO("Not yet implemented")
    }

    override fun get(serverRequest: ServerRequest): ServerResponse {
        TODO("Not yet implemented")
    }

    override fun delete(serverRequest: ServerRequest): ServerResponse {
        TODO("Not yet implemented")
    }

    private fun getUserIdByContext(): Mono<String> {
        return ReactiveSecurityContextHolder.getContext()
            .map {
                it.authentication.name
            }
    }

    private fun getUserRoleByContext(): Mono<String> {
        return ReactiveSecurityContextHolder.getContext()
            .map{
                it.authentication.authorities.first().authority
            }
    }
}