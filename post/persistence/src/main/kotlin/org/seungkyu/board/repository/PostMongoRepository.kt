package org.seungkyu.board.repository

import org.bson.types.ObjectId
import org.seungkyu.board.entity.PostDocument
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface PostMongoRepository: ReactiveMongoRepository<PostDocument, ObjectId> {
}