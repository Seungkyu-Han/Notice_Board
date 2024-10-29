package org.seungkyu.board.service.impl

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.seungkyu.board.dto.req.CommentPatchReq
import org.seungkyu.board.dto.req.CommentPostReq
import org.seungkyu.board.entity.CommentDocument
import org.seungkyu.board.repository.PostMongoRepository
import org.seungkyu.board.service.CommentService
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull
import reactor.core.publisher.Mono
import java.lang.IndexOutOfBoundsException

@Service
class CommentServiceImpl(
    private val postMongoRepository: PostMongoRepository
): CommentService {

    override suspend fun post(serverRequest: ServerRequest): ServerResponse {
        val commentPostReq = serverRequest.bodyToMono(CommentPostReq::class.java).awaitSingleOrNull()

        val postDocument = postMongoRepository.findById(ObjectId(commentPostReq!!.postId)).awaitSingleOrNull() ?:
            return ServerResponse.notFound().buildAndAwait()

        postDocument.comments.add(
            CommentDocument(
                contents = commentPostReq.contents,
                userId = this.getUserIdByContext().awaitSingle()
            )
        )

        postMongoRepository.save(postDocument).awaitSingle()

        return ServerResponse.ok().buildAndAwait()
    }

    override suspend fun patch(serverRequest: ServerRequest): ServerResponse {
        val commentPostReq = serverRequest.bodyToMono(CommentPatchReq::class.java).awaitSingleOrNull()

        val postDocument = postMongoRepository.findById(ObjectId(commentPostReq!!.postId)).awaitSingleOrNull() ?:
        return ServerResponse.notFound().buildAndAwait()

        try{
            postDocument.comments[commentPostReq.commentIndex].contents = commentPostReq.contents

        }catch(e: IndexOutOfBoundsException){
            return ServerResponse.notFound().buildAndAwait()
        }

        postMongoRepository.save(postDocument).awaitSingle()

        return ServerResponse.ok().buildAndAwait()
    }

    override suspend fun delete(serverRequest: ServerRequest): ServerResponse {
        val postId = serverRequest.queryParamOrNull("postId") ?: return ServerResponse.badRequest().buildAndAwait()
        val commentIndex = serverRequest.queryParamOrNull("commentIndex") ?: return ServerResponse.badRequest().buildAndAwait()

        val postDocument = postMongoRepository.findById(ObjectId(postId)).awaitSingleOrNull() ?:
            return ServerResponse.notFound().buildAndAwait()

        try{
            postDocument.comments.removeAt(commentIndex.toInt())
        }catch(e: IndexOutOfBoundsException){
            return ServerResponse.badRequest().buildAndAwait()
        }

        postMongoRepository.save(postDocument).awaitSingle()

        return ServerResponse.ok().buildAndAwait()
    }

    private fun getUserIdByContext(): Mono<String> {
        return ReactiveSecurityContextHolder.getContext()
            .map {
                it.authentication.name
            }
    }
}