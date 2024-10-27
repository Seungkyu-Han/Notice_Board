package org.seungkyu.board.service.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.seungkyu.board.data.enums.Role
import org.seungkyu.board.dto.req.CategoryPostReq
import org.seungkyu.board.dto.res.CategoryGetRes
import org.seungkyu.board.entity.CategoryDocument
import org.seungkyu.board.repository.CategoryMongoRepository
import org.seungkyu.board.service.CategoryService
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import reactor.core.publisher.Mono

@Service
class CategoryServiceImpl(
    private val categoryMongoRepository: CategoryMongoRepository
) : CategoryService{

    override suspend fun post(serverRequest: ServerRequest): ServerResponse {
        if(Role.ADMIN.name != getUserRoleByContext().awaitSingleOrNull()){
            return ServerResponse.status(403).buildAndAwait()
        }

        val categoryPostReq = serverRequest.bodyToMono(CategoryPostReq::class.java).awaitSingle()

        val categoryDocument = CategoryDocument(
            id= null,
            name = categoryPostReq.name,
            isAscending = categoryPostReq.isAscending,
            searchCount = 0
        )

        categoryMongoRepository.save(categoryDocument).awaitSingle()

        return ServerResponse.status(201).buildAndAwait()
    }

    override suspend fun get(serverRequest: ServerRequest): ServerResponse {
        return ServerResponse.ok().bodyValueAndAwait(withContext(Dispatchers.IO) {
            categoryMongoRepository.findAll()
                .map {
                    CategoryGetRes(name = it.name, searchCount = it.searchCount)
                }.toStream()
        }.toList())
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