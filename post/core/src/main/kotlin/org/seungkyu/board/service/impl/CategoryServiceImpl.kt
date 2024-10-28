package org.seungkyu.board.service.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import org.seungkyu.board.data.enums.Role
import org.seungkyu.board.dto.req.CategoryPatchReq
import org.seungkyu.board.dto.req.CategoryPostReq
import org.seungkyu.board.dto.res.CategoryGetRes
import org.seungkyu.board.entity.CategoryDocument
import org.seungkyu.board.repository.CategoryMongoRepository
import org.seungkyu.board.service.CategoryService
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*
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
            searchCount = 0,
            userId = getUserIdByContext().awaitSingle()
        )

        categoryMongoRepository.save(categoryDocument).awaitSingle()

        return ServerResponse.status(201).buildAndAwait()
    }

    override suspend fun get(serverRequest: ServerRequest): ServerResponse {
        return ServerResponse.ok().bodyValueAndAwait(withContext(Dispatchers.IO) {
            categoryMongoRepository.findAll()
                .map {
                    CategoryGetRes(name = it.name, searchCount = it.searchCount, id = it.id!!.toHexString())
                }.toStream()
        }.toList())
    }

    override suspend fun patch(serverRequest: ServerRequest): ServerResponse {

        val categoryPatchReq = serverRequest.bodyToMono(CategoryPatchReq::class.java)
            .awaitSingle()

        val categoryDocument = categoryMongoRepository.findById(ObjectId(categoryPatchReq.id)).awaitSingleOrNull()
            ?: return ServerResponse.status(404).buildAndAwait()

        if(categoryDocument.userId != getUserIdByContext().awaitSingle())
            return ServerResponse.status(403).buildAndAwait()

        categoryDocument.name = categoryPatchReq.name
        categoryDocument.isAscending = categoryPatchReq.isAscending

        categoryMongoRepository.save(categoryDocument).awaitSingle()

        return ServerResponse.ok().buildAndAwait()
    }

    override suspend fun delete(serverRequest: ServerRequest): ServerResponse {
        val categoryId = serverRequest.queryParamOrNull("id") ?: return ServerResponse.notFound().buildAndAwait()

        val categoryDocument = categoryMongoRepository.findById(ObjectId(categoryId)).awaitSingleOrNull() ?: return ServerResponse.notFound().buildAndAwait()

        if(categoryDocument.userId == getUserIdByContext().awaitSingle()){
            categoryMongoRepository.deleteById(categoryDocument.id!!).awaitSingleOrNull()
            return ServerResponse.ok().buildAndAwait()
        }
        else{
            return ServerResponse.status(403).buildAndAwait()
        }
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