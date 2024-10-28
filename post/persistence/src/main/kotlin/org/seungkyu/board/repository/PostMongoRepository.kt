package org.seungkyu.board.repository

import org.bson.types.ObjectId
import org.seungkyu.board.entity.PostDocument
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface PostMongoRepository: ReactiveMongoRepository<PostDocument, ObjectId> {

    fun findByUserId(userId: String): Flux<PostDocument>
}