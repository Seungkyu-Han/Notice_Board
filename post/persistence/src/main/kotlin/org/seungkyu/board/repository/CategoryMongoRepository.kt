package org.seungkyu.board.repository

import org.bson.types.ObjectId
import org.seungkyu.board.entity.CategoryDocument
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface CategoryMongoRepository: ReactiveMongoRepository<CategoryDocument, ObjectId> {
}