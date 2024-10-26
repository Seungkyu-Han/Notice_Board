package org.seungkyu.board.repository

import org.bson.types.ObjectId
import org.seungkyu.board.entity.UserDocument
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
interface UserMongoRepository: ReactiveMongoRepository<UserDocument, ObjectId> {

    fun findByUserId(userId: String): Mono<UserDocument>
    fun existsByUserId(userId: String): Mono<Boolean>
}