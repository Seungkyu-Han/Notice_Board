package org.seungkyu.board.service.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import org.seungkyu.board.dto.req.PostPatchReq
import org.seungkyu.board.dto.req.PostPostReq
import org.seungkyu.board.dto.res.PostFindRes
import org.seungkyu.board.dto.res.PostGetRes
import org.seungkyu.board.entity.PostDocument
import org.seungkyu.board.repository.CategoryMongoRepository
import org.seungkyu.board.repository.PostMongoRepository
import org.seungkyu.board.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

@Service
class PostServiceImpl(
    private val postMongoRepository: PostMongoRepository,
    private val categoryMongoRepository: CategoryMongoRepository,
): PostService {

    override suspend fun post(serverRequest: ServerRequest): ServerResponse {
        val userId = getUserIdByContext().awaitSingleOrNull() ?:
            return ServerResponse.status(HttpStatus.FORBIDDEN).buildAndAwait()

        val postPostReq = serverRequest.bodyToMono(PostPostReq::class.java).awaitSingle()
        val category = categoryMongoRepository.findById(ObjectId(postPostReq.categoryId)).awaitSingleOrNull()

        println(category)

        val postDocument = PostDocument(
            id = null,
            name = postPostReq.name,
            userId = userId,
            content = postPostReq.content,
            createdAt = null,
            updatedAt = null,
            categoryDocument = ObjectId(postPostReq.categoryId),
            comments = listOf()
        )


        postMongoRepository.save(postDocument).awaitSingle()

        return ServerResponse.status(201).buildAndAwait()
    }

    override suspend fun patch(serverRequest: ServerRequest): ServerResponse {
        val userId = getUserIdByContext().awaitSingleOrNull() ?:
        return ServerResponse.status(HttpStatus.FORBIDDEN).buildAndAwait()

        val postPatchReq = serverRequest.bodyToMono(PostPatchReq::class.java).awaitSingle()

        val postDocument = postMongoRepository.findById(ObjectId(postPatchReq.id)).awaitSingleOrNull()

        println(postDocument)

        if(postDocument == null)
            return ServerResponse.status(HttpStatus.NOT_FOUND).buildAndAwait()
        else if (postDocument.userId != userId) {
            return ServerResponse.status(HttpStatus.FORBIDDEN).buildAndAwait()
        }
        else{
            postDocument.name = postPatchReq.name
            postDocument.content = postPatchReq.content

            postMongoRepository.save(postDocument).awaitSingle()
            return ServerResponse.ok().buildAndAwait()
        }
    }

    override suspend fun get(serverRequest: ServerRequest): ServerResponse {
        val userId = getUserIdByContext().awaitSingleOrNull() ?: return ServerResponse.status(HttpStatus.FORBIDDEN).buildAndAwait()
        return ServerResponse.ok().bodyValueAndAwait(withContext(Dispatchers.IO) {
            postMongoRepository.findByUserId(userId)
                .map {
                    PostGetRes(
                        id = it.id!!.toHexString(),
                        name = it.name,
                        content = it.content,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt,
                        categoryId = it.categoryDocument!!.toHexString(),
                    )
                }.toStream()
        }.toList())
    }

    override suspend fun delete(serverRequest: ServerRequest): ServerResponse {
        val userId = getUserIdByContext().awaitSingleOrNull() ?: return ServerResponse.status(HttpStatus.FORBIDDEN).buildAndAwait()

        val id = serverRequest.queryParamOrNull("id") ?: return ServerResponse.status(HttpStatus.NOT_FOUND).buildAndAwait()

        val postDocument = postMongoRepository.findById(ObjectId(id)).awaitSingleOrNull() ?: return ServerResponse.status(HttpStatus.NOT_FOUND).buildAndAwait()

        if(postDocument.userId != userId){
            return ServerResponse.status(HttpStatus.FORBIDDEN).buildAndAwait()
        }
        else{
            postMongoRepository.delete(postDocument).awaitSingle()
            return ServerResponse.ok().buildAndAwait()
        }
    }

    override suspend fun find(serverRequest: ServerRequest): ServerResponse {
        val name = serverRequest.queryParamOrNull("name") ?: return ServerResponse.badRequest().buildAndAwait()

        return ServerResponse.ok().bodyValueAndAwait(withContext(Dispatchers.IO) {
            postMongoRepository.findByNameContains(name)
                .map {
                    PostFindRes(
                        id = it.id!!.toHexString(),
                        name = it.name,
                        isAdmin = true,
                        views = 0,
                        categoryId = it.categoryDocument?.toHexString(),
                        createdAt = it.createdAt!!,
                        updatedAt = it.updatedAt!!,
                        fileIds = listOf()
                    )
                }.toStream()
        }.toList())
    }

    private fun getUserIdByContext(): Mono<String> {
        return ReactiveSecurityContextHolder.getContext()
            .map {
                it.authentication.name
            }
    }
}