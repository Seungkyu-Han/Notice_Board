package org.seungkyu.board.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "Category")
data class CategoryDocument(
    @Id
    val id: ObjectId?,
    var name: String,
    val userId: String,
    var isAscending: Boolean,
    var searchCount: Int,
)
